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
 * Created by Carole Adam, January 2008
 */

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.ext.institutions.InstitutionTools;
import jade.semantics.ext.institutions.InstitutionalAgent;
import jade.semantics.ext.institutions.InstitutionalAgentGui;
import jade.semantics.ext.institutions.InstitutionalCapabilities;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.QueryResult.BoolWrapper;
import jade.semantics.kbase.filters.FilterKBaseImpl;
import jade.semantics.kbase.filters.FiltersDefinition;
import jade.semantics.kbase.filters.KBQueryFilter;
import jade.semantics.kbase.observers.EventCreationObserver;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Symbol;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jade.util.leap.Set;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;


/**
 * This class gathers what is useful for a business agent, 
 * evolving in an institution given as an attribute.
 * 
 * @author wdvh2120
 * @version 1.1 April 9th 2008
 */
public class BusinessAgent extends InstitutionalAgent {


	public BusinessCapabilities getSemanticCapabilities() {
		SemanticCapabilities capab = super.getSemanticCapabilities();
		if (capab instanceof BusinessCapabilities) {
			return (BusinessCapabilities)capab;
		}
		System.err.println("WARNING : The capabilities "+capab+" should be an instance of BusinessCapabilities but it is a "+capab.getClass()+" !!!");
		throw new ClassCastException();
	}



	/*******************************
	 *        SETUP METHODS        *
	 * for various types of agents *
	 *******************************/

	public String getMyPath() {
		return MainClass.myDemoPath;
	}


	public void setup(BusinessCapabilities capabilities) {
		super.setup(capabilities);

		// read the bp file
		InstitutionTools.readBusinessProcessSpecificationNEW(
				capabilities, 
				capabilities.getInstitutionName());
		// read the config file
		readConfigurationFile();
		// in demo mode, hide the GUI (useless for the mediation platform internal agents)
		if (MainClass.SIMPLIFIED) {
			hideGUI();
		}
	}

	public void setupInstitution(InstitutionalCapabilities capabilities,
			String institution) {
		super.setupInstitution(capabilities, institution);
		if (MainClass.SIMPLIFIED) {
			hideGUI();
		}
	}

	public void setupMediator(InstitutionalCapabilities capabilities,
			String institution) {
		super.setupMediator(capabilities, institution);
		if (MainClass.SIMPLIFIED) {
			hideGUI();
		}
	}

	public void setupEnterprise(InstitutionalCapabilities capabilities) {
		// additional parameter to specify if the gui should be simplified (demo mode)
		this.setup(MainClass.SIMPLIFIED,capabilities);
		showGUI();
	}

	// READ THE CONFIGURATION FILES OF AGENTS 
	// (.config files = agent config, different from the .gui files = GUI config)
	public void readConfigurationFile() {
		String line = null;
		SemanticCapabilities capabilities = getSemanticCapabilities();
		String file = capabilities.getAgent().getLocalName()+".config";
		try {
			InputStreamReader isr = InstitutionalAgentGui.getDemoFileReader(getMyPath(),file,"optional");
			if (isr==null) {return;}
			java.io.BufferedReader reader = new BufferedReader(isr);
			while ( (line = reader.readLine()) != null ) {
				if ( !line.equals("") ) {
					String fields[] = line.split("#");
					if (line.startsWith("client#"))
					{
						Term agent = SL.term("(agent-identifier :name "+fields[1].trim()+")");
						Double amount = Double.parseDouble(fields[2].trim());
						if (capabilities.getAgent() instanceof LoaningBankAgent) {
							((LoaningBankAgent)capabilities.getAgent()).createAccount(agent, amount);
						}
					}
					if (line.startsWith("belief#"))
					{
						Formula belief = SL.formula(fields[1].trim());
						capabilities.interpret(belief);
					}
				}
			}
		}
		catch (FileNotFoundException fnfe) {
			System.err.println("NO CONFIG FILE for "+getName());
		}
		catch (Exception e) {e.printStackTrace();}
	}

}


/****************************************
 * INTERNAL BUSINESS CAPABILITIES       *
 * FOR BUSINESS AGENTS                  *
 *                                      *
 * INCLUDES THE SEMANTIC ACTION TABLE   *
 * WITH STANDARD INSTITUTIONAL ACTIONS  *
 * FOR THESE AGENTS                     *
 *                                      *
 * @author wdvh2120                     *
 ****************************************/

class BusinessCapabilities extends InstitutionalCapabilities {

	private final boolean DEBUG = false;

	public final double VAT_RATE = 0.196;

	/*****************
	 * REDEFINITIONS *
	 *****************/
	// to avoid castings

	public BusinessAgent getAgent() {
		Agent agent = super.getAgent();
		if (agent instanceof BusinessAgent) {
			return (BusinessAgent)agent;
		}
		System.err.println("WARNING : The agent "+getAgentName()+" should be a BusinessAgent and he is a "+agent.getClass()+" !!!");
		throw new ClassCastException();
	}


	/************
	 * PATTERNS *
	 ************/

	private final static Formula d_INSTITUTION_price_SELLER_BUYER_ITEM_PRICE = 
		new InstitutionalFactNode(
				new MetaTermReferenceNode("institution"),
				new PredicateNode(
						SL.symbol("price"),
						new ListOfTerm(new Term[] {
								new MetaTermReferenceNode("seller"),
								new MetaTermReferenceNode("buyer"),
								new MetaTermReferenceNode("item"),
								new MetaTermReferenceNode("price")})
				)
		);		

	protected final static Formula desiredItemPattern = SL.formula("(desired-item ??item ??number)");

	protected final static Formula until_d_INSTITUTION_price_SELLER_BUYER_ITEM_PRICE_VALIDITY = 
		InstitutionTools.buildUntilFormula(
				new InstitutionalFactNode(
						new MetaTermReferenceNode("institution"),
						SL.formula("(price ??seller ??buyer ??item ??price)")
				),
				new MetaFormulaReferenceNode("validity"));

	private final static Formula buildPackagePattern = SL.formula("(B ??myself (buildPackage ??orderLines ??package))");


	// action patterns
	protected static final Formula intend_AGENT_done_action_AGENT_ACTION_true =
		new IntentionNode(new MetaTermReferenceNode("agent"),
				new DoneNode(
						new ActionExpressionNode(
								new MetaTermReferenceNode("agent"),
								new MetaTermReferenceNode("action")),
								SL.TRUE));


	/**************
	 * ATTRIBUTES *
	 **************/

	/* 
	 * The name of the institution managed by these capabilities.
	 * Should NOT be an attribute of InstitutionalCapabilities since
	 * an instance of InstitutionalCapabilities can manage SEVERAL institutions,
	 * and thus an InstitutionalAgent has an attribute listOfInstitutions
	 */
	private String institutionName;

	/****************
	 * CONSTRUCTORS *
	 ****************/

	// complete detailed constructor with booleans to specify the agent's behaviour 
	public BusinessCapabilities(String instName,boolean isLazy, boolean isConscientious, boolean isTrustless) {
		super(isLazy,isConscientious,isTrustless);
		institutionName = instName;
	}

	// standard constructor with default behaviour (not lazy, not conscientious, NOT trustless)
	public BusinessCapabilities(String instName) {
		this(instName,false,false,false);
	}


	/*************
	 * ACCESSORS *
	 *************/

	public String getInstitutionName() {
		return institutionName;
	}

	public Term getMediator() {
		return getAgent().getMediator(institutionName);
	}


	/******************************
	 *     USEFUL     METHODS     *
	 ******************************/

	// get the institutional price of an item
	public Long getCataloguePriceOfItem(Term buyer, Term seller, Constant item) {
		// instantiate everything but the price
		Formula institutionalPrice2 = (Formula)
		SL.instantiate(d_INSTITUTION_price_SELLER_BUYER_ITEM_PRICE,
				"institution",SL.term("b2b"),
				"seller",seller,
				"buyer",buyer,
				"item",item);
		// query if some price is grounded for this item (meaning
		// that it is in the catalogue)
		QueryResult qr = getMyKBase().query(institutionalPrice2);

		// if the price was found (the item exists in the catalogue)
		if (qr != null) { // cannot be KNOWN since ??price is not instantiated
			// directly extract the unique institutional price
			MatchResult mr = qr.getResult(0);
			InstitutionTools.printTraceMessage("mrPrice = "+mr,DEBUG);
			return Long.parseLong(mr.term("price").toString());
		}
		// if the item is not in the catalogue
		// or if no price was institutionally fixed for this item
		return null;
	}


	// get the bank of an agent (predicate myBank)
	public AID getBank() {
		QueryResult qr = myKBase.query(SL.formula("(myBank ??bank)"));
		return Tools.term2AID(qr.getResult(0).term("bank"));
		// WARNING null if an agent has no bank (should not occur)
	}



	/*********************************************
	 * BUILDING METHODS USED IN AGENT SUBCLASSES *
	 *********************************************/

	private static int paymentOrderCounter = 0;
	// build the payment order from the received invoice
	public Term buildPaymentOrderFromInvoice(Term invoiceTerm,AID bank) {
		Invoice invoiceObject = new Invoice(invoiceTerm);
		RealConstantNode computedAmount = 
			(RealConstantNode)SL.real(invoiceObject.getNetAmount().realValue()+
					invoiceObject.getVatAmount().realValue());
		PaymentOrder paymentOrderObject = 
			new PaymentOrder(
					(StringConstantNode)SL.string("PAY_ORD_"+paymentOrderCounter++),
					new DateTimeConstantNode(new Date(System.currentTimeMillis())),
					invoiceObject.getCustomerAID(),
					bank,
					invoiceObject.getSupplierAID(),
					invoiceObject.getSupplierBankAID(),
					invoiceObject.getId(),
					computedAmount);
		Term paymentOrderTerm = paymentOrderObject.getPaymentOrderTerm();
		return paymentOrderTerm;
	}


	/*******************************
	 * SEMANTIC ACTION TABLE SETUP *
	 *******************************/

	protected SemanticActionTable setupSemanticActions() {

		SemanticActionTable table = super.setupSemanticActions();

		/*********************
		 * SOME TEST ACTIONS *
		 *********************/

		// test action: prints a content to stderr
		table.addSemanticAction(new OntologicalAction(this,
				"(PRINT :content ??content)",
				//effect
				SL.TRUE,
				//precondition
				SL.TRUE
		){
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println(capabilities.getAgentName()+" PRINTS "+getActionParameter("content")+"\n");
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});

		// test action : procedure of a power
		// (param can be modified to allow an agent to perform several times
		// the procedure during the same simulation)
		table.addSemanticAction(new OntologicalAction(this,
				"(PROC :dest ??dest :param ??param)",
				//effect
				SL.TRUE,
				//precondition
				SL.TRUE
		) {
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println(getAgentName()+" has performed the procedure to "+getActionParameter("dest"));
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});

		// test action: unfeasible action
		table.addSemanticAction(new OntologicalAction(this,
				"(RETURN :past ??past)",
				//effect
				SL.TRUE,
				//precondition
				SL.FALSE
		) {
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println("oops, "+getAgentName()+" has just performed an unfeasible action...");
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});

		// action with a precondition for tests on is_trying
		table.addSemanticAction(new OntologicalAction(this,
				"(TESTCOND :cond ??cond)",
				//effect
				SL.TRUE,
				//precondition
				SL.formula("condition")
		) {
			public void perform(OntoActionBehaviour behaviour) {
				Formula condition = ((FactNode)getActionParameter("cond")).as_formula();
				if (getSemanticCapabilities().getMyKBase().query(condition) != null) {
					System.err.println(getAgentName()+" has performed CONDITIONAL ACTION with condition = "+condition);
					behaviour.setState(SemanticBehaviour.SUCCESS);
				}
				else {
					System.err.println(getAgentName()+" has FAILED to perform CONDITIONAL ACTION because "+condition+" was false");
					behaviour.setState(SemanticBehaviour.FEASIBILITY_FAILURE);
				}
			}
		});


		/************************************
		 * SIMULATION OF COMMUNICATIVE ACTS *
		 ************************************/

		/********************
		 * BUSINESS ACTIONS *
		 ********************/

		// REQUEST CATALOGUE
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.requestCataloguePattern,
						"requestCatalogue",
						"supplier"
				));

		// SEND CATALOGUE TO A CLIENT
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendCataloguePattern,
						"catalogue",
						"customer"
				));

		// SEND PURCHASE ORDER TO SELLER 
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendPurchaseOrderPattern,
						"purchaseOrder",
						"supplier"
				));

		// CANCEL PURCHASE ORDER
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.cancelPurchaseOrderPattern,
						"cancellation",
						"supplier"
				));

		// SEND DETAILED ANSWER TO PURCHASE ORDER
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendResponsePattern,
						"response",
						"customer"
				));

		// SEND INVOICE (supplier -> customer)
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendInvoicePattern,
						"invoice",
						"customer"
				));

		// SEND PAYMENT ADVICE
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendPaymentAdvicePattern,
						"paymentAdvice",
						"supplier"
				));

		// SEND PAYMENT ORDER
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendPaymentOrderPattern,
						"paymentOrder",
						"bank",
						SL.formula("(can-pay ??actor ??orderReference)")
				));
		// WARNING: meta-references used in precondition should be parameters of action...

		// SEND RECEIPT ADVICE	
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendReceiptAdvicePattern,
						"receiptAdvice",
						"supplier"
				));

		// SEND DISPATCH ADVICE
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendDispatchAdvicePattern,
						"dispatchAdvice",
						"customer"
				));

		// SEND PAYMENT NOTE
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.sendPaymentNotePattern,
						"paymentNote",
						"creditor"
				));
		// precondition = dispose of the note ?

		// QUERY AGREEMENT TO THE BANK
		table.addSemanticAction(
				new SendDocumentAction(this,
						BusinessActionPatterns.queryAgreementPattern,
						"queryAgreement",
						"bank"
				));

		// BUYER SIGNALS A PROBLEM TO MEDIATOR
		table.addSemanticAction(new OntologicalAction(this,
				BusinessActionPatterns.signalProblemPattern,
				//effect
				new TrueNode(),
				//precondition
				new TrueNode()) {
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println(getAuthor()+" SIGNALS to mediator "+getActionParameter("mediator")+
						" that his order "+getActionParameter("rejectedPurchaseOrder")+
						" was refused because "+getActionParameter("rejectionReason"));
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});

		// SELLER DELIVERS THE CONTENT OF PACKAGE ORDERED BY CLIENT, FACTURED WITH ITS PRICE
		// end of the transaction - the buyer can take the initiative to send a new purchase order
		table.addSemanticAction(new OntologicalAction(this,
				BusinessActionPatterns.deliverPackagePattern, 
				//effect
				SL.formula("(document ??actor ??customer ??deliveryNote)"),
				//precondition
				SL.formula("(can-deliver ??actor ??orderReference)")
		) {
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println("------- PHYSICAL DISPATCH -------");
				InstitutionTools.waitForSomeTime(5000);
				System.err.println("------- ONGOING TRANSPORT -------");
				InstitutionTools.waitForSomeTime(5000);
				System.err.println("------- PHYSICAL DELIVERY -------");
				System.err.println("--- supplier = "+getAuthor());
				System.err.println("--- customer = "+getActionParameter("customer"));
				System.err.println("--- delivery note = "+getActionParameter("deliveryNote"));
				System.err.println("--- orderReference = "+getActionParameter("orderReference")+"\n");
				// obliged to repeat order ref as a direct parameter of action 
				// (it is also a parameter of delivery note, but action precondition cannot depend on it...)
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});


		// CUSTOMER BANK PAYS THE SUPPLIER BANK (PHYSICAL ACTION)
		table.addSemanticAction(new OntologicalAction(this,
				BusinessActionPatterns.payBankPattern,
				//effect
				new AndNode(SL.formula("(document ??actor ??creditorBank ??paymentNote)"),
						new AndNode(SL.formula("(updateAccount ??debtorAgent ??debtorBank debit ??amount)"),
								SL.formula("(updateAccount ??creditorAgent ??creditorBank credit ??amount)"))),
								//precondition : no, if client orders to pay, the bank pays 
								// (possibly a condition on the available amount of money on client's bank account : TODO) 
								SL.TRUE
		) {
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println("------- PHYSICAL PAYMENT -------");
				System.err.println("--- debtor = "+getActionParameter("debtorAgent")+" (via bank "+getAuthor()+")");
				System.err.println("--- creditor = "+getActionParameter("creditorAgent")+" (via bank "+getActionParameter("creditorBank")+")");
				System.err.println("--- payment note = "+getActionParameter("paymentNote")+"\n");

				// only bankAgents should perform this action
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});


		// CUSTOMER DELEGATES POWER TO AN INTERMEDIARY FOR A PURCHASE VIA A LOANING BANK
		table.addSemanticAction(new OntologicalAction(this,
				BusinessActionPatterns.delegatePurchasePattern,
				// effect - no instantiation since variables have the same name in action and doc patterns
				SL.formula("(document ??actor ??intermediary "+BusinessFunctionalTerms.getDelegationPattern()+")"),
				// precondition
				SL.TRUE
		) {
			public void perform(OntoActionBehaviour behaviour) {
				super.perform(behaviour);
			}
		});


		/*************
		 * THE TABLE *
		 *************/

		return table;

	}

	/*************************************************
	 * SEMANTIC INTERPRETATION PRINCIPLE TABLE SETUP *
	 *************************************************/

	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();
		table.addSemanticInterpretationPrinciple(new EngagementOnCataloguePricesInterpretation(this));

		// SIPs specific to the representative agents
		if (!(getAgent() instanceof EnterpriseInterface)) {
			// graphical display
			table.addSemanticInterpretationPrinciple(new DocumentGraphicalDisplaySIP(this));
			// translation of ingoing UBL messages into ACL
			table.addSemanticInterpretationPrinciple(new UBL2ACLtranslationSIP(this));
			// translation of outgoing ACL messages into UBL messages
			// only for buyer and seller (otherwise the bank will not understand mediator's messages)
			if ((getAgent() instanceof BuyerAgent) || (getAgent() instanceof SellerAgent)) {
				table.addSemanticInterpretationPrinciple(new ACL2UBLtranslationSIP(this));
			}
		}
		return table;
	}


	/***************************************
	 * SIP FOR GRAPHICAL DISPLAY AND PAUSE *
	 ***************************************/

	// adapted from: http://java.developpez.com/faq/gui/?page=graphique_general_images#GRAPHIQUE_IMAGE_redimensionner
	public static ImageIcon scale(ImageIcon source, Dimension dim) {
		/* Create a new image with good dimensions */
		BufferedImage buf = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_ARGB);

		/* Draw on the Graphics of the buffered image */
		Graphics2D g = buf.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(source.getImage(), 0, 0, dim.width-17, dim.height-17, null);
		g.dispose();

		/* Return the buffered image, that is an image */
		ImageIcon img = new ImageIcon(buf);
		buf.flush();
		return img;
	}

	static int stepCounter = -1;

	public static String getPPTname(String documentType, String senderLocalName, String receiverLocalName) {
		String name="default";
		if (documentType.equals("requestCatalogue")) name = "1";
		if (documentType.equals("catalogue")) name = "2";
		if (documentType.equals("purchaseOrder")) {
			if (senderLocalName.equals(B2BInstitution.ENTERPRISE_A_AGENT_NAME)) name = "3-a";
			if (senderLocalName.equals(B2BInstitution.INTERMEDIARY_NAME)) name="4-c";
		}

		if (documentType.equals("response")) {
			if (senderLocalName.equals(B2BInstitution.ENTERPRISE_B_AGENT_NAME)) {
				if (receiverLocalName.equals(B2BInstitution.ENTERPRISE_A_AGENT_NAME)) name = "3-b";
				if (receiverLocalName.equals(B2BInstitution.INTERMEDIARY_NAME)) name = "5-a";
			}
			if (senderLocalName.equals(B2BInstitution.INTERMEDIARY_NAME)) name="5-b";
		}
		if (documentType.equals("queryAgreement")) name = "3-c";
		if (documentType.equals("agreement")) name = "4-a";
		if (documentType.equals("delegation")) name = "4-b";
		if (documentType.equals("invoice")) {
			if (senderLocalName.equals(B2BInstitution.ENTERPRISE_B_AGENT_NAME)) name = "6-a"; 
			if (senderLocalName.equals(B2BInstitution.INTERMEDIARY_NAME)) name = "12-c";
		}
		if (documentType.equals("paymentOrder")) name = "6-b";
		if (documentType.equals("paymentAdvice")) {
			if (senderLocalName.equals(B2BInstitution.BANK_A_AGENT_NAME)) name = "7-a"; 
			if (senderLocalName.equals(B2BInstitution.INTERMEDIARY_NAME)) name = "7-b"; 
			if (senderLocalName.equals(B2BInstitution.ENTERPRISE_A_AGENT_NAME)) name = "14";
		}
		if (documentType.equals("dispatchAdvice")) {
			if (senderLocalName.equals(B2BInstitution.ENTERPRISE_B_AGENT_NAME)) name = "10-a"; 
			if (senderLocalName.equals(B2BInstitution.INTERMEDIARY_NAME)) name = "10-b";
		}
		if (documentType.equals("receiptAdvice")) {
			if (senderLocalName.equals(B2BInstitution.ENTERPRISE_A_AGENT_NAME)) name = "12-a"; 
			if (senderLocalName.equals(B2BInstitution.INTERMEDIARY_NAME)) name = "12-b";
		}
		return name;
	}

	static boolean STOP = false;
	class DocumentGraphicalDisplaySIP extends SemanticInterpretationPrinciple {

		public DocumentGraphicalDisplaySIP(BusinessCapabilities capabilities) {
			super(capabilities,"(document ??sender ??receiver ??document)",
					SemanticInterpretationPrincipleTable.APPLICATION_SPECIFIC);
		}

		public ArrayList apply(SemanticRepresentation sr)
		throws SemanticInterpretationPrincipleException {
			MatchResult applyResult = pattern.match(sr.getSLRepresentation());
			if (applyResult != null) {
				Term receiverz = applyResult.term("receiver");

				/* 
				 * If this SIP is given to real SI agents, receiver must be their representative
				 * If this SIP is given to representative agents, receiver should be the agent itself
				 * But test does not need to be changed since getRepresentative(representative agent) = itself
				 */
				if (receiverz.equals(getAgentName())) {
					String receiver = Tools.term2AID(applyResult.term("receiver")).getLocalName();
					String sender = Tools.term2AID(applyResult.term("sender")).getLocalName();
					Term document = applyResult.term("document");
					String docType = ((FunctionalTermParamNode)document).as_symbol().toString();

					// main frame
					JFrame jf1 = new JFrame();
					JDialog jf = new JDialog(jf1,"DOCUMENT "+docType+" FROM "+sender+" TO "+receiver);
					jf.setModal(true);
					jf.setAlwaysOnTop(true);

					// SIZING
					Dimension myScreen = Toolkit.getDefaultToolkit().getScreenSize();

					// free the resources used by this window when it is closed (otherwise by default it is only hidden)
					jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

					jf.addWindowListener(new WindowListener() {  // TODO rather use an adapter
						public void windowClosed(WindowEvent arg0) {
							InstitutionTools.printTraceMessage("window closed !!",DEBUG);
						}

						public void windowOpened(WindowEvent arg0) {
							InstitutionTools.printTraceMessage("window open",DEBUG);
							InstitutionTools.suspendAllOtherThreads(false);
						}

						public void windowActivated(WindowEvent arg0) {
							InstitutionTools.printTraceMessage("window activated",DEBUG);
						}

						public void windowClosing(WindowEvent arg0) {
							InstitutionTools.printTraceMessage("window closing",DEBUG);
							STOP = true; // DO NOT DELETE
							InstitutionTools.notifyAllThreads(false);
						}

						public void windowDeactivated(WindowEvent arg0) {
							InstitutionTools.printTraceMessage("window deactivated",DEBUG);
						}

						public void windowDeiconified(WindowEvent arg0) {
							InstitutionTools.printTraceMessage("window deiconified",DEBUG);
						}

						public void windowIconified(WindowEvent arg0) {
							InstitutionTools.printTraceMessage("window iconified",DEBUG);
						}
					});

					// component 1 = image of document type
					JLabel l = new JLabel(InstitutionalAgentGui.getDemoIcon(getMyPath(),"documentImages/"+docType+"Doc.gif"));

					// component 2 = text about receiver and sender
					JTextArea a = new JTextArea("FROM\n"+sender+"\nTO\n"+receiver);

					// component 3 = detailed textual display of document
					String doc = Document.buildDocument(document).toString();
					JEditorPane jep = new JEditorPane();
					jep.setText(doc);

					// component 4 = sniffer image
					ImageIcon ppt =
						scale(InstitutionalAgentGui.getDemoIcon(getMyPath(),"documentImages/Images/"+getPPTname(docType, sender, receiver)+".jpg"),myScreen);
					JLabel img = new JLabel(ppt);

					// LAYOUT ----------
					JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
					JSplitPane west = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
					west.add(l);
					west.add(a);
					split.add(west);
					split.add(new JScrollPane(jep));
					jep.setMinimumSize(new Dimension(333,444));

					// TABBED LAYOUT
					JTabbedPane stitch = new JTabbedPane();
					stitch.addTab("Image",new JScrollPane(img));
					stitch.addTab("Document",split);

					// ADD LAYOUT
					jf.add(stitch);
					jf.pack();
					jf.setVisible(true);

					ArrayList result = new ArrayList();
					sr.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+1);
					result.add(sr);

					return result;
				}//enf if receiver is my representative
			}//end if applyresult not null
			return null;
		}
	}


	/*************
	 * ********* *
	 * ** UBL ** *
	 * ********* *
	 *************/

	/***********************************************************
	 * **  UBL 1  **  UBL 1 **  UBL 1  **  UBL 1  **  UBL 1  ***
	 * SIP to translate UBL messages received from WSIG agents *
	 ***********************************************************/

	// auxiliary method to read in the file
	static String readFile(String fileName) {
		String result="";
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
			int lu=reader.read();
			char c;
			// while not end of file
			while (lu != -1) {
				// read a char, returns an integer
				c = (char) lu;
				result = result+c;
				lu=reader.read();
			}
			reader.close();
			return result;
		} catch (IOException e) {
			System.err.println("Impossible to read file: "+fileName);
			e.printStackTrace();
			return "";
		}
	}

	class UBL2ACLtranslationSIP extends ApplicationSpecificSIPAdapter {

		public UBL2ACLtranslationSIP(BusinessCapabilities capabilities) {
			super(capabilities,"(ubl ??action ??xml)");
		}

		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {

			if (applyResult != null) {
				// extract features
				Term receiver = getAgentName();
				Term sender = getRepresented(receiver);
				String action = applyResult.term("action").toString();
				String ublNameOfFile = ((StringConstantNode)applyResult.term("xml")).stringValue();

				// read file content
				String xmlFile = getMyPath()+"files/ubl/in/"+ublNameOfFile;
				System.err.println("received UBL at : "+xmlFile);

				String str = readFile(xmlFile);
				System.err.println("read: "+str);

				// mapping
				Formula ublContent = ACLMessages.getFormulaFromUBL(action);
				// if the corresponding message is null, this SIP does not apply
				if (ublContent == null) return null;
				// otherwise translate the UBL message into an ACL inform
				Formula doneInformMessage = new DoneNode(
						new ActionExpressionNode(
								sender,
								InstitutionTools.buildInformTerm(
										ublContent, 
										sender, 
										receiver)
						),
						SL.TRUE);

				// interpret the translated message instead of the original one
				ArrayList newResult = new ArrayList();
				newResult.add(new SemanticRepresentation(doneInformMessage));
				return newResult;

			}//end if applyResult!=null
			return null;
		}
	}

	/*********************************************************
	 * **  UBL 2  **  UBL 2 **  UBL 2  **  UBL 2  **  UBL 2  *
	 * SIP to translate ACL messages to represented into UBL *
	 *********************************************************/

	class ACL2UBLtranslationSIP extends ApplicationSpecificSIPAdapter {

		public ACL2UBLtranslationSIP(BusinessCapabilities capabilities) {
			super(capabilities, 
					InstitutionTools.buildIntendToInform(
							new DoneNode(
									new ActionExpressionNode(
											new MetaTermReferenceNode("anyActor"),
											new MetaTermReferenceNode("anyAction")
									),
									SL.TRUE
							),
							capabilities.getAgentName(),
							getRepresented(capabilities.getAgentName())
					));
		}

		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {

			if (applyResult != null) {

				InstitutionTools.printTraceMessage("ubl2, actor= "+applyResult.term("anyActor"),DEBUG);
				InstitutionTools.printTraceMessage("ubl2, action="+applyResult.term("anyAction"),DEBUG);

				Term theAction = applyResult.term("anyAction");
				if (!(theAction instanceof FunctionalTermParamNode)) return result;
				FunctionalTermParamNode ftpnAction = (FunctionalTermParamNode)theAction;
				Symbol nameOfAction = ftpnAction.as_symbol();
				String ublAction = ACLMessages.getActionUBLparam(nameOfAction.toString());
				Term param1 = ftpnAction.getParameter(ublAction);

				// TODO : create the xml file containing the doc !
				try {
					String xmlFile = ublAction+".ubl";
					FileWriter fw = new FileWriter(xmlFile); // create UBL files in the current directory
					fw.write(Document.buildDocument(param1).toString());
					fw.close();

					// Content of the UBL message
					Formula ublContent = SL.formula("(ubl ??action ??xml)");
					ublContent = (Formula)SL.instantiate(ublContent,
							"action",SL.term(ublAction),
							"xml",SL.string(xmlFile));
					InstitutionTools.printTraceMessage("ubl msg="+ublContent,DEBUG);

					// Intention to send this message cannot work since the agent does not believe it.
					// Thus directly send the message.
					myCapabilities.inform(ublContent, ACLMessages.getWSIGagentName(ublAction, getRepresented(getAgentName())));

					// absorb this intention and replace it with a new one
					ArrayList newResult = new ArrayList();
					return newResult;
				}
				catch (IOException e) {
					System.err.println("IO exception while writing UBL file");
					e.printStackTrace();
				}
			}
			return null;
		}
	}


	/******************************************
	 * SIP FOR REPRESENTATIVES OF ENTERPRISES *
	 ******************************************/

	public Term getRepresentative(Term representedAgent) {
		QueryResult qr = myKBase.query(SL.formula("(representative "+representedAgent+" ??repr)"));
		if (qr!=null) {
			return qr.getResult(0).term("repr");
		}
		// if no declared representative, the agent is its own representative
		return representedAgent;
	}

	public Term getRepresented(Term representativeAgent) {
		QueryResult qr = myKBase.query(SL.formula("(representative ??repr "+representativeAgent+")"));
		if (qr!=null) {
			return qr.getResult(0).term("repr");
		}
		// if the agent represents no enterprise, return null
		return null;
	}

	// when receiving a message from the represented enterprise, 
	// forward this message to the specified receiver
	class HandleMessagesFromEnterprise extends ApplicationSpecificSIPAdapter {

		public HandleMessagesFromEnterprise(BusinessCapabilities capabilities) {
			super(capabilities,"(message ??receiver (fact ??message))");
		}

		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {
			if (applyResult != null) {
				Term newReceiver = getRepresentative(applyResult.term("receiver"));

				Formula contentOfMessage = applyResult.formula("message"); 
				Formula intendToInform = InstitutionTools.buildIntendToInform(contentOfMessage,getAgentName(),newReceiver);

				// absorb the message
				ArrayList newResult = new ArrayList();
				newResult.add(new SemanticRepresentation(contentOfMessage));
				newResult.add(new SemanticRepresentation(intendToInform));
				return newResult;
			}
			return null;
		}
	}

	// NO generic SIP to forward all messages to the represented enterprise
	// BUT specific SIP for all messages determining what is forwarded,
	// modified, delayed...
	abstract class HandleMessagesToEnterprise extends SemanticInterpretationPrinciple {

		// store the particular content to be able to design different SIP for different contents
		Formula theContentOfInformPattern;

		// pattern = sender informs receiver that phi
		public HandleMessagesToEnterprise(BusinessCapabilities capabilities, Formula contentOfInformPattern) {
			super(capabilities,
					new DoneNode(
							new ActionExpressionNode(
									new MetaTermReferenceNode("sender"),
									InstitutionTools.buildInformTerm(
											contentOfInformPattern,
											new MetaTermReferenceNode("sender"), 
											getAgentName())),
											new TrueNode()),
											SemanticInterpretationPrincipleTable.APPLICATION_SPECIFIC);
			theContentOfInformPattern = contentOfInformPattern;
		}

		public ArrayList apply(SemanticRepresentation sr)
		throws SemanticInterpretationPrincipleException {

			MatchResult applyResult = pattern.match(sr.getSLRepresentation());
			if (applyResult != null) {
				Formula instantiatedContentOfInform = (Formula)SL.instantiate(theContentOfInformPattern,applyResult);
				// compute the new content formula depending on the original one
				// this depends on the particular subtype of management
				Formula psi = doApply(instantiatedContentOfInform);

				// interpret the returned formula psi
				ArrayList newResult = new ArrayList();
				sr.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+1);
				newResult.add(sr);
				newResult.add(new SemanticRepresentation(psi));
				return newResult;
			}
			return null;
		}

		// to be specified in subclasses (modify, forward, delay...)
		protected abstract Formula doApply(Formula contentOfInform);
	}

	// particular case of modify with the modify method that is an identity
	class ForwardMessage extends HandleMessagesToEnterprise {

		public ForwardMessage(BusinessCapabilities capabilities, Formula contentOfInformPattern) {
			super(capabilities,contentOfInformPattern);
		}

		protected Formula doApply(Formula contentOfInform) {
			// the representative agent intends to inform his represented enterprise
			return InstitutionTools.buildIntendToInform(contentOfInform, getAgentName(), getRepresented(getAgentName()));
		}	
	}


	abstract class ModifyMessage extends HandleMessagesToEnterprise {

		public ModifyMessage(BusinessCapabilities capabilities, Formula contentOfInform) {
			super(capabilities, contentOfInform);
		}

		// modify the content depending on the particular instantiation of the specific modification method
		protected Formula doApply(Formula contentOfInform) {
			return InstitutionTools.buildIntendToInform(
					modifyContent(contentOfInform), 
					getAgentName(), 
					getRepresented(getAgentName()));
		}

		abstract Formula modifyContent(Formula content);
	}


	// particular case of ModifyAndDelay where the modifyContent method is an identity
	class DelayMessage extends HandleMessagesToEnterprise {

		Formula theTriggeringEvent;

		public DelayMessage(BusinessCapabilities capabilities, Formula contentOfInform, Formula triggeringEvent) {
			super(capabilities, contentOfInform);
			theTriggeringEvent = triggeringEvent;
		}

		protected Formula doApply(Formula contentOfInform) {
			EventCreationObserver o = new EventCreationObserver(
					myKBase,
					theTriggeringEvent,
					InstitutionTools.buildIntendToInform(
							contentOfInform,
							getAgentName(),
							getRepresented(getAgentName())),
							getSemanticInterpreterBehaviour());
			myKBase.addObserver(o);
			myKBase.updateObservers(null);
			return SL.TRUE;
		}
	}


	abstract class ModifyAndDelayMessage extends HandleMessagesToEnterprise {

		Formula theTriggeringEvent;

		public ModifyAndDelayMessage(BusinessCapabilities capabilities, Formula contentOfInform, Formula triggeringEvent) {
			super(capabilities, contentOfInform);
			theTriggeringEvent = triggeringEvent;
		}

		protected Formula doApply(Formula contentOfInform) {
			EventCreationObserver o = new EventCreationObserver(
					myKBase,
					theTriggeringEvent,
					InstitutionTools.buildIntendToInform(
							modifyContent(contentOfInform),
							getAgentName(),
							getRepresented(getAgentName())),
							getSemanticInterpreterBehaviour());
			myKBase.addObserver(o);
			myKBase.updateObservers(null);
			return SL.TRUE;
		}	

		abstract Formula modifyContent(Formula content);
	}


	/**********************************
	 * PARTICULAR SIP ADAPTER         *
	 * to simplify the coding         *
	 * of agent's action in response  *
	 * to the reception of a document *
	 **********************************/

	abstract class ActionInResponseToDocumentSIPAdapter extends SemanticInterpretationPrinciple {

		// attributes
		Term documentPattern;

		public ActionInResponseToDocumentSIPAdapter(InstitutionalCapabilities capabilities,
				Term docPattern, Term senderPattern, Term receiverPattern) {
			super(capabilities,"(document "+senderPattern+" "+receiverPattern+" "+docPattern+")",
					SemanticInterpretationPrincipleTable.APPLICATION_SPECIFIC);
			documentPattern = docPattern;
		}

		public abstract ArrayList apply(SemanticRepresentation sr)
		throws SemanticInterpretationPrincipleException; 

		// this should instantiate the action pattern correctly depending on applyResult
		public abstract Term doApply(MatchResult applyResult, SemanticRepresentation sr,Term documentTerm);

		class PerformActionBehaviour extends OneShotBehaviour {
			// add behaviour to delay the interpretation
			// in order to let the institutional SIP interpret this action first, trigger the seller's power
			// and subsequently interpret the catalogue that will be requested for the buyer's interpretation

			private Term theAction;

			public PerformActionBehaviour(Agent a, Term anAction) {
				super(a);
				theAction = anAction;
			}

			public void action() {
				Formula intendToPerformAction = (Formula)SL.instantiate(
						intend_AGENT_done_action_AGENT_ACTION_true,
						"agent",getAgentName(),
						"action",theAction);
				InstitutionTools.printTraceMessage("intend to perform = "+intendToPerformAction, DEBUG);
				myCapabilities.interpret(intendToPerformAction);
			}//end action method

		}//end internal class behaviour

	}

	abstract class ActionInResponseToDocumentReception extends ActionInResponseToDocumentSIPAdapter {

		public ActionInResponseToDocumentReception(InstitutionalCapabilities capabilities,Term docPattern) {
			super(capabilities,docPattern,new MetaTermReferenceNode("sender"),getAgentName());
		}

		public ArrayList apply(SemanticRepresentation sr)
		throws SemanticInterpretationPrincipleException {

			MatchResult applyResult = pattern.match(sr.getSLRepresentation());

			if (applyResult != null) {
				// extract the document term to be used in doApply
				Formula patternn = new BelieveNode(getAgentName(),BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC);
				MatchResult mrr = patternn.match(sr.getSLRepresentation());
				Term documentTerm = mrr.term("doc");

				// extract sender
				Term sender = applyResult.term("sender");
				Term receiver = getAgentName();

				System.err.println("#### DOCUMENT RECEPTION");
				System.err.println("  - by: "+receiver);
				System.err.println("  - from: "+sender);
				System.err.println("  - doc="+documentTerm);

				// instantiate actionPattern from applyResult
				Term actionTerm = doApply(applyResult,sr,documentTerm); 

				// add a behaviour to delay the interpretation of the intention
				if (actionTerm != null) {
					if (actionTerm instanceof TermSetNode) {
						TermSetNode actionTerms = (TermSetNode)actionTerm;
						for (int i=0;i<actionTerms.size();i++) {
							myCapabilities.getAgent().addBehaviour(
									new PerformActionBehaviour(myCapabilities.getAgent(),actionTerms.getTerm(i)));
						}
					}
					else {
						myCapabilities.getAgent().addBehaviour(
								new PerformActionBehaviour(myCapabilities.getAgent(),actionTerm));
					}
				}

				// build the neutral list of SR to return
				ArrayList neutralResult = new ArrayList();
				neutralResult.add(new SemanticRepresentation(sr, sr.getSemanticInterpretationPrincipleIndex() +1));
				return neutralResult;
			}
			return null;
		}

	}

	abstract class ActionInResponseToDocumentEmission extends ActionInResponseToDocumentSIPAdapter {

		public ActionInResponseToDocumentEmission(InstitutionalCapabilities capabilities, Term docPattern) {
			super(capabilities, docPattern, getAgentName(), new MetaTermReferenceNode("receiver"));
		}

		public ArrayList apply(SemanticRepresentation sr)
		throws SemanticInterpretationPrincipleException {

			MatchResult applyResult = pattern.match(sr.getSLRepresentation());

			if (applyResult != null) {
				// extract the document term to be used in doApply
				Formula patternn = new BelieveNode(getAgentName(),BusinessFunctionalTerms.document_SENDER_RECEIVER_DOC);
				MatchResult mrr = patternn.match(sr.getSLRepresentation());
				Term documentTerm = mrr.term("doc");

				// extract sender
				Term sender = getAgentName();
				Term receiver = applyResult.term("receiver");

				System.err.println("#### DOCUMENT EMISSION");
				System.err.println("  - by: "+sender);
				System.err.println("  - to: "+receiver);
				System.err.println("  - doc="+documentTerm);

				// instantiate actionPattern from applyResult
				Term actionTerm = doApply(applyResult,sr,documentTerm); 

				// add a behaviour to delay the interpretation of the intention
				if (actionTerm != null) {
					if (actionTerm instanceof TermSetNode) {
						TermSetNode actionTerms = (TermSetNode)actionTerm;
						for (int i=0;i<actionTerms.size();i++) {
							myCapabilities.getAgent().addBehaviour(
									new PerformActionBehaviour(myCapabilities.getAgent(),actionTerms.getTerm(i)));
						}
					}
					else {
						myCapabilities.getAgent().addBehaviour(
								new PerformActionBehaviour(myCapabilities.getAgent(),actionTerm));
					}
				}

				// build the neutral list of SR to return
				ArrayList neutralResult = new ArrayList();
				neutralResult.add(new SemanticRepresentation(sr, sr.getSemanticInterpretationPrincipleIndex() +1));
				return neutralResult;
			}
			return null;
		}

	}


	/******************************
	 *   USEFUL  SPECIFIC  SIPs   *
	 ******************************/

	/* allows all agents to interpret the effect of the seller's action to
	 * send his catalogue to the client. Due to the seller's power, this 
	 * action engages him (creates an obligation for himself) to believe
	 * that prices are correct, for a limited delay
	 * This SIP splits this global engagement into atomic obligations
	 * concerning the price of each item, each one having the same 
	 * validity delay as the original commitment 
	 */ 
	class EngagementOnCataloguePricesInterpretation extends ApplicationSpecificSIPAdapter {

		public final boolean DEBUG = false;

		/* Interprets the obligation for any agent to believe that the prices in the catalogue are correct
		 * 
		 * TODO : actually this SIP pattern should be (document (catalogue ...)), what would allow 
		 * to remove the is_institutional effect of the SEND_CATALOGUE action
		 */
		public EngagementOnCataloguePricesInterpretation(InstitutionalCapabilities capabilities) {
			super(capabilities,InstitutionTools.buildUntilFormula(
					new InstitutionalFactNode(
							new MetaTermReferenceNode("inst"),
							SL.formula("(is_institutional ??inst ??client ??seller ??catalogueTerm)")),
							new MetaFormulaReferenceNode("validity"))
			);
		}
		

		protected ArrayList doApply(MatchResult applyResult, ArrayList result,
				SemanticRepresentation sr) {
			int numberItems = 0;

			if (applyResult != null) {
				InstitutionTools.printTraceMessage("### sr="+sr,DEBUG);
				InstitutionTools.printTraceMessage("### pattern="+pattern,DEBUG);
				InstitutionTools.printTraceMessage("### match="+applyResult,DEBUG);
				InstitutionTools.printTraceMessage("Engagement on catalogue prices &&&&&&& AGENT = "+myCapabilities.getAgentName(),DEBUG);

				ArrayList resultNew = new ArrayList();

				// extract features
				Term seller = applyResult.term("seller");
				Term buyer = applyResult.term("client");
				Term institution = applyResult.term("inst");
				Term theCatalogueTerm = applyResult.term("catalogueTerm");

				Catalogue theCatalogueObject = new Catalogue(theCatalogueTerm);
				// extract the list of items
				TermSetNode catalogueItems = theCatalogueObject.getCatalogueContent();
				// validity of the catalogue (WARNING: for now, begin of validity is not handled, considered to start at once)
				Formula validity = theCatalogueObject.getEndValidity();

				// scan the catalogue to extract all prices and ground them in the institution
				for (int i=0; i<catalogueItems.size(); i++) {
					// get the i-th line
					Term catalogueItemTermI = catalogueItems.getTerm(i);
					CatalogueItem catalogueItemObjectI = new CatalogueItem(catalogueItemTermI);

					// match it with its pattern to extract the useful features of product
					// extract the name of the i-th item
					Term productName = catalogueItemObjectI.getItemName(); 
					Term productPrice = catalogueItemObjectI.getItemDescription();

					// prepare the formula to interpret and instantiate it from previously extracted features
					Formula institutionalPrice = (Formula)InstitutionTools.instantiate(
							until_d_INSTITUTION_price_SELLER_BUYER_ITEM_PRICE_VALIDITY,
							"institution",institution,
							"seller",seller,
							"buyer",buyer,
							"item",productName,
							"price",productPrice,
							"validity",validity);
					resultNew.add(new SemanticRepresentation(institutionalPrice));
					InstitutionTools.printTraceMessage("!!! "+getAgentName()+" INTERPRETS institutional price("+i+") = "+institutionalPrice,DEBUG);
					// This institutional price counts as an engagement from the seller to respect it (see b2b.specif)
					// The client TOWARDS whom the commitment is taken is a parameter of the predicate interpreted by this SIP

					// count this item in the total number of items
					numberItems++;
				}//end for on i<catalogueItems.size

				/* At the end of the interpretation of all institutional prices
				 * inform (indirectly) the client that he can command
				 * (the buyer has a sip waiting specifically for this formula)
				 * pattern = (command-open ??seller ??buyer ??nbItems)
				 */
				if (numberItems>0) {
					Formula commandOpen = SL.formula("(command-open "+seller+" "+buyer+" "+numberItems+")");
					myCapabilities.getAgent().addBehaviour(
							new CommandOpenDeclarationBehaviour(
									myCapabilities.getAgent(),
									commandOpen));
				}

				// absorb this predicate
				return resultNew;

			}//end if applyResult != null

			return null;
		}

		// behaviour to delay the declaration of the possibility to command
		// after the END of the interpretation of the institutional prices
		class CommandOpenDeclarationBehaviour extends OneShotBehaviour {

			private Formula commandOpenFormula;

			public CommandOpenDeclarationBehaviour(Agent a, Formula commandOpen) {
				super(a);
				commandOpenFormula = commandOpen;
			}

			public void action() {
				myCapabilities.interpret(commandOpenFormula);
			}
		}

	}



	/******************************************************
	 * KBASE INITIAL CONTENT, OBSERVERS AND FILTERS SETUP *
	 ******************************************************/

	protected KBase setupKbase() {
		FilterKBaseImpl kbase = (FilterKBaseImpl)super.setupKbase();
		kbase.addFiltersDefinition(new BusinessFilters());
		businessProcessInitialisation();

		// representatives of enterprises are known by all business agents
		// (including enterprises interfaces for now)
		Formula reprPattern = SL.formula("(representative ??agent ??repr)");
		Formula reprA = (Formula)SL.instantiate(reprPattern,"agent",B2BInstitution.ENTERPRISE_A,"repr",B2BInstitution.ENTERPRISE_A_AGENT);
		Formula reprB = (Formula)SL.instantiate(reprPattern,"agent",B2BInstitution.ENTERPRISE_B,"repr",B2BInstitution.ENTERPRISE_B_AGENT);
		kbase.assertFormula(reprA);
		kbase.assertFormula(reprB);

		// representative of real banks
		kbase.assertFormula((Formula)SL.instantiate(reprPattern,"agent",B2BInstitution.BANK_A,"repr",B2BInstitution.BANK_A_AGENT));
		kbase.assertFormula((Formula)SL.instantiate(reprPattern,"agent",B2BInstitution.BANK_B,"repr",B2BInstitution.BANK_B_AGENT));

		return kbase;
	}


	// ------- Business Process Managing -------

	public void businessProcessInitialisation() {
		// useful formulas
		Formula sellerCanDeliverAfterPayment = SL.formula("(implies " +
				"(and (document ??buyer ??seller (paymentAdvice :creditorAID ??seller :invoiceReference ??invoiceReference)) " +
				"(document ??seller ??buyer (invoice :id ??invoiceReference :orderReference ??orderReference))) " +
		"(can-deliver ??seller ??orderReference))");
		Formula sellerCanDeliverAnytime = SL.formula("(can-deliver ??seller ??orderReference)");

		Formula buyerCanPayAfterDelivery = SL.formula("(implies " +
				"(document ??seller ??buyer (deliveryNote :orderReference ??orderReference)) " +
		"(can-pay ??buyer ??orderReference))");
		Formula buyerCanPayAnytime = SL.formula("(can-pay ??buyer ??orderReference)");

		// standard business agents representing enterprises
		if (this.getAgent() instanceof BuyerAgent) {
			BuyerAgent agent = (BuyerAgent)this.getAgent();
			if (agent.paysAfterDelivery()) {
				buyerCanPayAfterDelivery = (Formula)SL.instantiate(
						buyerCanPayAfterDelivery,"buyer",getAgentName());
				interpret(buyerCanPayAfterDelivery);
			}
			else {
				buyerCanPayAnytime = (Formula)SL.instantiate(
						buyerCanPayAnytime,"buyer",getAgentName());
				interpret(buyerCanPayAnytime);
			}
		}
		else if (this.getAgent() instanceof SellerAgent) {
			SellerAgent agent = (SellerAgent)this.getAgent();
			if (agent.deliversAfterPayment()) {
				sellerCanDeliverAfterPayment = (Formula)SL.instantiate(
						sellerCanDeliverAfterPayment,"seller",getAgentName());
				interpret(sellerCanDeliverAfterPayment);
			}
			else {
				sellerCanDeliverAnytime = (Formula)SL.instantiate(
						sellerCanDeliverAnytime,"seller",getAgentName());
				interpret(sellerCanDeliverAnytime);
			}
		}
		// mediating agent know all business processes
		else if (this.getAgent() instanceof MediatingAgent) {
			/* TODO : ??buyer and ??seller are not instantiated in formulas 
			 * Mediator should scan a list of buyers and sellers, 
			 * and for each one instantiate and interpret the right formula
			 */
			MediatingAgent agent = (MediatingAgent)this.getAgent();
			if (agent.customerPaysAfterDelivery()) {
				interpret(buyerCanPayAfterDelivery);
			}
			else {
				interpret(buyerCanPayAnytime);
			}
			if (agent.supplierDeliversAfterPayment()) {
				interpret(sellerCanDeliverAfterPayment);
			}
			else {
				interpret(sellerCanDeliverAnytime);
			}
		}
		// other agents (institution, banks...) interpret nothing
	}




	/***************************************
	 * INTERNAL CLASS: FILTERS DEFINITIONS *
	 ***************************************/

	class BusinessFilters extends FiltersDefinition {

		public BusinessFilters() {


			/*************************************
			// LINK AND ANALYSIS PREDICATES
			// two filters for each predicate - positive and negative query
			// those predicates are not stored at all in the KBase, and not even asserted
			 ***************************************/

			// POSITIVE QUERY : buildPackage from orderLines
			defineFilter(new KBQueryFilter() {

				public QueryResult apply(Formula formula, ArrayList falsityReasons, BoolWrapper goOn) {
					MatchResult applyResult = buildPackagePattern.match(formula);
					if (applyResult != null) {
						System.err.println("buildpackage, applyresult = "+applyResult);
						goOn.setBool(false); // Do not apply further filters 
						// scan the orderLines
						TermSetNode orderLines = (TermSetNode)applyResult.term("orderLines");
						TermSetNode thePackage = new TermSetNode();
						for (int i=0; i<orderLines.size(); i++) {
							Term orderLineTermI = orderLines.getTerm(i);
							OrderLine orderLineObjectI = new OrderLine(orderLineTermI);
							// extract the name of item
							Term productName = orderLineObjectI.getItemName();
							thePackage.addTerm(productName);
						}
						return new QueryResult(SL.match(applyResult.term("package"),thePackage));
					}
					return null;
				}

				public boolean getObserverTriggerPatterns(Formula formula, Set set) {
					return false;
				}

			}); //end filter

		}//end constructor 
	}//end business filters

}//end business agent

// to generalise all agents representing enterprises
abstract class RepresentativeAgent extends BusinessAgent {

}