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
 * ObligationNotification.java
 * Created on 18 December 2007
 * Author : Carole Adam
 */

package jade.semantics.ext.institutions.sips.interaction;

/*
 * Class ObligationNotification
 * Created by Carole Adam, 18 December 2007
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.ext.institutions.sips.planning.ObligationTransfer;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ActionDoneSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP interprets the notification by any agent of an obligation
 * concerning the agent, namely an obligation that the agent performs
 * some action.
 * When such a notification is interpreted, the apply method forces the
 * (re-)interpretation of the corresponding obligation, even if the agent
 * already knows it, because its interpretation by the {@link ObligationTransfer}
 * SIP will generate an intention to respect this obligation.
 * (Useful for example when a condition to respect the obligation was to be
 *
 * If the sender is the institution or its mediator, still inform him when the 
 * obligation is already reached or tried (instead of immediately adopting it)
 * 
 * STEPS:
 * 	1) assert is_interested
 *  2) check if already reached, inform sender
 *  3) check if is_trying, inform sender
 *  4) check if already believed -> reinterpret it (neutral)
 *  5) check if not believed (while not reached): several cases
 *      - if mediator or institution -> accept it
 *      - if other agent -> refuse it, inform sender
 * 
 * @author wdvh2120
 * @version date: 18 December 2007, revision: 1.0
 */

public class ObligationNotification extends ActionDoneSIPAdapter {
	
	public ObligationNotification(InstitutionalCapabilities capabilities) {
		super(capabilities,new ActionExpressionNode(
				new MetaTermReferenceNode("sender"),
				SL.term("(INFORM :sender ??sender :receiver (set ??receiver) :content (content (D ??institution (O (done (action ??receiver ??action) ??phi)))))")));
	}


	/**
	 * This method handles notifications of obligations depending on their sender
	 *   - always trust notification from the institution imposing the obligation
	 *   - never trust notifications from other agents unless already believes this obligation to be true
	 * TODO : check from the institution if obligations notified by other agents really hold
	 */
	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		if (applyResult != null) {
			ArrayList resultNew = (ArrayList)result.clone();
			
			Term sender = applyResult.term("sender");
			Term receiver = applyResult.term("receiver");
			Term me = myCapabilities.getAgentName();
			
			// only consider notifications from another agent to the self agent
			if ((!sender.equals(me)) &&	(receiver.equals(me))) {

				Term institution = applyResult.term("institution");
				Term action = applyResult.term("action");

				if (action != null) {			
					Formula doneAction = new DoneNode(
									new ActionExpressionNode(receiver,action),
									SL.TRUE);
					Formula obligationDoneAction = new InstitutionalFactNode(
							institution,
							new ObligationNode(doneAction));

					// IN ANY CASE - the notifier should be informed of the performance of the action
					// Assert this predicate storing the fact that the notifier is interested
					// in this action and should be informed of its performance whenever it occurs
					Formula interested = new PredicateNode(SL.symbol("is_interested"),
							new ListOfTerm(new Term[] {sender,
									// the requested action
									new ActionExpressionNode(receiver,action)
							}));
					myCapabilities.interpret(interested);
					
					// SPECIAL CASE : INSTITUTION - the sender is the institution
					// trust the institution and accept the obligation immediately
					if (Tools.term2AID(sender).getLocalName().equals(institution.toString())) {
						resultNew.add(new SemanticRepresentation(obligationDoneAction));
						return resultNew;
					}
					
					// SPECIAL CASE 2 : MEDIATOR - the sender is the mediator of the appropriate institution
					// also trust the mediator of this institution
					else if (((InstitutionalAgent)myCapabilities.getAgent()).believesThatMediatorIs(institution.toString(),sender)) {
						resultNew.add(new SemanticRepresentation(obligationDoneAction));
						return resultNew;
					}

					// NORMAL CASE : the sender is any other agent
					// do NOT trust him unless the self agent already believes this obligation to be valid
					else {
						// check if the receiver agent really believes that this obligation holds
						QueryResult qr = myCapabilities.getMyKBase().query(obligationDoneAction);

						// NORMAL CASE 1 : OBLIGATION NOT KNOWN
						// if this obligation does not hold in his KBase
						// either it has existed but was retracted when reached (subcase 1)
						// or it has never existed and should not be trusted (final subcase)
						if (qr==null) {
							// FIRST SUBCASE : THE OBLIGATION IS ALREADY RESPECTED 
							// (but the other agent did not know it)
							if (myCapabilities.getMyKBase().query(doneAction) != null) {
								// inform the notifier that the obligation is already respected
								resultNew.add(
									new SemanticRepresentation(
										InstitutionTools.buildIntendToInform(
												doneAction, me, sender)));
							}

							// FINAL SUBCASE : THE OBLIGATION IS NOT BELIEVED TO BE TRUE
							// do not trust any agent to give new obligations
							else {
								// TODO here : ask institution for confirmation ??
								// for now: just inform the sender that I do not believe this obligation to be valid
								// and do NOT interpret the notified obligation: do not accept it
								resultNew.add(new SemanticRepresentation(InstitutionTools.buildIntendToInform(new NotNode(new BelieveNode(me,obligationDoneAction)),me, sender)));
							}
						}
						
						// NORMAL CASE 2 : OBLIGATION ALREADY KNOWN
						// if this obligation already holds in the agent's KBase
						// either the agent is already trying (in this case intention was retracted): inform the requester
						// if not trying: re-interpret the obligation
						else {
							// SECOND SUBCASE : THE AGENT ALREADY HAS THE INTENTION (but is_trying, because precondition false)
							// build the is_trying to check it
							Formula istrying = InstitutionTools.buildIsTrying(me, institution, new ActionExpressionNode(me,action));
							if (myCapabilities.getMyKBase().query(istrying) != null) {
								resultNew.add(new SemanticRepresentation(InstitutionTools.buildIntendToInform(istrying, me, sender)));
							}

							// NORMAL SUBCASE
							else {
								// reinterpret it to trigger ObligationTransfer SIP that will create the
								// corresponding intention (in lazy mode the notification is needed...)
								resultNew.add(new SemanticRepresentation(obligationDoneAction));
							}
						}
					}
					return resultNew;
				}//end if action not null
			}
		}
		// in all other cases
		return null;
	}
}


