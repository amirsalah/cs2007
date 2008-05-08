package TREENODE;

import java.util.Iterator;

public class TREENODE_METHOD extends AbstractViewableMachineNode {
    private String att_name = null;

    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<METHOD name=\"" + att_name + "\">");

        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }

        indent(indentLevel);
        System.out.println("</METHOD>");
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
            System.out.println("Error: interface name has not been initialized");
            return null;
        }
    }
}
