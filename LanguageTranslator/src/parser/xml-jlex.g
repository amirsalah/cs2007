
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
	
	parent : T_PARENT attribute T_ENDELEMENT ;
	
	method : T_METHOD attribute  T_ENDTAG 
					 result
					 parameter*
					 T_END_METHOD ;
	
	result : T_RESULT attribute  T_ENDELEMENT ;
	
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


///////////////////////////////////////////////////////
attribute : tATTRIBUTE_NAME tEQUALS tATTRIBUTE_VALUE

xmlDELC : tXMLHEAD attribute* tEND_XMLHEAD

docTYPE : tDOCTYPE tROOT tSYSTEM tATTRIBUTE_VALUE tENDETAG
