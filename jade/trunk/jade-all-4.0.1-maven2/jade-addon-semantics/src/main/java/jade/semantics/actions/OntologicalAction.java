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
 * OntologicalAction.java
 * Created on 15 déc. 2004
 * Author : louisvi
 */
package jade.semantics.actions;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfNodes;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * Defines a prototype for ontological actions.
 * 
 * @author Vincent Louis - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 * @since JSA 1.0
 * 
 * Modification in instantiateFeatures to instantiate 
 * also MetaFormulaReferenceNodes 
 * @author Carole Adam - France Telecom
 * @version Revision 1.1 Date: 2008/01/24
 * @since JSA 1.5
 */
public class OntologicalAction extends SemanticActionImpl implements Cloneable {

	/**
	 * Pattern to recognise an action expression corresponding to this 
     * ontological action
	 */
    private ActionExpression actionPattern;

	/**
	 * Pattern to recognise and build the rational effect and the postcondition
     * of this ontological action
     * CA: made it protected to be accessed in subclass InstitutionalAction, 12Dec07
	 */
    protected Formula postconditionPattern;

	/**
	 * Pattern to build the feasibility precondition of this ontological action
	 */
    private Formula preconditionPattern;

	/**
	 * Table that stores the values of the parameters of an instance of this
	 * ontological action
	 */
    protected MatchResult actionParameters;

	/**
	 * Creates a new Ontological Action prototype defined by an action pattern, a
	 * postcondition pattern and a precondition pattern. All the metaVariables
	 * of these patterns must refer to SL terms representing one of the
	 * arguments of the action and must use the same names for these
	 * metaVariables. These patterns may refer to the reserved metaReference "<code>??actor</code>",
	 * which denotes the agent of the action. A call to one of the
	 * <code>newAction</code> methods creates instances of this Ontological
	 * Action prototype such that :
	 * <ul>
	 * <li> the <code>getFeasibilityPrecondition</code> and
	 * <code>getPostCondition</code> methods return formulae that comply with
	 * the corresponding patterns given to this constructor,</li>
	 * <li> the <code>getRationalEffect</code> method returns the same as the
	 * <code>
	 *           getPostCondition</code> method,</li>
	 * <li> the <code>getPersistentFeasibilityPrecondition</code> method
	 * returns the true formula,</li>
	 * <li> the <code>computeBehaviour</code> method returns a
	 * <code>OntoActionBehaviour</code> that automatically manages the
	 * specified preconditions and postconditions for this OntologicalAction.</li>
	 * </ul>
	 * 
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
	 * @param actionPattern
	 *            pattern used to recognise the SL functional term representing
	 *            this action. Given as a jade.semantics.lang.sl.grammar.Term
	 * @param postconditionPattern
	 *            pattern used to both recognise SL formulae representing the
	 *            rational effect of this action and instantiate the SL formula
	 *            representing the postcondition of this action.
	 * @param preconditionPattern
	 *            pattern used to instantiate the SL formula representing the
	 *            precondition of this action.
	 */
	public OntologicalAction(SemanticCapabilities capabilities,
							 Term actionPattern, 
			 				 Formula postconditionPattern,
			 				 Formula preconditionPattern) {
		super(capabilities);
		this.actionPattern = new ActionExpressionNode(new MetaTermReferenceNode(ACTOR), actionPattern.getSimplifiedTerm());
		this.postconditionPattern = postconditionPattern;
		this.preconditionPattern = preconditionPattern;
	} 
	
	/**
	 * The same as the previous constructor, except that the action
	 * pattern is given as an SL String instead of a Term 
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
	 * @param actionPattern
	 * 			  pattern used to recognise the SL functional term representing
	 *            this action. Given as an SL String, e.g.,
	 *            "<code>(CLOSE :what ??what)</code>"
	 * @param postconditionPattern
	 *            pattern used to both recognise SL formulae representing the
	 *            rational effect of this action and instantiate the SL formula
	 *            representing the postcondition of this action.
	 * @param preconditionPattern
	 * 			  pattern used to instantiate the SL formula representing the
	 *            precondition of this action.
	 */
	public OntologicalAction(SemanticCapabilities capabilities,
				             String actionPattern, 
			 				 Formula postconditionPattern,
			 				 Formula preconditionPattern) {
		this(capabilities, SL.term(actionPattern), postconditionPattern, preconditionPattern);
	} 

    /**
     * Creates a new instance of this prototype of semantic action from
     * the specified action expression. The action expression must match the
     * pattern (action ??actor " + actionPattern + ") where actionPattern is
     * given in the constructor (second parameter). 
     * 
     * @param actionExpression
     *          an expression of action that specifies the instance to create
     * @return a new instance of the semantic action, the action expression of
     * which is specified, or null if no instance of the semantic action with
     * the specified action expression can be created.
     * @throws SemanticInterpretationException if any exception occurs
     */
	@Override
	public SemanticAction newAction(ActionExpression actionExpression) throws SemanticInterpretationException {
		return newAction(actionPattern, actionExpression, false);
	} 

    /**
     * Creates a new instance of this prototype of semantic action from
     * the specified rational effect. The rational effect must match the 
     * postcondition pattern given in the constructor (third parameter).
     * @param rationalEffect
     *              a formula that specifies the rational effect of the instance 
     *              to create
     * @param inReplyTo an ACL message the message to answer
     * @return a new instance of the semantic action, the rational effect of
     * which is specified, or null if no instance of the semantic action with
     * the specified rational effect can be created
     */
	@Override
	public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
		try {
            return newAction(postconditionPattern, rationalEffect, true);
        }
        catch (SemanticInterpretationException e) {
            return null;
        }
	} 

	/**
	 * Internal implementation for building a new instance of this Ontological
	 * Action prototype
	 * 
	 * @param pattern
	 *            pattern to match
	 * @param node
	 *            instantiated formula or term that identifies the parameters of
	 *            the instance of action to create (when matching the pattern)
     * @param isAuthorToBeSet if true the author of the action is the one which 
     * appears in the actor_reference metavariable, and if false the author is 
     * the current agent.
	 * @return the new instance of the action prototype that has been created
     * @throws SemanticInterpretationException if any exception occurs
	 */
	private SemanticAction newAction(Node pattern, Node node, boolean isAuthorToBeSet) throws SemanticInterpretationException {
		MatchResult matchResult = SL.match(pattern, node);
		if (matchResult != null) {
		    try {
		        OntologicalAction result = (OntologicalAction)clone();
		        if (isAuthorToBeSet) {
		            result.setAuthor(getSemanticCapabilities().getAgentName());
		            if (matchResult.getTerm(ACTOR) != null
		                    && !matchResult.getTerm(ACTOR).equals(result.getAuthor())) {
		                throw new SemanticInterpretationException("inconsistent-author", SL.word(""));
		            }
		        }
		        else {
		            result.setAuthor(matchResult.getTerm(ACTOR));
		        }
		        result.actionParameters = matchResult;
		        return result;
		    } catch (Exception e) { // WrongTypeException or CloneNotSupported
		        throw new SemanticInterpretationException("cannot-read-author", SL.string(node.toString()));
		    }
		}
		return null;
	} // End of newAction/3

	/**
     * @inheritDoc
	 */
    @Override
	public ActionExpression toActionExpression() throws SemanticInterpretationException {
	    try {
            return (ActionExpression)instantiateFeatures(actionPattern);
        }
        catch (WrongTypeException e) {
            throw new SemanticInterpretationException("cannot-expand-action", SL.string("missing or bad parameter"));
        }
    }
    
    /**
     * @inheritDoc
     * @return a <code>OntoActionBehaviour</code>.
     */
    @Override
	public Behaviour computeBehaviour() {
        return new OntoActionBehaviour(this);
    }
    /**
     * @inheritDoc
     */
    @Override
	public Formula computeFeasibilityPrecondition() throws WrongTypeException {
        return (Formula)instantiateFeatures(preconditionPattern);
    }
    
    /**
     * @inheritDoc
     * @return returns a <code>TrueNode</code>
     */
    @Override
	public Formula computePersistentFeasibilityPreconditon() throws WrongTypeException {
        return SL.TRUE;
    }
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computeRationalEffect() throws WrongTypeException  {
        return SL.TRUE;
    }
    
    /**
     * @inheritDoc
     */
    @Override
	public Formula computePostCondition() throws WrongTypeException {
        return (Formula)instantiateFeatures(postconditionPattern);
    }

	/**
	 * Internal method that helps instantiated some semantic features of an
	 * instance of this action being created from the values of the parameters
	 * of the action (which should have been set)
	 * 
	 * @param pattern
	 *            pattern to instantiate
	 * @return instantiated pattern with the values of the parameters of the
	 *         action
	 * @throws WrongTypeException 
	 */
	private Node instantiateFeatures(Node pattern) throws WrongTypeException {
		ListOfNodes metaTermReferences = new ListOfNodes();
		ListOfNodes metaFormulaReferences = new ListOfNodes();
		if (pattern.childrenOfKind(MetaTermReferenceNode.class, metaTermReferences)) {
			for (int i = 0; i < metaTermReferences.size(); i++) {
				String parameterName = ((MetaTermReferenceNode)metaTermReferences.get(i)).lx_name();
				SL.set(pattern, parameterName,
				        (parameterName.equals(ACTOR) ? getAuthor() : actionParameters.getTerm(parameterName)));
			}
		}
		// added by CA, 24/1/08 to manage (fact ??formula) patterns FIXME
		if (pattern.childrenOfKind(MetaFormulaReferenceNode.class, metaFormulaReferences)) {
			//System.err.println("TODO!!! add instantiation of formula features !");
			for (int i = 0; i < metaFormulaReferences.size(); i++) {
				String parameterName = ((MetaFormulaReferenceNode)metaFormulaReferences.get(i)).lx_name();
				SL.set(pattern, parameterName,
				        actionParameters.getFormula(parameterName));
			}			
		}
        Node result = SL.instantiate(pattern);
        SL.clearMetaReferences(pattern);
//        System.err.println("instantiated result = "+result);
        return result;
	} // End of instantiateFeatures/1

	/**
	 * Implementation of the behaviour of the ontological action. This method must
	 * be developed along the same way as the <code>action</code> method of
	 * the <code>Behaviour</code>. This method must be overridden in all the
	 * subclasses (by default, does nothing but setting the internal state to
	 * the <code>SUCCESS</code> constant).
	 * 
	 * @param behaviour
	 *            Nesting SemanticBehaviour (useful for setting the internal
	 *            state of the SemanticBehaviour with the
	 *            <code>setState(int)</code> method).
	 * @see jade.core.behaviours.Behaviour#action()
	 * @see SemanticBehaviourBase#action()
	 */
	public void perform(OntoActionBehaviour behaviour) {
		behaviour.setState(SemanticBehaviour.SUCCESS);
	} // End of perform/1

	/**
     * Returns a Term representing a parameter from the given parameter name.
     * @param parameterName a name of parameter
	 * @return an action parameter
	 */
	public Term getActionParameter(String parameterName) {
		try {
            return actionParameters.getTerm(parameterName);
        }
        catch (WrongTypeException e) {
            return null;
        }
	} // End of getActionParameter/1
	
	/**
	 * Returns the list of action parameters (according to the pattern of functionnal
	 * term associated to this OntologicalAction).
	 * 
	 * @return the list of the action parameters
	 */
	public MatchResult getActionParameters() {
		return actionParameters;
	}
    
	/**
	 * added by Carole Adam, December 12, 2007
	 * 
	 * @return the instantiated pattern of this action
	 */
	public ActionExpression getActionPattern() {
		return (ActionExpression)actionPattern.instantiate(ACTOR,getActor());
	}
	
} // End of OntologicalAction
