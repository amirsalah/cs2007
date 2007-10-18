import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.io.*;



/**
 * 
 */

/**
 * @author xingxing SONG
 *
 */
public class FsaImpl implements Fsa {

	private Set<State> m_setState = new HashSet<State>() ;
	private State  m_stateInit ;
	private boolean m_bEnable  = false ;
	private Set<State> m_setCurState = new HashSet<State>();
	private FsaGui m_fsaGui ;
	public void setFsaGui(FsaGui fsaGui)
	{
		m_fsaGui = fsaGui ;
	}
	
	// 2006-11-01 song added constructor
	public FsaImpl()
	{
		
	}
	//boolean getEnable(){ return m_bEnable ; }
	//void setEnable(boolean bEnable){ m_bEnable = bEnable ; }
    //Read in an Fsa from a stream
    //For the format of the input, see the practical specification sheet
    //The data read from the file replaces any information already in the FSa
    public void read(InputStream is)
      throws FsaFileException
      {
    	m_bEnable = false ;
    	m_setState.clear() ;
    	//int c;
    	try{
    		//DataInputStream dis = new DataInputStream(is) ; 
    		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    		int iLineNum = 1 ;
    		String strLine ;
    		String strItem ;
    		while ((strLine = reader.readLine()) != null) { 
    			
    		if(null!=m_fsaGui)// 2006-11-02 song added
    		{
        		m_fsaGui.output("read : " + strLine) ;			
    		}
    		
    		StringTokenizer st = new StringTokenizer(strLine); 
    		if(st.hasMoreTokens() )
    		{
    			strItem = st.nextToken() ;
    			if (strItem.equals("state")) {
					String strName = "";
					int ihPos = 0;
					int ivPos = 0;
					String strDesc = "";
					if (st.hasMoreTokens()) {
							strName = st.nextToken();
					}

					if (st.hasMoreTokens()) {
						ihPos = Integer.parseInt(st.nextToken());
					} 
					else {
						throw new FsaFileException(strLine + " hPos error", iLineNum);
					}

					if (st.hasMoreTokens()) {
						ivPos = Integer.parseInt(st.nextToken());
					}
					else {
						throw new FsaFileException(strLine+ " vPos error", iLineNum);
					}
					if (st.hasMoreTokens()) {
						strDesc = st.nextToken();
					}
					State state = new StateImpl(strName ,ihPos,ivPos , strDesc);
					m_setState.add(state) ;     	  
                }    			
    			else if (strItem.equals("transition")) {
						State 	stateFrom ;
						State 	stateTo ;		
						String 	strName = "" ;
						String 	strOutput = "" ;
						String 	strDesc	= "" ;
						if (st.hasMoreTokens()) {						
							stateFrom = new StateImpl(st.nextToken() ) ;
						}
						else
						{
							throw new FsaFileException(strLine + " fromName error", iLineNum) ;
						}
						if (st.hasMoreTokens()) {						
							stateTo = new StateImpl(st.nextToken() ) ;
						}
						else
						{
							throw new FsaFileException(strLine + " toName error", iLineNum) ;
						}
						
						if (st.hasMoreTokens()) {						
							strName = st.nextToken() ;
						}
						else
						{
							throw new FsaFileException(strLine + " eventName error", iLineNum) ;
						}
						
						if (st.hasMoreTokens()) {						
							strOutput = st.nextToken() ;
							if( strOutput.equals(new String(".") ) )
							{
								strOutput = "";
							}
						}
						else
						{
							throw new FsaFileException(strLine + " output error", iLineNum) ;
						}
						
						if (st.hasMoreTokens()) {						
							strDesc = st.nextToken() ;
						}						
						
						boolean bstateFrom = false ;
						boolean bstateTo = false ;
						Iterator<State> iterator = m_setState.iterator() ;
						while(iterator.hasNext())
						{
							State state = iterator.next() ;
							if( state.equals(stateFrom) )
							{
								stateFrom = state ;
								bstateFrom = true ;
							}
							if(state.equals(stateTo))
							{
								stateTo = state ;
								bstateTo = true ;
							}
							if( (true == bstateFrom)&&(true == bstateTo) )
							{
								break ;
							}
						}
						
						if( (true == bstateFrom)&&(true == bstateTo) )
						{
							Transition transtion = new TransitionImpl(stateFrom ,stateTo,strName,strOutput, strDesc);
							stateFrom.getTransitions().add(transtion) ;
						}
						else
						{
							throw new FsaFileException(strLine + " stateName error", iLineNum) ;
						}	
					}// end of else if(strArray.get(0).equals("transition"))
					else if(strItem.equals("initial") )
					{
						String strName = "" ;
						int 	ihPos	= 0 ;
						int		ivPos	= 0 ;
						String 	strDesc	= "" ;
						State stateInit ;
						if (st.hasMoreTokens()) {						
							stateInit = new StateImpl(st.nextToken() ) ;
						}
						else
						{
							throw new FsaFileException(strLine + " stateName error", iLineNum) ;
						}
						if (st.hasMoreTokens()) {						
							strDesc = st.nextToken() ;
						}
						
						boolean bstate = false ;
						
						Iterator<State> iterator = m_setState.iterator() ;
						
						while(iterator.hasNext())
						{
							State state = iterator.next() ;
							if( state.equals(stateInit) )
							{
								stateInit = state ;
								bstate = true ;
								break ;
							}
							
						}
						
						if( true == bstate )
						{
							m_stateInit = stateInit ;
							m_setCurState.clear();
							m_setCurState.add(m_stateInit);
							((DrawObj)m_stateInit).setProperty(DrawObj.DRAW_STATE_INIT) ;
							((DrawObj)m_stateInit).setProperty(DrawObj.DRAW_STATE_CURRENT) ;
							m_bEnable = true ;
						}
						else
						{
							throw new FsaFileException(strLine + " stateName error", iLineNum) ;
						}	
					}
					else
					{
						throw new FsaFileException(strLine + " error", iLineNum) ;
					}
    		}
              
            strLine	= "" ;  
      		iLineNum++;  
      		
            } // end of while

    	}   	
    	catch (IOException e) {     
    		System.err.println("Caught IOException: " 
	                                + e.getMessage());
    		
    
	            
	    }
    	
    	
      }
    

 //   private Object (getInteger(String string) {
		// TODO Auto-generated method stub
//		return null;
//	}


	//Return a list of all states in this Fsa
    public Set<State> getStates(){
    	return m_setState ;
    }

    //Return the initial state for this Fsa
    public State getInitialState(){
    	return  m_stateInit ;
    }

    //Return a string describing this Fsa in the format
    //described in the practical specification sheet
    public String toString(){
    	if(false == m_bEnable)
    	{
    		return new String("") ;
    	}
    	else
    	{
    		String str = new String("") ; 
    		String strState = new String("") ; 
    		String strTran = new String("") ; 
    		String strInitState  = new String(""); 
    		Iterator<State> iterator = m_setState.iterator() ;
			while(iterator.hasNext())
			{
				State state = iterator.next() ;
				//strState = strState + ((DrawObj)state).toPropertyString(DrawObj.DRAW_STATE) + "\n" ;//"\r\n"// 2006-11-01 song modified
				strState = strState + "state " + state.toString() + "\n";
				Iterator<Transition> iteratorTran = state.getTransitions().iterator() ;
				
				while(iteratorTran.hasNext())
				{
					Transition transition = iteratorTran.next() ;
					//strTran = strTran + ((DrawObj)transition).toPropertyString(DrawObj.DRAW_TRANSITION) + "\n" ;//"\r\n"
					strTran = strTran +"transition " + transition.toString() + "\n";
				}
			}
			
			if(null != m_stateInit)// 2006-11-01 song added
			{
				strInitState = ((DrawObj)m_stateInit).toPropertyString(DrawObj.DRAW_STATE_INIT) + "\n" ;//"\r\n"
			}
			
			str = strState + strTran + strInitState ;
    		return  str;
    	}
    	
    }
    
    private void setCurState2Handled()
    {
        Iterator iter = m_setCurState.iterator();
    	while(iter.hasNext())
    	{
    		State state = (State)iter.next();
    		// clear
    		((DrawObj)state).setProperty(DrawObj.DRAW_STATE_HANDLED) ;
    	}   	
    }

	
    //
    // NEW METHODS FOR PRAC 3 FOLLOW!
    //
    
	// Returns the outputs generated when this event is processed
	// Outputs must be in alphabetical order, and may contain duplicates
	public List<String> step( Object event )
		throws UnhandledEventException
		{
			if(false == m_bEnable)
			{
				throw new UnhandledEventException("FSA file has not been loaded");
			}
			
			boolean bHandled = false;
			List sortedList = new LinkedList();
			String strEvent = (String)event;
			Iterator iter = m_setCurState.iterator();
			Set<State> setNewState = new HashSet<State>();
			
			
			while(iter.hasNext())
			{
				State state = (State)iter.next();
				
				Iterator iterTran = state.getTransitions().iterator();			
				while(iterTran.hasNext())
				{
					Transition tran = (Transition)iterTran.next();
					if(tran.eventName().equals(strEvent))
					{
						String strOutput = tran.output();
						int index = Collections.binarySearch(sortedList, strOutput);      // -4
					    
						// Add the non-existent item to the list
						//if (index < 0) 
						//{
							sortedList.add(-index-1, strOutput );
						//}
						
						if(!setNewState.contains(tran.toState()))
						{
							setNewState.add(tran.toState() );
							//((DrawObj)tran.toState()).setProperty(DrawObj.DRAW_STATE_CURRENT) ;
						}				
						bHandled = true;
					}
				}
			}
			
			if(false == bHandled)
			{
				setCurState2Handled();// 2006-11-12 song added 
				m_setCurState.clear();// 2006-11-12 song added 
				throw new UnhandledEventException("event " + strEvent + " is not handled");
			}
			setCurState2Handled();
			m_setCurState.clear();
			Iterator iterNewState = setNewState.iterator();			
			while(iterNewState.hasNext())
			{
				((DrawObj)iterNewState.next()).setProperty(DrawObj.DRAW_STATE_CURRENT) ;
			}
			m_setCurState.addAll(setNewState);
			
			return sortedList ;
		}
		
	// Reset the simulation.
	// The FSA returns to its initial state
	public void reset()
	{
		setCurState2Handled();
		m_setCurState.clear();
		m_setCurState.add(m_stateInit);
		((DrawObj)m_stateInit).setProperty(DrawObj.DRAW_STATE_CURRENT) ;
	}

	// Returns the current states of the FSA
	public Set<State> getCurrentStates()
	{
		return m_setCurState ;
	}
	
	//
	// PRAC 4 METHODS
	//
	
	// Sets the initial state of the FSA to be s
	public void setInitialState( State s )
	{
		if(null != m_stateInit)
		{
			((DrawObj)m_stateInit).setProperty(DrawObj.DRAW_STATE);
		}
		m_stateInit = s;
		((DrawObj)m_stateInit).setProperty(DrawObj.DRAW_STATE_INIT);
	}
	
	// Adds a state with specified name and (x,y) position to the FSA
	// Throws IllegalArgumentException if the name is empty or 
	// duplicates an existing state name
	public void addState( String name, int x, int y ) 
		throws IllegalArgumentException {
		if(0 == name.length())
		{
			throw new IllegalArgumentException("Error: state name is error");
		}
		State stateNew = new StateImpl(name, x, y, "");
		Iterator<State> iterator = m_setState.iterator();
		while (iterator.hasNext()) {
			State state = iterator.next();
			if(stateNew.equals(state))
			{
				throw new IllegalArgumentException("Error: state name is duplicate");
			}
		}
		
		m_setState.add(stateNew);
		m_bEnable = true ;
	}
	
	// Adds a transition with specified from and to states, event name
	// and output to the FSA
	// Throws IllegalArgumentException if the event name is empty
	public void addTransition(State from, State to, String eventName,
			String output) throws IllegalArgumentException {
		if (0 == eventName.length()) {
			throw new IllegalArgumentException(
					"Error: transition name is error");
		}

		State stateFrom = from;
		State stateTo = to;
		boolean bstateFrom = false;
		boolean bstateTo = false;
		Iterator<State> iterator = m_setState.iterator();
		while (iterator.hasNext()) {

			State state = iterator.next();
			if (state.equals(stateFrom)) {
				stateFrom = state;
				bstateFrom = true;
			}
			if (state.equals(stateTo)) {
				stateTo = state;
				bstateTo = true;
			}
			if ((true == bstateFrom) && (true == bstateTo)) {
				break;
			}
		}

		if ((true == bstateFrom) && (true == bstateTo)) {
			Transition transtion = new TransitionImpl(stateFrom, stateTo,
					eventName, output, "");
			stateFrom.getTransitions().add(transtion);
		} else {
			throw new IllegalArgumentException("Error: transition is error");
		}
	}
	
	// Removes state s from the FSA
	// Also removes any transitions to or from s
	public void removeState( State s )
	{
		// removes any transitions to s
		Iterator<State> iterator = m_setState.iterator() ;
		
		while(iterator.hasNext())
		{
			State state = iterator.next() ;
			
			
			Iterator<Transition> iteratorTran = state.getTransitions().iterator() ;
			
			while(iteratorTran.hasNext())
			{
				Transition transition = iteratorTran.next() ;
				if(s.equals(transition.toState() ) )
				{
					iteratorTran.remove();
				}
			}
		}
		// Removes state s from the FSA
		// Also removes any transitions from s
		iterator = m_setState.iterator() ;
		while(iterator.hasNext())
		{
			State state = iterator.next() ;
			if(s.equals(state ) )
			{
				Iterator<Transition> iteratorTran = state.getTransitions().iterator() ;
				
				while(iteratorTran.hasNext())
				{
					iteratorTran.next();
					iteratorTran.remove() ;
				}
				iterator.remove();
				
				//	2006-11-12 song added --{
				Iterator<State> iteratorCur = m_setCurState.iterator();
				while(iteratorCur.hasNext())
				{
					if(s.equals(iteratorCur.next() ) )
					{
						iteratorCur.remove();
						break;
					}
				}
				// 2006-11-12 song added --}
				
//				2006-11-13 song added --{
				if(s.equals(m_stateInit))
				{
					m_stateInit = null ;
				}
//				 2006-11-13 song added --}
			}
		}	
	}
	
	// Removes transition t from the FSA
	public void removeTransition( Transition t )
	{
		Iterator<State> iterator = m_setState.iterator() ;
		while(iterator.hasNext())
		{
			State state = iterator.next() ;
		
			Iterator<Transition> iteratorTran = state.getTransitions().iterator() ;
			
			while(iteratorTran.hasNext())
			{
				Transition transition = iteratorTran.next() ;
				if(t.equals(transition) )
				{
					iteratorTran.remove();
				}
			}
		}
	}
}
