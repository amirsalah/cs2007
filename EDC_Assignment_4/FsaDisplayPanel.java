/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 5th, Oct 2007
=========================================================*/
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;


public class FsaDisplayPanel extends JPanel{
	private Fsa fsa;
	private FsaRenderer fsaRenderer;
	// Map: shape -> state
	private Map<Shape, State> mapShapeState = new HashMap<Shape, State>();
	private Set<State> selectedStates = new HashSet<State>();
	
	// Map: shape -> Transition
	private Map<Shape, Transition> mapShapeTransition = new HashMap<Shape, Transition>();
	private Set<Transition> selectedTransitions = new HashSet<Transition>();
	
	// Map: state -> status of state selection
//	private Map<State, Boolean> mapStateSlection = new HashMap<State, Boolean>();
	// Map: State name -> Current State
//	private Map<String, Boolean> mapCurrentStates = new HashMap<String, Boolean>();
	private ArrayList<String> initStates = new ArrayList<String>();
	private ArrayList<String> allStates = new ArrayList<String>();
	private ArrayList<String> allTransitions = new ArrayList<String>();
	// Map: Transition -> multiplicity
	private Map<String, Integer> mapMultiplicity = new HashMap<String, Integer>();
	private boolean fsaLoaded = false;
	
	private Set<State> currentState = null;
	
	/*
	 * The different display states
	 */
	private final int DISPLAY_SELECTION = 0;  
	private final int DISPLAY_SELECTED = 1;
	private final int DISPLAY_DRAW_REGION = 2;
	
	private int myDisplay = DISPLAY_SELECTION; // Indicate the current display state
	
	// The rectangular region
	private Rectangle2D.Double rectRegion= new Rectangle2D.Double();
	Graphics2D gra2d = null;
	
	// Initialize the display panel
	public FsaDisplayPanel(){
//		setBackground(Color.WHITE);
		MyListener myMouseListener = new MyListener();
        addMouseListener(myMouseListener);
        addMouseMotionListener(myMouseListener);
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
			shape = fsaRenderer.drawTransition(gra2d, t, numMulti, false);
			RemoveMapValue(mapShapeTransition, t);
			mapShapeTransition.put(shape, t);
		}
		
		// Draw selection rectangular region
		if(myDisplay == DISPLAY_DRAW_REGION){
			gra2d.setColor(Color.MAGENTA);
			gra2d.draw(rectRegion);
		}
	}
	
	
	/*
	 * Remove an existing map, to prevent from duplicated mapping
	 * because once the shape moved, the state or transition stay the same
	 * Therefore, the state/transition (value) should be checked, rather than the key.
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
		
		// Save transitions' name
		Transition t = null;
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		Iterator<Transition> itr_transition = ((FsaImpl)fsa).GetTransitions().iterator();
		while (itr_transition.hasNext()){
			t = itr_transition.next();
			allTransitions.add(t.toString());
			transitions.add(t);
		}
		
		// Set the multiplicity of each transition
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
		
		return true;
	}
	
	private class MyListener extends MouseInputAdapter {
		Set<Shape> allShapes = null;
		private int xPressedPos = 0;
		private int yPressedPos = 0;
		private int xDragedPos = 0;
		private int yDragedPos = 0;
		
		public void mousePressed(MouseEvent e){
			xPressedPos = e.getX();
			yPressedPos = e.getY();
			xDragedPos = xPressedPos;
			yDragedPos = yPressedPos;
			
			// 
			if(myDisplay == DISPLAY_SELECTION){
				selectedStates.clear();
			
				allShapes = mapShapeState.keySet(); // all the state shapes
				Iterator<Shape> itr_shape = allShapes.iterator();
				Shape selectedShape = null;
				State selectedState = null;
			
				while(itr_shape.hasNext()){
					selectedShape = itr_shape.next();
					if(selectedShape.contains(xPressedPos, yPressedPos)){
						selectedState = mapShapeState.get(selectedShape);
						selectedStates.add(selectedState);
					}
				}
				
//				allShapes = mapShapeTransition.keySet(); // all the transition shapes
//				itr_shape = allShapes.iterator(); 
			
				if(!selectedStates.isEmpty()){
					myDisplay = DISPLAY_SELECTED;
				}else{
					myDisplay = DISPLAY_DRAW_REGION;
					rectRegion.x = xPressedPos;
					rectRegion.y = yPressedPos;
				}
				
				repaint();
			}
		}
		
		public void mouseDragged(MouseEvent e){
			int xPos = e.getX();
			int yPos = e.getY();
			
			int rectWidth = 0;
			int rectHeight = 0;
			
			if(myDisplay == DISPLAY_SELECTED){
				int xOffset = xPos - xDragedPos;
				int yOffset = yPos - yDragedPos;
			
				xDragedPos = xPos;
				yDragedPos = yPos;
			
				Iterator<State> itr_state = selectedStates.iterator();
				while(itr_state.hasNext()){
					itr_state.next().moveBy(xOffset, yOffset);
				}
			}

			if(myDisplay == DISPLAY_DRAW_REGION){
				rectWidth = xPos - xPressedPos;
				rectHeight = yPos - yPressedPos;
				rectRegion.height = rectHeight;
				rectRegion.width = rectWidth;
			}

			repaint();
			return;
		}
		
		public void mouseReleased(MouseEvent e){
//			selectedStates.clear();
			if(myDisplay == DISPLAY_SELECTED){
				myDisplay = DISPLAY_SELECTION;
			}
			
			if(myDisplay == DISPLAY_DRAW_REGION){
				Set<Shape> allShapes = null;
				
				myDisplay = DISPLAY_SELECTION;
				// Check selected shapes
				allShapes = mapShapeState.keySet();
				Iterator<Shape> itr_shape = allShapes.iterator();
				Shape shape = null;
				State selectedState = null;
				
				while(itr_shape.hasNext()){
					shape = itr_shape.next();
					if(shape.intersects(rectRegion)){
						selectedState = mapShapeState.get(shape);
						selectedStates.add(selectedState);
					}
				}
				// recover the initial rectangular regions
				rectRegion.height = 0;
				rectRegion.width = 0;
			}
			repaint();
		}
	}
}
