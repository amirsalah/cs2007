lexer grammar Expr;

T8 : '=' ;
T9 : '+' ;
T10 : '-' ;
T11 : '*' ;
T12 : '(' ;
T13 : ')' ;

// $ANTLR src "Expr.g" 74
ID  :   ('a'..'z'|'A'..'Z')+ ;
// $ANTLR src "Expr.g" 75
INT :   '0'..'9'+ ;
// $ANTLR src "Expr.g" 76
NEWLINE:'\r'? '\n' ;
// $ANTLR src "Expr.g" 77
WS  :   (' '|'\t'|'\n'|'\r')+ {skip();} ;
// END:tokens
