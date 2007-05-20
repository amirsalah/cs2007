public class Stage3TestChatImpl extends java.rmi.server.UnicastRemoteObject implements Chat {
	protected String theTestCase = "";
	protected int clientsConnected = 0;
	
	//Constructor
    public Stage3TestChatImpl(String testCase) throws java.rmi.RemoteException {
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
    
    class TestKey implements ChatKey {
    	public boolean amPrivileged() {
    		return true;
    	}
    }
    
    private ChatKey testKey = new TestKey();
    
    private final String ADMIN_USERNAME = "admin";
	private String adminPassword = "initadmin";


	public ChatKey login(String username,String password) throws java.rmi.RemoteException {
        if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.LOGIN]) ) {
			throw new java.rmi.RemoteException();
		} else if ( username.equals(ADMIN_USERNAME) && password.equals(adminPassword) ) {
			return testKey;
		} else {
			return null;
		}
	}

	public boolean connect(ChatKey key,ClientCallbacks cl) throws java.rmi.RemoteException,java.security.InvalidKeyException {
        if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.CONNECTKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.CONNECT]) ) {
			throw new java.rmi.RemoteException();
		} else {
			clientsConnected++;
			return ( clientsConnected == 1 );
		}
    }

   	public void disconnect(ChatKey key,ClientCallbacks cl) throws java.rmi.RemoteException,java.security.InvalidKeyException {
        if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.DISCONNECTKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.DISCONNECT]) ) {
			throw new java.rmi.RemoteException();
		} else if( clientsConnected > 0 ){
			clientsConnected--;
        } else{
            System.err.println("Fault: disconnect called when no clients connected");
        }
    }
    
    public void sendMessage(ChatKey key,String msg) throws java.rmi.RemoteException,java.security.InvalidKeyException {
        if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SENDKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.SEND]) ) {
			throw new java.rmi.RemoteException();
		} else if( clientsConnected <= 0 ){
            System.err.println("Fault: sendMessage called when no clients connected");
        } else {
        	System.out.println(msg);
        }
    }
    
	private String[] transcriptNames = new String[] { "One", "Two", "Three" };
	
	
	public String[] getTranscriptList(ChatKey key) throws java.rmi.RemoteException,java.security.InvalidKeyException,java.security.AccessControlException {
		if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.GETTRANSKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.GETTRANSPRIV]) ) {
			throw new java.security.AccessControlException("Insufficiently Privileged Key");
		} else if ( theTestCase.equals(Stage2Faults.FAULTS[Stage2Faults.LIST]) ) {
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

	public boolean connect(ChatKey key,ClientCallbacks cl,String transcriptName) throws java.rmi.RemoteException,java.io.FileNotFoundException,java.security.InvalidKeyException,java.security.AccessControlException {
		if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.CONNECTWITHTRANSKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.CONNECTWITHTRANSPRIV]) ) {
			throw new java.security.AccessControlException("Insufficiently Privileged Key");
		} else if ( theTestCase.equals(Stage2Faults.FAULTS[Stage2Faults.CONNECT_WITH_TRANS]) ) {
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
	
	public void saveTranscript(ChatKey key,String transcriptName) throws java.rmi.RemoteException,java.security.InvalidKeyException,java.security.AccessControlException {
		if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SAVETRANSKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SAVETRANSPRIV]) ) {
			throw new java.security.AccessControlException("Insufficiently Privileged Key");
		} else if ( theTestCase.equals(Stage2Faults.FAULTS[Stage2Faults.SAVE]) ) {
			throw new java.rmi.RemoteException();
		} 
	}
	
    public void shutdown(ChatKey key) throws java.rmi.RemoteException,java.security.InvalidKeyException,java.security.AccessControlException {
    	if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SHUTDOWNKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SHUTDOWNPRIV]) ) {
			throw new java.security.AccessControlException("Insufficiently Privileged Key");
		} else {
    		throw new java.rmi.RemoteException();
    	}
	}
	
	public void logout(ChatKey key)  throws java.rmi.RemoteException,java.security.InvalidKeyException {
		if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.LOGOUTKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.LOGOUT]) ) {
			throw new java.rmi.RemoteException();
		} 
	}
	
	public boolean createAccount(ChatKey creatorKey,String username,String password,boolean isPrivileged) throws java.rmi.RemoteException,java.security.InvalidKeyException,java.security.AccessControlException {
		if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.CREATEACCKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.CREATEACCPRIV]) ) {
			throw new java.security.AccessControlException("Insufficiently Privileged Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.CREATEACC]) ) {
			throw new java.rmi.RemoteException();
		} else {
			return true;
		}
	}
	
	public void setPrivilege(ChatKey adminKey,String username,boolean isPrivileged) throws java.rmi.RemoteException,java.security.InvalidKeyException,java.security.AccessControlException {
		if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SETPRIVKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SETPRIVPRIV]) ) {
			throw new java.security.AccessControlException("Insufficiently Privileged Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SETPRIV]) ) {
			throw new java.rmi.RemoteException();
		} 
	}
	
	public void setPassword(ChatKey key,String username,String newPassword) throws java.rmi.RemoteException,java.security.InvalidKeyException,java.security.AccessControlException {
		if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SETPASSKEY]) ) {
			throw new java.security.InvalidKeyException("Invalid Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SETPASSPRIV]) ) {
			throw new java.security.AccessControlException("Insufficiently Privileged Key");
		} else if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.SETPASS]) ) {
			throw new java.rmi.RemoteException();
		} 
	}

	public boolean isPrivileged(ChatKey key) throws java.rmi.RemoteException{ 
		if ( theTestCase.equals(Stage3Faults.FAULTS[Stage3Faults.ISPRIV]) ) {
			throw new java.rmi.RemoteException();
		} else {
	    	return true;
	    }
    }
}
