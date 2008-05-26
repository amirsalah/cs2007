package semantics;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import parser.astnode.AbstractViewableMachineNode;
import parser.astnode.ArgumentNode;
import parser.astnode.ClassNode;
import parser.astnode.InstanceNode;
import parser.astnode.InterfaceNode;
import parser.astnode.MachineNode;
import parser.astnode.MethodNode;
import parser.astnode.ParameterNode;
import parser.astnode.ParentNode;
import parser.astnode.ResultNode;

public class XMLSemantics {
    AbstractViewableMachineNode rootNode = null;
    private Vector<String> allJavaTypes = new Vector<String>();
    private Vector<String> interfaceNames = new Vector<String>();
    // class name -> class implements
    private HashMap<String, String> mapClass = new HashMap<String, String>();
    // machine name -> machine extends
    private HashMap<String, String> mapMachine = new HashMap<String, String>();
    // instance name -> instance kind
    private HashMap<String, String> mapInstance = new HashMap<String, String>();
    // interface name -> interface parent name
    private HashMap<String, String> mapInterfaceParent = new HashMap<String, String>();
    // method, result type, parameter types (1 method ONLY)
    private Vector<String> interfaceMethod = new Vector<String>();
    private Vector<String> methodParameterNames = new Vector<String>();
    // all instances
    private Vector<String> instances = new Vector<String>();
    
    private int numInterface = 0, numClass = 0, numMachine = 0, numInstance = 0;
    
    public XMLSemantics(AbstractViewableMachineNode rootNode){
        this.rootNode = rootNode;
    }
    
    public void semanticChecking(){
        boolean validSemantics;
        initValidTypes();
        initSymbolTables();
        /* Check names */
        validSemantics = checkNames();
        validSemantics = checkInterfaceExistence();
        validSemantics = checkClassExistence();
        validSemantics = checkInstanceExistence();
        validSemantics = checkDuplicatedEntries();
        validSemantics = checkInterfaceParent();
        checkInterfaceMethods(); // check the java data types
        checkStateMachines();
        checkInstances();
    }
    
    public void semanticWarning(String msg){
        System.out.println("Semantic warning: " + msg);
    }
    
    /******* Utility functions *******/
    /**
     * Check if the name is a valid java identifier name
     * @param name
     * @return
     */
    private boolean isJavaIdentifier(String name){
        if( !Character.isJavaIdentifierStart(name.charAt(0)) ){
            return false;
        }
        
        for(int i=1; i<name.length(); i++){
            if( Character.isJavaIdentifierPart(name.charAt(i)) ){
                continue;
            }else{
                return false;
            }
        }
        
        return true;
    }
    
    /* initialization of the valid Java types */
    private void initValidTypes(){
        /* add java primitive types */
        allJavaTypes.add("void");
        allJavaTypes.add("int");
        allJavaTypes.add("float");
        allJavaTypes.add("double");
        allJavaTypes.add("boolean");
        allJavaTypes.add("short");
        allJavaTypes.add("long");
        allJavaTypes.add("char");
        allJavaTypes.add("byte");
        
        allJavaTypes.add("Event");
        
        /* add declared java classes & interfaces */
        Iterator i;
        AbstractViewableMachineNode node;
        for(i=rootNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            if(node.getNodeType().equals("INTERFACE")){
                allJavaTypes.add( ((InterfaceNode)node).getAttribute_name() );
            }
            
            if(node.getNodeType().equals("CLASS")){
                allJavaTypes.add( ((ClassNode)node).getAttribute_name() );
            }
            
            if(node.getNodeType().equals("MACHINE")){
                allJavaTypes.add( ((MachineNode)node).getAttribute_name() );
            }
        }
        
    }
    
    /**
     * Check the given type (Java primitive type or classes) is valid or not
     * @param type the type to be checked
     * @return true if the type is valid
     */
    private boolean isValidType(String type){
        if(allJavaTypes.contains(type)){
            return true;
        }else{
            semanticWarning("invalid type > " + type);
            return false;
        }
    }
    
    /**
     * find the first duplicated string in the strs vector
     * @param strs the strings to be checked
     * @return the first duplicated string encountered, or null if no duplicate
     */
    private String firstDuplicate(Vector<String> strs){
        String result = null;
        
        HashSet<String> set = new HashSet<String>();
        
        for (int i = 0; i < strs.size(); i++) {
            boolean val = set.add(strs.get(i));
            if (val == false) {
                return strs.get(i);
            }
        }
        
        return result;
    }

    /******* Initialization *******/
    /**
     * Initialization of symbol tables
     */
    public void initSymbolTables(){
        Iterator i;
        AbstractViewableMachineNode node;
        for(i=rootNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            /* Initialize interface names */
            if(node.getNodeType().equals("INTERFACE")){
                interfaceNames.add( ((InterfaceNode)node).getAttribute_name() );
                initInterfaceParent((InterfaceNode)node);
                initInterfaceMethod((InterfaceNode)node);
            }
            
            /* Initialize class name, implementation */
            if(node.getNodeType().equals("CLASS")){
                mapClass.put( ((ClassNode)node).getAttribute_name(), ((ClassNode)node).getAttribute_implements() );
                numClass++;
            }
            
            /* Initialize machine name, extends */
            if(node.getNodeType().equals("MACHINE")){
                mapMachine.put(  ((MachineNode)node).getAttribute_name(), ((MachineNode)node).getAttribute_extends() );
                numMachine++;
            }
            
            /* Initialize instance name, kind */
            if(node.getNodeType().equals("INSTANCE")){
                mapInstance.put( ((InstanceNode)node).getAttribute_name(), ((InstanceNode)node).getAttribute_kind() );
                numInstance++;
            }
            
        }
           
    }
    
    /**
     * Initialize the symbol table for interface -> its parent
     * @param node a interface node from the AST tree
     */
    private void initInterfaceParent(InterfaceNode node){
        AbstractViewableMachineNode pNode = (AbstractViewableMachineNode)node.getChildAt(0);
        if(pNode.getNodeType().equals("PARENT")){
            mapInterfaceParent.put(node.getAttribute_name(), ((ParentNode)pNode).getAttribute_name());
        }
    }
    
    
    private void initInterfaceMethod(InterfaceNode node){
        Iterator i;
        AbstractViewableMachineNode mNode = (AbstractViewableMachineNode)node.getChildAt(0);
        
        for(i=node.childIterator(); i.hasNext(); ){
            mNode = (AbstractViewableMachineNode)i.next();
            // Initialize a method
            if(mNode.getNodeType().equals("METHOD")){
                
                // add the method name
                interfaceMethod.add( ((MethodNode)mNode).getAttribute_name() );
                // add the method's return type
                ResultNode rNode = (ResultNode)mNode.getChildAt(0);
                interfaceMethod.add(rNode.getAttribute_type());
                
                // add the method's parameter types
                Iterator j;
                AbstractViewableMachineNode paramNode;
                for(j=mNode.childIterator(); j.hasNext(); ){
                    paramNode = (AbstractViewableMachineNode)j.next();
                    if(paramNode.getNodeType().equals("PARAMETER")){
                        interfaceMethod.add( ((ParameterNode)paramNode).getAttribute_type() );
                        methodParameterNames.add(((ParameterNode)paramNode).getAttribute_name());
                    }
                }
                
                checkInterfaceMethod();
                interfaceMethod.clear();
                methodParameterNames.clear();
            }
        }
    }
    
    
    /* Check semantics */
    /**
     * Check if the class names are all valid java identifier
     * @return true if no error was found, false if any error occcured
     */
    private boolean checkNames(){
        Iterator i;
        String name;
        boolean noError = true;
        
        for(i=interfaceNames.iterator(); i.hasNext(); ){
            name = (String)i.next();
            if( !isJavaIdentifier(name) ){
                semanticWarning("invalid interface name > " + name);
                noError = false;
            }
        }
        
        /* Check class names */
        for(i=mapClass.keySet().iterator(); i.hasNext(); ){
            name = (String)i.next();
            if( !isJavaIdentifier(name) ){
                semanticWarning("invalid class name > " + name);
                noError = false;
            }
        }
        
        /* Check class implements {NOT necessary, since the names already checked} */
        /*
        for(i=mapClass.values().iterator(); i.hasNext(); ){
            name = (String)i.next();
            if( !isJavaIdentifier(name) ){
                semanticWarning("invalid implement name > " + name);
                noError = false;
            }
        }
        */
        
        /* Check machine name */
        for(i=mapMachine.keySet().iterator(); i.hasNext(); ){
            name = (String)i.next();
            if( !isJavaIdentifier(name) ){
                semanticWarning("invalid machine name > " + name);
                noError = false;
            }
        }
        
        /* Check instance name */
        for(i=mapInstance.keySet().iterator(); i.hasNext(); ){
            name = (String)i.next();
            if( !isJavaIdentifier(name) ){
                semanticWarning("invalid instance name > " + name);
                noError = false;
            }
        }
        
        return noError;
    }

    
    /**
     * Check if the interfaces classes implement exist
     * @return true if all interfaces are already defined, false otherwise
     */
    private boolean checkInterfaceExistence(){
        boolean noError = true;
        
        Iterator i;
        String interface_name;
        for(i=mapClass.values().iterator(); i.hasNext(); ){
            interface_name = (String)i.next();
            if( !interfaceNames.contains(interface_name) ){
                semanticWarning("non-exist interface > " + interface_name);
                noError = false;
            }
        }
        
        return noError;
    }
    
    /**
     * Check if the classes machines extend all exist
     * @return true if all classes extended are valid, false otherwise
     */
    private boolean checkClassExistence(){
        boolean noError = true;
        
        Iterator i;
        String class_name;
        Set<String> allClassNames = mapClass.keySet();
        
        for(i=mapMachine.values().iterator(); i.hasNext(); ){
            class_name = (String)i.next();
            if( !allClassNames.contains(class_name) ){
                semanticWarning("non-exist class > " + class_name);
                noError = false;
            }
        }
        
        return noError;
    }
    
    /**
     * Check if the instance kind are valid
     * @return
     */
    private boolean checkInstanceExistence(){
        boolean noError = true;
        
        Iterator i;
        String machine_name;
        Set<String> allClassNames = mapMachine.keySet();
        
        for(i=mapInstance.values().iterator(); i.hasNext(); ){
            machine_name = (String)i.next();
            if( !allClassNames.contains(machine_name) ){
                semanticWarning("non-exist machine > " + machine_name);
                noError = false;
            }
        }
        
        return noError;
    }
    
    /**
     * Check if there are duplicated classes, machines or instances
     * @return true if there is no duplicated entries.
     */
    private boolean checkDuplicatedEntries(){
        boolean noError = true;
        
        /* Check duplicated entries for Interfaces */
        
        /* Check classes */
        int numValidClasses = mapClass.keySet().size();
        if(numValidClasses != numClass){
            semanticWarning("duplicated class declaration occured");
            noError = false;
        }
        
        if(mapMachine.keySet().size() != numMachine){
            semanticWarning("duplicated machine declaration");
            noError = false;
        }
        
        if(mapInstance.keySet().size() != numInstance){
            semanticWarning("duplicated instance declaration");
            noError = false;
        }
        
        return noError;
    }
    
    /**
     * Check if the parent of given interface exist,
     * @return true if all the interfaces' parent exist
     */
    private boolean checkInterfaceParent(){
        boolean noError = true;
        Collection<String> allParents = mapInterfaceParent.values();
        Iterator i;
        String parentName;
        
        /* check duplicated interface and parent */
        for(i=allParents.iterator(); i.hasNext(); ){
            parentName = (String)i.next();
            if( !interfaceNames.contains(parentName) ){
                semanticWarning("parent interface doesn't exist > " + parentName);
                noError = false;
            }
        }
        
        return noError;
    }
    
    
    /**
     * Check every method: its return type, parameter types
     * TODO Check duplicated method names
     * Check parameters have unique names in this method
     * @return true if no error occurs
     */
    private boolean checkInterfaceMethod(){
        boolean noError = true;
        
        /* Check the result and parameter types */
        for(int i=1; i<interfaceMethod.size(); i++){
            if( !isValidType(interfaceMethod.get(i)) ){
                semanticWarning("wrong data type >" + interfaceMethod.get(i));
                noError = false;
            }
        }
        
        /* Check parameter names */
        String duplicatedParam = null;
        duplicatedParam = firstDuplicate(methodParameterNames);
        if(duplicatedParam != null){
            semanticWarning("duplicated parameters > " + duplicatedParam);
        }
        
        return noError;
    }
    
    /**
     * Check all the methods' result type, parameter types
     * @return
     */
    private void checkInterfaceMethods(){
     
        Iterator i;
        AbstractViewableMachineNode node;
        for(i=rootNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            if(node.getNodeType().equals("INTERFACE")){
                initInterfaceMethod((InterfaceNode)node);
                /*
                AbstractViewableMachineNode mNode;  // METHOD node
                
                Iterator itrMethods; // iterator for the methods under this interface
                for(itrMethods=node.childIterator(); itrMethods.hasNext(); ){
                    mNode = (AbstractViewableMachineNode)itrMethods.next();
                    if(mNode.getNodeType().equals("METHOD")){
                        
                    }
                } */
            }
            
        }
    }
    
    /**
     * Check the semantics of the state machines
     * @return
     */
    private boolean checkStateMachines(){
        boolean noError = true;
        Iterator i;
        AbstractViewableMachineNode node;
        
        for(i=rootNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            if(node.getNodeType().equals("MACHINE")){
                StateMachineSemantics sm = new StateMachineSemantics( (MachineNode)node );
                noError = sm.checkStateMachineSemantics();
            }
        }
        
        return noError;
 
    }
    
    /**
     * Check if all the arguments in the instance have been defined
     * @return
     */
    private boolean checkInstances(){
        boolean noError = true;
        Iterator i;
        AbstractViewableMachineNode node;
        
        /* Initialize all the instances, which may be used as arguments later */
        for(i=rootNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            if(node.getNodeType().equals("INSTANCE")){
                instances.add( ((InstanceNode)node).getAttribute_name() );
            }
        }
        
        for(i=rootNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            if(node.getNodeType().equals("INSTANCE")){
                noError = checkSingleInstance((InstanceNode)node);
            }
        }
        
        return noError;
    }
    
    /**
     * Check the arguments needed by this instance have already been defined 
     * @param iNode the instance node to be checked
     * @return true if all the arguments have been defined.
     */
    private boolean checkSingleInstance(InstanceNode iNode){
        boolean noError = true;
        Vector<String> arguments = new Vector<String>();
        
        /* Initialize the arguments list */
        Iterator itr;
        AbstractViewableMachineNode argNode;
        for(itr=iNode.childIterator(); itr.hasNext(); ){
            argNode = (AbstractViewableMachineNode)itr.next();
            
            if(argNode.getNodeType().equals("ARGUMENT")){
                arguments.add( ((ArgumentNode)argNode).getPCDATA().trim() );
            }
        }
        
        for(int i=0; i<arguments.size(); i++){
            if( !instances.contains(arguments.get(i)) ){
                semanticWarning("argument not defined > " + arguments.get(i) + " in instance " + iNode.getAttribute_name());
            }
        }
        
        return noError;
    }

}
