import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.AccessControlException;

public class ChatImpl extends java.rmi.server.UnicastRemoteObject implements
		Chat

{
private static final long serialVersionUID = 1L;
private Vector ClientV= new Vector(); 		
private Vector ClientKey= new Vector(); 		
private Vector<String> ClientUsername= new Vector<String>(); 	
private Vector<String> ClientPassword= new Vector<String>(); 	
private Vector ClientP= new Vector(); 			

	public String server_name;
	public Vector<ClientCallbacks> clients;
	Naming interf_name;
	String name_URL;
	ArrayList list = new ArrayList();
    
	public ChatImpl(String boundURL, Naming naming, String name)
			throws java.rmi.RemoteException {
		super();
		clients = new Vector();
		name_URL = boundURL;
		interf_name = naming;
		server_name = name;
		
		File f= new File("userdata.dat");
        try
        {
        	if(f.exists()==false)	
        	{        		
        		BufferedWriter out = new BufferedWriter(new FileWriter("userdata.dat"));
 				out.write("Admin"+"\t");	
 				out.write("1234"+"\t");		
 				out.write("true"+"\n");		
 				out.flush();
 				out.close();
 			}
 			ReadAccount();		
 		}
 		
 		catch(Exception ex)
 		{
 		}
		

	}
	
	public void ReadAccount()
    {
    	String accountLine = null;
        String left = null;
        int location = 0;
        try
        {
        	ClientUsername.clear();	
        	ClientPassword.clear();	
        	ClientP.clear();		
        	
        	BufferedReader in = new BufferedReader(new FileReader("userdata.dat"));
        	while((accountLine = in.readLine()) != null)
        	{
        		location = accountLine.indexOf("\t");		
        		ClientUsername.add(accountLine.substring(0, location));
        		
        		left = accountLine.substring(location+1);	
        		location = left.indexOf("\t");
        		
        		ClientPassword.add(left.substring(0,location));
        		ClientP.add(left.substring(location+1));	
        	}
        }
        catch(Exception e)
        {
        }
    }

	public String serverName() throws java.rmi.RemoteException {

		return server_name;
	}

public ChatKey login(String username,String password)
       throws java.rmi.RemoteException
{
	ChatKeyImpl mKey= null;
    	try
    	{
    	
    		int index = ClientUsername.indexOf(username);	

			if (index != -1)	
			{
				if (password.equals(ClientPassword.get(index)))
				{
					mKey = new ChatKeyImpl(this);	
					ClientKey.add(mKey);	
					ClientKey.add(username);
				}
			}
			else
			{
			}
    		
    	}
    	catch(Exception ex)
    	{
    	}
    	return mKey;
}

public boolean connect(ChatKey key, ClientCallbacks client)
	throws java.rmi.RemoteException,InvalidKeyException
{
try{
       
for(int j=0; j<ClientKey.size(); j=j+2)
 {
   if((ClientKey.get(j)).equals(key))    
  {
       
		if (clients.isEmpty()) 
		{

			clients.addElement(client);
			

		 } 
		else {
			clients.addElement(client);

			String[] str = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {

				str[i] = (String) list.get(i);// create a string array

				client.receiveMessage(str[i]);

			}
			return false;
		}
   }
    else
     {
     }
 }
 }catch(Exception e)
    {
     }
     return true;
}

	public boolean connect(ChatKey key,ClientCallbacks client, String transcriptName)
			throws java.rmi.RemoteException, java.io.FileNotFoundException,InvalidKeyException,
	AccessControlException{
		
		FileReader fr = new FileReader("./transfile/" + transcriptName);
		BufferedReader buffer = new BufferedReader(fr);
		String file_Con = "";
	 
try {
	    
	    if(key.amPrivileged())
	    {
	     if (clients.isEmpty()) 
	       {
	       	  clients.addElement(client);
			    file_Con = buffer.readLine();	
			     while (file_Con != null) 
			     {	
							
				list.add(file_Con);
				client.receiveMessage(file_Con);
				file_Con = buffer.readLine();				
			      }
		} else 
		  {
			clients.addElement(client);
			String[] str = new String[list.size()];
			for (int i = 0; i < list.size(); i++)
			{
				str[i] = (String) list.get(i);
				client.receiveMessage(str[i]);
			}
			return false;
		   }
	          } 
	          
	          else{
	          }
	
        }catch (IOException e) 
	{
	}
	 return true;
	}

	public void disconnect(ChatKey key, ClientCallbacks client)
			throws java.rmi.RemoteException, InvalidKeyException{
	try{
	
	for(int j=0; j<ClientKey.size(); j=j+2)	
    	{
    		
    		if((ClientKey.get(j)).equals(key))	
    		{				
				
		        clients.removeElement(client);
    		}
    		else
    		{
    		}
    	}
    } catch(Exception e)
       {
       }
    }

public void logout(ChatKey key)
        throws java.rmi.RemoteException,InvalidKeyException{
        	
        	
      int k;
      try{
      	
	    for(int j=0; j<ClientKey.size(); j=j+2)	
    	{
    		
    		if((ClientKey.get(j)).equals(key))	
    		{				
	            ClientKey.removeElementAt(j);
	            k=j+1;
        		ClientKey.removeElementAt(k);
    		}
    		else
    		{
    		}
    	}	
        	
        }catch(Exception e)
        {
        }	
}

	public void sendMessage(ChatKey key, String msg) throws java.rmi.RemoteException,
	InvalidKeyException {

		
		
	for(int j=0; j<ClientKey.size(); j=j+2)		
    	{
    		if((ClientKey.get(j)).equals(key))
    		{
    			try
    			{
    	           int i = 0;
		           while (i < clients.size()) 
		           {
			       ClientCallbacks client = (ClientCallbacks) clients.elementAt(i);
			       try {
				       client.receiveMessage(msg);
				        i++;
			           } catch (RemoteException e) 
			           {
			            }
		            }
		               list.add(msg);
                 }catch(Exception e)
                 {
                 }
	        }
	        else{
	        }
	     }
	     
    }

	public void saveTranscript(ChatKey key,String transcriptName)
			throws java.rmi.RemoteException,InvalidKeyException,
	AccessControlException  {
	
		try {
			
			if(key.amPrivileged()){
			
			File dir = new File("./transfile");

			if (!dir.exists()) {
				dir.mkdir();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter("./transfile/" + transcriptName)));
			for (int i = 0; i < list.size(); i++) {
				out.println(list.get(i));
			}
			out.close();
			list.clear();
			}
			
			else{
	        }
			
			

		} catch (Exception e) 
		{
			System.out.println(e);
		}
	 }

	public void shutdown(ChatKey key) throws java.rmi.RemoteException,InvalidKeyException,
	AccessControlException{
			
			if(key.amPrivileged())
		     {
		     
		       UnicastRemoteObject.unexportObject(this, true);
		        try {
			            interf_name.unbind(name_URL);
		            } catch (NotBoundException e) {
		            } catch (java.net.MalformedURLException e) {
		            }

		        System.exit(1);
		     }
		    else{
	        }
	}

	public String[] getTranscriptList(ChatKey key) throws java.rmi.RemoteException,InvalidKeyException,
	AccessControlException 
 {
    String[] str=null;
	
	try	{
      
	if(key.amPrivileged())		
    	{
    		File f=new File("./transfile");	
	    	
	    	if(f.exists())
	         str= f.list();
		 }              
	}catch(Exception e){
	}	 
	     	return str;
  }
  
  
  
  public boolean isPrivileged (ChatKey key)
    throws java.rmi.RemoteException
    {
		boolean isP=false;
    	
    	try
    	{
    		for(int i=0; i<ClientKey.size(); i=i+2)	
    		{
    			if((ClientKey.get(i)).equals(key))	
    			{
    				String str=(String)ClientKey.get(i+1);	
    				
    				for(int j=0; j<ClientUsername.size(); j++)
    				
    				{	
    					if (((String)ClientUsername.get(j)).equalsIgnoreCase(str) && ClientP.get(j).equals("true") )

    						isP=true;
    				}
    			}
    		}
    	}
    	catch(Exception ex)
    	{
    	}
    	return isP;
    	
    }
  
  public boolean createAccount (ChatKey creatorkey, String username, String password, boolean isprivileged)
    throws java.rmi.RemoteException,InvalidKeyException,AccessControlException
    {
    	boolean create_ok=false;
    	try
    	{
    		if(creatorkey!=null&&creatorkey.amPrivileged())
    		{
    			int index = ClientUsername.indexOf(username);	

				if (index == -1)	
    			{
    				ClientUsername.addElement(username);	
    				ClientPassword.addElement(password);
    				if (isprivileged)							
    					ClientP.addElement("true");
    				else
    					ClientP.addElement("false");
    				WriteAccount();		
    				create_ok= true;
    			}
    		}
    	}
    	catch(Exception ex)
    	{
    		return false;
    	}
    	return create_ok;
    }
  
  public void WriteAccount()
    {
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter("userdata.dat"));
			for(int i=0; i<ClientUsername.size();i++)	
			{
				out.write((String)ClientUsername.get(i) + "\t");	
				out.write((String)ClientPassword.get(i) + "\t");	
				out.write((String)ClientP.get(i) + "\n");			
			}
			out.flush();											
			out.close();
		}
		catch(Exception e)
		{
		}
    }
 public void setPrivilege (ChatKey key, String username, boolean isprivileged)
    throws java.rmi.RemoteException,InvalidKeyException,AccessControlException
    {
      try
    	{
    		if(key!=null&&key.amPrivileged())		
    		{
    			int index= ClientUsername.indexOf(username);	
    			
    			if(index!= -1&&isprivileged&&!username.equals("admin"))		
    			      ClientP.set(index,isprivileged);
    			else
    				  ClientP.set(index,"false");
    				
    				  WriteAccount();	
    		}
        }
    	catch(Exception ex)
    	{
    	}
    
    }
  
  public void setPassword (ChatKey key, String username, String newPassword)
      throws java.rmi.RemoteException,InvalidKeyException,AccessControlException
    {
    	try
    	{
    		boolean isUser=false;
    		for(int i=0; i<ClientKey.size(); i=i+2)
			{
				if((ClientKey.get(i)).equals(key))
				{
					isUser=ClientKey.get(i+1).equals(username);
				}
				else{
	             }    			
    		}
    		if(key.amPrivileged() || isUser)	
    		{
    			int index = ClientUsername.indexOf(username);
    			ClientPassword.set(index,newPassword);	
    			WriteAccount();	
    		}
    		else{
	             } 
    	}
    	catch(Exception ex)
    	{
    	}
    }
}
