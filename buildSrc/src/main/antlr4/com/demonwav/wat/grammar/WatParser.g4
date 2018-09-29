parser grammar WatParser;

options { tokenVocab=WatLexer; }

any_ws : WS | CRLF ;
crlf_ws : (WS | CRLF)* CRLF (WS | CRLF)* ;

file : struct* ;

struct : any_ws* name any_ws+ LBRACE crlf_ws+ declaration+ RBRACE crlf_ws+ ;

declaration : type any_ws+ name crlf_ws* ;

name : WORD ;

type : (PRIMITIVE | WORD) ARRAY* ;
