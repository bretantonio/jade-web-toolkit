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

package jade.semantics.ext.institutions;

/*
 * Class InstitutionTools.java
 * Created by Carole Adam, 23 November 2007
 */

import jade.core.AID;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.observers.EventCreationObserver;
import jade.semantics.kbase.observers.Observer;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.CountAsNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.OrNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.SymbolNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.parser.ParseException;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.HashMap;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.io.FileReader;
import java.util.Iterator;


/**
 * This class gathers Tools to manipulate institutions
 * or other useful static tools. It also gathers static
 * patterns of formulas and terms so that they are 
 * computed only once.
 * 
 * @author wdvh2120
 * @version 1.0 date August 2008
 */
public class InstitutionTools {

	/****************************
	 *  USEFUL STATIC PATTERNS  *
	 * to be computed only once *
	 ****************************/
	public static final Formula power_AGENT_INSTITUTION_fact_CONDITION_ACTION_fact_EFFECT =
		SL.formula("(power ??agent ??institution (fact ??condition) ??action (fact ??effect))");

	public static final Formula istrying_AGENT_INSTITUTION_ACTION = 
		new PredicateNode(
				SL.symbol("is_trying"),
				new ListOfTerm(new Term[] {
						new MetaTermReferenceNode("agent"),
						new MetaTermReferenceNode("institution"),
						new MetaTermReferenceNode("action")}));

	public static final Formula until_fact_PHI_fact_END = 
		new PredicateNode(SL.symbol("until"),
				new ListOfTerm(new Term[] {
						new FactNode(new MetaFormulaReferenceNode("phi")),
						new FactNode(new MetaFormulaReferenceNode("end"))
				}));


	// pattern of complaint
	public static final Formula complainContent_INSTITUTION_COMPLAINANT_DEFENDANT_ACTION = 
		new AndNode(
				new InstitutionalFactNode(
						new MetaTermReferenceNode("institution"),
						new ObligationNode(
								new DoneNode(
										new ActionExpressionNode(
												new MetaTermReferenceNode("defendant"),
												new MetaTermReferenceNode("action")
										),
										SL.TRUE
								)//end done		
						)//end oblig
				),
				new NotNode(
						new BelieveNode(
								new MetaTermReferenceNode("complainant"),
								new DoneNode(
										new ActionExpressionNode(
												new MetaTermReferenceNode("defendant"),
												new MetaTermReferenceNode("action")
										),
										SL.TRUE
								)
						)//end believe
				)
		);//end of AndNode 

	// patterns of communicative acts

	public static final Term inform_SENDER_RECEIVERS_content_PHI = 
		SL.term("(INFORM :sender ??sender :receiver ??receivers :content (content ??phi))").getSimplifiedTerm();

	public static final Term inform_SENDER_set_RECEIVER_content_PHI = 
		SL.term("(INFORM :sender ??sender :receiver (set ??receiver) :content (content ??phi))").getSimplifiedTerm();

	public static final Term queryif_SENDER_RECEIVERS_content_PHI = 
		SL.term("(QUERY-IF :sender ??sender :receiver ??receivers :content (content ??phi))").getSimplifiedTerm();

	public static final Term queryif_SENDER_set_RECEIVER_content_PHI =
		SL.term("(QUERY-IF :sender ??sender :receiver (set ??receiver) :content (content ??phi))").getSimplifiedTerm();

	public static final Term request_SENDER_RECEIVERS_content_ACTION =
		SL.term("(REQUEST :sender ??sender :receiver ??receivers :content (content ??action))").getSimplifiedTerm();

	public static final Term request_SENDER_set_RECEIVER_content_ACTION =
		SL.term("(REQUEST :sender ??sender :receiver (set ??receiver) :content (content ??action))").getSimplifiedTerm();

	// associates a Term agent with a Long amount of money on his account
	private static HashMap bankAccounts = new HashMap();


	/*******************
	 * WAITING METHODS *
	 *******************/
	// to simulate physical delay (ex: of delivery)
	static int delay;
	static boolean stop;
	public static void waitForSomeTime(int time) {
		delay = time;
		stop = false;
		Thread sleepingThread = new Thread() {
			public void run() {
				try {
					int N=20;
					for (int i=0;i<N;i++) {
						sleep(delay/N);
					}
					stop = true;
				}
				catch(InterruptedException ie) {
					ie.printStackTrace();
				}
			}// end run
		}; //end new thread
		sleepingThread.start();
		while (!stop);
	}

	// use with includingMe = false !!
	public static void suspendAllOtherThreads(boolean includingMe) {
		int nb = Thread.activeCount();
		Thread[] table = new Thread[nb];
		Thread.enumerate(table);

		for (int i=0;i<table.length;i++) {
			if ((table[i] != Thread.currentThread()) || includingMe) {
				if (table[i].getThreadGroup().getName().equals("JADE User Agents")) {
					table[i].suspend(); //instead of wait	
				}
			}
			else {
				//System.err.println("do not suspend current thread");
			}
		}		
	}

	// use with includingMe = false !!
	public static void notifyAllThreads(boolean includingMe) {
		int nb = Thread.activeCount();
		Thread[] table = new Thread[nb];
		Thread.enumerate(table);

		for (int i=0;i<table.length;i++) {
			if (includingMe || (table[i] != Thread.currentThread())) {
				if (table[i].getThreadGroup().getName().equals("JADE User Agents")) {
					table[i].resume();
				}
			}
			else {
				//System.err.println("do not resume current thread");
			}
		}		
	}


	/*******************
	 * MONEY ACCESSORS *
	 *******************/

	public static Long getMoneyOf(Term agent) {
		// obliged to trick, a simple bankAccounts.get(agent) returns null...! (why??)
		Long result = (Long)bankAccounts.get(agent.toString());
		if (result==null) {
			result = new Long(0);
		}
		// an agent who is not stored is considered to have no money
		return result;
	}

	public static void setMoneyOf(Term agent,Long newMoney) {
		// same kind of trick needed (otherwise a second entry for the same key is created)
		// if the agent is not stored yet (initialisation)
		bankAccounts.put(agent.toString(),newMoney);
	}

	public static void creditMoney(Term receiver,Long gain) {
		Long money = getMoneyOf(receiver);
		setMoneyOf(receiver, money+gain);
	}

	public static void debitMoney(Term sender,Long loss) {
		setMoneyOf(sender, getMoneyOf(sender)-loss);
	}

	/***************************************
	 * SPECIFICATION FILES PARSING METHODS *
	 ***************************************/
	
	// READ THE SPECIFICATION OF THE FUNCTIONING OF THE INSTITUTION (ROLES, RULES)
	// IN THE INST.SPECIF FILE
	public static void readInstitutionSpecification(InstitutionalCapabilities capabilities,String institution) {
		ListOfFormula list;
		try {
			list = SLParser.getParser().parseFormulas(InstitutionalAgentGui.getDemoFileReader(capabilities.getMyPath(),institution+".specif","no specif for "+institution), true);
					// old first arg = new FileReader(institution+".specif")
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		Term inst = SL.term(institution);
		for (Iterator i = list.iterator(); i.hasNext(); ) {
			Formula f = (Formula)i.next();
			if (f instanceof CountAsNode) {
				capabilities.interpret(f);
			}
			else {
				capabilities.interpret(new InstitutionalFactNode(inst,f));
			}
		}
	}


	public static void readDefaultSpecification(InstitutionalCapabilities capabilities, Term institution) {
		// add the default rules to the given institution kbase
		// to be called in InstitutionalFactFilters when creating the kbase for a new institution
		ListOfFormula list;
		try {
			// WARNING: the file default.specif must be in the folder of the demo
			list = SLParser.getParser().parseFormulas(InstitutionalAgentGui.getDemoFileReader(capabilities.getMyPath(),"default.specif","!! NO DEFAULT institutional specification ..."), true);
			for (Iterator i = list.iterator(); i.hasNext(); ) {
				Formula f = (Formula)i.next();
				// WARNING: do not change the name of the 
				// meta-reference used for the default institution
				f = f.instantiate("_institution", institution);
				if (f instanceof CountAsNode) {
					capabilities.interpret(f);
				}
				else {
					capabilities.interpret(new InstitutionalFactNode(institution,f));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
	}


	// READ THE FEATURES OF INSTITUTIONAL ACTIONS IN THE INST.ACTIONS FILE
	public static void readInstitutionalActionsSpecification(InstitutionalCapabilities capabilities,String institution) {
		ListOfFormula list;
		try {
			list = SLParser.getParser().parseFormulas(InstitutionalAgentGui.getDemoFileReader(capabilities.getMyPath(),institution+".actions","no actions file for "+institution), true);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		Term inst = SL.term(institution);
		for (Iterator i = list.iterator(); i.hasNext(); ) {
			Formula f = (Formula)i.next();
			if ((f instanceof PredicateNode) && 
					(((SymbolNode)((PredicateNode)f).as_symbol()).lx_value().equals("institutional-action"))) {
				capabilities.interpret(f);
			}
			else {
				capabilities.interpret(new InstitutionalFactNode(inst,f));
			}
		}
	}


	// SCAN DEFAULT ACTIONS (instantiated for the given institution)
	public static void readDefaultActionsSpecification(InstitutionalCapabilities capabilities,Term institution) {
		ListOfFormula list;
		try {
			list = SLParser.getParser().parseFormulas(InstitutionalAgentGui.getDemoFileReader(capabilities.getMyPath(),"default.actions","!! No default institutional actions ..."), true);
			for (Iterator i = list.iterator(); i.hasNext(); ) {
				Formula f = (Formula)i.next();
				f = f.instantiate("_institution",institution);
				if ((f instanceof PredicateNode) && 
						(((SymbolNode)((PredicateNode)f).as_symbol()).lx_value().equals("institutional-action"))) {
					capabilities.interpret(f);
				}
				else {
					capabilities.interpret(new InstitutionalFactNode(institution,f));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		} 
	}


	// NEW SCANNING METHOD FOR BP FILES
	// each agent scans his own file, mediator scans all files
	public static void readBusinessProcessSpecificationNEW(InstitutionalCapabilities capabilities,String institution) {
		ListOfFormula list;
		try {
			list = SLParser.getParser().parseFormulas(InstitutionalAgentGui.getDemoFileReader(capabilities.getMyPath(),institution+".bp","no BP for "+institution), true);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		for (Iterator i = list.iterator(); i.hasNext(); ) {
			Formula f = (Formula)i.next();
			Formula patternBP = SL.formula("(business-process ??agent (fact ??formula))");
			MatchResult mr = patternBP.match(f);

			if (mr != null) {
				Term agentBP = mr.term("agent");
				Formula formulaBP = mr.formula("formula");

				boolean b1 = capabilities.getAgent().believesToBeMediator(institution); 
				boolean b2 = capabilities.getAgentName().equals(agentBP);

				// if the interpreting agent is :
				//     - the mediator of the institution specified in this file
				//     - or the agent specified in this BP line
				if (b1 || b2){
					// specific managing of imply formulas
					if (formulaBP instanceof ImpliesNode) {
						Formula premise = ((ImpliesNode)formulaBP).as_left_formula();
						Formula deduction = ((ImpliesNode)formulaBP).as_right_formula();
						System.err.println("\n @@@@@@@ premise="+premise);
						System.err.println("@@@@@@@ deduction="+deduction);
						Observer obs = new EventCreationObserver(capabilities.getMyKBase(),
								premise,deduction,capabilities.getSemanticInterpreterBehaviour());
						capabilities.getMyKBase().addObserver(obs);
						System.err.println("@@@@@@@ observer="+obs+"\n");
						obs.update(null);
					}
					else {
						capabilities.interpret(f);
					}
				}
			}//end if mr!= null
			else {
				Formula debug = SL.formula("(business-process-debug ??agent (fact ??formula))");
				if (debug.match(f)==null) {
					System.err.println("??? formula not interpreted in bp file: "+f);
				}
			}
		}
	}


	// SCANNING METHOD FOR STRATEGIES AGAINST BLOCKINGS
	// to be used only by mediating agents 
	public static void readStrategiesSpecification(InstitutionalCapabilities capabilities,String institution) {
		ListOfFormula list;
		try {
			list = SLParser.getParser().parseFormulas(InstitutionalAgentGui.getDemoFileReader(capabilities.getMyPath(),institution+".strategy","no strategy file for "+institution), true);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		for (Iterator i = list.iterator(); i.hasNext(); ) {
			Formula f = (Formula)i.next();
			if ((f instanceof PredicateNode) && 
					(((SymbolNode)((PredicateNode)f).as_symbol()).lx_value().equals("strategy"))) {
				capabilities.interpret((Formula)SL.instantiate(f,"mediator",capabilities.getAgentName()));
			}
			else {
				System.err.println("??? formula not interpreted in strategy file: "+f);
			}
		}
	}


	// SCANNING METHODS FOR SANCTIONS IN CASE OF VIOLATION
	public static void readSanctionsSpecification(InstitutionalCapabilities capabilities,String institution) {
		ListOfFormula list;
		try {
//			FileReader fr = InstitutionalAgentGui.getDemoFileReader(capabilities.getMyPath(),institution+".sanctions","optional");
//			if (fr == null) {return;}
			list = SLParser.getParser().parseFormulas(
					InstitutionalAgentGui.getDemoFileReader(capabilities.getMyPath(),institution+".sanctions","optional"), true);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		for (Iterator i = list.iterator(); i.hasNext(); ) {
			Formula f = (Formula)i.next();
			if ((f instanceof PredicateNode) && 
					(((SymbolNode)((PredicateNode)f).as_symbol()).lx_value().equals("sanction"))) {
				// FIXME for now the punisher is the institution itself - later: use a judge agent... ?
				capabilities.interpret((Formula)SL.instantiate(f,"punisher",capabilities.getAgentName()));
			}
			else {
				System.err.println("??? formula not interpreted in sanctions file: "+f);
			}
		}
	}


	/*************************
	 * COMMUNICATION METHODS *
	 *************************/

	// inform the mediator of the given institution (if there is one) of phi
	// if there is no mediator in this institution: do nothing
	public static void informMediatorAboutPhiThroughO2A(InstitutionalCapabilities capabilities, String institution, Formula phi) {
		Term mediatorAgent = capabilities.getAgent().getMediator(institution);
		informAgentAboutPhiThroughO2A(capabilities,mediatorAgent, phi);
	}

	// inform the agent representing the institution
	public static void informInstitutionAboutPhiThroughO2A(InstitutionalCapabilities capabilities, String institution, Formula phi) {
		Term institutionAgent = Tools.AID2Term(new AID(institution,AID.ISLOCALNAME));
		informAgentAboutPhiThroughO2A(capabilities,institutionAgent, phi);
	}


	// agent term = (agent-identifier :name agentname@test)
	private static void informAgentAboutPhiThroughO2A(InstitutionalCapabilities capabilities, Term agent, Formula phi) {
		if (agent != null) {
			String agentName = Tools.term2AID(agent).getLocalName();
			try {
				// prevent an agent from communicating with himself through O2A
				if (capabilities.getAgentName().equals(agent)) {
					return;
				}
				AgentController agtctrl = capabilities.getAgent().getContainerController().getPlatformController().getAgent(agentName);
				// non blocking call (return just after putting the object in the queue)
				agtctrl.putO2AObject(phi,AgentController.ASYNC);
			}
			catch(ControllerException ce) {
				System.err.println("WARNING !! agent "+agent+" does not exist, impossible to inform him that "+phi);
			}
		}
	}

	/************************
	 * CONSTRUCTION METHODS *
	 ************************/

	public static Formula buildUntilFormula(Formula theUntilFormula,Formula theEndValidityFormula) {
		return (Formula)SL.instantiate(
				until_fact_PHI_fact_END,
				"phi",theUntilFormula,
				"end",theEndValidityFormula);
	}

	// this is not an action expression but an action pattern (second param of action expression)
	public static Term buildInformTerm(Formula phi,Term sender, Term receiver) {
		Term result;
		if (receiver instanceof TermSetNode) {
			result = (Term)SL.instantiate(inform_SENDER_RECEIVERS_content_PHI,
					"sender",sender,
					"phi",phi,
					"receivers",receiver);
		}
		else {
			result = (Term)SL.instantiate(inform_SENDER_set_RECEIVER_content_PHI,
					"sender",sender,
					"phi",phi,
					"receiver",receiver);
		}
		return result;
	}

	// build a query
	public static Term buildQueryifTerm(Formula phi,Term sender, Term receiver) {
		Term result;
		if (receiver instanceof TermSetNode) {
			result = (Term)SL.instantiate(queryif_SENDER_RECEIVERS_content_PHI,
					"sender",sender,
					"phi",phi,
					"receivers",receiver);
		}
		else {
			result = (Term)SL.instantiate(queryif_SENDER_set_RECEIVER_content_PHI,
					"sender",sender,
					"phi",phi,
					"receiver",receiver);
		}
		return result;
	}

	// build the corresponding intention to QUERY
	public static IntentionNode buildIntendToQueryif(Formula phi, Term sender, Term receiver) {
		return new IntentionNode(
				sender,
				new DoneNode(
						new ActionExpressionNode(
								sender,
								buildQueryifTerm(phi, sender, receiver)),
								SL.TRUE));
	}

	// INFORM with one or several receivers 
	public static IntentionNode buildIntendToInform(Formula phi, Term sender, Term receiver) {
		return new IntentionNode(
				sender,
				new DoneNode(
						new ActionExpressionNode(
								sender,
								buildInformTerm(phi, sender, receiver)),
								SL.TRUE));
	}


	// build a request term
	public static Term buildRequestTerm(ActionExpression actionExpression,Term sender, Term receiver) {
		Term result;
		if (receiver instanceof TermSetNode) {
			result = (Term)SL.instantiate(request_SENDER_RECEIVERS_content_ACTION,
					"sender",sender,
					"receivers",receiver,
					"action",actionExpression);
		}
		else {
			result = (Term)SL.instantiate(request_SENDER_set_RECEIVER_content_ACTION,
					"sender",sender,
					"receiver",receiver,
					"action",actionExpression);  // new ActionContentExpressionNode(action) (with old pattern)
		}
		return result;
	}

	public static IntentionNode buildIntendToRequest(ActionExpression actionExpression, Term sender, Term receiver) {
		return new IntentionNode(
				sender,
				new DoneNode(
						new ActionExpressionNode(
								sender,
								buildRequestTerm(actionExpression, sender, receiver)),
								SL.TRUE));
	}


	// build a plan to query an information, wait the answer, test it
	public static ActionExpression buildCheckPlan(Formula phi, Term me, Term informant) {

		// query if phi is grounded
		Term queryAction = new ActionExpressionNode(me,buildQueryifTerm(phi, me, informant));

		// check if an answer was received 
		Formula answerReceived = new BelieveNode(me,new OrNode(phi,new NotNode(phi)));
		ActionExpression waitAction = new ActionExpressionNode(me,SL.term("(WAIT (fact ??phi) 2000)"));
		waitAction = (ActionExpression)waitAction.instantiate("phi",answerReceived);

		// test the answer of informant - succeeds if phi is true
		ActionExpression testAction = new ActionExpressionNode(me,SL.term("(TEST (fact "+phi+"))"));

		// final plan
		ActionExpression finalPlan = new SequenceActionExpressionNode(queryAction,waitAction);
		finalPlan = new SequenceActionExpressionNode(finalPlan,testAction);

		return finalPlan;
	}


	public static Formula buildPower(Term agent,Term institutionName,Formula condition,Term procedure,Formula effectOfPower) {
		// (power agent inst (fact cond) (ACTION :param ??param) (fact effect-of-power))
		Formula powerFormula = (Formula)power_AGENT_INSTITUTION_fact_CONDITION_ACTION_fact_EFFECT.getClone();
		try {
			SL.set(powerFormula, "agent",agent);
			SL.set(powerFormula, "institution",institutionName);
			SL.set(powerFormula, "condition", condition);
			SL.set(powerFormula, "action", procedure);
			SL.set(powerFormula, "effect", effectOfPower);
			SL.substituteMetaReferences(powerFormula);
			return powerFormula;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	// build an istrying formula
	// action IS an action expression (action agent act)
	public static Formula buildIsTrying(Term agent,Term institution,Term action) {
		return (Formula)SL.instantiate(istrying_AGENT_INSTITUTION_ACTION,
				"agent",agent,
				"institution",institution,
				"action",action);
	}


	// build a complaint about an undone obliged action
	public static Formula buildComplaintContent(Term institution, Term complainant, Term defendant, Term undoneAction) {
		return (Formula)SL.instantiate(complainContent_INSTITUTION_COMPLAINANT_DEFENDANT_ACTION,
				"institution",institution,
				"complainant",complainant,
				"defendant",defendant,
				"action",undoneAction);
	}


	/*********************
	 * ACCESSING METHODS *
	 *********************/

	// returns the Term agent holding a role given as a string
	//(for now read from the agent's KBase)
	public static Term getHoldingAgent(KBase kb,String institution,String role) {
		// query the KBase
		Term query = SL.term("(any ?x (D "+institution+" (holder ?x "+role+")))");
		ListOfTerm holder = kb.queryRef((IdentifyingExpression)query);
		if (holder != null) {
			// return the first result
			return (Term)holder.get(0);
		}
		return null;
	}


	// get adapted strategy
	// a simple query does not work because match is not symmetric
	public static Term getStrategyPlan(KBase kb, Term typeOfProb, Term purchaseOrder) {

		Formula strategyPattern = SL.formula("(strategy ??problem ??order ??plan)");
		QueryResult qr = kb.query((Formula)SL.instantiate(strategyPattern,"problem",typeOfProb));
		if (qr != null) { // cannot be just KNOWN
			MatchResult stratMR = qr.getResult(0);
			// take the first strategy (should be only one per problem type)
			// match the purchase orders in inverse order
			MatchResult matchOrders = stratMR.term("order").match(purchaseOrder);
			if (matchOrders != null) {
				Term plan = stratMR.term("plan");
				plan = (Term)SL.instantiate(plan,matchOrders);
				return plan;
			}
		}
		return null;		
	}


	/*****************
	 * DEBUG METHODS *
	 *****************/

	public static void printTraceMessage(String message, boolean DEBUG) {
		if (DEBUG) {
			System.err.println("JIA! "+message);
		}
	}


	/*************************
	 * INSTANTIATION METHODS *
	 *************************/
	// same as SL.instantiate, but with 5 pairs (name,value)
	public static Node instantiate(Node expression, String varname1,
			Node value1, String varname2, Node value2, String varname3,
			Node value3, String varname4, Node value4,String varname5,
			Node value5) {
		try {
			Node result = expression.getClone();
			SL.set(result, varname1, value1);
			SL.set(result, varname2, value2);
			SL.set(result, varname3, value3);
			SL.set(result, varname4, value4);
			SL.set(result, varname5, value5);

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate "+ expression);
			e.printStackTrace();
			return null;
		}
	}

	// same with 6 pairs
	public static Node instantiate(Node expression, String varname1,
			Node value1, String varname2, Node value2, String varname3,
			Node value3, String varname4, Node value4,String varname5,
			Node value5, String varname6, Node value6) {
		try {
			Node result = expression.getClone();
			SL.set(result, varname1, value1);
			SL.set(result, varname2, value2);
			SL.set(result, varname3, value3);
			SL.set(result, varname4, value4);
			SL.set(result, varname5, value5);
			SL.set(result, varname6, value6);

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate "+ expression);
			e.printStackTrace();
			return null;
		}
	}

	// same with 7 pairs
	public static Node instantiate(Node expression, String varname1,
			Node value1, String varname2, Node value2, String varname3,
			Node value3, String varname4, Node value4, String varname5,
			Node value5, String varname6, Node value6, String varname7, 
			Node value7) {
		try {
			Node result = expression.getClone();
			SL.set(result, varname1, value1);
			SL.set(result, varname2, value2);
			SL.set(result, varname3, value3);
			SL.set(result, varname4, value4);
			SL.set(result, varname5, value5);
			SL.set(result, varname6, value6);
			SL.set(result, varname7, value7);

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate "+ expression);
			e.printStackTrace();
			return null;
		}
	}

	// same with 8 pairs
	public static Node instantiate(Node expression, 
			String varname1, Node value1, 
			String varname2, Node value2, 
			String varname3, Node value3, 
			String varname4, Node value4, 
			String varname5, Node value5, 
			String varname6, Node value6, 
			String varname7, Node value7, 
			String varname8, Node value8) {
		try {
			Node result = expression.getClone();
			SL.set(result, varname1, value1);
			SL.set(result, varname2, value2);
			SL.set(result, varname3, value3);
			SL.set(result, varname4, value4);
			SL.set(result, varname5, value5);
			SL.set(result, varname6, value6);
			SL.set(result, varname7, value7);
			SL.set(result, varname8, value8);

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate "+ expression);
			e.printStackTrace();
			return null;
		}
	}

	// same with NINE pairs
	public static Node instantiate(Node expression, 
			String varname1, Node value1, 
			String varname2, Node value2, 
			String varname3, Node value3, 
			String varname4, Node value4, 
			String varname5, Node value5, 
			String varname6, Node value6, 
			String varname7, Node value7, 
			String varname8, Node value8,
			String varname9, Node value9) {
		try {
			Node result = expression.getClone();
			SL.set(result, varname1, value1);
			SL.set(result, varname2, value2);
			SL.set(result, varname3, value3);
			SL.set(result, varname4, value4);
			SL.set(result, varname5, value5);
			SL.set(result, varname6, value6);
			SL.set(result, varname7, value7);
			SL.set(result, varname8, value8);
			SL.set(result, varname9, value9);

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate "+ expression);
			e.printStackTrace();
			return null;
		}
	}

	// same with TEN pairs
	public static Node instantiate(Node expression, 
			String varname1, Node value1, 
			String varname2, Node value2, 
			String varname3, Node value3, 
			String varname4, Node value4, 
			String varname5, Node value5, 
			String varname6, Node value6, 
			String varname7, Node value7, 
			String varname8, Node value8,
			String varname9, Node value9,
			String varname10, Node value10) {
		try {
			Node result = expression.getClone();
			SL.set(result, varname1, value1);
			SL.set(result, varname2, value2);
			SL.set(result, varname3, value3);
			SL.set(result, varname4, value4);
			SL.set(result, varname5, value5);
			SL.set(result, varname6, value6);
			SL.set(result, varname7, value7);
			SL.set(result, varname8, value8);
			SL.set(result, varname9, value9);
			SL.set(result, varname10, value10);

			SL.substituteMetaReferences(result);
			return result;
		} catch (Exception e) {
			System.err.println("Exception occurs when trying to instantiate "+ expression);
			e.printStackTrace();
			return null;
		}
	}


	// version for Nodes replace two versions for formulas and terms
	// rather join all mr in only one, then call SL.instantiate(f,joinmr)
	public static Node instantiateFromMatchResults(Node f, MatchResult[] listOfMatch) {
		MatchResult globalMR = new MatchResult();
		for (int i=0;i<listOfMatch.length;i++) {
			globalMR = globalMR.join(listOfMatch[i]);
		}
		return instantiateFromMatchResult(f, globalMR);
		// thus, the clone is performed only once by SL.instantiate
		// in order not to lose links between same meta-references
	}


	public static Node instantiateFromMatchResult(Node f,MatchResult mr) {
		return SL.instantiate(f, mr);
	}

}
