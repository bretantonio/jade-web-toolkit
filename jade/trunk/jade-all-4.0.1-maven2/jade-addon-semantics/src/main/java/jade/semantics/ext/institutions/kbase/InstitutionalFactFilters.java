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

package jade.semantics.ext.institutions.kbase;

/*
 * created on 19 November 2007 by Carole Adam
 * Inspired by NestedBeliefsFilters
 */

import jade.core.AID;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.QueryResult.BoolWrapper;
import jade.semantics.kbase.filters.FiltersDefinition;
import jade.semantics.kbase.filters.KBAssertFilterAdapter;
import jade.semantics.kbase.filters.KBQueryFilter;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jade.util.leap.HashMap;
import jade.util.leap.Iterator;
import jade.util.leap.Set;

/**
 * The assert filter ensures that InstitutionalFacts are stored in a 
 * nested KBase corresponding to the institution to which they belong.
 * The query filter ensures that InstitutionalFacts are queried from 
 * the nested KBase corresponding to their owning institution.
 * 
 * Modification : now handles query with a not instantiated institution,
 * by browsing the list of keys of the HashMap of institutional KBases,
 * and then querying all the possible instantiated formulas
 * 
 * @author Carole Adam - France Telecom
 * @date 19 November 2007
 * @version 1.1 date 7 January 2008
 * @version 1.2 date 14 March 2008, CA - institutional ArrayList instead
 * of KBase in order to be able to explicitly store NotNode (since a 
 * commitment can be taken on a negative formula, and is different from no
 * commitment at all... so formulas like (D inst (not (B agt phi))) should 
 * be stored
 */
public abstract class InstitutionalFactFilters extends FiltersDefinition {
	
	private final boolean DEDUC_DEBUG = false;
	private final boolean NOT_DEBUG = false;
	private final boolean QUERY_DEBUG = false;
	private final boolean METHOD_DEBUG = false;
	
	// the HashMap of KBases for each institution
	private HashMap institutionalArrayLists;

	private Formula bPattern = SL.formula("(B ??myself (D ??inst ??phi))");
	private Formula notBPattern = SL.formula("(not (B ??myself (D ??inst ??phi)))");
	private Formula bNotPattern = SL.formula("(B ??myself (not (D ??inst ??phi)))");
	private Formula bDNotPhiPattern = SL.formula("(B ??myself (D ??inst (not ??phi)))");
	
	/**
	 * One KBase for each institution the agent belongs to
	 */
	public InstitutionalFactFilters() {
		this.institutionalArrayLists = new HashMap();

		/****************
		 * QUERY FILTER *
		 ****************/
		defineFilter(new KBQueryFilter() {

			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, jade.semantics.kbase.QueryResult.BoolWrapper goOn) {
				MatchResult applyResult = bPattern.match(formula);

				if (applyResult!=null) {
					//goOn.setBool(false);  // Do not try further filters
					// get the queried formula (encapsulated in the institutional fact)
					Formula institutionalFact = applyResult.formula("phi");
					Term owningInstitution = applyResult.term("inst");
					
					// SPECIAL CASE when institution is a MetaTermReferenceNode
					// call the standard case on all possible instantiations of institution
					if (owningInstitution instanceof MetaTermReferenceNode) {
						// get the list of institutions of this KBase
						Set listOfInstitutions = institutionalArrayLists.keySet();
						InstitutionTools.printTraceMessage("!!!!! SCAN list of inst = "+listOfInstitutions, QUERY_DEBUG);
						Iterator it = listOfInstitutions.iterator();
						// scan ALL institutions to which the agent belongs
						QueryResult result = new QueryResult();
						while ((it.hasNext())) { //even if qr != null
							// instantiate the queried formula with the current institution name
							String thisInst = (String) it.next();
							InstitutionTools.printTraceMessage("!!!!! -> inst="+thisInst, QUERY_DEBUG);
							MatchResult matchInst = owningInstitution.match(SL.term(thisInst));
							InstitutionTools.printTraceMessage("!!!!! -> matchInst with "+owningInstitution+": mr="+matchInst, QUERY_DEBUG);
							//Formula instForm = formula.instantiate("inst",SL.term(thisInst));
							Formula instForm = (Formula)InstitutionTools.instantiateFromMatchResult(formula,matchInst);
							InstitutionTools.printTraceMessage("!!!!! -> instantiated formula="+instForm,QUERY_DEBUG);
							// query the fact to this institution: see standard case (manages institutional deductions)
							QueryResult qr = myKBase.query(instForm);
							// scan the results in qr, join each one with matchInst before adding it to result
							
							// CODE BELOW MOVED INSIDE THE WHILE LOOP 18/03/07
							// if an institution recognising the queried fact was found (matchInst cannot be null or qr would be null too)
							if ((qr != null)) { 
								// create a MatchResult instantiating the institution
								if (qr.size()==0) {
									// if the result is Query.KNOWN, the result only 
									// gives the instantiation of the institution name
									result.add(matchInst);
								}
								else {
									for (int k=0;k<qr.size();k++) {
										result.add(matchInst.join(qr.getResult(k)));
									}
								}
							}
						}
						
						// if no institution recognising this fact was found
						if (result.size() == 0) {
							return QueryResult.UNKNOWN;
						}
						return result;
					}

					// true is true in any institution...
					if (institutionalFact instanceof TrueNode) {
						return QueryResult.KNOWN;
						// done AFTER the meta-ref check
						// if institution is a meta-ref, this should return a query result instantiating ??inst
						// that is, query(D ??inst true) should not return KNOWN ...
					}
					
					// STANDARD CASE : INSTANTIATED INSTITUTION
					// get from the HashMap the KBase corresponding to the owning institution
					ArrayList owningInstitutionArrayList = (ArrayList)institutionalArrayLists.get(owningInstitution.toString()); //FIXME
					// prepare the institutional deduction pattern
					Formula institutionalDeductionFormula = 
						new PredicateNode(SL.symbol("institutional-deduction"),
							new ListOfTerm(new Term[] {
								new FactNode(new InstitutionalFactNode(
										owningInstitution,
										institutionalFact))}));
					InstitutionTools.printTraceMessage("institutional deduction = "+institutionalDeductionFormula,DEDUC_DEBUG);
					
					if (owningInstitutionArrayList != null) {
						// query the institutional fact to this KBase
						QueryResult directQR = queryFormulaInArrayList(institutionalFact,owningInstitutionArrayList);
						InstitutionTools.printTraceMessage("query "+institutionalFact,DEDUC_DEBUG);
						InstitutionTools.printTraceMessage("directQR = "+directQR,DEDUC_DEBUG);
						if (directQR != null) { // UNKNOWN == null // && (directQR != QueryResult.UNKNOWN)) {
							return directQR;
						}
						QueryResult indirectQR = myKBase.query(institutionalDeductionFormula);
						InstitutionTools.printTraceMessage("indirect qr = "+indirectQR,DEDUC_DEBUG);
						return indirectQR;
					}
					// if this KBase is null the query fails...
					return myKBase.query(institutionalDeductionFormula);
					
					/* TODO if an agent is not able to answer a query about 
					 * an institutional fact by himself, he could transfer 
					 * the request to the agent representing the corresponding 
					 * institution to get an answer. */
				}
				return null;
			}
			
			// return a QueryResult as if phi was queried from a KBase
			public QueryResult queryFormulaInArrayList(Formula phi, ArrayList kbase) {
				ArrayList results = new ArrayList();
				InstitutionTools.printTraceMessage("seek "+phi+" in "+results,METHOD_DEBUG);
				for (int i=0; i<kbase.size();i++) {
					Formula fi = (Formula)kbase.get(i);
					InstitutionTools.printTraceMessage(" --> compare with f("+i+")="+fi,METHOD_DEBUG);
					MatchResult mri = SL.match(phi,fi);
					if (mri != null) {
						InstitutionTools.printTraceMessage(" --> -> add mri="+mri,METHOD_DEBUG);
						results.add(mri);
					}
				}
				if (results.size() ==0) {
					InstitutionTools.printTraceMessage(" --> empty list of mr, return UNKNWON",METHOD_DEBUG);
					return QueryResult.UNKNOWN;
				}
				QueryResult qr = new QueryResult();
				qr.addAll(results);
				InstitutionTools.printTraceMessage(" --> qr="+qr,METHOD_DEBUG);
				return qr;
			}
			
			
			/* (non-Javadoc)
			 * @see jade.semantics.kbase.filter.KBQueryFilter#getObserverTriggerPatterns(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.Set)
			 */
			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				return false;
			}

			// display of institutional facts in the KBase
			@Override
			public ArrayList toStrings() {
				ArrayList result = new ArrayList(institutionalArrayLists.size() * 15);
				Iterator its = institutionalArrayLists.keySet().iterator();
				while (its.hasNext()) {
					String anInst = (String)its.next();
					ArrayList institutionalArrayList = (ArrayList)institutionalArrayLists.get(anInst);
					result.add("******* InstitutionalFacts from "+anInst+" *******");
					
					for (int j=0;j<institutionalArrayList.size();j++) {
						// to avoid to display (D inst ***KBase***) (the first line returned by FilterKBase.toStrings()
						// TODO do the same check in NestedBeliefFilters !!
						String line = institutionalArrayList.get(j).toString();
						if (!line.startsWith("***")) {
							result.add("(D " + anInst + " " + line + ")");
						}
					}
					result.add("***");
				}
				return result;
			}
		});

		
		/*****************************
		 * QUERY FILTER ON NEGATIONS *
		 * OF INSTITUTIONAL FORMULAS *
		 *****************************/
		
		// query on a NotNode containing an institutional fact
		// reformulated as a query on the beliefs of the institution about this negated instfact
		defineFilter(new KBQueryFilter() {
			
			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
				MatchResult applyResult = bNotPattern.match(formula);
				if (applyResult != null) {
					//goOn.setBool(false);  // Do not try further filters
					InstitutionTools.printTraceMessage("inst fact filters, not-query-filter, applyresult="+applyResult,NOT_DEBUG);
					//extract the institution holding the queried institutional fact
					Term inst = applyResult.term("inst");
					// create the believe of this institution on the not-inst-fact
					Term institutionAgent = Tools.AID2Term(new AID(inst.toString(),AID.ISLOCALNAME));
					// check that the institution agent is NOT the owner of this KBase
					if (!institutionAgent.equals(myKBase.getAgentName())) {
						// create the believe of the institution-agent on this negated institutional fact
						BelieveNode instBelNotFact = new BelieveNode(institutionAgent,((BelieveNode)formula).as_formula());
						InstitutionTools.printTraceMessage("not-query-filter, ask instead: "+instBelNotFact,NOT_DEBUG);
						// query the agent's KBase about the institution's belief instead
						QueryResult instBelQR = myKBase.query(instBelNotFact);
						// trust the beliefs of the institution
						return instBelQR;
					}
				}
				return null;
			}
						
			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				return false;
			}
			
			// toStrings: nothing to display for this filter FIXME: print the not-inst-facts ??
			
		});
		
			
		// query on a NotNode containing an institutional fact
		// reformulated as a query on the institutional fact encapsulating the negation of the formula
		// query(not Ds phi) -> query(Ds not phi)   (if true it implies the original formula since D is rational)
		defineFilter(new KBQueryFilter() {
			
			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
				MatchResult applyResult = bNotPattern.match(formula);
				if (applyResult != null) {
					//goOn.setBool(false);  // Do not try further filters
					InstitutionTools.printTraceMessage("inst fact filters, not-query-filter-2, applyresult="+applyResult,NOT_DEBUG);
					//extract the institution holding the queried institutional fact
					Term inst = applyResult.term("inst");
					// create the believe of this institution on the not-inst-fact
					Term institutionAgent = Tools.AID2Term(new AID(inst.toString(),AID.ISLOCALNAME));
					// check that the institution agent is NOT the owner of this KBase
					if (!institutionAgent.equals(myKBase.getAgentName())) {
						// extract phi
						Formula phi = ((InstitutionalFactNode)((NotNode)((BelieveNode)formula).as_formula()).as_formula()).as_fact();
						// create the believe of the institution-agent on this negated institutional fact
						InstitutionalFactNode dInstNotPhi = new InstitutionalFactNode(inst,new NotNode(phi));
						InstitutionTools.printTraceMessage("not-query-filter-2, ask instead: "+dInstNotPhi,NOT_DEBUG);
						// query the agent's KBase about the institution's belief instead
						QueryResult dnotQR = myKBase.query(dInstNotPhi);
						// trust the beliefs of the institution
						return dnotQR;
					}
				}
				return null;
			}
						
			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				return false;
			}
			
			// toStrings: nothing to display for this filter FIXME: print the not-inst-facts ??
			
		});
		
		
		/*****************
		 * ASSERT FILTER *
		 *****************/
		defineFilter(new KBAssertFilterAdapter(bPattern) {
			/* (non-Javadoc)
			 * @see jade.semantics.kbase.filter.KBAssertFilterAdapter#doApply(jade.semantics.lang.sl.grammar.Formula, jade.semantics.lang.sl.tools.MatchResult)
			 */
			@Override
			public Formula doApply(Formula formula, MatchResult match) {
				if (match != null) {
					Formula institutionalFormula = match.formula("phi");
					if (institutionalFormula instanceof AndNode) {
						System.err.println("An institutional fact arrived unsplit in InstitutionalFactFilters !!!");
					}
					Term owningInstitution = match.term("inst");

					// get the KBase corresponding to the institution holding this fact
					ArrayList owningInstitutionArrayList = (ArrayList)institutionalArrayLists.get(owningInstitution.toString()); 
					if (owningInstitutionArrayList == null) {
						// create it if not existing yet 
						owningInstitutionArrayList = new ArrayList(); //newInstance(owningInstitution);
						// in this case add the default rules ? impossible: capabilities not known here
						institutionalArrayLists.put(owningInstitution.toString(), owningInstitutionArrayList);
					}
					// assert the institutional formula in the right KBase
					// WARNING: only add NEW formulas to avoid doubles in the list...
					if (! owningInstitutionArrayList.contains(institutionalFormula))  {
						owningInstitutionArrayList.add(institutionalFormula);  // assertFormula
					}
					
					/* update Observers (since this formula is locally stored in the filter,
					 * it is never asserted in the agent's KBase so observers are never updated...
					 * (see ArrayListKBaseImpl.assertFormula)
					 * 
					 * useless to encapsulate the nestedFormula in a belief of the agent owning
					 * the FilterKBase since this operation is made by Observer.update(Formula)
					 */
					myKBase.updateObservers(new InstitutionalFactNode(owningInstitution, institutionalFormula));
					return SL.TRUE;
				}
				return super.doApply(formula, match);				
			}//end doApply
		});

		/*******************
		 * RETRACT FILTERS *
		 *******************/
		// notBPattern corresponds to the belief asserted when retracting bPattern
		defineFilter(new KBAssertFilterAdapter(notBPattern) {
			@Override
			public Formula doApply(Formula formula, MatchResult match) {
				if (match != null) {
					// get the KBase corresponding to the owning institution
					Term owningInstitution = match.term("inst");
					ArrayList owningInstitutionArrayList = (ArrayList)institutionalArrayLists.get(owningInstitution.toString());

					// remove the institutional fact from this KBase
					Formula institutionalFact = match.formula("phi");
					owningInstitutionArrayList.remove(institutionalFact); //retractFormula(institutionalFact);
					return SL.TRUE;
				}
				return super.doApply(formula, match);
			}
		});

		// also retract the institutional fact when asserting (not (D inst phi))
		defineFilter(new KBAssertFilterAdapter(bNotPattern) {
			@Override
			public Formula doApply(Formula formula, MatchResult match) {
				if (match != null) {
					// get the KBase corresponding to the owning institution
					Term owningInstitution = match.term("inst");
					ArrayList owningInstitutionArrayList = (ArrayList)institutionalArrayLists.get(owningInstitution.toString());

					// remove the institutional fact from this KBase
					Formula institutionalFact = match.formula("phi");
					//myKBase.retractFormula(new InstitutionalFactNode(owningInstitution,institutionalFact));
					owningInstitutionArrayList.remove(institutionalFact); //retractFormula
					return SL.TRUE;
				}
				return super.doApply(formula, match);
			}
		});		

		
		/************************************
		 * COMMITMENT ON A NEGATIVE FORMULA *
		 ************************************/
		// this should not only retract the opposite commitment,
		// but also be stored to detect possible subsequent contradictions
		defineFilter(new KBAssertFilterAdapter(bDNotPhiPattern) {
			@Override
			public Formula doApply(Formula formula, MatchResult match) {
				if (match != null) {
					// get the KBase corresponding to the owning institution
					Term owningInstitution = match.term("inst");
					ArrayList owningInstitutionArrayList = (ArrayList)institutionalArrayLists.get(owningInstitution.toString());

					// remove the institutional fact from this KBase
					Formula institutionalFact = match.formula("phi");
					owningInstitutionArrayList.remove(institutionalFact);
					owningInstitutionArrayList.add(new NotNode(institutionalFact));
					return SL.TRUE;
				}
				return super.doApply(formula, match);
			}
		});
		
	} //end constructor

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