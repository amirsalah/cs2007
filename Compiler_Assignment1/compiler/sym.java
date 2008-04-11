package compiler;

public class sym {
    /*
     * This class has the infrastructure that pertains to symbols.
     * 
     * We do it this way because Java lacks an enumeration type...
     */

  /* terminals */
    public static final int EOF             = 0; // Indicates lexer has run out of input
    public static final int ERROR           = 1; // illegal character..

//    public static final int tSLASH          = 15; // /
    public static final int tEQUALS         = 16; // =

    public static final int tMARK	        = 18; // <

    public static final int tCLOSETAG	    = 20; // >

    public static final int tSTRING         = 28; // "..a string"

    
    public static final int tATTRIBUTE_NAME = 30; // Attribute name

    public static final int tSYSTEMSPEC		= 31; //
    public static final int tINTERFACE		= 32;
    public static final int tCLASS			= 33;
    public static final int tMACHINE		= 34;
    public static final int tINSTANCE		= 35;
    
    public static final int tPARENT			= 36;
    public static final int tMETHOD			= 37;
    public static final int tRESULT			= 38;
    public static final int tPARAMETER		= 39;
    
    
    public static final int tEVENTDEF		= 40;
    public static final int tSTATE			= 41;
    public static final int tTRANSITION		= 42;
    public static final int tACTION			= 43;
    public static final int tOUTGOING		= 44;
    
    public static final int tSOURCE			= 45;
    public static final int tTARGET			= 46;
    public static final int tEVENT			= 47;
    public static final int tGUARD			= 48;
    public static final int tARGUMENT		= 49;
    
    public static final int tATTRIBUTE_VALUE= 50;
    public static final int tENDELEMENT		= 51; // />
    
    public static final int tPCDATA			= 52;
    
    public static final int tEND_SYSTEMSPEC		= 53;
    public static final int tEND_INTERFACE		= 54;
    //public static final int tEND_CLASS			= 33;
    public static final int tEND_MACHINE		= 55;
    public static final int tEND_INSTANCE		= 56;
    
    //public static final int tEND_PARENT			= 36;
    public static final int tEND_METHOD			= 57;
    //public static final int tEND_RESULT			= 38;
    //public static final int tEND_PARAMETER		= 39;
    
    
    public static final int tEND_EVENTDEF		= 58;
    public static final int tEND_STATE			= 59;
    public static final int tEND_TRANSITION		= 60;
    public static final int tEND_ACTION			= 61;
    //public static final int tEND_OUTGOING		    = 44;
    
    //public static final int tEND_SOURCE			= 45;
    //public static final int tEND_TARGET			= 46;
    //public static final int tEND_EVENT			= 47;
    public static final int tEND_GUARD			= 62;
    public static final int tEND_ARGUMENT		= 63;
    
    public static final int tDOCTYPE			= 64;
    public static final int tROOT				= 65; // SYSTEM-SPEC in this case
    public static final int tXMLHEAD			= 66; // <?XML
    public static final int tEND_XMLHEAD		= 67; // ?>
    public static final int tSYSTEM				= 68; // SYSTEM in the <!Doctype ...>

    public static String getTokenName(int tokenID){
    	String tokenName = null;
    	switch(tokenID){
    	case 0: tokenName = "tEOF";
    			break;
    	case 1: tokenName = "tERROR";
				break;
    	case 16: tokenName = "tEQUALS";
				break;
    	case 18: tokenName = "tMARK";
				break;				
    	case 20: tokenName = "tCLOSETAG";
				break;				
    	case 28: tokenName = "tSTRING";
				break;
    	case 30: tokenName = "tATTRIBUTE_NAME";
				break;
    	case 31: tokenName = "tSYSTEMSPEC";
				break;
    	case 32: tokenName = "tINTERFACE";
				break;
    	case 33: tokenName = "tCLASS";
				break;
    	case 34: tokenName = "tMACHINE";
				break;
    	case 35: tokenName = "tINSTANCE";
				break;
    	case 36: tokenName = "tPARENT";
				break;
    	case 37: tokenName = "tMETHOD";
				break;
    	case 38: tokenName = "tRESULT";
				break;
    	case 39: tokenName = "tPARAMETER";
				break;
    	case 40: tokenName = "tEVENTDEF";
				break;
    	case 41: tokenName = "tSTATE";
				break;
    	case 42: tokenName = "tTRANSITION";
				break;
    	case 43: tokenName = "tACTION";
				break;
    	case 44: tokenName = "tOUTGOING";
				break;
    	case 45: tokenName = "tSOURCE";
				break;
    	case 46: tokenName = "tTARGET";
				break;
    	case 47: tokenName = "tEVENT";
				break;
    	case 48: tokenName = "tGUARD";
				break;
    	case 49: tokenName = "tARGUMENT";
				break;
    	case 50: tokenName = "tATTRIBUTE_VALUE";
				break;
    	case 51: tokenName = "tENDELEMENT";
				break;
    	case 52: tokenName = "tPCDATA";
				break;
    	case 53: tokenName = "tEND_SYSTEMSPEC";
				break;
    	case 54: tokenName = "tEND_INTERFACE";
				break;
    	case 55: tokenName = "tEND_MACHINE";
				break;
    	case 56: tokenName = "tEND_INSTANCE";
				break;
    	case 57: tokenName = "tEND_METHOD";
				break;
    	case 58: tokenName = "tEND_EVENTDEF";
				break;
    	case 59: tokenName = "tEND_STATE";
				break;    	
		case 60: tokenName = "tEND_TRANSITION";
				break;
    	case 61: tokenName = "tEND_ACTION";
				break;
    	case 62: tokenName = "tEND_GUARD";
				break;
		case 63: tokenName = "tEND_ARGUMENT";
				break;
    	case 64: tokenName = "tEND_DOCTYPE";
				break;
    	case 65: tokenName = "tEND_ROOT";
				break;
    	case 66: tokenName = "tXMLHEAD";
				break;
    	case 67: tokenName = "tEND_XMLHEAD";
				break;
    	case 68: tokenName = "tSYSTEM";
				break;
    	}
    	
    	return tokenName;
    }
    
}