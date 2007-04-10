public class Stage1TestDriver {
	private static void usage(String[] args) {
		System.err.println("usage: java Stage1TestDriver [ test=<testCase> | <number of clients> ] [localserver]");
		System.err.print("  where <testcase> is one of: ");
		for ( int testCase = Stage1Faults.FIRST; testCase < Stage1Faults.LIMIT1; testCase++ ) {
		  System.err.print(Stage1Faults.FAULTS[testCase] + " ");
		}
		System.err.println("");
		System.err.println("args.length = "  + args.length);
		System.exit(2);
	}
		
    public static void main(String[] args) { 
    	boolean test = false;
    	boolean localServer = false;
    	String testCase = "";
    	int numClients = 1;
    	
    	if ( args.length > 2 ) {
    		usage(args);
    	} else {
    		for ( int currentArg = 0; currentArg < args.length; currentArg++ ) {
    			if ( args[currentArg].startsWith("test=") ) {
    				test = true;
    				localServer = true;
    				testCase = args[currentArg].substring(5);
    				boolean validTest = false;
    				for ( int testCaseNum = Stage1Faults.FIRST; testCaseNum < Stage1Faults.LIMIT1; testCaseNum++ ) {
		  				if ( testCase.equals(Stage1Faults.FAULTS[testCaseNum])) {
		  					validTest = true;
		  				}
					}
					if ( validTest == false ) usage(args);
				} else if ( args[currentArg].startsWith("localserver") ) {
		    		localServer = true;
		    	} else if ( Character.isDigit(args[currentArg].charAt(0)) ) {
		    	    char digit = args[currentArg].charAt(0);
		    	    numClients = Character.digit(digit,10);
    			} else {
    				usage(args);
    			}
    		}
   		}
   		   			
    	final Naming remoteNaming = new RegistryNamingImpl();
    	final Naming localNaming = new LocalNamingImpl(testCase);
    	if ( localServer ) {
        	try {
        		Chat server;
        		if ( test ) {
        			server = new Stage1TestChatImpl(testCase);
        		} else {
        			server = new ChatImpl();
        		}
	        	try {
    	    		localNaming.rebind("rmi://test",server);
        		} catch (Exception ex) {
        			System.err.println("Exception: " + ex);
        		}
        	} catch (Exception ex) {
        		System.err.println("Exception: " + ex);
        	}
        } 
        
     	final Naming naming = (localServer ? localNaming : remoteNaming);   
     	
    	final Stage1Backend[] backends = new Stage1Backend[numClients];
    	
    	for (int clientNum = 0; clientNum < numClients; clientNum++ ) {
    		backends[clientNum] = new Client(naming);
    	}
    	
        Stage1TextUI textUI = new Stage1TextUI(backends);
       	textUI.processInput();
    }
}
