	package org.agentcommunity.ANSMAS.NAES.Behaviour;

	import jade.core.*;
	import jade.core.Agent;
	import jade.lang.acl.ACLMessage;
	import jade.lang.acl.MessageTemplate;
	import jade.core.behaviours.CyclicBehaviour;
	import jade.domain.*;
	import jade.domain.FIPANames.InteractionProtocol;
	import jade.domain.FIPAAgentManagement.*;
	import jade.content.*;
	import jade.content.lang.*;
	import jade.content.lang.sl.*;
	import jade.content.onto.*;
	import jade.content.onto.basic.*;
//	import jade.util.leap.*;
	import org.agentcommunity.ANSMAS.*;
	import org.agentcommunity.ANSMAS.NCS.*;
	import org.agentcommunity.ANSMAS.NAES.*;
	import org.agentcommunity.ANSMAS.NCS.ontologies.*;
	import org.agentcommunity.ANSMAS.NSSS.ontologies.*;

	public class NegotiationProcessStartBehaviour extends CyclicBehaviour {

		private ContentManager cm = myAgent.getContentManager();
		private MessageTemplate mt;
		private NCAGUI gui; 
		private Codec language = new SLCodec();
		private Ontology niOntology = NIOntology.getInstance();
		private Ontology nsOntology = NSOntology.getInstance();
		private String messageSequence; // 

		public NegotiationProcessStartBehaviour(Agent agent, NCAGUI gui) {
			super(agent);
			this.gui = gui;
		}

		public void action() {
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				messageSequence = msg.getConversationId();
				try{
					//Receive: 11.Respond-Negotiation-Strategy
					if(messageSequence.equals("11.Respond-Negotiation-Strategy") || msg.getPerformative() == ACLMessage.INFORM){
						ContentElement ce = cm.extractContent(msg);
						if (ce instanceof NegotiationStrategy) {
							NegotiationStrategy ns = (NegotiationStrategy) ce;
							gui.RMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 3. Negotiation Process Start Protocol\n" + messageSequence + "(INFORM)\n---------------------------------------------------------\n" + msg.toString() +"\n---------------------------------------------------------\n");
						}else{
							System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + ":Received unknown message!\n" + msg.toString());
						}//if(messageSequence.equals("11.Respond-Negotiation-Strategy") || msg.getPerformative() == ACLMessage.INFORM)
					//Receive: 11.Respond-Negotiation-Strategy

					//Receive: 8.Request-Negotiation-Process-Start
					}else if(messageSequence.equals("8.Request-Negotiation-Process-Start") || msg.getPerformative() == ACLMessage.REQUEST_WHEN){
						String ni_uid = null;
						String pns_uid = null ;
						String dns_uid = null ;
						ContentElement ce = cm.extractContent(msg);
						if (ce instanceof NegotiationInformation) {
							NegotiationInformation ni = (NegotiationInformation) ce;
							ni_uid = ni.getUID();
							pns_uid = ni.getProviderStrategyUid();
							dns_uid = ni.getDeciderStrategyUid();
							gui.RMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 3. Negotiation Process Start Protocol\n" + messageSequence + "(INFORM)\n---------------------------------------------------------\n" + msg.toString() +"\n---------------------------------------------------------\n");
						}else{
							System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + ":Received unknown message!\n" + msg.toString());
						}//else if(messageSequence.equals("8.Request-Negotiation-Process-Start") || msg.getPerformative() == ACLMessage.REQUEST_WHEN)
					//Send: 9.Respond-Negotiation-Process-Start
						ACLMessage respondNegotiationProcessStart = new ACLMessage(ACLMessage.INFORM);
						try{
							NegotiationInformation ni = new NegotiationInformation();
							ni.setUID(ni_uid);
							respondNegotiationProcessStart.addReceiver(new AID("NMA",AID.ISLOCALNAME));
							respondNegotiationProcessStart.setConversationId("9.Respond-Negotiation-Process-Start");
							respondNegotiationProcessStart.setLanguage(language.getName());
							respondNegotiationProcessStart.setOntology(niOntology.getName());
							cm.fillContent(respondNegotiationProcessStart,ni);
							myAgent.send(respondNegotiationProcessStart);
							gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 3. Negotiation Process Start Protocol\n" + respondNegotiationProcessStart.getConversationId() + "(INFORM)\n---------------------------------------------------------\n" + respondNegotiationProcessStart.toString() +"\n---------------------------------------------------------\n");
							//Send: 10.Request-Negotiation-Strategy
							ACLMessage requestProviderNegotiatioStrategy = new ACLMessage(ACLMessage.INFORM_REF);
							ACLMessage requestDeciderNegotiatioStrategy = new ACLMessage(ACLMessage.INFORM_REF);
							try{
								NegotiationStrategy pns = new NegotiationStrategy();
								NegotiationStrategy dns = new NegotiationStrategy();
								pns.setUID(pns_uid);//협상 제안자 에이전트의 전략 요청
								dns.setUID(dns_uid);//협상 결정자 에이전트의 전략 요청
								//협상 제안자 에이전트의 전략 요청
								requestProviderNegotiatioStrategy.addReceiver(new AID("NSA",AID.ISLOCALNAME));
								requestProviderNegotiatioStrategy.setConversationId("10.Request-Negotiation-Strategy");
								requestProviderNegotiatioStrategy.setLanguage(language.getName());
								requestProviderNegotiatioStrategy.setOntology(nsOntology.getName());
								cm.fillContent(requestProviderNegotiatioStrategy,pns);
								myAgent.send(requestProviderNegotiatioStrategy);
								gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 3. Negotiation Process Start Protocol\n" + requestProviderNegotiatioStrategy.getConversationId() + "(INFORM)\n---------------------------------------------------------\n" + requestProviderNegotiatioStrategy.toString() +"\n---------------------------------------------------------------------------\n");
								//협상 결정자 에이전트의 전략 요청
								requestDeciderNegotiatioStrategy.addReceiver(new AID("NSA",AID.ISLOCALNAME));
								requestDeciderNegotiatioStrategy.setConversationId("10.Request-Negotiation-Strategy");
								requestDeciderNegotiatioStrategy.setLanguage(language.getName());
								requestDeciderNegotiatioStrategy.setOntology(nsOntology.getName());
								cm.fillContent(requestDeciderNegotiatioStrategy,dns);
								myAgent.send(requestDeciderNegotiatioStrategy);
								gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 3. Negotiation Process Start Protocol\n" + requestDeciderNegotiatioStrategy.getConversationId() + "(INFORM)\n---------------------------------------------------------\n" + requestDeciderNegotiatioStrategy.toString() +"\n---------------------------------------------------------------------------\n");
							}catch(Exception e){
								System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + "/ERROR: Sending message to NSA(requestNegotiatioStrategy)\n" + requestProviderNegotiatioStrategy.toString() + "\n\n" +requestDeciderNegotiatioStrategy.toString());
								e.printStackTrace();
							}

								//Send: 10.Request-Negotiation-Strategy

								//Send: 12.Request-Negotiation-Start

								//NPA 협상 시작 요청 전송

								//Send: 12.Request-Negotiation-Start

								//Receive: 15.Respond-Negotiation-Process-Start

								//NPA 협상 시작 요청 전송


								//Receive: 15.Respond-Negotiation-Process-Start

						}catch(Exception e){
							System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + "/ERROR: Sending message to NMA(9.Respond-Negotiation-Process-Start)\n" + respondNegotiationProcessStart.toString());
							e.printStackTrace();
						}
					//Send: 9.Respond-Negotiation-Process-Start
					}
				//Receive: 8.Request-Negotiation-Process-Start
				}catch (Exception ex) {
					ex.printStackTrace();
				}//try
			}else{
				block();
			}//if (msg != null)
		}//action()
	}//NCAReceiverBehaviour
