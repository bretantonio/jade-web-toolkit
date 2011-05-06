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
 * created on 22 November 2007 by Carole Adam
 */


package jade.semantics.ext.institutions.kbase;

/*
 * Created by Carole Adam, 22 November 2007
 * Upgraded by Carole Adam, 18 February 2008
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
import jade.semantics.lang.sl.grammar.CountAsNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jade.util.leap.HashMap;
import jade.util.leap.Iterator;
import jade.util.leap.Set;

/**
 * The assert filter ensures that CountAsFacts are stored in a nested KBase
 * corresponding to the institution to which they belong.
 * The query filter ensures that CountAsFacts are queried from the nested
 * KBase corresponding to their owning institution.
 * 
 * @author Carole Adam - France Telecom
 * @date 22 November 2007
 * @version 1.1 date 4 February 2008 :
 *     - correction of retract filter 
 *     - correction of return type of query filter: QueryResult instead of boolean
 *     - added a new QueryFilter for institutional deductions using the list of countas formulas
 * @version 1.2 date 18 February 2008
 *     - second retract filter with different pattern
 *     - filter for the query of negated countas formulas
 */
public abstract class CountAsFilters extends FiltersDefinition {

	private final boolean DEBUG = false;
	private final boolean NEW_DEBUG = false;

	// the HashMap of ArrayLists of CountAs Formulas 
	// keys = names of institution holding the arraylist of countas formulas
	private HashMap countAsFormulas;

	// the patterns triggering the (assert, retract and query) filters 
	private Formula bPattern = SL.formula("(B ??myself (countas ??inst ??phi ??psi))");
	private Formula notBPattern = SL.formula("(not (B ??myself (countas ??inst ??phi ??psi)))");
	private Formula bNotPattern = SL.formula("(B ??myself (not (countas ??inst ??phi ??psi)))");


	/**
	 * One KBase for each institution the agent belongs to
	 */
	public CountAsFilters() {
		this.countAsFormulas = new HashMap();

		/*************************
		 * STANDARD QUERY FILTER *
		 *************************/
		/* Query filter redirecting the query to the ArrayList stored
		 * in the HashMap under the key name of the corresponding institution
		 * Manages queries with a non-instantiated institution name (MetaTermReferenceNode)
		 */		
		defineFilter(new KBQueryFilter() {
			// FIXME: only manages instantiated formulas !!
			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
				MatchResult applyResult = bPattern.match(formula); 

				if (applyResult!=null) {
					//goOn.setBool(false); // Do not try further filters
					// get the owning institution and its ArrayList from the HashMap
					Term owningInstitution = applyResult.term("inst");
					if (owningInstitution instanceof MetaTermReferenceNode) {
						// scan the possible instantiations of the institution
						Set listOfInsts = countAsFormulas.keySet();
						Iterator it = listOfInsts.iterator();
						MatchResult matchInst = null;
						QueryResult qr = null;
						String thisInst="";
						while ((it.hasNext()) && (qr == null)) {
							thisInst = (String) it.next();
							matchInst = owningInstitution.match(SL.term(thisInst));
							if (matchInst == null) {
								return QueryResult.UNKNOWN;
							}
							Formula instCountAs = (Formula)InstitutionTools.instantiateFromMatchResult(formula,matchInst);
							// query the CountAs instantiated with the current institution name
							qr = myKBase.query(instCountAs);
						}
						if (matchInst == null) {
							return QueryResult.UNKNOWN;
						}
						// as soon as one instantiated count as is known, 
						// or after having tested in vain all institutions
						if ((qr != null)&&(thisInst!="")) {
							if (qr.size()==0) { // qr == QueryResult.KNOWN
								// return the instantiation of the institution name
								return new QueryResult(matchInst);
							}
							/* return the instantiation of the institution, joined with all
							 * possible instantiations of other metareferences 
							 */ 
							QueryResult result = new QueryResult();
							for (int k=0;k<qr.size();k++) {
								result.add(matchInst.join(qr.getResult(k)));
							}
							return result;
						}
						// if no institution recognising this countas was found
						return QueryResult.UNKNOWN;
					}

					// extract its features
					Formula phi = applyResult.formula("phi"); //countAsFormula.as_left_formula
					Formula psi = applyResult.formula("psi"); 
					// at this point owningInstitution is instantiated (MetaTermReference case handled at previous step)
					// countAsFormula should be = new InstitutionalFactNode(owningInstitution,phi,psi)
					// the count as formula with instantiated institution !!
					CountAsNode countAsFormula = new CountAsNode(owningInstitution,phi,psi);
					ArrayList owningInstitutionArrayList = (ArrayList)countAsFormulas.get(owningInstitution.toString());
					if (owningInstitutionArrayList == null) {
						return QueryResult.UNKNOWN;
					}

					// decompose the formula if possible 
					// (query redirected to the standard case of this filter)
					// axiom CC
					if (psi instanceof AndNode) {
						Formula q1 = ((AndNode)psi).as_left_formula();
						Formula q2 = ((AndNode)psi).as_right_formula();
						Formula ca1 = new CountAsNode(owningInstitution,phi,q1);
						Formula ca2 = new CountAsNode(owningInstitution,phi,q2);
						// query (phi countas q1) and (phi countas q2) instead of (phi countas (q1 and q2))

						return myKBase.query(new AndNode(ca1,ca2), falsityReasons);
						//result = (result || (isValidCountAs(ca1,owningInstitution) && isValidCountAs(ca2, owningInstitution)) );
					}

					// axiom CA 
					if (phi instanceof OrNode) {
						Formula p1 = ((OrNode)phi).as_left_formula();
						Formula p2 = ((OrNode)phi).as_right_formula();
						Formula ca1 = new CountAsNode(owningInstitution,p1,phi);
						Formula ca2 = new CountAsNode(owningInstitution,p2,phi);
						// query (p1 countas psi) and (p2 countas psi) instead of ((p1 or p2) countas psi)

						return myKBase.query(new AndNode(ca1,ca2), falsityReasons);
						//result = (result || ( isValidCountAs(ca1, owningInstitution) && isValidCountAs(ca2, owningInstitution) ));
					}

					// STANDARD CASE

					// FIRST SUBCASE
					// check if the countAsFormula is directly valid (phi and psi instantiated)
					if ((!(phi instanceof MetaFormulaReferenceNode) && (!(psi instanceof MetaFormulaReferenceNode)))) {
						if (owningInstitutionArrayList.contains(countAsFormula)) {
							return QueryResult.KNOWN;
						}
						return QueryResult.UNKNOWN;
						//result = (result || isValidCountAs(countAsFormula, owningInstitution));
					}

					// SECOND SUBCASE
					// case when one or two formulas are not instantiated (MetaFormulaReferenceNode)
					// -> scan the whole list of CountAs formulas and try to match
					// add the result of the matching to the QueryResult
					QueryResult builtQR = new QueryResult();
					for (int i=0;i<owningInstitutionArrayList.size();i++) {
						CountAsNode oneCountAs = (CountAsNode)owningInstitutionArrayList.get(i);
						InstitutionTools.printTraceMessage("@CAF@ ca1="+oneCountAs,DEBUG);
						InstitutionTools.printTraceMessage("@CAF@ ca2="+countAsFormula,DEBUG);
						MatchResult oneMatch = SL.match(oneCountAs,countAsFormula);
						InstitutionTools.printTraceMessage("@CAF@ !!! oneMatch computed ="+oneMatch,DEBUG);

						if (oneMatch != null) {
							InstitutionTools.printTraceMessage("oneMatch="+oneMatch,DEBUG);
							InstitutionTools.printTraceMessage("builtQR="+builtQR,DEBUG);
							builtQR.add(oneMatch);
						}
					}
					// return the built QueryResult if it contains at least one MatchResult
					if (builtQR.size()>0) {
						return builtQR;
					}
					return QueryResult.UNKNOWN;
				}
				return null;
			}

			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				return false;
			}

			@Override
			public ArrayList toStrings() {
				ArrayList result = new ArrayList(countAsFormulas.size() * 15);
				result.add("******* CountAs formulas *******");
				for (Iterator it = countAsFormulas.values().iterator(); it.hasNext(); ) {
					ArrayList stringKBase = (ArrayList)it.next(); 
					for (Iterator jt = stringKBase.iterator(); jt.hasNext(); ) {
						result.add(jt.next());
					}
				}
				result.add("***");
				return result;
			}

		});


		/*******************************
		 * QUERY FILTER FOR DEDUCTIONS *
		 *******************************/
		// Query filter for deductions made from countas stored in this filter
		defineFilter(new KBQueryFilter() {

			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
				Formula pattern = new BelieveNode(myKBase.getAgentName(),new PredicateNode(SL.symbol("institutional-deduction"),
						new ListOfTerm(new Term[] {
								new FactNode(new InstitutionalFactNode(
										new MetaTermReferenceNode("institution"),
										new MetaFormulaReferenceNode("phi")))})));
				MatchResult mr = pattern.match(formula);
				if (mr != null) {
					//goOn.setBool(false);  // Do not try further filters
					InstitutionTools.printTraceMessage("deduction filter, pattern="+pattern,DEBUG);
					InstitutionTools.printTraceMessage("deduction filters, formula="+formula,DEBUG);
					InstitutionTools.printTraceMessage("deduction filters, match="+mr,DEBUG);
					// extract features of the institutional fact
					Term institution = mr.term("institution");
					Formula phi = mr.formula("phi"); //FIXME what if it is a metaterm ?
					Formula psiPattern = new MetaFormulaReferenceNode("psi");

					// CASE 1
					// query (countas inst phi q), and if true and phi true, then ok (axiom SC)
					CountAsNode phiCountAsPsi = new CountAsNode(institution,phi,psiPattern);
					InstitutionTools.printTraceMessage("first query : "+phiCountAsPsi,DEBUG);
					if (myKBase.query(phiCountAsPsi) != null) {
						InstitutionTools.printTraceMessage(">>> known, now query "+phi,DEBUG);
						if (myKBase.query(phi)!=null) {
							InstitutionTools.printTraceMessage("    >>> known !",DEBUG);
							return QueryResult.KNOWN;
						}
					}

					// CASE 2
					// query (countas inst q phi), and if true then query (D inst q)  
					// (consequence of axioms SD and C for CountAs (normal operator)
					else {
						CountAsNode psiCountAsPhi = new CountAsNode(institution,psiPattern,phi);
						QueryResult qr = myKBase.query(psiCountAsPhi);
						InstitutionTools.printTraceMessage("second query : "+psiCountAsPhi,DEBUG);
						if (qr != null) { // qr cannot be QueryResult.KNOWN since there is a ??psi metavariable
							InstitutionTools.printTraceMessage(">>> known, qr="+qr,DEBUG);
							for (int i=0;i<qr.size();i++) {
								Formula psi = qr.getResult(i).formula("psi");
								QueryResult qrPsi = myKBase.query(new InstitutionalFactNode(institution,psi));
								InstitutionTools.printTraceMessage("    >>> instantiation("+i+")="+qrPsi,DEBUG);
								if (qrPsi == QueryResult.KNOWN) {
									// as soon as one possible premise is known: success
									InstitutionTools.printTraceMessage("        >>> known: ok !",DEBUG);
									return QueryResult.KNOWN;
								}
								// else go on and try other possible instantiations 
							}
						}

						// CASE 3
						/* if it exists but (D inst q) is false, the InstitutionalFactQueryFilter will 
						 * look for (inst-deduc Dsq) and the InstDeducQueryFilter(Dsq) will find
						 * (countas inst q phi) and will be responsible for checking if q is true (thus back to case 1)
						 * and will finally answer that (D inst q) is true (by temporary deduction)
						 * If it answers FALSE it is because q is also false.
						 * So it is NOT necessary to check if q is true HERE.
						 */
						// FINAL CASE
						else {
							return QueryResult.UNKNOWN;
						}
					}
				}
				return null;
			}



			@Override
			public ArrayList toStrings() {
				ArrayList strings = new ArrayList(countAsFormulas.size() * 15);

				for (Iterator it = countAsFormulas.keySet().iterator(); it.hasNext(); ) {
					String oneInst = (String)it.next();
					InstitutionTools.printTraceMessage("#CAF# oneInst="+oneInst,DEBUG);
					ArrayList stringKBase = (ArrayList)countAsFormulas.get(oneInst);
					Tools.printTraceMessage("#CAF# stringKBase="+stringKBase,DEBUG);
					strings.add("******* Institutional deductions in "+oneInst+" *******");

					for (Iterator jt = stringKBase.iterator(); jt.hasNext(); ) {
						CountAsNode oneCountAs = (CountAsNode) jt.next();
						InstitutionTools.printTraceMessage("#CAF# oneCountAs="+oneCountAs,DEBUG);
						// extract features of countAs
						// WARNING : if the countas was generic (not instantiated, containing 
						// meta-references), its features can be uninstantiated as well
						Formula onePhi = oneCountAs.as_left_formula();
						Formula onePsi = oneCountAs.as_right_formula();
						Formula oneDsPhi = new InstitutionalFactNode(SL.term(oneInst),onePhi);
						Formula oneDsPsi = new InstitutionalFactNode(SL.term(oneInst),onePsi);
						InstitutionTools.printTraceMessage("#CAF# onePhi="+onePhi,DEBUG);
						InstitutionTools.printTraceMessage("#CAF# onePsi="+onePsi,DEBUG);
						InstitutionTools.printTraceMessage("#CAF# oneDsPhi="+oneDsPhi,DEBUG);
						InstitutionTools.printTraceMessage("#CAF# oneDsPsi="+oneDsPsi,DEBUG);

						QueryResult queryPhi = myKBase.query(onePhi);
						InstitutionTools.printTraceMessage("#CAF# queryPhi="+queryPhi,DEBUG);
						if (queryPhi!=null) {
							// scan the possible instantiations of onePhi 
							// to find the corresponding instantiations of oneDsPhi and oneDsPsi
							if (queryPhi != QueryResult.KNOWN) {
								InstitutionTools.printTraceMessage("queryPhi="+queryPhi,DEBUG);
								for (int i=0;i<queryPhi.size();i++) {
									MatchResult onePhiMatch = queryPhi.getResult(i);
									InstitutionTools.printTraceMessage("#CAF# onePhiMatch="+onePhiMatch,DEBUG);
									strings.add(InstitutionTools.instantiateFromMatchResult(oneDsPhi,onePhiMatch));
									strings.add(InstitutionTools.instantiateFromMatchResult(oneDsPsi,onePhiMatch));
									InstitutionTools.printTraceMessage("#CAF# oneDsPhi inst("+i+")="+InstitutionTools.instantiateFromMatchResult(oneDsPhi,onePhiMatch),DEBUG);
									InstitutionTools.printTraceMessage("#CAF# oneDsPsi inst("+i+")="+InstitutionTools.instantiateFromMatchResult(oneDsPsi,onePhiMatch),DEBUG);
								}
							}
							// if the result is QueryResult.KNOWN, the features are already instantiated
							else {
								// query phi, if true axiom SC allows to deduce Ds-phi
								strings.add(oneDsPhi);
								// from Ds-phi, axiom SD allows to deduce Ds-psi (thanks to axiom C for normal operators)
								strings.add(oneDsPsi);
								InstitutionTools.printTraceMessage("#CAF# oneDsPhi (already instantiated) ="+oneDsPhi,DEBUG);
								InstitutionTools.printTraceMessage("#CAF# oneDsPsi (already instantiated) ="+oneDsPsi,DEBUG);
							}
						}
						// else ask if Ds-phi is nevertheless true
						else {
							// from Ds-phi, axiom SD allows to deduce Ds-psi (thanks to axiom C for normal operators)
							Tools.printTraceMessage("#CAF# !!! query "+oneDsPhi,DEBUG);
							QueryResult queryDsPhi = myKBase.query(oneDsPhi);
							Tools.printTraceMessage("#CAF# queryDsPhi="+queryDsPhi,DEBUG);
							if (queryDsPhi!=null) {
								// if there are several instantiations, scan them to instantiate all possible deductions
								if (queryDsPhi != QueryResult.KNOWN) {
									for (int j=0;j<queryDsPhi.size();j++) {
										MatchResult oneDsPhiMatch = queryDsPhi.getResult(j);
										Tools.printTraceMessage("#CAF# oneDsPhiMatch("+j+")="+oneDsPhiMatch,DEBUG);
										strings.add(InstitutionTools.instantiateFromMatchResult(oneDsPsi,oneDsPhiMatch));
										Tools.printTraceMessage("#CAF# oneDsPsi inst("+j+")="+InstitutionTools.instantiateFromMatchResult(oneDsPsi,oneDsPhiMatch),DEBUG);
									}
								}
								// if the deduction is already fully instantiated, directly add it
								else {
									Tools.printTraceMessage("#CAF# oneDsPsi already instantiated ="+oneDsPsi,DEBUG);
									strings.add(oneDsPsi);									
								}
							}
						}
					}
				}
				strings.add("***");
				return strings;
			}

			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				return false;
			}

		});


		/*****************************
		 * QUERY FILTER ON NEGATIONS *
		 *    OF COUNTAS FORMULAS    *
		 *****************************/

		// query on a NotNode containing a countas formula
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
					InstitutionTools.printTraceMessage("countas filters, not-query-filter, applyresult="+applyResult,NEW_DEBUG);
					//extract the institution holding the queried institutional fact
					Term inst = applyResult.term("inst");
					// create the believe of this institution on the not-inst-fact
					Term institutionAgent = Tools.AID2Term(new AID(inst.toString(),AID.ISLOCALNAME));
					// check that the institution agent is NOT the owner of this KBase
					if (!institutionAgent.equals(myKBase.getAgentName())) {
						// create the believe of the institution-agent on this negated institutional fact
						BelieveNode instBelNotCountas = new BelieveNode(institutionAgent,((BelieveNode)formula).as_formula());
						InstitutionTools.printTraceMessage("not-query-filter, ask instead: "+instBelNotCountas,NEW_DEBUG);
						// query the agent's KBase about the institution's belief instead
						QueryResult instBelQR = myKBase.query(instBelNotCountas);
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

			// toStrings: nothing to display for this filter

		});



		/*****************
		 * ASSERT FILTER *
		 *****************/

		defineFilter(new KBAssertFilterAdapter(bPattern) {

			@Override
			public Formula doApply(Formula formula, MatchResult match) {
				if (match != null) {
					Formula countAsFormula = ((BelieveNode)formula).as_formula();
					Term owningInstitution = match.term("inst");

					ArrayList owningInstitutionArrayList = (ArrayList)countAsFormulas.get(owningInstitution.toString()); 
					if (owningInstitutionArrayList == null) {
						// create it if not existing yet 
						owningInstitutionArrayList = new ArrayList();
						countAsFormulas.put(owningInstitution.toString(), owningInstitutionArrayList);
					}
					// assert the institutional formula in the right KBase
					owningInstitutionArrayList.add(countAsFormula);
					// update observers possibly waiting for this formula (and only 
					// watching the main KBase and not the local storages of its filters) 
					myKBase.updateObservers(countAsFormula);
					return SL.TRUE;
				}
				return super.doApply(formula, match);				
			}//end doApply
		});



		/*******************************
		 *       RETRACT FILTERS       *
		 * CASE 1: NOT BELIEVE COUNTAS *
		 *******************************/

		// retract case1
		// notBPattern corresponds to the belief asserted when retracting bPattern
		defineFilter(new KBAssertFilterAdapter(notBPattern) {
			@Override
			public Formula doApply(Formula formula, MatchResult match) {
				if (match != null) {
					// get the KBase corresponding to the owning institution
					Term owningInstitution = match.term("inst");
					ArrayList owningInstitutionList = (ArrayList)countAsFormulas.get(owningInstitution.toString());
					CountAsNode countAsFormula = (CountAsNode)((BelieveNode)((NotNode)formula).as_formula()).as_formula();
					InstitutionTools.printTraceMessage("countas-reract-filter, countasformula="+countAsFormula,DEBUG);
					InstitutionTools.printTraceMessage("countas retract filter, formula="+formula,DEBUG);

					if (owningInstitutionList != null) {
						// remove the institutional fact from this KBase
						InstitutionTools.printTraceMessage("notbpattern, remove "+countAsFormula+" from "+owningInstitutionList.toString(),DEBUG);
						owningInstitutionList.remove(countAsFormula);
					}

					return SL.TRUE;
				}
				return super.doApply(formula, match);
			}
		});

		
		/*******************************
		 *       RETRACT FILTERS       *
		 * CASE 2: BELIEVE NOT COUNTAS *
		 *******************************/
		
		// retract case2 (assertion of the opposite)
		defineFilter(new KBAssertFilterAdapter(bNotPattern) {
			@Override
			public Formula doApply(Formula formula, MatchResult match) {
				if (match != null) {
					// get the KBase corresponding to the owning institution
					Term owningInstitution = match.term("inst");
					ArrayList owningInstitutionList = (ArrayList)countAsFormulas.get(owningInstitution.toString());
					Formula countAsFormula = ((NotNode)((BelieveNode)formula).as_formula()).as_formula();

					if (owningInstitutionList != null) {
						// remove the institutional fact from this KBase
						owningInstitutionList.remove(countAsFormula);
					}

					return SL.TRUE;
				}
				return super.doApply(formula, match);
			}
		});

	} //end constructor



	/***********
	 * METHODS *
	 ***********/

	public boolean isValidCountAs(Formula aFormula, Term anInstitution) {
		ArrayList owningInstitutionArrayList = (ArrayList)countAsFormulas.get(anInstitution.toString());
		return ((owningInstitutionArrayList != null) &&
				(owningInstitutionArrayList.indexOf(aFormula) != -1));
	}


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