package ast;

import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

/**
 * This class implements a simple tree node that can be viewed in a JTree.
 * It should be extended to implement concrete nodes.
 * This is an implementation of the adapter design pattern
 *
 * @author Rob Esser
 * @version 1.2 2/3/2004
 */
public class AbstractViewableASTNode implements ASTNode, MutableTreeNode {

  /**
   * the encapsulated ASTNode
   */
  private ASTNode node = new AbstractASTNode();

// implementation of the ASTNode interface
// just defers all calls to the encapsulated ASTNode

  /**
   * @return an Iterator on all the children of this node
   */
  public Iterator childIterator() {
    return node.childIterator();
  }

  /**
   * Adds a child to the list of children
   * This method also sets parent of child
   * - postcondition the number of children will increase by one
   *
   * @param child the ASTNode to insert as a child
   */
  public void addChild(ASTNode child) {
    node.addChild(child);
    child.newParent(this);
  }

  /**
   * Removes the child from the list of children
   * This method also sets parent of child to be null
   * - postcondition the number of children will deccrease by one if node is a child
   *   or throws an exception
   *
   * @param childNode the node to remove from the list children
   * @throws ASTNodeNotFoundException if the child is not a child of this node
   */
  public void removeChild(ASTNode childNode) throws ASTNodeNotFoundException {
    node.removeChild(childNode);
  }

  /**
   * @returns the number of children
   */
  public int numChildren() {
    return node.numChildren();
  }

  /**
   * @returns the parent node or null if this node is a root node
   */
  public ASTNode currentParent() {
    return node.currentParent();
  }

  /**
   * sets the parent of this node
   *
   * @param parent the new parent node
   */
  public void newParent(ASTNode parent) {
    node.newParent(parent);
  }

  /**
   * @returns information on what the node represents
   */
  public String toString() {
    return super.toString();
  }

// MutableTreeNode interface implementation

  /**
   * Adds <code>child</code> to the receiver at <code>index</code>.
   * <code>child</code> will be messaged with <code>setParent</code>.
   *
   * @param child MutableTreeNode
   * @param index int
   */
  public void insert(MutableTreeNode child, int index) {
    List tempList = new ArrayList();
//copy children into tempory data structure
    for (Iterator i = childIterator(); i.hasNext(); ) {
      tempList.add(i.next());
    }
//remove all children
    for (Iterator i = tempList.iterator(); i.hasNext(); ) {
      try {
        removeChild((ASTNode) i.next());
      } catch (ASTNodeNotFoundException e) {
        System.err.println(e);
      }
    }
//insert child at appropriate spot
    tempList.add(index, child);
//add all children
    for (Iterator i = tempList.iterator(); i.hasNext(); ) {
      addChild((ASTNode) i.next());
    }
  }

  /**
   * Removes the child at <code>index</code> from the receiver.
   *
   * @param index int
   */
  public void remove(int index) {
    List tempList = new ArrayList();
//copy children into tempory data structure
    for (Iterator i = childIterator(); i.hasNext(); ) {
      tempList.add(i.next());
    }
//remove all children
    for (Iterator i = tempList.iterator(); i.hasNext(); ) {
      try {
        removeChild((ASTNode) i.next());
      } catch (ASTNodeNotFoundException e) {
        System.err.println(e);
      }
    }
//remove child at appropriate spot
    tempList.remove(index);
//add all children
    for (Iterator i = tempList.iterator(); i.hasNext(); ) {
      addChild((ASTNode) i.next());
    }
  }

  /**
   * Removes <code>node</code> from the receiver. <code>setParent</code>
   * will be messaged on <code>node</code>.
   *
   * @param node MutableTreeNode
   */
  public void remove(MutableTreeNode node) {
    try {
      removeChild((ASTNode) node);
    } catch (ASTNodeNotFoundException e) {
      System.err.println(e);
    }
  }

  /**
   * Resets the user object of the receiver to <code>object</code>.
   * In this implementation we do not use the user object
   *
   * @param object Object
   */
  public void setUserObject(Object object) {
//do nothing
  }

  /**
   * Removes the receiver from its parent.
   */
  public void removeFromParent() {
    newParent(null);
  }

  /**
   * Sets the parent of the receiver to <code>newParent</code>.
   * @param newParent MutableTreeNode
   */
  public void setParent(MutableTreeNode newParent) {
    newParent((ASTNode) newParent);
  }

  /**
   * Returns the child <code>TreeNode</code> at index
   * <code>childIndex</code>.
   *
   * @param childIndex int
   * @return TreeNode
   */
  public TreeNode getChildAt(int childIndex) {
    int index = 0;
    for (Iterator i = childIterator(); i.hasNext(); ) {
      Object n = i.next();
      if (childIndex == index) {
        return (TreeNode) n;
      }
      ;
      index++;
    }
    return null;
  }

  /**
   * Returns the number of children <code>TreeNode</code>s the receiver
   * contains.
   *
   * @return int
   */
  public int getChildCount() {
    return numChildren();
  }

  /**
   * Returns the parent <code>TreeNode</code> of the receiver.
   *
   * @return TreeNode
   */
  public TreeNode getParent() {
    return (TreeNode) currentParent();
  }

  /**
   * Returns the index of <code>node</code> in the receivers children.
   * If the receiver does not contain <code>node</code>, -1 will be
   * returned.
   *
   * @param node TreeNode
   * @return int
   */
  public int getIndex(TreeNode node) {
    int index = 0;
    for (Iterator i = childIterator(); i.hasNext(); ) {
      if (node.equals(i.next())) {
        return index;
      }
      index++;
    }
    return -1;
  }

  /**
   * Returns true if the receiver allows children.
   *
   * @return boolean
   */
  public boolean getAllowsChildren() {
    return true;
  }

  /**
   * Returns true if the receiver is a leaf.
   *
   * @return boolean
   */
  public boolean isLeaf() {
    return node.numChildren() == 0;
  }

  /**
   * Returns the children of the reciever as an Enumeration.
   *
   * @return Enumeration
   */
  public Enumeration children() {
    return new ChildEnumeration();
  }

  class ChildEnumeration implements Enumeration {

    Iterator iterator = childIterator();

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return  <code>true</code> if and only if this enumeration object
     *           contains at least one more element to provide;
     *          <code>false</code> otherwise.
     */
    public boolean hasMoreElements() {
      return iterator.hasNext();
    }

    /**
     * Returns the next element of this enumeration if this enumeration
     * object has at least one more element to provide.
     *
     * @return     the next element of this enumeration.
     * @exception  NoSuchElementException  if no more elements exist.
     */
    public Object nextElement() {
      return iterator.next();
    }
  }

  /**
   * A method to display an AST.
   */
  public void display() {
    JFrame frame = new JFrame("Tree Visualization");
    JScrollPane treeScroller = new JScrollPane();
    JTree parseTreePane = new JTree(new DefaultTreeModel(this, false));
    treeScroller.getViewport().add(parseTreePane);
    frame.getContentPane().add(treeScroller);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setBounds(10, 10, 400, 400);
    frame.show();
  }

  /**
   * Build an example AST and print it out
   *
   * @param args String[]
   */
  public static void main(String[] args) {
    ASTNode n1, n2, n3, n4, n5, n6;
    AbstractViewableASTNode root = new AbstractViewableASTNode();

    System.out.println("\"\"");

    root.addChild(n1 = new AbstractViewableASTNode());
    root.addChild(n2 = new AbstractViewableASTNode());
    root.addChild(n3 = new AbstractViewableASTNode());
    n1.addChild(n4 = new AbstractViewableASTNode());
    n1.addChild(n5 = new AbstractViewableASTNode());
    n2.addChild(n6 = new AbstractViewableASTNode());
    n4.addChild(new AbstractViewableASTNode());
    root.display();
  }

}
