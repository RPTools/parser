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
import java.util.ArrayList;
import java.util.logging.Logger;
import net.rptools.parser.ast.AST;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.Function;

public class DeterministicTreeParser {
  private static final Logger log = Logger.getLogger(DeterministicTreeParser.class.getName());

  private final Parser parser;

  public DeterministicTreeParser(Parser parser) {
    this.parser = parser;
  }

  public AST evaluate(AST node, VariableResolver resolver) throws ParserException {
    return switch (node) {
      case AST.Variable variable -> {
        String name = variable.variable();
        if (!resolver.containsVariable(name, VariableModifiers.None)) {
          throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        Object value = resolver.getVariable(name, VariableModifiers.None);
        yield createNode(value);
      }
      case AST.PromptVariable promptVariable -> {
        String name = promptVariable.variable();
        if (!resolver.containsVariable(name, VariableModifiers.None)) {
          throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        Object value = resolver.getVariable(name, VariableModifiers.None);
        yield createNode(value);
      }
      case AST.NumberLiteral numberLiteral -> numberLiteral;
      case AST.StringLiteral stringLiteral -> stringLiteral;
      case AST.Unary unary ->
          new AST.Unary(unary.text(), unary.operator(), evaluate(unary.operand(), resolver));
      case AST.Binary binary ->
          new AST.Binary(
              binary.text(),
              binary.operator(),
              evaluate(binary.lhs(), resolver),
              evaluate(binary.rhs(), resolver));
      case AST.Assignment assignment ->
          new AST.Assignment(
              assignment.text(), assignment.lhs(), evaluate(assignment.rhs(), resolver));
      case AST.FunctionCall functionCall -> {
        String name = functionCall.function();
        Function function = parser.getFunction(name);
        if (function == null) {
          throw new EvaluationException(String.format("Undefined function: %s", name));
        }

        if (!function.isDeterministic()) {
          Object value = parser.getEvaluationTreeParser().evaluate(functionCall, resolver);
          yield createNode(value);
        } else {
          var parameters = new ArrayList<AST>();
          for (var param : functionCall.parameters()) {
            var result = evaluate(param, resolver);
            parameters.add(result);
          }
          yield new AST.FunctionCall(functionCall.text(), functionCall.function(), parameters);
        }
      }
    };
  }

  private AST createNode(Object value) {
    if (value instanceof BigDecimal bd) {
      return new AST.NumberLiteral(bd.toPlainString(), bd);
    } else {
      var string = value.toString();
      return new AST.StringLiteral(string, string);
    }
  }
}
