package parser.astnode;

import ast.AbstractViewableASTNode;

public abstract class AbstractViewableMachineNode 
    extends AbstractViewableASTNode implements MachineDTDNode{
    
    protected void indent(int indentLevel){
        for(int i=0; i<indentLevel; i++){
            System.out.print(" ");
        }
    }
}
