import TREENODE.*;

public class lt_parser {
    private lt_lexer lexer;
    private Yytoken currentToken;
    private AbstractViewableMachineNode rootNode;
    private boolean correct = true;


    public lt_parser(){
        String source = "testing/MachineSample.xml";
        lexer = new lt_lexer();
        try{
            lexer.initLexer(source);
        }
        catch (java.io.IOException ioe){
            System.err.println("source file: " + source+ " initialization failed");
        }
    }

    /**
     * parse the source file
     * @return
     */
    public boolean parsing(){
            advanceLexer();
            n_document();
            rootNode.prettyPrint(0);
            rootNode.display();
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
            System.err.println("mismatch symbol: " + sym.tName(symbol));
            System.err.println("got: " + sym.tName(currentToken.m_index));
        }
    }

    private boolean mustbe_noAdvance(int symbol){
        if(symbol == currentToken.m_index){
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @throws RecognitionException
     */
    public void n_document() {
        if(have(sym.LEFTXML)){
            n_xmlDECL();
        }

        if(have(sym.DOCTYPE)){
            n_docTYPE();
        }

        mustbe(sym.LEFT_SYSTEMSPEC);
        n_system_spec();
        mustbe(sym.EOF);
        System.out.println("The source code is valid");
        return;
    }

    public void n_xmlDECL() {
        while(have(sym.SYM_VERSION)){
            mustbe(sym.SYM_EQUALS);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.RIGHTXML);
    }

    public void n_docTYPE() {
        mustbe(sym.ATT_VALUE);
        mustbe(sym.RIGHTANGLE);
    }

    public void n_system_spec()  {
        rootNode = new TREENODE_SYSTEMSPEC();
        mustbe(sym.RIGHTANGLE);

        mustbe(sym.LEFT_INTERFACE);
        n_interface(rootNode);

        while(have(sym.LEFT_INTERFACE)){
            n_interface(rootNode);
        }

        mustbe(sym.SYM_CLASS);
        n_class(rootNode);

        while(have(sym.SYM_CLASS)){
            n_class(rootNode);
        }

        mustbe(sym.LEFT_MACHINE);
        n_machine(rootNode);

        while(have(sym.LEFT_MACHINE)){
            n_machine(rootNode);
        }

        mustbe(sym.LEFT_INSTANCE);
        n_instance(rootNode);

        while(have(sym.LEFT_INSTANCE)){
            n_instance(rootNode);
        }

        mustbe(sym.SYSTEMSPEC_RIGHT);

    }


    public void n_interface(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_INTERFACE();
        parentNode.addChild(node);
        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);

        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_INTERFACE) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);

        mustbe(sym.RIGHTANGLE);

        while(have(sym.SYM_PARENT)){
            n_parent(node);
        }

        while(have(sym.LEFT_METHOD)){
            n_method(node);
        }

        mustbe(sym.INTERFACE_RIGHT);
    }

    public void n_parent(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_PARENT();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_PARENT) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.ELEMENT_RIGHT);
    }

    public void n_method(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_METHOD();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_METHOD) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.RIGHTANGLE);

        mustbe(sym.SYM_RESULT);
        n_result(node);

        while(have(sym.SYM_PARAMETER)){
            n_parameter(node);
        }

        mustbe(sym.METHOD_RIGHT);
    }

    public void n_result(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_RESULT();
        parentNode.addChild(node);

        mustbe(sym.SYM_TYPE);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_RESULT) node).setAttribute_type(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.ELEMENT_RIGHT);
    }

    public void n_parameter(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_PARAMETER();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_PARAMETER) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.SYM_TYPE);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_PARAMETER) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.ELEMENT_RIGHT);
    }

    public void n_class(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_CLASS();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_CLASS) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.SYM_IMPLEMENTS);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_CLASS) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.ELEMENT_RIGHT);
    }

    public void n_machine(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_MACHINE();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_MACHINE) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);

        mustbe(sym.SYM_EXTENDS);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_MACHINE) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.RIGHTANGLE);

        while(have(sym.LEFT_EVENTDEF)){
            n_eventdef(node);
        }

        mustbe(sym.LEFT_STATE);
        n_state(node);

        while(have(sym.LEFT_STATE)){
            n_state(node);
        }

        while(have(sym.LEFT_TRANSITION)){
            n_transition(node);
        }

        mustbe(sym.MACHINE_RIGHT);
    }

    public void n_eventdef(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_EVENTDEF();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_EVENTDEF) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.RIGHTANGLE);

        while(have(sym.SYM_PARAMETER)){
            n_parameter(node);
        }

        mustbe(sym.EVENTDEF_RIGHT);
    }

    public void n_state(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_STATE();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_STATE) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.RIGHTANGLE);

        while(have(sym.LEFT_ACTION)){
            n_action(node);
        }

        while(have(sym.SYM_OUTGOING)){
            n_outgoing(node);
        }

        mustbe(sym.STATE_RIGHT);
    }

    public void n_action(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_ACTION();
        parentNode.addChild(node);

        if(mustbe_noAdvance(sym.SYM_PCDATA)){
            ((TREENODE_ACTION) node).setPCDATA(currentToken.m_text);
    }
        mustbe(sym.SYM_PCDATA);
        mustbe(sym.ACTION_RIGHT);
    }

    public void n_outgoing(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_OUTGOING();
        parentNode.addChild(node);

        mustbe(sym.SYM_TRANS);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_OUTGOING) node).setAttribute_trans(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.ELEMENT_RIGHT);
    }

    public void n_transition(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_TRANSITION();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_TRANSITION) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.RIGHTANGLE);

        mustbe(sym.SYM_SOURCE);
        n_source(node);

        mustbe(sym.SYM_TARGET);
        n_target(node);

        mustbe(sym.SYM_EVENT);
        n_event(node);

        mustbe(sym.LEFT_GUARD);
        n_guard(node);

        while(have(sym.LEFT_ACTION)){
            n_action(node);
        }

        mustbe(sym.TRANSITION_RIGHT);
    }

    public void n_source(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_SOURCE();
        parentNode.addChild(node);

        mustbe(sym.SYM_ATTSTATE);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_SOURCE) node).setAttribute_state(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.ELEMENT_RIGHT);
    }

    public void n_target(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_TARGET();
        parentNode.addChild(node);

        mustbe(sym.SYM_ATTSTATE);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_TARGET) node).setAttribute_state(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.ELEMENT_RIGHT);

    }

    public void n_event(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_EVENT();
        parentNode.addChild(node);

        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_EVENT) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.ELEMENT_RIGHT);

    }

    public void n_guard(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_GUARD();
        parentNode.addChild(node);


        if(mustbe_noAdvance(sym.SYM_PCDATA)){
            ((TREENODE_GUARD) node).setPCDATA(currentToken.m_text);
    }
        mustbe(sym.SYM_PCDATA);
        mustbe(sym.GUARD_RIGHT);
    }

    public void n_instance(AbstractViewableMachineNode parentNode){
        AbstractViewableMachineNode node = new TREENODE_INSTANCE();
        parentNode.addChild(node);


        mustbe(sym.SYM_NAME);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_INSTANCE) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.SYM_KIND);
        mustbe(sym.SYM_EQUALS);
        if(mustbe_noAdvance(sym.ATT_VALUE)){
            ((TREENODE_INSTANCE) node).setAttribute_name(currentToken.m_text);
        }
        mustbe(sym.ATT_VALUE);
        mustbe(sym.RIGHTANGLE);

        while(have(sym.LEFT_ARG)){
            n_argument(node);
        }

        mustbe(sym.INSTANCE_RIGHT);
    }

    public void n_argument(AbstractViewableMachineNode parentNode){

        AbstractViewableMachineNode node = new TREENODE_ARGUMENT();
        parentNode.addChild(node);

        if(mustbe_noAdvance(sym.SYM_PCDATA)){
            ((TREENODE_ARGUMENT) node).setPCDATA(currentToken.m_text);
        mustbe(sym.SYM_PCDATA);
        mustbe(sym.ARGUMENT_RIGHT);
        }
    }
}