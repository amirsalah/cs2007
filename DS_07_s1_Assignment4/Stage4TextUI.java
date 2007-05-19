import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Stage4TextUI {	
	private Stage3TextUI.UI[] theUIs; 
	private Stage4Backend[] theBackends;
	
	

	public Stage4TextUI(Stage4Backend[] backends) {
		if ( backends.length > 10 ) {
			throw new Error("At most 10 clients supported");
		}
		
		theUIs = new Stage3TextUI.UI[backends.length];
		theBackends = new Stage4Backend[backends.length];
		
		for ( int backendNum = 0; backendNum < backends.length; backendNum++ ) {
			theBackends[backendNum] = backends[backendNum];
			theUIs[backendNum] = new Stage3TextUI.UI(theBackends[backendNum],backendNum);
		}
	}
	
	
	public void processInput() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
			String line;
			
			final int clientNumLen = 3;
			final int opLen = 5;
			final int prefixLen = clientNumLen + opLen;
		
			while ( ( line = in.readLine() ) != null ) {
				int clientNum = ( line.length() > 1 ) ? Integer.parseInt(line.substring(0,1)) : 0;
				String op = ( line.length() > clientNumLen ) ? line.substring(clientNumLen) : "";
				String arg = ( line.length() > prefixLen ) ? line.substring(prefixLen) : "";
				if ( op.startsWith("LOCA") ) {
					theBackends[clientNum].locate(arg);
				} else if ( op.startsWith("DISC") ) {
					theBackends[clientNum].disconnect();
				} else if ( op.startsWith("SEND") ) {
					theBackends[clientNum].sendMessage(arg);
				} else if ( op.startsWith("FAIL") ) {
					theUIs[clientNum].shouldFailNext();
				} else if ( op.startsWith("SAVE") ) {
					theBackends[clientNum].shutdownAndSave(arg);
				} else if ( op.startsWith("ABOR") ) {
					theBackends[clientNum].shutdownAndAbort();
				} else if ( op.startsWith("CONN") ) {
					theBackends[clientNum].connect();
				} else if ( op.startsWith("SELE") ) {
					theBackends[clientNum].selectTranscript(arg);
				} else if ( op.startsWith("LIST") ) {
					String[] transcripts = theBackends[clientNum].getTranscriptList();
					System.out.println(clientNum + ": List of transcripts:");
					for ( int transNum = 0; transcripts != null && transNum < transcripts.length; transNum++ ) {
						System.out.println(clientNum + ":   " + transcripts[transNum]);
					}
					System.out.println(clientNum + ": End");		
				} else if ( op.startsWith("LOGI") ) {
					String[] creds = arg.split(" ",2);
					String username = creds[0];
					String password = creds[1];
					theBackends[clientNum].login(username,password);
					if ( theBackends[clientNum].isLoggedIn() ) {
						System.out.println(clientNum + ": logged in with username: " + username);
					}
				} else if ( op.startsWith("LOGO") ) {
					theBackends[clientNum].logout();
				} else if ( op.startsWith("CREA") ) {
					String[] creds = arg.split(" ",3);
					String username = creds[0];
					String password = creds[1];
					String priv = creds[2];
					theBackends[clientNum].createAccount(username,password,priv.equals("true"));
				} else if ( op.startsWith("SETP") ) {
					String[] creds = arg.split(" ",2);
					String username = creds[0];
					String password = creds[1];
					theBackends[clientNum].setPassword(username,password);
				} else if ( op.startsWith("PRIV") ) {
					String[] creds = arg.split(" ",2);
					String username = creds[0];
					String priv = creds[1];
					theBackends[clientNum].setPrivilege(username,priv.equals("true"));
				} else if ( op.startsWith("ISPR") ) {
					System.out.println(clientNum + ":" + ((theBackends[clientNum].isPrivileged()) ? " is privileged." : " is not privileged.") );
				} else if ( op.startsWith("JOIN") ) {
					theBackends[clientNum].joinPeer(arg);
				} else if ( op.startsWith("LEAV") ) {
					theBackends[clientNum].leavePeer();
				}
			}
		} catch (java.io.IOException ex) {
			;
		}
		System.exit(0);
	}
}