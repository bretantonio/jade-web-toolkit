package org.agentcommunity.ANSMAS.NCS;

import java.util.*;

import jade.core.*;
import jade.core.behaviours.*;

import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import jade.domain.*;
import jade.domain.mobility.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.JADEAgentManagement.*;

import jade.gui.*;

import jade.lang.acl.*;

import org.agentcommunity.ANSMAS.*;

import org.agentcommunity.ANSMAS.NCS.ontologies.*;
import org.agentcommunity.ANSMAS.NCS.NMABehaviour.*;

public class NMA extends GuiAgent implements NIVocabulary{

/*****************************************************************
 *                 global attributs                              *
 *****************************************************************/
	protected Codec language = new SLCodec();//코덱 
	protected Ontology niOntology = NIOntology.getInstance();//형상 정보 온톨로지
	private ContentManager cm = (ContentManager)getContentManager();
	protected jade.core.Runtime runtime = jade.core.Runtime.instance();//JADE Runtime instance
	protected NMAGUI gui;// NMA GUI
	private jade.wrapper.AgentContainer home;//현재 컨테이너(NCS))

	private int nmaCnt = 1; //NAG에서 생성된 협상 이동 에이전트 아이디(카운트)
	protected HashMap nsr = new HashMap();//NSR(협상 정보 샘플 데이터)
    private Vector negotiationMobileAgents = new Vector();//생성된 협상 에이전트들

/*****************************************************************
 *                  setup()                                      *
 *****************************************************************/

	protected void setup() { 
	
       /*****************************************************************
		*컨텐츠 매니저에 코덱과 온톨로지(협상 정보/모바일 온톨로지) 등록*
		*협상 공유 저장소 생성(샘플 데이터)                             *
		*****************************************************************/

		cm.registerLanguage(language);//코덱
		cm.registerOntology(niOntology);//협상 정보 온톨로지
	    cm.registerOntology(MobilityOntology.getInstance());//모바일 온톨로지
		NSR negotiationSharedRepository = new NSR(this);//

       /*****************************************************************
		*AMS에 가용한 컨테이너 위치를 요청(ACLMessage.REQUEST)          *
		*협상 공유 저장소 생성(샘플 데이터)                             *
		*****************************************************************/
		// Set up the  gui
		gui = new NMAGUI(this);	
		gui.setVisible(true);	

	  // Set this agent main behaviour
	  addBehaviour(new NegotiationAgentCreationBehaviour(this, gui));
	  addBehaviour(new NegotiationProcessStartBehaviour(this, gui));
		registerDFAgentDescription();	
		




	}//setup()



/******************************************************************************************************************
 *                                               HELPER METHOD                                                    *
 ******************************************************************************************************************/


/*****************************************************************
 *                  sendRequest(Action action)                   *
 *****************************************************************/

	void sendRequest(Action action) {

       /*****************************************************************
		*ACLMessage.REQUEST                                             *
		*                                                               *
		*****************************************************************/

		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.setLanguage(new SLCodec().getName());
		request.setOntology(MobilityOntology.getInstance().getName());
		
		try {
			getContentManager().fillContent(request, action);
			request.addReceiver(action.getActor());
			send(request);
		gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":Request Message(ACLMessage.REQUEST)\n"+ request.toString() + "\n");
		}catch (Exception ex) {
			ex.printStackTrace(); 
		}//try

	}//sendRequest(Action action)

public void startNegotiation(String guiEventParam){


						ACLMessage startNegotiation = new ACLMessage(ACLMessage.INFORM);
							try{

NegotiationInformation rni = (NegotiationInformation) gui.nsr.get(guiEventParam);
		NegotiationInformation ni = new NegotiationInformation();

				ni.setUID(rni.getUID());
				ni.setCatalogId(rni.getCatalogId());
				ni.setUnspscId(rni.getUnspscId());
				ni.setProviderId(rni.getProviderId());
				ni.setProviderStrategyUid(rni.getProviderStrategyUid());
				ni.setDeciderId(rni.getDeciderId());
				ni.setDeciderStrategyUid(rni.getDeciderStrategyUid());


								startNegotiation.addReceiver(new AID("MCA",AID.ISLOCALNAME));
								startNegotiation.setConversationId("0.Start-Negotiation");
								startNegotiation.setLanguage(language.getName());
								startNegotiation.setOntology(niOntology.getName());
								cm.fillContent(startNegotiation,ni);
								send(startNegotiation);
								gui.SMjTextArea.append(Utils.getSystemDate() + "\n---------------------------------------------------------\nStep 0.Start Negotiation\n" + startNegotiation.getConversationId() + "(INFORM)\n---------------------------------------------------------\n" + startNegotiation.toString() +"\n---------------------------------------------------------------------------\n");
								
		
									}catch(Exception e){
								System.out.println(Utils.getSystemDate() + getLocalName() + "/ERROR: Sending message to NMA(startNegotiation)\n" + startNegotiation.toString() + "\n");
								e.printStackTrace();
							}
}
/*****************************************************************
 *                  registerDFAgentDescription()                 *
 *****************************************************************/
		
	protected void registerDFAgentDescription(){
		
       /*****************************************************************
		*DF에 NMA 등록                                                  *
		*                                                               *
		*****************************************************************/

		DFAgentDescription dfaDescription = new DFAgentDescription();

		dfaDescription.addLanguages(language.getName());
		dfaDescription.addOntologies(niOntology.getName());
		dfaDescription.addProtocols(InteractionProtocol.FIPA_REQUEST);
		dfaDescription.setName(getAID());

		ServiceDescription serviceDesc = new ServiceDescription();

		serviceDesc.setName(getLocalName());
		serviceDesc.setType(NMA_SERVICE_TYPE); 
		serviceDesc.setOwnership(NMA_OWNERSHIP);
		serviceDesc.addLanguages(language.getName());
		serviceDesc.addOntologies(niOntology.getName());
		serviceDesc.addProtocols(InteractionProtocol.FIPA_REQUEST);

		dfaDescription.addServices(serviceDesc);

		try{
			DFService.register(this,dfaDescription);
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":Registering DF Agent Description...successfully.");	
		}catch(FIPAException e){
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":Registering DF Agent Description..fail, exiting:" + e);
			doDelete();
			return;
		}//try
	}//registerDFAgentDescription()


/*****************************************************************
 *                  takeDown()                                   *
 *****************************************************************/
    
	protected void takeDown(){
		try{
			DFService.deregister(this);
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":DFService is deregistered");
			gui.dispose();
		}catch(FIPAException e){
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + "/ERROR: failed when deregistering from DF");
		}//try
    }//takeDown()

/*****************************************************************
 *                  onGuiEvent(GuiEvent ev)                      *
 *****************************************************************/

   protected void onGuiEvent(GuiEvent ev) {
  
	  int command = ev.getType();


	  if (command == NMA_EXIT) {
	     gui.setVisible(false);
	     gui.dispose();
		 doDelete();
		 System.out.println(Utils.getSystemDate() + getLocalName() + ":NCS is destroyed.");
		 System.exit(0);
	  } else if(command == AUTOMATED_NEGOTIATION_START){
	
	  String guiEventParam = (String)ev.getParameter(0);


		startNegotiation(guiEventParam);
		
		
		}

  }//onGuiEvent(GuiEvent ev)

}//NMA CLASS