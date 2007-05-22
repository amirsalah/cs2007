
import java.util.*;
import java.security.InvalidKeyException;
import java.security.AccessControlException;
import java.rmi.RemoteException;


public class Client implements ClientCallbacks, Stage3Backend
{
	private Chat chat;
	private Naming name;

	private Stage1UserInterface ui;
	private Stage2UserInterface ui2;
	private Stage3UserInterface ui3;
	private String tps;
	private ChatKey chat_key = null;
	
	public Client(Naming name)
	{
	  super();
	  this.name=name;
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
			ui.displayAlert("Locate: Malformed URL: "+e.getMessage());
			return false;
		}
		catch(java.rmi.StubNotFoundException e)
		{
		   ui.displayAlert("Locate: "+"Stub is not of expected type.");
		   return false;
		}
		 catch(java.rmi.RemoteException e)
		 {
		  ui.displayAlert("Locate: "+"Could not communicate with registry.");
		 	return false;
		}
		catch(Exception e)
		{
		   ui.displayAlert("Locate: "+"Stub is not of expected type.");
		   }
		return false;
	}
	
	public boolean login(String username,String password)
	{
		try{
			chat_key=chat.login(username,password);
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
	
	public boolean isPrivileged()
	{
		boolean Privileged = false;
		try{
		  Privileged = chat_key.amPrivileged();
		}
		catch(Exception e){}
	  return Privileged;
	}
	
	public boolean isLoggedIn()
	{
		if(chat_key!=null)
		  return true;
		else
		  return false;
	}
	
	public boolean logout()
	{
		try{
			chat.logout(chat_key);
			chat_key=null;
			try{
			    java.rmi.server.UnicastRemoteObject.unexportObject(this,true);
		   }
		  catch(java.rmi.RemoteException er){}
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
		boolean Privileged = false;
		
		try{
		Privileged = chat.createAccount(chat_key,username,password,priv);
		}
		catch(RemoteException e)
		{
			ui.displayAlert("CreateAccount: Could not communicate with server.");
			return false;
		}
		catch(InvalidKeyException e)
		{
			  ui3.invalidKey("CreateAccount");
			  chat_key=null;
			  return false;
		 }
		 catch(AccessControlException e)
		 {
		 	  ui.displayAlert("CreateAccount: Unprivileged key.");
			  chat_key=null;
			  return false;
		 }
		 return Privileged;
	}
	
	public boolean setPrivilege(String username,boolean priv)
	{
		try{
			chat.setPrivilege(chat_key,username,priv);
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
			chat.setPassword(chat_key,username,password);
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
			java.rmi.server.UnicastRemoteObject.exportObject(this);
		}
		catch(java.rmi.RemoteException e)
		{
			ui.displayAlert(e.getMessage());
			return false;
		}
		
		
		if(tps==null)
		{
			 String server_Name="";
			 try{
				server_Name=chat.serverName();
					
			}
			catch(java.rmi.RemoteException re)
			{
					ui.displayAlert("ServerName: Could not communicate with server.");
					return false;
			}
			try{
				if(chat.connect(chat_key,this))
				{
					ui2.setTranscriptLabelText("Started new session at server "+"\""+server_Name+"\"");
				}else{
					ui2.setTranscriptLabelText("Connected to ongoing session at server "+"\""+server_Name+"\"");
				}
				return true;
			  }
			  catch(java.rmi.RemoteException re)
			  {
			  	ui.displayAlert("Connect: "+"Could not communicate with server.");
			  	return false;
			  }
			  catch(InvalidKeyException ine)
			  {
			  	ui3.invalidKey("Connect");
			  }
			}else{
				String server_Name=new String();
				try{
					server_Name=chat.serverName();
				}
				catch(java.rmi.RemoteException re)
				{
					ui.displayAlert("ServerName: Could not communicate with server.");
					return false;
				}
				try{
				  if(chat.connect(chat_key,this,tps))
			          {
					  ui2.setTranscriptLabelText("Resumed session using transcript \""+tps+"\" at server "+"\""+server_Name+"\"");
				  }else{
					  ui2.setTranscriptLabelText("Connected to ongoing session at server "+"\""+server_Name+"\"");
					  
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
			chat.disconnect(chat_key,this);
		}
		catch(java.rmi.RemoteException e)
		{
			ui.displayAlert("Disconnect: "+"Could not communicate with server.");
			return false;
		}
		catch(InvalidKeyException e)
		{
			  ui3.invalidKey("Disconnect");
		      return false;
		 }
		try{
			java.rmi.server.UnicastRemoteObject.unexportObject(this,true);
		}
		catch(java.rmi.RemoteException e)
		{
			return false;
		}
		return true;
		
	}
	
	public boolean sendMessage(String message)
	{
		try{
			chat.sendMessage(chat_key,message);
			return true;
		}
		catch(java.rmi.RemoteException e)
		{
			 try{
			    java.rmi.server.UnicastRemoteObject.unexportObject(this,true);
			 }
			 catch(java.rmi.RemoteException except){}
			    ui.displayAlert("SendMessage: "+"Could not communicate with server.");
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
		     String[] temp2 = chat.getTranscriptList(chat_key);
		     return temp2;
		}
		catch(java.rmi.RemoteException e)
		{
			ui.displayAlert("GetTranscriptList: Could not communicate with server.");
		}
		catch(InvalidKeyException e)
		{
			  ui.displayAlert("GetTranscriptList: Invalid Key.");
		  
		 }
		 catch(AccessControlException e)
		 {
		 	  ui.displayAlert("GetTranscriptList: Unprivileged key.");
		 	}
		 return null;
	}
	
	public void selectTranscript(String transcriptName)
	{
		tps=transcriptName;
	}
	
  public boolean shutdownAndSave(String transcriptName)
  {
  	try{
  		chat.saveTranscript(chat_key,transcriptName);
  		chat.shutdown(chat_key);
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
  		chat.shutdown(chat_key);
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

