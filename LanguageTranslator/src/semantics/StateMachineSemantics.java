package semantics;

import java.util.Iterator;
import java.util.Vector;

import parser.astnode.AbstractViewableMachineNode;
import parser.astnode.EventNode;
import parser.astnode.EventdefNode;
import parser.astnode.MachineNode;
import parser.astnode.OutgoingNode;
import parser.astnode.SourceNode;
import parser.astnode.StateNode;
import parser.astnode.TargetNode;
import parser.astnode.TransitionNode;

public class StateMachineSemantics {
    private MachineNode mNode = null;
    private Vector<String> eventNames = new Vector<String>();
    private Vector<String> stateNames = new Vector<String>();
    private Vector<String> transitionNames = new Vector<String>();
    
    
    StateMachineSemantics(MachineNode mNode){
        this.mNode = mNode;
    }
    
    public boolean checkStateMachineSemantics(){
        boolean noError = true;
        initSymbolTables();
        
        noError = checkStateOutgoings();
        noError = checkTransitions();
        return noError;
    }
    
    public void semanticWarning(String msg){
        System.out.println("Semantic warning: " + msg);
    }
    
    /**
     * Initialize symbol tables for the state machine (mNode)
     */
    private void initSymbolTables(){
        /* Initialize states' name */
        Iterator i;
        AbstractViewableMachineNode node;
        for(i=mNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            
            if(node.getNodeType().equals("EVENTDEF")){
                eventNames.add( ((EventdefNode)node).getAttribute_name() );
            }
            
            if(node.getNodeType().equals("STATE")){
                stateNames.add( ((StateNode)node).getAttribute_name() );
            }
            
            if(node.getNodeType().equals("TRANSITION")){
                transitionNames.add( ((TransitionNode)node).getAttribute_name() );
            }
        }
    }
    
    /**
     * Check if the outgoing exist for every state
     * @return true if all outgoing are defined.
     */
    private boolean checkStateOutgoings(){
        boolean noError = true;
        Iterator i;
        AbstractViewableMachineNode node;
        for(i=mNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            
            if(node.getNodeType().equals("STATE")){
                checkOutgoing( (StateNode)node );
            }
        }
        return noError;
    }
    
    /**
     * Check existence of all outgoing for a single state (sNode)
     * @param sNode the node of the state
     * @return true if the sNode has valid outgoings
     */
    private boolean checkOutgoing(StateNode sNode){
        boolean noError = true;
        Vector<String> outgoings = new Vector<String>();
        
        // initialize the outgoings from this state
        Iterator itr;
        AbstractViewableMachineNode outNode;
        for(itr=sNode.childIterator(); itr.hasNext(); ){
            outNode = (AbstractViewableMachineNode)itr.next();
            
            if(outNode.getNodeType().equals("OUTGOING")){
                outgoings.add( ((OutgoingNode)outNode).getAttribute_trans() );
            }
        }

        for(int i=0; i<outgoings.size(); i++){
            if( !transitionNames.contains( outgoings.get(i)) ){
                semanticWarning("undefined transition > " + outgoings.get(i) + " in STATE " + sNode.getAttribute_name());
                noError = false;
            }
        }
        return noError;
    }
    
    
    private boolean checkTransitions(){
        boolean noError = true;
        Iterator i;
        AbstractViewableMachineNode node;
        
        for(i=mNode.childIterator(); i.hasNext(); ){
            node = (AbstractViewableMachineNode)i.next();
            
            if(node.getNodeType().equals("TRANSITION")){
                checkTransition((TransitionNode)node);
            }
        }
        
        return noError;
    }

    
    private boolean checkTransition(TransitionNode tNode){
        boolean noError = true;
        String sourceState = null;
        String targetState = null;
        String eventName = null;
        
        // initialize the source and target states, as well as event name
        Iterator itr;
        AbstractViewableMachineNode sNode;
        for(itr=tNode.childIterator(); itr.hasNext(); ){
            sNode = (AbstractViewableMachineNode)itr.next();
            
            if(sNode.getNodeType().equals("SOURCE")){
                sourceState = ((SourceNode)sNode).getAttribute_state();
            }
            
            if(sNode.getNodeType().equals("TARGET")){
                targetState = ((TargetNode)sNode).getAttribute_state();
            }
            
            if(sNode.getNodeType().equals("EVENT")){
                eventName = ((EventNode)sNode).getAttribute_name();
            }
        }
        
        if( !stateNames.contains(sourceState) ){
            semanticWarning("invalid source state > " + sourceState + " in Transition " + tNode.getAttribute_name());
        }
        
        if( !stateNames.contains(targetState) ){
            semanticWarning("invalid target state > " + sourceState + " in Transition " + tNode.getAttribute_name());
        }
        
        if( !eventNames.contains(eventName) ){
            semanticWarning("invalid event > " + sourceState + " in Transition " + tNode.getAttribute_name());
        }
        
        return noError;

    }
}
