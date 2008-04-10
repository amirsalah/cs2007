import java.lang.System;
import compiler.sym;  // In here, all the definitions of token values

class XmlLexer {
    /*
     * Just a simple driver - loops till end-of-file **
     * prints out the yytoken received..via the to_string method in yytoken
     *
     * **NOTE: Eof weirdness - For some strange reason (as yet unresolved)
     * it takes 3 (three) ^D's to make EOF happen.  Don't know why.
     *
     */

    public static void main(String argv[]) throws java.io.IOException {
	Yylex yy = new Yylex(System.in);
	Yytoken t;
	do {
	    t = yy.yylex();
	    System.out.println(t);
	} while (t.m_index != sym.EOF);
    }
}

class Utility {
  public static void my_assert(boolean expr) { 
	if (false == expr) {
	  throw (new Error("Error: Assertion failed."));
	}
  }
  
  private static final String errorMsg[] = {
    "Error: Unmatched end-of-comment punctuation.",
    "Error: Unmatched start-of-comment punctuation.",
    "Error: Unclosed string.",
    "Error: Illegal character.",
    "Error: Illegal string."
    };
  
  public static final int E_ENDCOMMENT = 0; 
  public static final int E_STARTCOMMENT = 1; 
  public static final int E_UNCLOSEDSTR = 2; 
  public static final int E_UNMATCHED = 3; 
  public static final int E_UNMATCHED_STRING = 4;

  public static void error(int code) {
	System.out.println(errorMsg[code]);
      }
}

class Yytoken {
  Yytoken (int index, String text, int line, int charBegin, int charEnd) {
	m_index = index;
	m_text = new String(text);
	m_line = line;
	m_charBegin = charBegin;
	m_charEnd = charEnd;
      }

  public int m_index;
  public String m_text;
  public int m_line;
  public int m_charBegin;
  public int m_charEnd;
  
  public String toString() {
      return "Token #"+m_index+": "+m_text+" (line "+m_line+", character pos"
	  +m_charBegin+":"+m_charEnd+" )";
  }
}

%%

%{
  private int comment_count = 0;
%}

%eofval{
  return (new Yytoken(sym.EOF,yytext(),yyline,yychar,yychar+1));
%eofval}
%line
%char
%state COMMENT,COMMENTLINE,GETPCDATA



ALPHA=[A-Za-z]
DIGIT=[0-9]
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b\012]
WHITE_SPACE_CHAR=[\n\ \t\b\012]
STRING_TEXT=(\\\"|[^\n\"]|\\{WHITE_SPACE_CHAR}+\\)*
COMMENT_TEXT=([^-]|-[^-]|--[^>])*
ATTRIBUTE_NAME="version"|"name"|"type"|"state"|"trans"|"extends"|"implements"
NAMECHAR={ALPHA}|{DIGIT}|"."|"-"|"_"|":"
NMTOKEN=({NAMECHAR})+
PCDATA = ([^<])*

%% 

<YYINITIAL> "," { return (new Yytoken(sym.tCOMMA,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> ":" { return (new Yytoken(sym.tCOLON,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> ";" { return (new Yytoken(sym.tSEMICOLON,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "(" { return (new Yytoken(sym.tLPAREN,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> ")" { return (new Yytoken(sym.tRPAREN,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "[" { return (new Yytoken(sym.tLBRACK,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "]" { return (new Yytoken(sym.tRBRACK,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "{" { return (new Yytoken(sym.tLBRACE,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "}" { return (new Yytoken(sym.tRBRACE,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "." { return (new Yytoken(sym.tDOT,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "+" { return (new Yytoken(sym.tPLUS,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "-" { return (new Yytoken(sym.tMINUS,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "*" { return (new Yytoken(sym.tSTAR,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "/" { return (new Yytoken(sym.tSLASH,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "=" { return (new Yytoken(sym.tEQUALS,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "<>" { return (new Yytoken(sym.tNOTEQUALS,yytext(),yyline,yychar,yychar+2)); }
<YYINITIAL> "<"  { return (new Yytoken(sym.tLESSTHAN,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "<=" { return (new Yytoken(sym.tLESSOREQUAL,yytext(),yyline,yychar,yychar+2)); }
<YYINITIAL> ">"  { return (new Yytoken(sym.tCLOSETAG,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "/>"  { return (new Yytoken(sym.tENDELEMENT,yytext(),yyline,yychar,yychar+1)); }

<YYINITIAL> ">=" { return (new Yytoken(sym.tGREATEROREQUAL,yytext(),yyline,yychar,yychar+2)); }
<YYINITIAL> "&"  { return (new Yytoken(sym.tAND,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "|"  { return (new Yytoken(sym.tOR,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> ":=" { return (new Yytoken(sym.tBECOMES,yytext(),yyline,yychar,yychar+2)); }

<YYINITIAL> {NONNEWLINE_WHITE_SPACE_CHAR}+ { }

<YYINITIAL,COMMENT> \n { }

<YYINITIAL> "<!--" { yybegin(COMMENT); comment_count = comment_count + 1; }

<COMMENT> "-->" { 
	comment_count = comment_count - 1; 
	Utility.my_assert(comment_count >= 0);
	if (comment_count == 0) {
    		yybegin(YYINITIAL);
	}
}

<COMMENT> {COMMENT_TEXT} { }

<YYINITIAL> {ATTRIBUTE_NAME} {return (new Yytoken(sym.tATTRIBUTE_NAME, yytext(), yyline, yychar, yychar+yytext().length()));}

<YYINITIAL> "<SYSTEM-SPEC" {return (new Yytoken(sym.tSYSTEMSPEC, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<INTERFACE" {return (new Yytoken(sym.tINTERFACE, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<CLASS" {return (new Yytoken(sym.tCLASS, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<MACHINE" {return (new Yytoken(sym.tMACHINE, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<INSTANCE" {return (new Yytoken(sym.tINSTANCE, yytext(), yyline, yychar, yychar+yytext().length()));}

<YYINITIAL> "<PARENT" {return (new Yytoken(sym.tPARENT, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<METHOD" {return (new Yytoken(sym.tMETHOD, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<RESULT" {return (new Yytoken(sym.tRESULT, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<PARAMETER" {return (new Yytoken(sym.tPARAMETER, yytext(), yyline, yychar, yychar+yytext().length()));}


<YYINITIAL> "<EVENTDEF" {return (new Yytoken(sym.tEVENTDEF, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<STATE" {return (new Yytoken(sym.tSTATE, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<TRANSITION" {return (new Yytoken(sym.tTRANSITION, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<ACTION"({WHITE_SPACE_CHAR})*">" {
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tACTION, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<OUTGOING" {return (new Yytoken(sym.tOUTGOING, yytext(), yyline, yychar, yychar+yytext().length()));}

<YYINITIAL> "<SOURCE" {return (new Yytoken(sym.tSOURCE, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<TARGET" {return (new Yytoken(sym.tTARGET, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<EVENT" {return (new Yytoken(sym.tEVENT, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<GUARD"({WHITE_SPACE_CHAR})*">" {
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tGUARD, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "<ARGUMENT"({WHITE_SPACE_CHAR})*">" {
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tARGUMENT, yytext(), yyline, yychar, yychar+yytext().length()));}

<YYINITIAL> "</SYSTEM-SPEC>" {return (new Yytoken(sym.tEND_SYSTEMSPEC, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "</INTERFACE>" {return (new Yytoken(sym.tEND_INTERFACE, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "</MACHINE>" {return (new Yytoken(sym.tEND_MACHINE, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "</INSTANCE>" {return (new Yytoken(sym.tEND_INSTANCE, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "</METHOD>" {return (new Yytoken(sym.tEND_METHOD, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "</EVENTDEF>" {return (new Yytoken(sym.tEND_EVENTDEF, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "</STATE>" {return (new Yytoken(sym.tEND_STATE, yytext(), yyline, yychar, yychar+yytext().length()));}
<YYINITIAL> "</TRANSITION>" {return (new Yytoken(sym.tEND_TRANSITION, yytext(), yyline, yychar, yychar+yytext().length()));}


<YYINITIAL> \"{NMTOKEN}\" {
	String str =  yytext().substring(1,yytext().length() - 1);
	
	Utility.my_assert(str.length() == yytext().length() - 2);
	return (new Yytoken(sym.tSTRING,str,yyline,yychar,yychar + str.length()));
}

<YYINITIAL> \"{NMTOKEN} {
	String str =  yytext().substring(1,yytext().length());

	Utility.error(Utility.E_UNCLOSEDSTR);
	Utility.my_assert(str.length() == yytext().length() - 1);
	return (new Yytoken(sym.tSTRING,str,yyline,yychar,yychar + str.length()));
} 

<GETPCDATA> {PCDATA} {
	return (new Yytoken(sym.tPCDATA, yytext(), yyline, yychar, yychar + yytext().length()));
}

<GETPCDATA> "</ACTION>" {
	yybegin(YYINITIAL);
	return (new Yytoken(sym.tEND_ACTION, yytext(), yyline, yychar, yychar+yytext().length()));
}

<GETPCDATA> "</GUARD>" {
	yybegin(YYINITIAL);
	return (new Yytoken(sym.tEND_GUARD, yytext(), yyline, yychar, yychar+yytext().length()));
}

<GETPCDATA> "</ARGUMENT>" {
	yybegin(YYINITIAL);
	return (new Yytoken(sym.tEND_ARGUMENT, yytext(), yyline, yychar, yychar+yytext().length()));
}

<YYINITIAL> {ALPHA}({ALPHA}|{DIGIT}|_)* {
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}	

<YYINITIAL,COMMENT> . {
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
