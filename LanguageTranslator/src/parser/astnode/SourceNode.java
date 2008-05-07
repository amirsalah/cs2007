package parser.astnode;

public class SourceNode extends AbstractViewableMachineNode {
    private String att_state = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<SOURCE state=\"" + att_state + "\"/>");
    }
    
    public void setAttribute_state(String state){
        this.att_state = state;
    }
    
    /**
     * Get the state attribute
     * @return
     */
    public String getAttribute_state(){
        if(att_state != null){
            return att_state;
        }else{
            System.out.println("Error: SOURCE attribute state has not been initialized");
            return null;
        }
    }
}
