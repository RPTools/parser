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
import net.rptools.parser.VariableResolver;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

public class StrEquals extends AbstractFunction {
  public StrEquals() {
    super(2, -1, "eqs", "strEquals", "equalsStrict");
  }

  @Override
  public Object childEvaluate(
      Parser parser, VariableResolver resolver, String functionName, List<Object> parameters)
      throws EvaluationException, ParameterException {
    boolean value = true;

    if (!containsOnlyBigDecimals(parameters)) {
      for (int i = 0; i < parameters.size() - 1; i++) {
        String s1 = parameters.get(i).toString();
        String s2 = parameters.get(i + 1).toString();
        value &= s1.trim().equals(s2.trim());
      }
    } else {
      for (int i = 0; i < parameters.size() - 1; i++) {
        BigDecimal d1 = (BigDecimal) parameters.get(i);
        BigDecimal d2 = (BigDecimal) parameters.get(i + 1);

        value &= (d1.compareTo(d2) == 0);
      }
    }

    return value ? BigDecimal.ONE : BigDecimal.ZERO;
  }
}
