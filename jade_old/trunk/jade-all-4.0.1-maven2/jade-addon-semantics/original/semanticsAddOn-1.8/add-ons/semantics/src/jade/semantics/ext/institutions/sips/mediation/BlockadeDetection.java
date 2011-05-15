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


package jade.semantics.ext.institutions.sips.mediation;

/* 
 * Class BlockadeDetection.java
 * Created by Carole Adam, 11 March 2008
 */

import jade.semantics.actions.SemanticAction;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * SIP only for mediator (added at setup).
 * Test any new obligation to detect an inter-blocking 
 * with another existing obligation. If so, asserts a
 * specific predicate for future reference.
 * <code> (blockade-detected ??action1 ??action2) </code>
 * 
 * @author wdvh2120
 * @version 1.0 date 11 March 2008
 */
public class BlockadeDetection extends ApplicationSpecificSIPAdapter {

	private final boolean DEBUG = false;

	private ArrayList managedObligations; 

	// pattern = an institutional obligation
	public BlockadeDetection(InstitutionalCapabilities capabilities) {
		super(capabilities,
				// pattern = any agent is obliged to perform any action 
				// in any institution 
				// it should be the one for which this mediator works,
				// this is checked in doApply
				new InstitutionalFactNode(
						new MetaTermReferenceNode("inst"),
						new ObligationNode(
								new DoneNode(
										new ActionExpressionNode(
												new MetaTermReferenceNode("agent"),
												new MetaTermReferenceNode("action")
										),
										SL.TRUE
								))));
		managedObligations = new ArrayList();
	}

	@Override
	protected ArrayList doApply(MatchResult applyResult,
			ArrayList result, SemanticRepresentation sr) {

		if (applyResult != null) {

			Term institution = applyResult.term("inst");
			Term myInstitution = SL.term(((InstitutionalAgent)myCapabilities.getAgent()).isMediatorOf(myCapabilities.getAgentName()));

			if (institution.equals(myInstitution)) {

				ArrayList resultNew = (ArrayList)result.clone();

				InstitutionTools.printTraceMessage("############ BEGIN ##############",DEBUG);
				InstitutionTools.printTraceMessage("## new oblig detect, sr="+sr,DEBUG);
				InstitutionTools.printTraceMessage("## new oblig detect, pattern="+pattern,DEBUG);
				InstitutionTools.printTraceMessage("## new oblig detect, match="+applyResult,DEBUG);
				InstitutionTools.printTraceMessage("## ",DEBUG);

				Term theObligedAction = applyResult.term("action");
				Term theObligedAgent = applyResult.term("agent");
				ActionExpressionNode theObligedActionExpr = new ActionExpressionNode(theObligedAgent,theObligedAction);

				Formula obligedActionRealPrecondition = getRealPrecondition(theObligedActionExpr);
				InstitutionTools.printTraceMessage("## obliged action real precond = "+obligedActionRealPrecondition,DEBUG);

				if (obligedActionRealPrecondition instanceof DoneNode) { //FIXME useless test with the new getRealPrecond()

					ActionExpressionNode theBlockingActionExpr = (ActionExpressionNode)((DoneNode)obligedActionRealPrecondition).as_action();
					Formula blockingActionRealPrecondition = getRealPrecondition(theBlockingActionExpr);
					InstitutionTools.printTraceMessage("## blocking action real precondition = "+blockingActionRealPrecondition,DEBUG);

					if (blockingActionRealPrecondition instanceof DoneNode) {

						ActionExpressionNode theFinalActionExpr = (ActionExpressionNode)((DoneNode)blockingActionRealPrecondition).as_action();
						InstitutionTools.printTraceMessage("## final action expr="+theFinalActionExpr,DEBUG);
						InstitutionTools.printTraceMessage("## initial action expr="+theObligedActionExpr,DEBUG);

						if (SL.match(theFinalActionExpr,theObligedActionExpr) != null) {

							InstitutionTools.printTraceMessage("## !!! PROACTIVE INTER-BLOCKING DETECTION !!!",DEBUG);

							Formula blockadeDetectedPattern = SL.formula("(blockade-detected ??action1 ??action2)");
							Formula blockadeDetected = blockadeDetectedPattern.instantiate("action1",theObligedActionExpr).instantiate("action2",theBlockingActionExpr);

							InstitutionTools.printTraceMessage("manage oblig list="+managedObligations,DEBUG);
							if (!managedObligations.contains(theObligedActionExpr) && !managedObligations.contains(theBlockingActionExpr)) {
								managedObligations.add(theObligedActionExpr);
								managedObligations.add(theBlockingActionExpr);									
								InstitutionTools.printTraceMessage("## -> NEW blockade detected between "+theObligedActionExpr+" and "+theBlockingActionExpr+" : start handling!",DEBUG);
								InstitutionTools.printTraceMessage("## ---> assert (not inverse) "+blockadeDetected,DEBUG);
								resultNew.add(new SemanticRepresentation(blockadeDetected));
							}
							else {
								InstitutionTools.printTraceMessage("## -> OLD blockade detected between "+theObligedActionExpr+" and "+theBlockingActionExpr+" : already being handled",DEBUG);
							}
						}//end if final action (blocking the blocking action) = obliged action: inter blocking
					}//end if blocking action precond = done node
				}//end if obligedAction precond = done node
				return resultNew;
			}//end if institution is mine
		}//end if applyresult!=null
		return null;
	}


	/******************
	 * USEFUL METHODS *
	 ******************/
	
	public DoneNode getRealPrecondition(ActionExpression actionExpr) {
		// return the first blocking action
		ArrayList list = getPotentiallyBlockingActions(actionExpr);
		if ((list != null) && (list.size()>0)) {
			return new DoneNode((ActionExpression)list.get(0),SL.TRUE);
		}
		return null;
	}
	
	// Scan all the leaves of the preconditions, and possibly use business processes to
	// know if some action is blocking the performance of the given action
	// FIXME return an arrayList of blocking actions but for now the mediator only manages TWO interblocking actions at a time
	public ArrayList getPotentiallyBlockingActions(ActionExpression actionExpr) {
		try {
			ArrayList listOfBlockingActions = new ArrayList();

			SemanticAction semAct = myCapabilities.getMySemanticActionTable().getSemanticActionInstance((ActionExpression)actionExpr.getSimplifiedTerm());
			Formula semPrecond = semAct.getFeasibilityPrecondition();
			if (semPrecond instanceof AndNode) {
				// analyse all leaves
				ListOfFormula leaves = ((AndNode)semPrecond).getLeaves();
				for (int i=0;i<leaves.size();i++) {
					Formula oneLeave = getBusinessProcessPrecond(leaves.element(i));
					if (oneLeave instanceof DoneNode) {
						ActionExpressionNode action = (ActionExpressionNode)((DoneNode)oneLeave).as_action();
						listOfBlockingActions.add(action);
					}
				}
			}
			// otherwise directly analyse semPrecond - SAME management as for each leave
			else {
				Formula oneCond = getBusinessProcessPrecond(semPrecond);
				if (oneCond instanceof DoneNode) {
					ActionExpressionNode action = (ActionExpressionNode)((DoneNode)oneCond).as_action();
					listOfBlockingActions.add(action);
				}
			}
			// return an array list containing all blocking actions
			return listOfBlockingActions;

		}
		catch(SemanticInterpretationException sie) {
			sie.printStackTrace();
			return null;
		}
	}

	
	// auxiliary function using business processes to analyse one formula
	public Formula getBusinessProcessPrecond(Formula precond) {
		Formula businessProcessLink = SL.formula("(business-process-link (fact ??precond) (fact ??realPrecond))");
		businessProcessLink = businessProcessLink.instantiate("precond",precond);
		QueryResult bpQR = myCapabilities.getMyKBase().query(businessProcessLink);
		if (bpQR != null) {
			// Only read first result (there should only be one business process ...)
			return bpQR.getResult(0).formula("realPrecond");
		}
		return precond;
	}
	
}

