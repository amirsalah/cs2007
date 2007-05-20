public interface ClientCallbacks extends java.rmi.Remote 
{
	// ReceiveMessage::
	//	FUNCTION: Calls the displayMessage() method of the attached user interface.
	//	NOTE: Should NOT catch RemoteException if it is thrown by displayMessage(). 
	public void receiveMessage(String msg) throws java.rmi.RemoteException;

	// Ping::
	//	FUNCTION: Calls the pinged() method of the user interface.
	//	NOTE: Should NOT catch RemoteException if it is thrown by pinged(). 
	public void ping() throws java.rmi.RemoteException;

	// UpdatePrivilege::
	//	FUNCTION: Notifies the attached user interface (via the updatePrivilege() method) that the client's privilege
	//	 has changed.
	public void updatePrivilege(boolean isNowPrivileged) throws java.rmi.RemoteException;
}
