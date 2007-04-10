///////////////////////////////////////////////////////////////////////////////
//
//Osim is the main controlling program of the simulator
//It processes command line arguments then it reads in the joblist, and stores
//it in order of starting time.
//Once the simulation begins, it calls the OS from time to time to start jobs.
//When the simulation is complete, it prints statistics for the run
//
///////////////////////////////////////////////////////////////////////////////
import java.lang.Thread;
import java.util.Vector;

public class Osim extends Object
{
    private static final boolean GUI_DEFAULT= false;
    
    private static Cpu cpu;
    private static IoDevice ioDevice;
    private static Os os;
    private static boolean hasGui;
    private static boolean Quantumset = false;
    private static Dispatcher dispatcher;
    
    private static int time;
    private static Vector<Object> jobs;
    private static Vector<Object> liveTasks;
    private static OsimFrame osimFrame;
    private static boolean running;
    
    public static void main(String[] args)
    {
        //Process command line params
        doParams(args);
        
        //Read in the jobs to be executed
        readJobs();
        
        Osim osim= new Osim(hasGui);
    }
    
    
    private Thread thread;
    private boolean quit;
    private Vector<Object> jobList;
    
    private Osim(boolean hasGui)
    {
        running= false;
        
        //Layer 0: Create the system...
        cpu= new Cpu();
        ioDevice= new IoDevice(cpu);
        
        //Layer 1: Create the O/S
        os= new Os(ioDevice);
        cpu.installOs(os);
        
        //Layer2: Configure the O/S
        os.setDispatcher(dispatcher);
        dispatcher.setCpu(cpu);
        
        //Enable gui things
        cpu.enableGui(hasGui);
        ioDevice.enableGui(hasGui);
        dispatcher.enableGui(hasGui);
        
        //Initialise the operating system
        reset();
        
        //Write dispatcher name to output
        System.out.println("Using "+dispatcher.getName());
        System.out.println("name\tstart\tend\tready\trunning\tblocked");
        
        //Run the simulation
        if (hasGui) {
            osimFrame= new OsimFrame("Osim",this);
            osimFrame.setVisible(true);
            
            quit= false;
            while (!quit) {
                try{
                    Thread.sleep(50);
                }catch(InterruptedException ie) {
                    System.exit(1);
                    return;
                }
            }
            
        }else{
            
            while (liveTasks.size()+jobList.size()>0) {
                //There are still jobs running, or yet to be run
                step();
            }
        }
		
        printStats();
        
        System.exit(0);
        return;
    }
    
    
    
    public void reset()
    {
        running= false;
        cpu.reset();
        ioDevice.reset();
        os.reset();
        State.resetGlobalStats();
        
        // Copy the jobs to joblist
        jobList= new Vector<Object>(jobs);
        
        liveTasks= new Vector<Object>();
        time= 0;
        if (osimFrame!=null) {
            osimFrame.updateTime(time);
            osimFrame.updatePending(jobList.size());
            osimFrame.updateLive(liveTasks.size());
        }
    }
    
    
    public void step()
    {
        //Check for new jobs starting at this time
        while (jobList.size()>0 && 
               ((Job)jobList.get(0)).startTime<=time) {
            //Its time to start a new job
            
            Job job= (Job)jobList.remove(0);
            job.task.initialise();
            os.startTask(job.task,job.priority);
            
            //Remember that the task is now running
            liveTasks.add(job.task);
            
            if (osimFrame!=null) {
                osimFrame.updatePending(jobList.size());
                osimFrame.updateLive(liveTasks.size());
            }
        }
        
        //Advance time in various things
        //We use two-phase clocking to avoid problems with sequencing
        //of time in separate modules
        cpu.tickPhase1();
        ioDevice.tickPhase1();
        
        os.timerInterrupt();
        
        cpu.tickPhase2();
        ioDevice.tickPhase2();
        
        time++;
        if (osimFrame!=null) {
            osimFrame.updateTime(time);
        }
    }
    
    
    
    public void start()
    {
        running= true;
    }
    
    
    public void stop()
    {
        running= false;
    }
    
    
    public void exit()
    {
        quit= true;
    }
    
    
    
    //Process command parameters
    private static void doParams(String[] args)
    {
        hasGui= GUI_DEFAULT;
        dispatcher= null;
        
        int ix=0;
        while (ix<args.length) {
            if (args[ix].equals("-gui")) {
                hasGui= !hasGui;
            }
            
            if (args[ix].equals("-rtc")) {
                dispatcher= new RtcDispatcher();
            }
            if (args[ix].equals("-fcfs")) {
                dispatcher= new FcfsDispatcher();
            }
            if (args[ix].equals("-npp")) {
                dispatcher= new NppDispatcher();
            }
            if (args[ix].equals("-pp")) {
                dispatcher= new PpDispatcher();
            }
            if (args[ix].equals("-rr")) {
                dispatcher= new RrDispatcher();
            }
            if (args[ix].equals("-cb")) {
                dispatcher= new CbDispatcher();
            }
            if (args[ix].equals("-q")) {
                ix++;
                dispatcher.InitQuantum(Integer.parseInt(args[ix]));
                Quantumset = true;
            }
            ix++;
        }
        
        if (dispatcher==null) {
            System.err.println(
                               "You MUST specify a dispatcher: -rtc -fcfs -npp -pp -rr -cb");
            System.exit(1);
            return;
        }
    }
    
    
    
    private static void readJobs()
    {
        jobs= new Vector<Object>();
        JobReader jobReader= new JobReader();
        while (jobReader.hasMore()) {
            Job job= jobReader.getNext();
            //Insert it into the job list in  start-time order
            int jobix= 0;
            while (jobix<jobs.size() && sooner((Job)(jobs.get(jobix)),job)){
                jobix++;
            }
            jobs.add(jobix,job);
        }
    }
    
    
    
    private static boolean sooner(Job j1, Job j2) {
        if (j1.startTime<j2.startTime) {
            return true;
        }
        
        if (j1.startTime>j2.startTime) {
            return false;
        }
        
        //Equal start times, so...
        return j1.priority<=j2.priority;
    }
    
    
    
    //Called when a task completes - needed so that Osim knows whe to stop
    public static void jobCompleted(Task task)
    {
        //A task has announced its death, remove it
        liveTasks.remove(task);
        if (osimFrame!=null) {
            osimFrame.updateLive(liveTasks.size());
        }
    }
    
    
    private static void printStats()
    {
        System.out.println();
        System.out.println(State.getGlobalStats());
        if (Quantumset) {
                System.out.println(State.getResStats());
                System.out.println(Os.getNoCS());
        }
    }
}
