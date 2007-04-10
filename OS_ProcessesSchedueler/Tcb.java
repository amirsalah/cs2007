////////////////////////////////////////////////////////////////////////////////
//
//A task control block (TCB) records the state of a task once it has been
//entered into the running system.
//
////////////////////////////////////////////////////////////////////////////////
public class Tcb extends Object
{
    
    private Task task;
    private State state;
    private int priority;
    private int quantum;
    private int credits;
    
    
    
    public Tcb(Task task, int priority, int time)
    {
        this(task,priority,2147483647,time);
    }
    
    
    public Tcb(Task task, int priority, int quantum, int time)
    {
        this.task= task;
        this.priority= priority;
        this.quantum= quantum;
        this.credits= 0;
        state= new State(time);
        state.set(State.READY,time);
    }
    
    
    //Get the task pointer
    public Task task()
    {
        return task;
    }
    
    
    
    //Get the time-quantum for this task
    public int quantum()
    {
        return quantum;
    }
    
    
    
    //Set the time-quantum for this task
    public void setQuantum(int quantum)
    {
        this.quantum= quantum;
    }
    
    
    
    //Get the priority of this task
    public int priority()
    {
        return priority;
    }
    
    
    
    //Get the current state of this task
    public State state()
    {
        return state;
    }
    
    
    
    //Set the state of this task.  The time paraemeter specifies the time
    //at which the change occurred
    public void setState(int newState, int time)
    {
        state.set(newState,time);
    }
    
    
    
    //Get the number of credits for this task
    public int credits()
    {
        return credits;
    }
    
    
    
    //Set the number of credits for this task
    public void setCredits(int credits)
    {
        this.credits=credits;
    }
    
    
    
    //Decrement the number of credits belonging to this task
    public void decrementCredits()
    {
        if (credits==0) {
            //Already at lowest value
            return;
        }
        
        credits--;
    }
    
    
    
    public String toString()
    {
        return task.name()+" "+state+" pri="+priority+" qtm="+quantum+
        " cr="+credits;
    }
    
    
    public String getStats()
    {
        return state.getStats();
    }
}
