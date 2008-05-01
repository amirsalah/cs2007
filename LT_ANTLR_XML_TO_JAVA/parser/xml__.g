lexer grammar xml;
options {
  language=Java;

}

// $ANTLR src "xml.g" 94
fragment ALPHA : ('A'..'Z')|('a'..'z');
	// $ANTLR src "xml.g" 95
fragment DIGIT : ('0'..'9');
	// $ANTLR src "xml.g" 96
fragment NEWLINE : '\r'? '\n' ;
	// $ANTLR src "xml.g" 97
WS  :   (' '|'\t'|'\n'|'\r')+ {$channel = HIDDEN;} ;
	
	// $ANTLR src "xml.g" 99
fragment NMCHAR : ALPHA | DIGIT | '.' | '_' | '-' | ':' ;
	// $ANTLR src "xml.g" 100
fragment NMTOKEN: (NMCHAR)+ ;
	//fragment STRING : 
	// $ANTLR src "xml.g" 102
fragment PCDATA : (~'<')+ ; 
	
	// $ANTLR src "xml.g" 104
fragment GENERIC_ID 
	    : ( ALPHA | '_' | ':') 
	        ( options {greedy=true;} : NMCHAR )*
		;
	
	// $ANTLR src "xml.g" 109
fragment VALUE : 
	        ( '\"' (~'\"')* '\"'
	        | '\'' (~'\'')* '\''
	        )
		;
	
	// $ANTLR src "xml.g" 115
fragment XMLDECL :
	        '<?' ('x'|'X') ('m'|'M') ('l'|'L')  
	          { System.out.println("XML Declaration"); }
	        ( ATTRIBUTE  )*  '?>'
		;
	
	// $ANTLR src "xml.g" 121
fragment ATTRIBUTE 
	    : name=ATTRIBUTE_NAME  '='  value=VALUE
	        { System.out.println("Attr: "+$name.text+"="+$value.text); }
	    ;
	
	// $ANTLR src "xml.g" 126
fragment ATTRIBUTE_NAME : 'version'|'name'|'type'|'state'|'trans'|'extends'|'implements'|'kind';
	
	// $ANTLR src "xml.g" 128
fragment DOCTYPE
	    :
	        '<!DOCTYPE' WS 'SYSTEM-SPEC' WS 'SYSTEM' WS VALUE  '>' ;
	
	//fragment DTDFILE:
	//				 '\"' (options {greedy=false;} : .)* '.dtd\"';
	    
	/* Definition of detailed elements */
	// $ANTLR src "xml.g" 136
COMMENT
		:	'<!--' (options {greedy=false;} : .)* '-->' {$channel=HIDDEN;}
		;
	
	//fragment WS_COMMENT : ( COMMENT?)+ ;
	
	// $ANTLR src "xml.g" 142
fragment T_ENDTAG : '<';
	
	// $ANTLR src "xml.g" 144
fragment T_ENDELEMENT : '/<';
	
	
	// $ANTLR src "xml.g" 147
fragment T_CLASS : '<CLASS'; 
	
	
	// $ANTLR src "xml.g" 150
fragment T_SYSTEMSPEC : '<SYSTEM-SPEC';
	
	// $ANTLR src "xml.g" 152
fragment T_END_SYSTEMSPEC : '</SYSTEM-SPEC>';
	
	// $ANTLR src "xml.g" 154
fragment T_INTERFACE : '<INTERFACE';
	
	// $ANTLR src "xml.g" 156
fragment T_METHOD : '<METHOD';
	
	// $ANTLR src "xml.g" 158
fragment T_RESULT : '<RESULT';
	
	// $ANTLR src "xml.g" 160
fragment T_PARAMETER: '<PARAMETER';
	
	// $ANTLR src "xml.g" 162
fragment T_END_METHOD: '</METHOD>';
	
	// $ANTLR src "xml.g" 164
fragment T_END_INTERFACE : '</INTERFACE>';
	
	// $ANTLR src "xml.g" 166
fragment T_PARENT : '<PARENT';
	
	// $ANTLR src "xml.g" 168
fragment T_MACHINE : '<MACHINE';
	
	// $ANTLR src "xml.g" 170
fragment T_END_MACHINE : '</MACHINE>';
	
	// $ANTLR src "xml.g" 172
fragment T_EVENTDEF : '<EVENTDEF';
	
	// $ANTLR src "xml.g" 174
fragment T_END_EVENTDEF: '</EVENTDEF>';
	
	// $ANTLR src "xml.g" 176
fragment T_STATE : '<STATE';
	
	// $ANTLR src "xml.g" 178
fragment T_ACTION : '<ACTION';
	
	// $ANTLR src "xml.g" 180
fragment T_END_STATE : '</STATE>';
	
	// $ANTLR src "xml.g" 182
fragment T_END_ACTION : '</ACTION>';
	
	// $ANTLR src "xml.g" 184
fragment T_OUTGOING : '<OUTGOING';
	
	// $ANTLR src "xml.g" 186
fragment T_TRANSITION : '<TRANSITION';
	
	// $ANTLR src "xml.g" 188
fragment T_END_TRANSITION : '</TRANSITION>';
	
	// $ANTLR src "xml.g" 190
fragment T_SOURCE : '<SOURCE';
	
	// $ANTLR src "xml.g" 192
fragment T_TARGET : '<TARGET';
	
	// $ANTLR src "xml.g" 194
fragment T_EVENT : '<EVENT';
	
	// $ANTLR src "xml.g" 196
fragment T_GUARD : '<GUARD';
	
	// $ANTLR src "xml.g" 198
fragment T_END_GUARD : '</GUARD>';
	
	// $ANTLR src "xml.g" 200
fragment T_INSTANCE : '<INSTANCE';
	
	// $ANTLR src "xml.g" 202
fragment T_END_INSTANCE : '</INSTANCE>';
	
	// $ANTLR src "xml.g" 204
fragment T_ARGUMENT : '<ARGUMENT';
	
	// $ANTLR src "xml.g" 206
fragment T_END_ARGUMENT : '</ARGUMENT>';
	
	
	
