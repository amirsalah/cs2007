package parser.astnode;

import java.util.ArrayList;
import java.util.Iterator;

public class StateNode extends AbstractViewableMachineNode {
    private String att_name = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<STATE name=\"" + att_name + "\">");
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }
        
        indent(indentLevel);
        System.out.println("</STATE>");
    }
    
    public String getNodeType(){
        return "STATE";
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
            System.out.println("Error: STATE attribute name has not been initialized");
            return null;
        }
    }
    
    /**
     * Generate code for entering this state
     * @return
     */
    public String genCode(){
        String code = null;
        ArrayList<String> actions = new ArrayList<String>();
        String action = null;
        String indent = "    ";
        
        /* obtain action statement */
        AbstractViewableMachineNode actionNode = null;
        for(Iterator i=this.childIterator(); i.hasNext();){
            actionNode = (AbstractViewableMachineNode)i.next();
            if(actionNode.getNodeType().equals("ACTION")){
                action = ((ActionNode)actionNode).getPCDATA();
                actions.add(action);
            }
        }
        
        
        code = indent + "void enter_" + att_name + "() {\n";
        
        // there may be more than one action
        for(int i=0; i<actions.size(); i++){
            code = code + indent + indent + actions.get(i) + "\n";
        }
             
        code = code
             + indent + indent + "theState = States." + att_name + ";\n"
             + indent + "}\n";
        
        return code;
    }
    
}
