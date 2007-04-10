////////////////////////////////////////////////////////////////////////////////
//
//Job is used to record the details of a new job, before it is entered into
//the running simulation
//
////////////////////////////////////////////////////////////////////////////////
public class Job extends Object
{
    
    public int startTime;
    public int priority;
    public Task task;
    
    
    public Job(int startTime, int priority, Task task)
    {
        this.startTime= startTime;
        this.priority= priority;
        this.task= task;
    }
    
    public String toString()
    {
        return "Job("+startTime+" "+priority+" "+task+")";
    }
}

