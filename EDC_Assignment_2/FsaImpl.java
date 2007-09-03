/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 3rd, Sept 2007
=========================================================*/

import java.util.List;
import java.util.Set;

public class FsaImpl implements FsaSim, Fsa{
	public FsaImpl(){
		
	}
	
    //Create a new State and add it to this FSA
    //Return the new state
    //Throws IllegalArgumentException if:
    //the name is not valid or is the same as that
    //of an existing state
    public State newState(String name, int x, int y)
      throws IllegalArgumentException{
    	
    }


    //Remove a state from the FSA
    //If the state does not exist, returns without error
    public void removeState(State s){
    	
    }


    //Find and return the State with the given name
    //If no state exists with given name, return NULL
    public State findState(String stateName){
    	
    }


    //Return a set containing all the states in this Fsa
    public Set<State> getStates(){
    	
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
    	
    }


    //Remove a transition t from the FSA
    //If the transition does not exist, returns without error
    public void removeTransition(Transition t){
    	
    }


    //Add the state s to the set of initial states for this FSA
    //Throws IllegalArgumentException if the state s is not in the FSA
    public void addInitialState(State s)
      throws IllegalArgumentException{
    	
    }


    //Remove the state s to the set of initial states for this FSA
    //If the state does not exist, returns without error
    public void removeInitialState(State s){
    	
    }


    //Return the set of initial states of this Fsa
    public Set<State> getInitialStates(){
    	
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
