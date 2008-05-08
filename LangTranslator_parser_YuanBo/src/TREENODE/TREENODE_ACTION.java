package TREENODE;

public class TREENODE_ACTION extends AbstractViewableMachineNode {
    private String pcdata = null;

    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<ACTION>" + pcdata + "</ACTION>");
    }

    public void setPCDATA(String pcdata){
        this.pcdata = pcdata;
    }

    /**
     * Get the pcdata of the this action
     * @return
     */
    public String getPCDATA(){
        if(pcdata != null){
            return pcdata;
        }else{
            System.out.println("Error: ACTION pcdata has not been initialized");
            return null;
        }
    }
}
