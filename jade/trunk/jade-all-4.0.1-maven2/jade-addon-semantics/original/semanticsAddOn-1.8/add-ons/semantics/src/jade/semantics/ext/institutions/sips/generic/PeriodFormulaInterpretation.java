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
 */

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP manages abbreviations (period ??phi ??begin ??end)
 * = (since (until ??phi ??end) ??begin)
 * @author wdvh2120
 *
 * TODO InstitutionalPeriodFormulaInterpretation
 * to inverse encapsulation in an InstitutionalFactNode
 */
public class PeriodFormulaInterpretation extends
		ApplicationSpecificSIPAdapter {

	private final static Formula until_PHI_END = SL.formula("(until ??phi ??end)");
	private final static Formula since_PHI_BEGIN = SL.formula("(since ??phi ??begin)");
	
	public PeriodFormulaInterpretation(SemanticCapabilities capabilities) {
		super(capabilities,"(period ??phi ??begin ??end)");
	}

	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		if (applyResult != null) {
			ArrayList newResult = new ArrayList();
			Term factPhi = applyResult.term("phi");
			Term factBegin = applyResult.term("begin");
			Term factEnd = applyResult.term("end");
			
			Formula sinceUntilPhiEndBegin = (Formula)SL.instantiate(
					since_PHI_BEGIN,
					"phi",new FactNode((Formula)SL.instantiate(
							until_PHI_END,
							"phi",factPhi,
							"end",factEnd)),
					"begin",factBegin);

			// absorb the period formula, return this one instead
			newResult.add(new SemanticRepresentation(sinceUntilPhiEndBegin));
			return newResult;
		}
		return null;
	}


}
