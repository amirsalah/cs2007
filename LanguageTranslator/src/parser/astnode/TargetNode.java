package parser.astnode;

public class TargetNode extends AbstractViewableMachineNode {
    private String att_state = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<TARGET state=\"" + att_state + "\"/>");
    }
    
    public String getNodeType(){
        return "TARGET";
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
            System.out.println("Error: TARGET attribute state has not been initialized");
            return null;
        }
    }
}
