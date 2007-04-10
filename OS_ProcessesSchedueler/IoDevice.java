///////////////////////////////////////////////////////////////////////////////
//
//This module handles the delays caused by I/O operations
//
//It maintains a circular list of Task Control Blocks, each with an associated
//timer.  The timer list contains the time difference from one event to the 
//next.  New entries are inserted into the correct position in the list.
//The entry at the head of the list is decremented as each tick occurs.  When
//the entry reaches zero, it is removed.
//
///////////////////////////////////////////////////////////////////////////////
public class IoDevice extends Object
{
    public static final int MAXENTRIES= 1000;
    
    public int[] deltas;
    public Tcb[] tcbs;
    public int head;
    public int tail;
    public int nrEntries;
    
    private IoFrame iof;
    private Cpu cpu;
    
    public IoDevice(Cpu cpu)
    {
        this.cpu= cpu;
        
        iof= null;
        reset();
    }
    
    
    
    public void reset()
    {	
        deltas= new int[MAXENTRIES];
        tcbs= new Tcb[MAXENTRIES];
        head= 0;
        tail= 0;
        nrEntries= 0;
    }
    
    
    
    public void enableGui(boolean hasGui)
    {
        iof= null;
        if (hasGui) {
            iof= new IoFrame(this);
        }
    }
    
    
    
    //Add a new entryy to the timer list
    public void startTransfer(Tcb nextTcb, int nextDelta)
    {
        if (nrEntries==MAXENTRIES) {
            throw new RuntimeException("Disaster!");
        }
        
        //Find right place to insert
        int i= head;
        while (i!=tail && nextDelta>deltas[i]) {
            nextDelta= nextDelta-deltas[i];
            
            i++;
            if (i>=MAXENTRIES) {
                i= 0;
            }
        }
        
        if (i!=tail) {
            //Adjust next timer;
            deltas[i]= deltas[i]-nextDelta;
        }
        
        //Move existing entries up the array
        while (i!=tail) {
            int delta= deltas[i];
            Tcb tcb= tcbs[i];
            
            deltas[i]= nextDelta;
            tcbs[i]= nextTcb;
            
            nextDelta= delta;
            nextTcb= tcb;
            
            i++;
            if (i>=MAXENTRIES) {
                i= 0;
            }
        }
        
        deltas[i]= nextDelta;
        tcbs[i]= nextTcb;
        tail++;
        if (tail>=MAXENTRIES) {
            tail= 0;
        }
        
        nrEntries++;
    }
	
    
    //Decrement the timer at the head of the list
    public void tickPhase1()
    {
        if (nrEntries==0) {
            //No pending I/O
            return;
        }
        
        deltas[head]--;
    }
    
    //Delink the entry, if the timer has expired
    public void tickPhase2()
    {
        while (nrEntries>0 && deltas[head]==0) {
            //I/O operation completed
            cpu.ioInterrupt(tcbs[head]);
            
            //Adjust pointer
            head++;
            if (head>=MAXENTRIES) {
                head= 0;
            }
            nrEntries--;
        }
        
        //Make changes visible
        if (iof!=null) {
            iof.update();
        }
    }
    
    
    
    //Print the list out (debugging tool)
    public String dump()
    {
        StringBuffer sb= new StringBuffer();
        sb.append("head=");
        sb.append(Integer.toString(head));
        sb.append(" tail=");
        sb.append(Integer.toString(tail));
        sb.append(" nrEntries=");
        sb.append(Integer.toString(nrEntries));
        sb.append(" queue=[");
        int p= head;
        while (p!=tail) {
            sb.append("(");
            sb.append(Integer.toString(deltas[p]));
            sb.append(",");
            sb.append(tcbs[p]);
            sb.append(")");
            
            p++;
            if (p>=MAXENTRIES) {
                p= 0;
            };
        }
        sb.append("]");
        return sb.toString();
    }
}
