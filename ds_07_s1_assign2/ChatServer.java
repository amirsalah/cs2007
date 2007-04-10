/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 5th, April 2007           */
/***********************************/

public class ChatServer {
	
	public static void main(String[] args) throws java.rmi.RemoteException{
//		String boundURL = "rmi://localhost:2067/ChatServer";
		RegistryNamingImpl rNaming = new RegistryNamingImpl();
		String serverName = "Bo's place";
		
		Chat ca = new ChatImpl(args[0], rNaming, serverName);
		
		try{
			java.rmi.Naming.rebind(args[0], ca);
//			rNaming.rebind(boundURL, ca);
		}
		catch(java.net.MalformedURLException e){
			System.out.println("Bad URL");
		}
		catch(java.rmi.ConnectException ce){
			System.out.println("Connection fails");
		}
	}
}
