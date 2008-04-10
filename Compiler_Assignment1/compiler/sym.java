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
    public static final int tCOMMA          = 2; // ,
    public static final int tCOLON          = 3; // :
    public static final int tSEMICOLON      = 4; // ;
    public static final int tLPAREN         = 5; // (
    public static final int tRPAREN         = 6; // )
    public static final int tLBRACK         = 7; // [
    public static final int tRBRACK         = 8; // ]
    public static final int tLBRACE         = 9; // {
    public static final int tRBRACE         = 10; // }
    public static final int tDOT            = 11; // .
    public static final int tPLUS           = 12; // +
    public static final int tMINUS          = 13; // -
    public static final int tSTAR           = 14; // *
    public static final int tSLASH          = 15; // /
    public static final int tEQUALS         = 16; // =
    public static final int tNOTEQUALS      = 17; // <>
    public static final int tLESSTHAN       = 18; // <
    public static final int tLESSOREQUAL    = 19; // <=
    public static final int tCLOSETAG	    = 20; // >
    public static final int tGREATEROREQUAL = 21; // >=
    public static final int tAND            = 22; // &
    public static final int tOR             = 23; // |
    public static final int tBECOMES        = 24; // :=
    public static final int tPROCEDURE      = 25; // procedure
    public static final int tIDENTIFIER     = 27; // ...any valid ID
    public static final int tSTRING         = 28; // "..a string"
    public static final int tNUMBER         = 29; // A Number (integer, series of digits)
    
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
    public static final int tENDELEMENT		= 51;
    
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

}