package TREENODE;

import java.util.Iterator;

public class TREENODE_MACHINE extends AbstractViewableMachineNode {
    private String att_name = null;
    private String att_extends = null;

    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<MACHINE name=\"" + att_name + "\" extends=\"" + att_extends + "\">");

        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }

        indent(indentLevel);
        System.out.println("</MACHINE>");
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
}
