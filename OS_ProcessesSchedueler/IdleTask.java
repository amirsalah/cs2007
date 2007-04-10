public class IdleTask extends Object
implements Task
{
    
    //Clled to initialise the task
    public void initialise()
{
        //Nothing needed
}

//Called whenever this task is running
public void tick(Cpu cpu)
{
	//We have received a tick
	//...which we simply ignore
}


//Returns the task name
public String name()
{
	return "Idle";
}


public String toString()
{
	return "IdleTask";
}
}
