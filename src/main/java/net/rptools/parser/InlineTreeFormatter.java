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
import static net.rptools.parser.ExpressionParserTokenTypes.FUNCTION;
import static net.rptools.parser.ExpressionParserTokenTypes.HEXNUMBER;
import static net.rptools.parser.ExpressionParserTokenTypes.NUMBER;
import static net.rptools.parser.ExpressionParserTokenTypes.OPERATOR;
import static net.rptools.parser.ExpressionParserTokenTypes.STRING;
import static net.rptools.parser.ExpressionParserTokenTypes.UNARY_OPERATOR;
import static net.rptools.parser.ExpressionParserTokenTypes.VARIABLE;

import antlr.collections.AST;
import java.util.HashMap;
import java.util.Map;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

public class InlineTreeFormatter {

  private static Map<String, Integer> ORDER_OF_OPERATIONS = new HashMap<String, Integer>();

  static {
    // P(1) E(2) MD(3) AS(4):
    ORDER_OF_OPERATIONS.put("=", 0);
    ORDER_OF_OPERATIONS.put("^", 2);
    ORDER_OF_OPERATIONS.put("*", 3);
    ORDER_OF_OPERATIONS.put("/", 3);
    ORDER_OF_OPERATIONS.put("+", 4);
    ORDER_OF_OPERATIONS.put("-", 4);
  }

  public String format(AST node) throws EvaluationException, ParameterException {
    StringBuilder sb = new StringBuilder();
    format(node, sb);

    return sb.toString();
  }

  private void format(AST node, StringBuilder sb) throws EvaluationException, ParameterException {
    if (node == null) return;

    switch (node.getType()) {
      case ASSIGNEE:
      case STRING:
      case VARIABLE:
      case NUMBER:
      case HEXNUMBER:
        {
          sb.append(node.getText());
          return;
        }
      case UNARY_OPERATOR:
        {
          if (!"+".equals(node.getText())) {
            sb.append(node.getText());
          }
          format(node.getFirstChild(), sb);
          return;
        }
      case OPERATOR:
        {
          int currentLevel = ORDER_OF_OPERATIONS.get(node.getText());

          AST child = node.getFirstChild();
          while (child != null) {
            if (child.getType() == OPERATOR) {
              int childLevel = ORDER_OF_OPERATIONS.get(child.getText());
              if (currentLevel < childLevel) sb.append("(");
              format(child, sb);
              if (currentLevel < childLevel) sb.append(")");
            } else {
              format(child, sb);
            }

            child = child.getNextSibling();

            if (child != null) sb.append(' ').append(node.getText()).append(' ');
          }

          return;
        }
      case FUNCTION:
        {
          sb.append(node.getText()).append("(");
          AST child = node.getFirstChild();
          while (child != null) {
            format(child, sb);
            child = child.getNextSibling();
            if (child != null) sb.append(", ");
          }

          sb.append(")");
          return;
        }
      default:
        throw new EvaluationException(
            String.format("Unknown node type: name=%s, type=%d", node.getText(), node.getType()));
    }
  }
}
