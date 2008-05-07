package parser.astnode;

public class GuardNode extends AbstractViewableMachineNode {
    private String pcdata = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<GUARD>" + pcdata + "</GUARD>");
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
            System.out.println("Error: GUARD pcdata has not been initialized");
            return null;
        }
    }
}
