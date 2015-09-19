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

import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;
import antlr.collections.AST;

public class Expression {
	private static final InlineTreeFormatter inlineFormatter = new InlineTreeFormatter(); 
	
    private final Parser           parser;
    private final ExpressionParser expressionParser;
    private final AST              tree;

    private transient Expression   deterministicExpression;

    Expression(Parser parser, ExpressionParser expressionParser, AST tree, boolean deterministic) {
        this.parser = parser;
        this.expressionParser = expressionParser;
        this.tree = tree;
        if (deterministic) {
            deterministicExpression = this;
        }
    }

    Expression(Parser parser, ExpressionParser expressionParser, AST tree) {
        this(parser, expressionParser, tree, false);
    }

    public Parser getParser() {
        return parser;
    }

    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    public AST getTree() {
        return tree;
    }

    public Object evaluate() throws ParserException {
        return parser.getEvaluationTreeParser().evaluate(tree);
    }

    private void createDeterministicExpression() throws ParserException {
        DeterministicTreeParser tp = new DeterministicTreeParser(parser, expressionParser);
        
        AST dupTree = expressionParser.getASTFactory().dupTree(tree);
        AST newTree = tp.evaluate(dupTree);

        if (tree.equalsTree(newTree)) {
            deterministicExpression = this;
        } else {
            deterministicExpression = new Expression(parser, expressionParser, newTree, true);
        }
    }

    public boolean isDeterministic() throws ParserException {
        if (deterministicExpression == null) {
            createDeterministicExpression();
        }

        return deterministicExpression == this;
    }

    public Expression getDeterministicExpression() throws ParserException {
        if (deterministicExpression == null) {
            createDeterministicExpression();
        }

        return deterministicExpression;
    }
    
    public String format() throws EvaluationException, ParameterException {
    	return inlineFormatter.format(tree);
    }
}
