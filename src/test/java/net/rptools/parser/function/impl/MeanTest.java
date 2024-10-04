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

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;
import org.junit.jupiter.api.Test;

public class MeanTest {
  @Test
  public void testEvaluate() throws EvaluationException, ParameterException {
    Mean mean = new Mean();

    assertEquals(
        new BigDecimal(10), mean.childEvaluate(null, null, null, createArgs(new BigDecimal(10))));
    assertEquals(
        new BigDecimal(6),
        mean.childEvaluate(null, null, null, createArgs(new BigDecimal(10), new BigDecimal(2))));
    assertEquals(
        new BigDecimal(5),
        mean.childEvaluate(
            null,
            null,
            null,
            createArgs(new BigDecimal(10), new BigDecimal(2), new BigDecimal(3))));
  }

  private List<Object> createArgs(Object... arguments) {
    List<Object> ret = new ArrayList<Object>();
    for (Object o : arguments) ret.add(o);

    return ret;
  }
}
