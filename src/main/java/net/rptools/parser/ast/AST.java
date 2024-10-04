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

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An immutable node in the AST.
 *
 * <p>Each node is associated with a string value, which is the corresponding text from the input.
 * This text does not include any child's text, but when combined with all children's text can
 * produce a string equivalent to the original input.
 *
 * <p>Each possible expression type is represented by an implementation of {@code expr}, so that
 * pattern matching can be used to analyze and manipulate the AST.
 */
public sealed interface AST {
  String text();

  default boolean equalsTree(AST other) {
    // Everything is implemented as records, which makes this trivial.
    return this.equals(other);
  }

  /**
   * Represents this AST as an s-expression.
   *
   * <p>For nodes with no children, this is just the text of the node. For nodes with children, this
   * is a LISP-style list of the form `( {node text} {child 1} {child 2} ... )` where each child is
   * also converted to an s-expression.
   *
   * <p>This is useful for having an easy-to-compare texture representation of the tree structure.
   *
   * @return The s-expression representation of the node.
   */
  default String toStringTree() {
    return " " + toStringList(this);
  }

  private static String toStringList(AST node) {
    var children = getChildren(node);
    if (children.isEmpty()) {
      return node.text();
    }

    return "( "
        + node.text()
        + " "
        + children.stream().map(AST::toStringList).collect(Collectors.joining(" "))
        + " )";
  }

  private static List<AST> getChildren(AST node) {
    return switch (node) {
      case Variable variable -> List.of();
      case PromptVariable promptVariable -> List.of();
      case NumberLiteral numberLiteral -> List.of();
      case StringLiteral stringLiteral -> List.of();
      case Unary unary -> List.of(unary.operand());
      case Binary binary -> List.of(binary.lhs(), binary.rhs());
      case Assignment assignment -> List.of(assignment.lhs(), assignment.rhs());
      case FunctionCall functionCall -> functionCall.parameters();
    };
  }

  record Variable(String text, String variable) implements AST {}

  record PromptVariable(String text, String variable) implements AST {}

  record NumberLiteral(String text, BigDecimal value) implements AST {}

  record StringLiteral(String text, String value) implements AST {}

  record Unary(String text, UnaryOperator operator, AST operand) implements AST {}

  record Binary(String text, BinaryOperator operator, AST lhs, AST rhs) implements AST {}

  record Assignment(String text, Variable lhs, AST rhs) implements AST {}

  record FunctionCall(String text, String function, List<AST> parameters) implements AST {
    public FunctionCall(String text, String function, List<AST> parameters) {
      this.text = text;
      this.function = function;
      // Make the parameters immutable.
      this.parameters = List.copyOf(parameters);
    }
  }
}
