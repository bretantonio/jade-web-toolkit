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
 * created on 7 mars 2007 by Vincent Louis
 */

/**
 * 
 */
package jade.semantics.interpreter.sips.adapters;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.LoopingInstantiationException;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * This Semantic Inperpretation Principle Adapter makes it possible to control
 * the answers to <code>CFP</code> requests by "preparing" the results before
 * generating the actual answer. Such a preparation is needed because answers
 * to a <code>CFP</code> request are generally difficult to store directly in
 * the belief base and generally require some run-time computation. Preparing
 * an answer to a <code>CFP</code> request may also result from interacting
 * with other agents.
 * <br>The preparation of an answer to a <code>CFP</code> request works along
 * the same line as the
 * {@link jade.semantics.interpreter.sips.adapters.QueryRefPreparationSIPAdapter}
 * SIP and must be specified within the abstract
 * {@link #prepareProposal(IdentifyingExpression, ActionExpressionNode, Formula, Term, MatchResult, MatchResult, MatchResult, ArrayList, SemanticRepresentation)}
 * method.</br>
 * <p>
 * Roughly speaking, this SIP adapter is expected to be neutral, that is, it
 * produces the same SR as the SR it consumes (its only role is to update
 * properly the belief base, so that, when further interpreted, the input SR
 * generates an answer with the correct values). More precisely, it consumes
 * (and produces) SRs of the form <code>(I ??myself ??PHI)</code>, where
 * <code>??PHI</code> is a complex formula representing the fact that an agent
 * intends a an answer to a <code>CFP</code> request (see the
 * <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729693">FIPA
 * specifications</a> for details on this formula).
 * </p>
 * <p>
 * Several instances of such a SIP may be added to the SIP table of the agent.
 * </p>
 * <p>
 * <b>The <code>CFP</code> requests, which are not handled by a CFP Preparation SIP
 * Adapter, will likely not be processed</b>. Consequently, a proper SIP must be
 * defined for each <code>CFP</code> request to be dealt with. Alternatively,
 * a generic CFP Preparation SIP can be installed using the
 * {@linkplain #CFPSIPAdapter(SemanticCapabilities) default constructor}
 * of this class. The generic processing relies on a simplifying assumption that
 * consists in querying the belief base for the condition attached to the CFP
 * independently from its requested action.
 * </p>
 * 
 * @author Vincent Louis - France Telecom
 * @since JSA 1.4
 * 
 */
public class CFPSIPAdapter extends QueryRefPreparationSIPAdapter {

	static Formula PROPOSE_FORMULA_PATTERN = SL.formula(
			"(or (not (I ??__agent (done ??__act ??__condition))) (I ??myself (done ??__act ??__condition)))");
	
	Formula equals_all_VARS_PHI_VALUES = SL.formula("(= (all ??__vars " + PROPOSE_FORMULA_PATTERN + ") ??__values)");
		
	private Term actPattern;
	
	private Formula conditionPattern;
	
	private Term agentPattern;
	
//	/**
//	 * @param capabilities
//	 * @param timeout
//	 */
//	public CFPSIPAdapter(SemanticCapabilities capabilities, Date timeout) {
//		super(capabilities, timeout);
//	}
//
//	/**
//	 * @param capabilities
//	 * @param timeout
//	 */
//	public CFPSIPAdapter(SemanticCapabilities capabilities, long timeout) {
//		super(capabilities, timeout);
//	}

	//**************************************************************************
	//**** CONSTRUCTORS
	//**************************************************************************

	/**
	 * Creates a generic CFP Preparation SIP Adapter. The resulting SIP computes
	 * answers to <code>CFP</code> requests by querying the belief base for the
	 * condition associated to the CFP, independently from the action associated
	 * to it.
	 * <p>To better figure it, recall that a <code>CFP</code> message is defined
	 * by two content elements (see the
	 * <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729693">FIPA
	 * specifications</a>):
	 * <ul>
	 *     <li>an action to perform,</li>
	 *     <li>a condition, under which to perform the action.</li>
	 * </ul>
	 * The actual meaning of a <code>CFP</code> request is "for wich values of
	 * the condition would you agree to perform the action". The generic CFP
	 * Preparation SIP Adapter makes a simplifying assumption and interprets
	 * this request as "which values make true the condition", assuming the
	 * semantic agent will agree to perform the action whatever the values of
	 * the condition are.
	 * </p> 
	 *  
	 * @param capabilities {@link SemanticCapabilities} instance of the
	 *                     semantic agent owning this instance of SIP.
	 */
	public CFPSIPAdapter(SemanticCapabilities capabilities) {
		this(capabilities, QueryRefPreparationSIPAdapter.IRE_QUANTIFIER, new MetaTermReferenceNode("__ireVariables"),
				new MetaTermReferenceNode("__act"),
				new MetaFormulaReferenceNode("__condition"),
				new MetaTermReferenceNode("__agent"));
	}
	
	/**
	 * Creates a CFP Preparation SIP Adapter applying to a given pattern of
	 * <code>CFP</code> request and a given pattern of originating agent (that
	 * is, the agent the request comes from).
	 * The first pattern is actually defined by four patterns: one for the
	 * IRE quantifier of the CFP condition, one for the quantified variables
	 * of the CFP condition, one for the quantified formula of the CFP condition
	 * and one for the CFP action.
	 * 
	 * @param capabilities         {@link SemanticCapabilities} instance of the
	 *                             semantic agent owning this instance of SIP.
	 * @param ireQuantifierPattern the pattern of the IRE quantifier of the CFP
	 *                             condition this SIP applies to. It is specified
	 *                             as a disjunction between the {@link #ANY},
	 *                             {@link #IOTA}, {@link #SOME} and {@link #ALL}
	 *                             constants.
	 * @param ireVariablesPattern  the pattern of the IRE quantified variables
	 *                             of the CFP condition this SIP applies to. It
	 *                             is specified as a FIPA-SL term (e.g.
	 *                             <code>??var</code>,
	 *                             <code>(sequence ??v1 ??v2)</code>, etc.). 
	 * @param actPattern           the pattern of the CFP action this SIP applies
	 *                             to. It is specified as a FIPA-SL Action
	 *                             Expression.
	 * @param conditionPattern     the pattern of the IRE quantified formula
	 *                             of the CFP condition this SIP applies to.
	 * @param agentPattern         the pattern of agent originating the
	 *                             <code>CFP</code> request this SIP applies to.
	 */
	public CFPSIPAdapter(SemanticCapabilities capabilities, int ireQuantifierPattern,
			Term ireVariablesPattern, Term actPattern, Formula conditionPattern, Term agentPattern) {
		super(capabilities, ireQuantifierPattern, ireVariablesPattern, PROPOSE_FORMULA_PATTERN, agentPattern);
		this.actPattern = actPattern;
		this.conditionPattern = conditionPattern;
		this.agentPattern = agentPattern;
		equals_all_VARS_PHI_VALUES = equals_all_VARS_PHI_VALUES.instantiate("myself", capabilities.getAgentName());
	}

	//**************************************************************************
	//**** OVERRIDDEN METHODS
	//**************************************************************************

	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.sips.adapters.QueryRefPreparationSIPAdapter#prepareQueryRef(jade.semantics.lang.sl.grammar.IdentifyingExpression, jade.semantics.lang.sl.tools.MatchResult, jade.semantics.lang.sl.tools.MatchResult, jade.semantics.lang.sl.tools.MatchResult, jade.semantics.interpreter.SemanticRepresentation)
	 */
	@Override
	final protected ArrayList prepareQueryRef(IdentifyingExpression ire, MatchResult ireVariablesMatch, MatchResult ireFormulaMatch, MatchResult agentMatch, ArrayList result, SemanticRepresentation sr) {
		Term agent = ireFormulaMatch.term("__agent");
		MatchResult agentInFormulaMatch = agentPattern.match(agent);
		if (agentInFormulaMatch != null && agentInFormulaMatch.equals(agentMatch)) {
			ActionExpression act = (ActionExpression)ireFormulaMatch.term("__act");
			Term myself = myCapabilities.getAgentName();
			MatchResult actMatch = actPattern.match(act);
			if (act instanceof ActionExpressionNode &&
					myself.equals(ireFormulaMatch.term("myself")) &&
					!myself.equals(agent) &&
					myself.equals(act.getActor()) &&
					actMatch != null) {
				Formula condition = ireFormulaMatch.formula("__condition");
				MatchResult conditionMatch = conditionPattern.match(condition);
				if (conditionMatch != null) {
					return prepareProposal(ire, (ActionExpressionNode)act, condition, agent,
							actMatch, conditionMatch, agentMatch, result, sr);
				}
			}
		}
		return null;
	}

	//**************************************************************************
	//**** METHODS TO OVERRIDE
	//**************************************************************************

	/**
	 * This method must be overriden to specify what to do to prepare the answer
	 * to the matched <code>CFP</code> request. Such a preparation usually
	 * consists in updating the agent's belief base with proper values. To do so,
	 * use the provided
	 * {@link #assertProposals(Term, Term, ActionExpressionNode, Formula, ListOfTerm)}
	 * method, which hides the complexity of the actual formula representing
	 * proposal values.
	 * <p>
	 * As for the return value, it works along the same line as the
	 * {@link #prepareQueryRef(IdentifyingExpression, MatchResult, MatchResult, MatchResult, ArrayList, SemanticRepresentation)}
	 * method.
	 * </p>
	 * <p>
	 * The provided default implementation of this method is used by generic
	 * CFP Preparation SIP Adapters built using the
	 * {@linkplain #CFPSIPAdapter(SemanticCapabilities) default constructor}. 
	 * </p>
	 * 
	 * @param ire            complete IRE corresponding to the matched <code>CFP</code>
	 *                       request, that is the complex IRE given in the
	 *                       <a href="http://fipa.org/specs/fipa00037/SC00037J.html#_Toc26729693">
	 *                       FIPA specifications</a>. The enclosed formula being
	 *                       rather complex, it is generally useful only to
	 *                       retrieve the IRE quantifier.
	 * @param act            action associated to the matched <code>CFP</code>
	 *                       request.
	 * @param condition      formula of the condition associated to the matched
	 *                       <code>CFP</code> request.
	 * @param agent          agent originating the <code>CFP</code> request.
	 * @param actMatch       result of the matching of the action of the CFP
	 *                       against the pattern specified in the constructor.
	 * @param conditionMatch result of the matching of the formula of the
	 *                       condition of the CFP against the pattern specified
	 *                       in the constructor.
	 * @param agentMatch     result of the matching of the agent originating the
	 *                       CFP against the pattern specified in the constructor.
	 * @param result         value to return (if the answer to the <code>CFP</code>
	 *                       request can be computed at once) or to interpret
	 *                       later (if the computation of the answer needs
	 *                       further time and/or interactions).
	 * @param sr             incoming Semantic Representation that triggered
	 *                       the application of this SIP Adapter (this SR is
	 *                       therefore consummed if the SIP is eventually
	 *                       applicable).
	 * 
	 * @return               <code>null</code>, if the SIP is not applicable,
	 *                       <br>an empty {@link ArrayList} to delay the preparation,</br>
	 *                       <br>or <code>result</code> in other cases.
	 */
	protected ArrayList prepareProposal(IdentifyingExpression ire,
											ActionExpressionNode act, Formula condition, Term agent,
											MatchResult actMatch, MatchResult conditionMatch, MatchResult agentMatch,
											ArrayList result, SemanticRepresentation sr) {
		IdentifyingExpression simplifiedIre = ire;
		simplifiedIre.as_formula(condition);
		assertProposals(simplifiedIre.as_term(), agent, act, condition, myCapabilities.getMyKBase().queryRef(simplifiedIre));
		return result;
	}
	
	//**************************************************************************
	//**** PUBLIC METHODS
	//**************************************************************************

	/**
	 * This method must be used within the
	 * {@link #prepareProposal(IdentifyingExpression, ActionExpressionNode, Formula, Term, MatchResult, MatchResult, MatchResult, ArrayList, SemanticRepresentation)}
	 * method to assert a set of values (possibly empty, meaning there is no
	 * proposal available) satisfying a given proposal. Before actually asserting
	 * the specified values, it deletes all previouly asserted ones. The asserted
	 * values will be used by further SIPs in the JSA framework to generate
	 * the proper answer to the <code>CFP</code> request being processed.
	 *  
	 * @param variables FIPA-SL term representing the quantified variables of 
	 *                  the condition associated to the CFP (e.g. <code>?x</code>,
	 *                  <code>(sequence ?x1 ?x2)</code>, etc.).
	 * @param agent     FIPA-SL term representing the AID of the agent originating
	 *                  the CFP.
	 * @param act       FIPA-SL action expression representing the action associated
	 *                  to the CFP
	 * @param condition FIPA-SL formula representing the formula of the condition
	 *                  associated to the CFP
	 * @param values    list of terms representing each a value for the specified
	 *                  proposal.
	 */
	protected void assertProposals(Term variables, Term agent, ActionExpressionNode act, Formula condition, ListOfTerm values) {
		Formula assertion = (Formula)equals_all_VARS_PHI_VALUES.getClone();
		try {
			SL.set(assertion, "__vars", variables);
			SL.set(assertion, "__agent", agent);
			SL.set(assertion, "__act", act);
			SL.set(assertion, "__condition", condition);
			SL.set(assertion, "__values", new TermSetNode(values));
			SL.substituteMetaReferences(assertion);
		} catch (WrongTypeException e) {
			e.printStackTrace();
			return;
		} catch (LoopingInstantiationException e) {
			e.printStackTrace();
			return;
		}
		potentiallyAssertFormula(assertion);
	}
}

