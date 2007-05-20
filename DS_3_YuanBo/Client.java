
import java.util.*;
import java.security.InvalidKeyException;
import java.security.AccessControlException;
import java.rmi.RemoteException;


public class Client implements ClientCallbacks, Stage3Backend
{
	private Stage1UserInterface ui;
	private Stage2UserInterface ui2;
	private Stage3UserInterface ui3;
	private Chat chat;
	private Naming name;
	private ClientCallbacks callbacks;
	private String untype="Stub is not of expected type.";
	private String stubnotfound="Stub is not of expected type.";
	private String registryremote="Could not communicate with registry.";
	private String serverremote="Could not communicate with server.";
	private String scriptname;
	private ChatKey k=null;
	//private int ID;
	//private Random rd;
	
	public Client(Naming name)
	{
	  //rd = new Random();
	  super();
	  this.name=name;
	  callbacks=this;
	  //ID=rd.nextInt();
	}	
	public boolean locate(String url)
	{
		try
		{
		  chat = (Chat) name.lookup(url);
		  return true;
		 }
		
		catch(java.rmi.NotBoundException e)
		{
			ui.displayAlert("Locate: No binding in registry for: "+url);
			return false;
		}
		catch(java.net.MalformedURLException e)
		{ 
			//ui.displayAlert(url);
			ui.displayAlert("Locate: Malformed URL: "+e.getMessage());
			return false;
		}
		 catch(java.rmi.StubNotFoundException e)
		 {
		    ui.displayAlert("Locate: "+stubnotfound);
		    return false;
		   }
		 catch(java.rmi.RemoteException e)
		 {
		  ui.displayAlert("Locate: "+registryremote);
		 	return false;
		}
		catch(Exception e)
		{
		   ui.displayAlert("Locate: "+untype);
		   }
		return false;
	}
	public boolean login(String username,String password)
	{
		try{
			k=chat.login(username,password);
			if(!isLoggedIn())
			  ui.displayAlert("Login: Bad username or password.");
		  return true;
		}
		catch(RemoteException e)
		{
			ui.displayAlert("Login: Could not communicate with server.");
		}
		return false;
	}
	
	public boolean isPrivileged()//?
	{
		boolean test = false;
		try{
		  //test = chat.isPrivileged(k);
		  test = k.amPrivileged();
		}
		catch(Exception e)
		{
		   
		   //ui.displayAlert("isprivilege");
		}
	  return test;
	}
	
	public boolean isLoggedIn()//??
	{
		if(k!=null)
		  return true;
		else
		  return false;
	}
	
	public boolean logout()//???
	{
		try{
			chat.logout(k);
			k=null;
			try{//????
			    java.rmi.server.UnicastRemoteObject.unexportObject(callbacks,true);
		   }
		  catch(java.rmi.RemoteException er)
		  {}
		}
		catch(RemoteException e)
		{
			ui.displayAlert("Logout: Could not communicate with server.");
			return false;
		}
		catch(InvalidKeyException e)
		{
			  ui3.invalidKey("Logout");
		  
		 }
		 return true;
	}
	
	public boolean createAccount(String username,String password,boolean priv)
	{
		boolean test = false;
		
		try{
		test = chat.createAccount(k,username,password,priv);
		}
		catch(RemoteException e)
		{
			ui.displayAlert("CreateAccount: Could not communicate with server.");
			//ui.displayAlert("Login: Bad username or password.");
			return false;
		}
		catch(InvalidKeyException e)
		{
			  ui3.invalidKey("CreateAccount");
			  k=null;
			  return false;
		  
		 }
		 catch(AccessControlException e)
		 {
		 	  ui.displayAlert("CreateAccount: Unprivileged key.");
			  k=null;
			  return false;
		 	}
		 	
		 return test;
	}
	
	public boolean setPrivilege(String username,boolean priv)
	{
		try{
			chat.setPrivilege(k,username,priv);
		}
		catch(RemoteException e)
		{
			ui.displayAlert("SetPrivilege: Could not communicate with server.");
		}
		catch(InvalidKeyException e)
		{
			  ui3.invalidKey("SetPrivilege");
		  
		 }
		 catch(AccessControlException e)
		 {
		 	  ui.displayAlert("SetPrivilege: Unprivileged key.");
		 	}
		 	
		 	return true;
	}
	
	public boolean setPassword(String username,String password)
	{
		try{
			chat.setPassword(k,username,password);
		}
		catch(RemoteException e)
		{
			ui.displayAlert("SetPassword: Could not communicate with server.");
		}
		catch(InvalidKeyException e)
		{
			  ui3.invalidKey("setPassword");
		  
		 }
		 catch(AccessControlException e)
		 {
		 	  ui.displayAlert("SetPassword: Unprivileged key.");
		 	}
		 	
		 	return true;
	}
	
	public void attach(Stage3UserInterface ui3)
	{
		this.ui3=ui3;
	}
	
	public boolean connect()
	{
		try{
			java.rmi.server.UnicastRemoteObject.exportObject(callbacks);
		 
		}
		catch(java.rmi.RemoteException e)
		{
			ui.displayAlert(e.getMessage());
			return false;
		}
		
		
			if(scriptname==null)
			 {
			 	String servername=new String();
				try{
					servername=chat.serverName();
					
				}
				catch(java.rmi.RemoteException re)
				{
						ui.displayAlert("ServerName: Could not communicate with server.");
						return false;
				}
				 try{
				 if(chat.connect(k,callbacks))
			        {
		
					ui2.setTranscriptLabelText("Started new session at server "+"\""+servername+"\"");
				}else{
					ui2.setTranscriptLabelText("Connected to ongoing session at server "+"\""+servername+"\"");
				}
				return true;
			  }
			  catch(java.rmi.RemoteException re)
			  {
			  	ui.displayAlert("Connect: "+serverremote);
			  	return false;
			  }
			  catch(InvalidKeyException ine)
			  {
			  	ui3.invalidKey("Connect");
			  }
			}else{
				String servername=new String();
				try{
					servername=chat.serverName();
					
				}
				catch(java.rmi.RemoteException re)
				{
						ui.displayAlert("ServerName: Could not communicate with server.");
						return false;
				}
				try{
				  if(chat.connect(k,callbacks,scriptname))
			          {
					  ui2.setTranscriptLabelText("Resumed session using transcript \""+scriptname+"\" at server "+"\""+servername+"\"");
				  }else{
					  ui2.setTranscriptLabelText("Connected to ongoing session at server "+"\""+servername+"\"");
					  
				  }
				  return true;
				}
				catch(java.io.FileNotFoundException ioe)
				{
					
					ui.displayAlert("Connect: transcript not found: savetestnotfound");
					return false;
				}
				catch(java.rmi.RemoteException re2)
				{
					ui.displayAlert("Connect: Could not communicate with server.");
					
				}
				catch(InvalidKeyException ine)
		    {
			      ui3.invalidKey("Connect");
		  
		    }
		    catch(AccessControlException ace)
		    {
		 	      ui.displayAlert("Connect: Unprivileged key.");
		 	  }
			}
		
	 return false;
	}
	
	public boolean disconnect()
	{
		try{
			chat.disconnect(k,callbacks);
		}
		catch(java.rmi.RemoteException e)
		{
			ui.displayAlert("Disconnect: "+serverremote);
			return false;
		}
		catch(InvalidKeyException e)
		{
			  ui3.invalidKey("Disconnect");
		          return false;
		 }
		try{
			java.rmi.server.UnicastRemoteObject.unexportObject(callbacks,true);
		}
		catch(java.rmi.RemoteException e)
		{
			//ui.displayAlert("Disconnect: "+serverremote);
			return false;
		}
		return true;
		
	}
	public boolean sendMessage(String message)
	{
		try{
			chat.sendMessage(k,message);
			return true;
		}
		catch(java.rmi.RemoteException e)
		{
			 try{
			    java.rmi.server.UnicastRemoteObject.unexportObject(callbacks,true);
		   }
		   catch(java.rmi.RemoteException er)
		   {}
			ui.displayAlert("SendMessage: "+serverremote);
			return false;
			}
		catch(InvalidKeyException e)
		{
			  ui3.invalidKey("SendMessage");
		  
		}
		return false;
		}
		
	public void attach(Stage1UserInterface ui)
	{
	  this.ui=ui;
	}	
	
	public void receiveMessage(String msg) throws java.rmi.RemoteException
	{
		
		  ui.displayMessage(msg);
	}
	
	public void ping() throws java.rmi.RemoteException
	{
		ui3.pinged();
	}
	
	public void updatePrivilege(boolean isNowPrivileged) throws java.rmi.RemoteException
	{
		updatePrivilege(isNowPrivileged);
	}
	
	public void attach(Stage2UserInterface ui)
	{
		ui2=ui;
	}
	
	public String[] getTranscriptList()
	{
		
		try{
		     String[] temp2 = chat.getTranscriptList(k);
		     return temp2;
		}
		catch(java.rmi.RemoteException e)
		{
			ui.displayAlert("GetTranscriptList: Could not communicate with server.");
			//return null;
		}
		catch(InvalidKeyException e)
		{
			  ui.displayAlert("GetTranscriptList: Invalid Key.");
		  
		 }
		 catch(AccessControlException e)
		 {
		 	  ui.displayAlert("GetTranscriptList: Unprivileged key.");
		 	}
		//String[] is={"is","js"};
		return null;
	}
	
	public void selectTranscript(String transcriptName)
	{
		scriptname=transcriptName;
	}
	
  public boolean shutdownAndSave(String transcriptName)
  {
  	try{
  		chat.saveTranscript(k,transcriptName);
  		chat.shutdown(k);
  	}
  	catch(java.rmi.RemoteException e)
  	{
  		ui.displayAlert("SaveTranscript: Could not communicate with server.");
  		return false;
  	}
  	catch(InvalidKeyException e)
		{
			  ui3.invalidKey("SaveTranscript");
		  
		 }
		 catch(AccessControlException e)
		 {
		 	  ui.displayAlert("SaveTranscript: Unprivileged key.");
		 	}
		 return true;
		 
  }
  
  public boolean shutdownAndAbort()
  {
  	try{
  		chat.shutdown(k);
  	}
  	catch(java.rmi.RemoteException e)
  	{
  		ui.displayAlert(e.getMessage());
  		return false;
  	}
  	catch(InvalidKeyException e)
		{
			  ui3.invalidKey("Shutdown");
		  
		 }
		 catch(AccessControlException e)
		 {
		 	  ui.displayAlert("Shutdown: Unprivileged key.");
		 }
		return true;
		
  }
}

