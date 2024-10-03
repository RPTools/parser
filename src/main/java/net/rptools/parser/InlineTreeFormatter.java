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
import net.rptools.parser.ast.AssignmentNode;
import net.rptools.parser.ast.BinaryNode;
import net.rptools.parser.ast.ExpressionNode;
import net.rptools.parser.ast.FunctionCallNode;
import net.rptools.parser.ast.NumberNode;
import net.rptools.parser.ast.PromptVariableNode;
import net.rptools.parser.ast.StringNode;
import net.rptools.parser.ast.UnaryNode;
import net.rptools.parser.ast.VariableNode;

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

  public String format(ExpressionNode node) {
    StringBuilder sb = new StringBuilder();
    format(node, sb, STRONGEST_BINDING);

    return sb.toString();
  }

  private int getOrderOfOperator(String op) {
    Integer result = ORDER_OF_OPERATIONS.get(op);
    // revert to a default high order of for any not mapped operator
    return result == null ? STRONGEST_BINDING : result;
  }

  private void format(ExpressionNode node, StringBuilder sb, int binding) {
    if (node == null) return;

    switch (node) {
      case FunctionCallNode functionCallNode -> {
        sb.append(functionCallNode.function()).append("(");
        boolean first = true;
        for (var child : functionCallNode.parameters()) {
          if (!first) {
            sb.append(", ");
          }
          first = false;

          format(child, sb, STRONGEST_BINDING);
        }
        sb.append(")");
      }
      case PromptVariableNode promptVariableNode ->
          sb.append("?").append(promptVariableNode.variable());
      case VariableNode variableNode -> sb.append(variableNode.variable());
      case NumberNode numberNode -> sb.append(numberNode.value().toPlainString());
      case StringNode stringNode ->
          sb.append(stringNode.quote()).append(stringNode.value()).append(stringNode.quote());
      case UnaryNode unaryNode -> {
        sb.append(unaryNode.operator().asText());
        format(unaryNode.operand(), sb, binding);
      }
      case BinaryNode binaryNode -> {
        var asText = binaryNode.operator().asText();
        int precedence = getOrderOfOperator(asText);
        if (precedence > binding) {
          sb.append("(");
          format(binaryNode.lhs(), sb, STRONGEST_BINDING);
          sb.append(" ").append(asText).append(" ");
          format(binaryNode.rhs(), sb, STRONGEST_BINDING);
          sb.append(")");
        } else {
          format(binaryNode.lhs(), sb, precedence);
          sb.append(" ").append(asText).append(" ");
          format(binaryNode.rhs(), sb, precedence);
        }
      }
      case AssignmentNode assignmentNode -> {
        sb.append(assignmentNode.lhs()).append(" = ");
        format(assignmentNode.rhs(), sb, ORDER_OF_OPERATIONS.get("="));
      }
    }
  }
}
