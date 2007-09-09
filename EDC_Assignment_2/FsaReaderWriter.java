/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 3rd, Sept 2007
=========================================================*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class FsaReaderWriter implements FsaIo{
    //This class handles reading and writing FSA representations as 
    //described in the practical specification

    //Read the description of a finite-state automaton from the 
    //InputStream, is, and transfers it to Fsa, f.
	
	public FsaReaderWriter(){
		
	}
	
    public void read(Reader r, Fsa f) 
    throws IOException, FsaFormatException{
    	BufferedReader reader = new BufferedReader(r);
    	String line = null;
    	
    	StringTokenizer tokens = null;
    	ArrayList<String> recordItems = new ArrayList<String>();
    	
    	String fromState = null;
    	String toState = null;
    	String event = null;
    	String output = null;
    	String initialState = null;
    	
    	//Retrieve all lines
    	while((line = reader.readLine()) != null){
    		tokens = new StringTokenizer(line);
    		recordItems.clear(); //New record
    		
    		while(tokens.hasMoreTokens()){
    			recordItems.add(tokens.nextToken());
    		}
    		
    		//Check if the number of items is valid
    		int numItems = recordItems.size();
    		if(numItems != 2 && numItems != 4 && numItems != 5){
    			continue;
    		}
    		
    		//Store states into fsa
    		if(recordItems.get(0).equals("STATE")){
    			f.newState(recordItems.get(1), Integer.valueOf(recordItems.get(2)), Integer.valueOf(recordItems.get(3)));
    		}
    		
    		//Store transitions into fsa
    		if(recordItems.get(0).equals("TRANSITION")){
    			fromState = recordItems.get(1);
    			toState = recordItems.get(4);
    			event = recordItems.get(2);
    			output = recordItems.get(3);
    			f.newTransition((StateImpl)f.findState(fromState), (StateImpl)f.findState(toState), event, output);
    		}
    		
    		//Store initial states
    		if(recordItems.get(0).equals("INITIAL")){
    			initialState = recordItems.get(1);
    			f.addInitialState(f.findState(initialState));
    		}
    	}
    	
    	reader.close();
    }
    
    
    //Write a representation of the Fsa, f, to the OutputStream, os.
    public void write(Writer w, Fsa f)
    throws IOException{
    	BufferedWriter writer = new BufferedWriter(w);
    	writer.append(f.toString());
    	writer.flush();
    	writer.close();
    }
}
