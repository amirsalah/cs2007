///////////////////////////////////////////////////////////////////////////////
//
//First-come-first-served dispatcher
//
//Runs each task until it blocks, then runs the next task in the ready 
//queue until it blocks, and so on.
//When a task's I/O completes, the task is put on the end of the ready queue.
//
//David Knight Aug-03
//
///////////////////////////////////////////////////////////////////////////////

import java.util.Vector;

public class FcfsDispatcher extends Object
implements Dispatcher, DispatcherObserver
{
    private Vector<Object> tcbQueue;
    private Tcb currentTcb;
    private DispatcherFrame frame;
    private Task idleTask;
    private Cpu cpu;
    
    public FcfsDispatcher()
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
        return "FcfsDispatcher";
    }
    
    public void InitQuantum(int n)
    {
    	// ignore this 
    }
    
    
    public void reset(int atTime)
    {
        if (frame!=null) {
            frame.reset();
        }
        tcbQueue= new Vector<Object>();
        currentTcb= null;
        dispatch(atTime);
    }
    
    
    
    public void enableGui(boolean hasGui)
    {
        frame= null;
        if (hasGui) {
            frame= new DispatcherFrame("FcfsDispatcher",this);
        }
    }
    
    
    
    private void dispatch(int atTime)
    {
        if (tcbQueue.size()==0) {
            //Nothing to run...
            currentTcb= null;
            cpu.run(idleTask,Cpu.FOREVER);
            if (frame!=null) {
                frame.update();
            }
            return;
        }
        
        if (currentTcb!=null) {
            //Leave everything alone...
            return;
        }
        
        currentTcb= (Tcb)tcbQueue.remove(0);
        currentTcb.setState(State.RUNNING,atTime);
        cpu.run(currentTcb.task(),currentTcb.quantum());
        
        //Update the GUI
        if (frame!=null) {
            frame.update();
        }
    }
    
    
    ///////////////////////////////////
    //Accessor methods for the GUI only
    ///////////////////////////////////
    public Vector getQueue()
    {
        return tcbQueue;
    }
    
    
    //////////////////////////////////
    //Methods for Dispatcher interface
    //////////////////////////////////
    public void addTask(int atTime, Task task, int priority)
    {
        Tcb tcb= new Tcb(task,priority,atTime);
        tcbQueue.add(tcb);
        if (frame!=null ) {
            frame.update();
        }
        dispatch(atTime);
    }
    
    
    
    public void taskBlocked(int atTime)
    {
        currentTcb.setState(State.BLOCKED,atTime);
        currentTcb= null;
        dispatch(atTime);
    }
    
    
    
    public void ioCompleted(int atTime, Tcb tcb)
    {
        tcb.setState(State.READY,atTime);
        tcbQueue.add(tcb);
        if (frame!=null ) {
            frame.update();
        }
        dispatch(atTime);
    }
    
    
    
    public void quantumExpired(int atTime)
    {
        // no quantum limit for FCFS
    }
    
    
    
    public void taskTerminated(int atTime)
    {
        currentTcb.setState(State.TERMINATED,atTime);
        System.out.println(currentTcb.task().name()+" "+currentTcb.getStats());
        
        currentTcb= null;
        dispatch(atTime);
    }
    
    
    
    
    public Tcb currentTcb()
    {
        return currentTcb;
    }
    
}

