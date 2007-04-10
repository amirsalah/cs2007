///////////////////////////////////////////////////////////////////////////////
//
//Non-preemptive priority dispatcher
//
//Tasks are entered into the ready queue in priority order
//The dispatcher runs the highest-priority task until it blocks, then runs
//the highest-priority task until it blocks, and so on.
//When a task's I/O completes, the task is put into the ready queue according
//to its priority
//
//David Knight Aug-03
//
///////////////////////////////////////////////////////////////////////////////

import java.util.Vector;

public class NppDispatcher extends Object
implements Dispatcher, DispatcherObserver
{
    private DispatcherFrame frame;
    private Cpu cpu;
    
    private Vector<Object> tcbQueue;
    private Tcb currentTcb;
    private Task idleTask;
    
    public NppDispatcher()
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
        return "NppDispatcher";
    }
    
    public void InitQuantum(int n)
    {
        //Ignore it
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
            frame= new DispatcherFrame("NppDispatcher",this);
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
    
    /* Insert a task to the tcbQueue by its priority
     * If the priorities are the same, fcfs algorithm is performed.
     * Note: 0 is the highest priority, 1 is the second, 2 is the third ... 
     */
    private void insertTcb(Tcb tcb){
//    	int insertIndex = 0;
    	Tcb tcbInQueue;
    	int priority = tcb.priority();
    	
    	if ( tcbQueue == null){
    		tcbQueue.add(0, tcb);
    	}else{
    		int queueLength = tcbQueue.size();
//    		do{
//    			insertIndex++;
//    			tcbInQueue = (Tcb)tcbQueue.get(insertIndex);
//    		}while( (priority >= tcbInQueue.priority()) && (insertIndex < queueLength - 1) );

    		for( int i = 0; i < queueLength; i++){
    			tcbInQueue = (Tcb)tcbQueue.get(i);
    			if (priority < tcbInQueue.priority()){
    				tcbQueue.add(i, tcb);
    				return;
    			}
    		}
    		tcbQueue.add(queueLength, tcb);
    		return;
    		
    	}
    }
    
    
    ///////////////////////////////////
    //Accessor methods for the GUI only
    ///////////////////////////////////
    public Vector getQueue()
    {
        return null;
    }
    
    
    //////////////////////////////////
    //Methods for Dispatcher interface
    //////////////////////////////////
    public void addTask(int atTime, Task task, int priority)
    {
    	Tcb tcb = new Tcb(task, priority, atTime);
    	insertTcb(tcb);
        if (frame!=null ) {
            frame.update();
        }
        dispatch(atTime);
    }
    
    
    /* Launch new task when current task is blocked */
    public void taskBlocked(int atTime)
    {
        currentTcb.setState(State.BLOCKED,atTime);
        currentTcb= null;
        dispatch(atTime);    	
    }
    
    
    
    public void ioCompleted(int atTime, Tcb tcb)
    {
        tcb.setState(State.READY,atTime);
        insertTcb(tcb);
        if (frame!=null ) {
            frame.update();
        }
        dispatch(atTime);
    }
    
    
    
    public void quantumExpired(int atTime)
    {
    	// No quantum needed in this algorithm
    }
    
    
    
    public void taskTerminated(int atTime)
    {
    	currentTcb.setState(State.TERMINATED, atTime);
        System.out.println(currentTcb.task().name()+" "+currentTcb.getStats());
        
        currentTcb= null;
        dispatch(atTime);
    }
    
    
    
    
    public Tcb currentTcb()
    {
        return currentTcb;
    }
    
}
