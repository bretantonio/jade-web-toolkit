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
 * SemanticBehaviour.java
 * Created on 23 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.semantics.interpreter.SemanticCapabilities;

/**
 * The interface for all Semantic Behaviours.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/23 Revision: 1.0
 */
public interface SemanticBehaviour {

    
    /**
     * Start value
     */
    public static final int START = -1;

    /**
     * Ends with success
     */
    public static final int SUCCESS = 0;
    
    /**
     * Ends with execution failure
     */
    public static final int EXECUTION_FAILURE = 1;
    
    /**
     * Ends with feasibility failure
     */
    public static final int FEASIBILITY_FAILURE = 2;
    
    /**
     * Cancelled
     */
    public static final int CANCELLATION = 3;

    /**
     * Is running
     */
    public static final int RUNNING = 4; //3

    public static final String WAITING_BEHAVIOUR_KEY = "waiting";
    public static final String IN_REPLY_TO_KEY = "incomingMessage";
    public static final String FAILURE_REASON_KEY = "reason";
    /**
     * Returns the execution state of the behaviour
     * @return <code>SUCCESS</code>, <code>FEASIBILITY_FAILURE</code>, 
     * or <code>EXECUTION_FAILURE</code>
     */
    public int getState();

    /**
     * Sets the execution state of the behaviour
     * @param state <code>SUCCESS</code>, <code>FEASIBILITY_FAILURE</code>, 
     * or <code>EXECUTION_FAILURE</code>
     */
    public void setState(int state);

    /**
     * @return the semantic capabilities of the semantic agent that owns this
     *         behaviour
     */
    public SemanticCapabilities getMySemanticCapabilities();
    
    public void putAnnotation(String key, Object value);
    public Object getAnnotation(String key);
}
