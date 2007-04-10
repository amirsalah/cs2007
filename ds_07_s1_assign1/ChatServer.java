/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 22th, Mar. 2007           */
/***********************************/
import java.rmi.RMISecurityManager;

public class ChatServer {
	
	public static void main(String[] args) throws java.rmi.RemoteException{

		Chat ca = new ChatImpl();
		
	    // Assign security manager
//	    if (System.getSecurityManager() == null)
//	    {
//	        System.setSecurityManager   (new RMISecurityManager());
//	    }
	    
		try{
			java.rmi.Naming.rebind(args[0], ca);
		}
		catch(java.net.MalformedURLException e){
			System.out.println("Bad URL");
		}
		catch(java.rmi.ConnectException ce){
			System.out.println("Connection fails");
		}
	}
}
