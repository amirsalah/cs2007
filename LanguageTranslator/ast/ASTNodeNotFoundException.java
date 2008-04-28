package ast;

/**
 * An instance of this class represents an exception
 *
 * @author Rob Esser
 * @version 1.1 2/3/2004
 */
public class ASTNodeNotFoundException extends Exception {

  /**
   * Constructs an <code>ASTNodeNotFoundException</code> with the specified detail message.
   *
   * @param   s   the detail message.
   */
  public ASTNodeNotFoundException(String s) {
    super(s);
  }
}
