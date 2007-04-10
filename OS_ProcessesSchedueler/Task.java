public interface Task
{
    //Called to initialise the task
    public void initialise();
    
    //Called whenever the task is running
    public void tick(Cpu cpu);
    
    //Returns the name of the task
    public String name();
}
