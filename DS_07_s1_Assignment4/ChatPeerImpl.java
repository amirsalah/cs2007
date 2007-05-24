/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 5th, April 2007           */
/***********************************/

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.PrintWriter;

public class ChatPeerImpl 
	extends java.rmi.server.UnicastRemoteObject
	implements ChatPeer 
{
  //
  // Your implementation of your ChatPeer interface here.
  // NOTE: you can choose to make ChatPeerImpl extend java.rmi.server.UnicastRemoteObject
  // if you wish, OR use java.rmi.server.UnicastRemoteObject.exportObject and 
  // java.rmi.server.UnicastRemoteObject.unexportObject.
  //

	private Vector<ClientCallbacks> activeClients = new Vector<ClientCallbacks>();
	private String serverName = null;
	private Naming naming = null;
	private String boundURL = null;
	private ArrayList<String> messages = new ArrayList<String>();
	private String folderName = "./transcriptsFolder/";
	
    public ChatPeerImpl() throws java.rmi.RemoteException
    {
    	super();
    }
	
	public ChatPeerImpl(String boundURL, Naming naming, String name) 
		throws java.rmi.RemoteException{
		super();
		serverName = name;
		this.naming = naming;
		this.boundURL = boundURL;
	}
	
	public String serverName() throws java.rmi.RemoteException{
		return serverName;
	}
	
	public boolean ConnectPeer(ClientCallbacks cl) throws java.rmi.RemoteException{
		// Test if the new client has been in the set.
		if(activeClients.contains(cl)){
			System.out.println("Duplicated client");
			return false;
		}else{
			activeClients.add(cl);
			// New session
			if(activeClients.size() == 1){
				messages.clear();
				return true;
			}else{
				// Existing session
				// Receive existing messages in this session
				for(int i=0; i<messages.size(); i++){
					cl.receiveMessage(messages.get(i));	
				}
				return false;
			}
		}
	}

	public boolean ConnectPeer(ClientCallbacks cl,String transcriptName) 
		throws java.rmi.RemoteException,java.io.FileNotFoundException{
		// Existing session, join the session
		if(!activeClients.isEmpty()){
			activeClients.add(cl);
			// Receive existing messages in this session
			for(int i=0; i<messages.size(); i++){
				cl.receiveMessage(messages.get(i));	
			}
			return false;
		}
		
		// New session.
		// Clear old messages in the previous session
		messages.clear(); 
		
		
		activeClients.add(cl);
		// Read the file storing transcripts
		File scriptFile = new File(folderName + transcriptName);
		String oneMessage = null;
        
        if(scriptFile.exists()){
        	BufferedReader tpReader = new BufferedReader(new FileReader(scriptFile));
        	try{
        		oneMessage = tpReader.readLine();
        		// Restore the previous messages
        		while(oneMessage != null){
        			messages.add(oneMessage);
        			// Send messages to the currently connecting client
        			cl.receiveMessage(oneMessage);
        			oneMessage = tpReader.readLine();
        		}
        		tpReader.close();
        		return true;
        	}
            catch(java.io.IOException ioe){
            	System.out.println("IOException");
            }
        }else{
        	throw(new FileNotFoundException(transcriptName));
        }
        return false;
		
	}

	public void Disconnect(ClientCallbacks cl) throws java.rmi.RemoteException{
		if(activeClients.contains(cl)){
			activeClients.remove(cl);
		}else{
			System.out.println("No such client");
		}
	}
	
	public void SendMessage(String msg) throws java.rmi.RemoteException{
		// Send message to each client in the active clients vector
		for(int i=0; i<activeClients.size(); i++){
			try{
				if(activeClients.get(i) != null){
					((ClientCallbacks)activeClients.get(i)).receiveMessage(msg);
				}
			}
			catch (java.rmi.RemoteException re){
				activeClients.set(i, null);
			}
		}
		
		// Record the message
		messages.add(msg);
	}
	
    public void SaveTranscript(String transcriptName) throws java.rmi.RemoteException{
    	String transcripts = folderName + transcriptName;
    	File folder = new File("./transcriptsFolder");
	    if(!folder.exists()){
	      	folder.mkdir();
	    }
	    
    	// Save the transcript to a file
    	PrintWriter tpWriter = null;
    	try{
    		tpWriter = new PrintWriter(new BufferedWriter(new FileWriter(transcripts)));
            for(int i=0; i<messages.size(); i++){
            	tpWriter.println(messages.get(i));
            }
            tpWriter.close();
            messages.clear();
    	}
        catch (java.io.IOException e) {
            System.out.println("Error in writing file");
        }
    }
    
    public void ShutDown() throws java.rmi.RemoteException{
    	try{
    		naming.unbind(boundURL);
    	}
    	catch(java.rmi.NotBoundException e){
    		System.out.println("NotBoundException");
    	}
		catch(java.net.MalformedURLException e){
			System.out.println("Bad URL");
		}
		
		UnicastRemoteObject.unexportObject(this,true);
		messages.clear();
		System.exit(1);
    }
    
    public String[] GetTranscriptList() throws java.rmi.RemoteException{
    	String[] tpList = null;
    	File f = new File("./transcriptsFolder");
    	tpList = f.list();
    	int fileNum = tpList.length;
    	String tmp = null;
    	
    	for(int i=0; i<fileNum/2; i++){
    		tmp = tpList[fileNum - i - 1];
    		tpList[fileNum - i - 1] = tpList[i];
    		tpList[i] = tmp;
    	}
    	return tpList;
    }

}