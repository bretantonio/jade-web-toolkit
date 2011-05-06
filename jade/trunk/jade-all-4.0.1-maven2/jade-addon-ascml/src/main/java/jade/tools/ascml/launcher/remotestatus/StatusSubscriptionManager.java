/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.launcher.remotestatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import jade.content.abs.AbsAgentAction;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPredicate;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.BasicOntology;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionResponder.Subscription;
import jade.proto.SubscriptionResponder.SubscriptionManager;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.launcher.*;
import jade.tools.ascml.onto.*;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class StatusSubscriptionManager implements SubscriptionManager {
	
	HashMap<String, Vector<Subscription>> subTable;
	AgentLauncher al;
	
	public StatusSubscriptionManager(AgentLauncher al) {
		subTable = new HashMap<String, Vector<Subscription>>();
		this.al=al;
	}
	
	public boolean register(Subscription s) throws RefuseException,
	NotUnderstoodException {
		
		ACLMessage subMessage = s.getMessage();		
		AbsIRE absIota;		
		
		try {
			absIota = (AbsIRE) al.getContentManager().extractAbsContent(subMessage);
			AbsPredicate absEQ = absIota.getProposition();
			AbsConcept absModel = (AbsConcept) absEQ.getAbsObject("left");
			//String modelType = absModel.getTypeName();
			String fqn = absModel.getString("Name");
			//Is this necessary String myFQN = modelType+"::"+fqn;
			if (subTable.containsKey(fqn)) {
				Vector<Subscription> subscribers = subTable.get(fqn);
				subscribers.add(s);
			} else {
				Vector<Subscription> subscribers = new Vector<Subscription>();
				subscribers.add(s);				
				subTable.put(fqn,subscribers);
			}
		} catch (Exception e) {
			throw new NotUnderstoodException(e.getMessage());
		}
		
		return true;
	}
	
	public boolean deregister(Subscription s) throws FailureException {
		ACLMessage subMessage = s.getMessage();		
		AbsIRE absIota;		
		
		try {
			absIota = (AbsIRE) al.getContentManager().extractAbsContent(subMessage);
			AbsPredicate absEQ = absIota.getProposition();
			AbsConcept absModel = (AbsConcept) absEQ.getAbsObject("left");
			String fqn = absModel.getString("Name");
			if (subTable.containsKey(fqn)) {
				Vector<Subscription> subscribers = subTable.get(fqn);
				if (subscribers.contains(s)) {
					subscribers.remove(s);
				} else {
					throw new FailureException("Subscription was not found");
				}
			} else {
				throw new FailureException("Name '"+fqn+"' was not found ");				
			}						
		} catch (Exception e) {
			throw new FailureException(e.getMessage());
		}
		
		return true;
	}
	
	public void notify(IAbstractRunnable model) {
		String fqn = model.getFullyQualifiedName();
		
		if (subTable.containsKey(fqn)) {		
			Vector<Subscription> subscribers = subTable.get(fqn);
			Iterator<Subscription> subIt = subscribers.iterator();
			while (subIt.hasNext()) {
				Subscription s = subIt.next();
				try {				
					ACLMessage msg = s.getMessage().createReply();
					
					msg.setPerformative(ACLMessage.INFORM);
					AbsPredicate absEquals = new AbsPredicate(SLVocabulary.EQUALS);
					absEquals.set(SLVocabulary.EQUALS_LEFT, al.getContentManager().extractAbsContent(s.getMessage()));
					Status modelStatus = model.getStatus();
					modelStatus.setDetailedStatus(model.getDetailedStatus());
					absEquals.set(SLVocabulary.EQUALS_RIGHT, ASCMLOntology.getInstance().fromObject(modelStatus));
					
					al.getContentManager().fillContent(msg, absEquals);
					s.notify(msg);
				} catch (Exception e) {
					e.printStackTrace();      
				}				
			}
		}
	}
	
}
