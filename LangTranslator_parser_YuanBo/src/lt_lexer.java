import java.io.FileReader;
import java.lang.System;

class lt_lexer {
    private Yylex yy;

    public void initLexer(String sourceFileName) throws java.io.IOException {
        yy = new Yylex(new FileReader(sourceFileName));
    }
    public Yytoken nextToken() throws java.io.IOException {
        return yy.yylex();
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
  	  return "<" + m_charBegin + ", " + m_charEnd + ", " + m_text + ", " + sym.tName(m_index) + ">";
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
	private final int PCDATA_STATE = 2;
	private final int YYINITIAL = 0;
	private final int COMMENT = 1;
	private final int yy_state_dtrans[] = {
		0,
		48,
		50
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
		/* 54 */ YY_NOT_ACCEPT,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NOT_ACCEPT,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NOT_ACCEPT,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NOT_ACCEPT,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NOT_ACCEPT,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NO_ANCHOR,
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
		/* 224 */ YY_NOT_ACCEPT,
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
		/* 240 */ YY_NOT_ACCEPT,
		/* 241 */ YY_NOT_ACCEPT,
		/* 242 */ YY_NOT_ACCEPT,
		/* 243 */ YY_NOT_ACCEPT,
		/* 244 */ YY_NOT_ACCEPT,
		/* 245 */ YY_NOT_ACCEPT,
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
		/* 259 */ YY_NOT_ACCEPT,
		/* 260 */ YY_NOT_ACCEPT,
		/* 261 */ YY_NOT_ACCEPT,
		/* 262 */ YY_NOT_ACCEPT,
		/* 263 */ YY_NOT_ACCEPT,
		/* 264 */ YY_NOT_ACCEPT,
		/* 265 */ YY_NOT_ACCEPT,
		/* 266 */ YY_NOT_ACCEPT,
		/* 267 */ YY_NOT_ACCEPT,
		/* 268 */ YY_NOT_ACCEPT,
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
		/* 279 */ YY_NOT_ACCEPT,
		/* 280 */ YY_NOT_ACCEPT,
		/* 281 */ YY_NOT_ACCEPT,
		/* 282 */ YY_NOT_ACCEPT,
		/* 283 */ YY_NOT_ACCEPT,
		/* 284 */ YY_NOT_ACCEPT,
		/* 285 */ YY_NOT_ACCEPT,
		/* 286 */ YY_NOT_ACCEPT,
		/* 287 */ YY_NOT_ACCEPT,
		/* 288 */ YY_NOT_ACCEPT,
		/* 289 */ YY_NOT_ACCEPT,
		/* 290 */ YY_NOT_ACCEPT,
		/* 291 */ YY_NOT_ACCEPT,
		/* 292 */ YY_NOT_ACCEPT,
		/* 293 */ YY_NOT_ACCEPT,
		/* 294 */ YY_NOT_ACCEPT,
		/* 295 */ YY_NOT_ACCEPT,
		/* 296 */ YY_NOT_ACCEPT,
		/* 297 */ YY_NOT_ACCEPT,
		/* 298 */ YY_NOT_ACCEPT,
		/* 299 */ YY_NOT_ACCEPT,
		/* 300 */ YY_NOT_ACCEPT,
		/* 301 */ YY_NOT_ACCEPT,
		/* 302 */ YY_NOT_ACCEPT,
		/* 303 */ YY_NOT_ACCEPT,
		/* 304 */ YY_NOT_ACCEPT,
		/* 305 */ YY_NOT_ACCEPT,
		/* 306 */ YY_NOT_ACCEPT,
		/* 307 */ YY_NOT_ACCEPT,
		/* 308 */ YY_NOT_ACCEPT,
		/* 309 */ YY_NOT_ACCEPT,
		/* 310 */ YY_NOT_ACCEPT,
		/* 311 */ YY_NOT_ACCEPT,
		/* 312 */ YY_NOT_ACCEPT,
		/* 313 */ YY_NOT_ACCEPT
	};
	private int yy_cmap[] = unpackFromString(1,130,
"46:8,5:3,46:2,8,46:18,5,6,45,46:10,7,46,4,46:12,2,1,3,44,46,36,46,31,40,28," +
"35,43,38,32,46:2,37,29,33,39,30,46,34,25,27,41,42,46:2,26,46:7,16,46:2,22,1" +
"0,46:3,13,46,24,23,17,15,14,20,46,11,12,18,46,9,46,21,19,46:6,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,314,
"0,1:2,2,1,3,4,1:11,5,1:29,6,1,7,1:3,8,1,9,10,11,12,13,14,15,16,17,18,19,20," +
"21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45," +
"46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70," +
"71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95," +
"96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115" +
",116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,13" +
"4,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,1" +
"53,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171," +
"172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190" +
",191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,20" +
"9,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,2" +
"28,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246," +
"247,248,249,250,251,252,253,254,255,256,257,258,259,260,261,262,263,264,265" +
",266")[0];

	private int yy_nxt[][] = unpackFromString(267,47,
"1,2,3,4,5,6,55:2,-1,60,62,55,64,66,55,68,55:2,70,55:5,72,55:19,74,76,55,-1:" +
"51,54,-1,59,-1:18,61,-1,63,65,67,69,71,73,-1,75,-1,77,-1:2,79,-1:3,236,80,-" +
"1:5,7,-1:48,6,-1:81,283,-1:6,1,57:4,56,57,78,57:39,1,58,213,58:44,-1:25,88," +
"-1,89,233,304,-1:2,295,-1:15,57:4,56,57,210,57:39,-1,57:6,210,57:39,-1,58,-" +
"1,58:44,-1:7,90,-1:32,91,-1:16,81,-1:62,92,234,-1:11,272,-1:28,82,-1:59,271" +
",-1,241,-1:28,83,-1:70,235,-1:21,84,-1:57,93,-1:7,94,-1:26,85,-1:66,273,-1:" +
"21,237,-1:7,238,-1:64,95,-1:22,86,-1:66,96,-1:16,8,-1:71,242,-1:19,87:44,-1" +
",87,-1:31,243,-1:2,97,-1:13,57:6,211,57:39,-1:41,275,-1:26,239,-1:36,98,-1:" +
"53,99,-1:44,100,-1:50,101,-1:43,102,-1:44,104,-1:32,87:44,9,87,-1:26,274,27" +
"6,-1:53,288,-1:19,10,-1:78,244,-1:32,287,-1:48,108,-1:50,251,-1:51,110,-1:3" +
"5,111,-1,248,-1:62,254,-1:15,116,-1:44,117,-1:54,118,-1:51,119,-1:33,11,-1:" +
"46,12,-1:58,13,-1:52,289,-1:52,123,-1:45,252,-1:51,125,-1:36,126,-1:7,127,-" +
"1:35,128,-1:48,129,-1:60,131,-1:37,257,-1:57,281,-1:26,14,-1:36,133,-1:48,1" +
"34,-1:41,15,-1:46,135,-1:48,16,-1:62,312,-1:46,17,-1:49,140,-1:42,18,-1:58," +
"142,-1:40,143,-1:42,144,-1:42,19,-1:57,145,-1:44,146,-1:49,147,-1:49,148,-1" +
":20,149,-1:54,150,-1:41,151,-1:54,310,-1:48,154,-1:51,284,-1:40,155,-1:48,2" +
"0,-1:45,21,-1:59,22,-1:33,23,-1:47,264,-1:51,159,-1:48,160,-1:38,24,-1:22,2" +
"5,-1,148,-1:56,26,-1:43,27,-1:44,163,-1:65,313,-1:20,28,-1,153,-1:81,164,-1" +
":36,285,-1:23,166,-1:66,167,-1:47,29,-1:49,169,-1:51,170,-1:13,30,-1,161,-1" +
":74,171,-1:28,173,-1:59,174,-1:21,31,-1,165,-1:66,178,-1:53,179,-1:49,32,-1" +
":39,33,-1:49,181,-1:42,182,-1:62,34,-1:21,183,-1:63,184,-1:14,35,-1,175,-1:" +
"69,185,-1:23,177,-1:19,187,-1:51,270,-1:55,188,-1:41,36,-1:40,37,-1:21,38,-" +
"1,182,-1:53,39,-1:37,40,-1,184,-1:44,41,-1,185,-1:69,190,-1:44,191,-1:53,42" +
",-1:46,194,-1:16,43,-1,190,-1:66,195,-1:52,44,-1:46,196,-1:18,45,-1,194,-1:" +
"68,197,-1:22,46,-1,196,-1:69,198,-1:47,199,-1:24,200,-1:64,201,-1:51,202,-1" +
":44,203,-1:49,204,-1:20,204,-1:19,205,-1:47,206,-1:45,207,-1:48,208,-1:47,2" +
"09,-1:47,47,-1:18,57:6,212,57:39,-1,57:2,49,57:43,-1,57:2,-1,57:43,-1:4,214" +
",-1:78,215,-1:6,216,-1:34,217,-1:2,218,-1:53,219,-1:32,220,-1:62,221,-1:39," +
"222,-1:42,223,-1:55,224,-1:39,225,-1:51,226,-1:36,227,-1:57,228,-1:39,229,-" +
"1:41,230,-1:21,51,-1:46,52,-1:76,231,-1:40,232,-1:22,53,-1:85,105,-1:40,293" +
",-1:38,250,-1:59,245,-1:21,240,-1:50,103,-1:43,115,-1:44,120,-1:65,247,-1:3" +
"7,112,-1:48,113,-1:50,279,-1:51,249,-1:35,311,-1,121,-1:62,280,-1:31,130,-1" +
":52,132,-1:45,124,-1:51,255,-1:33,260,-1:48,290,-1:60,298,-1:37,258,-1:42,2" +
"59,-1:57,262,-1:40,158,-1:42,156,-1:49,157,-1:42,162,-1:51,161,-1:53,165,-1" +
":33,292,-1:50,176,-1:48,172,-1:41,175,-1:49,186,-1:54,189,-1:35,192,-1:54,1" +
"07,-1:51,106,-1:39,109,-1:37,253,-1:48,114,-1:55,303,-1:43,136,-1:51,138,-1" +
":35,139,-1:47,141,-1:57,282,-1:39,266,-1:42,168,-1:51,267,-1:41,177,-1:46,1" +
"93,-1:45,256,-1:55,277,-1:43,137,-1:41,152,-1:57,263,-1:35,180,-1:45,122,-1" +
":47,153,-1:51,246,-1:44,278,-1:53,291,-1:37,261,-1:50,265,-1:49,268,-1:42,2" +
"69,-1:44,286,-1:43,294,-1:47,305,-1:7,296,-1:37,297,-1:55,299,-1:45,300,-1:" +
"38,301,-1:44,302,-1:53,308,-1:41,306,-1:53,307,-1:19,309,-1:39");

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
						{
	return (new Yytoken(sym.SYM_EQUALS,yytext(),yyline,yychar,yychar+1));
}
					case -3:
						break;
					case 3:
						{
	return (new Yytoken(sym. LEFTANLGLE,yytext(),yyline,yychar,yychar+1));
}
					case -4:
						break;
					case 4:
						{
	return (new Yytoken(sym.RIGHTANGLE,yytext(),yyline,yychar,yychar+1));
}
					case -5:
						break;
					case 5:
						{
    System.out.println("Error: " + yytext() + "");
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
	return (new Yytoken(sym.ELEMENT_RIGHT,yytext(),yyline,yychar,yychar+1));
}
					case -8:
						break;
					case 8:
						{
	return (new Yytoken(sym.RIGHTXML, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -9:
						break;
					case 9:
						{
	return (new Yytoken(sym.ATT_VALUE, yytext(), yyline, yychar, yychar + yytext().length()));
}
					case -10:
						break;
					case 10:
						{ yybegin(COMMENT); }
					case -11:
						break;
					case 11:
						{
	return (new Yytoken(sym.SYM_NAME, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -12:
						break;
					case 12:
						{
	return (new Yytoken(sym.SYM_TYPE, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -13:
						break;
					case 13:
						{
	return (new Yytoken(sym.SYM_KIND, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -14:
						break;
					case 14:
						{
	return (new Yytoken(sym.LEFTXML, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -15:
						break;
					case 15:
						{
	return (new Yytoken(sym.SYM_ATTSTATE, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -16:
						break;
					case 16:
						{
	return (new Yytoken(sym.SYM_TRANS, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -17:
						break;
					case 17:
						{
	return (new Yytoken(sym.LEFT_STATE, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -18:
						break;
					case 18:
						{
	return (new Yytoken(sym.SYM_EVENT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -19:
						break;
					case 19:
						{
	return (new Yytoken(sym.SYM_CLASS, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -20:
						break;
					case 20:
						{
	return (new Yytoken(sym.SYM_SOURCE, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -21:
						break;
					case 21:
						{
	return (new Yytoken(sym.SYM_TARGET, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -22:
						break;
					case 22:
						{
	return (new Yytoken(sym.LEFT_METHOD, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -23:
						break;
					case 23:
						{
	return (new Yytoken(sym.SYM_PARENT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -24:
						break;
					case 24:
						{
	return (new Yytoken(sym.SYM_RESULT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -25:
						break;
					case 25:
						{
	yybegin(PCDATA_STATE);
	return (new Yytoken(sym.LEFT_GUARD, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -26:
						break;
					case 26:
						{
	return (new Yytoken(sym.SYM_VERSION, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -27:
						break;
					case 27:
						{
	return (new Yytoken(sym.SYM_EXTENDS, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -28:
						break;
					case 28:
						{
	return (new Yytoken(sym.STATE_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -29:
						break;
					case 29:
						{
	return (new Yytoken(sym.LEFT_MACHINE, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -30:
						break;
					case 30:
						{
	yybegin(PCDATA_STATE);
	return (new Yytoken(sym.LEFT_ACTION, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -31:
						break;
					case 31:
						{
	return (new Yytoken(sym.METHOD_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -32:
						break;
					case 32:
						{
	return (new Yytoken(sym.LEFT_EVENTDEF, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -33:
						break;
					case 33:
						{
	return (new Yytoken(sym.LEFT_INSTANCE, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -34:
						break;
					case 34:
						{
	return (new Yytoken(sym.SYM_OUTGOING, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -35:
						break;
					case 35:
						{
	return (new Yytoken(sym.MACHINE_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -36:
						break;
					case 36:
						{
	return (new Yytoken(sym.SYM_PARAMETER, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -37:
						break;
					case 37:
						{
	return (new Yytoken(sym.LEFT_INTERFACE, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -38:
						break;
					case 38:
						{
	yybegin(PCDATA_STATE);
	return (new Yytoken(sym.LEFT_ARG, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -39:
						break;
					case 39:
						{
	return (new Yytoken(sym.SYM_IMPLEMENTS, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -40:
						break;
					case 40:
						{
	return (new Yytoken(sym.EVENTDEF_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -41:
						break;
					case 41:
						{
	return (new Yytoken(sym.INSTANCE_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -42:
						break;
					case 42:
						{
	return (new Yytoken(sym.LEFT_TRANSITION, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -43:
						break;
					case 43:
						{
	return (new Yytoken(sym.INTERFACE_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -44:
						break;
					case 44:
						{
	return (new Yytoken(sym.LEFT_SYSTEMSPEC, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -45:
						break;
					case 45:
						{
	return (new Yytoken(sym.TRANSITION_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -46:
						break;
					case 46:
						{
	return (new Yytoken(sym.SYSTEMSPEC_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -47:
						break;
					case 47:
						{
	return (new Yytoken(sym.DOCTYPE, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -48:
						break;
					case 48:
						{ }
					case -49:
						break;
					case 49:
						{ yybegin(YYINITIAL); }
					case -50:
						break;
					case 50:
						{
	return (new Yytoken(sym.SYM_PCDATA, yytext(), yyline, yychar, yychar + yytext().length()));
}
					case -51:
						break;
					case 51:
						{
	yybegin(YYINITIAL);
	return (new Yytoken(sym.GUARD_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -52:
						break;
					case 52:
						{
	yybegin(YYINITIAL);
	return (new Yytoken(sym.ACTION_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -53:
						break;
					case 53:
						{
	yybegin(YYINITIAL);
	return (new Yytoken(sym.ARGUMENT_RIGHT, yytext(), yyline, yychar, yychar+yytext().length()));
}
					case -54:
						break;
					case 55:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -55:
						break;
					case 56:
						{ }
					case -56:
						break;
					case 57:
						{ }
					case -57:
						break;
					case 58:
						{
	return (new Yytoken(sym.SYM_PCDATA, yytext(), yyline, yychar, yychar + yytext().length()));
}
					case -58:
						break;
					case 60:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -59:
						break;
					case 62:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -60:
						break;
					case 64:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -61:
						break;
					case 66:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -62:
						break;
					case 68:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -63:
						break;
					case 70:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -64:
						break;
					case 72:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -65:
						break;
					case 74:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -66:
						break;
					case 76:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -67:
						break;
					case 78:
						{
    System.out.println("Error: " + yytext() + "");
    return (new Yytoken(sym.ERROR,yytext(),yyline,yychar,yychar + yytext().length()));
}
					case -68:
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
