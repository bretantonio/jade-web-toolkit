/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2006 France Télécom

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
 * created on 21 mars 2007 by Vincent Louis
 */

/**
 * 
 */
package jade.semantics.actions;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.operators.Alternative;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * This class implements the prototype of the <code>DO</code> primitive action
 * (not part of the FIPA specifications), which brings about a given goal.
 * <p>
 * Actually, the performance of this action relies on the same algorithm as the
 * one used by the {@link jade.semantics.interpreter.sips.RationalityPrinciple}
 * SIP. That is, it consists in searching the agent's semantic action table for
 * an action whose effect exactly matches the given goal.
 * Hence, performing the <code>DO</code> action is roughly equivalent to assert
 * the intention of the given goal.
 * </p>
 * <p>
 * This class is not intended to be used directly by developers.
 * </p>
 * <p>
 * Rather, this prototype is loaded in the default semantic action table of
 * semantic agents, so that the <code>DO</code> action can be used in FIPA-SL
 * Action Expressions with the following pattern:
 * <code>(action ??actor (DO ??goal))</code>, where <code>??goal</code> must be
 * a string constant representing the goal expressed in FIPA-SL. For example,
 * <code>(action ??myself (DO "(B peter (age mary 25))"))</code> represents
 * the action of performing some course of action, so that peter comes to believe
 * that mary is 25 years old (which will likely result in performing an
 * <code>INFORM</code>, <code>DISCONFIRM</code> or <code>CONFIRM</code> act,
 * depending on the agent's beliefs on peter's beliefs).
 * </p>
 * 
 * @author Vincent Louis - France Telecom
 * @since JSA 1.4
 */
public class Do extends SemanticActionImpl implements Cloneable {
	
	Formula goal;
	
	static Term DO_ACTION_PATTERN = SL.term("(action ??" + ACTOR + " (DO ??__phi))");

	/**
	 * Create a prototype for the <code>DO</code> primitive action.
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
	 *                     agent holding this prototype in his semantic action
	 *                     table.
	 */
	public Do(SemanticCapabilities capabilities) {
		super(capabilities);
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#newAction(jade.semantics.lang.sl.grammar.ActionExpression)
	 */
	@Override
	public SemanticAction newAction(ActionExpression actionExpression)
			throws SemanticInterpretationException {
		MatchResult matchResult = DO_ACTION_PATTERN.match(actionExpression);
		if (matchResult != null) {
			Do result;
			try {
				result = (Do)clone();
				result.setAuthor(matchResult.getTerm(ACTOR));
				result.goal = SL.formula(((Constant)matchResult.getTerm("__phi")).stringValue());
		    } catch (Exception e) { // WrongTypeException or CloneNotSupported
		        throw new SemanticInterpretationException("cannot-read-author", SL.string(actionExpression.toString()));
		    }
		    return result;
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

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#toActionExpression()
	 */
	@Override
	public ActionExpression toActionExpression()
			throws SemanticInterpretationException {
		ActionExpression result = (ActionExpression)DO_ACTION_PATTERN.instantiate(ACTOR, getAuthor());
		result.sm_action(this);
		return result;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computeFeasibilityPrecondition()
	 */
	@Override
	public Formula computeFeasibilityPrecondition() throws WrongTypeException {
		return SL.TRUE;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computePersistentFeasibilityPreconditon()
	 */
	@Override
	public Formula computePersistentFeasibilityPreconditon()
			throws WrongTypeException {
		return SL.TRUE;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computeRationalEffect()
	 */
	@Override
	public Formula computeRationalEffect() throws WrongTypeException {
		return SL.TRUE;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computePostCondition()
	 */
	@Override
	public Formula computePostCondition() throws WrongTypeException {
		return goal;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computeBehaviour()
	 */
	@Override
	public Behaviour computeBehaviour() {
		ArrayList actions = new ArrayList();
		getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstances(actions, goal, null);
		return new Alternative(getSemanticCapabilities()).newAction(actions).getBehaviour();		
	}

}

