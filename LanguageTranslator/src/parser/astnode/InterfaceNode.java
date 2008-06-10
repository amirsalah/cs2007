package parser.astnode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class InterfaceNode extends AbstractViewableMachineNode {
    private String att_name = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<INTERFACE name=\"" + att_name + "\">");
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }
        
        indent(indentLevel);
        System.out.println("</INTERFACE>");
    }
    
    public String getNodeType(){
        return "INTERFACE";
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
            System.out.println("Error: INTERFACE attribute name has not been initialized");
            return null;
        }
    }
    
    /**
     * Generate a interface file
     */
    public void genCode(){
        String code = null;
        
        /* generate interface head */
        String parentName = null;
        AbstractViewableMachineNode parentNode = null;
        code = "interface " + att_name;
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            parentNode = (AbstractViewableMachineNode)i.next();
            if(parentNode.getNodeType().equals("PARENT")){
                parentName = ((ParentNode)parentNode).getAttribute_name();
                break;
            }
        }
        
        // extends from parent (Process)
        if(parentName != null){
            code = code + " extends " + parentName + " {\n"; 
        }else{
            code += " {\n";
        }
        
        /* generate methods defined in this interface */
        AbstractViewableMachineNode methodNode = null;
        String methodStr = null; // store a method
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            methodNode = (AbstractViewableMachineNode)i.next();
            if( methodNode.getNodeType().equals("METHOD") ){
                code += "    ";
                methodStr = ((MethodNode)methodNode).genCode();
                code += methodStr;
                code += "\n";
            }
        }
        
        /* generate the } symbol */
        code += "}\n";
        
        /* write the generated code to a file */
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("output/" + att_name + ".java"));
            out.write(code);
            out.close();
        } catch (IOException e) {
            System.err.println("Cannot write to the file: " + att_name);
        }

        
    }
}
