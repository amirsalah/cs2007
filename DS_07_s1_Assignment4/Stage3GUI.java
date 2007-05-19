import java.awt.*;
import java.awt.event.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;



public class Stage3GUI extends Stage2GUI {

    static protected class Stage3EnabledGUIState extends Stage2EnabledGUIState {  
    	protected boolean loggedIn = false;
    	protected boolean privileged = false;
    
    	public boolean loginMenuItemState() {
    		return located && !loggedIn;
    	}

    	public boolean logoutMenuItemState() {
    		return located && loggedIn;
    	}

    	public boolean setPasswordMenuItemState() {
    		return located && loggedIn;
    	}

    	public boolean setOthersPasswordMenuItemState() {
    		return located && loggedIn && privileged;
    	}
    	    	
    	public boolean createAccountMenuItemState() {
    		return located && loggedIn && privileged;
    	}
    	
    	public boolean setPrivilegeMenuItemState() {
    		return located && loggedIn && privileged;
    	}
 
    	public boolean shutdownAndAbortMenuItemState() {
    		return located && connected && loggedIn && privileged;
    	}
    	
    	public boolean selectTranscriptMenuItemState() {
    		return located && !connected && loggedIn && privileged;
    	}
    	
    	public boolean shutdownAndSaveMenuItemState() {
    		return located && connected && loggedIn && privileged;
    	}
    	
    	public boolean locateMenuItemState() {
    		return !connected && !loggedIn;
    	}
    	
    	public boolean connectMenuItemState() {
    		return located && !connected && loggedIn;
    	}
    	

    	public boolean disconnectMenuItemState() {
    		return located && connected && loggedIn;
    	}
    	

    	public boolean quitMenuItemState() {
    		return true;
    	}
    	
    	public boolean messageState() {
    		return located && connected && loggedIn;
    	}
    	
    	public void hasLoggedIn() {
    		loggedIn = true;			
 		}
 		
 		public void hasLoggedOut() {
 			loggedIn = false;
 	 		connected = false;  
 	 		privileged = false;
 	 		curUsername = "";
		}
 		
 		public void hasPrivilege(boolean priv) {
 			privileged = priv;
 		}
 		
 		String curUsername = "";
 		
 		public String currentUsername() {
 			return curUsername;
 		}
 		
 		public void setCurrentUsername(String user) {
 			curUsername = user;
 		}
 		
 		public void hasLostContactWithServer() {
 			super.hasLostContactWithServer();
 			hasLoggedOut();
 		}
    }


    static protected class Stage3EnabledGUIStateActuator extends EnabledGUIStateActuator {
 		protected Stage3EnabledGUIState theState;
    	protected JMenuItem loginMenuItem;
    	protected JMenuItem logoutMenuItem;
    	protected JMenuItem createAccountMenuItem;   	
    	protected JMenuItem setPasswordMenuItem;   	
     	protected JMenuItem setOthersPasswordMenuItem;   	
     	protected JMenuItem setPrivilegeMenuItem;   	
    	
    	public Stage3EnabledGUIStateActuator(
    		Stage3EnabledGUIState state,
    		EnabledGUIStateActuator prev,
    		JMenuItem login,
    		JMenuItem logout,
     		JMenuItem createAcc,
    		JMenuItem setPriv,
	   		JMenuItem setPass,
	   		JMenuItem setOthersPass
    	) {    		
    		super(prev);
    		theState = state;
     		loginMenuItem = login;
    		logoutMenuItem = logout;
     		createAccountMenuItem = createAcc;
    		setPrivilegeMenuItem = setPriv;
	   		setPasswordMenuItem = setPass;
	   		setOthersPasswordMenuItem = setOthersPass;
    	}

 		protected void actuateLocal() {
    		loginMenuItem.setEnabled(theState.loginMenuItemState());                            
 			logoutMenuItem.setEnabled(theState.logoutMenuItemState());                            
			createAccountMenuItem.setEnabled(theState.createAccountMenuItemState());                            
    		setPrivilegeMenuItem.setEnabled(theState.setPrivilegeMenuItemState());                            
 			setPasswordMenuItem.setEnabled(theState.setPasswordMenuItemState());                            
 			setOthersPasswordMenuItem.setEnabled(theState.setOthersPasswordMenuItemState());                            
    	}

 	}
 	
 	static class Stage3Decorator implements GUIDecorator {
		private Stage3Backend theBackend;
		private GUIDecorator theContinuation;
		
		public Stage3Decorator(Stage3Backend backend,GUIDecorator continuation) {
			theBackend = backend;
			theContinuation = continuation;
		}
				
    	public void applyTo(
    		final JMenuBar menuBar,
    		final JPanel contentPane,
    		final JFrame mainFrame, 
    		final Stage1EnabledGUIState state,
    		final EnabledGUIStateActuator prevActuator
    	) {
    		final Stage3EnabledGUIState theState = (Stage3EnabledGUIState)state;
    	
    		//Add the login label to the content pane.
    		final JLabel currentLoginLabel = new JLabel("       ");
			contentPane.add(currentLoginLabel, BorderLayout.NORTH);

    
			final JMenu accountsMenu = new JMenu("Accounts");
			accountsMenu.setMnemonic(KeyEvent.VK_A);
        	accountsMenu.getAccessibleContext().setAccessibleDescription(
                "Accounts Functions");
                
            final JMenuItem loginMenuItem = new JMenuItem("Login",
                                 KeyEvent.VK_O);
        	loginMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.ALT_MASK));
        	loginMenuItem.getAccessibleContext().setAccessibleDescription(
                "Login to the Chat server");
 
            final JMenuItem logoutMenuItem = new JMenuItem("Logout",
                                 KeyEvent.VK_X);
        	logoutMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.ALT_MASK));
        	logoutMenuItem.getAccessibleContext().setAccessibleDescription(
                "Logout from the Chat server");
 
        	final JMenuItem changePasswordMenuItem = new JMenuItem("Change Own Password",
                                 KeyEvent.VK_P);
        	changePasswordMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        	changePasswordMenuItem.getAccessibleContext().setAccessibleDescription(
                "Change Own Password");
                
        	final JMenuItem changeOthersPasswordMenuItem = new JMenuItem("Change Others' Passwords",
                                 KeyEvent.VK_P);
        	changeOthersPasswordMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        	changeOthersPasswordMenuItem.getAccessibleContext().setAccessibleDescription(
                "Change Others' Passwords");

       		final JMenuItem setPrivilegeMenuItem = new JMenuItem("Set Privilege Levels",
                                 KeyEvent.VK_P);
        	setPrivilegeMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        	setPrivilegeMenuItem.getAccessibleContext().setAccessibleDescription(
                "Set Privilege Levels");

       		final JMenuItem createAccountMenuItem = new JMenuItem("Create Account",
                                 KeyEvent.VK_P);
        	createAccountMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        	createAccountMenuItem.getAccessibleContext().setAccessibleDescription(
                "Create Account");
			

		   
			final Stage3EnabledGUIStateActuator actuator = 
				new Stage3EnabledGUIStateActuator(
					theState,prevActuator,loginMenuItem,logoutMenuItem, createAccountMenuItem,
					setPrivilegeMenuItem, changePasswordMenuItem, changeOthersPasswordMenuItem
				);
				
			class UICallback implements Stage3UserInterface {	    
				public void updatePrivilege(boolean isNowPrivileged) {
					theState.hasPrivilege(isNowPrivileged);
					actuator.actuate();
				}
				
				public void invalidKey(String message) {
					JOptionPane.showMessageDialog(null,message + ": Invalid Key","Alert",JOptionPane.WARNING_MESSAGE);
					theState.hasLoggedOut();
					actuator.actuate();
				}
				
				public void pinged() throws java.rmi.RemoteException {
					;
				}
			}
			
			final UICallback uiCallback = new UICallback();
			theBackend.attach(uiCallback);

			class LoginListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					JPasswordField password = new JPasswordField(10);
					JTextField username = new JTextField(10);
					JOptionPane.showOptionDialog(
						null,"Login","Login",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,
						new Object[]{"OK",password,new JLabel("Password:"),username,new JLabel("Username:")},null
					);	
					if ( theBackend.login(username.getText(),new String(password.getPassword())) ) {
						if ( theBackend.isLoggedIn() ) {
							theState.hasLoggedIn();
							theState.hasPrivilege(theBackend.isPrivileged());
							theState.setCurrentUsername(username.getText());
							actuator.actuate();
						}
					} else {
						theState.hasLostContactWithServer();
						actuator.actuate();
					}						
				}
			}
			
			loginMenuItem.addActionListener(new LoginListener());
			
			class LogoutListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					if ( theBackend.logout() ) {
						theState.hasLoggedOut();
					} else {
						theState.hasLostContactWithServer();   
					}
					actuator.actuate();
				}
			}

			logoutMenuItem.addActionListener(new LogoutListener());
			
			class ChangeOthersPasswordListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					JPasswordField password = new JPasswordField(10);
					JTextField username = new JTextField(10);
					JOptionPane.showOptionDialog(
						null,"Change Password for User","Change Password",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,
						new Object[]{"OK",password,new JLabel("Password:"),username,new JLabel("Username:")},null
					);	
					if ( theBackend.setPassword(username.getText(),new String(password.getPassword())) == false ) {
						theState.hasLostContactWithServer();
						actuator.actuate();
					}						
				}
			}

			changeOthersPasswordMenuItem.addActionListener(new ChangeOthersPasswordListener());
			
			class ChangePasswordListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					JPasswordField password = new JPasswordField(10);
					JOptionPane.showOptionDialog(
						null,"Change Your Password","Change Password",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,
						new Object[]{"OK",password,new JLabel("Password:")},null
					);	
					if ( theBackend.setPassword(theState.currentUsername(),new String(password.getPassword())) == false ) {
						theState.hasLostContactWithServer();
						actuator.actuate();
					}						
				}
			}

			changePasswordMenuItem.addActionListener(new ChangePasswordListener());
			
			class SetPrivilegeListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					JComboBox priv = new JComboBox(new String[]{"Privileged","Unprivileged"});
					JTextField username = new JTextField(10);
					JOptionPane.showOptionDialog(
						null,"Change Privilege for User","Change Privilege",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,
						new Object[]{"OK",priv,new JLabel("Privilege:"),username,new JLabel("Username:")},null
					);	
					String privString = (String)priv.getSelectedItem();
					if ( theBackend.setPrivilege(username.getText(),privString.equals("Privileged")) == false ) {
						theState.hasLostContactWithServer();
						actuator.actuate();
					}						
				}
			}

			setPrivilegeMenuItem.addActionListener(new SetPrivilegeListener());

			class CreateAccountListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					JComboBox priv = new JComboBox(new String[]{"Privileged","Unprivileged"});
					JPasswordField password = new JPasswordField(10);
					JTextField username = new JTextField(10);
					JOptionPane.showOptionDialog(
						null,"Create Account","Create Account",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE,null,
						new Object[]{"OK",priv,new JLabel("Privilege:"),password,new JLabel("Password:"),username,new JLabel("Username:")},null
					);	
					String privString = (String)priv.getSelectedItem();
					if ( theBackend.createAccount(username.getText(),new String(password.getPassword()),privString.equals("Privileged")) == false ) {
						theState.hasLostContactWithServer();
						actuator.actuate();
					}						
				}
			}

			createAccountMenuItem.addActionListener(new CreateAccountListener());

			accountsMenu.add(loginMenuItem);
			accountsMenu.add(logoutMenuItem);
			accountsMenu.add(createAccountMenuItem);
			accountsMenu.add(setPrivilegeMenuItem);
			accountsMenu.add(changePasswordMenuItem);
			accountsMenu.add(changeOthersPasswordMenuItem);
			

			menuBar.add(accountsMenu);
			
			if ( theContinuation != null ) theContinuation.applyTo(menuBar,contentPane,mainFrame,theState,actuator);
		}
    }
 	    
    public Stage3GUI(Stage3Backend backend,GUIDecorator continuation,Stage3EnabledGUIState state) {
    	super(backend,new Stage3Decorator(backend,continuation),state);
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
     static void createAndShowGUI(Stage3Backend backend) {
        Stage3GUI gui = new Stage3GUI(backend,null,new Stage3EnabledGUIState());
    }

}