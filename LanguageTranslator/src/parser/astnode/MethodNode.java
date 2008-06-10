package parser.astnode;

import java.util.ArrayList;
import java.util.Iterator;

public class MethodNode extends AbstractViewableMachineNode {
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
    
    public String getNodeType(){
        return "METHOD";
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
    
    /**
     * Code generation for the method
     * @return the generated method (string)
     */
    public String genCode(){
        String code = null;
        String returnType = null;
        
        /* obtain return type */
        for(Iterator i=this.childIterator(); i.hasNext();){
            AbstractViewableMachineNode resultNode = (AbstractViewableMachineNode)i.next();
            if( resultNode.getNodeType().equals("RESULT") ){
                returnType = ((ResultNode)resultNode).getAttribute_type();
                break;
            }
        }
        
        /* obtain parameter lists */
        ArrayList<String> parameterNames = new ArrayList<String>(); // Store all the parameter names
        ArrayList<String> parameterTypes = new ArrayList<String>(); // Store corresponding parameter types
        String paramName = null; // A single parameter name
        String paramType = null;
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            AbstractViewableMachineNode paramNode = (AbstractViewableMachineNode)i.next();
            if( paramNode.getNodeType().equals("PARAMETER") ){
                paramName = ((ParameterNode)paramNode).getAttribute_name();
                paramType = ((ParameterNode)paramNode).getAttribute_type();
                parameterNames.add(paramName);
                parameterTypes.add(paramType);
            }
        }
        
        /* generate code of this method */
        code = returnType + " " + att_name + "(";
        for(int i=0; i<parameterNames.size(); i++){
            code = code + parameterTypes.get(i) + " " + parameterNames.get(i);
            if(i != parameterNames.size() - 1){
                // not the last parameter
                code += ", ";
            }
        }
        
        code += ");";
        
        return code;
    }
}