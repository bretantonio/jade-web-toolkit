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

package jade.semantics.interpreter.sips.adapters;

/**
 * Planning.java
 * @date: created on 4 nov. 2004
 * @author : Vincent Pautret
 * @version 2.0 Carole Adam, November 2007 (new functioning) 
 **/

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP adapter is the root class to create planning SIPs, that is, SIPs
 * that computes a proper action plan to reach a given pattern of goal of the
 * semantic agent. This SIP adapter is NOT responsible ANYMORE for running
 * and monitoring the execution of the computed plan. 
 * (This is done by the PlanExecutionSIP that catches the generated 
 * <code>is_doing ??myself ??PLAN</code> SR and installs the corresponding 
 * behaviour on the agent. 
 * 
 * In this way, between the generation of the plan by the PlanningSIPAdapter, and its 
 * execution by the PlanExecutionSIP, other PlanProcessingSIPAdapters can intercept 
 * and post-process the plan.
 *  
 * <br>
 * Roughly speaking, this SIP adapter consumes Semantic Representations of the
 * form <code>(I ??myself ??goal)</code>, and, if applicable (that is, if a plan
 * can be generated), generates a SR of the form <code>is_doing ??myself ??plan</code>.
 * It also propagates the same SR (I ??myself ??goal) with maximal index to be 
 * asserted into the belief base without being further interpreted. In this way, 
 * the belief base contains an intention for all goals being currently performed 
 * by a generated plan.
 * </br>
 * 
 * <br>
 * Several instances of such a SIP may be added to the SIP table of the agent,
 * either to deal with different patterns of goals, or to provide possibly several
 * plans to deal with the same pattern of goal. If a given goal matches several
 * Planning SIP, the SIPs will be tried in the order they appeared in the agent's
 * SIP table. <i>Note that using such SIPs replaces the use of the
 * <code>Planner</code> interface in the previous JSA versions</i>.
 * </br>
 * <br>
 * The goals that are dealt with by no PlanningSIPAdapter will be dealt with
 * (if they match their patterns) by the two generic
 * {@link jade.semantics.interpreter.sips.RationalityPrinciple} and
 * {@link jade.semantics.interpreter.sips.ActionPerformance} PlanningSIPs.
 * </br>
 * 
 * @author Vincent Pautret - France Telecom
 * @author Vincent Louis - France Telecom
 * @version 06 November 2007 - Revision 2.0 (Carole Adam)
 * @since JSA 1.4
 */
public abstract class PlanningSIPAdapter extends SemanticInterpretationPrinciple {
        	
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Create a PlanningSIPAdapter to deal with a given pattern of goals.
     * 
	 * @param capabilities {@link SemanticCapabilities} instance of the
	 *                     semantic agent owning this instance of SIP.
     * @param goalPattern  the pattern of goal, this SIP may compute a plan to
     *                     reach 
     */
    public PlanningSIPAdapter(SemanticCapabilities capabilities, Formula goalPattern) {
        this(capabilities, goalPattern.toString());
    } 
	
    /**
     * Create a Planning SIP Adapter to deal with a given pattern of goals.
     * Equivalent to
	 * {@link #PlanningSIPAdapter(SemanticCapabilities, Formula)},
	 * with the <code>goalPattern</code> parameter specified as a {@link String}
	 * object (representing a FIPA-SL formula).
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the
	 *                     semantic agent owning this instance of SIP.
     * @param goalPattern  the pattern of goal, this SIP may compute a plan to
     *                     reach 
     */
    public PlanningSIPAdapter(SemanticCapabilities capabilities, String goalPattern) {
        super(capabilities,
        	  "(I ??myself " + goalPattern + ")",
        	  SemanticInterpretationPrincipleTable.PLANNING);
    } 

    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Returns a <code>is_doing ??myself ??PLAN</code> SR encapsulating the plan computed
     * by the abstract {@link #doApply(MatchResult, SemanticRepresentation)} method.
     * 
     * This SR will possibly be interpreted by some PlanPerformanceSIPAdapters that can
     * modify the encapsulated plan, and it will finally be interpreted by the PlanExecutionSIP 
     * that will install a PlanPerformanceBehaviour to perform the encapsulated plan.
     * 
     * This method also propagates the original intention SR with maximal index: 
     * this intention will not be interpreted anymore but it will be asserted in
     * the KBase.
     * 
     * @param sr {@inheritDoc}
     * @throws SemanticInterpretationPrincipleException {@inheritDoc}
     */
    @Override
	final public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = pattern.match(sr.getSLRepresentation());
            if (matchResult != null) {
                ActionExpression plan = doApply(matchResult, sr);
                if ( plan != null ) {
                	/* creates the is_doing(agent,plan) SR with the same annotations as the
                	 * original intentionSR, and sets its interpretation index to 0 so that 
                	 * it is interpreted by all SIPs.
                	 */ 
                	SemanticRepresentation isDoingPlanSR = new SemanticRepresentation(sr,0);
                	isDoingPlanSR.setSLRepresentation(
                			new PredicateNode(
                					SL.symbol("is_doing"),
                					new ListOfTerm(new Term[] {myCapabilities.getAgentName(),plan})));
                	
                	// annotations for the PlanPerformanceBehaviour
                	SemanticRepresentation taggingSR;
                	
                	taggingSR = new SemanticRepresentation(sr,
                			myCapabilities.getMySemanticInterpretationTable().getIndex(
                					SemanticInterpretationPrincipleTable.UNREACHABLE_GOAL));
                	isDoingPlanSR.addAnnotation(SemanticRepresentation.INTERPRET_ON_PLAN_EXCEPTION_KEY, taggingSR);
                	
                	taggingSR = new SemanticRepresentation(sr, getIndex()+1);
                	isDoingPlanSR.addAnnotation(SemanticRepresentation.INTERPRET_ON_PLAN_FAILURE_KEY, taggingSR);
                	
                	isDoingPlanSR.addAnnotation(SemanticRepresentation.CANCEL_ON_KEY,
                			new NotNode(sr.getSLRepresentation()));
                	
                	// returns these two SR, only the isDoingSR is further interpreted
                    ArrayList result = new ArrayList();
                    sr.setSemanticInterpretationPrincipleIndex(SemanticRepresentation.NO_LONGER_APPLICABLE);
                    result.add(sr);
                    result.add(isDoingPlanSR);
                    return result;
                }                 
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } 
	
	/**
	 * Method to be overridden in each sub-class, to compute a proper plan of
	 * action to reach the pattern of goal attached to the SIP.
	 * This method must return either:
	 * <ul>
	 *     <li><code>null</code> if the SIP is not able to compute a proper plan,</li>
	 *     <li>an {@link ActionExpression} representing a plan that can reach
	 *         the matched goal.</li>
	 * </ul>
	 * <b>Important note:</b> the Action Expression returned by this method
	 * should be exclusively built upon primitive actions that are defined as
	 * semantic actions within the agent's semantic action table. If it is not
	 * the case, the PlanExecutionSIP will not be able to generate a suitable JADE
	 * behaviour to perform the corresponding plan.
	 * 
	 * @param matchResult result of the matching of the pattern of goal specified
	 *                    in the constructor against the actual goal being
	 *                    interpreted.
	 * @param sr          input SR, which is to be consumed by the SIP if it is
	 *                    eventually applicable.
	 * @return <code>null</code> if the SIP is not applicable, or a plan to reach
	 *         the agent's goal (given as an {@link ActionExpression})
	 */
	public abstract ActionExpression doApply(MatchResult matchResult, SemanticRepresentation sr);
}
