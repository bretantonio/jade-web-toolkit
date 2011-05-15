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


package jade.semantics.ext.institutions.sips.planning;

/*
 * Class FutureObligationAnticipation.java
 * Created by Carole Adam, 2008
 */

import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP allows agents to anticipate their future obligations
 * by immediately adopting the intention to respect them.
 * 
 * @author wdvh2120
 * @version 1.0
 */
public class FutureObligationAnticipation extends ApplicationSpecificSIPAdapter {

	public FutureObligationAnticipation(InstitutionalCapabilities capabilities) {
		super(capabilities,"(since (fact (D ??inst (O (done (action ??myself ??action) true)))) (fact ??validity))");
	}

	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {
		if (applyResult != null) {
			// lazy agents do not anticipate
			boolean lazy = ((InstitutionalCapabilities)myCapabilities).isLazy();
			if (lazy) {
				return result;
			}
			// other agents
			// instead of waiting for the given delay before interpreting my obligation
			// interpret it at once (anticipate it to act at once)
			Formula myOblig = new InstitutionalFactNode(applyResult.term("inst"),
					new ObligationNode(new DoneNode(new ActionExpressionNode(
							myCapabilities.getAgentName(),applyResult.term("action")),SL.TRUE)));
			ArrayList anticipateResult = new ArrayList();
			anticipateResult.add(new SemanticRepresentation(myOblig));
			// absorb the future formula, replaced by an immediate one
			return anticipateResult;
		}
		return null;
	}

}
