/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

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


package jade.core.persistence;

//#MIDP_EXCLUDE_FILE

import jade.core.ContainerID;
import jade.core.ServiceNotActiveException;

import jade.content.onto.Ontology;
import jade.content.Concept;
import jade.content.Predicate;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.content.onto.basic.Result;

import jade.domain.RequestManagementBehaviour;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.UnsupportedFunction;
import jade.domain.persistence.*;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.security.JADESecurityException;



/**
   This behaviour serves the actions of the persistent management
   ontology supported by the AMS.

   @author Giovanni Rimassa - FRAMeTech
 */
public class PersistenceManagementBehaviour extends RequestManagementBehaviour {

	public PersistenceManagementBehaviour() {
		// Matches all REQUEST messages with the JADE-Persistence ontology
		super(null, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchOntology(PersistenceVocabulary.NAME)));
	}

	public void onStart() {
		super.onStart();
		if(firstTime) {
			firstTime = false;

			// Register the persistence ontology, if it's not already there
			Ontology o = myAgent.getContentManager().lookupOntology(PersistenceVocabulary.NAME);
			if(o == null) {
				myAgent.getContentManager().registerOntology(PersistenceOntology.getInstance());
			}
		}
	}

	protected ACLMessage performAction(Action slAction, ACLMessage request) throws JADESecurityException, FIPAException {

		Concept action = slAction.getAction();
		Object result = null;
		boolean resultNeeded = false;


		if(action instanceof LoadAgent) {
			LoadAgent la = (LoadAgent)action;
			try {
				PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
				h.loadAgent(la.getAgent(), la.getRepository(), la.getWhere());
			}
			catch(ServiceNotActiveException snae) {
				// The persistence service is not installed. Abort operation.
				throw new FailureException("-- Persistence service not active --");
			}
			catch(Exception e) {
				// Some other error occurred. Throw a FIPA Failure exception.
				e.printStackTrace();
				throw new FailureException("load-agent failed [" + e.getMessage() + "]");
			}
		}
		else if(action instanceof ReloadAgent) {
			ReloadAgent ra = (ReloadAgent)action;
			if (myAgent.getAID().equals(ra.getAgent())) {
				throw new FailureException("-- The AMS cannot be reloaded --");
			}
			else {
				try {
					PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
					h.reloadAgent(ra.getAgent(), ra.getRepository());
				}
				catch(ServiceNotActiveException snae) {
					// The persistence service is not installed. Abort operation.
					throw new FailureException("-- Persistence service not active --");
				}
				catch(Exception e) {
					// some other error occurred. Throws a FIPA Failure exception.
					e.printStackTrace();
					throw new FailureException("reload-agent failed [" + e.getMessage() + "]");
				}
			}
		}
		else if(action instanceof SaveAgent) {
			SaveAgent sa = (SaveAgent)action;
			if (myAgent.getAID().equals(sa.getAgent())) {
				throw new FailureException("-- The AMS cannot be saved --");
			}
			else {
				try {
					PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
					h.saveAgent(sa.getAgent(), sa.getRepository());
				}
				catch(ServiceNotActiveException snae) {
					// The persistence service is not installed. Abort operation.
					throw new FailureException("-- Persistence service not active --");
				}
				catch(Exception e) {
					// Some other error occurred. Throw a FIPA Failure exception.
					e.printStackTrace();
					throw new FailureException("save-agent failed [" + e.getMessage() + "]");
				}
			}
		}
		else if(action instanceof DeleteAgent) {
			DeleteAgent da = (DeleteAgent)action;
			try {
				PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
				h.deleteAgent(da.getAgent(), da.getRepository(), da.getWhere());
			}
			catch(ServiceNotActiveException snae) {
				// The persistence service is not installed. Abort operation.
				throw new FailureException("-- Persistence service not active --");
			}
			catch(Exception e) {
				// Some other error occurred. Throw a FIPA Failure exception.
				e.printStackTrace();
				throw new FailureException("delete-agent failed [" + e.getMessage() + "]");
			}
		}
		else if(action instanceof FreezeAgent) {
			FreezeAgent fa = (FreezeAgent)action;	
			if (myAgent.getAID().equals(fa.getAgent())) {
				throw new FailureException("-- The AMS cannot be frozen --");
			}
			else {
				try {
					PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
					h.freezeAgent(fa.getAgent(), fa.getRepository(), fa.getBufferContainer());
				}
				catch(ServiceNotActiveException snae) {
					// The persistence service is not installed. Abort operation.
					throw new FailureException("-- Persistence service not active --");
				}
				catch(Exception e) {
					// Some other error occurred. Throw a FIPA Failure exception.
					e.printStackTrace();
					throw new FailureException("freeze-agent failed [" + e.getClass().getName() + ": " + e.getMessage() + "]");
				}
			}
		}
		else if(action instanceof ThawAgent) {
			ThawAgent ta = (ThawAgent)action;
			try {
				PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
				h.thawAgent(ta.getAgent(), ta.getRepository(), ta.getNewContainer());
			}
			catch(ServiceNotActiveException snae) {
				// The persistence service is not installed. Abort operation.
				throw new FailureException("-- Persistence service not active --");
			}
			catch(Exception e) {
				// Some other error occurred. Throw a FIPA Failure exception.
				e.printStackTrace();
				throw new FailureException("thaw-agent failed [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			}
		}
		else if(action instanceof SaveContainer) {
			SaveContainer sc = (SaveContainer)action;
			if (myAgent.here().equals(sc.getContainer())) {
				throw new FailureException("-- The Main Container cannot be saved --");
			}
			else {
				try {
					PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
					h.saveContainer(sc.getContainer(), sc.getRepository());
				}
				catch(ServiceNotActiveException snae) {
					// The persistence service is not installed. Abort operation.
					throw new FailureException("-- Persistence service not active --");
				}
				catch(Exception e) {
					// Some other error occurred. Throw a FIPA Failure exception.
					e.printStackTrace();
					throw new FailureException("save-container failed [" + e.getClass().getName() + ": " + e.getMessage() + "]");
				}
			}
		}
		else if(action instanceof LoadContainer) {
			LoadContainer lc = (LoadContainer)action;			
			if (myAgent.here().equals(lc.getContainer())) {
				throw new FailureException("-- The Main Container cannot be loaded --");
			}
			else {
				try {
					PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
					h.loadContainer(lc.getContainer(), lc.getRepository());
				}
				catch(ServiceNotActiveException snae) {
					// The persistence service is not installed. Abort operation.
					throw new FailureException("-- Persistence service not active --");
				}
				catch(Exception e) {
					// Some other error occurred. Throw a FIPA Failure exception.
					e.printStackTrace();
					throw new FailureException("load-container failed [" + e.getClass().getName() + ": " + e.getMessage() + "]");
				}
			}
		}
		else if(action instanceof DeleteContainer) {
			try {
				DeleteContainer dc = (DeleteContainer)action;

				PersistenceHelperImpl h = (PersistenceHelperImpl)myAgent.getHelper(PersistenceHelper.NAME);
				h.deleteContainer(dc.getContainer(), dc.getWhere(), dc.getRepository());
			}
			catch(ServiceNotActiveException snae) {
				// The persistence service is not installed. Abort operation.
				throw new FailureException("-- Persistence service not active --");
			}
			catch(Exception e) {
				// Some other error occurred. Throw a FIPA Failure exception.
				e.printStackTrace();
				throw new FailureException("delete-container failed [" + e.getClass().getName() + ": " + e.getMessage() + "]");
			}
		}
		else if(action instanceof LoadAgentGroup) {
			throw new UnsupportedFunction("load-agent-group");
		}
		else if(action instanceof SaveAgentGroup) {
			throw new UnsupportedFunction("save-agent-group");
		}
		else if(action instanceof DeleteAgentGroup) {
			throw new UnsupportedFunction("delete-agent-group");
		}
		else {
			throw new UnsupportedFunction();
		}

		// Prepare the notification
		ACLMessage notification = request.createReply();
		notification.setPerformative(ACLMessage.INFORM);
		Predicate p = null;
		if (resultNeeded) {
			// The action produced a result
			p = new Result(slAction, result);
		}
		else {
			p = new Done(slAction);
		}
		try {
			myAgent.getContentManager().fillContent(notification, p);
		}
		catch (Exception e) {
			// Should never happen
			e.printStackTrace();
		}

		return notification;
	}

	private boolean firstTime = true;

}
