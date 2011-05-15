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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.absmodel.IRunnableAgentInstance;
import jade.tools.ascml.absmodel.IRunnableSocietyInstance;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.ASCMLOntology;
import jade.tools.ascml.onto.AbsModel;
import jade.tools.ascml.onto.AgentInstance;
import jade.tools.ascml.onto.AgentType;
import jade.tools.ascml.onto.SocietyInstance;
import jade.tools.ascml.onto.Start;
import jade.tools.ascml.onto.Starting;
import jade.tools.ascml.onto.Stop;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 *
 */
public class RemoteActionRequestListener extends CyclicBehaviour implements ModelChangedListener {

	private MessageTemplate myTemplate;
	private AgentLauncher launcher;
	private HashMap<IAbstractRunnable,Vector<ACLMessage>> runnable2startMap;
	private HashMap<IAbstractRunnable,Vector<ACLMessage>> runnable2stopMap;
	
	public RemoteActionRequestListener(AgentLauncher launcher) {
		this.launcher = launcher;
        myTemplate = MessageTemplate.MatchOntology(ASCMLOntology.ONTOLOGY_NAME);
		myTemplate = MessageTemplate.and(myTemplate, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_QUERY));
		myTemplate = MessageTemplate.and(myTemplate, MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
		runnable2startMap = new HashMap<IAbstractRunnable,Vector<ACLMessage>>();
	}
	
	private ACLMessage createResonse (ACLMessage request) {
		ACLMessage response = request.createReply();
		response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		try
		{
			ContentElement ce = launcher.getContentManager().extractContent(request);
			//Nachricht dekodieren
			Action a = (Action)ce;
			List models = new ArrayList();
			HashMap<IAbstractRunnable,Vector<ACLMessage>> map2use;
			if (a.getAction() instanceof Start) {
				Start s = (Start)a.getAction();
				models = s.getModels();
				map2use = runnable2startMap;				
			} else if (a.getAction() instanceof Stop) {
				Stop s= (Stop)a.getAction();
				models= s.getModels();
				map2use = runnable2stopMap;
			} else {
				//Etwas anderes? Kennen wir nicht -> Not Understood schicken
				response.setContent("Neither Start nor Stop");
				response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				return response;
			}
						
			//Wir haben die richtige Nachricht bekommen
			//Hat sie einen sinnvollen Inhalt (sind agenten angegeben?)
			if(models.size()>0)
			{
				AbsModel m = (AbsModel)models.get(0);
				//Überprüfung:
				//Welchen Typ hat das Model?
				if(m instanceof AgentInstance)
				{
					response.setPerformative(ACLMessage.AGREE);
					AgentInstance ai = (AgentInstance)m;
					IRunnableAgentInstance[] instances = launcher.getRepository().createRunnableAgentInstance(ai.getFullQuallifiedName(),1);
					Vector <ACLMessage> messageVector;
					if (map2use.containsKey(instances[0])) {
						messageVector = map2use.get(instances[0]);
					} else {
						messageVector = new Vector<ACLMessage>();
					}
					messageVector.add(request);
					map2use.put(instances[0],messageVector);
					if (a.getAction() instanceof Start) {					
						launcher.getDependencyManager().startThisAgent(instances[0]);
					} else {
						launcher.getDependencyManager().stopThisAgent(instances[0]);
					}

				}
				else if(m instanceof AgentType)
				{
					response.setPerformative(ACLMessage.AGREE);
					AgentType at = (AgentType)m;
					String fqn = at.getFullQuallifiedName() + request.getSender().getLocalName() + Long.toString(System.currentTimeMillis());
					IRunnableAgentInstance[] instances = launcher.getRepository().createRunnableAgentInstance(fqn,1);
					Vector <ACLMessage> messageVector;
					if (map2use.containsKey(instances[0])) {
						messageVector = map2use.get(instances[0]);
					} else {
						messageVector = new Vector<ACLMessage>();
					}
					messageVector.add(request);
					map2use.put(instances[0],messageVector);
					if (a.getAction() instanceof Start) {					
						launcher.getDependencyManager().startThisAgent(instances[0]);
					} else {
						launcher.getDependencyManager().stopThisAgent(instances[0]);
					}
				}
				else if(m instanceof SocietyInstance)
				{
					response.setPerformative(ACLMessage.AGREE);
					SocietyInstance sc = (SocietyInstance) m;
					IRunnableSocietyInstance rsoc = launcher.getRepository().createRunnableSocietyInstance(sc.getFullQuallifiedName());
					Vector <ACLMessage> messageVector;
					if (map2use.containsKey(rsoc)) {
						messageVector = map2use.get(rsoc);
					} else {
						messageVector = new Vector<ACLMessage>();
					}
					messageVector.add(request);
					map2use.put(rsoc,messageVector);
					if (a.getAction() instanceof Start) {					
						launcher.getDependencyManager().startThisSociety(rsoc);
					} else {
						launcher.getDependencyManager().stopThisSociety(rsoc);
					}

				}
				else
				{
					//Etwas anderes? Kennen wir nicht -> Not Understood schicken
					response.setContent("Neither StartAgent nor StartSociety nor StartType");
					response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				}
			}
			else
			{
				//Wir versthen die Nachricht zwar, aber sie hat keinen sinnvollen Inhalt
				response.setContent("Neither StartAgent nor StartSociety nor StartType");
				response.setPerformative(ACLMessage.REFUSE);
			}
			return response;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("MARListener: Msg not understood:"+request.getContent());
			//Wir haben die Nachricht nicht verstanden -> Default abschicken
			response.setContent(e.getMessage());
			return response;
		}
	}

	public void action() {
        ACLMessage request = launcher.receive(myTemplate);
        if (request!=null) {
			ACLMessage response = createResonse(request);
			launcher.send(response);
        }
        block();
	}


	public void modelChanged(ModelChangedEvent evt) {
		if (evt.getEventCode() == ModelChangedEvent.STATUS_CHANGED) {			
			if (evt.getModel() instanceof IAbstractRunnable) {
				IAbstractRunnable absRunnable = (IAbstractRunnable)evt.getModel();
				if (absRunnable.getStatus().equals(new Starting())) {
					return;
				}
				if (runnable2startMap.containsKey(absRunnable)) {
					Vector<ACLMessage> toRespond = runnable2startMap.get(absRunnable);
					//FIXME: Ist der Status Dead() oder Running() oder Functional() oder Error()
					//Dann benachritige
					/*try {
						doAction();
						Iterator it = message.getAllReceiver();
			            while (it.hasNext()) {
			                jade.core.AID recv = (jade.core.AID)it.next();
			            }
						jade.content.onto.basic.Done done = new jade.content.onto.basic.Done();
						done.setAction(action);

						al.getContentManager().fillContent(message, done);
						message.setPerformative(ACLMessage.INFORM);
						System.out.println("AbstractMARThread.run: replying with INFORM-Done");
						al.send(message);
					} catch (Exception e) {
						e.printStackTrace();
						message.setContent(e.getMessage());
						al.send(message);
					}*/					
				}
			}
		}
	}
}
