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
import net.rptools.parser.ast.AssignmentNode;
import net.rptools.parser.ast.BinaryNode;
import net.rptools.parser.ast.ExpressionNode;
import net.rptools.parser.ast.FunctionCallNode;
import net.rptools.parser.ast.NumberNode;
import net.rptools.parser.ast.PromptVariableNode;
import net.rptools.parser.ast.StringNode;
import net.rptools.parser.ast.UnaryNode;
import net.rptools.parser.ast.VariableNode;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.Function;

public class EvaluationTreeParser {
  private final Parser parser;

  public EvaluationTreeParser(Parser parser) {
    this.parser = parser;
  }

  public Object evaluate(ExpressionNode node, VariableResolver resolver) throws ParserException {
    return switch (node) {
      case FunctionCallNode functionCallNode -> {
        String name = functionCallNode.function();

        var params = new ArrayList<>();
        for (var child : functionCallNode.parameters()) {
          params.add(evaluate(child, resolver));
        }

        Function function = parser.getFunction(name);
        if (function == null) {
          throw new EvaluationException(String.format("Undefined function: %s", name));
        }
        yield function.evaluate(parser, resolver, name, params);
      }
      case PromptVariableNode promptVariableNode -> {
        String name = promptVariableNode.variable();
        if (!resolver.containsVariable(name, VariableModifiers.Prompt)) {
          throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        yield resolver.getVariable(promptVariableNode.variable(), VariableModifiers.Prompt);
      }
      case VariableNode variableNode -> {
        String name = variableNode.variable();
        if (!resolver.containsVariable(name, VariableModifiers.None)) {
          throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        yield resolver.getVariable(name, VariableModifiers.None);
      }
      case NumberNode numberNode -> numberNode.value();
      case StringNode stringNode -> stringNode.value();
      case UnaryNode unaryNode -> {
        var child = evaluate(unaryNode.operand(), resolver);

        var functionName = unaryNode.operator().asText();
        Function function = parser.getFunction(functionName);
        if (function == null) {
          throw new EvaluationException(
              String.format("Undefined unary function: %s", functionName));
        }
        yield function.evaluate(parser, resolver, functionName, List.of(child));
      }
      case BinaryNode binaryNode -> {
        var lhs = evaluate(binaryNode.lhs(), resolver);
        var rhs = evaluate(binaryNode.rhs(), resolver);

        var functionName = binaryNode.operator().asText();
        Function function = parser.getFunction(functionName);
        if (function == null) {
          throw new EvaluationException(
              String.format("Undefined binary function: %s", functionName));
        }
        yield function.evaluate(parser, resolver, functionName, List.of(lhs, rhs));
      }
      case AssignmentNode assignmentNode -> {
        var lhs = assignmentNode.lhs();
        var rhs = evaluate(assignmentNode.rhs(), resolver);

        var functionName = "=";
        Function function = parser.getFunction(functionName);
        if (function == null) {
          throw new EvaluationException(
              String.format("Undefined binary function: %s", functionName));
        }
        yield function.evaluate(parser, resolver, functionName, List.of(lhs, rhs));
      }
    };
  }
}
