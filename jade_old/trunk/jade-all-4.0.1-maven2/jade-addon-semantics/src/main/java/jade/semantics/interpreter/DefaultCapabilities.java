/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2007 France Telecom

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

/*
* SemanticCapabilities.java
* Created on 13 mai 2005
* Author : Vincent Pautret
*/
package jade.semantics.interpreter;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.CommunicativeActionFactory;
import jade.semantics.actions.DefaultSemanticActionLoader;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.actions.SemanticActionTableImpl;
import jade.semantics.behaviours.PlanPerformanceBehaviour;
import jade.semantics.kbase.ArrayListKBaseImpl;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filters.FilterKBaseImpl;
import jade.semantics.kbase.filters.std.DefaultFilterKBaseLoader;
import jade.semantics.lang.sl.content.ContentParser;
import jade.semantics.lang.sl.content.DefaultContentParser;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jade.util.leap.HashMap;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

/**
 * This class provide a default implementation of the 
 * {@link SemanticCapabilities} interface.
 * 
 * @author Thierry Martinez - France Telecom
 * @version Date: 2007/05/14 Revision: 1.0
 * @version Date: 2007/06/11 Revision: 1.1 (Carole Adam, method interpretAfterPlan)
 */
public class DefaultCapabilities implements SemanticCapabilities {
	  
	/**
	 * The message template of messages to be interpreted by these capabilities,
	 * or null if all messages should be interpreted by these capabilities.
	 */
	private MessageTemplate myMessageTemplate;
	
   /**
    * The JADE agent these capabilities are installed on 
    */
	private Agent myAgent;
   
   /**
    * The Belief base these capabilities rely on
    */
    protected KBase myKBase; // DEBUG
   
   /**
    * The SIP (Semantic Interpretation Principle) table used by these capabilities
    */
   private SemanticInterpretationPrincipleTable mySemanticInterpretationTable;
   
   /**
    * The semantic action table used by these capabilities
    */
   private SemanticActionTable mySemanticActionTable;
   
   /**
    * Semantic interpreter behaviour associated to these capabilities
    */
   private SemanticInterpreterBehaviour myBehaviour;
      
   /**
    * Semantic agent customization
    */
   private StandardCustomization myStandardCustomization;
   
   /**
    * Semantic agent customization
    */
   private HashMap myContentParsers;
   
   /**
    * The SL Formula representation of this agent
    */
   private Term agentName;
   
   /**
    * This object is used to facilitate the building of communicative 
    * uppon the SemanticActionTable
    */
   private CommunicativeActionFactory myCommunicativeActionFactory;
   
   /**
    * This object is used to convert received ACL messages into Semantic actions
    */
   private ACLMessageConsumer myACLMessageConsumer;
   
   /**
    * This object is used to convert Semantic actions into ACL messages to send
    */
   private ACLMessageProducer myACLMessageProducer;
   
   /*********************************************************************/
   /**                         CONSTRUCTOR                             **/
   /*********************************************************************/
   /**
    * Creates a new {@link DefaultCapabilities} instance.
    * @param template a pattern to filter the incoming ACL messages to be
    *                 interpreted by the semantic agent.
    * @deprecated Use the default constructor and override the
    *             {@link #setupSemanticInterpreterBehaviour(MessageTemplate)}
    *             method instead.
    */
   @Deprecated
public DefaultCapabilities(MessageTemplate template) {
	   myMessageTemplate = template;
   } 
   
   /**
    * Creates a new {@link DefaultCapabilities} instance.
    */
   public DefaultCapabilities() {
       this(null);
   } 
   
   /*********************************************************************/
   /**                       PROTECTED METHODS                         **/
   /*********************************************************************/
   /**
    * Sets up the semantic agent's action table. This table defines all the actions
    * that are available for the agent and described in a semantic way (by pre-
    * and post-conditions), and which can be used in planning processes. This method
    * creates a default action table based on the {@link SemanticActionTableImpl}
    * class and filled with all FIPA-ACL communicative acts plus the FIPA-SL
    * alternative and sequence operators to combine action expressions, which are
    * provided by the {@link DefaultSemanticActionLoader} class.
    * <br>
    * It must be overridden to add application-specific actions. In this case, do not
    * forget to call the <code>super()</code> method, otherwise the semantic agent
    * would lack all FIPA-ACL communicative acts and be likely unable to interpret
    * them.
    * For example:
    * <pre>
protected SemanticActionTable setupSemanticActions() {
    SemanticActionTable t = super. setupSemanticActions();
    t.addSemanticAction(<my_semantic_action>);
    ...
    return t;
}
    * </pre>
    * 
    * @return the semantic action table to use by the semantic agent
    * @see jade.semantics.actions.SemanticAction
    */
   protected SemanticActionTable setupSemanticActions() {
       return new SemanticActionTableImpl(this, 
    		                              new DefaultSemanticActionLoader());
   } 
   
   /**
    * Sets up the semantic agent's SIP table. The SIPs (Semantic Interpretation
    * Principles) are the basic rules that specify how to interpret the events
    * perceived by the agent (such as the receipt of a FIPA-ACL message). This
    * method creates a default SIP table based on the
    * {@link SemanticInterpretationPrincipleTableImpl} class and filled with the
    * generic SIPs provided by the {@link DefaultSemanticInterpretationPrincipleLoader}
    * class.
    * <br>
    * It must be overridden to add application-specific SIPs. In this case, do not
    * forget to call the <code>super()</code> method, otherwise the semantic agent
    * would lack all basic generic SIPs and be likely unable to interpret
    * standard FIPA-ACL messages.
    * For example: 
    * <pre>
protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
    SemanticInterpretationPrincipleTable t = super.setupSemanticInterpretationPrinciples();
    t.addSemanticInterpretationPrinciple(<my_SIP>);
    ...
    return t;
}
    * </pre>
    * 
    * @return the SIP table to use by the semantic agent
    * @see SemanticInterpretationPrinciple
    */
   protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
	   return new SemanticInterpretationPrincipleTableImpl(
			   new DefaultSemanticInterpretationPrincipleLoader(this));
   } 
   
   /**
    * Sets up the semantic agent's belief base. This method creates a default
    * belief base based on the {@link FilterKBaseImpl} class and filled with the
    * generic filters provided by the {@link DefaultFilterKBaseLoader} class.
    * <br>
    * It must be overridden to customize the belief base to use.
    * For example:
    * <pre>
protected KBase setupKbase() {
    FilterKBase kb = super.setupKbase();
    kb.addKBAssertFilter(<my_assert_filter>);
    kb.addKBQueryFilter(<my_query_filter>);
    ...
    return kb;
}
    * </pre>
    * 
    * @return the belief base instance to use by the semantic agent
    * @see KBase
    */
   protected KBase setupKbase() {
       return new FilterKBaseImpl(new ArrayListKBaseImpl(getAgentName()),
    		                      new DefaultFilterKBaseLoader());
   } 
   
   public void setKBase(KBase kb) {
	   myKBase = kb;
   }
      
   /**
    * Sets up the {@link SemanticInterpreterBehaviour} instance running the
    * semantic agent's interpretation algorithm. This method creates a
    * default interpretation behaviour based on the {@link SemanticInterpreterBehaviour}
    * class.
    * <br>
    * Although the default interpretation behaviour should be sufficient, it
    * may be customized by overriding this method.
    * 
    * @param template the MessageTemplate that filters the received messages that
    *                 must be interpreted by this SemanticCapabilities object
    * @return the SemanticInterpreterBehaviour instance that must run the interpretation
    *         algorithm
    */
   protected SemanticInterpreterBehaviour setupSemanticInterpreterBehaviour(MessageTemplate template) {
       return new SemanticInterpreterBehaviour(template, this);
   }

   /**
    * Setup of the standard customization. By default, sets the customization
    * with an instance of <code>StandardCustomizationAdapter</code>.
    * @deprecated The StandardCustomization object is replaced with a set of
    *             appropriate SIP adapters to add to the agent's SIP table.
    * @see StandardCustomization
    */
   @Deprecated
protected StandardCustomization setupStandardCustomization() {
       return new StandardCustomizationAdapter();
   } 
   
   /**
    * Extend this method to be able to use other content languages than FIPA-SL
    * for semantic agents.
    * 
    * @return a {@link HashMap} object containing all {@link ContentParser}
    *         instances for all content languages to be used by the semantic
    *         agent.
    */
   protected HashMap setupContentParsers() {
	   HashMap result = new HashMap();
       ContentParser cp = new DefaultContentParser();
       result.put(cp.getLanguage(), cp);
       return result;
   } 

   /**
    * Extend this method to set up the {@link ACLMessageProducer} object to be
    * used by the semantic agent to convert {@link CommunicativeAction} objects
    * into {@link jade.lang.acl.ACLMessage} objects to be sent.
    * 
    * @return the ACL Message Producer to be used by the semantic agent.
    */
   protected ACLMessageProducer setupACLMessageProducer() {
	   return new DefaultACLMessageProducer(this);
   } 

   /**
    * Extend this method to set up the {@link ACLMessageConsumer} object to be
    * used by the semantic agent to convert received {@link jade.lang.acl.ACLMessage}
    * objects into {@link CommunicativeAction} objects usable by the JSA framework.
    * 
    * @return the ACL Message Consumer to be used by the semantic agent.
    */
   protected ACLMessageConsumer setupACLMessageConsumer() {
	   return new DefaultACLMessageConsumer(this);
   } 

   /*********************************************************************/
   /**     IMPLEMENTATION OF THE SEMANTIC CAPABILITIES INTERFACE       **/
   /*********************************************************************/
   /**
    * Installs this {@link SemanticCapabilities} instance on a JADE agent.
    * This method consists in initializing all the semantic components by calling
    * the different <code>setupXXX</code> methods, and adding to the agent the
    * behaviour running the semantic interpretation algorithm.
    * 
    * @param agent the JADE agent, which to install this <code>SemanticCapabilities</code>
    *              object on
    */
   public void install(Agent agent) {
       //
       // Set the agent on which to install these capabilities
       myAgent = agent;
       agentName = Tools.AID2Term(myAgent.getAID()); 
       
       myBehaviour = setupSemanticInterpreterBehaviour(myMessageTemplate);
       // Add the semantic behaviour to the semantic agent
       myAgent.addBehaviour(myBehaviour);

       //
       // Setup all the customization for this agent
       myContentParsers = setupContentParsers();
       myACLMessageConsumer = setupACLMessageConsumer();
       myACLMessageProducer = setupACLMessageProducer();
       myStandardCustomization = setupStandardCustomization(); /* This method is deprecated */

	   myKBase = setupKbase();
	   myKBase.updateObservers(null);
	   mySemanticInterpretationTable = setupSemanticInterpretationPrinciples();
	   mySemanticActionTable = setupSemanticActions();
       myCommunicativeActionFactory = new CommunicativeActionFactory(mySemanticActionTable, getAgentName());
   } 

   /**
    * Sends a FIPA-ACL message given as a {@link CommunicativeAction} object.
    * In most cases, consider using the convenient methods defined for each
    * FIPA-ACL perfomative instead of this method. See {@link #inform(Formula, Term)},
    * {@link #request(ActionExpression, Term)}, etc. methods.
    * 
    * @param action the communicative action to send
    */
   public void sendCommunicativeAction(CommunicativeAction action) {
	   ACLMessage msg = getMyACLMessageProducer().produce(action);
	   if ( msg != null ) {
		   myAgent.send(msg);
	   }
   } 
   
   /**
    * Returns the behaviour running the semantic interpretation algorithm associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the behaviour running the semantic interpretation algorithm of the semantic agent
    * @see #setupSemanticInterpreterBehaviour(MessageTemplate)
    */
   public SemanticInterpreterBehaviour getSemanticInterpreterBehaviour() {
       return myBehaviour;
   } 
   
   /**
    * Returns a reference to the (semantic) agent associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the agent associated to this <code>SemanticCapabilities</code> instance.
    */
   public Agent getAgent() {
       return myAgent;
   } 
   
   /**
    * Returns a FIPA-SL term representing the semantic agent's AID associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return a FIPA-SL representation for the semantic agent's name.
    */
   public Term getAgentName() {
       return agentName;
   } 
   
   /**
    * Returns the belief base of the semantic agent associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the semantic agent's belief base.
    * @see #setupKbase()
    */
   public KBase getMyKBase() {
       return myKBase;
   } 
   
   /**
    * Returns the content parser to use to deal with expressions expressed in a
    * given language. To be retrieved, such a content parser should have been
    * defined within this {@link SemanticCapabilities} instance by extending the
    * {@link #setupContentParsers()} method.
    * 
    * @param language a content language identifier (e.g. "fipa-sl")
    * @return the content parser which handles the given language, or
    *         <code>null</code> if no appropriate content parser could be found
    * @see #setupContentParsers()
    */
   public ContentParser getContentParser(String language) {
       return (ContentParser)myContentParsers.get(language == null ? "fipa-sl" : language);
   } 
   
   /**
    * Returns the semantic action table of the semantic agent associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the semantic agent's action table.
    * @see #setupSemanticActions()
    */
   public SemanticActionTable getMySemanticActionTable() {
       return mySemanticActionTable;
   } 
      
   /**
    * Returns the SIP table of the semantic agent associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the semantic agent's SIP table.
    * @see #setupSemanticInterpretationPrinciples()
    */
   public SemanticInterpretationPrincipleTable getMySemanticInterpretationTable() {
       return mySemanticInterpretationTable;
   } 
      
   /**
    * Returns the {@link ACLMessageConsumer} object associated to this
    * {@link SemanticCapabilities} instance. This object is used by the semantic
    * agent to convert incoming ACL messages into {@link CommunicativeAction}
    * objects.
    * 
    * @return the semantic agent's ACL Message Consumer.
    */
   public ACLMessageConsumer getMyACLMessageConsumer() {
	   return myACLMessageConsumer;
   }
   
   /**
    * Returns the {@link ACLMessageProducer} object associated to this
    * {@link SemanticCapabilities} instance. This object is used by the semantic
    * agent to convert {@link CommunicativeAction} objects into ACL messages
    * to be sent.
    * 
    * @return the semantic agent's ACL Message Producer.
    */
   public ACLMessageProducer getMyACLMessageProducer() {
	   return myACLMessageProducer;
   }

   /**
    * Returns the standard customisation
    * @deprecated See the notes on {@link StandardCustomization}
    * @return the StandardCustomization.
    */
   @Deprecated
public StandardCustomization getMyStandardCustomization() {
       return myStandardCustomization;
   } 
  
   /*********************************************************************/
   /**                     CONVENIENT METHODS                          **/
   /*********************************************************************/
   
   /**
    * Adds a list of {@link SemanticRepresentation} objects to the internal FIFO
    * list of events to be semantically interpreted. This method calls the
    * {@link SemanticInterpreterBehaviour#interpret(ArrayList, boolean)} method,
    * so that:
    * <ul>
    *   <li>the specified SRs will be interpreted at the next schedule of the
    *       {@link SemanticInterpreterBehaviour} instance attached to this
    *       {@link SemanticCapabilities} instance.</li>
    *   <li>before being interpreted, the FIPA-SL formula associated to each
    *       SR is surrounded by a belief modality and each occurring
    *       "<code>??myself</code>" meta-reference is instantiated with the
    *       semantic agent's AID.</li>
    * </ul>
    * 
    * @see SemanticInterpreterBehaviour#interpret(ArrayList, boolean)
    * @param listOfSR list of the {@link SemanticRepresentation} instances to be
    *                 interpreted.
    */
   public void interpret(ArrayList listOfSR) {
       getSemanticInterpreterBehaviour().interpret(listOfSR, false);
   } 

   /**
    * Adds a particular {@link SemanticRepresentation} object to the internal FIFO
    * list of events to be semantically interpreted. This method calls the
    * {@link SemanticInterpreterBehaviour#interpret(SemanticRepresentation, boolean)}
    * method, so that:
    * <ul>
    *   <li>the specified SR will be interpreted at the next schedule of the
    *       {@link SemanticInterpreterBehaviour} instance attached to this
    *       {@link SemanticCapabilities} instance.</li>
    *   <li>before being interpreted, the FIPA-SL formula associated to the
    *       SR is surrounded by a belief modality and each occurring
    *       "<code>??myself</code>" meta-reference is instantiated with the
    *       semantic agent's AID.</li>
    * </ul>
    * 
    * @see SemanticInterpreterBehaviour#interpret(SemanticRepresentation, boolean)
    * @param sr the {@link SemanticRepresentation} instance to be interpreted.
    */
   public void interpret(SemanticRepresentation sr) {
       getSemanticInterpreterBehaviour().interpret(sr, false);
   } 
   
   /**
    * Adds a particular FIPA-SL formula to the internal FIFO
    * list of events to be semantically interpreted. This method calls the
    * {@link SemanticInterpreterBehaviour#interpret(SemanticRepresentation, boolean)}
    * method, so that:
    * <ul>
    *   <li>the {@link SemanticRepresentation} instance to be interpreted is
    *       created from a given formula.</li>
    *   <li>this SR will be interpreted at the next schedule of the
    *       {@link SemanticInterpreterBehaviour} instance attached to this
    *       {@link SemanticCapabilities} instance.</li>
    *   <li>before being interpreted, the specified formula is automatically
    *       surrounded by a belief modality and each occurring
    *       "<code>??myself</code>" meta-reference is instantiated with the
    *       semantic agent's AID.</li>
    * </ul>
    * <p><i>Note:</i>
    * As a result of the interpretation process, this method, in addition to
    * asserting formulas into the semantic agent's belief base, 
    * triggers all relevant applicable Semantic Interpretation Principles (SIPs),
    * and so performs the behaviour semantically specified for the agent. As a
    * consequence, it is stongly advised to use this method instead of 
    * {@link KBase#assertFormula(Formula)} in order to assert formulas into the
    * agent's belief base. 
    * </p>
    * 
    * @see SemanticInterpreterBehaviour#interpret(SemanticRepresentation, boolean)
    * @param formula the FIPA-SL formula to be interpreted.
    */
   public void interpret(Formula formula) {
	   getSemanticInterpreterBehaviour().interpret(new SemanticRepresentation(formula), false);
   } 
   
   /**
    * Adds a particular FIPA-SL formula to the internal FIFO
    * list of events to be semantically interpreted.
    * <br>
    * This is the same method as {@link #interpret(Formula)}, except that
    * the formula to interpret is given as a {@link String} (expressed in FIPA-SL).
    * </br>
    * 
    * @see #interpret(Formula)
    * @param formula the FIPA-SL formula to be interpreted, given as a {@link String}.
    */
   public void interpret(String formula) {
	   interpret(SL.formula(formula));
   } 
   
   /**
    * Reads all lines of a given input reader and interprets each of them as a
    * formula. This convenient method is equivalent to call {@link #interpret(String)}
    * on each line of the reader. Only lines that start with a "(" character are
    * interpreted (the other ones can be considered as comments).
    * <br>
    * Like for the {@link #interpret(String)} method, each formula is
    * automatically surrounded by a belief modality and each occurring
    * "<code>??myself</code>" meta-reference is instantiated with the
    * semantic agent's AID.</li>
    * </br>
    * 
    * @see #interpret(String)
    * @param r the input reader, which formulas to interpret are read from.
    * @throws IOException if an exception occurs while reading the input reader.
    */
   public void interpret(Reader r) throws IOException {
	   ListOfFormula list;
	   try {
		   list = SLParser.getParser().parseFormulas(r, true);
	   } catch (ParseException e) {
		   e.printStackTrace();
		   return;
	   }
	   for (Iterator i = list.iterator(); i.hasNext(); ) {
		   interpret((Formula)i.next());
	   }
   }  
   
   
   /**
    * This method adds o the agent a PlanPerformanceBehaviour that will
    * perform the given plan, and depending on its ending state will interpret
    * either the given failureSRlist (execution or feasibility failure) or the
    * successSRlist (success of plan).
    * 
    * The successSR and failureSR can contain application-specific SR to 
    * specify the subsequent behaviour of the agent.
    * 
    * This method should be called in an IntentionTransferSIPAdapter to allow
    * the agent to perform a decision plan before deciding to interpret the 
    * acceptResult or the refuseResult parameter of the doApply method.
    * 
    * @param plan the plan to execute
    * @param successSR the ArrayList of SR to interpret if the plan succeeds
    * @param failureSR the ArrayList of SR to interpret if the plan fails
    */
   public void interpretAfterPlan(ActionExpression plan, ArrayList successSRlist, ArrayList failureSRlist) {
		   myAgent.addBehaviour(
				   new PlanPerformanceBehaviour(plan,
						   						successSRlist,
						   						// execution failure
						   						failureSRlist,
						   						// feasibility failure
						   						failureSRlist,
						   						DefaultCapabilities.this));
   }
   
   public void interpretAfterPlan(String plan, ArrayList successSRlist, ArrayList failureSRlist) {
	   try {
		   interpretAfterPlan((ActionExpression)SL.term(plan), successSRlist, failureSRlist);
	   }
	   catch(ClassCastException cce) {
		   cce.printStackTrace();
		   interpret(failureSRlist);
	   }
   }
   
   
   /*********************************************************************/
   /**             COMMUNICATIVE ACTION CONVENIENT METHOD              **/
   /*********************************************************************/
   
   /**
    * Convenient method to send an <code>ACCEPT-PROPOSAL</code> message to a
    * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
    * @param action action expression representing the first content element
    *               of the performative.
    * @param condition formula representing the second content element of the
    *                  performative.
    * @param receiver term representing the receiver's AID.
    * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729690">
    *      The FIPA Communicative Act Library Specification</a>
    */
	public void acceptProposal(ActionExpression action, Formula condition, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createAcceptProposal(action, condition, receiver));
	}
	
	/**
	 * Convenient method to send an <code>ACCEPT-PROPOSAL</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element of the
	 *                  performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729690">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void acceptProposal(ActionExpression action, Formula condition, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createAcceptProposal(action, condition, receivers));
	}
	
	/**
	 * Convenient method to send an <code>AGREE</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element of the
	 *                  performative.
	 * @param receiver term representing the receiver's AID.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729691">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void agree(ActionExpression action, Formula condition, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createAgree(action, condition, receiver));
	}
	
	/**
	 * Convenient method to send an <code>AGREE</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element of the
	 *                  performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729691">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void agree(ActionExpression action, Formula condition, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createAgree(action, condition, receivers));
	}
	
	/**
	 * Convenient method to send an <code>CANCEL</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param action action expression representing the content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729692">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void cancel(ActionExpression action, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createCancel(action, receiver));
	}
	
	/**
	 * Convenient method to send an <code>CANCEL</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param action action expression representing the content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729692">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void cancel(ActionExpression action, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createCancel(action, receivers));
	}
	
	/**
	 * Convenient method to send an <code>CFP</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param ire identifying referential expression representing the second content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729693">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void callForProposal(ActionExpression action, IdentifyingExpression ire, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createCFP(action, ire, receiver));
	}
	
	/**
	 * Convenient method to send an <code>CFP</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param ire identifying referential expression representing the second content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729693">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void callForProposal(ActionExpression action, IdentifyingExpression ire, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createCFP(action, ire, receivers));
	}
	
	/**
	 * Convenient method to send an <code>CONFIRM</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param formula formula representing the content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729694">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void confirm(Formula formula, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createConfirm(formula, receiver));
	}

	/**
	 * Convenient method to send an <code>CONFIRM</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param formula formula representing the content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729694">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void confirm(Formula formula, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createConfirm(formula, receivers));
	}

	/**
	 * Convenient method to send an <code>DISCONFIRM</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param formula formula representing the content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729695">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void disconfirm(Formula formula, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createDisconfirm(formula, receiver));
	}

	/**
	 * Convenient method to send an <code>DISCONFIRM</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @param formula formula representing the content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729695">
	 *      The FIPA Communicative Act Library Specification</a>
	 */
	public void disconfirm(Formula formula, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createDisconfirm(formula, receivers));
	}

	/**
	 * Convenient method to send an <code>FAILURE</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729696">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param formula formula representing the second content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void failure(ActionExpression action, Formula formula, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createFailure(action, formula, receiver));
	}

	/**
	 * Convenient method to send an <code>FAILURE</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729696">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param formula formula representing the second content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void failure(ActionExpression action, Formula formula, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createFailure(action, formula, receivers));
	}

	/**
	 * Convenient method to send an <code>INFORM</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729697">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param formula formula representing the content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void inform(Formula formula, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createInform(formula, receiver));
	}

	/**
	 * Convenient method to send an <code>INFORM</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729697">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param formula formula representing the content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void inform(Formula formula, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createInform(formula, receivers));
	}
    
	/**
	 * Convenient method to send an <code>NOT-UNDERSTOOD</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729700">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param reason formula representing the second content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void notUnderstood(ActionExpression action, Formula reason, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createNotUnderstood(action, reason, receiver));
	}

	/**
	 * Convenient method to send an <code>NOT-UNDERSTOOD</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729700">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param reason formula representing the second content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void notUnderstood(ActionExpression action, Formula reason, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createNotUnderstood(action, reason, receivers));
	}
    
	/**
	 * Convenient method to send an <code>PROPOSE</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729702">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void propose(ActionExpression action, Formula condition, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createPropose(action, condition, receiver));
	}

	/**
	 * Convenient method to send an <code>PROPOSE</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729702">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void propose(ActionExpression action, Formula condition, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createPropose(action, condition, receivers));
	}
    
	/**
	 * Convenient method to send an <code>QUERY-IF</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729704">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param formula formula representing the content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void queryIf(Formula formula, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createQueryIf(formula, receiver));
	}

	/**
	 * Convenient method to send an <code>QUERY-IF</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729704">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param formula formula representing the content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void queryIf(Formula formula, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createQueryIf(formula, receivers));
	}
    
	/**
	 * Convenient method to send an <code>QUERY-REF</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729705">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param ire identifying referential expression representing the content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void queryRef(IdentifyingExpression ire, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createQueryRef(ire, receiver));
	}
	
	/**
	 * Convenient method to send an <code>QUERY-REF</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729705">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param ire identifying referential expression representing the content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void queryRef(IdentifyingExpression ire, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createQueryRef(ire, receivers));
	}
        
	/**
	 * Convenient method to send an <code>REFUSE</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729706">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param reason formula representing the second content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void refuse(ActionExpression action, Formula reason, Term receiver) {	
		sendCommunicativeAction(myCommunicativeActionFactory.createRefuse(action, reason, receiver));
	}

	/**
	 * Convenient method to send an <code>REFUSE</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729706">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param reason formula representing the second content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void refuse(ActionExpression action, Formula reason, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRefuse(action, reason, receivers));
	}
    
	/**
	 * Convenient method to send an <code>REJECT-PROPOSAL</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729707">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element
	 *               of the performative.
	 * @param reason formula representing the third content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void rejectProposal(ActionExpression action, Formula condition, Formula reason, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRejectProposal(action, condition, reason, receiver));
	}

	/**
	 * Convenient method to send an <code>REJECT-PROPOSAL</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729707">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element
	 *               of the performative.
	 * @param reason formula representing the third content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void rejectProposal(ActionExpression action, Formula condition, Formula reason, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRejectProposal(action, condition, reason, receivers));
	}
    
	/**
	 * Convenient method to send an <code>REQUEST</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729708">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void request(ActionExpression action, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRequest(action, receiver));
	}

	/**
	 * Convenient method to send an <code>REQUEST</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729708">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void request(ActionExpression action, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRequest(action, receivers));
	}
    
	/**
	 * Convenient method to send an <code>REQUEST-WHEN</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729709">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void requestWhen(ActionExpression action, Formula condition, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRequestWhen(action, condition, receiver));
	}

	/**
	 * Convenient method to send an <code>REQUEST-WHEN</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729709">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void requestWhen(ActionExpression action, Formula condition, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRequestWhen(action, condition, receivers));
	}
    
	/**
	 * Convenient method to send an <code>REQUEST-WHENEVER</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729710">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void requestWhenever(ActionExpression action, Formula condition, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRequestWhenever(action, condition, receiver));
	}

	/**
	 * Convenient method to send an <code>REQUEST-WHENEVER</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729710">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param action action expression representing the first content element
	 *               of the performative.
	 * @param condition formula representing the second content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void requestWhenever(ActionExpression action, Formula condition, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createRequestWhenever(action, condition, receivers));
	}
    
	/**
	 * Convenient method to send an <code>SUBSCRIBE</code> message to a
	 * <b>single</b> agent. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729711">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param ire identifying referential expression representing the content element
	 *               of the performative.
	 * @param receiver term representing the receiver's AID.
	 */
	public void subscribe(IdentifyingExpression ire, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createSubscribe(ire, receiver));
	}

	/**
	 * Convenient method to send an <code>SUBSCRIBE</code> message to a
	 * <b>set</b> of agents. It uses the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * @see <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729711">
	 *      The FIPA Communicative Act Library Specification</a>
	 * @param ire identifying referential expression representing the content element
	 *               of the performative.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void subscribe(IdentifyingExpression ire, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createSubscribe(ire, receivers));
	}

	/**
	 * Convenient method to send the equivalent of an <i><code>UNSUBSCRIBE</code></i>
	 * message to a <b>single</b> agent. In fact, this performative does not
	 * directly exist in the FIPA Communicative Act Library Specification. This
	 * method generates an <code>INFORM</code> message with the appropriate content,
	 * and sends it using the {@link #sendCommunicativeAction(CommunicativeAction)}
	 * method of the {@link SemanticCapabilities} class.
	 * <p><i>Note:</i> the message sent by this method corresponds
	 * to a semantically correct expression of the <code>CANCEL</code> message
	 * suggested in the <a href="http://fipa.org/specs/fipa00035/SC00035H.html#_Toc26669463">
	 * FIPA Cancel "Meta-Protocol"</a>, which is actually not correct
	 * from the semantic point of view.
	 * </p>
	 * 
	 * @param ire identifying referential expression representing the previously
	 *            subscribed IRE (through a <code>SUBSCRIBE</code> message) to
	 *            unsubscribe.
	 * @param receiver term representing the receiver's AID.
	 */
	public void unsubscribe(IdentifyingExpression ire, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createUnsubscribe(ire, receiver));
	}

	/**
	 * Convenient method to send the equivalent of an <i><code>UNSUBSCRIBE</code></i>
	 * message to a <b>set</b> of agents.
	 * @see #unsubscribe(IdentifyingExpression, Term)
	 * 
	 * @param ire identifying referential expression representing the previously
	 *            subscribed IRE (through a <code>SUBSCRIBE</code> message) to
	 *            unsubscribe.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void unsubscribe(IdentifyingExpression ire, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createUnsubscribe(ire, receivers));
	}
    
	/**
	 * Convenient method to send the equivalent of an <i><code>UNSUBSCRIBE</code></i>
	 * message to a <b>single</b> agent.
	 * @see #unsubscribe(IdentifyingExpression, Term)
	 * 
	 * @param action action expression representing the previously subscribed
	 *               action (either through a <code>REQUEST-WHEN</code> or a
	 *               <code>REQUEST-WHENEVER</code> message) to unsubscribe,
	 *               whatever its associated condition.
	 * @param receiver term representing the receiver's AID.
	 */
	public void unsubscribe(ActionExpression action, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createUnsubscribe(action, receiver));
	}

	/**
	 * Convenient method to send the equivalent of an <i><code>UNSUBSCRIBE</code></i>
	 * message to a <b>set</b> of agents.
	 * @see #unsubscribe(IdentifyingExpression, Term)
	 * 
	 * @param action action expression representing the previously subscribed
	 *               action (either through a <code>REQUEST-WHEN</code> or a
	 *               <code>REQUEST-WHENEVER</code> message) to unsubscribe,
	 *               whatever its associated condition.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void unsubscribe(ActionExpression action, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createUnsubscribe(action, receivers));
	}
    
	/**
	 * Convenient method to send the equivalent of an <i><code>UNSUBSCRIBE</code></i>
	 * message to a <b>single</b> agent.
	 * @see #unsubscribe(IdentifyingExpression, Term)
	 * 
	 * @param action action expression representing the previously subscribed
	 *               action (either through a <code>REQUEST-WHEN</code> or a
	 *               <code>REQUEST-WHENEVER</code> message) to unsubscribe.
	 * @param condition formula representing the condition associated to the
	 *                  (previously subscribed) action to unsubscribe. If the same
	 *                  action was subscribed with several different conditions,
	 *                  only the subscription associated to the specified
	 *                  condition is cancelled.
	 * @param receiver term representing the receiver's AID.
	 */
	public void unsubscribe(ActionExpression action, Formula condition, Term receiver) {
		sendCommunicativeAction(myCommunicativeActionFactory.createUnsubscribe(action, condition, receiver));
	}

	/**
	 * Convenient method to send the equivalent of an <i><code>UNSUBSCRIBE</code></i>
	 * message to a <b>set</b> of agents.
	 * @see #unsubscribe(IdentifyingExpression, Term)
	 * 
	 * @param action action expression representing the previously subscribed
	 *               action (either through a <code>REQUEST-WHEN</code> or a
	 *               <code>REQUEST-WHENEVER</code> message) to unsubscribe.
	 * @param condition formula representing the condition associated to the
	 *                  (previously subscribed) action to unsubscribe. If the same
	 *                  action was subscribed with several different conditions,
	 *                  only the subscription associated to the specified
	 *                  condition is cancelled.
	 * @param receivers array of terms representing the receivers' AIDs.
	 */
	public void unsubscribe(ActionExpression action, Formula condition, Term[] receivers) {
		sendCommunicativeAction(myCommunicativeActionFactory.createUnsubscribe(action, condition, receivers));
	}
} 
