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


package jade.semantics.actions;

/*
 * Interpret.java
 * Created on 5 November 2007
 * Author : Carole Adam
 */

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.behaviours.SemanticBehaviourBase;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * This class implements the prototype of the <code>INTERPRET</code> 
 * primitive action (not part of the FIPA specifications), which 
 * interprets a given formula, and then always succeeds.
 * <p>
 * @author Carole Adam - France Telecom
 * @since JSA 1.5
 * @version 1.2 modified 27/02/08 to use FactNode
 */
public class Interpret extends SemanticActionImpl implements Cloneable {

	Formula formula;
	
	static Term INTERPRET_ACTION_PATTERN = SL.term("(action ??" + ACTOR + " (INTERPRET (fact ??__phi)))");

	/**
	 * Create a prototype for the <code>TEST</code> primitive action.
	 * 
	 * @param capabilities {@link SemanticCapabilities} instance of the semantic
	 *                     agent holding this prototype in his semantic action
	 *                     table.
	 */
	public Interpret(SemanticCapabilities capabilities) {
		super(capabilities);
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#newAction(jade.semantics.lang.sl.grammar.ActionExpression)
	 */
	@Override
	public SemanticAction newAction(ActionExpression actionExpression)
			throws SemanticInterpretationException {
		MatchResult matchResult = INTERPRET_ACTION_PATTERN.match(actionExpression);
		if (matchResult != null) {
			Interpret result;
			try {
				result = (Interpret)clone();
				result.setAuthor(matchResult.getTerm(ACTOR));
				result.formula = //SL.formula(((Constant)matchResult.getTerm("__phi")).stringValue());
					matchResult.formula("__phi");
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
		ActionExpression result = (ActionExpression)INTERPRET_ACTION_PATTERN.instantiate(ACTOR, getAuthor());
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
		return SL.TRUE;
	}

	/* (non-Javadoc)
	 * @see jade.semantics.actions.SemanticActionImpl#computeBehaviour()
	 */
	@Override
	public Behaviour computeBehaviour() {
		return new SemanticBehaviourBase(capabilities) {
			@Override
			public void action() {
				System.out.println("behaviour de interpret("+formula+")");
				capabilities.interpret(formula);
				setState(SUCCESS);
			}
		};//end behaviour
	}//end computeBehaviour
	
}
