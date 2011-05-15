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

package jade.semantics.ext.institutions.kbase;

/*
 * Created by Carole Adam, France Telecom
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.QueryResult.BoolWrapper;
import jade.semantics.kbase.filters.FiltersDefinition;
import jade.semantics.kbase.filters.KBAssertFilterAdapter;
import jade.semantics.kbase.filters.KBQueryFilter;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jade.util.leap.Set;

/**
 * Filters to manage the agents' bank accounts.
 * @author wdvh2120
 */
public abstract class MoneyFiltersNew extends FiltersDefinition {

	private final boolean DEBUG = false;

	// prevent agents from accessing the bank account of other agents
	private final static Formula believe_MYSELF_hasmoney_OTHER_AMOUNT = SL.formula("(B ??myself (has-money ??other ??amount))");
	private final static Formula believe_MYSELF_not_hasmoney_OTHER_AMOUNT = SL.formula("(B ??myself (not (has-money ??other ??amount)))");
	private final static Formula hasMoneyPattern = SL.formula("(has-money ??agent ??amount)");
	
	public MoneyFiltersNew() {

		/*************************************
		 * ********************************* *
		 * ******* HAS-MONEY FILTERS ******* *
		 * ********************************* *
		 *************************************/

		/***********************************
		 * HAS-MONEY POSITIVE QUERY FILTER *
		 ***********************************/
		defineFilter(new KBQueryFilter() {
			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
				MatchResult applyResult = believe_MYSELF_hasmoney_OTHER_AMOUNT.match(formula); 

				if (applyResult!=null) {
					//goOn.setBool(false);  // Do not try further filters
					Term me = applyResult.term("myself");
					Term other = applyResult.term("other");
					if (! me.equals(other)) {
						return QueryResult.UNKNOWN;
					}
					// in the following: other = myself
					InstitutionTools.printTraceMessage("query if has money, match="+applyResult,DEBUG);
					InstitutionTools.printTraceMessage("agent="+myKBase.getAgentName(),DEBUG);
					Term amount = applyResult.term("amount");
					// do not manage the case when it is a meta-reference in this filter
					if (amount instanceof Constant) {
						Long theAmount = ((Constant)amount).intValue();
						InstitutionTools.printTraceMessage("theAmount="+theAmount,DEBUG);
						if (theAmount <= InstitutionTools.getMoneyOf(myKBase.getAgentName())) {
							return QueryResult.KNOWN;
						}
					}
					else if (amount instanceof MetaTermReferenceNode) {
						return new QueryResult(SL.match(amount,SL.integer(InstitutionTools.getMoneyOf(myKBase.getAgentName()))));
					}
					return QueryResult.UNKNOWN;
				}
				return null;
			}

			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				MatchResult mr = believe_MYSELF_hasmoney_OTHER_AMOUNT.match(formula);
				if (mr != null) {
					Term agent = mr.term("other");
					// observers triggered by any amount
					set.add(SL.instantiate(hasMoneyPattern,"agent",agent));
					return false;
				}
				return true;
			}

			// Each agent only knows his own amount of money
			// (it is added at the very end of the KBase... see FilterKBaseImpl.toStrings())
			@Override
			public ArrayList toStrings() {
				ArrayList result = new ArrayList(1);
				Formula myAccount = SL.formula("(has-money ??agent ??amount)");
				myAccount = myAccount.instantiate("agent",myKBase.getAgentName());
				myAccount = myAccount.instantiate("amount",SL.integer(InstitutionTools.getMoneyOf(myKBase.getAgentName())));
				result.add(myAccount.toString());
				return result;
			}
			
			// no specific toStrings()
		});


		/***********************************
		 * HAS-MONEY NEGATIVE QUERY FILTER *
		 ***********************************/
		defineFilter(new KBQueryFilter() {
			// query not : answer KNOWN if owned-amount < asked-amount
			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
				MatchResult applyResult = believe_MYSELF_not_hasmoney_OTHER_AMOUNT.match(formula); 
				if (applyResult!=null) {
					//goOn.setBool(false);  // Do not try further filters
					Term me = applyResult.term("myself");
					Term other = applyResult.term("other");
					if (! me.equals(other)) {
						return QueryResult.UNKNOWN;
					}
					// in the following other = myself
					InstitutionTools.printTraceMessage("query if not has money, match="+applyResult,DEBUG);
					InstitutionTools.printTraceMessage("agent="+myKBase.getAgentName(),DEBUG);
					Term amount = applyResult.term("amount");
					// do not manage the case when it is a meta-reference in this filter
					if (amount instanceof IntegerConstantNode) {
						Long theAmount = ((IntegerConstantNode)amount).lx_value();
						InstitutionTools.printTraceMessage("theAmount="+theAmount,DEBUG);
						if (theAmount > InstitutionTools.getMoneyOf(myKBase.getAgentName())) {
							return QueryResult.KNOWN;
						}
					}
					return QueryResult.UNKNOWN;
				}
				return null;
			}

			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				return false;
			}

			// no specific toStrings()
		});


		/***************************
		 * HAS-MONEY ASSERT FILTER *
		 ***************************/
		defineFilter(new KBAssertFilterAdapter(believe_MYSELF_hasmoney_OTHER_AMOUNT) {

			// assert has-money (at setup of agent to initialise myMoney)			
			@Override
			public Formula doApply(Formula formula, MatchResult match) {
				if (match!=null) {
					Term me = match.term("myself");
					Term other = match.term("other");
					if (! me.equals(other)) {
						// absorb money of others
						return SL.TRUE;
					}
					// in the following, other = myself
					InstitutionTools.printTraceMessage("assert has money, match="+match,DEBUG);
					InstitutionTools.printTraceMessage("agent="+myKBase.getAgentName(),DEBUG);
					Term amount = match.term("amount");
					// do not manage the case when it is a meta-reference in this filter
					if (amount instanceof Constant) {
						Long theAmount = ((Constant)match.term("amount")).intValue();
						// retract the old has-money
						InstitutionTools.setMoneyOf(myKBase.getAgentName(),theAmount);
						InstitutionTools.printTraceMessage("new my money="+theAmount,DEBUG);
						// ABSORB
						myKBase.updateObservers(formula);
						return SL.TRUE;
					}
				}
				// transmit the formula to be filtered and finally asserted
				return super.doApply(formula, match);
			}
		});

	}//end of constructor containing filters definitions
	
	
	/**
	 * Creates a new instance of KBase. The new instance should be set exactly
	 * as the KBase instance that runs the invoked newInstance() method.
	 * For example, for FilterKBaseImpl instances, newly created instances
	 * should be set with the same filters as the original instance.
	 * 
	 * @param institution the institution to which belong the institutional facts 
	 * of the new instance of KBase
	 * @return a new instance of KBase
	 */
	public abstract KBase newInstance(Term institution);
	
	
}
