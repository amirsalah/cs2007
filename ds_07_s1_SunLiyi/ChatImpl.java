import java.util.*;
import java.io.*;

public class ChatImpl 
	extends java.rmi.server.UnicastRemoteObject 
implements Chat 
{	
	public String serverName = null;
	public String serverURL = null;
	public Naming naming = null;
	private Vector<String> AllMessages= new Vector<String>();
	private Vector<ClientCallbacks> clients=new Vector<ClientCallbacks>();
	private File transcripts = new File("transcripts");

	public ChatImpl()
	throws java.rmi.RemoteException
	{	
		super();
		serverName="Liyi's place";
	}
	
	public ChatImpl(String boundURL,Naming naming,String name)
	throws java.rmi.RemoteException
	{
		super();
		this.naming = naming;
		serverName=name;
		serverURL=boundURL;
	}
	
	public String serverName()
	throws java.rmi.RemoteException
	{
		return serverName;
	}
	
	public boolean connect(ClientCallbacks cl)
	throws java.rmi.RemoteException
	{
		clients.add(cl);
		if(clients.size()==1)
		{
			AllMessages.clear();
			return true;
		}
		else
		{
			for(int i=0;i<AllMessages.size();i++)
			{
				cl.receiveMessage(AllMessages.get(i));
			}
			return false;
		}
	}
	
	public boolean connect(ClientCallbacks cl,String transcriptName)
	throws java.rmi.RemoteException,java.io.FileNotFoundException
	{
		
		if(clients.isEmpty())
		{
			AllMessages.clear();
			String messageline=null;
			clients.add(cl);
			File load = new File("transcripts/"+ transcriptName);
			if (load.exists()) 
			{
				try
				{
				BufferedReader loadFile = new BufferedReader(new FileReader("transcripts/"+ transcriptName));
				messageline=loadFile.readLine();
				while(messageline!=null) 
					{
					AllMessages.add(messageline); 
					cl.receiveMessage(messageline);
					messageline=loadFile.readLine();
					}
				loadFile.close();
				return true;
				}
				catch(IOException e){
					System.out.println("IO Exception");
					return true;
				}
			} 
			else 
				throw new java.io.FileNotFoundException(transcriptName);
		}
		else
		{
			clients.add(cl);
			for(int i=0;i<AllMessages.size();i++)
				{
				cl.receiveMessage(AllMessages.get(i));
				}
				return false;
		}
		    	  	
	}
	
	public void disconnect(ClientCallbacks cl)
	throws java.rmi.RemoteException
	{
		if(clients.contains(cl)) 
		{
			clients.remove(cl);
		}
		else 
			throw new java.rmi.RemoteException();
	}
	
	public void sendMessage(String msg) 
	throws java.rmi.RemoteException
	{
		for(int i=0;i<clients.size();i++)
		{
			try
			{
				if(clients.get(i)!=null)
				((ClientCallbacks)clients.get(i)).receiveMessage(msg);
			}
			catch(java.rmi.RemoteException e)
			{
				clients.set(i,null);
			}
		}
		AllMessages.add(msg);
	}
	
	public void shutdown() throws java.rmi.RemoteException
	{
		try 
		{
			java.rmi.server.UnicastRemoteObject.unexportObject(this,true);
			naming.unbind(serverURL);
		} 
		catch(java.rmi.RemoteException e) 
		{
			System.out.println("Exception");
		}
		catch(java.net.MalformedURLException e)
		{
			System.out.println("Exception");
		}
		catch(java.rmi.NotBoundException e)
		{
			System.out.println("Exception");
		}
		AllMessages.clear();
		System.exit(1);
		
	}
	
	public void saveTranscript(String transcriptName) 
	throws java.rmi.RemoteException
	{
		PrintWriter writer = null;
		try 
		{
			transcripts.mkdir();
			writer=new PrintWriter(new BufferedWriter(new FileWriter("transcripts/"+transcriptName)));
			for(int i=0; i<AllMessages.size(); i++)
			{
				writer.println(AllMessages.get(i));
			}
			writer.close();
			AllMessages.clear();
		}
		catch(IOException e)
		{
			System.out.println("IO Exception");
		}
	}
	
	public String[] getTranscriptList() throws java.rmi.RemoteException
	{
		String[] temp = null;
		int length = transcripts.list().length;
		String[] localtranscripts = transcripts.list();
		for(int i=0; i<length/2; i++){
			temp[i] = localtranscripts[length-1-i];
		}
			return temp;
	}
}
