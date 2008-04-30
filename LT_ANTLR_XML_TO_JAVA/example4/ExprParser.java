// $ANTLR 3.0.1 Expr.g 2008-04-30 01:08:07

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;


import org.antlr.runtime.tree.*;

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
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "Expr.g"; }


    public static class prog_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start prog
    // Expr.g:8:1: prog : ( stat )+ ;
    public final prog_return prog() throws RecognitionException {
        prog_return retval = new prog_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        stat_return stat1 = null;



        try {
            // Expr.g:15:5: ( ( stat )+ )
            // Expr.g:15:9: ( stat )+
            {
            root_0 = (CommonTree)adaptor.nil();

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
            	    // Expr.g:15:11: stat
            	    {
            	    pushFollow(FOLLOW_stat_in_prog39);
            	    stat1=stat();
            	    _fsp--;

            	    adaptor.addChild(root_0, stat1.getTree());
            	    System.out.println(((CommonTree)stat1.tree).toStringTree());

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

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end prog

    public static class stat_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start stat
    // Expr.g:17:1: stat : ( expr NEWLINE | ID '=' expr NEWLINE | NEWLINE );
    public final stat_return stat() throws RecognitionException {
        stat_return retval = new stat_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token NEWLINE3=null;
        Token ID4=null;
        Token char_literal5=null;
        Token NEWLINE7=null;
        Token NEWLINE8=null;
        expr_return expr2 = null;

        expr_return expr6 = null;


        CommonTree NEWLINE3_tree=null;
        CommonTree ID4_tree=null;
        CommonTree char_literal5_tree=null;
        CommonTree NEWLINE7_tree=null;
        CommonTree NEWLINE8_tree=null;

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
                    // Expr.g:17:9: expr NEWLINE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_expr_in_stat54);
                    expr2=expr();
                    _fsp--;

                    adaptor.addChild(root_0, expr2.getTree());
                    NEWLINE3=(Token)input.LT(1);
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_stat56); 

                    }
                    break;
                case 2 :
                    // Expr.g:18:9: ID '=' expr NEWLINE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ID4=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_stat67); 
                    ID4_tree = (CommonTree)adaptor.create(ID4);
                    adaptor.addChild(root_0, ID4_tree);

                    char_literal5=(Token)input.LT(1);
                    match(input,8,FOLLOW_8_in_stat69); 
                    char_literal5_tree = (CommonTree)adaptor.create(char_literal5);
                    root_0 = (CommonTree)adaptor.becomeRoot(char_literal5_tree, root_0);

                    pushFollow(FOLLOW_expr_in_stat72);
                    expr6=expr();
                    _fsp--;

                    adaptor.addChild(root_0, expr6.getTree());
                    NEWLINE7=(Token)input.LT(1);
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_stat74); 

                    }
                    break;
                case 3 :
                    // Expr.g:19:9: NEWLINE
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    NEWLINE8=(Token)input.LT(1);
                    match(input,NEWLINE,FOLLOW_NEWLINE_in_stat86); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end stat

    public static class expr_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start expr
    // Expr.g:24:1: expr : multExpr ( ( '+' | '-' ) multExpr )* ;
    public final expr_return expr() throws RecognitionException {
        expr_return retval = new expr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal10=null;
        Token char_literal11=null;
        multExpr_return multExpr9 = null;

        multExpr_return multExpr12 = null;


        CommonTree char_literal10_tree=null;
        CommonTree char_literal11_tree=null;

        try {
            // Expr.g:24:5: ( multExpr ( ( '+' | '-' ) multExpr )* )
            // Expr.g:24:9: multExpr ( ( '+' | '-' ) multExpr )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_multExpr_in_expr103);
            multExpr9=multExpr();
            _fsp--;

            adaptor.addChild(root_0, multExpr9.getTree());
            // Expr.g:24:18: ( ( '+' | '-' ) multExpr )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( ((LA4_0>=9 && LA4_0<=10)) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // Expr.g:24:19: ( '+' | '-' ) multExpr
            	    {
            	    // Expr.g:24:19: ( '+' | '-' )
            	    int alt3=2;
            	    int LA3_0 = input.LA(1);

            	    if ( (LA3_0==9) ) {
            	        alt3=1;
            	    }
            	    else if ( (LA3_0==10) ) {
            	        alt3=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("24:19: ( '+' | '-' )", 3, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt3) {
            	        case 1 :
            	            // Expr.g:24:20: '+'
            	            {
            	            char_literal10=(Token)input.LT(1);
            	            match(input,9,FOLLOW_9_in_expr107); 
            	            char_literal10_tree = (CommonTree)adaptor.create(char_literal10);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal10_tree, root_0);


            	            }
            	            break;
            	        case 2 :
            	            // Expr.g:24:25: '-'
            	            {
            	            char_literal11=(Token)input.LT(1);
            	            match(input,10,FOLLOW_10_in_expr110); 
            	            char_literal11_tree = (CommonTree)adaptor.create(char_literal11);
            	            root_0 = (CommonTree)adaptor.becomeRoot(char_literal11_tree, root_0);


            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_multExpr_in_expr114);
            	    multExpr12=multExpr();
            	    _fsp--;

            	    adaptor.addChild(root_0, multExpr12.getTree());

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end expr

    public static class multExpr_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start multExpr
    // Expr.g:27:1: multExpr : atom ( '*' atom )* ;
    public final multExpr_return multExpr() throws RecognitionException {
        multExpr_return retval = new multExpr_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token char_literal14=null;
        atom_return atom13 = null;

        atom_return atom15 = null;


        CommonTree char_literal14_tree=null;

        try {
            // Expr.g:28:5: ( atom ( '*' atom )* )
            // Expr.g:28:9: atom ( '*' atom )*
            {
            root_0 = (CommonTree)adaptor.nil();

            pushFollow(FOLLOW_atom_in_multExpr136);
            atom13=atom();
            _fsp--;

            adaptor.addChild(root_0, atom13.getTree());
            // Expr.g:28:14: ( '*' atom )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==11) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // Expr.g:28:15: '*' atom
            	    {
            	    char_literal14=(Token)input.LT(1);
            	    match(input,11,FOLLOW_11_in_multExpr139); 
            	    char_literal14_tree = (CommonTree)adaptor.create(char_literal14);
            	    root_0 = (CommonTree)adaptor.becomeRoot(char_literal14_tree, root_0);

            	    pushFollow(FOLLOW_atom_in_multExpr142);
            	    atom15=atom();
            	    _fsp--;

            	    adaptor.addChild(root_0, atom15.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            }

            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end multExpr

    public static class atom_return extends ParserRuleReturnScope {
        CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start atom
    // Expr.g:31:1: atom : ( INT | ID | '(' expr ')' );
    public final atom_return atom() throws RecognitionException {
        atom_return retval = new atom_return();
        retval.start = input.LT(1);

        CommonTree root_0 = null;

        Token INT16=null;
        Token ID17=null;
        Token char_literal18=null;
        Token char_literal20=null;
        expr_return expr19 = null;


        CommonTree INT16_tree=null;
        CommonTree ID17_tree=null;
        CommonTree char_literal18_tree=null;
        CommonTree char_literal20_tree=null;

        try {
            // Expr.g:31:5: ( INT | ID | '(' expr ')' )
            int alt6=3;
            switch ( input.LA(1) ) {
            case INT:
                {
                alt6=1;
                }
                break;
            case ID:
                {
                alt6=2;
                }
                break;
            case 12:
                {
                alt6=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("31:1: atom : ( INT | ID | '(' expr ')' );", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // Expr.g:31:9: INT
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    INT16=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_atom159); 
                    INT16_tree = (CommonTree)adaptor.create(INT16);
                    adaptor.addChild(root_0, INT16_tree);


                    }
                    break;
                case 2 :
                    // Expr.g:32:9: ID
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    ID17=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_atom170); 
                    ID17_tree = (CommonTree)adaptor.create(ID17);
                    adaptor.addChild(root_0, ID17_tree);


                    }
                    break;
                case 3 :
                    // Expr.g:33:9: '(' expr ')'
                    {
                    root_0 = (CommonTree)adaptor.nil();

                    char_literal18=(Token)input.LT(1);
                    match(input,12,FOLLOW_12_in_atom180); 
                    pushFollow(FOLLOW_expr_in_atom183);
                    expr19=expr();
                    _fsp--;

                    adaptor.addChild(root_0, expr19.getTree());
                    char_literal20=(Token)input.LT(1);
                    match(input,13,FOLLOW_13_in_atom185); 

                    }
                    break;

            }
            retval.stop = input.LT(-1);

                retval.tree = (CommonTree)adaptor.rulePostProcessing(root_0);
                adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return retval;
    }
    // $ANTLR end atom


 

    public static final BitSet FOLLOW_stat_in_prog39 = new BitSet(new long[]{0x0000000000001072L});
    public static final BitSet FOLLOW_expr_in_stat54 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NEWLINE_in_stat56 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_stat67 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_8_in_stat69 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_expr_in_stat72 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_NEWLINE_in_stat74 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEWLINE_in_stat86 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_multExpr_in_expr103 = new BitSet(new long[]{0x0000000000000602L});
    public static final BitSet FOLLOW_9_in_expr107 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_10_in_expr110 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_multExpr_in_expr114 = new BitSet(new long[]{0x0000000000000602L});
    public static final BitSet FOLLOW_atom_in_multExpr136 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_11_in_multExpr139 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_atom_in_multExpr142 = new BitSet(new long[]{0x0000000000000802L});
    public static final BitSet FOLLOW_INT_in_atom159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_atom170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_12_in_atom180 = new BitSet(new long[]{0x0000000000001060L});
    public static final BitSet FOLLOW_expr_in_atom183 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_atom185 = new BitSet(new long[]{0x0000000000000002L});

}