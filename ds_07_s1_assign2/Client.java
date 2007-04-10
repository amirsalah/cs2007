/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 5th, April 2007           */
/***********************************/

import java.rmi.server.*;
import java.rmi.*;

public class Client 
	implements ClientCallbacks, Stage2Backend{
	protected Stage1UserInterface clientUIS1 = null;
	protected Stage2UserInterface clientUIS2 = null;
	protected Naming naming = null;
	protected Chat ca= null;
	protected String selectedTP = null;
	

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
	
	public String[] getTranscriptList(){
		String[] tpList = null;
		try{
			tpList = ca.getTranscriptList();
			return tpList;
		}
		catch(RemoteException re){
			clientUIS1.displayAlert("Could not communicate with registry");
		}
		return null;
	}
	
	public void selectTranscript(String transcriptName){
		selectedTP = transcriptName;
	}
	
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
        		if(ca.connect(this)){
        			// Indicate starting a new session
        			clientUIS2.setTranscriptLabelText("Started new session at server \"" + serverName + "\"");
        		}else{
        			clientUIS2.setTranscriptLabelText("Connected to ongoing session at server \"" + serverName + "\"");
        		}
        	}else{
        		// Choose second version of connect(*,*) method
        		if(ca.connect(this, selectedTP)){
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
        	clientUIS1.displayAlert("File not found exception.");
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
        	clientUIS1.displayAlert("Disconnect: Could not communicate with server.");
        }
	}
	
	public boolean sendMessage(String message){
        try{
        	ca.sendMessage(message);
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
        return true;
	}
	
	public void shutdownAndSave(String transcriptName){
		try{
			ca.saveTranscript(transcriptName);
			ca.shutdown();
		}
        catch(RemoteException re){
        	clientUIS1.displayAlert("ShutdownAndSave: Could not communicate with server.");
        }	
	}
	
	public void shutdownAndAbort(){
		try{
			ca.shutdown();
		}
        catch(RemoteException re){
        	clientUIS1.displayAlert("ShutdownAndAbort: Could not communicate with server.");
        }	
	}
	
	public void receiveMessage(String msg) throws java.rmi.RemoteException{
		clientUIS1.displayMessage(msg);
	}
	
}
