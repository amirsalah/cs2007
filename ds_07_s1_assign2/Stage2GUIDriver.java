public class Stage2GUIDriver {
	private static void usage() {
		System.err.println("usage: java Stage2GUIDriver [test=<testCase> | localserver ]");
		System.err.print("  where <testcase> is one of: ");
		Stage2DriverBase.printTestCases();
		System.err.println("");
		System.exit(2);
	}
		
    public static void main(String[] args) { 
    	boolean localServer = false;
    	String testCase = "";
    	
    	if ( args.length > 1 ) {
    		usage();
    	} else {
    		for ( int currentArg = 0; currentArg < args.length; currentArg++ ) {
				if ( args[currentArg].startsWith("test=") ) {
    				localServer = true;
    				testCase = Stage2DriverBase.getTestCase(args[currentArg]);
					if ( testCase == "" ) usage();
				} else if ( args[currentArg].startsWith("localserver") ) {
		    		localServer = true;
    			} else {
    				usage();
    			}
    		}
   		}
   		   			
     	final Naming naming = ( localServer ? new LocalNamingImpl(testCase) : new RegistryNamingImpl());   	

    	if ( localServer ) {
    		Stage2DriverBase.runLocalServer(testCase,naming);
        } 
        
    	final Stage2Backend backend = new Client(naming);
    	
       	//Schedule a job for the event-dispatching thread:
       	//creating and showing this application's GUI.
       	javax.swing.SwingUtilities.invokeLater(new Runnable() {
           	public void run() {
               	Stage2GUI.createAndShowGUI(backend);
           	}
       	});
    }
}
