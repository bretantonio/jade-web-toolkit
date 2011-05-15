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
 * Class InstitutionalBeliefTransfer
 * Created by Carole Adam, 22/2/2008
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
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;


/**
 * This SIP prevents agents from adopting beliefs about institutional
 * facts without checking their truth by querying the institution
 * (functioning similar to GroundedBeliefTransfer)
 * 
 * @author wdvh2120
 * @version 1.0 date February 22, 2008
 */
public class InstitutionalBeliefTransfer extends BeliefTransferSIPAdapter {

	private final boolean DEBUG = false;
	
	// patterns
	private static final Term action_ME_queryif_SENDER_set_RECEIVER_content_DPHI =
		new ActionExpressionNode(
				new MetaTermReferenceNode("me"),
				SL.term("(QUERY-IF :sender ??sender :receiver (set ??receiver) :content (content ??dphi))").getSimplifiedTerm());
	
	// pattern that is handled by ObligationNotification SIP
	private static final Formula o_done_action_ME_ACTION_true = 
		new ObligationNode(
			new DoneNode(
				new ActionExpressionNode(
					new MetaTermReferenceNode("me"),
					new MetaTermReferenceNode("action")),
				SL.TRUE));
	
	public InstitutionalBeliefTransfer(InstitutionalCapabilities capabilities) {
		// also control the not pattern
		super(capabilities,
			// pattern of belief
			new InstitutionalFactNode(
				new MetaTermReferenceNode("institution"),
				new MetaFormulaReferenceNode("phi")),
			// originating agent
			new MetaTermReferenceNode("agent"),
			// also control notPattern, do not control allPattern
			true,false);
	}
	
	@Override
	protected ArrayList doApply(MatchResult matchFormula,
			MatchResult matchAgent, ArrayList acceptResult,
			ArrayList refuseResult, SemanticRepresentation sr) {

		InstitutionTools.printTraceMessage("*** \n InstitutionalBeliefTransfer (by "+myCapabilities.getAgentName()+"), sr="+sr, DEBUG);
		InstitutionTools.printTraceMessage("InstitutionalBeliefTransfer, matchAgent="+matchAgent, DEBUG);
		InstitutionTools.printTraceMessage("InstitutionalBeliefTransfer, matchFormula="+matchFormula+"\n *** \n", DEBUG);
		
		if ((matchAgent != null)&&(matchFormula!=null))  {
		
			//extract features 
			Term institution = matchFormula.term("institution");
			Term institutionAgent = Tools.AID2Term(new AID(institution.toString(),AID.ISLOCALNAME));
			Formula phi = matchFormula.formula("phi");
			Formula instPhi = new InstitutionalFactNode(institution,phi);
			Term sender = matchAgent.term("agent");
			Term me = myCapabilities.getAgentName();
			InstitutionTools.printTraceMessage(sender+" sent institutional ("+institution+") belief "+phi+" to "+me,DEBUG); 
			
			// build the refuse result: add the intention to inform the 
			// sender that I he is mistaken (so I refuse his information)
			Formula refusal = new IntentionNode(me,
					new BelieveNode(sender,new NotNode(instPhi)) );
			refuseResult.add(new SemanticRepresentation(refusal));
			
			// do not handle inform from the agent himself
			if (!sender.equals(me)) { 

				// -----------------------------------------
				// 0: SPECIAL CASES NOT HANDLED IN THIS SIP 
				// -----------------------------------------
				
				// an obligation of myself to perform an action
				Formula myObligation = (Formula)SL.instantiate(
						o_done_action_ME_ACTION_true,
						"me",myCapabilities.getAgentName());
				
				// when the transferred institutional fact matches this pattern, it should not be handled 
				// in this SIP (since it will be specifically handled by ObligationNotification)
				if (myObligation.match(phi) != null) {
					InstitutionTools.printTraceMessage(" case 0.1 : "+phi+" matches "+myObligation+" -> managed outside, return null", DEBUG);
					return null;
				}
				
				
				// -----------------------------------------------------------
				// 1: CASES OF DIRECT ACCEPT WITHOUT CHECK AT THE INSTITUTION
				// -----------------------------------------------------------
				
				// DIRECT ACCEPT CASE 1: INFORMATION FROM THE RIGHT INSTITUTION
				// always believe the institution informing about an institutional fact that she imposes/grounds herself
				if (institutionAgent.equals(sender)) {
					InstitutionTools.printTraceMessage(" case 1.1 : the sender is the institution "+sender+" -> accept immediately", DEBUG);
					return acceptResult;
				}
			
				// DIRECT ACCEPT CASE 1bis: INFORMATION FROM THE MEDIATOR OF THIS INSTITUTION
				if (((InstitutionalAgent)myCapabilities.getAgent()).believesThatMediatorIs(institution.toString(),sender)) {
					InstitutionTools.printTraceMessage(" case 1.2 : the sender is the mediator "+sender+" -> accept immediately", DEBUG);
					return acceptResult;
				}
				
				// DIRECT ACCEPT CASE 2: INFORMATION ALREADY BELIEVED TO BE TRUE
				// if the receiver already believes the institutional fact just sent, simply accept without any check
				if (myCapabilities.getMyKBase().query(instPhi) != null) {
					InstitutionTools.printTraceMessage(" case 1.3 : I already believe "+instPhi+" -> accept immediately", DEBUG);
					return acceptResult;
				}
			
				
				// -----------------------------------------------------------
				// 2: INFORMATION FROM ANY AGENT WHO IS NOT THE INSTITUTION
				// if the information does not come from the institution, it should be checked before acceptance
				// -----------------------------------------------------------
				// Contrarily to GroundedBeliefTransfer, only need to check one (known) institution here
				// The formula to ask is the institutional fact: instPhi
				
				// LOCAL CHECK WITHOUT ASKING THE INSTITUTION
				QueryResult qr1 = myCapabilities.getMyKBase().query(new NotNode(instPhi));
				QueryResult qr2 = myCapabilities.getMyKBase().query(new InstitutionalFactNode(institution,new NotNode(phi)).getSimplifiedFormula());
				InstitutionTools.printTraceMessage(" case 2 : new information, from a standard agent, first perform local check", DEBUG);
				InstitutionTools.printTraceMessage("   - query 1 on "+new NotNode(instPhi)+" -> returns "+qr1, DEBUG);
				InstitutionTools.printTraceMessage("   - query 1 on "+new InstitutionalFactNode(institution,new NotNode(phi)).getSimplifiedFormula()+" -> returns "+qr2, DEBUG);
				if ((qr1!=null) || (qr2 != null)) {
					InstitutionTools.printTraceMessage("   --> local check makes me refuse the information !", DEBUG);
					return refuseResult;
				}
				
				// prevent the institution from checking herself ...
				if (!(me.equals(institutionAgent))) {
					InstitutionTools.printTraceMessage("I am not the institution, I can ask her for more information", DEBUG);

					// ----------------------------------------
					// BUILD THE PLAN TO QUERY THE INSTITUTION
					
					// query if phi is grounded
					Term queryAction = (Term)SL.instantiate(
							action_ME_queryif_SENDER_set_RECEIVER_content_DPHI,
							"me",me,
							"sender",me,
							"receiver",institutionAgent,
							"dphi",instPhi);
										 
					// check if an answer was received 
					Formula answerReceived = new BelieveNode(me,new OrNode(instPhi,new NotNode(instPhi)));
					ActionExpression waitAction = new ActionExpressionNode(me,SL.term("(WAIT (fact ??phi) 2000)"));
					waitAction = (ActionExpression)waitAction.instantiate("phi",answerReceived);
					
					// test the answer of institution - succeeds if the institutional fact sent is true
					ActionExpression testAction = new ActionExpressionNode(me,SL.term("(TEST (fact "+instPhi+"))"));

					// final plan
					ActionExpression finalPlan = new SequenceActionExpressionNode(queryAction,waitAction);
					finalPlan = new SequenceActionExpressionNode(finalPlan,testAction);

					InstitutionTools.printTraceMessage(" my check plan = "+finalPlan, DEBUG);
					
					// END OF PLAN BUILDING
					// ---------------------
					
					// interpret the correct result depending on the execution of this plan
					myCapabilities.interpretAfterPlan(finalPlan,acceptResult,refuseResult);
					InstitutionTools.printTraceMessage("   - if plan succeeds, interpret "+acceptResult, DEBUG);
					InstitutionTools.printTraceMessage("   - if plan fails, interpret "+refuseResult, DEBUG);
					// absorb the original SR so that it is not handled twice (by different BeliefTransfer SIPs)
					return new ArrayList();
					
				}//end if I am not the institution agent grounding the sent institutional fact
					
			}//end if I am not the sender	
				
		}//end if match results are not null
		return null;
	}

}
