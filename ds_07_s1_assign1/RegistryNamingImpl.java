import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

public class RegistryNamingImpl implements Naming {
	public void rebind(String url,Chat server) 
		throws RemoteException, MalformedURLException 
	{
		java.rmi.Naming.rebind(url,server);
	}
	
	public java.rmi.Remote lookup(String url)
		throws RemoteException,NotBoundException, MalformedURLException 
	{
		return java.rmi.Naming.lookup(url);
	}

}