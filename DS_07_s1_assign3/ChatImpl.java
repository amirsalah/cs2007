/***********************************/
/* Author: Bo CHEN                 */
/* Student ID: 1139520             */
/* Date: 6th, May 2007             */
/***********************************/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.AccessControlException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import java.util.LinkedList;

public class ChatImpl 
	extends java.rmi.server.UnicastRemoteObject 
	implements Chat{

	private Vector<ClientCallbacks> activeClients = new Vector<ClientCallbacks>();
	private String serverName = null;
	private Naming naming = null;
	private String boundURL = null;
	private ArrayList<String> messages = new ArrayList<String>();
	private String folderName = "./transcriptsFolder/";
	
	// accounts: <user name, password>
	private Map<String, String> accounts = new HashMap<String, String>();
	// privileges: <user name, previlege>
	private Map<String, Boolean> privileges = new HashMap<String, Boolean>();
	// keys: <ChatKey, user name>
	private Map<ChatKey, String> keys = new HashMap<ChatKey, String>();
	// loginedAccounts: <user name>
	private LinkedList<String> loginedAccounts = new LinkedList<String>();
	
    public ChatImpl() throws java.rmi.RemoteException
    {
    	super();
    	
    	CreateAdmin();
    }
	
    /**
     * @param boundURL the URL with which the server object is to be bound in the registry
     * @param naming an object implementing the Naming interface (this is provided so the server can unbind
     * 	itself from the Naming system when it is shutdown, but may be useful in other ways as well).
     * @param name the name to be retunred by the serverName method.
     * @throws java.rmi.RemoteException
     */
	public ChatImpl(String boundURL, Naming naming, String name) 
		throws java.rmi.RemoteException{
		super();
		serverName = name;
		this.naming = naming;
		this.boundURL = boundURL;
		
		CreateAdmin();
		LoadAccounts();
	}
	
	/**
	 * Create an admin account when it's initialized
	 * with account name: admin, password:intadmin
	 */
	private void CreateAdmin(){
		String admin = "admin";
		accounts.put(admin, "initadmin");
		loginedAccounts.add(admin);
		privileges.put(admin, true);
	}
	
	/**
	 * Load the existing accounts which previously stored in a file named Accounts.ds
	 * The structure of this file is that: 
	 * for every 3 lines:
	 * 1. user name
	 * 2. password
	 * 3. is privileged?
	 */
	private void LoadAccounts(){
		File accountsFile = new File("Accounts.ds");
		String account = null;
		String password = null;
		String isPrivileged = null;
		
		try{
			if(accountsFile.exists()){
				BufferedReader reader = new BufferedReader(new FileReader(accountsFile));
				account = reader.readLine();
				
				while(account != null){
					// Read password of this account
					password = reader.readLine();
					if(password != null){
						accounts.put(account, password);
					}else{
						System.out.println("invalid password in the file: Accounts.ds");
					}
					// Read privilege of this account
					isPrivileged = reader.readLine();
					if(isPrivileged.equalsIgnoreCase("y")){
						privileges.put(account, true);
					}else{
						if(isPrivileged.equalsIgnoreCase("n")){
							privileges.put(account, false);
						}else{
							System.out.println("invalid previleges in the file: Accounts.ds");
						}
					}
					account = reader.readLine();
				}
			}
		}
		catch(IOException ioe){
			System.out.println("File Accounts.ds is not found");
		}
	}
	
	/**
	 * Save all the accounts currently in the HashMap into a file named Accounts.ds
	 */
	private void SaveAccounts(){
		File accountsFile = new File("Accounts.ds");
		String account = null;
		String password = null;
		Boolean isPrivileged = null;
		
		try{
			if(accountsFile.exists()){
				PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(accountsFile)));
				Iterator<String> accountsIterator = accounts.keySet().iterator();
				
				// Save accounts, passwords and privileges to a file
				while(accountsIterator.hasNext()){
					account = accountsIterator.next();
					writer.println(account);
					
					password = accounts.get(account);
					writer.println(password);
					
					isPrivileged = privileges.get(account);
					if(isPrivileged)
						writer.println("y");
					else
						writer.println("n");
				}
			}
		}
		catch(IOException ioe){
			System.out.println("File Accounts.ds is not found");
		}
	}
	
	public String serverName() throws java.rmi.RemoteException{
		return serverName;
	}
	
	public boolean connect(ChatKey key,ClientCallbacks cl)
		throws java.rmi.RemoteException{
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

	public boolean connect(ChatKey key,ClientCallbacks cl,String transcriptName) 
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

	public void disconnect(ChatKey key,ClientCallbacks cl) throws java.rmi.RemoteException{
		if(activeClients.contains(cl)){
			activeClients.remove(cl);
		}else{
			System.out.println("No such client");
		}
	}
	
	public void sendMessage(ChatKey key,String msg) throws java.rmi.RemoteException{
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
	
    public void saveTranscript(ChatKey key,String transcriptName) throws java.rmi.RemoteException{
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
    
    public void shutdown(ChatKey key) throws java.rmi.RemoteException{
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
    
    public String[] getTranscriptList(ChatKey key) throws java.rmi.RemoteException{
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
    
    
    
    
	public ChatKey login(String username,String password)
		throws RemoteException{
		ChatKey userChatKey = null;
		
		// Verify the user name
		if( !accounts.containsKey(username) ){
			return null;
		}
		
		// Verify the password
		String actualPWD = accounts.get(username);
		if( !actualPWD.equals(password) ){
			System.out.println("Password mismatched");
			return null;
		}
		
		// Verify the privilege
		userChatKey = new ChatKeyImpl(privileges.get(username));
		
		// Add the new logined client to the array list
		loginedAccounts.add(username);
		keys.put(userChatKey, username);
		
		return userChatKey;
	}
	
	public void logout(ChatKey key)  
		throws RemoteException,InvalidKeyException{
		String userName = keys.get(key);
		
		loginedAccounts.remove(userName);
		
	}
    
	// IsPrivileged::
	//	FUNCTION: indicates whether given key is privileged, if it is a key (or copy) generated by the server.
	//	RETURN VALUE: privilege status of given key, if it is a key (or copy) generated by the server. If called with a 
	//	 key not generated by the server, returns false.
	//	CALL CONSTRAINTS: can be called at anytime, by any client.  
	public boolean isPrivileged(ChatKey key) 
		throws RemoteException{
		return key.amPrivileged();
	}
		
	// CreateAccount::
	//	FUNCTION: permits a logged in, privileged client to create a new user account.
	//	RETURN VALUE: true of account created, false otherwise (account already exists).
	//	CALL CONSTRAINTS: only clients providing valid, privileged ChatKeys can call successfully, others get 
	//	 InvalidKeyException or AccessControlException (valid but unprivileged Key).
	public boolean createAccount(ChatKey creatorKey,String username,String password,boolean isPrivileged)
		throws RemoteException,InvalidKeyException,AccessControlException{
		String creator = keys.get(creatorKey);
		
		// Test if the creator is logined
		if( !loginedAccounts.contains(creator) ){
			InvalidKeyException ike = new InvalidKeyException();
			throw ike;
		}
		
		// Test the privilege
		if( !creatorKey.amPrivileged() ){
			AccessControlException ace = new AccessControlException("unprivileged Key");
			throw ace;
		}
		
		// Test if the username has been existed
		if( accounts.containsKey(username) ){
			return false;
		}
		
		accounts.put(username, password);
		privileges.put(username, isPrivileged);
		SaveAccounts();
		return true;
	}
		 
	// SetPrivilege::
	//	FUNCTION: permits a logged in, privileged client to change the privilege level of any account. Attempts to
	//	 change privilege of "admin" are ignored. Calls specifying non-existent users have no effect.
	//	CALL CONSTRAINTS: only clients providing valid, privileged ChatKeys can call successfully, others get 
	//	 InvalidKeyException or AccessControlException (valid but unprivileged Key).
	public void setPrivilege(ChatKey adminKey,String username,boolean isPrivileged)
		throws RemoteException,InvalidKeyException,AccessControlException{
		
		String admin = keys.get(adminKey);
		
		// Ignore changing "admin"'s privilege
		if(username.equals("admin")){
			return;
		}
		
		// return when call non-existent users
		if( !accounts.containsKey(username) ){
			return;
		}
		
		// Test if the admin is logined
		if( !loginedAccounts.contains(admin) ){
			InvalidKeyException ike = new InvalidKeyException();
			throw ike;
		}
		
		// Test the privilege
		if( !adminKey.amPrivileged() ){
			AccessControlException ace = new AccessControlException("unprivileged Key");
			throw ace;
		}
		
		privileges.remove(username);
		privileges.put(username, isPrivileged);
		SaveAccounts();
	}
		 
 	// SetPassword::
 	//	FUNCTION: permits a logged in, privileged client to change the password level of any account and permits
 	//	 any logged in client to change own password. Calls specifying non-existent users have no effect.
	//	CALL CONSTRAINTS: only clients providing valid ChatKeys can call successfully, others get InvalidKeyException.
	//	 Only clients providing privileged ChatKeys can successfully change another user's password, other clients
	//	 get AccessControlException.
	public void setPassword(ChatKey key,String username,String newPassword) 
		throws RemoteException,InvalidKeyException,AccessControlException{
		String user = keys.get(key);
		
		// return when call non-existent users
		if( !accounts.containsKey(username) ){
			return;
		}
		
		// Test if the user is logined
		if( !loginedAccounts.contains(user) ){
			InvalidKeyException ike = new InvalidKeyException();
			throw ike;
		}
		
		// user change his own password
		if( username == keys.get(key) ){
			accounts.remove(key);
			accounts.put(username, newPassword);
			SaveAccounts();
			return;
		}
		
		// user change another user's password, test the privilege
		if( !key.amPrivileged() ){
			AccessControlException ace = new AccessControlException("unprivileged Key");
			throw ace;
		}
		
		accounts.remove(username);
		accounts.put(username, newPassword);
		SaveAccounts();
	}
}
