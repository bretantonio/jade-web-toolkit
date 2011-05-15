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
 * to replace TemporaryInstitutionalFormulaInterpretation
 */

import jade.semantics.ext.institutions.InstitutionTools;
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
 * This SIP allows to express temporary effects of powers in
 * inst.specif file. When interpreted these until-formulas
 * are encapsulated in an institutional fact: this SIP changes
 * the Ds(until-formula phi delay) into 
 * (until-formula (Ds phi) delay) to trigger the specific 
 * mechanism for the interpretation of until-formulas
 * (see UntilFormulaInterpretation SIP)
 * 
 * Replaces the previous TemporaryInstitutionalFormulaInterpretation
 * 
 * @author wdvh2120
 * @version date 10 April 2008
 */
public class InstitutionalUntilFormulaInterpretation extends
		SemanticInterpretationPrinciple {

	private final boolean DEBUG = false;
	
	private final static Formula until_fact_FORMULA_fact_END =
		new PredicateNode(SL.symbol("until"),
			new ListOfTerm(new Term[] {
				new FactNode(new MetaFormulaReferenceNode("formula")),
				new FactNode(new MetaFormulaReferenceNode("end"))
			}));
	
	public InstitutionalUntilFormulaInterpretation(InstitutionalCapabilities capabilities) {
		super(capabilities,"(D ??inst (until (fact ??formula) (fact ??end)))",
				SemanticInterpretationPrincipleTable.SPLIT_FORMULA);
	}
	
	@Override
	public ArrayList apply(SemanticRepresentation sr)
			throws SemanticInterpretationPrincipleException {
		
		MatchResult applyResult = pattern.match(sr.getSLRepresentation());
		if (applyResult != null) {
			Term institution = applyResult.term("inst");
			Formula form = applyResult.formula("formula");
			Formula end = applyResult.formula("end");
			Formula untilFormula = (Formula)SL.instantiate(
				until_fact_FORMULA_fact_END,
				"formula",new InstitutionalFactNode(institution,form),
				"end",end);
				
			ArrayList resultNew = new ArrayList();
			InstitutionTools.printTraceMessage("until institutional fact, interpret : "+untilFormula,DEBUG);
			resultNew.add(new SemanticRepresentation(untilFormula));

			// the initial sr is absorbed
			return resultNew;			
		}
		return null;
	}

}
