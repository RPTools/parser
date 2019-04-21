/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * RPTools Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.parser;

import static net.rptools.parser.ExpressionParserTokenTypes.ASSIGNEE;
import static net.rptools.parser.ExpressionParserTokenTypes.FALSE;
import static net.rptools.parser.ExpressionParserTokenTypes.FUNCTION;
import static net.rptools.parser.ExpressionParserTokenTypes.HEXNUMBER;
import static net.rptools.parser.ExpressionParserTokenTypes.NUMBER;
import static net.rptools.parser.ExpressionParserTokenTypes.OPERATOR;
import static net.rptools.parser.ExpressionParserTokenTypes.PROMPTVARIABLE;
import static net.rptools.parser.ExpressionParserTokenTypes.STRING;
import static net.rptools.parser.ExpressionParserTokenTypes.TRUE;
import static net.rptools.parser.ExpressionParserTokenTypes.UNARY_OPERATOR;
import static net.rptools.parser.ExpressionParserTokenTypes.VARIABLE;

import antlr.collections.AST;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.Function;

public class DeterministicTreeParser {
  private static final Logger log = Logger.getLogger(EvaluationTreeParser.class.getName());

  private final Parser parser;
  private final ExpressionParser xParser;

  public DeterministicTreeParser(Parser parser, ExpressionParser xParser) {
    this.parser = parser;
    this.xParser = xParser;
  }

  public AST evaluate(AST node) throws ParserException {
    if (node == null) return null;

    switch (node.getType()) {
      case STRING:
      case NUMBER:
      case HEXNUMBER:
      case ASSIGNEE:
      case TRUE:
      case FALSE:
        node.setNextSibling(evaluate(node.getNextSibling()));
        return node;
      case VARIABLE:
        {
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
      case PROMPTVARIABLE:
        {
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
      case FUNCTION:
        {
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
        throw new EvaluationException(
            String.format("Unknown node type: name=%s, type=%d", node.getText(), node.getType()));
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
