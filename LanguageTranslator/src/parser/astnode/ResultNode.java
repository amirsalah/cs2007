package parser.astnode;

import java.util.Iterator;

public class ResultNode extends AbstractViewableMachineNode {
    private String att_type = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<RESULT type=\"" + att_type + "\"/>");
    }
    
    public void setAttribute_type(String type){
        this.att_type = type;
    }
    
    /**
     * Get the type of the interface
     * @return
     */
    public String getAttribute_type(){
        if(att_type != null){
            return att_type;
        }else{
            System.out.println("Error: RESULT attribute type has not been initialized");
            return null;
        }
    }
}
