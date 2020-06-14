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

import static net.rptools.parser.ExpressionParserTokenTypes.*;

import antlr.collections.AST;
import java.util.HashMap;
import java.util.Map;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

public class InlineTreeFormatter {

  private static Map<String, Integer> ORDER_OF_OPERATIONS = new HashMap<String, Integer>();

  static {
    // P(1) E(2) MD(3) AS(4):
    // provide order-of for all operators as per
    // https://en.wikipedia.org/wiki/Order_of_operations#Programming_languages
    // Note that parser historically places operator = first
    ORDER_OF_OPERATIONS.put("=", 1);
    ORDER_OF_OPERATIONS.put("^", 2);
    ORDER_OF_OPERATIONS.put("*", 3);
    ORDER_OF_OPERATIONS.put("/", 3);
    ORDER_OF_OPERATIONS.put("+", 4);
    ORDER_OF_OPERATIONS.put("-", 4);
    ORDER_OF_OPERATIONS.put("<", 6);
    ORDER_OF_OPERATIONS.put("<=", 6);
    ORDER_OF_OPERATIONS.put(">", 6);
    ORDER_OF_OPERATIONS.put(">=", 6);
    ORDER_OF_OPERATIONS.put("==", 7);
    ORDER_OF_OPERATIONS.put("!=", 7);
    ORDER_OF_OPERATIONS.put("&&", 11);
    ORDER_OF_OPERATIONS.put("||", 13);
  }

  public String format(AST node) throws EvaluationException, ParameterException {
    StringBuilder sb = new StringBuilder();
    format(node, sb);

    return sb.toString();
  }

  private int getOrderOfOperator(String op) {
    Integer result = ORDER_OF_OPERATIONS.get(op);
    // revert to a default high order of for any not mapped operator
    return result == null ? Integer.MAX_VALUE : result;
  }

  private void format(AST node, StringBuilder sb) throws EvaluationException, ParameterException {
    if (node == null) return;

    switch (node.getType()) {
      case ASSIGNEE:
      case STRING:
      case VARIABLE:
      case NUMBER:
      case HEXNUMBER:
      case TRUE:
      case FALSE:
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
          int currentLevel = getOrderOfOperator(node.getText());

          AST child = node.getFirstChild();
          while (child != null) {
            if (child.getType() == OPERATOR) {
              int childLevel = getOrderOfOperator(child.getText());
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
