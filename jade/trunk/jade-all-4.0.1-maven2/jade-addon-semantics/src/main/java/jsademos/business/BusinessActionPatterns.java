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

import jade.semantics.actions.OntologicalAction;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

/*
 * Created by Carole Adam, friday 13 June 2008
 * to gather patterns of actions
 */

public class BusinessActionPatterns {
	// This class only gathers the patterns of actions.
	// The declaration of OntologicalActions is in BusinessAgent

	// receiver -> supplier
	public static final Term requestCataloguePattern = 
		SL.term("(REQUEST_CATALOGUE :supplier ??supplier " +
					":requestCatalogue ??requestCatalogue)");
	
	// receiver -> customer
	public static final Term sendCataloguePattern =
		SL.term("(SEND_CATALOGUE " +
					":customer ??customer " +
					":catalogue ??catalogue)");
	
	// receiver -> supplier
	public static final Term sendPurchaseOrderPattern =
		SL.term("(SEND_PURCHASE_ORDER " +
					":supplier ??supplier " +
					":purchaseOrder ??purchaseOrder " +
					"(::? :orderReference ??orderReference) " +
					"(::? :responseReference ??responseReference))");
	
	public static final Term cancelPurchaseOrderPattern =
		SL.term("(CANCEL_PURCHASE_ORDER " +
					":supplier ??supplier " +
					":cancellation ??cancellation)");

	public static final Term sendResponsePattern =
		SL.term("(SEND_RESPONSE " +
					":customer ??customer " +
					":response ??response)");
	
	public static final Term sendAcceptPattern =
		(Term)SL.instantiate(sendResponsePattern,
				"response",
				SL.instantiate(BusinessFunctionalTerms.getResponsePattern(),
						"acceptedIndicator",
						SL.string(Response.ACCEPTED)));

	public static final Term sendRefusePattern =
		(Term)SL.instantiate(sendResponsePattern,
				"response",
				SL.instantiate(BusinessFunctionalTerms.getResponsePattern(),
						"acceptedIndicator",
						SL.string(Response.REJECTED)));

	public static final Term sendInvoicePattern = 
		SL.term("(SEND_INVOICE " +
					":customer ??customer " +
					":invoice ??invoice)");
	
	public static final Term sendPaymentAdvicePattern = 
		SL.term("(SEND_PAYMENT_ADVICE " +
					":supplier ??supplier " +
					":paymentAdvice ??paymentAdvice)");
	
	public static final Term sendPaymentOrderPattern = 
		SL.term("(SEND_PAYMENT_ORDER " +
					":bank ??bank " +
					":paymentOrder ??paymentOrder)");
	
	public static final Term sendReceiptAdvicePattern =
		SL.term("(SEND_RECEIPT_ADVICE :supplier ??supplier :receiptAdvice ??receiptAdvice)");

	public static final Term sendDispatchAdvicePattern =
		SL.term("(SEND_DISPATCH_ADVICE :customer ??customer :dispatchAdvice ??dispatchAdvice)");

	// bank sends a physical proof of payment to its client (when he is the creditor)
	public static final Term sendPaymentNotePattern =
		SL.term("(SEND_PAYMENT_NOTE " +
					":creditor ??creditor " +
					":paymentNote ??paymentNote)");
	
	
	// DELIVERY and PAYMENT - "PHYSICAL" ACTIONS
	public static final Term deliverPackagePattern =
		SL.term("(DELIVER_PACKAGE " +
				":customer ??customer " +
				":deliveryNote ??deliveryNote " +
				":orderReference ??orderReference)");
	// The price can be retrieved in the purchase order referred to in the note
	// Obliged to repeat orderReference as a parameter of action in order for precondition to depend on it
	
	public static final Term payBankPattern =
		SL.term("(PAY_BANK " +
				":amount ??amount " +
				":debtorAgent ??debtorAgent " +
				":debtorBank ??debtorBank " +
				":creditorAgent ??creditorAgent " +
				":creditorBank ??creditorBank " +
				":paymentNote ??paymentNote)");
	// Payment note is the document generated by this action as a physical proof of payment.
	// Obliged to have this document in order to trigger SIPs waiting for effective payment.
	// Repeat the amount in direct parameters to have it available in precondition of PAY_BANK action.
	
	// MEDIATING ACTIONS
	
	// signal problem
	public static final Term signalProblemPattern = 
		SL.term("(SIGNAL_PROBLEM " +
					":mediator ??mediator " +
					":rejectedPurchaseOrder ??rejectedPurchaseOrder " +
					":rejectionReason ??rejectionReason)");

	// delegate power to purchase via a loaning bank
	public static final Term delegatePurchasePattern = 
		SL.term("(DELEGATE_PURCHASE " +
					":intermediary ??intermediary " +
					":purchaseOrder ??purchaseOrder " +
					":loaningBank ??loaningBank)");
	
	// query agreement
	public static final Term queryAgreementPattern = 
		SL.term("(QUERY_AGREEMENT :bank ??bank :queryAgreement ??queryAgreement)");
	
	// future actions:
	//  - mediator's answers ? (I manage, I have no strategy...)
	//  - mediator's request to the bank ASK_LOAN_AGREEMENT
	
}


class TestActionPatterns {
	public static final Term printPattern = 
		SL.term("(PRINT :content ??content)");
}


// generic SendDocument action class
class SendDocumentAction extends OntologicalAction {
	
	String typeOfDoc;
	String receiverOfDoc;
	
	/****************
	 * CONSTRUCTORS *
	 ****************/
	
	// constructor allowing to specify precondition
	public SendDocumentAction(SemanticCapabilities capabilities,
			Term actionPattern, String nameOfDoc,
			String nameOfReceiver,
			Formula precondition) {
		super(capabilities,actionPattern,
				// standard effect = generation of document
				(Formula)SL.instantiate(BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC,
						"sender",new MetaTermReferenceNode("actor"),
						"receiver",new MetaTermReferenceNode(nameOfReceiver),
						"doc",new MetaTermReferenceNode(nameOfDoc)),
				// specified precondition
				precondition);
		typeOfDoc = nameOfDoc;
		receiverOfDoc = nameOfReceiver;
	}
	
	// constructor with default true precondition
	public SendDocumentAction(SemanticCapabilities capabilities,
			Term actionPattern, String nameOfDoc,String nameOfReceiver) {
		this(capabilities,actionPattern,nameOfDoc,nameOfReceiver,
				// standard true precondition
				SL.TRUE);
	}
	
	
	/******************
	 * PERFORM METHOD *
	 ******************/
	
	// TODO : this perform method should be different in mediation mode !
	@Override
	public void perform(OntoActionBehaviour behaviour) {
		// observers are declared in the specification file of institution
		System.err.println("#### ACTION"+
				"\n  - "+getAuthor()+
				"\n  - sends document (type="+typeOfDoc+") : "+getActionParameter(typeOfDoc)+
				"\n  - to (type="+receiverOfDoc+") : "+getActionParameter(receiverOfDoc));
		System.err.println("");
		behaviour.setState(SemanticBehaviour.SUCCESS);
	}
	
	
}//end class SendDocumentAction
