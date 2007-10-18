/**
 * 
 */
import java.awt.*;           // For Graphics, Color etc.
import java.awt.geom.*;      // For Rectangle2D, etc.

/**
 * @author Xingxing SONG
 */
interface DrawObj {
	
	static final public int DRAW_STATE = 0 ;
	static final public int DRAW_STATE_INIT = 0x1 ;
	static final public int DRAW_STATE_CURRENT = 0x1 << 1 ;	
	static final public int DRAW_STATE_HANDLED = 0x1 << 2 ;
	static final public int DRAW_STATE_SELECTED = 0x1 << 3 ;
	static final public int DRAW_STATE_UNSELECTED = 0x1 << 4 ;
	static final public int DRAW_TRANSITION = 0x1 << 5 ;	
	static final public int DRAW_TRANSITION_SELECTED = 0x1 << 6 ;	
	static final public int DRAW_TRANSITION_UNSELECTED = 0x1 << 7 ;
	
		
	// set the kind of the object
	public void setProperty(int iProperty) ;
	
	// used by Fsa.toString 
	public String toPropertyString(int iProperty) ;

	// draw itself star song added 
	public void draw(Graphics2D g2d) ;
	
	// get the handle , the end 
	public Point getHandle(int iHandle) ;
	
	// get the center of the obj 
	public Point getCenter() ;
	// Tests if a specified coordinate is inside the boundary of this DrawObj
	public boolean contains(int x, int y) ;
	
	// Test if it intersects the rect region selection 
	public boolean intersects(Rectangle2D.Double rect);
	
	// add the object to the selection list
	public void select(Rectangle2D.Double rect, java.util.List<DrawObj> lstCurDrawObj);
	
	// add the object to the selection list
	public void select(int x, int y, java.util.List<DrawObj> lstCurDrawObj);
	
	// deselect the object
	public void deselect();
	
	static final public int LEFT_TOP = 0 ;
	static final public int CENTER_TOP_L = 101 ;
	static final public int CENTER_TOP = 1 ;
	static final public int CENTER_TOP_R = 102 ;
	static final public int RIGHT_TOP = 2 ;
	static final public int RIGHT_CENTER_T = 301 ;
	static final public int RIGHT_CENTER = 3 ;
	static final public int RIGHT_CENTER_B = 302 ;
	static final public int RIGHT_BOTTOM = 4 ;
	static final public int CENTER_BOTTOM_L = 501 ;
	static final public int CENTER_BOTTOM = 5 ;
	static final public int CENTER_BOTTOM_R = 502 ;
	static final public int LEFT_BOTTOM = 6 ;
	static final public int LEFT_CENTER_T = 701 ;
	static final public int LEFT_CENTER = 7 ;
	static final public int LEFT_CENTER_B = 702 ;
}
