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
package net.rptools.parser.function.impl;

import java.util.List;
import net.rptools.parser.Expression;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.ParameterException;

public class Eval extends AbstractFunction {
  public Eval() {
    super(1, -1, "eval");
  }

  @Override
  public Object childEvaluate(Parser parser, String functionName, List<Object> parameters)
      throws ParserException {

    Object ret = null;

    for (Object p : parameters) {
      String x = (String) p;

      Expression expression;
      try {
        expression = parser.parseExpression(x);
      } catch (ParserException e) {
        throw new ParameterException(String.format("Unable to evaluate expression %s", x));
      }

      ret = expression.evaluate();
    }

    return ret;
  }

  @Override
  public void checkParameters(String functionName, List<Object> parameters)
      throws ParameterException {
    super.checkParameters(functionName, parameters);

    for (Object param : parameters) {
      if (!(param instanceof String))
        throw new ParameterException(
            String.format(
                "Illegal argument type %s, expecting %s",
                param.getClass().getName(), String.class.getName()));
    }
  }
}
