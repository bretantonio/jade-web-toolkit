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
 * Class InstitutionalCapabilities.java
 * Created by Carole Adam, November 2007
 */

import jade.core.Agent;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.ext.institutions.kbase.CountAsFilters;
import jade.semantics.ext.institutions.kbase.InstitutionalFactFilters;
import jade.semantics.ext.institutions.kbase.MoneyFiltersNew;
import jade.semantics.ext.institutions.sips.actions.InstitutionalActionDeclaration;
import jade.semantics.ext.institutions.sips.actions.InstitutionalActionDoneNew;
import jade.semantics.ext.institutions.sips.actions.ObligedActionDone;
import jade.semantics.ext.institutions.sips.generic.InstitutionalSinceFormulaInterpretation;
import jade.semantics.ext.institutions.sips.generic.InstitutionalUntilFormulaInterpretation;
import jade.semantics.ext.institutions.sips.generic.PaymentInterpretation;
import jade.semantics.ext.institutions.sips.generic.PeriodFormulaInterpretation;
import jade.semantics.ext.institutions.sips.generic.SinceFormulaInterpretation;
import jade.semantics.ext.institutions.sips.generic.SplitInstitutionalFact;
import jade.semantics.ext.institutions.sips.generic.TimePredicateSIP;
import jade.semantics.ext.institutions.sips.generic.UntilFormulaInterpretation;
import jade.semantics.ext.institutions.sips.interaction.CommitmentInterpretation;
import jade.semantics.ext.institutions.sips.interaction.ConditionalObligationInterpretation;
import jade.semantics.ext.institutions.sips.interaction.GroundedBeliefTransfer;
import jade.semantics.ext.institutions.sips.interaction.InstitutionalBeliefTransfer;
import jade.semantics.ext.institutions.sips.interaction.InstitutionalIntentionTransfer;
import jade.semantics.ext.institutions.sips.interaction.ObligationInterpretation;
import jade.semantics.ext.institutions.sips.interaction.ObligationNotification;
import jade.semantics.ext.institutions.sips.planning.FutureObligationAnticipation;
import jade.semantics.ext.institutions.sips.planning.ObligationCreationSIP;
import jade.semantics.ext.institutions.sips.planning.ObligationTransfer;
import jade.semantics.ext.institutions.sips.planning.PerseveranceSIP;
import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.kbase.ArrayListKBaseImpl;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.QueryResult;
import jade.semantics.kbase.filters.FilterKBaseImpl;
import jade.semantics.kbase.filters.std.DefaultFilterKBaseLoader;
import jade.semantics.kbase.filters.std.NestedBeliefFilters;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.DoneNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ImpliesNode;
import jade.semantics.lang.sl.grammar.InstitutionalFactNode;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.ObligationNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

/**
 * This class allows to create InstitutionalCapabilities for
 * InstitutionalAgents. InstitutionalCapabilities are particular
 * SemanticCapabilities extended with new SIPs and filters to
 * manage institution related concepts.
 * 
 * @author wdvh2120
 * @version Date November 2007
 */
public class InstitutionalCapabilities extends DefaultCapabilities {

	/**********************
	 * *** ATTRIBUTES *** *
	 **********************/

	// specifies if the agent is lazy (true) or cooperative (false)
	// by default it is set to false (cooperative)
	// a lazy agent only obeys to explicitly notified obligations
	private boolean lazy;

	// specifies if the agent is conscientious (true) or not
	// by default it is set to false (normal agent)
	// a conscientious agent watches if other agents obey the obligations
	// that he creates while using his institutional powers
	// @see InstitutionalActionDone SIP
	private boolean conscientious;

	// trustless agent check everything before trusting the sender of an inform
	private boolean trustless;

	// super lazy agents only consider obligations notified by the mediator of the institution
	private boolean superLazy=false;

	// added 130808 - myPath (the package path of the children class, used to locate resources like the agent's gui file)
	//private String myPath="";
	
	/************************
	 * *** CONSTRUCTORS *** *
	 ************************/

	public InstitutionalCapabilities() {
		super();
		lazy = false;
		conscientious = false;
		trustless = false;
	}

	public InstitutionalCapabilities(boolean isLazy, boolean isConscientious, boolean isTrustless) {
		super();
		lazy = isLazy;
		conscientious = isConscientious;
		trustless = isTrustless;
	}

	
	/***********************
	 * REDEFINED ACCESSORS *
	 ***********************/

	@Override
	public InstitutionalAgent getAgent() {
		Agent agent = super.getAgent();
		if (agent instanceof InstitutionalAgent) {
			return (InstitutionalAgent)agent;
		}
		System.err.println("The agent "+getAgentName()+" should be an InstitutionalAgent and he is a "+agent.getClass()+" !!!");
		throw new ClassCastException();
	}
	

	/*********************
	 * *** ACCESSORS *** *
	 *********************/

	/**
	 * Method to know if these capabilities are in lazy mode or not
	 * @return the lazy attribute
	 */
	public boolean isLazy() {
		return lazy;
	}

	/**
	 * Set these capabilities in a given mode of laziness
	 * @param isLazy boolean indicating in which mode to set the capabilities (true: lazy, false: cooperative)
	 */
	public void setLazy(boolean isLazy) {
		lazy = isLazy;
		if (lazy) {
			System.out.println(this.getAgentName()+" is LAZY");
		}
		else {
			System.out.println(this.getAgentName()+" is NOT lazy");
		}
	}


	/**
	 * Method to know if these capabilities are in conscientious mode or not
	 * @return the conscientious boolean attribute
	 */
	public boolean isConscientious() {
		return conscientious;
	}

	/**
	 * Set these capabilities in a given mode of conscientiousness
	 * @param isConscientious boolean indicating in which mode to set the capabilities (true: conscientious, false: normal)
	 */
	public void setConscientious(boolean isConscientious) {
		conscientious = isConscientious;
		if (conscientious) {
			System.out.println(this.getAgentName()+" is CONSCIENTIOUS");
		}
		else {
			System.out.println(this.getAgentName()+" is NOT conscientious");
		}
	}


	/**
	 * Method to know if these capabilities are in lazy mode or not
	 * @return the lazy attribute
	 */
	public boolean isTrustless() {
		return trustless;
	}

	/**
	 * Set these capabilities in a given mode of laziness
	 * @param isLazy boolean indicating in which mode to set the capabilities (true: lazy, false: cooperative)
	 */
	public void setTrustless(boolean isTrustless) {
		trustless = isTrustless;
		if (trustless) {
			System.out.println(this.getAgentName()+" is TRUSTLESS");
		}
		else {
			System.out.println(this.getAgentName()+" is NOT trustless");
		}
	}

	/**
	 * Super lazy (as a debug option for now)
	 */
	public void setSuperLazy(boolean isSuperLazy) {
		superLazy = isSuperLazy;
		if (superLazy) {
			System.out.println(this.getAgentName()+" is SUPER LAZY");
		}
		else {
			System.out.println(this.getAgentName()+" is NOT super lazy");
		}
	}

	public boolean isSuperLazy() {
		return superLazy;
	}

	public boolean isProactive() {
		return getAgent().isProactive();
	}

	public String getMyPath() {
		return getAgent().getMyPath();
	}
	
	/*************************
	 * *** SETUP METHODS *** *
	 *************************/

	@Override
	protected KBase setupKbase() {
		FilterKBaseImpl kbase = (FilterKBaseImpl)super.setupKbase();

		// Add filters for institutional facts
		InstitutionalFactFilters iff = new InstitutionalFactFilters() {
			@Override
			public KBase newInstance(Term agent) {
				// create institutional kbase with default filters
				FilterKBaseImpl newkbase = new FilterKBaseImpl(new ArrayListKBaseImpl(agent),new DefaultFilterKBaseLoader());
				// put other filters !!! (not loaded in the default loader)
				NestedBeliefFilters newnbf = new NestedBeliefFilters() {
					@Override
					public KBase newInstance(Term agent) {
						return new FilterKBaseImpl(new ArrayListKBaseImpl(agent));
					}
				};
				newkbase.addFiltersDefinition(newnbf);
				return newkbase;
			} 
		};
		kbase.addFiltersDefinition(iff);

		// nested belief filters
		NestedBeliefFilters nbf = new NestedBeliefFilters() {
			@Override
			public KBase newInstance(Term agent) {
				FilterKBaseImpl newkbase = new FilterKBaseImpl(new ArrayListKBaseImpl(agent));
				// put other filters !!! (not loaded in the default loader)
				MoneyFiltersNew newmfn = new MoneyFiltersNew() {
					@Override
					public KBase newInstance(Term agent) {
						return new FilterKBaseImpl(new ArrayListKBaseImpl(agent));
					}
				};
				// TODO only prevent from storing beliefs on other agent's beliefs on other agents' money
				// for example B-i B-j (has-money j x) is still stored ... (belief about his own money, even if nested...)
				// should add (instead) a specific filter absorbing any belief about money
				newkbase.addFiltersDefinition(newmfn);
				return newkbase;
			}
		};
		kbase.addFiltersDefinition(nbf);
	
		// Add filters for countAs formulas
		kbase.addFiltersDefinition(new CountAsFilters() {
			@Override
			public KBase newInstance(Term agent) {
				return new FilterKBaseImpl(new ArrayListKBaseImpl(agent));
			}
		});

		// add filters for managing the transfer of money between agents
		kbase.addFiltersDefinition(new MoneyFiltersNew() {
			@Override
			public KBase newInstance(Term agent) {
				return new FilterKBaseImpl(new ArrayListKBaseImpl(agent));
			}
		});

		return kbase;
	}


	@Override
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {

		SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();

		// ******* ************* GENERIC ************* *******
		
		// split institutional AndNodes into an AndNode of InstitutionalFacts
		table.addSemanticInterpretationPrinciple(new SplitInstitutionalFact(this));
		// managing of formulas with a limited validity
		table.addSemanticInterpretationPrinciple(new UntilFormulaInterpretation(this));
		table.addSemanticInterpretationPrinciple(new InstitutionalUntilFormulaInterpretation(this));
		table.addSemanticInterpretationPrinciple(new SinceFormulaInterpretation(this));
		table.addSemanticInterpretationPrinciple(new InstitutionalSinceFormulaInterpretation(this));
		table.addSemanticInterpretationPrinciple(new PeriodFormulaInterpretation(this));
		// to avoid flooding the kbase with time predicates
		table.addSemanticInterpretationPrinciple(new TimePredicateSIP(this));
		// trick to update bank account of receiver after a PAY action
		table.addSemanticInterpretationPrinciple(new PaymentInterpretation(this));
		
		// ************* ******* TRANSFER / INTERACTION ******* *************
		// (from another agent)
		
		// specific managing of requests for institutional agents
		table.addSemanticInterpretationPrinciple(new InstitutionalIntentionTransfer(this));
		// special management of explicitly notified obligations 
		table.addSemanticInterpretationPrinciple(new ObligationNotification(this));
		// prevent agents from adopting beliefs about institutional facts coming from any agent
		table.addSemanticInterpretationPrinciple(new InstitutionalBeliefTransfer(this));
		// prevent contradictions on grounded beliefs (only for trustless agents)
		if (isTrustless()) {
			table.addSemanticInterpretationPrinciple(new GroundedBeliefTransfer(this));
		}
		// manage new commitments
		table.addSemanticInterpretationPrinciple(new CommitmentInterpretation(this));
		// mediator and conscientious agents watch the performance of all obliged actions they observe
		table.addSemanticInterpretationPrinciple(new ObligationInterpretation(this));
		// force the interpretation of conditional obligations when the condition becomes true
		table.addSemanticInterpretationPrinciple(new ConditionalObligationInterpretation(this));
		
		
		// ******* ****** ******* PLANNING ******* ****** *******
		
		// cooperative transfer of obligation into intention to respect it
		table.addSemanticInterpretationPrinciple(new ObligationTransfer(this));
		// perseverance on intentions triggered by obligations
		table.addSemanticInterpretationPrinciple(new PerseveranceSIP(this));
		// possibility to create obligations from powers (planning sip)
		table.addSemanticInterpretationPrinciple(new ObligationCreationSIP(this));
		// anticipation of my future obligations (if I am not lazy)
		table.addSemanticInterpretationPrinciple(new FutureObligationAnticipation(this));
		
		// ******* ****** ******* ACTION ******* ****** *******
		
		// interpretation of the external declaration of institutional actions
		table.addSemanticInterpretationPrinciple(new InstitutionalActionDeclaration(this));
		// new management of actions done: unify old InstitutionalActionDoneSIP and InstitutionalActionInterpretation
		table.addSemanticInterpretationPrinciple(new InstitutionalActionDoneNew(this));
		// retractation of obligations to perform an action when the action is performed
		table.addSemanticInterpretationPrinciple(new ObligedActionDone(this));
		
		
		return table;
	}


	@Override
	protected SemanticActionTable setupSemanticActions() {
		SemanticActionTable table = super.setupSemanticActions();

		// PROMISE TO, in a given institution, under a given condition
		table.addSemanticAction(new OntologicalAction(this,
				"(PROMISE_TO :action ??action :creditor ??creditor :institution ??institution :condition (fact ??condition) :deadline (fact ??deadline))",
				//effect
				new ImpliesNode(
					new MetaFormulaReferenceNode("condition"),
					new InstitutionalFactNode(
						new MetaTermReferenceNode("institution"),
						new ObligationNode(
							new DoneNode(
								new ActionExpressionNode(
									new MetaTermReferenceNode("actor"),
									new MetaTermReferenceNode("action")),
								SL.TRUE)))),
				//precondition
				SL.TRUE
		) {
			@Override
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println(getAuthor()+" ... "+getActionParameter("..."));
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});
			
		// OLD VERSIONS
		
		// AN AGENT PAYS AN AMOUNT OF MONEY TO ANOTHER ONE (USED FOR SANCTIONS)
		table.addSemanticAction(new OntologicalAction(this,
				"(PAY :amount ??amount :receiver ??receiver :reference ??reference)",
				//effect
				SL.TRUE,
				//precondition
				SL.formula("(has-money ??actor ??amount)")
		) {
			@Override
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println(getAuthor()+" PAYS "+getActionParameter("amount")+" to "+getActionParameter("receiver")+" for reference "+getActionParameter("reference"));
				// physically modify his money
				Long amount = Long.parseLong(getActionParameter("amount").toString());
				InstitutionTools.debitMoney(getActor(),amount);
				InstitutionTools.creditMoney(getActionParameter("receiver"),amount);
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});

		// AN AGENT PROMISES THAT A FORMULA IS TRUE
		// institutional spied inform : register, store ? promise ?
		table.addSemanticAction(new OntologicalAction(this,
				// promise that a formula is true (and will be true during a given delay)
				// the engagement is taken towards a creditor, in a given institution
				"(PROMISE_THAT :creditor ??creditor :institution ??institution :validity ??validity :what ??what)",
				//effect (FIXME: PB: the author of the promise is NOT personally concerned by the obligation... IDEA: O(Bi-phi) ??)
				SL.TRUE,
				//precondition
				SL.TRUE
		) {
			@Override
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println(getAgentName()+" has promised (before institution "+getActionParameter("institution")+") to "+getActionParameter("creditor")+" that "+getActionParameter("what")+" will be true until "+getActionParameter("validity"));
				System.err.println("  -> corresponding commitment: C(active,"+getAuthor()+","+getActionParameter("creditor")+","+getActionParameter("what")+"|true,"+getActionParameter("validity")+")");

				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});

		// AN AGENT PROMISE TO PERFORM AN ACTION BEFORE A DELAY
		// ??action is NOT an ActionExpression but a simple action term
		table.addSemanticAction(new OntologicalAction(this,
				"(PROMISE_TO :action ??action :creditor ??creditor :institution ??institution :validity ??validity)",
				//effect (FIXME: PB: the author of the promise is NOT personally concerned by the obligation... IDEA: O(Bi-phi) ??)
				SL.TRUE,
				//precondition
				SL.TRUE
		) {
			@Override
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println(getAgentName()+" has promised (before institution "+getActionParameter("institution")+") to "+getActionParameter("creditor")+" to perform "+getActionParameter("action")+" before "+getActionParameter("delay"));
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});

		// GIVE A FINE FOR SOME REASON (possibly arguable by the condemned)
		table.addSemanticAction(new OntologicalAction(this,
				// give a fine to an agent for some reason, valid in a given institution, with a delay to pay it
				"(GIVE_FINE_TO :condemned ??condemned :amount ??amount :institution ??institution :reason ??reason :delay (fact ??delay) :reference ??reference)",
				//effect
				SL.TRUE,
				//precondition
				SL.TRUE
		) {
			@Override
			public void perform(OntoActionBehaviour behaviour) {
				System.err.println(getAgentName()+" has condemned "+getActionParameter("condemned")+ "(before institution "+getActionParameter("institution")+") to pay a fine of "+getActionParameter("amount")+" under reference "+getActionParameter("reference")+" before "+getActionParameter("delay")+" because "+getActionParameter("reason"));
				behaviour.setState(SemanticBehaviour.SUCCESS);
			}
		});
		
		return table;
	}


	/*************************
	 * *** OTHER METHODS *** *
	 *************************/

	// check if an obligation was notified to the agent holding these capabilities
	// in a given institution
	public boolean wasNotified(ObligationNode obligationNode, Term institution) {

		// check if the obligation was explicitly notified to the agent by some other agent
		Term notifyOblig = SL.term("(INFORM :sender ??sender :receiver (set ??receiver) :content (content ??content))").getSimplifiedTerm();
		notifyOblig = notifyOblig.instantiate("sender",new MetaTermReferenceNode("other"));
		notifyOblig = notifyOblig.instantiate("receiver",getAgentName());
		notifyOblig = notifyOblig.instantiate("content",new InstitutionalFactNode(institution,obligationNode));
		Formula toAsk = new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("other"),notifyOblig),SL.TRUE);
		QueryResult qr = getMyKBase().query(toAsk);

		return (qr != null);	
	}

	// check if it was notified in ANY institution
	public boolean wasNotified(ObligationNode obligationNode) {
		return wasNotified(obligationNode,new MetaTermReferenceNode("institution"));
	}

	// check if it was notified by the mediator of the corresponding institution
	public boolean wasNotifiedByMediator(ObligationNode obligationNode,Term institution) {
		// check if the obligation was explicitly notified to the agent by some other agent
		Term notifyOblig = SL.term("(INFORM :sender ??sender :receiver (set ??receiver) :content (content ??content))").getSimplifiedTerm();
		notifyOblig = notifyOblig.instantiate("sender",new MetaTermReferenceNode("other"));
		notifyOblig = notifyOblig.instantiate("receiver",getAgentName());
		notifyOblig = notifyOblig.instantiate("content",new InstitutionalFactNode(institution,obligationNode));
		Formula toAsk = new DoneNode(new ActionExpressionNode(new MetaTermReferenceNode("other"),notifyOblig),SL.TRUE);
		QueryResult qr = getMyKBase().query(toAsk);

		if (qr != null) {
			boolean result = false;
			for (int i=0;i<qr.size() && !result;i++) {
				Term sender = qr.getResult(i).term("sender");
				result = result || (getAgent().believesThatMediatorIs(institution.toString(),sender));
			}
			return result;
		}
		return false;
	}

	// check if the agent is trying to perform an action
	public boolean isTrying(Term agent,Term institution,Term action) {
		Formula istrying = InstitutionTools.buildIsTrying(agent, institution, action);
		return (getMyKBase().query(istrying) != null);
	}




}
