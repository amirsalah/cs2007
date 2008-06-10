package parser.astnode;

import java.util.Iterator;

public class TransitionNode extends AbstractViewableMachineNode {
    private String att_name = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<TRANSITION name=\"" + att_name + "\">");
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }
        
        indent(indentLevel);
        System.out.println("</TRANSITION>");
    }
    
    public String getNodeType(){
        return "TRANSITION";
    }
    
    public void setAttribute_name(String name){
        this.att_name = name;
    }
    
    /**
     * Get the attribute name
     * @return
     */
    public String getAttribute_name(){
        if(att_name != null){
            return att_name;
        }else{
            System.out.println("Error: TRANSITION attribute name has not been initialized");
            return null;
        }
    }
    
    
    /**
     * Generate code for the transition function
     * @param machineName the machine this transition belongs to
     * @return
     */
    public String genCode(String machineName){
        String code = "";
        String action = "";
        String indent = "    ";
        String sourceState = null, targetState = null, eventName = null;
        
        
        // obtain source , target states, event name and action
        AbstractViewableMachineNode childNode = null;
        for(Iterator i=this.childIterator(); i.hasNext();){
            childNode = (AbstractViewableMachineNode)i.next();
            if(childNode.getNodeType().equals("SOURCE")){
                sourceState = ((SourceNode)childNode).getAttribute_state();
            }
            
            if(childNode.getNodeType().equals("TARGET")){
                targetState = ((TargetNode)childNode).getAttribute_state();
            }
            
            if(childNode.getNodeType().equals("EVENT")){
                eventName = machineName + "_" + ((EventNode)childNode).getAttribute_name();
            }
            
            if(childNode.getNodeType().equals("ACTION")){
                action = ((ActionNode)childNode).getPCDATA();
                if(!action.equals("")){
                    action = indent + indent + indent + action + "\n";
                }
            }
        }

        
        code = indent + "boolean do_" + att_name + "(Event evt, boolean grd) {\n"
             + indent + indent + "if (theState == States." + sourceState + " && evt.getKind().equals(" + "\""
                               + eventName + "\") && true) {\n"
                               + action
             + indent + indent + indent + "enter_" + targetState + "();\n"
             + indent + indent + indent + "return true;\n"
             + indent + indent + "}\n"
             + indent +  indent + "return false;\n"
             + indent + "}\n";
        
        return code;
    }
    
}
