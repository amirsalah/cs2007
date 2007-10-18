/**
 * 
 */

/**
 * @author Xingxing SONG
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class EventSequenceImpl implements EventSequence {

	private  List<Object> m_lstEvent = new ArrayList<Object>() ;
	
	private FsaGui m_fsaGui ;
	public void setFsaGui(FsaGui fsaGui)
	{
		m_fsaGui = fsaGui ;
	}
	//Read in a sequence of events from a stream
    //For the format of the input, see the practical specification sheet
    //The data read from the file replaces any information already stored
    // The order of the events in the stream must be retained.
    public void read(InputStream is)
    {
    	m_lstEvent.clear() ;
    	//int c;
    	try{
    		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    		int iLineNum = 1;
			String strLine;
			String strItem;
			while ((strLine = reader.readLine()) != null) {
				m_fsaGui.output("read : " + strLine);
				StringTokenizer st = new StringTokenizer(strLine);
				if (st.hasMoreTokens()) {
					strItem = st.nextToken();
					String str = new String(strItem);
					m_lstEvent.add(str);
				}
			}
		}
    	catch (IOException e) {     
    		System.err.println("Caught IOException: " 
	                                + e.getMessage());   			            
	    }
    }
      
    //Return a list of all events in this sequence, in order!
    public List<Object> getEvents()
    {
    	return m_lstEvent ;
    }	
    
    // Print the list of events, one per line.
    public String toString()
    {
    	Iterator<Object> Iter = m_lstEvent.iterator() ;
    	String str = new String() ;
    	while(Iter.hasNext() )
    	{
    		str = str + (String)(Iter.next()) + "\n" ;
    	}
    	return str ;
    }
}
