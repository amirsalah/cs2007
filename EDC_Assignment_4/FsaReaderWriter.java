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
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class FsaReaderWriter implements FsaIo{
    //This class handles reading and writing FSA representations as 
    //described in the practical specification

    //Read the description of a finite-state automaton from the 
    //InputStream, is, and transfers it to Fsa, f.
	
	private enum dataPattern{
		lowerCase, upperCase
	}
	dataPattern inputFormat = dataPattern.lowerCase;
	
	public FsaReaderWriter(){
		
	}
	
    public void read(Reader r, Fsa f) 
    throws IOException, FsaFormatException{
    	BufferedReader reader1 = new BufferedReader(r);
    	String line = null;
    	
    	StringTokenizer tokens = null;
    	ArrayList<String> recordItems = new ArrayList<String>();
    	
    	String fromState = null;
    	String toState = null;
    	String event = null;
    	String output = null;
    	String initialState = null;

    	//Retrieve all lines
    	while((line = reader1.readLine()) != null){
    		tokens = new StringTokenizer(line);
    		recordItems.clear(); //New record
    		
    		while(tokens.hasMoreTokens()){
    			recordItems.add(tokens.nextToken());
    		}
    		
    		//Check if the number of items is valid
    		int numItems = recordItems.size();
    		if(numItems != 4 && numItems != 2 && numItems != 5){
    			continue;
    		}
    		
    		//Store states into fsa
    		if(recordItems.get(0).equalsIgnoreCase("STATE")){
    			f.newState(recordItems.get(1), Integer.valueOf(recordItems.get(2)), Integer.valueOf(recordItems.get(3)));
    		}
    		   		
    		//Store transitions into fsa
    		if(recordItems.get(0).equalsIgnoreCase("TRANSITION")){
    			fromState = recordItems.get(1);
    			toState = recordItems.get(4);
    			event = recordItems.get(2);
    			output = recordItems.get(3);
    			f.newTransition(f.findState(fromState), f.findState(toState), event, output);
    		}
    		
    		//Store initial states
    		if(recordItems.get(0).equalsIgnoreCase("INITIAL")){
    			initialState = recordItems.get(1);
    			f.addInitialState(f.findState(initialState));
    			if(recordItems.get(0).equals("INITIAL")){
    				inputFormat = dataPattern.upperCase;
    			}
    		}
    	}
    	
    	// Reset FSA after reading all the initial data
    	((FsaImpl)f).reset();
    }
    
    
    //Write a representation of the Fsa, f, to the OutputStream, os.
    public void write(Writer w, Fsa f)
    throws IOException{
    	BufferedWriter writer = new BufferedWriter(w);
    	String stateKeyWord = "STATE";
    	String transitionKeyWord = "TRANSITION";
    	String initialKeyWord = "INITIAL";
    	
    	String LinesOutput = "";
    	
    	if(inputFormat == dataPattern.lowerCase){
    		stateKeyWord = stateKeyWord.toLowerCase();
    		transitionKeyWord = transitionKeyWord.toLowerCase();
    		initialKeyWord = initialKeyWord.toLowerCase();
    	}
    	
    	//Output all states
    	Set<State> allStates = null;
    	allStates = f.getStates();
    	State aState = null;
    	Iterator itr = allStates.iterator();
    	while(itr.hasNext()){
    		aState = (State)itr.next();
    		LinesOutput = LinesOutput + stateKeyWord + " " + aState.getName() + " " 
    			+ aState.getXCoord() + " " + aState.getYCoord() + "\n";
    	}
    	
    	//Output transitions
    	itr = ((FsaImpl)f).GetTransitions().iterator();
    	Transition aTransition = null;
    	while(itr.hasNext()){
    		aTransition = (Transition)itr.next();
    		LinesOutput = LinesOutput + transitionKeyWord + " " + aTransition.fromState().getName() + " "
    			+ aTransition.eventName() + " " + aTransition.output() + " " + aTransition.toState().getName() + "\n";
    	}
    	
    	itr = f.getInitialStates().iterator();
    	State initialState = null;
    	//Output initial states
    	while(itr.hasNext()){
    		initialState = (State)itr.next();
    		LinesOutput = LinesOutput + initialKeyWord + " " + initialState.getName() + "\n";
    	}
    	
    	writer.write(LinesOutput);
    	writer.flush();
    }
}
