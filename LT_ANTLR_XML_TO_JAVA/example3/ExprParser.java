// $ANTLR 3.0.1 Expr.g 2008-04-29 22:29:22

import java.util.HashMap;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class ExprParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "NEWLINE", "ID", "INT", "WS", "'='", "'+'", "'-'", "'*'", "'('", "')'"
    };
    public static final int WS=7;
    public static final int NEWLINE=4;
    public static final int INT=6;
    public static final int ID=5;
    public static final int EOF=-1;

        public ExprParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "Expr.g"; }


    /** Map variable name to Integer object holding value */
    HashMap memory = new HashMap();



    // $ANTLR start prog
    // Expr.g:15:1: prog : ( stat )+ ;
    public final void prog() throws RecognitionException {
        try {
            // Expr.g:15:5: ( ( stat )+ )
            // Expr.g:15:9: ( stat )+
            {
            // Expr.g:15:9: ( stat )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=NEWLINE && LA1_0<=INT)||LA1_0==12) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // Expr.g:15:9: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_prog26);
            	    stat();
            	    _fsp--;


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

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end prog


    // $ANTLR start stat
    // Expr.g:17:1: stat : ( expr NEWLINE | ID '=' expr NEWLINE | NEWLINE );
    public final void stat() throws RecognitionException {
        Token ID2=null;
        int expr1 = 0;

        int expr3 = 0;


        try {
            // Expr.g:17:5: ( expr NEWLINE | ID '=' expr NEWLINE | NEWLINE )
            int alt2=3;
            switch ( input.LA(1) ) {
            case INT:
            case 12:
                {
                alt2=1;
                }
                break;
            case ID:
                {
                int LA2_2 = input.LA(2);

                if ( (LA2_2==8) ) {
                    alt2=2;
                }
                else if ( (LA2_2==NEWLINE||(LA2_2>=9 && LA2_2<=11)) ) {
                    alt2=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("17:1: stat : ( expr NEWLINE | ID '=' expr NEWLINE | NEWLINE );", 2, 2, input);

                    throw nvae;
                }
                }
                break;
            case NEWLINE:
                {
                alt2=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("17:1: stat : ( expr NEWLINE | ID '=' expr NEWLINE | NEWLINE );", 2, 0, input);

                throw nvae;
            }

            switch (alt2) {
                case 1 :
                    // Expr.g:19:9: expr NEWLINE
                    {
                    pushFollow(FOLLOW_expr_in_stat71);
                    expr1=expr();
                    _fsp--;

                    match(input,NEWLINE,FOLLOW_NEWLINE_in_stat73); 
                    System.out.println(expr1);

                    }
                    break;
                case 2 :
                    // Expr.g:23:9: ID '=' expr NEWLINE
                    {
                    ID2=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_stat104); 
                    match(input,8,FOLLOW_8_in_stat106); 
                    pushFollow(FOLLOW_expr_in_stat108);
                    expr3=expr();
                    _fsp--;

                    match(input,NEWLINE,FOLLOW_NEWLINE_in_stat110); 
                    memory.put(ID2.getText(), new Integer(expr3));

                    }
                    break;
                case 3 :
                    // Expr.g:27:9: NEWLINE
                    {
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_stat140); 

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end stat


    // $ANTLR start expr
    // Expr.g:32:1: expr returns [int value] : e= multExpr ( '+' e= multExpr | '-' e= multExpr )* ;
    public final int expr() throws RecognitionException {
        int value = 0;

        int e = 0;


        try {
            // Expr.g:36:5: (e= multExpr ( '+' e= multExpr | '-' e= multExpr )* )
            // Expr.g:36:9: e= multExpr ( '+' e= multExpr | '-' e= multExpr )*
            {
            pushFollow(FOLLOW_multExpr_in_expr169);
            e=multExpr();
            _fsp--;

            value = e;
            // Expr.g:37:9: ( '+' e= multExpr | '-' e= multExpr )*
            loop3:
            do {
                int alt3=3;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==9) ) {
                    alt3=1;
                }
                else if ( (LA3_0==10) ) {
                    alt3=2;
                }


                switch (alt3) {
            	case 1 :
            	    // Expr.g:37:13: '+' e= multExpr
            	    {
            	    match(input,9,FOLLOW_9_in_expr185); 
            	    pushFollow(FOLLOW_multExpr_in_expr189);
            	    e=multExpr();
            	    _fsp--;

            	    value += e;

            	    }
            	    break;
            	case 2 :
            	    // Expr.g:38:13: '-' e= multExpr
            	    {
            	    match(input,10,FOLLOW_10_in_expr205); 
            	    pushFollow(FOLLOW_multExpr_in_expr209);
            	    e=multExpr();
            	    _fsp--;

            	    value -= e;

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end expr


    // $ANTLR start multExpr
    // Expr.g:44:1: multExpr returns [int value] : ee= atom ( '*' e= atom )* ;
    public final int multExpr() throws RecognitionException {
        int value = 0;

        int ee = 0;

        int e = 0;


        try {
            // Expr.g:50:5: (ee= atom ( '*' e= atom )* )
            // Expr.g:50:9: ee= atom ( '*' e= atom )*
            {
            pushFollow(FOLLOW_atom_in_multExpr251);
            ee=atom();
            _fsp--;

            value = ee;
            // Expr.g:50:39: ( '*' e= atom )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==11) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // Expr.g:50:40: '*' e= atom
            	    {
            	    match(input,11,FOLLOW_11_in_multExpr256); 
            	    pushFollow(FOLLOW_atom_in_multExpr260);
            	    e=atom();
            	    _fsp--;

            	    value *= e;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end multExpr


    // $ANTLR start atom
    // Expr.g:55:1: atom returns [int value] : ( INT | ID | '(' expr ')' );
    public final int atom() throws RecognitionException {
        int value = 0;

        Token INT4=null;
        Token ID5=null;
        int expr6 = 0;


        try {
            // Expr.g:56:5: ( INT | ID | '(' expr ')' )
            int alt5=3;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt5=1;
                }
                break;
            case ID:
                {
                alt5=2;
                }
                break;
            case 12:
                {
                alt5=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("55:1: atom returns [int value] : ( INT | ID | '(' expr ')' );", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // Expr.g:57:9: INT
                    {
                    INT4=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_atom299); 
                    value = Integer.parseInt(INT4.getText());

                    }
                    break;
                case 2 :
                    // Expr.g:59:9: ID
                    {
                    ID5=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_atom312); 

                            // look up value of variable
                            Integer v = (Integer)memory.get(ID5.getText());
                            // if found, set return value else error
                            if ( v!=null ) value = v.intValue();
                            else System.err.println("undefined variable "+ID5.getText());
                            

                    }
                    break;
                case 3 :
                    // Expr.g:69:9: '(' expr ')'
                    {
                    match(input,12,FOLLOW_12_in_atom343); 
                    pushFollow(FOLLOW_expr_in_atom345);
                    expr6=expr();
                    _fsp--;

                    match(input,13,FOLLOW_13_in_atom347); 
                    value = expr6;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return value;
    }
    // $ANTLR end atom


 

    public static final BitSet FOLLOW_stat_in_prog26 = new BitSet(new long[]{0x0000000000001072L});
    public static final BitSet FOLLOW_expr_in_stat71 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NEWLINE_in_stat73 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_stat104 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_stat106 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_expr_in_stat108 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NEWLINE_in_stat110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_in_stat140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multExpr_in_expr169 = new BitSet(new long[]{0x0000000000000602L});
    public static final BitSet FOLLOW_9_in_expr185 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_multExpr_in_expr189 = new BitSet(new long[]{0x0000000000000602L});
    public static final BitSet FOLLOW_10_in_expr205 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_multExpr_in_expr209 = new BitSet(new long[]{0x0000000000000602L});
    public static final BitSet FOLLOW_atom_in_multExpr251 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_11_in_multExpr256 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_atom_in_multExpr260 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_INT_in_atom299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_atom312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_atom343 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_expr_in_atom345 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_atom347 = new BitSet(new long[]{0x0000000000000002L});

}