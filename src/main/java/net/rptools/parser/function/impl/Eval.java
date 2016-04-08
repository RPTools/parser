/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.rptools.parser.function.impl;

import java.util.List;

import net.rptools.parser.Expression;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.ParameterException;

public class Eval extends AbstractFunction {
	public Eval() {
		super(1, -1, "eval");
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws ParserException {

		Object ret = null;

		for (Object p : parameters) {
			String x = (String) p;

			Expression expression;
			try {
				expression = parser.parseExpression(x);
			} catch (ParserException e) {
				throw new ParameterException(String.format("Unable to evaluate expression %s", x));
			}

			ret = expression.evaluate();
		}

		return ret;
	}

	@Override
	public void checkParameters(List<Object> parameters) throws ParameterException {
		super.checkParameters(parameters);

		for (Object param : parameters) {
			if (!(param instanceof String))
				throw new ParameterException(String.format("Illegal argument type %s, expecting %s", param.getClass().getName(), String.class.getName()));
		}
	}
}
