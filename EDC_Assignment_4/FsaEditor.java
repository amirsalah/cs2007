/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 5th, Oct 2007
=========================================================*/
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class FsaEditor {

	public static void main(String[] args) {
		JFrame mainFrame = new FsaFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}
}

class FsaFrame extends JFrame {
	public FsaFrame() {
		initComponents();
	}

	private void LoadFSA_MouseClicked(ActionEvent e){
		String filePath = null;
		// Choose a file containing fsa info.
		int loadFileResult = fileChooser.showOpenDialog(this); 

		if(loadFileResult == JFileChooser.APPROVE_OPTION){
			try{
				messagesArea.setText("");
				filePath = fileChooser.getSelectedFile().getPath();
				fileR = new FileReader(filePath);
			}
			catch(FileNotFoundException fnfe){
				System.out.println("File not found");
			}
		
			try{
				((FsaImpl)fsa).Clear();
				fsaRW.read(fileR, fsa);
			}
			catch(FsaFormatException ffe){
				System.out.println("fsa exception");
			}
			catch(IOException ioe){
				System.out.println("fsa io exception");
			}
			isFsaLoaded = true;
			messagesArea.append("Loaded FSA from file : " + filePath + "\n") ;
			displayArea.LoadFsa(fsa);
		}
	}

	private void StoreFSA_MouseClicked(ActionEvent e) {
		String filePath = null;
		
		if(!isFsaLoaded){
			messagesArea.append("FSA has not been loaded, save file failed" + "\n");
			return;
		}
		// Choose the file to store fsa info.
		int saveFileResult = fileChooser.showOpenDialog(this); 
		if(saveFileResult == JFileChooser.APPROVE_OPTION){
			try{
				filePath = fileChooser.getSelectedFile().getPath();
				fileW = new FileWriter(filePath);
			}
		    catch (FileNotFoundException fnfe) {
		        System.err.println("Caught IOException: " + fnfe.getMessage());
		    }
		    catch(IOException ioe){
		    	System.err.println("IO exception:" + ioe.getMessage());
		    }
		    
		    try{
		    	fsaRW.write(fileW, fsa);
		    }
		    catch(IOException ioe){
		    	System.err.println("IO exception:" + ioe.getMessage());
		    }
		    
		    messagesArea.append("Saved FSA to file : " + filePath + "\n") ;
		}
	}

	private void Loadevents_MouseClicked(ActionEvent e) {
		String filePath = null;
		// Choose the file containing fsa info.
		int loadFileResult = fileChooser.showOpenDialog(this); 
		if(loadFileResult == JFileChooser.APPROVE_OPTION){
			try{
				filePath = fileChooser.getSelectedFile().getPath();
				fileR = new FileReader(filePath);
			}catch(FileNotFoundException fnfe){
				System.out.println("File not found");
			}
			
			messagesArea.append("\n");
			events = new EventManager(messagesArea);
			try{
				events.read(fileR);
			}
			catch(EventFileException efe){
				System.out.println("event file exception");
			}catch(IOException ioe){
				System.out.println("event reader io exception");
			}
		}
		
	}

	private void Quit_MouseClicked(ActionEvent e) {
		System.exit(0);
	}

	private void newState_MouseClicked(ActionEvent e) {
		if(!isFsaLoaded){
			messagesArea.append("FSA has not been loaded" + "\n");
			return;
		}
		
		displayArea.NewState();
	}

	private void newTransition_MouseClicked(ActionEvent e) {
		// TODO
	}

	private void renameState_MouseClicked(ActionEvent e) {
		// TODO
	}

	private void setInitialState_MouseClicked(ActionEvent e) {
		if(!isFsaLoaded){
			messagesArea.append("FSA has not been loaded" + "\n");
			return;
		}
		if(!displayArea.SetInitialStates()){
			messagesArea.append("No state is selected, please specify the states" + "\n");
			return;
		}
		if(simulationStarted == false){
			((FsaImpl)fsa).reset();
		}
		displayArea.repaint();
	}

	private void deleteStates_MouseClicked(ActionEvent e) {
		if(!isFsaLoaded){
			messagesArea.append("FSA has not been loaded" + "\n");
			return;
		}
		
		displayArea.DeleteSelectedStates();
	}

	private void useBasicRenderer_MouseClicked(ActionEvent e) {
		((FsaDisplayPanel)displayArea).SetDefaultRenderer();
	}

	private void useMyRenderer_MouseClicked(ActionEvent e) {
		((FsaDisplayPanel)displayArea).SetMyRenderer();
	}

	private void resetButton_MouseClicked(ActionEvent e) {
		((FsaImpl)fsa).reset();
		try{
			events.reset();
		}
		catch(NullPointerException npe){
			messagesArea.append("No event is loaded" + "\n");
		}
		
		if(!isFsaLoaded){
			messagesArea.append("FSA has not been loaded" + "\n");
			return;
		}
		
		simulationStarted = false;
		messagesArea.append("Reset the FSA" + "\n");
		displayArea.repaint();
	}

	private void stepButton_MouseClicked(ActionEvent e) {
		String outputMessage = null;
		if(!isFsaLoaded){
			messagesArea.append("FSA has not been loaded" + "\n");
			return;
		}
		if(events == null){
			messagesArea.append("No events are loaded" + "\n");
			return;
		}
		
		simulationStarted = true;
		String eventName = events.nextEvent();
		List outputList = ((FsaImpl)fsa).step(eventName);
		
		// Dies, no next transition. Reset to initial state
		if(outputList == null || outputList.size() == 0){
			((FsaImpl)fsa).reset();
			events.reset();
			messagesArea.append("Terminated & Reset the FSA." + "\n");
			simulationStarted = false;
			displayArea.repaint(); 
			return;
		}
		
		outputMessage = outputList.toString();
		messagesArea.append("Event: " + eventName + "  Output: ");
		messagesArea.append(outputMessage + "\n");
		displayArea.repaint(); 
	}

	private void playButton_MouseClicked(MouseEvent e) {
		// TODO 
	}

	private void initComponents() {
		dialogPane = new JPanel();
		menuBar1 = new JMenuBar();
		fileMenu = new JMenu();
		loadFsaMenuItem = new JMenuItem();
		storeFsaMenuItem = new JMenuItem();
		separator1 = new JSeparator();
		loadEventsMenuItem = new JMenuItem();
		separator2 = new JSeparator();
		quitMenuItem = new JMenuItem();
		editMenu = new JMenu();
		newStateMenuItem = new JMenuItem();
		newTransitionMenuItem = new JMenuItem();
		renameStateMenuItem = new JMenuItem();
		setInitialStateMenuItem = new JMenuItem();
		deleteStatesMenuItem = new JMenuItem();
		OptionsMenu = new JMenu();
		useBasicRenderMenuItem = new JMenuItem();
		myRenderMenuItem = new JMenuItem();
		helpMenu = new JMenu();
		helpContentsMenuItem = new JMenuItem();
		aboutMenuItem = new JMenuItem();
		messageLabel = new JLabel();
		scrollPane1 = new JScrollPane();
		messagesArea = new JTextArea();
		controlLabel = new JLabel();
		resetButton = new JButton();
		stepButton = new JButton();
		playButton = new JButton();
		displayLabel = new JLabel();
		displayArea = new FsaDisplayPanel(messagesArea);
		scrollDisplayArea = new JScrollPane(displayArea, 
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		fileChooser = new JFileChooser(System.getProperty( "user.dir" ));
		
		//======== this ========
		setTitle("FSA");
		Container contentPane = getContentPane();
		contentPane.setLayout(null);

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== menuBar1 ========
			{
				menuBar1.setBackground(UIManager.getColor("Button.background"));

				//======== fileMenu ========
				{
					fileMenu.setText("File");
					fileMenu.setBackground(UIManager.getColor("Button.background"));

					//---- loadFsaMenuItem ----
					loadFsaMenuItem.setText("LoadFSA...");
					loadFsaMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							LoadFSA_MouseClicked(e);
						}
					});
					loadFsaMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
					fileMenu.add(loadFsaMenuItem);

					//---- storeFsaMenuItem ----
					storeFsaMenuItem.setText("StoreFSA...");
					storeFsaMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							StoreFSA_MouseClicked(e);
						}
					});
					storeFsaMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
					fileMenu.add(storeFsaMenuItem);
					fileMenu.add(separator1);

					//---- loadEventsMenuItem ----
					loadEventsMenuItem.setText("Loadevents...");
					loadEventsMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Loadevents_MouseClicked(e);
						}
					});
					loadEventsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
					fileMenu.add(loadEventsMenuItem);
					fileMenu.add(separator2);

					//---- quitMenuItem ----
					quitMenuItem.setText("Quit");
					quitMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Quit_MouseClicked(e);
						}
					});
					quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
					fileMenu.add(quitMenuItem);
				}
				menuBar1.add(fileMenu);

				//======== editMenu ========
				{
					editMenu.setText("Edit");
					editMenu.setBackground(UIManager.getColor("Button.background"));

					//---- newStateMenuItem ----
					newStateMenuItem.setText("New state");
					newStateMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							newState_MouseClicked(e);
						}
					});
					newStateMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
					editMenu.add(newStateMenuItem);

					//---- newTransitionMenuItem ----
					newTransitionMenuItem.setText("newTransition");
					newTransitionMenuItem.setForeground(SystemColor.textInactiveText);
					newTransitionMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e){
							newTransition_MouseClicked(e);
						}
					});
					editMenu.add(newTransitionMenuItem);

					//---- renameStateMenuItem ----
					renameStateMenuItem.setText("renameState");
					renameStateMenuItem.setForeground(SystemColor.textInactiveText);
					renameStateMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							renameState_MouseClicked(e);
						}
					});
					editMenu.add(renameStateMenuItem);

					//---- setInitialStateMenuItem ----
					setInitialStateMenuItem.setText("Add initial state");
					setInitialStateMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							setInitialState_MouseClicked(e);
						}
					});
					setInitialStateMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
					editMenu.add(setInitialStateMenuItem);

					//---- deleteStatesMenuItem ----
					deleteStatesMenuItem.setText("Delete selection");
//					deleteStatesMenuItem.setForeground(SystemColor.textInactiveText);
					deleteStatesMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							deleteStates_MouseClicked(e);
						}
					});
					deleteStatesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
					editMenu.add(deleteStatesMenuItem);
				}
				menuBar1.add(editMenu);

				//======== OptionsMenu ========
				{
					OptionsMenu.setText("Options");

					//---- useBasicRenderMenuItem ----
					useBasicRenderMenuItem.setText("useBasicRenderer");
					useBasicRenderMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							useBasicRenderer_MouseClicked(e);
						}
					});
					OptionsMenu.add(useBasicRenderMenuItem);

					//---- myRenderMenuItem ----
					myRenderMenuItem.setText("useMyRenderer");
					myRenderMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							useMyRenderer_MouseClicked(e);
						}
					});
					OptionsMenu.add(myRenderMenuItem);
				}
				menuBar1.add(OptionsMenu);

				//======== helpMenu ========
				{
					helpMenu.setText("Help");
					helpMenu.setBackground(UIManager.getColor("Button.background"));

					//---- helpContentsMenuItem ----
					helpContentsMenuItem.setText("Help contents");
					helpMenu.add(helpContentsMenuItem);

					//---- aboutMenuItem ----
					aboutMenuItem.setText("About...");
					helpMenu.add(aboutMenuItem);
				}
				menuBar1.add(helpMenu);
			}
			dialogPane.add(menuBar1, BorderLayout.NORTH);
		}
		contentPane.add(dialogPane);
		dialogPane.setBounds(0, 0, 411, 45);

		//---- messageLabel ----
		messageLabel.setText("Messages and Output");
		contentPane.add(messageLabel);
		messageLabel.setBounds(new Rectangle(new Point(225, 360), messageLabel.getPreferredSize()));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(messagesArea);
		}
		contentPane.add(scrollPane1);
		scrollPane1.setBounds(225, 385, 400, 115);

		//---- controlLabel ----
		controlLabel.setText("Control Area");
		contentPane.add(controlLabel);
		controlLabel.setBounds(25, 360, 95, 20);

		//---- resetButton ----
		resetButton.setText("Reset");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetButton_MouseClicked(e);
			}
		});
		contentPane.add(resetButton);
		resetButton.setBounds(25, 385, 90, 25);

		//---- stepButton ----
		stepButton.setText("Step");
		stepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stepButton_MouseClicked(e);
			}
		});
		contentPane.add(stepButton);
		stepButton.setBounds(25, 427, 90, 25);

		//---- playButton ----
		playButton.setText("Play");
		playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				playButton_MouseClicked(e);
			}
		});
		contentPane.add(playButton);
		playButton.setBounds(25, 469, 90, playButton.getPreferredSize().height);

		//---- displayLabel ----
		displayLabel.setText("Display Area");
		contentPane.add(displayLabel);
		displayLabel.setBounds(15, 45, 105, displayLabel.getPreferredSize().height);
		contentPane.add(displayArea);
//		contentPane.add(scrollDisplayArea, BorderLayout.CENTER);
		displayArea.setBounds(30, 70, 590, 285);
//		displayArea.setPreferredSize(new Dimension(590 , 285));
//		scrollDisplayArea.setViewportView(displayArea);
		
		{ // compute preferred size
			Dimension preferredSize = new Dimension();
			for(int i = 0; i < contentPane.getComponentCount(); i++) {
				Rectangle bounds = contentPane.getComponent(i).getBounds();
				preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
				preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
			}
			Insets insets = contentPane.getInsets();
			preferredSize.width += insets.right;
			preferredSize.height += insets.bottom;
			contentPane.setMinimumSize(preferredSize);
			contentPane.setPreferredSize(preferredSize);
		}
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel dialogPane;
	private JMenuBar menuBar1;
	private JMenu fileMenu;
	private JMenuItem loadFsaMenuItem;
	private JMenuItem storeFsaMenuItem;
	private JSeparator separator1;
	private JMenuItem loadEventsMenuItem;
	private JSeparator separator2;
	private JMenuItem quitMenuItem;
	private JMenu editMenu;
	private JMenuItem newStateMenuItem;
	private JMenuItem newTransitionMenuItem;
	private JMenuItem renameStateMenuItem;
	private JMenuItem setInitialStateMenuItem;
	private JMenuItem deleteStatesMenuItem;
	private JMenu OptionsMenu;
	private JMenuItem useBasicRenderMenuItem;
	private JMenuItem myRenderMenuItem;
	private JMenu helpMenu;
	private JMenuItem helpContentsMenuItem;
	private JMenuItem aboutMenuItem;
	private JLabel messageLabel;
	private JScrollPane scrollPane1;
	private JTextArea messagesArea;
	private JLabel controlLabel;
	private JButton resetButton;
	private JButton stepButton;
	private JButton playButton;
	private JLabel displayLabel;
	private FsaDisplayPanel displayArea;
	
	private JScrollPane scrollDisplayArea;
	private JFileChooser fileChooser;
	private FileReader fileR = null;
	private FileWriter fileW = null;
	private FsaReaderWriter fsaRW = new FsaReaderWriter();;
	private Fsa fsa = new FsaImpl();;
	private EventSeq events = null;
	private Boolean isFsaLoaded = false;
	private Boolean simulationStarted = false;
}
