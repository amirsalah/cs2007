import java.util.List;

public interface FsaSim 
{
    //Reset the simulation to its initial state(s)
    public void reset();
    
    //Take one step in the simulation
    //Returns a list of outputs, sorted in ascending order, generated 
    //by this transition
    //Returns null if the event is not handled in this state
    public List<String> step(String event);
}
