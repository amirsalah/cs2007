public interface Stage4Backend extends Stage3Backend {
	// JoinPeer::
	//	FUNCTION: Uses the Stub obtained by locate() to call the joinPeer method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface. If,
	//	 the joinPeer method of the server returns true (join is succesful) then uses the setTranscriptLabelText method
	//   to indicate that the session at the peer server has been joined.
	public boolean joinPeer(String peerURL);

	// LeavePeer::
	//  FUNCTION: Uses the Stub obtained by locate() to call the leavePeer method of the server, passing the ChatKey 
	//	 obtained by login() to authenticate.
	//	RETURN VALUE: Returns false if RemoteException is thrown, true otherwise.  
	//	USER INTERFACE: Reports various errors using the displayAlert and invalidKey methods of the user interface.
	public boolean leavePeer();
}
