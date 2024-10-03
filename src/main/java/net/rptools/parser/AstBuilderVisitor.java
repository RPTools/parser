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

import java.math.BigDecimal;
import java.math.BigInteger;
import net.rptools.parser.ast.AssignmentNode;
import net.rptools.parser.ast.BinaryNode;
import net.rptools.parser.ast.ExpressionNode;
import net.rptools.parser.ast.FunctionCallNode;
import net.rptools.parser.ast.NumberNode;
import net.rptools.parser.ast.PromptVariableNode;
import net.rptools.parser.ast.StringNode;
import net.rptools.parser.ast.UnaryNode;
import net.rptools.parser.ast.VariableNode;

public class AstBuilderVisitor extends ExpressionParserBaseVisitor<ExpressionNode> {
  @Override
  public ExpressionNode visitFunction(ExpressionParser.FunctionContext ctx) {
    var function = ctx.id.getText();
    var params = ctx.expr().stream().map(this::visit).toList();

    return new FunctionCallNode(function, params);
  }

  @Override
  public ExpressionNode visitVariable(ExpressionParser.VariableContext ctx) {
    return new VariableNode(ctx.id.getText());
  }

  @Override
  public ExpressionNode visitPromptvariable(ExpressionParser.PromptvariableContext ctx) {
    var id = ctx.id.getText();
    return new PromptVariableNode(id);
  }

  @Override
  public ExpressionNode visitString(ExpressionParser.StringContext ctx) {
    // First and last character are the quotes, so discard those.
    var text = ctx.string.getText();
    var quoteChar = text.charAt(0);
    var value = text.substring(1, text.length() - 1);
    return new StringNode(value, quoteChar);
  }

  @Override
  public ExpressionNode visitDecimal(ExpressionParser.DecimalContext ctx) {
    var value = new BigDecimal(ctx.number.getText());
    return new NumberNode(value);
  }

  @Override
  public ExpressionNode visitHexadecimal(ExpressionParser.HexadecimalContext ctx) {
    var value = new BigInteger(ctx.number.getText().substring(2), 16);
    return new NumberNode(new BigDecimal(value));
  }

  @Override
  public ExpressionNode visitBracket(ExpressionParser.BracketContext ctx) {
    return visit(ctx.child);
  }

  @Override
  public ExpressionNode visitCompare(ExpressionParser.CompareContext ctx) {
    var lhs = visit(ctx.lhs);
    var rhs = visit(ctx.rhs);
    var operator =
        switch (ctx.operator.getType()) {
          case ExpressionLexer.GE -> BinaryNode.Operator.GE;
          case ExpressionLexer.GT -> BinaryNode.Operator.GT;
          case ExpressionLexer.LT -> BinaryNode.Operator.LT;
          case ExpressionLexer.LE -> BinaryNode.Operator.LE;
          case ExpressionLexer.EQUALS -> BinaryNode.Operator.EQ;
          case ExpressionLexer.NOTEQUALS -> BinaryNode.Operator.NE;
          default ->
              throw new RuntimeException("Invalid comparison operator " + ctx.operator.getText());
        };

    return new BinaryNode(operator, lhs, rhs);
  }

  @Override
  public ExpressionNode visitOr(ExpressionParser.OrContext ctx) {
    var lhs = visit(ctx.lhs);
    var rhs = visit(ctx.rhs);
    return new BinaryNode(BinaryNode.Operator.Or, lhs, rhs);
  }

  @Override
  public ExpressionNode visitAnd(ExpressionParser.AndContext ctx) {
    var lhs = visit(ctx.lhs);
    var rhs = visit(ctx.rhs);
    return new BinaryNode(BinaryNode.Operator.And, lhs, rhs);
  }

  @Override
  public ExpressionNode visitUnary(ExpressionParser.UnaryContext ctx) {
    var operand = visit(ctx.operand);
    var operator =
        switch (ctx.operator.getType()) {
          case ExpressionLexer.PLUS -> UnaryNode.Operator.Plus;
          case ExpressionLexer.MINUS -> UnaryNode.Operator.Minus;
          case ExpressionLexer.NOT -> UnaryNode.Operator.Not;
          default -> throw new RuntimeException("Invalid unary operator " + ctx.operator.getText());
        };
    return new UnaryNode(operator, operand);
  }

  @Override
  public ExpressionNode visitAdditive(ExpressionParser.AdditiveContext ctx) {
    var lhs = visit(ctx.lhs);
    var rhs = visit(ctx.rhs);

    var operator =
        switch (ctx.operator.getType()) {
          case ExpressionLexer.PLUS -> BinaryNode.Operator.Plus;
          case ExpressionLexer.MINUS -> BinaryNode.Operator.Minus;
          default ->
              throw new RuntimeException(
                  "Invalid additive operator "
                      + ctx.operator.getText()
                      + " ("
                      + ctx.operator.getType()
                      + ")");
        };
    return new BinaryNode(operator, lhs, rhs);
  }

  @Override
  public ExpressionNode visitMultiplicative(ExpressionParser.MultiplicativeContext ctx) {
    var lhs = visit(ctx.lhs);
    var rhs = visit(ctx.rhs);
    var operator =
        switch (ctx.operator.getType()) {
          case ExpressionLexer.MULTIPLY -> BinaryNode.Operator.Multiply;
          case ExpressionLexer.DIVIDE -> BinaryNode.Operator.Divide;
          default ->
              throw new RuntimeException(
                  "Invalid multiplicative operator " + ctx.operator.getText());
        };
    return new BinaryNode(operator, lhs, rhs);
  }

  @Override
  public ExpressionNode visitPower(ExpressionParser.PowerContext ctx) {
    var lhs = visit(ctx.lhs);
    var rhs = visit(ctx.rhs);
    var operator =
        switch (ctx.operator.getType()) {
          case ExpressionLexer.POWER -> BinaryNode.Operator.Power;
          default -> throw new RuntimeException("Invalid power operator " + ctx.operator.getText());
        };
    return new BinaryNode(operator, lhs, rhs);
  }

  @Override
  public ExpressionNode visitAssignment(ExpressionParser.AssignmentContext ctx) {
    var lhs = ctx.id.getText();
    var rhs = visit(ctx.rhs);

    return new AssignmentNode(lhs, rhs);
  }
}
