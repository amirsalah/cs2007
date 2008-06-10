package parser.astnode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MachineNode extends AbstractViewableMachineNode {
    private String att_name = null;
    private String att_extends = null;
    private MethodNode createMethodNode = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<MACHINE name=\"" + att_name + "\" extends=\"" + att_extends + "\">");
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }
        
        indent(indentLevel);
        System.out.println("</MACHINE>");
    }
    
    public String getNodeType(){
        return "MACHINE";
    }
    
    public void setAttribute_extends(String att_extends){
        this.att_extends = att_extends;
    }
    
    /**
     * Get the extends of the interface
     * @return
     */
    public String getAttribute_extends(){
        if(att_extends != null){
            return att_extends;
        }else{
            System.out.println("Error: MACHINE attribute extends has not been initialized");
            return null;
        }
    }
    
    public void setAttribute_name(String name){
        this.att_name = name;
    }
    
    /**
     * Get the name of the interface
     * @return
     */
    public String getAttribute_name(){
        if(att_name != null){
            return att_name;
        }else{
            System.out.println("Error: MACHINE attribute name has not been initialized");
            return null;
        }
    }
    
    /**
     * generate code files for this machine 
     */
    public void genCode(MethodNode methodNode){
        String code = null;
        this.createMethodNode = methodNode;
        
        AbstractViewableMachineNode eventdefNode = null;
        /* generate event definition classes (generate files) */
        for(Iterator i=this.childIterator(); i.hasNext();){
            eventdefNode = (AbstractViewableMachineNode)i.next();
            if(eventdefNode.getNodeType().equals("EVENTDEF")){
                ((EventdefNode)eventdefNode).genCode(att_name);
            }
        }
        
        /* generate machine implementation file */
        code = "class " + att_name + " extends " + att_extends + " {\n";
        
        // obtain state names
        ArrayList<String> stateNames = new ArrayList<String>();
        String stateName = null;
        AbstractViewableMachineNode stateNode = null;
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            stateNode = (AbstractViewableMachineNode)i.next();
            if(stateNode.getNodeType().equals("STATE")){
                stateName = ((StateNode)stateNode).getAttribute_name();
                stateNames.add(stateName);
            }
        }
        
        // generate enum States { s1 .... sn}
        code = code + "    enum States{ ";
        for(int i=0; i<stateNames.size(); i++){
            code = code + stateNames.get(i);
            if(i != stateNames.size() - 1){
                // not the last state
                code += ", ";
            }
        }
        
        // generate the definition for current state (fixed code)
        code = code + " };\n\n"
                    + "    States theState;\n\n";
        
        // generate event functions
        code = code + genEventFunctions();
                
        // generate the machine constructor(s)
        code = code + genConstructor(stateNames.get(0));
        
        // generate state functions
        code = code + genStateFunctions();
        
        // generate transition functions
        code = code + genTransitionFunctions();
        
        // generate occurrence implementation
        code = code + genOccurrence();
        
        code = code + "}\n";
        
        /* write the code to the machine file */
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("output/" + att_name + ".java"));
            out.write(code);
            out.close();
        } catch (IOException e) {
            System.err.println("failed to write to the file: " + att_name);
        }
        
    }
    
    /* generate event functions (return a event instance) */
    private String genEventFunctions(){
        String eventFuncCode = "";
        AbstractViewableMachineNode eventdefNode = null;
        for(Iterator i=this.childIterator(); i.hasNext();){
            eventdefNode = (AbstractViewableMachineNode)i.next();
            if(eventdefNode.getNodeType().equals("EVENTDEF")){
                eventFuncCode = eventFuncCode + ((EventdefNode)eventdefNode).genEvent(att_name) + "\n";
            }
        }
        
        return eventFuncCode;
    }
    

    /**
     * Generate the machine constructor
     * @param initStateName the initial state of this machine (the first state defined)
     * @return the generated constructor code
     */
    private String genConstructor(String initStateName){
        String constructorCode = "";
        String indent = "    ";
        
        constructorCode = indent + "public " + att_name + "() {\n"
                        + indent + indent + "create();\n"
                        + indent + indent + "enter_" + initStateName + "();\n"
                        + indent + "}\n\n";
        
        /* this method node is create() node, we need to generate another constructor */
        if(createMethodNode != null){
            // initialize parameter types and names
            ArrayList<String> paramTypes = new ArrayList<String>();
            ArrayList<String> paramNames = new ArrayList<String>();
            String paramType = null, paramName = null;
            
            AbstractViewableMachineNode paramNode = null;
            for(Iterator i=createMethodNode.childIterator(); i.hasNext();){
                paramNode = (AbstractViewableMachineNode)i.next();
                if(paramNode.getNodeType().equals("PARAMETER")){
                    paramName = ((ParameterNode)paramNode).getAttribute_name();
                    paramType = ((ParameterNode)paramNode).getAttribute_type();
                    paramNames.add(paramName);
                    paramTypes.add(paramType);
                }
            }
            
            // generate an additional constructor for this machine, provided a "create" method is present in the interface.
            constructorCode = constructorCode + indent + "public " + att_name + "(";
            // generate parameter list
            for(int i=0; i<paramNames.size(); i++){
                constructorCode = constructorCode + paramTypes.get(i) + " " + paramNames.get(i);
                if(i != paramNames.size() -1){
                    // not the last parameter
                    constructorCode += ", ";
                }
            }
            
            constructorCode = constructorCode + ") {\n"
                              + indent + indent + "create(";
            
            // generate parameter names
            for(int i=0; i<paramNames.size(); i++){
                constructorCode = constructorCode + paramNames.get(i);
                if(i != paramNames.size() -1){
                    // not the last parameter
                    constructorCode += ", ";
                }
            }
            
            constructorCode = constructorCode + ");\n"
                            + indent + indent + "enter_" + initStateName + "();\n"
                            + indent + "}\n\n";
                             
        }
        
        
        return constructorCode;
    }
    

    
    
    /**
     * Generate state functions
     * @return
     */
    private String genStateFunctions(){
        String stateCode = "";
        
        AbstractViewableMachineNode stateNode = null;
        for(Iterator i=this.childIterator(); i.hasNext();){
            stateNode = (AbstractViewableMachineNode)i.next();
            if(stateNode.getNodeType().equals("STATE")){
                stateCode = stateCode + ((StateNode)stateNode).genCode() + "\n";
            }
        }
        
        return stateCode;
    }
    
    
    private String genTransitionFunctions(){
        String transitionCode = "";

        AbstractViewableMachineNode transitionNode = null;
        for(Iterator i=this.childIterator(); i.hasNext();){
            transitionNode = (AbstractViewableMachineNode)i.next();
            if(transitionNode.getNodeType().equals("TRANSITION")){
                transitionCode = transitionCode + ((TransitionNode)transitionNode).genCode(att_name) + "\n";
            }
        }
        
        return transitionCode;
    }
    
    
    private String genOccurrence(){
        String occurCode = "";
        String indent = "    ";
        ArrayList<String> transNames = new ArrayList<String>();
        String transName = null;
        
        occurCode = indent + "public void occurrence (double time, Event evt) {\n";
        
        // initialize the transition name
        AbstractViewableMachineNode transitionNode = null;
        for(Iterator i=this.childIterator(); i.hasNext();){
            transitionNode = (AbstractViewableMachineNode)i.next();
            if(transitionNode.getNodeType().equals("TRANSITION")){
                transName = ((TransitionNode)transitionNode).getAttribute_name();
                transNames.add(transName);
            }
        }
        
        for(int i=0; i<transNames.size(); i++){
            occurCode = occurCode + indent + indent + "if(do_" + transNames.get(i) + "(evt,true))  return;\n";
        }
        
        occurCode = occurCode + indent + "}\n\n";
            
        return occurCode;
    }
}
