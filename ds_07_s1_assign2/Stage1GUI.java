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


public class Stage1GUI {
    protected JTextArea output;
    protected JScrollPane scrollPane;
    protected final String newline = "\n";
    
    static protected class Stage1EnabledGUIState {
    	protected boolean connected = false;
    	protected boolean located = false;
    
    	public boolean locateMenuItemState() {
    		return !connected;
    	}
    	
    	public boolean connectMenuItemState() {
    		return located && !connected;
    	}
    	

    	public boolean disconnectMenuItemState() {
    		return located && connected;
    	}
    	

    	public boolean quitMenuItemState() {
    		return true;
    	}
    	

    	public boolean messageState() {
    		return connected;
    	}
    	
 		
 		public void hasLocatedServer() {
    		located = true;			
 		}
 		
 		public void hasConnected() {
 	 		connected = true;                            	
 		}
 		
 		public void hasDisconnected() {
 	 		connected = false;                            	
 		}
 		
 		public void hasLostContactWithServer() {	// Server has crashed or shutdown, or network has failed
    		located = false;
    		connected = false;
    	}

 	}
 	
 	static protected abstract class EnabledGUIStateActuator {
    	private EnabledGUIStateActuator nextActuator = null;
     	private EnabledGUIStateActuator prevActuator;
		public EnabledGUIStateActuator(EnabledGUIStateActuator prev) {
			prevActuator = prev;
    		if ( prevActuator != null ) prevActuator.setNextActuator(this);
    	}
    	
    	public void actuate() {
 			if ( prevActuator == null ) {
 				actuateLocal();
 				if ( nextActuator != null ) nextActuator.actuateContinue();
 			} else {
 				prevActuator.actuate();
 			}
 		}
 		
 		public void actuateContinue() {
 	 		actuateLocal();
 			if ( nextActuator != null ) nextActuator.actuateContinue();
 		}
		

		protected abstract void actuateLocal();
		
    	private void setNextActuator(EnabledGUIStateActuator next) {
    		nextActuator = next;
    	}
	}

			
		 		
 		
 	
 	static protected class Stage1EnabledGUIStateActuator extends EnabledGUIStateActuator {
 		protected Stage1EnabledGUIState theState;
   		protected JMenuItem locateMenuItem;
    	protected JMenuItem connectMenuItem;
    	protected JMenuItem disconnectMenuItem;
    	protected JMenuItem quitMenuItem;
    	protected JTextField message;
    	
    	public Stage1EnabledGUIStateActuator(
    		Stage1EnabledGUIState state,
    		EnabledGUIStateActuator prev,
    		JMenuItem locate,
    		JMenuItem connect,
    		JMenuItem disconnect,
    		JMenuItem quit,
    		JTextField mess
    	) {    		
    		super(prev);
    		theState = state;
    		locateMenuItem = locate;
    		connectMenuItem = connect;
    		disconnectMenuItem = disconnect;
    		quitMenuItem = quit;
    		message = mess;
 		}
 		
 		
 		protected void actuateLocal() {
    		locateMenuItem.setEnabled(theState.locateMenuItemState());                            
 			connectMenuItem.setEnabled(theState.connectMenuItemState());                            
			disconnectMenuItem.setEnabled(theState.disconnectMenuItemState());                            
			quitMenuItem.setEnabled(theState.quitMenuItemState());                            
 			message.setEnabled(theState.messageState());
    	}
   	}
 	
	protected interface GUIDecorator {
		public void applyTo(
			JMenuBar menuBar,JPanel contentPane,JFrame mainFrame,
			Stage1EnabledGUIState state,EnabledGUIStateActuator prevActuator
		);
	}
	
	
	class Stage1Decorator implements GUIDecorator {
		private Stage1Backend theBackend;
		private GUIDecorator theContinuation;
		
		public  Stage1Decorator(Stage1Backend backend,GUIDecorator continuation) {
			theBackend = backend;
			theContinuation = continuation;
		}
		
    	public void applyTo(
    		final JMenuBar menuBar,
    		final JPanel contentPane,
    		final JFrame mainFrame, 
    		final Stage1EnabledGUIState theState,
    		final EnabledGUIStateActuator prevActuator
    	) {

			final JMenu chatMenu = new JMenu("Chat");
			chatMenu.setMnemonic(KeyEvent.VK_C);
			chatMenu.getAccessibleContext().setAccessibleDescription(
					"Chat Functions");
	
	
			final JMenuItem locateMenuItem = new JMenuItem("Locate",
									 KeyEvent.VK_L);
			locateMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_L, ActionEvent.ALT_MASK));
			locateMenuItem.getAccessibleContext().setAccessibleDescription(
					"Locate a Chat server");
					
			final JMenuItem connectMenuItem = new JMenuItem("Connect",
									 KeyEvent.VK_C);
			connectMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_C, ActionEvent.ALT_MASK));
			connectMenuItem.getAccessibleContext().setAccessibleDescription(
					"Connect to the Chat server");
					
			final JMenuItem disconnectMenuItem = new JMenuItem("Disconnect",
									 KeyEvent.VK_D);
			disconnectMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_D, ActionEvent.ALT_MASK));
			disconnectMenuItem.getAccessibleContext().setAccessibleDescription(
					"Disconnect from the Chat server");
	
			final JMenuItem quitMenuItem = new JMenuItem("Quit",
									 KeyEvent.VK_Q);
			quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_Q, ActionEvent.ALT_MASK));
			quitMenuItem.getAccessibleContext().setAccessibleDescription(
					"Quit");
					
			// Text field used to enter message to send to server
			final JTextField message = new JTextField(52);                        
	
			final Stage1EnabledGUIStateActuator actuator = 
				new Stage1EnabledGUIStateActuator(
					theState,prevActuator,locateMenuItem, connectMenuItem, 
					disconnectMenuItem, quitMenuItem, message
				);
			
										   								 
			class LocateListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					String url = (String)JOptionPane.showInputDialog(
						mainFrame,
						"Please Enter the RMI URL of the server",
						"Locate Server",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						"rmi://");
					if ( url != null && theBackend.locate(url) ) {
						theState.hasLocatedServer();   
						actuator.actuate();
					}
				}
			}
	
			locateMenuItem.addActionListener(new LocateListener());
			
	
	
			class ConnectListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					if ( theBackend.connect() ) {
						theState.hasConnected();  
					} else {
						theState.hasLostContactWithServer();
					}
					actuator.actuate();
				}
			}
	
			connectMenuItem.addActionListener(new ConnectListener());
			
	
	
			class DisconnectListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					theBackend.disconnect(); 					// Error - should return boolean to distinguish graceful disconnect...
					theState.hasDisconnected();                              
					actuator.actuate();
				}
			}
	
			disconnectMenuItem.addActionListener(new DisconnectListener());
			
			class QuitActionListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
						System.exit(0);
				}
			}
	
			quitMenuItem.addActionListener(new QuitActionListener());
	
	
			chatMenu.add(locateMenuItem);
			chatMenu.add(connectMenuItem);
			chatMenu.add(disconnectMenuItem);
			chatMenu.add(quitMenuItem);
			menuBar.add(chatMenu);
	 
			contentPane.setOpaque(true);
	
			//Create a scrolled text area.
			output = new JTextArea(5, 30);
			output.setEditable(false);
			scrollPane = new JScrollPane(output);
	
			//Add the text area to the content pane.
			contentPane.add(scrollPane, BorderLayout.CENTER);
			
			//Add the text area to the content pane.
			contentPane.add(message, BorderLayout.SOUTH);
			
			
			class MessageListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					if ( theBackend.sendMessage(message.getText()) == false ) {
						theState.hasLostContactWithServer();                              
						actuator.actuate();
					}
					message.setText("");
				}
			}
			
			message.addActionListener(new MessageListener());
			
			if ( theContinuation != null ) theContinuation.applyTo(menuBar,contentPane,mainFrame,theState,actuator);
			
			actuator.actuate();
		}
	}
    
    private void printMessageToTextPane(String message) {
	      output.append(message + newline);
          output.setCaretPosition(output.getDocument().getLength());
	    }

	protected class UICallback implements Stage1UserInterface {
	    public void displayMessage(String message)  throws java.rmi.RemoteException {
	      printMessageToTextPane(message);
	    }
	    
	    public void displayAlert(String message) {
	      JOptionPane.showMessageDialog(null,message,"Alert",JOptionPane.WARNING_MESSAGE);
	    }
	    
	    public void quit(String reason) {
	      displayAlert(reason);
	      System.exit(1);
	    }
	}


    // Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }
    
    protected JMenuBar menuBar = new JMenuBar();
    protected JFrame frame = new JFrame("Chat GUI");
        
    public Stage1GUI(
    	Stage1Backend backend,GUIDecorator continuation,Stage1EnabledGUIState state
    ) {    	
        //Create and set up the window.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the GUI elements.
        JPanel contentPane = new JPanel(new BorderLayout());
        
        Stage1Decorator decorator = new Stage1Decorator(backend,continuation);
        decorator.applyTo(menuBar,contentPane,frame,state,null);
        
        frame.setJMenuBar(menuBar);
        frame.setContentPane(contentPane);
        
        backend.attach(new UICallback());

        //Display the window.
        frame.setSize(450, 260);
        frame.setVisible(true);
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
     static void createAndShowGUI(Stage1Backend backend) {
        Stage1GUI gui = new Stage1GUI(backend,null,new Stage1EnabledGUIState());
    }
}
