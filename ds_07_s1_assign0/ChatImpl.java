/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 7th, Mar. 2007            */
/***********************************/

public class ChatImpl 
	extends java.rmi.server.UnicastRemoteObject
	implements Chat{
	
	public ChatImpl() throws java.rmi.RemoteException{
		super();
	}
	
	public String serverName() throws java.rmi.RemoteException{
		return "Bo's place";
	}
	
	

}
