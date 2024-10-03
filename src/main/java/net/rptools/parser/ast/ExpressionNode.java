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
package net.rptools.parser.ast;

import java.util.List;

public sealed interface ExpressionNode
    permits FunctionCallNode,
        PromptVariableNode,
        VariableNode,
        NumberNode,
        StringNode,
        UnaryNode,
        BinaryNode,
        AssignmentNode {

  default boolean equalsTree(ExpressionNode other) {
    // Everything is implemented as records, which makes this trivial.
    return this.equals(other);
  }

  default String toStringTree() {
    return " " + toStringList();
  }

  default String toStringList() {
    var parts = getParts();
    return switch (parts.size()) {
      case 0 -> "";
      case 1 -> parts.getFirst();
      default -> "( " + String.join(" ", parts) + " )";
    };
  }

  List<String> getParts();
}
