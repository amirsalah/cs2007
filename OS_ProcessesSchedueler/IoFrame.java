import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class IoFrame extends JFrame
{
    private JTextArea ioField;
    private IoDevice ioDevice;
    
    
    public IoFrame(IoDevice ioDevice)
    {
        super("Io queue");
        this.ioDevice= ioDevice;
        
        Container cp= getContentPane();
        cp.setLayout(new FlowLayout());
        
        //Create I/O queue area
        ioField= new JTextArea(" ");
        ioField.setEditable(false);
        cp.add(ioField);
        
        pack();
        setSize(new Dimension(150,400));
        setVisible(true);
    }
    
    
    public void reset()
    {
        
    }
    
    
    public void update()
    {
        StringBuffer sb= new StringBuffer();
        int p= ioDevice.head;
        int nrEntries= ioDevice.nrEntries;
        if (nrEntries>0) {
            int i=0;
            for (;;){
                int delta= ioDevice.deltas[p];
                Tcb tcb= (Tcb)ioDevice.tcbs[p];
                
                sb.append(Integer.toString(delta));
                sb.append(" ");
                sb.append(tcb.task().name());
                sb.append(" ");
                sb.append(tcb.state().toString());
                i++;
                if (i>=nrEntries) break;
                sb.append("\n");
                
                p++;
                if (p>=ioDevice.MAXENTRIES) {
                    p= 0;
                }
            }
        }
        ioField.setText(sb.toString());
    }
    
    
}
