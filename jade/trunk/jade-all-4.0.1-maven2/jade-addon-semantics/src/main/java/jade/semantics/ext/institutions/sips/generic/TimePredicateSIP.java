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
 * Class TimePredicateSIP
 * Created by Carole Adam, 20 March 2008
 */

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;


/**
 * This SIP allows time predicates to trigger the observers waiting
 * for their assertion in the KBase, but it immediately retracts
 * them after, in order not to flood the KBase with useless predicates
 * that could prevent the correct functioning of other observers waiting
 * for the same value later.
 * @see {@link TemporaryFormulaInterpretation} and {@link FutureFormulaInterpretation}
 * 
 * @author wdvh2120
 * @version 1.0 - 20 March 2008 
 */
public class TimePredicateSIP extends ApplicationSpecificSIPAdapter {

	public TimePredicateSIP(SemanticCapabilities capabilities) {
		super(capabilities,SL.formula("(time ??x)"));
	}
	
	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {
		if (applyResult != null) {
			Term x = applyResult.term("x");
			Formula f = SL.formula("(time ??x)").instantiate("x",x);
			
			// immediately assert the time predicate to trigger observers 
			myCapabilities.getMyKBase().assertFormula(f);
			// post the not node encapsulating it for later interpretation
			// to retract the time predicate once it has triggered observers			
			myCapabilities.getMyKBase().retractFormula(f);
			return new ArrayList();
			
		}
		return null;
	}

}
