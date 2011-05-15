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
 * PrimitiveBehaviour.java
 * Created on 3 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;


import jade.semantics.actions.CommunicativeAction;
import jade.util.Logger;

/**
 * Class that represents the behaviour associated with a primitive semantic
 * action (Inform, Confirm, Disconfirm, or Request).
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class PrimitiveBehaviour extends CommunicativeActionBehaviour {
    
	
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a PrimitiveBehaviour.
     * @param action the semantic action to which this behaviour belongs
     */
    public PrimitiveBehaviour(CommunicativeAction action) {
        super(action);
    } 
    
    /*********************************************************************/
    /**				 			PUBLIC METHODS							**/
    /*********************************************************************/
    
    /**
     * @return true if the feasibility preconditon are statisfied, false if not.
     * @throws Exception if any exception occurs
     * @see jade.core.behaviours.Behaviour#action()
     */
    public boolean compute() throws Exception {
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Cheking Feasibility Precondition: " + action.getFeasibilityPrecondition());
        return (getMySemanticCapabilities().getMyKBase().query(action.getFeasibilityPrecondition()) != null);
    }
    
} 
