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
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.QueryResult;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

import java.util.Date;

/*
 * Created by Carole Adam, 12 June 2008
 * Gathers patterns and operations on functional terms 
 * of the ontology
 */

public class BusinessFunctionalTerms {

	/************
	 * PATTERNS *
	 ************/

	public static final Formula document_SENDER_RECEIVER_DOC = SL.formula("(document ??sender ??receiver ??doc)");


	/***********
	 * METHODS *
	 ***********/

	public static DateTimeConstantNode buildNewDate(
			DateTimeConstantNode initialDate,
			long delayInMillisecs) {
		return new DateTimeConstantNode(new Date(initialDate.lx_value().getTime()+delayInMillisecs));
	}

	/*************************
	 * **                 ** *
	 * ACCESSORS TO SUBTERMS *
	 * **                 ** *
	 *************************/

	public static Term getCataloguePattern() {
		return Catalogue.cataloguePattern;
	}

	public static Term getPurchaseOrderPattern() {
		return PurchaseOrder.purchaseOrderPattern;
	}

	public static Term getResponsePattern() {
		return Response.responsePattern;
	}

	public static Term getInvoicePattern() {
		return Invoice.invoicePattern;
	}

	public static Term getPaymentOrderPattern() {
		return PaymentOrder.paymentOrderPattern;
	}

	public static Term getPaymentAdvicePattern() {
		return PaymentAdvice.paymentAdvicePattern;
	}

	public static Term getDispatchAdvicePattern() {
		return DispatchAdvice.dispatchAdvicePattern;
	}

	public static Term getReceiptAdvicePattern() {
		return ReceiptAdvice.receiptAdvicePattern;
	}

	public static Term getDeliveryNotePattern() {
		return DeliveryNote.deliveryNotePattern;
	}

	public static Term getPaymentNotePattern() {
		return PaymentNote.paymentNotePattern;
	}

	public static Term getDelegationPattern() {
		return Delegation.delegationPattern;
	}

}//end BusinessFunctionalTerms

// to gather the display of special elements inside documents
abstract class DocumentElement {
	public abstract String toString(String decal);
	
	private enum typesOfElements {MODIFIED_ORDER_LINE,MODIFIED_PERIOD,
		ORDER_LINE,PERIOD,TOTAL_LINE,CATALOGUE_ITEM};
	
	private static typesOfElements convert(String type) {
		if (type.equals("modifiedOrderLine")) return typesOfElements.MODIFIED_ORDER_LINE;
		if (type.equals("modifiedPeriod")) return typesOfElements.MODIFIED_PERIOD;
		if (type.equals("orderLine")) return typesOfElements.ORDER_LINE;
		if (type.equals("period")) return typesOfElements.PERIOD;
		if (type.equals("totalLine")) return typesOfElements.TOTAL_LINE;
		if (type.equals("catalogueItem")) return typesOfElements.CATALOGUE_ITEM;
		return null;
	}
	
	// build a document element of the appropriate type - used in display
	public static DocumentElement buildDocumentElement(Term docElemTerm) {
		if (docElemTerm instanceof FunctionalTermParamNode) {
			String typeOfDocElem = ((FunctionalTermParamNode)docElemTerm).as_symbol().toString();
			switch(convert(typeOfDocElem)) {
			case MODIFIED_ORDER_LINE : return new ModifiedOrderLine(docElemTerm);
			case MODIFIED_PERIOD : return new ModifiedPeriod(docElemTerm);
			case ORDER_LINE : return new OrderLine(docElemTerm);
			case PERIOD : return new Period(docElemTerm);
			case TOTAL_LINE : return new TotalLine(docElemTerm);
			case CATALOGUE_ITEM : return new CatalogueItem(docElemTerm);
			}	
		}
		return null;
	}
	
	// displays a set of DocumentElement 
	public static String elementSetToString(TermSetNode tsn,String decal) {
	String s = "";
	for (int i=0;i<tsn.size();i++) {
		DocumentElement elem = buildDocumentElement(tsn.getTerm(i));
		s += "\n"+decal+" - "+elem.toString(decal+"\t");
	}
	return s;
}
	
}

//gathers common attributes of all documents
abstract class Document {

	/**************
	 * ATTRIBUTES *
	 **************/

	StringConstantNode id;
	DateTimeConstantNode issueDateTime; 

	/****************************
	 * types of docs management *
	 ****************************/
	
	public enum typesOfDocs {REQUEST_CATALOGUE,CATALOGUE, DELEGATION, 
		DELIVERY_NOTE, DISPATCH_ADVICE, INVOICE, PAYMENT_ADVICE, 
		PAYMENT_NOTE, PAYMENT_ORDER, PURCHASE_ORDER, RECEIPT_ADVICE, 
		RESPONSE, AGREEMENT, QUERY_AGREEMENT};
	
	public static typesOfDocs convert(String type) {
		if (type.equals("requestCatalogue")) return typesOfDocs.REQUEST_CATALOGUE;
		if (type.equals("catalogue")) return typesOfDocs.CATALOGUE;
		if (type.equals("delegation")) return typesOfDocs.DELEGATION;
		if (type.equals("deliveryNote")) return typesOfDocs.DELIVERY_NOTE;
		if (type.equals("dispatchAdvice")) return typesOfDocs.DISPATCH_ADVICE;
		if (type.equals("invoice")) return typesOfDocs.INVOICE;
		if (type.equals("paymentAdvice")) return typesOfDocs.PAYMENT_ADVICE;
		if (type.equals("paymentNote")) return typesOfDocs.PAYMENT_NOTE;
		if (type.equals("paymentOrder")) return typesOfDocs.PAYMENT_ORDER;
		if (type.equals("purchaseOrder")) return typesOfDocs.PURCHASE_ORDER;
		if (type.equals("receiptAdvice")) return typesOfDocs.RECEIPT_ADVICE;
		if (type.equals("response")) return typesOfDocs.RESPONSE;
		if (type.equals("agreement")) return typesOfDocs.AGREEMENT;
		if (type.equals("queryAgreement")) return typesOfDocs.QUERY_AGREEMENT;
		return null;
	}
		
	// constructors in children abstract classes (in order not to compute mr three times)

	/*************
	 * ACCESSORS *
	 *************/

	public abstract Term getPattern();

	public StringConstantNode getId() {
		return id;
	}

	public DateTimeConstantNode getIssueDateTime() {
		return issueDateTime;
	}


	/***************************
	 * ABSTRACT DISPLAY METHOD *
	 ***************************/

	public abstract String toString();
	
	/******************
	 * STATIC METHODS *
	 ******************/

	// get a full document term from its id and pattern (used to rebuild it from mr)
	public static Term getReferredDocument(
			StringConstantNode ref,Term documentPattern,BusinessCapabilities capab) {
		Term documentTerm = (Term)SL.instantiate(
				documentPattern, 
				"id",ref);
		Formula belDoc = new BelieveNode(capab.getAgentName(),
				BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC);
		belDoc = (Formula)SL.instantiate(belDoc,"doc",documentTerm);

		QueryResult qr = capab.getMyKBase().query(belDoc);
		// instantiate the document pattern from this result
		if (qr != null) {
			// Extract the result, that should be unique since identified by its unique ref
			MatchResult mr = qr.getResult(0); 
			documentTerm = (Term)SL.instantiate(documentTerm,mr);
			return documentTerm;
		}
		// if the document was not found return null
		return null;
	}

	// get a document from the value of one of its attributes
	public static Term getDocumentTermWithAttribute(String nameOfAttribute,
			Term valueOfAttribute,Term documentPattern,BusinessCapabilities capab) {
		Term documentTerm = (Term)SL.instantiate(
				documentPattern, 
				nameOfAttribute,valueOfAttribute);
		BelieveNode belDoc = new BelieveNode(capab.getAgentName(),
				BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC);
		belDoc = (BelieveNode)SL.instantiate(belDoc,"doc",documentTerm);

		QueryResult qr = capab.getMyKBase().query(belDoc);
		// instantiate the document pattern from this result
		if (qr != null) {
			// extract result, that should be unique since identified by its unique ref
			MatchResult mr = qr.getResult(0); 
			documentTerm = (Term)SL.instantiate(documentTerm,mr);
			return documentTerm;
		}
		// if the document was not found return null
		return null;
	}

	// build a document of the appropriate type - used in display
	public static Document buildDocument(Term docTerm) {
		if (docTerm instanceof FunctionalTermParamNode) {
			String typeOfDoc = ((FunctionalTermParamNode)docTerm).as_symbol().toString();
			switch(convert(typeOfDoc)) {
			case REQUEST_CATALOGUE : return new RequestCatalogue(docTerm);
			case CATALOGUE : return new Catalogue(docTerm);
			case DELEGATION : return new Delegation(docTerm);
			case DELIVERY_NOTE : return new DeliveryNote(docTerm);
			case DISPATCH_ADVICE : return new DispatchAdvice(docTerm);
			case INVOICE : return new Invoice(docTerm);
			case PAYMENT_ADVICE : return new PaymentAdvice(docTerm);
			case PAYMENT_NOTE : return new PaymentNote(docTerm);
			case PAYMENT_ORDER : return new PaymentOrder(docTerm);
			case PURCHASE_ORDER : return new PurchaseOrder(docTerm);
			case RECEIPT_ADVICE : return new ReceiptAdvice(docTerm);
			case RESPONSE : return Response.buildAppropriateTypeOfAnswer(docTerm);
			case AGREEMENT : return new Agreement(docTerm);
			case QUERY_AGREEMENT : return new QueryAgreement(docTerm);
			}	
		}
		return null;
	}
	
}


abstract class BusinessDocument extends Document {
	AID customerAID;
	AID supplierAID;

	public BusinessDocument(StringConstantNode docId, 
			DateTimeConstantNode docIssueDateTime,
			AID docCustomerAID,
			AID docSupplierAID) {
		id = docId;
		issueDateTime = docIssueDateTime;
		customerAID = docCustomerAID;
		supplierAID = docSupplierAID;
	}

	// from Term AND particular pattern
	public BusinessDocument(Term documentTerm, Term documentPattern) {
		MatchResult mr = documentPattern.match(documentTerm);
		if (mr != null) {
			// build the java object
			id = (StringConstantNode)mr.term("id");
			issueDateTime = (DateTimeConstantNode)mr.term("issueDateTime");
			customerAID = Tools.term2AID(mr.term("customerAID"));
			supplierAID = Tools.term2AID(mr.term("supplierAID"));
		}
		else {
			System.err.println("error while creating new generic BusinessDocument " +
					"from \n - term: "+documentTerm+" \n - and pattern: "+documentPattern);
		}
	}

	public AID getCustomerAID() {
		return customerAID;
	}

	public AID getSupplierAID() {
		return supplierAID;
	}

}


abstract class BankDocument extends Document {
	AID creditorAID;
	AID debtorAID;

	public BankDocument(StringConstantNode docId, 
			DateTimeConstantNode docIssueDateTime,
			AID docCreditorAID,
			AID docDebtorAID) {
		id = docId;
		issueDateTime = docIssueDateTime;
		creditorAID = docCreditorAID;
		debtorAID = docDebtorAID;
	}

	// from Term AND particular pattern
	public BankDocument(Term documentTerm, Term documentPattern) {
		MatchResult mr = documentPattern.match(documentTerm);
		if (mr != null) {
			// build the java object
			id = (StringConstantNode)mr.term("id");
			issueDateTime = (DateTimeConstantNode)mr.term("issueDateTime");
			creditorAID = Tools.term2AID(mr.term("creditorAID"));
			debtorAID = Tools.term2AID(mr.term("debtorAID"));
		}
		else {
			System.err.println("error while creating new generic BankDocument " +
					"from \n - term: "+documentTerm+" \n - and pattern: "+documentPattern);
		}
	}

	public AID getCreditorAID() {
		return creditorAID;
	}

	public AID getDebtorAID() {
		return debtorAID;
	}

}


class Catalogue extends BusinessDocument {

	/***********
	 * PATTERN *
	 ***********/

	public static final Term cataloguePattern = 
		SL.term("(catalogue " +
				":id ??id " +
				":issueDateTime ??issueDateTime " +
				":customerAID ??customerAID " +
				":supplierAID ??supplierAID " +
				":validityPeriod ??validityPeriod " +
		":catalogueContent ??catalogueContent)");

	/**************
	 * ATTRIBUTES *
	 **************/

	Period validityPeriod; 
	TermSetNode catalogueContent;


	/****************
	 * CONSTRUCTORS *
	 ****************/

	// from term
	public Catalogue (Term catalogueTerm) {
		super(catalogueTerm,cataloguePattern);
		// FIXME compute mr again ... (already computed in super()... )
		MatchResult mr = cataloguePattern.match(catalogueTerm);
		if (mr != null) {
			// build the java object
			validityPeriod = new Period(mr.term("validityPeriod"));
			catalogueContent = (TermSetNode)mr.term("catalogueContent");
		}
		else {
			System.err.println("exception while building new Catalogue from term: "+catalogueTerm);
		}
	}


	// only one constructor since all attributes are always required
	public Catalogue(StringConstantNode cid,
			DateTimeConstantNode cIssueDateTime,
			AID cCustomerAID,
			AID cSupplierAID,
			Period cValidityPeriod,
			TermSetNode cCatalogueContent) {
		super(cid,cIssueDateTime,cCustomerAID,cSupplierAID);
		validityPeriod = cValidityPeriod;
		catalogueContent = cCatalogueContent;
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return cataloguePattern;
	}

	public Period getValidityPeriod() {
		return validityPeriod;
	}

	public Formula getEndValidity() {
		return validityPeriod.getEnd();
	}

	public TermSetNode getCatalogueContent() {
		return catalogueContent;
	}

	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getCatalogueTerm() {
		try {
			Term result = (Term)cataloguePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"validityPeriod",validityPeriod.getPeriodTerm());
			SL.set(result,"catalogueContent",catalogueContent);
			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate catalogueItemPattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		String s = "CATALOGUE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - validityPeriod = "+validityPeriod.toString("\t\t");
		String content = "\n \t - catalogueContent = "+DocumentElement.elementSetToString(catalogueContent,"\t\t");
		return s+content;
	}
	
	
}


//this is NOT a document, but an element of a document
class CatalogueItem extends DocumentElement {

	/***********
	 * PATTERN *
	 ***********/	
	public static final Term catalogueItemPattern = 
		SL.term("(catalogueItem " +
				":id ??id " +
				":itemName ??itemName " +
				":itemDescription ??itemDescription " +
				"(::? :salesUnit ??salesUnit) " +
		":unitPrice ??unitPrice)");

	/**************
	 * ATTRIBUTES *
	 **************/
	// required
	StringConstantNode id;
	StringConstantNode itemName;
	StringConstantNode itemDescription;
	RealConstantNode unitPrice;

	// optional
	StringConstantNode salesUnit;


	/****************
	 * CONSTRUCTORS *
	 ****************/

	// from term
	public CatalogueItem(Term catalogueItemTerm) {
		MatchResult mr = catalogueItemPattern.match(catalogueItemTerm);
		// Extract features from MatchResult to build item
		// Be careful of optional attributes
		if (mr != null) {
			id = (StringConstantNode)mr.term("id");
			itemName = (StringConstantNode)mr.term("itemName");
			itemDescription = (StringConstantNode)mr.term("itemDescription");
			unitPrice = (RealConstantNode)mr.term("unitPrice");
			// sales unit, potentially null
			salesUnit = (StringConstantNode)mr.term("salesUnit");
		}
		else {
			System.err.println("exception while creating new catalogue item from term: "+catalogueItemTerm);
		}
	}

	// constructor with all attributes
	public CatalogueItem(StringConstantNode ciid,
			StringConstantNode ciItemName,
			StringConstantNode ciItemDescription,
			RealConstantNode ciUnitPrice,
			StringConstantNode ciSalesUnit) {
		id = ciid; 
		itemName = ciItemName;
		itemDescription = ciItemDescription;
		unitPrice = ciUnitPrice;
		salesUnit = ciSalesUnit;
	}

	// constructor without optional attribute salesUnit
	public CatalogueItem(StringConstantNode ciid,
			StringConstantNode ciItemName,
			StringConstantNode ciItemDescription,
			RealConstantNode ciUnitPrice) {
		// default value for salesUnit is an empty string
		this(ciid,
				ciItemName,
				ciItemDescription,
				ciUnitPrice,
				(StringConstantNode)SL.string(""));
	}

	/*************
	 * ACCESSORS *
	 *************/

	public StringConstantNode getId() {
		return id;
	}

	public StringConstantNode getItemName() {
		return itemName;
	}

	public StringConstantNode getItemDescription() {
		return itemDescription;
	}

	public RealConstantNode getUnitPrice() {
		return unitPrice;
	}

	public Double getUnitPriceDouble() {
		return unitPrice.realValue();
	}

	public StringConstantNode getSalesUnit() {
		return salesUnit;
	}

	/**********************
	 * CONVERSION to TERM *
	 **********************/

	public Term getCatalogueItemTerm() {
		try {
			Term result = (Term)catalogueItemPattern.getClone();
			SL.set(result, "id",id);
			SL.set(result, "itemName",itemName);
			SL.set(result, "itemDescription",itemDescription);
			SL.set(result, "salesUnit",salesUnit);
			SL.set(result, "unitPrice",unitPrice);
			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate catalogueItemPattern");
			e.printStackTrace();
			return null;
		}
	}

	
	/***********
	 * DISPLAY *
	 ***********/
	
	// decal used to align it inside a biggest document
	public String toString(String decal) {
		return "catalogueItem " +
			"\n"+decal+" - id = "+id +
			"\n"+decal+" - itemName = "+itemName +
			"\n"+decal+" - itemDescription = "+itemDescription +
			// optional sales unit
			"\n"+decal+" - salesUnit = "+salesUnit +
			"\n"+decal+" - unitPrice = "+unitPrice;
	}
}


class Period extends DocumentElement {

	//pattern
	public static final Term periodPattern = 
		SL.term("(period (fact ??begin) (fact ??end))");

	// attributes
	private Formula begin;
	private Formula end;

	/********************************
	 * CONSTRUCTORS from attributes *
	 ********************************/

	public Period(Formula pbeginFormula,Formula pendFormula) {
		begin = pbeginFormula;
		end = pendFormula;
	}

	public Period(Term pbeginFact, Term pendFact) {
		begin = ((FactNode)pbeginFact).as_formula();
		end = ((FactNode)pendFact).as_formula();
	}

	/*************************
	 * CONSTRUCTOR from Term *
	 *************************/

	public Period(Term periodTerm) {
		MatchResult mr;
		if ((periodTerm != null) && ((mr = periodPattern.match(periodTerm)) != null)) {
			begin = mr.formula("begin");
			end = mr.formula("end");
		}
		else {
			System.err.println("exception while building new Period from term: "+periodTerm);
		}
	}

	/*************
	 * ACCESSORS *
	 *************/
	public Formula getBegin() {
		return begin;
	}

	public Formula getEnd() {
		return end;
	}

	public static Period emptyPeriod() {
		// never begins
		return new Period(SL.FALSE,SL.TRUE);
	}

	public static Period eternalPeriod() {
		// begins at once, never ends
		return new Period(SL.TRUE,SL.FALSE);
	}

	public Term getPeriodTerm() {
		return (Term)SL.instantiate(periodPattern,"begin",begin,"end",end);
	}


	/***********
	 * METHODS *
	 ***********/

	public String toString(String decal) {
		return "Period between "+"\n"+
				decal+" - "+begin+" \n"+
				decal+" and \n "+
				decal+" - "+end;
	}

}


class PurchaseOrder extends BusinessDocument {

	/***********
	 * PATTERN *
	 ***********/
	public static final Term purchaseOrderPattern = 
		SL.term("(purchaseOrder " +
				":id ??id " +
				":issueDateTime ??issueDateTime " +
				":customerAID ??customerAID " +
				":supplierAID ??supplierAID " +
				"(::? :validityPeriod ??validityPeriod) " +
				":deliveryPeriod ??deliveryPeriod " +   // (period (fact ??begin) (fact ??end))
				":paymentPeriod ??paymentPeriod " +
				"(::? :cancellationPeriod ??cancellationPeriod) " +
				":orderLines ??orderLines " +
				":totalLine ??totalLine)"
		);

	/**************
	 * ATTRIBUTES *
	 **************/
	// required
	Period deliveryPeriod;
	Period paymentPeriod;
	TermSetNode orderLines; //Set<OrderLine>
	TotalLine totalLine;
	// optional
	Period validityPeriod;
	Period cancellationPeriod;


	/****************
	 * CONSTRUCTORS *
	 ****************/
	// constructor from term
	public PurchaseOrder(Term purchaseOrderTerm) {
		super(purchaseOrderTerm,purchaseOrderPattern);
		MatchResult mr = purchaseOrderPattern.match(purchaseOrderTerm);
		if (mr != null) {
			deliveryPeriod = new Period(mr.term("deliveryPeriod"));
			paymentPeriod =	new Period(mr.term("paymentPeriod"));
			orderLines = (TermSetNode)mr.term("orderLines");
			totalLine = new TotalLine(mr.term("totalLine"));
			validityPeriod = new Period(mr.term("validityPeriod"));
			cancellationPeriod = new Period(mr.term("cancellationPeriod"));
		}
		else {
			System.err.println("error while creating new PurchaseOrder from term: "+purchaseOrderTerm);
		}	
	}

	// constructor with all attributes
	public PurchaseOrder(StringConstantNode poId,
			DateTimeConstantNode poIssueDateTime,
			AID poCustomerAID,
			AID poSupplierAID,
			Period poDeliveryPeriod,
			Period poPaymentPeriod,
			TermSetNode poOrderLines,
			TotalLine poTotalLine,
			Period poValidityPeriod,
			Period poCancellationPeriod) {
		super(poId,poIssueDateTime,poCustomerAID,poSupplierAID);
		deliveryPeriod = poDeliveryPeriod;
		paymentPeriod = poPaymentPeriod;
		orderLines = poOrderLines;
		totalLine = poTotalLine;
		validityPeriod = poValidityPeriod;
		cancellationPeriod = poCancellationPeriod;			
	}

	// constructor with validity period but no cancellation period
	public PurchaseOrder(StringConstantNode poId,
			DateTimeConstantNode poIssueDateTime,
			AID poCustomerAID,
			AID poSupplierAID,
			Period poDeliveryPeriod,
			Period poPaymentPeriod,
			TermSetNode poOrderLines,
			TotalLine poTotalLine,
			Period poValidityPeriod) {
		// default value for cancellation period = empty period
		this(poId,poIssueDateTime,poCustomerAID,poSupplierAID,poDeliveryPeriod,poPaymentPeriod,
				poOrderLines,poTotalLine,poValidityPeriod,Period.emptyPeriod());
	}

	// constructor with no validity period, no cancellation period
	public PurchaseOrder(StringConstantNode poId,
			DateTimeConstantNode poIssueDateTime,
			AID poCustomerAID,
			AID poSupplierAID,
			Period poDeliveryPeriod,
			Period poPaymentPeriod,
			TermSetNode poOrderLines,
			TotalLine poTotalLine) {
		// default value for cancellation period = empty period
		// default validity period is eternal
		this(poId,poIssueDateTime,poCustomerAID,poSupplierAID,poDeliveryPeriod,poPaymentPeriod,
				poOrderLines,poTotalLine,Period.eternalPeriod(),Period.emptyPeriod());
	}

	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return purchaseOrderPattern;
	}

	public Period getDeliveryPeriod() {
		return deliveryPeriod;
	}

	public Period getPaymentPeriod() {
		return paymentPeriod;
	}

	public TermSetNode getOrderLines() {
		return orderLines; //Set<OrderLine>
	}

	public TotalLine getTotalLine() {
		return totalLine;
	}

	public Period getValidityPeriod() {
		return validityPeriod;
	}

	public Period getCancellationPeriod() {
		return cancellationPeriod;
	}


	/*************
	 * MODIFIERS *
	 *************/

	public void setDeliveryPeriod(Formula newBegin, Formula newEnd) {
		deliveryPeriod = new Period(newBegin,newEnd);
	}

	public void setDeliveryPeriod(Period newPeriod) {
		deliveryPeriod = newPeriod;
	}

	public void setPaymentPeriod(Formula newBegin, Formula newEnd) {
		paymentPeriod = new Period(newBegin, newEnd);
	}

	public void setPaymentPeriod(Period newPeriod) {
		paymentPeriod = newPeriod;
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getPurchaseOrderTerm() {
		// decompose instantiation
		try {
			Term result = (Term)purchaseOrderPattern.getClone();

			SL.set(result, "id",id);
			SL.set(result, "issueDateTime",issueDateTime);
			SL.set(result, "customerAID",Tools.AID2Term(customerAID));
			SL.set(result, "supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result, "deliveryPeriod",deliveryPeriod.getPeriodTerm());
			SL.set(result, "paymentPeriod",paymentPeriod.getPeriodTerm());
			SL.set(result, "orderLines",orderLines); 
			SL.set(result, "totalLine",totalLine.getTotalLineTerm());
			SL.set(result, "validityPeriod",validityPeriod.getPeriodTerm());
			SL.set(result, "cancellationPeriod",cancellationPeriod.getPeriodTerm());
			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate purchaseOrderPattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		String decal = "\t\t";
		String s = "PURCHASE ORDER " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - validityPeriod = "+validityPeriod.toString(decal) +
		"\n \t - deliveryPeriod = "+deliveryPeriod.toString(decal) + 
		"\n \t - paymentPeriod = "+paymentPeriod.toString(decal) +
		"\n \t - cancellationPeriod = "+cancellationPeriod.toString(decal) +
		"\n \t - orderLines = "+DocumentElement.elementSetToString(orderLines,decal) +
		"\n \t - totalLine = "+totalLine.toString(decal);
		return s;
	}

}


//NOT a document
class OrderLine extends DocumentElement {

	/***********
	 * PATTERN *
	 ***********/
	public static final Term orderLinePattern = 
		SL.term("(orderLine " +
				":itemReference ??itemReference " +
				":catalogueReference ??catalogueReference " +
				"(::? :itemName ??itemName) " +
				"(::? :itemDescription ??itemDescription) " +
				":quantity ??quantity " +
				":unitPrice ??unitPrice " +
		":lineNetAmount ??lineNetAmount)");

	/**************
	 * ATTRIBUTES *
	 **************/
	// required
	StringConstantNode itemReference;
	StringConstantNode catalogueReference;
	IntegerConstantNode quantity;
	RealConstantNode unitPrice;
	RealConstantNode lineNetAmount;
	// optional
	StringConstantNode itemName;
	StringConstantNode itemDescription;

	/****************
	 * CONSTRUCTORS *
	 ****************/
	// from term
	public OrderLine(Term orderLineTerm) {
		MatchResult mr = orderLinePattern.match(orderLineTerm);
		if (mr != null) {
			itemReference = (StringConstantNode)mr.term("itemReference");
			catalogueReference = (StringConstantNode)mr.term("catalogueReference");
			quantity = (IntegerConstantNode)mr.term("quantity");
			unitPrice = (RealConstantNode)mr.term("unitPrice");
			lineNetAmount = (RealConstantNode)mr.term("lineNetAmount");
			// item name and description possibly null
			itemName = (StringConstantNode)mr.term("itemName");
			itemDescription = (StringConstantNode)mr.term("itemDescription");
		}
		else {
			System.err.println("error while creating new OrderLine from term: "+orderLineTerm);
		}
	}

	// constructor with all attributes
	public OrderLine(StringConstantNode olItemReference,
			StringConstantNode olCatalogueReference,
			IntegerConstantNode olQuantity,
			RealConstantNode olUnitPrice,
			RealConstantNode olLineNetAmount,
			StringConstantNode olItemName,
			StringConstantNode olItemDescription
	) {
		itemReference = olItemReference;
		catalogueReference = olCatalogueReference;
		quantity = olQuantity;
		unitPrice = olUnitPrice;
		lineNetAmount = olLineNetAmount;
		itemName = olItemName;
		itemDescription = olItemDescription;
	}


	// constructor 2: with name, without description
	public OrderLine(StringConstantNode olItemReference,
			StringConstantNode olCatalogueReference,
			IntegerConstantNode olQuantity,
			RealConstantNode olUnitPrice,
			RealConstantNode olLineNetAmount,
			StringConstantNode olItemName
	) {
		this(olItemReference,olCatalogueReference,olQuantity,olUnitPrice,olLineNetAmount,olItemName,
				(StringConstantNode)SL.string(""));
	}
	// constructor with description, without name -> does not make sense


	// constructor without name, without description
	public OrderLine(StringConstantNode olItemReference,
			StringConstantNode olCatalogueReference,
			IntegerConstantNode olQuantity,
			RealConstantNode olUnitPrice,
			RealConstantNode olLineNetAmount
	) {
		this(olItemReference,olCatalogueReference,olQuantity,olUnitPrice,olLineNetAmount,
				// default values are empty strings
				(StringConstantNode)SL.string(""),
				(StringConstantNode)SL.string(""));
	}


	/*************
	 * ACCESSORS *
	 *************/
	public StringConstantNode getItemReference() {
		return itemReference;
	}

	public StringConstantNode getCatalogueReference() {
		return catalogueReference;
	}

	public IntegerConstantNode getQuantity() {
		return quantity;
	}

	public RealConstantNode getUnitPrice() {
		return unitPrice;
	}

	public RealConstantNode getLineNetAmount() {
		return lineNetAmount;
	}

	public StringConstantNode getItemName() {
		return itemName;
	}

	public StringConstantNode getItemDescription() {
		return itemDescription;
	}

	/**********************
	 * CONVERSION TO TERM *
	 **********************/
	// transform the current instance of OrderLine (object o) into a SL term t representing it
	// (actually BusinessFunctionalTerms.buildOrderLine(t) = o) 
	public Term getOrderLineTerm() {
		// decompose the instantiation (no method instantiate exists with so much parameters)
		try {
			Term result = (Term)orderLinePattern.getClone();
			SL.set(result, "itemReference",itemReference);
			SL.set(result, "catalogueReference",catalogueReference);
			SL.set(result, "quantity",quantity);
			SL.set(result, "unitPrice",unitPrice);
			SL.set(result, "lineNetAmount",lineNetAmount);
			SL.set(result, "itemName",itemName);
			SL.set(result, "itemDescription",itemDescription);
			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate orderLinePattern");
			e.printStackTrace();
			return null;
		}
	}

	/***********
	 * DISPLAY *
	 ***********/
	
	public String toString(String decal) {
		return "ORDER LINE " +
				"\n"+decal+" - itemReference = "+itemReference +
				"\n"+decal+" - catalogueReference = "+catalogueReference +
				// optional item name and description
				"\n"+decal+" - itemName = "+itemName +
				"\n"+decal+" - itemDescription = "+itemDescription +
				"\n"+decal+" - quantity = "+quantity +
				"\n"+decal+" - unitPrice = "+unitPrice +
				"\n"+decal+" - lineNetAmount = "+lineNetAmount;
	}
	
}


//total line of a purchase order
class TotalLine extends DocumentElement {

	/***********
	 * PATTERN *
	 ***********/
	public final static Term totalLinePattern = 
		SL.term("(totalLine " +
				":nbOfOrderLines ??nbOfOrderLines " +
		":totalNetAmount ??totalNetAmount)");


	/**************
	 * ATTRIBUTES *
	 **************/
	// required
	IntegerConstantNode nbOfOrderLines;
	RealConstantNode totalNetAmount;

	/****************
	 * CONSTRUCTORS *
	 ****************/
	// from term
	public TotalLine(Term totalLineTerm) {
		MatchResult mr = totalLinePattern.match(totalLineTerm);
		if (mr != null) {
			nbOfOrderLines = (IntegerConstantNode)mr.term("nbOfOrderLines");
			totalNetAmount = (RealConstantNode)mr.term("totalNetAmount");
		}
		else {
			System.err.println("exception while building new TotalLine from term: "+totalLineTerm);
		}
	}

	// standard constructor
	public TotalLine(IntegerConstantNode tlNbOfOrderLines,
			RealConstantNode tlTotalNetAmount) {
		nbOfOrderLines = tlNbOfOrderLines;
		totalNetAmount = tlTotalNetAmount;	
	}


	/*************
	 * ACCESSORS *
	 *************/
	public IntegerConstantNode getNbOfOrderLines() {
		return nbOfOrderLines;
	}

	public RealConstantNode getTotalNetAmount() {
		return totalNetAmount;
	}

	public Term getTotalLineTerm() {
		return (Term)SL.instantiate(totalLinePattern,
				"nbOfOrderLines",nbOfOrderLines,
				"totalNetAmount",totalNetAmount);
	}
	
	/***********
	 * DISPLAY *
	 ***********/
	
	public String toString(String decal) {
		return "\n"+decal+"TOTAL LINE " +
				"\n"+decal+"\t - number of order lines = "+nbOfOrderLines +
				"\n"+decal+"\t - total net amount = "+totalNetAmount;
	}

}



class ModifiedPeriod extends DocumentElement {

	// pattern
	public static final Term modifiedPeriodPattern = 
		SL.term("(modifiedPeriod " +
				":period ??period " +
				"(::? :modificationReason ??modificationReason) " +
		"(::? :modificationNote ??modificationNote))");

	// required attributes
	Period period;
	//optional attributes
	StringConstantNode modificationNote;
	FactNode modificationReason;

	// constructor from term
	public ModifiedPeriod(Term modifiedPeriodTerm) {
		MatchResult mr = modifiedPeriodPattern.match(modifiedPeriodTerm);
		if (mr != null) {
			period = new Period(mr.term("period"));
			modificationNote = (StringConstantNode)mr.term("modificationNote");
			modificationReason = (FactNode)mr.term("modificationReason");
		}
		else {
			System.err.println("exception while building new ModifiedPeriod from term: "+modifiedPeriodTerm);
		}
	}

	// full constructor
	public ModifiedPeriod(Period mpPeriod, StringConstantNode mpModificationNote, FactNode mpModificationReason) {
		period = mpPeriod;
		modificationNote = mpModificationNote;
		modificationReason = mpModificationReason;
	}

	// partial constructor with default init of optional attributes
	public ModifiedPeriod(Period mpPeriod, StringConstantNode mpModificationNote) {
		this(mpPeriod,mpModificationNote,new FactNode(SL.TRUE));
	}

	public ModifiedPeriod(Period mpPeriod, FactNode mpModificationReason) {
		this(mpPeriod,(StringConstantNode)SL.string(""),mpModificationReason);
	}

	public ModifiedPeriod(Period mpPeriod) {
		this(mpPeriod,(StringConstantNode)SL.string(""),new FactNode(SL.TRUE));
	}

	// accessors
	public Period getPeriod() {
		return period;
	}

	public StringConstantNode getModificationNote() {
		return modificationNote;
	}

	public FactNode getModificationReason() {
		return modificationReason;
	}

	// conversion to term
	public Term getModifiedPeriodTerm() {
		return (Term) SL.instantiate(modifiedPeriodPattern,
				"period",period.getPeriodTerm(),
				"modificationNote",modificationNote,
				"modificationReason",modificationReason
		);
	}
	
	/***********
	 * DISPLAY *
	 ***********/
	
	public String toString(String decal) {
		return "MODIFIED PERIOD " +
				"\n"+decal+"\t - period = "+period +
				// optional reason and note
				"\n"+decal+"\t - modificationReason = "+modificationReason +
				"\n"+decal+"\t - modificationNote = "+modificationNote;
	}
	
}


class ModifiedOrderLine extends DocumentElement {

	/***********
	 * PATTERN *
	 ***********/
	public static final Term modifiedOrderLinePattern = 
		SL.term("(modifiedOrderLine " +
				":orderLine ??orderLine " +
				"(::? :modificationNote ??modificationNote) " +
		"(::? :modificationReason ??modificationReason))");

	/**************
	 * ATTRIBUTES *
	 **************/
	// required
	OrderLine orderLine;
	//optional
	StringConstantNode modificationNote;
	FactNode modificationReason;


	/****************
	 * CONSTRUCTORS *
	 ****************/
	// constructor from term: never used

	// full constructor
	public ModifiedOrderLine(OrderLine molOrderLine, StringConstantNode molModificationNote, FactNode molModificationReason) {
		orderLine = molOrderLine;
		modificationNote = molModificationNote;
		modificationReason = molModificationReason;
	}

	// partial constructor with default init of optional attributes
	public ModifiedOrderLine(OrderLine molOrderLine, StringConstantNode molModificationNote) {
		this(molOrderLine,molModificationNote,new FactNode(SL.TRUE));
	}

	public ModifiedOrderLine(OrderLine molOrderLine, FactNode molModificationReason) {
		this(molOrderLine,(StringConstantNode)SL.string(""),molModificationReason);
	}

	public ModifiedOrderLine(OrderLine molOrderLine) {
		this(molOrderLine,(StringConstantNode)SL.string(""),new FactNode(SL.TRUE));
	}

	// from term
	public ModifiedOrderLine(Term modifiedOrderLineTerm) {
		MatchResult mr = modifiedOrderLinePattern.match(modifiedOrderLineTerm);
		if (mr != null) {
			orderLine = new OrderLine(mr.term("orderLine"));
			modificationNote = (StringConstantNode)mr.term("modificationNote");
			modificationReason = (FactNode)mr.term("modificationReason");
		}
		else {
			System.err.println("exception while building new ModifiedOrderLine from term: "+modifiedOrderLineTerm);
		}
	}
	

	/*************
	 * ACCESSORS *
	 *************/

	public OrderLine getOrderLine() {
		return orderLine;
	}

	public StringConstantNode getModificationNote() {
		return modificationNote;
	}

	public FactNode getModificationReason() {
		return modificationReason;
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getModifiedOrderLineTerm() {
		return (Term) SL.instantiate(modifiedOrderLinePattern,
				"orderLine",orderLine.getOrderLineTerm(),
				"modificationNote",modificationNote,
				"modificationReason",modificationReason
		);
	}
	
	/***********
	 * DISPLAY *
	 ***********/
	
	public String toString(String decal) {
		return "MODIFIED ORDER LINE " +
			"\n"+decal+"\t - orderLine = "+orderLine.toString("\t"+decal) +
			"\n"+decal+"\t - modificationNote = "+modificationNote +
			"\n"+decal+"\t - modificationReason = "+modificationReason;
	}
}


abstract class Response extends BusinessDocument {

	/***********
	 * PATTERN *
	 ***********/
	// response pattern (unifying simple and detailed types of answers
	public static final Term responsePattern = SL.term("(response " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":customerAID ??customerAID " +
			":supplierAID ??supplierAID " +
			":orderReference ??orderReference " +
			"(::? :validityPeriod ??validityPeriod) " +
			":acceptedIndicator ??acceptedIndicator " +
			"(::? :rejectionNote ??rejectionNote) " +
			"(::? :rejectionReason ??rejectionReason) " +
			"(::? :modifiedDeliveryPeriod ??modifiedDeliveryPeriod) " +
			"(::? :modifiedPaymentPeriod ??modifiedPaymentPeriod) " +
			"(::? :modifiedCancellationPeriod ??modifiedCancellationPeriod) " +
			"(::? :modifiedOrderLines ??modifiedOrderLines) " +
	"(::? :modifiedTotalLine ??modifiedTotalLine))");


	/**************
	 * ATTRIBUTES *
	 **************/
	// 2 required
	StringConstantNode orderReference;
	// limited range of values, see constants ("accepted", "rejected", "modified")
	StringConstantNode acceptedIndicator; 
	
	// 1 optional
	Period validityPeriod;

	/*************
	 * CONSTANTS *
	 *************/
	public static final String MODIFIED = "modified";
	public static final String ACCEPTED = "accepted";
	public static final String REJECTED = "rejected";

	/****************
	 * CONSTRUCTORS * 
	 ****************/
	// from term
	public Response(Term responseTerm) {
		super(responseTerm,responsePattern);
		// only initialise common attributes
		// will be called with super() in constructors of subclasses
		MatchResult mr = responsePattern.match(responseTerm);
		if (mr != null) {
			orderReference = (StringConstantNode)mr.term("orderReference");
			acceptedIndicator = (StringConstantNode)mr.term("acceptedIndicator");
			// optional: possibly null
			Term valid = mr.term("validityPeriod");
			if (valid == null) {
				System.err.println("WARNING : response term "+responseTerm+" has a null validity period !!");
				validityPeriod = Period.eternalPeriod();
			}
			else {
				validityPeriod = new Period(mr.term("validityPeriod"));
			}
		}
		else {
			System.err.println("error while building generic response from term: "+responseTerm);
		}
	}

	// full constructor
	public Response(
			StringConstantNode rId,
			DateTimeConstantNode rIssueDateTime,
			AID rCustomerAID,
			AID rSupplierAID,
			StringConstantNode rOrderReference,
			StringConstantNode rAcceptedIndicator,
			Period rValidityPeriod
	) {
		super(rId,rIssueDateTime,rCustomerAID,rSupplierAID);
		orderReference = rOrderReference;
		acceptedIndicator = rAcceptedIndicator;
		validityPeriod = rValidityPeriod;
	}


	// constructor without validity period
	public Response(
			StringConstantNode rId,
			DateTimeConstantNode rIssueDateTime,
			AID rCustomerAID,
			AID rSupplierAID,
			StringConstantNode rOrderReference,
			StringConstantNode rAcceptedIndicator) {
		this(rId,rIssueDateTime,rCustomerAID,rSupplierAID,rOrderReference,rAcceptedIndicator,
				// default validity period
				Period.eternalPeriod()
		);
	}

	/*************
	 * MODIFIERS *
	 *************/


	/*******************
	 * OTHER METHODS *
	 ***************/

	public static Response buildAppropriateTypeOfAnswer(Term responseTerm) {
		MatchResult mr = responsePattern.match(responseTerm);
		if (mr != null) {
			String indic = ((StringConstantNode)mr.term("acceptedIndicator")).stringValue();
			Response responseObject;
			if (indic.equals(ACCEPTED)) {
				responseObject = new SimpleAcceptResponse(responseTerm);
			}
			else if (indic.equals(REJECTED)) {
				responseObject = new SimpleRejectResponse(responseTerm);
			}
			else if (indic.equals(MODIFIED)) {
				responseObject = new DetailedModifiedResponse(responseTerm);
			}
			else {
				responseObject = null;
			}
			return responseObject;
		}
		return null;
	}


	/********************************
	 * common ACCESSORS             *
	 * (for all types of responses) *
	 ********************************/

	public Term getPattern() {
		return responsePattern;
	}

	public StringConstantNode getOrderReference() {
		return orderReference;
	}

	public StringConstantNode getAcceptedIndicator() {
		return acceptedIndicator; 
		// limited range of values!! : "accepted", "rejected", "modified"
	}

	public Period getValidityPeriod() {
		return validityPeriod;
	}


	/*************************************
	 * CONVERSION TO TERM                *
	 * specific to each type of response *
	 *************************************/

	public abstract Term getResponseTerm();

}


class SimpleAcceptResponse extends Response {

	// no additional attribute

	/****************
	 * CONSTRUCTORS *
	 ****************/

	// from the values of attributes
	public SimpleAcceptResponse(StringConstantNode rId,
			DateTimeConstantNode rIssueDateTime,
			AID rCustomerAID,
			AID rSupplierAID,
			StringConstantNode rOrderReference) {
		super(rId,rIssueDateTime,rCustomerAID,rSupplierAID,rOrderReference,
				// accepted indicator = accepted
				(StringConstantNode)SL.string(ACCEPTED));		
	}

	// constructor from term
	public SimpleAcceptResponse(Term simpleAcceptResponseTerm) {
		super(simpleAcceptResponseTerm);
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getResponseTerm() {
		try {
			Term result = (Term)responsePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"orderReference",orderReference);
			SL.set(result,"acceptedIndicator",acceptedIndicator);
			// do not instantiate useless optional attributes

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate catalogueItemPattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "ACCEPTANCE RESPONSE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - orderReference = "+orderReference +
		// optional validityPeriod
		"\n \t - validityPeriod = "+validityPeriod.toString("\t\t");
	}

}//end simpleAcceptResponse



class SimpleRejectResponse extends Response {

	/**************
	 * ATTRIBUTES *
	 **************/

	// optional, even for a rejection response
	StringConstantNode rejectionNote;
	FactNode rejectionReason;


	/****************
	 * CONSTRUCTORS *
	 ****************/

	// constructor from term
	public SimpleRejectResponse(Term simpleRejectResponse) {
		super(simpleRejectResponse);
		// compute mr a second time (FIXME: already computed in super() )
		MatchResult mr = responsePattern.match(simpleRejectResponse);
		rejectionNote = (StringConstantNode)mr.term("rejectionNote");
		rejectionReason = (FactNode)mr.term("rejectionReason");
	}

	// standard constructor
	public SimpleRejectResponse(StringConstantNode rId,
			DateTimeConstantNode rIssueDateTime,
			AID rCustomerAID,
			AID rSupplierAID,
			StringConstantNode rOrderReference,
			StringConstantNode rRejectionNote,
			FactNode rRejectionReason) {
		super(rId,rIssueDateTime,rCustomerAID,rSupplierAID,rOrderReference,
				// fixed value for accepted indicator: rejected 
				(StringConstantNode)SL.string(REJECTED));
		// initialisation of additional optional attributes
		rejectionReason = rRejectionReason;
		rejectionNote = rRejectionNote;
	}

	public SimpleRejectResponse(StringConstantNode rId,
			DateTimeConstantNode rIssueDateTime,
			AID rCustomerAID,
			AID rSupplierAID,
			StringConstantNode rOrderReference) {
		this(rId,rIssueDateTime,rCustomerAID,rSupplierAID,rOrderReference,
				// default values of optional attributes
				(StringConstantNode)SL.string(""),
				new FactNode(SL.TRUE));
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getResponseTerm() {
		try {
			Term result = (Term)responsePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"orderReference",orderReference);
			SL.set(result,"acceptedIndicator",acceptedIndicator);
			// useful optional attributes
			SL.set(result,"rejectionNote",rejectionNote);
			SL.set(result,"rejectionReason",rejectionReason);
			// do not instantiate useless optional attributes

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate catalogueItemPattern");
			e.printStackTrace();
			return null;
		}
	}

	public FactNode getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(Formula reason) {
		rejectionReason = new FactNode(reason);
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "REJECTION RESPONSE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - orderReference = "+orderReference +
		// optional validP
		"\n \t - validityPeriod = "+validityPeriod.toString("\t\t") +
		// optional rejectionN, rejR
		"\n \t - rejectionNote = "+rejectionNote +
		"\n \t - rejectionReason = "+rejectionReason;
	}

}//end simpleRejectResponse


class DetailedModifiedResponse extends Response {

	/**************
	 * ATTRIBUTES *
	 **************/

	// optional, depend on the modifications done
	ModifiedPeriod modifiedDeliveryPeriod;
	ModifiedPeriod modifiedPaymentPeriod;
	ModifiedPeriod modifiedCancellationPeriod;
	TermSetNode modifiedOrderLines;  //Set<ModifiedOrderLine>
	TotalLine modifiedTotalLine;


	/****************
	 * CONSTRUCTORS *
	 ****************/

	// constructor from term
	public DetailedModifiedResponse(Term detailedModifiedResponseTerm) {
		super(detailedModifiedResponseTerm);
		MatchResult mr = responsePattern.match(detailedModifiedResponseTerm);
		modifiedDeliveryPeriod = new ModifiedPeriod(mr.term("modifiedDeliveryPeriod"));
		modifiedPaymentPeriod = new ModifiedPeriod(mr.term("modifiedPaymentPeriod"));
		modifiedCancellationPeriod = new ModifiedPeriod(mr.term("modifiedCancellationPeriod"));
		modifiedOrderLines = (TermSetNode)mr.term("modifiedOrderLines");
		modifiedTotalLine = new TotalLine(mr.term("modifiedTotalLine"));
	}

	/* Several subtypes of modified responses: 
	 * 		- modification of one of the three periods,
	 *      - or modification of lines (and thus also of the total), 
	 *      - or both lines and period(s)
	 */

	// constructor for double modification (in lines and periods)
	public DetailedModifiedResponse(StringConstantNode rId,
			DateTimeConstantNode rIssueDateTime,
			AID rCustomerAID,
			AID rSupplierAID,
			StringConstantNode rOrderReference,
			TermSetNode rModifiedOrderLines,
			TotalLine rModifiedTotalLine,
			ModifiedPeriod rModifiedDeliveryPeriod,
			ModifiedPeriod rModifiedPaymentPeriod,
			ModifiedPeriod rModifiedCancellationPeriod) {
		super(rId,rIssueDateTime,rCustomerAID,rSupplierAID,rOrderReference,
				(StringConstantNode)SL.string(MODIFIED));
		// initialisation of additional attributes
		modifiedOrderLines = rModifiedOrderLines;
		modifiedTotalLine = rModifiedTotalLine;
		modifiedDeliveryPeriod = rModifiedDeliveryPeriod;
		modifiedPaymentPeriod = rModifiedPaymentPeriod;
		modifiedCancellationPeriod = rModifiedCancellationPeriod;
	}

	// constructor for a modification in lines
	public DetailedModifiedResponse(StringConstantNode rId,
			DateTimeConstantNode rIssueDateTime,
			AID rCustomerAID,
			AID rSupplierAID,
			StringConstantNode rOrderReference,
			TermSetNode rModifiedOrderLines,
			TotalLine rModifiedTotalLine) {
		this(rId,rIssueDateTime,rCustomerAID,rSupplierAID,rOrderReference,
				rModifiedOrderLines,rModifiedTotalLine,null,null,null);
		// modified periods are null
	}

	// constructor for a modification on one of the periods 
	// (unmodified periods should be null)
	public DetailedModifiedResponse(StringConstantNode rId,
			DateTimeConstantNode rIssueDateTime,
			AID rCustomerAID,
			AID rSupplierAID,
			StringConstantNode rOrderReference,
			ModifiedPeriod rModifiedDeliveryPeriod,
			ModifiedPeriod rModifiedPaymentPeriod,
			ModifiedPeriod rModifiedCancellationPeriod) {
		this(rId,rIssueDateTime,rCustomerAID,rSupplierAID,rOrderReference,
				// empty set of modified lines
				new TermSetNode(),
				// null modified total line (since not modified)
				null,
				rModifiedDeliveryPeriod,
				rModifiedPaymentPeriod,
				rModifiedCancellationPeriod);
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getResponseTerm() {
		try {
			Term result = (Term)responsePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"orderReference",orderReference);
			SL.set(result,"acceptedIndicator",acceptedIndicator);
			// useful optional attributes (some of them can be null)
			SL.set(result,"modifiedDeliveryPeriod",modifiedDeliveryPeriod.getModifiedPeriodTerm());
			SL.set(result,"modifiedPaymentPeriod",modifiedPaymentPeriod.getModifiedPeriodTerm());
			SL.set(result,"modifiedCancellationPeriod",modifiedCancellationPeriod.getModifiedPeriodTerm());
			SL.set(result,"modifiedOrderLines",modifiedOrderLines);  //Set<ModifiedOrderLine>
			SL.set(result,"modifiedTotalLine",modifiedTotalLine.getTotalLineTerm());
			// do not instantiate useless optional attributes

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate catalogueItemPattern");
			e.printStackTrace();
			return null;
		}
	}//end getRespTerm()


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		String decal = "\t\t";
		return "MODIFICATION RESPONSE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - orderReference = "+orderReference +
		// optional validP
		"\n \t - validityPeriod = "+validityPeriod.toString(decal) +
		// optional modDP, modPP, modCP, modOL, modTL
		"\n \t - modifiedDeliveryPeriod = "+modifiedDeliveryPeriod.toString(decal) +
		"\n \t - modifiedPaymentPeriod = "+modifiedPaymentPeriod.toString(decal) +
		"\n \t - modifiedCancellationPeriod = "+modifiedCancellationPeriod.toString(decal) +
		"\n \t - modifiedOrderLines = "+DocumentElement.elementSetToString(modifiedOrderLines,decal) +
		"\n \t - modifiedTotalLine = "+modifiedTotalLine.toString(decal);
	}

}//end class Response



class Cancellation extends BusinessDocument {

	/***********
	 * PATTERN *
	 ***********/
	public static final Term cancellationPattern = SL.term("(cancellation " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":customerAID ??customerAID " +
			":supplierAID ??supplierAID " +
			":orderReference ??orderReference " +
			"(::? :cancellationNote ??cancellationNote) " +
			"(::? :cancellationReason ??cancellationReason))"
	);


	/**************
	 * ATTRIBUTES *
	 **************/
	//required
	StringConstantNode orderReference;

	// optional
	StringConstantNode cancellationNote;
	FactNode cancellationReason;


	/****************
	 * CONSTRUCTORS *
	 ****************/

	// constructor from term
	public Cancellation(Term cancellationTerm) {
		super(cancellationTerm,cancellationPattern);
		MatchResult mr = cancellationPattern.match(cancellationTerm);
		if (mr != null) {
			orderReference = (StringConstantNode)mr.term("orderReference");
			cancellationNote = (StringConstantNode)mr.term("cancellationNote");
			cancellationReason = (FactNode)mr.term("cancellationReason");
		}
		else {
			System.err.println("exception while creating new Cancellation from term: "+cancellationTerm);
		}
	}

	// full constructor
	public Cancellation(StringConstantNode cId, 
			DateTimeConstantNode cIssueDateTime,
			AID cCustomerAID,
			AID cSupplierAID,
			StringConstantNode cOrderReference,
			StringConstantNode cCancellationNote,
			FactNode cCancellationReason
	) {
		super(cId,cIssueDateTime,cCustomerAID,cSupplierAID);
		orderReference = cOrderReference;
		cancellationNote = cCancellationNote;
		cancellationReason = cCancellationReason;
	}

	// partial constructor with default note
	public Cancellation(StringConstantNode cId, 
			DateTimeConstantNode cIssueDateTime,
			AID cCustomerAID,
			AID cSupplierAID,
			StringConstantNode cOrderReference,
			FactNode cCancellationReason) {
		this(cId,cIssueDateTime,cCustomerAID,cSupplierAID,cOrderReference,
				(StringConstantNode)SL.string(""),cCancellationReason);
	}

	// partial constructor with default note and reason
	public Cancellation(StringConstantNode cId, 
			DateTimeConstantNode cIssueDateTime,
			AID cCustomerAID,
			AID cSupplierAID,
			StringConstantNode cOrderReference) {
		this(cId,cIssueDateTime,cCustomerAID,cSupplierAID,cOrderReference,
				(StringConstantNode)SL.string(""),new FactNode(SL.TRUE));
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return cancellationPattern;
	}


	public StringConstantNode getOrderReference() {
		return orderReference;
	}

	public StringConstantNode getCancellationNote() {
		return cancellationNote;
	}

	public FactNode getCancellationReason() {
		return cancellationReason;
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getCancellationTerm() {
		try {
			Term result = (Term)cancellationPattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"orderReference",orderReference);
			SL.set(result,"cancellationNote",cancellationNote);
			SL.set(result,"cancellationReason",cancellationReason);
			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate catalogueItemPattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "CANCELLATION "+
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - orderReference = "+orderReference +
		// optional note and reason
		"\n \t - cancellationNote = "+cancellationNote +
		"\n \t - cancellationReason = "+cancellationReason;
	}

}//end class Cancellation



class Invoice extends BusinessDocument {

	/***********
	 * PATTERN *
	 ***********/

	public static final Term invoicePattern = SL.term("(invoice " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":customerAID ??customerAID " +
			":supplierAID ??supplierAID " +
			":supplierBankAID ??supplierBankAID " +
			":latestPaymentDateTime ??latestPaymentDateTime " +
			":orderReference ??orderReference " +
			"(::? :receiptAdviceReference ??receiptAdviceReference) " + 
			"(::? :dispatchAdviceReference ??dispatchAdviceReference) " +
			"(::? :invoicedItems ??invoicedItems) " +
			":netAmount ??netAmount " +
	":vatAmount ??vatAmount)");

	/**************
	 * ATTRIBUTES *
	 **************/

	//required
	DateTimeConstantNode latestPaymentDateTime;
	StringConstantNode orderReference;
	RealConstantNode netAmount; // price without tax
	RealConstantNode vatAmount; // tax
	AID supplierBankAID;

	// optional
	StringConstantNode receiptAdviceReference;
	StringConstantNode dispatchAdviceReference;
	TermSetNode invoicedItems; //set of OrderLine if partial invoice

	/****************
	 * CONSTRUCTORS *
	 ****************/

	// from term
	public Invoice(Term invoiceTerm) {
		super(invoiceTerm,invoicePattern);
		MatchResult mr = invoicePattern.match(invoiceTerm);
		if (mr != null) {
			latestPaymentDateTime = (DateTimeConstantNode)mr.term("latestPaymentDateTime");
			orderReference = (StringConstantNode)mr.term("orderReference");
			netAmount = (RealConstantNode)mr.term("netAmount");
			vatAmount = (RealConstantNode)mr.term("vatAmount");
			supplierBankAID = Tools.term2AID(mr.term("supplierBankAID"));
			// optional: possibly null
			receiptAdviceReference = (StringConstantNode)mr.term("receiptAdviceReference");
			dispatchAdviceReference = (StringConstantNode)mr.term("dispatchAdviceReference");
			invoicedItems = (TermSetNode)mr.term("invoicedItems");
		}
		else {
			System.err.println("error while creating new Invoice from term: "+invoiceTerm);
		}
	}

	// standard - invoice for A PART OF dispatched/received items
	// (give the reference of dispatch xor receipt advice, the other ref should be null)
	public Invoice(StringConstantNode iId,
			DateTimeConstantNode iIssueDateTime,
			AID iCustomerAID,
			AID iSupplierAID,
			AID iSupplierBankAID,
			DateTimeConstantNode iLatestPaymentDateTime,
			StringConstantNode iOrderReference,
			RealConstantNode iNetAmount,
			RealConstantNode iVatAmount,
			StringConstantNode iReceiptAdviceReference,
			StringConstantNode iDispatchAdviceReference,
			TermSetNode iInvoicedItems) {
		super(iId,iIssueDateTime,iCustomerAID,iSupplierAID);
		supplierBankAID = iSupplierBankAID;
		latestPaymentDateTime = iLatestPaymentDateTime;
		orderReference = iOrderReference;
		netAmount = iNetAmount;
		vatAmount = iVatAmount;
		receiptAdviceReference = iReceiptAdviceReference;
		dispatchAdviceReference = iDispatchAdviceReference;
		invoicedItems = iInvoicedItems;
	}


	// standard - full invoice: no invoicedItems parameter
	// only give one of the receipt/dispatch advice references (the other one should be null)
	public Invoice(StringConstantNode iId,
			DateTimeConstantNode iIssueDateTime,
			AID iCustomerAID,
			AID iSupplierAID,
			AID iSupplierBankAID,
			DateTimeConstantNode iLatestPaymentDateTime,
			StringConstantNode iOrderReference,
			RealConstantNode iNetAmount,
			RealConstantNode iVatAmount,
			StringConstantNode iReceiptAdviceReference,
			StringConstantNode iDispatchAdviceReference) {
		this(iId,iIssueDateTime,iCustomerAID,iSupplierAID,iSupplierBankAID,iLatestPaymentDateTime,
				iOrderReference,iNetAmount,iVatAmount,iReceiptAdviceReference,
				iDispatchAdviceReference,new TermSetNode());
		// set of items is empty if all items are invoiced
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return invoicePattern;
	}

	public AID getSupplierBankAID() {
		return supplierBankAID;
	}

	public DateTimeConstantNode getLatestPaymentDateTime() {
		return latestPaymentDateTime;
	}

	public StringConstantNode getOrderReference() {
		return orderReference;
	}

	public RealConstantNode getNetAmount() {
		return netAmount; // price without tax
	}

	public RealConstantNode getVatAmount() {
		return vatAmount; // tax
	}

	public StringConstantNode getReceiptAdviceReference() {
		return receiptAdviceReference;
	}

	public StringConstantNode getDispatchAdviceReference() {
		return dispatchAdviceReference;
	}

	public TermSetNode getInvoicedItems() {
		return invoicedItems; //set of OrderLine if partial invoice
	}



	/**********************
	 * CONVERSION to TERM *
	 **********************/

	public Term getInvoiceTerm() {
		try {
			Term result = (Term)invoicePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"supplierBankAID",Tools.AID2Term(supplierBankAID));
			SL.set(result,"latestPaymentDateTime",latestPaymentDateTime);
			SL.set(result,"orderReference",orderReference);
			SL.set(result,"receiptAdviceReference",receiptAdviceReference); 
			SL.set(result,"dispatchAdviceReference",dispatchAdviceReference);
			SL.set(result,"invoicedItems",invoicedItems);
			SL.set(result,"netAmount",netAmount);
			SL.set(result,"vatAmount",vatAmount);
			SL.substituteMetaReferences(result);
			return result;
		}
		catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate invoicePattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "INVOICE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - supplierBankAID = "+supplierBankAID +
		"\n \t - latestPaymentDateTime = "+latestPaymentDateTime +
		"\n \t - orderReference = "+orderReference +
		// 3 optional: RAref, DAref, invoicedItems
		"\n \t - receiptAdviceReference = "+receiptAdviceReference + 
		"\n \t - dispatchAdviceReference = "+dispatchAdviceReference +
		// TODO : if empty set, write ALL instead
		"\n \t - invoicedItems = "+DocumentElement.elementSetToString(invoicedItems,"\t") +
		"\n \t - netAmount = "+netAmount +
		"\n \t - vatAmount = "+vatAmount;
	}
}


//this is not a standard Document: no customer/supplier, but debitor/creditor
class PaymentOrder extends BankDocument {

	/***********
	 * PATTERN *
	 ***********/

	public final static Term paymentOrderPattern = SL.term("(paymentOrder " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":debtorAID ??debtorAID " +
			":debtorBankAID ??debtorBankAID " +
			":creditorAID ??creditorAID " +
			":creditorBankAID ??creditorBankAID " +
			":invoiceReference ??invoiceReference " +
	":amount ??amount)");

	/**************
	 * ATTRIBUTES *
	 **************/

	AID creditorBankAID;
	AID debtorBankAID;
	StringConstantNode invoiceReference;
	RealConstantNode amount;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	// from term
	public PaymentOrder(Term paymentOrderTerm) {
		super(paymentOrderTerm,paymentOrderPattern);
		MatchResult mr = paymentOrderPattern.match(paymentOrderTerm);
		if (mr != null) {
			debtorBankAID = Tools.term2AID(mr.term("debtorBankAID"));
			creditorBankAID = Tools.term2AID(mr.term("creditorBankAID"));
			invoiceReference = (StringConstantNode)mr.term("invoiceReference");
			amount = (RealConstantNode)mr.term("amount");
		}
		else {
			System.err.println("error occurred while trying to create new PaymentOrder from term: "+paymentOrderTerm);
		}
	}

	// standard (unique since all parameters are required)
	public PaymentOrder(StringConstantNode poId,
			DateTimeConstantNode poIssueDateTime,
			AID poDebtorAID,
			AID poDebtorBankAID,
			AID poCreditorAID,
			AID poCreditorBankAID,
			StringConstantNode poInvoiceReference,
			RealConstantNode poAmount) {
		super(poId,poIssueDateTime,poCreditorAID,poDebtorAID);
		debtorBankAID = poDebtorBankAID;
		creditorBankAID = poCreditorBankAID;
		invoiceReference = poInvoiceReference;
		amount = poAmount;
	}

	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return paymentOrderPattern;
	}

	public AID getDebtorBankAID() {
		return debtorBankAID;
	}

	public AID getCreditorBankAID() {
		return creditorBankAID;
	}

	public StringConstantNode getInvoiceReference() {
		return invoiceReference;
	}

	public RealConstantNode getAmount() {
		return amount;
	}

	/**********************
	 * CONVERSION to TERM *
	 **********************/

	public Term getPaymentOrderTerm() {
		try {
			Term result = (Term)paymentOrderPattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"debtorAID",Tools.AID2Term(debtorAID));
			SL.set(result,"debtorBankAID",Tools.AID2Term(debtorBankAID));
			SL.set(result,"creditorAID",Tools.AID2Term(creditorAID));
			SL.set(result,"creditorBankAID",Tools.AID2Term(creditorBankAID));
			SL.set(result,"invoiceReference",invoiceReference);
			SL.set(result,"amount",amount);
			SL.substituteMetaReferences(result);
			return result;
		}
		catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate paymentOrderPattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "PAYMENT ORDER " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - debtorAID = "+debtorAID +
		"\n \t - debtorBankAID = "+debtorBankAID +
		"\n \t - creditorAID = "+creditorAID +
		"\n \t - creditorBankAID = "+creditorBankAID +
		"\n \t - invoiceReference = "+invoiceReference +
		"\n \t - amount = "+amount;
	}

}//end class PaymentOrder



class PaymentAdvice extends BankDocument {

	/***********
	 * PATTERN *
	 ***********/

	public static final Term paymentAdvicePattern = SL.term("(paymentAdvice " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":debtorAID ??debtorAID " +
			":creditorAID ??creditorAID " +
			":invoiceReference ??invoiceReference " +
			":amount ??amount " +
	":paymentDateTime ??paymentDateTime)");


	/**************
	 * ATTRIBUTES *
	 **************/
	StringConstantNode invoiceReference;
	RealConstantNode amount;
	DateTimeConstantNode paymentDateTime;


	/****************
	 * CONSTRUCTORS *
	 ****************/

	// from term
	public PaymentAdvice(Term paymentAdviceTerm) {
		super(paymentAdviceTerm,paymentAdvicePattern);
		MatchResult mr = paymentAdvicePattern.match(paymentAdviceTerm);
		if (mr!=null) {
			invoiceReference = (StringConstantNode)mr.term("invoiceReference");
			amount = (RealConstantNode)mr.term("amount");
			paymentDateTime = (DateTimeConstantNode)mr.term("paymentDateTime");
		}
		else {
			System.err.println("exception while building new PaymentAdvice from term: "+paymentAdviceTerm);
		}
	}

	// standard
	public PaymentAdvice(StringConstantNode paId,
			DateTimeConstantNode paIssueDateTime,
			AID paDebtorAID,
			AID paCreditorAID,
			StringConstantNode paInvoiceReference,
			RealConstantNode paAmount,
			DateTimeConstantNode paPaymentDateTime) {
		super(paId,paIssueDateTime,paCreditorAID,paDebtorAID);
		invoiceReference = paInvoiceReference;
		amount = paAmount;
		paymentDateTime = paPaymentDateTime;
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return paymentAdvicePattern;
	}

	public StringConstantNode getInvoiceReference() {
		return invoiceReference;
	}

	public RealConstantNode getAmount() {
		return amount;
	}

	public DateTimeConstantNode getPaymentDateTime() {
		return paymentDateTime;
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getPaymentAdviceTerm() {
		try {
			Term result = (Term)paymentAdvicePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"debtorAID",Tools.AID2Term(debtorAID));
			SL.set(result,"creditorAID",Tools.AID2Term(creditorAID));
			SL.set(result,"invoiceReference",invoiceReference);
			SL.set(result,"amount",amount);
			SL.set(result,"paymentDateTime",paymentDateTime);
			SL.substituteMetaReferences(result);
			return result;
		}
		catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate paymentAdvicePattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "PAYMENT ADVICE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - debtorAID = "+debtorAID +
		"\n \t - creditorAID = "+creditorAID +
		"\n \t - invoiceReference = "+invoiceReference +
		"\n \t - amount = "+amount +
		"\n \t - paymentDateTime = "+paymentDateTime;
	}
}//end class PaymentAdvice



class ReceiptAdvice extends BusinessDocument {

	/***********
	 * PATTERN *
	 ***********/

	public static final Term receiptAdvicePattern = SL.term("(receiptAdvice " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":customerAID ??customerAID " +
			":supplierAID ??supplierAID " +
			":orderReference ??orderReference " +
			"(::? :receivedItems ??receivedItems) " +
	":receiptDateTime ??receiptDateTime)");


	/**************
	 * ATTRIBUTES *
	 **************/
	// required
	StringConstantNode orderReference;
	DateTimeConstantNode receiptDateTime;
	//optional
	TermSetNode receivedItems; //set of orderLine


	/****************
	 * CONSTRUCTORS *
	 ****************/

	// from term
	public ReceiptAdvice(Term receiptAdviceTerm) {
		super(receiptAdviceTerm,receiptAdvicePattern);
		MatchResult mr = receiptAdvicePattern.match(receiptAdviceTerm);
		if (mr!=null) {
			orderReference = (StringConstantNode)mr.term("orderReference");
			receiptDateTime = (DateTimeConstantNode)mr.term("receiptDateTime");
			receivedItems = (TermSetNode)mr.term("receivedItems"); // FIXME cast: what if the object is null?
		}
	}

	// standard full
	public ReceiptAdvice(StringConstantNode raId,
			DateTimeConstantNode raIssueDateTime,
			AID raCustomerAID,
			AID raSupplierAID,
			StringConstantNode raOrderReference,
			DateTimeConstantNode raReceiptDateTime,
			TermSetNode raReceivedItems) {
		super(raId,raIssueDateTime,raCustomerAID,raSupplierAID);
		orderReference = raOrderReference;
		receiptDateTime = raReceiptDateTime;
		receivedItems = raReceivedItems;
	}

	// partial (if all items were received)
	public ReceiptAdvice(StringConstantNode raId,
			DateTimeConstantNode raIssueDateTime,
			AID raCustomerAID,
			AID raSupplierAID,
			StringConstantNode raOrderReference,
			DateTimeConstantNode raReceiptDateTime) {
		super(raId,raIssueDateTime,raCustomerAID,raSupplierAID);
		orderReference = raOrderReference;
		receiptDateTime = raReceiptDateTime;
		receivedItems = new TermSetNode();
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return receiptAdvicePattern;
	}

	public StringConstantNode getOrderReference() {
		return orderReference;
	}

	public DateTimeConstantNode getReceiptDateTime() {
		return receiptDateTime;
	}

	public TermSetNode getReceivedItems() {
		return receivedItems; //set of orderLine
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getReceiptAdviceTerm() {
		try {
			Term result = (Term)receiptAdvicePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"orderReference",orderReference);
			SL.set(result,"receiptDateTime",receiptDateTime);
			SL.set(result,"receivedItems",receivedItems);
			SL.substituteMetaReferences(result);
			return result;
		}
		catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate receiptAdvicePattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "RECEIPT ADVICE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - orderReference = "+orderReference +
		// optional list
		"\n \t - receivedItems = "+DocumentElement.elementSetToString(receivedItems,"\t") +
		"\n \t - receiptDateTime = "+receiptDateTime;
	}

}//end class ReceiptAdvice



class DispatchAdvice extends BusinessDocument {

	/***********
	 * PATTERN *
	 ***********/

	public static final Term dispatchAdvicePattern = SL.term("(dispatchAdvice " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":customerAID ??customerAID " +
			":supplierAID ??supplierAID " +
			":orderReference ??orderReference " +
			"(::? :dispatchedItems ??dispatchedItems) " +
			":dispatchDateTime ??dispatchDateTime " +
	"(::? :expectedDeliveryDateTime ??expectedDeliveryDateTime))");

	/**************
	 * ATTRIBUTES *
	 **************/
	// required
	StringConstantNode orderReference;
	DateTimeConstantNode dispatchDateTime;
	//optional
	TermSetNode dispatchedItems; //set of orderLine
	DateTimeConstantNode expectedDeliveryDateTime;


	/****************
	 * CONSTRUCTORS *
	 ****************/

	// from term
	public DispatchAdvice(Term dispatchAdviceTerm) {
		super(dispatchAdviceTerm,dispatchAdvicePattern);
		MatchResult mr = dispatchAdvicePattern.match(dispatchAdviceTerm);
		if (mr!=null) {
			orderReference = (StringConstantNode)mr.term("orderReference");
			dispatchDateTime = (DateTimeConstantNode)mr.term("dispatchDateTime");
			dispatchedItems = (TermSetNode)mr.term("dispatchedItems"); // FIXME cast: what if the object is null?
			expectedDeliveryDateTime = (DateTimeConstantNode)mr.term("expectedDeliveryDateTime");
		}
		else {
			System.err.println("NO MATCH !! unable to build dispatch advice from term : "+dispatchAdviceTerm);
		}
	}

	// standard full
	public DispatchAdvice(StringConstantNode daId,
			DateTimeConstantNode daIssueDateTime,
			AID daCustomerAID,
			AID daSupplierAID,
			StringConstantNode daOrderReference,
			DateTimeConstantNode daDispatchDateTime,
			TermSetNode daDispatchedItems,
			DateTimeConstantNode daExpectedDeliveryDateTime) {
		super(daId,daIssueDateTime,daCustomerAID,daSupplierAID);
		orderReference = daOrderReference;
		dispatchDateTime = daDispatchDateTime;
		dispatchedItems = daDispatchedItems;
		expectedDeliveryDateTime = daExpectedDeliveryDateTime;
	}

	// partial (if all items were received) - still with expected delivery DT
	public DispatchAdvice(StringConstantNode daId,
			DateTimeConstantNode daIssueDateTime,
			AID daCustomerAID,
			AID daSupplierAID,
			StringConstantNode daOrderReference,
			DateTimeConstantNode daDispatchDateTime,
			DateTimeConstantNode daExpectedDeliveryDateTime) {
		this(daId,daIssueDateTime,daCustomerAID,daSupplierAID,
				daOrderReference,daDispatchDateTime,
				// empty set of items if all were dispatched
				new TermSetNode(),
				daExpectedDeliveryDateTime);
	}

	// partial (list of items provided, but no expected date)
	public DispatchAdvice(StringConstantNode daId,
			DateTimeConstantNode daIssueDateTime,
			AID daCustomerAID,
			AID daSupplierAID,
			StringConstantNode daOrderReference,
			DateTimeConstantNode daDispatchDateTime,
			TermSetNode daDispatchedItems) {
		this(daId,daIssueDateTime,daCustomerAID,daSupplierAID,
				daOrderReference,daDispatchDateTime,
				daDispatchedItems,BusinessFunctionalTerms.buildNewDate(daDispatchDateTime,177000));
		// default expected delivery date is +3mn
	}

	// partial (neither list of items nor expected date)
	public DispatchAdvice(StringConstantNode daId,
			DateTimeConstantNode daIssueDateTime,
			AID daCustomerAID,
			AID daSupplierAID,
			StringConstantNode daOrderReference,
			DateTimeConstantNode daDispatchDateTime) {
		this(daId,daIssueDateTime,daCustomerAID,daSupplierAID,
				daOrderReference,daDispatchDateTime,
				// default set of items and delivery date
				new TermSetNode(),
				BusinessFunctionalTerms.buildNewDate(daDispatchDateTime,177000));
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return dispatchAdvicePattern;
	}

	public StringConstantNode getOrderReference() {
		return orderReference;
	}

	public DateTimeConstantNode getDispatchDateTime() {
		return dispatchDateTime;
	}

	public TermSetNode getDispatchItems() {
		return dispatchedItems; //set of orderLine
	}

	public DateTimeConstantNode getExpectedDeliveryDateTime() {
		return expectedDeliveryDateTime;
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getDispatchAdviceTerm() {
		try {
			Term result = (Term)dispatchAdvicePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"orderReference",orderReference);
			SL.set(result,"dispatchDateTime",dispatchDateTime);
			SL.set(result,"dispatchedItems",dispatchedItems);
			SL.set(result,"expectedDeliveryDateTime",expectedDeliveryDateTime);
			SL.substituteMetaReferences(result);
			return result;
		}
		catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate dispatchAdvicePattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "DISPATCH ADVICE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - orderReference = "+orderReference +
		// optional list (write ALL instead)
		"\n \t - dispatchedItems = "+DocumentElement.elementSetToString(dispatchedItems,"\t") +
		"\n \t - dispatchDateTime = "+dispatchDateTime +
		// optional date (write asap instead)
		"\n \t - expectedDeliveryDateTime = "+expectedDeliveryDateTime;
	}
}// end class DispatchAdvice



/* Document accompanying the shipment of goods,
 * generated by the action to deliver a package,
 * physical proof of delivery for customer
 */
class DeliveryNote extends BusinessDocument {

	/***********
	 * PATTERN *
	 ***********/

	public static final Term deliveryNotePattern = SL.term("(deliveryNote " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":customerAID ??customerAID " +
			":supplierAID ??supplierAID " +
			":orderReference ??orderReference " +
	":listOfItems ??listOfItems)");

	/**************
	 * ATTRIBUTES *
	 **************/

	StringConstantNode orderReference;
	TermSetNode listOfItems;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	// standard full
	public DeliveryNote(StringConstantNode dnId,
			DateTimeConstantNode dnIssueDateTime,
			AID dnCustomerAID,
			AID dnSupplierAID,
			StringConstantNode dnOrderReference,
			TermSetNode dnListOfItems) {
		super(dnId,dnIssueDateTime,dnCustomerAID,dnSupplierAID);
		listOfItems = dnListOfItems;
		orderReference = dnOrderReference;
	}

	// from term
	public DeliveryNote(Term deliveryNoteTerm) {
		super(deliveryNoteTerm,deliveryNotePattern);
		MatchResult mr = deliveryNotePattern.match(deliveryNoteTerm);
		if (mr!=null) {
			orderReference = (StringConstantNode)mr.term("orderReference");
			listOfItems = (TermSetNode)mr.term("listOfItems");
		}
		else {
			System.err.println("unable to build delivery note from term : "+deliveryNoteTerm+" (no match)");
		}
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return deliveryNotePattern;
	};

	public TermSetNode getListOfItems() {
		return listOfItems;
	}

	public StringConstantNode getOrderReference() {
		return orderReference;
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getDeliveryNoteTerm() {
		try {
			Term result = (Term)deliveryNotePattern.getClone();
			SL.set(result,"id",id);
			SL.set(result,"issueDateTime",issueDateTime);
			SL.set(result,"customerAID",Tools.AID2Term(customerAID));
			SL.set(result,"supplierAID",Tools.AID2Term(supplierAID));
			SL.set(result,"orderReference",orderReference);
			SL.set(result,"listOfItems",listOfItems);
			SL.substituteMetaReferences(result);
			return result;
		}
		catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate deliveryNotePattern");
			e.printStackTrace();
			return null;
		}
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "DELIVERY NOTE " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - customerAID = "+customerAID +
		"\n \t - supplierAID = "+supplierAID +
		"\n \t - orderReference = "+orderReference +
		"\n \t - listOfItems = "+DocumentElement.elementSetToString(listOfItems,"\t");
	}

}//end class DeliveryNote


//physical proof of payment for seller
class PaymentNote extends BankDocument {

	/***********
	 * PATTERN *
	 ***********/

	public static final Term paymentNotePattern = SL.term("(paymentNote " +
			":id ??id " +
			":issueDateTime ??issueDateTime " +
			":debtorAID ??debtorAID " +
			":debtorBankAID ??debtorBankAID " +
			":creditorAID ??creditorAID " +
			":creditorBankAID ??creditorBankAID " +
			":invoiceReference ??invoiceReference " +
	":amount ??amount)");

	/**************
	 * ATTRIBUTES *
	 **************/

	AID debtorBankAID;
	AID creditorBankAID;
	StringConstantNode invoiceReference;
	RealConstantNode amount;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	// standard full
	public PaymentNote(StringConstantNode pnId,
			DateTimeConstantNode pnIssueDateTime,
			AID pnDebtorAID,
			AID pnDebtorBankAID,
			AID pnCreditorAID,
			AID pnCreditorBankAID,
			StringConstantNode pnInvoiceReference,
			RealConstantNode pnAmount) {
		super(pnId,pnIssueDateTime,pnCreditorAID,pnDebtorAID);
		invoiceReference = pnInvoiceReference;
		amount = pnAmount;
		debtorBankAID = pnDebtorBankAID;
		creditorBankAID = pnCreditorBankAID;
	}

	// from term
	public PaymentNote(Term paymentNoteTerm) {
		super(paymentNoteTerm,paymentNotePattern);
		MatchResult mr = paymentNotePattern.match(paymentNoteTerm);
		if (mr!=null) {
			invoiceReference = (StringConstantNode)mr.term("invoiceReference");
			amount = (RealConstantNode)mr.term("amount");
			debtorBankAID = Tools.term2AID(mr.term("debtorBankAID"));
			creditorBankAID = Tools.term2AID(mr.term("creditorBankAID"));
		}
		else {
			System.err.println("unable to build payment note from term : "+paymentNoteTerm+" (no match)");
		}
	}

	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return paymentNotePattern;
	};

	public StringConstantNode getInvoiceReference() {
		return invoiceReference;
	}

	public RealConstantNode getAmount() {
		return amount;
	}

	public AID getDebtorBankAID() {
		return debtorBankAID;
	}

	public Term getDebtorBank() {
		return Tools.AID2Term(debtorBankAID);
	}

	public AID getCreditorBankAID() {
		return creditorBankAID;
	}

	public Term getCreditorBank() {
		return Tools.AID2Term(creditorBankAID);
	}


	/**********************
	 * CONVERSION TO TERM *
	 **********************/

	public Term getPaymentNoteTerm() {
		return (Term)InstitutionTools.instantiate(paymentNotePattern, 
				"id",id,
				"issueDateTime",issueDateTime,
				"debtorAID",Tools.AID2Term(debtorAID),
				"debtorBankAID",Tools.AID2Term(debtorBankAID),
				"creditorAID",Tools.AID2Term(creditorAID),
				"creditorBankAID",Tools.AID2Term(creditorBankAID),
				"invoiceReference",invoiceReference,
				"amount",amount);
	}


	/***********
	 * DISPLAY *
	 ***********/

	public String toString() {
		return "(paymentNote " +
		"\n \t - id = "+id +
		"\n \t - issueDateTime = "+issueDateTime +
		"\n \t - debtorAID = "+debtorAID +
		"\n \t - debtorBankAID = "+debtorBankAID +
		"\n \t - creditorAID = "+creditorAID +
		"\n \t - creditorBankAID = "+creditorBankAID +
		"\n \t - invoiceReference = "+invoiceReference +
		"\n \t - amount = "+amount;
	}

}//end PaymentNote


class Delegation extends Document {

	/***********
	 * PATTERN *
	 ***********/

	public static final Term delegationPattern = SL.term("(delegation " +
			":purchaseOrder ??purchaseOrder " +
			":loaningBank ??loaningBank)");

	/**************
	 * ATTRIBUTES *
	 **************/

	private Term purchaseOrder;
	private Term loaningBank;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	public Delegation(Term delegationTerm) {
		MatchResult mr = delegationPattern.match(delegationTerm);
		if (mr!=null) {
			purchaseOrder = mr.term("purchaseOrder");
			loaningBank = mr.term("loaningBank");
		}
		else {
			System.err.println("unable to build delegation from term : "+delegationTerm+" (no match)");
		}
	}


	/*************
	 * ACCESSORS *
	 *************/

	public Term getPattern() {
		return delegationPattern;
	}

	public Term getLoaningBank() {
		return loaningBank;
	}

	public Term getPurchaseOrder() {
		return purchaseOrder;
	}


	/***********
	 * DISPLAY *
	 ***********/
	
	public String toString() {
		return "DELEGATION " +
			"\n - purchaseOrder = "+new PurchaseOrder(purchaseOrder).toString() +
			"\n - loaningBank = "+loaningBank;
	}
}// end Delegation

// response from bank to a query of agreement
class Agreement extends Document {
	
	public static final Term agreementPattern = SL.term("(agreement :acceptedIndicator ??acceptedIndicator)");
	StringConstantNode acceptedIndicator;
	
	public static final String ACCEPTED = "accepted";
	public static final String REJECTED = "rejected";
	
	public Agreement(Term agreementTerm) {
		MatchResult mr = agreementPattern.match(agreementTerm);
		if (mr != null) {
			acceptedIndicator = (StringConstantNode)mr.term("acceptedIndicator");
		}
		else {
			System.err.println("error while building agreement from term: "+agreementTerm);
		}
	}
	
	public Term getPattern() {
		return agreementPattern;
	}
	
	public String toString() {
		return acceptedIndicator.stringValue();
	}
}//end class Agreement


// query of agreement for paying in advance, sent by mediator to client's bank
class QueryAgreement extends Document {
	
	public static final Term queryAgreementPattern = 
		SL.term("(queryAgreement " +
				":customerAID ??customerAID " +
				":supplierAID ??supplierAID " +
				":amount ??amount)");

	AID customerAID;
	AID supplierAID;
	RealConstantNode amount;
	
	
	public QueryAgreement(Term queryAgreementTerm) {
		MatchResult mr = queryAgreementPattern.match(queryAgreementTerm);
		if (mr != null) {
			customerAID = Tools.term2AID(mr.term("customerAID"));
			supplierAID = Tools.term2AID(mr.term("supplierAID"));
			amount = (RealConstantNode)mr.term("amount");
		}
		else {
			System.err.println("error while creating new QueryAgreement from term: "+queryAgreementTerm);
		}
	}

	// constructor with all attributes
	public QueryAgreement(
			AID qaCustomerAID,
			AID qaSupplierAID,
			RealConstantNode qaAmount
	) {
		customerAID = qaCustomerAID;
		supplierAID = qaSupplierAID;
		amount = qaAmount;
	}
	
	public Term getPattern() {
		return queryAgreementPattern;
	}
	
	public String toString() {
		return "QUERY AGREEMENT "+
				"\n - customerAID = "+customerAID+
				"\n - supplierAID = "+supplierAID+
				"\n - amount = "+amount;
	}
	
}//end class QueryAgreement


class RequestCatalogue extends Document {
	
	AID customerAID;
	AID supplierAID;
	IntegerConstantNode delayInDays;
	
	public static final Term requestCataloguePattern = 
		SL.term("(requestCatalogue " +
				":customerAID ??customerAID " +
				":supplierAID ??supplierAID " +
				":delayInDays ??delayInDays)");

	
	public RequestCatalogue(Term requestCatalogueTerm) {
		MatchResult mr = requestCataloguePattern.match(requestCatalogueTerm);
		if (mr != null) {
			customerAID = Tools.term2AID(mr.term("customerAID"));
			supplierAID = Tools.term2AID(mr.term("supplierAID"));
			delayInDays = (IntegerConstantNode)mr.term("delayInDays"); 
		}
		else {
			System.err.println("error while creating new RequestCatalogue from term: "+requestCatalogueTerm);
		}
	}
	
	public Term getPattern() {
		return requestCataloguePattern;
	}
	
	public String toString() {
		return "REQUEST CATALOGUE "+
		"\n - customerAID = "+customerAID+
		"\n - supplierAID = "+supplierAID+
		"\n - within "+delayInDays+" days";
	}
	
}






