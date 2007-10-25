/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 5th, Oct. 2007
=========================================================*/
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FsaImpl implements FsaSim, Fsa{
	private Hashtable<String, State> statesSet = new Hashtable<String, State>();
	private Set<Transition> transitionsSet = new HashSet<Transition>();
	private ArrayList<String> initialStatesNames = new ArrayList<String>();
	private ArrayList<String> currentStatesNames = new ArrayList<String>();
	
	
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
    	String stateName = s.getName();
    	if( !statesSet.containsKey(stateName) ){
    		return;
    	}
    	
    	//Remove the state
    	statesSet.remove(stateName);
    	initialStatesNames.remove(stateName);
    	currentStatesNames.remove(stateName);
    	
    	//Remove the corresponding transition(s)
    	Iterator<Transition> itr = transitionsSet.iterator();
    	Transition aTransition = null;
    	while(itr.hasNext()){
    		aTransition = itr.next();
    		if(aTransition.fromState().equals(s) || aTransition.toState().equals(s)){
//    			transitionsSet.remove(aTransition);
    		}
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
    	if(from == null || to == null){
    		throw new IllegalArgumentException("New Transition");
    	}
    	String fromStateName = from.getName();
    	String toStateName = to.getName();
    	if( !statesSet.containsKey(fromStateName) || !statesSet.containsKey(toStateName)){
    		throw new IllegalArgumentException("New Transition");
    	}
    	
    	//Check the vadility of the event name
    	if( !IsValidEventName(eventName) ){
    		throw new IllegalArgumentException("New Transition");
    	}
    	
    	//Check the vadility of the output name
    	if( !IsValidOutput(output) ){
    		throw new IllegalArgumentException("New Transition");
    	}
    	
    	Transition newTransition = new TransitionImpl(from, to, eventName, output);
    	((StateImpl)from).AddTransitionFrom(newTransition);
    	((StateImpl)to).AddTransitionTo(newTransition);
    	transitionsSet.add(newTransition);
    	
    	return newTransition;
    }
    
    /*
     * Check if the given even name is valid
     */
    private boolean IsValidEventName(String eventName){
    	//Epsilon transition event
    	if( (eventName.length() == 1) && eventName.equalsIgnoreCase("?") ){
    		return true;
    	}
    	
    	if( eventName == null){
    		return true;
    	}
    	
    	for(int i=0; i<eventName.length(); i++){
    		if( !Character.isLetter(eventName.charAt(i)) ){
    			return false;
    		}
    	}
    	
    	return true;
    }

    /*
     * Check if the given event name is valid
     */
    private boolean IsValidOutput(String output){
    	//Special value "-" is also valid
    	if( (output.length() == 1) && (output.equalsIgnoreCase("-")) ){
    		return true;
    	}
    	
    	if(output == null){
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
    		Transition checkedTransition = (Transition)itr.next();
    		
    		if(t.equals(checkedTransition)){
    			transitionsSet.remove(checkedTransition);
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
    	
    	initialStatesNames.add(stateName);
    }


    //Remove the state s from the set of initial states for this FSA
    //If the state does not exist, returns without error
    public void removeInitialState(State s){
    	String stateName = s.getName();
    	
    	if( !statesSet.containsKey(stateName) ){
    		return;
    	}
    	
    	for(int i=0; i<initialStatesNames.size(); i++){
    		if(initialStatesNames.get(i).equals(stateName)){
    			initialStatesNames.remove(i);
    			return;
    		}
    	}
    }


    //Return the set of initial states of this Fsa
    public Set<State> getInitialStates(){
    	Set<State> allStates = new HashSet<State>();
    	String stateName = null;
    	
    	for(int i=0; i<initialStatesNames.size(); i++){
    		stateName = initialStatesNames.get(i);
    		allStates.add(statesSet.get(stateName));
    	}
    	
    	return allStates;
    }


    //Returns a set containing all the current states of this FSA
    public Set<State> getCurrentStates(){
    	Set<State> allStates = new HashSet<State>();
    	String stateName = null;
    	
    	for(int i=0; i<currentStatesNames.size(); i++){
    		stateName = currentStatesNames.get(i);
    		allStates.add(statesSet.get(stateName));
    	}
    	
    	return allStates;
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
    	String LinesOutput = "";
    	
    	//Output all states
    	Set<State> allStates = null;
    	allStates = getStates();
    	Iterator itr = allStates.iterator();
    	while(itr.hasNext()){
    		LinesOutput = LinesOutput + "STATE " + itr.next().toString() + "\n";
    	}
    	
    	//Output transitions
    	itr = transitionsSet.iterator();
    	while(itr.hasNext()){
    		LinesOutput = LinesOutput + "TRANSITION " + itr.next().toString() + "\n";
    	}
    	
    	//Output initial states
    	for(int i=0; i<initialStatesNames.size(); i++){
    		LinesOutput = LinesOutput + "INITIAL " + initialStatesNames.get(i) + "\n";
    	}
    	
    	return LinesOutput;
    }
    
    //Reset the simulation to its initial state(s)
    public void reset(){
//    	statesSet.clear();
//    	initialStatesNames.clear();
//    	transitionsSet.clear();
    	currentStatesNames = (ArrayList<String>)initialStatesNames.clone();
    }
    
    /*
     * Check if the given event is handled in this fsa
     */ 
    private boolean IsValidEvent(String event){
    	Iterator itr = transitionsSet.iterator();
    	Transition aTransition = null;
    	
    	while(itr.hasNext()){
    		aTransition = (Transition)itr.next();
    		if(aTransition.eventName().equals(event)){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    //Take one step in the simulation
    //Returns a list of outputs, sorted in ascending order, generated 
    //by this transition
    //Returns null if the event is not handled in this state
    public List<String> step(String event){
    	if( !IsValidEvent(event) ){
    		return null;
    	}
    	
    	List<String> outputs = new ArrayList<String>();
    	// Actual current states, extending the original by epsilon transition
    	Set<State> exCurrentStates = StatesClosure(getCurrentStates());
    	Set<Transition> transitions = null;
    	
    	Set<State> nextStates = new HashSet<State>(); // The valid states to be next "current state"
    	currentStatesNames.clear();
    	
    	Iterator<State> itr_state = exCurrentStates.iterator();
    	State s = null;
    	Iterator<Transition> itr_transition = null;
    	Transition t = null;
    	
    	// Visit each element in the extended current state set
    	while(itr_state.hasNext()){
    		s = itr_state.next();
    		transitions = s.transitionsFrom();
    		itr_transition = transitions.iterator();
    		// Visit all the transition from this state
    		while(itr_transition.hasNext()){
    			t = itr_transition.next();
    			// Add a NEXT current state
    			if(t.eventName().equals(event)){
    				nextStates.add(t.toState());
    				currentStatesNames.add(t.toState().getName());
    				outputs.add(t.output());
    			}
    		}
    	}
    	
    	Collections.sort(outputs);
    	return outputs;
    }
    
    /**
     * Return the closure of all the states
     * @param currentStates
     * @return the set containing all states could be reached by epsilon from current states
     */
    public Set<State> StatesClosure(Set<State> currentStates){
    	State s = null;
    	Transition t = null;
    	
    	Set<State> states = currentStates;
    	Set<Transition> transitions = null;
    	Iterator<State> itr = currentStates.iterator();
    	Iterator<Transition> itr_transition = null;
    	
    	Set<State> processedStates = new HashSet<State>(); // The states that have been processed
    	
    	// Check each state in the current states
    	// Note: the current states set will grow with new current states added
    	while(itr.hasNext()){
    		s = itr.next();
    		
    		if(processedStates.contains(s)){
    			continue;
    		}else{
    			processedStates.add(s);
    		}
    		
    		transitions = s.transitionsFrom();
    		itr_transition = transitions.iterator();
    		
    		// Check each transition
    		while(itr_transition.hasNext()){
    			t = itr_transition.next();
    			// epsilon transition?
    			if(t.eventName().equals("?")){
    				states.add(t.toState()); // Set will ensure no duplicated elements
    			}
    		}
    		itr = states.iterator();
    	}
    	
    	return states;
    }
    
    public Set<Transition> GetTransitions(){
    	return transitionsSet;
    }
}

class StateImpl implements State{
	private int xPos;
	private int yPos;
	private String stateName;
	private HashSet<Transition> transitionsFromSet = new HashSet<Transition>();
	private HashSet<Transition> transitionsToSet = new HashSet<Transition>();
	
	public StateImpl(String name, int x, int y){
		stateName = name;
		xPos = x;
		yPos = y;
	}
	
	/*
	 * Add a new transition which is From this state
	 */
	public void AddTransitionFrom(Transition from){
		transitionsFromSet.add(from);
	}
	
	/*
	 * Delete a existing transition in the fromSet
	 */
	public void DeleteTransitionFrom(Transition from){
		transitionsFromSet.remove(from);
	}
	
	/*
	 * Delete a existing transition in the toSet
	 */
	public void DeleteTransitionTo(Transition to){
		transitionsToSet.remove(to);
	}
	
	/*
	 * Add a new transition which is To this state
	 */
	public void AddTransitionTo(Transition to){
		transitionsToSet.add(to);
	}
	
	/*
	 * Test if the given state is equals this state
	 */
	public boolean Equals(State state){
		if(stateName == state.getName()){
			return true;
		}else{
			return false;
		}
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
	private State fromState = null;
	private State toState = null;
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
