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
 * Class GroundedBeliefTransfer
 * Created by Carole Adam, 11 February 2008
 */

import jade.core.AID;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.BeliefTransferSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP is only given to trustless agents.
 * It is a {@link BeliefTransferSIPAdapter} controlling the adoption
 * of belief transferred from any other agent.
 * Actually it makes the agent adopt a plan to check if the sender
 * does not have contradictory commitments on this information, by
 * asking the institution.
 * 
 * WARNING: if a button should allow modification of trustless at 
 * runtime, the corresponding method should not only change the 
 * boolean but also add this SIP to the agent's SIP table.
 *  
 * @author wdvh2120
 * @version 1.0 date 11 February 2008
 * @version 1.1 date 17 March 2008
 */
public class GroundedBeliefTransfer extends BeliefTransferSIPAdapter { //ActionDoneSIPAdapter {

	private final boolean DEBUG = false; 

	public GroundedBeliefTransfer(InstitutionalCapabilities capabilities) {
		/* Control any belief from any agent.
		 * Do not control not-belief or forall-belief.
		 * WARNING: only works if the agent is the only 
		 * receiver of the inform...
		 */
		super(capabilities,new MetaFormulaReferenceNode("phi"),new MetaTermReferenceNode("sender"),false,false);
	}


	@Override
	protected ArrayList doApply(MatchResult matchFormula,
			MatchResult matchAgent, ArrayList acceptResult,
			ArrayList refuseResult, SemanticRepresentation sr) {
		return doApply(matchFormula,matchAgent,acceptResult,acceptResult,refuseResult,sr);
	}
	
	@Override
	protected ArrayList doApply(MatchResult matchFormula,
			MatchResult matchAgent, ArrayList acceptResult,
			ArrayList localAcceptResult,
			ArrayList refuseResult, SemanticRepresentation sr) {

		if ((matchFormula != null)&&(matchAgent != null)) {
			InstitutionTools.printTraceMessage(myCapabilities.getAgentName()+" applies GrBelTr to sr="+sr,DEBUG);
			InstitutionTools.printTraceMessage("matchAgent="+matchAgent,DEBUG);
			InstitutionTools.printTraceMessage("matchFormula="+matchFormula,DEBUG);
			InstitutionTools.printTraceMessage("refuseResult="+refuseResult,DEBUG);
			InstitutionTools.printTraceMessage("acceptResult="+acceptResult,DEBUG);

			// useful features
			Term sender = matchAgent.term("sender");
			Term me = myCapabilities.getAgentName();
			Formula phi = matchFormula.formula("phi");

			// build the refuse result
			Formula refusal = new IntentionNode(me,
					new BelieveNode(sender,
							new NotNode(new IntentionNode(me,
									new BelieveNode(me,phi)	))	));
			refuseResult.add(new SemanticRepresentation(refusal));

			// do not handle inform from the agent himself 
			if (!sender.equals(me)) {

				/*********************************
				 * DIRECT ACCEPT CASES           *
				 * (no check at the institution) *
				 *********************************/
				// 1: CASES OF DIRECT ACCEPT WITHOUT CHECK AT THE INSTITUTION

				/*************************************
				 * DIRECT ACCEPT SUBCASE 1 :         *
				 * information from a trusted agent  *
				 * (the institution or its mediator) *
				 *************************************/
				// DIRECT ACCEPT CASE 1: INFORMATION FROM INSTITUTION
				// always believe the institution (or the agent will ask the institution if she is saying the truth...)
				if (((InstitutionalAgent)myCapabilities.getAgent()).belongsToInstitution(sender)) {
					return localAcceptResult;
				}

				// the mediator of an institution is believed to tell the truth
				if (((InstitutionalAgent)myCapabilities.getAgent()).isMediator(sender)) {
					return localAcceptResult;
				}


				/*********************************************
				 * DIRECT ACCEPT SUBCASE 2 :                 *
				 * information already known by the receiver *
				 *********************************************/
				// DIRECT ACCEPT CASE 2 
				// if the receiver already believes phi, simply accept without any check
				if (myCapabilities.getMyKBase().query(phi) != null) {
					return localAcceptResult;
				}


				/***********************************
				 * INDIRECT CASES                  *
				 * (need to decide whether a query *
				 *  to the institution is useful)  *
				 ***********************************/
				// 2: INFORMATION FROM ANY AGENT WHO IS NOT THE INSTITUTION
				// if the information comes from another agent, it can possibly be refused

				// LOCAL DECISION: IS THE ADVISE OF THE INSTITUTION NEEDED ??
				boolean localDecision = 
					// only if it is not an inform about an action done
					!(phi instanceof DoneNode) &&
					// only if it is not an inform about a mental attitude of the sender agent
					!phi.isMentalAttitude(sender)&&
					// only if it is not an inform about an institutional fact (what is interesting in this case
					// is rather to know if this fact is really institutional -> InstitutionalBeliefTransfer)
					!(phi instanceof InstitutionalFactNode) &&
					!(new NotNode(phi).getSimplifiedFormula() instanceof InstitutionalFactNode);
				// FIXME should also check not(done) and not(mental attitude) ??

				// do not check is_trying formulas
				Formula istryingPattern = new PredicateNode(SL.symbol("is_trying"),new ListOfTerm(new Term[] {
						sender,
						new MetaTermReferenceNode("institution"),
						new ActionExpressionNode(sender,new MetaTermReferenceNode("action"))}));
				localDecision = localDecision && (istryingPattern.match(phi)==null);

				/*****************************************
				 * NO CHECK NEEDED SUBCASE               *
				 * Some types of information do not      *
				 * need to be checked at the institution *
				 *****************************************/
				if (!localDecision) {
					return localAcceptResult;
				}
				//else {
					/********************************************
					 * CHECK NEEDED CASES :                     *
					 * the information need to be checked in    *
					 * ALL institutions the receiver belongs to *
					 ********************************************/
					// NEED TO SCAN ALL INSTITUTION TO GET ADVISE FROM ALL OF THEM

					// get the list of institutions the receiver belongs to
					ArrayList listOfInstitutions = ((InstitutionalAgent)myCapabilities.getAgent()).getAllInstitutions();
					// initial plan: succeeds - will be updated for each institution
					ActionExpression finalPlan = (ActionExpression)SL.term("(action "+me+" SUCCEED)");;

					// scan all institutions the agent belongs to and check for each one
					for (int i=0; i<listOfInstitutions.size(); i++) {
						String institution = listOfInstitutions.get(i).toString();
						Term institutionAgent = Tools.AID2Term(new AID(institution,AID.ISLOCALNAME));
						InstitutionTools.printTraceMessage("loop on institutions, i="+i,DEBUG);
						InstitutionTools.printTraceMessage("## inst("+i+")="+institution,DEBUG);

						// only if the agent is not the institution currently tested
						// otherwise do nothing, do not ask the institution agent since it is the agent himself (prevent institution from asking herself ...)
						if (!(me.equals(institutionAgent))) {
							// build the formula representing the sender's commitment on the opposite belief
							InstitutionalFactNode groundedBSenderNotPhi = 
								new InstitutionalFactNode(
										SL.term(institution),
										new BelieveNode(sender, new NotNode(phi).getSimplifiedFormula()));
							// build the formula representing that the answer of a given institution is already known
							Formula adviceAlreadyTaken = new BelieveNode(institutionAgent,new NotNode(groundedBSenderNotPhi));

							// ONLY if advice was NOT taken YET from this institution
							// otherwise (case of go on) -> do nothing, go to next institution
							if (myCapabilities.getMyKBase().query(adviceAlreadyTaken) == null)  {
								
								/*****************************************
								 * CHECK NEEDED SUBCASE 1 : LOCAL REFUSE *
								 * (if the agent has a contradiction in  *
								 *  his own KBase, no need to ask the    *
								 *  institution about another one)       *
								 *****************************************/
								// query the receiver's KBase about a sender's commitment on the opposite belief
								QueryResult qr = myCapabilities.getMyKBase().query(groundedBSenderNotPhi);
								InstitutionTools.printTraceMessage("advice not taken yet, qr="+qr,DEBUG);
								
								// if the agent personally knows a contradictory engagement of the sender
								if (qr != null) {
									// he refuses the sent belief
									return refuseResult;
								}
								
								/**********************************************
								 * CHECK NEEDED SUBCASE 2 : ASK INSTITUTION   *
								 * (ask institution before deciding to accept *
								 *  or refuse the sent belief)                *
								 **********************************************/
								// The agent does not have contradictory information in his KBase.
								// He thus asks the institution to be sure there are really no contradiction
								// (except if he is the institution, thus needing no confirmation of qr : already managed before)
								//else {
									// BUILD THE PLAN TO QUERY THE INSTITUTION
									// the formula to check to know if the institution has answered the query
									Formula answerReceived1 = 
										new BelieveNode(me,new OrNode(groundedBSenderNotPhi,new NotNode(groundedBSenderNotPhi)));
									InstitutionTools.printTraceMessage("qr is null, build plan, answer received ="+answerReceived1,DEBUG);

									// the action to query the institution about a contradiction
									Term queryToInstitution =
										new ActionExpressionNode(
												myCapabilities.getAgentName(),
												SL.term("(QUERY-IF :sender ??sender :receiver (set ??receiver) :content (content ??dnotphi))").getSimplifiedTerm());
									queryToInstitution = queryToInstitution.instantiate("sender",myCapabilities.getAgentName());
									queryToInstitution = queryToInstitution.instantiate("receiver",institutionAgent);
									queryToInstitution = queryToInstitution.instantiate("dnotphi",groundedBSenderNotPhi);

									// the action to wait for the institution's answer (blocking the sequel of the plan)
									ActionExpression waitPlan = new ActionExpressionNode(myCapabilities.getAgentName(),SL.term("(WAIT (fact ??phi) 7000)"));
									waitPlan = (ActionExpression)waitPlan.instantiate("phi",answerReceived1);

									// the action to test if there are no contradiction in this institution (if test ok, continue to check next institutions)
									ActionExpression testAction = new ActionExpressionNode(myCapabilities.getAgentName(),SL.term("(TEST (fact "+new NotNode(groundedBSenderNotPhi)+"))"));

									// the sequential plan: query, wait answer, test if it is positive
									ActionExpression plan = new SequenceActionExpressionNode(queryToInstitution,waitPlan);
									plan = new SequenceActionExpressionNode(plan,testAction);

									// NORMAL ENDING: enqueue the plan to check THIS institution in the global plan
									InstitutionTools.printTraceMessage("plan="+plan,DEBUG);
									finalPlan = new SequenceActionExpressionNode(finalPlan,plan);
									InstitutionTools.printTraceMessage("final plan get new part "+finalPlan,DEBUG);
								//}//end if I have no local contradiction (qr==null)
							}//end if advice was not already taken
						}// end if I am not this institution
					}//end for all institutions

					InstitutionTools.printTraceMessage("end of loop on institutions: absorb",DEBUG);
					/* Finally answer after advice of all institutions
					 * Interpret the global sequential plan to get advice from all institutions:
					 * 	  - this plan fails as soon as one test reveals a contradiction: in this 
					 * 		case refuse the sent belief
					 *    - if all tests from all institutions fail (no contradiction), the
					 *    	global plan succeeds: in this case accept the belief transfer 
					 */
					myCapabilities.interpretAfterPlan(finalPlan,acceptResult,refuseResult);
					// absorb the original SR (other agent's belief). FIXME why ? could be asserted ?
					return new ArrayList();

				//}//end else (local decision: need to check)
			}//end if i am the receiver and not the sender
		}//end if match (applyResult != null)
		return null;
	}//end doapply
}


