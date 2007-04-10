public interface ClientCallbacks extends java.rmi.Remote 
{
    public void receiveMessage(String msg) throws java.rmi.RemoteException;
    
}
