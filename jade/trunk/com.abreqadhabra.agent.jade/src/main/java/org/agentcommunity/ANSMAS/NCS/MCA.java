package org.agentcommunity.ANSMAS.NCS;

import java.util.*;
import java.io.*;

import jade.lang.acl.*;
import jade.content.*;
import jade.content.onto.basic.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.mobility.*;
import jade.domain.JADEAgentManagement.*;
import jade.gui.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import org.agentcommunity.ANSMAS.NCS.ontologies.*;
import org.agentcommunity.ANSMAS.NCS.MCABehaviour.*;

public class MCA extends GuiAgent implements NIVocabulary{
// --------------------------------------------
   private jade.wrapper.AgentContainer home;


   private Map locations = new HashMap();
   private Vector agents = new Vector();
   private int agentCnt = 0;
   private int command;
   transient protected MCAGUI gui;
	protected Codec language = new SLCodec();//코덱 
	protected Ontology niOntology = NIOntology.getInstance();//형상 정보 온톨로지
	private ContentManager cm = (ContentManager)getContentManager();


   // Get a JADE Runtime instance
   jade.core.Runtime runtime = jade.core.Runtime.instance();

   protected void setup() {
// ------------------------

	cm.registerLanguage(language);//코덱
		cm.registerOntology(niOntology);//협상 정보 온톨로지
	    cm.registerOntology(MobilityOntology.getInstance());//모바일 온톨로지

      try {

         home = getContainerController();//?  

	     // Get available locations with AMS
	     sendRequest(new Action(getAMS(), new QueryPlatformLocationsAction()));

	     //Receive response from AMS
         MessageTemplate mt = MessageTemplate.and(
			                  MessageTemplate.MatchSender(getAMS()),
			                  MessageTemplate.MatchPerformative(ACLMessage.INFORM));
         ACLMessage resp = blockingReceive(mt);
         ContentElement ce = getContentManager().extractContent(resp);
         Result result = (Result) ce;
         jade.util.leap.Iterator it = result.getItems().iterator();
		 String locationName;
         while (it.hasNext()) {
            Location loc = (Location)it.next();
			locationName = loc.getName();

			if(locationName.length() >=4){
				locationName = loc.getName().substring(0,4);

				if(locationName.equals("NAES")){
					locations.put(loc.getName(), loc);
				}
			}
		 }
	  }
	  catch (Exception e) { e.printStackTrace(); }


	  // Create and show the gui
      gui = new MCAGUI(this, locations.keySet());
      gui.setVisible(true);
	  addBehaviour(new NegotiationAgentCreationBehaviour(this, gui));

   }


   protected void onGuiEvent(GuiEvent ev) {
// ----------------------------------------

	  command = ev.getType();

	  if (command == MCA_EXIT) {

	     gui.setVisible(false);
	     gui.dispose();
		 doDelete();
		 System.exit(0);
      }
	  if (command == MCA_NEW_NAGENT) {

	     jade.wrapper.AgentController a = null;
         try {

			 agentCnt++;

            Object[] args = new Object[2];
            args[0] = getAID();
            String pnaName = "PNA"+agentCnt;
            a = home.createNewAgent(pnaName, PNA.class.getName(), args);
	        a.start();
	        agents.add(pnaName);

            String dnaName = "DNA"+agentCnt;
            a = home.createNewAgent(dnaName , DNA.class.getName(), args);
	        a.start();
	        agents.add(dnaName);



	        gui.updateList(agents);
	     }
         catch (Exception e) {
		    System.out.println("Problem creating new agent");
					e.printStackTrace();

	     }
         return;
	  }
      String agentName = (String)ev.getParameter(0);
      AID aid = new AID(agentName, AID.ISLOCALNAME);

	  if (command == MCA_MOVE_NAGENT) {

         String destName = (String)ev.getParameter(1);
         Location dest = (Location)locations.get(destName);
         MobileAgentDescription mad = new MobileAgentDescription();
         mad.setName(aid);
         mad.setDestination(dest);
         MoveAction ma = new MoveAction();
         ma.setMobileAgentDescription(mad);
         sendRequest(new Action(aid, ma));
	  }
      else if (command == MCA_CLONE_NAGENT) {

         String destName = (String)ev.getParameter(1);
         Location dest = (Location)locations.get(destName);
         MobileAgentDescription mad = new MobileAgentDescription();
         mad.setName(aid);
         mad.setDestination(dest);
         String newName = "Clone-"+agentName;
         CloneAction ca = new CloneAction();
         ca.setNewName(newName);
         ca.setMobileAgentDescription(mad);
         sendRequest(new Action(aid, ca));
         agents.add(newName);
         gui.updateList(agents);
	  }
      else if (command == MCA_KILL_NAGENT) {

         KillAgent ka = new KillAgent();
         ka.setAgent(aid);
         sendRequest(new Action(aid, ka));
	     agents.remove(agentName);
		 gui.updateList(agents);
	  }
   }


   void sendRequest(Action action) {
// ---------------------------------

      ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
      request.setLanguage(new SLCodec().getName());
      request.setOntology(MobilityOntology.getInstance().getName());
      try {
	     getContentManager().fillContent(request, action);
	     request.addReceiver(action.getActor());
	     send(request);
	  }
	  catch (Exception ex) { ex.printStackTrace(); }
   }

}//class Controller
