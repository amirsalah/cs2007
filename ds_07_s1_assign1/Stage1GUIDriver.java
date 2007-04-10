public class Stage1GUIDriver {
	private static void usage() {
		System.err.println("usage: java Stage1GUIDriver [test=<testCase>]");
		System.err.print("  where <testcase> is one of: ");
		for ( int testCase = Stage1Faults.FIRST; testCase < Stage1Faults.LIMIT1; testCase++ ) {
		  System.err.print(Stage1Faults.FAULTS[testCase] + " ");
		}
		System.err.println("");
		System.exit(2);
	}
		
    public static void main(String[] args) { 
    	boolean test = false;
    	String testCase = "";
    	
    	if ( args.length > 1 ) {
    		usage();
    	} else {
    		for ( int currentArg = 0; currentArg < args.length; currentArg++ ) {
				if ( args[currentArg].startsWith("test=") ) {
    				test = true;
    				testCase = args[currentArg].substring(5);
    				boolean validTest = false;
    				for ( int testCaseNum = Stage1Faults.FIRST; testCaseNum < Stage1Faults.LIMIT1; testCaseNum++ ) {
		  				if ( testCase.equals(Stage1Faults.FAULTS[testCaseNum])) {
		  					validTest = true;
		  				}
					}
					if ( validTest == false ) usage();
    			} else {
    				usage();
    			}
    		}
   		}
   		   			
    	final Naming remoteNaming = new RegistryNamingImpl();
    	final Naming localNaming = new LocalNamingImpl(testCase);
    	if ( test == false ) {
        	try {
        		final Chat testServer = new Stage1TestChatImpl(testCase);
	        	try {
    	    		localNaming.rebind("rmi://test",testServer);
        		} catch (Exception ex) {
        			System.err.println("Exception: " + ex);
        		}
        	} catch (Exception ex) {
        		System.err.println("Exception: " + ex);
        	}
        }
     	final Naming naming = (test ? localNaming : remoteNaming);   	
    	
    	final Stage1Backend backend = new Client(naming);
    	
       	//Schedule a job for the event-dispatching thread:
       	//creating and showing this application's GUI.
       	javax.swing.SwingUtilities.invokeLater(new Runnable() {
           	public void run() {
               	Stage1GUI.createAndShowGUI(backend);
           	}
       	});
    }
}
