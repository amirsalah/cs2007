import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.Timer;

class OsimFrame extends JFrame
implements ActionListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Osim osim;
    private boolean running;
    private Timer timer;
    
    private JComboBox tickrateMenu;
    private JTextArea timeArea;
    private JTextArea pendingArea;
    private JTextArea liveArea;
    private JButton resetButton;
    private JButton stepButton;
    private JButton runButton;
    private JButton quitButton;
    
    
    public OsimFrame(String name, Osim osim)
    {
        super(name);
        this.osim= osim;
        running= false;
        
        //Create repeating timer
        timer= new Timer(100,this);
        timer.setRepeats(true);
        
        Container cp= getContentPane();
        cp.setLayout(new GridLayout(8,2));
        
        //Add Timing menu
        cp.add(new JLabel("Tickrate"));
        tickrateMenu= new JComboBox();
        tickrateMenu.addActionListener(this);
        tickrateMenu.addItem("100/sec");
        tickrateMenu.addItem("10/sec");
        tickrateMenu.addItem("1/sec");
        cp.add(tickrateMenu);
        
        //Add time
        cp.add(new JLabel("Time"));
        
        //Add TIME area
        timeArea= new JTextArea();
        cp.add(timeArea);
        
        //Add time
        cp.add(new JLabel("Pending"));
        
        //Add PENDING area
        pendingArea= new JTextArea();
        cp.add(pendingArea);
        
        //Add time
        cp.add(new JLabel("In progress"));
        
        //Add LIVE area
        liveArea= new JTextArea();
        cp.add(liveArea);
        
        //Add spacer
        cp.add(new JLabel(" "));
        
        //Add RESET button
        resetButton= new JButton("Reset");
        resetButton.addActionListener(this);
        cp.add(resetButton);
        
        //Add spacer
        cp.add(new JLabel(" "));
        
        //Add STEP button
        stepButton= new JButton("Step");
        stepButton.addActionListener(this);
        cp.add(stepButton);
        
        //Add spacer
        cp.add(new JLabel(" "));
        
        //Add Run button
        runButton= new JButton("Run");
        runButton.addActionListener(this);
        cp.add(runButton);
        
        //Add spacer
        cp.add(new JLabel(" "));
        
        //Add QUIT button
        quitButton= new JButton("Quit");
        quitButton.addActionListener(this);
        cp.add(quitButton);
        
        pack();
        setVisible(true);
        
        //Set default timer value
        tickrateMenu.setSelectedIndex(1);
        timer.start();
    }
    
    
    public void actionPerformed(ActionEvent ae)
    {
        Object source= ae.getSource();
        
        if (source==tickrateMenu) {
            //TICKRATE changed
            switch (tickrateMenu.getSelectedIndex()) {
                case 0:
                    timer.setDelay(10);
                    break;
                    
                case 1:
                    timer.setDelay(100);
                    break;
                    
                case 2:
                    timer.setDelay(1000);
                    break;
                    
                default:
                    throw new RuntimeException("impossible error");
            }
            
            return;
        }
        
        if (source==resetButton) {
            //RESET pressed
            osim.reset();
            running= false;
            runButton.setText("Run");
            return;
        }
        
        if (source==stepButton) {
            //STEP pressed
            running= false;
            osim.stop();
            
            runButton.setText("Run");
            osim.step();	    
            return;
        }
        
        if (source==runButton) {
            //RUN pressed
            if (running) {
                running= false;
                runButton.setText("Run");
                osim.stop();
                return;
            }else{
                running= true;
                runButton.setText("Stop");
                osim.start();
                return;
            }
        }
        
        if (source==quitButton) {
            //QUIT pressed
            osim.exit();
            return;
        }
        
        if (source==timer) {
            if (running) {
                osim.step();
            }
            return;
        }
        
        throw new RuntimeException("Impossible error");
    }
    
    public void updateTime(int time)
    {
        timeArea.setText(Integer.toString(time));
    }
    
    
    public void updatePending(int nrJobs)
    {
        pendingArea.setText(Integer.toString(nrJobs));
    }
    
    
    public void updateLive(int nrJobs)
    {
        liveArea.setText(Integer.toString(nrJobs));
    }
    
    
}
