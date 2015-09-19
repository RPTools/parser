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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.rptools.CaseInsensitiveHashMap;
import net.rptools.parser.function.Function;
import net.rptools.parser.function.impl.AbsoluteValue;
import net.rptools.parser.function.impl.Addition;
import net.rptools.parser.function.impl.Assignment;
import net.rptools.parser.function.impl.BitwiseAnd;
import net.rptools.parser.function.impl.BitwiseNot;
import net.rptools.parser.function.impl.BitwiseOr;
import net.rptools.parser.function.impl.BitwiseXor;
import net.rptools.parser.function.impl.Ceiling;
import net.rptools.parser.function.impl.Division;
import net.rptools.parser.function.impl.Eval;
import net.rptools.parser.function.impl.Floor;
import net.rptools.parser.function.impl.Hex;
import net.rptools.parser.function.impl.Hypotenuse;
import net.rptools.parser.function.impl.Log;
import net.rptools.parser.function.impl.Ln;
import net.rptools.parser.function.impl.Max;
import net.rptools.parser.function.impl.Mean;
import net.rptools.parser.function.impl.Median;
import net.rptools.parser.function.impl.Min;
import net.rptools.parser.function.impl.Not;
import net.rptools.parser.function.impl.And;
import net.rptools.parser.function.impl.NotEquals;
import net.rptools.parser.function.impl.Or;
import net.rptools.parser.function.impl.Equals;
import net.rptools.parser.function.impl.Greater;
import net.rptools.parser.function.impl.GreaterOrEqual;
import net.rptools.parser.function.impl.Lesser;
import net.rptools.parser.function.impl.LesserEqual;
import net.rptools.parser.function.impl.Multiplication;
import net.rptools.parser.function.impl.Power;
import net.rptools.parser.function.impl.Round;
import net.rptools.parser.function.impl.SquareRoot;
import net.rptools.parser.function.impl.StrEquals;
import net.rptools.parser.function.impl.StrNotEquals;
import net.rptools.parser.function.impl.Subtraction;
import net.rptools.parser.transform.Transformer;
import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;

public class Parser implements VariableResolver {
    private final Map<String, Function> functions = new CaseInsensitiveHashMap<Function>();
    
    private final List<Transformer> transforms = new ArrayList<Transformer>();
    
    private final EvaluationTreeParser evaluationTreeParser;
    
    private final VariableResolver variableResolver;
    
    ///////////////////////////////////////////////////////////////////////////
    // Constructor(s)
    ///////////////////////////////////////////////////////////////////////////
    
    public Parser() {
        this(true);
    }
    
    public Parser(boolean addDefaultFunctions) {
    	this(null, addDefaultFunctions);
    }
    
    public Parser(VariableResolver variableResolver, boolean addDefaultFunctions) {
        
        if (addDefaultFunctions) {
            addStandardOperators();
            addStandardMathFunctions();
            addBitwiseLogicFunctions();
            addLogicalFunctions();
            addExtraFunctions();
        }
        
        this.evaluationTreeParser = new EvaluationTreeParser(this);

        if (variableResolver == null)
            this.variableResolver = new MapVariableResolver();
        else
            this.variableResolver = variableResolver;
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

    ///////////////////////////////////////////////////////////////////////////
    // Variable
    ///////////////////////////////////////////////////////////////////////////
    
    public VariableResolver getVariableResolver() {
    	return variableResolver;
    }
    
    public boolean containsVariable(String name) throws ParserException {
        return variableResolver.containsVariable(name, VariableModifiers.None);
    }

    public void setVariable(String name, Object value) throws ParserException {
        variableResolver.setVariable(name, VariableModifiers.None, value);
    }

    public Object getVariable(String variableName) throws ParserException {
        return variableResolver.getVariable(variableName, VariableModifiers.None);
    }
    
    public boolean containsVariable(String name, VariableModifiers vType) throws ParserException {
        return variableResolver.containsVariable(name, vType);
    }

    public void setVariable(String name, VariableModifiers vType, Object value) throws ParserException {
        variableResolver.setVariable(name, vType, value);
    }

    public Object getVariable(String variableName, VariableModifiers vType) throws ParserException {
        return variableResolver.getVariable(variableName, vType);
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
        	
            ExpressionLexer lexer = new ExpressionLexer(new ByteArrayInputStream(s.getBytes()));
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
