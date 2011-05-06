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
 * Class CommitmentInterpretation
 * Created by Carole Adam, 14 March 2008
 */

import jade.core.AID;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
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
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP manages new commitments.
 * @author wdvh2120
 * @version date March 14, 2008
 */
public class CommitmentInterpretation extends ApplicationSpecificSIPAdapter {

	private final boolean DEBUG = false;
	
	public CommitmentInterpretation(InstitutionalCapabilities capabilities) {
		super(capabilities,
			new BelieveNode(
				capabilities.getAgentName(),
				new InstitutionalFactNode(
					new MetaTermReferenceNode("institution"),
					new BelieveNode(
						new MetaTermReferenceNode("agent"),
						new MetaFormulaReferenceNode("phi")
					)
				)));
	}
	
	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		if (applyResult != null) {
			// build the result
			ArrayList resultNew = (ArrayList)result.clone();
			
			Term me = myCapabilities.getAgentName();
			Term committedAgent = applyResult.term("agent");
			// check if the committed agent is not the interpreting agent
			if (!me.equals(committedAgent)) {
				Term institution = applyResult.term("institution");
				InstitutionTools.printTraceMessage("new commitment of "+committedAgent+" towards "+me+" in "+institution, DEBUG);
				InstitutionalAgent interpretingAgent = (InstitutionalAgent)myCapabilities.getAgent();
				Term institutionAgent = Tools.AID2Term(new AID(institution.toString(),AID.ISLOCALNAME));
				
				// DO NOTHING IF THE COMMITTED AGENT IS EITHER AN INSTITUTION OR A MEDIATOR
				if (committedAgent.equals(institutionAgent)) {
					InstitutionTools.printTraceMessage("committed agent ("+committedAgent+") is the institution...", DEBUG);
					return null;
				}	
				if (interpretingAgent.believesThatMediatorIs(institution.toString(), committedAgent)) {
					InstitutionTools.printTraceMessage("committed agent ("+committedAgent+") is the mediator of institution "+institution+" ...", DEBUG);
					return null;
				}

				// some useful features
				Formula phi = applyResult.formula("phi");
				BelieveNode theBelief = new BelieveNode(committedAgent,phi);
				BelieveNode theOppositeBelief = (BelieveNode)new BelieveNode(committedAgent,new NotNode(phi)).getSimplifiedFormula();
				// D not bel phi
				InstitutionalFactNode theOppositeCommitment = new InstitutionalFactNode(institution,new NotNode(theBelief));
				// D bel not phi (more serious) (should imply the previous one)
				//InstitutionalFactNode theInverseCommitment = new InstitutionalFactNode(institution,theOppositeBelief);
				
				/*******************************************************
				 * FIRST CASE : COMMITMENT ABOUT an INSTITUTIONAL FACT *
				 * -> its validity can be checked exactly              *
				 *******************************************************/
				if (phi instanceof InstitutionalFactNode) {
					// in this case what should be checked is not possible inverse commitments 
					// but rather the real validity of the precondition
					
					/**********************************
					 * SUBCASE 1.1 : LOCAL CHECK ONLY *
					 **********************************/
					QueryResult qrInstLocal = myCapabilities.getMyKBase().query(phi);
					if (qrInstLocal == null) {
						// if the object of the commitment is locally believed to be false
						// adopt the intention to signal the problem
						Formula intendToSignal = InstitutionTools.buildIntendToInform(new NotNode(
							new BelieveNode(me,phi)), me, committedAgent);
						resultNew.add(new SemanticRepresentation(intendToSignal));
						return resultNew;
					}
					
					/*******************************************************************
					 * SUBCASE 1.2 : INSTITUTIONAL CHECK FOR CONSCIENTIOUS AGENTS ONLY *
					 *******************************************************************/
					if (((InstitutionalCapabilities)myCapabilities).isConscientious()) {
						ActionExpression checkPlan = InstitutionTools.buildCheckPlan(phi, me, institutionAgent);
						Formula ifViolation = InstitutionTools.buildIntendToInform(new NotNode(phi), me, committedAgent);
						ArrayList listOfViolationSR = new ArrayList();
						listOfViolationSR.add(new SemanticRepresentation(ifViolation));
						ArrayList listOfNoViolationSR = new ArrayList();
						InstitutionTools.printTraceMessage("conscientious agent adopts plan "+checkPlan, DEBUG);
						InstitutionTools.printTraceMessage(" ---> in case of success (violation) : "+listOfViolationSR, DEBUG);
						InstitutionTools.printTraceMessage(" ---> in case of failure (no violation) : "+listOfNoViolationSR, DEBUG);
						myCapabilities.interpretAfterPlan(checkPlan,listOfViolationSR,listOfNoViolationSR);
						return resultNew;
					}
				}
				
				/*******************************************************************************
				 * SECOND CASE : INSTITUTIONAL PRECONDITION is any (not institutional) formula *
				 * -> the only thing that can be checked is that the agent's new commitment is *
				 * not conflicting with one of his previous commitments                        *
				 *******************************************************************************/

				/***************************************************
				 * SUBCASE 2.1 : LOCAL SIMPLE CONTRADICTION        *
				 * (contradiction with a belief but no commitment) *
				 ***************************************************/
				// (the agent said the opposite before, but without institutionally committing on it)
				QueryResult qrSimple = myCapabilities.getMyKBase().query(theOppositeBelief);
				InstitutionTools.printTraceMessage("simple local check, f="+theOppositeBelief, DEBUG);
				InstitutionTools.printTraceMessage(" ---> qrSimple = "+qrSimple, DEBUG);
				if (qrSimple != null) {
					Formula intendToSignal = InstitutionTools.buildIntendToInform(new BelieveNode(me,theOppositeBelief), me, committedAgent);
					resultNew.add(new SemanticRepresentation(intendToSignal));
					return resultNew;
				}
				
				/************************************************************
				 * SUBCASE 2.2 : LOCAL GROUNDED CONTRADICTION               *
				 * (contradiction with a locally known previous commitment) *
				 ************************************************************/
				// institutional contradiction (still locally)
				QueryResult qrInstitutional = myCapabilities.getMyKBase().query(theOppositeCommitment);
				InstitutionTools.printTraceMessage("institutional local check, f="+theOppositeCommitment, DEBUG);
				InstitutionTools.printTraceMessage(" ---> qrInst = "+qrInstitutional, DEBUG);
				if (qrInstitutional != null) {
					Formula intendToSignal = InstitutionTools.buildIntendToInform(new BelieveNode(me,theOppositeCommitment), me, committedAgent);
					resultNew.add(new SemanticRepresentation(intendToSignal));
					return resultNew;
				}
				
				/****************************************************
				 * SUBCASE 2.3 : CONSCIENTIOUS CHECK OF COMMITMENTS *
				 * (if no local contradiction, conscientious agents *
				 * ask the institution about possibly ignored       *
				 * previous commitments)                            *
				 ****************************************************/
				// IF CONSCIENTIOUS : INSTITUTION CHECK (of institutional contradiction)
				// Check if the institutional fact is really TRUE
				if (((InstitutionalCapabilities)myCapabilities).isConscientious()) {
					// build the plan to ask the institution
					ActionExpression checkPlan = InstitutionTools.buildCheckPlan(theOppositeCommitment, me, institutionAgent);
					Formula ifViolation = new IntentionNode(me,new DoneNode(new ActionExpressionNode(me,InstitutionTools.buildInformTerm(SL.formula("violation"), me, committedAgent)),SL.TRUE));
					Formula ifNoViolation = new IntentionNode(me, new DoneNode(new ActionExpressionNode(me,InstitutionTools.buildInformTerm(SL.formula("no-violation"), me, committedAgent)),SL.TRUE));
					ArrayList listOfViolationSR = new ArrayList();
					listOfViolationSR.add(new SemanticRepresentation(ifViolation));
					ArrayList listOfNoViolationSR = new ArrayList();
					listOfNoViolationSR.add(new SemanticRepresentation(ifNoViolation));
					InstitutionTools.printTraceMessage("conscientious agent adopts plan "+checkPlan, DEBUG);
					InstitutionTools.printTraceMessage(" ---> in case of success (violation) : "+listOfViolationSR, DEBUG);
					InstitutionTools.printTraceMessage(" ---> in case of failure (no violation) : "+listOfNoViolationSR, DEBUG);
					myCapabilities.interpretAfterPlan(checkPlan,listOfViolationSR,listOfNoViolationSR);
					return resultNew;
				}
				return resultNew;	
			}
		}
		return null;

	}

}
