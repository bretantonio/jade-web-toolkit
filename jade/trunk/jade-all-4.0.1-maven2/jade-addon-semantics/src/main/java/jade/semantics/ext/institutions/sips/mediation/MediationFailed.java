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
 * Class MediationFailed.java
 * Created by Carole Adam, March 2008
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP is only given to the institution agent.
 * It allows him to find and apply a sanction to a guilty agent
 * when the mediator signals that his mediation has failed.
 * 
 * @author wdvh2120
 * @version 1.0 March 2008 : sanctions file gives formulas to interpret
 * @version 1.1 21 April 2008 : sanctions file gives actions to perform,
 * 		actually it gives an intention formula to interpret (more generic, 
 * 		as in strategies file)
 */
public class MediationFailed extends ApplicationSpecificSIPAdapter {

	private final boolean DEBUG = false;
	private static int counterOfRefs = 0;
	
	public MediationFailed(InstitutionalCapabilities capabilities) {
		super(capabilities,SL.formula("(mediation-failed ??actions ??type)"));
	}
	
	@Override
	protected ArrayList doApply(MatchResult applyResult, ArrayList result,
			SemanticRepresentation sr) {

		if (applyResult != null) {
			System.out.println("institution detects a mediation failed !!!");

			// extract the features of the violation signalled by the mediator
			Term type = applyResult.term("type");
			TermSetNode actions = (TermSetNode)applyResult.term("actions");
			
			InstitutionTools.printTraceMessage("type of violation="+type,DEBUG);
			InstitutionTools.printTraceMessage("involved actions="+actions,DEBUG);

			// scan the involved actions and find a sanction for each one
			Formula holder = SL.formula("(holder ??agent ??role)");
			for (int i=0;i<actions.size();i++) {
				ActionExpressionNode actionExprI = (ActionExpressionNode)actions.getTerm(i);
				Term agenti = actionExprI.as_agent();
				Term actioni = actionExprI.as_term();
				
				InstitutionTools.printTraceMessage("agent i ="+agenti,DEBUG);
				InstitutionTools.printTraceMessage("action-i = "+actioni,DEBUG);
				
				// query the role(s) of this agent
				QueryResult qrh = myCapabilities.getMyKBase().query(holder.instantiate("agent",agenti));
				InstitutionTools.printTraceMessage("query "+holder.instantiate("agent",agenti)+" -> "+qrh, DEBUG);
				if (qrh != null) {
					// scan all roles and apply sanction for each one
					for (int j=0;j<qrh.size();j++) {
						Term roleij = qrh.getResult(j).term("role");
						InstitutionTools.printTraceMessage("role-ij = "+roleij, DEBUG);
						getAndApplySanction(agenti, roleij, actioni, type);
					}
				}
				else {
					Term rolei = SL.term("member");
					InstitutionTools.printTraceMessage("role-i = "+rolei,DEBUG);
					getAndApplySanction(agenti, rolei, actioni, type);
				}
				
			}
			return result;
		}
		return null;
	}


	// new version : interpret a formula without encapsulating it in an institutional fact
	public void getAndApplySanction(Term agent,Term role, Term action, Term type) {
		Formula sanctionFormula = new PredicateNode(SL.symbol("sanction"),
				new ListOfTerm(new Term[]{
					agent,
					role,
					action,
					type,
					new FactNode(new MetaFormulaReferenceNode("formula"))
				}));
		InstitutionTools.printTraceMessage("sanction formula = "+sanctionFormula,DEBUG);
		QueryResult qr = myCapabilities.getMyKBase().query(sanctionFormula);
		InstitutionTools.printTraceMessage("qr sanction = "+qr,DEBUG);
		// if a sanction is specified for this type of violation
		if (qr != null) {
			// apply all found sanctions
			for (int i=0;i<qr.size();i++) {
				// FIXME : there should be only one sanction, or possibly several ?
				Formula sanction = qr.getResult(i).formula("formula");
				
				// generate a unique ref for fine
				sanction = sanction.instantiate("_reference",SL.integer(new Long(counterOfRefs)));
				InstitutionTools.printTraceMessage("sanction="+sanction,DEBUG);
				myCapabilities.interpret(sanction);
				counterOfRefs +=1;
				
			}
		}
	}
	
}
