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


package jade.tools.ascml.launcher.remoteactions;

import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.absmodel.IRunnableRemoteSocietyInstanceReference;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.*;
/*
public class RemoteStopperThread extends AbstractMARWaitThread {
	
	public RemoteStopperThread(IAbstractRunnable ar, AgentLauncher al, long timeout) {
		super(ar, al, timeout);
		t.start();
	}
	
	public void run() {
		if (ar instanceof IRunnableRemoteSocietyInstanceReference) {
			IRunnableRemoteSocietyInstanceReference remoteRef = (IRunnableRemoteSocietyInstanceReference) ar;
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			AID receiver = new AID(remoteRef.getLauncher().getName(), AID.ISGUID);
			
			msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
			
			msg.setOntology(ASCMLOntology.ONTOLOGY_NAME);
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			
			//TODO: What to do if we have multiple adresses
			receiver.addAddresses(remoteRef.getLauncher().getAddresses()[0]); 
			
			// for(int t =0;t<soc.getLauncherAddresses().length;t++)
			// System.out.println(soc.getLauncherAddresses()[t]);
			
			msg.addReceiver(receiver);
			jade.content.onto.basic.Action contentAction = new jade.content.onto.basic.Action();
			
			Stop action = new Stop();
			action.setActor(receiver);
			SocietyInstance newsoc = new SocietyInstance();
			
			// System.out.println("DispatcherThread is sending MSG to : " + receiver + " to start:");
			// System.out.print("     ");
			newsoc.setFullQuallifiedName(remoteRef.getFullyQualifiedName());
			
			action.addModels(newsoc);
			
			contentAction.setAction(action);
			contentAction.setActor(al.getAID());
			
			try {
				al.getContentManager().setValidationMode(true);
				al.getContentManager().fillContent(msg, contentAction);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//al.StartRemoteSociety(msg, this);	
		}
	}
	
	
}
*/