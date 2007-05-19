import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

public interface Naming {
	public void rebind(String url,Chat server) 
		throws RemoteException, MalformedURLException;
	public java.rmi.Remote lookup(String url)
		throws RemoteException,NotBoundException, MalformedURLException;
	public void unbind(String url) 
		throws RemoteException,NotBoundException, MalformedURLException;
}