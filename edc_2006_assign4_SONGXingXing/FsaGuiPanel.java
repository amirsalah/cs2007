/**
 * 
 */
import javax.swing.*;        // For JPanel, etc.
import java.awt.*;           // For Graphics, Color etc.
import java.awt.geom.*;      // For Rectangle2D, etc.
import java.awt.event.*;           
import java.util.*;
import java.util.List;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

// For Graphics, Color etc.

/**
 * @author xingxing SONG
 *
 */
public class FsaGuiPanel extends JPanel{

	private boolean m_bEnable  = false ;
	boolean getEnable(){ return m_bEnable ; }
	void setEnable(boolean bEnable){ m_bEnable = bEnable ; }
	
	static final public int VIEW_STATE_SELECTION = 0 ;
	static final public int VIEW_STATE_REGIONSEL = 0x1 ;
	static final public int VIEW_STATE_REGIONSELED = 0x2 ;
	static final public int VIEW_STATE_NEWSTATE = 0x3 ;	
	static final public int VIEW_STATE_NEWTRANSTION = 0x4 ;	
	
	private int m_iState  = VIEW_STATE_SELECTION ;
	void setState(int iState)
	{
		this.m_iState = iState ;
	}
	
	int getState()
	{
		return this.m_iState  ;
	}	
	private Rectangle2D.Double m_rectSelection = new Rectangle2D.Double();
	private Line2D.Double m_lineNewTransition = new Line2D.Double();
	State newTranFrom;
	State newTranTo;
	private FsaGui m_fsaGui;
	public void setFsaGui(FsaGui fsaGui)
	{
		this.m_fsaGui = fsaGui ;
	}
	
	
	private Fsa m_fsa ; 
	
	public void setFsa(Fsa fsa)
	{
		this.m_fsa = fsa ;
	}
	
	public Fsa getFsa()
	{
		return this.m_fsa  ;
	}
	
	public FsaGuiPanel()
	{
		setBackground(Color.WHITE);
		MyMouseListener myMouseListener = new MyMouseListener();
        addMouseListener(myMouseListener);
        addMouseMotionListener(myMouseListener);
	}
	
	
    public void paintComponent(Graphics g) 
	{
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
	
		if(false ==  m_bEnable )
		{
			return ;
		}
		
		// Draw rectangular region selection
		if(VIEW_STATE_REGIONSEL == m_iState)
    	{
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.draw(m_rectSelection);
			g2d.setColor(Color.BLACK);
    	}
		if (VIEW_STATE_NEWTRANSTION == m_iState) {

			g2d.setColor(Color.GRAY);
			g2d.draw(m_lineNewTransition);
			g2d.setColor(Color.BLACK);

		}
		
		Iterator<State> iterator =  m_fsa.getStates().iterator() ;
		while(iterator.hasNext())
		{
			DrawObj drawObj = (DrawObj)iterator.next() ;
			drawObj.draw(g2d) ;
		}
		
		//g2d.drawString(Integer.toString(m_iCurPosX),10,10);
		//g2d.drawString(Integer.toString(m_iCurPosY),50,10);
    }	
    private java.util.List<DrawObj> m_lstCurDrawObj = new LinkedList<DrawObj>();
    private int m_iCurPosX = 0;
    private int m_iCurPosY = 0;
    
    private class MyMouseListener extends MouseInputAdapter {
        public void mousePressed(MouseEvent e) {
        	int x = e.getX();
            int y = e.getY();
            m_iCurPosX = x;
			m_iCurPosY = y;
			if (VIEW_STATE_SELECTION == m_iState) {
				Iterator<DrawObj> iterCur = m_lstCurDrawObj.iterator();
				while (iterCur.hasNext()) {
					iterCur.next().deselect();
					//iterCur.next().setProperty(DrawObj.DRAW_STATE_UNSELECTED);
				}
				m_lstCurDrawObj.clear();
				setState(VIEW_STATE_SELECTION);
				m_rectSelection.setFrame(0, 0, 0, 0);

				Iterator<State> iterator = (m_fsa.getStates().iterator());
				while (iterator.hasNext()) {
					DrawObj drawObj = (DrawObj) iterator.next();
					drawObj.select(x,y,m_lstCurDrawObj);
					/*if (drawObj.contains(x, y)) {
						m_lstCurDrawObj.add(drawObj);
						//m_iCurPosX = x;
						//m_iCurPosY = y;
						drawObj.setProperty(DrawObj.DRAW_STATE_SELECTED);
						// break;
					}*/
				}

				if (m_lstCurDrawObj.isEmpty()) {
					setState(VIEW_STATE_REGIONSEL);
					m_rectSelection.x = x;
					m_rectSelection.y = y;
				}

			}
			else if(VIEW_STATE_REGIONSELED == m_iState)
			{
				boolean bSame = false;
				Iterator<DrawObj> iteratorDrawObj = (m_lstCurDrawObj.iterator());
				while (iteratorDrawObj.hasNext()) {
					DrawObj drawObj = (DrawObj) iteratorDrawObj.next();
					if (drawObj.contains(x, y)) {
						bSame=true;
						
						break;
					}
				}
				
				if(!bSame)
				{
					Iterator<DrawObj> iterCur = m_lstCurDrawObj.iterator();
					while (iterCur.hasNext()) {
						iterCur.next().deselect();
						//iterCur.next().setProperty(DrawObj.DRAW_STATE_UNSELECTED);
					}
					m_lstCurDrawObj.clear();
					setState(VIEW_STATE_SELECTION);
					m_rectSelection.setFrame(0, 0, 0, 0);

					Iterator<State> iterator = (m_fsa.getStates().iterator());
					while (iterator.hasNext()) {
						DrawObj drawObj = (DrawObj) iterator.next();
						drawObj.select(x,y,m_lstCurDrawObj);
						/*if (drawObj.contains(x, y)) {
							m_lstCurDrawObj.add(drawObj);
							//m_iCurPosX = x;
							//m_iCurPosY = y;
							drawObj.setProperty(DrawObj.DRAW_STATE_SELECTED);
							// break;
						}*/
					}

					if (m_lstCurDrawObj.isEmpty()) {
						setState(VIEW_STATE_REGIONSEL);
						m_rectSelection.x = x;
						m_rectSelection.y = y;
					}
				}
			}
			else if(VIEW_STATE_NEWSTATE == m_iState)
			{
				try{
				String text = JOptionPane.showInputDialog("State name: ", "");
			    if (text == null) {
			        
			    }
			    else
			    {
			    	m_fsa.addState(text, x, y ); 
			    	m_bEnable = true;
			    }
				}catch(IllegalArgumentException exception)
				{
					m_fsaGui.output(exception.getMessage());
				}
				setState(VIEW_STATE_SELECTION);
				setCursor(Cursor.getDefaultCursor() );

			}
			else if(VIEW_STATE_NEWTRANSTION == m_iState)
			{
				boolean bSel = false;
				Iterator<State> iteratorDrawObj = m_fsa.getStates().iterator();
				while (iteratorDrawObj.hasNext()) {
					DrawObj drawObj = (DrawObj) iteratorDrawObj.next();
					if (drawObj.contains(x, y)) {
						bSel=true;
						m_lineNewTransition.x1 = x;
						m_lineNewTransition.y1 = y;	
						m_lineNewTransition.x2 = x;
						m_lineNewTransition.y2 = y;	
						
						newTranFrom = (State)drawObj;
						break;
					}
				}
				if(false == bSel)
				{
					setState(VIEW_STATE_SELECTION);
					setCursor(Cursor.getDefaultCursor() );
				}
			}
			
			repaint();
		}
        
        private void move(MouseEvent e)
        {
        	int x = e.getX();
            int y = e.getY();
            
        	if(VIEW_STATE_REGIONSEL == m_iState) // Region Selection
        	{
        		m_rectSelection.width = x - m_rectSelection.x;
        		m_rectSelection.height = y - m_rectSelection.y;
        	}
        	else if((VIEW_STATE_SELECTION == m_iState)
        			||(VIEW_STATE_REGIONSELED == m_iState))// Moving
        	{
        		if(!m_lstCurDrawObj.isEmpty())
            	{
                	Iterator<DrawObj> iterator =  m_lstCurDrawObj.iterator() ;
                	if(!iterator.hasNext())
                	{
                		return;
                	}
             
                    int xOff = x-m_iCurPosX;
                    int yOff = y-m_iCurPosY;
                	
            		while(iterator.hasNext())
            		{
            			DrawObj obj = iterator.next() ;
            			if(obj instanceof State)
            			{
            				State state = (State)obj;
                			state.moveBy(xOff, yOff);
            			}
            			
            		}
            		m_iCurPosX = x;
            		m_iCurPosY = y;        		
            	}
        	}
        	else if(VIEW_STATE_NEWTRANSTION == m_iState)
			{
				m_lineNewTransition.x2 = x;
				m_lineNewTransition.y2 = y;		
			}
        }
        
        public void mouseDragged(MouseEvent e) {
        	
        	move(e);
        	repaint();
        }

        public void mouseReleased(MouseEvent e) {
        	int x = e.getX();
            int y = e.getY();
        	if(VIEW_STATE_REGIONSEL == m_iState)
        	{
        		Iterator<DrawObj> iterCur =  m_lstCurDrawObj.iterator() ;
        		while(iterCur.hasNext())
        		{
        			iterCur.next().deselect();
        			//iterCur.next().setProperty(DrawObj.DRAW_STATE_UNSELECTED);
        		}
                m_lstCurDrawObj.clear();
                Iterator<State> iterator =  (m_fsa.getStates().iterator()) ;
                while(iterator.hasNext() )
        		{
        			DrawObj drawObj = (DrawObj)iterator.next() ;
        			drawObj.select(m_rectSelection, m_lstCurDrawObj);
        			/*if(drawObj.intersects(m_rectSelection))
        			{
        				m_lstCurDrawObj.add(drawObj);
        				drawObj.setProperty(DrawObj.DRAW_STATE_SELECTED);
        				//break;
        			}*/
        		}
                if(!m_lstCurDrawObj.isEmpty())
                {
                	setState(VIEW_STATE_REGIONSELED);
                }
                else
                {
                	setState(VIEW_STATE_SELECTION);
                }
        		
        	}
        	else if(VIEW_STATE_NEWTRANSTION == m_iState)
			{
				boolean bSel = false;
				Iterator<State> iteratorDrawObj = m_fsa.getStates().iterator();
				while (iteratorDrawObj.hasNext()) {
					DrawObj drawObj = (DrawObj) iteratorDrawObj.next();
					if (drawObj.contains(x, y)) {
						bSel=true;
						m_lineNewTransition.x2 = x;
						m_lineNewTransition.y2 = y;		
						newTranTo = (State)drawObj;
						
						break;
					}
				}
				if(bSel)
				{
					try{
						String text = JOptionPane.showInputDialog("Transition name, transition output: ", "name,output");
					    if (text == null) {
					        
					    }
					    else
					    {
					    	String strName = "";
					    	String strOutput = "";
					    	StringTokenizer st = new StringTokenizer(text,","); 
				    		if(st.hasMoreTokens() )
				    		{
				    			strName = st.nextToken() ;
				    		}
				    		if(st.hasMoreTokens() )
				    		{
				    			strOutput = st.nextToken() ;
				    		}
					    	m_fsa.addTransition(newTranFrom, newTranTo,strName,strOutput ); 
					    	
					    }
						}catch(IllegalArgumentException exception)
						{
							m_fsaGui.output(exception.getMessage());
						}
				}
				setState(VIEW_STATE_SELECTION);
				setCursor(Cursor.getDefaultCursor() );
			}
        	else
        	{
        		move(e);
        		//repaint();
        	}
        	repaint();
        	
        }
        
    }
    
    public void deleteSelection()
    {
    	Iterator<DrawObj> iterCur =  m_lstCurDrawObj.iterator() ;
		while(iterCur.hasNext())
		{
			DrawObj obj = iterCur.next();
			obj.deselect();
			
			if(obj instanceof State)
			{
				getFsa().removeState((State)obj);
			}
			else if(obj instanceof Transition)
			{
				getFsa().removeTransition((Transition)obj);
			}
			
		}
		m_lstCurDrawObj.clear();
		repaint();
    }
    
    public boolean setInitialState() {
		if (1 == m_lstCurDrawObj.size()) {
			Iterator<DrawObj> iterCur = m_lstCurDrawObj.iterator();
			if (iterCur.hasNext()) {
				DrawObj obj = iterCur.next();
				if (obj instanceof State) {
					getFsa().setInitialState((State)obj);
					repaint();
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}		
		}
		else
		{
			return false;
		}
		
	}
    public void newState()
    {
    	m_iState = VIEW_STATE_NEWSTATE;
    	Cursor c = new Cursor(Cursor.CROSSHAIR_CURSOR) ;
    	this.setCursor(c);
    }
    
    public void newTranstion()
    {
    	m_iState = VIEW_STATE_NEWTRANSTION;
    	Cursor c = new Cursor(Cursor.HAND_CURSOR) ;
    	this.setCursor(c);
    }
}
