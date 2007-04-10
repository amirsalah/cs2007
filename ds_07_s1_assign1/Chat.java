public interface Chat extends java.rmi.Remote 
{
    public String serverName() throws java.rmi.RemoteException;
    public void connect(ClientCallbacks cl) throws java.rmi.RemoteException;
    public void disconnect(ClientCallbacks cl) throws java.rmi.RemoteException;
    public void sendMessage(String msg) throws java.rmi.RemoteException;
    
}
