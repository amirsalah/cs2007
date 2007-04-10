import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.Remote;

public class LocalNamingImpl implements Naming {
	Chat theServer = null;
	String theTestCase = "";
	
	LocalNamingImpl(String testCase) {
		theTestCase = testCase;
	}	
	
	class BadType implements Remote {
	}
	
	public Remote maskType() {
		return new BadType();
	}

	public void rebind(String url,Chat server) 
		throws RemoteException, MalformedURLException 
	{
		if ( url.startsWith("rmi://") == false ) {
			throw new MalformedURLException(url);
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.BIND]) ) {
			throw new RemoteException();
		} else {
			theServer = server;
		}
	}
	
	public Remote lookup(String url)
		throws RemoteException,NotBoundException, MalformedURLException 
	{	
		if ( url.startsWith("rmi://") == false ) {
			throw new MalformedURLException(url);
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.LOOKUP]) ) {
			throw new RemoteException();
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.NOT_BOUND]) ) {
			throw new NotBoundException(url);
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.WRONG_TYPE]) ) {
			return maskType();
		} else {
			return theServer;
		}

	}

	public void unbind(String url)
		throws RemoteException,NotBoundException, MalformedURLException 
	{
		if ( url.startsWith("rmi://") == false ) {
			throw new MalformedURLException(url);
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.LOOKUP]) ) {
			throw new RemoteException();
		} else if ( theTestCase.equals(Stage1Faults.FAULTS[Stage1Faults.NOT_BOUND]) ) {
			throw new NotBoundException(url);
		} else {
			;
		}

	}
}