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
header { package net.rptools.parser; }

class ExpressionParser extends Parser;

options {
    k = 2;
    buildAST = true;
}

imaginaryTokenDefinitions :
	ASSIGNEE
    SIGN_MINUS
    SIGN_PLUS
    FUNCTION
    OPERATOR
    UNARY_OPERATOR
    VARIABLE
    PROMPTVARIABLE
    STRING
    ;
    
expression:
                expr
                |
                assignmentExpression
    ;
    
assignmentExpression:
                id:IDENTIFIER {#id.setType(ASSIGNEE);} t1:ASSIGN^ {#t1.setType(OPERATOR);} expr
    ;

expr:
                orExpression
    ;
    
orExpression:
				andExpression
				(
					t1:OR^ {#t1.setType(OPERATOR);}
					andExpression) *
	;
    
andExpression:
				compareExpression
				(
					t1:AND^ {#t1.setType(OPERATOR);}
					compareExpression) *
	;
	
compareExpression:
				 notExpression
                (
                    (
                    	t1:GE^ {#t1.setType(OPERATOR);}
                       |t2:GT^ {#t2.setType(OPERATOR);}
                       |t3:LT^ {#t3.setType(OPERATOR);}
                       |t4:LE^ {#t4.setType(OPERATOR);}
                       |t5:EQUALS^ {#t5.setType(OPERATOR);}
                       |t6:NOTEQUALS^ {#t6.setType(OPERATOR);}
                     ) 
                     notExpression)*
    ;
	
notExpression:
                (
                    (t1:NOT^ {#t1.setType(UNARY_OPERATOR);})? 
                    additiveExpression
                )
    ;
    
additiveExpression:   
                multiplicitiveExpression
                (
                    (
                    	t1:PLUS^ {#t1.setType(OPERATOR);}
                       |t2:MINUS^ {#t2.setType(OPERATOR);}
                     ) 
                     multiplicitiveExpression)*
    ;

multiplicitiveExpression:
                powerExpression
                (
                	(
                		t1:MULTIPLY^ {#t1.setType(OPERATOR);}
                	   |t2:DIVIDE^ {#t2.setType(OPERATOR);}
                	) 
                	powerExpression)*
    ;
    
powerExpression:
                unaryExpression
                (
                	(
                		t1:POWER^ {#t1.setType(OPERATOR);}
                	) 
                	unaryExpression)*
	;

unaryExpression:
                (
                    (t1:PLUS^ {#t1.setType(UNARY_OPERATOR);}
                    |
                    t2:MINUS^ {#t2.setType(UNARY_OPERATOR);}
                    |
                    t3:NOT^ {#t3.setType(UNARY_OPERATOR);})? 
                    constantExpression
                )
    ;

constantExpression:
                (function) => function
                |
                NUMBER
                |
                HEXNUMBER
                |
                TRUE
                |
                FALSE
                |
                variable
                |
                t1:SINGLE_QUOTED_STRING {#t1.setType(STRING);}
                |
                t2:DOUBLE_QUOTED_STRING {#t2.setType(STRING);}
                | 
                LPAREN! expr RPAREN!
    ;



variable:
               QUESTION! id2:IDENTIFIER {#id2.setType(PROMPTVARIABLE);}
               |
               id1:IDENTIFIER {#id1.setType(VARIABLE);}
    ;

function:
               id:IDENTIFIER^ {#id.setType(FUNCTION);} LPAREN! parameterList RPAREN!
    ;

parameterList: (expr (COMMA! expr)* )?
    ;


class ExpressionLexer extends Lexer;

options {
    k=3;    // needed for newline junk
    charVocabulary='\u0000'..'\u00FF';  // allow ISO 8859-1
}

ASSIGN  :   '=' ;
QUOTE   :   '"' ;

// Logical operators
OR      :   "||" ;
AND     :   "&&" ;
NOT		:   '!' ;
EQUALS  :   "==" ;
NOTEQUALS : "!=" ;
GE		:   ">=" ;
GT      :   ">" ;
LT      :   "<" ;
LE      :   "<=" ;
TRUE	:  "true";
FALSE   :  "false";

// Math operators
PLUS    :   '+' ;
MINUS   :   '-' ;
MULTIPLY:   '*' ;
DIVIDE  :   '/' ;
POWER   :   '^' ;

LPAREN  :   '(' ;
RPAREN  :   ')' ;
COMMA   :   ',' ;
WS      :   ( ' '
            | "\r\n"
            | '\n'
            | '\t'
            )
            {$setType(Token.SKIP);}
    ;
SEMI    : ';' ;
QUESTION : '?' ;

NUMBER               : INT ('.' INT)? ;
HEXNUMBER			 : '0' 'x' (HEXDIGIT)+ ;
IDENTIFIER           : LETTER (LETTER|DIGIT|'.'|'_')* ;
SINGLE_QUOTED_STRING : '\'' ( ~'\'' )* '\'' ;
DOUBLE_QUOTED_STRING : '\"' ( ~'\"' )* '\"' ;

protected HEXDIGIT   : ('0'..'9'|'A'..'F'|'a'..'f');
protected DID        : ('d' | 'D') ;
protected INT        : ('0'..'9')+ ;
protected DIGIT      : '0'..'9' ;
protected LETTER     : ('A'..'Z'|'a'..'z') ;



