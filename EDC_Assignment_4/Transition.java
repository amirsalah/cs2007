public interface Transition
{
    //Return the from-state of this transition
    public State fromState();
    

    //Return the to-state of this transition
    public State toState();
    

    //Return the name of the event that causes this transition
    public String eventName();
    

    //Return the output associated with this transition
    //Return an empty string if there is no output.
    public String output();

    
    //Return a string containing information about this transition 
    //in the form (without quotes, of course!):
    //"fromStateName(eventName:output)toStateName"
    public String toString();
}
