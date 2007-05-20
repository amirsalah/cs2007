public interface ChatKey extends java.io.Serializable {
	// AmPrivileged::
	//	FUNCTION:  query isPrivileged() method of the server that generated the key, to determine if the key is 
	//	 currently privileged.
	//	RETURN VALUE: privilege status of the key, as returned by the server.
	//	CALL CONSTRAINTS: can be called at anytime, by any client or server.
	public boolean amPrivileged();
}
