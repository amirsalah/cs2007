/*
 *Author: Fenglin Liu
 *ID:a1136672
 *DATE:07/09/2006
 *Describe:
 * ChatKey class, implements ChatKey interface.
 */

import java.lang.Object;
import java.lang.Math;
import java.util.Random;
import java.rmi.RemoteException;


public class ChatKeyImpl implements ChatKey
{
	private int capabilityNumber;
	private Chat serverReference;
	
	//Constructs new key using hashCode, got random capabilitynumber.
	//reference the key created in server.
	
	public ChatKeyImpl (Chat serverRef) 
	{
		this.capabilityNumber = super.hashCode();
		serverReference= serverRef;
	}
	
	// returns true if the parameter ChatKey is the same as the current.		
	
	
	
	public boolean equals(Object obj) 
	{
		boolean e=false;
		try
		{
			if(this.capabilityNumber==((ChatKeyImpl)obj).capabilityNumber)
				e= true;
			else
				e= false;
		}
		catch(Exception ex)
		{
			System.out.println("equals error"+ex.toString());
		}
		return e;
		
	}
	


	// returns true if the current ChatKey is a privileged Key
	public boolean amPrivileged()
	{
	    boolean k=true;
	try{
		
	     k=serverReference.isPrivileged(this);
	}catch(Exception e)
	{
	}
	return k;
	}
	//  return a int number
	public int hashCode()
	{
		return capabilityNumber;
	}

}