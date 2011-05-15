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
 * Refuse.java
 * Created on 25 avr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.UnexpectedContentSIException;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * The action of refusing to perform a given action, and explaining the reason 
 * for the refusal. The agent receiving a <code>refuse</code> act is entitled to
 * believe that:
 * <ul>
 * <li>the action has not been done,
 * <li>the action is not feasible (from the point of view of the sender of 
 * the refusal), and,
 * <li>the reason for the refusal is represented by a proposition which is the 
 * second element of the content (which may be the constant <code>true</code>).
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/04/25 Revision: 1.0
 * @since JSA 1.0
 */
public class Refuse extends CommunicativeActionImpl {
    
    /*
     * Patterns used to build the feasibility precondition
     * (the last two ones are the same as Inform and Disconfirm)
     */
    /**
     * Pattern used to build the feasibility precondition
     */
    private final Formula informContentPattern = SL.formula("(and ??condition (and (not (done ??act)) (not (I ??sender (done ??act)))))");
    /**
     * Pattern used to build the feasibility precondition
     */
    private final Formula disconfirmContentPattern = SL.formula("(feasible ??act)");
    /**
     * Pattern used to build the feasibility precondition
     */
    private final Formula informFeasibilityPreconditionPattern = SL.formula("(and (B ??sender ??formula) (not (B ??sender (or (or (B ??receiver ??formula) (B ??receiver (not ??formula))) (or (U ??receiver ??formula) (U ??receiver (not ??formula)))))))");
    /**
     * Pattern used to build the feasibility precondition
     */
    private final Formula disconfirmFeasibilityPreconditionPattern = SL.formula("(and (B ??sender ??formula) (not (B ??sender (or (or (B ??receiver ??formula) (B ??receiver (not ??formula))) (or (U ??receiver ??formula) (U ??receiver (not ??formula)))))))");    
    
    /** ****************************************************************** */
    /** CONSTRUCTOR * */
    /** ****************************************************************** */
    
    /**
     * Creates a new <code>Refuse</code> prototype. By default, the surface content format is
     * set to [ActionExpression, Formula]. The rational effect recognition pattern
     * is set to "(and (B ??receiver (not (feasible ??act)))(and (B ??receiver 
     * ??condition) (and (B ??receiver (not (done ??act)))(B ??receiver 
     * (not (I ??sender (done ??act)))))))". 
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content (used to control the validity of the content)
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param rationalEffectRecognition pattern used to recognized the
     * rational effect of this action
     */
    
	public Refuse(SemanticCapabilities capabilities,
				  int surfacePerformative,
				  Class[] surfaceContentFormat, 
				  String surfaceContentFormatMessage, 
				  Formula rationalEffectRecognition) {
        super(capabilities,
			  surfacePerformative,
			  (surfaceContentFormat == null ? new Class[] {ActionExpressionNode.class, Formula.class} : surfaceContentFormat),
			  (surfaceContentFormatMessage == null ? "an action and a formula" : surfaceContentFormatMessage),
			  rationalEffectRecognition,
			  SL.formula(
                        "(and (B ??receiver (not (feasible ??act))) " +
                        "     (and (B ??receiver ??condition) " +
                        "          (and (B ??receiver (not (done ??act)))" +
                        "               (B ??receiver (not (I ??sender (done ??act))))" +
                        "           )" +
                        "      )" +
                ")"),
               null, null, null);
        setPerformative(ACLMessage.REFUSE);
        this.contentSize = 2;
    }
    
    /**
     * Creates a new <code>Refuse</code> prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>REFUSE</code>.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public Refuse(SemanticCapabilities capabilities) {
        this(capabilities, ACLMessage.REFUSE, null, null, null);
    }
    
    /**
     * Returns an instance of <code>Refuse</code>
     * @return an instance of <code>Refuse</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new Refuse(getSemanticCapabilities());
    }
    
    /** ****************************************************************** */
    /** METHODS * */
    /** ****************************************************************** */
    
    /**
     * @inheritDoc
     */
    @Override
	public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        if (((ActionExpressionNode)surfaceContent.getContentElement(0)).as_agent().equals(getAuthor())) {
            setContent((Content)surfaceContent.getSimplifiedTerm());
            return this;
        }
        //else {
            throw new UnexpectedContentSIException(getSurfacePerformative(),
                    "an action from the sender [" + getAuthor() + "]", ((ActionExpressionNode)surfaceContent.getContentElement(0)).as_agent().toString());
        //}
    }
    
    /**
     * @inheritDoc
     */
    @Override
	public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        if (getAuthor().equals(rationalEffectMatching.getTerm("sender"))) {
            setReceiver(rationalEffectMatching.getTerm("receiver"));
            setSurfaceContentElement(0, rationalEffectMatching.getTerm("act"));
            setSurfaceContentElement(1, rationalEffectMatching.getFormula("condition"));
            return true;
        }
        return false;
    }
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computeFeasibilityPrecondition() throws WrongTypeException {
        Formula disconfirmFP = (Formula)SL.instantiate(disconfirmFeasibilityPreconditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "formula", SL.instantiate(disconfirmContentPattern, "act", getContentElement(0)));
        Formula informFP = (Formula)SL.instantiate(informFeasibilityPreconditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "formula", SL.instantiate(informContentPattern,
                        "act", getContentElement(0),
                        "sender", getAuthor(),
                        "condition", getContentElement(1)));
        return new AndNode(disconfirmFP, informFP);
    } // End of feasibilityPreconditionCalculation/0
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computePersistentFeasibilityPreconditon() throws WrongTypeException{
        Formula disconfirmFP = (Formula)SL.instantiate(persistentFeasibilityPreconditionPattern,
                "sender", getAuthor(),
                "formula", new NotNode((Formula)SL.instantiate(disconfirmContentPattern, "act", getContentElement(0))));
        Formula informFP = (Formula)SL.instantiate(persistentFeasibilityPreconditionPattern,
                "sender", getAuthor(),
                "formula", SL.instantiate(informContentPattern,
                        "act", getContentElement(0),
                        "sender", getAuthor(),
                        "condition", getContentElement(1)));
        return new AndNode(disconfirmFP, informFP);
    }
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computeRationalEffect() throws WrongTypeException {
        return (Formula)SL.instantiate(rationalEffectPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "act", getContentElement(0),
                "condition", getContentElement(1));
    } // End of rationalEffectCalculation/0
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computePostCondition() throws WrongTypeException {
        Formula disconfirmFP = (Formula)SL.instantiate(postConditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "formula", new NotNode((Formula)SL.instantiate(disconfirmContentPattern, "act", getContentElement(0))));
        Formula informFP = (Formula)SL.instantiate(postConditionPattern,
                "sender", getAuthor(),
                "receiver", getReceiver(),
                "formula", SL.instantiate(informContentPattern,
                        "act", getContentElement(0),
                        "sender", getAuthor(),
                        "condition", getContentElement(1)));
        return new AndNode(disconfirmFP, informFP);
    } // End of postConditionCalculation/0
    
} // End of class Refuse