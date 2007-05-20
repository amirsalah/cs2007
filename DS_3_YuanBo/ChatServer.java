
public class ChatServer
{
	public static void main(String[] args) 
	                      throws java.rmi.RemoteException
	{
		
		String servername="wenjie's space";
		Naming name=new RegistryNamingImpl();
		
		Chat c=new ChatImpl(args[0],name,servername);
		try{
			java.rmi.Naming.rebind(args[0],c);
		}
		catch(Exception e)
		{
			//System.out.println(e.getMessage());
		}
		
	}
} 