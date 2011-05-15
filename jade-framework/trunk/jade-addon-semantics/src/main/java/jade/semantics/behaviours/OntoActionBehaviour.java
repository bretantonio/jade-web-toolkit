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
 * OntoActionBehaviour.java
 * Created on 15 déc. 2004
 * Author : louisvi
 */
package jade.semantics.behaviours;

import jade.semantics.actions.OntologicalAction;

/**
 * Class that represents a behaviour for an ontological action.
 * @author Vincent Louis - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class OntoActionBehaviour extends PrimitiveActionBehaviour {
	    
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates an OntoActionBehaviour.
     * @param action the semantic action to which this behaviour belongs
     */
    public OntoActionBehaviour(OntologicalAction action) {
        super(action);
    } 
    
    /*********************************************************************/
    /**				 			PUBLIC METHODS							**/
    /*********************************************************************/
    
    /**
     * If the action is feasible; the action is performed and the belief of 
     * feasibility precondition and belief of postcondition are considered as
     * internal events (and so are interpreted by the agent).
     * @see jade.core.behaviours.Behaviour#action()
     */
    @Override
	public void doAction() {
        try {
//        	System.err.println("onto action behaviour de "+action);
            ((OntologicalAction)action).perform(this);
//            System.err.println("state=(0:success,1:execfail,2:feasibfail,3:run) -> "+state);
        } catch (Exception e) {
        	// FIXME FOR DEBUG ONLY
        	System.err.println("onto action behaviour, state=failure, e="+e.getStackTrace());
        	e.printStackTrace();
        	// FIXME END DEBUG
        	state = EXECUTION_FAILURE;
            return;
        }
    } 
        
} 
