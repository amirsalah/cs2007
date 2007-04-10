///////////////////////////////////////////////////////////////////////////////
//
//This interface defines the behaviour that all dispatchers must provide
//
///////////////////////////////////////////////////////////////////////////////
public interface Dispatcher
{
    //Reset the dispatcher to itys initial state
    public void reset(int atTime);
    
    //The name of the dispatcher
    public String getName();
    
    //Add a new task to be run by the dispatcher
    public void addTask(int startTime, Task task, int priority);
    
    //Cllaed when a task is unable to continue execution
    public void taskBlocked(int atTime);
    
    //Called when an I/O operation for a task completes
    public void ioCompleted(int atTime, Tcb tcb);
    
    //Called for time-sharing dispatchers when a task's execution quantum expires
    public void quantumExpired(int atTime);
    
    //Called for time-sharing dispatchers to change the quantum 
    public void InitQuantum(int n);
    
    //Called when a task finishes executing
    public void taskTerminated(int atTime);
    
    //Returns the tcb of the task currently being executed
    public Tcb currentTcb();
    
    //Used to enable/disable the gui (only called by Osim)
    public void enableGui(boolean hasGui);
    
    //Used to give dispatcher access to the CPU (only called by Osim)
    public void setCpu(Cpu cpu);
}
