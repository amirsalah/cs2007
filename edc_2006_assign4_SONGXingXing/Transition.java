public interface Transition
{
    //Return the name of the event that causes this transition
    public String eventName();

    //Return the from-state of this transition
    public State fromState();

    //Return the to-state of this transition
    public State toState();

    // Return the output of this transition.
   	// Return an empty string if there is no output.
    public String output();
    
    //Two transitions are equal if:
    //THe fromStates are the same and
    //The toStates are the same and
    //The eventName is the same and
    //The output is the same
    public boolean equals(Object obj);

    //Return a string containing information about this state in the form:
    //"fromStateName toStateName eventName output"
    public String toString();
}
