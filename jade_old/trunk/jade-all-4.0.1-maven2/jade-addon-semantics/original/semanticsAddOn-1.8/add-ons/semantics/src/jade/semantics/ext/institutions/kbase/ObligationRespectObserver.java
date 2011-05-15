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

package jade.semantics.ext.institutions.kbase;

/*
 * Created by Carole Adam, December 20, 2007
 */

import jade.core.AID;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.ext.institutions.sips.actions.InstitutionalActionDoneNew;
import jade.semantics.interpreter.SemanticInterpreterBehaviour;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.observers.Observer;
import jade.semantics.kbase.observers.ObserverAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This class allows to create observers dedicated to watching if
 * agents respect their obligations. It is created by the
 * {@link InstitutionalActionDoneNew} when the institutional effect
 * of the applied power is an obligation of another agent.
 * 
 * @author wdvh2120
 * @version Date 20 December 2007
 */

public class ObligationRespectObserver extends ObserverAdapter {

	// for any agent's behaviour
	private final boolean DEBUG = false;
	// for mediator
	private final boolean MDEBUG = false;

	/**********************
	 * *** ATTRIBUTES *** *
	 **********************/

	// the interpreter behaviour used to trigger the appropriate 
	// communicative behaviour in the action and timeout methods
	SemanticInterpreterBehaviour interpreter;

	// the institutional fact encapsulating the obligation whose respect
	// is watched by this observer
	InstitutionalFactNode obligation;

	// number of notifications before claim to institution
	int counter;

	// the agent to notify of the obligation if it is not respected 
	// before expiration of the delay
	Term agent;

	// the formula to observe (corresponding to the fulfilment of the obligation)
	// computed from obligation, no need to be stored

	// the mediator agent to inform in case of problem
	Term mediator;

	/*********************
	 * *** ACCESSORS *** *
	 *********************/

	/**
	 * Method to get the interpreter behaviour associated to this observer
	 * @return the interpreter attribute of this observer
	 */
	public SemanticInterpreterBehaviour getInterpreter() {
		return interpreter;
	}


	/************************
	 * *** CONSTRUCTORS *** *
	 ************************/

	/**
	 * CONSTRUCTOR with explicit counter of notifications before claim
	 * @param kbase the kbase watched by this observer
	 * @oldparam formula the formula to watch (the action that the watched agent is obliged to perform)
	 * @param timeout the delay before considering that the obligation was not respected
	 * @param counter the number of notifications to the guilty agent before warning the institution
	 * @param sib the interpreter behaviour used to send communicative acts
	 * @param oblig the InstitutionalFactNode encapsulating the obligation watched by this observer
	 * @param aMediator the Term representing the mediator to inform in case of problem 
	 *    (null if no mediation: in this case the institution is directly informed instead)
	 */
	public ObligationRespectObserver(
			KBase kbase, 
			long timeout, 
			int counter, 
			SemanticInterpreterBehaviour sib, 
			InstitutionalFactNode oblig,
			Term aMediator) {
		super(kbase,((ObligationNode)oblig.as_fact()).as_formula(),timeout);
		this.interpreter = sib;
		this.obligation = oblig;
		this.counter = counter;
		this.mediator = aMediator;
		Formula formula = ((ObligationNode)oblig.as_fact()).as_formula();
		if (formula instanceof DoneNode) {
			// obligations to do
			Term ae = ((DoneNode)formula).as_action();
			if (ae instanceof ActionExpressionNode) {
				agent = ((ActionExpressionNode)ae).as_agent();
			}
		}
		else if (formula instanceof BelieveNode) {
			// obligations to believe
			agent = ((BelieveNode)formula).as_agent();
		}
		else { 
			// if the obligation is not to perform an action, the agent
			// cannot know which agent to notify when it is not respected
			agent = null; 
		}
		InstitutionTools.printTraceMessage("\n BUILD OBLIGATION RESPECT OBSERVER", DEBUG);
		InstitutionTools.printTraceMessage(" - owner="+kbase.getAgentName(), DEBUG);
		InstitutionTools.printTraceMessage(" - obligation="+obligation, DEBUG);
		InstitutionTools.printTraceMessage(" - counter="+counter, DEBUG);
		InstitutionTools.printTraceMessage(" - mediator="+mediator, DEBUG);
		InstitutionTools.printTraceMessage(" - agent="+agent+"\n", DEBUG);
		InstitutionTools.printTraceMessage(" - observed formula="+getObservedFormula(), DEBUG);
		InstitutionTools.printTraceMessage(" - formula="+formula, DEBUG);
		
	}


	/**
	 * Same constructor but without specified mediator
	 * (matches old version of default constructor)
	 * 
	 * @param kbase the kbase watched by this observer
	 * @param timeout the delay before considering that the obligation was not respected
	 * @param counter the number of notifications to the guilty agent before warning the institution
	 * @param sib the interpreter behaviour used to send communicative acts
	 * @param oblig the InstitutionalFactNode encapsulating the obligation watched by this observer
	 */
	public ObligationRespectObserver(
			KBase kbase, 
			long timeout, 
			int counter, 
			SemanticInterpreterBehaviour sib, 
			InstitutionalFactNode oblig) {
		this(kbase,timeout,counter,sib,oblig,null);
	}

	/**
	 * CONSTRUCTOR with default counter = 1 and default mediator = null
	 * Same as before but the number of notifications is set to 1.
	 * Since there is no mediator the institution is directly warned
	 * 
	 * @param kbase the kbase watched by this observer
	 * @param timeout the delay before considering that the obligation was not respected
	 * @param sib the interpreter behaviour used to send communicative acts
	 * @param oblig the InstitutionalFactNode encapsulating the obligation watched by this observer
	 */
	public ObligationRespectObserver(KBase kbase, 
			long timeout, 
			SemanticInterpreterBehaviour sib, 
			InstitutionalFactNode oblig) {
		this(kbase,timeout,1,sib,oblig,null);
	}


	/**
	 * CONSTRUCTOR with specified mediator but default counter = 1
	 * Same as before but the number of notifications is set to 1.
	 * 
	 * @param kbase the kbase watched by this observer
	 * @param timeout the delay before considering that the obligation was not respected
	 * @param sib the interpreter behaviour used to send communicative acts
	 * @param oblig the InstitutionalFactNode encapsulating the obligation watched by this observer
	 * @param aMediator the Term representing the mediator to inform in case of problem
	 */
	public ObligationRespectObserver(KBase kbase, 
			long timeout, 
			SemanticInterpreterBehaviour sib, 
			InstitutionalFactNode oblig,
			Term aMediator) {
		this(kbase,timeout,1,sib,oblig,aMediator);
	}



	/*************************
	 * ******* METHODS *******
	 *************************/

	/**
	 * action method triggered when the watched formula becomes true.
	 * 
	 * The effect is to remove the current observer (once the action 
	 * is done, the obligation is fulfilled and need not to be watched 
	 * anymore). Moreover the timeout thread is disabled in order to
	 * prevent the timeout method from being performed anyway at the 
	 * end of the delay.
	 */
	@Override
	public void action(QueryResult value) {
		disableTimeout();
		getMyKBase().removeObserver(this);
		/* NOTE: retract the fulfilled obligation here would be less generic
		 * (only conscientious agents would retract the obligations of others,
		 * after having watched that they were fulfilled
		 * Generic solution: obligations of others are retracted by an 
		 * external ObligedActionDone SIP whatever the situation.
		 */ 
		InstitutionTools.printTraceMessage("ObligationRespectObserver.action(), value="+value, DEBUG);
	}


	/**
	 * timeout method invoked when the delay expires, if the timeout 
	 * is still active (it is disabled when the watched formula
	 * is observed and thus the action method invoked, or when the
	 * observer is removed from the KBase for any reason).
	 * 
	 * The effect is to repeatedly notify the guilty agent and give him
	 * a new delay to fulfil his obligation, until the counter is 0. In
	 * this last case if the obligation is still not fulfilled, the agent
	 * finally warns the institution of the situation. 
	 */
	@Override
	public void timeout() {
		// only execute this method if the timeout is still active
		if (isEnabledTimeout()) {
			// notify the agent 
			if (agent != null) {
				// WARNING getObservedFormula() prefixes the observed DoneNode with a Believe operator
				// -> need to extract the DoneNode : observedFormula = ((BelieveNode)getObservedFormula()).as_formula();
				InstitutionTools.printTraceMessage("ObligationRespectObserver.timeout() for agent "+getMyKBase().getAgentName(), DEBUG);

				// useful features
				InstitutionalAgent myself = ((InstitutionalAgent)interpreter.getMyCapabilities().getAgent());
				Term me = interpreter.getMyCapabilities().getAgentName();
				String institution = obligation.as_institution().toString();
				Formula obligationFormula = obligation.as_fact();
				Formula doneFormula = ((ObligationNode)obligationFormula).as_formula();
				InstitutionTools.printTraceMessage(" @ test if "+myself+" believes "+me+" to be the mediator of "+institution, DEBUG);

				/*********************************************************
				 * SPECIAL CASE : DETECTION OF INTERBLOCKING BY MEDIATOR *
				 *********************************************************/
				if (myself.believesThatMediatorIs(institution, me)) {

					InstitutionTools.printTraceMessage(" @ -> true ! now test if "+doneFormula+" is a done node", DEBUG);
					InstitutionTools.printTraceMessage("!!! MEDIATOR begins timeout of observer on "+getObservedFormula(), MDEBUG);

					// first check if it is not an inter-blocking
					if (doneFormula instanceof DoneNode) {
						ActionExpressionNode obligedActionExpr = (ActionExpressionNode)((DoneNode)doneFormula).as_action();
						Formula blockadeDetectedPattern = SL.formula("(blockade-detected ??action1 ??action2)");
						Formula blockadeWithAction1 = blockadeDetectedPattern.instantiate("action1", obligedActionExpr);
						Formula blockadeWithAction2 = blockadeDetectedPattern.instantiate("action2", obligedActionExpr);
						ActionExpressionNode action1=null;
						ActionExpressionNode action2=null;
						QueryResult qr = getMyKBase().query(blockadeWithAction1);
						InstitutionTools.printTraceMessage(" @ -> true, now query blockade1="+blockadeWithAction1, DEBUG);
						InstitutionTools.printTraceMessage(" @ -> qr="+qr, DEBUG);
						// check two possible orders of blockade
						if (qr != null) {
							action1 = obligedActionExpr;
							action2 = (ActionExpressionNode)qr.getResult(0).term("action2");
							InstitutionTools.printTraceMessage(" @ -> qr not null, action1="+action1+" ; action2="+action2, DEBUG);
						}
						else {
							qr = getMyKBase().query(blockadeWithAction2);
							InstitutionTools.printTraceMessage(" @ -> qr=null, now query blockade2="+blockadeWithAction2, DEBUG);
							if (qr != null) {
								action2 = obligedActionExpr;
								action1 = (ActionExpressionNode)qr.getResult(0).term("action1");
								InstitutionTools.printTraceMessage(" @ -> new qr not null, action1="+action1+" ; action2="+action2, DEBUG);
							}
						}

						/********************************************
						 * SUBCASE : MEDIATOR DETECTS INTERBLOCKING *
						 ********************************************/
						// specific management (useless to notify unreachable obligations)
						if ((action1!=null)&&(action2!=null)) {
							InstitutionTools.printTraceMessage("ObligationRespectObserver observes an interblocking between "+action1+" and "+action2+" !!!",DEBUG);

							// ******************************************************************
							// BEGIN OF TREATMENT FROM BLOCKADE MANAGING SIP

							// specific predicate to prevent from managing the same blockade twice
							Formula managingBlockade = new PredicateNode(SL.symbol("is-managing-blockade"),
									new ListOfTerm(new Term[]{
											me,SL.term(institution),
											action1,
											action2
									}));
							boolean newBlockade = false;
							if (getMyKBase().query(managingBlockade) == null) {
								getMyKBase().assertFormula(managingBlockade);
								// also assert the opposite order
								managingBlockade = new PredicateNode(SL.symbol("is-managing-blockade"),
										new ListOfTerm(new Term[]{
												me,SL.term(institution),
												action2,
												action1
										}));
								getMyKBase().assertFormula(managingBlockade);
								// no need to interpret these formulas since they trigger no SIP
								newBlockade = true;
							}

							// BOTH ISMANAGING ARE ALWAYS TRUE AT THE END OF THE TIMEOUT !!!
							// test if he is already managing one or the other of these two actions
							// if so do not try to mediate again
							Formula managingPattern = new PredicateNode(SL.symbol("is_managing"),
							new ListOfTerm(new Term[] {
							// the agent calling this method = the mediator
							me,
							// the given obligation
							new FactNode(new MetaFormulaReferenceNode("what"))}));
							Formula obligationPattern = new InstitutionalFactNode(SL.term(institution),new ObligationNode(new DoneNode(new MetaTermReferenceNode("action"),SL.TRUE)));
							Formula isManaging1 = managingPattern.instantiate("what",obligationPattern.instantiate("action",action1));
							Formula isManaging2 = managingPattern.instantiate("what",obligationPattern.instantiate("action",action2));

							if (newBlockade) {
								InstitutionTools.printTraceMessage("this blockade is NEW !!!", MDEBUG);

								// extract the two inter-blocking actions
								InstitutionTools.printTraceMessage("NEW !!! MANAGE BLOCKADE between "+action1+" and "+action2, DEBUG);
								ActionExpression plan = buildManagingPlan(action1, action2);

								// in case of success -> retract both is_managing formulas
								ArrayList successSRlist = new ArrayList();
								successSRlist.add(new SemanticRepresentation(new NotNode(isManaging1)));
								successSRlist.add(new SemanticRepresentation(new NotNode(isManaging2)));

								// in case of failure -> inform the institution of the type of violation
								// PREPARE THE MESSAGE FOR INSTITUTION
								TermSetNode actionsInvolved = new TermSetNode();
								actionsInvolved.addTerm(action1);
								actionsInvolved.addTerm(action2);
								Formula contentOfInform = new PredicateNode(
										SL.symbol("mediation-failed"),
										new ListOfTerm(new Term[] {
												actionsInvolved,
												SL.term("interblocking")}));
								// need to be asserted or the intention to inform will fail
								getMyKBase().assertFormula(contentOfInform);

								// get the institution agent
								Term institutionAgent = Tools.AID2Term(new AID(myself.isMediatorOf(me),AID.ISLOCALNAME));
								// intention to inform the institution of the problem
								Formula intendToInformInstitution = InstitutionTools.buildIntendToInform(contentOfInform,me,institutionAgent);
								ArrayList failureSRlist = new ArrayList();
								failureSRlist.add(new SemanticRepresentation(intendToInformInstitution));
								// retract the is-managing sr
								failureSRlist.add(new SemanticRepresentation(new NotNode(isManaging1)));
								failureSRlist.add(new SemanticRepresentation(new NotNode(isManaging2)));
								// should assert a has-managed sr ? or use standard beliefs to detect already managed complaints

								// try to perform the plan
								InstitutionTools.printTraceMessage(" --- plan="+plan,DEBUG);
								InstitutionTools.printTraceMessage(" --- successSR="+successSRlist,DEBUG);
								InstitutionTools.printTraceMessage(" --- failureSR="+failureSRlist,DEBUG);
								interpreter.getMyCapabilities().interpretAfterPlan(plan, successSRlist, failureSRlist);
								// the mediator who detects an interblocking should not perform the rest of the method
							}
							// even if the blockade is already being managed, return
							return;

							// END OF TREATMENT FROM BLOCKADE MANAGING SIP
							// ***********************************************
						}
					}//end if DoneNode
				}

				/*****************************************
				 * NORMAL CASE : NO INTERBLOCKING        *
				 *  - BEHAVIOUR OF ANY STANDARD AGENT    *
				 *  - OR OF MEDIATOR IF NO INTERBLOCKING *
				 *****************************************/

				/***********************************************************
				 * SUBCASE 1 : NOTIFICATIONS UNTIL END OF COUNTER          *
				 *   -> similar behaviour for mediator and standard agents *
				 ***********************************************************/
				// As long as the number of remaining notifications is positive
				// (notifications are useless in case of interblocking)
				if ((counter > 0)) { 
					/* Inform the guilty agent of his obligation
					 * WARNING: do not inform the agent himself ... (ok since this observer 
					 * should not even be posted for obligations of the agent himself) 
					 */
					// remark: one cannot adopt the intention to notify the agent if one
					// believes that this agent already knows his obligation
					Formula believeAgentIgnoreOblig = new BelieveNode(me,new NotNode(new BelieveNode(agent,obligation)));
					InstitutionTools.printTraceMessage(" -- "+getMyKBase().getAgentName()+" interprets "+believeAgentIgnoreOblig, DEBUG);
					Formula intendToNotifyOblig = InstitutionTools.buildIntendToInform(obligation,me,agent);
					InstitutionTools.printTraceMessage(" -- "+getMyKBase().getAgentName()+" interprets "+intendToNotifyOblig, DEBUG);
					// PB: some agents do not notify obligations (because it is useless)
					// -> FORCE NOTIFICATION:
					interpreter.getMyCapabilities().inform(obligation, agent);

					// Remove the current observer, and disable its timeout thread
					disableTimeout();
					getMyKBase().removeObserver(this);

					// add a new observer that is a copy of the current one but
					// with the number of notifications decreased by one
					// -> give a new chance to the guilty agent
					Observer obs = 
						new ObligationRespectObserver(getMyKBase(),getTimeOut(),counter-1,getInterpreter(),obligation,mediator);
					getMyKBase().addObserver(obs);
					obs.update(null);
				}

				/******************************************
				 * SUBCASE 2 : EXPIRATION OF COUNTER      *                           
				 *   -> specific different behaviours for *
				 *      mediator and standard agents      *
				 ******************************************/
				// interblocking case is fully managed above
				else if ((counter == 0)) { 
					// finally when all notifications have been made (without result)
					// remove the observer and disable its timeout
					disableTimeout();
					getMyKBase().removeObserver(this);

					Term institutionAgent = Tools.AID2Term(new AID(institution,AID.ISLOCALNAME));
					ActionExpression action = (ActionExpression) ((DoneNode)((BelieveNode)getObservedFormula()).as_formula()).as_action(); 

					/*****************************************
					 * END OF COUNTER - SUBCASE 1 : MEDIATOR * 
					 *****************************************/
					// IF THE AGENT IS THE MEDIATOR  (WARNING: now the mediator attribute is NOT null anymore for the mediator agent)
					// (ObligationInterpretation ensures that he is not already managing this problem
					// before adding this ObligationRespectObserver)
					// so does ComplaintManaging SIP
					if (myself.believesThatMediatorIs(institution, me)) {

						// PREPARE THE MESSAGE FOR INSTITUTION
						TermSetNode actionsInvolved = new TermSetNode();
						actionsInvolved.addTerm(action);
						Formula signalProblem = new PredicateNode(
								SL.symbol("mediation-failed"),
								new ListOfTerm(new Term[] {
										actionsInvolved,
										new MetaTermReferenceNode("reason")}));
						Formula notintend = new NotNode(new IntentionNode(agent,getObservedFormula()));

						// Formula actionWasPerformed to test if obligation is fulfilled during this time
						Formula actionWasPerformed = getObservedFormula();
						InstitutionTools.printTraceMessage("action was performed = "+actionWasPerformed, DEBUG);
						
						// TEST 1: istrying
						if (((InstitutionalCapabilities)getInterpreter().getMyCapabilities()
						).isTrying(agent, SL.term(institution), action)) {
							InstitutionTools.printTraceMessage("is trying !!!",DEBUG);
							signalProblem = signalProblem.instantiate("reason",SL.term("istrying"));
							interpreter.getMyCapabilities().inform(signalProblem,institutionAgent);	
						}

						// TEST 2: notintend
						else if (getMyKBase().query(notintend) != null) {
							InstitutionTools.printTraceMessage("not intend !!!",DEBUG);
							signalProblem = signalProblem.instantiate("reason",SL.term("notintend"));
							interpreter.getMyCapabilities().inform(signalProblem,institutionAgent);
						}

						// TEST 3: action was finally performed
						else if (getMyKBase().query(actionWasPerformed) != null) {
							// do nothing in this case
							InstitutionTools.printTraceMessage("action was performed !!!", DEBUG);
						}
						
						// DEFAULT CASE (NO TEST) : INFORM INSTITUTION
						else {
							// This complaint is not managed by institution yet
							// (actually the mediator should never enter this case)
							interpreter.getMyCapabilities().inform(new AndNode(obligation,new NotNode(getObservedFormula())),institutionAgent);
						}

					}

					/*********************************************
					 * END OF COUNTER - SUBCASE 2 : NOT MEDIATOR *
					 *********************************************/
					// ANY AGENT WHO IS NOT THE MEDIATOR
					else {
						// IF NO MEDIATION
						if (mediator == null) {
							// refer to the institution: warn its representative agent about the situation
							interpreter.getMyCapabilities().inform(new AndNode(obligation,new NotNode(getObservedFormula())),institutionAgent);
						}
						else {
							// complain to the mediator to trigger ComplaintManaging SIP
							// This SIP allows a not proactive mediator to handle problems
							// when an agent signals them to him (reactively)
							interpreter.getMyCapabilities().inform(new AndNode(obligation,new NotNode(getObservedFormula())),mediator);
						}
					}//end of not mediating agent
				}
			}
		}
	}



	/*****************************************************
	 * METHOD TO BUILD THE BIG CONDITIONAL MANAGING PLAN *
	 *****************************************************/

	public ActionExpression buildManagingPlan(ActionExpressionNode action1, ActionExpressionNode action2) {

		// resulting plan
		Term theAgent = interpreter.getMyCapabilities().getAgentName();
		ActionExpression plan = new ActionExpressionNode(theAgent,SL.term("FAIL"));

		// Success condition of the strategy (must be true within a given delay after application)
		// (Thus the mediator must be informed of the realisation of actions; otherwise he may try to
		// get an agent observing the action and try to ask him if action was performed)
		Formula successCondition = new AndNode(
				new DoneNode(action1,SL.TRUE),
				new DoneNode(action2,SL.TRUE));
		ActionExpression waitSuccess = new ActionExpressionNode(theAgent,SL.term("(WAIT (fact ??success) 7700)"));
		// action to perform at the end of each strategy to check if it was a success
		waitSuccess = (ActionExpression) waitSuccess.instantiate("success",successCondition);

		// pattern of interpretation action (to interpret the SRs representing the strategy)
		ActionExpression interpretFactPattern = new ActionExpressionNode(theAgent,SL.term("(INTERPRET ??factphi)")); 

		// get the list of strategies
		Formula strategyPredicate = SL.formula("(strategy ??action1 ??action2 ??strategy)");
		Formula strategyPredicate12 = strategyPredicate.instantiate("action1",action1);
		strategyPredicate12 = strategyPredicate12.instantiate("action2",action2);
		Formula strategyPredicate21 = strategyPredicate.instantiate("action1",action2);
		strategyPredicate21 = strategyPredicate21.instantiate("action2",action1);
		QueryResult qr12 = interpreter.getMyCapabilities().getMyKBase().query(strategyPredicate12);
		QueryResult qr21 = interpreter.getMyCapabilities().getMyKBase().query(strategyPredicate21);
		QueryResult qr;
		if (qr12 == null) {	qr = qr21;	}
		else if (qr21 == null) { qr = qr12;	}
		// only if both are not null
		else { qr = qr12.union(qr21); }
		InstitutionTools.printTraceMessage("query strategies result = "+qr,DEBUG);

		// scan the strategies and build the sub-plans
		if (qr != null) {
			for (int i=0; i< qr.size();i++) {
				MatchResult mri = qr.getResult(i);
				TermSetNode strategyi = (TermSetNode)mri.term("strategy");
				ActionExpression tryStrategyi = new ActionExpressionNode(theAgent,SL.term("SUCCEED"));
				// build the sequence interpretation action
				for (int j=0;j<strategyi.size();j++) {
					// extract the formula to interpret
					Formula factJ = ((FactNode)strategyi.getTerm(j)).as_formula();
					factJ = factJ.instantiate("mediator",theAgent);
					factJ = (Formula)InstitutionTools.instantiateFromMatchResult(factJ,mri);
					ActionExpression interpretFactJ = (ActionExpression)interpretFactPattern.instantiate("factphi",new FactNode(factJ));
					tryStrategyi = new SequenceActionExpressionNode(tryStrategyi,interpretFactJ);
				}
				ActionExpression plani = new SequenceActionExpressionNode(tryStrategyi,waitSuccess);
				plan = new AlternativeActionExpressionNode(plan,plani);					
			}
		}
		else {
			// no strategy available ...
			// plan = FAIL (already initialised to fail, do nothing more) 
		}

		return plan;
	}

}
