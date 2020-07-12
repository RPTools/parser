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
import java.math.MathContext;
import java.util.List;
import net.rptools.parser.Parser;
import net.rptools.parser.VariableResolver;
import net.rptools.parser.function.AbstractNumberFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

public class Power extends AbstractNumberFunction {
  private static final int DEFAULT_SCALE = 2;

  public Power() {
    super(1, 2, "sqr", "square", "power", "factor", "pow", "^");
  }

  @Override
  public Object childEvaluate(
      Parser parser, VariableResolver resolver, String functionName, List<Object> parameters)
      throws EvaluationException, ParameterException {
    BigDecimal value = (BigDecimal) parameters.get(0);

    int scale = DEFAULT_SCALE;
    if (parameters.size() == 2) {
      scale = ((BigDecimal) parameters.get(1)).intValue();
    }

    return value.pow(scale, MathContext.DECIMAL128);
  }
}
