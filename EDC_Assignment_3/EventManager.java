import java.io.IOException;
import java.io.Reader;


public class EventManager implements EventSeq {
    //Read an event sequence from the given Reader.
    public void read(Reader r)
      throws IOException, EventFileException{
    	
    }

    //Reset the event sequence to its starting position
    public void reset(){
    	
    }


    //Return the next event in the sequence
    //Returns null, if there are no more events
    public String nextEvent(){
    	
    }


    //Returns a string representation of this event sequence, with the
    //event-names separated by commas, all enclosed in [  ] characters.
    //The NEXT event to be used is shown by  ->
    //For example: 
    //  [->ava,avb]      When the next event is the first event
    //  [eva,evb,evc,evd->eve,evf,evg]   ...In the middle...
    //  [eva,evb->]       ...At the end
    public String toString(){
    	
    }
}
