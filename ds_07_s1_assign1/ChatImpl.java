/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 22th, Mar. 2007            */
/***********************************/
import java.util.Vector;

public class ChatImpl 
	extends java.rmi.server.UnicastRemoteObject 
	implements Chat{
	
	Vector<ClientCallbacks> activeClients = new Vector<ClientCallbacks>();
	
	public ChatImpl() throws java.rmi.RemoteException{
		super();
	}
	
	public String serverName() throws java.rmi.RemoteException{
		return "Bo's place";
	}
	
	public void connect(ClientCallbacks cl) throws java.rmi.RemoteException{
		if(activeClients.contains(cl)){
			System.out.println("Duplicated client");
		}else{
			activeClients.add(cl);
		}
	}
	
	public void disconnect(ClientCallbacks cl) throws java.rmi.RemoteException{
		if(activeClients.contains(cl)){
			activeClients.remove(cl);
		}else{
			System.out.println("No such client");
		}
	}
	
	public void sendMessage(String msg) throws java.rmi.RemoteException{
		// Send message to each client in the active clients vector
		for(int i=0; i<activeClients.size(); i++){
			try{
			    if(activeClients.get(i) != null){
				activeClients.get(i).receiveMessage(msg);
			    }
			}
			catch (java.rmi.RemoteException re){
				activeClients.set(i, null);
				continue;
			}
		}
	}
}
