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


package jade.semantics.ext.institutions.sips.mediation;

/*
 * Class ComplaintManaging.java
 * Created by Carole Adam, 21 March 2008
 */

import jade.core.AID;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.ext.institutions.kbase.ObligationRespectObserver;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;


/**
 * This SIP is intended for mediators and institutions to handle
 * complaints received from agents, concerning their institution.
 * Checks to make in the apply method:
 *   - institution only handles complaints from its mediator; when 
 *   it receives complaints from another agent, it answers
 *   by requesting him to complain to the mediator
 *   - institution and mediator only handle complaints concerning
 *   obligations imposed in their own institution
 * 
 * It is now added only to mediator and institution in 
 * InstitutionalAgent.setupMediator() and setupInstitution()
 * 
 * @author wdvh2120
 * @version 1.0 21 March 2008 
 * 			updated 21 April 2008 (behaviour of institution)
 * 
 * TODO case of institution without mediator : what does the institution agent do of complaints ?
 */
public class ComplaintManaging extends ApplicationSpecificSIPAdapter {

	public static final Formula d_INSTITUTION_holder_AGENT_rolemediator = 
		SL.formula("(D ??institution (holder ??agent roleMediator))");

	public static final boolean DEBUG = false;

	public ComplaintManaging(InstitutionalCapabilities capabilities) {
		super(capabilities, 
				new DoneNode(
						new ActionExpressionNode(
								new MetaTermReferenceNode("complainant"),
								InstitutionTools.buildInformTerm(
										InstitutionTools.complainContent_INSTITUTION_COMPLAINANT_DEFENDANT_ACTION,
										// sender
										new MetaTermReferenceNode("complainant"),
										// receiver
										capabilities.getAgentName()
								)//end of inform term
						),
						SL.TRUE
				)
		);
	}

	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		if (applyResult != null) {
			InstitutionTools.printTraceMessage(myCapabilities.getAgentName()+" applies ComplaintManagingSIP", DEBUG);

			Term me = myCapabilities.getAgentName();
			InstitutionalAgent myself = ((InstitutionalAgent)myCapabilities.getAgent());

			// extract all useful features of the complaint
			Term complainant = applyResult.term("complainant");
			Term defendant = applyResult.term("defendant");
			Term institution = applyResult.term("institution");
			Term action = applyResult.term("action");
			String institutionName = institution.toString();
			// the mediator and the institution
			Term theMediator = myself.getMediator(institutionName);
			Term institutionAgent = Tools.AID2Term(new AID(institutionName,AID.ISLOCALNAME));
			InstitutionTools.printTraceMessage(" -> features: complainant="+complainant+"; defendant="+defendant+"; institution="+institution+"; action="+action+"; mediator="+theMediator, DEBUG);

			// re-build the reported obligation
			InstitutionalFactNode institutionalObligation = 
				new InstitutionalFactNode(institution,
						new ObligationNode(
								new DoneNode(
										new ActionExpressionNode(defendant,action),
										SL.TRUE)));

			/*********************
			 * CASE 1 : MEDIATOR *
			 *********************/
			if (me.equals(theMediator)) {
				InstitutionTools.printTraceMessage(" - case 1, I am the mediator:"+theMediator,DEBUG);

				// ** PREPARE the feedback to give to the complainant if his complaint is managed
				// (be it new or not) (no feedback if the reported obligation is false)
				// FIXME: the isManaging formula is built three times (to check it, to set it, and to inform about it)
				Formula managing = new PredicateNode(SL.symbol("is_managing"),
						new ListOfTerm(new Term[] {
								// the agent calling this method = the mediator
								me,
								// the given obligation
								new FactNode(institutionalObligation)}));
				IntentionNode intendToInformComplainant = InstitutionTools.buildIntendToInform(managing,me,complainant);


				/**********************************
				 * SUBCASE 1.1 : ALREADY MANAGING *
				 * do nothing special             *
				 **********************************/
				// check if the mediator is already managing this obligation
				if (myself.isManaging(institutionalObligation)) {
					InstitutionTools.printTraceMessage(" --- subcase 1.1 : Complaint received by mediator : "+myself+" is already managing "+institutionalObligation, DEBUG);
					// if he is, a feedback should be given to the complainant
					result.add(new SemanticRepresentation(intendToInformComplainant));
				}

				/********************************
				 * SUBCASE 2 : NOT MANAGING YET *
				 * post a new observer          *
				 ********************************/
				else {
					InstitutionTools.printTraceMessage(" --- case 1.2, manager is not managing yet "+institutionalObligation, DEBUG);
					// check the validity of the reported obligation
					// (the mediator's kbase is up to date : local check is enough)
					QueryResult qr = myCapabilities.getMyKBase().query(institutionalObligation);
					InstitutionTools.printTraceMessage(" --- local check if obligation is true -> qr="+qr, DEBUG);

					/* ******** SUBCASE 2.1 ******** //
					 * the complainant has reported  // 
					 * a false obligation : just     //
					 * signal it to him for now      //
					 * FIXME: no sanction ?          //
					 * ******** ******* *** ******** */
					if (qr == null) {
						InstitutionTools.printTraceMessage(" --- qr is null, automatic disconfirm", DEBUG);
						// no is_managing feedback to complainant
						// automatic disconfirm feedback
					}

					/* ******** SUBCASE 2.2 ******** //
					 * the obligation is true,       //
					 * manage it normally            //
					 * ******** ******* *** ******** */
					else {
						InstitutionTools.printTraceMessage("the obligation is true, manage it normally, add observer on: "+institutionalObligation, DEBUG);
						// otherwise starts managing the signalled problem
						myself.setManaging(institutionalObligation);
						// concretely: posts the adapted ObligationRespectObserver
						ObligationRespectObserver obs = new ObligationRespectObserver(
								myCapabilities.getMyKBase(),
								// delay before notification and denunciation
								3000,
								// number of notifications before denunciation to institution
								1,
								myCapabilities.getSemanticInterpreterBehaviour(),
								institutionalObligation,
								theMediator
						);
						InstitutionTools.printTraceMessage("ComplaintManaging adds ObligRespectObserver to "+theMediator+" to manage "+institutionalObligation, DEBUG);
						myCapabilities.getMyKBase().addObserver(obs);
						obs.update(null);
						// feedback to complainant
						result.add(new SemanticRepresentation(intendToInformComplainant));
					}
				}
			}

			/************************
			 * CASE 2 : INSTITUTION *
			 ************************/
			if (me.equals(institutionAgent)) {
				InstitutionTools.printTraceMessage("complaint received by institution !!! :"+institutionAgent,DEBUG);
				// should inform the complainant that complaints should be addressed to the
				// mediator, unless the agent precisely complains about the mediator himself

				System.err.println("complainant is "+complainant+" ; mediator is "+theMediator);
				// if there is a mediator, and he is not the complainant
				if ((theMediator != null)  && (!complainant.equals(theMediator))) { // warning use .equals to compare agent terms
					InstitutionTools.printTraceMessage("-> test says that complainant IS NOT the mediator",DEBUG);
					// check the validity of the reported obligation
					QueryResult qr = myCapabilities.getMyKBase().query(institutionalObligation);
					if (qr == null) {
						// do nothing, automatic disconfirm
						InstitutionTools.printTraceMessage(" --> false obligation",DEBUG);
					}
					else {
						InstitutionTools.printTraceMessage(" --> valid obligation",DEBUG);
						ActionExpression complainToMediator = new ActionExpressionNode(complainant,
								InstitutionTools.buildInformTerm(
										InstitutionTools.buildComplaintContent(institution,complainant, defendant, action),
										complainant,
										theMediator));
						Formula intendToRequestToComplainToMediator = 
							InstitutionTools.buildIntendToRequest(complainToMediator, me, complainant);
						result.add(new SemanticRepresentation(intendToRequestToComplainToMediator));
					}
				}

				/* 
				 * other cases:
				 *  - what if the mediator sends a complaint ?
				 *     -> institution should ask him to try a mediation
				 *        or ask him if a mediation was tried and failed
				 *  - what if there is no mediator ? 
				 */

			}

			/***************
			 * IN ANY CASE *
			 ***************/
			InstitutionTools.printTraceMessage(" finally result="+result, DEBUG);
			return result;
		}
		return null;
	}

}
