package org.agentcommunity.ANSMAS.NSSS;

import java.util.*;

import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
//import jade.util.leap.*;
import jade.gui.*;

import org.agentcommunity.ANSMAS.*;
import org.agentcommunity.ANSMAS.NSSS.ontologies.*;
import org.agentcommunity.ANSMAS.NSSS.Behaviour.*;
public class NSA extends GuiAgent implements NSVocabulary{

	private Codec language = new SLCodec();
	private Ontology nsOntology = NSOntology.getInstance();
	private ContentManager cm = (ContentManager)getContentManager();
    private NSAGUI gui;  // The gui
    static final int EXIT = 1000;  // constants for gui events
//-------------------------------------------------------------------------
	protected void setup() { 
	
		// Register language and negotiationStrategyOntology Ontology
		cm.registerLanguage(language);
		cm.registerOntology(nsOntology);

	  // Set up the gui
	  gui = new NSAGUI(this);
	  gui.setVisible(true);	
	  

		registerDFAgentDescription();
		searchDFAgentDescription();
	  
	  // Set this agent main behaviour
		addBehaviour(new NegotiationProcessStartBehaviour(this, gui));
    



	}//setup()

//-------------------------------------------------------------------------
	protected void registerDFAgentDescription(){
		// Register DF Agent Description
		gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ": Registering DF Agent Description(fipa-agent-management ontology representing the dfaDescription of an agent in the DF catalogue)");	
		DFAgentDescription dfaDescription = new DFAgentDescription();
		dfaDescription.addLanguages(language.getName());
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" + language.getName() + " - Add a content language name to the languages slot collection of this object...ok");	
		dfaDescription.addOntologies(nsOntology.getName());
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" + nsOntology.getName() + " - Add an ontology name to the ontologies slot collection of this object...ok");	
		dfaDescription.addProtocols(InteractionProtocol.FIPA_REQUEST);
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":Add Protocol " + InteractionProtocol.FIPA_REQUEST + " - Add a protocol name to the protocols slot collection of this object...ok");	
		dfaDescription.setName(getAID());
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" + getAID() + "Set the identifier of the agent(AID)...ok");	
		// Register Service Description
		ServiceDescription serviceDesc = new ServiceDescription();
		serviceDesc.setName(getLocalName());
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" + getLocalName() + " - Set the name slot of this object...ok");
		serviceDesc.setType(NSA_SERVICE_TYPE); 
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" + NSA_SERVICE_TYPE + " - Set the type slot of this object...ok");
		serviceDesc.setOwnership(NSA_OWNERSHIP);
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" +  NSA_OWNERSHIP + " - Set the type slot of this object...ok");
		//Add the service has a list of supported languages, ontologies and protocols for this service.
		serviceDesc.addLanguages(language.getName());
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" + language.getName() + " - Add a content language name to the languages slot collection of this object...ok");
		serviceDesc.addOntologies(nsOntology.getName());
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" + nsOntology.getName() + " - Add a content language name to the languages slot collection of this object...ok");
		serviceDesc.addProtocols(InteractionProtocol.FIPA_REQUEST);
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":" + InteractionProtocol.FIPA_REQUEST + " - Add a protocol name to the protocols slot collection of this object...ok");
		dfaDescription.addServices(serviceDesc);
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ": Add a service description to the service slot collection of this object...ok");
		//register synchronously registers us with the DF, we may
		//prefer to do this asynchronously using a behaviour.

		try{
			DFService.register(this,dfaDescription);
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":Registering DF Agent Description...successfully");	
		}catch(FIPAException e){
			System.out.println("\n" + Utils.getSystemDate() + getLocalName() + ":Registering DF Agent Description..fail, exiting:" + e);
			doDelete();
			return;
		}//try
	}//registerDFAgentDescription()

//-------------------------------------------------------------------------
	protected void searchDFAgentDescription(){
		///DF Search begins here: 

		//we construct the search query in the same way as we would do
		//if we were registereing, except we only enter properties
		//that we want to match.
		DFAgentDescription searchDesc= new DFAgentDescription();
		ServiceDescription serviceDesc = new ServiceDescription();
		
		//we want to find all agents that match this service type
		serviceDesc.setType(NSA_SERVICE_TYPE);

		//there is little point in trying to find agents who don't
		//speak the same language as us.
		serviceDesc.addOntologies(nsOntology.getName());
		serviceDesc.addLanguages(language.getName());
		serviceDesc.addProtocols(InteractionProtocol.FIPA_REQUEST);

		searchDesc.addServices(serviceDesc);
			DFAgentDescription results[];
		try{
			
			gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ": DF Search begining...find all agents that match the service type");
			results= DFService.search(this,searchDesc);
		}catch(FIPAException e){
			System.out.println("\n" + Utils.getSystemDate() + getLocalName() + ": Could not search the DF, exiting:" + e);
			doDelete();
			return;
		}

		if(null==results || results.length ==0 ){
				System.out.println("\n" + Utils.getSystemDate() + getLocalName() + ": No Agents found, exiting");
			doDelete();
			return;
		}else{
			for(int i= 0 ;i < results.length; i ++){
				gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":Found (" + (i+1) + " of " + results.length + ")  agent(s) founded"+ results[i].getName().getLocalName());
				if(results[i].getName().getLocalName().equals("NSA") ){
					gui.SLjTextArea.append("\n" + Utils.getSystemDate() + getLocalName() + ":NSA(Negotiation Strategy Agent) is founded in DF!!!" +"( " + results[i].getName() +")");
				}
			}
		}
		//DF search ends here. 
	}






    protected void takeDown(){
	try{
	    DFService.deregister(this);
		System.out.println("\n" + Utils.getSystemDate() + getLocalName() + ":DFService is deregistered");
	    gui.dispose();
	}catch(FIPAException e){
	    System.out.println("\n" + Utils.getSystemDate() + getLocalName() + "/ERROR: failed when deregistering from DF");
	}//try
    }//takeDown()

  /**
   * Process events from GUI
   * @param ev Event from GUI
   */
  protected void onGuiEvent(GuiEvent ev) {
    switch (ev.getType()) {
      case EXIT:
        doDelete();
        break;
    }
  }

}//NSA