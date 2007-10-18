

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.*;
import java.util.Set;
import java.awt.Graphics;


import javax.swing.*;        // For JPanel, etc.
import java.awt.*;           // For Graphics, Color etc.
import java.awt.geom.*;      // For Rectangle2D, etc.
import java.awt.event.*; 

/**
 * 
 */

/**
 * @author xingxing SONG
 *
 */
public class StateImpl implements State , DrawObj {
	private int 	m_ihSize	= 80 ;
	private int 	m_ivSize	= 40 ;
	private String 	m_strName = "" ;
	private int 	m_ihPos	= 0 ;
	private int		m_ivPos	= 0 ;
	private String 	m_strDesc	= "" ;
	private Set<Transition> setTransition  = new HashSet<Transition>();
	
	private int m_iProperty = DRAW_STATE ;
	private int m_iCurCounter = 0 ;
	private boolean m_bSelected = false ;
	private RoundRectangle2D.Double m_outer =  new RoundRectangle2D.Double(10,10,20,20,20.0,15.0 ) ;
	//private Ellipse2D.Double m_outer =  new Ellipse2D.Double() ;
		
	public StateImpl(String strName) {
		m_strName = strName;
		m_ihPos = 0;
		m_ivPos = 0;
		m_strDesc = "";
	}
	
	public StateImpl(String strName ,
					int 	ihPos,
					int		ivPos ,
					String 	strDesc)
	{
		m_strName = strName ;
		m_ihPos	= ihPos ;
		m_ivPos	= ivPos ;
		m_strDesc	= strDesc ;
	}
	//Return a list of all transitions from this state
    //If there are no transitions, return an empty list
    public Set<Transition> getTransitions(){
    	//Set<Transition> set =  new HashSet<Transition>() ;
    	return setTransition ;
    }
    //Two states are equal if:
    //The stateNames are the same
    public boolean equals(Object obj)
    {
    	if( !( obj instanceof StateImpl ) ){
    		return false;
    		}

    		StateImpl that = (StateImpl)obj;
    		
    		return this.m_strName.equals(that.m_strName);

    }

    //Return a string containing information about this state in the form:
    //"stateName xPos yPos"  (without the quotes, of course!)
    public String toString()
    {
    	String str = new String() ;
    	str = m_strName + " " + String.valueOf(m_ihPos) + " " + String.valueOf(m_ivPos) ;
    	
    	return str ;
    }
  
//  used by Fsa.toString 
	public String toPropertyString(int iProperty) 
	{
		
		String str = new String() ;
		
		switch(iProperty ){
		case DRAW_STATE :
			str = "state" + " " + m_strName + " "+ String.valueOf(m_ihPos) + " " 
	    	+ String.valueOf(m_ivPos)+ " " 
	    	 + m_strDesc ;
			break ;
		case DRAW_STATE_INIT :
			str = "initial" + " "
	    	+ m_strName ;
			break ;
		default:
			break;
		}
    	
    	return str ;
	}
	
//  draw itself star song added 
	public void draw(Graphics2D g2d)
	{
				
		m_outer.setFrame(m_ihPos-(m_ihSize/2.0),m_ivPos-(m_ivSize/2.0),
				m_ihSize ,m_ivSize);
		switch(m_iProperty ){
		case DRAW_STATE :
			g2d.setColor(Color.YELLOW);
			break ;
		case DRAW_STATE_INIT :
			g2d.setColor(Color.WHITE);
			break ;
		default:
			g2d.setColor(Color.YELLOW);
			break;
		}
		
		g2d.fill(m_outer);
		if(0 != m_iCurCounter)
		{
			g2d.setColor(Color.GREEN);
		}
		else
		{
			g2d.setColor(Color.BLACK);
		}
		
		//g2d.setColor(Color.BLACK);
		g2d.draw(m_outer) ;
		
		if(0 != m_iCurCounter)
		{
			RoundRectangle2D.Double outer =  new RoundRectangle2D.Double(10,10,20,20,20.0,15.0 ) ;
			outer.setFrame(m_ihPos-(m_ihSize/2.0)-2,m_ivPos-(m_ivSize/2.0)-2,
					m_ihSize+4 ,m_ivSize+4 );
			g2d.setColor(Color.GREEN);
			g2d.draw(outer) ;
			//g2d.setColor(Color.BLACK);
		}
		g2d.setColor(Color.BLACK);
		g2d.drawString(m_strName ,m_ihPos-(m_ihSize/6),m_ivPos ) ;
		
		if(m_bSelected)
		{
			Rectangle2D.Double  rect = new Rectangle2D.Double() ;
			rect.setFrame(getHandle(LEFT_TOP).getX()-3,(getHandle(LEFT_TOP).getY()-3),6,6);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.BLACK);
			g2d.draw(rect) ;
			rect.setFrame(getHandle(CENTER_TOP).getX()-3,(getHandle(CENTER_TOP).getY()-3),6,6);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.BLACK);
			g2d.draw(rect) ;
			rect.setFrame(getHandle(RIGHT_TOP).getX()-3,(getHandle(RIGHT_TOP).getY()-3),6,6);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.BLACK);
			g2d.draw(rect) ;
			rect.setFrame(getHandle(LEFT_BOTTOM).getX()-3,(getHandle(LEFT_BOTTOM).getY()-3),6,6);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.BLACK);
			g2d.draw(rect) ;
			rect.setFrame(getHandle(CENTER_BOTTOM).getX()-3,(getHandle(CENTER_BOTTOM).getY()-3),6,6);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.BLACK);
			g2d.draw(rect) ;
			rect.setFrame(getHandle(RIGHT_BOTTOM).getX()-3,(getHandle(RIGHT_BOTTOM).getY()-3),6,6);
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.BLACK);
			g2d.draw(rect) ;
		}
		Iterator<Transition> iterator = setTransition.iterator() ;
		while(iterator.hasNext())
		{
			DrawObj obj = (DrawObj)iterator.next() ;
			obj.draw(g2d) ;
		}
		
	}
	
//	 get the center of the obj 
	public Point getCenter() 
	{
		return new Point(m_ihPos , m_ivPos ) ;
	}
	
//	 returns logical coords of center of handle
	public Point getHandle(int iHandle)
	{
		
		int x = 0, y = 0, xCenter, yCenter;

		// this gets the center regardless of left/right and top/bottom ordering
		xCenter = m_ihPos ;//m_position.left + m_position.Width() / 2;
		yCenter = m_ivPos ;//m_position.top + m_position.Height() / 2;
		int left =  m_ihPos - (m_ihSize/2) ;
		int top = m_ivPos - (m_ivSize/2) ;
		int right = m_ihPos + (m_ihSize/2) ;
		int bottom = m_ivPos + (m_ivSize/2) ;
		
		
		switch (iHandle)
		{
		default:
			break  ;

		case LEFT_TOP:
			x = left;
			y = top;
			break;
		case CENTER_TOP_L:
			x = (xCenter+left)/2;
			y = top;
			break;
		case CENTER_TOP:
			x = xCenter;
			y = top;
			break;
		case CENTER_TOP_R:
			x = (xCenter+right)/2;
			y = top;
			break;
		case RIGHT_TOP:
			x = right;
			y = top;
			break;

		case RIGHT_CENTER_T:
			x = right;
			y = (yCenter+top)/2;
			break;
			
		case RIGHT_CENTER:
			x = right;
			y = yCenter;
			break;
			
		case RIGHT_CENTER_B:
			x = right;
			y = (yCenter+bottom)/2;
			break;

		case RIGHT_BOTTOM:
			x = right;
			y = bottom;
			break;

		case CENTER_BOTTOM_L:
			x = (xCenter+left)/2;
			y = bottom;
			break;
			
		case CENTER_BOTTOM:
			x = xCenter;
			y = bottom;
			break;
			
		case CENTER_BOTTOM_R:
			x = (xCenter+right)/2;
			y = bottom;
			break;

		case LEFT_BOTTOM:
			x = left;
			y = bottom;
			break;

		case LEFT_CENTER_T:
			x = left;
			y = (yCenter+top)/2;
			break;
			
		case LEFT_CENTER:
			x = left;
			y = yCenter;
			break;
			
		case LEFT_CENTER_B:
			x = left;
			y = (yCenter+bottom)/2;
			break;
		}

		return new Point(x, y);
	}
	
//	 set the kind of the object
	public void setProperty(int iProperty)
	{
		switch(iProperty)
		{
		default:
			break  ;

		case DRAW_STATE:
		case DRAW_STATE_INIT:
			m_iProperty = iProperty ;
			break;
		case DRAW_STATE_CURRENT:
			m_iCurCounter++;
			break;
		case DRAW_STATE_HANDLED:
			m_iCurCounter = 0;
			break;
		case DRAW_STATE_SELECTED:
			m_bSelected = true;
			Iterator<Transition> iterator = setTransition.iterator() ;
			while(iterator.hasNext())
			{
				DrawObj obj = (DrawObj)iterator.next() ;
				obj.setProperty(DrawObj.DRAW_TRANSITION_SELECTED);
			}
			
			break;	
		case DRAW_STATE_UNSELECTED:
			m_bSelected = false;
			Iterator<Transition> iteratorTran = setTransition.iterator() ;
			while(iteratorTran.hasNext())
			{
				DrawObj obj = (DrawObj)iteratorTran.next() ;
				obj.setProperty(DrawObj.DRAW_TRANSITION_UNSELECTED);
			}
			
			break;
		}
		
	}
	
    // Move the (x,y) position of this state 
    // by this much from its current position
    public void moveBy( int x, int y )
    {
    	m_ihPos = m_ihPos + x;
    	m_ivPos = m_ivPos + y;
    }
    
	// Tests if a specified coordinate is inside the boundary of this DrawObj
	public boolean contains(int x, int y) 
	{
		boolean bSelected = m_outer.contains(x,y);
		
		return bSelected;
		
	}
	
//	 Test if it intersects the rect region selection 
	public boolean intersects(Rectangle2D.Double rect)
	{
		boolean bSelected = m_outer.intersects(rect.x,rect.y,
				rect.width, rect.height);
		Iterator<Transition> iterator = setTransition.iterator();
		while(iterator.hasNext())
		{
			DrawObj obj = (DrawObj)iterator.next();
			obj.intersects( rect);
		}
		return bSelected;
	}
//	 add the object to the selection list
	public void select(int x, int y, java.util.List<DrawObj> lstCurDrawObj)
	{
		m_bSelected = m_outer.contains(x,y);
		if(m_bSelected)
		{
			lstCurDrawObj.add(this);
		}
	}

	public void select(Rectangle2D.Double rect, java.util.List<DrawObj> lstCurDrawObj)
	{
		m_bSelected = m_outer.intersects(rect.x,rect.y,
				rect.width, rect.height);
		if(m_bSelected)
		{
			lstCurDrawObj.add(this);
		}
		
		Iterator<Transition> iterator = setTransition.iterator();
		while(iterator.hasNext())
		{
			DrawObj obj = (DrawObj)iterator.next();
			obj.select( rect, lstCurDrawObj);
		}
		
	}
	
	// deselect the object
	public void deselect()
	{
		m_bSelected = false;
	}
}
