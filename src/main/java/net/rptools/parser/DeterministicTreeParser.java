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

import static net.rptools.parser.ExpressionParserTokenTypes.ASSIGNEE;
import static net.rptools.parser.ExpressionParserTokenTypes.FUNCTION;
import static net.rptools.parser.ExpressionParserTokenTypes.NUMBER;
import static net.rptools.parser.ExpressionParserTokenTypes.HEXNUMBER;
import static net.rptools.parser.ExpressionParserTokenTypes.OPERATOR;
import static net.rptools.parser.ExpressionParserTokenTypes.PROMPTVARIABLE;
import static net.rptools.parser.ExpressionParserTokenTypes.STRING;
import static net.rptools.parser.ExpressionParserTokenTypes.UNARY_OPERATOR;
import static net.rptools.parser.ExpressionParserTokenTypes.VARIABLE;
import static net.rptools.parser.ExpressionParserTokenTypes.TRUE;
import static net.rptools.parser.ExpressionParserTokenTypes.FALSE;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.Function;
import antlr.collections.AST;

public class DeterministicTreeParser {
	private static final Logger log = Logger.getLogger(EvaluationTreeParser.class.getName());

	private final Parser parser;
	private final ExpressionParser xParser;

	public DeterministicTreeParser(Parser parser, ExpressionParser xParser) {
		this.parser = parser;
		this.xParser = xParser;
	}

	public AST evaluate(AST node) throws ParserException {
		if (node == null)
			return null;

		switch (node.getType()) {
		case STRING:
		case NUMBER:
		case HEXNUMBER:
		case ASSIGNEE:
		case TRUE:
		case FALSE:
			node.setNextSibling(evaluate(node.getNextSibling()));
			return node;
		case VARIABLE: {
			String name = node.getText();
			if (!parser.containsVariable(name, VariableModifiers.None)) {
				throw new EvaluationException(String.format("Undefined variable: %s", name));
			}
			Object value = parser.getVariable(node.getText(), VariableModifiers.None);

			if (log.isLoggable(Level.FINEST))
				log.finest(String.format("VARIABLE: name=%s, value=%s\n", node.getText(), value));

			AST newNode = createNode(value);
			newNode.setNextSibling(evaluate(node.getNextSibling()));

			return newNode;
		}
		case PROMPTVARIABLE: {
			String name = node.getText();
			if (!parser.containsVariable(name, VariableModifiers.None)) {
				throw new EvaluationException(String.format("Undefined variable: %s", name));
			}
			Object value = parser.getVariable(node.getText(), VariableModifiers.None);

			if (log.isLoggable(Level.FINEST))
				log.finest(String.format("VARIABLE: name=%s, value=%s\n", node.getText(), value));

			AST newNode = createNode(value);
			newNode.setNextSibling(evaluate(node.getNextSibling()));

			return newNode;
		}
		case UNARY_OPERATOR:
		case OPERATOR:
		case FUNCTION: {
			String name = node.getText();
			Function function = parser.getFunction(node.getText());
			if (function == null) {
				throw new EvaluationException(String.format("Undefined function: %s", name));
			}

			if (!function.isDeterministic()) {
				Object value = parser.getEvaluationTreeParser().evaluate(node);

				AST newNode = createNode(value);
				newNode.setNextSibling(evaluate(node.getNextSibling()));

				return newNode;
			} else {
				node.setFirstChild(evaluate(node.getFirstChild()));
				node.setNextSibling(evaluate(node.getNextSibling()));
				return node;
			}
		}
		default:
			throw new EvaluationException(String.format("Unknown node type: name=%s, type=%d", node.getText(), node.getType()));
		}
	}

	private AST createNode(Object value) {
		AST newNode = xParser.getASTFactory().create();

		if (value instanceof BigDecimal) {
			newNode.setType(NUMBER);
			newNode.setText(value.toString());
		} else {
			newNode.setType(STRING);
			newNode.setText(value.toString());
		}

		return newNode;
	}
}
