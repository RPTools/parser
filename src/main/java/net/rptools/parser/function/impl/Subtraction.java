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
import net.rptools.parser.function.AbstractNumberFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

public class Subtraction extends AbstractNumberFunction {
  public Subtraction() {
    super(1, -1, "subtract", "-");
  }

  @Override
  public Object childEvaluate(Parser parser, String functionName, List<Object> parameters)
      throws EvaluationException, ParameterException {
    if (parameters.size() == 1) {
      // unary usage
      BigDecimal f = (BigDecimal) parameters.get(0);
      return f.negate();
    } else {
      BigDecimal total = null;
      boolean first = true;

      for (Object param : parameters) {
        BigDecimal n = (BigDecimal) param;

        if (first) {
          total = n;
          first = false;
        } else {
          total = total.subtract(n);
        }
      }

      return total;
    }
  }
}
