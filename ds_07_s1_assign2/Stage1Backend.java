public interface Stage1Backend {
	public boolean locate(String url);
	public boolean connect();
	public void disconnect();
	public boolean sendMessage(String message);
	public void attach(Stage1UserInterface ui);
}