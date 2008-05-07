package parser.astnode;

import java.util.Iterator;

public class SystemSpecNode extends AbstractViewableMachineNode {
    
    public void prettyPrint(int indentLevel){
        indent(indentLevel);
        System.out.println("<SYSTEM-SPEC>");
        // call each child's prettyPrint() method
        for(Iterator i=this.childIterator(); i.hasNext();){
            ((MachineDTDNode)i.next()).prettyPrint(indentLevel+2);
        }
        
        System.out.println("</SYSTEM-SPEC>");
    }
    
}
