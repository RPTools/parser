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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.List;
import net.rptools.parser.function.AbstractFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class EvaluationTest {
  @Test
  public void testAssignToTrue() throws ParserException {
    Parser p = new Parser();
    try {
      evaluateExpression(p, "true = 2", BigDecimal.valueOf(2));
      fail("Was able to assign to 'true'");
    } catch (ParserException ex) {
      // Expected
    }
  }

  @Test
  public void testAssignToFalse() throws ParserException {
    Parser p = new Parser();
    try {
      evaluateExpression(p, "false = 2", BigDecimal.valueOf(2));
      fail("Was able to assign to 'fakse'");
    } catch (ParserException ex) {
      // Expected
    }
  }

  @ParameterizedTest(name = "{0}; {1}; {2}")
  @CsvFileSource(
      resources = "EvaluationTest.testSuccessfulEvaluations.csv",
      numLinesToSkip = 1,
      delimiter = ';',
      quoteCharacter = '`',
      ignoreLeadingAndTrailingWhitespace = false)
  public void testSuccessfulEvaluations(String label, String input, Object expectedValue)
      throws ParserException {
    // Cast expectation to BigDecimal
    if (expectedValue instanceof String s) {
      try {
        expectedValue = new BigDecimal(s);
      } catch (NumberFormatException e) {
        // Ignore. It's just not a number, okay?
      }
    }

    Parser p = new Parser(true);
    VariableResolver resolver = new MapVariableResolver();

    evaluateExpression(p, resolver, input, expectedValue);
  }

  @Test
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

  @Test
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

  @Test
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

  @Test
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

  private void evaluateExpression(Parser p, String expression, Object answer)
      throws ParserException {
    evaluateExpression(p, new MapVariableResolver(), expression, answer);
  }

  private void evaluateExpression(Parser p, VariableResolver r, String expression, Object answer)
      throws ParserException {

    Object result = p.parseExpression(expression).evaluate(r);

    if (answer instanceof BigDecimal bd) {
      assertInstanceOf(BigDecimal.class, result, "%s is also a BigDecimal");
      assertEquals(
          0,
          bd.compareTo((BigDecimal) result),
          String.format(
              "%s evaluated incorrectly expected <%s> but was <%s>", expression, answer, result));
    } else {
      assertEquals(
          answer,
          result,
          String.format(
              "%s evaluated incorrectly expected <%s> but was <%s>", expression, answer, result));
    }
  }

  private void evaluateStringExpression(
      Parser p, VariableResolver resolver, String expression, String answer)
      throws ParserException {
    String result = (String) p.parseExpression(expression).evaluate(resolver);

    assertEquals(answer, result);
  }
}
