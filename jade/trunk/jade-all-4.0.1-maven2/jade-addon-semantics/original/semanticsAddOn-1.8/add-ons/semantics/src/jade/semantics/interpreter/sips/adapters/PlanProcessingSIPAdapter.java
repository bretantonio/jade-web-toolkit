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

/*
 * PlanProcessingSIPAdapter.java
 * Created on 6 November 2007
 * Author : Carole Adam
 */

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP adapter is the root class to create PlanProcessing SIPs, that is, SIPs
 * that catch <code>(is_doing ??agent ??plan)</code> and post-process this plan, before returning
 * a new <code>(is_doing ??agent ??plan')</code> SR encapsulating the processed plan.
 * 
 * Currently, this SIP adapter is responsible for the processing of the agent's
 * current plan: it can modify it before transmitting it to other 
 * PlanProcessingSIPAdapters.
 * At the end of the chain, the generic PlanExecutionSIP is responsible for running and 
 * monitoring the execution of the final plan as computed by the PlanningSIPAdapters 
 * and modified by the chain of PlanProcessingSIPAdapters. For that, it installs the
 * corresponding behaviour on the agent. 
 * 
 * Thanks to this new SIP, the planning SIP can delegate the running and 
 * monitoring of the plan that it computes, so that it is now possible to
 * intercept and post-process generated plans with a new PlanProcessingSIPAdapter.
 * 
 * The processing of the plan must be specified within the abstract
 * {@link #doApply(MatchResult, SemanticRepresentation)} method.
 *  
 * <br>
 * Roughly speaking, this SIP adapter intercepts Semantic Representations of the
 * form <code>(is_doing ??agent ??plan)</code>, and processes the given plan. 
 * It returns the same SR (possibly encapsulating a new plan) with 
 * index+1 so that the plan can possibly be processed again by other 
 * PlanProcessingSIPAdapters.
 * </br>
 * 
 * <br>
 * Several instances of such a SIP may be added to the SIP table of the agent,
 * to manage different kinds of post-processing (for example emotional tagging) 
 * of the plan. These SIPs will be tried in the order they appear in the agent's
 * SIP table. 
 * </br>
 * 
 * <br>
 * At the end of the processing by all matching ActingSIPAdapters, the plan is 
 * dealt with by the generic {@link jade.semantics.interpreter.sips.PlanExecutionSIP} 
 * that finally installs the behaviour to execute the final plan. 
 * </br>
 * 
 * @author Carole Adam - France Telecom
 * @since JSA 1.5
 */
public abstract class PlanProcessingSIPAdapter extends SemanticInterpretationPrinciple {
	 /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Create a PlanProcessingSIPAdapter to post-process a given plan.
     * 
	 * @param capabilities {@link SemanticCapabilities} instance of the
	 *                     semantic agent owning this instance of SIP.
     * @param planPattern  the pattern of plan that this SIP may post-process.
     */
    public PlanProcessingSIPAdapter(SemanticCapabilities capabilities, Term planPattern) {
    	super(capabilities,new PredicateNode(
				SL.symbol("is_doing"),
				new ListOfTerm(new Term[] {capabilities.getAgentName(),planPattern})),
            	  SemanticInterpretationPrincipleTable.ACTING);
    } 

    
    /**
     * Create a PlanProcessingSIPAdapter to post-process a given plan.
     * Equivalent to
	 * {@link #PlanningSIPAdapter(SemanticCapabilities, Formula)},
	 * with the <code>planPattern</code> parameter specified as a {@link String}
	 * object (representing a FIPA-SL plan).
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the
	 *                     semantic agent owning this instance of SIP.
     * @param planPattern  the pattern of plan that this SIP may post-process.
     */
    public PlanProcessingSIPAdapter(SemanticCapabilities capabilities, String planPattern) {
        this(capabilities,SL.term(planPattern));
    } 


    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Interprets a <code>(is_doing ??agent ??PLAN)</code> SR.
     * 
     * Returns a <code>(is_doing ??agent ??PLAN')</code> SR encapsulating the new plan 
     * as post-processed by the abstract {@link #doApply(MatchResult, SemanticRepresentation)} 
     * method. 
     * 
     * After application of all applicable PlanProcessingSIPAdapters, this SR will be 
     * interpreted by the generic PlanExecutionSIP that will install a PlanPerformanceBehaviour 
     * to perform the final plan.
     * 
     * @param sr {@inheritDoc}
     * @throws SemanticInterpretationPrincipleException {@inheritDoc}
     */
    @Override
	final public ArrayList apply(SemanticRepresentation sr) 
    	throws SemanticInterpretationPrincipleException {
        try {
        	MatchResult matchResult = pattern.match(sr.getSLRepresentation());
            if (matchResult != null) {
            	ActionExpression newPlan = doApply(matchResult, sr);
                if ( newPlan != null ) {
                	/* creates the (is_doing agent plan) SR with the same annotations as the original
                	 * intentionSR, but increases its index so that the plan is post-processed by the 
                	 * next PlanProcessingSIPAdapter
                	 */ 
                	SemanticRepresentation isDoingPlanSR = new SemanticRepresentation(sr,getIndex()+1);
                	isDoingPlanSR.setSLRepresentation(
                			new PredicateNode(
                					SL.symbol("is_doing"),
                					new ListOfTerm(new Term[] {myCapabilities.getAgentName(),newPlan})));
                	
                	// returns a new SR = (is_doing agent newPlan), index+1
                	ArrayList result = new ArrayList();
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
	 * Method to be overridden in each sub-class, to perform a proper post-processing 
	 * of the plan attached to the SIP.
	 *
	 * <b>Important note:</b> the Action Expression returned by this method
	 * should be exclusively built upon primitive actions that are defined as
	 * semantic actions within the agent's semantic action table. If it is not
	 * the case, the PlanExecutionSIP will not be able to generate a suitable JADE
	 * behaviour to perform the corresponding plan.
	 * 
	 * @param matchResult result of the matching of the plan specified in the 
	 * 					  constructor against the actual plan being processed.
	 * @param sr          input SR, which is to be propagated by the SIP if it is
	 *                    possibly applicable, so that other PlanProcessingSIPAdapters
	 *                    can possibly also post-process the plan before its execution.
	 *                    
	 * @return the post-processed plan to reach the agent's goal (given as an {@link ActionExpression})
	 */
	public abstract ActionExpression doApply(MatchResult matchResult, SemanticRepresentation sr) 
				throws SemanticInterpretationPrincipleException;

}
