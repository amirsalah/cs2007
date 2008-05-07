package parser.astnode;

import ast.ASTNode;

public interface MachineDTDNode extends ASTNode {
    // Pretty print the node
    public void prettyPrint(int indentLevel);
    
    // Get the current symbol for this node
//    public int getSymbol();
    
    // Get the node value
//    public String getNodeValue();
}
