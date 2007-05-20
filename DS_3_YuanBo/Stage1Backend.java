public interface Stage1Backend {
	// Locate::
	//	FUNCTION: Looks up the server at the given URL, using the Naming reference passed in to the constructor.
	//	RETURN VALUE: Returns true if a Stub for the server is successfully obtained, false otherwise.
	//	USER INTERFACE: Reports various errors using the displayAlert method of the user interface.
	public boolean locate(String url);
	
	// Connect::
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
	public boolean connect();
	
	// Disconnect::
	//	FUNCTION: Uses the Stub obtained by locate() to call the disconnect method on the server, passing the ChatKey 
	//	 obtained by login() to authenticate. After calling disconnect on the server, it should unexport the client 
	//	 object, in order to shut down the callbacks facility.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean disconnect();
	
	// SendMessage::
	//	FUNCTION: Uses the Stub obtained by locate() to call the sendMessage method on the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean sendMessage(String message);
	
	// Attach::
	//	FUNCTION: Saves a reference to the given user interface, for use in other methods.
	public void attach(Stage1UserInterface ui);
}
