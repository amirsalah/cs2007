import java.awt.Graphics2D;
import java.awt.Shape;

public interface FsaRenderer
{
    //Draw an initial transistion on state s
    //Returns the Shape that was drawn
    public Shape drawInitialTransition(Graphics2D gra2d, State s, 
      boolean isSelected);

    //Draw a state s
    //isSelected will be true if the state is currently selected
    //isCurrent will be true if the state is a current state
    //Returns the Shape that was drawn
    public Shape drawState(Graphics2D gra2d, State s, boolean isSelected, 
      boolean isCurrent);
   
    //Draw a a transition t
    //Since there can be multiple transistions form one state to another,
    //the multip[licity parameter indicates if this is the 1st, 2nd, 3rd, etc
    //such transition
    //isSelected will be true if the transition is currently selected
    //Returns the Shape that was drawn
    public Shape drawTransition(Graphics2D gra2d, Transition t, 
      int multiplicity, boolean isSelected); 
}
