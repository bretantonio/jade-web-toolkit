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
 * Alternative.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */

package jade.semantics.actions.operators;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionImpl;
import jade.semantics.behaviours.AlternativeBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * Defines a prototype for the alternative operator.
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 * @since JSA 1.0
 */
public class Alternative extends SemanticActionImpl {
    
    /**
     * Left action of the alternative (could be an alternative or a sequence)
     */
    private SemanticAction leftAction;
    
    /**
     * Right action of the alternative (could be an alternative or a sequence)
     */
    private SemanticAction rightAction;
    
    /**
     * Pattern used to recognize an alternative of actions
     */
    private Term alternativePattern = SL.term("(| ??leftPart ??rightPart)");;
    
    /**
     * Pattern used to recognize an alternative of formulae
     */
    private Formula orPattern = SL.formula("(or ??left ??right)");
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new Alternative Action prototype.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public Alternative(SemanticCapabilities capabilities) {
        super(capabilities);
    } 
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Creates a new instance of this prototype of semantic action from
     * the specified action expression. The action expression must match the
     * pattern (| ??leftPart ??rightPart). It returns an instance of alternative
     * with the left and the right action correctly set.
     * @param actionExpression
     *          an expression of action that specifies the instance to create
     * @return a new instance of alternative, or null if no instance of the 
     * semantic action with the specified action expression can be created
     * @throws SemanticInterpretationException if any exception occurs
     */
    @Override
	public SemanticAction newAction(ActionExpression actionExpression) throws SemanticInterpretationException {
        MatchResult matchResult = SL.match(alternativePattern, actionExpression);
        if (matchResult != null) {
            ActionExpression leftPart;
            ActionExpression rightPart;
            try {
                leftPart = (ActionExpression)matchResult.getTerm("leftPart");
                rightPart = (ActionExpression)matchResult.getTerm("rightPart");
            }
            catch (WrongTypeException e) {
                throw new SemanticInterpretationException("ill-formed-message", SL.word(""));
            }
            Alternative result = new Alternative(getSemanticCapabilities());
            result.setLeftAction(getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(leftPart));
            result.setRightAction(getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(rightPart));
            if (result.getLeftAction() != null) {
                if (result.getRightAction() != null) {
                    return result;
                }
                //else {
                    throw new SemanticInterpretationException("not-understood-action-expression", rightPart);
                //}
            }
            //else {
                throw new SemanticInterpretationException("not-understood-action-expression", leftPart);
            //}
        } 
        return null;
    } 
    
    /**
     * Returns an <code>Alternative</code> instance that is an alternative of 
     * all the actions which are in the list.
     * @param list a list of actions
     * @return an <code>Alternative</code> if the size of the list is more than 
     * 1, a <code>SemanticAction</code> if the size equals 1, null if the list 
     * is empty or if an exception occurs. 
     */
    public SemanticAction newAction(ArrayList list) {
        try {
            if (list.size() == 1) {
                return (SemanticAction)list.get(0);
            } else if (list.size() > 0 ) {
                Alternative result = new Alternative(getSemanticCapabilities());
                result.setLeftAction((SemanticAction)list.remove(0));
                result.setRightAction(newAction(list));
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
    
    /**
     * Creates a new instance of this prototype of semantic action from
     * the specified rational effect. The rational effect must match the pattern
     * (or ??left ??right).
     * @param rationalEffect a formula that specifies the rational effet of the
     *  instance to create
     * @param inReplyTo an ACL message the message to answer
     * @return a new instance of alternative, or null if no instance can be 
     * created
     */
    @Override
	public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
        try {
            MatchResult matchResult = SL.match(orPattern, rationalEffect);
            if (matchResult != null) {
                Formula leftRationalEffect = matchResult.getFormula("left");
                Formula rightRationalEffect = matchResult.getFormula("right");
                ArrayList actionList = new ArrayList(); 
				getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstances(actionList, leftRationalEffect, inReplyTo);
				getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstances(actionList, rightRationalEffect, inReplyTo);
                return newAction(actionList);
            }       
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
    
    /**
     * @inheritDoc
     */
    @Override
	public ActionExpression toActionExpression() throws SemanticInterpretationException {
        ActionExpression result;
        result = (ActionExpression)SL.instantiate(alternativePattern,
        		"leftPart", getLeftAction().toActionExpression(),
        		"rightPart", getRightAction().toActionExpression());
        SL.clearMetaReferences(alternativePattern);
        return result;
    } 
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computeFeasibilityPrecondition() 
    //throws WrongTypeException 
    {
        return new OrNode(getLeftAction().getFeasibilityPrecondition(), getRightAction().getFeasibilityPrecondition()).getSimplifiedFormula();
    } 
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computePersistentFeasibilityPreconditon() 
    //throws WrongTypeException 
    {
        return new OrNode(getLeftAction().getPersistentFeasibilityPrecondition(), getRightAction().getPersistentFeasibilityPrecondition()).getSimplifiedFormula();
    } 
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computeRationalEffect() 
    //throws WrongTypeException  
    {
        return new OrNode(getLeftAction().getRationalEffect(), getRightAction().getRationalEffect()).getSimplifiedFormula();
    } 
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computePostCondition() 
    //throws WrongTypeException 
    {
        return new OrNode(getLeftAction().getPostCondition(), getRightAction().getPostCondition()).getSimplifiedFormula();
    } 
    
    /**
     * @inheritDoc
     * @return an <code>AlternativeBehaviour</code>
     */
    @Override
	public Behaviour computeBehaviour() {
        return new AlternativeBehaviour(capabilities,
        								(SemanticBehaviour)getLeftAction().getBehaviour(), 
        								(SemanticBehaviour)getRightAction().getBehaviour());
    } 
    
    /**
     * Returns the leftAction.
     * @return the leftAction.
     */
    public SemanticAction getLeftAction() {
        return leftAction;
    }  
    
    /**
     * Sets the left action.
     * @param leftAction The leftAction to set.
     */
    public void setLeftAction(SemanticAction leftAction) {
        this.leftAction = leftAction;
    } 
    
    /**
     * Returns the rightAction.
     * @return the rightAction.
     */
    public SemanticAction getRightAction() {
        return rightAction;
    } 
    
    /**
     * Sets the right action.
     * @param rightAction The rightAction to set.
     */
    public void setRightAction(SemanticAction rightAction) {
        this.rightAction = rightAction;
    } 
    
} 
