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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.Function;

public class EvaluationTreeParser {
  private static final Logger log = Logger.getLogger(EvaluationTreeParser.class.getName());

  private final Parser parser;

  public EvaluationTreeParser(Parser parser) {
    this.parser = parser;
  }

  public Object evaluate(AST node) throws ParserException {
    AST child;

    switch (node.getType()) {
      case ASSIGNEE:
        {
          String name = node.getText();
          return name;
        }
      case TRUE:
        return BigDecimal.ONE;
      case FALSE:
        return BigDecimal.ZERO;
      case NUMBER:
        {
          BigDecimal d = new BigDecimal(node.getText());
          if (log.isLoggable(Level.FINEST)) log.finest(String.format("NUMBER: value=%f\n", d));
          return d;
        }
      case HEXNUMBER:
        {
          String s = node.getText();
          BigInteger i = new BigInteger(s.substring(2), 16);
          if (log.isLoggable(Level.FINEST)) log.finest(String.format("HEXNUMBER: value=%f\n", i));
          return new BigDecimal(i);
        }
      case UNARY_OPERATOR:
        {
          String name = node.getText();

          if (log.isLoggable(Level.FINEST))
            log.finest(String.format("UNARY_FUNCTION: name=%s type=%d\n", name, node.getType()));

          List<Object> params = new ArrayList<Object>();

          child = node.getFirstChild();
          if (child != null) {
            params.add(evaluate(child));
            while ((child = child.getNextSibling()) != null) {
              params.add(evaluate(child));
            }
          }

          Function function = parser.getFunction(node.getText());
          if (function == null) {
            throw new EvaluationException(String.format("Undefined unary function: %s", name));
          }
          return function.evaluate(parser, name, params);
        }
      case OPERATOR:
      case FUNCTION:
        {
          String name = node.getText();

          if (log.isLoggable(Level.FINEST))
            log.finest(String.format("FUNCTION: name=%s type=%d\n", name, node.getType()));

          List<Object> params = new ArrayList<Object>();

          child = node.getFirstChild();
          if (child != null) {
            params.add(evaluate(child));
            while ((child = child.getNextSibling()) != null) {
              params.add(evaluate(child));
            }
          }

          Function function = parser.getFunction(name);
          if (function == null) {
            throw new EvaluationException(String.format("Undefined function: %s", name));
          }
          return function.evaluate(parser, name, params);
        }
      case VARIABLE:
        {
          String name = node.getText();
          if (!parser.containsVariable(name, VariableModifiers.None)) {
            throw new EvaluationException(String.format("Undefined variable: %s", name));
          }
          Object value = parser.getVariable(node.getText(), VariableModifiers.None);
          if (log.isLoggable(Level.FINEST))
            log.finest(String.format("VARIABLE: name=%s, value=%s\n", node.getText(), value));
          return value;
        }
      case PROMPTVARIABLE:
        {
          String name = node.getText();
          if (!parser.containsVariable(name, VariableModifiers.Prompt)) {
            throw new EvaluationException(String.format("Undefined variable: %s", name));
          }
          Object value = parser.getVariable(node.getText(), VariableModifiers.Prompt);
          if (log.isLoggable(Level.FINEST))
            log.finest(String.format("VARIABLE: name=%s, value=%s\n", node.getText(), value));
          return value;
        }
      case STRING:
        {
          String str = node.getText();
          // Strip off the quotes from the string
          if (str.length() >= 2) {
            char first = str.charAt(0);
            char last = str.charAt(str.length() - 1);

            if (first == last && first == '\'' || first == '"')
              str = str.substring(1, str.length() - 1);
          }
          return str;
        }
      default:
        throw new EvaluationException(
            String.format("Unknown node type: name=%s, type=%d", node.getText(), node.getType()));
    }
  }
}
