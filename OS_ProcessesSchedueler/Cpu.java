////////////////////////////////////////////////////////////////////////////////
//
//Cpu implements the behaviour of the hardware CPU
//
////////////////////////////////////////////////////////////////////////////////

public class Cpu extends Object
{
    public static final int FOREVER= 2147483647;
    
    private  int timer;
    private Task task;
    private int runtime;
    private Os os;
    
    private CpuFrame cpuFrame;
    private boolean hasGui;    
    
    public Cpu()
    {
        timer= 0;
        task= null;
        os= null;
    }
    
    
    public void installOs(Os os)
    {
        this.os= os;
    }
    
    
    public void enableGui(boolean hasGui)
    {
        this.hasGui= hasGui;
    }
    
    
    
    public void reset()
    {
        timer= 0;
        task= null;
        runtime= -1;
        
        if (hasGui) {
            if (cpuFrame==null) {
                //No frame, create one
                cpuFrame= new CpuFrame("Cpu usage");
            }else{
                //Frame exists, clear it
                cpuFrame.reset();
            }
            
        }
    }
    
    
    
    public void run(Task t, int quantum)
    {
	    
        //Update statistics for previous task
        if (cpuFrame!=null) {
            cpuFrame.add(task,runtime);
        }
        
        //Start running new task
        task= t;
        timer= quantum;
        runtime= 0;
    }
    
    
    
    public int getRemainingQuantum()
    {
        return timer;
    }
    
    
    
    public  void tickPhase1()
    {
        runtime++;
        timer--;
    };
    
    
    public void tickPhase2()
    {
        //Execute the current task
        if (task!=null) {
            task.tick(this);
        }
        //Update statistics for this task
        if (cpuFrame!=null) {
            cpuFrame.add(task,runtime);
        }
        
        //Check the cpu quantum-timer
        if (timer<=0) {
            os.quantumInterrupt();
        }
    }
    
    
    public void ioInterrupt(Tcb tcb)
    {
        os.ioInterrupt(tcb);
    }
    
    
    /////////////////////////////////
    //OS calls from a running program
    /////////////////////////////////
    public void osExit()
    {
        os.osExit();
    }
    
    
    public void osIoRequest(int duration)
    {
        os.osIoRequest(duration);
    }
}
