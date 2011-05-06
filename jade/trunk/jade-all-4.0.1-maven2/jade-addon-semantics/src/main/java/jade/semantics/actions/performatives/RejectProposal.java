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
 * RejectProposal.java
 * Created on 24 févr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.UnexpectedContentSIException;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * The action of rejecting a proposal to perform some action during a negotiation.
 * It is a general-purpose rejection to a previously submitted proposal. The agent
 * sending the rejection informs the receiver that it has no intention that the
 * recipient performs the given action under the given preconditions. 
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/02/24 Revision: 1.0
 * @since JSA 1.0
 */
public class RejectProposal extends NonPrimitiveInform {
    
    
    /**
     * Creates a new <code>RejectProposal</code> Action prototype. By default, the inform content
     * is set to "(and (not (I ??sender (done ??act ??phi))) ??psi)". By default, 
     * the surface content format is set to 
     * <code>[ActionExpression, Formula, Formula]</code>.  
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param informContentPattern pattern of the inform content
     */
    
	public RejectProposal(SemanticCapabilities capabilities,
			              int surfacePerformative, 
			              Class[] surfaceContentFormat,
			              String surfaceContentFormatMessage, 
			              Formula informContentPattern) {
        super(capabilities,
			  surfacePerformative,
			  (surfaceContentFormat == null ? new Class[] {ActionExpressionNode.class, Formula.class, Formula.class} : surfaceContentFormat),
			  (surfaceContentFormatMessage == null ? "an action and two formulas" : surfaceContentFormatMessage),
			  (informContentPattern == null ? SL.formula("(and (not (I ??sender (done ??act ??phi))) ??psi)") : informContentPattern));
    }
    
    /**
     * Creates a new <code>RejectProposal</code> Action prototype.
     * The surface content format, the surface content format message, and
     * the inform content pattern are the default ones. 
     * The surface performative is set to <code>REJECT_PROPOSAL</code>.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public RejectProposal(SemanticCapabilities capabilities) {
        this(capabilities, ACLMessage.REJECT_PROPOSAL, null, null, null);
    }
    
    /**
     * Returns an instance of <code>RejectProposal</code>
     * @return an instance of <code>RejectProposal</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new RejectProposal(getSemanticCapabilities());
    }
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    @Override
	public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        ActionExpressionNode act = (ActionExpressionNode)surfaceContent.getContentElement(0);
        if (act.as_agent().equals(getReceiver())) {
            return super.doNewAction(surfaceContent);
        }
        //else {
            throw new UnexpectedContentSIException(getSurfacePerformative(),
                    "an action from the receiver [" + getReceiver() + "]", act.as_agent().toString());
        //}
    }        
    
    /**
     * @inheritDoc
     */
    @Override
	protected Formula instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException {
        return (Formula)SL.instantiate(informContentPattern,
                "sender", getAuthor(),
                "act", surfaceContent.getContentElement(0),
                "phi", surfaceContent.getContentElement(1),
                "psi", surfaceContent.getContentElement(2));
    }    
} // End of class RejectProposal
