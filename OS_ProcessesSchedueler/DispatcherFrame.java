import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextArea;

class DispatcherFrame extends JFrame
{
    
    private DispatcherObserver dispatcher;
    private JTextArea currentTaskLabel;
    private JTextArea queueField;
    
    
    public DispatcherFrame(String name, DispatcherObserver dispatcher)
    {
        super(name);
        this.dispatcher= dispatcher;
        
        Container cp= getContentPane();
        cp.setLayout(new BorderLayout());
        
        //Create panel to hold fixed-size stuff
        JPanel northPanel= new JPanel();
        northPanel.setLayout(new GridLayout(3,1));
        cp.add(northPanel,BorderLayout.NORTH);
        
        //Create current task panel
        northPanel.add(new JLabel("Current",JLabel.CENTER));
        currentTaskLabel= new JTextArea("??Uninitialised");
        currentTaskLabel.setEditable(false);
        //??currentTaskLabel.setSize(new Dimension(100,50));
        northPanel.add(currentTaskLabel);
        
        //Create queue panel
        northPanel.add(new JLabel("Queue",JLabel.CENTER));
        
        //Create queue area
        queueField= new JTextArea("??Uninitialised");
        queueField.setEditable(false);
        queueField.setSize(new Dimension(100,250));
        cp.add(queueField,BorderLayout.CENTER);
        
        pack();
        setSize(new Dimension(250,400));
        setVisible(true);
    }
    
    
    public void reset()
    {
        
    }
    
    
    public void update()
    {
        Tcb currentTcb= dispatcher.currentTcb();
        currentTaskLabel.setText(currentTcb==null? "NULL" : 
                                 currentTcb.state().toString()+" "+currentTcb.task().name());
        
        Vector tcbQueue= dispatcher.getQueue();
        StringBuffer sb= new StringBuffer();
        for (int i=0; i<tcbQueue.size(); i++) {
            Tcb tcb= (Tcb)tcbQueue.get(i);
            sb.append(tcb.task().name());
            sb.append(" p="+tcb.priority());
            sb.append(" q="+tcb.quantum());
            sb.append("\n");
        }
        queueField.setText(sb.toString());
    }
    
    
}
