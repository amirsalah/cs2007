/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 6th, May 2007             */
/***********************************/
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ChatKeyImpl implements ChatKey{
	// The existing IDs, used to prevent from duplicated IDs
	private static Set<Integer> validIDs = new HashSet<Integer>();
	private int ID;
	private boolean amPrivileged;
	
	public ChatKeyImpl(boolean privilege){
		Random ran = new Random();
		ID = ran.nextInt();
		
		while(validIDs.contains(ID)){
			ID = ran.nextInt();
		}
		
		amPrivileged = privilege;
	}
	
	public boolean amPrivileged(){
		return amPrivileged;
	}
	
	
}
