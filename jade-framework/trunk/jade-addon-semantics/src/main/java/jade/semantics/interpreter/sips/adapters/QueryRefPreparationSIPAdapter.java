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
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.AllNode;
import jade.semantics.lang.sl.grammar.AnyNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.IotaNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.MetaVariableReferenceNode;
import jade.semantics.lang.sl.grammar.SomeNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.util.Date;

/**
 * This Semantic Inperpretation Principle Adapter makes it possible to finely
 * control the answers to <code>QUERY-REF</code> requests by "preparing" the
 * results before generating the actual answer. Such a preparation is useful
 * when the answer have to be computed at run-time or needs some delay
 * (typically to interact with other agents) for computation. In the second
 * case, this SIP should be absorbent (that is, consumes the input SR but
 * produces no output SR) and installs a proper behaviour to interpret later
 * the regular output SR.
 * <br>The preparation of an answer to a <code>QUERY-REF</code> request must be
 * specified within the abstract
 * {@link #prepareQueryRef(IdentifyingExpression, MatchResult, MatchResult, MatchResult, ArrayList, SemanticRepresentation)}
 * method.</br>
 * <p>
 * Roughly speaking, this SIP adapter is expected to be neutral, that is, it
 * produces the same SR as the SR it consumes (its only role is to update
 * properly the belief base, so that, when further interpreted, the input SR
 * generates an answer with the correct values). More precisely, it consumes
 * (and produces) SRs of the form <code>(I ??myself ??PHI)</code>, where
 * <code>??PHI</code> is of one of the following forms:
 * <ul>
 *     <li><code>(done &lt;<i>INFORM-REF instance</i>&gt;)</code>,</li>
 *     <li><code>(exists ??var (B ??agent (= &lt;<i>IRE instance</i>&gt; ??var)))</code>.</li>
 * </ul>
 * </p>
 * <p>
 * Several instances of such a SIP may be added to the SIP table of the agent.
 * </p>
 * <p>
 * The <code>QUERY-REF</code> requests, which are not handled by a Query-Ref
 * Preparation SIP Adapter, will be processed according to the current content
 * of the belief base. This is actually the normal case, the use of such a SIP
 * adpater is advocated only when the results cannot be directly stored in the
 * belief base or need some interactions with other agents to be computed.
 * </p>
 * 
 * @author Vincent Louis - France Telecom
 * @since JSA 1.4
 */
public abstract class QueryRefPreparationSIPAdapter extends ApplicationSpecificSIPAdapter {
	
	/**
	 * Constant representing the pattern of the ANY IRE quantifier. It is used
	 * in the constructor to specify the pattern of <code>QUERY-REF</code> request
	 * this SIP applies to, and can be combined with other constants with the
	 * bitwise inclusive or operator (|).
	 */
	static public int ANY = 1;

	/**
	 * Constant representing the pattern of the IOTA IRE quantifier. It is used
	 * in the constructor to specify the pattern of <code>QUERY-REF</code> request
	 * this SIP applies to, and can be combined with other constants with the
	 * bitwise inclusive or operator (|).
	 */
	static public int IOTA = 1 <<2;
	
	/**
	 * Constant representing the pattern of the SOME IRE quantifier. It is used
	 * in the constructor to specify the pattern of <code>QUERY-REF</code> request
	 * this SIP applies to, and can be combined with other constants with the
	 * bitwise inclusive or operator (|).
	 */
	static public int SOME = 1 <<3;
	
	/**
	 * Constant representing the pattern of the ALL IRE quantifier. It is used
	 * in the constructor to specify the pattern of <code>QUERY-REF</code> request
	 * this SIP applies to, and can be combined with other constants with the
	 * bitwise inclusive or operator (|).
	 */
	static public int ALL = 1 <<4;
	
	/**
	 * Constant representing the pattern of any IRE quantifier. It is used
	 * in the constructor to specify the pattern of <code>QUERY-REF</code> request
	 * this SIP applies to. It is defined as the disjunction of the {@link #ANY},
	 * {@link #IOTA}, {@link #SOME} and {@link #ALL} constants.
	 */
	static public int IRE_QUANTIFIER = ANY | IOTA | SOME | ALL;

	
	Formula exists_VAR_b_AGENT_equals_IRE_VAR = SL.formula("(exists ??__var (B ??__agent (= ??__ire ??__var)))");

	private Term ireVariablesPattern;
	
	private Formula ireFormulaPattern;
	
	private Term agentPattern;
	
	private int ireQuantifierPattern;

	//**************************************************************************
	//**** CONSTRUCTORS
	//**************************************************************************
	
	/**
	 * Create a Query-Ref Preparation SIP Adapter applying to a given pattern
	 * of <code>QUERY-REF</code> request (defined by three patterns: one for the
	 * IRE quantifier, one for the quantified variables and one for the
	 * quantified formula) and a given pattern of originating agent (that is,
	 * the agent the request comes from).
	 * 
	 * @param capabilities         {@link SemanticCapabilities} instance of the
	 *                             semantic agent owning this instance of SIP.
	 * @param ireQuantifierPattern the pattern of the IRE quantifier this SIP
	 *                             applies to. It is specified as a disjunction
	 *                             between the {@link #ANY}, {@link #IOTA},
	 *                             {@link #SOME} and {@link #ALL} constants.
	 * @param ireVariablesPattern  the pattern of the IRE quantified variables
	 *                             this SIP applies to. It is specified as a
	 *                             FIPA-SL term (e.g. <code>??var</code>,
	 *                             <code>(sequence ??v1 ??v2)</code>, etc.). 
	 * @param ireFormulaPattern    the pattern of the IRE quantified formula
	 *                             this SIP applies to.
	 * @param agentPattern         the pattern of agent originating the
	 *                             <code>QUERY-REF</code> request this SIP
	 *                             applies to.
	 */
	public QueryRefPreparationSIPAdapter(SemanticCapabilities capabilities, int ireQuantifierPattern, Term ireVariablesPattern, Formula ireFormulaPattern, Term agentPattern) {
		super(capabilities, SL.formula("(I ??myself ??__phi)"));
		this.ireQuantifierPattern = ireQuantifierPattern;
		this.ireVariablesPattern = ireVariablesPattern;
		this.ireFormulaPattern = ireFormulaPattern;
		this.agentPattern = agentPattern;
	}

	/**
	 * Create a Query-Ref Preparation SIP Adapter applying to a given pattern
	 * of <code>QUERY-REF</code> request and a given pattern of originating
	 * agent. Equivalent to the
	 * {@link #QueryRefPreparationSIPAdapter(SemanticCapabilities, int, Term, Formula, Term)}
	 * constructor, with the <code>ireVariablesPattern</code>,
	 * <code>ireFormulaPattern</code> and <code>agentPattern</code> parameters
	 * specified as {@link String} objects (representing FIPA-SL expressions).
	 * 
	 * @param capabilities         {@link SemanticCapabilities} instance of the
	 *                             semantic agent owning this instance of SIP.
	 * @param ireQuantifierPattern the pattern of the IRE quantifier this SIP
	 *                             applies to. It is specified as a disjunction
	 *                             between the {@link #ANY}, {@link #IOTA},
	 *                             {@link #SOME} and {@link #ALL} constants.
	 * @param ireVariablesPattern  the pattern of the IRE quantified variables
	 *                             this SIP applies to.
	 * @param ireFormulaPattern    the pattern of the IRE quantified formula
	 *                             this SIP applies to.
	 * @param agentPattern         the pattern of agent originating the
	 *                             <code>QUERY-REF</code> request this SIP
	 *                             applies to.
	 */
	public QueryRefPreparationSIPAdapter(SemanticCapabilities capabilities, int ireQuantifierPattern, String ireVariablesPattern, String ireFormulaPattern, String agentPattern) {
		this(capabilities, ireQuantifierPattern, SL.term(ireVariablesPattern), SL.formula(ireFormulaPattern), SL.term(agentPattern));
	}

	/**
	 * Create a Query-Ref Preparation SIP Adapter applying to a given pattern
	 * of <code>QUERY-REF</code> request and a given pattern of originating
	 * agent, and has a given deadline to be applied. A <code>null</code>
	 * deadline does not set any "timeout" or "one shot" option of the SIP.
	 * The <code>ireVariablesPattern</code>, <code>ireFormulaPattern</code> and
	 * <code>agentPattern</code> parameters are specified as {@link String}
	 * objects (representing FIPA-SL expressions).
	 * 
	 * @param capabilities         {@link SemanticCapabilities} instance of the
	 *                             semantic agent owning this instance of SIP.
	 * @param ireQuantifierPattern the pattern of the IRE quantifier this SIP
	 *                             applies to. It is specified as a disjunction
	 *                             between the {@link #ANY}, {@link #IOTA},
	 *                             {@link #SOME} and {@link #ALL} constants.
	 * @param ireVariablesPattern  the pattern of the IRE quantified variables
	 *                             this SIP applies to.
	 * @param ireFormulaPattern    the pattern of the IRE quantified formula
	 *                             this SIP applies to.
	 * @param agentPattern         the pattern of agent originating the
	 *                             <code>QUERY-REF</code> request this SIP
	 *                             applies to.
	 * @param timeout              the deadline (given as a {@link Date})
	 *                             attached to the SIP.
	 * 
	 * @see ApplicationSpecificSIPAdapter#setTimeout(Date)
	 */
	public QueryRefPreparationSIPAdapter(SemanticCapabilities capabilities,
			int ireQuantifierPattern, String ireVariablesPattern, String ireFormulaPattern, String agentPattern, Date timeout) {
		this(capabilities, ireQuantifierPattern, ireVariablesPattern, ireFormulaPattern, agentPattern);
		setTimeout(timeout);
	}

	/**
	 * Create a Query-Ref Preparation SIP Adapter applying to a given pattern
	 * of <code>QUERY-REF</code> request and a given pattern of originating
	 * agent, and has a given timeout. A null timeout only sets the SIP in "one
	 * shot" mode (without timeout).
	 * The <code>ireVariablesPattern</code>, <code>ireFormulaPattern</code> and
	 * <code>agentPattern</code> parameters are specified as {@link String}
	 * objects (representing FIPA-SL expressions).
	 * 
	 * @param capabilities         {@link SemanticCapabilities} instance of the
	 *                             semantic agent owning this instance of SIP.
	 * @param ireQuantifierPattern the pattern of the IRE quantifier this SIP
	 *                             applies to. It is specified as a disjunction
	 *                             between the {@link #ANY}, {@link #IOTA},
	 *                             {@link #SOME} and {@link #ALL} constants.
	 * @param ireVariablesPattern  the pattern of the IRE quantified variables
	 *                             this SIP applies to.
	 * @param ireFormulaPattern    the pattern of the IRE quantified formula
	 *                             this SIP applies to.
	 * @param agentPattern         the pattern of agent originating the
	 *                             <code>QUERY-REF</code> request this SIP
	 *                             applies to.
	 * @param timeout              the deadline (given as a {@link Date})
	 *                             attached to the SIP.
	 * 
	 * @see ApplicationSpecificSIPAdapter#setTimeout(long)
	 */
	public QueryRefPreparationSIPAdapter(SemanticCapabilities capabilities,
			int ireQuantifierPattern, String ireVariablesPattern, String ireFormulaPattern, String agentPattern, long timeout) {
		this(capabilities, ireQuantifierPattern, ireVariablesPattern, ireFormulaPattern, agentPattern);
		setTimeout(timeout);
	}

	//**************************************************************************
	//**** OVERRIDDEN METHODS
	//**************************************************************************

	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter#doApply(jade.semantics.lang.sl.tools.MatchResult, jade.util.leap.ArrayList, jade.semantics.interpreter.SemanticRepresentation)
	 */
	@Override
	final protected ArrayList doApply(MatchResult applyResult, ArrayList result, SemanticRepresentation sr) {
		Formula goal = applyResult.formula("__phi");
		if (goal instanceof DoneNode &&
				((DoneNode)goal).as_formula() instanceof TrueNode &&
				((DoneNode)goal).as_action() instanceof ActionExpression) {
			try {
				goal = myCapabilities.getMySemanticActionTable()
						.getSemanticActionInstance((ActionExpression)((DoneNode)goal).as_action())
						.getRationalEffect();
			} catch (SemanticInterpretationException e) {
			}
		}
		
		Term ire = null;
		Term agent = null;
		
		applyResult = exists_VAR_b_AGENT_equals_IRE_VAR.match(goal);
		if (applyResult == null) {
			MetaVariableReferenceNode var = new MetaVariableReferenceNode("__var");
			Formula quantifiedFormula = goal.isExistsOn(var);
			if (quantifiedFormula != null && var.sm_value() != null) {
				MetaTermReferenceNode ag = new MetaTermReferenceNode("__agent");
				Formula believedFormula = quantifiedFormula.isBeliefFrom(ag);
				if (believedFormula != null) {
					ire = new AnyNode(var.sm_value(), believedFormula);
					agent = ag.sm_value();
				}
			}
		}
		else {
			ire = applyResult.term("__ire");
			agent = applyResult.term("__agent");
		}

		if (ire != null && ire instanceof IdentifyingExpression
				&& agent != null && !myCapabilities.getAgentName().equals(agent)) {
			int ireQuantifierMatch = 0;
			if (ire instanceof AnyNode) {
				ireQuantifierMatch = ireQuantifierPattern & ANY;
			}
			else if (ire instanceof IotaNode) {
				ireQuantifierMatch = ireQuantifierPattern & IOTA;
			}
			else if (ire instanceof SomeNode) {
				ireQuantifierMatch = ireQuantifierPattern & SOME;
			}
			else if (ire instanceof AllNode) {
				ireQuantifierMatch = ireQuantifierPattern & ALL;
			}

			MatchResult ireVariablesMatch = ireVariablesPattern.match(((IdentifyingExpression)ire).as_term());
			MatchResult ireFormulaMatch = ireFormulaPattern.match(((IdentifyingExpression)ire).as_formula());
			MatchResult agentMatch = agentPattern.match(agent);
			
			if (ireQuantifierMatch != 0 && ireVariablesMatch != null && ireFormulaMatch != null && agentMatch != null) {
				return prepareQueryRef((IdentifyingExpression)ire, ireVariablesMatch, ireFormulaMatch, agentMatch, result, sr);
			}
		}
		return null;
	}
	
	//**************************************************************************
	//**** METHODS TO OVERRIDE
	//**************************************************************************
	
	/**
	 * This method must be overriden to specify what to do to prepare the answer
	 * to the matched <code>QUERY-REF</code> request. Such a preparation usually
	 * consists in updating the agent's belief base with proper values. To do so,
	 * use preferably the
	 * {@link jade.semantics.interpreter.SemanticInterpretationPrinciple#potentiallyAssertFormula(Formula)}
	 * method instead of directly invoking the belief base object.
	 * <p>
	 * As for the return value, it works along the same line as the
	 * {@link jade.semantics.interpreter.SemanticInterpretationPrinciple#apply(SemanticRepresentation)}
	 * method. However, it is expected to be either:
	 * <ul>
	 *     <li><code>null</code>, if the SIP is actually not applicable,</li>
	 *     <li>the value of the <code>result</code> parameter, to make the SIP
	 *         neutral,
	 *     <li>an empty {@link ArrayList}, if the SIP is absorbent. In this case,
	 *         it is expected to interpret later (through a properly installed
	 *         behaviour) the value of the <code>result</code> parameter. This
	 *         option is particularly useful to interact with other agents
	 *         before getting the answer to the matched <code>QUERY-REF</code>
	 *         request.</li>
	 * </ul>
	 * Modifying the value of the <code>result</code> parameter or returning
	 * another value is strongly discouraged.
	 * </p>
	 * 
	 * @param ire               complete IRE of the matched <code>QUERY-REF</code>
	 *                          request.
	 * @param ireVariablesMatch result of the matching of the IRE quantified
	 *                          variables againt the pattern specified in the
	 *                          constructor.
	 * @param ireFormulaMatch   result of the matching of the IRE quantified
	 *                          formula against the pattern specified in the
	 *                          constructor.
	 * @param agentMatch        result of the matching of the agent originating
	 *                          the <code>QUERY-REF</code> request against the
	 *                          pattern specified in the constructor.
	 * @param result            value to return (if the answer to the
	 *                          <code>QUERY-REF</code> request can be computed
	 *                          at once) or to interpret later (if the computation
	 *                          of the answer needs further time and/or interactions).
	 * @param sr                incoming Semantic Representation that triggered
	 *                          the application of this SIP Adapter (this SR is
	 *                          therefore consummed if the SIP is eventually
	 *                          applicable).
	 *                          
	 * @return <code>null</code>, if the SIP is not applicable,
	 *         <br>an empty {@link ArrayList} to delay the preparation,</br>
	 *         <br>or <code>result</code> in other cases.
	 */
	abstract protected ArrayList prepareQueryRef(IdentifyingExpression ire,
											  MatchResult ireVariablesMatch,
											  MatchResult ireFormulaMatch,
											  MatchResult agentMatch,
											  ArrayList result,
											  SemanticRepresentation sr);
}

