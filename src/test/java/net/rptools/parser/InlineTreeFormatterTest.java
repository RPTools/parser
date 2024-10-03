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

import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;
import org.junit.jupiter.api.Test;

public class InlineTreeFormatterTest {
  @Test
  public void testFormatSimple() throws ParserException, EvaluationException, ParameterException {
    compare("200*2", "200 * 2");
    compare("200*VAR+2", "200 * VAR + 2");
    compare("200*(2*4)*7", "200 * 2 * 4 * 7");
  }

  @Test
  public void testFormatComplex() throws ParserException, EvaluationException, ParameterException {
    compare("200+2+2*2", "200 + 2 + 2 * 2");
    compare("200+(2+2)*2", "200 + (2 + 2) * 2");
  }

  @Test
  public void testFormatFunction() throws ParserException, EvaluationException, ParameterException {
    compare("100*func(2)", "100 * func(2)");
    compare("100*func(2,(2+3)*7)", "100 * func(2, (2 + 3) * 7)");
    compare("100*func(2,(2+func2(3))*7)", "100 * func(2, (2 + func2(3)) * 7)");
  }

  @Test
  public void testFormatOperatorAsFunction()
      throws ParserException, EvaluationException, ParameterException {
    compare("sum(200, 2)", "sum(200, 2)");
    compare("100 + 200 + multiply(7, 30)", "100 + 200 + multiply(7, 30)");
  }

  @Test
  public void testFormatAssignment()
      throws ParserException, EvaluationException, ParameterException {
    compare("a=200+7", "a = (200 + 7)");
  }

  @Test
  public void testFormatEval() throws ParserException, EvaluationException, ParameterException {
    compare("eval('2*2')", "eval('2*2')");
  }

  @Test
  public void testLogicalOperators()
      throws ParserException, EvaluationException, ParameterException {
    /**
     * OR : "||" ; AND : "&&" ; NOT : '!' ; EQUALS : "==" ; NOTEQUALS : "!=" ; GE : ">=" ; GT : ">"
     * ; LT : "<" ; LE : "<=" ; TRUE : "true"; FALSE : "false";
     */
    compare("1||1", "1 || 1");
    compare("1||1", "1 || 1");
    compare("1&&1", "1 && 1");

    compare("x<2||x>3", "x < 2 || x > 3");
    compare("1&&1 || 0||1", "1 && 1 || 0 || 1");
    compare("1||1 && 0||1", "1 || 1 && 0 || 1");
    compare("(1||1)&&(0||1)", "(1 || 1) && (0 || 1)");

    compare("!true", "!true");
    compare("1==1", "1 == 1");
    compare("a!=b", "a != b");
    compare("1>2", "1 > 2");
    compare("1+1>=2+2", "1 + 1 >= 2 + 2");
    compare("1<2", "1 < 2");
    compare("1+1<=2+2", "1 + 1 <= 2 + 2");
    compare("true == (true && false) && false", "true == (true && false) && false");
    compare("true", "true");
    compare("false", "false");
  }

  private void compare(String expression, String expected)
      throws ParserException, EvaluationException, ParameterException {
    Parser p = new Parser();
    Expression xp = p.parseExpression(expression);
    assertEquals(expected, xp.format());
  }
}
