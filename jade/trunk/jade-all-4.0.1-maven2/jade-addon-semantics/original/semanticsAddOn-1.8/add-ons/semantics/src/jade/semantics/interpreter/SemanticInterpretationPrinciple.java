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

/*
 * SemanticInterpretationPrincipleImpl.java
 * Created on 29 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.core.behaviours.Behaviour;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This abstract class represents a Semantic Interpretation Principle (or "SIP"),
 * that is an interpretation rule to be used by the JSA engine.
 * When applied, such a rule consumes an input {@link SemanticRepresentation}
 * instance (or "SR") and produces zero, one or more output SRs.
 * Typical examples of SIPs are the {@link jade.semantics.interpreter.sips.ActionFeatures}
 * SIP, which computes the preconditions and effects of a just occured action
 * (including received messages), or the {@link jade.semantics.interpreter.sips.BeliefTransfer}
 * SIP, which determines
 * whether a fact, which an external agents intends the semantic (interpreting) agent
 * to believe, will be actually adopted as a regular belief of the semantic agent.
 * <br>
 * Before extending this class to create new SIPs, check the
 * {@link jade.semantics.interpreter.sips.adapters} package does not already
 * contains an appropriate adapter, which is generally more convenient to
 * specialize.
 * </br>
 *   
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public abstract class SemanticInterpretationPrinciple {
    
    /**
     * Class ID of the Semantic Interpretation Principle, used to add
     * the SIP at the right place into the SIP table
     */
    private int classIndex;

	/**
     * {@link SemanticCapabilities} instance owning this Semantic Interpretation
     * Principle.
     * In particular, this fields makes it possible to retrieve the SIP table,
     * which this SIP is stored in (using the
     * {@link SemanticCapabilities#getMySemanticInterpretationTable()} method).
     */
    protected SemanticCapabilities myCapabilities;
    
    /**
     * Pattern of the formula associated to SRs consumed by this Semantic
     * Interpretation Principle. This pattern is usually set when calling the
     * constructor of this class (and its subclasses) to create a new SIP
     * instance. This pattern should match all consumed SRs, however a matching
     * SR should not necessarily be consumed (it depends on the further computation
     * specified in the {@link #apply(SemanticRepresentation)} method).
     */
    protected Formula pattern;
    
    /**************************************************************************/
    /**								CONSTRUCTORS		 			         **/
    /**************************************************************************/

    /**
     * Creates a new {@link SemanticInterpretationPrinciple} instance.
     * Note that the pattern of formula specified for SRs to be consumed by the
     * created SIP is automatically surrounded by a "<code>(B ??myself ...)</code>"
     * belief modality, and all occurrences of the "<code>??myself</code>" meta-
     * reference are instantiated with the semantic agent's AID. For example,
     * specifying the "<code>(hasName ??myself ??name)</code>" pattern of SR is
     * equivalent to specifying the "<code>(B myAID (hasName myAID ??name))</code>"
     * one (where <code>myAID</code> stands for the actual semantic agent's AID).
     * 
     * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
     * @param pattern      Pattern of formula applicable to this SIP, that is
     *                     pattern of SRs consumed by this SIP.
     * @param classIndex   Class ID of this SIP (used to add the SIP at the right
     *                     place into the SIP table of the owning semantic agent,
     *                     see the {@link SemanticInterpretationPrincipleTable}
     *                     interface).
     * @see SemanticInterpretationPrincipleTable
     */
    protected SemanticInterpretationPrinciple(SemanticCapabilities capabilities,
											  Formula pattern,
											  int classIndex) {
     	this.myCapabilities = capabilities;
        this.pattern = new BelieveNode(capabilities.getAgentName(), pattern)
        		.instantiate("myself", capabilities.getAgentName()).getSimplifiedFormula();
		this.classIndex = classIndex;
    }
   
    /**
     * Creates a new {@link SemanticInterpretationPrinciple} instance. This is
     * the same constructor as {@link #SemanticInterpretationPrinciple(SemanticCapabilities, Formula, int)},
     * except the pattern of SR to be consumed by the created SIP is specified
     * as a {@link String} (representing a FIPA-SL formula).
     * 
     * @param capabilities {@link SemanticCapabilities} instance of the semantic
     *                     agent owning this instance of SIP.
     * @param pattern      Pattern of formula applicable to this SIP, that is
     *                     pattern of SRs consumed by this SIP.
     * @param classIndex   Class ID of this SIP (used to add the SIP at the right
     *                     place into the SIP table of the owning semantic agent,
     *                     see the {@link SemanticInterpretationPrincipleTable}
     *                     interface).
     * @see #SemanticInterpretationPrinciple(SemanticCapabilities, Formula, int)
     */
    protected SemanticInterpretationPrinciple(SemanticCapabilities capabilities,
											  String pattern,
											  int classIndex) {
        this(capabilities, SL.formula(pattern), classIndex);
    }

    /**
     * This exception is thrown by the
     * {@link SemanticInterpretationPrinciple#apply(SemanticRepresentation)}
     * method of SIP instances when an unexpected problem arises while it is
     * applied.
     * 
     * @see SemanticInterpretationPrinciple#apply(SemanticRepresentation)
     */
    public static class SemanticInterpretationPrincipleException extends Exception {
    	
    }
    
    /**************************************************************************/
    /**									ABSTRACT METHODS					 **/
    /**************************************************************************/
    
    /**
     * Tries to apply this {@link SemanticInterpretationPrinciple} instance
     * against an input {@link SemanticRepresentation} instance.
     * It may return either:
     * <ul>
     *   <li><code>null</code> if the SIP is actually not applicable (in this
     *       case, the input SR is not consumed by the SIP),</li>
     *   <li>or an {@link ArrayList} object, which contains the Semantic
     *       Representations produced by the SIP application (in this case, the
     *       input SR is consumed).</li>
     * </ul>
     * While being applied, a SIP may add/remove behaviours to/from the semantic
     * (interpreting) agent and assert facts into his belief base. To perform
     * such operations, one should use the provided
     * {@link SemanticInterpretationPrinciple#potentiallyAddBehaviour(Behaviour)},
     * {@link SemanticInterpretationPrinciple#potentiallyRemoveBehaviour(Behaviour)} and
     * {@link SemanticInterpretationPrinciple#potentiallyAssertFormula(Formula)}
     * methods, instead of the native ones available in the {@link jade.core.Agent}
     * and {@link jade.semantics.kbase.KBase} classes.
     * Otherwise, behaviours or facts may be not properly added, removed
     * or asserted (in particular if the current interpretation process turns
     * out to be inconsistent and thus discarded).
     *  
     * @param sr the Semantic Representation, on which to apply the SIP
     * @return a list of the SR produced by the SIP application, or <code>null</code>
     *         if the SIP is not applicable
     * @throws SemanticInterpretationPrincipleException if any exception occurs
     **/
    public abstract ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException;
    
    /**************************************************************************/
    /**									PUBLIC METHODS						 **/
    /**************************************************************************/
    
    /**
     * Returns the class ID of this SIP in the semantic agent's SIP table.
     * 
     * @return the class ID of this SIP.
     * @see SemanticInterpretationPrincipleTable
     */
    public int getClassIndex() {
        return classIndex;
    } 
    
    /**
     * Returns the actual index of this SIP within the semantic agent's SIP
     * table, or -1 if the SIP is not in the table.
     * 
     * @return the index of the SIP in the SIP table it belongs to.
     */
    public int getIndex() {
        return myCapabilities.getMySemanticInterpretationTable().getIndex(this);
    } 
    
    /**
     * Adds a behaviour to the semantic agent. This method is intended to be
     * called while applying this SIP, that is in the scope of the
     * {@link SemanticInterpretationPrinciple#apply(SemanticRepresentation)} method.
     * 
     * The difference with the usual
     * {@link jade.core.Agent#addBehaviour(jade.core.behaviours.Behaviour)}
     * method is that the behaviour is not added at once to the agent. It is
     * actually added at the end of the current interpretation process, only if
     * this process turns out to be successful.
     * 
     * @param behaviour a behaviour to add to the semantic agent
     * @see SemanticInterpretationPrinciple#apply(SemanticRepresentation)
     */
    public void potentiallyAddBehaviour(Behaviour behaviour) {
        myCapabilities.getSemanticInterpreterBehaviour().getBehaviourToAdd().add(behaviour);
    } 
    
    /**
     * Removes a behaviour from the semantic agent. This method is intended to be
     * called while applying this SIP, that is in the scope of the
     * {@link SemanticInterpretationPrinciple#apply(SemanticRepresentation)} method.
     * 
     * The difference with the usual
     * {@link jade.core.Agent#removeBehaviour(jade.core.behaviours.Behaviour)}
     * method is that the behaviour is not removed at once from the agent. It is
     * actually removed at the end of the current interpretation process, only if
     * this process turns out to be successful.
     * 
     * @param behaviour a behaviour to remove from the semantic agent
     * @see SemanticInterpretationPrinciple#apply(SemanticRepresentation)
     */
    public void potentiallyRemoveBehaviour(Behaviour behaviour) {
        myCapabilities.getSemanticInterpreterBehaviour().getBehaviourToRemove().add(behaviour);
    }
    
    /**
     * Asserts a formula into the semantic agent's belief base. This method is
     * intended to be called while applying this SIP, that is in the scope of the
     * {@link SemanticInterpretationPrinciple#apply(SemanticRepresentation)} method.
     * 
     * The difference with the usual
     * {@link jade.semantics.kbase.KBase#assertFormula(Formula)} method is that
     * the formula is not asserted at once into the belief base. It is
     * actually asserted at the end of the current interpretation process, only if
     * this process turns out to be successful.
     * 
     * @param formula a formula to assert into the semantic agent's belief base
     * @see SemanticInterpretationPrinciple#apply(SemanticRepresentation)
     */
   public void potentiallyAssertFormula(Formula formula) {
        myCapabilities.getSemanticInterpreterBehaviour().getFormulaToAssert().add(formula);
    }
   
   public void interpretAfterPlan(ActionExpression plan, ArrayList successSRlist, ArrayList failureSRlist) {
	   myCapabilities.interpretAfterPlan(plan, successSRlist, failureSRlist);
   }

   public void interpretAfterPlan(String plan, ArrayList successSRlist, ArrayList failureSRlist) {
	   myCapabilities.interpretAfterPlan(plan, successSRlist, failureSRlist);
   }
}
