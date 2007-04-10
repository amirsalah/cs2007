///////////////////////////////////////////////////////////////////////////////
//
//Simple task implements a task that computes for a fixed interval, then
//performs I/O for a fixed period, and repeats this cycle until a preset
//amount of computation has been performed.  Then it stops.
//
///////////////////////////////////////////////////////////////////////////////
import java.util.StringTokenizer;

public class SimpleTask extends Object
implements Task
{     
    private String id;
    private int computeBurst;
    private int ioBurst;
    private int endTime;
    
    private int runTimer;
    private int computeTimer;
    
    public SimpleTask(String id, int computeBurst, int ioBurst, int endTime)
    {
        this.id= id;
        this.computeBurst= computeBurst;
        this.ioBurst= ioBurst;
        this.endTime= endTime;
    }
    
    
    
    public SimpleTask(StringTokenizer st)
    {
        if (st.countTokens()<4) {
            throw new RuntimeException(
                                       "SimpleTask expected ID COMPUTETIME IOTIME ENDTIME");
        }
        
        id= st.nextToken();
        
        String computeStr= st.nextToken();
        try{
            computeBurst= Integer.parseInt(computeStr);
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(
                                       "SimpleTask expected INTEGER, found: "+computeStr);
        }
        
        String ioStr= st.nextToken();
        try{
            ioBurst= Integer.parseInt(ioStr);
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(
                                       "SimpleTask expected INTEGER, found: "+ioStr);
        }
        
        String runStr= st.nextToken();
        try{
            endTime= Integer.parseInt(runStr);
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(
                                       "SimpleTask expected INTEGER, found: "+runStr);
        }
    }
    
    
    //Initalise this task
    public void initialise()
    {
        runTimer= endTime;
        computeTimer= computeBurst;
    }
    
    
    
    //Perform one tick's worth of processing
    public void tick(Cpu cpu)
    {
        runTimer--;
        if (runTimer<=0) {
            //Task completed
            Osim.jobCompleted(this);
            cpu.osExit();
            return;
        }
        
        computeTimer--;
        if (computeTimer==0) {
            computeTimer= computeBurst;
            //Time for some I/O...
            cpu.osIoRequest(ioBurst);
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
