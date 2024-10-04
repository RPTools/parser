lexer grammar ExpressionLexer;

options {
}

WS        : (' '|'\r\n'|'\n'|'\t') -> channel(HIDDEN) ;
ASSIGN    : '='  ;
QUOTE     : '"'  ;

// Logical operators
OR        : '||' ;
AND       : '&&' ;
NOT       : '!'  ;
EQUALS    : '==' ;
NOTEQUALS : '!=' ;
GE        : '>=' ;
GT        : '>'  ;
LT        : '<'  ;
LE        : '<=' ;

// Math operators
PLUS      : '+'  ;
MINUS     : '-'  ;
MULTIPLY  : '*'  ;
DIVIDE    : '/'  ;
POWER     : '^'  ;

LPAREN    : '('  ;
RPAREN    : ')'  ;
COMMA     : ','  ;
SEMI      : ';'  ;
QUESTION  : '?'  ;

NUMBER               : INT ('.' INT)? ;
HEXNUMBER            : '0' 'x' (HEXDIGIT)+ ;
IDENTIFIER           : LETTER (LETTER|DIGIT|'.'|'_')* ;
SINGLE_QUOTED_STRING : '\'' ( ~'\'' )* '\'' ;
DOUBLE_QUOTED_STRING : '"' ( ~'"' )* '"' ;

fragment HEXDIGIT    : ('0'..'9'|'A'..'F'|'a'..'f') ;
fragment INT         : ('0'..'9')+ ;
fragment DIGIT       : '0'..'9' ;
fragment LETTER      : ('A'..'Z'|'a'..'z'|'\u00c0'..'\u00d6'|'\u00d8'..'\u00f6'|'\u00f8'..'\u00ff') ;
