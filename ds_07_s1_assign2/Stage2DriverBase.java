public class Stage2DriverBase {
	static String getTestCase(String arg) {
  		String testCase = arg.substring(5);
    	boolean validTest = false;
    	for ( int testCaseNum = Stage1Faults.FIRST; testCaseNum < Stage1Faults.LIMIT1; testCaseNum++ ) {
			if ( testCase.equals(Stage1Faults.FAULTS[testCaseNum])) {
		  		validTest = true;
		  	}
		}
    	for ( int testCaseNum = Stage2Faults.FIRST; testCaseNum < Stage2Faults.LIMIT2; testCaseNum++ ) {
			if ( testCase.equals(Stage2Faults.FAULTS[testCaseNum])) {
				validTest = true;
			}
		}
		if ( validTest ) {
			return testCase;
		} else {
			return "";
		}
	}
	
	static void printTestCases() {
		for ( int testCase = Stage1Faults.FIRST; testCase < Stage1Faults.LIMIT1; testCase++ ) {
			System.err.print(Stage1Faults.FAULTS[testCase] + " ");
		}
		for ( int testCase = Stage2Faults.FIRST; testCase < Stage2Faults.LIMIT2; testCase++ ) {
			System.err.print(Stage2Faults.FAULTS[testCase] + " ");
		}
	}
	
	static void runLocalServer(String testCase,Naming localNaming) {
		try {
			String testURL = "rmi://test";
        	Chat server = ( testCase.equals("") )  ? new ChatImpl(testURL,localNaming,"LocalServer") : new Stage2TestChatImpl(testCase);
	        try {
    	    	localNaming.rebind(testURL,server);
       		} catch (Exception ex) {
        		System.err.println("Exception in local rebind: " + ex);
        	}
        } catch (Exception ex) {
        	System.err.println("Exception creating local server: " + ex);
        }
    }

}


