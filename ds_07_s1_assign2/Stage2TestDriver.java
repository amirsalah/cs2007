public class Stage2TestDriver {
	private static void usage() {
		System.err.println("usage: java Stage2TestDriver [ test=<testCase> | <number of clients> ] [localserver]");
		System.err.print("  where <testcase> is one of: ");
		Stage2DriverBase.printTestCases();
		System.err.println("");
		System.exit(2);
	}
		
    public static void main(String[] args) { 
    	boolean localServer = false;
    	String testCase = "";
    	int numClients = 1;
    	
    	if ( args.length > 2 ) {
    		usage();
    	} else {
    		for ( int currentArg = 0; currentArg < args.length; currentArg++ ) {
    			if ( args[currentArg].startsWith("test=") ) {
    				localServer = true;
    				testCase = Stage2DriverBase.getTestCase(args[currentArg]);
					if ( testCase == null ) usage();
				} else if ( args[currentArg].startsWith("localserver") ) {
		    		localServer = true;
		    	} else if ( Character.isDigit(args[currentArg].charAt(0)) ) {
		    	    char digit = args[currentArg].charAt(0);
		    	    numClients = Character.digit(digit,10);
    			} else {
    				usage();
    			}
    		}
   		}
   		   			
     	final Naming naming = ( localServer ? new LocalNamingImpl(testCase) : new RegistryNamingImpl());   	

    	if ( localServer ) {
    		Stage2DriverBase.runLocalServer(testCase,naming);
        } 
     	
    	final Stage2Backend[] backends = new Stage2Backend[numClients];
    	
    	for (int clientNum = 0; clientNum < numClients; clientNum++ ) {
    		backends[clientNum] = new Client(naming);
    	}
    	
        Stage2TextUI textUI = new Stage2TextUI(backends);
       	textUI.processInput();
    }
}