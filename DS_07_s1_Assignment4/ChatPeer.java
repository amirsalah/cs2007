import java.rmi.RemoteException;

public interface ChatPeer extends java.rmi.Remote 
{
	//
	// ... Define and DOCUMENT the behaviour you design.
	//
	
	public boolean ConnectPeer(ClientCallbacks cl) throws java.rmi.RemoteException;
	
	public boolean ConnectPeer(ClientCallbacks cl,String transcriptName) 
	throws java.rmi.RemoteException,java.io.FileNotFoundException;
	
	public void Disconnect(ClientCallbacks cl) throws java.rmi.RemoteException;
	
	public void SendMessage(String msg) throws java.rmi.RemoteException;
	
	public void SaveTranscript(String transcriptName) throws java.rmi.RemoteException;
	
	public void ShutDown() throws java.rmi.RemoteException;
	
	public String[] GetTranscriptList() throws java.rmi.RemoteException;
}
