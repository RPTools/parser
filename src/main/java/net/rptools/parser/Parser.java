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
import antlr.RecognitionException;
import antlr.TokenStreamException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.rptools.CaseInsensitiveHashMap;
import net.rptools.parser.function.Function;
import net.rptools.parser.function.impl.AbsoluteValue;
import net.rptools.parser.function.impl.Addition;
import net.rptools.parser.function.impl.And;
import net.rptools.parser.function.impl.Assignment;
import net.rptools.parser.function.impl.BitwiseAnd;
import net.rptools.parser.function.impl.BitwiseNot;
import net.rptools.parser.function.impl.BitwiseOr;
import net.rptools.parser.function.impl.BitwiseXor;
import net.rptools.parser.function.impl.Ceiling;
import net.rptools.parser.function.impl.Division;
import net.rptools.parser.function.impl.Equals;
import net.rptools.parser.function.impl.Eval;
import net.rptools.parser.function.impl.Floor;
import net.rptools.parser.function.impl.Greater;
import net.rptools.parser.function.impl.GreaterOrEqual;
import net.rptools.parser.function.impl.Hex;
import net.rptools.parser.function.impl.Hypotenuse;
import net.rptools.parser.function.impl.Lesser;
import net.rptools.parser.function.impl.LesserEqual;
import net.rptools.parser.function.impl.Ln;
import net.rptools.parser.function.impl.Log;
import net.rptools.parser.function.impl.Max;
import net.rptools.parser.function.impl.Mean;
import net.rptools.parser.function.impl.Median;
import net.rptools.parser.function.impl.Min;
import net.rptools.parser.function.impl.Multiplication;
import net.rptools.parser.function.impl.Not;
import net.rptools.parser.function.impl.NotEquals;
import net.rptools.parser.function.impl.Or;
import net.rptools.parser.function.impl.Power;
import net.rptools.parser.function.impl.Round;
import net.rptools.parser.function.impl.SquareRoot;
import net.rptools.parser.function.impl.StrEquals;
import net.rptools.parser.function.impl.StrNotEquals;
import net.rptools.parser.function.impl.Subtraction;
import net.rptools.parser.transform.Transformer;

public class Parser {
  private final Map<String, Function> functions = new CaseInsensitiveHashMap<>();

  private final List<Transformer> transforms = new ArrayList<>();

  private final EvaluationTreeParser evaluationTreeParser;

  ///////////////////////////////////////////////////////////////////////////
  // Constructor(s)
  ///////////////////////////////////////////////////////////////////////////

  public Parser() {
    this(true);
  }

  public Parser(boolean addDefaultFunctions) {

    if (addDefaultFunctions) {
      addStandardOperators();
      addStandardMathFunctions();
      addBitwiseLogicFunctions();
      addLogicalFunctions();
      addExtraFunctions();
    }

    this.evaluationTreeParser = new EvaluationTreeParser(this);
  }

  ///////////////////////////////////////////////////////////////////////////
  // Functions
  ///////////////////////////////////////////////////////////////////////////

  public void addStandardOperators() {
    addFunction(new Assignment());

    addFunction(new Addition());
    addFunction(new Subtraction());

    addFunction(new Multiplication());
    addFunction(new Division());

    addFunction(new Power());
  }

  public void addStandardMathFunctions() {
    addFunction(new AbsoluteValue());
    addFunction(new Ceiling());
    addFunction(new Floor());
    addFunction(new Hypotenuse());
    addFunction(new Max());
    addFunction(new Min());
    addFunction(new Round());
    addFunction(new SquareRoot());
    addFunction(new Mean());
    addFunction(new Median());
    addFunction(new Log());
    addFunction(new Ln());
  }

  public void addBitwiseLogicFunctions() {
    addFunction(new BitwiseAnd());
    addFunction(new BitwiseOr());
    addFunction(new BitwiseNot());
    addFunction(new BitwiseXor());
    addFunction(new Hex());
  }

  public void addLogicalFunctions() {
    addFunction(new Not());
    addFunction(new Or());
    addFunction(new And());
    addFunction(new Equals());
    addFunction(new NotEquals());
    addFunction(new Greater());
    addFunction(new GreaterOrEqual());
    addFunction(new Lesser());
    addFunction(new LesserEqual());
    addFunction(new StrEquals());
    addFunction(new StrNotEquals());
  }

  public void addExtraFunctions() {
    addFunction(new Eval());
  }

  public void addFunction(Function function) {
    for (String alias : function.getAliases()) {
      functions.put(alias, function);
    }
  }

  public void addFunctions(Function[] functions) {
    for (Function f : functions) {
      addFunction(f);
    }
  }

  public void addFunctions(List<Function> functions) {
    for (Function f : functions) {
      addFunction(f);
    }
  }

  public Function getFunction(String functionName) {
    return functions.get(functionName);
  }

  public Collection<Function> getFunctions() {
    return functions.values();
  }

  ///////////////////////////////////////////////////////////////////////////
  // Transforms
  ///////////////////////////////////////////////////////////////////////////
  public void addTransformer(Transformer t) {
    transforms.add(t);
  }

  private String applyTransforms(String expression) {
    String s = expression;
    for (Transformer trans : transforms) {
      s = trans.transform(s);
    }

    return s;
  }

  public EvaluationTreeParser getEvaluationTreeParser() throws ParserException {
    return evaluationTreeParser;
  }

  ///////////////////////////////////////////////////////////////////////////
  // parseExpression
  ///////////////////////////////////////////////////////////////////////////

  public Expression parseExpression(String expression) throws ParserException {
    try {
      String s = applyTransforms(expression);

      ExpressionLexer lexer =
          new ExpressionLexer(new ByteArrayInputStream(s.getBytes(StandardCharsets.ISO_8859_1)));
      ExpressionParser parser = new ExpressionParser(lexer);

      parser.expression();
      CommonAST t = (CommonAST) parser.getAST();

      return new Expression(this, parser, t);

    } catch (RecognitionException e) {
      throw new ParserException(e);
    } catch (TokenStreamException e) {
      throw new ParserException(e);
    }
  }
}
