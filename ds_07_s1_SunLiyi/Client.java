
public class Client implements ClientCallbacks,Stage1Backend,Stage2Backend 
{
	private Chat remoteServer = null;                       
	private Naming naming;
	private String[] alltranscript;
	private String transcriptname="";
	private Stage1UserInterface ui1;
	private Stage2UserInterface ui2;
	
	public Client(Naming naming)
	{
		this.naming=naming;
	}
	
	public void attach(Stage1UserInterface ui)
	{
		try
		{
			ui1=ui;
		}
		catch(Exception e)
		{
			System.out.println("Exception");
		}
	}
	
	public void attach(Stage2UserInterface ui)
	{
		try
		{
			ui2=ui;
		}
		catch(Exception e)
		{
			System.out.println("Exception");
		}
	}
	
	public void receiveMessage(String msg) throws java.rmi.RemoteException
	{
		ui1.displayMessage(msg);
	}
	
	public boolean sendMessage(String message)
	{
		try
		{
			remoteServer.sendMessage(message);
		}
		catch(Exception e)
		{
			ui1.displayAlert("SendMessage: Could not communicate with server.");
			try
			{
				java.rmi.server.UnicastRemoteObject.unexportObject(this,true);
				return false;
			}
			catch(java.rmi.NoSuchObjectException nsoe)
			{
				ui1.displayAlert("Disconnect: Could not communicate with server.");
			}
			return false;
		}
		return true;
	}
	
	public boolean locate(String url)
	{
		try 	
		{
			remoteServer=(Chat)naming.lookup(url);
		} 
		catch (java.net.MalformedURLException e)
		{
			ui1.displayAlert("Locate: Malformed URL: "+ e.getMessage());
                        return false;
		}
		catch (java.rmi.RemoteException e)
		{
			ui1.displayAlert("Locate: Could not communicate with registry.");
			return false;
		}
		catch (java.rmi.NotBoundException e)
		{
			ui1.displayAlert("Locate: No binding in registry for: "+ e.getMessage());
			return false;
		}
		catch (java.lang.ClassCastException e)
		{
			ui1.displayAlert("Locate: Stub is not of expected type.");
			return false;
		}
	      	return true;
	}
	
	public boolean connect()
	{
		String serverName="";
		try
		{
			serverName=remoteServer.serverName();
		}
		catch(Exception e)
		{
			ui1.displayAlert("ServerName: Could not communicate with server.");
			return false;
		}
		
		try
		{
			java.rmi.server.UnicastRemoteObject.exportObject(this);
			if(transcriptname.equals(""))
			{
				if(remoteServer.connect(this))
					ui2.setTranscriptLabelText("Started new session at server \""+serverName+"\"");
				else
					ui2.setTranscriptLabelText("Connected to ongoing session at server \""+serverName+"\"");
			}
			else
			{
				if(remoteServer.connect(this,transcriptname))
					ui2.setTranscriptLabelText("Resumed session using transcript \""+transcriptname+"\" at server \""+serverName+"\"");
				else
					ui2.setTranscriptLabelText("Connected to ongoing session at server \""+serverName+"\"");
			}
		}
		catch(java.io.FileNotFoundException fnfe)
		{
			ui1.displayAlert("Connect: transcript not found: savetestnotfound");
			return false;
		}
		catch(java.rmi.RemoteException e)
		{
			ui1.displayAlert("Connect: Could not communicate with server.");
			return false;
		}

		return true;
	}
		
	public void disconnect()
	{
		try
		{
			remoteServer.disconnect(this);
			java.rmi.server.UnicastRemoteObject.unexportObject(this,true);				
		}
		catch(java.rmi.RemoteException e)
		{
			ui1.displayAlert("Disconnect: Could not communicate with server.");
		}
	}
	
	public String[] getTranscriptList()
	{
		try
		{
			alltranscript=remoteServer.getTranscriptList();
			return alltranscript;
		}
		catch(java.rmi.RemoteException e)
		{
  			ui1.displayAlert("GetTranscriptList: Could not communicate with server.");
  		}
  		return null;
	}
	
	public void selectTranscript(String transcriptName)
	{		
  		transcriptname=transcriptName;
  	}
	
	public void shutdownAndSave(String transcriptName)
	{
		try
		{
  			remoteServer.saveTranscript(transcriptName);
  			remoteServer.shutdown();
  		}
		catch(java.rmi.RemoteException e)
		{
			ui1.displayAlert("SaveTranscript: Could not communicate with server.");
  		}
	}
	
	public void shutdownAndAbort()
	{
		try
		{
  			remoteServer.shutdown();
  		}
		catch(java.rmi.RemoteException e)
		{
			ui1.displayAlert(e.getMessage());
  		}
	}
}
