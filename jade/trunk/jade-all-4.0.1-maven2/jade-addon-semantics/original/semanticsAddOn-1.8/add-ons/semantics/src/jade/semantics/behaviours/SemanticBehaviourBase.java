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
 * SemanticBehaviourBase.java
 * Created on 16 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.core.behaviours.Behaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;
import jade.util.Logger;

/**
 * Abstract class that represents a basic SemanticBehaviour. This class extends 
 * jade.core.behaviours.Behaviour and 
 * add a state to the behaviour, which indicates the final execution state 
 * (SUCCESS, FEASIBILITY_FAILURE, or EXECUTION_FAILURE) of it.  
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public abstract class SemanticBehaviourBase extends Behaviour implements SemanticBehaviour {
    
    /**
     * Pattern used to identify an agent
     */
    public static Term agentPattern = SL.term("(agent-identifier :name ??agent)");		
    
    /**
     * Execution state of the behaviour
     */
    public int state;
    
    /**
     * Logger
     */
    protected Logger logger;
    
    
	/**
	 * The capabilities of the holder agent.
	 */
	protected SemanticCapabilities myCapabilities;

	/*********************************************************************/
    /**				 		  DEFAULT CONSTRUCTOR						**/
    /*********************************************************************/
    
    /**
     * Creates a Semantic Behaviour.
     */
    public SemanticBehaviourBase(SemanticCapabilities capabilities) {
        super();
        logger = Logger.getMyLogger("jade.core.semantics.behaviours.SemanticBehaviour");
        this.myCapabilities = capabilities;
        state = START;
    } // End of SemanticBehaviour/0
    
    /*********************************************************************/
    /**				 			PUBLIC METHODS							**/
    /*********************************************************************/
    
    /**
     * Runs the behaviour. This abstract method must be implemented by
     * <code>SemanticBehaviour</code> subclasses to perform ordinary behaviour
     * duty.
     */
    @Override
	public abstract void action();
    
    /**
     * Checks if this behaviour is done.
     * @return true if the internal state equals<code>SUCCESS</code>, <code>FEASIBILITY_FAILURE</code>, 
     * or <code>EXECUTION_FAILURE</code>
     */
    @Override
	public boolean done() {
        return (state == SUCCESS || state == EXECUTION_FAILURE
        		|| state == FEASIBILITY_FAILURE || state == CANCELLATION);
    } // End of done/0
    
    /**
     * Returns the execution state of the behaviour
     * @return <code>SUCCESS</code>, <code>FEASIBILITY_FAILURE</code>, 
     * or <code>EXECUTION_FAILURE</code>
     */
    public int getState() {
        return state;
    } 
    
    /**
     * Sets the execution state of the behaviour
     * @param state <code>SUCCESS</code>, <code>FEASIBILITY_FAILURE</code>, 
     * or <code>EXECUTION_FAILURE</code>
     */
    public void setState(int state) {
        this.state = state;
    } 
    
    /* (non-Javadoc)
     * @see jade.core.behaviours.Behaviour#reset()
     */
    @Override
	public void reset() {
    	super.reset();
    	state = START;
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
