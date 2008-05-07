package parser.astnode;

public class OutgoingNode extends AbstractViewableMachineNode {
    private String att_trans = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<OUTGOING trans=\"" + att_trans + "\"/>");
    }
    
    public void setAttribute_trans(String trans){
        this.att_trans = trans;
    }
    
    /**
     * Get the trans attribute
     * @return
     */
    public String getAttribute_trans(){
        if(att_trans != null){
            return att_trans;
        }else{
            System.out.println("Error: OUTGOING attribute trans has not been initialized");
            return null;
        }
    }
}
