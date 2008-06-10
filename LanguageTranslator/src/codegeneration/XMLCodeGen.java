/**
 * Code generation for state machines
 */
package codegeneration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import parser.astnode.AbstractViewableMachineNode;
import parser.astnode.ClassNode;
import parser.astnode.InstanceNode;
import parser.astnode.InterfaceNode;
import parser.astnode.MachineNode;
import parser.astnode.MethodNode;

public class XMLCodeGen {
    AbstractViewableMachineNode rootNode = null;
    HashMap<String, String> classNames = new HashMap<String, String>();

    boolean hasCreate = false;
    
    /* Constructor */
    public XMLCodeGen(AbstractViewableMachineNode rootNode){
        this.rootNode = rootNode;
    }
    
    
    public void genCode(){
        AbstractViewableMachineNode childNode = null;
        

        for(Iterator i=rootNode.childIterator(); i.hasNext();){
            childNode = (AbstractViewableMachineNode)i.next();
            /* generate interface files */
            if( childNode.getNodeType().equals("INTERFACE") ){
                // do NOT generate Process and Schedule interface
                InterfaceNode interfaceNode = (InterfaceNode)childNode;
                if(interfaceNode.getAttribute_name().equals("Process") || interfaceNode.getAttribute_name().equals("Scheduler")){
                    continue;
                }
                interfaceNode.genCode();   
            }
            
            /* register class names mapped to its interface */
            if( childNode.getNodeType().equals("CLASS") ){
                String className = null;
                String classImpl = null;
                ClassNode classNode = (ClassNode)childNode;
                
                className = classNode.getAttribute_name();
                classImpl = classNode.getAttribute_implements();
                
                classNames.put(className, classImpl);
            }
            
            /* generate machine code */
            if( childNode.getNodeType().equals("MACHINE") ){
                // do NOT generate Process and Schedule interface
                MachineNode mNode = (MachineNode)childNode;
                MethodNode methodNode = checkMachineCreate(mNode);
                mNode.genCode(methodNode); 
            }
            
            /* generate tester code */
            genTester();
        }
        
        /* generate event interface */
        String eventCode = null;
        eventCode = "interface Event {\n"
                   +"    Object getKind();\n"
                   +"}\n";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("output/Event.java"));
            out.write(eventCode);
            out.close();
        } catch (IOException e) {
            System.err.println("failed to write to the file: Event.java");
        }
    }
    
    /**
     * Check if this machine has create method in the interface
     * @param mNode the machine to be checked
     * @return true if has create()
     */
    private MethodNode checkMachineCreate(MachineNode mNode){
        String className = mNode.getAttribute_extends();
        String interfaceName = classNames.get(className);
        String methodName = null;
        AbstractViewableMachineNode childNode = null, methodNode = null;

        for(Iterator i=rootNode.childIterator(); i.hasNext();){
            childNode = (AbstractViewableMachineNode)i.next();
            /* generate interface files */
            if( childNode.getNodeType().equals("INTERFACE") ){
                // do NOT generate Process and Schedule interface
                InterfaceNode interfaceNode = (InterfaceNode)childNode;
                if(interfaceNode.getAttribute_name().equals(interfaceName)){
                    
                    for(Iterator j=interfaceNode.childIterator(); j.hasNext(); ){
                        methodNode = (AbstractViewableMachineNode)(j.next());
                        // check if this method has create
                        if( methodNode.getNodeType().equals("METHOD") ){
                            if( ((MethodNode)methodNode).getAttribute_name().equals("create") ){
                                hasCreate = true;
                                return (MethodNode)methodNode;
                            }
                        }
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * Generate tester class file
     */
    private void genTester(){
        String code = null;
        String indent = "    ";
        
        code = "class Tester {\n"
             + indent + "public static void main (String args [])  {\n";
        
        // generate instances
        AbstractViewableMachineNode childNode = null;
        for(Iterator i=rootNode.childIterator(); i.hasNext();){
            childNode = (AbstractViewableMachineNode)i.next();
            if(childNode.getNodeType().equals("INSTANCE")){
                code += ((InstanceNode)childNode).genCode();
            }
        }
        
        code = code + "\n";
        // start this finite state machine
        code = code + indent + indent + "boolean done = false;\n"
                + indent + indent + "while (!done) {\n"
                + indent + indent + indent + "done = !Scheduler.theScheduler.handleFirst ();\n"
                + indent + indent + "}\n"
                + indent + "}\n"
                + "}\n";
        
        /* write the generated code to a file */
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("output/Tester.java"));
            out.write(code);
            out.close();
        } catch (IOException e) {
            System.err.println("Cannot write to the file: output/Tester.java");
        }
    }

}
