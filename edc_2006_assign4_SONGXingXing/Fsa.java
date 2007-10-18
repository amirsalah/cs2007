
import java.io.InputStream;
import java.util.Set;
import java.util.List;

interface Fsa
{
    //Read in an Fsa from a stream
    //For the format of the input, see the practical specification sheet
    //The data read from the file replaces any information already in the FSa
    public void read(InputStream is)
      throws FsaFileException;

    //Return a list of all states in this Fsa
    public Set<State> getStates();

    //Return the initial state for this Fsa
    public State getInitialState();

    //Return a string describing this Fsa in the format
    //described in the practical specification sheet
    public String toString();
    
    //
    // NEW METHODS FOR PRAC 3 FOLLOW!
    //
    
	// Returns the outputs generated when this event is processed.
	// Outputs must be in alphabetical order, and may contain duplicates.
	// A transition fires when its event name matches the event name
	// given in the event sequence file.
	public List<String> step( Object event )
		throws UnhandledEventException;
		
	// Reset the simulation.
	// The FSA returns to its initial state
	public void reset();

	// Returns the current states of the FSA
	public Set<State> getCurrentStates();	
	
	//
	// PRAC 4 METHODS
	//
	
	// Sets the initial state of the FSA to be s
	public void setInitialState( State s );
	
	// Adds a state with specified name and (x,y) position to the FSA
	// Throws IllegalArgumentException if the name is empty or 
	// duplicates an existing state name
	public void addState( String name, int x, int y ) 
		throws IllegalArgumentException;
	
	// Adds a transition with specified from and to states, event name
	// and output to the FSA
	// Throws IllegalArgumentException if the event name is empty
	public void addTransition( State from, State to, String eventName, String output ) 
		throws IllegalArgumentException;
	
	// Removes state s from the FSA
	// Also removes any transitions to or from s
	public void removeState( State s );
	
	// Removes transition t from the FSA
	public void removeTransition( Transition t );
}
