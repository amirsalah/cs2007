/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 3rd, Sept 2007
=========================================================*/
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.HashSet;

public class FsaImpl implements FsaSim, Fsa{
	private Hashtable<String, State> statesSet = new Hashtable<String, State>();
	private TreeSet<Transition> transitionsSet = new TreeSet<Transition>();
	private Hashtable<String, State> initialStatesSet = new Hashtable<String, State>();
	
	public FsaImpl(){
		
	}
	
    //Create a new State and add it to this FSA
    //Return the new state
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing state
    public State newState(String name, int x, int y)
      throws IllegalArgumentException{
    	//valid name?
    	if( !IsJavaIdentifier(name) ){
    		throw new IllegalArgumentException();
    	}
    	
    	//Existing state?
    	if( statesSet.containsKey(name) ){
    		throw new IllegalArgumentException();
    	}
    	
    	//Create new state
    	State newState = new StateImpl(name, x, y);
    	statesSet.put(name, newState);
    	
    	return newState;
    }


	/*
     * Check if the input name is a valid java identifier
     * return true if it is valid
     */
    private boolean IsJavaIdentifier(String name){
    	if(name.length() == 0){
    		return false;
    	}
    	
    	//	Start with a letter?
    	if( !Character.isLetter(name.charAt(0)) ){
    		return false;
    	}
    	
    	//	Check the remaining characters in the string
    	for(int i=1; i<name.length(); i++){
    		if( !Character.isLetterOrDigit(name.charAt(i)) && !name.substring(i, i+1).equalsIgnoreCase("_") ){
    			return false;
    		}
    	}
    	
    	return true;
    }

    //Remove a state from the FSA
    //If the state does not exist, returns without error
    public void removeState(State s){
    	if( !statesSet.containsKey(s.getName()) ){
    		return;
    	}
    	
    	
    }


    //Find and return the State with the given name
    //If no state exists with given name, return NULL
    public State findState(String stateName){
    	if(statesSet.containsKey(stateName)){
    		return statesSet.get(stateName);
    	}else{
    		return null;
    	}
    }


    //Return a set containing all the states in this Fsa
    public Set<State> getStates(){
    	Set<State> allStates = new HashSet<State>();
    	Set<String> allStatesName = statesSet.keySet();
    	Iterator<String> itr = allStatesName.iterator();
    	
    	//Add all existing states into a set
    	while(itr.hasNext()){
    		allStates.add(statesSet.get(itr.next()));
    	}
    	
    	return allStates;
    }
    

    //Create a new Transition and add it to this FSA
    //Returns the new transition.
    //Throws IllegalArgumentException if:
    //The fromState or toState does not exist or
    //the eventName is invalid ir
    //the output is invalid
    public Transition newTransition(State from, State to, 
      String eventName, String output) 
      throws IllegalArgumentException{
    	//Check the existance of fromState & toState
    	String fromStateName = from.getName();
    	String toStateName = to.getName();
    	if( !statesSet.containsKey(fromStateName) || !statesSet.containsKey(toStateName)){
    		throw new IllegalArgumentException("New Transition");
    	}
    	
    	//Check the vadility of the event name
    	if( !IsValidEvent(eventName) ){
    		throw new IllegalArgumentException("New Transition");
    	}
    	
    	//Check the vadility of the output name
    	if( !IsValidOutput(output) ){
    		throw new IllegalArgumentException("New Transition");
    	}
    	
    	Transition newTransition = new TransitionImpl(from, to, eventName, output);
    	transitionsSet.add(newTransition);
    	
    	return newTransition;
    }
    
    /*
     * Check if the given even name is valid
     */
    private boolean IsValidEvent(String eventName){
    	for(int i=0; i<eventName.length(); i++){
    		if( !Character.isLetter(eventName.charAt(i)) ){
    			return false;
    		}
    	}
    	
    	return true;
    }

    /*
     * Check if the given even name is valid
     */
    private boolean IsValidOutput(String output){
    	//Special value "-" is also valid
    	if( (output.length() == 1) && (output.equalsIgnoreCase("-")) ){
    		return true;
    	}
    	
    	for(int i=0; i<output.length(); i++){
    		if( !Character.isLetter(output.charAt(i)) ){
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    //Remove a transition t from the FSA
    //If the transition does not exist, returns without error
    public void removeTransition(Transition t){
    	Iterator itr = transitionsSet.iterator();
    	while(itr.hasNext()){
    		if(t.equals(itr.next())){
    			itr.remove();
    			return;
    		}
    	}
    }


    //Add the state s to the set of initial states for this FSA
    //Throws IllegalArgumentException if the state s is not in the FSA
    public void addInitialState(State s)
      throws IllegalArgumentException{
    	String stateName = s.getName();
    	if( !statesSet.containsKey(stateName) ){
    		throw new IllegalArgumentException("Add initial state");
    	}
    	
    	initialStatesSet.put(stateName, s);
    }


    //Remove the state s from the set of initial states for this FSA
    //If the state does not exist, returns without error
    public void removeInitialState(State s){
    	String stateName = s.getName();
    	if( !initialStatesSet.containsKey(stateName) ){
    		throw new IllegalArgumentException("remove a initial state");
    	}
    	
    	initialStatesSet.remove(stateName);
    }


    //Return the set of initial states of this Fsa
    public Set<State> getInitialStates(){
    	Set<State> allStates = new HashSet<State>();
    	Set<String> allStatesName = initialStatesSet.keySet();
    	Iterator<String> itr = allStatesName.iterator();
    	statesSet.
    	//Add all existing states into a set
    	while(itr.hasNext()){
    		allStates.add(statesSet.get(itr.next()));
    	}
    	
    	return allStates;
    }


    //Returns a set containing all the current states of this FSA
    public Set<State> getCurrentStates(){
    	
    }
    

    //Return a string describing this Fsa
    //Returns a string that contains (in this order):
    //for each state in the FSA, a line (terminated by \n)
    //  STATE followed the toString result for that state
    //for each transition in the FSA, a line (terminated by \n)
    //  TRANSITION followed the toString result for that transition
    //for each initial state in the FSA, a line (terminated by \n)
    //  INITIAL followed the name of the state
    public String toString(){
    	
    }
    
    //Reset the simulation to its initial state(s)
    public void reset(){
    	
    }
    
    //Take one step in the simulation
    //Returns a list of outputs, sorted in ascending order, generated 
    //by this transition
    //Returns null if the event is not handled in this state
    public List<String> step(String event){
    	
    }
}

class StateImpl implements State{
	private int xPos;
	private int yPos;
	private String stateName;
	private TreeSet<Transition> transitionsFromSet = new TreeSet<Transition>();
	private TreeSet<Transition> transitionsToSet = new TreeSet<Transition>();
	
//	private FsaImpl fsa;  //The finate state machine in which the state works
	
	public StateImpl(String name, int x, int y){
		stateName = name;
		xPos = x;
		yPos = y;
//		this.fsa = fsa;
	}
	
	/*
	 * Add a new transition which is From this state
	 */
	public void AddTransitionFrom(Transition from){
		transitionsFromSet.add(from);
	}
	
	/*
	 * Add a new transition which is To this state
	 */
	public void AddTransitionTo(Transition to){
		transitionsToSet.add(to);
	}
	
    //Return a set containing all transitions FROM this state
    public Set<Transition> transitionsFrom(){
    	return transitionsFromSet;
    }


    //Return a set containing all transitions TO this state
    public Set<Transition> transitionsTo(){
    	return transitionsToSet;
    }
    

    //Move the position of this state 
    //by (dx,dy) from its current position
    public void moveBy(int dx, int dy){
    	xPos += dx;
    	yPos += dy;
    }
    

    //Return a string containing information about this state 
    //in the form (without the quotes, of course!) :
    //"stateName(xPos,yPos)"  
    public String toString(){
    	return stateName + "(" + String.valueOf(xPos) + "," + String.valueOf(yPos) + ")";
    }
    

    //Returnthe name of this state 
    public String getName(){
    	return stateName;
    }
    

    //Return the X position of this state
    public int getXCoord(){
    	return xPos;
    }
    

    //Return the Y position of this state
    public int getYCoord(){
    	return yPos;
    }
}

class TransitionImpl implements Transition{
	private State fromState;
	private State toState;
	private String eventName = null;
	private String output = null;
	
	public TransitionImpl(State from, State to, 
		      String eventName, String output){
		fromState = from;
		this.eventName = eventName;
		this.output = output;
		toState = to;
		
	}
	
    //Return the from-state of this transition
    public State fromState(){
    	return fromState;
    }
    

    //Return the to-state of this transition
    public State toState(){
    	return toState;
    }
    

    //Return the name of the event that causes this transition
    public String eventName(){
    	return eventName;
    }
    
    
    //Return the output associated with this transition
    //Return an empty string if there is no output.
    public String output(){
    	return output;
    }

    
    //Return a string containing information about this transition 
    //in the form (without quotes, of course!):
    //"fromStateName(eventName:output)toStateName"
    public String toString(){
    	return fromState.getName() + "(" + eventName + ":" + output + ")" + toState.getName();
    }
}
