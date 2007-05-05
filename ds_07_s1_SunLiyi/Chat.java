public interface Chat extends java.rmi.Remote 
{
    public String serverName() throws java.rmi.RemoteException;
    public String[] getTranscriptList() throws java.rmi.RemoteException;
    public boolean connect(ClientCallbacks cl) throws java.rmi.RemoteException;
    public boolean connect(ClientCallbacks cl,String transcriptName) throws java.rmi.RemoteException,java.io.FileNotFoundException;
    public void disconnect(ClientCallbacks cl) throws java.rmi.RemoteException;
    public void sendMessage(String msg) throws java.rmi.RemoteException;
    public void saveTranscript(String transcriptName) throws java.rmi.RemoteException;
    public void shutdown() throws java.rmi.RemoteException;    
}
