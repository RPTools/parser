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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.rptools.parser.Parser;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;

public class Median extends AbstractFunction {
  public Median() {
    super(1, -1, "median");
  }

  @Override
  public Object childEvaluate(Parser parser, String functionName, List<Object> parameters)
      throws EvaluationException, ParameterException {
    if (parameters.size() == 1) {
      // unary usage
      return parameters.get(0);
    } else {

      Collections.sort(
          parameters,
          new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
              BigDecimal d1 = (BigDecimal) o1;
              BigDecimal d2 = (BigDecimal) o2;

              return d1.compareTo(d2);
            }
          });

      if (parameters.size() % 2 == 0) {
        // There are an even number, you have to round between the 2 middle numbers

        BigDecimal d1 = (BigDecimal) parameters.get(parameters.size() / 2 - 1);
        BigDecimal d2 = (BigDecimal) parameters.get(parameters.size() / 2);

        return d1.add(d2).divide(new BigDecimal(2), MathContext.DECIMAL128);
      } else {
        // There are an odd number, select the middle one.

        return (BigDecimal) parameters.get(parameters.size() / 2);
      }
    }
  }

  @Override
  public void checkParameters(String functionName, List<Object> parameters)
      throws ParameterException {
    super.checkParameters(functionName, parameters);

    for (Object param : parameters) {
      if (!(param instanceof BigDecimal))
        throw new ParameterException(
            String.format(
                "Illegal argument type %s, expecting %s",
                param.getClass().getName(), BigDecimal.class.getName()));
    }
  }
}
