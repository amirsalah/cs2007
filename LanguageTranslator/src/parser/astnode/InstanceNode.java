package parser.astnode;

import java.util.Iterator;

public class InstanceNode extends AbstractViewableMachineNode {
    private String att_name = null;
    private String att_kind = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<INSTANCE name=\"" + att_name + "\" kind=\"" + att_kind + "\">");
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }
        
        indent(indentLevel);
        System.out.print("</INSTANCE>");
    }
    
    public void setAttribute_kind(String att_kind){
        this.att_kind = att_kind;
    }
    
    /**
     * Get the kind attribute
     * @return
     */
    public String getAttribute_kind(){
        if(att_kind != null){
            return att_kind;
        }else{
            System.out.println("Error: INSTANCE attribute kind has not been initialized");
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
            System.out.println("Error: INSTANCE attribute name has not been initialized");
            return null;
        }
    }
}
