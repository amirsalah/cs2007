
public class ChatServer
{
	public static void main(String[] args) 
	                      throws java.rmi.RemoteException
	{
		Naming nm = new RegistryNamingImpl();
		String server_Name="Bo Yuan's space";
		Chat chat=new ChatImpl(args[0],nm,server_Name);
		
		try{
			java.rmi.Naming.rebind(args[0],chat);
		}
		catch(Exception e){}
	}
} 