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

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.QueryResult.BoolWrapper;
import jade.semantics.kbase.filters.FiltersDefinition;
import jade.semantics.kbase.filters.KBQueryFilter;
import jade.semantics.lang.sl.grammar.CountAsNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jade.util.leap.Set;


/**
 * This set of filters prevents the institution from answering "I don't know"
 * when she is asked about the negation of an institutional formula (countas
 * or institutional fact), since negations are not stored. This filters makes
 * her query the encapsulated formula, and inverse the result: 
 *   - if it is found, then the negation is false
 *   - if it is not found, then the negation is true (instead of unknown)
 * This allows agents to have certainties about institutional formulas being 
 * false (actually, everything that is not written in the institution registry
 * is considered to be false in the institution)
 * 
 * WARNING: when used, this filter must be located before the InstitutionalFactFilters and the CountAsFilters within the KBase filters (so added after in the code :)
 * TODO Use the closed predicate mechanism instead of this filter ?
 * 
 * 
 * @author wdvh2120 - Carole Adam, France Telecom
 * @version date 18 February 2008
 * @version 1.0 only manages institutional facts
 * @version 1.1 extended to manage countas formulas as well
 */
public class InstitutionalOmniscienceFilters extends FiltersDefinition {
	private final boolean DEBUG = false;
	
	// PATTERNS
	private Formula bNotPatternInstFact = SL.formula("(B ??myself (not (D ??inst ??phi)))");
	private Formula bNotPatternCountAs = SL.formula("(B ??myself (not (countas ??inst ??phi ??psi)))");
	
	// CONSTRUCTOR 
	public InstitutionalOmniscienceFilters() {

		/**************************************
		 * OMNISCIENCE ON INSTITUTIONAL FACTS *
		 **************************************/

		defineFilter(new KBQueryFilter() {
			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
				InstitutionTools.printTraceMessage("\n*** OmniscienceFilters filter1 for agent="+getMyKBase().getAgentName(), DEBUG);
				InstitutionTools.printTraceMessage("OmniscienceFilters filter1 pattern="+bNotPatternInstFact, DEBUG);
				InstitutionTools.printTraceMessage("OmniscienceFilters filter1 formula="+formula+"\n***", DEBUG);
				
				MatchResult applyResult = bNotPatternInstFact.match(formula);
				if (applyResult != null) {
					InstitutionTools.printTraceMessage("omniscience filters, not-inst-fact-query-filter, applyresult="+applyResult,DEBUG);
					
					//goOn.setBool(false);  // Do not try further filters
					//extract the formula
					Formula phi = applyResult.formula("phi");
					Term inst = applyResult.term("inst");
					Formula instFact = new InstitutionalFactNode(inst,phi);
					// query the institutional fact encapsulated in the negation instead
					QueryResult notQR = myKBase.query(instFact);
					// inverse the result
					InstitutionTools.printTraceMessage(" -> query("+instFact+") returns : "+notQR, DEBUG);
					if (notQR == QueryResult.UNKNOWN) {
						InstitutionTools.printTraceMessage(" -> inverse result, return KNOWN", DEBUG);
						return QueryResult.KNOWN;
					}
					return QueryResult.UNKNOWN; 
				}
				return null;
			}

			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				return false;
			}

			// nothing to display: do not override toStrings()

		});


		/***********************************
		 * OMNISCIENCE ON COUNTAS FORMULAS *
		 ***********************************/

		defineFilter(new KBQueryFilter() {
			/**
			 * {@inheritDoc}
			 * @see jade.semantics.kbase.filters.KBQueryFilter#apply(jade.semantics.lang.sl.grammar.Formula, jade.util.leap.ArrayList, jade.semantics.kbase.QueryResult.BoolWrapper)
			 */
			@Override
			public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
//				InstitutionTools.printTraceMessage("OmniscienceFilters filter2 for agent="+getMyKBase().getAgentName(), DEBUG);
//				InstitutionTools.printTraceMessage("OmniscienceFilters filter2 pattern="+bNotPatternCountAs, DEBUG);
//				InstitutionTools.printTraceMessage("OmniscienceFilters filter2 formula="+formula, DEBUG);
				
				MatchResult applyResult = bNotPatternCountAs.match(formula);
				if (applyResult != null) {
					InstitutionTools.printTraceMessage("omniscience filters, not-countas-query-filter, applyresult="+applyResult,DEBUG);
					//goOn.setBool(false);  // Do not try further filters
					//extract the formula
					Formula phi = applyResult.formula("phi");
					Formula psi = applyResult.formula("psi");
					Term inst = applyResult.term("inst");
					Formula countas = new CountAsNode(inst,phi,psi);
					// query the countas formula encapsulated in the negation instead
					QueryResult notQR = myKBase.query(countas);
					InstitutionTools.printTraceMessage(" -> query("+countas+") returns : "+notQR, DEBUG);
					// inverse the result
					if (notQR == QueryResult.UNKNOWN) {
						InstitutionTools.printTraceMessage(" inverse result, return KNOWN", DEBUG);
						return QueryResult.KNOWN;
					}
					return QueryResult.UNKNOWN;
				}
				return null;
			}

			@Override
			public boolean getObserverTriggerPatterns(Formula formula, Set set) {
				return false;
			}

			// nothing to display: do not override toStrings()

		});


		
	}


}
