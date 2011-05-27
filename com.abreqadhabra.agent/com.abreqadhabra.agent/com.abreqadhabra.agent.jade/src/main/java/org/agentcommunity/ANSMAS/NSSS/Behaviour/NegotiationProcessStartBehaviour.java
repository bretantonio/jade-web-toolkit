package org.agentcommunity.ANSMAS.NSSS.Behaviour;

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
import org.agentcommunity.ANSMAS.NSSS.*;
import org.agentcommunity.ANSMAS.NSSS.ontologies.*;

	public class NegotiationProcessStartBehaviour extends CyclicBehaviour {

		private ContentManager cm = myAgent.getContentManager();
		private MessageTemplate mt;
		private NSAGUI gui; 
		private Codec language = new SLCodec();
		private Ontology nsOntology = NSOntology.getInstance();
		private String messageSequence; // 
		private HashMap requestedNegotiationStrategy = new HashMap();

		public NegotiationProcessStartBehaviour(Agent agent, NSAGUI gui) {
			super(agent);
			this.gui = gui;
		}

		public void action() {

			ACLMessage msg = myAgent.receive(mt);




			if (msg != null) {
			messageSequence = msg.getConversationId();


	try{
	//Receive: 10.Request-Negotiation-Strategy
		if(messageSequence.equals("10.Request-Negotiation-Strategy") || msg.getPerformative() == ACLMessage.INFORM_REF){
		ContentElement ce = cm.extractContent(msg);
			if (ce instanceof NegotiationStrategy) {
				NegotiationStrategy ns = (NegotiationStrategy) ce;
				gui.RMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 3. Negotiation Process Start Protocol\n" + messageSequence + "(INFORM-IF)\n---------------------------------------------------------\n" + msg.toString() +"\n---------------------------------------------------------\n");
				//Send: 11.Respond-Negotiation-Strategy
				ACLMessage respondNegotiatioStrategy = new ACLMessage(ACLMessage.INFORM);
				try{
					requestedNegotiationStrategy = gui.nso;
					NegotiationStrategy rns = (NegotiationStrategy) requestedNegotiationStrategy.get(ns.getUID());
					ns.setUID(rns.getUID());
					ns.setName(rns.getName());
					ns.setAuthor(rns.getAuthor());
					ns.setVersion(rns.getVersion());
					ns.setAlgorithm(rns.getAlgorithm());
					ns.setIssues(rns.getIssues());
					ns.setDescription(rns.getDescription());
					ns.setRelativeURI(rns.getRelativeURI());
					respondNegotiatioStrategy.addReceiver(msg.getSender());
					respondNegotiatioStrategy.setConversationId("11.Respond-Negotiation-Strategy");
					respondNegotiatioStrategy.setLanguage(language.getName());
					respondNegotiatioStrategy.setOntology(nsOntology.getName());
					cm.fillContent(respondNegotiatioStrategy,ns);
					myAgent.send(respondNegotiatioStrategy);
					gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 3. Negotiation Process Start Protocol\n" + respondNegotiatioStrategy.getConversationId() + "(INFORM)\n---------------------------------------------------------\n" + respondNegotiatioStrategy.toString() +"\n---------------------------------------------------------\n");
				}catch(Exception e){
					System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + "/ERROR: Sending message to NSA(R11.Respond-Negotiation-Strategy)\n" + respondNegotiatioStrategy.toString());
					e.printStackTrace();
				}
				//Send: 11.Respond-Negotiation-Strategy
			}else{
			System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + ":Received unknown message!\n"+msg.toString());
			}//if
		}
	//Receive: 10.Request-Negotiation-Strategy


	}catch (Exception ex) {
		ex.printStackTrace();
	}//try
			}else{
			block();
			}//if (respondNegotiatioStrategy != null)
		}//action()

//-------------------------------------------------------------------------




	}//NegotiationProcessStartBehaviour
