/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/*
 * AlternativeBehaviour.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;

/**
 * Class that represents the behaviour associated with an alternative semantic
 * action.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class AlternativeBehaviour extends SequentialBehaviour implements SemanticBehaviour {
	
    /**
     * Current position in the sequence
     */
    private int currentIndex;
    
    /**
     * Internal state of the behaviour
     */
    private int state;
    
	/**
	 * The capabilities of the holder agent.
	 */
	private SemanticCapabilities myCapabilities;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates an Alternative Behaviour.
     * @param capabilities the capabilities of the holder agent
     * @param left the left behaviour of the alternative
     * @param right the right behaviour of the alternative
     */
    public AlternativeBehaviour(SemanticCapabilities capabilities, 
    						    SemanticBehaviour left, 
    						    SemanticBehaviour right) {
        super();
        this.myCapabilities = capabilities;
        this.addSubBehaviour((Behaviour)left);
        this.addSubBehaviour((Behaviour)right);
        state = START;
    } 
    
    /*********************************************************************/
    /**                         PUBLIC METHODS                          **/
    /*********************************************************************/
    
    
    /**
     * Checks if the behaviour is finished.
     * @return if the behaviour is done, returns true with internal state set at <code>SUCCESS</code> if one 
     * behaviour of the sequence returns <code>SUCCESS</code>, or returns true with
     * internal state set at <code>EXECUTION_FAILURE</code> if one behaviour 
     * returns <code>EXECUTION_FAILURE</code>, or returns true with internal
     * state set at <code>FEASIBILITY_FAILURE</code> if no behaviour returns
     * <code>SUCCESS</code> or <code>EXECUTION_FAILURE</code>. False, in others
     * cases.  
     * @see jade.core.behaviours.CompositeBehaviour#checkTermination(boolean, int)
     */
    @Override
	protected boolean checkTermination(boolean currentDone, int currentResult) {
        if (currentDone) {
            if (currentIndex >= 1) {
                this.setState(((SemanticBehaviour)getCurrent()).getState());
                return true;
            } else if (((SemanticBehaviour)getCurrent()).getState() == FEASIBILITY_FAILURE) {
                currentIndex++;
            } else  {
                this.setState(((SemanticBehaviour)getCurrent()).getState());
                return true;
            }
        }
        return false;
    } 
    
    /**
     * Sets internal state with the given state
     * @param state a state
     */
    public void setState(int state) {
        this.state = state;
    } 
    
    /**
     * Returns the internal state
     * @return the internal state
     */
    public int getState() {
        return state;
    } 

	/* (non-Javadoc)
	 * @see jade.semantics.behaviours.SemanticBehaviour#getMySemanticCapabilities()
	 */
	public SemanticCapabilities getMySemanticCapabilities() {
		return myCapabilities;
	}

	/* (non-Javadoc)
     * @see jade.semantics.behaviours.SemanticBehaviour#getAnnotation(java.lang.String)
     */
    public Object getAnnotation(String key) {
    	return root().getDataStore().get(key);
    }
    
    /* (non-Javadoc)
     * @see jade.semantics.behaviours.SemanticBehaviour#putAnnotation(java.lang.String, java.lang.Object)
     */
    public void putAnnotation(String key, Object value) {
    	root().getDataStore().put(key, value);
    }
    
} 
