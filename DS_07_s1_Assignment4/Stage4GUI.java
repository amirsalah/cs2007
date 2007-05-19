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



public class Stage4GUI extends Stage3GUI {

    static protected class Stage4EnabledGUIState extends Stage3EnabledGUIState {  
    
    	public boolean joinMenuItemState() {
    		return located && loggedIn && privileged && !connected;
    	}

    	public boolean leaveMenuItemState() {
    		return located && loggedIn && privileged && !connected;
    	}
    }


    static protected class Stage4EnabledGUIStateActuator extends EnabledGUIStateActuator {
 		protected Stage4EnabledGUIState theState;
    	protected JMenuItem joinMenuItem;
    	protected JMenuItem leaveMenuItem;
    	
    	public Stage4EnabledGUIStateActuator(
    		Stage4EnabledGUIState state,
    		EnabledGUIStateActuator prev,
    		JMenuItem join,
    		JMenuItem leave
    	) {    		
    		super(prev);
    		theState = state;
     		joinMenuItem = join;
    		leaveMenuItem = leave;
    	}

 		protected void actuateLocal() {
    		joinMenuItem.setEnabled(theState.joinMenuItemState());                            
 			leaveMenuItem.setEnabled(theState.leaveMenuItemState());                            
    	}

 	}
 	
 	static class Stage4Decorator implements GUIDecorator {
		private Stage4Backend theBackend;
		private GUIDecorator theContinuation;
		
		public Stage4Decorator(Stage4Backend backend,GUIDecorator continuation) {
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
    		final Stage4EnabledGUIState theState = (Stage4EnabledGUIState)state;
    	
    
			final JMenu peerMenu = new JMenu("Peering");
			peerMenu.setMnemonic(KeyEvent.VK_P);
        	peerMenu.getAccessibleContext().setAccessibleDescription(
                "Server Peering Functions");
                
            final JMenuItem joinMenuItem = new JMenuItem("Join",
                                 KeyEvent.VK_J);
        	joinMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_J, ActionEvent.ALT_MASK));
        	joinMenuItem.getAccessibleContext().setAccessibleDescription(
                "Join the Chat Server to a Peer");

            final JMenuItem leaveMenuItem = new JMenuItem("Leave",
                                 KeyEvent.VK_V);
        	leaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, ActionEvent.ALT_MASK));
        	leaveMenuItem.getAccessibleContext().setAccessibleDescription(
                "Cause the Chat Server to Leave the Peer it has Joined");
 		   
			final Stage4EnabledGUIStateActuator actuator = 
				new Stage4EnabledGUIStateActuator(
					theState,prevActuator,joinMenuItem,leaveMenuItem
				);
				
			class JoinListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					String url = (String)JOptionPane.showInputDialog(
						mainFrame,
						"Please Enter the RMI URL of the peer server that server is to join",
						"Join to Peer Server",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						"rmi://");
					if ( url != null ) {
						if ( ! theBackend.joinPeer(url) ) {
							theState.hasLostContactWithServer();   
							actuator.actuate();
						}
					}
				}
			}
			
			joinMenuItem.addActionListener(new JoinListener());
			
			class LeaveListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					if ( ! theBackend.leavePeer() ) {
						theState.hasLostContactWithServer();   
						actuator.actuate();
					}
				}
			}

			leaveMenuItem.addActionListener(new LeaveListener());
			
			peerMenu.add(joinMenuItem);
			peerMenu.add(leaveMenuItem);			

			menuBar.add(peerMenu);
			
			if ( theContinuation != null ) theContinuation.applyTo(menuBar,contentPane,mainFrame,theState,actuator);
		}
    }
 	    
    public Stage4GUI(Stage4Backend backend,GUIDecorator continuation,Stage4EnabledGUIState state) {
    	super(backend,new Stage4Decorator(backend,continuation),state);
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
     static void createAndShowGUI(Stage4Backend backend) {
        Stage3GUI gui = new Stage4GUI(backend,null,new Stage4EnabledGUIState());
    }

}