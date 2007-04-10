import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
//import javax.swing.JTextField;

class CpuFrame extends JFrame
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private JTextField tf;
    private Hashtable<Object,Object> historyTable;
    private int time;
    private Container cp;
    
    private JPanel westPanel;
    private JPanel centerPanel;
    
    public CpuFrame(String name)
    {
        super(name);
        
        cp= getContentPane();
        cp.setLayout(new BorderLayout());
        
        westPanel= new JPanel();
        westPanel.setLayout(new GridLayout(0,1));
        cp.add(westPanel,BorderLayout.WEST);
        
        centerPanel= new JPanel();
        centerPanel.setLayout(new GridLayout(0,1));
        JScrollPane jsp= new JScrollPane(centerPanel,
                                         JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cp.add(jsp,BorderLayout.CENTER);
        //??	cp.add(centerPanel,BorderLayout.CENTER);
        centerPanel.setSize(5000,200);
        
        pack();
        setSize(new Dimension(400,200));
        setVisible(true);
        
        reset();
    }
    
    
    public void reset()
    {
        historyTable= new Hashtable<Object,Object>();
        time= 0;
        //Clear existing data
        westPanel.removeAll();
        centerPanel.removeAll();
        
        //And update the screen
        repaint();
    }
    
    
    public void add(Task t, int duration)
    {
        if (t==null) {
            //Ignore it
            return;
        }
        
        //Find previous information
        TaskHistory th= (TaskHistory)historyTable.get(t);
        
        if (th==null) {
            //We have a new task...
            th= new TaskHistory();
            historyTable.put(t,th);
            
            westPanel.add(new JLabel(t.name()));
            centerPanel.add(th);
            
            //centerPanel.repaint();
            
            //Make it all visible...
            //??	    pack();//??repaint();
        }
        
        th.add(time,time+duration);
        time= time+duration;
        
        //	Dimension dimension=centerPanel.getSize();
        //	dimension.setSize(time,dimension.getHeight());
        //	centerPanel.setSize(dimension);
    }
}
