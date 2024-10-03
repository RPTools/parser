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

import java.util.ArrayList;
import java.util.List;
import net.rptools.parser.ast.AST;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.Function;

public class EvaluationTreeParser {
  private final Parser parser;

  public EvaluationTreeParser(Parser parser) {
    this.parser = parser;
  }

  public Object evaluate(AST node, VariableResolver resolver) throws ParserException {
    return switch (node) {
      case AST.Variable variable -> {
        String name = variable.variable();
        if (!resolver.containsVariable(name, VariableModifiers.None)) {
          throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        yield resolver.getVariable(name, VariableModifiers.None);
      }
      case AST.PromptVariable promptVariable -> {
        String name = promptVariable.variable();
        if (!resolver.containsVariable(name, VariableModifiers.Prompt)) {
          throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        yield resolver.getVariable(promptVariable.variable(), VariableModifiers.Prompt);
      }
      case AST.NumberLiteral numberLiteral -> numberLiteral.value();
      case AST.StringLiteral stringLiteral -> stringLiteral.value();
      case AST.Unary unary -> {
        var child = evaluate(unary.operand(), resolver);

        var functionName = unary.operator().asText();
        Function function = parser.getFunction(functionName);
        if (function == null) {
          throw new EvaluationException(
              String.format("Undefined unary function: %s", functionName));
        }
        yield function.evaluate(parser, resolver, functionName, List.of(child));
      }
      case AST.Binary binary -> {
        var lhs = evaluate(binary.lhs(), resolver);
        var rhs = evaluate(binary.rhs(), resolver);

        var functionName = binary.operator().asText();
        Function function = parser.getFunction(functionName);
        if (function == null) {
          throw new EvaluationException(
              String.format("Undefined binary function: %s", functionName));
        }
        yield function.evaluate(parser, resolver, functionName, List.of(lhs, rhs));
      }
      case AST.Assignment assignment -> {
        // Note: don't evaluate the left-hand side - we don't what to look up the variable!
        var lhs = assignment.lhs().text();
        var rhs = evaluate(assignment.rhs(), resolver);

        var functionName = "=";
        Function function = parser.getFunction(functionName);
        if (function == null) {
          throw new EvaluationException(
              String.format("Undefined binary function: %s", functionName));
        }
        yield function.evaluate(parser, resolver, functionName, List.of(lhs, rhs));
      }
      case AST.FunctionCall functionCall -> {
        String name = functionCall.function();

        var params = new ArrayList<>();
        for (var child : functionCall.parameters()) {
          params.add(evaluate(child, resolver));
        }

        Function function = parser.getFunction(name);
        if (function == null) {
          throw new EvaluationException(String.format("Undefined function: %s", name));
        }
        yield function.evaluate(parser, resolver, name, params);
      }
    };
  }
}
