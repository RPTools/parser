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
package net.rptools.parser;

import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;
import junit.framework.TestCase;

public class InlineTreeFormatterTest extends TestCase {
	public void testFormatSimple() throws ParserException, EvaluationException, ParameterException {
		compare("200*2", "200 * 2");
		compare("200*VAR+2", "200 * VAR + 2");
		compare("200*(2*4)*7", "200 * 2 * 4 * 7");
	}

	public void testFormatComplex() throws ParserException, EvaluationException, ParameterException {
		compare("200+2+2*2", "200 + 2 + 2 * 2");
		compare("200+(2+2)*2", "200 + (2 + 2) * 2");
	}

	public void testFormatFunction() throws ParserException, EvaluationException, ParameterException {
		compare("100*func(2)", "100 * func(2)");
		compare("100*func(2,(2+3)*7)", "100 * func(2, (2 + 3) * 7)");
		compare("100*func(2,(2+func2(3))*7)", "100 * func(2, (2 + func2(3)) * 7)");
	}

	public void testFormatOperatorAsFunction() throws ParserException, EvaluationException, ParameterException {
		compare("sum(200, 2)", "sum(200, 2)");
		compare("100 + 200 + multiply(7, 30)", "100 + 200 + multiply(7, 30)");
	}

	public void testFormatAssignment() throws ParserException, EvaluationException, ParameterException {
		compare("a=200+7", "a = (200 + 7)");
	}

	public void testFormatEval() throws ParserException, EvaluationException, ParameterException {
		compare("eval('2*2')", "eval('2*2')");
	}

	private void compare(String expression, String expected) throws ParserException, EvaluationException,
			ParameterException {
		Parser p = new Parser();
		Expression xp = p.parseExpression(expression);

		InlineTreeFormatter tf = new InlineTreeFormatter();

		assertEquals(expected, tf.format(xp.getTree()));
	}

}
