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
 * Class ObligationInterpretation
 * Created by Carole Adam, 22 February 2008
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.ext.institutions.kbase.ObligationRespectObserver;
import jade.semantics.ext.institutions.sips.actions.InstitutionalActionDoneNew;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP is triggered when an agent observes a new obligation
 * for ANOTHER agent to perform an action. Two cases are managed:
 *   - the interpreting agent is the mediator of the institution
 *   imposing this obligation, and he is proactive, and he is
 *   not already managing a blockade involving this action
 *   - or the interpreting agent is observing this action and is
 *   conscientious (as he is observing this action one can say 
 *   that he is concerned with its performance and should check it).
 * In both cases an ObligationRespectObserver is added to the
 * interpreting agent's KBase to determine its subsequent behaviour
 * when the obligation is violated.
 * 
 * NOTE : this procedure of watching the respect of obligation was 
 * previously only done by agents creating an obligation by using a 
 * power (in {@link InstitutionalActionDoneNew}). The problem was 
 * that obligations created without using a power, or created by the 
 * obliged agent himself, were watched by nobody ...
 * So now only agents observing an action (and thus aware of if it is
 * performed or not) watch the corresponding obligation. To allow agents
 * creating an obligation by using a power to watch it even if they are 
 * not initially observing the action, they are added as observers when 
 * they use their power (by {@link InstitutionalActionDoneNew}). Thus, 
 * InstitutionalActionDoneNew does no longer post ObligationRespectObservers 
 * (since otherwise they could be posted twice for agents first using a 
 * power and then interpreting the resulting obligation).
 * 
 * @author wdvh2120
 * @version 1.0 date 22 February 2008
 * @version 1.1 date 21 March 2008 - simplification and clean of code
 */
public class ObligationInterpretation extends SemanticInterpretationPrinciple {

	private final boolean DEBUG = false;

	public ObligationInterpretation(InstitutionalCapabilities capabilities) {
		super(capabilities,
				// interprets the obligation for an agent to perform an action
				new InstitutionalFactNode(
						new MetaTermReferenceNode("institution"),
						new ObligationNode(
								new DoneNode(
										new ActionExpressionNode(
												new MetaTermReferenceNode("agent"),
												new MetaTermReferenceNode("action")
										),
										SL.TRUE
								)
						)
				),
				SemanticInterpretationPrincipleTable.INSTITUTIONAL);

	}

	@Override
	public ArrayList apply(SemanticRepresentation sr)
	throws SemanticInterpretationPrincipleException {

		MatchResult applyResult = pattern.match(sr.getSLRepresentation());
		if (applyResult != null) {
			Term agent = applyResult.term("agent");
			Term institution = applyResult.term("institution");
			Term action = applyResult.term("action").getSimplifiedTerm();
			ActionExpression actExpr = new ActionExpressionNode(agent,action);
			DoneNode doneAction = new DoneNode(actExpr,SL.TRUE);

			/* SPECIAL CASE: 
			 * if the action was already done, do not interpret
			 * the obligation again or it will stay in the KBase 
			 * while already respected
			 */ 
			if (myCapabilities.getMyKBase().query(doneAction) != null) {
				return new ArrayList();
			}

			// This SIP only applies if the obligation concerns another agent
			// (the obliged agent should not look after himself...)
			if (!agent.equals(myCapabilities.getAgentName())) {
				// build the action expression that the agent is obliged to perform
				InstitutionalFactNode instObligDoneAction = 
					new InstitutionalFactNode(institution,new ObligationNode(doneAction));
				InstitutionTools.printTraceMessage("ObligationInterpretation, instObligDoneAction="+instObligDoneAction, DEBUG);
				
				// THERE ARE TWO CASES WHEN THE AGENT SHOULD WATCH THE RESPECT OF THIS OBLIGATION
				// compute a boolean indicating if one of these conditions is true
				boolean test;

				/*******************************************************************
				 * CASE 1 : HE IS THE MEDIATOR OF THE CORRESPONDING INSTITUTION    *
				 * and he is not already managing a blockade involving this action *
				 *******************************************************************/
				// get the agent holding the role of mediator in this institution (null if there is no mediator)
				Term theMediator = ((InstitutionalAgent)myCapabilities.getAgent()).getMediator(institution.toString());
				InstitutionTools.printTraceMessage(" & I am "+myCapabilities.getAgentName()+" and the mediator is "+theMediator, DEBUG);
				
				// Check if the agent is the mediator (in this case he needs not being conscientious
				// and observing the action to watch the respect of the corresponding obligation)
				if (myCapabilities.getAgentName().equals(theMediator)) {
					test = true;
					InstitutionTools.printTraceMessage(" & --> I am the mediator", DEBUG);
					
					// instead of checking inter-blocking, just check !is-managing 
					// (asserted if proactive, even if there is inter-blocking)
					test = !((InstitutionalAgent)myCapabilities.getAgent()).isManaging(instObligDoneAction);
					InstitutionTools.printTraceMessage(" & test if I am not already managing "+instObligDoneAction+" ==> test="+test, DEBUG);
					
					// also test if the mediator is proactive
					// (otherwise the ObligationRespectObserver will only be added
					// after a complaint from an agent, by ComplaintManaging SIP)
					test = test && ((InstitutionalCapabilities)myCapabilities).isProactive();
					InstitutionTools.printTraceMessage(" & test if I am proactive ==> "+((InstitutionalCapabilities)myCapabilities).isProactive(), DEBUG);
					InstitutionTools.printTraceMessage(" & --> finally test = "+test, DEBUG);
					if (test) {
						// If all conditions are valid, assert that the mediator is 
						// now managing this new problem. This will be useful in
						// ComplaintManaging to appropriately answer the complainant.
						((InstitutionalAgent)myCapabilities.getAgent()).setManaging(instObligDoneAction);
						InstitutionTools.printTraceMessage("proactive manager (was not but) is now managing "+instObligDoneAction,DEBUG);
					}
					// FOR DEBUG
					if (!test) {
						// If the mediator is already managing a blockade involving this action,
						// there is nothing to do (management performed by BlockadeManaging SIP)
						InstitutionTools.printTraceMessage("is already managing the problem or is reactive : do not post observer",DEBUG);
					}// END FOR DEBUG
				}//end if mediator

				/**********************************************************
				 * CASE 2 : HE IS A CONSCIENTIOUS OBSERVER OF THIS ACTION *
				 **********************************************************/
				// check if the agent is conscientious and observes this action (otherwise the SIP does not apply)
				else if (((InstitutionalCapabilities)myCapabilities).isConscientious() 
						&& ((InstitutionalAgent)myCapabilities.getAgent()).isObserving(actExpr)) {
					test = true;
					InstitutionTools.printTraceMessage("conscientious observer: test=true", DEBUG);
				}

				/*******************************************
				 * DEFAULT CASE : DO NOT POST THE OBSERVER *
				 *******************************************/
				else {
					test = false;
				}
				
				/**************************************************************
				 * GENERIC CASE : IF TEST IS TRUE THEN ADD THE OBSERVER       *
				 * Two possible reasons for test to be true                   *
				 *   - the agent is the mediator AND not already managing a   *
				 *   blockade involving the action AND is proactive           *
				 *   - the agent observes the action AND is conscientious     *
				 **************************************************************/

				// if one or the other condition is true, the action should be watched
				if (test) {
					InstitutionTools.printTraceMessage(" & test is true so post an observer on "+instObligDoneAction, DEBUG);
					// Post an ObligationRespectObserver to watch the performance 
					// of the obliged action (this will handle the subsequent 
					// behaviour of the agent)
					ObligationRespectObserver obs = new ObligationRespectObserver(
							myCapabilities.getMyKBase(),
							// delay before notification and denunciation
							3000,
							// number of notifications before denunciation to institution
							1,
							myCapabilities.getSemanticInterpreterBehaviour(),
							instObligDoneAction,
							theMediator
					);
					InstitutionTools.printTraceMessage("ObligInterp adds obligRespectObs to "+myCapabilities.getAgentName()+" to watch "+instObligDoneAction,DEBUG);
					myCapabilities.getMyKBase().addObserver(obs);
					obs.update(null);
				}

				// in any case return the same pre-computed result
				// prepare the result containing the original SR with increased index
				ArrayList result = new ArrayList();
				sr.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+1);
				result.add(sr);
				return result;					
			}
		}
		return null;
	}

}
