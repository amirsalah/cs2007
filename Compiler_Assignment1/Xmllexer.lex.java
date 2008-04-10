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


class Yylex {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

  private int comment_count = 0;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int GETPCDATA = 3;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int COMMENTLINE = 2;
	private final int yy_state_dtrans[] = {
		0,
		60,
		169,
		63
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NOT_ACCEPT,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NOT_ACCEPT,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NOT_ACCEPT,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NOT_ACCEPT,
		/* 82 */ YY_NOT_ACCEPT,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NOT_ACCEPT,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NOT_ACCEPT,
		/* 87 */ YY_NOT_ACCEPT,
		/* 88 */ YY_NOT_ACCEPT,
		/* 89 */ YY_NOT_ACCEPT,
		/* 90 */ YY_NOT_ACCEPT,
		/* 91 */ YY_NOT_ACCEPT,
		/* 92 */ YY_NOT_ACCEPT,
		/* 93 */ YY_NOT_ACCEPT,
		/* 94 */ YY_NOT_ACCEPT,
		/* 95 */ YY_NOT_ACCEPT,
		/* 96 */ YY_NOT_ACCEPT,
		/* 97 */ YY_NOT_ACCEPT,
		/* 98 */ YY_NOT_ACCEPT,
		/* 99 */ YY_NOT_ACCEPT,
		/* 100 */ YY_NOT_ACCEPT,
		/* 101 */ YY_NOT_ACCEPT,
		/* 102 */ YY_NOT_ACCEPT,
		/* 103 */ YY_NOT_ACCEPT,
		/* 104 */ YY_NOT_ACCEPT,
		/* 105 */ YY_NOT_ACCEPT,
		/* 106 */ YY_NOT_ACCEPT,
		/* 107 */ YY_NOT_ACCEPT,
		/* 108 */ YY_NOT_ACCEPT,
		/* 109 */ YY_NOT_ACCEPT,
		/* 110 */ YY_NOT_ACCEPT,
		/* 111 */ YY_NOT_ACCEPT,
		/* 112 */ YY_NOT_ACCEPT,
		/* 113 */ YY_NOT_ACCEPT,
		/* 114 */ YY_NOT_ACCEPT,
		/* 115 */ YY_NOT_ACCEPT,
		/* 116 */ YY_NOT_ACCEPT,
		/* 117 */ YY_NOT_ACCEPT,
		/* 118 */ YY_NOT_ACCEPT,
		/* 119 */ YY_NOT_ACCEPT,
		/* 120 */ YY_NOT_ACCEPT,
		/* 121 */ YY_NOT_ACCEPT,
		/* 122 */ YY_NOT_ACCEPT,
		/* 123 */ YY_NOT_ACCEPT,
		/* 124 */ YY_NOT_ACCEPT,
		/* 125 */ YY_NOT_ACCEPT,
		/* 126 */ YY_NOT_ACCEPT,
		/* 127 */ YY_NOT_ACCEPT,
		/* 128 */ YY_NOT_ACCEPT,
		/* 129 */ YY_NOT_ACCEPT,
		/* 130 */ YY_NOT_ACCEPT,
		/* 131 */ YY_NOT_ACCEPT,
		/* 132 */ YY_NOT_ACCEPT,
		/* 133 */ YY_NOT_ACCEPT,
		/* 134 */ YY_NOT_ACCEPT,
		/* 135 */ YY_NOT_ACCEPT,
		/* 136 */ YY_NOT_ACCEPT,
		/* 137 */ YY_NOT_ACCEPT,
		/* 138 */ YY_NOT_ACCEPT,
		/* 139 */ YY_NOT_ACCEPT,
		/* 140 */ YY_NOT_ACCEPT,
		/* 141 */ YY_NOT_ACCEPT,
		/* 142 */ YY_NOT_ACCEPT,
		/* 143 */ YY_NOT_ACCEPT,
		/* 144 */ YY_NOT_ACCEPT,
		/* 145 */ YY_NOT_ACCEPT,
		/* 146 */ YY_NOT_ACCEPT,
		/* 147 */ YY_NOT_ACCEPT,
		/* 148 */ YY_NOT_ACCEPT,
		/* 149 */ YY_NOT_ACCEPT,
		/* 150 */ YY_NOT_ACCEPT,
		/* 151 */ YY_NOT_ACCEPT,
		/* 152 */ YY_NOT_ACCEPT,
		/* 153 */ YY_NOT_ACCEPT,
		/* 154 */ YY_NOT_ACCEPT,
		/* 155 */ YY_NOT_ACCEPT,
		/* 156 */ YY_NOT_ACCEPT,
		/* 157 */ YY_NOT_ACCEPT,
		/* 158 */ YY_NOT_ACCEPT,
		/* 159 */ YY_NOT_ACCEPT,
		/* 160 */ YY_NOT_ACCEPT,
		/* 161 */ YY_NOT_ACCEPT,
		/* 162 */ YY_NOT_ACCEPT,
		/* 163 */ YY_NOT_ACCEPT,
		/* 164 */ YY_NOT_ACCEPT,
		/* 165 */ YY_NOT_ACCEPT,
		/* 166 */ YY_NOT_ACCEPT,
		/* 167 */ YY_NOT_ACCEPT,
		/* 168 */ YY_NOT_ACCEPT,
		/* 169 */ YY_NOT_ACCEPT,
		/* 170 */ YY_NOT_ACCEPT,
		/* 171 */ YY_NOT_ACCEPT,
		/* 172 */ YY_NOT_ACCEPT,
		/* 173 */ YY_NOT_ACCEPT,
		/* 174 */ YY_NOT_ACCEPT,
		/* 175 */ YY_NOT_ACCEPT,
		/* 176 */ YY_NOT_ACCEPT,
		/* 177 */ YY_NOT_ACCEPT,
		/* 178 */ YY_NOT_ACCEPT,
		/* 179 */ YY_NOT_ACCEPT,
		/* 180 */ YY_NOT_ACCEPT,
		/* 181 */ YY_NOT_ACCEPT,
		/* 182 */ YY_NOT_ACCEPT,
		/* 183 */ YY_NOT_ACCEPT,
		/* 184 */ YY_NOT_ACCEPT,
		/* 185 */ YY_NOT_ACCEPT,
		/* 186 */ YY_NOT_ACCEPT,
		/* 187 */ YY_NOT_ACCEPT,
		/* 188 */ YY_NOT_ACCEPT,
		/* 189 */ YY_NOT_ACCEPT,
		/* 190 */ YY_NO_ANCHOR,
		/* 191 */ YY_NOT_ACCEPT,
		/* 192 */ YY_NOT_ACCEPT,
		/* 193 */ YY_NOT_ACCEPT,
		/* 194 */ YY_NOT_ACCEPT,
		/* 195 */ YY_NOT_ACCEPT,
		/* 196 */ YY_NOT_ACCEPT,
		/* 197 */ YY_NOT_ACCEPT,
		/* 198 */ YY_NOT_ACCEPT,
		/* 199 */ YY_NOT_ACCEPT,
		/* 200 */ YY_NOT_ACCEPT,
		/* 201 */ YY_NOT_ACCEPT,
		/* 202 */ YY_NOT_ACCEPT,
		/* 203 */ YY_NOT_ACCEPT,
		/* 204 */ YY_NOT_ACCEPT,
		/* 205 */ YY_NOT_ACCEPT,
		/* 206 */ YY_NOT_ACCEPT,
		/* 207 */ YY_NOT_ACCEPT,
		/* 208 */ YY_NOT_ACCEPT,
		/* 209 */ YY_NOT_ACCEPT,
		/* 210 */ YY_NOT_ACCEPT,
		/* 211 */ YY_NOT_ACCEPT,
		/* 212 */ YY_NOT_ACCEPT,
		/* 213 */ YY_NOT_ACCEPT,
		/* 214 */ YY_NOT_ACCEPT,
		/* 215 */ YY_NOT_ACCEPT,
		/* 216 */ YY_NOT_ACCEPT,
		/* 217 */ YY_NOT_ACCEPT,
		/* 218 */ YY_NOT_ACCEPT,
		/* 219 */ YY_NOT_ACCEPT,
		/* 220 */ YY_NOT_ACCEPT,
		/* 221 */ YY_NOT_ACCEPT,
		/* 222 */ YY_NOT_ACCEPT,
		/* 223 */ YY_NOT_ACCEPT,
		/* 224 */ YY_NO_ANCHOR,
		/* 225 */ YY_NOT_ACCEPT,
		/* 226 */ YY_NOT_ACCEPT,
		/* 227 */ YY_NOT_ACCEPT,
		/* 228 */ YY_NOT_ACCEPT,
		/* 229 */ YY_NOT_ACCEPT,
		/* 230 */ YY_NOT_ACCEPT,
		/* 231 */ YY_NOT_ACCEPT,
		/* 232 */ YY_NOT_ACCEPT,
		/* 233 */ YY_NOT_ACCEPT,
		/* 234 */ YY_NOT_ACCEPT,
		/* 235 */ YY_NOT_ACCEPT,
		/* 236 */ YY_NOT_ACCEPT,
		/* 237 */ YY_NOT_ACCEPT,
		/* 238 */ YY_NOT_ACCEPT,
		/* 239 */ YY_NOT_ACCEPT,
		/* 240 */ YY_NO_ANCHOR,
		/* 241 */ YY_NOT_ACCEPT,
		/* 242 */ YY_NOT_ACCEPT,
		/* 243 */ YY_NOT_ACCEPT,
		/* 244 */ YY_NOT_ACCEPT,
		/* 245 */ YY_NOT_ACCEPT,
		/* 246 */ YY_NO_ANCHOR,
		/* 247 */ YY_NOT_ACCEPT,
		/* 248 */ YY_NOT_ACCEPT,
		/* 249 */ YY_NO_ANCHOR,
		/* 250 */ YY_NO_ANCHOR,
		/* 251 */ YY_NO_ANCHOR,
		/* 252 */ YY_NO_ANCHOR,
		/* 253 */ YY_NOT_ACCEPT,
		/* 254 */ YY_NOT_ACCEPT,
		/* 255 */ YY_NOT_ACCEPT,
		/* 256 */ YY_NOT_ACCEPT,
		/* 257 */ YY_NOT_ACCEPT,
		/* 258 */ YY_NOT_ACCEPT,
		/* 259 */ YY_NOT_ACCEPT,
		/* 260 */ YY_NOT_ACCEPT,
		/* 261 */ YY_NOT_ACCEPT,
		/* 262 */ YY_NO_ANCHOR,
		/* 263 */ YY_NO_ANCHOR,
		/* 264 */ YY_NO_ANCHOR,
		/* 265 */ YY_NO_ANCHOR,
		/* 266 */ YY_NO_ANCHOR,
		/* 267 */ YY_NO_ANCHOR,
		/* 268 */ YY_NO_ANCHOR,
		/* 269 */ YY_NOT_ACCEPT,
		/* 270 */ YY_NOT_ACCEPT,
		/* 271 */ YY_NOT_ACCEPT,
		/* 272 */ YY_NOT_ACCEPT,
		/* 273 */ YY_NOT_ACCEPT,
		/* 274 */ YY_NOT_ACCEPT,
		/* 275 */ YY_NOT_ACCEPT,
		/* 276 */ YY_NO_ANCHOR,
		/* 277 */ YY_NO_ANCHOR,
		/* 278 */ YY_NO_ANCHOR,
		/* 279 */ YY_NO_ANCHOR,
		/* 280 */ YY_NOT_ACCEPT,
		/* 281 */ YY_NOT_ACCEPT,
		/* 282 */ YY_NOT_ACCEPT,
		/* 283 */ YY_NO_ANCHOR,
		/* 284 */ YY_NO_ANCHOR,
		/* 285 */ YY_NO_ANCHOR,
		/* 286 */ YY_NO_ANCHOR,
		/* 287 */ YY_NO_ANCHOR,
		/* 288 */ YY_NO_ANCHOR,
		/* 289 */ YY_NO_ANCHOR,
		/* 290 */ YY_NO_ANCHOR,
		/* 291 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"61:8,20:2,21,61:2,23,61:18,20,22,58,61:3,18,61,4,5,13,11,1,12,10,14,60:10,2" +
",3,16,15,17,61:2,50,59,45,54,42,49,57,52,46,59:2,51,43,47,53,44,59,48,39,41" +
",55,56,59:2,40,59,6,61,7,61,60,61,31,59:2,37,25,59:3,28,59:2,38,32,30,29,35" +
",59,26,27,33,59,24,59,36,34,59,8,19,9,61:2,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,292,
"0,1:2,2,1:11,3,1,4,5,1:2,6,1,7,1:5,8,1:2,9,1,10,1:25,11,12,1,13,1:3,14,15,1" +
"6,12,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,4" +
"0,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,6" +
"5,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,9" +
"0,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,11" +
"1,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,1" +
"30,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148," +
"149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167" +
",168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,18" +
"6,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,2" +
"05,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223," +
"224,225,226,227,228,9,229,230,231,232,233,234,9,235")[0];

	private int yy_nxt[][] = unpackFromString(236,62,
"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21:2,22,-1,23,289,290,26" +
"8,291,290,252,290:2,262,290:24,68,290,22:2,-1:77,24,-1:63,25,-1:58,67,26,-1" +
",27,-1:4,72,-1:16,75,-1,77,78,79,80,81,82,-1,83,-1,84,-1:2,85,-1:3,194,-1:1" +
"9,28,-1:66,21:2,-1:64,290,279,290:32,-1,290,283,-1:3,29,-1:7,29,-1,29,-1:11" +
",29:34,30,29:2,-1:25,290:34,-1,290,283,-1:55,236,-1:7,1,70:11,73,70:8,61,70" +
":40,-1,70:11,166,70:49,1,71:15,170,71:45,-1:39,86,-1,87,191,269,-1:2,253,-1" +
":17,29,-1:7,29,-1,29,-1:11,29:34,-1,29:2,-1:25,290,32,290:32,-1,290,283,-1:" +
"2,71:15,-1,71:45,-1:12,88,-1:50,70:11,167,70:49,-1:24,290:3,32,290:30,-1,29" +
"0,283,-1:41,89,192,-1:11,226,-1:32,290:6,32,290:27,-1,290,283,-1:49,225,-1," +
"195,-1:67,193,-1:47,90,-1:7,91,-1:61,227,-1:62,92,-1:57,93,-1:56,196,-1:64," +
"197,-1:2,94,-1:68,229,-1:46,228,230,-1:68,242,-1:25,31,-1:88,241,-1:63,98,-" +
"1:65,204,-1:66,100,-1:50,101,-1,201,-1:77,207,-1:46,243,-1:67,107,-1:60,205" +
",-1:66,109,-1:51,110,-1:7,111,-1:50,112,-1:63,113,-1:75,115,-1:52,210,-1:72" +
",234,-1:46,281,-1:61,33,-1:64,120,-1:57,34,-1:73,122,-1:55,123,-1:57,124,-1" +
":57,35,-1:72,125,-1:59,126,-1:64,127,-1:64,128,-1:46,275,-1:63,131,-1:66,23" +
"7,-1:57,36,-1:60,37,-1:74,38,-1:48,39,-1:62,217,-1:66,135,-1:63,136,-1:53,4" +
"0,-1:37,41,-1:2,128:2,-1:83,282,-1:35,42,-1:98,139,-1:19,141,-1:90,142,-1:6" +
"2,43,-1:64,144,-1:66,145,-1:28,44,-1:2,137:2,-1:87,146,-1:56,148,-1:36,45,-" +
"1:83,151,-1:68,152,-1:64,46,-1:54,47,-1:64,154,-1:57,155,-1:77,48,-1:53,156" +
",-1:29,49,-1:86,157,-1:63,223,-1:70,159,-1:56,50,-1:55,51,-1:36,52,-1:2,155" +
":2,-1:57,53,-1:61,54,-1:86,161,-1:66,55,-1:61,164,-1:31,56,-1:89,57,-1:61,1" +
"65,-1:33,58,-1:61,59,-1:45,70:11,168,70:49,-1,70:16,62,70:44,-1,70:16,-1,70" +
":44,1,-1:75,171,-1:97,172,-1:6,173,-1:49,174,-1:2,175,-1:68,176,-1:47,177,-" +
"1:77,178,-1:54,179,-1:57,180,-1:70,181,-1:54,182,-1:66,183,-1:51,184,-1:72," +
"185,-1:54,186,-1:56,187,-1:36,64,-1:61,65,-1:91,188,-1:55,189,-1:37,66,-1:6" +
"8,290:8,69,290:25,-1,290,283,-1:57,95,-1:55,247,-1:53,203,-1:74,198,-1:54,2" +
"00,-1:52,102,-1:63,103,-1:70,202,-1:50,280,-1,105,-1:77,233,-1:46,114,-1:67" +
",116,-1:60,108,-1:66,208,-1:48,213,-1:63,244,-1:75,256,-1:52,211,-1:57,212," +
"-1:72,215,-1:55,134,-1:57,132,-1:64,133,-1:57,138,-1:66,137,-1:68,140,-1:48" +
",238,-1:65,150,-1:63,147,-1:56,149,-1:64,158,-1:69,160,-1:50,162,-1:43,290:" +
"11,69,290:22,-1,290,283,-1:51,97,-1:66,96,-1:54,99,-1:52,206,-1:63,104,-1:7" +
"0,261,-1:58,117,-1:66,119,-1:51,121,-1:72,235,-1:54,219,-1:57,143,-1:66,220" +
",-1:56,153,-1:61,163,-1:43,290:9,69,290:24,-1,290,283,-1:42,209,-1:70,231,-" +
"1:58,118,-1:56,129,-1:72,216,-1:32,290:6,74,290:27,-1,290,283,-1:42,106,-1:" +
"62,130,-1:43,290:5,76,290:28,-1,290,283,-1:25,290:13,74,290:20,-1,290,283,-" +
"1:25,290:9,74,290:24,-1,290,283,-1:25,290:7,190,290:26,-1,290,283,-1:48,199" +
",-1:59,232,-1:68,245,-1:52,214,-1:65,218,-1:64,221,-1:57,222,-1:59,239,-1:5" +
"8,248,-1:44,290:2,264,290:7,224,290:23,-1,290,283,-1:25,290:7,240,290:26,-1" +
",290,283,-1:25,290:7,246,290:26,-1,290,283,-1:25,290:4,249,290:29,-1,290,28" +
"3,-1:25,290:6,250,290:27,-1,290,283,-1:25,290:6,251,290:27,-1,290,283,-1:25" +
",290:9,263,290:24,-1,290,283,-1:43,270,-1:7,254,-1:52,255,-1:70,257,-1:60,2" +
"58,-1:53,259,-1:59,260,-1:68,273,-1:39,290:3,265,290:30,-1,290,283,-1:25,29" +
"0,266,290:32,-1,290,283,-1:25,290,267,290:32,-1,290,283,-1:25,290:2,276,290" +
":31,-1,290,283,-1:42,271,-1:68,272,-1:25,274,-1:73,290:9,277,290:24,-1,290," +
"283,-1:25,290:11,286,290:22,-1,290,283,-1:25,290:14,287,290:19,-1,290,283,-" +
"1:25,290,288,290:32,-1,290,283,-1:25,290:8,278,290:25,-1,290,283,-1:25,290:" +
"12,284,290:21,-1,290,283,-1:25,290:8,285,290:25,-1,290,283,-1");

	public Yytoken yylex ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

  return (new Yytoken(sym.EOF,yytext(),yyline,yychar,yychar+1));
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{ return (new Yytoken(sym.tCOMMA,yytext(),yyline,yychar,yychar+1)); }
					case -3:
						break;
					case 3:
						{ return (new Yytoken(sym.tCOLON,yytext(),yyline,yychar,yychar+1)); }
					case -4:
						break;
					case 4:
						{ return (new Yytoken(sym.tSEMICOLON,yytext(),yyline,yychar,yychar+1)); }
					case -5:
						break;
					case 5:
						{ return (new Yytoken(sym.tLPAREN,yytext(),yyline,yychar,yychar+1)); }
					case -6:
						break;
					case 6:
						{ return (new Yytoken(sym.tRPAREN,yytext(),yyline,yychar,yychar+1)); }
					case -7:
						break;
					case 7:
						{ return (new Yytoken(sym.tLBRACK,yytext(),yyline,yychar,yychar+1)); }
					case -8:
						break;
					case 8:
						{ return (new Yytoken(sym.tRBRACK,yytext(),yyline,yychar,yychar+1)); }
					case -9:
						break;
					case 9:
						{ return (new Yytoken(sym.tLBRACE,yytext(),yyline,yychar,yychar+1)); }
					case -10:
						break;
					case 10:
						{ return (new Yytoken(sym.tRBRACE,yytext(),yyline,yychar,yychar+1)); }
					case -11:
						break;
					case 11:
						{ return (new Yytoken(sym.tDOT,yytext(),yyline,yychar,yychar+1)); }
					case -12:
						break;
					case 12:
						{ return (new Yytoken(sym.tPLUS,yytext(),yyline,yychar,yychar+1)); }
					case -13:
						break;
					case 13:
						{ return (new Yytoken(sym.tMINUS,yytext(),yyline,yychar,yychar+1)); }
					case -14:
						break;
					case 14:
						{ return (new Yytoken(sym.tSTAR,yytext(),yyline,yychar,yychar+1)); }
					case -15:
						break;
					case 15:
						{ return (new Yytoken(sym.tSLASH,yytext(),yyline,yychar,yychar+1)); }
					case -16:
						break;
					case 16:
						{ return (new Yytoken(sym.tEQUALS,yytext(),yyline,yychar,yychar+1)); }
					case -17:
						break;
					case 17:
						{ return (new Yytoken(sym.tLESSTHAN,yytext(),yyline,yychar,yychar+1)); }
					case -18:
						break;
					case 18:
						{ return (new Yytoken(sym.tCLOSETAG,yytext(),yyline,yychar,yychar+1)); }
					case -19:
						break;
					case 19:
						{ return (new Yytoken(sym.tAND,yytext(),yyline,yychar,yychar+1)); }
					case -20:
						break;
					case 20:
						{ return (new Yytoken(sym.tOR,yytext(),yyline,yychar,yychar+1)); }
					case -21:
						break;
					case 21:
						{ }
					case -22:
						break;
					case 22:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -23:
						break;
					case 23:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -24:
						break;
					case 24:
						{ return (new Yytoken(sym.tBECOMES,yytext(),yyline,yychar,yychar+2)); }
					case -25:
						break;
					case 25:
						{ return (new Yytoken(sym.tENDELEMENT,yytext(),yyline,yychar,yychar+1)); }
					case -26:
						break;
					case 26:
						{ return (new Yytoken(sym.tLESSOREQUAL,yytext(),yyline,yychar,yychar+2)); }
					case -27:
						break;
					case 27:
						{ return (new Yytoken(sym.tNOTEQUALS,yytext(),yyline,yychar,yychar+2)); }
					case -28:
						break;
					case 28:
						{ return (new Yytoken(sym.tGREATEROREQUAL,yytext(),yyline,yychar,yychar+2)); }
					case -29:
						break;
					case 29:
						{
	String str =  yytext().substring(1,yytext().length());
	Utility.error(Utility.E_UNCLOSEDSTR);
	Utility.my_assert(str.length() == yytext().length() - 1);
	return (new Yytoken(sym.tSTRING,str,yyline,yychar,yychar + str.length()));
}
					case -30:
						break;
					case 30:
						{
	String str =  yytext().substring(1,yytext().length() - 1);
	Utility.my_assert(str.length() == yytext().length() - 2);
	return (new Yytoken(sym.tSTRING,str,yyline,yychar,yychar + str.length()));
}
					case -31:
						break;
					case 31:
						{ yybegin(COMMENT); comment_count = comment_count + 1; }
					case -32:
						break;
					case 32:
						{return (new Yytoken(sym.tATTRIBUTE_NAME, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -33:
						break;
					case 33:
						{return (new Yytoken(sym.tSTATE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -34:
						break;
					case 34:
						{return (new Yytoken(sym.tEVENT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -35:
						break;
					case 35:
						{return (new Yytoken(sym.tCLASS, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -36:
						break;
					case 36:
						{return (new Yytoken(sym.tSOURCE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -37:
						break;
					case 37:
						{return (new Yytoken(sym.tTARGET, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -38:
						break;
					case 38:
						{return (new Yytoken(sym.tMETHOD, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -39:
						break;
					case 39:
						{return (new Yytoken(sym.tPARENT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -40:
						break;
					case 40:
						{return (new Yytoken(sym.tRESULT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -41:
						break;
					case 41:
						{
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tGUARD, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -42:
						break;
					case 42:
						{return (new Yytoken(sym.tEND_STATE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -43:
						break;
					case 43:
						{return (new Yytoken(sym.tMACHINE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -44:
						break;
					case 44:
						{
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tACTION, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -45:
						break;
					case 45:
						{return (new Yytoken(sym.tEND_METHOD, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -46:
						break;
					case 46:
						{return (new Yytoken(sym.tEVENTDEF, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -47:
						break;
					case 47:
						{return (new Yytoken(sym.tINSTANCE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -48:
						break;
					case 48:
						{return (new Yytoken(sym.tOUTGOING, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -49:
						break;
					case 49:
						{return (new Yytoken(sym.tEND_MACHINE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -50:
						break;
					case 50:
						{return (new Yytoken(sym.tPARAMETER, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -51:
						break;
					case 51:
						{return (new Yytoken(sym.tINTERFACE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -52:
						break;
					case 52:
						{
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tARGUMENT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -53:
						break;
					case 53:
						{return (new Yytoken(sym.tEND_EVENTDEF, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -54:
						break;
					case 54:
						{return (new Yytoken(sym.tEND_INSTANCE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -55:
						break;
					case 55:
						{return (new Yytoken(sym.tTRANSITION, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -56:
						break;
					case 56:
						{return (new Yytoken(sym.tEND_INTERFACE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -57:
						break;
					case 57:
						{return (new Yytoken(sym.tSYSTEMSPEC, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -58:
						break;
					case 58:
						{return (new Yytoken(sym.tEND_TRANSITION, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -59:
						break;
					case 59:
						{return (new Yytoken(sym.tEND_SYSTEMSPEC, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -60:
						break;
					case 60:
						{ }
					case -61:
						break;
					case 61:
						{ }
					case -62:
						break;
					case 62:
						{ 
	comment_count = comment_count - 1; 
	Utility.my_assert(comment_count >= 0);
	if (comment_count == 0) {
    		yybegin(YYINITIAL);
	}
}
					case -63:
						break;
					case 63:
						{
	return (new Yytoken(sym.tPCDATA, yytext(), yyline, yychar, yychar + yytext().length()));
}
					case -64:
						break;
					case 64:
						{
	yybegin(YYINITIAL);
	return (new Yytoken(sym.tEND_GUARD, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -65:
						break;
					case 65:
						{
	yybegin(YYINITIAL);
	return (new Yytoken(sym.tEND_ACTION, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -66:
						break;
					case 66:
						{
	yybegin(YYINITIAL);
	return (new Yytoken(sym.tEND_ARGUMENT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -67:
						break;
					case 68:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -68:
						break;
					case 69:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -69:
						break;
					case 70:
						{ }
					case -70:
						break;
					case 71:
						{
	return (new Yytoken(sym.tPCDATA, yytext(), yyline, yychar, yychar + yytext().length()));
}
					case -71:
						break;
					case 73:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -72:
						break;
					case 74:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -73:
						break;
					case 76:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -74:
						break;
					case 190:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -75:
						break;
					case 224:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -76:
						break;
					case 240:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -77:
						break;
					case 246:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -78:
						break;
					case 249:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -79:
						break;
					case 250:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -80:
						break;
					case 251:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -81:
						break;
					case 252:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -82:
						break;
					case 262:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -83:
						break;
					case 263:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -84:
						break;
					case 264:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -85:
						break;
					case 265:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -86:
						break;
					case 266:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -87:
						break;
					case 267:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -88:
						break;
					case 268:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -89:
						break;
					case 276:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -90:
						break;
					case 277:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -91:
						break;
					case 278:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -92:
						break;
					case 279:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -93:
						break;
					case 283:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -94:
						break;
					case 284:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -95:
						break;
					case 285:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -96:
						break;
					case 286:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -97:
						break;
					case 287:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -98:
						break;
					case 288:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -99:
						break;
					case 289:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -100:
						break;
					case 290:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -101:
						break;
					case 291:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -102:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
