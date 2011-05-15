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

import jade.core.AID;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.util.Date;

public class SellerAgent extends RepresentativeAgent {

	public static final boolean DELIVERS_AFTER_PAYMENT = true;
	
	public void setup() {
		// boolean = can deliver after payment
		super.setup(new SellerCapabilities((String)getArguments()[0],DELIVERS_AFTER_PAYMENT));
	} // End of setup/0

	// redefine
	public SellerCapabilities getSemanticCapabilities() {
		SemanticCapabilities capab = super.getSemanticCapabilities();
		if (capab instanceof SellerCapabilities) {
			return (SellerCapabilities)capab;
		}
		System.err.println("WARNING : The capabilities "+capab+" should be an instance of SellerCapabilities but it is a "+capab.getClass()+" !!!");
		throw new ClassCastException();
	}
	
	public boolean deliversAfterPayment() {
		return getSemanticCapabilities().deliversAfterPayment();
	}
	
}//end seller agent


class SellerCapabilities extends BusinessCapabilities {

	private static final boolean DEBUG = false;
	
	/************
	 * PATTERNS *
	 ************/

	private static final Formula availabilityPattern = SL.formula("(availability ??item ??number)");
	
	/***************
	 * ID COUNTERS *
	 ***************/

	private static int catalogueIDcounter = 0; 
	private static int responseIDcounter = 0;
	private static int invoiceIDcounter = 0;
	private static int dispatchAdviceIDcounter = 0;
	private static int deliveryNoteIDcounter = 0;
	
	/**************
	 * ATTRIBUTES *
	 **************/
	
	private boolean deliversAfterPayment;
	
	/***************
	 * CONSTRUCTOR *
	 ***************/

	public SellerCapabilities(String instName,boolean deliversAfterPaymentBool) {
		// not lazy, nor conscientious, nor trustless
		super(instName,false,false,false);
		deliversAfterPayment = deliversAfterPaymentBool;
	}

	/*************
	 * ACCESSORS *
	 *************/
	
	public boolean deliversAfterPayment() {
		return deliversAfterPayment;
	}

	/*************************************
	 * USEFUL DOCUMENTS BUILDING METHODS *
	 *************************************/

	public Term buildCatalogue(Term receiver) {
		// prepare the catalogue term
		TermSetNode catalogueContent = new TermSetNode();

		// get the list of available items from the seller's kbase
		QueryResult qr = getMyKBase().query(SL.formula("(list-of-items ??list)"));
		if (qr != null) { 
			// there should be ONE and ONLY ONE result
			MatchResult mr = qr.getResult(0);
			Term tempList = mr.term("list");
			if (tempList instanceof TermSetNode) {
				TermSetNode list = (TermSetNode) tempList;
				// scan the list of items to build catalogueItems
				for (int i=0;i<list.size();i++) {
					// get the i-th item
					String item = list.getTerm(i).toString();
					// check its availability
					int number = getAvailability(item);	
					// if available, get its price
					if (number >0) {
						double price = getPreferredPrice(item);
						CatalogueItem catalogueItemObjectI = 
							new CatalogueItem(
									(StringConstantNode)SL.string("PROD_"+i),
									(StringConstantNode)SL.string(item),
									(StringConstantNode)SL.string(item),
									(RealConstantNode)SL.real(new Double(price))
									);
						Term catalogueItemTermI = catalogueItemObjectI.getCatalogueItemTerm();
						catalogueContent.addTerm(catalogueItemTermI); 
					}
				}
			}
			else {
				System.err.println("list of items should be a set !!!");
			}
		}

		// once the content is completed, build the catalogue object
		Catalogue catalogueObject = new Catalogue(
				(StringConstantNode)SL.string("CAT_"+catalogueIDcounter++),
				new DateTimeConstantNode(new Date(System.currentTimeMillis())),
				Tools.term2AID(receiver),
				Tools.term2AID(getAgentName()),
				Period.eternalPeriod(), 
				// note: use empirical duration instead of eternal period to see validity expire
				catalogueContent				
		);
		Term catalogueTerm = catalogueObject.getCatalogueTerm();
		
		return catalogueTerm;
	}

	// get availability of an item
	public int getAvailability(String item) {
		int nb=0;
		QueryResult qr = getMyKBase().query((Formula)SL.instantiate(availabilityPattern,"item",SL.string(item)));
		if (qr != null) { 
			// only get the first result (should be unique)  
			MatchResult mr = qr.getResult(0);
			nb = Integer.parseInt(mr.term("number").toString());
		}
		return nb;
	}

	// get preferredPrice of an item
	public int getPreferredPrice(String item) {
		// default empirical price = 77 ...
		int pp = 77;
		Formula preferredPricePattern = SL.formula("(preferred-price ??item ??price)");

		QueryResult qr = getMyKBase().query(preferredPricePattern.instantiate("item",SL.string(item)));
		if (qr != null) { 
			// only get the first result (should be unique)  
			MatchResult mr = qr.getResult(0);
			pp = Integer.parseInt(mr.term("price").toString());
		}
		return pp;
	}

	// method to build the response to a received purchaseOrder (matching ontology)
	public Term buildSimpleAcceptResponse(Term purchaseOrderTerm) {

		PurchaseOrder purchaseOrderObject = new PurchaseOrder(purchaseOrderTerm);
		
		// extract features
		StringConstantNode orderRef = purchaseOrderObject.getId(); 
		AID supplierAID = purchaseOrderObject.getSupplierAID(); 
		AID customerAID = purchaseOrderObject.getCustomerAID(); 

		// build the response
		Response responseObject = new SimpleAcceptResponse(
				(StringConstantNode)SL.string("RESP_"+responseIDcounter++),
				new DateTimeConstantNode(new Date(System.currentTimeMillis())),
				customerAID,
				supplierAID,
				orderRef
				);
		Term responseTerm = responseObject.getResponseTerm();

		return responseTerm;
	}
	
	// test method : build a rejection response
	public Term buildSimpleRejectResponse(Term purchaseOrderTerm,Formula reason) {

		PurchaseOrder purchaseOrderObject = new PurchaseOrder(purchaseOrderTerm);
		
		// extract features
		StringConstantNode orderRef = purchaseOrderObject.getId(); 
		AID supplierAID = purchaseOrderObject.getSupplierAID(); 
		AID customerAID = purchaseOrderObject.getCustomerAID(); 

		// build the response
		SimpleRejectResponse responseObject = new SimpleRejectResponse(
				(StringConstantNode)SL.string("RESP_"+responseIDcounter++),
				new DateTimeConstantNode(new Date(System.currentTimeMillis())),
				customerAID,
				supplierAID,
				orderRef,
				(StringConstantNode)SL.string(""+reason),
				new FactNode(reason));
		Term responseTerm = responseObject.getResponseTerm();

		return responseTerm;
	}


	// method to build the invoice corresponding to a response and an order
	public Term buildInvoiceAfterResponse(Term orderTerm, Term responseTerm) {
		
		// build the corresponding objects, easier to manipulate
		Response responseObject = Response.buildAppropriateTypeOfAnswer(responseTerm);
		PurchaseOrder orderObject = new PurchaseOrder(orderTerm);
		String indic = (responseObject.getAcceptedIndicator()).stringValue();
		Invoice invoice;
		if (indic.equals(Response.ACCEPTED)) {
			// get the total line
			TotalLine tl = orderObject.getTotalLine();
			double price = tl.getTotalNetAmount().realValue();
			double vat = VAT_RATE*price;
				
			invoice = new Invoice(
					(StringConstantNode)SL.string("INV_"+invoiceIDcounter++),
					new DateTimeConstantNode(new Date(System.currentTimeMillis())),
					orderObject.getCustomerAID(),
					orderObject.getSupplierAID(),
					getBank(),
					// default delay = 1 month = 2592000000 ms - for now 3 mn
					new DateTimeConstantNode(new Date(System.currentTimeMillis()+177000)),
					orderObject.getId(),
					(RealConstantNode)SL.real(price),
					(RealConstantNode)SL.real(vat),
					// receipt and dispatch advice references are empty
					(StringConstantNode)SL.string(""),
					(StringConstantNode)SL.string(""));
		}
		//else if (indic.equals(Response.MODIFIED)) {
			//invoice = null; 
		//}
		else {invoice=null;}
		// TODO: manage modified and rejected indicators
		
		// finally
		if (invoice != null) {
			Term invoiceTerm = invoice.getInvoiceTerm();
			return invoiceTerm;
		}
		// no invoice should be sent in case of rejected order for example
		return null;
	}
	
	// TODO: other methods to build an invoice from an order and (instead of response) either a receipt advice or a dispatch advice
	
	public Term buildDeliveryNoteFromPaymentAdvice(Term paymentAdviceTerm) {
	
		PaymentAdvice paymentAdviceObject = new PaymentAdvice(paymentAdviceTerm);
		
		Term invoiceTerm = Document.getReferredDocument(
				paymentAdviceObject.getInvoiceReference(), 
				BusinessFunctionalTerms.getInvoicePattern(),
				this);
		Invoice invoiceObj = new Invoice(invoiceTerm);
		StringConstantNode orderRef = invoiceObj.getOrderReference();
		
		Term orderTerm = Document.getReferredDocument(
				orderRef, 
				BusinessFunctionalTerms.getPurchaseOrderPattern(),
				this);
		PurchaseOrder orderObj = new PurchaseOrder(orderTerm);
		TermSetNode listItems = orderObj.getOrderLines();
		
		DeliveryNote deliveryNoteObject = new DeliveryNote(
				(StringConstantNode)SL.string("DELIV_"+deliveryNoteIDcounter++),
				new DateTimeConstantNode(new Date(System.currentTimeMillis())),
				paymentAdviceObject.getDebtorAID(),
				paymentAdviceObject.getCreditorAID(),
				orderRef,
				listItems);
		return deliveryNoteObject.getDeliveryNoteTerm();
		
	}
		
	
	public Term buildFullDispatchAdviceFromDeliveryNote(Term deliveryNoteTerm) {
		
		DeliveryNote deliveryNoteObject = new DeliveryNote(deliveryNoteTerm);
		
		StringConstantNode orderRef = deliveryNoteObject.getOrderReference();
		DispatchAdvice dispatchAdviceObject = 
			new DispatchAdvice(
					(StringConstantNode)SL.string("RESP_"+dispatchAdviceIDcounter++),
					new DateTimeConstantNode(new Date(System.currentTimeMillis())),
					deliveryNoteObject.getCustomerAID(),
					deliveryNoteObject.getSupplierAID(),
					orderRef,
					// suppose that dispatch was done 7s before date
					new DateTimeConstantNode(new Date(System.currentTimeMillis()-7000))
					// do not give list of dispatched items (all dispatched)
					// do not give expected delivery date time (default)
					);
		return dispatchAdviceObject.getDispatchAdviceTerm();
	}
	// TODO : methods to build partial dispatch advices
	
	
	/* other methods : TODO
	 *
	 * remove an article from catalogue = inform clients who received the catalogue
	 * that an article in this catalogue has run out of stock
	 *
	 * add an article : inform the client
	 *
	 * modify a price : inform the client (must be justified if a catalogue is still valid)
	 *
	 * remark: the seller must know (via the predicate (price ??seller ??buyer ??item ??price))
	 * the list of agents towards whom he is committed on an item's price and availability
	 */


	/**************************
	 * INITIAL KNOWLEDGE BASE *
	 **************************/

	// add beliefs about the seller's initial catalogue and preferred prices
	protected KBase setupKbase() {
		KBase kbase = super.setupKbase();
		// assert list-of-items
		kbase.assertFormula(SL.formula("(list-of-items (set phone television computer laptop))"));
		kbase = addPreferredPrice(kbase,"phone",25);
		kbase = addAvailability(kbase,"phone",100);
		kbase = addPreferredPrice(kbase,"television",200);
		kbase = addAvailability(kbase,"television",100);
		kbase = addPreferredPrice(kbase,"computer",400);
		kbase = addAvailability(kbase,"computer",100);
		kbase = addPreferredPrice(kbase,"laptop",1000);
		kbase = addAvailability(kbase,"laptop",2);

		// personal bank
		kbase.assertFormula(SL.formula("(myBank "+B2BInstitution.BANK_B_AGENT+")"));	

		return kbase;
	}

	public KBase addPreferredPrice(KBase kbase, String item, int price) {
		Formula preferredPricePattern = SL.formula("(preferred-price ??item ??price)");
		kbase.assertFormula((Formula)SL.instantiate(preferredPricePattern,
				"item",SL.string(item),
				"price",new IntegerConstantNode(new Long(price)))
		);
		return kbase;
	}

	public KBase addAvailability(KBase kbase, String item, int number) {
		Formula availabilityPattern = SL.formula("(availability ??item ??number)");
		kbase.assertFormula((Formula)SL.instantiate(availabilityPattern,
				"item",SL.string(item),
				"number",new IntegerConstantNode(new Long(number)))
		);
		return kbase;
	}

	public boolean mediation = false;
	
	/**************************************
	 * SEMANTIC INTERPRETATION PRINCIPLES *
	 **************************************/

	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();
		
		table.addSemanticInterpretationPrinciple(new HandleMessagesFromEnterprise(this));
		
		// transmission of info to represented enterprise
		// forward the request of a catalogue
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.requestCataloguePattern),SL.TRUE)));
		
		// forward the payment advice (the enterprise physically delivers, out of the platform)
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.sendPaymentAdvicePattern),SL.TRUE)));
		
		// forward the payment note from the bank
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.sendPaymentNotePattern),SL.TRUE)));
	
		// useless to forward receipt advice - the supplier does not care, once he is paid
		
		// SIP to determine what to do when receiving a purchase order
		// if acceptable, forward it
		// otherwise, directly answer a refusal
		table.addSemanticInterpretationPrinciple(new AutomaticallyManagePurchaseOrders(this));
		
		return table;
	}

	
	class AutomaticallyManagePurchaseOrders extends ApplicationSpecificSIPAdapter {
		
		public AutomaticallyManagePurchaseOrders(BusinessCapabilities capabilities) {
			// pattern = done(inform(done(sendPurchaseOrder...)))  (as for ForwardMessage)
			super(capabilities,new DoneNode(
						new ActionExpressionNode(
							new MetaTermReferenceNode("sender"), // possibly intermediary
							InstitutionTools.buildInformTerm(
								new DoneNode(
									new ActionExpressionNode(
										new MetaTermReferenceNode("customer"), // possibly buyer
										BusinessActionPatterns.sendPurchaseOrderPattern
									),
									SL.TRUE
								),
								new MetaTermReferenceNode("sender"), 
								getAgentName())),
						new TrueNode()));
		}
		
		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {
			// copy from old PurchaseOrderReceptionTriggersResponse
			if (applyResult != null) {
				Term purchaseOrderTerm = applyResult.term("purchaseOrder");

				// get the involved agents
				PurchaseOrder purchaseOrderObject = new PurchaseOrder(purchaseOrderTerm);
				Term supplier = Tools.AID2Term(purchaseOrderObject.getSupplierAID());
				Term customer = Tools.AID2Term(purchaseOrderObject.getCustomerAID());  // applyResult.term("customerAID");
				
				// decide to accept or reject order depending on specified period and own IS
				// if no constraint on sequence of action, accept the order
				boolean accept = true;
				if (deliversAfterPayment) {
					// get the delivery and payment Periods
					Period deliveryPeriod = purchaseOrderObject.getDeliveryPeriod();
					Period paymentPeriod = purchaseOrderObject.getPaymentPeriod();
					// get the order reference
					String orderRef = purchaseOrderObject.getId().stringValue();
					
					// payment period should start at once ?? (i.e. no condition before payment)
					if (paymentPeriod.getBegin().match(SL.TRUE) == null) {
						// TODO begin could also be some time value, and not a condition
						accept = false;
					}
					// delivery period should start after payment
					Formula docPaymentNote =
						(Formula)SL.instantiate(BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC,
								"sender",customer,
								"receiver",supplier,
								"doc",SL.term("(paymentNote :orderReference \""+orderRef+"\")"));
					if (deliveryPeriod.getBegin().match(docPaymentNote) == null) {
						accept = false;
					}
				}
				
				if (accept) {
					// forward the purchase order to the represented enterprise who decides the response
					Formula doneAction = (Formula)SL.instantiate(new DoneNode(
							new ActionExpressionNode(
									new MetaTermReferenceNode("customer"),
									BusinessActionPatterns.sendPurchaseOrderPattern
								),
								SL.TRUE), applyResult);
					result.add(
						new SemanticRepresentation(
							InstitutionTools.buildIntendToInform(
								doneAction,
								getAgentName(),
								getRepresented(getAgentName()))));
				}
				else {
					Term response = buildSimpleRejectResponse(purchaseOrderTerm,SL.formula("(period-is-bad)")); 
					// TODO : instantiate refuseReason
					Term responseActionTerm = (Term)SL.instantiate(
							BusinessActionPatterns.sendResponsePattern,
							"customer",applyResult.term("sender"),   ///-customer,
							"response",response);
					
					Formula intendToPerformAction = (Formula)SL.instantiate(
							intend_AGENT_done_action_AGENT_ACTION_true,
							"agent",getAgentName(),
							"action",responseActionTerm);
					
					result.add(new SemanticRepresentation(intendToPerformAction));
				}
				return result;
			}//end if ar!=null
			return null;
		}//end doApply
		
	}//end sip
	
	/* when the supplier receives a request for his catalogue
	 * he prepares it and sends it
	 * 
	 * SIP that intercepts the request of catalogue from the client
	 * then builds the catalogue with an external method,
	 * and finally gives to the seller the intention to send this 
	 * catalogue to the requesting client 
	 */
	class ManageCatalogueRequest extends ApplicationSpecificSIPAdapter {

		public ManageCatalogueRequest(InstitutionalCapabilities capabilities) {
			super(capabilities,new DoneNode(
					new ActionExpressionNode(
							new MetaTermReferenceNode("buyer"),
							SL.term("(REQUEST_CATALOGUE :supplier ??supplier)")),
							new TrueNode()));
		}

		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {
			InstitutionTools.printTraceMessage("manage catalogue request: "+applyResult, DEBUG);
			if (applyResult != null) {
				InstitutionTools.printTraceMessage(">>> applyResult="+applyResult,DEBUG);
				Term seller = applyResult.term("supplier");
				Term buyer = applyResult.term("buyer");
				InstitutionTools.printTraceMessage(">>> seller = "+seller,DEBUG);
				InstitutionTools.printTraceMessage(">>> agentName = "+getAgentName(),DEBUG);
				// only manage requests addressed to the self agent
				if (seller.equals(getAgentName())) {
					InstitutionTools.printTraceMessage(">>> seller is the agent",DEBUG);
					// build the catalogue
					Term catalogueObject = buildCatalogue(buyer);
					// adopt the intention to send the catalogue
					Formula intendToSendCatalogue = (IntentionNode)SL.instantiate(
							intend_AGENT_done_action_AGENT_ACTION_true, //sendcatalogue_RECEIVER_CATALOGUE,
							"agent",seller,
							"action",SL.instantiate(BusinessActionPatterns.sendCataloguePattern,
									"customer", buyer,
									"catalogue",catalogueObject));
					InstitutionTools.printTraceMessage(">>> intend to send catalogue = "+intendToSendCatalogue, DEBUG);
					interpret(intendToSendCatalogue);
					return result;
				}
			}
			return null;
		}
	}


	// when the supplier receives a purchase order
	// he prepares his response and sends it to the client
	class PurchaseOrderReceptionTriggersResponse extends ActionInResponseToDocumentReception {
		
		public PurchaseOrderReceptionTriggersResponse(InstitutionalCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getPurchaseOrderPattern());  //,"customerAID","supplierAID"
		}
		
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term purchaseOrderTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! purchaseOrder reception triggers response",DEBUG);
			if (applyResult != null) {
				// get the involved agents
				PurchaseOrder purchaseOrderObject = new PurchaseOrder(purchaseOrderTerm);
				Term supplier = Tools.AID2Term(purchaseOrderObject.getSupplierAID());
				Term customer = Tools.AID2Term(purchaseOrderObject.getCustomerAID());  // applyResult.term("customerAID");
				
				// decide to accept or reject order depending on specified period and own IS
				// if no constraint on sequence of action, accept the order
				boolean accept = true;
				if (deliversAfterPayment) {
					// get the delivery and payment Periods
					Period deliveryPeriod = purchaseOrderObject.getDeliveryPeriod();
					Period paymentPeriod = purchaseOrderObject.getPaymentPeriod();
					// get the order reference
					String orderRef = purchaseOrderObject.getId().stringValue();
					
					// payment period should start at once ?? (i.e. no condition before payment)
					if (paymentPeriod.getBegin().match(SL.TRUE) == null) {
						// TODO begin could also be some time value, and not a condition
						accept = false;
					}
					// delivery period should start after payment
					Formula docPaymentNote =
						(Formula)SL.instantiate(BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC,
								"sender",customer,
								"receiver",supplier,
								"doc",SL.term("(paymentNote :orderReference \""+orderRef+"\")"));
					if (deliveryPeriod.getBegin().match(docPaymentNote) == null) {
						accept = false;
					}
				}
				
				Term response;
				if (accept) {
					response = buildSimpleAcceptResponse(purchaseOrderTerm);
				}
				else {
					response = buildSimpleRejectResponse(purchaseOrderTerm,SL.formula("(period-is-bad)")); 
					// TODO : instantiate refuseReason
				}
					
				Term responseActionTerm = (Term)SL.instantiate(
						BusinessActionPatterns.sendResponsePattern,
						"customer",applyResult.term("sender"),
						"response",response);
				Term actionTerm = responseActionTerm; 
				return actionTerm;
			}
			return null;
		}
		
	}
	
	/* WARNING : this SIP should also be triggered by the purchase order:
	 * when supplier sends response (he also gets the document)
	 * he sends the invoice.
	 * Actually this can be parameterized: some suppliers could also send 
	 * the invoice only after receipt or dispatch.
	 */
	class ResponseEmissionTriggersInvoice extends ActionInResponseToDocumentEmission {
		
		public ResponseEmissionTriggersInvoice(InstitutionalCapabilities capabilities) {
			// behave as if the supplier sent the response to himself
			super(capabilities,BusinessFunctionalTerms.getResponsePattern());  //,"supplierAID","supplierAID"
		}
		
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term responseTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! response reception (by seller) triggers invoice",DEBUG);
			if (applyResult != null) {
				// get in the documents the purchase order referred to by the response
				Response responseObject = Response.buildAppropriateTypeOfAnswer(responseTerm);
				String indicator = responseObject.getAcceptedIndicator().stringValue();
				if (indicator.equals(Response.REJECTED)) {
					mediation = true;
				}
				else {
					// in case it stayed true after previous interaction
					mediation = false; 
				}
				
				if (mediation) {
					return (Term)SL.instantiate(
							TestActionPatterns.printPattern,
							"content",SL.string("!! NO INVOICE since supplier "+applyResult.term("supplierAID")+
									" \n sends a rejection response "+responseTerm+"\n to customer "+
									applyResult.term("customerAID")));
					
				}
				Term purchaseOrderTerm = Document.getReferredDocument(
						responseObject.getOrderReference(),
						BusinessFunctionalTerms.getPurchaseOrderPattern(),
						(BusinessCapabilities)myCapabilities);	
				return (Term)SL.instantiate(BusinessActionPatterns.sendInvoicePattern,
						"customer",applyResult.term("receiver"),  //same receiver as response    ///--customer,
						"invoice",buildInvoiceAfterResponse(purchaseOrderTerm,responseTerm));
			}
			return null;
		}
	}
	
	
	// when supplier receives payment advice, he delivers
	class PaymentAdviceReceptionTriggersDelivery extends ActionInResponseToDocumentReception {
		
		public PaymentAdviceReceptionTriggersDelivery(BusinessCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getPaymentAdvicePattern());  //,"debtorAID","creditorAID"
		}
		
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term paymentAdviceTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! payment advice reception triggers delivery",DEBUG);
			if (applyResult != null) {
				Term delivNoteTerm = buildDeliveryNoteFromPaymentAdvice(paymentAdviceTerm);
				Term orderRef = new DeliveryNote(delivNoteTerm).getOrderReference();
				Term actionTerm = (Term)SL.instantiate(
						BusinessActionPatterns.deliverPackagePattern,
						"customer",applyResult.term("debtorAID"),
						"deliveryNote",delivNoteTerm,
						"orderReference",orderRef);
				
				return actionTerm;
			}
			return null;
		}
	}//end pay_adv reception sip
	
	
	// when seller receives delivery note (sent by himself)
	// he sends dispatch advice
	class DeliveryNoteEmissionTriggersDispatchAdvice extends ActionInResponseToDocumentEmission {

		public DeliveryNoteEmissionTriggersDispatchAdvice(BusinessCapabilities capabilities) {
			super(capabilities, BusinessFunctionalTerms.getDeliveryNotePattern());  //,"supplierAID","customerAID"
		}

		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term deliveryNoteTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! delivery note reception (from oneself) triggers dispatch advice",DEBUG);
			if (applyResult != null) {
				System.err.println("!!!!! applyResult (customerAID ou sender) = "+applyResult);
				Term actionTerm = (Term)SL.instantiate(
						BusinessActionPatterns.sendDispatchAdvicePattern,
						"customer",applyResult.term("customerAID"),  ///-applyResult.term("customerAID"),
						"dispatchAdvice",buildFullDispatchAdviceFromDeliveryNote(deliveryNoteTerm));
				return actionTerm;
			}
			return null;
		}
	}


}//end capab
