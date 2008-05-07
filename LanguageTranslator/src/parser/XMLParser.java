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
import parser.astnode.*;

public class XMLParser {
    private XMLLexer lexer;
    private Yytoken currentToken;
    private boolean debuggingParser = true;
    private boolean validCode = true;
    private AbstractViewableMachineNode rootNode;
    
    public XMLParser(String sourceFile){
        String source_dir = "../source_language/";
        lexer = new XMLLexer();
        try{
//            lexer.initLexer(source_dir.concat(sourceFile));
            lexer.initLexer("source_language/" + sourceFile);
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
        
        if(validCode){
            rootNode.display();
            rootNode.prettyPrint(0);
            return true;
        }else{
            return false;
        }
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
            System.err.println("mismatch: mustbe  " + sym.getTokenName(symbol));
            System.err.println("          got     " + sym.getTokenName(currentToken.m_index));
            validCode = false;
        }
    }
    
    /**
     * Test if the given symbol is correct without advancing to next token
     * @param symbol
     * @return
     */
    private boolean mustbe_noAdvance(int symbol){
        if(symbol == currentToken.m_index){
            return true;
        } else {
            return false;
        }
    }
    
    private void printDebugInfo(String functionName){
        if(debuggingParser){
            System.out.println(functionName);
            System.out.println("current token: " + currentToken.m_text);
            System.out.println();
        }
    }
    
    
    /**
     * 
     * @throws RecognitionException
     */
    public void n_document() throws RecognitionException{
        printDebugInfo("n_document()");
        
        if(have(sym.tXMLHEAD)){
            n_xmlDECL();
        }
        
        if(have(sym.tDOCTYPE)){
            n_docTYPE();
        }
        
        mustbe(sym.tSYSTEMSPEC);
        n_system_spec();
        mustbe(sym.EOF);
        printDebugInfo("n_document() end");
        System.out.println("parsing finished");
        if(validCode){
            System.out.println("##The source code is valid##");
        }else{
            System.out.println("##The source code is invalid, tree visualization turned off##");
        }
        return;
    }
    
    public void n_xmlDECL() throws RecognitionException {
        printDebugInfo("n_xmlDECL()");

        while(have(sym.tATTRIBUTE_NAME)){
            n_attribute();
        }
        
        printDebugInfo("n_xmlDECL() end");
        mustbe(sym.tEND_XMLHEAD);
    }
    
    public void n_docTYPE() throws RecognitionException {
        printDebugInfo("n_docType()");

        mustbe(sym.tROOT);
        mustbe(sym.tSYSTEM);
        mustbe(sym.tATTRIBUTE_VALUE);
        
        printDebugInfo("n_docTyep() end");
        mustbe(sym.tENDTAG);
    }
    
    public void n_system_spec() throws RecognitionException {
        printDebugInfo("n_system_spec()");
        rootNode = new SystemSpecNode();
        mustbe(sym.tENDTAG);
        
        mustbe(sym.tINTERFACE);
        n_interface(rootNode);
        
        while(have(sym.tINTERFACE)){
            n_interface(rootNode);
        }
        
        mustbe(sym.tCLASS);
        n_class(rootNode);
        
        while(have(sym.tCLASS)){
            n_class(rootNode);
        }

        mustbe(sym.tMACHINE);
        n_machine(rootNode);
        
        while(have(sym.tMACHINE)){
            n_machine(rootNode);
        }

        mustbe(sym.tINSTANCE);
        n_instance(rootNode);
        
        while(have(sym.tINSTANCE)){
            n_instance(rootNode);
        }
        
        printDebugInfo("n_system_spec() end");
        mustbe(sym.tEND_SYSTEMSPEC);
    }

    public void n_attribute() {
//        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        mustbe(sym.tATTRIBUTE_VALUE);
    }
    
    public void n_interface(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_interface()");
        // create a node, and set its parent node
        AbstractViewableMachineNode node = new InterfaceNode();
        parentNode.addChild(node);
        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        // set the attribute of the node
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((InterfaceNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        mustbe(sym.tENDTAG);
        
        while(have(sym.tPARENT)){
            n_parent(node);
        }
        
        while(have(sym.tMETHOD)){
            n_method(node);
        }
        printDebugInfo("n_interface() end");
        mustbe(sym.tEND_INTERFACE);
    }
    
    public void n_parent(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_parent()");
        
        AbstractViewableMachineNode node = new ParentNode();
        parentNode.addChild(node);
        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((ParentNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        printDebugInfo("n_parent() end");
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_method(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_method()");
        
        AbstractViewableMachineNode node = new MethodNode();
        parentNode.addChild(node);
        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((MethodNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        mustbe(sym.tENDTAG);
        
        mustbe(sym.tRESULT);
        n_result(node);
        
        while(have(sym.tPARAMETER)){
            n_parameter(node);
        }
        printDebugInfo("n_method() end");
        mustbe(sym.tEND_METHOD);
    }
    
    public void n_result(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_result()");
        
        AbstractViewableMachineNode node = new ResultNode();
        parentNode.addChild(node);
        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((ResultNode) node).setAttribute_type(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        printDebugInfo("n_result() end");
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_parameter(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_parameter()");
        
        AbstractViewableMachineNode node = new ParameterNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((ParameterNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((ParameterNode) node).setAttribute_type(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        printDebugInfo("n_parameter() end");
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_class(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_class()");
        
        AbstractViewableMachineNode node = new ClassNode();
        parentNode.addChild(node);
        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((ClassNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((ClassNode) node).setAttribute_implements(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        printDebugInfo("n_class() end");
        mustbe(sym.tENDELEMENT);
    }
    
    public void n_machine(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_machine()");
        
        AbstractViewableMachineNode node = new MachineNode();
        parentNode.addChild(node);
        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((MachineNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((MachineNode) node).setAttribute_extends(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);


        mustbe(sym.tENDTAG);
        
        while(have(sym.tEVENTDEF)){
            n_eventdef(node);
        }
        
        mustbe(sym.tSTATE);
        n_state(node);
        
        while(have(sym.tSTATE)){
            n_state(node);
        }
        
        while(have(sym.tTRANSITION)){
            n_transition(node);
        }
        printDebugInfo("n_machine() end");
        mustbe(sym.tEND_MACHINE);
    }
    
    public void n_eventdef(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_eventdef()");
        
        AbstractViewableMachineNode node = new EventdefNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((EventdefNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

 
        mustbe(sym.tENDTAG);
        
        while(have(sym.tPARAMETER)){
            n_parameter(node);
        }
        printDebugInfo("n_eventdef()");
        mustbe(sym.tEND_EVENTDEF);
    }
    
    public void n_state(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_state()");
        
        AbstractViewableMachineNode node = new StateNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((StateNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);
 
        mustbe(sym.tENDTAG);
        
        while(have(sym.tACTION)){
            n_action(node);
        }
        
        while(have(sym.tOUTGOING)){
            n_outgoing(node);
        }
        printDebugInfo("n_state() end");
        mustbe(sym.tEND_STATE);
    }
    
    public void n_action(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_action()");
        
        AbstractViewableMachineNode node = new ActionNode();
        parentNode.addChild(node);

        if(mustbe_noAdvance(sym.tPCDATA)){
            ((ActionNode) node).setPCDATA(currentToken.m_text);
        }
        mustbe(sym.tPCDATA);
        printDebugInfo("n_action() end");
        mustbe(sym.tEND_ACTION);
    }
    
    public void n_outgoing(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_outgoing()");
        
        AbstractViewableMachineNode node = new OutgoingNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((OutgoingNode) node).setAttribute_trans(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        printDebugInfo("n_outgoing() end");
        mustbe(sym.tENDELEMENT);
        
    }
    
    public void n_transition(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_transition()");
        
        AbstractViewableMachineNode node = new TransitionNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((TransitionNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

 
        mustbe(sym.tENDTAG);
        
        mustbe(sym.tSOURCE);
        n_source(node);
        
        mustbe(sym.tTARGET);
        n_target(node);
        
        mustbe(sym.tEVENT);
        n_event(node);
        
        mustbe(sym.tGUARD);
        n_guard(node);
        
        while(have(sym.tACTION)){
            n_action(node);
        }
        
        printDebugInfo("n_transition() end");
        mustbe(sym.tEND_TRANSITION);
        
    }
    
    public void n_source(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_source()");
        
        AbstractViewableMachineNode node = new SourceNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((SourceNode) node).setAttribute_state(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        printDebugInfo("n_source() end");
        mustbe(sym.tENDELEMENT);
        
    }
    
    public void n_target(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_target()");
        
        AbstractViewableMachineNode node = new TargetNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((TargetNode) node).setAttribute_state(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        printDebugInfo("n_target() end");
        mustbe(sym.tENDELEMENT);
        
    }
    
    public void n_event(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_event()");
        
        AbstractViewableMachineNode node = new EventNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((EventNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

        
        printDebugInfo("n_event() end");
        mustbe(sym.tENDELEMENT);
        
    }
    
    public void n_guard(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_guard()");
        
        AbstractViewableMachineNode node = new GuardNode();
        parentNode.addChild(node);

        if(mustbe_noAdvance(sym.tPCDATA)){
            ((GuardNode) node).setPCDATA(currentToken.m_text);
        }
        mustbe(sym.tPCDATA);
        
        printDebugInfo("n_guard() end");
        mustbe(sym.tEND_GUARD); 
    }
    
    public void n_instance(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_instance()");
        
        AbstractViewableMachineNode node = new InstanceNode();
        parentNode.addChild(node);

        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((InstanceNode) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

 
        mustbe(sym.tATTRIBUTE_NAME);
        mustbe(sym.tEQUALS);
        
        if(mustbe_noAdvance(sym.tATTRIBUTE_VALUE)){
            ((InstanceNode) node).setAttribute_kind(currentToken.m_text);
        }
        mustbe(sym.tATTRIBUTE_VALUE);

 
        mustbe(sym.tENDTAG);

        while(have(sym.tARGUMENT)){
            n_argument(node);
        }
        
        printDebugInfo("n_instance() end");
        mustbe(sym.tEND_INSTANCE);
        
    }
    
    public void n_argument(AbstractViewableMachineNode parentNode){
        printDebugInfo("n_argument()");
        
        AbstractViewableMachineNode node = new ArgumentNode();
        parentNode.addChild(node);

        if(mustbe_noAdvance(sym.tPCDATA)){
            ((ArgumentNode) node).setPCDATA(currentToken.m_text);
        }
        mustbe(sym.tPCDATA);
        
        printDebugInfo("n_argument() end");
        mustbe(sym.tEND_ARGUMENT);
        
    }
}
