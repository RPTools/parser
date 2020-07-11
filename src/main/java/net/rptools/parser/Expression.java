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

import antlr.collections.AST;

public class Expression {
  private static final InlineTreeFormatter inlineFormatter = new InlineTreeFormatter();

  private final Parser parser;
  private final ExpressionParser expressionParser;
  private final AST tree;

  Expression(Parser parser, ExpressionParser expressionParser, AST tree) {
    this.parser = parser;
    this.expressionParser = expressionParser;
    this.tree = tree;
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
    return parser.getEvaluationTreeParser().evaluate(tree, new MapVariableResolver());
  }

  public Object evaluate(VariableResolver resolver) throws ParserException {
    return parser.getEvaluationTreeParser().evaluate(tree, resolver);
  }

  public Expression getDeterministicExpression(VariableResolver resolver) throws ParserException {
    DeterministicTreeParser tp = new DeterministicTreeParser(parser, expressionParser);

    AST dupTree = expressionParser.getASTFactory().dupTree(tree);
    AST newTree = tp.evaluate(dupTree, resolver);

    if (tree.equalsTree(newTree)) {
      return this;
    } else {
      return new Expression(parser, expressionParser, newTree);
    }
  }

  public String format() {
    return inlineFormatter.format(tree);
  }
}
