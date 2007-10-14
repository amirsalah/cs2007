import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

import javax.swing.*;

public class MyRenderer implements FsaRenderer {
	// parameters of the squre (states)
	private int squareHeight = 40;
	private int squareWidth = 40;
	private int xPos = 0;
	private int yPos = 0;
	private int arcWidth = 20;
	private int arcHeight = 15;
	private String stateName = null;

    private int barb = 20;
    private double phi = Math.toRadians(20);
    
	public MyRenderer() {
		// Initialize the square

	}

	// Draw an initial transistion on state s
	// Returns the Shape that was drawn
	public Shape drawInitialTransition(Graphics2D gra2d, State s,
			boolean isSelected) {
		xPos = s.getXCoord();
		yPos = s.getYCoord();
		// Ellipse indicates an intial state
		Ellipse2D.Double initStateShape = new Ellipse2D.Double();
		initStateShape.setFrame(xPos, yPos, squareWidth, squareHeight);
		
		// Set different color depends on whether the state is selected
		if(isSelected){
			gra2d.setColor(Color.ORANGE);
		}else{
			gra2d.setColor(Color.BLUE);
		}
		
		gra2d.fill(initStateShape);
		gra2d.draw(initStateShape);
		
		// Draw state name
		gra2d.setColor(Color.BLACK);
		stateName = s.getName();
		gra2d.drawString(stateName, xPos+2, yPos+2);
		
		return initStateShape;
	}

	// Draw a state s
	// isSelected will be true if the state is currently selected
	// isCurrent will be true if the state is a current state
	// Returns the Shape that was drawn
	public Shape drawState(Graphics2D gra2d, State s, boolean isSelected,
			boolean isCurrent) {
		xPos = s.getXCoord();
		yPos = s.getYCoord();
		// Ellipse indicates an intial state
		Shape stateShape = new RoundRectangle2D.Double();
		((RoundRectangle2D)stateShape).setRoundRect(xPos, yPos, squareWidth, squareHeight, arcWidth, arcHeight);

		// Set different color depends on whether the state is selected 
		// <color setting is a state machine in Java graphics, as OpenGL>
		if(isSelected){
			gra2d.setColor(Color.ORANGE);
		}else{
			gra2d.setColor(Color.BLUE);
		}
		
		// Set larger square if it is current state
		if(isCurrent){
			((RoundRectangle2D)stateShape).setRoundRect(xPos, yPos, squareWidth*2, squareHeight*2, arcWidth*2, arcHeight*2);
		}
		
		gra2d.fill(stateShape);
		gra2d.draw(stateShape);
		
		// Draw state name
		gra2d.setColor(Color.BLACK);
		stateName = s.getName();
		gra2d.drawString(stateName, xPos+2, yPos+2);
		
		return stateShape;
	}

	// Draw a a transition t
	// Since there can be multiple transistions form one state to another,
	// the multiplicity parameter indicates if this is the 1st, 2nd, 3rd, etc
	// such transition
	// isSelected will be true if the transition is currently selected
	// Returns the Shape that was drawn
	public Shape drawTransition(Graphics2D gra2d, Transition t,
			int multiplicity, boolean isSelected) {
		State from = t.fromState();
		State to = t.toState();
		
		// Select different points depends on its multiplicity
		int xFrom = from.getXCoord() + multiplicity *2;
		int yFrom = from.getYCoord() + multiplicity *2;
		int xTo = to.getXCoord() + multiplicity *2;
		int yTo = to.getYCoord() + multiplicity *2;
		
		double theta = Math.atan2(yTo - yFrom, xTo - xFrom);
        Point2D.Double p1 = new Point2D.Double(xFrom, yFrom);
        Point2D.Double p2 = new Point2D.Double(xTo, yTo);
        Line2D.Double transitionLine = new Line2D.Double(p1, p2);
        
        GeneralPath path = new GeneralPath(transitionLine);
        
        // Add an arrow head at p2.
        double x = p2.x + barb*Math.cos(theta+Math.PI-phi);
        double y = p2.y + barb*Math.sin(theta+Math.PI-phi);
        path.moveTo((float)x, (float)y);
        path.lineTo((float)p2.x, (float)p2.y);
        x = p2.x + barb*Math.cos(theta+Math.PI+phi);
        y = p2.y + barb*Math.sin(theta+Math.PI+phi);
        path.lineTo((float)x, (float)y);
        
		// Set different color depends on whether the state is selected
		if(isSelected){
			gra2d.setColor(Color.ORANGE);
		}else{
			gra2d.setColor(Color.BLUE);
		}
		
		gra2d.draw(path);
		
		return path;
	}

}
