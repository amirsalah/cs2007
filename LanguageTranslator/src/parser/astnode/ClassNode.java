package parser.astnode;

public class ClassNode extends AbstractViewableMachineNode {
    private String att_name = null;
    private String att_implements = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<CLASS name=\"" + att_name + "\" implements=\"" + att_implements + "\"/>");
    }
    
    public String getNodeType(){
        return "CLASS";
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
    
    public void setAttribute_implements(String impl){
        att_implements = impl;
    }
    
    public String getAttribute_implements(){
        return att_implements;
    }
}
