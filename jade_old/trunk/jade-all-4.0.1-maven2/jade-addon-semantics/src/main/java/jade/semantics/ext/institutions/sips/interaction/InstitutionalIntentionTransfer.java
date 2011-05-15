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

package jade.semantics.ext.institutions.sips.interaction;

/*
 * Class InstitutionalIntentionTransfer.java
 * Created by Carole Adam, 17 December 2007
 * Revised by Carole Adam, 4 January 2007
 * Corrected by Carole Adam, 25 June 2008: 
 * 		added a debug code to prevent this SIP from managing communicative actions
 */

import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.IntentionTransferSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;


/**
 * This SIP intercepts the intentions of other agents (received when
 * these agents send a request for example) and manage them.
 *   - if the other agent is the institution, the request counts as an obligation
 *   - if the other agent has the power to oblige the agent to accept the request,
 *   and if the corresponding procedure was not performed yet, then the agent will 
 *   request him to perform this procedure (and refuse the intention transfer for 
 *   now). THEN, if the other agent performs the procedure, the intention will be 
 *   created by the ObligationTransferSIP when it interprets the created obligation
 *   - if no power exist then accept the request (adopt the intention)
 *  
 *  Revision 04 January 2008, Carole Adam
 *  Managing of is_trying predicate: when the communicated intention is already
 *  being tried by the agent, inform the sender of the intention about that
 *  
 *  @author wdvh2120
 *  @version 1.3 date 27 March 2008 - case of mediator + cleaning of code
 */

public class InstitutionalIntentionTransfer extends IntentionTransferSIPAdapter {

	private final boolean DEBUG = false;

	/* Constructor of an InstitutionalIntentionTransfer SIP.
	 * Pattern = any goal, from any agent.
	 * Feedback to the originating agent is not required.
	 */
	public InstitutionalIntentionTransfer(InstitutionalCapabilities capabilities) {
		super(capabilities,
				new MetaFormulaReferenceNode("goal"),
				new MetaTermReferenceNode("agent"),
				// feedback NOT required
				false);
	}

	/* Apply method
	 * (non-Javadoc)
	 * @see jade.semantics.interpreter.sips.adapters.IntentionTransferSIPAdapter#doApply(jade.semantics.lang.sl.tools.MatchResult, jade.semantics.lang.sl.tools.MatchResult, jade.util.leap.ArrayList, jade.util.leap.ArrayList, jade.semantics.interpreter.SemanticRepresentation)
	 */
	@Override
	protected ArrayList doApply(MatchResult matchGoal,
			MatchResult matchAgent, ArrayList acceptResult,
			ArrayList refuseResult, SemanticRepresentation sr) {


		// get the agent sending the request
		Term otherAgent = matchAgent.term("agent");
		Term me = myCapabilities.getAgentName();
		// and the goal intended by this agent (decompose the SR = B(agent,I(other,done(agent,action))) )
		Formula done = matchGoal.formula("goal");

		// NORMAL CASE : intention from ANY (OTHER) AGENT

		/*
		 * SPECIAL SUBCASE : the goal is to perform an action
		 * Store the fact that the requester is interested in the
		 * performance of this action (that is, he should be sent 
		 * a feedback when this action is performed)
		 * 
		 * Then check if the agent is already trying to reach this goal
		 * in order to inform the requester about that
		 * FIXME: be careful of double inform if feedback was required !!
		 * 
		 * Check if the action is an institutional one to avoid
		 * having agents interested in communicative acts
		 */ 
		if (done instanceof DoneNode) {
			Term theAction = ((DoneNode)done).as_action();

			// ********* DEBUG **********
			try {
				SemanticAction semAct = myCapabilities.getMySemanticActionTable().getSemanticActionInstance((ActionExpression)theAction);
				InstitutionTools.printTraceMessage("InstIntentionTransf, semAct(debug)="+semAct,DEBUG);
				System.err.println("IntentionTransfer on action of type "+semAct.getClass());
				if (semAct instanceof OntologicalAction) {
					System.err.println(" ---> ontological action: continue");
				}
				else if (semAct instanceof CommunicativeAction) {
					System.err.println(" ---> communicative action: stop");
					return null;
				}
			}
			catch(SemanticInterpretationException sie) {
				System.err.println("exception in building semantic action instance");
				sie.printStackTrace();
			}
			// ********** END DEBUG ********
			
			// predicate storing the fact that the requester is interested
			// in this action and should be informed of its performance
			Formula interested = new PredicateNode(SL.symbol("is_interested"),
					new ListOfTerm(new Term[] {otherAgent,
							// the requested action (actionExpression (action agent act))
							theAction}));
			myCapabilities.interpret(interested);

			InstitutionalAgent myself = (InstitutionalAgent)myCapabilities.getAgent();
			
			// SPECIAL CASE : the intention comes from an institution the agent belongs to
			// or from a mediator of one of the agent's institutions
			if ((myself.belongsToInstitution(otherAgent)) || (myself.isMediator(otherAgent)))	{
				String name = Tools.term2AID(otherAgent).getLocalName();

				/*
				 * Create and interpret the obligation to obey this request
				 * Tags will be put by ObligationTransfer on the generated intention
				 *   - is_trying sr to interpret when feasibility failure
				 *   - not(obligation) sr to interpret when the intention succeeds
				 */
				ArrayList resultNew = (ArrayList)refuseResult.clone();
				resultNew.add(new SemanticRepresentation(new InstitutionalFactNode(SL.term(name),new ObligationNode(done))));

				/* But do NOT accept directly the intention:
				 * it will be generated by the interpretation of the obligation
				 * by the ObligationTransfer SIP
				 */
				return resultNew;
				
				// If the action was already done, still add the obligation: the
				// intention will be generated but then absorbed by GoalCommitment
			}

			// SITUATION 1: the agent is already trying -> inform the requester
			// check if the agent is already trying to perform this action
			Formula istrying = (Formula)SL.instantiate(
					InstitutionTools.istrying_AGENT_INSTITUTION_ACTION,
					"agent",me,
					"action",theAction);
			
			// if the agent is already trying to reach this goal (to perform this action)
			QueryResult qit = myCapabilities.getMyKBase().query(istrying);
			if (qit != null) { // cannot be just KNOWN since there is a meta-reference "institution" inside
				istrying = istrying.instantiate("institution",qit.getResult(0).term("institution"));
				// inform the requester that the agent is already trying to do what he asks him to do
				Formula intendToInform = InstitutionTools.buildIntendToInform(istrying, me, otherAgent);
				refuseResult.add(new SemanticRepresentation(intendToInform));
				// and refuse the intention
				return refuseResult;
				// TODO also explain why the action is currently infeasible ?
			}

			/* SITUATION 2: the agent cannot perform the action -> inform the requester
			 * If the precondition of this action is false, the agent cannot perform
			 * it, so it is useless to tell the requester how to oblige him to
			 * In this case simply accept (without feedback !!!): then, the agent will 
			 * try to perform the action and inform interested agents (the requester is
			 * one of them) that it is unfeasible when it fails  
			 * see method notifyFailureToInterestedAgents called in PerseveranceSIP
			 * 
			 * NOTE: This is called only if the action is obliged.
			 * If it was not, another mechanism makes the agent give a feedback to the requester...
			 */
			InstitutionTools.printTraceMessage("InstIntentionTransf, action="+theAction,DEBUG);
			try {
				SemanticAction semAct = myCapabilities.getMySemanticActionTable().getSemanticActionInstance((ActionExpression)theAction);
				InstitutionTools.printTraceMessage("InstIntentionTransf, semAct(2)="+semAct,DEBUG);
				Formula semPrecond = semAct.getFeasibilityPrecondition();
				QueryResult qrPrecond = myCapabilities.getMyKBase().query(semPrecond);
				if (qrPrecond == null) {
					return acceptResult;
					// FIXME: he will agree and try to perform an action that he knows to be unfeasible
				}
			}
			catch(SemanticInterpretationException sie) {
				System.err.println("exception in building semantic action instance");
				sie.printStackTrace();
			}
		}//end if done is a DoneNode 

		// NORMAL SUBCASE
		// check if this requesting agent has a valid power to oblige him to obey
		ActionExpression action = ((InstitutionalAgent)myCapabilities.getAgent()).existPower(otherAgent,done); 

		// FIRST SUBSUBCASE : there exists a power to oblige him to accept the intention
		if (action != null) {

			// if this procedure was already done, it has created the obligation
			// to obey and the intention to do so: never enter this branch
			if (myCapabilities.getMyKBase().query(new DoneNode(action,SL.TRUE)) != null) {
				System.err.println("action was already done, action="+action);
				return refuseResult;
			}

			// if it was not performed yet
			//else {
				/* 
				 * particular case: lazy agents refuse all intentions unless
				 * explicitly notified (see ObligationNotification SIP)
				 * They do not even help the requester by asking him to perform
				 * an obliging procedure, but simply inform him that they
				 * do NOT have the intention to cooperate
				 */  
				if (((InstitutionalCapabilities)myCapabilities).isLazy()) {
					Formula intendToInform = InstitutionTools.buildIntendToInform(
							new NotNode(new IntentionNode(me,done)), 
							me, otherAgent);
					refuseResult.add(new SemanticRepresentation(intendToInform));
				}
				// normal case: request the agent to perform it and refuse the
				// intention transfer while waiting the procedure to be performed
				else {
					Formula intendToRequest = InstitutionTools.buildIntendToRequest(action, me, otherAgent);
					refuseResult.add(new SemanticRepresentation(intendToRequest));
				}
				return refuseResult;
			//}//end else (not performed yet)
		}//end if action != null

		/* 
		 * SECOND SUBSUBCASE : the agent has no power to oblige me to accept 
		 * the intention. In this case cooperate, that is accept the intention
		 * transfer in order not to block the interaction 
		 */
		return acceptResult;
	}//end doApply

}//end class
