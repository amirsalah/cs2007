/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 7th, August 2007
=========================================================*/

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Display implements Gui{
	private Controller controller;
	private DisplayPanel myPanel = new DisplayPanel();
	
    //Connect gui to controller
    //(This method will be called before any other methods)
    public void connect(Controller controller){
    	this.controller = controller;
    }

    //Initialise the gui
    public void init(){
        MainFrame frame = new MainFrame(myPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //Called to change the displayed text
    public void setDisplay(String s){
    	myPanel.SetDisplayArea(s);
    }
    
    class MainFrame extends JFrame{
    	public MainFrame(DisplayPanel myPanel){
        	// Get screen dimensions
        	Toolkit kit = Toolkit.getDefaultToolkit();
        	Dimension screenSize = kit.getScreenSize();
        	
        	// Add a panel to the frame
        	add(myPanel);
        	
        	// The frame is located in the center of screen	
            setSize(screenSize.width/2, screenSize.height/2);
            setLocation(screenSize.width/4, screenSize.height/4);
            
            setTitle("Reaction System");
    	}
    }
    
    /* A panel in the main frame */
    class DisplayPanel extends JPanel{
    	private JTextField textArea = new JTextField("insert coin");
    	
    	public DisplayPanel(){
    		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    		
    		JButton moneyBtn = new JButton("Coin inserted");
    		JButton startBtn = new JButton("go/stop");
    		
    		add(textArea);
    		add(moneyBtn);
    		add(startBtn);
    		
    		moneyBtn.addActionListener(new
    			ActionListener(){
    				public void actionPerformed(ActionEvent e){		
    					controller.coinInserted();
    				}
    		});
    		
    		startBtn.addActionListener(new
        			ActionListener(){
        				public void actionPerformed(ActionEvent e){
        						controller.goStopPressed();
        				}
        		});
    		
    	}
    	
    	public void SetDisplayArea(String newDisplay){
    		textArea.setText(newDisplay);
    	}
    }

}



