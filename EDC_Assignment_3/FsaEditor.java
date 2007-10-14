/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 5th, Oct 2007
=========================================================*/
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
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

	private void LoadFSA_MouseClicked(ActionEvent e) {
		String filePath = null;
		try{
			// Choose the file containing fsa info.
			int loadFileResult = fileChooser.showOpenDialog(this); 
			if(loadFileResult == JFileChooser.APPROVE_OPTION){
				messagesArea.setText("");
				filePath = fileChooser.getSelectedFile().getPath();
				fileR = new FileReader(filePath);
			}
		}
		catch(FileNotFoundException fnfe){
			System.out.println("File not found");
		}catch(IOException ioe){
            System.out.println("IO error!");
        }
		
		try{
			fsaRW.read(fileR, fsa);
		}
		catch(FsaFormatException ffe){
			System.out.println("fsa exception");
		}
		catch(IOException ioe){
			System.out.println("fsa io exception");
		}
		
	}
	
	private void FsaInit(){
		
	}

	private void StoreFSA_MouseClicked(ActionEvent e) {
		// TODO add your code here
	}

	private void Loadevents_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void Quit_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void newState_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void newTransition_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void renameState_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void setInitialState_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void unsetInitialState_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void useBasicRenderer_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void useMyRenderer_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void resetButton_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void stepButton_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void playButton_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
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
		unsetInitialStateMenuItem = new JMenuItem();
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
		displayArea = new JLayeredPane();
		
		fileChooser = new JFileChooser();
		
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
					fileMenu.add(loadFsaMenuItem);

					//---- storeFsaMenuItem ----
					storeFsaMenuItem.setText("StoreFSA...");
					storeFsaMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
//							StoreFSA_MouseClicked(e);
						}
					});
					fileMenu.add(storeFsaMenuItem);
					fileMenu.add(separator1);

					//---- loadEventsMenuItem ----
					loadEventsMenuItem.setText("Loadevents...");
					loadEventsMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							Loadevents_MouseClicked(e);
						}
					});
					fileMenu.add(loadEventsMenuItem);
					fileMenu.add(separator2);

					//---- quitMenuItem ----
					quitMenuItem.setText("Quit");
					quitMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							Quit_MouseClicked(e);
						}
					});
					fileMenu.add(quitMenuItem);
				}
				menuBar1.add(fileMenu);

				//======== editMenu ========
				{
					editMenu.setText("Edit");
					editMenu.setBackground(UIManager.getColor("Button.background"));

					//---- newStateMenuItem ----
					newStateMenuItem.setText("newState");
					newStateMenuItem.setForeground(SystemColor.textInactiveText);
					newStateMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							newState_MouseClicked(e);
						}
					});
					editMenu.add(newStateMenuItem);

					//---- newTransitionMenuItem ----
					newTransitionMenuItem.setText("newTransition");
					newTransitionMenuItem.setForeground(SystemColor.textInactiveText);
					newTransitionMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							newTransition_MouseClicked(e);
						}
					});
					editMenu.add(newTransitionMenuItem);

					//---- renameStateMenuItem ----
					renameStateMenuItem.setText("renameState");
					renameStateMenuItem.setForeground(SystemColor.textInactiveText);
					renameStateMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							renameState_MouseClicked(e);
						}
					});
					editMenu.add(renameStateMenuItem);

					//---- setInitialStateMenuItem ----
					setInitialStateMenuItem.setText("setInitialState");
					setInitialStateMenuItem.setForeground(SystemColor.textInactiveText);
					setInitialStateMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							setInitialState_MouseClicked(e);
						}
					});
					editMenu.add(setInitialStateMenuItem);

					//---- unsetInitialStateMenuItem ----
					unsetInitialStateMenuItem.setText("unsetInitialState");
					unsetInitialStateMenuItem.setForeground(SystemColor.textInactiveText);
					unsetInitialStateMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							unsetInitialState_MouseClicked(e);
						}
					});
					editMenu.add(unsetInitialStateMenuItem);
				}
				menuBar1.add(editMenu);

				//======== OptionsMenu ========
				{
					OptionsMenu.setText("Options");

					//---- useBasicRenderMenuItem ----
					useBasicRenderMenuItem.setText("useBasicRenderer");
					useBasicRenderMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							useBasicRenderer_MouseClicked(e);
						}
					});
					OptionsMenu.add(useBasicRenderMenuItem);

					//---- myRenderMenuItem ----
					myRenderMenuItem.setText("useMyRenderer");
					myRenderMenuItem.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
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
		resetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				resetButton_MouseClicked(e);
			}
		});
		contentPane.add(resetButton);
		resetButton.setBounds(25, 385, 90, 25);

		//---- stepButton ----
		stepButton.setText("Step");
		stepButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
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
		displayArea.setBounds(30, 70, 590, 285);

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
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
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
	private JMenuItem unsetInitialStateMenuItem;
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
	private JLayeredPane displayArea;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	private JFileChooser fileChooser;
	private FileReader fileR = null;
	private FileWriter fileW = null;
	private FsaReaderWriter fsaRW = new FsaReaderWriter();;
	private Fsa fsa = new FsaImpl();;
}
