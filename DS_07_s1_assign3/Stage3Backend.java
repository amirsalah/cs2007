public interface Stage3Backend extends Stage2Backend {
	// Login::
	//	FUNCTION: Uses the Stub obtained by locate() to call the login method of the server, saving a references to the 
	//	 returned ChatKey to use in subsequent calls.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean login(String username,String password);

	// IsPrivileged::
	//	FUNCTION: Tests whether the client is both logged in and holds a privileged key.
	//	RETURN VALUE: Returns true if the client is logged in with a privileged key.
	public boolean isPrivileged();

	// IsLoggedIn::
	//	FUNCTION: test whether the client is logged in.
	//	RETURN VALUE: Returns true if the client is logged in.
	public boolean isLoggedIn();

	// Logout::
	//	FUNCTION: Uses the Stub obtained by locate() to call the logout method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate. After the call to the server, discards the ChatKey, and unexpports
	//	 the client if it is currently exported.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean logout();

	// CreateAccount::
	//	FUNCTION: Uses the Stub obtained by locate() to call the setPassword method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean createAccount(String username,String password,boolean priv);

	// SetPrivilege::
	//	FUNCTION: Uses the Stub obtained by locate() to call the setPrivilege method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean setPrivilege(String username,boolean priv);

	// SetPassword::
	//	FUNCTION: Uses the Stub obtained by locate() to call the setPassword method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean setPassword(String username,String password);

	// Attach::
	//	FUNCTION: Saves a reference to the given user interface, for use in other methods.
	public void attach(Stage3UserInterface ui3);
}
