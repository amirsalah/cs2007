lexer grammar Expr;

T8 : '=' ;
T9 : '+' ;
T10 : '-' ;
T11 : '*' ;
T12 : '(' ;
T13 : ')' ;

// $ANTLR src "Expr.g" 27
ID  :   ('a'..'z'|'A'..'Z')+ ;
// $ANTLR src "Expr.g" 28
INT :   '0'..'9'+ ;
// $ANTLR src "Expr.g" 29
NEWLINE:'\r'? '\n' ;
// $ANTLR src "Expr.g" 30
WS  :   (' '|'\t'|'\n'|'\r')+ {skip();} ;
// END:tokens
