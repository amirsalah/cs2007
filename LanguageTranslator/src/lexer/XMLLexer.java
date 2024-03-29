/* Author: Bo CHEN
 * ID: a1139520
 */
package lexer;
import java.lang.System;
import lexer.symbol.sym;
import lexer.Yytoken;
import java.io.FileReader;
public class XMLLexer {
    private Yylex yy;
    public void manualLexer() throws java.io.IOException {
        yy = new Yylex(System.in);
        Yytoken t;
        do {
            t = yy.yylex();
            System.out.println(t);
        } while (t.m_index != sym.EOF);
    }
    public void autoLexer(String sourceFileName) throws java.io.IOException{
        yy = new Yylex(new FileReader(sourceFileName));
        Yytoken t;
        do {
            t = yy.yylex();
            System.out.println(t);
        } while (t.m_index != sym.EOF);
    }
    public void initLexer(String sourceFileName) throws java.io.IOException {
        yy = new Yylex(new FileReader(sourceFileName));
    }
    public Yytoken nextToken() throws java.io.IOException {
        return yy.yylex();
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
		48,
		171,
		51
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
		/* 52 */ YY_NOT_ACCEPT,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NOT_ACCEPT,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NOT_ACCEPT,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NOT_ACCEPT,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NOT_ACCEPT,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NOT_ACCEPT,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NOT_ACCEPT,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NOT_ACCEPT,
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
		/* 172 */ YY_NO_ANCHOR,
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
		/* 190 */ YY_NOT_ACCEPT,
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
		/* 212 */ YY_NO_ANCHOR,
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
		/* 224 */ YY_NOT_ACCEPT,
		/* 225 */ YY_NOT_ACCEPT,
		/* 226 */ YY_NOT_ACCEPT,
		/* 227 */ YY_NOT_ACCEPT,
		/* 228 */ YY_NOT_ACCEPT,
		/* 229 */ YY_NOT_ACCEPT,
		/* 230 */ YY_NO_ANCHOR,
		/* 231 */ YY_NOT_ACCEPT,
		/* 232 */ YY_NOT_ACCEPT,
		/* 233 */ YY_NOT_ACCEPT,
		/* 234 */ YY_NOT_ACCEPT,
		/* 235 */ YY_NOT_ACCEPT,
		/* 236 */ YY_NOT_ACCEPT,
		/* 237 */ YY_NO_ANCHOR,
		/* 238 */ YY_NOT_ACCEPT,
		/* 239 */ YY_NOT_ACCEPT,
		/* 240 */ YY_NO_ANCHOR,
		/* 241 */ YY_NO_ANCHOR,
		/* 242 */ YY_NO_ANCHOR,
		/* 243 */ YY_NO_ANCHOR,
		/* 244 */ YY_NO_ANCHOR,
		/* 245 */ YY_NO_ANCHOR,
		/* 246 */ YY_NOT_ACCEPT,
		/* 247 */ YY_NOT_ACCEPT,
		/* 248 */ YY_NOT_ACCEPT,
		/* 249 */ YY_NOT_ACCEPT,
		/* 250 */ YY_NOT_ACCEPT,
		/* 251 */ YY_NOT_ACCEPT,
		/* 252 */ YY_NOT_ACCEPT,
		/* 253 */ YY_NOT_ACCEPT,
		/* 254 */ YY_NOT_ACCEPT,
		/* 255 */ YY_NOT_ACCEPT,
		/* 256 */ YY_NOT_ACCEPT,
		/* 257 */ YY_NOT_ACCEPT,
		/* 258 */ YY_NOT_ACCEPT,
		/* 259 */ YY_NO_ANCHOR,
		/* 260 */ YY_NOT_ACCEPT,
		/* 261 */ YY_NO_ANCHOR,
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
		/* 276 */ YY_NOT_ACCEPT,
		/* 277 */ YY_NOT_ACCEPT,
		/* 278 */ YY_NOT_ACCEPT,
		/* 279 */ YY_NO_ANCHOR,
		/* 280 */ YY_NO_ANCHOR,
		/* 281 */ YY_NO_ANCHOR,
		/* 282 */ YY_NO_ANCHOR,
		/* 283 */ YY_NO_ANCHOR,
		/* 284 */ YY_NOT_ACCEPT,
		/* 285 */ YY_NOT_ACCEPT,
		/* 286 */ YY_NOT_ACCEPT,
		/* 287 */ YY_NOT_ACCEPT,
		/* 288 */ YY_NO_ANCHOR,
		/* 289 */ YY_NO_ANCHOR,
		/* 290 */ YY_NO_ANCHOR,
		/* 291 */ YY_NO_ANCHOR,
		/* 292 */ YY_NO_ANCHOR,
		/* 293 */ YY_NO_ANCHOR,
		/* 294 */ YY_NO_ANCHOR,
		/* 295 */ YY_NO_ANCHOR,
		/* 296 */ YY_NO_ANCHOR,
		/* 297 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"50:8,5:2,6,50:2,9,50:18,5,7,46,50:10,8,49,4,48:10,49,50,2,1,3,45,50,37,47,3" +
"2,41,29,36,44,39,33,47:2,38,30,34,40,31,47,35,26,28,42,43,47:2,27,47,50:4,4" +
"8,50,17,47:2,23,11,47:3,14,47,25,24,18,16,15,21,47,12,13,19,47,10,47,22,20," +
"47,50:5,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,298,
"0,1:2,2,1,3,4,5,1:2,6,1:2,7,1:2,8,1,9,1:29,10,11,1,12,13,1,14,11,15,16,17,1" +
"8,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,4" +
"3,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,6" +
"8,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,9" +
"3,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113" +
",114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,13" +
"2,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,1" +
"51,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169," +
"170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188" +
",189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,20" +
"7,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,2" +
"26,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244," +
"245,246,247,7,248,249,250,251,252,253,7,254")[0];

	private int yy_nxt[][] = unpackFromString(255,51,
"1,2,3,4,5,6:2,53:2,-1,7,295,296,268,297,296,245,296:2,259,296:5,261,283,296" +
":18,58,61,296,53:3,-1:55,52,-1:2,57,-1:18,60,-1,63,66,68,69,70,71,-1,72,-1," +
"73,-1:2,74,-1:3,176,75,-1:8,8,-1:52,6:2,-1:54,296,288,296:33,-1:2,296,289,-" +
"1:10,10,-1,10:35,-1,11,10:3,-1:11,296:35,-1:2,296,289,-1:43,226,-1:17,135,-" +
"1,296:35,-1:2,296,289,-1:2,1,55:5,49,55,64,55:42,-1,55:7,168,55:42,1,56,-1," +
"56:48,-1:26,76,-1,77,173,269,-1:2,246,-1:3,284,-1:6,275,-1:16,296,13,296:33" +
",-1:2,296,289,-1:3,56,-1,56:48,-1:8,78,-1:32,79,-1:12,9,-1:57,296:13,13,296" +
":21,-1:2,296,289,-1:29,80,174,-1:11,214,-1:18,10,-1,10:35,-1:2,10:3,-1:11,2" +
"96:3,13,296:31,-1:2,296,289,-1:37,213,-1,177,-1:14,55:7,169,55:42,-1:10,296" +
":20,18,296:14,-1:2,296,289,-1:45,175,-1:17,296:6,13,296:28,-1:2,296,289,-1:" +
"31,81,-1:7,82,-1:50,215,-1:51,83,-1:46,84,-1:45,178,-1:53,179,-1:2,85,-1:57" +
",217,-1:30,86,-1:55,216,218,-1:57,232,-1:23,12,-1:82,180,-1:36,231,-1:52,90" +
",-1:54,187,-1:55,92,-1:39,93,-1,184,-1:66,190,-1:24,97,-1:61,233,-1:56,100," +
"-1:49,188,-1:55,102,-1:40,103,-1:7,104,-1:39,105,-1:52,106,-1:64,108,-1:41," +
"193,-1:61,224,-1:30,14,-1:55,287,-1:50,15,-1:53,114,-1:46,16,-1:62,116,-1:4" +
"4,117,-1:46,118,-1:46,17,-1:61,119,-1:48,120,-1:53,121,-1:53,122,-1:35,278," +
"-1:52,125,-1:55,227,-1:44,127,-1:52,19,-1:49,20,-1:63,21,-1:37,22,-1:51,202" +
",-1:55,131,-1:52,132,-1:42,23,-1:25,24,-1,122:2,-1:74,201,-1:23,25,-1,124:2" +
",-1:85,136,-1:12,26,-1,126:2,-1:75,139,-1:27,253,-1:70,140,-1:51,27,-1:53,1" +
"42,-1:55,143,-1:16,28,-1,133:2,-1:78,144,-1:42,146,-1:53,148,-1:24,29,-1,13" +
"7:2,-1:47,30,-1,138:2,-1:73,31,-1:54,151,-1:53,32,-1:43,33,-1:53,153,-1:46," +
"154,-1:66,34,-1:37,209,-1:45,155,-1:60,156,-1:17,35,-1,149:2,-1:73,157,-1:6" +
"1,160,-1:45,36,-1:44,37,-1:24,38,-1,154:2,-1:75,229,-1:22,39,-1,156:2,-1:47" +
",40,-1,157:2,-1:73,163,-1:24,41,-1,159:2,-1:78,42,-1:48,43,-1:52,166,-1:19," +
"44,-1,163:2,-1:76,45,-1:50,167,-1:21,46,-1,166:2,-1:47,47,-1,167:2,-1:45,55" +
":7,170,55:42,-1,55:2,50,55:47,-1,55:2,-1,55:47,1,-1:60,296:8,54,296:26,-1:2" +
",296,289,-1:45,87,-1:44,238,-1:42,186,-1:63,181,-1:43,183,-1:41,94,-1:52,95" +
",-1:54,222,-1:55,185,-1:39,286,-1,98,-1:66,223,-1:35,107,-1:56,109,-1:49,10" +
"1,-1:55,191,-1:37,197,-1:52,234,-1:64,249,-1:41,194,-1:46,195,-1:61,199,-1:" +
"44,130,-1:46,128,-1:61,126,-1:42,129,-1:46,134,-1:55,133,-1:57,137,-1:17,14" +
"7,-1:70,228,-1:54,150,-1:52,145,-1:45,149,-1:53,158,-1:46,159,-1:53,211,-1:" +
"48,161,-1:61,162,-1:39,164,-1:31,296:11,54,296:23,-1:2,296,289,-1:39,89,-1:" +
"55,88,-1:43,91,-1:41,189,-1:52,96,-1:59,256,-1:48,196,-1:49,110,-1:55,112,-" +
"1:39,113,-1:51,115,-1:61,225,-1:43,204,-1:46,141,-1:55,205,-1:45,152,-1:50," +
"165,-1:31,296:6,59,296:28,-1:2,296,289,-1:30,192,-1:59,220,-1:47,111,-1:45," +
"123,-1:61,200,-1:44,138,-1:26,296:9,54,296:25,-1:2,296,289,-1:30,99,-1:51,1" +
"24,-1:31,296:6,62,296:28,-1:2,296,289,-1:12,296:19,65,296:15,-1:2,296,289,-" +
"1:12,296:5,67,296:29,-1:2,296,289,-1:12,296:13,62,296:21,-1:2,296,289,-1:12" +
",296:9,62,296:25,-1:2,296,289,-1:12,296:7,172,296:27,-1:2,296,289,-1:36,182" +
",-1:48,221,-1:57,235,-1:41,198,-1:54,203,-1:53,206,-1:47,207,-1:42,208,-1:5" +
"7,210,-1:54,219,-1:41,239,-1:62,236,-1:39,252,-1:31,296:2,263,296:7,212,296" +
":24,-1:2,296,289,-1:32,258,-1:30,296:4,230,296:30,-1:2,296,289,-1:12,296:7," +
"237,296:27,-1:2,296,289,-1:12,296:7,240,296:27,-1:2,296,289,-1:12,296:18,24" +
"1,296:16,-1:2,296,289,-1:12,296:4,242,296:30,-1:2,296,289,-1:12,296:6,243,2" +
"96:28,-1:2,296,289,-1:12,296:6,244,296:28,-1:2,296,289,-1:12,296:9,262,296:" +
"25,-1:2,296,289,-1:31,270,-1:7,247,-1:41,248,-1:55,257,-1:54,250,-1:49,251," +
"-1:42,254,-1:64,255,-1:36,271,-1:64,260,-1:41,274,-1:27,296:16,264,296:18,-" +
"1:2,296,289,-1:12,296:3,265,296:31,-1:2,296,289,-1:12,296,266,296:33,-1:2,2" +
"96,289,-1:12,296,267,296:33,-1:2,296,289,-1:12,296:17,279,296:17,-1:2,296,2" +
"89,-1:34,276,-1:2,285,-1:59,277,-1:34,272,-1:57,273,-1:25,296:2,280,296:32," +
"-1:2,296,289,-1:12,296:9,281,296:25,-1:2,296,289,-1:12,296:11,292,296:23,-1" +
":2,296,289,-1:12,296:14,293,296:20,-1:2,296,289,-1:12,296,294,296:33,-1:2,2" +
"96,289,-1:12,296:8,282,296:26,-1:2,296,289,-1:12,296:12,290,296:22,-1:2,296" +
",289,-1:12,296:8,291,296:26,-1:2,296,289,-1:2");

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
						{ return (new Yytoken(sym.tEQUALS,yytext(),yyline,yychar,yychar+1)); }
					case -3:
						break;
					case 3:
						{ return (new Yytoken(sym.tMARK,yytext(),yyline,yychar,yychar+1)); }
					case -4:
						break;
					case 4:
						{ return (new Yytoken(sym.tENDTAG,yytext(),yyline,yychar,yychar+1)); }
					case -5:
						break;
					case 5:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -6:
						break;
					case 6:
						{ }
					case -7:
						break;
					case 7:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -8:
						break;
					case 8:
						{ return (new Yytoken(sym.tENDELEMENT,yytext(),yyline,yychar,yychar+1)); }
					case -9:
						break;
					case 9:
						{return (new Yytoken(sym.tEND_XMLHEAD, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -10:
						break;
					case 10:
						{
	String str =  yytext().substring(1,yytext().length());
	Utility.error(Utility.E_UNCLOSEDSTR);
	Utility.my_assert(str.length() == yytext().length() - 1);
	return (new Yytoken(sym.tATTRIBUTE_VALUE,str,yyline,yychar,yychar + str.length()));
}
					case -11:
						break;
					case 11:
						{
	String str =  yytext().substring(1,yytext().length() - 1);
	Utility.my_assert(str.length() == yytext().length() - 2);
	return (new Yytoken(sym.tATTRIBUTE_VALUE,str,yyline,yychar,yychar + str.length()));
}
					case -12:
						break;
					case 12:
						{ yybegin(COMMENT); comment_count = comment_count + 1; }
					case -13:
						break;
					case 13:
						{return (new Yytoken(sym.tATTRIBUTE_NAME, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -14:
						break;
					case 14:
						{return (new Yytoken(sym.tXMLHEAD, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -15:
						break;
					case 15:
						{return (new Yytoken(sym.tSTATE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -16:
						break;
					case 16:
						{return (new Yytoken(sym.tEVENT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -17:
						break;
					case 17:
						{return (new Yytoken(sym.tCLASS, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -18:
						break;
					case 18:
						{return (new Yytoken(sym.tSYSTEM, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -19:
						break;
					case 19:
						{return (new Yytoken(sym.tSOURCE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -20:
						break;
					case 20:
						{return (new Yytoken(sym.tTARGET, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -21:
						break;
					case 21:
						{return (new Yytoken(sym.tMETHOD, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -22:
						break;
					case 22:
						{return (new Yytoken(sym.tPARENT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -23:
						break;
					case 23:
						{return (new Yytoken(sym.tRESULT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -24:
						break;
					case 24:
						{
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tGUARD, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -25:
						break;
					case 25:
						{return (new Yytoken(sym.tEND_STATE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -26:
						break;
					case 26:
						{
	return (new Yytoken(sym.tEND_GUARD, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -27:
						break;
					case 27:
						{return (new Yytoken(sym.tMACHINE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -28:
						break;
					case 28:
						{
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tACTION, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -29:
						break;
					case 29:
						{return (new Yytoken(sym.tEND_METHOD, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -30:
						break;
					case 30:
						{
	return (new Yytoken(sym.tEND_ACTION, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -31:
						break;
					case 31:
						{return (new Yytoken(sym.tDOCTYPE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -32:
						break;
					case 32:
						{return (new Yytoken(sym.tEVENTDEF, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -33:
						break;
					case 33:
						{return (new Yytoken(sym.tINSTANCE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -34:
						break;
					case 34:
						{return (new Yytoken(sym.tOUTGOING, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -35:
						break;
					case 35:
						{return (new Yytoken(sym.tEND_MACHINE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -36:
						break;
					case 36:
						{return (new Yytoken(sym.tPARAMETER, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -37:
						break;
					case 37:
						{return (new Yytoken(sym.tINTERFACE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -38:
						break;
					case 38:
						{
				yybegin(GETPCDATA);
				return (new Yytoken(sym.tARGUMENT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -39:
						break;
					case 39:
						{return (new Yytoken(sym.tEND_EVENTDEF, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -40:
						break;
					case 40:
						{return (new Yytoken(sym.tEND_INSTANCE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -41:
						break;
					case 41:
						{
	return (new Yytoken(sym.tEND_ARGUMENT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -42:
						break;
					case 42:
						{return (new Yytoken(sym.tTRANSITION, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -43:
						break;
					case 43:
						{return (new Yytoken(sym.tROOT, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -44:
						break;
					case 44:
						{return (new Yytoken(sym.tEND_INTERFACE, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -45:
						break;
					case 45:
						{return (new Yytoken(sym.tSYSTEMSPEC, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -46:
						break;
					case 46:
						{return (new Yytoken(sym.tEND_TRANSITION, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -47:
						break;
					case 47:
						{return (new Yytoken(sym.tEND_SYSTEMSPEC, yytext(), yyline, yychar, yychar+yytext().length()));}
					case -48:
						break;
					case 48:
						{ }
					case -49:
						break;
					case 49:
						{ }
					case -50:
						break;
					case 50:
						{ 
	comment_count = comment_count - 1; 
	Utility.my_assert(comment_count >= 0);
	if (comment_count == 0) {
    		yybegin(YYINITIAL);
	}
}
					case -51:
						break;
					case 51:
						{
	yybegin(YYINITIAL);
	return (new Yytoken(sym.tPCDATA, yytext(), yyline, yychar, yychar + yytext().length()));
}
					case -52:
						break;
					case 53:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -53:
						break;
					case 54:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -54:
						break;
					case 55:
						{ }
					case -55:
						break;
					case 56:
						{
	yybegin(YYINITIAL);
	return (new Yytoken(sym.tPCDATA, yytext(), yyline, yychar, yychar + yytext().length()));
}
					case -56:
						break;
					case 58:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -57:
						break;
					case 59:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -58:
						break;
					case 61:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -59:
						break;
					case 62:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -60:
						break;
					case 64:
						{
        System.out.println("Illegal character: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -61:
						break;
					case 65:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -62:
						break;
					case 67:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -63:
						break;
					case 172:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -64:
						break;
					case 212:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -65:
						break;
					case 230:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -66:
						break;
					case 237:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -67:
						break;
					case 240:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -68:
						break;
					case 241:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -69:
						break;
					case 242:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -70:
						break;
					case 243:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -71:
						break;
					case 244:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -72:
						break;
					case 245:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -73:
						break;
					case 259:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -74:
						break;
					case 261:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -75:
						break;
					case 262:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -76:
						break;
					case 263:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -77:
						break;
					case 264:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -78:
						break;
					case 265:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -79:
						break;
					case 266:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -80:
						break;
					case 267:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -81:
						break;
					case 268:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -82:
						break;
					case 279:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -83:
						break;
					case 280:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -84:
						break;
					case 281:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -85:
						break;
					case 282:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -86:
						break;
					case 283:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -87:
						break;
					case 288:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -88:
						break;
					case 289:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -89:
						break;
					case 290:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -90:
						break;
					case 291:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -91:
						break;
					case 292:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -92:
						break;
					case 293:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -93:
						break;
					case 294:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -94:
						break;
					case 295:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -95:
						break;
					case 296:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -96:
						break;
					case 297:
						{
	    System.out.println("Illegal string: <" + yytext() + ">");
		Utility.error(Utility.E_UNMATCHED_STRING);
        return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -97:
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
