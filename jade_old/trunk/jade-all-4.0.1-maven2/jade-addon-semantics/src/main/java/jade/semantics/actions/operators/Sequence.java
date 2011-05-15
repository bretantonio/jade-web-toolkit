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
 * Sequence.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.operators;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionImpl;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.behaviours.SequenceBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * Defines a prototype for the sequence operator.
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 * @since JSA 1.0
 * 
 * MODIF CA 02/April/2008 : removed unused attribute andPattern
 * and its affectation in constructor
 */
public class Sequence extends SemanticActionImpl {
    /**
     * Left action of the sequence (could be sequence or an alternative)
     */
    private SemanticAction leftAction;
    
    /**
     * Right action of the sequence (could be sequence or an alternative)
     */
    private SemanticAction rightAction;
    
    /**
     * Pattern used to recognise a sequence of formulae
     */
    //private Formula andPattern;
    
    /**
     * Pattern used to recognise a sequence of actions
     */
    private ActionExpression sequencePattern;		
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new Sequence Action prototype.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public Sequence(SemanticCapabilities capabilities) {
        super(capabilities);
        sequencePattern = (ActionExpression) SL.term("(; ??leftPart ??rightPart)");
        //andPattern = SL.formula("(and ??left ??right)");
    } 
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Creates a new instance of this prototype of semantic action from
     * the specified action expression. The action expression must match the
     * pattern (; ??leftPart ??rightPart). It returns an instance of sequence
     * with the left and the right action correctly set.
     * @param actionExpression
     *          an expression of action that specifies the instance to create
     * @return a new instance of sequence, or null if no instance of the 
     * semantic action with the specified action expression can be created
     * @throws SemanticInterpretationException if any exception occurs
     */
    @Override
	public SemanticAction newAction(ActionExpression actionExpression) 
    throws SemanticInterpretationException 
    {
        Sequence result = new Sequence(getSemanticCapabilities());
        try {
            MatchResult matchResult = SL.match(sequencePattern, actionExpression);
            if (matchResult != null) {
                ActionExpression leftPart = (ActionExpression)matchResult.getTerm("leftPart");
                ActionExpression rightPart = (ActionExpression)matchResult.getTerm("rightPart");
                result.setLeftAction(getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(leftPart));
                result.setRightAction(getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(rightPart));
                if (result.getLeftAction() != null && result.getRightAction() != null) {
                    return result;
                }
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    } 
    
    
    /**
     * Returns a <code>Sequence</code> that is a sequence of all the 
     * actions which are in the list.
     * @param list a list of actions
     * @return a <code>Sequence</code> if the size of the list is more than 
     * 1, a <code>SemanticAction</code> if the size equals 1, null if the list 
     * is empty or if an exception occurs. 
     */
    public SemanticAction newAction(ArrayList list) {
        try {
            if (list.size() == 1) {
                return (SemanticAction)list.get(0);
            } else if (list.size() > 0 ) {
                Sequence result = new Sequence(getSemanticCapabilities());
                result.setLeftAction((SemanticAction)list.remove(0));
                result.setRightAction(newAction(list));
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    } 
    
    /* (non-Javadoc)
     * @see jade.semantics.actions.SemanticActionImpl#newAction(jade.semantics.lang.sl.grammar.Formula, jade.lang.acl.ACLMessage)
     */
    @Override
	public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
    	return null;
    }
//    /**
//     * Creates a new instance of this prototype of semantic action from
//     * the specified rational effect. The rational effect must match the pattern
//     * (and ??left ??right).
//     * @param rationalEffect a formula that specifies the rational effet of the
//     *  instance to create
//     * @param inReplyTo an ACL message the message to answer
//     * @return a new instance of sequence, or null if no instance can be 
//     * created
//     */
//    public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
//        Formula leftRationalEffect = null;
//        Formula rightRationalEffect = null;
//        try {
//            MatchResult matchResult = SL.match(andPattern, rationalEffect);
//            if (matchResult != null) {
//                leftRationalEffect = matchResult.getFormula("left");
//                rightRationalEffect = matchResult.getFormula("right");
//                ArrayList actionList = new ArrayList(); 
//                getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(actionList, leftRationalEffect, inReplyTo);
//                getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(actionList, rightRationalEffect, inReplyTo);
//                return newAction(actionList);
//            }       
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    } 
    
    /***
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
    
    /**
     * @inheritDoc
     * @return the feasibility precondition can not be defined in a simple way.
     * So, the feasibility precondition will be determined at runtime. Then the
     * method returns a <code>TrueNode</code>.
     */
    @Override
	public Formula computeFeasibilityPrecondition() 
    //throws WrongTypeException 
    {
        return SL.TRUE;
    } 
    
    /**
     * @inheritDoc
     * @return a the persistent feasibility precondition can not be defined in a
     * simple way. So, the persistent feasibility precondition will be 
     * determined at runtime. Then the method returns a <code>TrueNode</code>.
     */
    @Override
	public Formula computePersistentFeasibilityPreconditon() 
    //throws WrongTypeException 
    {
        return SL.TRUE;
    } 
    
    /**
     * @inheritDoc
     * @return the rational effect can not be defined in a simple way.
     * So, the rational effect will be determined at runtime. Then the
     * method returns a <code>TrueNode</code>.
     */
    @Override
	public Formula computeRationalEffect() 
    //throws WrongTypeException 
    {
        return SL.TRUE;
    } 
    
    /**
     * @inheritDoc
     * @return the postcondition can not be defined in a simple way.
     * So, the postcondition will be determined at runtime. Then the
     * method returns a <code>TrueNode</code>.
     */
    @Override
	public Formula computePostCondition() 
    //throws WrongTypeException 
    {
        return SL.TRUE;
    } 
    
    /**
     * @inheritDoc
     * @return a <code>SequenceBehaviour</code>.
     */
    @Override
	public Behaviour computeBehaviour() {
        return new SequenceBehaviour(capabilities,
        							 (SemanticBehaviour)getLeftAction().getBehaviour(), 
        							 (SemanticBehaviour)getRightAction().getBehaviour());
    } 
    
    /**
     * @inheritDoc
     */
    @Override
	public ActionExpression toActionExpression() throws SemanticInterpretationException {
        ActionExpression result;
        result = (ActionExpression)SL.instantiate(sequencePattern,
        		"leftPart", getLeftAction().toActionExpression(),
        		"rightPart", getRightAction().toActionExpression());
        SL.clearMetaReferences(sequencePattern);
        return result;
    } 
    
} 
