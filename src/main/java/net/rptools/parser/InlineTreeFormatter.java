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

import java.util.HashMap;
import java.util.Map;
import net.rptools.parser.ast.AST;
import net.rptools.parser.ast.UnaryOperator;

public class InlineTreeFormatter {

  private static final Map<String, Integer> ORDER_OF_OPERATIONS = new HashMap<>();
  private static final Integer STRONGEST_BINDING = Integer.MAX_VALUE;

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

  public String format(AST node) {
    StringBuilder sb = new StringBuilder();
    format(node, sb, STRONGEST_BINDING);

    return sb.toString();
  }

  private int getOrderOfOperator(String op) {
    Integer result = ORDER_OF_OPERATIONS.get(op);
    // revert to a default high order of for any not mapped operator
    return result == null ? STRONGEST_BINDING : result;
  }

  private void format(AST node, StringBuilder sb, int binding) {
    if (node == null) return;

    switch (node) {
      case AST.Variable variable -> sb.append(variable.variable());
      case AST.PromptVariable promptVariable -> sb.append("?").append(promptVariable.variable());
      case AST.NumberLiteral numberLiteral -> sb.append(numberLiteral.value().toPlainString());
      case AST.StringLiteral stringLiteral -> sb.append(stringLiteral.text());
      case AST.Unary unary -> {
        if (unary.operator() != UnaryOperator.Plus) {
          sb.append(unary.text());
        }
        format(unary.operand(), sb, binding);
      }
      case AST.Binary binary -> {
        int precedence = getOrderOfOperator(binary.operator().asText());
        if (precedence > binding) {
          sb.append("(");
          format(binary.lhs(), sb, STRONGEST_BINDING);
          sb.append(" ").append(binary.text()).append(" ");
          format(binary.rhs(), sb, STRONGEST_BINDING);
          sb.append(")");
        } else {
          format(binary.lhs(), sb, precedence);
          sb.append(" ").append(binary.text()).append(" ");
          format(binary.rhs(), sb, precedence);
        }
      }
      case AST.Assignment assignment -> {
        var order = getOrderOfOperator("=");
        format(assignment.lhs(), sb, order);
        sb.append(" ").append(assignment.text()).append(" ");
        format(assignment.rhs(), sb, order);
      }
      case AST.FunctionCall functionCall -> {
        sb.append(functionCall.function()).append("(");
        boolean first = true;
        for (var child : functionCall.parameters()) {
          if (!first) {
            sb.append(", ");
          }
          first = false;

          format(child, sb, STRONGEST_BINDING);
        }
        sb.append(")");
      }
    }
  }
}
