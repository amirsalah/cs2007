///////////////////////////////////////////////////////////////////////////////
//
//Cb dispatcher
//
//Tasks are entered into the ready queue in (decreasing) credit order
//The initial value of credits is deteremined by the task's priority
//The dispatcher runs the task with the larg4est number of credits until it 
//blocks, or its time quantum expires.  If the quantum expires, the task loses
//one credit
//When all runnable tasks have zero credits, the dispatcher recredits all 
//whether runnable or not, according to the formula 
//   credits= credits/2 + Priority
//When a task's I/O completes, the task is put into the ready queue according
//to its credits
//
//David Knight Aug-03
//
///////////////////////////////////////////////////////////////////////////////

import java.util.Vector;
import java.util.HashMap;

public class CbDispatcher extends Object
implements Dispatcher, DispatcherObserver
{
    private int Quantum= 10;
    private Vector<Object> tcbQueue;
    private Tcb currentTcb;
    private DispatcherFrame frame;
    private Task idleTask;
    private Cpu cpu;
    
    private Vector<Tcb> tcbZeroCredit;
    private Vector<Tcb> tcbBlocked;
    
    //Conversion from priority to credits
    private static final int[] base_credits= {16,12,10,8,6,4,3,2,1};
    
    public CbDispatcher(){
        frame= null;
        idleTask= new IdleTask();
    }
    
    
    public void setCpu(Cpu cpu){
        this.cpu= cpu;
    }
    
    
    public String getName(){
        return "CbDispatcher";
    }
    
    
    public void InitQuantum(int n){
        this.Quantum= n;
    }
    
    /* Initialize the dispatcher to the default state */
    public void reset(int atTime){
        if (frame!=null) {
            frame.reset();
        }
        tcbQueue= new Vector<Object>();
        tcbZeroCredit = new Vector<Tcb>();
        tcbBlocked = new Vector<Tcb>();
        currentTcb= null;
        dispatch(atTime);
    }
    
    
    public void enableGui(boolean hasGui){
        frame= null;
        if (hasGui) {
            frame= new DispatcherFrame("CbDispatcher",this);
        }
    }
    
    /* 
     * Launch the process at the head of ready queue if there is no process running
     * Do recrediting if the vector containing 0 credits processes is not empty && ready queue is empty
     *    This is the case that all processess are either in block or have 0 credit.
     */
    private void dispatch(int atTime){
        if ( (tcbQueue.size()==0) && (currentTcb == null) ) {      	
        	// Recredit when no runnable process has any credits.
        	if( !tcbZeroCredit.isEmpty() ){
        		recredit();
        	}else{
        		// Nothing to run...
        		currentTcb= null;
            	cpu.run(idleTask,Cpu.FOREVER);
            	if (frame!=null) {
                	frame.update();
            	}
            	return;
        	}
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
    
    private void recredit(){
    	Tcb tempTcb;
    	int newCredits = 0;
    	
    	// Recredit the zero credits processes in the vector
    	// Clear the Vector containing 0 credit after recrediting
    	for(int i = 0; i < tcbZeroCredit.size(); i++){
    		tempTcb = tcbZeroCredit.get(i);
    		tempTcb.setCredits(base_credits[tempTcb.priority()]);
    		tempTcb.setQuantum(Quantum);   // Initial the quantum after recredit
    		insertTcb(tempTcb);
    	}
    	
    	tcbZeroCredit.clear();
    	
    	// Recredit blocked processes in the hash map
    	for(int i = 0; i < tcbBlocked.size(); i++){
    		tempTcb = tcbBlocked.get(i);
    		newCredits = tempTcb.credits()/2 + base_credits[tempTcb.priority()];
    		tcbBlocked.get(i).setCredits(newCredits);
    	}
    }
    
    ///////////////////////////////////
    //Accessor methods for the GUI only
    ///////////////////////////////////
    public Vector getQueue(){
        return tcbQueue;
    }
    
    
    /* 
     * Insert a task to the tcbQueue by its credits
     * If the credits are the same, fcfs algorithm is performed. 
     */
    private void insertTcb(Tcb tcb){
    	Tcb tcbInQueue;
    	
    	if ( tcbQueue.isEmpty() ){
    		tcbQueue.add(0, tcb);
    	}else{
    		int queueLength = tcbQueue.size();
    		for( int i = 0; i < queueLength; i++){
    			tcbInQueue = (Tcb)tcbQueue.get(i);
    			if (tcb.credits() > tcbInQueue.credits()){
    				tcbQueue.add(i, tcb);
    				return;
    			}
    		}
    		
    		// Insert to tail of the tcb queue.
    		tcbQueue.add(queueLength, tcb);
    		return;
    	}
    }
    
    /* 
     * Perform preemption test, the process will preempt the current running process
     * or insert into the ready queue, based on its priority AND credits
     */
    private void processPosition(Tcb tcb, int atTime){
		// Note: the test of preemption is based on credits 
		if(tcb.credits() > currentTcb.credits()) {
			currentTcb.setState(State.READY, atTime);
			currentTcb.setQuantum(cpu.getRemainingQuantum());
			tcbQueue.add(0, currentTcb);
			tcbQueue.add(0, tcb);    // Note: put the current tcb to the head of ready queue
			currentTcb = null;
			if (frame!=null ) {
				frame.update();
			}
		}else{
			insertTcb(tcb);
			if (frame!=null ) {
				frame.update();
			}
		}
    }
    
    /*
     *  Add a new process to ready queue if its priority is lower than current process' priority
     *  otherwise, preempt current running process
     */
    public void addTask(int atTime, Task task, int priority){
    	Tcb tcb = new Tcb(task, priority, atTime);
    	tcb.setCredits(base_credits[tcb.priority()]);    // Allocate the initial credits
    	tcb.setQuantum(Quantum);
    	if(currentTcb == null){
    		tcbQueue.add(tcb);
    	}else{
    		processPosition(tcb, atTime);
    	}
    	dispatch(atTime);
    }
    
    
    /* 
     * Keep track of the blocked processes
     * Note: the blocked process do NOT decrease its credits, unless its quantum expired
     */
    public void taskBlocked(int atTime){
        currentTcb.setState(State.BLOCKED,atTime);
        currentTcb.setQuantum(Quantum);
        
        // Put the current tcb into a vector containing only blocked processes
        tcbBlocked.add(currentTcb);       
        currentTcb= null;
        dispatch(atTime);
    }

    /*
     * Remove the tcb from the vector containing blocked processes.
     * Preempt current running process when tcb's priority is higher.
     */
    public void ioCompleted(int atTime, Tcb tcb){
        tcb.setState(State.READY, atTime);        
        tcbBlocked.remove(tcb);   // Remove the tcb from the vector containning blocked processes 
        
        tcb.setQuantum(Quantum);

    	if(currentTcb == null){
    		tcbQueue.add(tcb);
    	}else{
    		processPosition(tcb, atTime);
    	}
		dispatch(atTime);
    }
    
    
    /* Decrease it credits and the process goes back to the ready queue for recrediting */
    public void quantumExpired(int atTime){
        currentTcb.setQuantum(Quantum);
        currentTcb.decrementCredits();
        currentTcb.setState(State.READY,atTime);
        
        // Add the tcb to ZeroCredit vector when its credits decreased to 0
        if( currentTcb.credits() == 0){
        	tcbZeroCredit.add(currentTcb);
        	currentTcb = null;
        }else{
        	insertTcb(currentTcb);   // Add to ready queue when its credits is not 0
        	currentTcb= null;
        	if (frame!=null ) {
        		frame.update();
        	}
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
    
    
    
    public Tcb currentTcb(){
        return currentTcb;
    }
    
}
