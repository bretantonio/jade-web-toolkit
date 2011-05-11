package org.agentcommunity.ANSMAS.NCS.NMABehaviour;

import java.util.*;

import jade.core.*;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import jade.content.onto.*;
import jade.content.onto.basic.*;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;

import jade.domain.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.FIPAAgentManagement.*;

import org.agentcommunity.ANSMAS.*;
import org.agentcommunity.ANSMAS.NCS.*;
import org.agentcommunity.ANSMAS.NCS.ontologies.*;

	public class NegotiationProcessStartBehaviour extends CyclicBehaviour {

		private ContentManager cm = myAgent.getContentManager();
		private MessageTemplate mt;
		private NMAGUI gui; 
		private Codec language = new SLCodec();
		private Ontology niOntology = NIOntology.getInstance();
		private String messageSequence = "8.Request-Negotiation-Process-Start"; // 

		private HashMap requestedNegotiationInformation = new HashMap();
		int step;

		public NegotiationProcessStartBehaviour(Agent agent, NMAGUI gui) {
			super(agent);
			this.gui = gui;
		}

		public void action() {

//Send: 8.Request-Negotiation-Process-Start

			if(messageSequence.equals("8.Request-Negotiation-Process-Start")){

				ACLMessage requestNegotiationProcessStart = new ACLMessage(ACLMessage.REQUEST_WHEN);
	try{
		requestedNegotiationInformation = gui.nsr;
		NegotiationInformation rni = (NegotiationInformation) requestedNegotiationInformation.get("001");
		NegotiationInformation ni = new NegotiationInformation();

				ni.setUID(rni.getUID());
				ni.setCatalogId(rni.getCatalogId());
				ni.setUnspscId(rni.getUnspscId());
				ni.setProviderId(rni.getProviderId());
				ni.setProviderStrategyUid(rni.getProviderStrategyUid());
				ni.setDeciderId(rni.getDeciderId());
				ni.setDeciderStrategyUid(rni.getDeciderStrategyUid());
				ni.setResult(rni.getResult());

///////////////////////
					requestNegotiationProcessStart.addReceiver(new AID("NCA",AID.ISLOCALNAME));
					requestNegotiationProcessStart.setConversationId("8.Request-Negotiation-Process-Start");
					requestNegotiationProcessStart.setLanguage(language.getName());
					requestNegotiationProcessStart.setOntology(niOntology.getName());
					cm.fillContent(requestNegotiationProcessStart,ni);
					myAgent.send(requestNegotiationProcessStart);

			gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 3. Negotiation Process Start Protocol\n" + requestNegotiationProcessStart.getConversationId() + "(REQUEST-WHEN)\n---------------------------------------------------------\n" + requestNegotiationProcessStart.toString() +"\n---------------------------------------------------------------------------\n");
messageSequence = "8n-Process-Start";

	}catch(Exception e){
		System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + "/ERROR: Sending message to NMA(requestNegotiationProcessStart)\n" + requestNegotiationProcessStart.toString());
		e.printStackTrace();
	}
						//Send: 8.Request-Negotiation-Process-Start
		
			}//if(messageSequence.equals("8.Request-Negotiation-Process-Start"))
		}//action()

//-------------------------------------------------------------------------




	}//NegotiationProcessStartBehaviour
