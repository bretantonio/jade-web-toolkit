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
 * SemanticActionImpl.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;


/**
 * This class is an implementation of the <code>SemanticAction</code> interface.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/10/28 Revision: 1.0 
 */
public abstract class SemanticActionImpl implements SemanticAction {
    
    /**
     * The actor of the action (this is the sender when considering
     * a communicative action)
     */
    private Term actor;
    
    /**
     * Precondition of feasibility
     */
    private Formula feasibilityPrecondition;
    
    /**
     * Persistent precondition of feasibility
     */
    private Formula persistentFeasibilityPrecondition;
    
    /**
     * Rational effect
     */
    private Formula rationalEffect;
    
    /**
     * Post-condition of the action
     */
    private Formula postCondition;
    
    /**
     * The behaviour of the action
     */
    private Behaviour behaviour;
    
    /**
     * The semantic capabilities of the agent holding the action
     */
    protected SemanticCapabilities capabilities;
    
    /********************************************************************/
    /** 			CONSTRUCTOR
     */
    /********************************************************************/
    
    /**
     * Constructor for a semantic action prototype
     * @param table the semantic action table that contains this semantic action
     */
    public SemanticActionImpl(SemanticCapabilities capabilities) {
        this.capabilities = capabilities;
        actor = null;
        feasibilityPrecondition = null;
        persistentFeasibilityPrecondition = null;
        rationalEffect = null;
        postCondition = null;
    } // End of SemanticActionImpl/1
    
    /********************************************************************/
    /** 			PUBLIC METHODS
     */
    /********************************************************************/
    
	/**
     * Returns the semantic capabilities of agent holding this action
     * @return Returns the semantic capabilities of agent holding this action
     */
    public SemanticCapabilities getSemanticCapabilities() {
        return capabilities;
    } 
        
	/**
     * {@inheritDoc}
     */
    public Term getAuthor() {
        return getActor();
    } 

    /**
     * {@inheritDoc}
     */
    public Term getActor() {
        return actor;
    } 
   
    /**
     * Sets the actor of the action
     * @param actor The actor to set.
     */
    public void setAuthor(Term actor) {
        this.actor = actor;
    }
    
    /**
     * Returns the feasibility precondition. 
     * @return Returns the feasibility precondition.
     **/
    public Formula getFeasibilityPrecondition() {
        if (feasibilityPrecondition == null) {
            try {
                setFeasibilityPrecondition(computeFeasibilityPrecondition());
            } catch (WrongTypeException wte) {
                wte.printStackTrace();
            }
        }
        return feasibilityPrecondition;
    } // End of getFeasibilityPrecondition/0
    
    /**
     * Sets the feasibility precondition of the action
     * @param formula the feasibility precondition to set
     **/
    public void setFeasibilityPrecondition (Formula formula) {
        feasibilityPrecondition = formula.getSimplifiedFormula();
    } // End of setFeasibilityPrecondition/1
    
    /**
     * Returns the persistentFeasibilityPrecondition.
     * @return Returns the persistentFeasibilityPrecondition.
     */
    public Formula getPersistentFeasibilityPrecondition() {
        if (persistentFeasibilityPrecondition == null) {
            try {
                setPersistentFeasibilityPrecondition(computePersistentFeasibilityPreconditon());
            } catch (WrongTypeException wte) {
                wte.printStackTrace();
            }
        }
        return persistentFeasibilityPrecondition;
    } // End of getPersitentFeasibilityPrecondition/0
    
    /**
     * Sets the persistent feasibility precondition
     * @param formula The persistentFeasibilityPrecondition to set.
     */
    public void setPersistentFeasibilityPrecondition(Formula formula) {
        persistentFeasibilityPrecondition = formula.getSimplifiedFormula();
    } // End of setPersitentFeasibilityPrecondition/1
    
    /**
     * Returns the rational effect of the action.
     * @return the rational effect of the action
     **/
    public Formula getRationalEffect() {
        if (rationalEffect == null) {
            try {
                setRationalEffect(computeRationalEffect());
            }
            catch (WrongTypeException wte) {
                wte.printStackTrace();
                // or return True/FalseNode???
            }
        }
        return rationalEffect;
    } // End of getRationalEffect/0
    
    /**
     * Sets the rational effect of the action
     * @param formula the rational effect to set
     **/
    public void setRationalEffect (Formula formula) {
        rationalEffect = formula.getSimplifiedFormula();
    } // End of setRationalEffect/1
    
    /**
     * Returns the postCondition.
     * @return the postCondition.
     */
    public Formula getPostCondition() {
        if (postCondition == null) {
            try {
                setPostCondition(computePostCondition());
            } catch (WrongTypeException wte) {
                wte.printStackTrace();
            }
        }
        return postCondition;
    } // End of getPostCondition/0
    
    /**
     * Sets the postcondition of this action.
     * @param formula The postCondition to set.
     */
    public void setPostCondition(Formula formula) {
        postCondition = formula.getSimplifiedFormula();
    } // End of setPostCondition/1
    
    /**
     * Returns the behaviour of the action.
     * @return the behaviour of the action
     **/
    public Behaviour getBehaviour() {
        if (behaviour == null) {
            setBehaviour(computeBehaviour());
        }
        return behaviour;
    } 
    
    /**
     * Sets the behaviour associated to this action.
     * @param behaviour The behaviour to set.
     */
    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    } 
    
    /**************************************************************************/
    /**								ABSTRACT METHODS						 **/
    /**************************************************************************/
    
    /**
     * Creates a new instance of this prototype of semantic action from
     * the specified action expression.
     * 
     * @param actionExpression
     * 			an expression of action that specifies the instance to create
     * @return a new instance of the semantic action, the action expression of
     * which is specified, or null if no instance of the semantic action with
     * the specified action expression can be created
     * @throws SemanticInterpretationException if any exception occurs
     */
    public abstract SemanticAction newAction(ActionExpression actionExpression) throws SemanticInterpretationException;
    
    /**
     * Creates a new instance of this prototype of semantic action from
     * the specified rational effect.
     * Should be overridden when using the rational effect of the action
     * (returns null by default).
     * 
     * @param rationalEffect
     *              a formula that specifies the rational effect of the instance to create
     * @param inReplyTo an ACL message the message to answer
     * @return a new instance of the semantic action, the rational effect of
     * which is specified, or null if no instance of the semantic action with
     * the specified rational effect can be created
     */
    public abstract SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo);
    
    /**
     * @inheritDoc
     */
    public abstract ActionExpression toActionExpression() throws SemanticInterpretationException;
    
    
    /**
     * Computes the feasibility precondition of the action.
     * @return the formula corresponding to the feasibility precondition
     * @throws WrongTypeException if any exception occurs
     */
    public abstract Formula computeFeasibilityPrecondition() throws WrongTypeException;
    
    /**
     * Computes the persistent feasibility precondition of the action.
     * @return the formula corresponding to the persistent feasibility precondition
     * @throws WrongTypeException if any exception occurs
     */
    public abstract Formula computePersistentFeasibilityPreconditon() throws WrongTypeException;
    
    /**
     * Computes the rational effect of the action.
     * @return the rational effect
     * @throws WrongTypeException if any exception occurs
     */
    public abstract Formula computeRationalEffect() throws WrongTypeException;
    
    /**
     * Computes the postcondition of the action.
     * @return the postcondition
     * @throws WrongTypeException if any exception occurs
     */
    public abstract Formula computePostCondition() throws WrongTypeException;
    
    /**
     * Computes the semantic behaviour for the action.
     * @return the behaviour of this action
     */
    public abstract Behaviour computeBehaviour();

} 
