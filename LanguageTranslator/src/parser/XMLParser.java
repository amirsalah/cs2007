/**
 * LL parser for XML files which comply with the dtd requirement
 * k = 1, means we only have to lookahead 1 token to make decisions
 * n_XXX means (n)onterminals
 */
package parser;

import lexer.Yytoken;
import lexer.XMLLexer;
import exception.RecognitionException;
import lexer.symbol.sym;

public class XMLParser {
    private XMLLexer lexer;
    private Yytoken currentToken;
    
    public XMLParser(String sourceFile){
        String source_dir = "../source_language/";
        lexer = new XMLLexer();
        try{
            lexer.initLexer(source_dir.concat(sourceFile));
//            lexer.initLexer("source_language/" + sourceFile);
        }
        catch (java.io.IOException ioe){
            System.err.println("source file: " + sourceFile + " initialization failed");
        }
    }
    
    /**
     * parse the source file
     * @return
     */
    public boolean parsing(){
        try{
            advanceLexer();
            n_document();
        }
        catch (RecognitionException re){
            System.out.println("parsing failed, the source code isn't valid");
        }
        return true;
    }
    
    /**
     * Advance the lexer to set next token
     */
    private void advanceLexer(){
        try{
            currentToken = lexer.nextToken();
        }
        catch (java.io.IOException ioe){
            System.out.print("failed to get next token");
        }
    }
    
    /**
     * Test if current token is the same as given symbol
     * @param symbol the symbol to be tested
     * @return true if symbol is current token, false otherwise
     */
    private boolean have(int symbol){
        if(symbol == currentToken.m_index){
            advanceLexer();
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Test if current token is the same as given symbol
     * @param symbol
     */
    private void mustbe(int symbol){
        if(symbol == currentToken.m_index){
            advanceLexer();
        } else {
            System.err.println("mismatch symbol: " + sym.getTokenName(symbol));
        }
    }
    
    /**
     * 
     * @throws RecognitionException
     */
    public void n_document() throws RecognitionException{
        if(have(sym.tXMLHEAD)){
            n_xmlDECL();
        } 
        
        if(have(sym.tDOCTYPE)){
            n_docTYPE();
        }
        
        mustbe(sym.tSYSTEMSPEC);
        n_system_spec();
        mustbe(sym.EOF);
        System.out.println("The source code is valid");
        return;
    }
    
    public void n_xmlDECL() throws RecognitionException {
        while(have(sym.tATTRIBUTE_NAME)){
            n_attribute();
        }
        mustbe(sym.tEND_XMLHEAD);
    }
    
    public void n_docTYPE() throws RecognitionException {
        mustbe(sym.tROOT);
        mustbe(sym.tSYSTEM);
        mustbe(sym.tATTRIBUTE_VALUE);
        mustbe(sym.tENDTAG);
    }
    
    public void n_system_spec() throws RecognitionException {
        mustbe(sym.tENDTAG);
        
        mustbe(sym.tINTERFACE);
        n_interface();
        
        while(have(sym.tINTERFACE)){
            n_interface();
        }
        
        mustbe(sym.tCLASS);
        n_class();
        
        while(have(sym.tCLASS)){
            n_class();
        }

        mustbe(sym.tMACHINE);
        n_machine();
        
        while(have(sym.tMACHINE)){
            n_machine();
        }

        mustbe(sym.tINSTANCE);
        n_instance();
        
        while(have(sym.tINSTANCE)){
            n_instance();
        }
        
        mustbe(sym.tEND_SYSTEMSPEC);

    }

    public void n_attribute() {
//        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        mustbe(sym.tATTRIBUTE_VALUE);
    }
    
    public void n_interface(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tENDTAG);
        
        while(have(sym.tPARENT)){
            n_parent();
        }
        
        while(have(sym.tMETHOD)){
            n_method();
        }
        
        mustbe(sym.tEND_INTERFACE);
    }
    
    public void n_parent(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_method(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tENDTAG);
        
        mustbe(sym.tRESULT);
        n_result();
        
        while(have(sym.tPARAMETER)){
            n_parameter();
        }
        
        mustbe(sym.tEND_METHOD);
    }
    
    public void n_result(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_parameter(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_class(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_machine(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
        
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();

        mustbe(sym.tENDTAG);
        
        while(have(sym.tEVENTDEF)){
            n_eventdef();
        }
        
        mustbe(sym.tSTATE);
        n_state();
        
        while(have(sym.tSTATE)){
            n_state();
        }
        
        while(have(sym.tTRANSITION)){
            n_transition();
        }
        
        mustbe(sym.tEND_MACHINE);
    }
    
    public void n_eventdef(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tENDTAG);
        
        while(have(sym.tPARAMETER)){
            n_parameter();
        }
    
        mustbe(sym.tEND_EVENTDEF);
    }
    
    public void n_state(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tENDTAG);
        
        while(have(sym.tACTION)){
            n_action();
        }
        
        while(have(sym.tOUTGOING)){
            n_outgoing();
        }
        
        mustbe(sym.tEND_STATE);
    }
    
    public void n_action(){
        mustbe(sym.tPCDATA);
        mustbe(sym.tEND_ACTION);
    }
    
    public void n_outgoing(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_transition(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tENDTAG);
        
        mustbe(sym.tSOURCE);
        n_source();
        
        mustbe(sym.tTARGET);
        n_target();
        
        mustbe(sym.tEVENT);
        n_event();
        
        mustbe(sym.tGUARD);
        n_guard();
        
        while(have(sym.tACTION)){
            n_action();
        }
        
        mustbe(sym.tEND_TRANSITION);
    }
    
    public void n_source(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_target(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tENDELEMENT);

    }
    
    public void n_event(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tENDELEMENT);

    }
    
    public void n_guard(){
        mustbe(sym.tPCDATA);
        mustbe(sym.tEND_GUARD);
    }
    
    public void n_instance(){
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tATTRIBUTE_NAME);
        n_attribute();
 
        mustbe(sym.tENDTAG);

        while(have(sym.tARGUMENT)){
            n_argument();
        }
        
        mustbe(sym.tEND_INSTANCE);
    }
    
    public void n_argument(){
        mustbe(sym.tPCDATA);
        mustbe(sym.tEND_ARGUMENT);
    }
}
