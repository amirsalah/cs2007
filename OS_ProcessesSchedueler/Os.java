////////////////////////////////////////////////////////////////////////////////
//
//Os implements to "operating System" of the simulator
//From time to time, tt is called by the simulator to add a new job
//It handles dispatching of interrupts, processing of OS calls
//
////////////////////////////////////////////////////////////////////////////////
import java.util.HashMap;

public class Os extends Object
{
    private Dispatcher dispatcher= null;
    private HashMap resourceList;
    private IoDevice ioDevice;
    private int clock;
    private static int NoContextSwitch;
    private static int NoQuantumSwitch;
    
    
    public Os(IoDevice ioDevice)
    {
        this.ioDevice= ioDevice;
    }
    
    
    //Reset the operating system
    public void reset()
    {
        clock= 0;
        NoContextSwitch= 0;
        NoQuantumSwitch= 0;
        dispatcher.reset(clock);
        resourceList= new HashMap();
    }
    
    
    //Install a dispatcher into the system
    public void setDispatcher(Dispatcher dispatcher)
    {
        this.dispatcher= dispatcher;
    }
    
    
    
    //Start a new task executing (called by Osim)
    public void startTask(Task task, int priority)
    {
        dispatcher.addTask(clock,task,priority);
    }
    
    
    //Called when the cpu time-quantum expires
    public void quantumInterrupt()
    {
        dispatcher.quantumExpired(clock);
	NoContextSwitch++;
	NoQuantumSwitch++;
    }
    
    public static String getNoCS()
    {
        StringBuffer sb= new StringBuffer();
        sb.append("Number of context switches = ");
        sb.append(Integer.toString(NoContextSwitch));
        sb.append("\n");
        sb.append("Number of quantum switches = ");
        sb.append(Integer.toString(NoQuantumSwitch));
        sb.append("\n");
        return sb.toString();
    }

    
    
    ///////////////////////////////////////////////
    //Operating system calls from a running program
    ///////////////////////////////////////////////
    public void osExit()
    {
        dispatcher.taskTerminated(clock);
	NoContextSwitch++;
    }
    
    
    public void osIoRequest(int duration)
    {
        ioDevice.startTransfer(dispatcher.currentTcb(),duration);
        dispatcher.taskBlocked(clock);
	NoContextSwitch++;
    }
    
    
    //??Unfinished business here
    public void resourceRequest(String resourceName)
    {
        //Find resource
        Resource resource= (Resource)resourceList.get(resourceName);
        
        if (resource==null) {
            //it doesn't exist
            throw new RuntimeException("Non-existent resource "+resourceName+
                                       " requested by task "+dispatcher.currentTcb().task().name());
        }
        
        
        if (resource.request(dispatcher.currentTcb())) {
            //resource is available, so keep running this task
            return;
        }
        
        //All instances of this resources in use
        dispatcher.taskBlocked(clock);
    }
    
    
    
    //??Unfinished business here
    public void resourceRelease(String resourceName)
    {
        //Find resource
        Resource resource= (Resource)resourceList.get(resourceName);
        
        if (resource==null) {
            //It doesn't exist
            throw new RuntimeException("Non-existent resource "+resourceName+
                                       " requested by task "+dispatcher.currentTcb().task().name());
        }
        
        resource.release(dispatcher.currentTcb());
    }
    
    
    
    
    ////////////////////
    //Called by hardware
    ////////////////////
    //Called when an I/O operation completes
    public void ioInterrupt(Tcb tcb)
    {
        dispatcher.ioCompleted(clock,tcb);
    }
    
    
    //Cllaed when a timer tick occurrs
    public void timerInterrupt()
    {
        //Increment clock;
        clock++;
    }
}
