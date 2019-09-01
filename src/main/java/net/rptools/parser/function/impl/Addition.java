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

import java.math.BigDecimal;
import java.util.List;
import net.rptools.parser.Parser;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

public class Addition extends AbstractFunction {
  public Addition() {
    super(1, -1, "add", "sum", "+", "concat");
  }

  @Override
  public Object childEvaluate(Parser parser, String functionName, List<Object> parameters)
      throws EvaluationException, ParameterException {
    if (parameters.size() == 1) {
      // unary usage
      return parameters.get(0);
    } else {

      if (containsString(parameters)) {
        StringBuilder sb = new StringBuilder();
        for (Object param : parameters) {
          sb.append(param.toString());
        }

        return sb.toString();
      } else {
        BigDecimal total = new BigDecimal(0);

        for (Object param : parameters) {
          BigDecimal n = (BigDecimal) param;

          total = total.add(n);
        }

        return total;
      }
    }
  }

  @Override
  public void checkParameters(String functionName, List<Object> parameters)
      throws ParameterException {
    super.checkParameters(functionName, parameters);

    for (Object param : parameters) {
      if (!(param instanceof BigDecimal || param instanceof String))
        throw new ParameterException(
            String.format(
                "Illegal argument type %s, expecting %s or %s",
                param.getClass().getName(), BigDecimal.class.getName(), String.class.getName()));
    }
  }
}
