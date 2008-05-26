package parser.astnode;

public class ParameterNode extends AbstractViewableMachineNode {
    private String att_name = null;
    private String att_type = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<PARAMETER name=\"" + att_name + "\" type=\"" + att_type + "\"/>");
    }
    
    public String getNodeType(){
        return "PARAMETER";
    }
    
    
    public void setAttribute_type(String type){
        this.att_type = type;
    }
    
    /**
     * Get the type of the interface
     * @return
     */
    public String getAttribute_type(){
        if(att_type != null){
            return att_type;
        }else{
            System.out.println("Error: PARAMETER attribute type has not been initialized");
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
            System.out.println("Error: PARAMETER attribute name has not been initialized");
            return null;
        }
    }
}
