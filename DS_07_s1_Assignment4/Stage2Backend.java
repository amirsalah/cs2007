public interface Stage2Backend extends Stage1Backend {
	// GetTranscriptList::
	//	FUNCTION: Uses the Stub obtained by locate() to call the getTranscriptList method of the server to retreive the 
	//	 set of transcripts saved on the server, passing the ChatKey obtained by login() to authenticate.
	//	RETURN VALUE: Returns null if RemoteException is thrown, the String[] returned from the server otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public String[] getTranscriptList();

	// SelectTranscript::
	//	FUNCTION: Records the name of a transcript, to be used in subsequent connect() call.
	public void selectTranscript(String transcriptName);

	// ShutdownAndSave::
	//	FUNCTION: Uses the Stub obtained by locate() to call the saveTranscript method of the server, followed by the 
	//	 shutdown method, passing the ChatKey obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown (i.e server has shutdown), true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean shutdownAndSave(String transcriptName);

	// ShutdownAndAbort::
	//	FUNCTION: Uses the Stub obtained by locate() to call the shutdown method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown (i.e server has shutdown), true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean shutdownAndAbort();  

	// Attach::
	//	FUNCTION: Saves a reference to the given user interface, for use in other methods.
	public void attach(Stage2UserInterface ui);
}
