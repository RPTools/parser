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
package net.rptools.parser.transform;

import static org.junit.jupiter.api.Assertions.*;

import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import org.junit.jupiter.api.Test;

public class RegexpStringTransformerTest {

  @Test
  public void testSimpleReplacement() throws ParserException {
    Parser p = new Parser();

    p.addTransformer(new RegexpStringTransformer(new String[] {"foo"}, new String[] {"bar"}));

    assertEquals("10 + bar + 17", p.parseExpression("10 + foo + 17").format());
    assertEquals("10 + bard + 17", p.parseExpression("10 + food + 17").format());
  }

  @Test
  public void testReplacementWithSubstitutions() throws ParserException {
    Parser p = new Parser();

    p.addTransformer(
        new RegexpStringTransformer(
            new String[] {"(\\d+)\\s*\\^\\s*(\\d*)"}, new String[] {"pow($1, $2)"}));

    assertEquals("pow(3, 7)", p.parseExpression("3^7").format());
    assertEquals("pow(3, 7)", p.parseExpression("3 ^ 7").format());
    assertEquals("1 + 2 + pow(3, 7) + 10", p.parseExpression("1 + 2 + 3^7 + 10").format());
  }
}
