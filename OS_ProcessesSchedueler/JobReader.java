////////////////////////////////////////////////////////////////////////////////
//
//Job reader is used to read in the data about each job to be executed 
//in the system
//
////////////////////////////////////////////////////////////////////////////////
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class JobReader extends Object
{
    private BufferedReader stdin= new BufferedReader(
                                                     new InputStreamReader(System.in));
    private String line;
    
    public JobReader()
    {
        try{
            line= stdin.readLine();
        }catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    
 
    public boolean hasMore()
    {
    	if ((line == null) || (line.length() < 5))
 			return false;
    	else 
    		return true;
//        return line!=null;
    }
    
    
    
    public Job getNext()
    {
        StringTokenizer st= new StringTokenizer(line);
        
        if (st.countTokens()<3) {
            throw new RuntimeException(
                                       "Expected TIME Priority TASK params, found: "+line);
        }
        
        String startStr= st.nextToken();
        int startTime;
        try{
            startTime= Integer.parseInt(startStr);
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(
                                       "Expected INTEGER, found: "+startStr);
        }
        
        String priorityStr= st.nextToken();
        int priority;
        try{
            priority= Integer.parseInt(priorityStr);
        }catch(NumberFormatException nfe) {
            throw new RuntimeException(
                                       "Expected INTEGER, found: "+priorityStr);
        }
        
        //Read next input line;
        try{
            line= stdin.readLine();
        }catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
        
        //Really, we should do this in a more general way, but it's 
        //probably not worth the effort
        String taskStr= st.nextToken();
        if (taskStr.equals("random")) {
            Task task= new RandomTask(st);
            return new Job(startTime,priority,task);
        }
        
        if (taskStr.equals("simple")) {
            Task task= new SimpleTask(st);
            return new Job(startTime,priority,task);
        }
        
        throw new RuntimeException("Unknown task type: "+taskStr);
    }
}
