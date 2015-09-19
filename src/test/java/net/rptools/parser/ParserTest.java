/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package net.rptools.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import junit.framework.TestCase;
import net.rptools.parser.Expression;
import net.rptools.parser.ParserException;
import net.rptools.parser.Parser;
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;
import antlr.CommonAST;

public class ParserTest extends TestCase {
    
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
    
    public void testEvaluateSimple() throws ParserException, EvaluationException, ParameterException {
        Parser p = new Parser();
        Expression xp = p.parseExpression("1 + 2");
        
        assertEquals(new BigDecimal(3), xp.evaluate());
    }
    
    public void testEvaluate() throws ParserException, EvaluationException, ParameterException {
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
    
    public void testEvaluateCustomFunction() throws ParserException, EvaluationException, ParameterException {
        Parser p = new Parser();
        p.addFunction(new AbstractFunction(1, 1, "increment") {

            @Override
            public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) {
                BigDecimal value = (BigDecimal) parameters.get(0);
                return value.add(BigDecimal.ONE);
            }
        });
        
        evaluateExpression(p, "increment(2)", new BigDecimal(3));
        evaluateExpression(p, "1 + increment(3)", new BigDecimal(5));
        evaluateExpression(p, "1 + increment(3 + 6)", new BigDecimal(11));
        evaluateExpression(p, "2 + increment(2 * 2) * 5", new BigDecimal(27));
    }
    
    public void testEvaluateVariables() throws EvaluationException, ParameterException, ParserException {
        Parser p = new Parser();
        p.setVariable("ii", new BigDecimal(100));
        
        evaluateExpression(p, "ii", new BigDecimal(100));
        evaluateExpression(p, "II", new BigDecimal(100));
        evaluateExpression(p, "ii + 10", new BigDecimal(110));
        evaluateExpression(p, "ii * 2", new BigDecimal(200));
        
        p.setVariable("C_mpl.x", new BigDecimal(42));
        
        evaluateExpression(p, "C_mpl.x * 10", new BigDecimal(420));
        
        p.setVariable("foo", VariableModifiers.Prompt, new BigDecimal(10));
        
        evaluateExpression(p, "?foo + 2", new BigDecimal(12));
    }
    
    public void testMath() throws EvaluationException, ParameterException, ParserException {
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
    
    public void testStringParameters() throws EvaluationException, ParameterException, ParserException {
    	Parser p = new Parser();
    	
    	evaluateStringExpression(p, "\"foo\" + \"bar\"", "foobar");
    	evaluateStringExpression(p, "1 + 2 + \"foo\" + \"bar\"", "3foobar");
    	evaluateStringExpression(p, "1 - 2 + \"foo\"", "-1foo");
    }
    
    public void testAssignment() throws EvaluationException, ParameterException, ParserException {
    	Parser p = new Parser();
    	
    	evaluateExpression(p, "a = 5", new BigDecimal(5));
    	assertEquals(p.getVariable("a"), new BigDecimal(5));
    	
    	evaluateExpression(p, "b = a * 2", new BigDecimal(10));
    	assertEquals(p.getVariable("b"), new BigDecimal(10));
    	
    	evaluateExpression(p, "b = b * b", new BigDecimal(100));
    	assertEquals(p.getVariable("b"), new BigDecimal(100));

        evaluateExpression(p, "10 * set(\"c\", 10)", new BigDecimal(100));
        assertEquals(p.getVariable("c"), new BigDecimal(10));
    }
    
    public void testEval() throws EvaluationException, ParameterException, ParserException {
    	Parser p = new Parser();
    	
    	evaluateExpression(p, "eval('2*2')", new BigDecimal(4));
    	evaluateExpression(p, "eval('a=2*2', 'b=3+1', 'a*b')", new BigDecimal(16));
    }
    
    public void testBitwise() throws EvaluationException, ParameterException, ParserException {
        Parser p = new Parser();
        
        evaluateExpression(p, "band(1, 2)", BigDecimal.ZERO);
        evaluateExpression(p, "bor(1, 2)", new BigDecimal(3));
        evaluateExpression(p, "bnot(3)", new BigDecimal(-4));
        evaluateExpression(p, "bxor(7, 2)", new BigDecimal(5));
        
        
        evaluateExpression(p, "bor(0xFF00, 0x00FF)", new BigDecimal(new BigInteger("FFFF", 16)));
        evaluateExpression(p, "band(0xFFF0, 0x00FF)", new BigDecimal(new BigInteger("00F0", 16)));
    }
    
    public void testHexNumber() throws EvaluationException, ParameterException, ParserException {
        Parser p = new Parser();
        
        evaluateExpression(p, "0xFF", new BigDecimal(255));
        
        evaluateStringExpression(p, "hex(0xFF)", "0xFF");
        evaluateStringExpression(p, "hex(0x00FF)", "0xFF");
        evaluateStringExpression(p, "hex(0xfac1)", "0xFAC1");
    }
    
    public void testLogicalOperators() throws EvaluationException, ParameterException, ParserException {
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
    
    public void testLogicalOperators_StringSupport() throws EvaluationException, ParameterException, ParserException {
        Parser p = new Parser();
        
        evaluateExpression(p, "'foo' == 'foo'", BigDecimal.ONE);
        evaluateExpression(p, "'foo' != 'bar'", BigDecimal.ONE);
        evaluateExpression(p, "'foo' == 'bar'", BigDecimal.ZERO);
        evaluateExpression(p, "'foo ' == ' foo'", BigDecimal.ONE);
        evaluateExpression(p, "'Foo ' == ' fOo '", BigDecimal.ONE);
        
        evaluateExpression(p, "eqs('Foo ', ' fOo ')", BigDecimal.ZERO);
        evaluateExpression(p, "eqs('foo', 'foo')", BigDecimal.ONE);
    }
    
    public void testMultilineExpressions() throws EvaluationException, ParameterException, ParserException {
        Parser p = new Parser();
        
        evaluateExpression(p, "10 + \n17 + \r\n3", new BigDecimal(30));
    }
    
    private void evaluateExpression(Parser p, String expression, BigDecimal answer) throws EvaluationException, ParameterException, ParserException {
        
        BigDecimal result = (BigDecimal) p.parseExpression(expression).evaluate();
        assertTrue(String.format("%s evaluated incorrectly expected <%s> but was <%s>", expression, answer, result), answer.compareTo(result) == 0);
    }
    
    private void evaluateStringExpression(Parser p, String expression, String answer) throws EvaluationException, ParameterException, ParserException {
    	String result = (String) p.parseExpression(expression).evaluate();
    	
    	assertEquals(answer, result);
    }
}
