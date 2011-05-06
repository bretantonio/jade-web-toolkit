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

/*
 * InstitutionalAgent.java
 * Created on 28 November 2007
 * Author : Carole Adam
 */

package jade.semantics.ext.institutions;

/*
 * Class InstitutionalAgent.java
 * Created by Carole Adam, November 28, 2007
 */

import jade.core.behaviours.SimpleBehaviour;
import jade.semantics.ext.institutions.kbase.InstitutionalOmniscienceFilters;
import jade.semantics.ext.institutions.sips.mediation.BlockadeDetection;
import jade.semantics.ext.institutions.sips.mediation.ComplaintManaging;
import jade.semantics.ext.institutions.sips.mediation.MediationFailed;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.Tools;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.kbase.observers.EventCreationObserver;
import jade.semantics.kbase.observers.Observer;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.FactNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.PredicateNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JToggleButton;


/**
 * This class provides an empty institutional agent, that is an empty JADE agent 
 * equipped with a default instance of {@link InstitutionalCapabilities}, 
 * providing special features to manage institutions. 
 * This class is to be extended by developers when they need an 
 * institutional agent, which is not based on an existing JADE agent.
 * 
 * For example in a TestAgent class extending InstitutionalAgent, the 
 * setup of the agent should begin with this call :
 * <code> super.setup(new TestAgentCapabilities(...)); </code> 
 * 
 * NEW 130808 : this class is now abstract, the getMyPath() method
 * should be instantiated in children agents to give the correct path
 * to resource files (located in the package path of each specific 
 * application and not at the location of this generic class !)
 * 
 * @author Carole Adam - France Telecom
 * @version Date: 2007/11/28 Revision: 1.0 
 */
public abstract class InstitutionalAgent extends SemanticAgent {

	private final boolean DEBUG = false;

	/************
	 * PATTERNS *
	 ************/

	private static final Formula d_INSTITUTION_power_AGENT_INSTITUTION_fact_CONDITION_ACTION_fact_EFFECT = 
		SL.formula("(D ??institution (power ??agent ??institution (fact ??condition) ??action (fact ??effect)))");


	private static final Formula d_INST_holder_AGENT_ROLE = 
		new InstitutionalFactNode(new MetaTermReferenceNode("inst"),SL.formula("(holder ??agent ??role)"));
	
	
	/****************
	 *  ATTRIBUTES  *
	 ****************/

	// attribute : GUI
	InstitutionalAgentGui gui = null;
	ArrayList listOfInstitutions;

	// content of bank account
	Long myMoney = new Long(5000);

	// only for institution
	private boolean spying = false;
	private boolean bigBrother = false;

	// only for mediator
	private boolean mediator = false;
	private boolean proactive = false;

	/***************
	 *  ACCESSORS  *
	 ***************/

	public InstitutionalAgentGui getGUI() {
		return gui;
	}
	
	public void hideGUI() {
		gui.setVisible(false);
	}
	
	public void showGUI() {
		gui.setVisible(true);
	}
	
	public void setSpying(boolean isSpying) {
		spying = isSpying;
		if (spying) {
			System.out.println(this.getName()+" starts SPYING");
		}
		else {
			System.out.println(this.getName()+" STOPS spying");
		}
	}

	public void setMediator(boolean isMediator) {
		mediator = isMediator;
	}

	public void setBigBrother(boolean isBigBrother) {
		bigBrother = isBigBrother;
		if (bigBrother) {
			System.out.println(this.getName()+" enters BIG BROTHER mode");
		}
		else {
			System.out.println(this.getName()+" quits BIG BROTHER mode");
		}
	}

	public void setProactive(boolean isProactive) {
		proactive = isProactive;
		if (proactive) {
			System.out.println(this.getName()+" is now PROACTIVE");
		}
		else {
			System.out.println(this.getName()+" is now REACTIVE");
		}
	}

	public boolean isProactive() {
		return proactive;
	}

	public Long getMyMoney() {
		return myMoney;
	}

	public void setMyMoney(Long newMoney) {
		myMoney = newMoney;
	}

	public void creditMoney(Long gain) {
		myMoney += gain;
	}

	public void debitMoney(Long loss) {
		myMoney -= loss;
	}


	/***************************
	 * ABSTRACT PATH ACCESSORS *
	 ***************************/
	
	public abstract String getMyPath();
	
	/***********************
	 * REDEFINED ACCESSORS *
	 ***********************/

	public InstitutionalCapabilities getSemanticCapabilities() {
		SemanticCapabilities capab = super.getSemanticCapabilities();
		if (capab instanceof InstitutionalCapabilities) {
			return ((InstitutionalCapabilities)capab);
		}
		System.err.println("The capabilities of the InstitutionalAgent "+getName()+" are not an instance of InstitutionalCapabilities !!!");
		throw new ClassCastException();
	}


	public void setSemanticCapabilities(InstitutionalCapabilities capab) {
		super.setSemanticCapabilities(capab);
	}

	/*********************************************************************/
	/**                         CONSTRUCTOR                             **/
	/*********************************************************************/

	/**
	 * Creates an InstitutionalAgent with a default InstitutionalCapabilities instance.
	 */
	public InstitutionalAgent() {
		this(new InstitutionalCapabilities());
	}

	/**
	 * Creates an InstitutionalAgent with a given InstitutionalCapabilities instance.
	 * By default, his list of institutions is empty (FIXME: can it be a problem?)
	 * 
	 * @param capabilities The InstitutionalCapabilities instance of the created agent
	 */
	public InstitutionalAgent(InstitutionalCapabilities capabilities) {
		super(capabilities);
		listOfInstitutions = new ArrayList();
	}


	/*********
	 * SETUP *
	 *********/

	/**
	 * Standard setup of the agent, called in the setup() method of 
	 * the agent classes extending this class, with their specific 
	 * capabilities as parameter : 
	 * <code>super.setup(new SpecificCapabilities(...));</code>
	 * 
	 * WARNING: the agent must be launched with two parameters: the
	 * name of its setup file, and an institution name.
	 * 
	 * MODIF: second parameter is now a list of institutions, and 
	 * all their files are parsed in this method; authorise an
	 * empty list, in this case read nothing
	 */
	public void setup(InstitutionalCapabilities capabilities) {
		setup(false,capabilities);
	}
	
	public void setup(boolean simplifyGui, InstitutionalCapabilities capabilities) {
		Object[] args = getArguments();

		// setup of DemoAgent
		if ( args == null || args.length < 1 ) {
			System.err.println("ERROR : agent should be run like this <agent name>:InstitutionAgent\\(<config_file_name,institution_name>\\)");
			System.err.print("ERROR : current args are :");
			if (args != null)
				for (int i=0; i<args.length; i++) {
					System.err.print(args[i]+" ");
				}
			System.err.println();
			System.exit(1);
		} else {
			System.err.println("SETUP of "+getClass()+"("+ args[0]+")");

			// set capabilities given in parameter (once the name of institution is known)
			setSemanticCapabilities(capabilities);
			if (capabilities.isConscientious()) {
				System.err.println("  >> CONSCIENTIOUS ");				
			}
			if (capabilities.isLazy()) {
				System.err.println("  >> LAZY ");				
			}
			// install capabilities, initialise SemanticInterpreterBehaviour
			super.setup();

			// scan the list of institutions to which the agent belongs
			// institution setup
			int i = 0;
			boolean keepScanning = true;
			String institutionName;
			while (keepScanning) {
				try {
					// try to get the i-th institution (triggers an exception if out of bounds)
					institutionName = (String)args[i];
					// if ok, add this institution to the list
					listOfInstitutions.add(institutionName);
					// interpret the institution specification AFTER the interpreter behaviour is installed
					InstitutionTools.readInstitutionSpecification(capabilities,institutionName);
					// also interpret the institutional actions declaration
					InstitutionTools.readInstitutionalActionsSpecification(capabilities,institutionName);
					// interpret the default rules for any institution, instantiated for this one
					InstitutionTools.readDefaultSpecification(capabilities, SL.term(institutionName));
					InstitutionTools.readDefaultActionsSpecification(capabilities, SL.term(institutionName));
					i++;
				}
				catch(IndexOutOfBoundsException e) {
					keepScanning = false;
				}
			} // number of institution files scanned = i-1

			// first boolean imposes not to absorb received messages
			// second boolean imposes to display the KBase			
			String nameOfFile = getLocalName()+".gui";  
			gui = new InstitutionalAgentGui(simplifyGui, getName(), getMyPath(), nameOfFile, this, true, true);
		}

		// only add these buttons in full mode
		if (!gui.isSimplified()) {
			addButtons();
		}

		// all institutionalAgent can use O2A communication
		// queue size fixed empirically here to 77 ...
		this.setEnabledO2ACommunication(true,77);

		// give an initial amount of money to all agents
		capabilities.getMyKBase().assertFormula(SL.formula("(has-money "+getSemanticCapabilities().getAgentName()+" 5000)"));

	} // End of setup/0


	/**
	 * Method to setup the agent representing the institution
	 * (particular setup)
	 * NOTE: no simplifyGui version of this setup since this GUI is hidden in demo
	 */
	public void setupInstitution(InstitutionalCapabilities capabilities,String institution) {
		// set spying before setup because setup adds buttons depending on spying
		setSpying(true);
		// standard setup of institutional agent
		this.setup(capabilities);
		// launch the spying behaviour
		launchWatchingBehaviour();

		// read the sanctions
		InstitutionTools.readSanctionsSpecification(capabilities,institution);

		// filters to make the institution able to answer negatively when she does not know the answer 
		// (that is, answer "false" instead of "i don't know")
		((FilterKBase)capabilities.getMyKBase()).addFiltersDefinition(new InstitutionalOmniscienceFilters());

		// to take sanctions when a mediation failed and is signalled by the mediator
		capabilities.getMySemanticInterpretationTable().addSemanticInterpretationPrinciple(new MediationFailed(capabilities));
		// to handle complaints received from agents (any agent, or the mediator)
		capabilities.getMySemanticInterpretationTable().addSemanticInterpretationPrinciple(new ComplaintManaging(capabilities));
	}


	/**
	 * Method to setup a mediator agent
	 * Should be called like this in a mediating agent setup() method
	 * super.setupMediator(new MediatingCapabilities((String)getArguments()[1]),(String)getArguments()[1]);
	 * 
	 * The first parameter at agent setup should be the institution name, 
	 * it is given as a parameter of this specific setup method
	 * 
	 * NOTE: no simplifyGui version of this setupMethod since this Gui is hidden in demo
	 */
	public void setupMediator(InstitutionalCapabilities capabilities, String institution) {
		setMediator(true);
		this.setup(capabilities);
		launchWatchingBehaviour();
		// the institution passed as a parameter is a parameter at the agent setup
		InstitutionTools.readBusinessProcessSpecificationNEW(capabilities, institution);
		InstitutionTools.readStrategiesSpecification(capabilities, institution);

		// specific SIPs to manage blockades between obliged actions
		capabilities.getMySemanticInterpretationTable().addSemanticInterpretationPrinciple(new BlockadeDetection(capabilities));
		// to allow mediator to handle complaints
		capabilities.getMySemanticInterpretationTable().addSemanticInterpretationPrinciple(new ComplaintManaging(capabilities));
		// by default the mediator is proactive (otherwise change the initial text of the proactive button)
		setProactive(true);

		// closed predicate for institutional fact (mediator is omniscient on the institution registry)
		((FilterKBase)capabilities.getMyKBase()).addFiltersDefinition(new InstitutionalOmniscienceFilters());
	}


	/*********************************
	 * Object to Agent Communication *
	 *********************************/

	/** Method used by the institution to continuously watch 
	 * the actions performed by institutional agents
	 * (an object is put in its queue by InstActionBehaviour 
	 * each time an action is successfully performed
	 * 
	 * This method should be launched at institution setup
	 */
	public void launchWatchingBehaviour() {
		// every period of 2 seconds (may be adjusted if necessary)
		// the institution checks all waiting objects in the queue
		InstitutionTools.printTraceMessage("add spying behaviour to "+this.getLocalName(),DEBUG);
		SimpleBehaviour newBehaviour = new SimpleBehaviour(this) {

			public void action() {
				Object object = this.myAgent.getO2AObject();
				if (object == null) {
					block();
				}
				else if (object instanceof DoneNode) {
					// the formula to check is not the same for institution and mediator
					String instName;
					// class cast ok since this behaviour is exclusively added to InstitutionalAgent
					InstitutionalAgent myIAgent = (InstitutionalAgent)myAgent;
					// mediating agent
					if (mediator) {
						instName = myIAgent.isMediatorOf(getSemanticCapabilities().getAgentName());
					}
					// institution agent
					else if (spying) {
						instName = getLocalName();
					}
					else {
						return;
					}
					Term actionPattern = ((ActionExpressionNode)((DoneNode)object).as_action()).as_term();
					Formula isInstitutional = new PredicateNode(
							SL.symbol("is-institutional"),
							new ListOfTerm(new Term[] {SL.term(instName),actionPattern}));
					QueryResult qr = getSemanticCapabilities().getMyKBase().query(isInstitutional);

					InstitutionTools.printTraceMessage("SPY BEHAVIOUR ACTION of "+myAgent.getLocalName(),DEBUG);
					InstitutionTools.printTraceMessage(" -> spying = "+spying,DEBUG);
					InstitutionTools.printTraceMessage(" -> is-inst-form = "+isInstitutional,DEBUG);
					InstitutionTools.printTraceMessage(" -> qr = "+qr,DEBUG);
					if ((qr!=null) || bigBrother) {
						Formula instFact = new InstitutionalFactNode(SL.term(instName),(Formula)object);
						InstitutionTools.printTraceMessage("SPY : "+myAgent.getLocalName()+" interprets "+instFact,DEBUG);
						getSemanticCapabilities().interpret(instFact);
						// also interpret the fact that the action was physically performed
						// (the institutional fact does not imply the physical fact, that is needed to deduce the effect of the action)
						getSemanticCapabilities().interpret((Formula)object);
					}						
				}// end if object read is a DoneNode
			}

			public boolean done() {
				// never stop
				return false;
			}

		};
		this.addBehaviour(newBehaviour);
	}


	/***************
	 * GUI METHODS *
	 ***************/

	/*
	 * Add buttons to set lazy and conscientious parameters
	 */
	public void addButtons() {
		// lazy button
		final JToggleButton toggleLazyButton = new JToggleButton("Set LAZY");
		toggleLazyButton.setIcon(InstitutionalAgentGui.getSystemIcon("notLazyIcon"));
		toggleLazyButton.addActionListener(new AbstractAction("ToggleLazy") {
			public void actionPerformed(ActionEvent evt) {
				if (toggleLazyButton.isSelected()) {
					toggleLazyButton.setIcon(InstitutionalAgentGui.getSystemIcon("lazyIcon"));
					toggleLazyButton.setText("Set NOT lazy");
					getSemanticCapabilities().setLazy(true);
				} 
				else {
					toggleLazyButton.setIcon(InstitutionalAgentGui.getSystemIcon("notLazyIcon"));
					toggleLazyButton.setText("Set LAZY");
					getSemanticCapabilities().setLazy(false);  // old: InstitutionalAgent.this.getSemCapab()...
				}
			}});

		// conscientious button
		final JToggleButton toggleConscientiousButton = new JToggleButton("Set Conscientious");
		toggleConscientiousButton.setIcon(InstitutionalAgentGui.getSystemIcon("notConscientiousIcon"));
		toggleConscientiousButton.addActionListener(new AbstractAction("ToggleConscientious") {
			public void actionPerformed(ActionEvent evt) {
				if (toggleConscientiousButton.isSelected()) {
					toggleConscientiousButton.setIcon(InstitutionalAgentGui.getSystemIcon("conscientiousIcon"));
					toggleConscientiousButton.setText("Set NOT conscientious");
					getSemanticCapabilities().setConscientious(true);
				} 
				else {
					toggleConscientiousButton.setIcon(InstitutionalAgentGui.getSystemIcon("notConscientiousIcon"));
					toggleConscientiousButton.setText("Set CONSCIENTIOUS");
					getSemanticCapabilities().setConscientious(false);
				}
			}});

		// add buttons
		// grid layout for this panel
		JPanel normalButtons = new JPanel();
		normalButtons.setLayout(new GridLayout(1,2));
		normalButtons.add(toggleLazyButton);
		normalButtons.add(toggleConscientiousButton);

		JPanel allButtons;

		if (spying) {
			final JToggleButton toggleSpyingButton = new JToggleButton("Stop Spying");
			toggleSpyingButton.setIcon(InstitutionalAgentGui.getSystemIcon("spyingIcon"));
			toggleSpyingButton.addActionListener(new AbstractAction("ToggleSpying") {
				public void actionPerformed(ActionEvent evt) {
					if (toggleSpyingButton.isSelected()) {
						toggleSpyingButton.setIcon(InstitutionalAgentGui.getSystemIcon("stopSpyingIcon"));
						toggleSpyingButton.setText("Start Spying");
						setSpying(false);
					} 
					else {
						toggleSpyingButton.setIcon(InstitutionalAgentGui.getSystemIcon("spyingIcon"));
						toggleSpyingButton.setText("Stop spying");
						setSpying(true);
					}
				}});
			allButtons = new JPanel();
			allButtons.setLayout(new GridLayout(2,1));
			allButtons.add(normalButtons);
			allButtons.add(toggleSpyingButton);
		}
		else if (mediator) {
			final JToggleButton toggleProactiveButton = new JToggleButton("Set REACTIVE");
			toggleProactiveButton.setIcon(InstitutionalAgentGui.getSystemIcon("proactiveIcon"));
			toggleProactiveButton.addActionListener(new AbstractAction("ToggleProactive") {
				public void actionPerformed(ActionEvent evt) {
					if (toggleProactiveButton.isSelected()) {
						toggleProactiveButton.setIcon(InstitutionalAgentGui.getSystemIcon("reactiveIcon"));
						toggleProactiveButton.setText("Set PROACTIVE");
						setProactive(false);
					} 
					else {
						toggleProactiveButton.setIcon(InstitutionalAgentGui.getSystemIcon("proactiveIcon"));
						toggleProactiveButton.setText("Set REACTIVE");
						setProactive(true);
					}
				}});
			allButtons = new JPanel();
			allButtons.setLayout(new GridLayout(2,1));
			allButtons.add(normalButtons);
			allButtons.add(toggleProactiveButton);
		}
		else {
			allButtons = normalButtons;
		}

		gui.getCustomPanel().add(BorderLayout.SOUTH,allButtons);
		gui.pack();

	}



	/***********
	 * METHODS *
	 ***********/

	/**
	 * Get all institutions to which the agent belongs, that is institutions
	 * for which he has institutional beliefs in his KBase
	 * This list is initialised at setup (list of institutions given 
	 * as a parameter of the agent)
	 */
	public ArrayList getAllInstitutions() {
		return listOfInstitutions;
	}


	/**
	 * Check if the agent given as a parameter is an institution
	 * to which the calling agent belongs.
	 * @param agent the agent that could be an institution
	 * @return a boolean indicating if this agent is one of the calling agent's institutions
	 */
	public boolean belongsToInstitution(Term agent) {
		return (getAllInstitutions().indexOf(Tools.term2AID(agent).getLocalName())>-1);
	}


	/**
	 * This method returns the action that the calling agent believes that the agent
	 * given as a first parameter (possibly the same as the calling agent) can do to 
	 * create an obligation that phi holds.
	 * In particular it is used to know how to oblige another agent to perform an 
	 * action (case of phi=done(other,action) )
	 * The calling agent is the one whose KBase is used to check the formulas.
	 *  
	 * @param agentPower the agent that should have the searched power
	 * @param phi the formula that the agent wants to be true in a given institution
	 * @param institution the institution in which the agents search a power
	 * @return an action expression the agent can do to make phi true in this institution (this is the procedure of the found institutional power)
	 */
	public ActionExpression existPower(Term agentPower, 
			Formula phi, String institution) {

		if (institution==null) { return null;}

		// instantiate some features
		// DO NOT instantiate effect since the effect of the power can be a AndNode including the wanted effect
		Formula powerPattern = (Formula)SL.instantiate(
				d_INSTITUTION_power_AGENT_INSTITUTION_fact_CONDITION_ACTION_fact_EFFECT,
				"institution",SL.term(institution));
		QueryResult exists = getSemanticCapabilities().getMyKBase().query(powerPattern);

		if (exists != null) {
			Formula wantedEffect = new ObligationNode(phi).getSimplifiedFormula();

			// the loop considers all results, does not stop before the last one
			for (int i=0;i<exists.size();i++) {
				MatchResult existi = exists.getResult(i);
				Term agent = existi.term("agent");
				MatchResult agentMatch = agent.match(agentPower); 
				if (agentMatch != null) {
					// instantiate the condition with this match to check it
					// instantiate condition from the matching of power-agent with calling agent
					Formula condition = existi.formula("condition").getSimplifiedFormula();
					condition = (Formula)InstitutionTools.instantiateFromMatchResult(condition,agentMatch);

					// Check this condition in the KBase to fully instantiate it if needed
					// The precondition is NOT institutional BUT physical
					QueryResult qr = getSemanticCapabilities().getMyKBase().query(condition);

					if (qr != null) {
						// instantiate the effect of this power 
						Formula effect = existi.formula("effect").getSimplifiedFormula();
						effect = (Formula)InstitutionTools.instantiateFromMatchResult(effect,agentMatch);

						// If qr is not just KNOWN, use it to further instantiate the effect
						if (qr.size()==0) { 
							// In this case qr=KNOWN, so effect is already instantiated
							// 1) check if it contains the wanted effect
							MatchResult effectMatch = effectMatchWithMultipleEffects(effect, wantedEffect);

							// 2) instantiate and return the corresponding procedure
							if (effectMatch != null) {
								// instantiate procedure with two matchResults
								Term procedure = existi.term("action").getSimplifiedTerm();
								procedure = (Term)InstitutionTools.instantiateFromMatchResults(procedure,new MatchResult[]{effectMatch,agentMatch});
								// return this instantiated procedure as a plan
								return new ActionExpressionNode(agentPower,procedure);
							}
						}
						else { 
							// In this case qr.size()>0, so one can use it to instantiate
							for (int k=0;k<qr.size();k++) {
								MatchResult mrK = qr.getResult(k);
								Formula effectK = (Formula)InstitutionTools.instantiateFromMatchResult(effect,mrK);
								// and check if it contains the wanted effect
								MatchResult effectMatchK = effectMatchWithMultipleEffects(effectK, wantedEffect);
								if (effectMatchK != null) {
									// instantiate procedure with now three matchResults
									Term procedureK = existi.term("action").getSimplifiedTerm();
									procedureK = (Term)InstitutionTools.instantiateFromMatchResults(procedureK,new MatchResult[]{effectMatchK,agentMatch,mrK});
									ActionExpression actionToDo = new ActionExpressionNode(agentPower,procedureK);
									// return the first procedure found
									return actionToDo;
								}
								// else: loop and try with the next instantiation of effect
							}
						}	
						/* FIXME 1 : if the first power found leads to a failure, the next 
						 * ones are not checked so possibly no action is returned while 
						 * one exists
						 * FIXME 2 : if the receiver of the request to perform an action 
						 * does not belong to the institution giving this power to the
						 * sender, is it a problem ? should not be...  
						 */
					}
				}				
			}// end of for loop
		}
		return null;
	}


	/**
	 * Same method as previous but it needs no institution as parameter.
	 * It checks all institutions the agent belongs to and tries to find 
	 * in one of them a power corresponding to what is searched.
	 * FIXME: as a consequence one does not know in which institution the
	 * returned procedure is valid to create an obligation.
	 *    - return a PowerRepresentation instead ?
	 *    - return a HashMap associating each institution the agent belongs 
	 *    to with the possible action expressions he can use in this institution ?
	 * FIXME: it should be checked if the other agent that this agent wants
	 * to oblige also belongs to the institution, otherwise the returned
	 * power is not valid to oblige him...
	 * 
	 * @param agentPower the agent that should have the searched power
	 * @param phi the formula that the agent wants to be true in a given institution
	 * @return an action expression the agent can do to make phi true (this action expression is the procedure of the found institutional power)
	 */
	public ActionExpression existPower(Term agentPower, Formula phi) {
		ActionExpression ae=null;
		// get all institutions for which the agent has institutional facts in his kbase
		ArrayList listOfInst = getAllInstitutions();
		int i=0;
		String inst;
		// scan this list of institutions until in one of them the agent has the searched power
		while ((i<listOfInst.size()) && (ae == null)) {
			inst = (String)listOfInst.get(i);
			ae = existPower(agentPower,phi,inst);
			i++;
			// TODO: if ae already done, continue to find a possibly second existing power
		}

		// returns a possibly null action expression
		// (if end of list reached before founding a power)
		return ae;
	}



	/**
	 * Method trying to get a matching between a wanted effect and the effect
	 * of a power, that can be an AndNode, or several imbricated AndNodes.
	 * Recursively manages these cases.
	 * @param effect the effect of a power, possible a AndNode or another complex node
	 * @param wantedEffect the searched effect, atomic obligation node
	 * @return a matchResult obtained after decomposing the effect of the power and recursively matched it with the searched effect
	 */
	public MatchResult effectMatchWithMultipleEffects(Formula effect, Formula wantedEffect) {
		// Recursively decompose the effect if it is made up of multiple obligations
		if (effect instanceof AndNode) {
			ListOfFormula andLeaves = ((AndNode)effect).getLeaves();
			// recursively browse the list to find one atomic effect that match
			MatchResult matchi=null;
			Formula effecti;
			int i=0;
			// stop as soon as one result is not null
			while (i<andLeaves.size() && (matchi==null)) {
				effecti = (Formula)andLeaves.get(i);
				matchi = effectMatchWithMultipleEffects(effecti, wantedEffect);
				i++;
			}
			return matchi;
		}
		return effect.match(wantedEffect);
	}


	/**
	 * This method reads the institutional powers of the agent that are 
	 * triggered by the given procedure in the given institution, and if 
	 * their condition is valid and their effect is to create an obligation 
	 * for an agent to perform some action, then this agent is added to the 
	 * returned list of agents concerned with the procedure.
	 * 
	 * @param procedure the action done by the agent (NOT an action expression, but its action parameter)
	 * @param institution the institution in which to search a power
	 * @return the list of agents that this procedure obliges to do something in the given institution
	 */
	public ArrayList getConcernedAgents(Term procedure,String institution) {
		ArrayList listOfAgents = new ArrayList();

		// query the kbase about a power 
		Formula powerPattern = SL.formula("(D "+institution+" (power ??agent ??institution (fact ??condition) ??action (fact ??effect)))").getSimplifiedFormula();
		Formula powerFormula = powerPattern.instantiate("action", procedure);
		powerFormula = powerFormula.instantiate("agent",getSemanticCapabilities().getAgentName());
		powerFormula = powerFormula.instantiate("institution",SL.term(institution));
		QueryResult exists = getSemanticCapabilities().getMyKBase().query(powerFormula);
		if (exists != null) {
			// scan the list of results
			MatchResult existi;
			for (int i=0;i<exists.size();i++) {
				existi = exists.getResult(i);
				// get the condition pattern
				Formula condition = existi.formula("condition").getSimplifiedFormula();
				// check if the instantiated condition is valid
				condition = (Formula)InstitutionTools.instantiateFromMatchResult(condition,existi); //FIXME
				Formula toAsk = SL.formula("(D wto "+condition+" )");
				QueryResult qr = getSemanticCapabilities().getMyKBase().query(toAsk);
				if (qr != null) {
					// instantiate the effect from the condition
					Formula powerEffect = existi.formula("effect").getSimplifiedFormula();
					powerEffect = (Formula)InstitutionTools.instantiateFromMatchResult(powerEffect,existi); //FIXME
					// scan the possible instantiations of the condition
					for (int j=0;j<qr.size();j++) {
						MatchResult mr = qr.getResult(j); 
						powerEffect = (Formula)InstitutionTools.instantiateFromMatchResult(powerEffect,mr);
					}//end for all instantiations of condition
					// extract agents outside the for loop (because if qr is KNOWN the loop is never entered)
					listOfAgents = extractAgentsFromObligation(powerEffect,listOfAgents);
				}//end if condition valid
			}//end for all powers
		}//end if exists power
		return listOfAgents;
	}


	/**
	 * Same method but without giving the institution.
	 * The agent scans all his institutions
	 * 
	 * @param procedure the action for which the agent is looking for concerned agents
	 * @return the ArrayList of all agents concerned by this action in any of the institutions the agent belongs to
	 */
	public ArrayList getConcernedAgents(Term procedure) {
		ArrayList listOfInstitutions = getAllInstitutions();
		ArrayList listOfConcernedAgents = new ArrayList();
		for (int i=0;i<listOfInstitutions.size();i++) {
			String institution = listOfInstitutions.get(i).toString();
			ArrayList listInThisInstitution = getConcernedAgents(procedure,institution);
			for (int j=0;j<listInThisInstitution.size();j++) {
				listOfConcernedAgents.add(listInThisInstitution.get(j));
			}
		}
		return listOfConcernedAgents;		
	}


	/**
	 * Useful auxiliary method for getConcernedAgents: return the given ArrayList 
	 * filled with new agents extracted from the effect of the power given in 
	 * parameter (decomposes AndNodes into obligation leaves if needed)
	 * 
	 * @param effectOfPower the power to analyse and recursively decompose to get concerned agents
	 * @param agents the arrayList of agents (Terms) to fill with new agents extracted from the given effect
	 * @return the given arrayList filled with new agents
	 */ 
	public ArrayList extractAgentsFromObligation(Formula effectOfPower, ArrayList agents) {
		// check if the effect of power is an obligation for some agent
		if (effectOfPower instanceof ObligationNode) {
			Formula f = ((ObligationNode)effectOfPower).as_formula();
			// agents who gain an obligation to perform some action
			// are concerned by the performance of the procedure
			if (f instanceof DoneNode) {
				Term t = ((DoneNode)f).as_action();
				if (t instanceof ActionExpression) {
					Term anAgent = ((ActionExpression)t).getActor();
					// check if the agent is not already in the list
					if (agents.indexOf(anAgent)<0) {
						agents.add(anAgent);
					}
				}
			}
			// Agents who gain an obligation to believe are
			// also concerned by the performance of the action
			if (f instanceof BelieveNode) {
				Term anAgent = ((BelieveNode)f).as_agent();
				if (agents.indexOf(anAgent)<0) {
					agents.add(anAgent);
				}
			}
			return agents;
		}//end if power effect is an obligation
		// or if it is a combination (AND) of obligations: treated separately
		else if (effectOfPower instanceof AndNode) {
			ListOfFormula leaves = ((AndNode)effectOfPower).getLeaves();
			ArrayList temp = agents;
			for (int k=0;k<leaves.size();k++) {
				Formula obligationLeave = leaves.element(k);
				temp = extractAgentsFromObligation(obligationLeave,temp);
			}
			return temp;
		}
		// in other cases do nothing, just return the parameter
		return agents;
	}


	/**
	 * Method to get agents interested by the performance of an action,
	 * that is agents for who the agent believes 
	 * (is-interested ??thisAgent ??thisAction).
	 * This predicate was asserted by InstitutionalIntentionTransferSIP
	 * for all agents informing the agent about an intention that he 
	 * performs an action; and (NEW) also by ObligationNotification for
	 * all agents notifying the agent that he is obliged to perform this
	 * action
	 * 
	 * This method is used in InstActionBehaviour to get agents who should
	 * be informed about the performance of the action.
	 */

	/**
	 * Gets interested agents
	 * if the boolean remove is true, also retracts the predicate 
	 * is-interested after querying it
	 */
	public ArrayList getInterestedAgents(Term action,boolean remove) {
		ArrayList result = new ArrayList();
		Formula interested = new PredicateNode(SL.symbol("is_interested"),
				new ListOfTerm(new Term[] {new MetaTermReferenceNode("anyAgent"),
						// the requested action
						action}));

		QueryResult qr = getSemanticCapabilities().getMyKBase().query(interested);
		if (qr!=null) {
			for (int k=0;k<qr.size();k++) {
				Term anAgent = qr.getResult(k).term("anyAgent");
				Formula oneInterested = interested;
				oneInterested = oneInterested.instantiate("anyAgent",anAgent);
				// do not add the self agent to the list
				// (obviously he has the intention to perform the action he performs)
				if (!anAgent.equals(getSemanticCapabilities().getAgentName())) {
					result.add(anAgent);
					if (remove) {
						getSemanticCapabilities().getMyKBase().retractFormula(oneInterested);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Get interested agents as before but also removes the predicate
	 * telling that they are interested: these agents will NOT be
	 * interested in the subsequent performances of this action, and
	 * thus they will not be informed anymore
	 * @param action the action that interests searched agents
	 * @return an ArrayList of agents interested in this action
	 */
	public ArrayList getAndRemoveInterestedAgents(Term action) {
		return getInterestedAgents(action,true);
	}

	/**
	 * get the list of agents physically observing the action. They are
	 * specified in the file <inst>.actions for each institutional action.
	 * The interpretation of this file results in the assertion of the
	 * predicate "(is_observing ??agent ??action)"
	 * 
	 * @param action the action observed
	 * @return an ArrayList of agents observing this action
	 */
	public ArrayList getObservingAgents(ActionExpressionNode action) {
		ArrayList result = new ArrayList();
		Formula observing = new PredicateNode(SL.symbol("is_observing"),
				new ListOfTerm(new Term[] {new MetaTermReferenceNode("anyAgent"),
						// the requested action
						action}));
		InstitutionTools.printTraceMessage("getObservingAgents, f="+observing,DEBUG);

		QueryResult qr = getSemanticCapabilities().getMyKBase().query(observing);
		InstitutionTools.printTraceMessage("qr="+qr,DEBUG);
		if (qr!=null) {
			for (int k=0;k<qr.size();k++) {   // qr cannot be just KNOWN
				Term anAgent = qr.getResult(k).term("anyAgent");
				Formula oneObserving = observing;
				oneObserving = oneObserving.instantiate("anyAgent",anAgent);
				// do not add the self agent to the list
				// (obviously he has the intention to perform the action he performs)
				if (!anAgent.equals(getSemanticCapabilities().getAgentName())) {
					result.add(anAgent);
					// DO NOT retract observing agents after one occurrence
					// (action could be performed twice or more, and observers should still observe it)
				}
			}
		}
		return result;

	}


	/**
	 * Checks if the agent is observing the given action
	 */
	public boolean isObserving(ActionExpression action) {
		Formula observing = new PredicateNode(SL.symbol("is_observing"),
				new ListOfTerm(new Term[] {
						// the agent calling this method
						this.getSemanticCapabilities().getAgentName(),
						// the given action
						action}));
		InstitutionTools.printTraceMessage("is_observing = "+observing,DEBUG);
		QueryResult qr = getSemanticCapabilities().getMyKBase().query(observing);
		InstitutionTools.printTraceMessage("qr="+qr,DEBUG);
		return (qr != null);
	}

	/**
	 * Asserts that an agent now observes the given action
	 */
	public void setObserving(ActionExpression action) {
		Formula observing = new PredicateNode(SL.symbol("is_observing"),
				new ListOfTerm(new Term[] {
						// the agent calling this method
						this.getSemanticCapabilities().getAgentName(),
						// the given action
						action}));
		getSemanticCapabilities().interpret(observing);
	}


	/**
	 * Asserts that an agent now observes the given action
	 * and retracts it once he has observed one occurrence of this action
	 */
	public void setObservingOneTime(ActionExpression action) {
		Formula observing = new PredicateNode(SL.symbol("is_observing"),
				new ListOfTerm(new Term[] {
						// the agent calling this method
						this.getSemanticCapabilities().getAgentName(),
						// the given action
						action}));

		// if the agent is not already observing
		if (getSemanticCapabilities().getMyKBase().query(observing) == null) {
			getSemanticCapabilities().interpret(observing);

			Observer obs = new EventCreationObserver(
					// the observing agent's kbase
					getSemanticCapabilities().getMyKBase(),
					// watch when the action is performed once
					new DoneNode(action,SL.TRUE),
					// when it is, retract the fact that the agent is observing it
					new NotNode(observing),
					// the interpreter behaviour
					getSemanticCapabilities().getSemanticInterpreterBehaviour());
			obs.update(null);
			getSemanticCapabilities().getMyKBase().addObserver(obs);
		}
	}


	/**
	 * Assert that a mediator is managing a problem with the given 
	 * obligation. This method is called in ObligationInterpretation
	 * when a mediator proactively starts watching for the respect of
	 * an obligation, or in ComplaintManaging when a mediator starts
	 * watching the respect of an obligation whose violation was 
	 * signalled by a complaining agent. 
	 */
	public void setManaging(InstitutionalFactNode institutionalObligation) {
		Formula managing = new PredicateNode(SL.symbol("is_managing"),
				new ListOfTerm(new Term[] {
						// the agent calling this method = the mediator
						this.getSemanticCapabilities().getAgentName(),
						// the given obligation
						new FactNode(institutionalObligation)}));
		getSemanticCapabilities().getMyKBase().assertFormula(managing);
	}

	/**
	 * Corresponding accessor to check if the mediator is managing
	 * a given institutional obligation
	 */
	public boolean isManaging(InstitutionalFactNode institutionalObligation) {
		Formula managing = new PredicateNode(SL.symbol("is_managing"),
				new ListOfTerm(new Term[] {
						// the agent calling this method = the mediator
						this.getSemanticCapabilities().getAgentName(),
						// the given obligation
						new FactNode(institutionalObligation)}));
		return (getSemanticCapabilities().getMyKBase().query(managing) != null);
	}


	/**
	 * Get the agent that this agent believes to be the mediator
	 * designed by the given institution
	 */
	public Term getMediator(String institution) {
		Term mediator = InstitutionTools.getHoldingAgent(getSemanticCapabilities().getMyKBase(),institution,"roleMediator"); 
		InstitutionTools.printTraceMessage("mediator in "+institution+" is "+mediator,DEBUG);
		return mediator;
	}

	public boolean believesThatMediatorIs(String institution,Term agent) {
		return agent.equals(getMediator(institution));
	}
	
	public Term getIntermediary(String institution) {
		return InstitutionTools.getHoldingAgent(getSemanticCapabilities().getMyKBase(),institution,"roleIntermediary");
	}
	
	public boolean believesToBeMediator(String institution) {
		return believesThatMediatorIs(institution, getSemanticCapabilities().getAgentName()); 
	}

	// Return true if the agent is mediator of ANY institution
	public boolean isMediator(Term agent) {
		Formula f = (Formula)SL.instantiate(
				d_INST_holder_AGENT_ROLE,
				"agent",agent,
				"role",SL.term("roleMediator"));
		QueryResult qr = getSemanticCapabilities().getMyKBase().query(f);
		return (qr != null);
	}

	// Get the institution in which an agent is the mediator
	public String isMediatorOf(Term agent) {
		Formula f = new InstitutionalFactNode(new MetaTermReferenceNode("inst"),SL.formula("(holder ??agent ??role)"));
		f = f.instantiate("agent",agent);
		f = f.instantiate("role",SL.term("roleMediator"));
		QueryResult qr = getSemanticCapabilities().getMyKBase().query(f);
		if (qr != null) { // cannot be just known
			return (qr.getResult(0).term("inst").toString());
		}
		return null;
	}

	/* TODO 
	 * getTrustedAgents(String inst): returns the institution
	 * agent and all its mediators or delegates...
	 */ 

} 
