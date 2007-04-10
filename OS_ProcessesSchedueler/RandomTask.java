///////////////////////////////////////////////////////////////////////////////
//
//Random task implements a task that computes for an interval, then
//performs I/O for an interval.  The intervals are chosen randomly within
//limits specified when the task is created.  The cycle of computation and I/O
//repeats until a preset amount of computation has been performed.  
//Then it stops.
//
///////////////////////////////////////////////////////////////////////////////

/*****************************************************************************/
/* Author: Bo CHEN															 */
/* Student ID: a1139520														 */
/* Last modified Date: 11, Sep. 2006										 */
/*****************************************************************************/

import java.util.Random;
import java.util.StringTokenizer;

public class RandomTask extends Object
implements Task
{     
    //We fix the seed, so that all runs will produce identical results
    private static Random rng= new Random(314159265358979L);
    
    private String id;
    private int computeBurst;
    private int ioBurst;
    private int endTime;
    
    private int computeTimer;
    private int runTimer;
    
    public RandomTask(String id, int computeBurst, int ioBurst, int endTime)
    {
        this.id= id;
        this.computeBurst= computeBurst;
        this.ioBurst= ioBurst;
        this.endTime= endTime;
    }
    
    
    
    public RandomTask(StringTokenizer st)
    {
        if (st.countTokens()<4) {
            throw new RuntimeException(
                                       "Expected ID COMPUTETIME IOTIME RUNTIME");
        }
        
        id= st.nextToken();
        
        String computeStr= st.nextToken();
        try{
            computeBurst= Integer.parseInt(computeStr);
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(
                                       "Expected INTEGER, found: "+computeStr);
        }
        
        String ioStr= st.nextToken();
        try{
            ioBurst= Integer.parseInt(ioStr);
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(
                                       "Expected INTEGER, found: "+ioStr);
        }
        
        String endStr= st.nextToken();
        try{
            endTime= Integer.parseInt(endStr);
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(
                                       "Expected INTEGER, found: "+endStr);
        }
    }
    
    
    
    //Initialise this task
    public void initialise()
    {
        runTimer= endTime;
        computeTimer= 1+(int)(computeBurst*rng.nextFloat());
    }
    
    
    
    //Perform one tick's worth of processing
    public void tick(Cpu cpu)
    {
        runTimer--;
        if (runTimer==0) {
            //Task completed
            Osim.jobCompleted(this);
            cpu.osExit();
            return;
        }
        
        computeTimer--;
        if (computeTimer==0) {
            computeTimer= 1+(int)(computeBurst*rng.nextFloat());
            
            //Time for some I/O...
            cpu.osIoRequest(1+(int)(ioBurst*rng.nextFloat()));
            return;
        }
    }
    
    
    //Get task name
    public String name()
    {
        return id;
    }
    
    
    public String toString()
    {
        return "Task("+id+" "+computeBurst+" "+ioBurst+" "+endTime+")";
    }
}
