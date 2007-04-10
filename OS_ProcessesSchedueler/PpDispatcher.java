///////////////////////////////////////////////////////////////////////////////
//
//Preemptive priority dispatcher
//
//Tasks are entered into the ready queue in priority order
//The dispatcher runs the highest-priority task until it blocks or is pre-empted by
//another ready task of higher priority,  and so on. 
//When a task's I/O completes, the task is put into the ready queue according
//to its priority
//
//David Knight Aug-03
//
///////////////////////////////////////////////////////////////////////////////

/*****************************************************************************/
/* Author: Bo CHEN															 */
/* Student ID: a1139520														 */
/* Last modified Date: 11, Sep. 2006										 */
/*****************************************************************************/

import java.util.Vector;

public class PpDispatcher extends Object
    implements Dispatcher, DispatcherObserver
{
    private DispatcherFrame frame;
    private Cpu cpu;
    
    private Vector<Object> tcbQueue;
    private Tcb currentTcb;
    private Task idleTask;

    public PpDispatcher(){
    	frame= null;
    	idleTask= new IdleTask();
    }


    public void setCpu(Cpu cpu){
    	this.cpu= cpu;
    }


    public String getName(){
    	return "PpDispatcher";
    }

    public void InitQuantum(int n){
        //Ignore it
    }

    /* Initialize the dispatcher to the default state */
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
    		frame= new DispatcherFrame("PpDispatcher",this);
    	}
    }
    
    /* 
     * Launch the process in the head of the ready queue, when there is no currently running process
     */
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
        cpu.run(currentTcb.task(),currentTcb.quantum());
        
        //Update the GUI
        if (frame!=null) {
            frame.update();
        }
    }
    

    /* 
     * Insert a task to the tcbQueue by its priority
     * If the priorities are the same, fcfs algorithm is performed.
     * Note: 0 is the highest priority, 1 is the second, 2 is the third ... 
     */
    private void insertTcb(Tcb tcb){
    	Tcb tcbInQueue;
    	int priority = tcb.priority();
    	
    	if ( tcbQueue == null){
    		tcbQueue.add(0, tcb);
    	}else{
    		int queueLength = tcbQueue.size();
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
    
    /* 
     * Perform preemption test, the process will preempt the current running process
     * or insert into the ready queue, based on its priority.
     */
    private void processPosition(Tcb tcb, int atTime){
		if( tcb.priority() < currentTcb.priority()){
			currentTcb.setState(State.READY, atTime);
			// Put the current tcb to the head of the ready queue
			tcbQueue.add(0, currentTcb);	
			tcbQueue.add(0, tcb);
			currentTcb = null;
			if (frame!=null ) {
				frame.update();
			}
			dispatch(atTime);
		}else{
			insertTcb(tcb);    		
			if (frame!=null ) {
				frame.update();
			}
			dispatch(atTime);
		}
    }
    

    ///////////////////////////////////
    //Accessor methods for the GUI only
    ///////////////////////////////////
    public Vector getQueue(){
    	return null;
    }


    /*
     * Add a new process to ready queue, run it, according to its priority and the system state
     * the preemption test is performed.
     */
    public void addTask(int atTime, Task task, int priority){
    	Tcb tcb = new Tcb(task, priority, atTime);
    	if(currentTcb == null){
    		tcbQueue.add(tcb);
    		dispatch(atTime);
    	}else{	
    		processPosition(tcb, atTime);
    	}
    }


    /* Mark the blocked process */
    public void taskBlocked(int atTime){
        currentTcb.setState(State.BLOCKED,atTime);
        currentTcb= null;
        dispatch(atTime);    
    }

    /* 
     *  Launch the process or schedualed into ready queue.
     *  The test preemption is based on priority.
     */
    public void ioCompleted(int atTime, Tcb tcb){
    	tcb.setState(State.READY, atTime);
    	if(currentTcb == null){
    		tcbQueue.add(tcb);
    		dispatch(atTime);
    	}else{
    		processPosition(tcb, atTime);
    	}
    }


    /* No quantum, process will be running until reach I/O burst */
    public void quantumExpired(int atTime){
    	
    }


    /* redispatch after a process terminate */
    public void taskTerminated(int atTime){
    	currentTcb.setState(State.TERMINATED, atTime);
        System.out.println(currentTcb.task().name()+" "+currentTcb.getStats());
        
        currentTcb= null;
        dispatch(atTime);
    }



    
    public Tcb currentTcb(){
    	return currentTcb;
    }

}
