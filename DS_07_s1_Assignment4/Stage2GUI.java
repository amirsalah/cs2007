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




public class Stage2GUI extends Stage1GUI { 


    static protected class Stage2EnabledGUIState extends Stage1EnabledGUIState {    
    	public boolean selectTranscriptMenuItemState() {
    		return located && !connected;
    	}
    	
    	public boolean shutdownAndSaveMenuItemState() {
    		return connected;
    	}
    	

    	public boolean shutdownAndAbortMenuItemState() {
    		return connected;
    	}
    }


    static protected class Stage2EnabledGUIStateActuator extends EnabledGUIStateActuator {
 		protected Stage2EnabledGUIState theState;
    	protected JMenuItem selectTranscriptMenuItem;
    	protected JMenuItem shutdownAndSaveMenuItem;
    	protected JMenuItem shutdownAndAbortMenuItem;   	
    	
    	public Stage2EnabledGUIStateActuator(
    		Stage2EnabledGUIState state,
    		EnabledGUIStateActuator prev,
    		JMenuItem select,
    		JMenuItem save,
    		JMenuItem abort    			
    	) {    		
    		super(prev);
    		theState = state;
     		selectTranscriptMenuItem = select;
    		shutdownAndSaveMenuItem = save;
    		shutdownAndAbortMenuItem = abort;
    	}

 		protected void actuateLocal() {
    		selectTranscriptMenuItem.setEnabled(theState.selectTranscriptMenuItemState());                            
 			shutdownAndSaveMenuItem.setEnabled(theState.shutdownAndSaveMenuItemState());                            
			shutdownAndAbortMenuItem.setEnabled(theState.shutdownAndAbortMenuItemState());                            
    	}

 	}

	static class Stage2Decorator implements GUIDecorator {
		private Stage2Backend theBackend;
		private GUIDecorator theContinuation;
		
		public Stage2Decorator(Stage2Backend backend,GUIDecorator continuation) {
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
    		final Stage2EnabledGUIState theState = (Stage2EnabledGUIState)state;
    	
    		//Add the transcript label to the content pane.
    		final JLabel currentTranscriptLabel = new JLabel("       ");
			contentPane.add(currentTranscriptLabel, BorderLayout.NORTH);

    
			final JMenu transcriptMenu = new JMenu("Transcript");
			transcriptMenu.setMnemonic(KeyEvent.VK_T);
			transcriptMenu.getAccessibleContext().setAccessibleDescription(
					"Transcript Functions");
	
			final JMenuItem selectTranscriptMenuItem = new JMenuItem("Select Transcript",KeyEvent.VK_T);
			selectTranscriptMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_T, ActionEvent.ALT_MASK));
			selectTranscriptMenuItem.getAccessibleContext().setAccessibleDescription(
					"Select a Transcript");
			
			final JMenuItem shutdownAndSaveMenuItem = new JMenuItem("Shutdown and Save",KeyEvent.VK_S);
			shutdownAndSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_S, ActionEvent.ALT_MASK));
			shutdownAndSaveMenuItem.getAccessibleContext().setAccessibleDescription(
					"Shutdown Server and Save Transcript");
			
			final JMenuItem shutdownAndAbortMenuItem = new JMenuItem("Shutdown and Abort",KeyEvent.VK_A);
			shutdownAndAbortMenuItem.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_A, ActionEvent.ALT_MASK));
			shutdownAndAbortMenuItem.getAccessibleContext().setAccessibleDescription(
					"Shutdown Server without Saving Transcript");
		   
			final Stage2EnabledGUIStateActuator actuator = 
				new Stage2EnabledGUIStateActuator(
					theState,prevActuator,selectTranscriptMenuItem, 
					shutdownAndSaveMenuItem, shutdownAndAbortMenuItem
				);
				
			class UICallback implements Stage2UserInterface {	    
	    		public void setTranscriptLabelText(String message) {
	    			currentTranscriptLabel.setText("  " + message);
				}	    
			}
			
			final UICallback uiCallback = new UICallback();
			theBackend.attach(uiCallback);

			class SelectTranscriptListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					String[] transcripts = theBackend.getTranscriptList();
					if ( transcripts != null ) {
						String transcript = (String)JOptionPane.showInputDialog(
							mainFrame,
							"Please Select a Transcript to Load",
							"Select Transcript",
							JOptionPane.PLAIN_MESSAGE,
							null,
							transcripts,
							"");
						theBackend.selectTranscript(transcript);
						uiCallback.setTranscriptLabelText("Selected transcript: " + transcript);
					} else {
						theState.hasLostContactWithServer();
						actuator.actuate();
					}
				}
			}
			
			selectTranscriptMenuItem.addActionListener(new SelectTranscriptListener());
				
			class SaveListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					String transcriptName = (String)JOptionPane.showInputDialog(
						mainFrame,
						"Please enter the name with which the transcript will be saved on the server",
						"Save Transcript",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						"");
					if ( transcriptName != null ) {
						theBackend.shutdownAndSave(transcriptName );
						theState.hasLostContactWithServer();   
						actuator.actuate();
					}
				}
			}

	
			shutdownAndSaveMenuItem.addActionListener(new SaveListener());
	
			class AbortListener implements ActionListener {
				public void actionPerformed(ActionEvent e) {
					theBackend.shutdownAndAbort();
					theState.hasLostContactWithServer();   
					actuator.actuate();
				}
			}

			shutdownAndAbortMenuItem.addActionListener(new AbortListener());
	
			transcriptMenu.add(selectTranscriptMenuItem);
			transcriptMenu.add(shutdownAndSaveMenuItem);
			transcriptMenu.add(shutdownAndAbortMenuItem);
			menuBar.add(transcriptMenu);
			
			if ( theContinuation != null ) theContinuation.applyTo(menuBar,contentPane,mainFrame,theState,actuator);
		}
    }
        
    public Stage2GUI(Stage2Backend backend,GUIDecorator continuation,Stage2EnabledGUIState state) {
    	super(backend,new Stage2Decorator(backend,continuation),state);
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
     static void createAndShowGUI(Stage2Backend backend) {
        Stage2GUI gui = new Stage2GUI(backend,null,new Stage2EnabledGUIState());
    }
}