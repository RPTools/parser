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

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class ParserTest {

  @Test
  public void testReuseExpression() throws ParserException {

    Expression exp = new Parser().parseExpression("x + 1");

    // use exp once
    VariableResolver vars = new MapVariableResolver();
    vars.setVariable("x", BigDecimal.ONE);
    assertEquals(new BigDecimal(2), exp.evaluate(vars));

    // use exp another time - this is possible because parsing and evaluation
    // is a separate step - the parser does not hang on to an initial variableresolver
    VariableResolver vars2 = new MapVariableResolver();
    vars2.setVariable("x", new BigDecimal(2));
    assertEquals(new BigDecimal(3), exp.evaluate(vars2));
  }

  @ParameterizedTest(name = "{0}; {1}; {2}")
  @CsvFileSource(
      resources = "ParserTest.testSuccessfulParses.csv",
      numLinesToSkip = 1,
      delimiter = ';',
      quoteCharacter = '`',
      ignoreLeadingAndTrailingWhitespace = false)
  public void testSuccessfulParses(String label, String input, String expectedStructure)
      throws ParserException {
    Parser p = new Parser();
    Expression xp = p.parseExpression(input);
    var structure = xp.getTree().toStringTree();
    assertEquals(expectedStructure, structure, "The parsed AST must match the expected structure");
  }
}
