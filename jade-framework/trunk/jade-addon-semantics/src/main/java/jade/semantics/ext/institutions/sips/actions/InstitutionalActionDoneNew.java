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

package jade.semantics.ext.institutions.sips.actions;

/*
 * Created by Carole Adam, February 18, 2008 
 * Replaces three old SIPs: InstitutionalActionDoneSIP, 
 * InstitutionalActionInterpretation, and InstitutionalPowerSIP
 */

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ActionDoneSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This static (added at setup) SIP is triggered when any action is performed.
 * It then makes a number of verifications:
 *   - if this action is the procedure of a power, it checks its condition
 *   and if true it asserts its institutional effect; if the agent performing
 *   this procedure is conscientious, it also makes him observe the performance
 *   of the actions that he made obliged (by asserting him as an observer of
 *   such actions; the corresponding ObligationRespectObserver will only be
 *   posted in ObligationInterpretation SIP centralising all obligations 
 *   whatever their source)
 *   - if the interpreting agent is the author of the action it informs other
 *   agents (interested, concerned, observing) as well as all institutions he
 *   belongs to (each institution only considers relevant information, unless
 *   it is in "big brother" mode), and their mediators.
 *   (FIXME: should the mediators be informed by their institution instead of by agents ?)
 *   
 * NOTE: this InstitutionalActionDoneNew SIP gathers the code that 
 * was previously performed in InstitutionalActionBehaviour (inform
 * relevant agents of the performance of an institutional action), 
 * in order to allow various agents belonging to various institutions 
 * to make various institutional interpretations of the same physical 
 * action. It also gathers the code from various old SIPs that were removed: 
 * InstitutionalActionDoneSIP and InstitutionalActionInterpretation.
 *  
 * @author wdvh2120
 * @version 1.0, 20 February 2008
 */
public class InstitutionalActionDoneNew extends ActionDoneSIPAdapter {

	private final boolean DEBUG = false;
	private final boolean INFO_DEBUG = false;
	private static final boolean ADD_DEBUG = false;
	
	public static final Formula d_INSTITUTION_power_AGENT_INSTITUTION_fact_CONDITION_PROCEDURE_fact_EFFECT =
		SL.formula("(D ??institution (power ??agent ??institution (fact ??condition) ??procedure (fact ??effect)))");
	
	// to avoid being obliged to cast myCapabilities each time
	private InstitutionalCapabilities myICapabilities;
	
	public InstitutionalActionDoneNew(InstitutionalCapabilities capabilities) {
		super(capabilities,new ActionExpressionNode(
				new MetaTermReferenceNode("agent"),
				new MetaTermReferenceNode("action")));
		myICapabilities = capabilities;
	}
	
	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		if (applyResult != null) {
			ArrayList resultNew = (ArrayList)result.clone();
			
			// extract features of the action expression observed
			Term authorOfAction = applyResult.term("agent");
			Term observedAction = applyResult.term("action");

			// ********************************************************************* //
			// CASE 0 : PERFORMED BY ALL AGENTS : DEDUCE THE EFFECTS OF VALID POWERS //
			// ********************************************************************* //
			// copy from InstitutionalActionDoneSIP
			
			// Get all powers whose procedure is the action just observed
			Formula powerPattern = null;
			powerPattern = (Formula)SL.instantiate(d_INSTITUTION_power_AGENT_INSTITUTION_fact_CONDITION_PROCEDURE_fact_EFFECT,
					"agent",authorOfAction,
					"procedure",observedAction);
			QueryResult queryPowers = myICapabilities.getMyKBase().query(powerPattern);
			
			InstitutionTools.printTraceMessage("*** InstitutionalActionDoneNew by agent "+myICapabilities.getAgentName()+" ***", DEBUG);
			InstitutionTools.printTraceMessage("*** power pattern = "+powerPattern,DEBUG);
			
			// DEBUG ONLY
			if (queryPowers == null) {
				InstitutionTools.printTraceMessage("*** --> queryPowers = "+queryPowers,DEBUG);
			} // END DEBUG
			
			// If there exist powers whose procedure is the action observed
			else { 
				InstitutionTools.printTraceMessage("*** --> queryPowers (size="+queryPowers.size()+") = "+queryPowers,DEBUG);
				
				// scan all these powers
				for (int i=0; i<queryPowers.size();i++) {  // cannot be KNOWN so size() works
					MatchResult mr = queryPowers.getResult(i);
					InstitutionTools.printTraceMessage("  @ !!! qr("+i+")=mr="+mr,DEBUG);
					
					// instantiate powerPattern from this result
					Formula onePower = (Formula)SL.instantiate(powerPattern, mr);
					InstitutionTools.printTraceMessage("  @ power("+i+")="+onePower,DEBUG);
					
					// Extract the features of the current power (institution, condition, effect...)
					// (these features match the observedFeatures in the code copied from InstActionDoneSIP)
					Term oneInstitution = mr.term("institution");
					Formula oneCondition = mr.formula("condition");
					Formula oneEffect = mr.formula("effect");
					InstitutionTools.printTraceMessage("  @ institution("+i+")="+oneInstitution,DEBUG);
					InstitutionTools.printTraceMessage("  @ condition("+i+")="+oneCondition,DEBUG);
					InstitutionTools.printTraceMessage("  @ effect("+i+")="+oneEffect,DEBUG);
					
					Formula instantiatedCondition = (Formula)SL.instantiate(oneCondition, applyResult);
					// instantiate the condition of the power with the parameters 
					// of the action observed (contained in applyResult)
					InstitutionTools.printTraceMessage("  @ !!! instantiate condition from "+applyResult,DEBUG);
					InstitutionTools.printTraceMessage("  @ instantiated condition("+i+")="+instantiatedCondition,DEBUG);
					
					/* Then query the instantiated condition to the KBase
					 * (the precondition is PHYSICAL, not INSTITUTIONAL) 
					 * FIXME: if institution is not instantiated, the power is not valid:
					 */
					QueryResult queryPrecondition = myICapabilities.getMyKBase().query(instantiatedCondition);
					InstitutionTools.printTraceMessage("  @ query("+instantiatedCondition+")",DEBUG);
					InstitutionTools.printTraceMessage("  @ --> queryPrecond = "+queryPrecondition,DEBUG);

					// if the precondition of the power is valid
					if (queryPrecondition != null) {
						// first instantiate the effect from previous MatchResults: mr and applyResult
						Formula instantiatedEffect = (Formula)SL.instantiate(oneEffect, applyResult);
//						instantiatedEffect = (Formula)InstitutionTools.instantiateFromMatchResult(instantiatedEffect, mr);
						InstitutionTools.printTraceMessage("  @ first instantiate effect from "+applyResult,DEBUG);
						
						// If there is no additional information in queryPrecondition, no need to use it to further instantiate the effect
						if (queryPrecondition == QueryResult.KNOWN) { // thus size() would throw NullPointerException
							InstitutionalFactNode theInstitutionalEffect = new InstitutionalFactNode(oneInstitution,instantiatedEffect);
							InstitutionTools.printTraceMessage("  @ --> interpret unique effect ="+theInstitutionalEffect,DEBUG);
							// directly add it to the SR to interpret (result returned by this SIP)
							resultNew.add(new SemanticRepresentation(theInstitutionalEffect));
							
							// If the agent is conscientious, add observers to watch the respect of 
							// the obligations he possibly created by applying his power
							if (myICapabilities.isConscientious()) {
								if (authorOfAction.equals(myICapabilities.getAgentName())) {
									addAllNecessaryObservers(instantiatedEffect);
								}// end if I am the executive agent
							}//end if conscientious mode
						}
						// If there is additional information in this query result, use it to further 
						// instantiate the effect of the power (if several formulas match the precondition,
						// then there will be several effects to interpret)
						else if (queryPrecondition.size() >0) {
							// Interpret all possible instantiations of the effect
							// obtained from all formulas matching the precondition
							for (int j=0;j<queryPrecondition.size();j++) {
								InstitutionTools.printTraceMessage("  @ queryPrecond("+j+")="+queryPrecondition.getResult(j),DEBUG);
								Formula instantiatedEffectJ = (Formula)SL.instantiate(instantiatedEffect, queryPrecondition.getResult(j));
								InstitutionTools.printTraceMessage("  @ second instantiate effect from "+queryPrecondition.getResult(j),DEBUG);
								
								// Encapsulate the instantiated effect in an institutional fact of the institution 
								// giving this power, and add this institutional fact to the SR to interpret (returned by this SIP)
								InstitutionalFactNode theInstitutionalEffectJ = (InstitutionalFactNode)new InstitutionalFactNode(oneInstitution,instantiatedEffectJ).getSimplifiedFormula();
								InstitutionTools.printTraceMessage("  @ --> interpret effect("+j+")="+theInstitutionalEffectJ,DEBUG);
								resultNew.add(new SemanticRepresentation(theInstitutionalEffectJ));
								
								/* If the effect is an obligation for another agent to perform 
								 * an action, then the agent holding the power having created
								 * this obligation, if he is conscientious, will now watch if 
								 * this agent respects his obligation : post observers.
								 */
								if (myICapabilities.isConscientious()) {
									if (authorOfAction.equals(myICapabilities.getAgentName())) {
										addAllNecessaryObservers(instantiatedEffectJ);
									}// end if I am the executive agent
								}//end if conscientious mode
							}//end for all instantiations of precondition
						}//end if queryResult.size>0 (qr != KNOWN)
					}// end if condition is valid
				}
			}
			
			// *********************************************************************************************** //
			// CASE 1 : PERFORMED ONLY BY THE AUTHOR : INFORM RELEVANT AGENTS OF THE PERFORMANCE OF THE ACTION //
			// *********************************************************************************************** //
			// copy from InstitutionalActionInterpretation
			
			DoneNode doneAction = (DoneNode)((BelieveNode)sr.getSLRepresentation()).as_formula();
			ActionExpressionNode theAction = (ActionExpressionNode)doneAction.as_action();
			InstitutionTools.printTraceMessage("extract doneAction="+doneAction+";author="+authorOfAction, DEBUG);
			
			// CASE 1 : THE AGENT IS THE AUTHOR OF THE ACTION
			if (authorOfAction.equals(myICapabilities.getAgentName())) {
				
				InstitutionTools.printTraceMessage("I am the author of action, add inform behaviour", DEBUG);
				// ACTION 1: inform the relevant agents (previously done in InstitutionalActionBehaviour, removed since JSAinst v2)
				// Add the specific behaviour to inform everyone about the action
				potentiallyAddBehaviour(new InformAboutActionBehaviour(myICapabilities.getAgent(),theAction,doneAction));

				// ACTION 2: inform the institution and the mediator
				// scan all institutions (ClassCast OK since only InstitutionalAgents have this SIP)
				ArrayList listOfInstitutionsToInform = myICapabilities.getAgent().getAllInstitutions();
				// inform all these agents-institutions AND their mediators through O2A communication
				// FIXME: should the mediators be informed by authors of actions or by their institution ?
				for (int k=0;k<listOfInstitutionsToInform.size();k++) {
					String institutionK = listOfInstitutionsToInform.get(k).toString();
					InstitutionTools.informInstitutionAboutPhiThroughO2A(myICapabilities, institutionK, doneAction);
					InstitutionTools.informMediatorAboutPhiThroughO2A(myICapabilities, institutionK, doneAction);
					InstitutionTools.printTraceMessage("inform inst("+k+")="+institutionK+" that "+doneAction, DEBUG);
				}
			}
			
			// REMARK: a power was asserted by InstitutionalActionDeclaration so that
			// an agent performing an institutional action commits himself on its
			// institutional preconditions. No need to perform any check here, thus.
			
			return resultNew;
		}
		return null;
	}

	
	
	/******************************
	 * INTERNAL AUXILIARY METHODS *
	 ******************************/

	/*
	 * Auxiliary method used in the apply method.
	 * Decomposes the institutional effect to extract one or several 
	 * obligations created by the power, and make the agent observe 
	 * the respect of these obligations he created. 
	 * In the previous version this method was in InstitutionalActionDoneSIP.
	 */
	private void addAllNecessaryObservers(Formula instantiatedEffect) {
		if (instantiatedEffect instanceof AndNode) {
			ListOfFormula andLeaves = ((AndNode)instantiatedEffect).getLeaves();
			for (int i=0;i<andLeaves.size();i++) {
				addObserverForOneObligation((Formula) andLeaves.get(i));
			}
		}
		else {
			addObserverForOneObligation(instantiatedEffect);
		}
	}

	/*
	 * Auxiliary method for the auxiliary method.
	 * Make the executive agent of the power observe the respect of the obligations
	 * (to perform an action, only) he just created.
	 * Old version: added an ObligationRespectObserver for each one.
	 * New version: simply asserts that the executive agent is an observer 
	 * of the obliged action. The {@link ObligationRespectObserver} will be added
	 * by {@link ObligationInterpretation} (a SIP interpreting any obligation, 
	 * whatever its source, to centralise their management).
	 */
	private void addObserverForOneObligation(Formula instantiatedEffect) {
		if (instantiatedEffect instanceof ObligationNode) {
			Formula anObligation = ((ObligationNode)instantiatedEffect).as_formula();
			// if the created obligation is to perform an action
			if (anObligation instanceof DoneNode) {
				Term actionExpr = ((DoneNode)anObligation).as_action();
				if (actionExpr instanceof ActionExpression) {
					Term agent = ((ActionExpression)actionExpr).getActor();
					InstitutionTools.printTraceMessage("add observer for obligation="+anObligation,ADD_DEBUG);
					InstitutionTools.printTraceMessage("agent="+agent,ADD_DEBUG);
					InstitutionTools.printTraceMessage("capab.agent="+myICapabilities.getAgentName(),ADD_DEBUG);
					// AND if this obligation concerns another agent
					if (!agent.equals(myICapabilities.getAgentName())) {
						InstitutionTools.printTraceMessage("agents are different",ADD_DEBUG);

						/* 
						 * Make the agent having created the obligation watch its respect.
						 * Now the observer will be posted by ObligationInterpretation for
						 * all observing conscientious agents. Here, simply assert that the
						 * agent who created the obligation to perform an action observes
						 * this action ! (observes just one performance of this action) 
						 */
						myICapabilities.getAgent().setObservingOneTime((ActionExpression)actionExpr);
					}
				}
			}
		}
	}

	/*
	 * Auxiliary method used in the inform-behaviour to merge the lists 
	 * of concerned, interested and observing agents, while removing the 
	 * doubles in order not to inform the same agent twice.
	 * Was previously in InstitutionalActionInterpretation.
	 */
	private static ArrayList mergeListsOfAgents(ArrayList list1,ArrayList list2) {
		ArrayList result = list1;
		InstitutionTools.printTraceMessage(">>>> merging "+list1.toString()+" with "+list2.toString(),ADD_DEBUG);
		for (int i=0;i<list2.size();i++) {
			if (!containsAgent(result,(Term)list2.get(i))) {
				InstitutionTools.printTraceMessage("index of "+list2.get(i)+" in "+result.toString()+" is "+result.indexOf(list2.get(i)),ADD_DEBUG);
				result.add(list2.get(i));
			}
			else {
				InstitutionTools.printTraceMessage(">>> "+result.toString()+" already contains "+list2.get(i),ADD_DEBUG);
			}
		}
		return result;
	}

	// Redefinition of contains because ArrayList.contains seems not to call SLEqualizer
	// thus it does not work correctly on (agent-identifier) terms...
	// Other solution: use a list of names of agents (strings)
	public static boolean containsAgent(ArrayList listOfAgents, Term anAgent) {
		for (int i=0;i<listOfAgents.size();i++) {
			if (listOfAgents.get(i).toString().equals(anAgent.toString())) {
				return true;
			}
		}
		return false;
	}

	
	

	/***********************************************
	 * INTERNAL CLASS : behaviour to inform agents *
	 ***********************************************/

	class InformAboutActionBehaviour extends OneShotBehaviour {

		private DoneNode doneAction;
		private ActionExpressionNode theAction;

		public InformAboutActionBehaviour(Agent a, ActionExpressionNode actionExpr, DoneNode done) {
			super(a);
			doneAction = done;
			theAction = actionExpr;
		}

		@Override
		public void action() {
			InstitutionTools.printTraceMessage("ACTION !!! the action="+theAction,INFO_DEBUG);

			// Get the interested agents (is-interested pattern asserted)
			// NOTE: only InstitutionalAgents have this SIP so the class cast is OK
			ArrayList interestedAgents = myICapabilities.getAgent().getAndRemoveInterestedAgents(theAction);
			InstitutionTools.printTraceMessage("interested agents = "+interestedAgents.toString(),INFO_DEBUG);

			// Get the concerned agents (method to get agents who gain an obligation in the given institution)
			ArrayList concernedAgents = myICapabilities.getAgent().getConcernedAgents(theAction.as_term());
			InstitutionTools.printTraceMessage("concerned agents = "+concernedAgents.toString(),INFO_DEBUG);

			// Get the observing agents (is-observing pattern asserted)
			ArrayList observingAgents = myICapabilities.getAgent().getObservingAgents(theAction);
			InstitutionTools.printTraceMessage("observing agents = "+observingAgents.toString(),INFO_DEBUG);

			// Merge these lists of agents and remove the doubles
			// in order not to inform the same agent twice
			ArrayList agentsToInform2 = mergeListsOfAgents(concernedAgents,interestedAgents);
			agentsToInform2 = mergeListsOfAgents(agentsToInform2,observingAgents);
			InstitutionTools.printTraceMessage("## agents to inform = "+agentsToInform2.toString(),INFO_DEBUG);
			
			// Build the receivers of the inform
			TermSetNode agentsToInformTerm = new TermSetNode();
			for (int i=0;i<agentsToInform2.size();i++) {
				Object agent = agentsToInform2.get(i);
				if (agent instanceof Term) {
					agentsToInformTerm.addTerm((Term)agent);
				}
			}
			
			/* SEPARATELY inform all agents to avoid that they know about
			 * the other receivers of the inform.
			 * (Otherwise they interpret useless SRs that trigger failing 
			 * behaviours...: actually they interpret the sender's intention 
			 * that other receivers know about phi, then they adopt this 
			 * intention (through intention transfer), and thus they also 
			 * try to inform other receivers about phi...)
			 */
			for (int k=0;k<agentsToInformTerm.size();k++) {
				Formula intendToInformK = InstitutionTools.buildIntendToInform(doneAction,myICapabilities.getAgentName(),agentsToInformTerm.getTerm(k));
				myICapabilities.interpret(intendToInformK);
			}
		}//end action()
		
		/* TODO (later) observing agents should not be informed by the agent
		 * but should observe by themselves (on the physical environment)
		 * that the action occurred
		 */

	}//end internal class behaviour


}
