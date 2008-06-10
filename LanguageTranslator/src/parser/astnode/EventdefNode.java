package parser.astnode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class EventdefNode extends AbstractViewableMachineNode {
    private String att_name = null;
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<EVENTDEF name=\"" + att_name + "\">");
        
        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }
        indent(indentLevel);
        System.out.println("</EVENTDEF>");
    }
    
    public String getNodeType(){
        return "EVENTDEF";
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
            System.out.println("Error: EVENTDEF attribute name has not been initialized");
            return null;
        }
    }
    
    /**
     * Generate event files for different machines
     * @param machineName the machine which this event belongs to
     */
    public void genCode(String machineName){
        String code = null;
        String className = machineName + "_" + att_name;
        code = "class " + className + " implements Event {\n";
        
        code = code + "    public Object getKind() {\n"
                    + "        return this.getClass().getName();\n"
                    + "    }\n"
                    + "}\n";
                    
        //TODO event parameters
        
        /* write the code to a event definition file */
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("output/" + className + ".java"));
            out.write(code);
            out.close();
        } catch (IOException e) {
            System.err.println("Cannot write to the file: " + className);
        }
    }
    
    public String genEvent(String machineName){
        String code = null;
        String indent = "    ";
        
        code = indent + "public static Event " + att_name +"() {\n"
                      + indent + indent + "return new " + machineName + "_" + att_name + "();\n"
                      + indent + "}\n";
        
        
        return code;
    }
}
