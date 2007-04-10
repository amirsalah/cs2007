/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 7th, Mar. 2007            */
/***********************************/

public class MiniClient {

	public static void main(String[] args) throws java.rmi.RemoteException{
		try{
			Chat ca = (Chat)java.rmi.Naming.lookup(args[0]);
			System.out.println(ca.serverName());
		}
		catch(java.net.MalformedURLException e){
			System.out.println("Bad URL in client");
		}
		catch(java.rmi.NotBoundException e){
			System.out.println("Not Bound Exception");
		}
		
		
	}

}
