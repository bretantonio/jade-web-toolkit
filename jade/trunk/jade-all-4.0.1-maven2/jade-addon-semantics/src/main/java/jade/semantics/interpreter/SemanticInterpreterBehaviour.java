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
 * SemanticInterpreterBehaviour.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.performatives.NotUnderstood;
import jade.semantics.interpreter.SemanticInterpretationPrinciple.SemanticInterpretationPrincipleException;
import jade.semantics.lang.sl.content.ContentParser;
import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.Logger;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;


/**
 * This class implements the semantic agents' behaviour that runs the semantic
 * interpretation algorithm.
 * 
 * It actually holds a FIFO list of events to be interpreted and consists of a
 * {@link jade.core.behaviours.CyclicBehaviour} that cyclically removes the last
 * event from this list and runs the semantic interpretation algorithm on it.
 * Currently, this list of events is fed in two ways:
 * <ul>
 *   <li>A new event is created upon receipt of a FIPA-ACL message by the semantic
 *       agent. In fact, only messages matching a given template are considered
 *       and such messages are exclusively received by the
 *       {@link SemanticInterpreterBehaviour}. As a consequence, the
 *       {@link jade.core.Agent#receive()} method of the JADE framework should
 *       be never used while programming with the JSA framework.</li>
 *   <li>Specific events may be created by calling one of the <code>interpret</code>
 *       methods. Actually, it is strongly advised to use instead the conveninent
 *       methods provided by the {@link jade.semantics.interpreter.SemanticCapabilities}
 *       class.</li>
 * </ul>
 * Events are represented by {@link jade.semantics.interpreter.SemanticRepresentation}
 * instances (or SRs) and are interpreted by rules (or SIPs) implemented by the
 * {@link jade.semantics.interpreter.SemanticInterpretationPrinciple} class. The
 * set of SIPs to use is set up by the {@link jade.semantics.interpreter.SemanticCapabilities#setupSemanticInterpretationPrinciples()}
 * method of the semantic agent's capabilities.
 * 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 * @since JSA 1.0
 * 
 * @version 1.1 2008/02/28 : get Logger from trunk
 */
public class SemanticInterpreterBehaviour extends CyclicBehaviour {
	
	
	/**
     * (B ??myself (done ...)) Pattern
     */
    static private final Formula donePattern = SL.formula("(B ??agent (done ??act))");

    /**
     * NotUnderstood content pattern
     */
    private Content NotUnderstoodContentPattern;
    
    /**
     * NotUnderstandable pattern
     */
    private Formula NotUnderstandablePattern;
    
    /**
     * NotUnderstandable default pattern
     */
    private Formula NotUnderstandableDefaultPattern = SL.formula("(B myself (not-understandable not-understandable null))");
    
    /**
     * Logger
     */
    private Logger logger;
    
    /**
     * List of behaviours to be added to the agent 
     */
    private ArrayList behaviourToAdd;
    
    /**
     * List of behaviours to be removed from the agent
     */
    private ArrayList behaviourToRemove;
    
    /**
     * List of formulae to be asserted in the belief base 
     */
    private ArrayList formulaToAssert;
    
    /**
     * List of events (external or internal)
     */
    private ArrayList eventList;
    
    /**
     * Pattern for matching incoming ACL messages
     */
    private MessageTemplate messageTemplate;
    
	/**
	 * The semantic capabilities of the semantic agent owning this behaviour.
	 */
	private SemanticCapabilities myCapabilities;

	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new semantic interpretation behaviour.
     * @param capabilities the {@link SemanticCapabilities} instance to which
     *                     this interpreter behaviour is attached.
     * @param msgTemplate a JADE message template describing the incoming FIPA-ACL
     *                    messages that must be semantically interpreted,
     *                    or <code>null</code> if all received messages must be
     *                    semantically interpreted. A non-<code>null</code> template
     *                    makes it possible to mix within the same JADE agent
     *                    a {@link SemanticInterpreterBehaviour} instance, which
     *                    received the specified messages, and other classical
     *                    behaviours, which receive the messages not matched by
     *                    this template.
     */
    public SemanticInterpreterBehaviour(MessageTemplate msgTemplate, SemanticCapabilities capabilities) {
        super();
        myCapabilities = capabilities;
        NotUnderstandablePattern = SL.formula("(B ??agent (not-understandable ??reason ??object))");
        NotUnderstoodContentPattern = SL.content("(??act (??reason ??object))");
        behaviourToAdd = new ArrayList();
        behaviourToRemove = new ArrayList();
        formulaToAssert = new ArrayList();
        eventList = new ArrayList();
        messageTemplate = msgTemplate;
        logger = Logger.getMyLogger("SEMANTICS.SemanticInterpreterBehaviour@" + capabilities.getAgent().getLocalName());
    } 
    
    /*********************************************************************/
    /**				 			LOCAL METHODS						    **/
    /*********************************************************************/
    /**
     * Receives an <b>ACL</b> message matching the template given in the 
     * constructor.
     * @return the incoming message
     * @see jade.core.Agent#receive(jade.lang.acl.MessageTemplate)
     * @see jade.core.Agent#receive()
     */
    private ACLMessage receiveNextMessage()
    {
        ACLMessage msg = null;
        if (messageTemplate != null) {
            msg = myAgent.receive(messageTemplate);
        }
        else {
            msg = myAgent.receive();
        }
        return msg;
    }
    
    /**
     * Returns a semantic representation that represents the ACL message passed
     * in parameter. Returns null if an exception occurs.
     * @param msg an ACL message
     * @return semantic representation that represents the ACL message passed in
     *         parameter.
     */
    private SemanticRepresentation directSRFromACLMessage(ACLMessage msg) {
        try {
        	CommunicativeAction action = myCapabilities.getMyACLMessageConsumer().consume(msg);
        	
        	if ( action == null || action instanceof NotUnderstood ) {
                return new SemanticRepresentation(SL.TRUE); // FIXME
        	}
        	//else {
        		SemanticRepresentation sr = new SemanticRepresentation();
        		sr.setMessage(msg);
        		sr.setSLRepresentation((Formula)SL.instantiate(donePattern,
            				"agent", myCapabilities.getAgentName(),
            				"act",   action.toActionExpression()));
            
        		// WARNING: PROVISORY CODE
                // used to simplify the semantical processing of communicative actions with multiple receivers
                msg.removeReceiver(myAgent.getAID());
                ArrayList list = new ArrayList();
                for(Iterator iter = msg.getAllReceiver();iter.hasNext();) {
                    list.add(iter.next());
                }
                msg.clearAllReceiver();
                msg.addReceiver(myAgent.getAID());
                for (int i =0; i < list.size(); i++) {
                    msg.addReceiver((AID)list.get(i));
                }
                //END OF PROVISORY CODE               
                return sr;
             //}
        }
        catch (SemanticInterpretationException sie) {
            return createNotUnderstandableSR(sie.getReason(), sie.getObject(), msg);
        }
    } 
	
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * This is the main method of the {@link SemanticInterpreterBehaviour} class,
     * in conformance to the JADE framework.
     * It consists in receiving any incoming FIPA-ACL message that matches the
     * message template specified in the constructor, transforming it into a
     * semantic representation, adding it to the internal FIFO list of events to
     * be interpreted, and running the semantic interpretation algorithm.
     * If no message was received, the method blocks the behaviour until the
     * agent receives a new message or the behaviour is restarted (in particular
     * when posting a new event to interpret by one of the <code>interpret</code>
     * methods of the {@link SemanticCapabilities} instance owning this behaviour. 
     * 
     * @see #interpret()
     * @see #interpret(SemanticRepresentation, boolean)
     * @see #interpret(ArrayList, boolean)
     */
    @Override
	public void action() {     
        try {
            ACLMessage msg = receiveNextMessage();
            SemanticRepresentation sr = null;
            if (msg != null) {
                sr = directSRFromACLMessage(msg);
                eventList.add(sr);
            }
            interpret();
            if (msg == null) block();
		} catch (Exception e) {
			//System.err.println("SemanticInterpreterBehaviour.action !!!!!!!");
           e.printStackTrace();
        }
    } 
    
    /**
     * Runs the semantic interpretation algorithm. This method is called by the
     * {@link #action()} method of the {@link SemanticInterpreterBehaviour} class.
     * Although it is not intended for usual JSA-based applications, this method
     * may be overridden to specialize this algorithm.
     * 
     * @throws SemanticInterpretationPrincipleException if a SIP throws such an
     *         exception during its application to an SR.
     */
    protected void interpret() throws SemanticInterpretationPrincipleException {    	
        while (eventList.size() != 0) {
//            if (logger.isLoggable(Logger.FINER)) logger.log(Logger.FINER, getMyCapabilities().getMySemanticActionTable().toString());

        	// PRINT SIP TABLE CONTENT
        	//if (logger.isLoggable(Logger.FINER)) logger.log(Logger.FINER, getMyCapabilities().getMySemanticInterpretationTable().toString());
            behaviourToAdd.clear();
            behaviourToRemove.clear();
            formulaToAssert.clear();
            
            ArrayList currentSRList = new ArrayList();
            ArrayList newSRList = new ArrayList();
        	SemanticRepresentation sr = (SemanticRepresentation)eventList.remove(0);
        	
            if (sr != null) {
            	if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "--------------- INTERPRET: " + sr);
                currentSRList.add(sr);
                if (!checkNotUnderstandable(currentSRList)) {
                    int minIndex = getMin(currentSRList);
					while(minIndex < getMyCapabilities().getMySemanticInterpretationTable().size() && minIndex >= 0) {
						for (int i= 0; i < newSRList.size(); i++) {
                            currentSRList.add(newSRList.get(i));
                        }
                        newSRList.clear();
                        SemanticInterpretationPrinciple currentSemanticInterpretationPrinciple =
                        	getMyCapabilities().getMySemanticInterpretationTable().getSemanticInterpretationPrinciple(minIndex);
//                        if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "CURRENT SEMANTIC INTERPRETATION PRINCIPLE: " + currentSemanticInterpretationPrinciple);
						while(!currentSRList.isEmpty()) {
							SemanticRepresentation currentSR = (SemanticRepresentation)currentSRList.remove(0);
//							if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "CURRENT SR: " + currentSR);
                            if (currentSR.getSemanticInterpretationPrincipleIndex() == minIndex) {
                                ArrayList srListResult = null;
                                srListResult = currentSemanticInterpretationPrinciple.apply(currentSR);
                                if (srListResult != null) {
									if (logger.isLoggable(Logger.FINER)) logger.log(Logger.FINER,
											"APPLIED SIP " + currentSemanticInterpretationPrinciple + "\nON " + currentSR.getSemanticInterpretationPrincipleIndex() + ", " + currentSR);
                                    for (int j = 0; j < srListResult.size(); j++) {
                                    	SemanticRepresentation resultSR = (SemanticRepresentation)srListResult.get(j);
                                    	resultSR.setSLRepresentation(
                                    			new BelieveNode(
                                    					myCapabilities.getAgentName(),
                                    					resultSR.getSLRepresentation().instantiate(
                                    							"myself",
                                    							myCapabilities.getAgentName()))
                                    			.getSimplifiedFormula());
                                        if (!newSRList.contains(resultSR) &&
                                                !currentSRList.contains(resultSR)) {
                                            newSRList.add(resultSR);
                                        }
                                        if (logger.isLoggable(Logger.FINER)) logger.log(Logger.FINER,
                                        		"PRODUCED: " + resultSR.getSemanticInterpretationPrincipleIndex() + ", " + resultSR);
                                    }
                                    if (checkNotUnderstandable(srListResult)) {
                                        newSRList.clear();
                                        currentSRList.clear();
                                        behaviourToAdd.clear();
                                        behaviourToRemove.clear();
                                        formulaToAssert.clear();
                                    }
                                } else {
//                                    if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, " STEP : " + currentSemanticInterpretationPrinciple + " not succeeded !");
                                    currentSR.setSemanticInterpretationPrincipleIndex(currentSR.getSemanticInterpretationPrincipleIndex() + 1);
                                    newSRList.add(currentSR);
                                }
                            } else {
                                newSRList.add(currentSR);
                            }
                        }
                        minIndex = getMin(newSRList);
                    }
                    for (int i = 0; i < newSRList.size(); i++) {
                        getMyCapabilities().getMyKBase().assertFormula(((SemanticRepresentation)newSRList.get(i)).getSLRepresentation());
                    }
                    for (int i = 0; i < behaviourToAdd.size(); i++) {			            
                        myAgent.addBehaviour((Behaviour)behaviourToAdd.get(i));
                    }
                    for (int i = 0; i < behaviourToRemove.size(); i++) {                       
                        myAgent.removeBehaviour((Behaviour)behaviourToRemove.get(i));
                    }
                    for (int i = 0; i < formulaToAssert.size(); i++) {
                    	getMyCapabilities().getMyKBase().assertFormula((Formula)formulaToAssert.get(i));
                    }
                }
            }
        }    	
    }
    
    /**
     * Returns the minimum index of a semantic interpretation principle
     * in the list of semantic representation. Returns -1 if the list is 
     * <code>null</code>.
     * @param srList a list (ArrayList) of SemanticRepresentation objects.
     * @return the minimum index
     */
    private int getMin(ArrayList srList) {
        if (srList == null) return -1;
        int min = -1;
        if (srList.size() > 0) {
            min = ((SemanticRepresentation)srList.get(0)).getSemanticInterpretationPrincipleIndex();
            for (int i = 1; i < srList.size(); i++) {
                if (((SemanticRepresentation)srList.get(i)).getSemanticInterpretationPrincipleIndex() < min ) {
                    min = ((SemanticRepresentation)srList.get(i)).getSemanticInterpretationPrincipleIndex();
                }
            }
        }
        return min;
    } 
    
    /**
     * Checks the consistency of a set of {@link SemanticRepresentation} instances.
     * It returns <code>true</code> if one of the SR is logically equivalent to
     * false and sends a <code>NOT-UNDERSTOOD</code> message (in reply to the
     * FIPA-ACL message attached to the inconsistent SR).
     * 
     * @param list the list of SR to check.
     * @return <code>true</code> if one of the SR contained in the list is
     *         logically inconsistent, or <code>false</code> otherwise. 
     */
    protected boolean checkNotUnderstandable(ArrayList list) {
        for(int i = 0; i < list.size(); i++) {
            SemanticRepresentation sr = (SemanticRepresentation)list.get(i);
            MatchResult matchResult = null;
            if( sr.getSLRepresentation() instanceof FalseNode || 
                    (matchResult = SL.match(NotUnderstandablePattern, sr.getSLRepresentation())) != null ) {
                ACLMessage reply = sr.getMessage().createReply();
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                ActionExpression msg = new ActionExpressionNode(SL.term(sr.getMessage().getSender().toString()),
                        SL.term(sr.getMessage().toString()));
                Content content = null;
                try {
                    content = (Content)SL.instantiate(NotUnderstoodContentPattern, "act", new ActionContentExpressionNode(msg));
                    if ( matchResult == null ) {
                        content = (Content)SL.instantiate(content, 
                                "reason", SL.symbol("inconsistent"),
                                "object", SL.word(""));
                    }
                    else {
                        content = (Content)SL.instantiate(content, 
                                "reason", SL.symbol(((StringConstantNode)matchResult.getTerm("reason")).lx_value()), 
                                "object", matchResult.getTerm("object"));
                    }
                    
                    ContentParser cp = getMyCapabilities().getContentParser(sr.getMessage().getLanguage());
                    if ( cp == null ) {
                        cp = getMyCapabilities().getContentParser("fipa-sl");
                        reply.setLanguage("fipa-sl");
                    }
                    reply.setContent(cp.unparseContent(content));
                }
                catch(Exception e) {
                    e.printStackTrace();
                    reply.setContent("((action "+sr.getMessage().getSender().toString()+" "+sr.getMessage().toString()+") true)");
                    reply.setLanguage("fipa-sl");
                }
                myAgent.send(reply);
                return true;
            }
        }
        return false;
    } 
    
    /**
     * Creates a Semantic Representation. The SL representation of this SR means 
     * that the agent regards the incoming act as not understandable. 
     * @param reason the reason 
     * @param object the object 
     * @param msg the message 
     * @return a semantic representation
     */
    private SemanticRepresentation createNotUnderstandableSR(String reason, Term object, ACLMessage msg) {
        try {
            return new SemanticRepresentation(
                    (Formula)SL.instantiate(NotUnderstandablePattern,
                            "agent", getMyCapabilities().getAgentName(),
                            "reason", SL.string(reason),
                            "object", (object == null) ? SL.string("") : object),
                    msg,
                    0);
        }
        catch(Exception e) {
            e.printStackTrace();
            return new SemanticRepresentation(
                    NotUnderstandableDefaultPattern,
                    msg,
                    0);		
        }
    }
    
    /**
     * Returns the array used to store the list of behaviours to add to the
     * semantic agent at the end of the current (or next) run of the semantic
     * interpretation algorithm in case of success. This array is mainly used by the
     * {@link SemanticInterpretationPrinciple#potentiallyAddBehaviour(Behaviour)}
     * method to add behaviours during a SIP application, so that these behaviours
     * are actually added only if the current interpretation process ends
     * successfully. If the interpretation process fails (because an inconsistent
     * SR is produced), such behaviours are discarded.
     * 
     * @return the array to store the behaviours to add at the end of the current
     *         interpretation process, if it ends successfully.
     * @see SemanticInterpretationPrinciple#potentiallyAddBehaviour(Behaviour)
     */
    public ArrayList getBehaviourToAdd() {
        return behaviourToAdd;
    }
    
    /**
     * Returns the array used to store the list of behaviours to remove from the
     * semantic agent at the end of the current (or next) run of the semantic
     * interpretation algorithm in case of success. This array is mainly used by the
     * {@link SemanticInterpretationPrinciple#potentiallyRemoveBehaviour(Behaviour)}
     * method to remove behaviours during a SIP application, so that these behaviours
     * are actually removed only if the current interpretation process ends
     * successfully. If the interpretation process fails (because an inconsistent
     * SR is produced), such behaviours are discarded.
     * 
     * @return the array to store the behaviours to remove at the end of the current
     *         interpretation process, if it ends successfully.
     * @see SemanticInterpretationPrinciple#potentiallyRemoveBehaviour(Behaviour)
     */
    public ArrayList getBehaviourToRemove() {
        return behaviourToRemove;
    }
    
    /**
     * Returns the array used to store the list of formulas to assert into the
     * semantic agent's belief base at the end of the current (or next) run of
     * the semantic interpretation algorithm in case of success.
     * This array is mainly used by the
     * {@link SemanticInterpretationPrinciple#potentiallyAssertFormula(Formula)}
     * method to assert formulas during a SIP application, so that these formulas
     * are actually asserted only if the current interpretation process ends
     * successfully. If the interpretation process fails (because an inconsistent
     * SR is produced), such formulas are discarded.
     * 
     * @return the array to store the formulas to assert at the end of the current
     *         interpretation process, if it ends successfully.
     * @see SemanticInterpretationPrinciple#potentiallyAssertFormula(Formula)
     */
    public ArrayList getFormulaToAssert() {
        return formulaToAssert;
    }
    
    /**
     * Adds a SR to the internal FIFO list of events to be interpreted. Surrounds
     * the formula associated to the SR by a belief modality and instantiates
     * the <code>??myself</code> meta-reference with the semantic agent's AID.
     * 
     * @param sr the SR to add to the internal FIFO list of events to be
     *           interpreted.
     */
    private void addEventToInterpret(SemanticRepresentation sr) {
    	Term myself = getMyCapabilities().getAgentName();
    	SemanticRepresentation srToAdd = new SemanticRepresentation(sr);
    	srToAdd.setSLRepresentation(new BelieveNode(myself, sr.getSLRepresentation())
    			.instantiate("myself", myself)
    			.getSimplifiedFormula());
    	eventList.add(srToAdd);    	
    }
    
    /**
     * Posts a list of events (represented as {@link SemanticRepresentation}
     * instances) to be interpreted into the FIFO list hold by
     * this {@link SemanticInterpreterBehaviour} instance and runs the semantic
     * interpretation algorithm.
     * <br>
     * Before being posted, the formula associated to each SR is automatically
     * surrounded by a "<code>(B ??myself ...)</code>" belief modality and each
     * occurring "<code>??myself</code>" meta-reference is instantiated with
     * the semantic agent's AID. This accounts for the subjective character of
     * semantic representations.
     * </br>
     * <br>
     * The interpretation algorithm may be run either by the calling thread
     * (e.g. in the context of another behaviour calling this method) or by
     * restarting this behaviour.
     * In the latter case, the algorithm will therefore be run at the next
     * scheduling of this {@link SemanticInterpreterBehaviour} instance.
     * </br>
     * <p><i>Note:</i>
     * It is strongly advised not to call this method directly (unless you need
     * the semantic interpretation algorithm to be run by the calling thread),
     * but to use instead the convenient <code>interpret</code> methods provided
     * by the {@link SemanticCapabilities} class.
     * </p>
     * 
     * @param listOfSR the list of SR to add to the internal FIFO list of events
     *                 to be interpreted.
     * @param atOnce if <code>true</code>, runs the semantic interpretation
     *               algorithm in the context of the calling thread,
     *               if <code>false</code>, does not run the algorithm, but
     *               restarts this behaviour in order to run the algorithm at
     *               its next scheduling by the JADE framework.
     * @see SemanticCapabilities#interpret(ArrayList)
     * @see Behaviour#restart()
     */
    public void interpret(ArrayList listOfSR, boolean atOnce) {
    	for (int i=0; i<listOfSR.size(); i++) {
    		addEventToInterpret((SemanticRepresentation)listOfSR.get(i));
    	}
        if (atOnce) {
        	try {
				interpret();
			} catch (SemanticInterpretationPrincipleException e) {
				e.printStackTrace();
			}
        }
        else {
        	this.restart();
        }
    } 

    /**
     * Posts an event (represented by a {@link SemanticRepresentation} instance)
     * to be interpreted into the FIFO list hold by this
     * {@link SemanticInterpreterBehaviour} instance and runs the semantic
     * interpretation algorithm.
     * <br>
     * Before being posted, the formula associated to the SR is automatically
     * surrounded by a "<code>(B ??myself ...)</code>" belief modality and each
     * occurring "<code>??myself</code>" meta-reference is instantiated with
     * the semantic agent's AID. This accounts for the subjective character of
     * semantic representations.
     * </br>
     * <br>
     * The interpretation algorithm may be run either by the calling thread
     * (e.g. in the context of another behaviour calling this method) or by
     * restarting this behaviour.
     * In the latter case, the algorithm will therefore be run at the next
     * scheduling of this {@link SemanticInterpreterBehaviour} instance.
     * </br>
     * <p><i>Note:</i>
     * It is strongly advised not to call this method directly (unless you need
     * the semantic interpretation algorithm to be run by the calling thread),
     * but to use instead the convenient <code>interpret</code> methods provided
     * by the {@link SemanticCapabilities} class.
     * </p>
     * 
     * @param sr the SR to add to the internal FIFO list of events to be interpreted.
     * @param atOnce if <code>true</code>, runs the semantic interpretation
     *               algorithm in the context of the calling thread,
     *               if <code>false</code>, does not run the algorithm, but
     *               restarts this behaviour in order to run the algorithm at
     *               its next scheduling by the JADE framework.
     * @see SemanticCapabilities#interpret(SemanticRepresentation)
     * @see SemanticCapabilities#interpret(Formula)
     * @see SemanticCapabilities#interpret(String)
     * @see Behaviour#restart()
     */
    public void interpret(SemanticRepresentation sr, boolean atOnce) {
        addEventToInterpret(sr);
        if (atOnce) {
        	try {
				interpret();
			} catch (SemanticInterpretationPrincipleException e) {
				e.printStackTrace();
			}
        }
        else {
        	this.restart();
        }
    }

    /**
     * Returns the {@link SemanticCapabilities} instance of the semantic agent
     * owning this behaviour.
     * 
     * @return the {@link SemanticCapabilities} instance of the semantic agent
     * owning this behaviour.
     */
	public SemanticCapabilities getMyCapabilities() {
		return myCapabilities;
	}     
} 
