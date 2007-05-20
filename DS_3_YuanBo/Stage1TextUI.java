import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Stage1TextUI {	
	static class UI implements Stage1UserInterface {
		private Stage1Backend theBackend;
		private int theUINum;
		protected boolean shouldFail = false;
		
		public UI(Stage1Backend backend,int UINum) {
			backend.attach(this);
			theBackend = backend;
			theUINum = UINum;
		}
		
		protected int uiNum() {
			return theUINum;
		}
		
		public void displayMessage(String message)  throws java.rmi.RemoteException {
			if ( shouldFail ) {
				shouldFail = false;
				throw new java.rmi.RemoteException();
			} else {
				System.out.println(uiNum() + ": " + message);
			}
		}
		
		public void shouldFailNext() {
				shouldFail = true;
		}
	    
		public void displayAlert(String message) {
			System.out.println(uiNum() + ": *** Alert: " + message + " ***");
		}
	    
		public void quit(String reason) {
	    	displayAlert(reason);
	    	System.exit(1);
		}
	}
	
	private UI[] theUIs; 
	private Stage1Backend[] theBackends;

	public Stage1TextUI(Stage1Backend[] backends) {
		if ( backends.length > 10 ) {
			throw new Error("At most 10 clients supported");
		}
		
		theUIs = new UI[backends.length];
		theBackends = new Stage1Backend[backends.length];
		
		for ( int backendNum = 0; backendNum < backends.length; backendNum++ ) {
			theBackends[backendNum] = backends[backendNum];
			theUIs[backendNum] = new UI(theBackends[backendNum],backendNum);
			theBackends[backendNum].attach(theUIs[backendNum]);
		}
	}
	
	
	public void processInput() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
			String line;
			boolean[] located = new boolean[theBackends.length];
			boolean[] connected = new boolean[theBackends.length];
			
			for ( int clientNum = 0; clientNum < theBackends.length; clientNum++ ) {
				located[clientNum] = false;
				connected[clientNum] = false;
			}
			
			final int clientNumLen = 3;
			final int opLen = 5;
			final int prefixLen = clientNumLen + opLen;
		
			while ( ( line = in.readLine() ) != null ) {
				int clientNum = ( line.length() > 1 ) ? Integer.parseInt(line.substring(0,1)) : 0;
				String op = ( line.length() > clientNumLen ) ? line.substring(clientNumLen) : "";
				String arg = ( line.length() > prefixLen ) ? line.substring(prefixLen) : "";
				if ( op.startsWith("LOCA") ) {
					if ( theBackends[clientNum].locate(arg) ) located[clientNum] = true;
				} else if ( op.startsWith("CONN") ) {
					if ( located[clientNum] && !connected[clientNum] && theBackends[clientNum].connect() ) connected[clientNum] = true;
				} else if ( op.startsWith("DISC") ) {
					if ( connected[clientNum] ) {
						theBackends[clientNum].disconnect();
						connected[clientNum] = false;
					}
				} else if ( op.startsWith("SEND") ) {
					if ( connected[clientNum] ) {
						if ( theBackends[clientNum].sendMessage(arg) == false ) {
							connected[clientNum] = false;
						}
					}
				} else if ( op.startsWith("FAIL") ) {
					if ( connected[clientNum] ) {
						theUIs[clientNum].shouldFailNext();
					}
				}
					
			}
		} catch (java.io.IOException ex) {
			;
		}
		System.exit(0);
	}
}