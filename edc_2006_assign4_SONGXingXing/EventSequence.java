
import java.io.InputStream;
import java.util.List;

interface EventSequence
{
    //Read in a sequence of events from a stream
    //For the format of the input, see the practical specification sheet
    //The data read from the file replaces any information already stored
    // The order of the events in the stream must be retained.
    public void read(InputStream is);
      
    //Return a list of all events in this sequence, in order!
    public List<Object> getEvents();
    
    // Print the list of events, one per line.
    public String toString();
}