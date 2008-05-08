
public class sym {
    public static final int EOF            	 = 0; // Indicates lexer has run out of input
    public static final int ERROR                = 1; // illegal character..
    public static final int SYM_EQUALS         	 = 2;// =

///////////////////////////////////////////////////////////////////////////////
    public static final int LEFTANLGLE	   	 = 3; // <       LEFTANLGLE
    public static final int RIGHTANGLE	   	 = 4; // >	     RIGHTANGLE    
    public static final int LEFT_SYSTEMSPEC	 = 6; 	// LEFT_SYSTEMSPEC
    public static final int LEFT_INTERFACE	 = 7;//LEFT_INTERFACE
    public static final int SYM_CLASS		 = 8;
    public static final int LEFT_MACHINE         = 9;	// LEFT_MACHINE
    public static final int LEFT_INSTANCE        = 10;	// LEFT_INSTANCE
    
    public static final int SYM_PARENT		 = 11;
    public static final int LEFT_METHOD		 = 12;	// LEFT_METHOD
    public static final int SYM_RESULT		 = 13;
    public static final int SYM_PARAMETER		 = 14;
    
    
    public static final int LEFT_EVENTDEF	 = 15;		// LEFT_EVENTDEF
    public static final int LEFT_STATE		 = 16;	// LEFT_STATE
    public static final int LEFT_TRANSITION	 = 17;		// LEFT_TRANSITION
    public static final int LEFT_ACTION		 = 18;	// LEFT_ACTION
    public static final int SYM_OUTGOING		 = 19;		
    
    public static final int SYM_SOURCE		 = 20;	
    public static final int SYM_TARGET		 = 21;	
    public static final int SYM_EVENT		 = 22;	
    public static final int LEFT_GUARD		 = 23;	//LEFT_GUARD
    public static final int LEFT_ARG		 = 24;		//LEFT_ARG
    
    public static final int ATT_VALUE		 = 25;		// ATT_VALUE
    public static final int ELEMENT_RIGHT	 = 26; // />	ELEMENT_RIGHT
    public static final int SYM_PCDATA		 = 27;

    public static final int SYSTEMSPEC_RIGHT	 = 28;	// SYSTEMSPEC_RIGHT
    public static final int INTERFACE_RIGHT	 = 29;	// INTERFACE_RIGHT
    public static final int MACHINE_RIGHT	 = 30;
    public static final int INSTANCE_RIGHT     	 = 31;
    public static final int METHOD_RIGHT	 = 32;
    public static final int EVENTDEF_RIGHT	 = 33;
    public static final int STATE_RIGHT		 = 34;
    public static final int TRANSITION_RIGHT	 = 35;
    public static final int ACTION_RIGHT	 = 36;
    public static final int GUARD_RIGHT		 = 37;
    public static final int ARGUMENT_RIGHT	 = 38;	//ARGUMENT_RIGHT

    public static final int DOCTYPE		 = 39;
    public static final int LEFTXML		 = 40;	//LEFTXML
    public static final int RIGHTXML		 = 41;	//RIGHTXML
 
    public static final int SYM_VERSION             = 42;
    public static final int SYM_NAME                = 43; 
    public static final int SYM_TYPE            	 = 44; 
    public static final int SYM_ATTSTATE            = 45; 
    public static final int SYM_TRANS            	 = 46; 
    public static final int SYM_EXTENDS             = 47; 
    public static final int SYM_IMPLEMENTS          = 48; 
    public static final int SYM_KIND            	 = 49; 
    
    public static String tName(int tID){
    	String token;
    	switch(tID){
    	case 0: token = "EOF";
    		break;
    	case 1: token = "ERROR";
		break;
    	case 2: token = "SYM_EQUALS";
		break;
    	case 3: token = "LEFTANLGLE";
		break;				
    	case 4: token = "RIGHTANGLE";
		break;				
  
	case 6: token = "LEFT_SYSTEMSPEC";
		break;
    	case 7: token = "LEFT_INTERFACE";
		break;
    	case 8: token = "SYM_CLASS";
		break;
    	case 9: token = "SYM_MACHINE";
		break;
    	case 10: token = "SYM_INSTANCE";
		break;
    	case 11: token = "SYM_PARENT";
		break;
    	case 12: token = "LEFT_METHOD";
		break;
    	case 13: token = "SYM_RESULT";
		break;
    	case 14: token = "SYM_PARAMETER";
		break;
    	case 15: token = "LEFT_EVENTDEF";
		break;
    	case 16: token = "LEFT_STATE";
		break;
    	case 17: token = "LEFT_TRANSITION";
		break;
    	case 18: token = "LEFT_ACTION";
		break;
    	case 19: token = "SYM_OUTGOING";
		break;
    	case 20: token = "SYM_SOURCE";
		break;
    	case 21: token = "SYM_TARGET";
		break;
    	case 22: token = "SYM_EVENT";
		break;
    	case 23: token = "LEFT_GUARD";
		break;
    	case 24: token = "LEFT_ARG";
		break;
    	case 25: token = "ATT_VALUE";
		break;
    	case 26: token = "ELEMENT_RIGHT";
		break;
    	case 27: token = "SYM_PCDATA";
		break;
    	case 28: token = "SYSTEMSPEC_RIGHT";
		break;
    	case 29: token = "INTERFACE_RIGHT";
		break;
    	case 30: token = "MACHINE_RIGHT";
		break;
    	case 31: token = "INSTANCE_RIGHT";
		break;
    	case 32: token = "METHOD_RIGHT";
		break;
    	case 33: token = "EVENTDEF_RIGHT";
		break;
    	case 34: token = "STATE_RIGHT";
		break;    	
	case 35: token = "TRANSITION_RIGHT";
		break;
    	case 36: token = "ACTION_RIGHT";
		break;
    	case 37: token = "GUARD_RIGHT";
		break;
	case 38: token = "ARGUMENT_RIGHT";
		break;
    	case 39: token = "DOCTYPE";
		break;
    	case 40: token = "LEFTXML";
		break;
    	case 41: token = "RIGHTXML";
		break;
    	case 42: token = "SYM_VERSION";
		break;
    	case 43: token = "SYM_NAME";
		break;
    	case 44: token = "SYM_TYPE";
		break;
    	case 45: token = "SYM_ATTSTATE";
		break;
    	case 46: token = "SYM_TRANS";
		break;
    	case 47: token = "SYM_EXTENDS";
		break;
    	case 48: token = "SYM_IMPLEMENTS";
		break;
    	case 49: token = "SYM_KIND";
    		break;
	default:
		return null;
    	}
    	
    	return token;
    }
    
}
