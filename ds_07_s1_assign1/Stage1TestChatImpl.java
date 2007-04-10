public class Stage1TestChatImpl extends java.rmi.server.UnicastRemoteObject implements Chat {
	private String theTestCase = "";
	private int clientsConnected = 0;
	
	//Constructor
    public Stage1TestChatImpl(String testCase) throws java.rmi.RemoteException {
        super();
        theTestCase = testCase;
    }

    public String serverName() throws java.rmi.RemoteException{
		if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.NAME]) ) {
			throw new java.rmi.RemoteException();
		} else {
        	return "ChatServer";
        }
    }

	public void connect(ClientCallbacks cl) throws java.rmi.RemoteException{
		if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.CONNECT]) ) {
			throw new java.rmi.RemoteException();
		} else {
			clientsConnected++;
		}
    }

   	public void disconnect(ClientCallbacks cl) throws java.rmi.RemoteException{
        if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.DISCONNECT]) ) {
			throw new java.rmi.RemoteException();
		} else if( clientsConnected > 0 ){
			clientsConnected--;
        } else{
            System.err.println("Fault: disconnect called when no clients connected");
        }
    }
    
    public void sendMessage(String msg) throws java.rmi.RemoteException{
        if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.SEND]) ) {
			throw new java.rmi.RemoteException();
		} else if( clientsConnected <= 0 ){
            System.err.println("Fault: sendMessage called when no clients connected");
        } else {
        	System.out.println(msg);
        }
    }
}
