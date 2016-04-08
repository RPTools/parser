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

import java.math.BigDecimal;
import java.util.List;

import junit.framework.TestCase;
import net.rptools.parser.function.AbstractNumberFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;
import antlr.collections.AST;

public class DeterministicTreeParserTest extends TestCase {
	public void testEvaluateOnlyDeterministicFunctions() throws ParserException, EvaluationException,
			ParameterException {
		Parser p = new Parser();
		Expression xp = p.parseExpression("200+2+2*2");
		Expression dxp = xp.getDeterministicExpression();

		assertSame(xp, dxp);

		AST tree = xp.getTree();
		AST deterministicTree = dxp.getTree();

		assertTrue(deterministicTree.equalsTree(tree));
	}

	public void testEvaluate() throws ParserException, EvaluationException, ParameterException {
		Parser p = new Parser();
		p.addFunction(new NonDeterministicFunction());

		Expression xp = p.parseExpression("200+2+nondeterministic(2, 2)+sum(1+2,2,3)");
		Expression dxp = xp.getDeterministicExpression();

		assertNotSame(xp, dxp);

		assertEquals(" ( + ( + ( + 200 2 ) ( nondeterministic 2 2 ) ) ( sum ( + 1 2 ) 2 3 ) )", xp.getTree()
				.toStringTree());
		assertEquals(" ( + ( + ( + 200 2 ) 1 ) ( sum ( + 1 2 ) 2 3 ) )", dxp.getTree().toStringTree());
	}

	public void testEvaluate_WithAssignment() throws ParserException, EvaluationException, ParameterException {
		Parser p = new Parser();
		p.addFunction(new NonDeterministicFunction());

		Expression xp = p.parseExpression("a=200+2+nondeterministic(2, 2)");
		assertEquals(" ( = a ( + ( + 200 2 ) ( nondeterministic 2 2 ) ) )", xp.getTree().toStringTree());

		Expression dxp = xp.getDeterministicExpression();

		assertNotSame(xp, dxp);

		assertEquals(" ( = a ( + ( + 200 2 ) 1 ) )", dxp.getTree().toStringTree());
	}

	public void testEvaluate2() throws ParserException {
		Parser p = new Parser();
		p.addFunction(new NonDeterministicFunction());

		Expression xp = p.parseExpression("100+nondeterministic(4, 1)*10");

		Expression dxp = xp.getDeterministicExpression();

		assertNotSame(xp, dxp);

		assertEquals(" ( + 100 ( * ( nondeterministic 4 1 ) 10 ) )", xp.getTree().toStringTree());
		assertEquals(" ( + 100 ( * 1 10 ) )", dxp.getTree().toStringTree());
	}

	public void testEvaluate_VariableResolution() throws ParserException {
		Parser p = new Parser();
		p.setVariable("simpleInt", new BigDecimal(10));

		Expression xp = p.parseExpression("1+simpleInt");
		Expression dxp = xp.getDeterministicExpression();

		assertNotSame(xp, dxp);

		assertEquals(" ( + 1 simpleInt )", xp.getTree().toStringTree());
		assertEquals(" ( + 1 10 )", dxp.getTree().toStringTree());
	}

	/**
	 * Test function that declares itself non-deterministic for the purposes of
	 * comparing the result of getting a deterministic expression from another
	 * expression.
	 */
	private static class NonDeterministicFunction extends AbstractNumberFunction {
		public NonDeterministicFunction() {
			super(2, 2, false, "nondeterministic");
		}

		@Override
		public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) {
			return BigDecimal.ONE;
		}

	}

}
