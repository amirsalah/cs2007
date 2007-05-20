

import java.util.*;
import java.io.*;

import java.security.InvalidKeyException;
import java.security.AccessControlException;
import java.rmi.RemoteException;

public class ChatImpl extends java.rmi.server.UnicastRemoteObject
                              implements Chat 
{
    private String server_name="wenjie's Place";
    private Vector clients = new Vector();
    private Vector messbuffer = new Vector();
    private Vector scriptList = new Vector();
    private String profix=".txt";
    private Naming naming;
    private String boundURL;
    private File folder=new File("script");
    
    private File account=new File("account.txt");
    private Hashtable acc=new Hashtable();
    private Vector acck=new Vector();
    private Vector acckKey=new Vector();
    
    private Hashtable privilege=new Hashtable();
    private String istrue="true";
    private String isfalse="false";
    
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
      
      if(!folder.exists())
      {
      	folder.mkdir();
      }
      //read account
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
        //acc.clear();
        //acck.clear();
        //privilege.clear();
        
        
        while((username=br.readLine())!=null)
        {
        	password=br.readLine();
        	pri     =br.readLine();
        	acc.put(username,password);
        	if(pri.equals(istrue))
        	{
        	 privilege.put(username,pri);
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
    	
    	if(acc.containsKey(username))
    	{
    		pw=(String) acc.get(username);
    		if(pw.equals(password))
    		{
    			acck.add(k);
			acckKey.add(username);
    			return k;
    		}else{
    			k=null;
    		}
    	}else{
	 String admin="admin";
	 String adpassword="initadmin";
	 if(username.equals(admin)&&adpassword.equals(password))
	 {
	   acc.put(username,password);
	   acck.add(k);
	   acckKey.add(username);
	   privilege.put(username,istrue);
	   return k;
	  }
	 }
	k=null;
    	return k;
    }
    
    public boolean isPrivileged(ChatKey key) throws RemoteException
    {
    	String username=new String();
    	if(acck.contains(key))
    	{
    		int index= acck.indexOf(key);
		
		username=(String) acckKey.elementAt(index);
    		if(privilege.containsKey(username))//????? if necessary?
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
    	
    	if(acck.contains(creatorKey))
    	{
    		if(creatorKey.amPrivileged())
    		{
    			if(!acc.containsKey(username))
    			{
    				acc.put(username,password);
    				//System.out.println("create a new count "+username+",  "+password);
				if(isPrivileged)
    				{
    					privilege.put(username,istrue);
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
			if(acck.contains(adminKey))
			{
				if(adminKey.amPrivileged())
				{
					if(acc.containsKey(username))
					{
						String admin="admin";
						if(!username.equals(admin))
						{
							if(isPrivileged)
							{
								if(!privilege.containsKey(username))
								{
									privilege.put(username,istrue);
									save();
								}
							}else{
								if(privilege.containsKey(username))
								{
									privilege.remove(username);
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
			
			if(acck.contains(key))
			{
				int index= acck.indexOf(key);
				user = (String) acckKey.elementAt(index);
				
				if(user.equals(username))
				{
					acc.remove(username);
					acc.put(username,newPassword);
					save();
				}else{
					if(key.amPrivileged())
					{
						if(acc.containsKey(username))
						{
						   acc.remove(username);
					     acc.put(username,newPassword);
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
			if(acck.contains(key))
			{
				int index = acck.indexOf(key);
				
				acckKey.removeElementAt(index);
				acck.remove(key);
				
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
    	
    	if(acck.contains(key))
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
    	     messbuffer.clear();//?????
    	     //scriptList.clear();
    	     isSession=true;
    	  }else{
    	  	clients.add(cl);
    	  	isSession=false;
    	    String msg=new String();
    	    for(int i=0;i<messbuffer.size();i++)
    	    {
    	    	msg=(String) messbuffer.elementAt(i);
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
    	 
    	 if(acck.contains(key))
    	 {
    	 if(key.amPrivileged())
    	 {
    	 
    	 File fi=new File(folder,transcriptName+profix);
	 boolean clienttest=true;
	 if(!clients.isEmpty())
	 {
	    for(int i=0;i<clients.size();i++)
	    {
	       if(clients.elementAt(i)!=null)
	            clienttest=false;
             }
	 }
	 
    	 if(clienttest)         //if(clients.isEmpty())
    	 {
    	     clients.add(cl);
    	     messbuffer.clear();
    	     scriptList.clear();
    	     if(fi.exists())
    	     {
    	     	 BufferedReader br=new BufferedReader(new FileReader(fi));
    	       
    	       String s=new String();
    	       //Vector savebuffer=new Vector();
	       try{
    	       while((s=br.readLine())!=null)
    	       {
    	       	     for(int j=0;j<clients.size();j++)
	             {
			   ((ClientCallbacks) (clients.elementAt(j))).receiveMessage(s);
    	                   messbuffer.add(s);
		     }
		 }
    	        br.close();//???
		
    	        }catch(IOException e)
    	         {}
		
    	      isSession=true;//???
    	      }else{
    	      	     FileNotFoundException fe=new FileNotFoundException(transcriptName);
    	      	     throw fe;
    	          }
    	       
    	      
    	  }else{
    	  	clients.add(cl);
    	  	isSession=false;
    	    String msg=new String();
    	    for(int i=0;i<messbuffer.size();i++)
    	    {
    	    	msg=(String) messbuffer.elementAt(i);
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
    	
    	if(acck.contains(key))
    	{
    	if(key.amPrivileged())
    	{
    		 
      File fi=new File(folder,transcriptName+profix);
    	
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
    		for(int i=0;i<messbuffer.size();i++)
    		{
    			pw.println((String) messbuffer.elementAt(i));
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
    		for(int i=0;i<messbuffer.size();i++)
    		{
    			pw.println((String) messbuffer.elementAt(i));
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
    	if(acck.contains(key))
    	{
    	if(key.amPrivileged())
    	{
    	String[] ls=folder.list();
    	String nul="";
    	for(int i=0;i<ls.length;i++)
    	{
    		ls[i]=ls[i].replaceAll(profix,nul); 
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
    	if(acck.contains(key))
    	{
    	if(key.amPrivileged())
    	{
    		
    	try{
    	naming.unbind(boundURL);
    	 }
    catch(Exception e)
    {}
    	
    	java.rmi.server.UnicastRemoteObject.unexportObject(this,true);
      messbuffer.clear();
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
    	if(acck.contains(key))
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
    	if(acck.contains(key))
    	{
    	messbuffer.add(msg);
    	
	for(int i=0;i<clients.size();i++)
    	{
		if(clients.elementAt(i)!=null){
		try{
    	 ((ClientCallbacks) (clients.elementAt(i))).receiveMessage(msg);
    		
    		}
		catch(java.rmi.RemoteException e)
		{
		 	
			clients.setElementAt(null,i);
			//acck.remove(key);		
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
    		   Enumeration users = acc.keys();
    		   Enumeration pw = acc.elements();
    		   PrintWriter spw = new PrintWriter(new FileWriter(account));
           
           while(users.hasMoreElements()) {
                    userName = (String)users.nextElement();
                    passWord = (String)pw.nextElement();
                    
                    if(privilege.containsKey(userName))

                        isp  = istrue;
                    else
                        isp  = isfalse;

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
