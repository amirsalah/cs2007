package TREENODE;

public class TREENODE_ARGUMENT extends AbstractViewableMachineNode {
    private String pcdata = null;

    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<ARGUMENT>" + pcdata + "</ARGUMENT>");
    }

    public void setPCDATA(String pcdata){
        this.pcdata = pcdata;
    }

    /**
     * Get the pcdata
     * @return
     */
    public String getPCDATA(){
        if(pcdata != null){
            return pcdata;
        }else{
            System.out.println("Error: ARGUMENT pcdata has not been initialized");
            return null;
        }
    }
}
