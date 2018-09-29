lexer grammar WatLexer;

LBRACE : '{' ;
RBRACE : '}' ;
ARRAY : '[]' ;

PRIMITIVE : ('char' | 'boolean' | 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' | 'String' | 'UUID') ;

WORD : [a-zA-Z][a-zA-Z0-9]* ;

CRLF : ('\r' | '\n' | '\r\n') ;
WS : [ \t]+  ;
