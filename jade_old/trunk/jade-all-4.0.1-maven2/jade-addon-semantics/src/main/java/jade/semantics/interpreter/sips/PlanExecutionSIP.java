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

package jade.semantics.interpreter.sips;

/**
 * PlanExecutionSIP.java
 * @author Carole Adam, Vincent Louis - France Telecom
 * @version 1.0
 * @date November 2007
 * @since JSA 1.6
 */


import jade.semantics.behaviours.PlanPerformanceBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.PlanProcessingSIPAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;

/** 
 * Roughly speaking, this SIP intercepts Semantic Representations of the
 * form <code>(is_doing ??myself ??plan)</code>, and installs on the agent 
 * a behaviour running this plan.
 * 
 * It also produces the same SR, to be asserted into the belief base. In this
 * way, the belief base contains the agent's current plans, so that one can know
 * what the agent is doing.
 * 
 *  * <br>
 * If a generated plan ends with success, then the corresponding intention is
 * dropped from the belief base. If it ends with a feasibility failure, then the
 * corresponding intention is retried against by the following Planning SIP (to try
 * another plan, if there is any). If it ends with an execution failure, then the
 * corresponding intention is dropped from the belief base and considered as
 * unreachable, and no more plan is tried.
 * This is managed by the PlanPerformanceBehaviour, only by reading annotations
 * put by IntentionTransferSIPAdapter (before_plan and after_plan) and by the
 * PlanningSIP (success, failures, cancel...). The developer implementing a 
 * PlanProcessingSIPAdapter can make it modify these annotations.
 * </br>
 **/


public class PlanExecutionSIP extends PlanProcessingSIPAdapter {
	
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Create a Planning SIP Adapter to deal with a given pattern of goals.
     * 
	 * @param capabilities {@link SemanticCapabilities} instance of the
	 *                     semantic agent owning this instance of SIP.
	 * @param plan the plan to execute
     */
	public PlanExecutionSIP(SemanticCapabilities capabilities) {
		super(capabilities,"??__plan");
	}


	
	/*********************************************************************/
    /**				 			METHODS  								**/
    /*********************************************************************/

	 /**
     * Adds a new PlanPerformanceBehaviour ({@link PlanPerformanceBehaviour}) 
     * to the semantic agent, to perform the given plan.
     * 
     * @param sr {@inheritDoc}
     * @throws SemanticInterpretationPrincipleException {@inheritDoc}
     */
	@Override
	public ActionExpression doApply(MatchResult matchResult,
			SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
		// get the plan from the matched SR (is_doing AGENT PLAN)
		Term matchPlan = matchResult.term("__plan");
		
		if (matchPlan != null && matchPlan instanceof ActionExpression) {
			ActionExpression plan = (ActionExpression)matchPlan;

			// install the planPerformance behaviour
			try {
				SemanticRepresentation isDoingSR = new SemanticRepresentation(sr);
				isDoingSR.addAnnotation(SemanticRepresentation.CANCEL_ON_KEY, new NotNode(sr.getSLRepresentation()));
				potentiallyAddBehaviour(
						new PlanPerformanceBehaviour(plan,isDoingSR,myCapabilities));
				/* return the same plan, that is encapsulated in a is_doing SR 
				 * by the PlanProcessingSIPAdapter apply method; this SR is 
				 * returned so that it is possibly processed by the next SIPs
				 * and finally asserted in the KBase (in order to let one know 
				 * what the agent is doing)
				 */
				return plan;
			}//end try
			catch(Exception e) {
				e.printStackTrace();
				throw new SemanticInterpretationPrincipleException();
			}//end catch
		}//end if instance of
		return null;
		
	}//end doapply
	
}//end class
