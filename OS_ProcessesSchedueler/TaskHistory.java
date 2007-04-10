////////////////////////////////////////////////////////////////////////////////
//
//Task hsitory records the execution history of a task
//
////////////////////////////////////////////////////////////////////////////////
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JPanel;


class TaskHistory extends JPanel
{
    private Vector<Object> startTimes;
    private Vector<Object> endTimes;
    private Dimension dimension;
    
    public TaskHistory()
    {
        startTimes=new Vector<Object>();
        endTimes= new Vector<Object>();
        dimension= new Dimension();
        
        setPreferredSize(new Dimension(400,20));
    }
    
    public void add(int startTime, int endTime)
    {
        startTimes.add(new Integer(startTime));
        endTimes.add(new Integer(endTime));
        //??
        repaint(50L);
    }
    
    
    public void paintComponent(Graphics g)
    {
        this.getSize(dimension);
        int inset= 2;
        int height= (int)dimension.getHeight()-2*inset;
        for (int i=0; i<endTimes.size(); i++) {
            int startTime= ((Integer)startTimes.get(i)).intValue();
            int endTime= ((Integer)endTimes.get(i)).intValue();
            
            g.fillRect(startTime,inset,endTime-startTime,height);
        }
    }
}
