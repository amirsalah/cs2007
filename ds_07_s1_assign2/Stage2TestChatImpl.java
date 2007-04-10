public class Stage2TestChatImpl extends java.rmi.server.UnicastRemoteObject implements Chat {
	protected String theTestCase = "";
	protected int clientsConnected = 0;
	
	//Constructor
    public Stage2TestChatImpl(String testCase) throws java.rmi.RemoteException {
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

	public boolean connect(ClientCallbacks cl) throws java.rmi.RemoteException{
		if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.CONNECT]) ) {
			throw new java.rmi.RemoteException();
		} else {
			clientsConnected++;
			return ( clientsConnected == 1 );
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
    
	private String[] transcriptNames = new String[] { "One", "Two", "Three" };
	
	
	public String[] getTranscriptList() throws java.rmi.RemoteException {
		if ( theTestCase.equals(Stage2Faults.FAULTS[Stage2Faults.LIST]) ) {
			throw new java.rmi.RemoteException();
		} else {
			return transcriptNames;
		}
	}
	
	private boolean validTranscriptName(String transcriptName) {
		boolean validTranscript = false;
		for ( int transNum = 0; transNum < transcriptNames.length; transNum++ ) {
			if ( transcriptName.equals(transcriptNames[transNum]) ) {
				validTranscript = true;
				break;
			}
		}
		return validTranscript;
	}

	public boolean connect(ClientCallbacks cl,String transcriptName) throws java.rmi.RemoteException,java.io.FileNotFoundException {
		if ( theTestCase.equals(Stage2Faults.FAULTS[Stage2Faults.CONNECT_WITH_TRANS]) ) {
			throw new java.rmi.RemoteException();
		} else if ( clientsConnected == 0 ) {
			if ( validTranscriptName(transcriptName) ) {
				clientsConnected++;
				return true;
			} else {
				throw new java.io.FileNotFoundException(transcriptName);
			}
		} else {
			clientsConnected++;
			return false;
		}
	}
	
	public void saveTranscript(String transcriptName) throws java.rmi.RemoteException {
		if ( theTestCase.equals(Stage2Faults.FAULTS[Stage2Faults.SAVE]) ) {
			throw new java.rmi.RemoteException();
		} 
	}
	
    public void shutdown() throws java.rmi.RemoteException {
    	throw new java.rmi.RemoteException();
	}

}
