import java.lang.System;
import compiler.sym;  // In here, all the definitions of token values

class Sample {
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
    "Error: Illegal character."
    };
  
  public static final int E_ENDCOMMENT = 0; 
  public static final int E_STARTCOMMENT = 1; 
  public static final int E_UNCLOSEDSTR = 2; 
  public static final int E_UNMATCHED = 3; 

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
%state COMMENT,COMMENTLINE

ALPHA=[A-Za-z]
DIGIT=[0-9]
NONNEWLINE_WHITE_SPACE_CHAR=[\ \t\b\012]
WHITE_SPACE_CHAR=[\n\ \t\b\012]
STRING_TEXT=(\\\"|[^\n\"]|\\{WHITE_SPACE_CHAR}+\\)*
COMMENT_TEXT=([^/*\n]|[^*\n]"/"[^*\n]|[^/\n]"*"[^/\n]|"*"[^/\n]|"/"[^*\n])*


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
<YYINITIAL> ">"  { return (new Yytoken(sym.tGREATERTHAN,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> ">=" { return (new Yytoken(sym.tGREATEROREQUAL,yytext(),yyline,yychar,yychar+2)); }
<YYINITIAL> "&"  { return (new Yytoken(sym.tAND,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> "|"  { return (new Yytoken(sym.tOR,yytext(),yyline,yychar,yychar+1)); }
<YYINITIAL> ":=" { return (new Yytoken(sym.tBECOMES,yytext(),yyline,yychar,yychar+2)); }

<YYINITIAL> {NONNEWLINE_WHITE_SPACE_CHAR}+ { }

<YYINITIAL> "//" {
	       yybegin(COMMENTLINE);
}
<COMMENTLINE> [^\n] {
}

<COMMENTLINE> [\n] {
  yybegin(YYINITIAL);
}

<YYINITIAL,COMMENT> \n { }

<YYINITIAL> "/*" { yybegin(COMMENT); comment_count = comment_count + 1; }

<COMMENT> "/*" { comment_count = comment_count + 1; }
<COMMENT> "*/" { 
	comment_count = comment_count - 1; 
	Utility.my_assert(comment_count >= 0);
	if (comment_count == 0) {
    		yybegin(YYINITIAL);
	}
}
<COMMENT> {COMMENT_TEXT} { }


<YYINITIAL> \"{STRING_TEXT}\" {
	String str =  yytext().substring(1,yytext().length() - 1);
	
	Utility.my_assert(str.length() == yytext().length() - 2);
	return (new Yytoken(sym.tSTRING,str,yyline,yychar,yychar + str.length()));
}
<YYINITIAL> \"{STRING_TEXT} {
	String str =  yytext().substring(1,yytext().length());

	Utility.error(Utility.E_UNCLOSEDSTR);
	Utility.my_assert(str.length() == yytext().length() - 1);
	return (new Yytoken(sym.tSTRING,str,yyline,yychar,yychar + str.length()));
} 
<YYINITIAL> {DIGIT}+ { 
	return (new Yytoken(sym.tNUMBER,yytext(),yyline,yychar,yychar + yytext().length()));
}	
<YYINITIAL> "function" {
	return (new Yytoken(sym.tFUNCTION, yytext(), yyline, yychar, yychar+yytext().length()));
}
<YYINITIAL> [Pp][Rr][Oo][Cc][Ee][Dd][Uu][Rr][Ee] {
	return (new Yytoken(sym.tPROCEDURE, yytext(), yyline, yychar, yychar+yytext().length()));
}
<YYINITIAL> {ALPHA}({ALPHA}|{DIGIT}|_)* {
	return (new Yytoken(sym.tPROCEDURE,yytext(),yyline,yychar,yychar + yytext().length()));
}	
<YYINITIAL,COMMENT> . {
        System.out.println("Illegal character: <" + yytext() + ">");
	Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
