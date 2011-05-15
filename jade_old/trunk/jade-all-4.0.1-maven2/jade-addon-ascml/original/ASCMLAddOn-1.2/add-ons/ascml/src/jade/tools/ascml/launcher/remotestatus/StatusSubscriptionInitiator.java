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

import jade.content.abs.AbsObject;
import jade.content.abs.AbsPredicate;
import jade.content.lang.sl.SLVocabulary;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.ASCMLOntology;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.onto.Stopping;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class StatusSubscriptionInitiator extends SubscriptionInitiator {

	AgentLauncher launcher;
	IAbstractRunnable model;
	AID receiver;

	public StatusSubscriptionInitiator(AgentLauncher launcher, ACLMessage msg, IAbstractRunnable model) {
		super(launcher, msg);
		this.model=model;
		this.launcher=launcher;
		this.receiver=(AID) msg.getAllReceiver().next();
	}	
	
	@Override
	protected void handleInform(ACLMessage inform) {
		//FIXME: if the new status is stopped, remove the ssi
		AbsPredicate absEquals;
		try {
			absEquals = (AbsPredicate)  launcher.getContentManager().extractAbsContent(inform);
			AbsObject absStatus = absEquals.getAbsObject(SLVocabulary.EQUALS_RIGHT);
			Status ms = (Status) ASCMLOntology.getInstance().toObject(absStatus);		
			model.setStatus(ms);
			if (ms.equals(new Stopping())) {
				//TODO: We should cancel the subscription
				launcher.removeBehaviour(this);
			}
		} catch (Exception e) {
		}		
		super.handleInform(inform);
	}
	
	public void cancel() {
		super.cancel(receiver,true);
	}

}
