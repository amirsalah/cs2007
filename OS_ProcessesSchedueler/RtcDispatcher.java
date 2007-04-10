//Run-to-completion dispatcher
//Runs each task completely before running the next task
//During I/O, the processor is idle
//
//David Knight Aug-03

import java.util.Vector;

public class RtcDispatcher extends Object
implements Dispatcher, DispatcherObserver
{
    private Vector<Object> tcbQueue;
    
    private Cpu cpu;
    private Tcb currentTcb;
    private DispatcherFrame frame;
    private IdleTask idleTask;
    
    public RtcDispatcher()
    {
        frame= null;
        idleTask= new IdleTask();
    }
    
    
    public void setCpu(Cpu cpu)
    {
        this.cpu= cpu;
    }
    
    
    public String getName()
    {
        return "RtcDispatcher";
    }
    
    public void InitQuantum(int n)
    {
        //Ignore this
    }
    
    
    public void reset(int atTime)
    {
        if (frame!=null) {
            frame.reset();
        }
        tcbQueue= new Vector<Object>();
        dispatch(atTime);
    }
    
    
    
    public void enableGui(boolean hasGui)
    {
        frame= null;
        if (hasGui) {
            frame= new DispatcherFrame("RtcDispatcher",this);
        }
    }
    
    
    
    private void dispatch(int atTime)
    {
        if (tcbQueue.size()==0) {
            //Nothing to run...
            cpu.run(idleTask,Cpu.FOREVER);
            if(frame!=null) {
                frame.update();
            }
            return;
        }
        
        currentTcb= (Tcb)tcbQueue.get(0);
        currentTcb.setState(State.RUNNING,atTime);
        cpu.run(currentTcb.task(),currentTcb.quantum());
        
        if (frame!=null) {
            frame.update();
        }
    }
    
    
    public Tcb currentTcb()
    {
        return currentTcb;
    }
    
    ///////////////////////////////////
    //Accessor methods for the GUI only
    ///////////////////////////////////
    public Vector getQueue()
    {
        return tcbQueue;
    }
    
    
    ////////////////////////////////////////
    //Methods from the Dispatcher interface
    ////////////////////////////////////////
    
    public void addTask(int atTime, Task task, int priority)
    {
        Tcb tcb= new Tcb(task,priority,atTime);
        tcbQueue.add(tcb);
        if (frame!=null) {
            frame.update();
        }
        dispatch(atTime);
    }
    
    
    
    public void taskBlocked(int atTime)
    {
        currentTcb.setState(State.BLOCKED,atTime);
        currentTcb= null;
        cpu.run(idleTask,2147483647);
        if (frame!=null) {
            frame.update();
        }
    }
    
    
    
    public void ioCompleted(int atTime, Tcb tcb)
    {
        dispatch(atTime);
    }
    
    
    
    public void quantumExpired(int atTime)
    {
        //Ignore this
    }
    
    
    
    public void taskTerminated(int atTime)
    {
        currentTcb.setState(State.TERMINATED,atTime);
        
        //print termination stats
        System.out.println(currentTcb.task().name()+" "+currentTcb.getStats());
        
        currentTcb= null;
        tcbQueue.remove(0);
        dispatch(atTime);
    }
}
