import java.util.Set;

public interface State
{
    //Return a set containing all transitions FROM this state
    public Set<Transition> transitionsFrom();


    //Return a set containing all transitions TO this state
    public Set<Transition> transitionsTo();
    

    //Move the position of this state 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy);
    

    //Return a string containing information about this state 
    //in the form (without the quotes, of course!) :
    //"stateName(xPos,yPos)"  
    public String toString();
    

    //Returnthe name of this state 
    public String getName();
    

    //Return the X position of this state
    public int getXCoord();
    

    //Return the Y position of this state
    public int getYCoord();
}
