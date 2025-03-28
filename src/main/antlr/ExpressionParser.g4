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

parser grammar ExpressionParser;

// Because we reuse the name "Parser".
// Eh., we should just move our grammer classes into a separate package.
@header {import org.antlr.v4.runtime.Parser;}

options {
    tokenVocab=ExpressionLexer;
}

full: result=expr (expr)* EOF;

expr:
      id=IDENTIFIER LPAREN (expr (COMMA expr)*)? RPAREN # function
    | QUESTION id=IDENTIFIER  # promptvariable
    | id=IDENTIFIER  # variable
    | number=NUMBER  # decimal
    | number=HEXNUMBER  # hexadecimal
    | string=(SINGLE_QUOTED_STRING|DOUBLE_QUOTED_STRING)  # string
    | LPAREN child=expr RPAREN # bracket
    | operator=(PLUS|MINUS|NOT) operand=expr  # unary
    | lhs=expr operator=POWER rhs=expr # power
    | lhs=expr operator=(MULTIPLY|DIVIDE) rhs=expr # multiplicative
    | lhs=expr operator=(PLUS|MINUS) rhs=expr # additive
    | lhs=expr operator=(GE|GT|LT|LE|EQUALS|NOTEQUALS) rhs=expr # compare
    | lhs=expr operator=AND rhs=expr # and
    | lhs=expr operator=OR rhs=expr # or
    | id=IDENTIFIER operator=ASSIGN rhs=expr # assignment
    ;
