package ast;

import java.util.Iterator;

/**
 * This interface defines the methods that all
 * abstract syntax nodes MUST implement.
 *
 * It is assumed that children are defined
 * by implementing the java.util.List interface
 *
 * No other methods can be used
 *
 * @author Rob Esser
 * @version 1.0 20/2/2003
 */
public interface ASTNode {

  /**
   * @return an Iterator on all the children of this node
   */
  public Iterator childIterator();

  /**
   * Adds a child to the list of children
   * This method also sets parent of child
   * - postcondition the number of children will increase by one
   *
   * @param child the cell to insert as a child
   */
  public void addChild(ASTNode child);

  /**
   * Removes the child from the list of children
   * This method also sets parent of child to be null
   * - postcondition the number of children will deccrease by one if node is a child
   *   or throws an exception
   *
   * @param node the node to remove from the list children
   * @throws ASTNodeNotFoundException if the child is not a child of this node
   */
  public void removeChild(ASTNode node) throws ASTNodeNotFoundException;

  /**
   * @returns the number of children
   */
  public int numChildren();

  /**
   * @returns the parent node or null if this node is a root node
   */
  public ASTNode currentParent();

  /**
   * sets the parent of this node
   *
   * @param parent the new parent node
   */
  public void newParent(ASTNode parent);

  /**
   * @returns information on what the node represents
   */
  public String toString();

}
