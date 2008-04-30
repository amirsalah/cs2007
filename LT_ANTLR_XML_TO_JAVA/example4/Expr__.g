lexer grammar Expr;

T8 : '=' ;
T9 : '+' ;
T10 : '-' ;
T11 : '*' ;
T12 : '(' ;
T13 : ')' ;

// $ANTLR src "Expr.g" 38
ID  :   ('a'..'z'|'A'..'Z')+ ;
// $ANTLR src "Expr.g" 39
INT :   '0'..'9'+ ;
// $ANTLR src "Expr.g" 40
NEWLINE:'\r'? '\n' ;
// $ANTLR src "Expr.g" 41
WS  :   (' '|'\t'|'\n'|'\r')+ {skip();} ;
// END:tokens
