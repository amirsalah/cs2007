public class Stage4DriverBase {
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
    	for ( int testCaseNum = Stage3Faults.FIRST; testCaseNum < Stage3Faults.LIMIT3; testCaseNum++ ) {
			if ( testCase.equals(Stage3Faults.FAULTS[testCaseNum])) {
				validTest = true;
			}
		}
    	for ( int testCaseNum = Stage4Faults.FIRST; testCaseNum < Stage4Faults.LIMIT4; testCaseNum++ ) {
			if ( testCase.equals(Stage4Faults.FAULTS[testCaseNum])) {
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
		for ( int testCase = Stage3Faults.FIRST; testCase < Stage3Faults.LIMIT3; testCase++ ) {
			System.err.print(Stage3Faults.FAULTS[testCase] + " ");
		}
		for ( int testCase = Stage4Faults.FIRST; testCase < Stage4Faults.LIMIT4; testCase++ ) {
			System.err.print(Stage4Faults.FAULTS[testCase] + " ");
		}
	}
	
	static void runLocalServer(String testCase,Naming localNaming) {
		try {
			String testURL = "rmi://test";
        	Chat server = ( testCase.equals("") )  ? new ChatImpl(testURL,localNaming,"LocalServer") : new Stage4TestChatImpl(testCase);
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


