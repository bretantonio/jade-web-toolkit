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

import jade.core.AID;
import jade.semantics.interpreter.Tools;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

/*
 * created by Carole Adam, 27 August 2008
 */

/**
 * This class gathers pre-computed messages
 * interpreted by representative agents when receiving an UBL message
 * from a WSIG agent, since this simplified (for now) UBL message does
 * not contain the parameter of the message to send
 * These terms will be the content of the inform message interpreted
 * instead of the caught UBL message in UBLtranslation SIP in BusinessAgent
 */
public class ACLMessages {

	// enterpriseA
	public static Formula requestCatalogue = 
		SL.formula("(message (agent-identifier :name enterpriseB@test) (fact (done (action (agent-identifier :name buyer@test) (REQUEST_CATALOGUE :supplier (agent-identifier :name seller@test) :requestCatalogue (requestCatalogue :customerAID (agent-identifier :name buyer@test) :supplierAID (agent-identifier :name seller@test) :delayInDays 7) )) true)) )");

	public static Formula purchaseOrder =
		SL.formula("(message (agent-identifier :name enterpriseB@test) (fact (done (action (agent-identifier :name buyer@test) (SEND_PURCHASE_ORDER :orderReference \"\" :purchaseOrder (purchaseOrder :cancellationPeriod (period (fact false) (fact true)) :customerAID (agent-identifier :name buyer@test) :deliveryPeriod (period (fact true) (fact false)) :id \"PUR_ORD_0\" :issueDateTime 20080723T103229608 :orderLines (set (orderLine :itemReference \"PROD_0\" :catalogueReference \"CAT_0\" :itemName \"\" :itemDescription \"\" :quantity 2 :unitPrice 25.0 :lineNetAmount 50.0) (orderLine :itemReference \"PROD_1\" :catalogueReference \"CAT_0\" :itemName \"\" :itemDescription \"\" :quantity 1 :unitPrice 200.0 :lineNetAmount 200.0)) :paymentPeriod (period (fact (document (agent-identifier :name seller@test) (agent-identifier :name buyer@test) (deliveryNote :orderReference PUR_ORD_0))) (fact false)) :supplierAID (agent-identifier :name seller@test) :totalLine (totalLine :nbOfOrderLines 2 :totalNetAmount 250.0) :validityPeriod (period (fact true) (fact false))) :responseReference \"\" :supplier (agent-identifier :name seller@test))) true)) )");
	
	public static Formula receiptAdvice = 
		SL.formula("(message (agent-identifier :name intermediary@test) (fact (done (action (agent-identifier :name buyer@test) (SEND_RECEIPT_ADVICE :receiptAdvice (receiptAdvice :customerAID (agent-identifier :name buyer@test) :id \"REC_ADV_0\" :issueDateTime 20080724T101448491 :orderReference \"PUR_ORD_0\" :receiptDateTime 20080724T101441491 :receivedItems (set) :supplierAID (agent-identifier :name seller@test)) :supplier (agent-identifier :name intermediary@test))) true)) )");
	
	public static Formula paymentAdvice = 
		SL.formula("(message (agent-identifier :name intermediary@test) (fact (done (action (agent-identifier :name buyer@test) (SEND_PAYMENT_ADVICE :paymentAdvice (paymentAdvice :amount 299.0 :creditorAID (agent-identifier :name seller@test) :debtorAID (agent-identifier :name buyer@test) :id \"PAY_ADV_0\" :invoiceReference \"INV_0\" :issueDateTime 20080723T115702397 :paymentDateTime 20080723T115654620) :supplier (agent-identifier :name intermediary@test))) true)) )");
	
	//enterpriseB
	public static Formula sendCatalogue =
		SL.formula("(message (agent-identifier :name enterpriseA@test) (fact (done (action (agent-identifier :name seller@test) (SEND_CATALOGUE :catalogue (catalogue :catalogueContent (set (catalogueItem :id \"PROD_0\" :itemName \"phone\" :itemDescription \"phone\" :salesUnit \"\" :unitPrice 25.0) (catalogueItem :id \"PROD_1\" :itemName \"television\" :itemDescription \"television\" :salesUnit \"\" :unitPrice 200.0) (catalogueItem :id \"PROD_2\" :itemName \"computer\" :itemDescription \"computer\" :salesUnit \"\" :unitPrice 400.0) (catalogueItem :id \"PROD_3\" :itemName \"laptop\" :itemDescription \"laptop\" :salesUnit \"\" :unitPrice 1000.0)) :customerAID (agent-identifier :name buyer@test) :id \"CAT_0\" :issueDateTime 20080723T091612135 :supplierAID (agent-identifier :name seller@test) :validityPeriod (period (fact true) (fact false))) :customer (agent-identifier :name buyer@test))) true)) )");

	public static Formula acceptOrder = 
		SL.formula("(message (agent-identifier :name intermediary@test) (fact (done (action (agent-identifier :name seller@test) (SEND_RESPONSE :customer (agent-identifier :name intermediary@test) :response (response :acceptedIndicator \"accepted\" :customerAID (agent-identifier :name buyer@test) :id \"RESP_1\" :issueDateTime 20080723T115059742 :orderReference \"PUR_ORD_0\" :supplierAID (agent-identifier :name seller@test)))) true)) )");

	public static Formula invoice = 
		SL.formula("(message (agent-identifier :name intermediary@test) (fact (done (action (agent-identifier :name seller@test) (SEND_INVOICE :customer (agent-identifier :name intermediary@test) :invoice (invoice :customerAID (agent-identifier :name buyer@test) :dispatchAdviceReference \"\" :id \"INV_0\" :invoicedItems (set) :issueDateTime 20080702T165053091 :latestPaymentDateTime 20080702T165350091 :netAmount 250.0 :orderReference \"PUR_ORD_0\" :receiptAdviceReference \"\" :supplierAID (agent-identifier :name seller@test) :supplierBankAID (agent-identifier :name bankBagent@test) :vatAmount 49.0))) true)) )");
	
	public static Formula dispatchAdvice = 
		SL.formula("(message (agent-identifier :name intermediary@test) (fact (done (action (agent-identifier :name seller@test) (SEND_DISPATCH_ADVICE :customer (agent-identifier :name intermediary@test) :dispatchAdvice (dispatchAdvice :customerAID (agent-identifier :name buyer@test) :dispatchDateTime 20080724T095938470 :dispatchedItems (set) :expectedDeliveryDateTime 20080724T100235470 :id \"RESP_0\" :issueDateTime 20080724T095945470 :orderReference \"PUR_ORD_0\" :supplierAID (agent-identifier :name seller@test)))) true)) )");
	
	public static Formula getFormulaFromUBL(String actionUBL) {
		
		// enterpriseA
		if (actionUBL.equals("requestCatalogue")) return requestCatalogue;
		if (actionUBL.equals("purchaseOrder")) return purchaseOrder;
		if (actionUBL.equals("receiptAdvice")) return receiptAdvice;
		if (actionUBL.equals("paymentAdvice")) return paymentAdvice;
		
		// enterpriseB
		if (actionUBL.equals("catalogue")) return sendCatalogue;
		if (actionUBL.equals("acceptOrder")) return acceptOrder;
		if (actionUBL.equals("invoice")) return invoice;
		if (actionUBL.equals("dispatchAdvice")) return dispatchAdvice;
		
		return null;
		
	}
	
	
	// actionSymbol = toString applied to the Symbol of the functionalTermParamNode representing the action
	public static String getActionUBLparam(String actionSymbol) {
		if (actionSymbol.equals("SEND_CATALOGUE")) return "catalogue";
		if (actionSymbol.equals("REQUEST_CATALOGUE")) return "requestCatalogue";
		if (actionSymbol.equals("SEND_PURCHASE_ORDER")) return "purchaseOrder";
		if (actionSymbol.equals("QUERY_AGREEMENT")) return "queryAgreement";
		if (actionSymbol.equals("SEND_RESPONSE")) return "response";
		if (actionSymbol.equals("SEND_PAYMENT_ORDER")) return "paymentOrder";
		if (actionSymbol.equals("SEND_PAYMENT_ADVICE")) return "paymentAdvice";
		if (actionSymbol.equals("SEND_DISPATCH_ADVICE")) return "dispatchAdvice";
		if (actionSymbol.equals("SEND_INVOICE")) return "invoice";
		if (actionSymbol.equals("SEND_RECEIPT_ADVICE")) return "receiptAdvice";
		// default
		return "";
	}
	
	
	// get the name of the WSIG agent to whom the business agent should send 
	// the outgoing UBL message
	public static Term getWSIGagentName(String ublDocType, Term enterpriseAgent) {
		Term wsigAgent = Tools.AID2Term(new AID(ublDocType,AID.ISLOCALNAME));
		
		// default case (TO BE COMMENTED in WSIG version)
		System.err.println("should answer to "+wsigAgent+" instead of "+enterpriseAgent);
		if (true) return enterpriseAgent;
		// END DEFAULT CASE
		
		return wsigAgent;
	}
	
	
}
