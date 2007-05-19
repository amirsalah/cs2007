public interface Stage3UserInterface {
  public void updatePrivilege(boolean isNowPrivileged);
  public void invalidKey(String message);
  public void pinged() throws java.rmi.RemoteException;
}