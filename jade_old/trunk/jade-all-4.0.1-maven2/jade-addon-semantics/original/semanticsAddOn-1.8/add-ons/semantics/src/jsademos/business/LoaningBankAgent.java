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
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.QueryResult.BoolWrapper;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.kbase.filters.FiltersDefinition;
import jade.semantics.kbase.filters.KBAssertFilterAdapter;
import jade.semantics.kbase.filters.KBQueryFilter;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jade.util.leap.Set;

import java.util.Date;

public class LoaningBankAgent extends RepresentativeAgent {

	/**************
	 * ATTRIBUTES *
	 **************/

	// pattern = (updateAccount agentId bankId credit/debit amount)
	public final static Formula updatePattern = SL.formula("(updateAccount ??agent ??bank ??type ??amount)");

	/******************************
	 * ACCESSORS to BANK ACCOUNTS *
	 ******************************/

	// UNUSED for now
	// credit money on account, returns a boolean (true if succeeded credit, false otherwise)
	public void creditMoneyOnAccountOf(Term agent,double amount) {
		Formula credit = (Formula)SL.instantiate(updatePattern,
				"agent",agent,
				"bank",getSemanticCapabilities().getAgentName(),
				"type",SL.term("credit"),
				"amount",SL.term(""+amount));
		getSemanticCapabilities().interpret(credit);
	}

	// UNUSED for now
	//	return a boolean indicating if the debit could be performed (enough money on account)
	public void debitMoneyOnAccountOf(Term agent,double amount) {

		Formula debit = (Formula)SL.instantiate(updatePattern,
				"agent",agent,
				"bank",getSemanticCapabilities().getAgentName(),
				"type",SL.term("debit"),
				"amount",SL.term(""+amount));
		getSemanticCapabilities().interpret(debit);
	}

	//	create a new account - called in BusinessAgent.setup(), when reading config files
	public void createAccount(Term agent, Double amount) {
		Formula create = (Formula)SL.instantiate(updatePattern,
				"agent",agent,
				"bank",getSemanticCapabilities().getAgentName(),
				"type",SL.term("create"),
				"amount",SL.term(""+amount));
		getSemanticCapabilities().interpret(create);
	}

	/*******************
	 * SETUP *** SETUP *
	 *******************/

	@Override
	public void setup() {
		super.setup(new LoaningBankCapabilities((String)getArguments()[0]));
		// initialisation of bank accounts of clients
	} // End of setup/0

}

class LoaningBankCapabilities extends BusinessCapabilities {

	private static final boolean DEBUG = false;

	/************
	 * COUNTERS *
	 ************/
	private static int paymentAdviceCounter = 0;
	private static int paymentNoteCounter = 0;

	public LoaningBankCapabilities(String instName) {
		// not lazy, not conscientious, but trustless
		super(instName,false,false,false);
	}


	/***************************
	 * USEFUL BUILDING METHODS *
	 ***************************/

	// generate the physical document accompanying (and proving) the payment
	public Term buildPaymentNoteFromPaymentOrder(Term paymentOrderTerm) {
		PaymentOrder paymentOrderObject = new PaymentOrder(paymentOrderTerm);
		PaymentNote paymentNoteObject = new PaymentNote(
				(StringConstantNode)SL.string("PAY_NOTE_"+paymentNoteCounter++),
				new DateTimeConstantNode(new Date(System.currentTimeMillis())),
				paymentOrderObject.getDebtorAID(),
				paymentOrderObject.getDebtorBankAID(),
				paymentOrderObject.getCreditorAID(),
				paymentOrderObject.getCreditorBankAID(),
				paymentOrderObject.getInvoiceReference(),
				paymentOrderObject.getAmount());

		return paymentNoteObject.getPaymentNoteTerm();
	}

	// generate the payment advice sent by the customer's bank to its client 
	// after sending the payment note proving the payment
	public Term buildPaymentAdviceFromPaymentNote(Term paymentNoteTerm) {
		PaymentNote paymentNoteObject = new PaymentNote(paymentNoteTerm);
		/* TODO : this method could get a payment date as a parameter
		 * The SIP would then pay, store the date of payment and send it 
		 * to this method to generate the corresponding payment advice
		 */
		PaymentAdvice paymentAdviceObject = 
			new PaymentAdvice(
					(StringConstantNode)SL.string("PAY_ADV_"+paymentAdviceCounter++),
					new DateTimeConstantNode(new Date(System.currentTimeMillis())),
					paymentNoteObject.getDebtorAID(),
					paymentNoteObject.getCreditorAID(),
					paymentNoteObject.getInvoiceReference(),
					paymentNoteObject.getAmount(),
					// payment date and time ?? suppose that payment was done 7s777 before date
					new DateTimeConstantNode(new Date(System.currentTimeMillis()-7777)));
		return paymentAdviceObject.getPaymentAdviceTerm();
	}



	/*****************
	 * SETUP METHODS *
	 *    - kbase    *
	 *    - SIPs     *
	 *****************/

	/*********
	 * KBASE *
	 *********/

	@Override
	protected KBase setupKbase() {
		FilterKBase kbase = (FilterKBase)super.setupKbase();

		// filters to manage the predicate updateAccount, and to display accounts in kbase
		kbase.addFiltersDefinition(new BankAccountsManaging());
		return kbase;
	}


	/************************
	 * BANK ACCOUNTS FILTER *
	 ************************/

	class BankAccountsManaging extends FiltersDefinition {

		// attribute : hashmap of accounts
		// HashMap<Term,double> of BankAccounts
		private java.util.HashMap<String,Double> bankAccounts; 

		private Formula belUpdatePattern = SL.formula("(B ??myself (updateAccount ??agent ??bank ??type ??amount))");

		public BankAccountsManaging() {

			bankAccounts = new java.util.HashMap<String, Double>();

			defineFilter(new KBQueryFilter() {

				@Override
				public QueryResult apply(Formula formula,
						ArrayList falsityReasons, BoolWrapper goOn) {

					return QueryResult.UNKNOWN;
				}

				@Override
				public ArrayList toStrings() {
					ArrayList list = new ArrayList();
					list.add("******* BANK ACCOUNTS *******");
					java.util.Set<String> keySet = bankAccounts.keySet();

					java.util.Iterator<String> it = keySet.iterator();
					while(it.hasNext()) {
						String agent = it.next();
						String accountBalance = bankAccounts.get(agent).toString();
						list.add("(accountBalance "+agent+" "+accountBalance+")");
					}

					return list;
				}

				@Override
				public boolean getObserverTriggerPatterns(Formula formula,
						Set set) {
					return false;
				}

			});

			defineFilter(new KBAssertFilterAdapter(belUpdatePattern) {

				@Override
				public Formula doApply(Formula formula, MatchResult match) {

					InstitutionTools.printTraceMessage("bank filters, formula = "+formula,DEBUG);
					InstitutionTools.printTraceMessage("          and pattern = "+belUpdatePattern,DEBUG);
					InstitutionTools.printTraceMessage("bankAccounts="+bankAccounts,DEBUG);

					if (match != null) {
						Term bank = match.term("bank");
						InstitutionTools.printTraceMessage("match="+match, DEBUG);
						InstitutionTools.printTraceMessage("bank="+bank, DEBUG);

						// only considers transfers in the bank itself
						if (bank.match(myKBase.getAgentName()) != null) {
							InstitutionTools.printTraceMessage("bankAgent="+myKBase.getAgentName(), DEBUG);
							Term agent = match.term("agent");
							InstitutionTools.printTraceMessage(" - match agent = "+agent, DEBUG);
							String type = match.term("type").toString();
							InstitutionTools.printTraceMessage(" - match type = "+type, DEBUG);
							Double amount = Double.parseDouble(match.term("amount").toString());
							InstitutionTools.printTraceMessage(" - match amount = "+amount, DEBUG);
							Double newAmount;
							if (type.equals("credit")) {
								InstitutionTools.printTraceMessage("CREDIT !! "+amount, DEBUG);
								Double oldAmount = bankAccounts.get(agent.toString()); 
								InstitutionTools.printTraceMessage("old amount="+oldAmount, DEBUG);
								if (oldAmount == null) { // FIXME what if the account does not exist ?
									return null;
								}
								newAmount = oldAmount+amount;
							}
							else if (type.equals("debit")) {
								InstitutionTools.printTraceMessage("DEBIT !! "+amount, DEBUG);
								Double oldAmount = bankAccounts.get(agent.toString());
								InstitutionTools.printTraceMessage("old amount="+oldAmount, DEBUG);
								if (oldAmount == null) { // FIXME what if the account does not exist ?
									return null;
								}
								newAmount = oldAmount-amount;
							}
							else if (type.equals("create")) {
								InstitutionTools.printTraceMessage("CREATE !! "+amount, DEBUG);
								newAmount = amount;
							}
							else {
								System.err.println("WARNING: unrecognized type of bank operation in LoaningBankCapabilities.BankAccountManaging filters !!! type="+type);
								return null;
							}
							bankAccounts.put(agent.toString(),newAmount);
						}
						// absorb the formula
						return SL.TRUE; 
					}
					return null;
				}	
			});

		}//end class filters def


	}


	/***************
	 * USEFUL SIPS * 
	 ***************/

	@Override
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();

		// representative SIP (represent real banks)
		table.addSemanticInterpretationPrinciple(new HandleMessagesFromEnterprise(this));

		// transfer the payment order to the real bank
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.sendPaymentOrderPattern),SL.TRUE)));

		// transfer the physical payment done information ? (the real bank will deduce the associated payment note)
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.payBankPattern),SL.TRUE)));

		// transfer the query of agreement from mediator
		table.addSemanticInterpretationPrinciple(new ForwardMessage(this,
				new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("sender"),BusinessActionPatterns.queryAgreementPattern),SL.TRUE)));
		
		return table;
	}

	// agent of current transaction (WARNING what if several parallel transactions with various clients ?)
	private static Term orderingAgent;

	// a (customer) bank receiving a payment order from its client performs the physical payment
	// exception: if the payment was already done (ordered by the intermediary before): send the payment advice instead
	class PaymentOrderReceptionTriggersPhysicalPayment extends ActionInResponseToDocumentReception {

		public PaymentOrderReceptionTriggersPhysicalPayment(InstitutionalCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getPaymentOrderPattern());  
		}

		// when receiving payment order, physically pays
		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr, Term paymentOrderTerm) {
			InstitutionTools.printTraceMessage("&& SIP !! payment order reception triggers physical payment",DEBUG);
			if (applyResult != null) {
				PaymentOrder paymentOrderObject = new PaymentOrder(paymentOrderTerm);

				// get the corresponding payment advice if exists
				StringConstantNode invoiceRef = paymentOrderObject.getInvoiceReference();
				Term paymentAdviceTerm = Document.getDocumentTermWithAttribute(
						"invoiceReference", invoiceRef, 
						BusinessFunctionalTerms.getPaymentAdvicePattern(),
						(BusinessCapabilities)myCapabilities);

				// prepare the action term to return
				Term actionTerm;

				// CASE 1 : PAYMENT WAS ALREADY PERFORMED (ORDERED BY INTERMEDIARY BEFORE)
				if (paymentAdviceTerm != null) {
					// transfer the found paymentAdvice
					actionTerm = (Term)SL.instantiate(
							BusinessActionPatterns.sendPaymentAdvicePattern,
							// receiver = buyer = debtor = sender of payment order
							"supplier",applyResult.term("sender"),
							"paymentAdvice",paymentAdviceTerm);
				}
				// CASE 2 : FIRST PAYMENT ORDER
				else {
					// (remember the name of ordering agent, who will be sent subsequent documents)
					orderingAgent = applyResult.term("sender");
					actionTerm = (Term)InstitutionTools.instantiate(
							BusinessActionPatterns.payBankPattern,
							"amount",paymentOrderObject.getAmount(),
							"debtorAgent",Tools.AID2Term(paymentOrderObject.getDebtorAID()),
							"debtorBank",Tools.AID2Term(paymentOrderObject.getDebtorBankAID()),
							"creditorAgent",Tools.AID2Term(paymentOrderObject.getCreditorAID()),
							"creditorBank",Tools.AID2Term(paymentOrderObject.getCreditorBankAID()),
							"paymentNote",buildPaymentNoteFromPaymentOrder(paymentOrderTerm));	
				}
				return actionTerm;
			}
			return null;
		}
	}//end sip

	// a (customer) bank emitting a payment note sends the corresponding payment advice to its client,
	// AND to the agent having ordered the payment if it is different (ex: intermediary)
	class PaymentNoteEmissionTriggersPaymentAdvice extends ActionInResponseToDocumentEmission {

		public PaymentNoteEmissionTriggersPaymentAdvice(BusinessCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getPaymentNotePattern());  //,"debtorBankAID","debtorBankAID"
		}

		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term paymentNoteTerm) {

			if (applyResult != null) {
				PaymentNote paymentNoteObject = new PaymentNote(paymentNoteTerm);

				// CASE 1 : EMISSION
				// the debtor bank, when sending the payment note (i.e. when it has payed)
				// sends a payment advice to the client who ordered this payment
				if (getAgentName().equals(paymentNoteObject.getDebtorBank())) {
					return (Term)SL.instantiate(
							BusinessActionPatterns.sendPaymentAdvicePattern,
							// warning : the variable is named supplier but can be the customer when the action is performed by his bank
							"supplier",orderingAgent,    
							"paymentAdvice",buildPaymentAdviceFromPaymentNote(paymentNoteTerm)
					);
				}
			}
			return null;
		}
	}//end emission SIP

	// a (supplier) bank receiving a payment note proving that its client has been payed
	// forwards this document to its client (the supplier, the creditor)
	class PaymentNoteReceptionTriggersPaymentNote extends ActionInResponseToDocumentReception {

		public PaymentNoteReceptionTriggersPaymentNote(BusinessCapabilities capabilities) {
			super(capabilities,BusinessFunctionalTerms.getPaymentNotePattern());  
		}

		@Override
		public Term doApply(MatchResult applyResult, SemanticRepresentation sr,
				Term paymentNoteTerm) {

			// CASE 2 : RECEPTION
			// when the creditor's bank receives the payment note it forwards it to its client (the supplier/creditor)
			if (applyResult != null) {
				PaymentNote paymentNoteObject = new PaymentNote(paymentNoteTerm);
				if (getAgentName().equals(paymentNoteObject.getCreditorBank())) {
					Term client =  Tools.AID2Term(paymentNoteObject.getCreditorAID());
					Term sendPaymentNoteToOrderingAgent = (Term)SL.instantiate(
							BusinessActionPatterns.sendPaymentNotePattern,
							"creditor",orderingAgent,
							"paymentNote",paymentNoteTerm);
					if (!client.equals(orderingAgent)) {
						// in this case send the note to both agents
						TermSetNode actions = new TermSetNode();
						Term sendPaymentNoteToClient = (Term)SL.instantiate(
								BusinessActionPatterns.sendPaymentNotePattern,
								"creditor",client,
								"paymentNote",paymentNoteTerm);
						//return new SequenceActionExpressionNode(sendPaymentNoteToClient,sendPaymentNoteToOrderingAgent);
						actions.addTerm(sendPaymentNoteToClient);
						actions.addTerm(sendPaymentNoteToOrderingAgent);
						// Syntax: (action agt act) ; (action agt act) !!!!
						return actions;
					}
					return sendPaymentNoteToOrderingAgent;
				}
			}
			return null;	
		}
	}

}//end class capabilities	

