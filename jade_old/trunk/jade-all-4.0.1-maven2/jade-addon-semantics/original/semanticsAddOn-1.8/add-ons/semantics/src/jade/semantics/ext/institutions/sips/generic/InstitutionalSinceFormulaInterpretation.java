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


package jade.semantics.ext.institutions.sips.generic;

/*
 * Created by Carole Adam, 10 April 2008
 * to replace FutureInstitutionalFormulaInterpretation
 */

import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;


/**
 * Since formulas institutionally deduced (as the effect of 
 * powers or countas) are encapsulated in an institutional fact
 * and thus do not trigger {@link SinceFormulaInterpretation}.
 * 
 * This SIP inverses the encapsulation: it interprets 
 * <code> (since (fact (D ??inst ??phi)) (fact ??begin)) </code>  
 * instead of 
 * <code> (D ??inst (since (fact ??phi) (fact ??begin))) </code>
 * 
 * Thus SinceFormulaInterpretation is triggered anyway
 * 
 * @author wdvh2120
 * @version 1.0 18 March 2008
 */
public class InstitutionalSinceFormulaInterpretation extends
		SemanticInterpretationPrinciple {

	private final static Formula since_fact_FORMULA_fact_BEGIN = 
		new PredicateNode(SL.symbol("since"),
			new ListOfTerm(new Term[] {
					new FactNode(new MetaFormulaReferenceNode("formula")),
					new FactNode(new MetaFormulaReferenceNode("begin"))
				}));
	
	public InstitutionalSinceFormulaInterpretation(InstitutionalCapabilities capabilities) {
		super(capabilities,"(D ??inst (since (fact ??formula) (fact ??begin)))",
				SemanticInterpretationPrincipleTable.SPLIT_FORMULA);
	}
	
	@Override
	public ArrayList apply(SemanticRepresentation sr)
			throws SemanticInterpretationPrincipleException {
		
		MatchResult applyResult = pattern.match(sr.getSLRepresentation());
		if (applyResult != null) {
			Term institution = applyResult.term("inst");
			Formula form = applyResult.formula("formula");
			Formula begin = applyResult.formula("begin");
			Formula futureFormula = (Formula)SL.instantiate(
				since_fact_FORMULA_fact_BEGIN,
				"formula",new InstitutionalFactNode(institution,form),
				"begin",begin
			);
			
			ArrayList resultNew = new ArrayList();
			resultNew.add(new SemanticRepresentation(futureFormula));
			// the initial sr is absorbed
			return resultNew;
			
		}
		return null;
	}

}


