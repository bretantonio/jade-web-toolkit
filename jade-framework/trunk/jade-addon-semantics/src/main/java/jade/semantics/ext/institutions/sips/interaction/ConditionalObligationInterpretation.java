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


package jade.semantics.ext.institutions.sips.interaction;

/*
 * Class ConditionalObligationInterpretation 
 * Created by Carole Adam, 30 June 2008
 */

import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.observers.EventCreationObserver;
import jade.semantics.kbase.observers.Observer;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.util.leap.ArrayList;


/**
 * This class allows agents to interpret their conditional obligations
 * as soon as they become valid (i.e. as soon as their condition becomes
 * true). Thus the corresponding behaviour is triggered by this 
 * interpretation: the agent acts as soon as possible to respect his 
 * obligation.
 * 
 * @author wdvh2120
 * @version 1.0 date 30 June 2008
 */
public class ConditionalObligationInterpretation extends
		ApplicationSpecificSIPAdapter {

	public ConditionalObligationInterpretation(InstitutionalCapabilities capabilities) {
		super(capabilities,
			// pattern = (condition -> D inst (O phi))
			new ImpliesNode(
				new MetaFormulaReferenceNode("condition"),
				new InstitutionalFactNode(
					new MetaTermReferenceNode("institution"),
					new ObligationNode(new MetaFormulaReferenceNode("phi"))
				)
			)
		);
	}
	
	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		if (applyResult != null) {
			
			// build the useful formulas
			Formula condition = applyResult.formula("condition");
			Formula obligation = new InstitutionalFactNode(
					applyResult.term("institution"),
					new ObligationNode(applyResult.formula("phi"))
				);
			
			// post an observer to force the interpretation of the
			// obligation as soon as the condition becomes true
			Observer obs = new EventCreationObserver(
					myCapabilities.getMyKBase(),
					condition,
					obligation,
					myCapabilities.getSemanticInterpreterBehaviour()
					);
			myCapabilities.getMyKBase().addObserver(obs);
			obs.update(null);
			
			// return neutral result (the implies formula is stored)
			return result;
		}
		return null;
	}

}
