import java.util.Set;

public interface State
{
    //Return a list of all transitions from this state
    //If there are no transitions, return an empty list
    public Set<Transition> getTransitions();

    //Two states are equal if:
    //The stateNames are the same
    public boolean equals(Object obj);

    // Move the (x,y) position of this state 
    // by this much from its current position
    public void moveBy( int x, int y );
    
    //Return a string containing information about this state in the form:
    //"stateName xPos yPos"  (without the quotes, of course!)
    public String toString();
}
