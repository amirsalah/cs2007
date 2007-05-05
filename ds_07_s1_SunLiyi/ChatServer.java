
public class ChatServer 
{
	public static void main(String args[]) throws java.rmi.RemoteException
	{
		Chat chat=new ChatImpl();
		try
		{
			java.rmi.Naming.rebind(args[0],chat);
		}
		catch(java.net.MalformedURLException e)
		{
			System.out.println("Bad URL");
		}
		catch(java.rmi.RemoteException a)
		{
			System.out.println("remote exception");
		}
	}
	
}
