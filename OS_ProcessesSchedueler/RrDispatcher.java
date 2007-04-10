///////////////////////////////////////////////////////////////////////////////
//
//Rr dispatcher
//
//Tasks are entered into the ready queue in arrival order
//The dispatcher runs the first entry in the queue until it blocks, or
//its time quantum is expired                                                
//When a task's I/O completes, the task is put onto the end of the queue
//
//David Knight Aug-03
//
///////////////////////////////////////////////////////////////////////////////

import java.util.Vector;

public class RrDispatcher extends Object
implements Dispatcher, DispatcherObserver{
    
    private int Quantum= 10;
    private Vector<Object> tcbQueue;
    private Tcb currentTcb;
    private DispatcherFrame frame;
    private Task idleTask;
    private Cpu cpu;
    
    public RrDispatcher(){
        frame= null;
        idleTask= new IdleTask();
    }
    
    
    public void setCpu(Cpu cpu){
        this.cpu= cpu;
    }
    
    
    public String getName(){
        return "RrDispatcher";
    }
    
    public void InitQuantum(int n){
        this.Quantum= n;
    }
    
    
    public void reset(int atTime){
        if (frame!=null) {
            frame.reset();
        }
        tcbQueue= new Vector<Object>();
        currentTcb= null;
        dispatch(atTime);
    }
    
    
    
    public void enableGui(boolean hasGui){
        frame= null;
        if (hasGui) {
            frame= new DispatcherFrame("RrDispatcher",this);
        }
    }
    
    
    private void dispatch(int atTime){
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
        currentTcb.setQuantum(Quantum);
        cpu.run(currentTcb.task(),currentTcb.quantum());
        
        //Update the GUI
        if (frame!=null) {
            frame.update();
        }
    }
 
    
    ///////////////////////////////////
    //Accessor methods for the GUI only
    ///////////////////////////////////
    public Vector getQueue(){
        return tcbQueue;
    }
    
    
    /* Add a new process to the tail of the ready queue */
    public void addTask(int atTime, Task task, int priority){
        Tcb tcb= new Tcb(task,priority,atTime);
        tcbQueue.add(tcb);
        if (frame!=null ) {
            frame.update();
        }
        dispatch(atTime);
    }
    
    
    /* Mark the blocked process */
    public void taskBlocked(int atTime){
        currentTcb.setState(State.BLOCKED,atTime);
        currentTcb= null;
        dispatch(atTime);
    }
    
    
    /* Insert to the tail of the ready queue after IO burst */
    public void ioCompleted(int atTime, Tcb tcb){
        tcb.setState(State.READY,atTime);
        tcbQueue.add(tcb);
        if (frame!=null ) {
            frame.update();
        }
        dispatch(atTime);
    }
    
    
    /* Put the current process in to the tail of ready queue after its quantum expired */
    public void quantumExpired(int atTime){
        currentTcb.setState(State.READY,atTime);
        tcbQueue.add(currentTcb);
        currentTcb= null;
        if (frame!=null ){
            frame.update();
        }
        dispatch(atTime);
    }
    
    
    /* redispatch after a process terminate */
    public void taskTerminated(int atTime){
        //Mark the current task as completed
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
