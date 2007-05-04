import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Stage2TextUI {	
	static class UI extends Stage1TextUI.UI implements Stage2UserInterface {
		public UI(Stage2Backend backend,int UINum) {
			super(backend,UINum);
			backend.attach((Stage2UserInterface)this);
		}

		public void setTranscriptLabelText(String message) {
			System.out.println(uiNum() + ": *** TranscriptLabel: " + message + " ***");
		}
	}

	private UI[] theUIs; 
	private Stage2Backend[] theBackends;
	
	

	public Stage2TextUI(Stage2Backend[] backends) {
		if ( backends.length > 10 ) {
			throw new Error("At most 10 clients supported");
		}
		
		theUIs = new UI[backends.length];
		theBackends = new Stage2Backend[backends.length];
		
		for ( int backendNum = 0; backendNum < backends.length; backendNum++ ) {
			theBackends[backendNum] = backends[backendNum];
			theUIs[backendNum] = new UI(theBackends[backendNum],backendNum);
			theBackends[backendNum].attach((Stage2UserInterface)theUIs[backendNum]);
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
						connected[clientNum] = false;
					}
				} else if ( op.startsWith("SAVE") ) {
					if ( connected[clientNum] ) {
						theBackends[clientNum].shutdownAndSave(arg);
						connected[clientNum] = false;
					}
				} else if ( op.startsWith("ABOR") ) {
					if ( connected[clientNum] ) {
						theBackends[clientNum].shutdownAndAbort();
						connected[clientNum] = false;
					}
				}  else if ( op.startsWith("SELE") ) {
					if ( located[clientNum] && !connected[clientNum] ) {
						theBackends[clientNum].selectTranscript(arg);
					}
				}  else if ( op.startsWith("LIST") ) {
					if ( located[clientNum] ) {
						String[] transcripts = theBackends[clientNum].getTranscriptList();
						System.out.println(clientNum + ": List of transcripts:");
						for ( int transNum = 0; transcripts != null && transNum < transcripts.length; transNum++ ) {
							System.out.println(clientNum + ":   " + transcripts[transNum]);
						}
						System.out.println(clientNum + ": End");						
					}
				} 
					
			}
		} catch (java.io.IOException ex) {
			;
		}
		System.exit(0);
	}
}