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
import java.util.ArrayList;
import net.rptools.parser.ast.AST;
import net.rptools.parser.ast.BinaryOperator;
import net.rptools.parser.ast.UnaryOperator;
import org.antlr.v4.runtime.Token;

public class AstBuilderVisitor extends ExpressionParserBaseVisitor<AST> {
  @Override
  public AST visitBracket(ExpressionParser.BracketContext ctx) {
    return visit(ctx.child);
  }

  @Override
  public AST.Variable visitVariable(ExpressionParser.VariableContext ctx) {
    return new AST.Variable(ctx.getText(), ctx.id.getText());
  }

  @Override
  public AST.PromptVariable visitPromptvariable(ExpressionParser.PromptvariableContext ctx) {
    var id = ctx.id.getText();
    return new AST.PromptVariable(ctx.getText(), id);
  }

  @Override
  public AST.NumberLiteral visitDecimal(ExpressionParser.DecimalContext ctx) {
    var value = new BigDecimal(ctx.number.getText());
    return new AST.NumberLiteral(ctx.getText(), value);
  }

  @Override
  public AST.NumberLiteral visitHexadecimal(ExpressionParser.HexadecimalContext ctx) {
    var value = new BigInteger(ctx.number.getText().substring(2), 16);
    return new AST.NumberLiteral(ctx.getText(), new BigDecimal(value));
  }

  @Override
  public AST.StringLiteral visitString(ExpressionParser.StringContext ctx) {
    // First and last character are the quotes, so discard those.
    var text = ctx.string.getText();
    var value = text.substring(1, text.length() - 1);
    return new AST.StringLiteral(ctx.getText(), value);
  }

  @Override
  public AST.Unary visitUnary(ExpressionParser.UnaryContext ctx) {
    return buildUnary(ctx.operator, ctx.operand);
  }

  @Override
  public AST.Binary visitCompare(ExpressionParser.CompareContext ctx) {
    return buildBinary(ctx.operator, ctx.lhs, ctx.rhs);
  }

  @Override
  public AST.Binary visitOr(ExpressionParser.OrContext ctx) {
    return buildBinary(ctx.operator, ctx.lhs, ctx.rhs);
  }

  @Override
  public AST.Binary visitAnd(ExpressionParser.AndContext ctx) {
    return buildBinary(ctx.operator, ctx.lhs, ctx.rhs);
  }

  @Override
  public AST.Binary visitAdditive(ExpressionParser.AdditiveContext ctx) {
    return buildBinary(ctx.operator, ctx.lhs, ctx.rhs);
  }

  @Override
  public AST.Binary visitMultiplicative(ExpressionParser.MultiplicativeContext ctx) {
    return buildBinary(ctx.operator, ctx.lhs, ctx.rhs);
  }

  @Override
  public AST.Binary visitPower(ExpressionParser.PowerContext ctx) {
    return buildBinary(ctx.operator, ctx.lhs, ctx.rhs);
  }

  @Override
  public AST.Assignment visitAssignment(ExpressionParser.AssignmentContext ctx) {
    var lhs = new AST.Variable(ctx.id.getText(), ctx.id.getText());
    var rhs = visit(ctx.rhs);
    return new AST.Assignment(ctx.operator.getText(), lhs, rhs);
  }

  @Override
  public AST.FunctionCall visitFunction(ExpressionParser.FunctionContext ctx) {
    var function = ctx.id.getText();
    var params = new ArrayList<AST>();
    for (var expr : ctx.expr()) {
      params.add(visit(expr));
    }
    return new AST.FunctionCall(function, function, params);
  }

  private AST.Unary buildUnary(Token operatorToken, ExpressionParser.ExprContext operand) {
    var operator =
        switch (operatorToken.getType()) {
          case ExpressionLexer.PLUS -> UnaryOperator.Plus;
          case ExpressionLexer.MINUS -> UnaryOperator.Minus;
          case ExpressionLexer.NOT -> UnaryOperator.Not;
          default ->
              throw new RuntimeException("Invalid unary operator " + operatorToken.getText());
        };

    return new AST.Unary(operatorToken.getText(), operator, visit(operand));
  }

  private AST.Binary buildBinary(
      Token operatorToken, ExpressionParser.ExprContext lhs, ExpressionParser.ExprContext rhs) {
    var operator =
        switch (operatorToken.getType()) {
          case ExpressionLexer.AND -> BinaryOperator.And;
          case ExpressionLexer.OR -> BinaryOperator.Or;
          case ExpressionLexer.PLUS -> BinaryOperator.Plus;
          case ExpressionLexer.MINUS -> BinaryOperator.Minus;
          case ExpressionLexer.MULTIPLY -> BinaryOperator.Multiply;
          case ExpressionLexer.DIVIDE -> BinaryOperator.Divide;
          case ExpressionLexer.POWER -> BinaryOperator.Power;
          case ExpressionLexer.GE -> BinaryOperator.GE;
          case ExpressionLexer.GT -> BinaryOperator.GT;
          case ExpressionLexer.LT -> BinaryOperator.LT;
          case ExpressionLexer.LE -> BinaryOperator.LE;
          case ExpressionLexer.EQUALS -> BinaryOperator.EQ;
          case ExpressionLexer.NOTEQUALS -> BinaryOperator.NE;
          default ->
              throw new RuntimeException("Invalid binary operator " + operatorToken.getText());
        };

    return new AST.Binary(operatorToken.getText(), operator, visit(lhs), visit(rhs));
  }
}
