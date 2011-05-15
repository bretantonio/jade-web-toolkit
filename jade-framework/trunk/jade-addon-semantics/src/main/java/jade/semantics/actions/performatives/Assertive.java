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
 * Assertive.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * This class represents the semantic action: <code>Inform</code>. <br>
 * The sender informs the receiver that a given proposition is true.<br>
 * The content of this action is a proposition.<br>
 * <code>Inform</code> indicates that the sending agent:
 * <ul>
 * <li> holds that some proposition is true,
 * <li> intends that the receiving agent also comes to believe that the 
 * propositon is true,
 * <li> does not already believe that the receiver as any knowledge of the thruth
 * of the proposition.
 * </ul>
 * From the receiver's viewpoint, receiving a <code>Inform</code> message 
 * entitles it to believe that:
 * <ul>
 * <li> the sender believes the proposition that is the content of the message,
 * <li> the sender whishes the receiver to believe that proposition also.
 * </ul> 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public abstract class Assertive extends CommunicativeActionImpl {
            
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Creates a new Assertive Action prototype. The rational effect is set
     * by default to "(B ??receiver ??formula)".
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content. by default, set to <code>Formula</code>
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param rationalEffectRecognition pattern used to recognized the
     * rational effect of this action
     * @param feasibilityPrecondition pattern used to instantiate the SL 
     * formula representing the feasibility precondition of this action.
     * @param persistentFeasibilityPrecondition pattern used to 
     * instantiate the SL formula representing the persistent feasibility 
     * precondition of this action.
     * @param postCondition pattern used to instantiate the SL formula 
     * representing the postcondition of this action.
     */    
    
	public Assertive(SemanticCapabilities capabilities,
					 int surfacePerformative, 
					 Class[] surfaceContentFormat, 
					 String surfaceContentFormatMessage,
					 Formula rationalEffectRecognition, 
					 Formula feasibilityPrecondition, 
					 Formula persistentFeasibilityPrecondition, 
					 Formula postCondition) {
        super(capabilities,
			  surfacePerformative,
			  (surfaceContentFormat == null ? new Class[] {Formula.class} : surfaceContentFormat),
			  (surfaceContentFormatMessage == null ? "a formula" : surfaceContentFormatMessage),
			  (rationalEffectRecognition == null ? new MetaFormulaReferenceNode("phi") : rationalEffectRecognition),
			  SL.formula("(B ??receiver ??formula)"), feasibilityPrecondition, persistentFeasibilityPrecondition, postCondition);
        this.contentSize = 1;
    }
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * Creates a new Assertive using the given surface content. The content of 
     * the action is the given content.
     * @param surfaceContent a content
     * @return a semantic action
     * @throws SemanticInterpretationException if any exception occurs
     */
    @Override
	public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        setContent((Content)surfaceContent.getSimplifiedTerm());
        return this;
    }
    
   
    /**
     * @inheritDoc     
     */
    @Override
	public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        Formula rationalEffect = rationalEffectMatching.getFormula("phi");
        MetaTermReferenceNode receiver = new MetaTermReferenceNode("receiver");
        Formula content = rationalEffect.isBeliefFrom(receiver);
        if (content != null) {
            setReceiver(receiver.sm_value());
            setSurfaceContentElement(0, content);
            return true;
        }
        return false;
    } // End of newAction/2
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computeFeasibilityPrecondition() throws WrongTypeException {
        return (Formula)SL.instantiate(feasibilityPreconditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "formula", getContentElement(0));
    } // End of feasibilityPreconditionCalculation/0

    /**
     * @inheritDoc
     */
    @Override
	public Formula computePersistentFeasibilityPreconditon() throws WrongTypeException{
        return (Formula)SL.instantiate(persistentFeasibilityPreconditionPattern,
                "sender", getAuthor(),
                "formula", getContentElement(0));
    } // End of persistentFeasibilityPreconditonCalculation/0

    /**
     * @inheritDoc
     */
    @Override
	public Formula computeRationalEffect() throws WrongTypeException {
        return (Formula)SL.instantiate(rationalEffectPattern,
                "receiver", getReceiver(),
                "formula", getContentElement(0));
    } // End of rationalEffectCalculation/0
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computePostCondition() throws WrongTypeException {
        return (Formula)SL.instantiate(postConditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "formula", getContentElement(0));
    } // End of postConditionCalculation/0
    
} // End of class Inform
