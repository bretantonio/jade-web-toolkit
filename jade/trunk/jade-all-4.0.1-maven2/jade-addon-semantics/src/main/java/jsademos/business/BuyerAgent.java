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
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ActionDoneSIPAdapter;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.util.Date;

public class BuyerAgent extends RepresentativeAgent {

	public static final boolean PAYS_AFTER_DELIVERY = true;

	public void setup() {
		// boolean = pays after delivery (parameter of capabilities)
		super.setup(new BuyerCapabilities((String)getArguments()[0],PAYS_AFTER_DELIVERY));
	} // End of setup/0

	// redefine
	public BuyerCapabilities getSemanticCapabilities() {
		SemanticCapabilities capab = super.getSemanticCapabilities();
		if (capab instanceof BuyerCapabilities) {
			return (BuyerCapabilities)capab;
		}
		System.err.println("WARNING : The capabilities "+capab+" should be an instance of BuyerCapabilities but it is a "+capab.getClass()+" !!!");
		throw new ClassCastException();
	}

	public boolean paysAfterDelivery() {
		return getSemanticCapabilities().paysAfterDelivery();
	}

}


class BuyerCapabilities extends BusinessCapabilities {

	private final boolean DEBUG = true;

	/************
	 * COUNTERS *
	 ************/

	private static int purchaseOrdersCounter = 0;
	private static int receiptAdviceCounter = 0;


	/**************
	 * ATTRIBUTES *
	 **************/

	private boolean paysAfterDelivery;

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public BuyerCapabilities(String instName,boolean paysAfterDeliveryBool) {
		// not lazy, nor conscientious, nor trustless
		super(instName,false,false,false);
		paysAfterDelivery = paysAfterDeliveryBool;
	}

	/*************
	 * ACCESSORS *
	 *************/

	public boolean paysAfterDelivery() {
		return paysAfterDelivery;
	}

	/*************************************
	 * METHODS TO BUILD USEFUL DOCUMENTS *
	 *************************************/

	/* method to build the client's purchase order from the initial
	 * declaration in his KBase of desired items
	 * 
	 * TODO later: the client has preferred prices and various strategies 
	 * to choose the price he proposes on his purchase order; for now
	 * he simply respects the prices given in the received catalogue
	 */
	public Term buildPurchaseOrderFromCatalogue(Term catalogueTerm) { 

		// total numbers to be updated along the building of the purchase order
		int nbOfLines=0;
		Double totalPrice=new Double(0);

		// get the catalogue (stored as a document)
		Catalogue catalogue = new Catalogue(catalogueTerm); 

		// the received catalogue to scan
		TermSetNode catalogueContent = catalogue.getCatalogueContent();

		// prepare the purchase order content
		TermSetNode purchaseOrderLines = new TermSetNode();

		// get the catalogue id to be referenced in each order line
		StringConstantNode catalogueRef = catalogue.getId(); 

		// scan this catalogue and build an orderLine for each desired item
		for (int i=0; i<catalogueContent.size(); i++) {
			// get the i-th catalogue line
			Term catalogueItemTermI = catalogueContent.getTerm(i);
			CatalogueItem catalogueItemObjectI = new CatalogueItem(catalogueItemTermI);
			Term itemName = catalogueItemObjectI.getItemName(); //mrItem.term("productName");
			// check the desirability of this item for the buyer
			QueryResult qrDesirability = getMyKBase().query((Formula)SL.instantiate(
					desiredItemPattern,"item",itemName));
			if (qrDesirability != null) {
				// get the number of items of this product that the client desires to buy
				int numberDesired = Integer.parseInt(qrDesirability.getResult(0).term("number").toString());
				if (numberDesired > 0) {
					Double unitPrice = catalogueItemObjectI.getUnitPriceDouble(); //Long.parseLong(mrItem.term("unitPrice").toString());
					// build an orderLine object
					OrderLine orderLineObjectI = new OrderLine(
							catalogueItemObjectI.getId(),
							catalogueRef,
							(IntegerConstantNode)SL.integer(numberDesired),
							(RealConstantNode)SL.real(unitPrice),
							(RealConstantNode)SL.real(numberDesired*unitPrice)
					);
					Term orderLineTermI = orderLineObjectI.getOrderLineTerm();
					InstitutionTools.printTraceMessage("order line("+i+") = "+orderLineTermI, DEBUG);
					purchaseOrderLines.addTerm(orderLineTermI);
					nbOfLines ++;
					totalPrice += unitPrice*numberDesired;
				}//end if nbDes >0
			}//end if qrDes != null
			//}//end if mrItem != null
		}//end for on catalogueContent

		// IF THE CUSTOMER HAS SOMETHING TO PURCHASE !!!
		if (nbOfLines>0) {

			// build the total line
			TotalLine totalLineObject = new TotalLine(
					(IntegerConstantNode)SL.integer(new Long(nbOfLines)),
					(RealConstantNode)SL.real(new Double(totalPrice)));

			// purchase order reference
			String orderRef = "PUR_ORD_"+purchaseOrdersCounter++;

			// choose periods depending on business process
			Period deliveryPeriod;
			Period paymentPeriod;
			if (paysAfterDelivery) {
				Formula docDeliveryNote =
					(Formula)SL.instantiate(BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC,
							"sender",Tools.AID2Term(catalogue.getSupplierAID()),
							"receiver",Tools.AID2Term(catalogue.getCustomerAID()),
							"doc",SL.term("(deliveryNote :orderReference "+orderRef+")"));
				deliveryPeriod = Period.eternalPeriod();
				paymentPeriod = new Period(docDeliveryNote,SL.FALSE);
			}
			else {
				deliveryPeriod = Period.eternalPeriod();
				paymentPeriod = Period.eternalPeriod();
				// If both periods are eternal, when do agent act ?
				// But actually they are obliged to act from the beginning of the period.
			}

			// prepare the purchaseOrder to return
			PurchaseOrder purchaseOrderObject = new PurchaseOrder(
					(StringConstantNode)SL.string(orderRef),
					new DateTimeConstantNode(new Date(System.currentTimeMillis())),
					Tools.term2AID(getAgentName()),
					catalogue.getSupplierAID(),
					deliveryPeriod, // delivery period is not managed (eternal: can be performed at once, no deadline)
					paymentPeriod, // eternal payment period for now
					purchaseOrderLines,
					totalLineObject,
					Period.eternalPeriod(), // default eternal period value for order validity period
					Period.emptyPeriod() // default empty period for cancellation
			);
			Term purchaseOrderTerm = purchaseOrderObject.getPurchaseOrderTerm();
			return purchaseOrderTerm;
		}//end if nboflines>0
		// if the buyer has nothing to purchase to this seller, return null
		return null;
	}


	// build the FULL receipt advice sent when receiving the delivery note joint to the package
	// (ALL items received)
	public Term buildReceiptAdviceFromDeliveryNote(Term deliveryNoteTerm) {

		DeliveryNote deliveryNoteObject = new DeliveryNote(deliveryNoteTerm);
		ReceiptAdvice receiptAdviceObject = 
			new ReceiptAdvice(
					(StringConstantNode)SL.string("REC_ADV_"+receiptAdviceCounter++),
					new DateTimeConstantNode(new Date(System.currentTimeMillis())),
					deliveryNoteObject.getCustomerAID(),
					deliveryNoteObject.getSupplierAID(),
					deliveryNoteObject.getOrderReference(),
					new DateTimeConstantNode(new Date(System.currentTimeMillis()-7000)));
		// do not give optional list of items: full reception
		return receiptAdviceObject.getReceiptAdviceTerm();
	}


	/**************************************
	 * SEMANTIC INTERPRETATION PRINCIPLES *
	 *   - table setup					  *
	 *   - SIPs for delegation			  *
	 *   - SIPs in response to docs		  *
	 **************************************/

	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();

		table.addSemanticInterpretationPrinciple(new ResponseReceptionTriggersPrint(this));
		table.addSemanticInterpretationPrinciple(new HandleMessagesFromEnterprise(this));
		
		// transmission of info to represented enterprise
		// forward the received catalogue
		table.addSemanticInterpretationPrinciple(
				new ForwardMessage(this,new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.sendCataloguePattern),SL.TRUE)));
		
		// DO NOT forward the refuse, automatically ask the mediator
		
		// forward the invoice (delayed by intermediary, enterpriseA arranges the payment and sends payment advice)
		table.addSemanticInterpretationPrinciple(
				new ForwardMessage(this,new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.sendInvoicePattern),SL.TRUE)));
		
		// forward the dispatch advice
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.sendDispatchAdvicePattern),SL.TRUE)));
		
		// forward the delivery-done information message
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.deliverPackagePattern),SL.TRUE)));
		
		// only forward accept response  (TODO: if mediation fails, finally forward the refuse response)
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.sendAcceptPattern),SL.TRUE)));

		table.addSemanticInterpretationPrinciple(new DelayMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.sendRefusePattern),SL.TRUE),
				SL.FALSE));
		
		return table;
	}



	/********************************************************
	 * 1) SIPs for delegation to intermediary 				*
	 *      - static SIP detecting a new delegation			*
	 *      - dynamic SIP to intercept outgoing messages	*
	 *      - dynamic SIP to intercept ingoing messages		*
	 ********************************************************/

	class DelegationEmissionHandling extends ApplicationSpecificSIPAdapter {

		public DelegationEmissionHandling(BusinessCapabilities capabilities) {
			// pattern = emission of a delegation to an intermediary
			super(capabilities,"(document ??myself ??intermediary (delegation :purchaseOrder ??purchaseOrder :loaningBank ??loaningBank))");
		}

		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {

			if (applyResult != null) {
				System.err.println("### DELEGATION !!! applyRez="+applyResult);
				// dynamically post the SIP that will intercept all outgoing communication
				myCapabilities.getMySemanticInterpretationTable().addSemanticInterpretationPrinciple(
						new OutgoingMessagesInterception(
								(BusinessCapabilities)myCapabilities,
								applyResult.term("intermediary"),
								(StringConstantNode)SL.string("ref_bidon")));
				
				// dynamically post the SIP that intercept ingoing messages
				myCapabilities.getMySemanticInterpretationTable().addSemanticInterpretationPrinciple(
						new IngoingMessagesInterception(
								(BusinessCapabilities)myCapabilities,
								applyResult.term("intermediary"),
								(StringConstantNode)SL.string("ref bidon")));
				
				// WARNING: should remove these SIPs at the end of delegation, how ?
				// put an observer on a special document (end of transaction ? payment note and delivery note ?)
				// that will remove these SIPs when observing the end of delegation on this purchase order

				// neutrally transmit the delegation
				ArrayList newRez = new ArrayList();
				sr.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+2);
				// because of the new SIPs added, the current SIP was shifted
				// so the sr would be interpreted by this same SIP if its index was not increased
				newRez.add(sr);
			}
			return null;
		}

	}


	class OutgoingMessagesInterception extends ApplicationSpecificSIPAdapter {

		/**************
		 * ATTRIBUTES *
		 **************/

		private Term theIntermediary;
		private StringConstantNode theOrderRef;

		
		/*************
		 * ACCESSORS *
		 *************/
		
		public StringConstantNode getOrderRef() {
			return theOrderRef;
		}

		
		/***************
		 * CONSTRUCTOR *
		 ***************/

		public OutgoingMessagesInterception(BusinessCapabilities capabilities, Term intermediary, StringConstantNode orderRef)  {
			// pattern = intend done action
			// + constraint that it is a communicative action, tested in doApply
			super(capabilities, "(I ??myself (done (action ??myself ??action)))");
			theIntermediary = intermediary;
			theOrderRef = orderRef;
		}

		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {

			if (applyResult != null) {
				try {
					ActionExpression actionExpr = new ActionExpressionNode(getAgentName(),applyResult.term("action"));
					SemanticAction semAct = myCapabilities.getMySemanticActionTable().getSemanticActionInstance(actionExpr);
					if (semAct instanceof OntologicalAction) {
						// ontOlogical actions are NOT intercepted by this SIP
						InstitutionTools.printTraceMessage(" ---> ontological action: no interception",DEBUG);
						return null;
					}
					else if (semAct instanceof CommunicativeAction) {
						InstitutionTools.printTraceMessage(" ---> communicative action: intercept",DEBUG);
						
						// extract the features of the message
						CommunicativeAction commAct = (CommunicativeAction)semAct;
						Term receiver = commAct.getReceiver();
						InstitutionTools.printTraceMessage("receiver="+receiver,DEBUG);
						InstitutionTools.printTraceMessage("theinterm = "+theIntermediary,DEBUG);
						InstitutionTools.printTraceMessage("--> bool="+receiver.equals(theIntermediary),DEBUG);
						if (receiver.equals(theIntermediary)) {
							System.err.println("do not intercept messages to intermediary !!");
							return null;
						}
						// the indirect message - encapsulate original action in a special predicate
						Formula indirectContent = SL.formula("(outgoingMessage ??sender ??receiver ??action)");
						indirectContent = (Formula)SL.instantiate(indirectContent,"action",actionExpr,"sender",getAgentName(),"receiver",receiver);
						// TODO : should also test if the message concerns the managed orderRef
						
						myCapabilities.inform(indirectContent,theIntermediary);
						return new ArrayList();
						
					}	
				}
				catch(SemanticInterpretationException sie) {
					System.err.println("BuyerAgent.OutgoingMessageInterceptionSIP: exception in building semantic action instance");
					sie.printStackTrace();
				}
			}
			return null;
		}

	}

	
	class IngoingMessagesInterception extends ActionDoneSIPAdapter {
		
		Term theIntermediary;
		StringConstantNode theOrderRef;
		
		public IngoingMessagesInterception(BusinessCapabilities capabilities,Term intermediary,StringConstantNode orderRef) {	
			super(capabilities,new ActionExpressionNode(
					new MetaTermReferenceNode("agent"),
					new MetaTermReferenceNode("action")));
			theIntermediary = intermediary;
			theOrderRef = orderRef;
		}
		
		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {
			if (applyResult != null) {
				// test if it is a communicative action
				try {
					ActionExpression actionExpr = new ActionExpressionNode(applyResult.term("agent"),applyResult.term("action"));
					SemanticAction semAct = myCapabilities.getMySemanticActionTable().getSemanticActionInstance(actionExpr);
					if (semAct instanceof OntologicalAction) {
						System.err.println(" ---> ontological action: no interception");
						return null;
					}
					else if (semAct instanceof CommunicativeAction) {
						System.err.println(" ---> communicative action: intercept");
						// extract the features of the message
						Term sender = applyResult.term("agent");
						System.err.println("sender="+sender);
						System.err.println("theinterm = "+theIntermediary);
						System.err.println("--> bool="+sender.equals(theIntermediary));
						if (sender.equals(theIntermediary)) {
							System.err.println("do not intercept messages FROM intermediary !!");
							return null;
						}
						if (sender.equals(getAgentName())) {
							System.err.println("this is a performed outgoing message to intermediary: do not intercept as ingoing");
							return null;
						}
						// the indirect message - encapsulate original action in a special predicate
						Formula indirectContent = SL.formula("(ingoingMessage ??sender ??receiver ??action)");
						indirectContent = (Formula)SL.instantiate(indirectContent,"action",actionExpr,"sender",sender,"receiver",getAgentName());
						// absorb the original message : the agent does NOT receive it
						// Direct inform (the agent does not believe the content to be true, thus 
						// interpret the intention to inform the intermediary about it won't work)
						myCapabilities.inform(indirectContent,theIntermediary);
						return new ArrayList();
					}
				}
				catch(SemanticInterpretationException sie) {
					System.err.println("BuyerAgent.OutgoingMessageInterceptionSIP: exception in building semantic action instance");
					sie.printStackTrace();
				}
				
			}
			return null;
		}
		
	}
	
	

	/************************************
	 * 2) SIPs in reaction to documents *
	 ************************************/

	// when client receives the catalogue document
	// he builds and send the corresponding purchase order
	class CatalogueReceptionTriggersOrder extends ActionInResponseToDocumentReception {

		public CatalogueReceptionTriggersOrder(InstitutionalCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getCataloguePattern());  ///-,"supplierAID","customerAID"
		}

		public Term doApply(MatchResult applyResult, SemanticRepresentation sr, Term catalogueTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! catalogue reception triggers order",DEBUG);
			if (applyResult != null) {
				Term buyer = applyResult.term("customerAID");
				Term seller = applyResult.term("supplierAID");
				// if the agent is the client receiving the catalogue
				if (buyer.equals(getAgentName())) {
					Term purchaseOrder = buildPurchaseOrderFromCatalogue(catalogueTerm);  

					if (purchaseOrder != null) {
						// instantiate the sendPurchaseOrder action pattern
						Term actionTerm = (Term)SL.instantiate(
								BusinessActionPatterns.sendPurchaseOrderPattern,
								"supplier",applyResult.term("sender"),  
								"purchaseOrder",purchaseOrder,
								"orderReference",SL.string(""),
								"responseReference",SL.string(""));
						// order reference and response reference are empty "" since this is a first order
						// (if they are null, variables are not instantiated from them...)
						return actionTerm;
					}
					System.err.println(getAgentName()+" has NOTHING to PURCHASE to "+seller);
				}
			}
			return null;
		}
	}


	// when client receives the response document
	// he does nothing: just print an acknowledgement on console
	class ResponseReceptionTriggersPrint extends ActionInResponseToDocumentReception {

		public ResponseReceptionTriggersPrint(InstitutionalCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getResponsePattern());  //,"supplierAID","customerAID"
		}

		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term responseTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! response reception triggers print",DEBUG);
			if (applyResult != null) {
				Response responseObject = Response.buildAppropriateTypeOfAnswer(responseTerm);
				String indicator = responseObject.getAcceptedIndicator().stringValue();

				if (indicator.equals(Response.REJECTED)) {  // if mediation
					Term rejectedOrder = Document.getReferredDocument(responseObject.getOrderReference(), 
							BusinessFunctionalTerms.getPurchaseOrderPattern(),(BusinessCapabilities)myCapabilities);
					return (Term)SL.instantiate(BusinessActionPatterns.signalProblemPattern,
							"mediator",((BusinessCapabilities)myCapabilities).getMediator(),
							"rejectedPurchaseOrder",rejectedOrder,
							"rejectionReason",((SimpleRejectResponse)responseObject).getRejectionReason());
				}
				// prepare the action term to return
				return (Term)SL.instantiate(
						TestActionPatterns.printPattern,
						"content",SL.string("!! customer "+applyResult.term("customerAID")+
								" \n receives response "+responseTerm+"\n from supplier "+
								applyResult.term("supplierAID")));
				// order reference and response reference stay empty since this is a first order
			}
			return null;
		}

	}


	class InvoiceReceptionTriggersPaymentOrder extends ActionInResponseToDocumentReception {

		public InvoiceReceptionTriggersPaymentOrder(InstitutionalCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getInvoicePattern());  //,"supplierAID","customerAID"
		}

		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term invoiceTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! invoice reception triggers payment order",DEBUG);
			if (applyResult!= null) {
				// get the AID of the bank of the customer
				AID bankAID = getBank();

				// prepare the action term to return
				Term actionTerm = (Term)SL.instantiate(
						BusinessActionPatterns.sendPaymentOrderPattern,
						"bank",Tools.AID2Term(bankAID),   ///+applyResult.term("sender"), 
						"paymentOrder",buildPaymentOrderFromInvoice(invoiceTerm, bankAID)
				);
				return actionTerm;
			}
			return null;
		}

	}


	// when customer receives the payment advice from his bank
	// he forwards it to the supplier
	class PaymentAdviceReceptionTriggersPaymentAdvice extends ActionInResponseToDocumentReception {

		public PaymentAdviceReceptionTriggersPaymentAdvice(InstitutionalCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getPaymentAdvicePattern() 
					/* WARNING: no access to the name of the bank who sent the advice ...
					 * actually the payment advice from the bank to his client (bank needed) should be 
					 * different from the payment advice action from customer to supplier (no bank needed)
					 * for now consider the debtor as sending the advice to himself
					 */
			);
		}

		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term paymentAdviceTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! payment advice reception (from bank) triggers payment advice (to supplier)",DEBUG);
			if (applyResult!= null) {
				// build the purchase order
				PaymentAdvice paymentAdviceObject = new PaymentAdvice(paymentAdviceTerm);
				StringConstantNode invoiceRef = paymentAdviceObject.getInvoiceReference();
				Term invoiceTerm = Document.getReferredDocument(
						invoiceRef,
						BusinessFunctionalTerms.getInvoicePattern(),
						(BusinessCapabilities)myCapabilities);
				Invoice invoiceObj = new Invoice(invoiceTerm);
				StringConstantNode orderRef = invoiceObj.getOrderReference();
				Term orderTerm = Document.getReferredDocument(
						orderRef,
						BusinessFunctionalTerms.getPurchaseOrderPattern(),
						(BusinessCapabilities)myCapabilities);

				// check if there is a delegation for this transaction
				Formula documentDelegation = (Formula)SL.instantiate(
						BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC,
						"sender",getAgentName(),
						//any receiver = intermediary
						"doc",SL.instantiate(
								BusinessFunctionalTerms.getDelegationPattern(),
								"purchaseOrder",orderTerm
								// any loaning bank
						));
				// HERE !!!!
				QueryResult qr = myKBase.query(documentDelegation);

				// the receiver of the payment advice
				Term delegate;
				if (qr != null) {
					// delegation: get the delegate
					delegate = qr.getResult(0).term("receiver");
				}
				else {
					// no delegation: directly send to supplier
					delegate = applyResult.term("creditorAID");
				}
				Term actionTerm = (Term)SL.instantiate(
						BusinessActionPatterns.sendPaymentAdvicePattern,
						"supplier",delegate,  
						// same payment advice term (just forwarded)
						"paymentAdvice",paymentAdviceTerm);
				return actionTerm;
			}
			return null;
		}
	}

	// when client receives dispatch advice 
	// he sends receipt advice
	class DeliveryNoteReceptionTriggersReceiptAdvice extends ActionInResponseToDocumentReception {

		public DeliveryNoteReceptionTriggersReceiptAdvice(InstitutionalCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getDeliveryNotePattern());  //,"supplierAID","customerAID"
		}

		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term deliveryNoteTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! delivery note reception triggers receipt advice",DEBUG);
			if (applyResult!= null) {

				// check if there is a delegation for this b2b exchange
				DeliveryNote deliveryNoteObject = new DeliveryNote(deliveryNoteTerm);
				StringConstantNode orderRef = deliveryNoteObject.getOrderReference();
				Term purchaseOrderTerm = Document.getReferredDocument(orderRef,BusinessFunctionalTerms.getPurchaseOrderPattern(), (BusinessCapabilities)myCapabilities);
				Formula documentDelegation = (Formula)SL.instantiate(
						BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC,
						"sender",getAgentName(),
						//any receiver = intermediary
						"doc",SL.instantiate(
								BusinessFunctionalTerms.getDelegationPattern(),
								"purchaseOrder",purchaseOrderTerm
								// any loaning bank
						));
				QueryResult qr = myKBase.query(documentDelegation);
				// prepare the list of actions to return
				TermSetNode actionsTerm = new TermSetNode();
				if (qr != null) {
					// if there is an intermediary: inform him
					Term intermediary = qr.getResult(0).term("receiver");
					Term sendReceiptAdviceToIntermediary = (Term)SL.instantiate(
							BusinessActionPatterns.sendReceiptAdvicePattern,
							"supplier",intermediary,
							"receiptAdvice",buildReceiptAdviceFromDeliveryNote(deliveryNoteTerm));
					actionsTerm.addTerm(sendReceiptAdviceToIntermediary);
				}
				// in any case: inform the sender of delivery note
				Term sendReceiptAdviceToSupplier = (Term)SL.instantiate(
						BusinessActionPatterns.sendReceiptAdvicePattern,
						"supplier",applyResult.term("sender"), ///-applyResult.term("supplierAID"),
						"receiptAdvice",buildReceiptAdviceFromDeliveryNote(deliveryNoteTerm));
				actionsTerm.addTerm(sendReceiptAdviceToSupplier);
				return actionsTerm;
			}
			return null;
		}
	}


	/*****************
	 * INITIAL KBASE *
	 *****************/

	protected KBase setupKbase() {
		KBase kbase = super.setupKbase();
		addDesiredItem(kbase,"television",1);
		addDesiredItem(kbase,"phone",2);
		// test item that is not in the catalogue of the seller
		addDesiredItem(kbase,"dvd",5);

		// name of personal bank
		kbase.assertFormula(SL.formula("(myBank "+B2BInstitution.BANK_A_AGENT+")"));

		return kbase;
	}

	// add a desired object (with number of desired items) in the kbase
	// TODO this should not be a specific predicate, but an intention
	// (or a desire) to have the given number of the given item ...
	public KBase addDesiredItem(KBase kbase, String item, int number) {
		Formula desiredItemPattern = SL.formula("(desired-item ??item ??number)");
		kbase.assertFormula((Formula)SL.instantiate(desiredItemPattern,
				"item",SL.string(item),
				"number",new IntegerConstantNode(new Long(number)))
		);
		return kbase;
	}

}