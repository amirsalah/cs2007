grammar xml;

options {
	language = Java;
}

DOCUMENT : XMLDECL? WS? DOCTYPE? WS? SYSTEM_SPEC WS?;

SYSTEM_SPEC : T_SYSTEMSPEC WS? T_ENDTAG WS_COMMENT? // <SYSTEM-SPEC>
							INTERFACE+
							CLASS+
							MACHINE+
							INSTANCE+ 
							T_END_SYSTEMSPEC WS_COMMENT?;

/* Interface section */
INTERFACE : T_INTERFACE WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?
					  PARENT*
					  METHOD*
					  T_END_INTERFACE WS_COMMENT?;

PARENT : T_PARENT WS ATTRIBUTE T_ENDELEMENT WS_COMMENT?;

METHOD : T_METHOD WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?
				 RESULT 
				 PARAMETER*
				 T_END_METHOD WS_COMMENT?;

RESULT : T_RESULT WS ATTRIBUTE WS? T_ENDELEMENT WS_COMMENT?;

PARAMETER : T_PARAMETER WS ATTRIBUTE WS ATTRIBUTE WS T_ENDELEMENT WS_COMMENT?;

/* Class section */
CLASS : T_CLASS WS ATTRIBUTE WS ATTRIBUTE WS? T_ENDELEMENT WS_COMMENT?;


/* Machine section */
MACHINE : T_MACHINE WS ATTRIBUTE WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?
					EVENTDEF* 
					STATE+ 
					TRANSITION* 
					T_END_MACHINE WS_COMMENT?;


EVENTDEF : T_EVENTDEF WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?
					 PARAMETER* 
					 T_END_EVENTDEF WS_COMMENT?;


STATE :  T_STATE WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?
				 ACTION* 
				 OUTGOING*
				 T_END_STATE WS_COMMENT?;

ACTION : T_ACTION WS T_ENDTAG
				 PCDATA
				 T_END_ACTION WS_COMMENT?;
				 
OUTGOING : T_OUTGOING WS ATTRIBUTE WS? T_ENDELEMENT WS_COMMENT?;


TRANSITION : T_TRANSITION WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?
						 SOURCE
						 TARGET 
						 EVENT 
						 GUARD 
						 ACTION*
						 T_END_TRANSITION WS_COMMENT?;

SOURCE : T_SOURCE WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?;

TARGET : T_TARGET WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?;

EVENT : T_EVENT WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?;

GUARD : T_GUARD WS? T_ENDTAG 
			  PCDATA
			  T_END_GUARD WS_COMMENT?;



INSTANCE : T_INSTANCE WS ATTRIBUTE WS ATTRIBUTE WS? T_ENDTAG WS_COMMENT?
					 ARGUMENT*
					 T_END_INSTANCE WS_COMMENT?;

ARGUMENT : T_ARGUMENT WS? T_ENDTAG
					 PCDATA
					 T_END_ARGUMENT WS_COMMENT?;




/* Tokens */
fragment ALPHA : ('A'..'Z')|('a'..'z');
fragment DIGIT : ('0'..'9');
fragment NEWLINE : '\r'? '\n' ;
fragment WS  :   (' '|'\t'|'\n'|'\r')+ {skip();} ;

fragment NMCHAR : ALPHA | DIGIT | '.' | '_' | '-' | ':' ;
fragment NMTOKEN: (NMCHAR)+ ;
//fragment STRING : 
fragment PCDATA : (~'<')+ ; 

fragment GENERIC_ID 
    : ( ALPHA | '_' | ':') 
        ( options {greedy=true;} : NMCHAR )*
	;

fragment VALUE : 
        ( '\"' (~'\"')* '\"'
        | '\'' (~'\'')* '\''
        )
	;

fragment XMLDECL :
        '<?' ('x'|'X') ('m'|'M') ('l'|'L') WS? 
          { System.out.println("XML Declaration"); }
        ( ATTRIBUTE WS? )*  '?>'
	;

fragment ATTRIBUTE 
    : name=ATTRIBUTE_NAME WS? '=' WS? value=VALUE
        { System.out.println("Attr: "+$name.Text+"="+$value.Text); }
    ;

fragment ATTRIBUTE_NAME : 'version'|'name'|'type'|'state'|'trans'|'extends'|'implements'|'kind';

fragment DOCTYPE
    :
        '<!DOCTYPE' WS 'SYSTEM-SPEC' WS 'SYSTEM' WS DTDFILE WS? '>' ;

fragment DTDFILE:
				 '\"' (options {greedy=false;} : .)* '.dtd\"';
    
/* Definition of detailed elements */
fragment COMMENT
	:	'<!--' (options {greedy=false;} : .)* '-->'
	;
	
fragment WS_COMMENT : (WS? COMMENT?)+ ;

fragment T_ENDTAG : '<';

fragment T_ENDELEMENT : '/<';


fragment T_CLASS : '<CLASS'; 


fragment T_SYSTEMSPEC : '<SYSTEM-SPEC';

fragment T_END_SYSTEMSPEC : '</SYSTEM-SPEC>';

fragment T_INTERFACE : '<INTERFACE';

fragment T_METHOD : '<METHOD';

fragment T_RESULT : '<RESULT';

fragment T_PARAMETER: '<PARAMETER';

fragment T_END_METHOD: '</METHOD>';

fragment T_END_INTERFACE : '</INTERFACE>';

fragment T_PARENT : '<PARENT';

fragment T_MACHINE : '<MACHINE';

fragment T_END_MACHINE : '</MACHINE>';

fragment T_EVENTDEF : '<EVENTDEF';

fragment T_END_EVENTDEF: '</EVENTDEF>';

fragment T_STATE : '<STATE';

fragment T_ACTION : '<ACTION';

fragment T_END_STATE : '</STATE>';

fragment T_END_ACTION : '</ACTION>';

fragment T_OUTGOING : '<OUTGOING';

fragment T_TRANSITION : '<TRANSITION';

fragment T_END_TRANSITION : '</TRANSITION>';

fragment T_SOURCE : '<SOURCE';

fragment T_TARGET : '<TARGET';

fragment T_EVENT : '<EVENT';

fragment T_GUARD : '<GUARD';

fragment T_END_GUARD : '</GUARD>';

fragment T_INSTANCE : '<INSTANCE';

fragment T_END_INSTANCE : '</INSTANCE>';

fragment T_ARGUMENT : '<ARGUMENT';

fragment T_END_ARGUMENT : '</ARGUMENT>';



