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
package net.rptools.parser.function;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public abstract class AbstractLogicalOperatorFunction extends AbstractFunction {

  public AbstractLogicalOperatorFunction(String... aliases) {
    super(aliases);
  }

  public AbstractLogicalOperatorFunction(int minParameters, int maxParameters, String... aliases) {
    super(minParameters, maxParameters, aliases);
  }

  public AbstractLogicalOperatorFunction(
      int minParameters, int maxParameters, boolean deterministic, String... aliases) {
    super(minParameters, maxParameters, deterministic, aliases);
  }

  @Override
  public void checkParameters(String functionName, List<Object> parameters)
      throws ParameterException {
    super.checkParameters(functionName, parameters);

    for (Object param : parameters) {
      if (!(param instanceof Boolean)
          && !(param instanceof BigDecimal)
          && !(param instanceof BigInteger)
          && !(param instanceof String))
        throw new ParameterException(
            String.format(
                "Illegal argument type %s, expecting %s",
                param == null ? "null" : param.getClass().getName(), BigDecimal.class.getName()));
    }
  }

  protected boolean ConvertToBoolean(Object o) {
    if (o instanceof Boolean) return ((Boolean) o).booleanValue();

    if (o instanceof BigDecimal) return !BigDecimal.ZERO.equals((BigDecimal) o);

    if (o instanceof BigInteger) return !BigInteger.ZERO.equals((BigInteger) o);

    if (o instanceof String) return ((String) o).length() > 0;

    return false;
  }

  protected BigDecimal BooleanAsBigDecimal(boolean b) {
    return b ? BigDecimal.ONE : BigDecimal.ZERO;
  }
}
