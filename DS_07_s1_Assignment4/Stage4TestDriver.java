public class Stage4TestDriver {
	private static void usage() {
		System.err.println("usage: java Stage4TestDriver [ test=<testCase> | <number of clients> ] [localserver]");
		System.err.print("  where <testcase> is one of: ");
		Stage4DriverBase.printTestCases();
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
    				testCase = Stage4DriverBase.getTestCase(args[currentArg]);
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
    		Stage4DriverBase.runLocalServer(testCase,naming);
        } 
     	
    	final Stage4Backend[] backends = new Stage4Backend[numClients];
    	
    	for (int clientNum = 0; clientNum < numClients; clientNum++ ) {
    		backends[clientNum] = new Client(naming);
    	}
    	
        Stage4TextUI textUI = new Stage4TextUI(backends);
       	textUI.processInput();
    }
}