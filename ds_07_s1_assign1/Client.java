import java.rmi.server.*;
import java.rmi.*;

public class Client 
	implements ClientCallbacks, Stage1Backend{
	protected Stage1UserInterface clientUI = null;
	protected Naming naming = null;
	protected Chat ca= null;
	

	public Client(Naming naming) {
		this.naming = naming;
	}

	public void attach(Stage1UserInterface ui){
		try{
			clientUI = ui;
		}
		catch(Exception e){
			
		}
	}
	
	public boolean locate(String url){
		try{
			ca = (Chat)naming.lookup(url);
		}
		catch(java.net.MalformedURLException e){
			clientUI.displayAlert("Locate: Malformed URL: " + e.getMessage());
			return false;
		}
		catch(java.rmi.RemoteException re){
			clientUI.displayAlert("Locate: Could not communicate with registry.");
			return false;
		}
		catch(java.rmi.NotBoundException e){
			clientUI.displayAlert("Locate: No binding in registry for: "+ e.getMessage());
			return false;
		}
		catch(java.lang.ClassCastException e)
		{
			clientUI.displayAlert("Locate: Stub is not of expected type.");
			return false;
		}
		return true;
	}
	
	public boolean connect(){
        
        try{
        	UnicastRemoteObject.exportObject(this);
        	ca.connect(this);
        }
        catch(RemoteException re){
        	clientUI.displayAlert("Connect: Could not communicate with server.");
        	return false;
        }
        
        return true;
	}
	
	public void disconnect(){
        try{
        	ca.disconnect(this);
        	UnicastRemoteObject.unexportObject(this,true);
        }
        catch(RemoteException re){
        	clientUI.displayAlert("Disconnect: Could not communicate with server.");
        }
	}
	
	public boolean sendMessage(String message){
        try{
        	ca.sendMessage(message);
        }
        catch(Exception e){
        	clientUI.displayAlert("SendMessage: Could not communicate with server.");
		return false;
        }
        
        return true;
	}
	
	
	public void receiveMessage(String msg) throws java.rmi.RemoteException{
		clientUI.displayMessage(msg);
	}
	
}
