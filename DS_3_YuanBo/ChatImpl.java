import java.util.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.AccessControlException;
import java.rmi.RemoteException;

public class ChatImpl extends java.rmi.server.UnicastRemoteObject
                              implements Chat 
{
	private static final long serialVersionUID = 1L;
    private Vector transcript_list = new Vector();
    private String postfix=".txt";
    private Vector acckKeyVector=new Vector();
    private Hashtable<String, String> privilegetable=new Hashtable<String, String>();
    private String stringtrue="true";
    private String stringfalse="false";
    private Naming naming;
    private String boundURL;
    private File data_folder=new File("script");
	private String server_name="yuanbo's Place";
    private Vector clients = new Vector();
    private Vector<String> all_messages = new Vector<String>();
    private File account=new File("account.txt");
    private Hashtable<String, String> accounttable=new Hashtable<String, String>();
    private Vector acckeytable=new Vector();

    public ChatImpl(String boundURL, Naming naming,String name) throws java.rmi.RemoteException
    {
      super();

      this.naming=naming;
      this.boundURL=boundURL;
      server_name=name;
      
      if(!data_folder.exists())
      {
      	data_folder.mkdir();
      }
      
      if(!account.exists())
      {
      	try{
      	  account.createNewFile();
      	}
      	catch(IOException e){}
      }else{
        try{
        BufferedReader br=new BufferedReader(new FileReader(account));
        String password="";
        String pri="";
        String username="";
        
        while((username=br.readLine())!=null)
        {
        	password=br.readLine();
        	pri=br.readLine();
        	accounttable.put(username,password);
        	if(pri.equals(stringtrue))
        	{
        	 privilegetable.put(username,pri);
        	}
        }
        br.close();
        }
        catch(Exception e){}
      }
    }
 
    
    public ChatImpl() throws java.rmi.RemoteException
    {
    	super();
    }
    
    
     public boolean createAccount(ChatKey creatorKey,String username,String password,boolean isPrivileged)
     throws RemoteException,InvalidKeyException,AccessControlException
     {
    	 boolean done=false;

if( !acckeytable.contains(creatorKey) )
{
InvalidKeyException ie=new InvalidKeyException();
throw ie;
}
if(!creatorKey.amPrivileged())
{
AccessControlException e=new AccessControlException("e");
throw e;
}
if(!accounttable.containsKey(username))
{
accounttable.put(username,password);
if(isPrivileged){
privilegetable.put(username,stringtrue);
}
savedata();
done=true;
}else{
done=false;
}
return done;
}
    
    public ChatKey login(String username,String password) throws RemoteException
    {
    	ChatKey key=new ChatKeyImpl(this);
    	String pwd=new String();
    	
    	if(accounttable.containsKey(username))
    	{
    		pwd=accounttable.get(username);
    		if(pwd.equals(password))
    		{
    			acckeytable.add(key);
    			acckKeyVector.add(username);
    			return key;
    		}else{
    			key=null;
    		}
    	}else{
    		String admin="admin";
    		String adpassword="initadmin";
    		if(username.equals(admin)&&adpassword.equals(password))
    		{
    			accounttable.put(username,password);
    			acckeytable.add(key);
    			acckKeyVector.add(username);
    			privilegetable.put(username,stringtrue);
    			return key;
    		}
    	}
    	key=null;
    	return key;
    }
    
    public boolean isPrivileged(ChatKey key) throws RemoteException
    {
    	String um=new String();
    	if(acckeytable.contains(key))
    	{
    		int index= acckeytable.indexOf(key);
    		um=(String) acckKeyVector.elementAt(index);
    		if(privilegetable.containsKey(um))
    		{
    			return true;
    		}
    	}
    	return false;
    }
    

    
    public void setPrivilege(ChatKey adminKey,String username,boolean isPrivileged)
		                     throws RemoteException,InvalidKeyException,AccessControlException
		{
			if(!acckeytable.contains(adminKey))
			{
	    		InvalidKeyException ie=new InvalidKeyException();
	    		throw ie;
			}
			
				if(adminKey.amPrivileged())
				{
					if(accounttable.containsKey(username))
					{
						String admin="admin";
						if(!username.equals(admin))
						{
							if(isPrivileged)
							{
								if(!privilegetable.containsKey(username))
								{
									privilegetable.put(username,stringtrue);
									savedata();
								}
							}else{
								if(privilegetable.containsKey(username))
								{
									privilegetable.remove(username);
									savedata();
								}
							}
					  }
					}
				}else{
					AccessControlException e=new AccessControlException("access");
    			throw e;
    		}
    }
    
    public String serverName() throws RemoteException
    {
    	return server_name;
    }
    
    public boolean connect(ChatKey key,ClientCallbacks cl) 
		        throws RemoteException,InvalidKeyException
    {
    	 boolean tmpBoolean=false;
    	
    	if(acckeytable.contains(key))
    	{
	
    		boolean clienttest=true;
    		if(!clients.isEmpty())
    		{
    			for(int i=0;i<clients.size();i++)
    			{
    				if(clients.elementAt(i)!=null)
    					clienttest=false;
    		}
    	}
    	
    	if(clienttest)
    	 {
    	     clients.add(cl);
    	     all_messages.clear();
    	     tmpBoolean=true;
    	  }else{
    	  	clients.add(cl);
    	  	tmpBoolean=false;
    	    String msg=new String();
    	    for(int i=0;i<all_messages.size();i++)
    	    {
    	    	msg=(String) all_messages.elementAt(i);
    	    	cl.receiveMessage(msg);
    	    }
    	  }
    	  
    	}else{
    		InvalidKeyException e=new InvalidKeyException();
        throw e;    	
    	}
    	
    	return tmpBoolean;
    }
    
    public void setPassword(ChatKey key,String username,String newPassword) 
		      throws RemoteException,InvalidKeyException,AccessControlException
		{
			String ur="";
			
			if(acckeytable.contains(key))
			{
				int tmpindex= acckeytable.indexOf(key);
				ur = (String) acckKeyVector.elementAt(tmpindex);
				
				if(ur.equals(username))
				{
					accounttable.remove(username);
					accounttable.put(username,newPassword);
					savedata();
				}else{
					if(key.amPrivileged())
					{
						if(accounttable.containsKey(username))
						{
						   accounttable.remove(username);
						   accounttable.put(username,newPassword);
						   savedata();
					    }
					}else{
						AccessControlException e=new AccessControlException("ae");
						throw e;
					}
				}
			}else{
				InvalidKeyException ie=new InvalidKeyException();
				throw ie;
			}
		}
						
		public void logout(ChatKey key)  
		       throws RemoteException,InvalidKeyException
		{
			if(!acckeytable.contains(key))
			{
			    InvalidKeyException e=new InvalidKeyException();
			    throw e;
			}else{
				int tmpindex = acckeytable.indexOf(key);
				
				acckKeyVector.removeElementAt(tmpindex);
				acckeytable.remove(key);
		  }
		}
		

    
    public boolean connect(ChatKey key,ClientCallbacks cl,String transcriptName) 
		throws RemoteException,FileNotFoundException,InvalidKeyException,AccessControlException
    {
    	boolean tmpBoolean=false;
    	 
    	 if(acckeytable.contains(key))
    	 {
    	 if(key.amPrivileged())
    	 {
    	 
    	 File fi=new File(data_folder, transcriptName + postfix);
    	 boolean clienttest=true;
    	 if(!clients.isEmpty())
    	 {
    		 for(int i=0;i<clients.size();i++)
    		 {
    			 if(clients.elementAt(i)!=null)
    			 clienttest=false;
             }
    	 }
	 
    	 if(clienttest)         
    	 {
    	     all_messages.clear();
    	     transcript_list.clear();
    	     clients.add(cl);

    	     if(!fi.exists())
    	     {
	      	     FileNotFoundException fe=new FileNotFoundException(transcriptName);
	      	     throw fe;
    	     	 
    	      }else{
    	    	 BufferedReader br=new BufferedReader(new FileReader(fi));
     	         String s=new String();
     	         try{
     	        	 while((s=br.readLine())!=null)
     	        	 {
     	        		 for(int j=0;j<clients.size();j++)
     	        		 {
     	        			 ((ClientCallbacks) (clients.elementAt(j))).receiveMessage(s);
     	        			 all_messages.add(s);
     	        		 }
     	        	 }
     	        	 br.close();
     	         }catch(IOException e){}
     	         	tmpBoolean=true;
    	          }
    	  }else{
    	  	clients.add(cl);
    	  	
    	    String msg="";
    	    tmpBoolean=false;
    	    
    	    for(int i=0;i<all_messages.size();i++)
    	    {
    	    	msg=(String) all_messages.elementAt(i);
    	    	cl.receiveMessage(msg);
    	    }
    	     }
    	}else{
    		AccessControlException e=new AccessControlException("access");
    		throw e;
    	}
    }else{
    	  InvalidKeyException ie=new InvalidKeyException();
    	  throw ie;
    	}
    	
    return tmpBoolean;
    }
    
    public String[] getTranscriptList(ChatKey key) 
	throws RemoteException,InvalidKeyException,AccessControlException
{
	if(acckeytable.contains(key))
	{
	if(key.amPrivileged())
	{
	String[] strlist=data_folder.list();
	String nul="";
	for(int i=0;i<strlist.length;i++)
	{
		strlist[i]=strlist[i].replaceAll(postfix,nul); 
	}
  return strlist;
  
  }else{
  	AccessControlException e=new AccessControlException("ae");
  	throw e;
  }
  }else{
  	InvalidKeyException ie=new InvalidKeyException();
  	throw ie;
  }
}
    
   public void saveTranscript(ChatKey key,String transcriptName) 
		throws RemoteException,InvalidKeyException,AccessControlException
    {
    	if(acckeytable.contains(key))
    	{
    	if(key.amPrivileged())
    	{
    		File file=new File(data_folder,transcriptName+postfix);
    	
    		String str=new String();
    		if(file.exists())
    		{
    			BufferedReader br=null;
    			try{
    				br=new BufferedReader(new FileReader(file));
    	  }
    	  catch(FileNotFoundException e){}
    	  
    		Vector<String> savebuffer=new Vector<String>();
    		try{
    			while((str=br.readLine())!=null)
    			{
    				savebuffer.add(str);
    			}
    		br.close();
    		file.delete();
    		file.createNewFile();
    		PrintWriter pw=new PrintWriter(new FileWriter(file));
    		for(int i=0;i<savebuffer.size();i++)
    		{
    			pw.println((String) savebuffer.elementAt(i));
    		}
    		for(int i=0;i<all_messages.size();i++)
    		{
    			pw.println((String) all_messages.elementAt(i));
    		}
    		
    		pw.close();
    	}
    	catch(IOException e){}
    	}else{
    		try{
    		file.createNewFile();
    	 }
    	 catch(IOException e) {}
    		PrintWriter pw=null;
    		try{
    			pw=new PrintWriter(new FileWriter(file));
    	 }
    	 catch(IOException e) {}
    	for(int i=0;i<all_messages.size();i++)
    	{
    		pw.println((String) all_messages.elementAt(i));
    	}
    		pw.close();
    	}
    	
    }else{
    	AccessControlException e=new AccessControlException("access");
    	throw e;
    }
    }else{
    	InvalidKeyException ie=new InvalidKeyException();
    	throw ie;
    } 

    }
    
    
    public void disconnect(ChatKey key,ClientCallbacks cl) 
		throws RemoteException,InvalidKeyException
    {
    	if(acckeytable.contains(key))
    	{
    	  clients.remove(cl);
    	}else{
    		InvalidKeyException ie=new InvalidKeyException();
    		throw ie;
    	}
    }
    
    public void shutdown(ChatKey key) 
	throws RemoteException,InvalidKeyException,AccessControlException
{
	if(acckeytable.contains(key))
	{
	if(key.amPrivileged())
	{
	try{
	naming.unbind(boundURL);
	 }
catch(Exception e){}
	java.rmi.server.UnicastRemoteObject.unexportObject(this,true);
  all_messages.clear();
  System.exit(1);
 }else{
 	AccessControlException e=new AccessControlException("access");
 	throw e;
}
}else{
	InvalidKeyException ie=new InvalidKeyException();
  throw ie;
}

}
    
    private void savedata()
    {
    	String pwd="";
    	String privileged="";
    	String u_name="";

    	try{
    	  if(account.exists())
    	  {
    		   account.delete();
		       account.createNewFile();
    		   Enumeration users = accounttable.keys();
    		   Enumeration pw = accounttable.elements();
    		   PrintWriter writer = new PrintWriter(new FileWriter(account));
           
           while(users.hasMoreElements()) {
                    u_name = (String)users.nextElement();
                    pwd = (String)pw.nextElement();
                    
                    if(!privilegetable.containsKey(u_name))
                    	privileged  = stringfalse;
                    else
                    	privileged  = stringtrue;

                    writer.println(u_name);
                    writer.println(pwd);
                    writer.println(privileged);
                }
           
           	writer.close();
            }
          }
          catch(IOException e){}
    
    }
    
    public void sendMessage(ChatKey key,String msg) 
	throws RemoteException,InvalidKeyException
{
	if(acckeytable.contains(key))
	{
	all_messages.add(msg);
	
for(int i=0;i<clients.size();i++)
	{
	if(clients.elementAt(i)!=null){
	try{
	 ((ClientCallbacks) (clients.elementAt(i))).receiveMessage(msg);
		
		}
	catch(java.rmi.RemoteException e)
	{
	 	
		clients.setElementAt(null,i);
	}
	}
	}

}else{
	InvalidKeyException ie=new InvalidKeyException();
	throw ie;
}
}

}
