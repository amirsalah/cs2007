/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 25th, Oct 2007
=========================================================*/
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputAdapter;


public class FsaDisplayPanel extends JPanel{
	private Fsa fsa;
	private FsaRenderer fsaRenderer;
	private JTextArea messagesArea;
	// Map: shape -> state
	private Map<Shape, State> mapShapeState = new HashMap<Shape, State>();
	private Set<State> selectedStates = new HashSet<State>();
	
	// Map: shape -> Transition
	private Map<Shape, Transition> mapShapeTransition = new HashMap<Shape, Transition>();
	private Set<Transition> selectedTransitions = new HashSet<Transition>();
	
	private ArrayList<String> initStates = new ArrayList<String>();
	private ArrayList<String> allStates = new ArrayList<String>();
//	private ArrayList<String> allTransitions = new ArrayList<String>();
	// Map: Transition -> multiplicity
	private Map<String, Integer> mapMultiplicity = new HashMap<String, Integer>();
	private boolean fsaLoaded = false;
	
	/*
	 * The different display states
	 */
	private final int DISPLAY_SELECTION = 0;  
	private final int DISPLAY_DRAW_REGION = 1;
	private final int DISPLAY_MOVING_SHAPES = 2;
	private final int DISPLAY_ADD_STATE = 3;
	private final int DISPLAY_ADD_TRANSITION = 4;
	
	private int myDisplay = DISPLAY_SELECTION; // Indicate the current display state
	
	// The rectangular region
	private Rectangle2D.Double rectRegion= new Rectangle2D.Double();
	Graphics2D gra2d = null;
	
	// The line link between 2 states, using to create a new transition
	private Line2D.Double transitionLine = new Line2D.Double();
	
	
	// Initialize the display panel
	public FsaDisplayPanel(JTextArea messagesArea){
		MyListener myMouseListener = new MyListener();
        addMouseListener(myMouseListener);
        addMouseMotionListener(myMouseListener);
        this.messagesArea = messagesArea;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		gra2d = (Graphics2D)g;
		
		if(!fsaLoaded){
			return;
		}
		
		// Draw all other states except the initial states
		Iterator<State> itr = fsa.getStates().iterator();
		State s = null;
		Shape shape = null;
		
		while(itr.hasNext()){
			s = itr.next();
			if(initStates.contains(s.getName())){
				continue;
			}
			shape = fsaRenderer.drawState(gra2d, s, selectedStates.contains(s), fsa.getCurrentStates().contains(s));
			RemoveMapValue(mapShapeState, s);
			mapShapeState.put(shape, s);
		}

		// Draw Initial states
		itr = fsa.getInitialStates().iterator();
		
		while(itr.hasNext()){
			s = itr.next();
			shape = fsaRenderer.drawInitialTransition(gra2d, s, selectedStates.contains(s));
			RemoveMapValue(mapShapeState, s);
			mapShapeState.put(shape, s);
		}
		
		// Draw transitions
		Iterator<Transition> itr_transition = ((FsaImpl)fsa).GetTransitions().iterator();
		int numMulti = 0;
		Transition t = null;
		while (itr_transition.hasNext()){
			t = itr_transition.next();
			numMulti = mapMultiplicity.get(t.toString());
			shape = fsaRenderer.drawTransition(gra2d, t, numMulti, selectedTransitions.contains(t));
			RemoveMapValue(mapShapeTransition, t);
			mapShapeTransition.put(shape, t);
		}
		
		// Draw selection rectangular region
		if(myDisplay == DISPLAY_DRAW_REGION){
			gra2d.setColor(Color.MAGENTA);
			gra2d.draw(rectRegion);
		}
		
		// Draw a new transition line during its creation process
		if(myDisplay == DISPLAY_ADD_TRANSITION){
			gra2d.setColor(Color.MAGENTA);
			gra2d.draw(transitionLine);
		}
	}
	

	/**
	 * Remove an existing map, to prevent from duplicated mapping
	 * because once the shape moved, the state or transition stay the same
	 * Therefore, the state/transition (value) should be checked, rather than the key.
	 * 
	 * @param value the value in the map
	 */
	private void RemoveMapValue(Map map, Object value){
		Iterator itr = map.keySet().iterator();
		Object key = null;
		
		// Return true if there is no such map
		if(!map.containsValue(value)){
			return;
		}
		
		while(itr.hasNext()){
			key = itr.next();
			
			if(map.get(key).equals(value)){
				map.remove(key);
				return;
			}
		}
		
		return;
	}
	/**
	 * Set a new Renderer
	 * @param renderer
	 */
	public void SetDefaultRenderer(){
		fsaRenderer = new SimpleRenderer();
		repaint();
	}
	
	public void SetMyRenderer(){
		fsaRenderer = new MyRenderer(fsa);
		repaint();
		
	}
	
	public boolean LoadFsa(Fsa fsa){
		// Check to see if the Fsa is valid
		if(fsa.getInitialStates().isEmpty()){
			System.out.println("Not a valid FSA");
			return false;
		}
		
		this.fsa = fsa;
		fsaLoaded = true;
		fsaRenderer = new MyRenderer(fsa);
		
		selectedStates.clear();
		
		// Save initial states' name
		Iterator<State> itr = fsa.getInitialStates().iterator();
		State s = null;
		while(itr.hasNext()){
			s = itr.next();
			initStates.add(s.getName());
		}
		
		// Save all states' name
		itr = fsa.getStates().iterator();
		while(itr.hasNext()){
			allStates.add(itr.next().getName());
		}
		
		// Set the multiplicity of each transition
		SetMultiplicity();
		
		return true;
	}
	
	/**
	 * Initialize the multiplicity of each transition
	 *
	 */
	private void SetMultiplicity(){
		Transition t = null;
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		Iterator<Transition> itr_transition = ((FsaImpl)fsa).GetTransitions().iterator();
		while (itr_transition.hasNext()){
			t = itr_transition.next();
			transitions.add(t);
		}
		
		int numMulti = 1;
		while(!transitions.isEmpty()){
			t = transitions.get(0);
			numMulti = 1;
			mapMultiplicity.put(t.toString(), numMulti);
			for(int i=1; i<transitions.size(); i++){
				if(transitions.get(i).fromState().getName().equals(t.fromState().getName()) &&
						transitions.get(i).toState().getName().equals(t.toState().getName())){
					numMulti++;
					mapMultiplicity.put(transitions.get(i).toString(), numMulti);
					transitions.remove(i);
					i--;
				}
			}
			transitions.remove(0);
		}
	}
	
	private class MyListener extends MouseInputAdapter {
		Set<Shape> allShapes = null;
		private int xPressedPos = 0;
		private int yPressedPos = 0;
		private int xDragedPos = 0;
		private int yDragedPos = 0;
		
		State startingState = null;
		State destinationState = null;
		
		public void mousePressed(MouseEvent e){
			xPressedPos = e.getX();
			yPressedPos = e.getY();
			xDragedPos = xPressedPos;
			yDragedPos = yPressedPos;
			
			allShapes = mapShapeState.keySet(); // all the state shapes
			Iterator<Shape> itr_shape = allShapes.iterator();
			Shape selectedShape = null;
			State selectedState = null;
			
			switch(myDisplay){
			/* in the stage of selecting states */
			case DISPLAY_SELECTION:
				while(itr_shape.hasNext()){
					selectedShape = itr_shape.next();
					if(selectedShape.contains(xPressedPos, yPressedPos)){
						selectedState = mapShapeState.get(selectedShape);
						selectedStates.add(selectedState);
					}
				}
				
				// One or more states are clicked
				if(!selectedStates.isEmpty()){
					myDisplay = DISPLAY_MOVING_SHAPES;
				}else{ // No states are clicked
					selectedStates.clear();
					selectedTransitions.clear();
					myDisplay = DISPLAY_DRAW_REGION;
					rectRegion.x = xPressedPos;
					rectRegion.y = yPressedPos;
				}
				break;
				
			/* ready to move one or more selected shapes */
			case DISPLAY_MOVING_SHAPES:
				boolean stateClicked = false;
				while(itr_shape.hasNext()){
					selectedShape = itr_shape.next();
					if(selectedShape.contains(xPressedPos, yPressedPos)){
						stateClicked = true;
						selectedState = mapShapeState.get(selectedShape);
						
						if(!selectedStates.contains(selectedState)){
							selectedStates.clear();
						}else{
							return;
						}
						selectedStates.add(selectedState);
					}
				}
				
				// Clicking position contains no state
				if(!stateClicked){
					selectedStates.clear();
					selectedTransitions.clear();
					myDisplay = DISPLAY_DRAW_REGION;
					rectRegion.x = xPressedPos;
					rectRegion.y = yPressedPos;
				}
				break;
				
			/* Add a new state to current FSA */
			case DISPLAY_ADD_STATE:
				String inputStateName = JOptionPane.showInputDialog("State name: ", "");
				try{
					if(inputStateName != null){
						fsa.newState(inputStateName, xPressedPos, yPressedPos);
					}
				}
				catch(IllegalArgumentException iae){
					messagesArea.append("State name is invalid" + "\n");
				}
					
				myDisplay = DISPLAY_SELECTION;
				setCursor(Cursor.getDefaultCursor());
				break;
				
			case DISPLAY_ADD_TRANSITION:
				stateClicked = false;
				selectedStates.clear();
				
				while(itr_shape.hasNext()){
					selectedShape = itr_shape.next();
					if(selectedShape.contains(xPressedPos, yPressedPos)){
						stateClicked = true;
						selectedState = mapShapeState.get(selectedShape);
						selectedStates.add(selectedState);
						//Set the start point of transition line
						transitionLine.x1 = xPressedPos;
						transitionLine.y1 = yPressedPos;
						transitionLine.x2 = xPressedPos;
						transitionLine.y2 = yPressedPos;
						
						startingState = selectedState;
						break;
					}
				}
				
				if(!stateClicked){
					myDisplay = DISPLAY_SELECTION;
					setCursor(Cursor.getDefaultCursor());
				}
				break;
			}
			repaint();
		}
		
		public void mouseDragged(MouseEvent e){
			int xPos = e.getX();
			int yPos = e.getY();
			
			int rectWidth = 0;
			int rectHeight = 0;
			
			switch(myDisplay){
			case DISPLAY_MOVING_SHAPES:
				int xOffset = xPos - xDragedPos;
				int yOffset = yPos - yDragedPos;
			
				xDragedPos = xPos;
				yDragedPos = yPos;
			
				Iterator<State> itr_state = selectedStates.iterator();
				while(itr_state.hasNext()){
					itr_state.next().moveBy(xOffset, yOffset);
				}
				break;
				
			case DISPLAY_DRAW_REGION:
				rectWidth = xPos - xPressedPos;
				rectHeight = yPos - yPressedPos;
				rectRegion.height = rectHeight;
				rectRegion.width = rectWidth;
				break;
				
				/* Draw a transition line */
			case DISPLAY_ADD_TRANSITION:
				transitionLine.x2 = xPos;
				transitionLine.y2 = yPos;
				break;
			}

			repaint();
			return;
		}
		
		public void mouseReleased(MouseEvent e){
			Set<Shape> allShapes = null;
			
			allShapes = mapShapeState.keySet();
			Iterator<Shape> itr_shape = allShapes.iterator();
			Shape shape = null;
			State selectedState = null;
			
			switch(myDisplay){
			case DISPLAY_DRAW_REGION:
				while(itr_shape.hasNext()){
					shape = itr_shape.next();
					if(shape.intersects(rectRegion)){
						selectedState = mapShapeState.get(shape);
						selectedStates.add(selectedState);
					}
				}
				
				// Check selected transitions
				allShapes = mapShapeTransition.keySet();
				
				Iterator<Shape> itr_transition = allShapes.iterator();
				Transition selectedTransition = null;
				
				while(itr_transition.hasNext()){
					shape = itr_transition.next();
					if(shape.intersects(rectRegion)){
						selectedTransition = mapShapeTransition.get(shape);
						selectedTransitions.add(selectedTransition);
					}
				}
				
				// recover the initial rectangular regions
				rectRegion.height = 0;
				rectRegion.width = 0;
				
				if(selectedStates.isEmpty()){
					myDisplay = DISPLAY_SELECTION;
				}else{
					myDisplay = DISPLAY_MOVING_SHAPES;
				}
				break;
				
			case DISPLAY_ADD_TRANSITION:
				int xDepressedPos = e.getX();
				int yDepressedPos = e.getY();
				boolean validDestination = false;
				
				while(itr_shape.hasNext()){
					shape = itr_shape.next();
					if(shape.contains(xDepressedPos, yDepressedPos)){
						validDestination = true;
						selectedState = mapShapeState.get(shape);
						selectedStates.add(selectedState);
						destinationState = selectedState;
						//Set the final destination of the transition line
						transitionLine.x2 = xDepressedPos;
						transitionLine.y2 = yDepressedPos;
						break;
					}
				}
				
				// Draw the new transition if the destination is a valid state
				if(validDestination == true){
					String inputTransition = JOptionPane.showInputDialog("Transition Name,Output: ", "Name,Output");
					
					try{
						if(inputTransition != null){
							StringTokenizer st = new StringTokenizer(inputTransition, ",");
							String eventName = st.nextToken();
							String output = st.nextToken();
							fsa.newTransition(startingState, destinationState, eventName, output);
							SetMultiplicity();
							selectedStates.clear();
							selectedTransitions.clear();
						}
					}
					catch(IllegalArgumentException iae){
						messagesArea.append("State name is invalid" + "\n");
					}
					catch(NoSuchElementException nsee){
						messagesArea.append("Please enter: event name,output" + "\n");
					}
				}
				myDisplay = DISPLAY_SELECTION;
				setCursor(Cursor.getDefaultCursor());
				break;
			}
			
			repaint();
		}
	}
	
	/*
	 * Remove current selected states & selected transitions,
	 * and all transitions linked to these states
	 */
	public void DeleteSelectedStates(){
		Iterator<State> itr_state = selectedStates.iterator();
		Iterator<Transition> itr_transition = selectedTransitions.iterator();
		State aState = null;
		Transition aTransition = null;
		
		Set<Transition> allTransitions = ((FsaImpl)fsa).GetTransitions();
		Iterator<Transition> itr_tmp = allTransitions.iterator();
		
		// Remove all transitions linked to the selected states
		while(itr_state.hasNext()){
			aState = itr_state.next();
	    	while(itr_tmp.hasNext()){
	    		aTransition = itr_tmp.next();
	    		if(aTransition.fromState().equals(aState) || aTransition.toState().equals(aState)){
	    			RemoveMapValue(mapShapeTransition, aTransition);
	    		}
	    	}
		}
		
		// Remove current states, as well as the corresponding shapes
		itr_state = null;
		itr_state = selectedStates.iterator();
		while(itr_state.hasNext()){
			aState = itr_state.next();
			fsa.removeState(aState);
			
			RemoveMapValue(mapShapeState, aState);
		}
		
		// Remove current transitions, as well as their shapes
		while(itr_transition.hasNext()){
			aTransition = itr_transition.next();
			fsa.removeTransition(aTransition);
			
			RemoveMapValue(mapShapeTransition, aTransition);
		}
		
		repaint();
	}
	
	/**
	 * Return false if there is no state selected
	 *
	 */
	public boolean SetInitialStates(){
		// Do nothing if no currently selected states
		if(selectedStates.isEmpty()){
			return false;
		}
		
		((FsaImpl)fsa).removeAllInitialStates(); //Remove initial states in the FSA
		initStates.clear(); //Remove initial states in this display panel
		
		Iterator<State> itr_state = selectedStates.iterator();
		State aState = null;
		while(itr_state.hasNext()){
			aState = itr_state.next();
			fsa.addInitialState(aState);
			initStates.add(aState.getName());
		}
		return true;
	}
	
	/** Add a new state to current fsa
	 *  the mouse also change to a hand 
	 */
	public void NewState(){
		myDisplay = DISPLAY_ADD_STATE;
    	setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
	
	/**
	 * Add a new transition to current fsa
	 *
	 */
	public void NewTransition(){
		myDisplay = DISPLAY_ADD_TRANSITION;
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
	}
}
