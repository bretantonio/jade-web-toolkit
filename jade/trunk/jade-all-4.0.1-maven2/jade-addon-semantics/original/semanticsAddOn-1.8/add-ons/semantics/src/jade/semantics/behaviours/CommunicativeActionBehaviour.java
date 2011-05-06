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
 * CommunicativeActionBehaviour.java
 * Created on 28 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.util.Logger;

/**
 * Used to gather the principles used by communicative action behaviours.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/28 Revision: 1.0
 */
public class CommunicativeActionBehaviour extends PrimitiveActionBehaviour {
	
	
	public CommunicativeActionBehaviour(CommunicativeAction action) {
		super(action);
	}
        
    /**
     * Sends an ACL message if the feasibility precondition of the action is 
     * satisfied. Stores in the belief base, the postcondition of the action
     * and the fact that the agent has done the action.
     * @see jade.core.behaviours.Behaviour#action()
     */
    @Override
	public void doAction() {
        if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "Trying the behaviour of " + ACLMessage.getPerformative(((CommunicativeAction)action).getSurfacePerformative()) + " ON " + ((CommunicativeAction)action).getSurfaceContent());
        try {          
        	getMySemanticCapabilities().sendCommunicativeAction((CommunicativeAction)action);
//        	myAgent.send(((CommunicativeAction)action).toAclMessage());
        	if (logger.isLoggable(Logger.FINEST)) logger.log(Logger.FINEST, "ACL Message has been sent!");                 
        	
        	if (((CommunicativeAction)action).getReplyBy() != null) {
        		root().getDataStore().put(REPLY_BY_KEY, ((CommunicativeAction)action).getReplyBy());
        	}
        	state = SUCCESS;
        } catch (Exception e) { 
        	state = EXECUTION_FAILURE;
        	if (logger.isLoggable(Logger.FINEST)) {
        		logger.log(Logger.FINEST, "Failed in sending the ACL Message");
        		logger.log(Logger.FINEST, "-> Behaviour ended with EXECUTION_FAILURE");
        	}
        }
    }
       
}
