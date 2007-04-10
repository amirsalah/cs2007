/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 7th, Mar. 2007            */
/***********************************/

public class ChatServer {

	public static void main(String[] args) throws java.rmi.RemoteException{

		Chat ca = new ChatImpl();
		
		try{
			java.rmi.Naming.rebind(args[0], ca);
		}
		catch(java.net.MalformedURLException e){
			System.out.println("Bad URL");
		}		
	}

}
