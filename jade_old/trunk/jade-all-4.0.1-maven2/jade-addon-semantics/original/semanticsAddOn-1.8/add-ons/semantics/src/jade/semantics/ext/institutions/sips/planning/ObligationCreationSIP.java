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

package jade.semantics.ext.institutions.sips.planning;

/*
 * Class ObligationCreationSIP
 * Created by Carole Adam - France Telecom, December 2007
 */

import jade.semantics.actions.SemanticAction;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.ext.institutions.kbase.InstitutionalFactFilters;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.PlanningSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;


/**
 * This SIP is a planning SIP adapter : it proposes a plan to
 * reach particular goals. Actually it checks if the intended
 * goal can become an obligation by exerting some power, and 
 * if so, it returns as a plan the institutional procedure of
 * this power. Thus by performing this plan the agent will oblige
 * some other agent to achieve his own goal. 
 * 
 * This is used by InstitutionalAgents because simple requests
 * to the other agent to perform the action will not work: 
 * institutional agents have a particular IntentionTransferSIP
 * making them refuse any direct request from another agent
 * (@see {@link jade.semantics.ext.institutions.InstitutionalCapabilities}.
 * Actually institutional agents only obey when obliged to do so.
 * 
 * In the new version the name of the institution is not given as
 * an attribute. Instead, the existPower method of the InstitutionalAgent
 * will test all the institutions to which this agent belongs (that is, 
 * all institutions for which he has an institutional KBase in his 
 * {@link InstitutionalFactFilters}).
 * 
 * @author wdvh2120
 * @version December 2007, revision 1.0
 * @since JSA 1.5
 */
public class ObligationCreationSIP extends PlanningSIPAdapter {

	private final boolean DEBUG = false;
	
	/**
	 * Constructor for an ObligationCreationSIP.
	 * The pattern is any action done by any agent.
	 * 
	 * @param capabilities semantic capabilities of the holding agent
	 */
	public ObligationCreationSIP(InstitutionalCapabilities capabilities) {
		super(capabilities,new DoneNode(
				new ActionExpressionNode(
						new MetaTermReferenceNode("agent"),
						new MetaTermReferenceNode("action")),
				new MetaFormulaReferenceNode("phi")));
	}


	/**
	 * Apply method of this planning SIP.
	 * If the holding agent is NOT the author of the intended action, determine 
	 * if he has some power, by performing some other action, of creating some 
	 * obligation for another agent to perform the intended action.
	 */

	@Override
	public ActionExpression doApply(MatchResult matchResult,
			SemanticRepresentation sr) {

		// if the agent intends that some action is performed by ANOTHER agent
		if (matchResult != null) {

			InstitutionTools.printTraceMessage("ObligCreation: matchResult="+matchResult,DEBUG);
			if (!matchResult.term("agent").equals(myCapabilities.getAgentName())) {				
				/* Extract the intended result, and search an action that 
				 * can be done to force the other agent to perform the 
				 * intended action.
				 */
				Formula done = ((IntentionNode)sr.getSLRepresentation()).as_formula();
				InstitutionTools.printTraceMessage("ObligCreation: done="+done,DEBUG);
				
				// check if the agent believes that he has the power to create an obligation
				ActionExpression action = ((InstitutionalAgent)myCapabilities.getAgent()
				).existPower(myCapabilities.getAgentName(),done);
				InstitutionTools.printTraceMessage("ObligCreation: existPower returns action="+action,DEBUG);
				
				// if such an action exists
				if (action!=null) {
					/* increase index so that sr is interpreted by next SIPs
					 * 
					 * TODO make this in an ObligationNotificationSIP that suggests
					 * a second plan: notifying the concerned agent of his obligation.
					 */
					sr.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+1);

					// check if it has not been performed yet
					QueryResult qr = myCapabilities.getMyKBase().query(new DoneNode(action,SL.TRUE));
					InstitutionTools.printTraceMessage("ObligCreation: query if action already done returns qr="+qr,DEBUG);
					if (qr == null) {
						try {
							/* Check if this action is feasible.
							 * TODO decompose AndNodes to explain precisely why the action is impossible.
							 */
							SemanticAction semAct = myCapabilities.getMySemanticActionTable().getSemanticActionInstance(action);
							Formula precondition = semAct.getFeasibilityPrecondition();
							InstitutionTools.printTraceMessage("ObligCreation: precond to perform this action is ="+precondition,DEBUG);
							if (myCapabilities.getMyKBase().query(precondition) != null) {
								// Precondition is valid: return this action as plan
								InstitutionTools.printTraceMessage("ObligCreation: precondition is valid",DEBUG);
								return action;
							}
							//else {
								InstitutionTools.printTraceMessage("ObligCreation: precondition is false",DEBUG);
								System.err.println("recommended action = "+action);
								System.err.println("precondition is false : "+precondition);
							//}
						}
						catch(SemanticInterpretationException e) {
							System.err.println("action="+action);
							e.printStackTrace();
						}
					}//end if action not already done
				}
			}
		}
		// if the actor is myself
		// if there is no action to force the other agent
		// or if the action was already performed
		// or if it is not feasible
		return null;
	}

}

