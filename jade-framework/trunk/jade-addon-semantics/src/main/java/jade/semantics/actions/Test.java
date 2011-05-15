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
 * Test.java
 * Created on 5 November 2007
 * Author : Carole Adam
 */

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.behaviours.SemanticBehaviourBase;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * This class implements the prototype of the <code>TEST</code> primitive 
 * action (not part of the FIPA specifications), which tests a given formula 
 * (given as a string), succeeds if true and fails otherwise.
 * <p>
 * @author Carole Adam - France Telecom
 * @since JSA 1.5
 */
public class Test extends SemanticActionImpl implements Cloneable {
		
		Formula condition;
		
		static Term TEST_ACTION_PATTERN = SL.term("(action ??" + ACTOR + " (TEST (fact ??__phi)))");

		/**
		 * Create a prototype for the <code>TEST</code> primitive action.
		 * 
		 * @param capabilities {@link SemanticCapabilities} instance of the semantic
		 *                     agent holding this prototype in his semantic action
		 *                     table.
		 */
		public Test(SemanticCapabilities capabilities) {
			super(capabilities);
		}

		/* (non-Javadoc)
		 * @see jade.semantics.actions.SemanticActionImpl#newAction(jade.semantics.lang.sl.grammar.ActionExpression)
		 */
		@Override
		public SemanticAction newAction(ActionExpression actionExpression)
				throws SemanticInterpretationException {
			MatchResult matchResult = TEST_ACTION_PATTERN.match(actionExpression);
			if (matchResult != null) {
				Test result;
				try {
					result = (Test)clone();
					result.setAuthor(matchResult.getTerm(ACTOR));
					//result.condition = SL.formula(((Constant)matchResult.getTerm("__phi")).stringValue());
					result.condition = matchResult.getFormula("__phi");
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
			ActionExpression result = (ActionExpression)TEST_ACTION_PATTERN.instantiate(ACTOR, getAuthor());
			result.sm_action(this);
			return result;
		}

		/* (non-Javadoc)
		 * @see jade.semantics.actions.SemanticActionImpl#computeFeasibilityPrecondition()
		 */
		@Override
		public Formula computeFeasibilityPrecondition() throws WrongTypeException {
			return condition;
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
				
				// The empty action does nothing!
				@Override
				public void action() {
					QueryResult feasibPrecondQR = getMySemanticCapabilities().getMyKBase().query(getFeasibilityPrecondition());
//					System.err.println("test action, feasib precond = "+getFeasibilityPrecondition());
//					System.err.println("test action, cdt="+condition);
//					System.err.println("test action, qr="+feasibPrecondQR);
//					System.err.println("!!!!!!! myKBase in TEST = !!!!!!! \n "+getMySemanticCapabilities().getMyKBase().toStrings());
					//condition = new BelieveNode(getActor(),condition);
					if ((feasibPrecondQR == null) || (feasibPrecondQR == QueryResult.UNKNOWN)) {
						setState(FEASIBILITY_FAILURE);
						System.err.println("FAILURE du behaviour de l'action test("+condition+")");
					}
					else {
						setState(SUCCESS);
						System.err.println("SUCCESS du behaviour de l'action test("+condition+")");
					}
				}
			};//end behaviour
		}//end computeBehaviour
		
		/**
		 * method to create a conditional plan.
		 * This is necessary because AlternativeActionExpressionNode does not perform the
		 * two alternative branches in the expected order, but rather in alphabetical order.
		 * This method creates mutually exclusive branches so that only the expected one
		 * is performed when performing the plan.
		 * @param condition the condition to be tested
		 * @param planThen the plan that is performed only if the condition is tested true
		 * @param planElse the plan that is performed only if the condition is tested false
		 * @return a conditional plan consisting in two branches, one tests if the condition is
		 * true and executes planThen, the other tests if the condition is false and executes planElse.
		 */
		// NEW VERSION WITH FACTNODE
		public static ActionExpression conditionalPlan(Term agent, String condition,ActionExpression planThen, ActionExpression planElse) {
			ActionExpression testCond = new ActionExpressionNode(agent,
					SL.term("(TEST (fact "+condition+"))"));
			ActionExpression testNotCond = new ActionExpressionNode(agent,
					SL.term("(TEST (fact (not "+condition+")))"));

			ActionExpression result = new AlternativeActionExpressionNode(
					new SequenceActionExpressionNode(testCond,planThen),
					new SequenceActionExpressionNode(testNotCond,planElse));
			return result;
		}

		
		public static ActionExpression conditionalPlan(Term agent, Formula condition,ActionExpression planThen, ActionExpression planElse) {
			ActionExpression testCond = new ActionExpressionNode(agent,
					SL.term("(TEST (fact "+condition+"))"));
			ActionExpression testNotCond = new ActionExpressionNode(agent,
					SL.term("(TEST (fact (not "+condition+")))"));

			ActionExpression result = new AlternativeActionExpressionNode(
					new SequenceActionExpressionNode(testCond,planThen),
					new SequenceActionExpressionNode(testNotCond,planElse));
			return result;
		}
		
		// OLD VERSION
//		public static ActionExpression conditionalPlan(Term agent, String condition,ActionExpression planThen, ActionExpression planElse) {
//			ActionExpression testCond = new ActionExpressionNode(agent,
//					SL.term("(TEST \""+condition+"\")"));
//			ActionExpression testNotCond = new ActionExpressionNode(agent,
//					SL.term("(TEST \"(not "+condition+")\")"));
//
//			ActionExpression result = new AlternativeActionExpressionNode(
//					new SequenceActionExpressionNode(testCond,planThen),
//					new SequenceActionExpressionNode(testNotCond,planElse));
//			return result;
//		}

		
}
