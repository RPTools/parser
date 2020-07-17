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

import antlr.CommonAST;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import junit.framework.TestCase;
import net.rptools.parser.function.AbstractFunction;

public class ParserTest extends TestCase {

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

  public void testIncompatibleArgumentOperations() throws ParserException {

    VariableResolver resolver = new MapVariableResolver();
    Parser p = new Parser(true);

    // string + object
    resolver.setVariable("x", List.of("one", "two"));
    evaluateStringExpression(p, resolver, "\"text\" + x", "text[one, two]");

    // num + object
    evaluateStringExpression(p, resolver, "1 + x", "1[one, two]");

    // string equals (case ignore) object
    evaluateExpression(p, resolver, "'[one, two]' == x", BigDecimal.ONE);

    // string equals (case ignore) object
    evaluateExpression(p, resolver, "'[one, three]' != x", BigDecimal.ONE);

    // string equals (strict) object
    evaluateExpression(p, resolver, "eqs('[one, two]',x)", BigDecimal.ONE);

    // string not equals (strict) object
    evaluateExpression(p, resolver, "neqs('[one, TWO]',x)", BigDecimal.ONE);
  }

  public void testParseExpressionSimple() throws ParserException {
    Parser p = new Parser();
    Expression xp = p.parseExpression("1+2");

    CommonAST tree = (CommonAST) xp.getTree();
    assertEquals(" ( + 1 2 )", tree.toStringTree());
  }

  public void testParseExpression() throws ParserException {
    Parser p = new Parser();
    Expression xp = p.parseExpression("200+2+roll(2,4)");

    CommonAST tree = (CommonAST) xp.getTree();
    assertEquals(" ( + ( + 200 2 ) ( roll 2 4 ) )", tree.toStringTree());
  }

  public void testEvaluateSimple() throws ParserException {
    Parser p = new Parser();
    Expression xp = p.parseExpression("1 + 2");

    assertEquals(new BigDecimal(3), xp.evaluate(new MapVariableResolver()));
  }

  public void testEvaluate() throws ParserException {
    Parser p = new Parser();

    evaluateExpression(p, "1 + 2", new BigDecimal(3));
    evaluateExpression(p, "5 - 2", new BigDecimal(3));
    evaluateExpression(p, "3 * 7", new BigDecimal(21));
    evaluateExpression(p, "1 + 2 * 10", new BigDecimal(21));
    evaluateExpression(p, "(1 + 2) * 10", new BigDecimal(30));
    evaluateExpression(p, "1 + 10 / 2", new BigDecimal(6));
    evaluateExpression(p, "4 * 4 / 2 ", new BigDecimal(8));
    evaluateExpression(p, "-1 + -5", new BigDecimal(-6));
    evaluateExpression(p, "-1 * (2 + 2 - 1) / -1", new BigDecimal(3));

    evaluateExpression(p, "1.2+ 2.7", new BigDecimal("3.9"));
    evaluateExpression(p, "1.3 + 2.2", new BigDecimal("3.5"));
    evaluateExpression(p, "12345.223344 - 1000.112233", new BigDecimal("11345.111111"));

    evaluateExpression(p, "2^3", new BigDecimal("8"));
    evaluateExpression(p, "1^100", new BigDecimal("1"));
    evaluateExpression(p, "2 * 2^3", new BigDecimal("16"));
  }

  public void testEvaluateCustomFunction() throws ParserException {
    Parser p = new Parser();
    p.addFunction(
        new AbstractFunction(1, 1, "increment") {

          @Override
          public Object childEvaluate(
              Parser parser,
              VariableResolver resolver,
              String functionName,
              List<Object> parameters) {
            BigDecimal value = (BigDecimal) parameters.get(0);
            return value.add(BigDecimal.ONE);
          }
        });

    evaluateExpression(p, "increment(2)", new BigDecimal(3));
    evaluateExpression(p, "1 + increment(3)", new BigDecimal(5));
    evaluateExpression(p, "1 + increment(3 + 6)", new BigDecimal(11));
    evaluateExpression(p, "2 + increment(2 * 2) * 5", new BigDecimal(27));
  }

  public void testEvaluateVariables() throws ParserException {
    Parser p = new Parser();
    VariableResolver r = new MapVariableResolver();
    r.setVariable("ii", new BigDecimal(100));

    evaluateExpression(p, r, "ii", new BigDecimal(100));
    evaluateExpression(p, r, "II", new BigDecimal(100));
    evaluateExpression(p, r, "ii + 10", new BigDecimal(110));
    evaluateExpression(p, r, "ii * 2", new BigDecimal(200));

    r.setVariable("C_mpl.x", new BigDecimal(42));

    evaluateExpression(p, r, "C_mpl.x * 10", new BigDecimal(420));

    r.setVariable("foo", VariableModifiers.Prompt, new BigDecimal(10));

    evaluateExpression(p, r, "?foo + 2", new BigDecimal(12));
  }

  public void testMath() throws ParserException {
    Parser p = new Parser();

    evaluateExpression(p, "abs(10)", new BigDecimal(10));
    evaluateExpression(p, "abs(-10)", new BigDecimal(10));
    evaluateExpression(p, "ABS(-10)", new BigDecimal(10));

    evaluateExpression(p, "ceil(2.2)", new BigDecimal(3));
    evaluateExpression(p, "floor(2.2)", new BigDecimal(2));

    evaluateExpression(p, "hypot(3.0, 4.0)", new BigDecimal(5));

    evaluateExpression(p, "max(1.0, 2.0)", new BigDecimal(2));
    evaluateExpression(p, "max(1.0, 2.0, 3.0, 1.1, 5.6)", new BigDecimal("5.6"));
    evaluateExpression(p, "min(1.0, 2.0)", BigDecimal.ONE);
    evaluateExpression(p, "min(1.0, 2.0, 3.0, 1.1, 5.6)", BigDecimal.ONE);

    evaluateExpression(p, "round(2.2)", new BigDecimal(2));
    evaluateExpression(p, "round(2.1234, 2)", new BigDecimal("2.12"));
    evaluateExpression(p, "round(2.2)", new BigDecimal(2));

    evaluateExpression(p, "sqr(2.2)", new BigDecimal("4.84"));
    evaluateExpression(p, "pow(2.2, 3)", new BigDecimal("10.648"));
    evaluateExpression(p, "pow(2, 8)", new BigDecimal("256"));
    evaluateExpression(p, "pow(8, 2)", new BigDecimal("64"));
    evaluateExpression(p, "sqrt(4.84)", new BigDecimal("2.2"));

    evaluateExpression(p, "log(10)", BigDecimal.ONE);
    evaluateExpression(p, "round(ln(9), 2)", new BigDecimal("2.20"));
  }

  public void testStringParameters() throws ParserException {
    Parser p = new Parser();

    evaluateStringExpression(p, "\"foo\" + \"bar\"", "foobar");
    evaluateStringExpression(p, "1 + 2 + \"foo\" + \"bar\"", "3foobar");
    evaluateStringExpression(p, "1 - 2 + \"foo\"", "-1foo");
  }

  public void testAssignment() throws ParserException {
    Parser p = new Parser();
    VariableResolver r = new MapVariableResolver();

    evaluateExpression(p, r, "a = 5", new BigDecimal(5));
    assertEquals(r.getVariable("a"), new BigDecimal(5));

    evaluateExpression(p, r, "b = a * 2", new BigDecimal(10));
    assertEquals(r.getVariable("b"), new BigDecimal(10));

    evaluateExpression(p, r, "b = b * b", new BigDecimal(100));
    assertEquals(r.getVariable("b"), new BigDecimal(100));

    evaluateExpression(p, r, "10 * set(\"c\", 10)", new BigDecimal(100));
    assertEquals(r.getVariable("c"), new BigDecimal(10));
  }

  public void testEval() throws ParserException {
    Parser p = new Parser();

    evaluateExpression(p, "eval('2*2')", new BigDecimal(4));
    evaluateExpression(p, "eval('a=2*2', 'b=3+1', 'a*b')", new BigDecimal(16));
  }

  public void testBitwise() throws ParserException {
    Parser p = new Parser();

    evaluateExpression(p, "band(1, 2)", BigDecimal.ZERO);
    evaluateExpression(p, "bor(1, 2)", new BigDecimal(3));
    evaluateExpression(p, "bnot(3)", new BigDecimal(-4));
    evaluateExpression(p, "bxor(7, 2)", new BigDecimal(5));

    evaluateExpression(p, "bor(0xFF00, 0x00FF)", new BigDecimal(new BigInteger("FFFF", 16)));
    evaluateExpression(p, "band(0xFFF0, 0x00FF)", new BigDecimal(new BigInteger("00F0", 16)));
  }

  public void testHexNumber() throws ParserException {
    Parser p = new Parser();

    evaluateExpression(p, "0xFF", new BigDecimal(255));

    evaluateStringExpression(p, "hex(0xFF)", "0xFF");
    evaluateStringExpression(p, "hex(0x00FF)", "0xFF");
    evaluateStringExpression(p, "hex(0xfac1)", "0xFAC1");
  }

  public void testLogicalOperators() throws ParserException {
    Parser p = new Parser();

    evaluateExpression(p, "true", BigDecimal.ONE);
    evaluateExpression(p, "false", BigDecimal.ZERO);

    evaluateExpression(p, "!10", BigDecimal.ZERO);
    evaluateExpression(p, "!0", BigDecimal.ONE);

    evaluateExpression(p, "10 && 7", BigDecimal.ONE);
    evaluateExpression(p, "10 && 0", BigDecimal.ZERO);
    evaluateExpression(p, "10 || 7 || 0", BigDecimal.ONE);
    evaluateExpression(p, "0 || 0", BigDecimal.ZERO);

    evaluateExpression(p, "10 == 10", BigDecimal.ONE);
    evaluateExpression(p, "10 == 1", BigDecimal.ZERO);
    evaluateExpression(p, "10 != 1", BigDecimal.ONE);
    evaluateExpression(p, "10 != 10", BigDecimal.ZERO);

    evaluateExpression(p, "10 > 7", BigDecimal.ONE);
    evaluateExpression(p, "10 > 12", BigDecimal.ZERO);
    evaluateExpression(p, "10 >= 10", BigDecimal.ONE);
    evaluateExpression(p, "10 >= 15", BigDecimal.ZERO);

    evaluateExpression(p, "10 < 7", BigDecimal.ZERO);
    evaluateExpression(p, "10 < 12", BigDecimal.ONE);
    evaluateExpression(p, "10 <= 10", BigDecimal.ONE);
    evaluateExpression(p, "10 <= 15", BigDecimal.ONE);

    evaluateExpression(p, "true && 10 > 15", BigDecimal.ZERO);
    evaluateExpression(p, "true && 15 > 10", BigDecimal.ONE);
    evaluateExpression(p, "true || 10 > 15", BigDecimal.ONE);
    evaluateExpression(p, "false || 15 > 10", BigDecimal.ONE);

    evaluateExpression(p, "10 <= 15 && 12 >= 12", BigDecimal.ONE);
  }

  public void testLogicalOperators_StringSupport() throws ParserException {
    Parser p = new Parser();

    evaluateExpression(p, "'foo' == 'foo'", BigDecimal.ONE);
    evaluateExpression(p, "'foo' != 'bar'", BigDecimal.ONE);
    evaluateExpression(p, "'foo' == 'bar'", BigDecimal.ZERO);
    evaluateExpression(p, "'foo ' == ' foo'", BigDecimal.ONE);
    evaluateExpression(p, "'Foo ' == ' fOo '", BigDecimal.ONE);

    evaluateExpression(p, "eqs('Foo ', ' fOo ')", BigDecimal.ZERO);
    evaluateExpression(p, "eqs('foo', 'foo')", BigDecimal.ONE);
  }

  public void testMultilineExpressions() throws ParserException {
    Parser p = new Parser();

    evaluateExpression(p, "10 + \n17 + \r\n3", new BigDecimal(30));
  }

  private void evaluateExpression(Parser p, String expression, BigDecimal answer)
      throws ParserException {
    evaluateExpression(p, new MapVariableResolver(), expression, answer);
  }

  private void evaluateExpression(
      Parser p, VariableResolver r, String expression, BigDecimal answer) throws ParserException {

    BigDecimal result = (BigDecimal) p.parseExpression(expression).evaluate(r);
    assertTrue(
        String.format(
            "%s evaluated incorrectly expected <%s> but was <%s>", expression, answer, result),
        answer.compareTo(result) == 0);
  }

  private void evaluateStringExpression(Parser p, String expression, String answer)
      throws ParserException {
    evaluateStringExpression(p, new MapVariableResolver(), expression, answer);
  }

  private void evaluateStringExpression(
      Parser p, VariableResolver resolver, String expression, String answer)
      throws ParserException {
    String result = (String) p.parseExpression(expression).evaluate(resolver);

    assertEquals(answer, result);
  }
}
