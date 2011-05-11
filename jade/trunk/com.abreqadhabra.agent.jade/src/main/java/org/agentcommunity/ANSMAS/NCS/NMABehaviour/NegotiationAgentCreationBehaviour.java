package org.agentcommunity.ANSMAS.NCS.NMABehaviour;

import java.util.*;

import jade.core.*;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import jade.content.onto.*;
import jade.content.onto.basic.*;

import jade.lang.acl.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;

import jade.domain.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.mobility.*;
import jade.domain.JADEAgentManagement.*;

import org.agentcommunity.ANSMAS.*;
import org.agentcommunity.ANSMAS.NCS.*;
import org.agentcommunity.ANSMAS.NCS.ontologies.*;

	public class NegotiationAgentCreationBehaviour extends CyclicBehaviour {

		private NMAGUI gui; 
		private String messageSequence; // 
		private ContentManager cm = myAgent.getContentManager();
		private MessageTemplate mt;
		private int step=0;
		private String ni_uid;
private NegotiationInformation ni;
   		private Ontology niOntology = NIOntology.getInstance();


		public NegotiationAgentCreationBehaviour(Agent agent, NMAGUI gui) {
			super(agent);
			this.gui = gui;
		}

	public void action() {

		ACLMessage msg = myAgent.receive(mt);



		if (msg != null && msg.getConversationId() !=null) {
		messageSequence = msg.getConversationId();


			try{
			//Receive: 2.Respond-NAES-Locations
				if(messageSequence.equals("2.Respond-NAES-Locations") && msg.getPerformative() == ACLMessage.INFORM){
						
										ContentElement ce = cm.extractContent(msg);
			
					if (ce instanceof NegotiationInformation) {
					ni = (NegotiationInformation) ce;	
					}

							gui.RMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 1. Negotiation Agent Creation Protocol\n"+ msg.getConversationId() +"\n---------------------------------------------------------\n" + msg.toString() +"\n---------------------------------------------------------\n");
								step=1;
				}else{

				}

			}catch (Exception ex) {
			ex.printStackTrace();
			}//try
	
		switch (step){
		case 1:
		//Send: 3.Request-NA-Creation	
		requestNACreation(ni);
		break;

		}	

		}else{
		block();
		}




	}//action()

//-------------------------------------------------------------------------
public void requestNACreation(NegotiationInformation n){
		//Send: 3.Request-NA-Creation

				ACLMessage requestNACreation = new ACLMessage(ACLMessage.REQUEST_WHEN);

							try{

								requestNACreation.addReceiver(new AID("MCA",AID.ISLOCALNAME));
								requestNACreation.setConversationId("3.Request-NA-Creation");
								requestNACreation.setLanguage(new SLCodec().getName());
								requestNACreation.setOntology(niOntology.getName());
								cm.fillContent(requestNACreation,n);
								myAgent.send(requestNACreation);

								gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 1. Negotiation Agent Creation Protocol\n" + requestNACreation.getConversationId() + "(REQUEST_WHEN)\n---------------------------------------------------------\n" + requestNACreation.toString() +"\n---------------------------------------------------------------------------\n");
								
		
									}catch(Exception e){
								System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + "/ERROR: Sending message to NMA(startNegotiation)\n" + requestNACreation.toString() + "\n");
								e.printStackTrace();
							}

}

	}//NegotiationAgentCreationBehaviour
