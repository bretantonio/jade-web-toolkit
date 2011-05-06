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
 * Created by Carole Adam, 29 January 2008
 */

import jade.core.AID;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;


/**
 * This SIP is triggered during the interpretation of the file specifying 
 * institutional actions of a given institution, at the agent setup.
 * For each declared institutional action, it performs four actions:
 *  - it interprets the declarations of observing agents (predicate
 *  <code (is-observing ...) </code>)
 *  - if the interpreting agent is the institution in which this action
 *  is declared to be institutional, or its mediator, it interprets a
 *  predicate <code>(is-institutional ...)</code> to remember that the
 *  action is institutional in this institution (in order for the institution
 *  agent not to spy actions that do not concern it)
 *  - it interprets the generic power for any agent to commit on the 
 *  institutional precondition of this action by performing it. So the
 *  commitment of the author of an action on its institutional precondition 
 *  (that is, the institutional fact that he believes this precondition to be 
 *  true) will be deduced by InstitutionalActionDoneNew when he performs it.
 * 	- it installs the powers corresponding to the institutional effect specified
 * 	for this action (no more powers in institution.specif)
 *   
 * @author wdvh2120
 * @version 1.0 29 January 2008
 * @version 1.1 February 2008 - replaces InstitutionalActionBehaviour to allow
 * various institutional interpretations of the same ontological action.
 * @version 1.2 21 March 2008 - interprets the power of the actor to commit on 
 * the institutional precondition of his action
 * @version 1.3 31 March 2008 - powers are now specified as institutional effects
 * of actions in the actions specification file, this SIP installs the corresponding
 * powers (that are not specified anymore in the institution specification file)
 */
public class InstitutionalActionDeclaration extends SemanticInterpretationPrinciple {
	
	private static final MetaTermReferenceNode actorMetaRef = new MetaTermReferenceNode("_actor");
	public static final Formula until_fact_WHAT_fact_END = SL.formula("(until (fact ??what) (fact ??end))");

	private final boolean DEBUG = false;
	private final boolean NEW_DEBUG = false;
	private final boolean DDEBUG = false;
	
	// This SIP is triggered by the predicate institutional-action used in
	// the institution action setup file, interpreted at setup.
	public InstitutionalActionDeclaration(InstitutionalCapabilities capabilities) {
		super(capabilities,"(institutional-action ??inst ??pattern ??observers (fact ??precond) (fact ??effect))",SemanticInterpretationPrincipleTable.INSTITUTIONAL);
	}

	@Override
	public ArrayList apply(SemanticRepresentation sr)
	throws SemanticInterpretationPrincipleException {

		MatchResult applyResult = pattern.match(sr.getSLRepresentation());
		if (applyResult != null) {
			ArrayList resultNew = new ArrayList();
			try {
				InstitutionTools.printTraceMessage("InstActionDecl, pattern="+pattern,DEBUG);
				InstitutionTools.printTraceMessage("IAD, sr="+sr,DEBUG);
				InstitutionTools.printTraceMessage("IAD, applyResult="+applyResult,DEBUG);

				// Get all features of the institutional action just declared
				Constant institution = (Constant)applyResult.term("inst");
				Term actBodyPattern = applyResult.term("pattern");
				TermSetNode observers = (TermSetNode)applyResult.term("observers");
				Formula precondition = applyResult.formula("precond");
				Formula effect = applyResult.formula("effect");
				InstitutionTools.printTraceMessage("institution = "+institution,DEBUG);
				InstitutionTools.printTraceMessage("pattern of action = "+actBodyPattern,DEBUG);
				InstitutionTools.printTraceMessage("precond inst = "+precondition,DEBUG);
				InstitutionTools.printTraceMessage("observers = "+observers,DEBUG);
				InstitutionTools.printTraceMessage("effect inst = "+effect, DEBUG);

				/***************************************
				 * ACTION 1 : DECLARE OBSERVING AGENTS *
				 ***************************************/
				// If the list of observing agents is not empty, assert an instance of
				// (is-observing pattern) for each observing agent.
				// This predicate will be read by in InstitutionalActionDoneNew
				// when seeking the agents to inform of the performance of an action.
				for (int i=0;i<observers.size();i++) {
					addObservingAgent(observers.getTerm(i),actBodyPattern);
					// FIXME: this method immediately interprets the observing 
					// agent: should rather be added to resultNew ??
				}

				/**************************************************
				 * ACTION 2 : DECLARE THE ACTION AS INSTITUTIONAL *
				 * only for the institution agent or its mediator *
				 **************************************************/
				// Assert that this action is institutional in this institution
				// so that the institution and its mediator are aware of it.
				// This is used in the spying behaviour to only spy actions that
				// are relevant for the spying agent (unless it is in big brother mode)				
				Term institutionAgent = Tools.AID2Term(new AID(institution.stringValue(),AID.ISLOCALNAME));
				Term mediatorAgent = ((InstitutionalAgent)myCapabilities.getAgent()).getMediator(institution.stringValue());
				Term me = myCapabilities.getAgentName();
				
				InstitutionTools.printTraceMessage("sr="+sr, DEBUG);
				InstitutionTools.printTraceMessage("inst="+institutionAgent, DEBUG);
				InstitutionTools.printTraceMessage("mediator="+mediatorAgent, DEBUG);
				InstitutionTools.printTraceMessage("me="+me, DEBUG);
				
				if ((me.equals(institutionAgent)) || (me.equals(mediatorAgent))) {
					// If the interpreting agent is the concerned institution,
					// assert that this action is institutional for him
					Formula isInstitutional = new PredicateNode(
							SL.symbol("is-institutional"),
							new ListOfTerm(new Term[] {institution,actBodyPattern}));
					InstitutionTools.printTraceMessage(" --> "+me+" interprets "+isInstitutional,DEBUG);
					resultNew.add(new SemanticRepresentation(isInstitutional));
				}

				/*****************************************************
				 * ACTION 3 : MANAGE THE INSTITUTIONAL PRECONDITIONS *
				 *****************************************************/

				// If the precondition is not trivial (true) 
				if (!(precondition instanceof TrueNode)) {
					/* Build the actor's power to commit on this precondition 
					 * by performing the action. Use the specific variable 
					 * ??_actor (used as such in the action specification file).
					 * 
					 * It is better to use a power since consequences of a CountAs 
					 * are only deducible but are not interpreted, so they do not
					 * trigger any SIP ...
					 * 
					 * If the precondition is a temporary formula, the commitment
					 * should also be temporary
					 */
					InstitutionTools.printTraceMessage("IAD: precondition="+precondition, NEW_DEBUG);
					// if precondition is a temporary formula: specific encapsulation ...
					MatchResult mr = until_fact_WHAT_fact_END.match(precondition);
					Formula engagementOnPrecondition = null;
					if (mr != null) {
						Formula what = mr.formula("what");
						Formula end = mr.formula("end");
						// can be meta-term reference nodes, so it is not possible to extract the formula from the fact for now
						InstitutionTools.printTraceMessage("IAD, precondition is temporary, what="+what+"; end-validity="+end, NEW_DEBUG);
						engagementOnPrecondition = (Formula)SL.instantiate(
								until_fact_WHAT_fact_END,
								"what", new BelieveNode(new MetaTermReferenceNode("_actor"),what),
								"end", end);
						InstitutionTools.printTraceMessage("IAD (case1), engagement on precondition="+engagementOnPrecondition, NEW_DEBUG);
					}
					else {
						engagementOnPrecondition = 
							new BelieveNode(
									new MetaTermReferenceNode("_actor"),
									precondition);
						InstitutionTools.printTraceMessage("IAD (case2), engagement on precondition="+engagementOnPrecondition, NEW_DEBUG);
					}

					Formula powerToCommitOnPrecond = 
						new InstitutionalFactNode(institution,InstitutionTools.buildPower(
								new MetaTermReferenceNode("_actor"), 
								institution, 
								SL.TRUE, 
								actBodyPattern, 
								engagementOnPrecondition));
					powerToCommitOnPrecond = (Formula)SL.linkSameMetaReferences(powerToCommitOnPrecond);
					resultNew.add(new SemanticRepresentation(powerToCommitOnPrecond));
					InstitutionTools.printTraceMessage("IAD, power to commit="+powerToCommitOnPrecond, NEW_DEBUG);
				}
				// Here the old version posted an InstitutionalActionInterpretation SIP
				// In the new version all institutional actions are interpreted by 
				// InstitutionalActionDoneNew posted since the agent setup

				/**********************************************
				 * ACTION 4 : MANAGE THE INSTITUTIONAL EFFECT *
				 **********************************************/
				if (!(effect instanceof TrueNode)) {
					Formula powerToGroundEffect = 
						new InstitutionalFactNode(institution,
								InstitutionTools.buildPower(
										// any agent has this power if it meets the preconditions
										// use the same meta-ref as in the action specification file
										new MetaTermReferenceNode("_actor"),
										institution, 
										// the precondition
										precondition, 
										// the pattern of this action
										actBodyPattern, 
										// its specified institutional effect
										effect));
					// to link meta-references having the same name so that they are instantiated by queries
					powerToGroundEffect = (Formula)SL.linkSameMetaReferences(powerToGroundEffect);
					InstitutionTools.printTraceMessage("!!!!!!! --> power to ground effect = "+powerToGroundEffect, DDEBUG);
					resultNew.add(new SemanticRepresentation(powerToGroundEffect));				
				}
			}
			catch(ClassCastException cce) {
				System.err.println("a parameter of institutional-action has a wrong type");
				cce.printStackTrace();
			}

			/* Return the result built along the SIP
			 *  - it does NOT contain the original SR 
			 * (the action declaration is absorbed, it should not be asserted)
			 *  - for now it does not contain the declarations of observing agents
			 *  since they are interpreted immediately by the auxiliary method.
			 *  - it possibly contains the is-institutional predicate 
			 *  (if the interpreting agent is the institution or its mediator)
			 *  - it possibly contains the generic power for any agent to commit
			 *  on the institutional precondition of this action by performing it
			 *  (if the precondition is not just true) 
			 */
			return resultNew;
		}
		return null;
	}


	/*********************
	 * AUXILIARY METHODS *
	 *********************/

	// Auxiliary method to declare an observing agent
	private void addObservingAgent(Term agent, Term actBody) {
		Formula observing = new PredicateNode(SL.symbol("is_observing"),
				new ListOfTerm(new Term[] {agent,
						// the requested action
						new ActionExpressionNode(actorMetaRef, actBody)
				}));
		myCapabilities.interpret(observing);
	}


}
