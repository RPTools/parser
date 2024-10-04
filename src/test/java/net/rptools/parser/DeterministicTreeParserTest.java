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
import java.util.List;
import net.rptools.parser.ast.AST;
import net.rptools.parser.function.AbstractNumberFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;
import org.junit.jupiter.api.Test;

public class DeterministicTreeParserTest {
  @Test
  public void testEvaluateOnlyDeterministicFunctions()
      throws ParserException, EvaluationException, ParameterException {
    Parser p = new Parser();
    Expression xp = p.parseExpression("200+2+2*2");
    Expression dxp = xp.getDeterministicExpression(new MapVariableResolver());

    assertSame(xp, dxp);

    AST tree = xp.getTree();
    AST deterministicTree = dxp.getTree();

    assertTrue(deterministicTree.equalsTree(tree));
  }

  @Test
  public void testEvaluate() throws ParserException, EvaluationException, ParameterException {
    Parser p = new Parser();
    p.addFunction(new NonDeterministicFunction());

    Expression xp = p.parseExpression("200+2+nondeterministic(2, 2)+sum(1+2,2,3)");
    Expression dxp = xp.getDeterministicExpression(new MapVariableResolver());

    assertNotSame(xp, dxp);

    assertEquals(
        " ( + ( + ( + 200 2 ) ( nondeterministic 2 2 ) ) ( sum ( + 1 2 ) 2 3 ) )",
        xp.getTree().toStringTree());
    assertEquals(" ( + ( + ( + 200 2 ) 1 ) ( sum ( + 1 2 ) 2 3 ) )", dxp.getTree().toStringTree());
  }

  @Test
  public void testEvaluate_WithAssignment()
      throws ParserException, EvaluationException, ParameterException {
    Parser p = new Parser();
    p.addFunction(new NonDeterministicFunction());

    Expression xp = p.parseExpression("a=200+2+nondeterministic(2, 2)");
    assertEquals(
        " ( = a ( + ( + 200 2 ) ( nondeterministic 2 2 ) ) )", xp.getTree().toStringTree());

    Expression dxp = xp.getDeterministicExpression(new MapVariableResolver());

    assertNotSame(xp, dxp);

    assertEquals(" ( = a ( + ( + 200 2 ) 1 ) )", dxp.getTree().toStringTree());
  }

  @Test
  public void testEvaluate2() throws ParserException {
    Parser p = new Parser();
    p.addFunction(new NonDeterministicFunction());

    Expression xp = p.parseExpression("100+nondeterministic(4, 1)*10");

    Expression dxp = xp.getDeterministicExpression(new MapVariableResolver());

    assertNotSame(xp, dxp);

    assertEquals(" ( + 100 ( * ( nondeterministic 4 1 ) 10 ) )", xp.getTree().toStringTree());
    assertEquals(" ( + 100 ( * 1 10 ) )", dxp.getTree().toStringTree());
  }

  @Test
  public void testEvaluate_VariableResolution() throws ParserException {
    Parser p = new Parser();
    VariableResolver r = new MapVariableResolver();

    r.setVariable("simpleInt", new BigDecimal(10));

    Expression xp = p.parseExpression("1+simpleInt");
    Expression dxp = xp.getDeterministicExpression(r);

    assertNotSame(xp, dxp);

    assertEquals(" ( + 1 simpleInt )", xp.getTree().toStringTree());
    assertEquals(" ( + 1 10 )", dxp.getTree().toStringTree());
  }

  @Test
  public void testEvaluate_VariableResolutionString() throws ParserException {
    Parser p = new Parser();
    VariableResolver r = new MapVariableResolver();

    r.setVariable("simpleString", "I am a string");

    Expression xp = p.parseExpression("1+simpleString");
    Expression dxp = xp.getDeterministicExpression(r);

    assertNotSame(xp, dxp);

    assertEquals(" ( + 1 simpleString )", xp.getTree().toStringTree());
    // Note that "I am a string" is not quoted below. Bug?
    assertEquals(" ( + 1 I am a string )", dxp.getTree().toStringTree());
  }

  /**
   * Test function that declares itself non-deterministic for the purposes of comparing the result
   * of getting a deterministic expression from another expression.
   */
  private static class NonDeterministicFunction extends AbstractNumberFunction {
    public NonDeterministicFunction() {
      super(2, 2, false, "nondeterministic");
    }

    @Override
    public Object childEvaluate(
        Parser parser, VariableResolver resolver, String functionName, List<Object> parameters) {
      return BigDecimal.ONE;
    }
  }
}
