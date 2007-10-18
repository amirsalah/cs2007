/*=======================================================
  @Author: Bo CHEN
  Student ID: 1139520
  Date: 5th, Oct 2007
=========================================================*/
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
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
	
	// Initialize the display panel
	public FsaDisplayPanel(){
//		setBackground(Color.WHITE);
		MyListener myMouseListener = new MyListener();
        addMouseListener(myMouseListener);
        addMouseMotionListener(myMouseListener);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D gra2d = (Graphics2D)g;
		
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
			mapShapeState.put(shape, s);
		}

		// Draw Initial states
		itr = fsa.getInitialStates().iterator();
		
		while(itr.hasNext()){
			s = itr.next();
			shape = fsaRenderer.drawInitialTransition(gra2d, s, selectedStates.contains(s));
			mapShapeState.put(shape, s);
		}
		
		// Draw transitions
		Iterator<Transition> itr_transition = ((FsaImpl)fsa).GetTransitions().iterator();
		int numMulti = 0;
		Transition t = null;
		while (itr_transition.hasNext()){
			t = itr_transition.next();
			numMulti = mapMultiplicity.get(t.toString());
			fsaRenderer.drawTransition(gra2d, t, numMulti, false);
		}
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
		private int xPressed = 0;
		private int yPressed = 0;
		
		public void mousePressed(MouseEvent e){
			xPressed = e.getX();
			yPressed = e.getY();
			
			selectedStates.clear();
			
			allShapes = mapShapeState.keySet();
			Iterator<Shape> itr_shape = allShapes.iterator();
			Shape selectedShape = null;
			State selectedState = null;
			
			while(itr_shape.hasNext()){
				selectedShape = itr_shape.next();
				if(selectedShape.contains(xPressed, yPressed)){
					selectedState = mapShapeState.get(selectedShape);
					selectedStates.add(selectedState);
				}
			}
			
			repaint();
		}
		
		public void mouseDragged(MouseEvent e){
			int xPos = e.getX();
			int yPos = e.getY();
			
			int xOffset = xPos - xPressed;
			int yOffset = yPos - yPressed;
			
			xPressed = xPos;
			yPressed = yPos;
			
			Iterator<State> itr_state = selectedStates.iterator();
			while(itr_state.hasNext()){
				itr_state.next().moveBy(xOffset, yOffset);
			}
			
			repaint();
		}
		
		public void mouseReleased(MouseEvent e){
			selectedStates.clear();
			
			repaint();
		}
	}
}
