import java.rmi.NotBoundException;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.AccessControlException;
import java.lang.NullPointerException;

public class Client implements Stage1Backend, Stage2Backend, Stage3Backend,ClientCallbacks 
{
	private Naming nm;
	private Chat stub;
	private String selectedTranscript_name;
	private Stage1UserInterface UserI;
	private Stage2UserInterface UserI1;
	private Stage3UserInterface UserI2;
	private ChatKey mykey;
	private boolean login;
	
	public Client(Naming name) {
		nm = name;
	}

	public void attach(Stage1UserInterface ui) {
		UserI = ui;
	}

	public void attach(Stage2UserInterface ui) {
		UserI1 = ui;
	}

	public void attach(Stage3UserInterface ui3) {
		UserI2 = ui3;
	}	
	
	public boolean locate(String url) {
		try {
			stub = (Chat) nm.lookup(url);
			
		}catch (MalformedURLException M) {
			UserI.displayAlert("Locate: Malformed URL:" + url);
			return false;
		}catch (NotBoundException N) {
			UserI.displayAlert("Locate: No binding in registry for:" + url);
			return false;
		}catch (RemoteException R) {
			UserI.displayAlert("Locate: Could not communicate with registry.");
			return false;
		}catch (Exception e) {
			UserI.displayAlert("Locate: Stub is not of expected type.");
			return false;
		}
		return true;
	}
	
	public boolean login(String username,String password){
		try{
		 mykey= stub.login(username,password);
		}catch (RemoteException e) {
			UserI.displayAlert("Login: Could not communicate with server.");
		    login=false;
		   return false;
		}
		login=true;
		return true;
	}

	public boolean isPrivileged(){
		boolean l=false;
		try{
			l=stub.isPrivileged (mykey);
		}
		catch(RemoteException e){
			UserI.displayAlert("Logout: Could not communicate with server.");
			return l;
		}
		    return l;
	}
	
	public boolean isLoggedIn(){
		return login;
}
	public boolean logout(){
		try{
			stub.logout(mykey);
			UnicastRemoteObject.unexportObject(this, true);
		}catch (RemoteException re) {
			UserI.displayAlert("Logout: Could not communicate with server.");
			return false;
		}catch (InvalidKeyException Ike){
			UserI2.invalidKey("Logout");
			return false;
			}
		
		    return true;
	}
	
	public boolean createAccount(String username,String password,boolean priv){
	   boolean p=false;
	try{	
	   stub.createAccount(mykey,username,password,priv);
	}catch (RemoteException e){
		UserI.displayAlert("CreatAccount: Could not communicate with server.");
	    return p;
	}catch (InvalidKeyException Ike){
		UserI2.invalidKey("CreatAccount");
		return p;
	}catch(AccessControlException Ace){
		return p;
	}
	return true;
	}
	
	public boolean setPrivilege(String username,boolean priv){
		try{	
			   stub.setPrivilege(mykey,username,priv);
			}catch (RemoteException e){
			UserI.displayAlert("SetPrivilege: Could not communicate with server.");
			    return false;
			}catch (InvalidKeyException Ike){
				UserI2.invalidKey("SetPrivilege");
				return false;
			}catch(AccessControlException Ace){
				
				return false;
			}
			return true;
		}
	
	public boolean setPassword(String username,String password){
		try{	
			   stub.setPassword(mykey,username,password);
			}
		      catch (RemoteException e){
		      UserI.displayAlert("SetPassword: Could not communicate with server.");
			    return false;
			}catch (InvalidKeyException Ike){
				UserI2.invalidKey("SetPassword");
				return false;
			}catch(AccessControlException Ace){
			}
			return true;
		}

	public void ping() throws java.rmi.RemoteException{
		UserI2.pinged();
	}
	public void updatePrivilege(boolean isNowPrivileged) throws java.rmi.RemoteException{
        UserI2.updatePrivilege(isNowPrivileged);
	}

	public String[] getTranscriptList() {

		String[] Tlist = null;
		try {
			Tlist = stub.getTranscriptList(mykey);
			
		} catch (java.rmi.RemoteException e) {
			Tlist = null;
			UserI.displayAlert("GetTranscriptList: Could not communicate with server.");
			return Tlist;
		}catch (InvalidKeyException Ike){
			UserI2.invalidKey("GetTranscriptList: Unprivileged key.");
			return Tlist;
		}catch(AccessControlException Ace){
			return Tlist;
		}
		return Tlist;
	}

  public void selectTranscript(String transcriptName) {

		selectedTranscript_name = transcriptName;

	}

    public boolean connect() {

		boolean m, n;
		String S_name = null;

		try {
			S_name = stub.serverName();
		}

		catch (RemoteException e) {
			UserI.displayAlert("ServerName: Could not communicate with server.");
			return false;
		}

		try {
			UnicastRemoteObject.exportObject(this);
			if (selectedTranscript_name == null) {
				try {
					if (stub.connect(mykey,this)) {
						UserI1.setTranscriptLabelText("Started new session at server "
										+ '"' + S_name + '"');
					} else {
						UserI1.setTranscriptLabelText("Connected to ongoing session at server "
										+ '"' + S_name + '"');
					}
					return true;
				} catch (RemoteException e) {
					UserI.displayAlert("Connect: Could not communicate with server.");
					return false;
				}catch (InvalidKeyException Ike){
					UserI2.invalidKey("Connect: Unprivileged key");
					return false;
				}catch(AccessControlException Ace){
					     return false;
				     }
			}
	         else {
	            try {
					     m = stub.connect(mykey,this,selectedTranscript_name); 
				     }catch (FileNotFoundException e){
					     UserI.displayAlert("Connect: transcript not found: "+ selectedTranscript_name);
					     return false;
				     }catch (InvalidKeyException Ike){
					    UserI2.invalidKey("Connect: Unprivileged key.");
					     return false;
				    }catch(AccessControlException Ace){
					     return false;
				     }
				
				       if (m == true) 
				         {
					       UserI1.setTranscriptLabelText("Resumed session using transcript "
									+ '"'
									+ selectedTranscript_name
									+ '"'
									+ " at server " + '"' + S_name + '"');
				          } 
				      else 
				          {
					          UserI1.setTranscriptLabelText("Connected to ongoing session at server "
									+ '"' + S_name + '"');
				          }
				           return true;
		           }

		}catch (java.rmi.RemoteException e) {
			UserI.displayAlert("Connect: Could not communicate with server.");
			return false;
		} catch (NullPointerException e) {
			return false;
		}
      catch(AccessControlException Ace){
			return false;
		}
    
}

	public boolean disconnect() {
		try {
			stub.disconnect(mykey,this);
			UnicastRemoteObject.unexportObject(this, true);
		}catch (RemoteException re) {
			UserI.displayAlert("Disconnect: Could not communicate with server.");
			return false;
		}catch (NullPointerException e) {
			return false;
		}catch (InvalidKeyException Ike){
			UserI2.invalidKey("Disconnect");
			return false;
			}
		
		    return true;
}

	public boolean sendMessage(String message) {

		try {
			stub.sendMessage(mykey,message);
			}catch (RemoteException RE) {
				UserI.displayAlert("SendMessage: Could not communicate with server.");
				return false;
			}catch (InvalidKeyException Ike){
				UserI2.invalidKey("SendMessage");
				return false;
		    	}
			return true;
}

	public boolean shutdownAndSave(String transcriptName) {
		try {

			stub.saveTranscript(mykey,transcriptName);
			stub.shutdown(mykey);
		} catch (RemoteException e) {
			UserI.displayAlert("SaveTranscript: Could not communicate with server.");
		    return false;
		}catch (InvalidKeyException Ike){
			UserI2.invalidKey("Shutdown: Unprivileged key.");
			return false;
		}catch(AccessControlException Ace){
			return false;
		} 
		return true;
	}

	public boolean shutdownAndAbort() {
		try {
			stub.shutdown(mykey);
		}catch (RemoteException e){
		    return false;
		}catch (InvalidKeyException Ike){
			UserI2.invalidKey("Shutdown");
			return false;
		}catch(AccessControlException Ace){
		}
		return true;
	}
	
	public void receiveMessage(String msg) throws java.rmi.RemoteException {
		try{
		UserI.displayMessage(msg);
		}catch (RemoteException e){
			try {
				stub.disconnect(mykey,this);
			} catch (InvalidKeyException ike) {
				UserI2.invalidKey("Disconnect");
			}
			
		}
	}
}
