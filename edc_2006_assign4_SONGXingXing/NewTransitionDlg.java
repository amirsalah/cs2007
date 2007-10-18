/**
 * 
 */
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * @author Xingxing SONG
 *
 */
public class NewTransitionDlg extends JDialog {

	
	public NewTransitionDlg(JFrame owner, boolean modal)
	{
		super(owner,"New transition",modal);
		this.setSize(200,200);
		
		JPanel panelDispaly = new JPanel();
		
		JPanel panelControl = new JPanel();
		panelControl.add(createOKButton());
		panelControl.add(createCancelButton());
		 
		JPanel items = new JPanel();
		items.setLayout( new BorderLayout() );
		items.add( panelDispaly, BorderLayout.CENTER );
		items.add( panelControl, BorderLayout.SOUTH );
	
		this.setContentPane(items);
        this.setVisible(true);
        
	}
	
	private JButton createOKButton()
	{
		JButton btnOK = new JButton("OK");
		class ButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				
			}
		}
		ActionListener listener  = new ButtonListener();
		btnOK.addActionListener(listener);
		return btnOK;	
	}

	private JButton createCancelButton()
	{
		JButton btnCancel = new JButton("Cancel");
		class ButtonListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				
			}
		}
		ActionListener listener  = new ButtonListener();
		btnCancel.addActionListener(listener);
		return btnCancel;	
	}
	
}
