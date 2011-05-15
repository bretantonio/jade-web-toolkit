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

/*
 * ObligationTransfer.java
 * Created on 19 November 2007
 * Author : Carole Adam
 */

package jade.semantics.ext.institutions.sips.planning;

/*
 * Class ObligationTransfer.java
 * Created by Carole Adam, November 19, 2007
 */

import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle expresses a principle of obedience 
 * to norms. An agent receiving an obligation adopts the intention to make this
 * obligation respected. 
 * 
 * This step is typically used to interpret the specification of an institution. 
 * 
 * This transfer can be cooperative (by default) or lazy: in this last case, the
 * agent only adopts intentions to respect his obligations when they have been 
 * explicitly notified to him (that is, when he has been informed about them).
 * When an obligation is "refused", it is stored in the KBase but no intention is
 * generated. Then when it is notified, the {@link ObligationNotofication} SIP 
 * forces a new interpretation of the obligation by this ObligationTransfer SIP,
 * and this time an intention will be generated. 
 * 
 * @author Carole Adam - France Telecom
 * @version Date: 2007/11/19 Revision: 1.0
 * @version Date: 2007/12/18 Revision 1.1 - added lazy parameter
 * @since JSA 1.5 
 */
public class ObligationTransfer extends ObligationTransferSIPAdapter {

	/*********************************
	 * *******   CONSTRUCTOR   *******
	 *********************************/

	/**
	 * Default constructor for this SIP.
	 * By default the transfer is not lazy, the obligation is transformed into
	 * an intention as soon as it is known (interpreted) by the agent.
	 * 
	 * @param capabilities the instance of InstitutionalCapabilities of the agent holding this SIP
	 */	
	public ObligationTransfer(InstitutionalCapabilities capabilities) {
		super(capabilities, 
				// catch only Obligations of the form (O done(??myself,action))
				new DoneNode(new ActionExpressionNode(
						capabilities.getAgentName(),
						new MetaTermReferenceNode("__action")),
						SL.TRUE),
						// imposed by any institution
						new MetaTermReferenceNode("__inst"));
	}


	/************************
	 * *** APPLY METHOD *** *
	 ************************/

	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.sips.adapters.ObligationTransferSIPAdapter#doApply(jade.semantics.lang.sl.tools.MatchResult, jade.semantics.lang.sl.tools.MatchResult, jade.util.leap.ArrayList, jade.util.leap.ArrayList, jade.semantics.interpreter.SemanticRepresentation)
	 */
	@Override
	protected ArrayList doApply(MatchResult matchObligation, MatchResult matchInstitution, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {

		if (!((InstitutionalCapabilities)myCapabilities).isLazy()) {
			/* cooperative transfer: any obligation concerning the agent 
			 * is transformed into an intention to respect it (to perform
			 * the corresponding action) 
			 */
			return acceptResult;
		}
		//else {
		/* lazy transfer: only explicitly notified obligations are 
		 * accepted as intentions. The other ones are refused: the 
		 * obligation itself is asserted but no intention is deduced
		 * from it so the agent engage in no action to respect his
		 * obligation (until he is explicitly notified of it).
		 */
		// check if the obligation was explicitly notified to the agent by some other agent
		ObligationNode obligNode = new ObligationNode(
				new DoneNode(
						new ActionExpressionNode(myCapabilities.getAgentName(),
								matchObligation.term("__action")),
								SL.TRUE));
		if (!((InstitutionalCapabilities)myCapabilities).isSuperLazy()) {
			if (((InstitutionalCapabilities)myCapabilities).wasNotified(obligNode, matchInstitution.term("__inst"))) {
				return acceptResult;
			}
			return refuseResult;
		}
		// super lazy mode: only consider obligations notified by the mediator
		if (((InstitutionalCapabilities)myCapabilities).wasNotifiedByMediator(obligNode, matchInstitution.term("__inst"))) {
			return acceptResult;
		}
		return refuseResult;
	}//end apply

}
