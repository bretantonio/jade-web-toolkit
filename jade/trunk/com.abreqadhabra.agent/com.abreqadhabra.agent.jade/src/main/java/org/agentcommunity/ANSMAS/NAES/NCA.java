package org.agentcommunity.ANSMAS.NAES;

import jade.core.*;
import jade.core.AID;
import jade.core.behaviours.*;

import jade.domain.*;
import jade.domain.FIPANames.InteractionProtocol;
import jade.domain.FIPAAgentManagement.*;

import jade.lang.acl.*;
import jade.lang.acl.ACLMessage;

import jade.content.*;
import jade.content.ContentManager;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;

import jade.util.leap.*;
import jade.gui.*;

import org.agentcommunity.ANSMAS.*;
import org.agentcommunity.ANSMAS.NCS.ontologies.*;
import org.agentcommunity.ANSMAS.NSSS.ontologies.*;
import org.agentcommunity.ANSMAS.NAES.Behaviour.*;

public class NCA extends GuiAgent implements NSVocabulary{


	private Codec language = new SLCodec();
	private Ontology nsOntology = NSOntology.getInstance();
	private Ontology niOntology = NIOntology.getInstance();

	private ContentManager cm = (ContentManager) getContentManager();
    private NCAGUI gui;  // The gui
    static final int EXIT = 1000;  // constants for gui events
//-------------------------------------------------------------------------
	public void setup(){
		// Register language and negotiationStrategyOntology Ontology
		cm.registerLanguage(language);
		cm.registerOntology(nsOntology);
		cm.registerOntology(niOntology);
	  
	  // Set up the gui
	  gui = new NCAGUI(this);
	  gui.setVisible(true);	
	  
	  registerDFAgentDescription();
	  // Set this agent main behaviour
      SequentialBehaviour sb = new SequentialBehaviour();
	  sb.addSubBehaviour(new NegotiationProcessStartBehaviour(this, gui));
      addBehaviour(sb);

	}


//-------------------------------------------------------------------------
	protected void registerDFAgentDescription(){
		// Register DF Agent Description
		gui.SLjTextArea.append(Utils.getSystemDate() + "\n-----------------------------------------------------\nRegistering DF Agent Description(fipa-agent-management ontology representing the dfaDescription of an agent in the DF catalogue)\n-----------------------------------------------------\n");	
		DFAgentDescription dfaDescription = new DFAgentDescription();
		dfaDescription.addLanguages(language.getName());
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" + language.getName() + " - Add a content language name to the languages slot collection of this object...ok\n");	
		dfaDescription.addOntologies(nsOntology.getName());
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" + nsOntology.getName() + " - Add an ontology name to the ontologies slot collection of this object...ok\n");	
		dfaDescription.addProtocols(InteractionProtocol.FIPA_REQUEST);
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":Add Protocol " + InteractionProtocol.FIPA_REQUEST + " - Add a protocol name to the protocols slot collection of this object...ok\n");	
		dfaDescription.setName(getAID());
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" + getAID() + "Set the identifier of the agent(AID)...ok\n");	
		// Register Service Description
		ServiceDescription serviceDesc = new ServiceDescription();
		serviceDesc.setName(getLocalName());
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" + getLocalName() + " - Set the name slot of this object...ok\n");
		serviceDesc.setType(NSA_SERVICE_TYPE); 
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" + NSA_SERVICE_TYPE + " - Set the type slot of this object...ok\n");
		serviceDesc.setOwnership(NSA_OWNERSHIP);
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" +  NSA_OWNERSHIP + " - Set the type slot of this object...ok\n");
		//Add the service has a list of supported languages, ontologies and protocols for this service.
		serviceDesc.addLanguages(language.getName());
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" + language.getName() + " - Add a content language name to the languages slot collection of this object...ok\n");
		serviceDesc.addOntologies(nsOntology.getName());
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" + nsOntology.getName() + " - Add a content language name to the languages slot collection of this object...ok\n");
		serviceDesc.addProtocols(InteractionProtocol.FIPA_REQUEST);
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ":" + InteractionProtocol.FIPA_REQUEST + " - Add a protocol name to the protocols slot collection of this object...ok\n");
		dfaDescription.addServices(serviceDesc);
			gui.SLjTextArea.append(Utils.getSystemDate() + getLocalName() + ": Add a service description to the service slot collection of this object...ok\n");
		//register synchronously registers us with the DF, we may
		//prefer to do this asynchronously using a behaviour.

		try{
			DFService.register(this,dfaDescription);
			gui.SLjTextArea.append(Utils.getSystemDate() + "Registering DF Agent Description...successfully");	
		}catch(FIPAException e){
			System.out.println(Utils.getSystemDate() + getLocalName() + "/ERROR:Registering DF Agent Description..fail, exiting:" + e);
			doDelete();
			return;
		}//try
	}//registerDFAgentDescription()


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



  }




