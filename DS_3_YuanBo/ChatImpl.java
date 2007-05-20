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

    public ChatImpl() throws java.rmi.RemoteException
    {
    	super();
    }
    
     public ChatImpl(String boundURL, Naming naming,String name) throws java.rmi.RemoteException
    {
      super();
      this.boundURL=boundURL;
      server_name=name;
      this.naming=naming;
      
      if(!data_folder.exists())
      {
      	data_folder.mkdir();
      }
      if(!account.exists())
      {
      	try{
      	  account.createNewFile();
      	}
      	catch(IOException e)
      	{}
      }else{
        try{
        BufferedReader br=new BufferedReader(new FileReader(account));
        String username=new String();
        String password=new String();
        String pri     =new String();
        
        while((username=br.readLine())!=null)
        {
        	password=br.readLine();
        	pri     =br.readLine();
        	accounttable.put(username,password);
        	if(pri.equals(stringtrue))
        	{
        	 privilegetable.put(username,pri);
        	}
        }
        br.close();
        }
        catch(Exception e)
        {}
      }
        	
    }
    
    public ChatKey login(String username,String password) throws RemoteException
    {
    	String pw=new String();
    	ChatKey k=new ChatKeyImpl(this);
    	
    	if(accounttable.containsKey(username))
    	{
    		pw=(String) accounttable.get(username);
    		if(pw.equals(password))
    		{
    			acckeytable.add(k);
			acckKeyVector.add(username);
    			return k;
    		}else{
    			k=null;
    		}
    	}else{
	 String admin="admin";
	 String adpassword="initadmin";
	 if(username.equals(admin)&&adpassword.equals(password))
	 {
	   accounttable.put(username,password);
	   acckeytable.add(k);
	   acckKeyVector.add(username);
	   privilegetable.put(username,stringtrue);
	   return k;
	  }
	 }
	k=null;
    	return k;
    }
    
    public boolean isPrivileged(ChatKey key) throws RemoteException
    {
    	String username=new String();
    	if(acckeytable.contains(key))
    	{
    		int index= acckeytable.indexOf(key);
		
		username=(String) acckKeyVector.elementAt(index);
    		if(privilegetable.containsKey(username))//????? if necessary?
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean createAccount(ChatKey creatorKey,String username,String password,boolean isPrivileged)
		                 throws RemoteException,InvalidKeyException,AccessControlException
    {
    	boolean test=false;
    	
    	if(acckeytable.contains(creatorKey))
    	{
    		if(creatorKey.amPrivileged())
    		{
    			if(!accounttable.containsKey(username))
    			{
    				accounttable.put(username,password);
    				//System.out.println("create a new count "+username+",  "+password);
				if(isPrivileged)
    				{
    					privilegetable.put(username,stringtrue);
    				}
    				save();
    			  test=true;
    			}else{
    				test=false;
				//System.out.println("didn't create a new count.");
				
    			}
    		}else{
    			AccessControlException e=new AccessControlException("access");
    			throw e;
    		}
    	}else{
    		InvalidKeyException ie=new InvalidKeyException();
    		throw ie;
    	}
    		
    	return test;
    }
    
    public void setPrivilege(ChatKey adminKey,String username,boolean isPrivileged)
		                     throws RemoteException,InvalidKeyException,AccessControlException
		{
			if(acckeytable.contains(adminKey))
			{
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
									save();
								}
							}else{
								if(privilegetable.containsKey(username))
								{
									privilegetable.remove(username);
									save();
								}
							}
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
    }
    
    public void setPassword(ChatKey key,String username,String newPassword) 
		      throws RemoteException,InvalidKeyException,AccessControlException
		{
			String user=new String();
			
			if(acckeytable.contains(key))
			{
				int index= acckeytable.indexOf(key);
				user = (String) acckKeyVector.elementAt(index);
				
				if(user.equals(username))
				{
					accounttable.remove(username);
					accounttable.put(username,newPassword);
					save();
				}else{
					if(key.amPrivileged())
					{
						if(accounttable.containsKey(username))
						{
						   accounttable.remove(username);
					     accounttable.put(username,newPassword);
					     save();
					    }
					}else{
						AccessControlException e=new AccessControlException("access");
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
			if(acckeytable.contains(key))
			{
				int index = acckeytable.indexOf(key);
				
				acckKeyVector.removeElementAt(index);
				acckeytable.remove(key);
				
			}else{
		    InvalidKeyException e=new InvalidKeyException();
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
    	 boolean isSession=false;
    	
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
    	     all_messages.clear();//?????
    	     //transcript_list.clear();
    	     isSession=true;
    	  }else{
    	  	clients.add(cl);
    	  	isSession=false;
    	    String msg=new String();
    	    for(int i=0;i<all_messages.size();i++)
    	    {
    	    	msg=(String) all_messages.elementAt(i);
    	    	//????exception
    	    	cl.receiveMessage(msg);
    	    }
    	  }
    	  
    	}else{
    		InvalidKeyException e=new InvalidKeyException();
        throw e;    	
    	}
    	
    	return isSession;
    }
    
    public boolean connect(ChatKey key,ClientCallbacks cl,String transcriptName) 
		throws RemoteException,FileNotFoundException,InvalidKeyException,AccessControlException
    {
    	boolean isSession=false;
    	 
    	 if(acckeytable.contains(key))
    	 {
    	 if(key.amPrivileged())
    	 {
    	 
    	 File fi=new File(data_folder,transcriptName+postfix);
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
    	     transcript_list.clear();
    	     if(fi.exists())
    	     {
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
    	      isSession=true;
    	      }else{
    	      	     FileNotFoundException fe=new FileNotFoundException(transcriptName);
    	      	     throw fe;
    	          }
    	       
    	      
    	  }else{
    	  	clients.add(cl);
    	  	isSession=false;
    	    String msg=new String();
    	    for(int i=0;i<all_messages.size();i++)
    	    {
    	    	msg=(String) all_messages.elementAt(i);
    	    	//????exception
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
    	
    return isSession;
    }
    
   public void saveTranscript(ChatKey key,String transcriptName) 
		throws RemoteException,InvalidKeyException,AccessControlException
    {
    	
    	if(acckeytable.contains(key))
    	{
    	if(key.amPrivileged())
    	{
    		 
      File fi=new File(data_folder,transcriptName+postfix);
    	
    	String s=new String();
    	if(fi.exists())
    	{
    		BufferedReader br=null;
    		try{
    		  br=new BufferedReader(new FileReader(fi));
    	  }
    	  catch(FileNotFoundException e)
    	  {}
    	  
    		Vector savebuffer=new Vector();
    		try{
    		while((s=br.readLine())!=null)
    		{
    			savebuffer.add(s);
    		}
    		br.close();
    		fi.delete();
    		fi.createNewFile();
    		PrintWriter pw=new PrintWriter(new FileWriter(fi));
    		for(int i=0;i<savebuffer.size();i++)
    		{ pw.println((String) savebuffer.elementAt(i));
    		}
    		for(int i=0;i<all_messages.size();i++)
    		{
    			pw.println((String) all_messages.elementAt(i));
    		}
    		pw.close();
    	}
    	catch(IOException e)
    	{}
    	}else{
    		try{
    		fi.createNewFile();
    	 }
    	 catch(IOException e)
    	 {}
    	 	
    		PrintWriter pw=null;
    		try{
    		pw=new PrintWriter(new FileWriter(fi));
    	 }
    	 catch(IOException e)
    	 {}
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
    
    public String[] getTranscriptList(ChatKey key) 
		throws RemoteException,InvalidKeyException,AccessControlException
    {
    	//??????
    	if(acckeytable.contains(key))
    	{
    	if(key.amPrivileged())
    	{
    	String[] ls=data_folder.list();
    	String nul="";
    	for(int i=0;i<ls.length;i++)
    	{
    		ls[i]=ls[i].replaceAll(postfix,nul); 
    	}
      return ls;
      
      }else{
      	AccessControlException e=new AccessControlException("access");
      	throw e;
      }
      }else{
      	InvalidKeyException ie=new InvalidKeyException();
      	throw ie;
      }
      	
    	//return null;
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
    catch(Exception e)
    {}
    	
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
    
    private void save()
    {
    	String userName=new String();
    	String passWord=new String();
    	String isp     =new String();
    	
    	try{
    	  if(account.exists())
    	  {
    		   account.delete();
		   account.createNewFile();
    		   Enumeration users = accounttable.keys();
    		   Enumeration pw = accounttable.elements();
    		   PrintWriter spw = new PrintWriter(new FileWriter(account));
           
           while(users.hasMoreElements()) {
                    userName = (String)users.nextElement();
                    passWord = (String)pw.nextElement();
                    
                    if(privilegetable.containsKey(userName))

                        isp  = stringtrue;
                    else
                        isp  = stringfalse;

                    spw.println(userName);
                    spw.println(passWord);
                    spw.println(isp);
                }
            spw.close();
            }
          }
          catch(IOException e)
          {}
    
    }
    
}
