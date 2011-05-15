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
 * SequenceBehaviour.java
 * Created on 8 déc. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;

/**
 * Class that represents the behaviour associated with a sequence semantic
 * action.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class SequenceBehaviour extends  SequentialBehaviour implements SemanticBehaviour {
	
	
    /**
     * State of the behaviour
     */
    int state;
    
    /**
     * Index in the list of behaviours
     */
    int currentIndex;
    
	/**
	 * The capabilities of the holder agent.
	 */
	private SemanticCapabilities myCapabilities;

	/*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a Sequence Behaviour.
     * @param leftAction the left action of the sequence
     * @param rightAction the right action of the sequence
     */
    public SequenceBehaviour(SemanticCapabilities capabilities,
    						 SemanticBehaviour left, 
    				 	     SemanticBehaviour right) {
        super();
        this.addSubBehaviour((Behaviour)left);
        this.addSubBehaviour((Behaviour)right);
        this.myCapabilities = capabilities;
        state = START;
        currentIndex = 0;
    } 
    
    /*********************************************************************/
    /**                         PUBLIC METHODS                          **/
    /*********************************************************************/
    
    /**
     * Checks if this behaviour is done.
     * First starts the behaviour for the the left part of the sequence.
     * If the behaviour ends up with execution failure, or feasibility failure,
     * the final result is respectively execution failure or feasibility failure. 
     * If the behaviour ends up with success, the right part of the alternative is 
     * considered and the result of the behaviour is the final result. 
     * @see jade.core.behaviours.CompositeBehaviour#checkTermination(boolean, int)
     */
    @Override
	protected boolean checkTermination(boolean currentDone, int currentResult) {
        if (currentDone) {
            if (((SemanticBehaviour)getCurrent()).getState() == SUCCESS) {
                if (currentIndex >= 1) {
                    this.setState(SUCCESS);
                    return true;
                } 
                //else {
                    currentIndex++;
                //} 
            } else {
                this.setState(((SemanticBehaviour)getCurrent()).getState());
                return true;
            }
        }
        return false;
    } 
    
    /**
     * Sets the state of the behaviour
     * @param state the state
     */
    public void setState(int state) {
        this.state = state;
    } 
    
    /**
     * Returns the state of this behaviour
     * @return the state of this behaviour
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