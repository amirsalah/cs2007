import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.text.BreakIterator;
import java.util.* ;
import java.awt.geom.* ;

import javax.swing.DefaultBoundedRangeModel;
import java.awt.Graphics ;
import java.lang.Math ;


/**
 * 
 */

/**
 * @author xingxing SONG
 *
 */
public class TransitionImpl implements Transition ,  DrawObj {
	
	private State 	m_stateFrom ;
	private State 	m_stateTo ;		
	
	//private String 	m_strStateFrom = "" ;
	//private String 	m_strStateTo = "" ;
	private String 	m_strName = "" ;
	private String 	m_strDesc = "" ;
	private String 	m_strOutput = "" ;
	
	private int m_iHandleFrom = 0;
	private int m_iHandleTo = 0 ;
	
	static final int FROM = 0 ;
	static final int TO = 1 ;
	
	private int m_iProperty = DRAW_TRANSITION ;
	private boolean m_bSelected = false;
	//private Arc2D.Double m_Arc2D = new Arc2D.Double(Arc2D.CHORD) ;
	
//	drawPolyline
	//int[] m_xPoints  = new int[4];
	////int[] m_yPoints  = new int[4];
	//int   m_nPoints ;

	private Line2D.Double m_Line = new Line2D.Double() ;
	// triangle
	private Line2D.Double m_LineL = new Line2D.Double() ;
	private Line2D.Double m_LineR = new Line2D.Double() ;
	public TransitionImpl(State 	stateFrom ,
	 State 		stateTo ,	
	 String 	strName ,
	 String 	strOutput,
	 String 	strDesc )
	{
		m_stateFrom = stateFrom;
		m_stateTo = stateTo;		
		m_strName = strName ;
		m_strOutput = strOutput;
		m_strDesc = strDesc ;
		
	}
    //Return the name of the event that causes this transition
    public String eventName()
    {
    	return m_strName ;
    }

    //Return the from-state of this transition
    public State fromState()
    {
    	return m_stateFrom ;
    }

    //Return the to-state of this transition
    public State toState()
    {
    	return m_stateTo ;
    }

    //Two transitions are equal if:
    //THe fromStates are the same and
    //The toStates are the same and
    //The eventName is the same
    public boolean equals(Object obj)
    {
    	if( !( obj instanceof TransitionImpl ) ){
    		return false;
    		}

    	TransitionImpl that = (TransitionImpl)obj;
    		
    	if(m_stateFrom.equals(that.m_stateFrom) &&
    		m_stateTo.equals(that.m_stateTo) &&
    		m_strName.equals(that.m_strName)  )
    	{
    		return true ;
    	}
    	else
    	{
        	return false ;	
    	}

    }

    //Return a string containing information about this state in the form:
    //"fromStateName toStateName eventName"
    public String toString()
    {
    	
    	String strfrom = new String(m_stateFrom.toString() ) ;
    	//String strItem ;
    	StringTokenizer stfrom = new StringTokenizer(strfrom); 
    	String strto = new String(m_stateTo.toString() ) ;
    	//String strItem ;
    	StringTokenizer stto = new StringTokenizer(strto); 
    	
		String strfromStateName = "";
		String strtoStateName = ""; 
		
		if (stfrom.hasMoreTokens()) {
			strfromStateName = stfrom.nextToken();
		}
		
		if (stto.hasMoreTokens()) {
			strtoStateName = stto.nextToken();
		}
		String strOutput;
		if(0 == m_strOutput.length())
		{
			strOutput = new String(".");
		}
		else
		{
			strOutput = m_strOutput;
		}
		String str = new String( strfromStateName + " " + strtoStateName  + " " + m_strName + " " + strOutput) ;
    	
    	return str ;
    }
//  used by Fsa.toString 
	public String toPropertyString(int iProperty) 
	{
		String str = new String() ;
    	String strfrom = new String(m_stateFrom.toString() ) ;
    	//String strItem ;
    	StringTokenizer stfrom = new StringTokenizer(strfrom); 
    	String strto = new String(m_stateTo.toString() ) ;
    	//String strItem ;
    	StringTokenizer stto = new StringTokenizer(strto); 
    	
		String strfromStateName = "";
		String strtoStateName = ""; 
		
		if (stfrom.hasMoreTokens()) {
			strfromStateName = stfrom.nextToken();
		}
		
		if (stto.hasMoreTokens()) {
			strtoStateName = stto.nextToken();
		}
		String strOutput;
		if(0 == m_strOutput.length())
		{
			strOutput = new String(".");
		}
		else
		{
			strOutput = m_strOutput;
		}
		str = "transition" + " " 
		+ strfromStateName + " "
		+ strtoStateName + " "
		+ m_strName + " " 
		+ strOutput + " " 
		+ m_strDesc ;
    	return str ;
	}
	
//  draw itself xingxing song added 
	public void draw(Graphics2D g2d)
	{
		Point pFromStateC = ((DrawObj)m_stateFrom).getCenter() ;
		Point pToStateC = ((DrawObj)m_stateTo).getCenter() ;
		
		Point pFrom = new Point();
		Point pTo = new Point();
		Point pL = new Point() ;
		Point pR = new Point() ;
		Rectangle2D.Double  rect = new Rectangle2D.Double() ;
		/* \
		 *  a
		 * */
		if((pFromStateC.x < pToStateC.x )&& (pFromStateC.y < pToStateC.y) ) //  \
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.CENTER_BOTTOM_R) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.LEFT_CENTER_T) ;
		}
		
		/* a 
		 *  \
		 * */
		if((pFromStateC.x > pToStateC.x )&& (pFromStateC.y > pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.CENTER_TOP_L) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.RIGHT_CENTER_B ) ;			
		}
		
		/*  ^
		 *  |
		 * */
		else if((pFromStateC.x == pToStateC.x )&& (pFromStateC.y > pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.CENTER_TOP_L) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.CENTER_BOTTOM_L) ;			
		}		
		/*  |
		 *  V
		 * */
		else if((pFromStateC.x == pToStateC.x )&& (pFromStateC.y < pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.CENTER_BOTTOM) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.CENTER_TOP) ;			
		}	
		
		/*  /
		 * a
		 * */
		else if((pFromStateC.x > pToStateC.x )&& (pFromStateC.y < pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.LEFT_CENTER_B ) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.CENTER_TOP_R ) ;			
		}	
		
		/*  a
		 * /
		 * */
		else if((pFromStateC.x < pToStateC.x )&& (pFromStateC.y > pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.RIGHT_CENTER_T ) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.CENTER_BOTTOM_L ) ;			
		}	
		
		/*  
		 * --->
		 * */
		else if((pFromStateC.x < pToStateC.x )&& (pFromStateC.y == pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.RIGHT_CENTER ) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.LEFT_CENTER ) ;			
		}	
		
		/*  
		 * <---
		 * */
		else if((pFromStateC.x > pToStateC.x )&& (pFromStateC.y == pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.LEFT_CENTER_B ) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.RIGHT_CENTER_B ) ;			
		}
		
		/*   |---|
		 *   a   |
		 *  ------ */
		else if((pFromStateC.x == pToStateC.x )&& (pFromStateC.y == pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.RIGHT_CENTER_T ) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.CENTER_TOP_R ) ;	
			
			Point p1 = new Point(pFrom.x+20 ,pFrom.y) ;
			m_Line.setLine(pFrom ,p1  ) ;
			g2d.draw(m_Line) ;

			Point p2 = new Point(p1.x ,pTo.y-20) ;
			m_Line.setLine( p1, p2  ) ;
			g2d.draw(m_Line) ;
			Point p3 = new Point(pTo.x ,p2.y) ;
			m_Line.setLine( p2, p3  ) ;
			g2d.draw(m_Line) ;
			
			if(m_bSelected)
		    {
		    	rect.setFrame(p1.getX()-3,(p1.getY()-3),6,6);
				g2d.setColor(Color.GRAY);
				g2d.fill(rect);
				g2d.setColor(Color.BLACK);
				g2d.draw(rect) ;
				
				rect.setFrame(p2.getX()-3,(p2.getY()-3),6,6);
				g2d.setColor(Color.GRAY);
				g2d.fill(rect);
				g2d.setColor(Color.BLACK);
				g2d.draw(rect) ;
		    }
			
			pFrom = p3 ;		
		}
				
		angle(pFrom,pTo, pL, pR) ;
		m_Line.setLine(pFrom , pTo ) ;
		m_LineL.setLine(pL , pTo ) ;
		m_LineR.setLine(pR , pTo ) ;
		g2d.draw(m_Line) ;
		g2d.draw(m_LineL) ;
	    g2d.draw(m_LineR) ;
	    g2d.drawString(m_strName ,pFrom.x+(pTo.x-pFrom.x)/4 ,pFrom.y+ (pTo.y-pFrom.y)/4 -2 ) ;
	    
	    if(m_bSelected)
	    {
	    	rect.setFrame(pFrom.getX()-3,(pFrom.getY()-3),6,6);
			g2d.setColor(Color.GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.BLACK);
			g2d.draw(rect) ;
			
			rect.setFrame(pTo.getX()-3,(pTo.getY()-3),6,6);
			g2d.setColor(Color.GRAY);
			g2d.fill(rect);
			g2d.setColor(Color.BLACK);
			g2d.draw(rect) ;
	    }
	    
	}
	void angle(Point pF,Point pT,Point pL,Point pR)
	{
		int x1 = pF.x ;
		int y1 = pF.y ;
		int x2 = pT.x ;
		int y2 = pT.y ;
		
		int S = 7 ;
		double L = Math.pow(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)+0.0 ),0.5) ;
			
			
		pL.x = (int)(x2 - (S/L)*(x2-x1) - (S/L)*(y2-y1));
		pL.y = (int)(y2 - (S/L)*(y2-y1) + (S/L)*(x2-x1));

		pR.x = (int)(x2 - (S/L)*(x2-x1) + (S/L)*(y2-y1));
		pR.y = (int)(y2 - (S/L)*(y2-y1) - (S/L)*(x2-x1));	
	}
//	 get the center of the obj 
	public Point getCenter() 
	{   // no use 
		return new Point(0 , 0 ) ;
	}
	
	public Point getHandle(int iHandle)
	{
		Point p = new Point();
		switch(iHandle){
		case FROM :
			p = ((DrawObj)m_stateFrom).getHandle(m_iHandleFrom) ;
			break;
		case TO :
			p = ((DrawObj)m_stateTo).getHandle(m_iHandleTo) ;
			break ;
		default :
			break ;	
		}
		return p ;
	}
	
//	 set the kind of the object
//	 set the kind of the object
	public void setProperty(int iProperty)
	{
		switch(iProperty)
		{
		default:
			break  ;
		case DRAW_TRANSITION_SELECTED:
			m_bSelected = true;
			break;	
		case DRAW_TRANSITION_UNSELECTED:
			m_bSelected = false;
			break;
		}
		
	}
	
	// Return the output of this transition.
   	// Return an empty string if there is no output.
    public String output()
    {
    	return m_strOutput ;
    }
    
	// Tests if a specified coordinate is inside the boundary of this DrawObj
	public boolean contains(int x, int y) 
	{
		//return m_Line.contains(x,y);
		return false;
	}
	
//	 Test if it intersects the rect region selection 
	public boolean intersects(Rectangle2D.Double rect)
	{
		boolean bSelected = false;
		Point pFromStateC = ((DrawObj)m_stateFrom).getCenter() ;
		Point pToStateC = ((DrawObj)m_stateTo).getCenter() ;
		
		Point pFrom = new Point();
		Point pTo = new Point();
		Point pL = new Point() ;
		Point pR = new Point() ;
		Line2D.Double line = new Line2D.Double();
		/*   |---|
		 *   a   |
		 *  ------ */
		if((pFromStateC.x == pToStateC.x )&& (pFromStateC.y == pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.RIGHT_CENTER_T ) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.CENTER_TOP_R ) ;	
			
			Point p1 = new Point(pFrom.x+20 ,pFrom.y) ;
			line.setLine(pFrom ,p1  ) ;
			bSelected = rect.intersectsLine(line);			
			Point p2 = new Point(p1.x ,pTo.y-20) ;
			line.setLine( p1, p2  ) ;
			bSelected = (rect.intersectsLine(line)|| m_bSelected);
			Point p3 = new Point(pTo.x ,p2.y) ;
			line.setLine( p2, p3  ) ;
			bSelected = (rect.intersectsLine(line)|| m_bSelected);
			pFrom = p3 ;		
			bSelected = (rect.intersectsLine(m_Line)|| m_bSelected);
		}		
		else
		{
			bSelected = rect.intersectsLine(m_Line);
		}

		return bSelected;
	}
	
	//	 add the object to the selection list
	public void select(int x, int y, java.util.List<DrawObj> lstCurDrawObj)
	{
		return;
	}
//	 add the object to the selection list
	public void select(Rectangle2D.Double rect, java.util.List<DrawObj> lstCurDrawObj)
	{
		m_bSelected = false;
		Point pFromStateC = ((DrawObj)m_stateFrom).getCenter() ;
		Point pToStateC = ((DrawObj)m_stateTo).getCenter() ;
		
		Point pFrom = new Point();
		Point pTo = new Point();
		Point pL = new Point() ;
		Point pR = new Point() ;
		Line2D.Double line = new Line2D.Double();
		/*   |---|
		 *   a   |
		 *  ------ */
		if((pFromStateC.x == pToStateC.x )&& (pFromStateC.y == pToStateC.y) )
		{
			pFrom =  ((DrawObj)m_stateFrom).getHandle(DrawObj.RIGHT_CENTER_T ) ;
			pTo =  ((DrawObj)m_stateTo).getHandle(DrawObj.CENTER_TOP_R ) ;	
			
			Point p1 = new Point(pFrom.x+20 ,pFrom.y) ;
			line.setLine(pFrom ,p1  ) ;
			m_bSelected = rect.intersectsLine(line);			
			Point p2 = new Point(p1.x ,pTo.y-20) ;
			line.setLine( p1, p2  ) ;
			m_bSelected = (rect.intersectsLine(line)|| m_bSelected);
			Point p3 = new Point(pTo.x ,p2.y) ;
			line.setLine( p2, p3  ) ;
			m_bSelected = (rect.intersectsLine(line)|| m_bSelected);
			pFrom = p3 ;		
			m_bSelected = (rect.intersectsLine(m_Line)|| m_bSelected);
		}		
		else
		{
			m_bSelected = rect.intersectsLine(m_Line);
		}
		
		if(m_bSelected)
		{
			lstCurDrawObj.add(this);
		}
		
		
	}
	
	// deselect the object
	public void deselect()
	{
		m_bSelected = false;
	}
}


