package org.agentcommunity.ANSMAS.NCS;

import java.util.*;
import java.io.*;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.basic.*;
import jade.domain.*;
import jade.domain.mobility.*;
import jade.domain.JADEAgentManagement.*;
import jade.gui.*;


public class DNA extends GuiAgent {
// ----------------------------------------

   private AID controller;
   private Location destination;
   transient protected DNAGUI gui;

   protected void setup() {
// ------------------------

   	  // Retrieve arguments passed during this agent creation
   	  Object[] args = getArguments();
	  controller = (AID) args[0];
	  destination = here();

	  init();

	  // Program the main behaviour of this agent
      addBehaviour(new ReceiveCommands(this));
   }

   void init() {
// -------------

	  // Register language and ontology
	  getContentManager().registerLanguage(new SLCodec());
	  getContentManager().registerOntology(MobilityOntology.getInstance());

	  // Create and display the gui
	  gui = new DNAGUI(this);
	  gui.setVisible(true);
	  gui.setLocation(destination.getName());
   }

   protected void onGuiEvent(GuiEvent e) {
// ---------------------------------------
   //No interaction with the gui
   }

   protected void beforeMove() {
// -----------------------------

      System.out.println("Moving now to location : " + destination.getName());
      gui.setVisible(false);
      gui.dispose();
   }

   protected void afterMove() {
// ----------------------------

	  init();
	  gui.setInfo("Arrived at location : " + destination.getName());
   }

   protected void beforeClone() {
// -----------------------------

      gui.setInfo("Cloning myself to location : " + destination.getName());
   }

   protected void afterClone() {
// ----------------------------

	  init();
   }


   /*
   * Receive all commands from the controller agent
   */
   class ReceiveCommands extends CyclicBehaviour {
// -----------------------------------------------

      ReceiveCommands(Agent a) { super(a); }

	  public void action() {

         ACLMessage msg = receive(MessageTemplate.MatchSender(controller));

         if (msg == null) { block(); return; }

	     if (msg.getPerformative() == ACLMessage.REQUEST){

		    try {
			   ContentElement content = getContentManager().extractContent(msg);
			   Concept concept = ((Action)content).getAction();

			   if (concept instanceof CloneAction){

				  CloneAction ca = (CloneAction)concept;
				  String newName = ca.getNewName();
				  Location l = ca.getMobileAgentDescription().getDestination();
				  if (l != null) destination = l;
				  doClone(destination, newName);
			   }
			   else if (concept instanceof MoveAction){

				  MoveAction ma = (MoveAction)concept;
				  Location l = ma.getMobileAgentDescription().getDestination();
				  if (l != null) doMove(destination = l);
			   }
			   else if (concept instanceof KillAgent){

				  gui.setVisible(false);
				  gui.dispose();
				  doDelete();
			   }
			}
			catch (Exception ex) { ex.printStackTrace(); }
		 }
		 else { System.out.println("Unexpected msg from controller agent"); }
	  }
   }

} // class DNA