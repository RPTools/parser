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

import java.math.BigDecimal;
import java.util.List;

import net.rptools.parser.Parser;
import net.rptools.parser.function.AbstractNumberFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

public class SquareRoot extends AbstractNumberFunction {
	private static final int DEFAULT_SCALE = 10;
	private static final BigDecimal TWO = new BigDecimal(2);

	public SquareRoot() {
		super(1, 2, "sqrt", "squareroot");
	}

	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws EvaluationException, ParameterException {
		int scale = DEFAULT_SCALE;
		if (parameters.size() == 2) {
			scale = ((BigDecimal) parameters.get(1)).intValue();
		}

		BigDecimal value = (BigDecimal) parameters.get(0);
		return sqrt(value, scale);
	}

	//  the Babylonian square root method (Newton's method)
	private BigDecimal sqrt(BigDecimal value, final int scale) {
		BigDecimal x0 = new BigDecimal("0");
		BigDecimal x1 = new BigDecimal(Math.sqrt(value.doubleValue()));

		while (!x0.equals(x1)) {
			x0 = x1;
			x1 = value.divide(x0, scale, BigDecimal.ROUND_HALF_UP);
			x1 = x1.add(x0);
			x1 = x1.divide(TWO, scale, BigDecimal.ROUND_HALF_UP);
		}

		return x1;
	}

}
