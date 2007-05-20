
/*
  Author: Fenglin Liu
  ID:a1136672
  DATE:07/9/2006
*/
//implement the driver program for your server 


import java.rmi.Naming;
import java.rmi.server.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class ChatServer 

{
	static String url;
    static Naming naming;
    static String S_name;
	
	
	public static void main(String args[]) throws java.rmi.RemoteException
	{
		url=args[0];
		RegistryNamingImpl  naming=new RegistryNamingImpl();
		S_name="Fenglin' place";
	    
		Chat c=new ChatImpl(url,naming,S_name);
		
		try{
			
			Naming.rebind(args[0],c);
			}
			catch (Exception e)
			
			{
				System.out.println("BAD URL");
				//System.exit(0);
			}
		
	}
	
}
