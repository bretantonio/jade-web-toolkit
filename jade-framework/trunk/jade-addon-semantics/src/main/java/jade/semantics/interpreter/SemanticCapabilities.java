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

package jade.semantics.interpreter;

import jade.core.Agent;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.kbase.KBase;
import jade.semantics.lang.sl.content.ContentParser;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.Term;
import jade.util.leap.ArrayList;

import java.io.IOException;
import java.io.Reader;

/**
 * This interface makes it possible to turn a JADE agent into a semantic one, by
 * installing all components of the JSA framework on a givent agent.
 * 
 * It is intended to be implemented for developing a specific JSA-based semantic
 * agent. Such a semantic agent mainly relies on 3 components:
 * <ul>
 *  <li>The <b>Belief Base</b>, which is responsible for storing and retrieving
 *      all the agent's beliefs, that is all the facts about his current
 *      representation of the world. The JSA provides a default belief base,
 *      handles most of FIPA-SL expressions. It can be setup by overriding the
 *      {@link #setupKbase()} method and accessed through the
 *      {@link #getMyKBase()} method.
 * 	</li>
 *  <li>The <b>Semantic Interpretation Principles</b> (or SIPs), which are the
 *      rules defining how the agent interprets events (such as the receipt of a
 *      message). The JSA provides a set of generic SIPs, which interpret the
 *      formal semantics of FIPA-ACL messages. It can be setup by overriding the
 *      {@link #setupSemanticInterpretationPrinciples()} method and accessed
 *      through the {@link #getMySemanticInterpretationTable()} method.
 *  </li>
 *  <li>The <b>Semantic Actions</b>, which semantically define (in particular
 *      by explicitly specifying preconditions and effetcs) the atomic actions
 *      the agent can perform. The JSA provides a default set of actions
 *      implementing each communicative act of the FIPA-ACL library (except the
 *      Propose and Proxy performatives, which will be implemented in some future
 *      version), so that any semantic agent natively handles communicative
 *      actions. This set can be setup by overriding the {@link #setupSemanticActions()}
 *      method and accessed through the {@link #getMySemanticActionTable()} method.
 *  </li>
 * </ul>
 * 
 * To facilitate such an implemantation, one may extend the default implementation
 * called {@link DefaultCapabilities}, as it is shown hereunder.
 * 
 * <pre>
class MySemanticCapabilities extends DefaultCapabilities {
    protected KBase setupKbase() {
	    FilterKBase kb = (FilterKBase) super.setupKbase();
        kb.addKBAssertFilter(...);
        kb.addKBQueryFilter(...);
        ...
        return kb;
    }

    protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
        SemanticInterpretationPrincipleTable t = super.setupSemanticInterpretationPrinciples();
        t.addSemanticInterpretationPrinciple(...);
        ...
        return t;
    }

    protected SemanticActionTable setupSemanticActions() {
        SemanticActionTable t = super. setupSemanticActions();
        t.addSemanticAction(...);
        ...
        return t;
    }
}
 * </pre>
 * 
 * The way to install it on a JADE agent is the following. The {@link #install(Agent)}
 * method actually consists in setting up the JSA components (by calling
 * the <code>setupXXX</code> methods and installing an instance of
 * {@link jade.semantics.interpreter.SemanticInterpreterBehaviour}, a cyclic JADE
 * behaviour, which runs the interpretation algorithm of the agent).
 * 
 * <pre>
class MyAgent extends Agent {
    void setup() [
        super.setup();
        SemanticCapabilities sc = new MySemanticCapabilities();
        sc.install(this);
    }
}
 * </pre>
 *
 * Additionally, this class provides a number of convenient methods for the
 * programming of semantic agents:
 * <ul>
 *  <li>The {@link #interpret(String)} method and its variants are useful to
 *      assert facts to be interpreted,
 *  </li>
 *  <li>The {@link #getAgentName()} method retrieves the agent AID expressed as
 *      a FIPA-SL term,
 *  </li>
 *  <li>The {@link #inform(Formula, Term)} and others are useful to send FIPA-ACL
 *      messages to other agents in a very concise way.
 *  </li>
 * </ul>
 * 
 * @author Vincent Pautret - France Telecom
 * @author Thierry Martinez - France Telecom
 * @author Vincent Louis - France Telecom
 * @version Date: 2005/05/13 Revision: 1.0
 * @version Date: 2007/05/14 Revision: 1.1
 */
public interface SemanticCapabilities {
	  

   /*********************************************************************/
   /**                         METHODS                             **/
   /*********************************************************************/
   
	/**
	 * Installs this {@link SemanticCapabilities} instance on a JADE agent.
	 * This method consists in initializing all the semantic components and 
	 * adding to the agent the behaviour running the semantic interpretation 
	 * algorithm.
	 * 
	 * @param agent the JADE agent, which to install this <code>SemanticCapabilities</code>
	 *              object on
	 */
	public void install(Agent agent);

	/**
	 * Sends a FIPA-ACL message given as a {@link CommunicativeAction} object.
	 * In most cases, consider using the convenient methods defined for each
	 * FIPA-ACL perfomative instead of this method. See {@link #inform(Formula, Term)},
	 * {@link #request(ActionExpression, Term)}, etc. methods.
	 * 
	 * @param action the communicative action to send
	 */
	public void sendCommunicativeAction(CommunicativeAction action);

	/**
	 * Returns the behaviour running the semantic interpretation algorithm associated to
	 * this <code>SemanticCapabilities</code> instance.
	 * @return the behaviour running the semantic interpretation algorithm of the semantic agent
	 */
   public SemanticInterpreterBehaviour getSemanticInterpreterBehaviour();

   /**
    * Returns a reference to the (semantic) agent associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the agent associated to this <code>SemanticCapabilities</code> instance.
    */
   public Agent getAgent();
   
   /**
    * Returns a FIPA-SL term representing the semantic agent's AID associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return a FIPA-SL representation for the semantic agent's name.
    */
   public Term getAgentName();
   
   /**
    * Returns the belief base of the semantic agent associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the semantic agent's belief base.
    * @see #setupKbase()
    */
   public KBase getMyKBase();
      
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
   public ContentParser getContentParser(String language);
   
   /**
    * Returns the semantic action table of the semantic agent associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the semantic agent's action table.
    * @see #setupSemanticActions()
    */
   public SemanticActionTable getMySemanticActionTable();
   
   /**
    * Returns the SIP table of the semantic agent associated to
    * this <code>SemanticCapabilities</code> instance.
    * @return the semantic agent's SIP table.
    * @see #setupSemanticInterpretationPrinciples()
    */
   public SemanticInterpretationPrincipleTable getMySemanticInterpretationTable();
   
   /**
    * Returns the standard customisation
    * @deprecated See the notes on {@link StandardCustomization}
    * @return the StandardCustomization.
    */
   @Deprecated
public StandardCustomization getMyStandardCustomization();
   
   /**
    * Returns the {@link ACLMessageProducer} object associated to this
    * {@link SemanticCapabilities} instance. This object is used by the semantic
    * agent to convert {@link CommunicativeAction} objects into ACL messages
    * to be sent.
    * 
    * @return the semantic agent's ACL Message Producer.
    */
   public ACLMessageProducer getMyACLMessageProducer();
   
   /**
    * Returns the {@link ACLMessageConsumer} object associated to this
    * {@link SemanticCapabilities} instance. This object is used by the semantic
    * agent to convert incoming ACL messages into {@link CommunicativeAction}
    * objects.
    * 
    * @return the semantic agent's ACL Message Consumer.
    */
   public ACLMessageConsumer getMyACLMessageConsumer();
   
   /*********************************************************************/
   /**                  INTERPRETATION METHODS                         **/
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
   public void interpret(ArrayList listOfSR);

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
   public void interpret(SemanticRepresentation sr);
   
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
   public void interpret(Formula formula);
   
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
   public void interpret(String s);
   
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
   public void interpret(Reader r) throws IOException ;
   
   public void interpretAfterPlan(String plan, ArrayList successSRlist, ArrayList failureSRlist);
   /**
    * Interpret a different SR list depending on the result of a plan
    * (see SemanticCapabilities for details)
    */
   public void interpretAfterPlan(ActionExpression plan, ArrayList successSRlist, ArrayList failureSRlist);

   
   /*********************************************************************/
   /**                        CONVENIENT METHOD                        **/
   /** The following methods can be used to issue a communicative act.  **/
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
	public void acceptProposal(ActionExpression action, Formula condition, Term receiver);

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
	public void acceptProposal(ActionExpression action, Formula condition, Term[] receivers);
	
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
	public void agree(ActionExpression action, Formula condition, Term receiver);
	
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
	public void agree(ActionExpression action, Formula condition, Term[] receivers);	
	
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
	public void cancel(ActionExpression action, Term receiver);
	
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
	public void cancel(ActionExpression action, Term[] receivers);
	
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
	public void callForProposal(ActionExpression action, IdentifyingExpression ire, Term receiver);
	
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
	public void callForProposal(ActionExpression action, IdentifyingExpression ire, Term[] receivers);
	
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
	public void confirm(Formula formula, Term receiver);

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
	public void confirm(Formula formula, Term[] receivers);

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
	public void disconfirm(Formula formula, Term receiver);

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
	public void disconfirm(Formula formula, Term[] receivers);

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
	public void failure(ActionExpression action, Formula formula, Term receiver);

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
	public void failure(ActionExpression action, Formula formula, Term[] receivers);

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
	public void inform(Formula formula, Term receiver);

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
	public void inform(Formula formula, Term[] receivers);
    
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
    public void notUnderstood(ActionExpression action, Formula reason, Term receiver);

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
	public void notUnderstood(ActionExpression action, Formula reason, Term[] receivers);
    
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
    public void propose(ActionExpression action, Formula condition, Term receiver);

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
	public void propose(ActionExpression action, Formula condition, Term[] receivers);
    
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
	public void queryIf(Formula formula, Term receiver);

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
	public void queryIf(Formula formula, Term[] receivers);
    
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
    public void queryRef(IdentifyingExpression ire, Term receiver);
	
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
	public void queryRef(IdentifyingExpression ire, Term[] receivers);
    
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
	public void refuse(ActionExpression action, Formula reason, Term receiver);

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
	public void refuse(ActionExpression action, Formula reason, Term[] receivers);
    
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
	public void rejectProposal(ActionExpression action, Formula condition, Formula reason, Term receiver);

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
	public void rejectProposal(ActionExpression action, Formula condition, Formula reason, Term[] receivers);
    
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
	public void request(ActionExpression action, Term receiver);

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
	public void request(ActionExpression action, Term[] receivers);
    
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
	public void requestWhen(ActionExpression action, Formula condition, Term receiver);

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
	public void requestWhen(ActionExpression action, Formula condition, Term[] receivers);
    
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
	public void requestWhenever(ActionExpression action, Formula condition, Term receiver);

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
	public void requestWhenever(ActionExpression action, Formula condition, Term[] receivers);
    
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
	public void subscribe(IdentifyingExpression ire, Term receiver);

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
	public void subscribe(IdentifyingExpression ire, Term[] receivers);

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
	public void unsubscribe(IdentifyingExpression ire, Term receiver);

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
	public void unsubscribe(IdentifyingExpression ire, Term[] receivers);
    
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
	public void unsubscribe(ActionExpression action, Term receiver);

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
	public void unsubscribe(ActionExpression action, Term[] receivers);
    
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
	public void unsubscribe(ActionExpression action, Formula property, Term receiver);

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
	public void unsubscribe(ActionExpression action, Formula property, Term[] receivers);
} 
