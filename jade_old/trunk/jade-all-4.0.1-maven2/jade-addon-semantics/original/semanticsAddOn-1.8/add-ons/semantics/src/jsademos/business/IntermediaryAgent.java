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

import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/*
 * Created by Carole Adam, July 1st, 2008
 */



/**
 * This agent serves as an intermediary, a middle man, a third party
 * between two businesses having interoperability problems.
 * The mediating agents redirects these businesses towards the 
 * intermediary agent that fits their specific problem.
 * 
 * @author wdvh2120
 * @version 1.0, date July 1st, 2008
 */
public class IntermediaryAgent extends BusinessAgent {

	@Override
	public void setup() {
		super.setup(new IntermediaryCapabilities((String)getArguments()[0]));
	} // End of setup/0
	
	// redefine
	@Override
	public IntermediaryCapabilities getSemanticCapabilities() {
		SemanticCapabilities capab = super.getSemanticCapabilities();
		if (capab instanceof IntermediaryCapabilities) {
			return (IntermediaryCapabilities)capab;
		}
		System.err.println("WARNING : The capabilities "+capab+" should be an instance of IntermediaryCapabilities but it is a "+capab.getClass()+" !!!");
		throw new ClassCastException();
	}
	
	
}


class IntermediaryCapabilities extends BusinessCapabilities {
	
	private final static boolean DEBUG = false;
	
	/***************
	 * CONSTRUCTOR *
	 ***************/
	
	public IntermediaryCapabilities(String instName) {
		// not lazy, nor conscientious, nor trustless
		super(instName,false,false,false);
	}
	
	
	/***************
	 * KBASE SETUP *
	 ***************/
	
	@Override
	protected KBase setupKbase() {
		KBase kbase = super.setupKbase();
		Formula intermediaryCanPayAnytime = SL.formula("(can-pay "+getAgentName()+" ??orderReference)");
		kbase.assertFormula(intermediaryCanPayAnytime);
		return kbase;
	}
	
	
	/*****************
	 * SPECIFIC SIPS *
	 *****************/
	
	@Override
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();
		
		// reaction to documents
		table.addSemanticInterpretationPrinciple(new PurchaseOrderModificationAndForward(this));
		table.addSemanticInterpretationPrinciple(new ResponseModificationAndForward(this));
		table.addSemanticInterpretationPrinciple(new InvoiceManagement(this));
		table.addSemanticInterpretationPrinciple(new PaymentAdviceTransmission(this));
		table.addSemanticInterpretationPrinciple(new DispatchAdviceTransmission(this));
		table.addSemanticInterpretationPrinciple(new ReceiptAdviceReceptionTriggersInvoiceTransmission(this));
		
		// messages interception
		table.addSemanticInterpretationPrinciple(new ManageInterceptedOutgoingMessages(this));
		table.addSemanticInterpretationPrinciple(new ManageInterceptedIngoingMessages(this));
		
		return table;
	}
	
	// local attributes (initialised at the beginning of transaction
	private static Term supplier;
	private static Term customer;
	private static Term bank;
	
	
	/*******************************************
	 * SIP IN REACTION TO INTERCEPTED MESSAGES *
	 *******************************************/

	class ManageInterceptedOutgoingMessages extends ApplicationSpecificSIPAdapter {
		
		public ManageInterceptedOutgoingMessages(BusinessCapabilities capabilities) {
			super(capabilities,"(outgoingMessage ??sender ??receiver ??actionExpr)");
		}
		
		@Override
		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {
			if (applyResult != null) {
				System.err.println("intermediary intercepts outgoing message : "+applyResult);
			}
			return null;
		}
	}
	
	
	class ManageInterceptedIngoingMessages extends ApplicationSpecificSIPAdapter {
		
		public ManageInterceptedIngoingMessages(BusinessCapabilities capabilities) {
			super(capabilities,"(ingoingMessage ??sender ??receiver ??actionExpr)");
		}
		
		@Override
		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {
			if (applyResult != null) {
				System.err.println("intermediary intercepts ingoing message : "+applyResult);
			}
			return null;
		}
	}
	
	
	/********************************
	 * SIP IN REACTION TO DOCUMENTS *
	 ********************************/
	
	/* 
	 * SIP 1 : receive a delegation for a purchase order (with deliveryPeriod 
	 * starting at once and paymentPeriod starting after delivery)
	 *   - modifies the periods to be compatible
	 *   - store them for future reference, as well as the AID of customer and supplier
	 *   - forwards the modified purchase order to supplier
	 */ 
	class PurchaseOrderModificationAndForward extends ActionInResponseToDocumentReception {
		
		public PurchaseOrderModificationAndForward(InstitutionalCapabilities capab) {
			super(capab,BusinessFunctionalTerms.getDelegationPattern()); 
		}
		
		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term delegationTerm) {

			if (applyResult != null) {
				InstitutionTools.printTraceMessage("&& SIP !! intermediary: purchase order modification and forward", DEBUG);				
				Delegation delegationObject = new Delegation(delegationTerm);
				
				// extract the purchase order in order to modify it before forward
				Term purchaseOrderTerm = delegationObject.getPurchaseOrder();
				PurchaseOrder purchaseOrderObject = new PurchaseOrder(purchaseOrderTerm);
				
				//   --> build the new delivery period (FIXME: after payment ok, but eternal for now !)
				Term orderRef = purchaseOrderObject.getId();
				supplier = Tools.AID2Term(purchaseOrderObject.getSupplierAID());
				customer = Tools.AID2Term(purchaseOrderObject.getCustomerAID());
				
				System.err.println("INIT of supplier="+supplier);
				System.err.println("INIT of customer="+customer);
				
				// STORE the name of loaning bank (to be used in SIP3)
				bank = applyResult.term("loaningBank");
				Formula f = SL.formula("(bank "+customer+" "+bank+")");
				myKBase.assertFormula(f);
				
				Formula beginDelivery = (Formula)SL.instantiate(
									BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC,
									"sender",customer, 
									"receiver",supplier,
									"doc",SL.term("(paymentNote :orderReference "+orderRef+")"));
				purchaseOrderObject.setDeliveryPeriod(beginDelivery,SL.FALSE);
				
				//   --> build the new payment period: at once (FIXME: no deadline for now)
				purchaseOrderObject.setPaymentPeriod(SL.TRUE,SL.FALSE);
				
				// update the term
				Term newPurchaseOrderTerm = purchaseOrderObject.getPurchaseOrderTerm();
				
				// the intermediary's action = forward the modified purchase order to the supplier
				Term actionTerm = (Term) SL.instantiate(
											BusinessActionPatterns.sendPurchaseOrderPattern,
											"supplier",supplier, 
											"purchaseOrder",newPurchaseOrderTerm);
				return actionTerm;
			}
			return null;
		
		}
		
	}
	
	
	/*
	 * SIP 2 : receives the response from supplier
	 * simply forwards it, but modifies again the delivery and payment periods
	 * to match the original ones put by the BuyerAgent
	 */ 
	class ResponseModificationAndForward extends ActionInResponseToDocumentReception {
		
		public ResponseModificationAndForward(InstitutionalCapabilities capab) {
			super(capab,BusinessFunctionalTerms.getResponsePattern());  //,"customerAID","supplierAID"
		}
		
		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term responseTerm) {

			if (applyResult != null)  {
				
				InstitutionTools.printTraceMessage("&& SIP !! intermediary: response modification and forward", DEBUG);
				
				// directly transfer the response
				// (it only quotes the reference of order, so there is nothing to change)
				return (Term)SL.instantiate(BusinessActionPatterns.sendResponsePattern,
						"customer",customer,
						"response",responseTerm);
			}
			return null;
		}
		
	}
	
	
	/*
	 * SIP 3 : receives the invoice from supplier
	 *   - sends payment order to bank "in name of" buyer
	 */
	class InvoiceManagement extends ActionInResponseToDocumentReception {
		
		public InvoiceManagement(InstitutionalCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getInvoicePattern());
		}
		
		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term invoiceTerm) {
			if (applyResult!= null) {
				InstitutionTools.printTraceMessage("&& SIP !! intermediary in name of buyer: invoice management", DEBUG);
				// get the AID of the bank of the customer
				Formula f = SL.formula("(bank "+customer+" ??bank)");
				Term customerBank = myKBase.query(f).getResult(0).term("bank");
				
				// prepare the action term to return
				Term actionTerm = (Term)SL.instantiate(
						BusinessActionPatterns.sendPaymentOrderPattern,
						"bank",customerBank,
						"paymentOrder",buildPaymentOrderFromInvoice(invoiceTerm, Tools.term2AID(customerBank))
						);
				
				return actionTerm;
			}
			return null;
		}
		
	}
	
	
	/*
	 * SIP 4 : receives and forwards payment advice from customer to supplier
	 * PLUS : sends an ending message to mediator
	 */
	class PaymentAdviceTransmission extends ActionInResponseToDocumentReception {
		
		public PaymentAdviceTransmission(InstitutionalCapabilities capab) {
			super(capab,BusinessFunctionalTerms.getPaymentAdvicePattern());
		}
		
		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term paymentAdviceTerm) {
			if (applyResult != null)  {
				InstitutionTools.printTraceMessage("&& SIP !! intermediary: payment advice transmission", DEBUG);
				Term sender = applyResult.term("sender");
				
				/*************************************
				 * CASE 1 : PAYMENT ADVICE FROM BANK *
				 *   - forward it to supplier        *
				 *   - no ending message             *
				 *************************************/
				if (sender.equals(bank)) {
					// directly transfer the document
					Term actionTerm1 = (Term)SL.instantiate(BusinessActionPatterns.sendPaymentAdvicePattern,
							"supplier",supplier,
							"paymentAdvice",paymentAdviceTerm);
					return actionTerm1;
				}
				
				/**************************************
				 * CASE 2 : PAYMENT ADVICE FROM BUYER *
				 *   - no forward to supplier         *
				 *   - ending message to mediator     *
				 **************************************/
				if (sender.equals(customer)) {
					// TODO : give order reference to identify which transaction is over ?
					Term actionTerm2 = InstitutionTools.buildInformTerm(
						SL.formula("transaction-over"),
						getAgentName(),
						getMediator());
					myKBase.assertFormula(SL.formula("transaction-over"));
					return actionTerm2;
				}
				
				/****************
				 * CASE PROBLEM (DEBUG ONLY) *
				 ****************/
				System.err.println("case debug, sender="+sender+"\n customer="+customer+"\n bank="+bank);
			
			}
			return null;
		}
	}
	
	
	/*
	 * SIP 4 bis : receives and forwards dispatch advice from supplier to customer
	 */
	class DispatchAdviceTransmission extends ActionInResponseToDocumentReception {
		
		public DispatchAdviceTransmission(InstitutionalCapabilities capab) {
			super(capab,BusinessFunctionalTerms.getDispatchAdvicePattern());
		}
		
		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term dispatchAdviceTerm) {
			if (applyResult != null)  {
				InstitutionTools.printTraceMessage("&& SIP !! intermediary: dispatch advice transmission", DEBUG);
				// directly transfer the document
				Term t = (Term)SL.instantiate(BusinessActionPatterns.sendDispatchAdvicePattern,
						"customer",customer,
						"dispatchAdvice",dispatchAdviceTerm);
				return t;
			}
			return null;
		}
	}

	
	/*
	 * SIP 5 : receives the receipt advice forwarded by customer
	 * subsequently sends the corresponding invoice to this customer 
	 */
	class ReceiptAdviceReceptionTriggersInvoiceTransmission extends ActionInResponseToDocumentReception {
		
		public ReceiptAdviceReceptionTriggersInvoiceTransmission(InstitutionalCapabilities capab) {
			super(capab,BusinessFunctionalTerms.getReceiptAdvicePattern());
		}
		
		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term receiptAdviceTerm) {
			if (applyResult != null) {
				InstitutionTools.printTraceMessage("&& SIP !! intermediary: receipt advice triggers transmission of invoice", DEBUG);
				ReceiptAdvice receiptAdviceObject = new ReceiptAdvice(receiptAdviceTerm);
				StringConstantNode orderRef = receiptAdviceObject.getOrderReference();
				// get invoice in documents
				Term invoiceTerm = Document.getDocumentTermWithAttribute(
						"orderReference", orderRef, 
						BusinessFunctionalTerms.getInvoicePattern(), 
						(BusinessCapabilities)myCapabilities);
				
				TermSetNode actionTerm = new TermSetNode();
				// action 1 = send invoice to customer
				Term actionTerm1 = (Term)SL.instantiate(BusinessActionPatterns.sendInvoicePattern,
						"customer",customer,
						"invoice",invoiceTerm
						);
				// action 2 = forward receipt advice to supplier
				Term actionTerm2 = (Term)SL.instantiate(BusinessActionPatterns.sendReceiptAdvicePattern,
						"supplier",supplier,
						"receiptAdvice",receiptAdviceTerm);
				actionTerm.addTerm(actionTerm1);
				actionTerm.addTerm(actionTerm2);
				return actionTerm;
			}
			return null;
		}
		
	}
	
}//end intermediaryCapab
