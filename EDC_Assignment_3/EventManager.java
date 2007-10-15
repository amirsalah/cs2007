import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JTextArea;


public class EventManager implements EventSeq {
	private ArrayList<String> events = new ArrayList<String>();
	private int eventNum = 0;
	private JTextArea messagesArea = null; //Used to display messages in the UI
	
	public EventManager(JTextArea messagesArea){
		this.messagesArea = messagesArea;
	}
	
    //Read an event sequence from the given Reader.
    public void read(Reader r)
      throws IOException, EventFileException{
    	BufferedReader reader = new BufferedReader(r);
    	String line = null;
    	StringTokenizer tokens = null;
    	
    	String eventName = null;
    	
    	//Retrieve all lines
    	while((line = reader.readLine()) != null){
    		tokens = new StringTokenizer(line);
    		
    		// Comment line, ignored
    		if(line.length() > 0 && line.indexOf("#") == 0){
    			continue;
    		}
    		
    		// Get the 1st element from current line
    		if(tokens.hasMoreTokens()){
    			eventName = tokens.nextToken();
    			if(IsValidEventName(eventName)){
    				events.add(eventName);
    				messagesArea.append("Reading event: " + eventName + "\n");
    			}else{
    				messagesArea.append("invalid event: " + eventName + "\n");
    			}
    		}
    	}
    }
    
    /*
     * Check if the given even name is valid
     */
    private boolean IsValidEventName(String eventName){
    	//Epsilon transition event
    	if( (eventName.length() == 1) && eventName.equalsIgnoreCase("?") ){
    		return true;
    	}
    	
    	if( eventName == null){
    		return false;
    	}
    	
    	for(int i=0; i<eventName.length(); i++){
    		if( !Character.isLetter(eventName.charAt(i)) ){
    			return false;
    		}
    	}
    	
    	return true;
    }

    //Reset the event sequence to its starting position
    public void reset(){
    	eventNum = 0;
    }


    //Return the next event in the sequence
    //Returns null, if there are no more events
    public String nextEvent(){
    	if(eventNum >= events.size() - 1){
    		return null;
    	}else{
    		String nextEvent = events.get(eventNum);
    		eventNum++;
    		return nextEvent;
    	}
    }


    //Returns a string representation of this event sequence, with the
    //event-names separated by commas, all enclosed in [  ] characters.
    //The NEXT event to be used is shown by  ->
    //For example: 
    //  [->ava,avb]      When the next event is the first event
    //  [eva,evb,evc,evd->eve,evf,evg]   ...In the middle...
    //  [eva,evb->]       ...At the end
    public String toString(){
    	String output = null;
    	
    	// The NEXT event is the 1st event
    	if(eventNum == 0){
    		output += "[->";
    		for(int i=0; i<events.size(); i++){
    			output += events.get(i);
    			output += ",";
    		}
    		output = output.substring(0, output.length()-2); // eliminate the last comma
    		output += "]";
    	}
    	
    	// The NEXT event is in the middle
    	if(eventNum > 0 && eventNum < (events.size()-1)){
    		output += "[";
    		for(int i=0; i<eventNum; i++){
    			output += events.get(i);
    			output += ",";
    		}
    		
    		output = output.substring(0, output.length()-2); // eliminate the last comma
    		
    		output += "->";
    		
    		for(int i=eventNum; i<events.size(); i++){
    			output += events.get(i);
    			output += ",";
    		}
    		
    		output = output.substring(0, output.length()-2); // eliminate the last comma
    	}
    	
    	// The NEXT event is at the end
    	if(eventNum == (events.size()-1)){
    		output += "[";
    		for(int i=0; i<events.size(); i++){
    			output += events.get(i);
    			output += ",";
    		}
    		
    		output = output.substring(0, output.length()-2); // eliminate the last comma
    		output += "->]";
    	}
    	
    	return output;
    }
}
