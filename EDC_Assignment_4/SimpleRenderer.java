import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class SimpleRenderer
  implements FsaRenderer
{	    
    private static final int RADIUS= 40;

    public SimpleRenderer()
    {
	//Nothing
    }


    public Shape  drawInitialTransition(Graphics2D g, State s, boolean isSelected)
    {
	int x1= s.getXCoord();
	int y1= s.getYCoord();
	Shape line= new Line2D.Double(x1,y1-RADIUS,x1,y1+RADIUS);

	g.setColor(Color.BLACK);
	g.draw(line);

	return line;
    }


    public Shape drawState(Graphics2D g, State s, boolean isSelected, 
      boolean isCurrent) 
    {
	int topX= s.getXCoord()-RADIUS;
	int topY= s.getYCoord()-RADIUS;
	int diam= 2*RADIUS;
	Shape circle= new Ellipse2D.Double(topX,topY,diam,diam);
	
	g.setColor(Color.BLACK);
	g.draw(circle);

	//??Should be able to centre better...
	int textX= s.getXCoord();
	int textY= s.getYCoord();
	g.drawString(s.getName(),textX,textY);

	return circle;
    }

    
   public Shape drawTransition(Graphics2D g, Transition t, int multiplicity, 
      boolean isSelected) 
    { 
	State from= t.fromState();
	State to= t.toState();
	int x1= from.getXCoord();
	int y1= from.getYCoord();
	int x2= to.getXCoord();
	int y2= to.getYCoord();

	//Midpoint of line of centres
	int mx= (x1+x2)/2;
	int my= (y1+y2)/2;

	Shape line= new Line2D.Double(x1,y1,x2,y2);

	g.setColor(Color.BLACK);
	g.draw(line);

	//Draw label
	g.drawString(t.eventName()+": "+t.output(),mx,my);

	return line;
    }  
}
