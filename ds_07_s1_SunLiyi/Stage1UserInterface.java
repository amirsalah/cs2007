public interface Stage1UserInterface {
  public void displayMessage(String message) throws java.rmi.RemoteException;
  public void displayAlert(String message);
  public void quit(String reason);
}