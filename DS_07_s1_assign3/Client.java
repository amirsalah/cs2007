/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 6th, May   2007           */
/***********************************/

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidKeyException;

public class Client
	implements ClientCallbacks, Stage3Backend{
	protected Stage1UserInterface clientUIS1 = null;
	protected Stage2UserInterface clientUIS2 = null;
	protected Stage3UserInterface clientUIS3 = null;
	protected Naming naming = null;
	protected Chat ca= null;
	protected String selectedTP = null;
	
	protected ChatKey chatKey = null;

	public Client(Naming naming) {
		this.naming = naming;
	}

	public void attach(Stage1UserInterface ui){
		try{
			clientUIS1 = ui;
		}
		catch(Exception e){
			
		}
	}
	
	public void attach(Stage2UserInterface ui){
		clientUIS2 = ui;
	}
	
	//  Attach::
	//	FUNCTION: Saves a reference to the given user interface, for use in other methods.
	public void attach(Stage3UserInterface ui3){
		clientUIS3 = ui3;
	}
	
	//	Login::
	//	FUNCTION: Uses the Stub obtained by locate() to call the login method of the server, saving a references to the 
	//	 returned ChatKey to use in subsequent calls.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean login(String username,String password){
		try{
			chatKey = ca.login(username, password);
		}
		catch(RemoteException re){
			clientUIS1.displayAlert("Login failed");
			return false;
		}
		
		return true;
	}
	
	//	IsPrivileged::
	//	FUNCTION: Tests whether the client is both logged in and holds a privileged key.
	//	RETURN VALUE: Returns true if the client is logged in with a privileged key.
	public boolean isPrivileged(){
		if(isLoggedIn() && chatKey.amPrivileged()){
			return true;
		}else{
			return false;
		}
	}

	// IsLoggedIn::
	//	FUNCTION: test whether the client is logged in.
	//	RETURN VALUE: Returns true if the client is logged in.
	public boolean isLoggedIn(){
		if(chatKey != null){
			return true;
		}else{
			return false;
		}
	}
	
	//	Logout::
	//	FUNCTION: Uses the Stub obtained by locate() to call the logout method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate. After the call to the server, discards the ChatKey, and unexpports
	//	 the client if it is currently exported.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean logout(){
		try{
			ca.logout(chatKey);
			chatKey = null;
			java.rmi.server.UnicastRemoteObject.unexportObject(this,true);
		}
		catch(RemoteException re){
			clientUIS1.displayAlert("logout failed");
			return false;
		}
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("Logout failed in UI3");
		 }
		
		return true;
	}

	// CreateAccount::
	//	FUNCTION: Uses the Stub obtained by locate() to call the setPassword method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean createAccount(String username,String password,boolean priv){
		try{
			ca.createAccount(chatKey, username, password, priv);
		}
		catch(RemoteException re){
			clientUIS1.displayAlert("createACcount failed");
			return false;
		}
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("createACcount failed in UI3");
		 }
		
		return true;
	}

	// SetPrivilege::
	//	FUNCTION: Uses the Stub obtained by locate() to call the setPrivilege method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean setPrivilege(String username,boolean priv){
		try{
			ca.setPrivilege(chatKey, username, priv);
		}
		catch(RemoteException re){
			clientUIS1.displayAlert("set Privilege failed");
			return false;
		}
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("set Privilege failed in UI3");
			chatKey = null;
			return false;
		 }
		
		return true;
	}
	
	//	SetPassword::
	//	FUNCTION: Uses the Stub obtained by locate() to call the setPassword method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean setPassword(String username,String password){
		try{
			ca.setPassword(chatKey, username, password);
		}
		catch(RemoteException re){
			clientUIS1.displayAlert("set password failed");
			return false;
		}
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("set Privilege failed in UI3");
			chatKey = null;
			return false;
		 }
		
		return true;
	}
	
	
	
	public boolean locate(String url){
		try{
			ca = (Chat)naming.lookup(url);
		}
		catch(java.net.MalformedURLException e){
			clientUIS1.displayAlert("Locate: Malformed URL: " + e.getMessage());
			return false;
		}
		catch(java.rmi.RemoteException re){
			clientUIS1.displayAlert("Locate: Could not communicate with registry.");
			return false;
		}
		catch(java.rmi.NotBoundException e){
			clientUIS1.displayAlert("Locate: No binding in registry for: "+ e.getMessage());
			return false;
		}
		catch(java.lang.ClassCastException e)
		{
			clientUIS1.displayAlert("Locate: Stub is not of expected type.");
			return false;
		}
		return true;
	}
	
	// GetTranscriptList::
	//	FUNCTION: Uses the Stub obtained by locate() to call the getTranscriptList method of the server to retreive the 
	//	set of transcripts saved on the server, passing the ChatKey obtained by login() to authenticate.
	//	RETURN VALUE: Returns null if RemoteException is thrown, the String[] returned from the server otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public String[] getTranscriptList(){
		
		String[] tpList = null;
		try{
			tpList = ca.getTranscriptList(chatKey);
			return tpList;
		}
		catch(RemoteException re){
			clientUIS1.displayAlert("GetTranscriptList: Could not communicate with server.");
			return null;
		}
		catch(InvalidKeyException ie){
			clientUIS3.invalidKey("getTranscriptList failed in UI3");
		}
		
		return null;
	}
	
	public void selectTranscript(String transcriptName){
		selectedTP = transcriptName;
	}
	
	//	Connect::
	//	FUNCTION: Uses the Stub obtained by locate() to call one of the connect methods on the server, passing the 
	//	 ChatKey obtained by login() to authenticate. Before calling the server, this method should export the client 
	//	 object so that it can be passed as the parameter to connect, in order to set up a callbacks facility. The 
	//	 particular connect method to be called on the server is determined by the most recent call to 
	//	 selectTranscript(): if the value passed is the empty String or null (or selectTranscript() has not been 
	//	 called) then the first version of connect (taking two parameters) should be called, otherwise the second 
	//	 version (taking three parameters) should be called. 
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	//	 Calls the setTranscriptLabelText method of the user interface to report session status.
	public boolean connect(){
		String serverName = null;
		try{
			serverName = ca.serverName();
		}
        catch(RemoteException re){
        	clientUIS1.displayAlert("ServerName: Could not communicate with server.");
        	return false;
        }
        try{
        	UnicastRemoteObject.exportObject(this);
        	if(selectedTP == null){
        		// Choosing first version of connect(*) method
        		if(ca.connect(chatKey, this)){
        			// Indicate starting a new session
        			clientUIS2.setTranscriptLabelText("Started new session at server \"" + serverName + "\"");
        		}else{
        			clientUIS2.setTranscriptLabelText("Connected to ongoing session at server \"" + serverName + "\"");
        		}
        	}else{
        		// Choose second version of connect(*,*) method
        		if(ca.connect(chatKey, this, selectedTP)){
        			clientUIS2.setTranscriptLabelText("Resumed session using transcript \"" + selectedTP + "\"" + " at server \"" + serverName + "\"");
        		}else{
        			clientUIS2.setTranscriptLabelText("Connected to ongoing session at server \"" + serverName + "\"");
//        			clientUIS2.setTranscriptLabelText("Resumed session using transcript \"" + selectedTP + "\"" + " at server \"" + serverName + "\"");
        		}
        	}
        }
        catch(RemoteException re){
        	clientUIS1.displayAlert("Connect: Could not communicate with server.");
        	return false;
        }
        catch(java.io.FileNotFoundException fnfe){
        	clientUIS1.displayAlert("Connect: transcript not found: savetestnotfound");
        	return false;
        }
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("connect failed in UI3");
		}
		
        return true;
	}


	// Disconnect::
	//	FUNCTION: Uses the Stub obtained by locate() to call the disconnect method on the server, passing the ChatKey 
	//	 obtained by login() to authenticate. After calling disconnect on the server, it should unexport the client 
	//	 object, in order to shut down the callbacks facility.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean disconnect(){
        try{
        	ca.disconnect(chatKey, this);
        	UnicastRemoteObject.unexportObject(this,true);
        }
        catch(RemoteException re){
        	clientUIS1.displayAlert("Disconnect: Could not communicate with server.");
        	return false;
        }
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("disconnect failed in UI3");
		}
		
        return true;
	}
	

	// SendMessage::
	//	FUNCTION: Uses the Stub obtained by locate() to call the sendMessage method on the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean sendMessage(String message){
        try{
        	ca.sendMessage(chatKey, message);
        }
        catch(RemoteException re){
        	clientUIS1.displayAlert("SendMessage: Could not communicate with server.");
        	try{
        		UnicastRemoteObject.unexportObject(this,true);
        	}
            catch(RemoteException e){
            	clientUIS1.displayAlert("Disconnect: Could not communicate with server.");
            }
        	return false;
        }
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("shutdownAndAbort failed in UI3");
		}
		
        return true;
	}
	
	//	ShutdownAndSave::
	//	FUNCTION: Uses the Stub obtained by locate() to call the saveTranscript method of the server, followed by the 
	//	 shutdown method, passing the ChatKey obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown (i.e server has shutdown), true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean shutdownAndSave(String transcriptName){
		try{
			ca.saveTranscript(chatKey, transcriptName);
			ca.shutdown(chatKey);
		}
        catch(RemoteException re){
        	clientUIS1.displayAlert("SaveTranscript: Could not communicate with server.");
        	return false;
        }
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("shutdownAndSave failed in UI3");
		}
		
		return true;
        
	}
	
	//	ShutdownAndAbort::
	//	FUNCTION: Uses the Stub obtained by locate() to call the shutdown method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown (i.e server has shutdown), true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean shutdownAndAbort(){
		try{
			ca.shutdown(chatKey);
		}
        catch(RemoteException re){
        	clientUIS1.displayAlert("ShutdownAndAbort: Could not communicate with server.");
        	return false;
        }
		catch(InvalidKeyException ie)
		{
			clientUIS3.invalidKey("shutdownAndAbort failed in UI3");
		}
		
		return true;
	}
	
	public void receiveMessage(String msg) throws java.rmi.RemoteException{
		clientUIS1.displayMessage(msg);
	}
	

}
