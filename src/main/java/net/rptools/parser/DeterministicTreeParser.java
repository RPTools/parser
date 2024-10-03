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

public class DeterministicTreeParser {
  private static final Logger log = Logger.getLogger(DeterministicTreeParser.class.getName());

  private final Parser parser;

  public DeterministicTreeParser(Parser parser) {
    this.parser = parser;
  }

  public ExpressionNode evaluate(ExpressionNode node, VariableResolver resolver)
      throws ParserException {
    return switch (node) {
      case FunctionCallNode functionCallNode -> {
        String name = functionCallNode.function();
        Function function = parser.getFunction(name);
        if (function == null) {
          throw new EvaluationException(String.format("Undefined function: %s", name));
        }

        if (!function.isDeterministic()) {
          Object value = parser.getEvaluationTreeParser().evaluate(node, resolver);
          yield createNode(value);
        } else {
          var parameters = new ArrayList<ExpressionNode>();
          for (var param : functionCallNode.parameters()) {
            var result = evaluate(param, resolver);
            parameters.add(result);
          }
          yield new FunctionCallNode(functionCallNode.function(), parameters);
        }
      }
      case PromptVariableNode promptVariableNode -> {
        String name = promptVariableNode.variable();
        if (!resolver.containsVariable(name, VariableModifiers.None)) {
          throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        Object value = resolver.getVariable(name, VariableModifiers.None);
        yield createNode(value);
      }
      case VariableNode variableNode -> {
        String name = variableNode.variable();
        if (!resolver.containsVariable(name, VariableModifiers.None)) {
          throw new EvaluationException(String.format("Undefined variable: %s", name));
        }
        Object value = resolver.getVariable(name, VariableModifiers.None);
        yield createNode(value);
      }
      case NumberNode numberNode -> numberNode;
      case StringNode stringNode -> stringNode;
      case UnaryNode unaryNode ->
          new UnaryNode(unaryNode.operator(), evaluate(unaryNode.operand(), resolver));
      case BinaryNode binaryNode ->
          new BinaryNode(
              binaryNode.operator(),
              evaluate(binaryNode.lhs(), resolver),
              evaluate(binaryNode.rhs(), resolver));
      case AssignmentNode assignmentNode ->
          new AssignmentNode(assignmentNode.lhs(), evaluate(assignmentNode.rhs(), resolver));
    };
  }

  private ExpressionNode createNode(Object value) {
    if (value instanceof BigDecimal bd) {
      return new NumberNode(bd);
    } else {
      return new StringNode(value.toString(), '"');
    }
  }
}
