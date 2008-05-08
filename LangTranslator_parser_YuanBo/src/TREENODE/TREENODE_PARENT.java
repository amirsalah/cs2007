package TREENODE;

public class TREENODE_PARENT extends AbstractViewableMachineNode {
    private String att_name = null;

    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<PARENT name=\"" + att_name + "\"/>");
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
            System.out.println("Error: parent attribute name has not been initialized");
            return null;
        }
    }
}
