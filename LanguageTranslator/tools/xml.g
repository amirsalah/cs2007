grammar xml;

options {
    language = Java;
}

@lexer::members{
    boolean PCDATAMode = false;
}

document : xmlDECL? docTYPE? system_spec EOF;

system_spec : T_SYSTEMSPEC  T_ENDTAG
                                     interface_x+
                                     class_x+
                                     machine+
                                     instance+
                                     T_END_SYSTEMSPEC;

/* Interface section */
interface_x : T_INTERFACE  attribute T_ENDTAG
                                              parent*
                                              method*
                                              T_END_INTERFACE ;

parent : T_PARENT attribute T_ENDELEMENT;

method : T_METHOD attribute  T_ENDTAG
                                      result
                                      parameter*
                                      T_END_METHOD ;

result : T_RESULT attribute  T_ENDELEMENT;

parameter : T_PARAMETER attribute attribute T_ENDELEMENT ;

/* Class section */
class_x : T_CLASS attribute attribute  T_ENDELEMENT ;


/* Machine section */
machine : T_MACHINE attribute attribute  T_ENDTAG
                                                  eventdef*
                                                  state+
                                                  transition*
                                                  T_END_MACHINE ;


eventdef : T_EVENTDEF attribute  T_ENDTAG
                                          parameter*
                                          T_END_EVENTDEF ;


state :  T_STATE attribute  T_ENDTAG
                                     action*
                                     outgoing*
                                     T_END_STATE ;

action : T_ACTION
                  PCDATA
                  T_END_ACTION;

outgoing : T_OUTGOING attribute  T_ENDELEMENT;


transition : T_TRANSITION attribute  T_ENDTAG
                                              source
                                              target
                                              event
                                              guard
                                              action*
                                              T_END_TRANSITION;

source : T_SOURCE attribute  T_ENDELEMENT;

target : T_TARGET attribute  T_ENDELEMENT;

event : T_EVENT attribute  T_ENDELEMENT;

guard : T_GUARD
                PCDATA
                T_END_GUARD;



instance : T_INSTANCE attribute attribute T_ENDTAG
                                                   argument*
                                                   T_END_INSTANCE;

argument : T_ARGUMENT
                      PCDATA
                      T_END_ARGUMENT;


/* Tokens */
fragment ALPHA : ('A'..'Z')|('a'..'z');
fragment DIGIT : ('0'..'9');
fragment NEWLINE : '\r'? '\n' ;
WS  :   (' '|'\t'|'\n'|'\r')+ {$channel = HIDDEN;} ;

fragment NMCHAR : ALPHA | DIGIT | '.' | '_' | '-' | ':' ;
fragment NMTOKEN: (NMCHAR)+ ;
//fragment STRING :
PCDATA : {PCDATAMode}?=> (~'<')+ ;

ATTRIBUTEVALUE : {!PCDATAMode}?=>
            ( '\"' (~'\"')* '\"'
            | '\'' (~'\'')* '\''
            )
        ;

fragment xmlDECL :
            '<?' ('x'|'X') ('m'|'M') ('l'|'L')
              { System.out.println("XML Declaration"); }
            ( attribute  )*  '?>'
        ;

fragment attribute
        : name=attribute_NAME  '='  value=ATTRIBUTEVALUE
            { System.out.println("Attr: "+$name.text+"="+$value.text); }
        ;

fragment attribute_NAME : 'version'|'name'|'type'|'state'|'trans'|'extends'|'implements'|'kind';

fragment docTYPE
        :
            '<!DOCTYPE' 'SYSTEM-SPEC' 'SYSTEM' ATTRIBUTEVALUE  '>'
        ;

    //fragment DTDFILE:
    //				 '\"' (options {greedy=false;} : .)* '.dtd\"';

/* Definition of detailed elements */
COMMENT
      :	'<!--' (options {greedy=false;} : .)* '-->' {$channel=HIDDEN;}
      ;

T_ENDTAG : '>' ;

T_ENDELEMENT : '/>'  ;


T_CLASS : '<CLASS'  ;


T_SYSTEMSPEC : '<SYSTEM-SPEC' ;

T_END_SYSTEMSPEC : '</SYSTEM-SPEC' (' ')* '>' ;

T_INTERFACE : '<INTERFACE' ;

T_METHOD : '<METHOD' ;

T_RESULT : '<RESULT' ;

T_PARAMETER: '<PARAMETER' ;

T_END_METHOD: '</METHOD' (' ')* '>' ;

T_END_INTERFACE : '</INTERFACE' (' ')* '>' ;

T_PARENT : '<PARENT' ;

T_MACHINE : '<MACHINE' ;

T_END_MACHINE : '</MACHINE' (' ')* '>' ;

T_EVENTDEF : '<EVENTDEF' ;

T_END_EVENTDEF: '</EVENTDEF' (' ')* '>';

T_STATE : '<STATE' ;

T_ACTION : '<ACTION' (' ')* '>' { PCDATAMode = true; };

T_END_STATE : '</STATE>';

T_END_ACTION : '</ACTION' (' ')* '>' { PCDATAMode = false; };

T_OUTGOING : '<OUTGOING' ;

T_TRANSITION : '<TRANSITION' ;

T_END_TRANSITION : '</TRANSITION' (' ')* '>' ;

T_SOURCE : '<SOURCE';

T_TARGET : '<TARGET' ;

T_EVENT : '<EVENT' ;

T_GUARD : '<GUARD' (' ')* '>' { PCDATAMode = true; };

T_END_GUARD : '</GUARD' (' ')* '>' { PCDATAMode = false; };

T_INSTANCE : '<INSTANCE' ;

T_END_INSTANCE : '</INSTANCE' (' ')* '>' ;

T_ARGUMENT : '<ARGUMENT' (' ')* '>' { PCDATAMode = true; };

T_END_ARGUMENT : '</ARGUMENT' (' ')* '>' { PCDATAMode = false; };
