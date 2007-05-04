import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.AccessControlException;
import java.io.FileNotFoundException;

public interface Chat extends java.rmi.Remote 
{
	// ServerName::
	//	FUNCTION: returns the server's name.
	//	RETURN VALUE: the server's name.
	//	CALL CONSTRAINTS: can be called at any time, by any client.
	public String serverName() 
		throws RemoteException;
		
	// Login::
	//	FUNCTION: permits a client providing a valid username and corresponding password to login.
	//	RETURN VALUE: a ChatKey if authentication successful, null otherwise.
	//	CALL CONSTRAINTS: can be called at anytime, by any client.
	public ChatKey login(String username,String password) 
		throws RemoteException;
		
	// GetTranscriptList::
	//	FUNCTION: permits a logged in, privileged client to obtain a list of saved transcripts.
	//	RETURN VALUE: the list of transcripts.
	//	CALL CONSTRAINTS: only clients providing valid, privileged ChatKeys can call successfully, others get 
	//	 InvalidKeyException or AccessControlException (valid but unprivileged Key)
	public String[] getTranscriptList(ChatKey key) 
		throws RemoteException,InvalidKeyException,AccessControlException;
			   
	// Connect::
	//	FUNCTION: permits a logged in client to connect to an existing session (if there is one) or start a new session.
	//	RETURN VALUE: true if a new session is started, false if joining an existing session.
	//	CALL CONSTRAINTS: only clients providing valid ChatKeys can call successfully, others get InvalidKeyException.
	public boolean connect(ChatKey key,ClientCallbacks cl) 
		throws RemoteException,InvalidKeyException;
		
	// Connect:: 
	//	FUNCTION: permits a logged in and privileged client to connect to an existing session (if any) or resume a chat
	//	 session from a transcript.
	//	RETURN VALUE: true if the session is resumed from the transcript, false if joining an existing session.
	//	CALL CONSTRAINTS: only clients providing valid, privileged ChatKeys can call successfully, others get 
	//	 InvalidKeyException or AccessControlException (valid but unprivileged Key). If the transcript doesn't exist,
	//	 FileNotFoundException is thrown.
	public boolean connect(ChatKey key,ClientCallbacks cl,String transcriptName) 
		throws RemoteException,FileNotFoundException,InvalidKeyException,AccessControlException;
			   
	// Disconnect::
	//	FUNCTION: permits a logged in client to disconnect from the chat session; the client remains logged in.
	//	CALL CONSTRAINTS: only clients providing valid ChatKeys can call successfully, others get InvalidKeyException.
	public void disconnect(ChatKey key,ClientCallbacks cl) 
		throws RemoteException,InvalidKeyException;
		
	// Logout::
	//	FUNCTION: permits a logged in client to logout from the chat server, also disconnecting the client 
	//	CALL CONSTRAINTS: only clients providing valid ChatKeys can call successfully, others get InvalidKeyException.
	public void logout(ChatKey key)  
		throws RemoteException,InvalidKeyException;
		
	// SendMessage::
	//	FUNCTION: permits a logged in client to send a message in the chat session to which it is connected.
	//	CALL CONSTRAINTS: only clients providing valid ChatKeys can call successfully, others get InvalidKeyException.
	public void sendMessage(ChatKey key,String msg) 
		throws RemoteException,InvalidKeyException;
		
	// SaveTranscript::
	//	FUNCTION: permits a logged in and privileged client to save the current session to a transcript
	//	CALL CONSTRAINTS: only clients providing valid, privileged ChatKeys can call successfully, others get 
	//	 InvalidKeyException or AccessControlException (valid but unprivileged Key).
	public void saveTranscript(ChatKey key,String transcriptName) 
		throws RemoteException,InvalidKeyException,AccessControlException;
		 
	// Shutdown::
	//	FUNCTION: permits a logged in and privileged client to shutdown the server.
	//	CALL CONSTRAINTS: only clients providing valid, privileged ChatKeys can call successfully,
	//	 others get InvalidKeyException or AccessControlException (valid but unprivileged Key).
	public void shutdown(ChatKey key) 
		throws RemoteException,InvalidKeyException,AccessControlException;
		 
	// IsPrivileged::
	//	FUNCTION: indicates whether given key is privileged, if it is a key (or copy) generated by the server.
	//	RETURN VALUE: privilege status of given key, if it is a key (or copy) generated by the server. If called with a 
	//	 key not generated by the server, returns false.
	//	CALL CONSTRAINTS: can be called at anytime, by any client.  
	public boolean isPrivileged(ChatKey key) 
		throws RemoteException;
		
	// CreateAccount::
	//	FUNCTION: permits a logged in, privileged client to create a new user account.
	//	RETURN VALUE: true of account created, false otherwise (account already exists).
	//	CALL CONSTRAINTS: only clients providing valid, privileged ChatKeys can call successfully, others get 
	//	 InvalidKeyException or AccessControlException (valid but unprivileged Key).
	public boolean createAccount(ChatKey creatorKey,String username,String password,boolean isPrivileged)
		throws RemoteException,InvalidKeyException,AccessControlException;
		 
	// SetPrivilege::
	//	FUNCTION: permits a logged in, privileged client to change the privilege level of any account. Attempts to
	//	 change privilege of "admin" are ignored. Calls specifying non-existent users have no effect.
	//	CALL CONSTRAINTS: only clients providing valid, privileged ChatKeys can call successfully, others get 
	//	 InvalidKeyException or AccessControlException (valid but unprivileged Key).
	public void setPrivilege(ChatKey adminKey,String username,boolean isPrivileged)
		throws RemoteException,InvalidKeyException,AccessControlException;
		 
 	// SetPassword::
 	//	FUNCTION: permits a logged in, privileged client to change the password level of any account and permits
 	//	 any logged in client to change own password. Calls specifying non-existent users have no effect.
	//	CALL CONSTRAINTS: only clients providing valid ChatKeys can call successfully, others get InvalidKeyException.
	//	 Only clients providing privileged ChatKeys can successfully change another user's password, other clients
	//	 get AccessControlException.
	public void setPassword(ChatKey key,String username,String newPassword) 
		throws RemoteException,InvalidKeyException,AccessControlException;
}
