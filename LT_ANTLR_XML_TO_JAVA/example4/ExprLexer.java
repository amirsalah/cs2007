// $ANTLR 3.0.1 Expr.g 2008-04-30 01:08:08

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ExprLexer extends Lexer {
    public static final int WS=7;
    public static final int NEWLINE=4;
    public static final int T10=10;
    public static final int T11=11;
    public static final int INT=6;
    public static final int T12=12;
    public static final int T13=13;
    public static final int T8=8;
    public static final int T9=9;
    public static final int ID=5;
    public static final int Tokens=14;
    public static final int EOF=-1;
    public ExprLexer() {;} 
    public ExprLexer(CharStream input) {
        super(input);
    }
    public String getGrammarFileName() { return "Expr.g"; }

    // $ANTLR start T8
    public final void mT8() throws RecognitionException {
        try {
            int _type = T8;
            // Expr.g:3:4: ( '=' )
            // Expr.g:3:6: '='
            {
            match('='); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T8

    // $ANTLR start T9
    public final void mT9() throws RecognitionException {
        try {
            int _type = T9;
            // Expr.g:4:4: ( '+' )
            // Expr.g:4:6: '+'
            {
            match('+'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T9

    // $ANTLR start T10
    public final void mT10() throws RecognitionException {
        try {
            int _type = T10;
            // Expr.g:5:5: ( '-' )
            // Expr.g:5:7: '-'
            {
            match('-'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T10

    // $ANTLR start T11
    public final void mT11() throws RecognitionException {
        try {
            int _type = T11;
            // Expr.g:6:5: ( '*' )
            // Expr.g:6:7: '*'
            {
            match('*'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T11

    // $ANTLR start T12
    public final void mT12() throws RecognitionException {
        try {
            int _type = T12;
            // Expr.g:7:5: ( '(' )
            // Expr.g:7:7: '('
            {
            match('('); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T12

    // $ANTLR start T13
    public final void mT13() throws RecognitionException {
        try {
            int _type = T13;
            // Expr.g:8:5: ( ')' )
            // Expr.g:8:7: ')'
            {
            match(')'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end T13

    // $ANTLR start ID
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            // Expr.g:38:5: ( ( 'a' .. 'z' | 'A' .. 'Z' )+ )
            // Expr.g:38:9: ( 'a' .. 'z' | 'A' .. 'Z' )+
            {
            // Expr.g:38:9: ( 'a' .. 'z' | 'A' .. 'Z' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='A' && LA1_0<='Z')||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Expr.g:
            	    {
            	    if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end ID

    // $ANTLR start INT
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            // Expr.g:39:5: ( ( '0' .. '9' )+ )
            // Expr.g:39:9: ( '0' .. '9' )+
            {
            // Expr.g:39:9: ( '0' .. '9' )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // Expr.g:39:9: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);


            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end INT

    // $ANTLR start NEWLINE
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            // Expr.g:40:8: ( ( '\\r' )? '\\n' )
            // Expr.g:40:9: ( '\\r' )? '\\n'
            {
            // Expr.g:40:9: ( '\\r' )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='\r') ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // Expr.g:40:9: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }

            match('\n'); 

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end NEWLINE

    // $ANTLR start WS
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            // Expr.g:41:5: ( ( ' ' | '\\t' | '\\n' | '\\r' )+ )
            // Expr.g:41:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            {
            // Expr.g:41:9: ( ' ' | '\\t' | '\\n' | '\\r' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>='\t' && LA4_0<='\n')||LA4_0=='\r'||LA4_0==' ') ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // Expr.g:
            	    {
            	    if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recover(mse);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);

            skip();

            }

            this.type = _type;
        }
        finally {
        }
    }
    // $ANTLR end WS

    public void mTokens() throws RecognitionException {
        // Expr.g:1:8: ( T8 | T9 | T10 | T11 | T12 | T13 | ID | INT | NEWLINE | WS )
        int alt5=10;
        switch ( input.LA(1) ) {
        case '=':
            {
            alt5=1;
            }
            break;
        case '+':
            {
            alt5=2;
            }
            break;
        case '-':
            {
            alt5=3;
            }
            break;
        case '*':
            {
            alt5=4;
            }
            break;
        case '(':
            {
            alt5=5;
            }
            break;
        case ')':
            {
            alt5=6;
            }
            break;
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt5=7;
            }
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
            alt5=8;
            }
            break;
        case '\r':
            {
            int LA5_9 = input.LA(2);

            if ( (LA5_9=='\n') ) {
                int LA5_10 = input.LA(3);

                if ( ((LA5_10>='\t' && LA5_10<='\n')||LA5_10=='\r'||LA5_10==' ') ) {
                    alt5=10;
                }
                else {
                    alt5=9;}
            }
            else {
                alt5=10;}
            }
            break;
        case '\n':
            {
            int LA5_10 = input.LA(2);

            if ( ((LA5_10>='\t' && LA5_10<='\n')||LA5_10=='\r'||LA5_10==' ') ) {
                alt5=10;
            }
            else {
                alt5=9;}
            }
            break;
        case '\t':
        case ' ':
            {
            alt5=10;
            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("1:1: Tokens : ( T8 | T9 | T10 | T11 | T12 | T13 | ID | INT | NEWLINE | WS );", 5, 0, input);

            throw nvae;
        }

        switch (alt5) {
            case 1 :
                // Expr.g:1:10: T8
                {
                mT8(); 

                }
                break;
            case 2 :
                // Expr.g:1:13: T9
                {
                mT9(); 

                }
                break;
            case 3 :
                // Expr.g:1:16: T10
                {
                mT10(); 

                }
                break;
            case 4 :
                // Expr.g:1:20: T11
                {
                mT11(); 

                }
                break;
            case 5 :
                // Expr.g:1:24: T12
                {
                mT12(); 

                }
                break;
            case 6 :
                // Expr.g:1:28: T13
                {
                mT13(); 

                }
                break;
            case 7 :
                // Expr.g:1:32: ID
                {
                mID(); 

                }
                break;
            case 8 :
                // Expr.g:1:35: INT
                {
                mINT(); 

                }
                break;
            case 9 :
                // Expr.g:1:39: NEWLINE
                {
                mNEWLINE(); 

                }
                break;
            case 10 :
                // Expr.g:1:47: WS
                {
                mWS(); 

                }
                break;

        }

    }


 

}