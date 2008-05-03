package ast;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * This class implements a very simple tree node suitable for ASTs.
 * It should be extended to implement concrete nodes
 *
 * @author Rob Esser
 * @version 1.0 20/2/2003
 */
public class AbstractASTNode implements ASTNode {

  /**
   * children are stored as a list
   */
  private List children = new ArrayList();

  /**
   * the parent node
   */
  private ASTNode parent = null;

  /**
   * @return an Iterator on all the children of this node
   */
  public Iterator childIterator() {
    return children.iterator();
  }

  /**
   * Adds a child to the list of children
   * This method also sets parent of child
   * - postcondition the number of children will increase by one
   *
   * @param child the ASTNode to insert as a child
   */
  public void addChild(ASTNode child) {
    children.add(child);
    child.newParent(this);
  }

  /**
   * Removes the child from the list of children
   * This method also sets parent of child to be null
   * - postcondition the number of children will deccrease by one if node is a child
   *   or throws an exception
   *
   * @param node the node to remove from the list children
   * @throws ASTNodeNotFoundException if the child is not a child of this node
   */
  public void removeChild(ASTNode node) throws ASTNodeNotFoundException {
    boolean present = children.remove(node);
    if (!present) {
      throw new ASTNodeNotFoundException(
          "attempt to remove a non existent child");
    }
  }

  /**
   * @returns the number of children
   */
  public int numChildren() {
    return children.size();
  }

  /**
   * @returns the parent node or null if this node is a root node
   */
  public ASTNode currentParent() {
    return parent;
  }

  /**
   * sets the parent of this node
   *
   * @param parent the new parent node
   */
  public void newParent(ASTNode parent) {
    this.parent = parent;
  }

  /**
   * @returns information on what the node represents
   */
  public String toString() {
    String s = super.toString() + "\n";
    for (Iterator i = childIterator(); i.hasNext(); ) {
      s = s + i.next().toString();
    }
    return s;
  }

  /**
   * Build an example AST and print it out
   * @param args String[] passed to the main method from the vm
   */
  public static void main(String[] args) {
    ASTNode n1, n2, n3, n4, n5, n6;
    ASTNode root = new AbstractASTNode();

    root.addChild(n1 = new AbstractASTNode());
    root.addChild(n2 = new AbstractASTNode());
    root.addChild(n3 = new AbstractASTNode());
    n1.addChild(n4 = new AbstractASTNode());
    n1.addChild(n5 = new AbstractASTNode());
    n2.addChild(n6 = new AbstractASTNode());
    n4.addChild(new AbstractASTNode());
    System.out.println(root.toString());
  }
}
