///////////////////////////////////////////////////////////////////////////////
//
//State keeps track of the state of a task as it is managed by a dispatcher.
//State also records the starting and ending times for a task, and also
//the amount of time spent in each state, for later analysis
//
///////////////////////////////////////////////////////////////////////////////
public class State extends Object
{
    public final static int NIL= 0;
    public final static int READY= 1;
    public final static int RUNNING= 2;
    public final static int BLOCKED= 3;
    public final static int TERMINATED= 4;
    
    private static int nrTerminated= 0;
    private static int latestTerminateTime= 0;
    private static int totalTurnaround= 0;
    private static int maxTurnaround= 0;
    private static int totalWaiting= 0;
    private static int maxWaiting= 0;
    private static int totalCpuTime= 0;

    private static int nrResponses= 0;
    private static int totalResponse= 0;
    private static int maxResponse= 0;
    
    
    private int state;
    private int time;
    
    //Outsiders, read-only.  Do not change!
    public int startTime;
    public int terminateTime;
    public int readyTime;
    public int runTime;
    public int blockedTime;
    
    
    public State(int startTime)
    {
        this.startTime= startTime;
        
        state= READY;
        time= startTime;
        
        readyTime= 0;
        runTime= 0;
        blockedTime= 0;
        terminateTime= 0;
    }
    
    
    public static void resetGlobalStats()
    {
        nrTerminated= 0;
        latestTerminateTime= 0;
        totalTurnaround= 0;
        maxTurnaround= 0;
        totalWaiting= 0;
        maxWaiting= 0;
        totalCpuTime= 0;

        nrResponses= 0;
        totalResponse= 0;
        maxResponse= 0;
    }	
    
    public void set(int newState, int newTime)
    {
        if (newState<NIL || newState>TERMINATED) {
            throw new RuntimeException("Illegal state value");
        }
        
        switch (state) {
            case READY:
                if (newState==BLOCKED || newState==TERMINATED) {
                    throw new RuntimeException("Illegal state transition");
                }
                if (readyTime > 0) {
			nrResponses++;
                        int response = newTime-time;
			totalResponse+= response;
                        if (response > maxResponse){
                           maxResponse = response;
                        }
                }
                readyTime+= (newTime-time);
                break;
                
            case RUNNING:
                runTime+= (newTime-time);
                break;
                
            case BLOCKED:
                if (newState==TERMINATED) {
                    throw new RuntimeException("Illegal state transition");
                }
                
                blockedTime+= (newTime-time);
                break;
                
            case TERMINATED:
                throw new RuntimeException("Illegal state transition");
                
        }
        
        if (newState==TERMINATED) {
            terminateTime= newTime;
            accumulateStats();
        }
        
        state= newState;
        time= newTime;
    }
    
    
    
    public int get()
    {
        return state;
    }
    
    
    private void accumulateStats()
    {
        int turnaround= terminateTime-startTime;
        
        nrTerminated++;
        latestTerminateTime= terminateTime;
        
        totalTurnaround+= turnaround;
        if (turnaround>maxTurnaround) {
            maxTurnaround= turnaround;
        }
        
        totalWaiting+= readyTime;
        if (readyTime>maxWaiting) {
            maxWaiting= readyTime;
        }
        
        
        
        
        totalCpuTime+= runTime;
    }
    
    
    public String getStats()
    {
        StringBuffer sb= new StringBuffer();
        sb.append("\t");
        sb.append(Integer.toString(startTime));
        sb.append("\t");
        sb.append(Integer.toString(terminateTime));
        sb.append("\t");
        sb.append(Integer.toString(readyTime));
        sb.append("\t");
        sb.append(Integer.toString(runTime));
        sb.append("\t");
        sb.append(Integer.toString(blockedTime));
        return sb.toString();
    }
    
    
    
    public static String getGlobalStats()
    {
        StringBuffer sb= new StringBuffer();
        sb.append("Tasks completed= ");
        sb.append(Integer.toString(nrTerminated));
        sb.append("\n");
        sb.append("Max turnaround time= ");
        sb.append(Integer.toString(maxTurnaround));
        sb.append("\n");
        int averageTurnaround= (int)(((float)totalTurnaround)/nrTerminated);
        sb.append("Average turnaround time= ");
        sb.append(Integer.toString(averageTurnaround));
        sb.append("\n");
        sb.append("Max waiting time= ");
        sb.append(Integer.toString(maxWaiting));
        sb.append("\n");
        int averageWaiting= (int)(((float)totalWaiting)/nrTerminated);
        sb.append("Average waiting time= ");
        sb.append(Integer.toString(averageWaiting));
        sb.append("\n");
        int utilisation= (int)(100*((float)totalCpuTime)/latestTerminateTime);
        sb.append("CPU utilisation= ");
        sb.append(Integer.toString(utilisation));
        sb.append("%\n");
        return sb.toString();
    }
    
    public static String getResStats()
    {
        StringBuffer sb= new StringBuffer();
        sb.append("Max response time= ");
        sb.append(Integer.toString(maxResponse));
        sb.append("\n");
        int averageResponse= (int)(((float)totalResponse)/nrResponses);
        sb.append("Average response time= ");
        sb.append(Integer.toString(averageResponse));
        return sb.toString();
    }
    
    
    public String toString() 
    {
        switch (state) {
            case NIL:
                return "NIL";
                
            case RUNNING:
                return "RUNNING";
                
            case READY:
                return "READY";
                
            case BLOCKED:
                return "BLOCKED";
                
            case TERMINATED:
                return "TERMINATED";
                
            default:
                throw new RuntimeException("Impossible error");
        }
    }
}
