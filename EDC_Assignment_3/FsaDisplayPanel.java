import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;


public class FsaDisplayPanel extends JPanel{
	private Fsa fsa;
	private FsaRenderer fsaRenderer;
	// Map from state name to the status of state selection
	private Map<String, Boolean> stateSelectionMap = new HashMap<String, Boolean>();
	private ArrayList<String> initStates = new ArrayList<String>();
	private ArrayList<String> allStates = new ArrayList<String>();
	private ArrayList<String> allTransitions = new ArrayList<String>();
	// Map: Transition -> multiplicity
	private Map<String, Integer> mapMultiplicity = new HashMap<String, Integer>();
	private boolean fsaLoaded = false;
	
	// Initialize the display panel
	public FsaDisplayPanel(){
//		setBackground(Color.WHITE);
		fsaRenderer = new MyRenderer();
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
		
		while(itr.hasNext()){
			s = itr.next();
			if(initStates.contains(s.getName())){
				continue;
			}
			fsaRenderer.drawState(gra2d, s, false, false);
		}

		// Draw Initial states
		itr = fsa.getInitialStates().iterator();
		
		while(itr.hasNext()){
			fsaRenderer.drawInitialTransition(gra2d, itr.next(), true);
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
	 * Set the state to indicate if it is selected.
	 * @param stateName
	 * @param picked true if the state is selected, false otherwise.
	 */
	private void SetStatePick(String stateName, Boolean picked){
		
		if(!stateSelectionMap.containsKey(stateName)){
			System.out.println("invalid state");
			return;
		}
		
		// Set new map for the state
		stateSelectionMap.remove(stateName);
		stateSelectionMap.put(stateName, picked);
	}
	
	/**
	 * Set a new Renderer
	 * @param renderer
	 */
	private void SetRenderer(FsaRenderer renderer){
		fsaRenderer = renderer;
	}
	
	public boolean LoadFsa(Fsa fsa){
		// Check to see if the Fsa is valid
		if(fsa.getInitialStates().isEmpty()){
			System.out.println("Not a valid FSA");
			return false;
		}
		
		this.fsa = fsa;
		fsaLoaded = true;
		
		stateSelectionMap.clear();
		
		// Save initial states' name
		Iterator<State> itr = fsa.getInitialStates().iterator();
		while(itr.hasNext()){
			initStates.add(itr.next().getName());
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
}
