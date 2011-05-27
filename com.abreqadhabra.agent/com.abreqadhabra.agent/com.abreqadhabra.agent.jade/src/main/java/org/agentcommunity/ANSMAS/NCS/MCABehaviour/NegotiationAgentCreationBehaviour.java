package org.agentcommunity.ANSMAS.NCS.MCABehaviour;

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

		private MCAGUI gui; 
		private String messageSequence; // 
		private ContentManager cm = myAgent.getContentManager();
		protected jade.core.Runtime runtime = jade.core.Runtime.instance();//JADE Runtime instance
		private jade.wrapper.AgentContainer home;//현재 컨테이너(NCS))
		private MessageTemplate mt;
		private int step=0;
   private Map locations = new HashMap();
   		private Ontology niOntology = NIOntology.getInstance();
private NegotiationInformation ni;



		public NegotiationAgentCreationBehaviour(Agent agent, MCAGUI gui) {
			super(agent);
			this.gui = gui;
		}

	public void action() {

		ACLMessage msg = myAgent.receive(mt);


		if (msg != null && msg.getConversationId() !=null) {
		messageSequence = msg.getConversationId();


			try{
			//Receive: 0.Start-Negotiation
				if(messageSequence.equals("0.Start-Negotiation") && msg.getPerformative() == ACLMessage.INFORM){
				ContentElement ce = cm.extractContent(msg);
			
					if (ce instanceof NegotiationInformation) {
					ni = (NegotiationInformation) ce;	
					}
					
					gui.RMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 0. Start Negotiation\n" + messageSequence + "(INFORM)\n---------------------------------------------------------\n" + msg.toString() +"\n---------------------------------------------------------\n");
						
			requestNAESLocations();


				}else if(messageSequence.equals("3.Request-NA-Creation") && msg.getPerformative() == ACLMessage.REQUEST_WHEN){
				ContentElement ce = cm.extractContent(msg);
			
					if (ce instanceof NegotiationInformation) {
					ni = (NegotiationInformation) ce;	
					}
					
					gui.RMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 0. Start Negotiation\n" + messageSequence + "(REQUEST_WHEN)\n---------------------------------------------------------\n" + msg.toString() +"\n---------------------------------------------------------\n");

			respondNACreation(ni);
					
						}else{

				}

			
			
			}catch (Exception ex) {
			ex.printStackTrace();
			}//try





		}else{
		block();
		}



	}//action()

//-------------------------------------------------------------------------


public void respondNAESLocations(NegotiationInformation n){
		try {

	//AMS로 부터 받은 응답 처리
			MessageTemplate mt = MessageTemplate.and(
			MessageTemplate.MatchSender(myAgent.getAMS()),
			MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage requestNAESLocations = myAgent.blockingReceive(mt);
			ContentElement ce = cm.extractContent(requestNAESLocations);
			Result result = (Result) ce;
			jade.util.leap.Iterator it = result.getItems().iterator();
			
				
				gui.RMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 1. Negotiation Agent Creation Protocol\n1.Request-NAES-Locations(INFORM)\n---------------------------------------------------------\n" + requestNAESLocations.toString() +"\n---------------------------------------------------------\n");

		
	//NAES 위치 정보 갱신			
				String locationName = null;

				while (it.hasNext()) {
				Location loc = (Location)it.next();
							locationName = loc.getName();

						if(locationName.length() >=4){
				locationName = loc.getName().substring(0,4);

				if(locationName.equals("NAES")){
					locations.put(loc.getName(), loc);
					gui.SLjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\n" + loc.getName() + " is available.\n---------------------------------------------------------\nID:"+loc.getID()+"\nPROTOCOL:"+loc.getProtocol()+"\nNAME:"+loc.getName()+"\nADDRESS:"+loc.getAddress()+"\nLOCATION:"+loc);//위치 정보 출력 
				//EXAMPLE: ID: NAES@JADE-IMTP://abreqadhabra	PROTOCOL: JADE-IMTP	NAME: NAES	ADDRESS: abreqadhabra LOCATION: NAES@JADE-IMTP://abreqadhabra






				}
			}

				//gui.locations.put(loc.getName(),loc); //해쉬맵에 정보 저장
			}//while 
			gui.updateLocations(locations.keySet());

			//Send: 2.Respond-NAES-Locations

				ACLMessage respondNAESLocations = new ACLMessage(ACLMessage.INFORM);

							try{

								respondNAESLocations.addReceiver(new AID("NMA",AID.ISLOCALNAME));
								respondNAESLocations.setConversationId("2.Respond-NAES-Locations");
								respondNAESLocations.setLanguage(new SLCodec().getName());
								respondNAESLocations.setOntology(niOntology.getName());
								cm.fillContent(respondNAESLocations,n);
								myAgent.send(respondNAESLocations);

								gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 1. Negotiation Agent Creation Protocol\n" + respondNAESLocations.getConversationId() + "(INFORM)\n---------------------------------------------------------\n" + respondNAESLocations.toString() +"\n---------------------------------------------------------------------------\n");
								
		
									}catch(Exception e){
								System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + "/ERROR: Sending message to NMA(startNegotiation)\n" + respondNAESLocations.toString() + "\n");
								e.printStackTrace();
							}

		}catch (Exception ex) {
			ex.printStackTrace(); 
		}//try

}

public void requestNAESLocations(){
		//Send: 1.Request-NAES-Locations from AMS
		try {

		ACLMessage requestNAESLocations = new ACLMessage(ACLMessage.REQUEST);
		requestNAESLocations.setLanguage(new SLCodec().getName());
		requestNAESLocations.setOntology(MobilityOntology.getInstance().getName());
		Action action = new Action(myAgent.getAMS(), new QueryPlatformLocationsAction());
			myAgent.getContentManager().fillContent(requestNAESLocations,action );
			requestNAESLocations.addReceiver(action.getActor());
			myAgent.send(requestNAESLocations);
			gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 1. Negotiation Agent Creation Protocol\n1.Request-NAES-Locations(REQUEST)\n---------------------------------------------------------\n" + requestNAESLocations.toString() +"\n---------------------------------------------------------------------------\n");
		}catch (Exception ex) {
			ex.printStackTrace(); 
		}//try

			respondNAESLocations(ni);

}


public void respondNACreation(NegotiationInformation n){

	ACLMessage respondNACreation = new ACLMessage(ACLMessage.INFORM);

							try{

								respondNACreation.addReceiver(new AID("NMA",AID.ISLOCALNAME));
								respondNACreation.setConversationId("4.Respond-NA-Creation");
								respondNACreation.setLanguage(new SLCodec().getName());
								respondNACreation.setOntology(niOntology.getName());
								cm.fillContent(respondNACreation,n);
								myAgent.send(respondNACreation);

								gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 1. Negotiation Agent Creation Protocol\n" + respondNACreation.getConversationId() + "(INFORM)\n---------------------------------------------------------\n" + respondNACreation.toString() +"\n---------------------------------------------------------------------------\n");
								
							}catch(Exception e){
								System.out.println(Utils.getSystemDate() + myAgent.getLocalName() + "/ERROR: Sending message to NMA(startNegotiation)\n" + respondNACreation.toString() + "\n");
								e.printStackTrace();
							}

/*


	  jade.wrapper.AgentController a = null;
         try {

			home = myAgent.getContainerController();
			String agentID = n.getUID();
            Object[] args = new Object[2];
            args[0] = myAgent.getAID();
			System.out.println(args[0]);
            String pnaName = "PNA"+ agentID;
            a = home.createNewAgent(pnaName, PNA.class.getName(), args);
	        a.start();

            String dnaName = "DNA"+ agentID;
            a = home.createNewAgent(dnaName , DNA.class.getName(), args);
	        a.start();


	     }
         catch (Exception e) {
		    System.out.println("Problem creating new agent");
					e.printStackTrace();

	     }
         return;

*/
		

			}

	}//NegotiationAgentCreationBehaviour
