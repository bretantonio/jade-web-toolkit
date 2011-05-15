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


package jsademos.business;

/* 
 * Created by Carole Adam, 2008
 */

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.ActionDoneSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;


public class MediatingAgent extends BusinessAgent {
	
	public static final boolean SELLER_DELIVERS_AFTER_PAYMENT = true;
	public static final boolean BUYER_PAYS_AFTER_DELIVERY = true;
	
	@Override
	public void setup() {
		// boolean 1 = customer pays after delivery
		// boolean 2 = supplier delivers after payment
		super.setupMediator(new MediatingCapabilities(
								(String)getArguments()[0],
								BUYER_PAYS_AFTER_DELIVERY,
								SELLER_DELIVERS_AFTER_PAYMENT
							),
							(String)getArguments()[0]);
	} // End of setup/0

	// ------- redefined accessor to capabilities (avoid castings) -------
	@Override
	public MediatingCapabilities getSemanticCapabilities() {
		SemanticCapabilities capab = super.getSemanticCapabilities();
		if (capab instanceof MediatingCapabilities) {
			return (MediatingCapabilities)capab;
		}
		System.err.println("WARNING : The capabilities "+capab+" should be an instance of MediatingCapabilities but it is a "+capab.getClass()+" !!!");
		throw new ClassCastException();
	}
	
	// ------- accessors -------
	public boolean customerPaysAfterDelivery() {
		return getSemanticCapabilities().customerPaysAfterDelivery();
	}

	public boolean supplierDeliversAfterPayment() {
		return getSemanticCapabilities().supplierDeliversAfterPayment();
	}

	
}//end agent class

class MediatingCapabilities extends BusinessCapabilities {

	public static final boolean DEBUG = false;
	
	/**************
	 * ATTRIBUTES *
	 **************/

	private boolean customerPaysAfterDelivery;
	private boolean supplierDeliversAfterPayment;
	
	/***************
	 * CONSTRUCTOR *
	 ***************/

	public MediatingCapabilities(String instName,
			boolean customerPaysAfterDeliveryBool, 
			boolean supplierDeliversAfterPaymebtBool) {
		// not lazy, not conscientious, not trustless
		super(instName,false,false,false);
		customerPaysAfterDelivery = customerPaysAfterDeliveryBool;
		supplierDeliversAfterPayment = supplierDeliversAfterPaymebtBool;
	}


	/***************************
	 * ACCESSORS AND MODIFIERS *
	 ***************************/

	public boolean customerPaysAfterDelivery() {
		return customerPaysAfterDelivery;
	}

	public boolean supplierDeliversAfterPayment() {
		return supplierDeliversAfterPayment;
	}

	
	/***************
	 * SETUP KBASE *
	 ***************/
	
	@Override
	protected KBase setupKbase() {
		KBase kbase = super.setupKbase();
		
		// give mediator the names of the two business partners' banks (used in mediation)
		Formula bankPattern = SL.formula("(bank ??agent ??bank)");
		Formula buyerBank = (Formula)SL.instantiate(bankPattern,"agent",B2BInstitution.ENTERPRISE_A_AGENT,"bank",B2BInstitution.BANK_A_AGENT);
		Formula sellerBank = (Formula)SL.instantiate(bankPattern,"agent",B2BInstitution.ENTERPRISE_B_AGENT,"bank",B2BInstitution.BANK_B_AGENT);
		kbase.assertFormula(buyerBank); 
		kbase.assertFormula(sellerBank);
		
		return kbase;
	}
	
	
	/*******************
	 * SETUP SIP TABLE *
	 *******************/

	@Override
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();
		table.addSemanticInterpretationPrinciple(new SearchAppropriateStrategy(this));
		return table;
	}

	
	/***************
	 * SIP CLASSES *
	 ***************/
	
	// in reaction to a SIGNAL_PROBLEM action,
	// read the strategy corresponding to the type of problem signalled
	// in a specific strategies file, and apply it
	class SearchAppropriateStrategy extends ActionDoneSIPAdapter {
		
		public SearchAppropriateStrategy(InstitutionalCapabilities capab) {
			super(capab,
				// pattern = a complainant signalled a problem with an inform
				// prevent the mediator from reacting proactively when spying
				new ActionExpressionNode(
					new MetaTermReferenceNode("complainant"),
					InstitutionTools.buildInformTerm(
						new DoneNode(
							new ActionExpressionNode(
								new MetaTermReferenceNode("complainant"),
								BusinessActionPatterns.signalProblemPattern),
							SL.TRUE),
						new MetaTermReferenceNode("complainant"),
						getAgentName())));
		}
		
		@Override
		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {
			
			if (applyResult != null) {
				
				//extract the type of problem
				Term typeOfProb = applyResult.term("rejectionReason");
				Term rejectedOrder = applyResult.term("rejectedPurchaseOrder");
				
				// build the corresponding strategy formula
				Formula strategyPattern = SL.formula("(strategy ??problem ??order ??plan)");
				Formula correspStrategy = (Formula)SL.instantiate(strategyPattern,
																"problem",typeOfProb,
																"order",rejectedOrder);
				// query the strategy in the KBase
				InstitutionTools.printTraceMessage("queried strategy = "+correspStrategy,DEBUG);
				QueryResult qr = myKBase.query(correspStrategy);
				InstitutionTools.printTraceMessage("&&&&&&& qr strategy = "+qr,DEBUG);
				Term myPlan = InstitutionTools.getStrategyPlan(myKBase, typeOfProb, rejectedOrder);
				InstitutionTools.printTraceMessage("myPlan="+myPlan,DEBUG);
				if (myPlan!=null) {
					Term plan = 
						myPlan;
					InstitutionTools.printTraceMessage("&&&&&&& plan = "+plan,DEBUG);
					
					// instantiate the plan from applyResult
					plan = (Term)SL.instantiate(plan,applyResult);
					InstitutionTools.printTraceMessage("instantiated plan = "+plan,DEBUG);
					
					// intend to perform it
					result.add(new SemanticRepresentation(
							new IntentionNode(
									getAgentName(),
									new DoneNode(plan,SL.TRUE))));
					return result;
				}
				InstitutionTools.printTraceMessage("no strategy",DEBUG);
				myKBase.assertFormula(SL.formula("no-available-strategy"));
				Term complainant = applyResult.term("complainant");
				Formula intendToInformNoStrategy = InstitutionTools.buildIntendToInform(SL.formula("no-available-strategy"), getAgentName(), complainant);
				result.add(new SemanticRepresentation(intendToInformNoStrategy));
				return result;
			}
			return null;
		}
		
	}
	
}//end capabilities class	


