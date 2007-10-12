import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
	
public class FsaEditor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame mainFrame = new edc3();
		mainFrame.show();
	}

}

class edc3 extends JFrame {
	public edc3() {
		initComponents();
	}

	private void LoadFSA_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void StoreFSA_MouseClicked(MouseEvent e) {
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

	private void useBasicRenderer_MouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	private void useMyRenderer_MouseClicked(MouseEvent e) {
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
		menu1 = new JMenu();
		menuItem1 = new JMenuItem();
		menuItem2 = new JMenuItem();
		separator1 = new JSeparator();
		menuItem3 = new JMenuItem();
		separator2 = new JSeparator();
		menuItem4 = new JMenuItem();
		menu2 = new JMenu();
		menuItem5 = new JMenuItem();
		menuItem6 = new JMenuItem();
		menuItem7 = new JMenuItem();
		menuItem8 = new JMenuItem();
		menuItem9 = new JMenuItem();
		menu3 = new JMenu();
		menuItem10 = new JMenuItem();
		menuItem11 = new JMenuItem();
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		textPane1 = new JTextPane();
		label2 = new JLabel();
		button1 = new JButton();
		button2 = new JButton();
		button3 = new JButton();
		label3 = new JLabel();
		layeredPane1 = new JLayeredPane();

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

				//======== menu1 ========
				{
					menu1.setText("File");
					menu1.setBackground(UIManager.getColor("Button.background"));

					//---- menuItem1 ----
					menuItem1.setText("LoadFSA...");
					menuItem1.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							LoadFSA_MouseClicked(e);
						}
					});
					menu1.add(menuItem1);

					//---- menuItem2 ----
					menuItem2.setText("StoreFSA...");
					menuItem2.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							StoreFSA_MouseClicked(e);
						}
					});
					menu1.add(menuItem2);
					menu1.add(separator1);

					//---- menuItem3 ----
					menuItem3.setText("Loadevents...");
					menuItem3.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							Loadevents_MouseClicked(e);
						}
					});
					menu1.add(menuItem3);
					menu1.add(separator2);

					//---- menuItem4 ----
					menuItem4.setText("Quit");
					menuItem4.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							Quit_MouseClicked(e);
						}
					});
					menu1.add(menuItem4);
				}
				menuBar1.add(menu1);

				//======== menu2 ========
				{
					menu2.setText("Edit");
					menu2.setBackground(UIManager.getColor("Button.background"));

					//---- menuItem5 ----
					menuItem5.setText("newState");
					menuItem5.setForeground(SystemColor.textInactiveText);
					menuItem5.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							newState_MouseClicked(e);
						}
					});
					menu2.add(menuItem5);

					//---- menuItem6 ----
					menuItem6.setText("newTransition");
					menuItem6.setForeground(SystemColor.textInactiveText);
					menuItem6.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							newTransition_MouseClicked(e);
						}
					});
					menu2.add(menuItem6);

					//---- menuItem7 ----
					menuItem7.setText("renameState");
					menuItem7.setForeground(SystemColor.textInactiveText);
					menuItem7.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							renameState_MouseClicked(e);
						}
					});
					menu2.add(menuItem7);

					//---- menuItem8 ----
					menuItem8.setText("setInitialState");
					menuItem8.setForeground(SystemColor.textInactiveText);
					menuItem8.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							setInitialState_MouseClicked(e);
						}
					});
					menu2.add(menuItem8);

					//---- menuItem9 ----
					menuItem9.setText("unsetInitialState");
					menuItem9.setForeground(SystemColor.textInactiveText);
					menuItem9.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							unsetInitialState_MouseClicked(e);
						}
					});
					menu2.add(menuItem9);
				}
				menuBar1.add(menu2);

				//======== menu3 ========
				{
					menu3.setText("Options");

					//---- menuItem10 ----
					menuItem10.setText("useBasicRenderer");
					menuItem10.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							useBasicRenderer_MouseClicked(e);
						}
					});
					menu3.add(menuItem10);

					//---- menuItem11 ----
					menuItem11.setText("useMyRenderer");
					menuItem11.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							useMyRenderer_MouseClicked(e);
						}
					});
					menu3.add(menuItem11);
				}
				menuBar1.add(menu3);
			}
			dialogPane.add(menuBar1, BorderLayout.NORTH);
		}
		contentPane.add(dialogPane);
		dialogPane.setBounds(0, 0, 411, 45);

		//---- label1 ----
		label1.setText("Messages and Output");
		contentPane.add(label1);
		label1.setBounds(new Rectangle(new Point(225, 360), label1.getPreferredSize()));

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(textPane1);
		}
		contentPane.add(scrollPane1);
		scrollPane1.setBounds(225, 385, 400, 115);

		//---- label2 ----
		label2.setText("Control Area");
		contentPane.add(label2);
		label2.setBounds(25, 360, 95, 20);

		//---- button1 ----
		button1.setText("Reset");
		button1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				resetButton_MouseClicked(e);
			}
		});
		contentPane.add(button1);
		button1.setBounds(25, 385, 65, 25);

		//---- button2 ----
		button2.setText("Step");
		button2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				stepButton_MouseClicked(e);
			}
		});
		contentPane.add(button2);
		button2.setBounds(25, 427, 65, 25);

		//---- button3 ----
		button3.setText("Play");
		button3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				playButton_MouseClicked(e);
			}
		});
		contentPane.add(button3);
		button3.setBounds(25, 469, 65, button3.getPreferredSize().height);

		//---- label3 ----
		label3.setText("Display Area");
		contentPane.add(label3);
		label3.setBounds(15, 45, 105, label3.getPreferredSize().height);
		contentPane.add(layeredPane1);
		layeredPane1.setBounds(30, 70, 590, 285);

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
	private JMenu menu1;
	private JMenuItem menuItem1;
	private JMenuItem menuItem2;
	private JSeparator separator1;
	private JMenuItem menuItem3;
	private JSeparator separator2;
	private JMenuItem menuItem4;
	private JMenu menu2;
	private JMenuItem menuItem5;
	private JMenuItem menuItem6;
	private JMenuItem menuItem7;
	private JMenuItem menuItem8;
	private JMenuItem menuItem9;
	private JMenu menu3;
	private JMenuItem menuItem10;
	private JMenuItem menuItem11;
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JTextPane textPane1;
	private JLabel label2;
	private JButton button1;
	private JButton button2;
	private JButton button3;
	private JLabel label3;
	private JLayeredPane layeredPane1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}