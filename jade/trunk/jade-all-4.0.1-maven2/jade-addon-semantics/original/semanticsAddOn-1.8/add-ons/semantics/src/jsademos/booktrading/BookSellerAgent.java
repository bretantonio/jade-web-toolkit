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

package jsademos.booktrading;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.BeliefTransferSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.kbase.filters.KBAssertFilterAdapter;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSequence;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.io.FileReader;

public class BookSellerAgent extends SemanticAgent {
	
	//---------------------------------------------------------------
	//                      CONSTANTS 
	//---------------------------------------------------------------
	static final IdentifyingExpression BOOKS_IRE = (IdentifyingExpression)
		SL.term("(some (sequence ?i ?d ?p ?m) " +
				"          (and (for_sale ?i ??myself) " +
				"               (selling_delay ?i ?d)" +
				"               (selling_price ?i ?p ??agent)" +
				"               (min_selling_price ?i ?m ??agent)))");

	static final IdentifyingExpression TITLE_IRE = (IdentifyingExpression)
		SL.term("(any ?t (title ??isbn ?t))");

	public static final Formula EQUALS_ALL_FOR_SALE =
		SL.formula("(= (all ?i (for_sale ?i ??agent)) (set))");
	
	//---------------------------------------------------------------
	//                      INNER CLASSES 
	//---------------------------------------------------------------
	class BookSellerCapabilities extends DefaultCapabilities {
		
		@Override
		protected KBase setupKbase() {
			FilterKBase kb = (FilterKBase)super.setupKbase();
			
			kb.addKBAssertFilter(new KBAssertFilterAdapter("(B ??agent "+Ontology.FOR_SALE_PREDICATE+")"){
				@Override
				public Formula doApply(Formula formula,MatchResult match) {
					queryRef((IdentifyingExpression)TITLE_IRE.instantiate("isbn", match.term("isbn")), isbnHolder);
	                return formula; 
				}
			});

			kb.addKBAssertFilter(new KBAssertFilterAdapter("(B ??agent "+Ontology.NOT_FOR_SALE_PREDICATE+")"){
				@Override
				public Formula doApply(Formula formula,MatchResult match) {
					Term isbn = match.term("isbn");
					myKBase.retractFormula(Ontology.SELLING_DELAY_PREDICATE.instantiate("isbn", isbn));
					myKBase.retractFormula(Ontology.SELLING_PRICE_PREDICATE.instantiate("isbn", isbn));
					myKBase.retractFormula(Ontology.MIN_SELLING_PRICE_PREDICATE.instantiate("isbn", isbn));
					myKBase.retractFormula(Ontology.TITLE_PREDICATE.instantiate("isbn", isbn));
					myKBase.retractFormula(Ontology.FOR_SALE_PREDICATE.instantiate("isbn", isbn));
//	                   return new TrueNode();
	                   return formula; // To trigger the subscription filters
				}
			});

			kb.addKBAssertFilter(new KBAssertFilterAdapter("(B ??agent "+Ontology.SELLING_DELAY_PREDICATE+")"){
				@Override
				public Formula doApply(Formula formula,MatchResult match) {
					Term isbn = match.term("isbn");
					myKBase.retractFormula(Ontology.SELLING_DELAY_PREDICATE.instantiate("isbn", isbn));
					return formula;
				}
			});	

			kb.addKBAssertFilter(new KBAssertFilterAdapter("(B ??agent "+Ontology.SELLING_PRICE_PREDICATE+")"){
				@Override
				public Formula doApply(Formula formula,MatchResult match) {
					Term isbn = match.term("isbn");
					myKBase.retractFormula(Ontology.SELLING_PRICE_PREDICATE.instantiate("isbn", isbn));
					return formula;
				}
			});	

			return kb;
		}
		
		@Override
		protected SemanticActionTable setupSemanticActions() {
			SemanticActionTable table = super.setupSemanticActions();
			
			table.addSemanticAction(new OntologicalAction(this,
					Ontology.SELL_ACTION_TERM,
					SL.formula("(not (for_sale ??isbn ??actor))"),
					SL.formula("(for_sale ??isbn ??actor)")) {

				@Override
				public void perform(OntoActionBehaviour behaviour) {
					System.err.println(getActionParameter("actor")+
							" is selling "+getActionParameter("isbn")+
							", to "+getActionParameter("buyer")+
							", at the price of "+getActionParameter("price"));
					behaviour.setState(SemanticBehaviour.SUCCESS);
				}
			});
			
			return table;
		}

		@Override
		protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {

			SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();
			
			table.addSemanticInterpretationPrinciple(
					new BeliefTransferSIPAdapter(BookSellerCapabilities.this,
					                             Ontology.SELLING_PRICE_PREDICATE,
					                             new MetaTermReferenceNode("agent")) {
						@Override
						protected ArrayList doApply(MatchResult matchFormula, 
								                    MatchResult matchAgent, 
								                    ArrayList acceptResult, 
								                    ArrayList refuseResult, 
								                    SemanticRepresentation sr) {
							return getAgentName().equals(matchFormula.term("seller")) ?
									acceptResult :refuseResult;
						}
					});
			
			return table;
		}
	}
	
	class UpdateBooksBehaviour extends TickerBehaviour {

		protected long period = 5000;
		
		public UpdateBooksBehaviour(Agent agent, long period) {
			super(agent, period);
			this.period = period;
		}
		
		@Override
		protected void onTick() {
			FilterKBase kb = (FilterKBase)getSemanticCapabilities().getMyKBase();
			ListOfTerm isbns = kb.queryRef(BOOKS_IRE);

			if ( isbns != null ) {
				for (int i=0; i<isbns.size(); i++) {
					TermSequence s = (TermSequence)isbns.get(i);
					Constant isbn = (Constant)s.getTerm(0);
					Constant delay = (Constant)s.getTerm(1);
					Constant price = (Constant)s.getTerm(2);
					Constant min_price = (Constant)s.getTerm(3);
					if ( delay.intValue().intValue() <= 0 ) {
						kb.assertFormula(Ontology.NOT_FOR_SALE_PREDICATE
								.instantiate("isbn", isbn)
								.instantiate("agent", getSemanticCapabilities().getAgentName()));
					}
					else {
						// Apply the PRICE_REDUCE
						double reduce = ((price.realValue().doubleValue() - min_price.realValue().doubleValue())
							* period) / delay.intValue().longValue();
						double newprice = Math.rint((price.realValue().doubleValue() - reduce)*100)/100;
						Double nP = new Double(newprice);
						Formula pF = Ontology.SELLING_PRICE_PREDICATE.instantiate("isbn", isbn);
						if ( nP.doubleValue() > min_price.realValue().doubleValue() ) {
							kb.assertFormula(pF.instantiate("price", SL.real(nP))
									           .instantiate("agent", getSemanticCapabilities().getAgentName()));
						}
						else {
							kb.assertFormula(pF.instantiate("price", min_price)
		   						               .instantiate("agent", getSemanticCapabilities().getAgentName()));								
						}
						
						// Reduce the delai of period
						Long nD = new Long(delay.intValue().longValue() - period);
						Formula dF = Ontology.SELLING_DELAY_PREDICATE.instantiate("isbn", isbn);
						kb.assertFormula(dF.instantiate("delay", SL.integer(nD)));
					}
				}
			}
			myGui.refreshBookList();
		}
	}
	
	//---------------------------------------------------------------
	//                      FIELDS 
	//---------------------------------------------------------------
	IBookSellerAgentGui myGui;
	
	Term isbnHolder = null;
	
	//---------------------------------------------------------------
	//                      METHODS 
	//---------------------------------------------------------------
	@Override
	public void setup() {
		super.setup();
		
		myGui = new BookSellerAgentGui((BookSellerCapabilities)getSemanticCapabilities());

		isbnHolder = Tools.AID2Term(new AID((String)getArguments()[1], AID.ISLOCALNAME));
			
		try {
			getSemanticCapabilities().interpret(EQUALS_ALL_FOR_SALE.instantiate("agent", getSemanticCapabilities().getAgentName()));
			getSemanticCapabilities().interpret(new FileReader(getArguments()[0].toString()));			
			myGui.refreshBookList();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
				
		addBehaviour(new UpdateBooksBehaviour(this, 2000));
	}

	//---------------------------------------------------------------
	//                      CONSTRUCTOR 
	//---------------------------------------------------------------
	public BookSellerAgent() {
		setSemanticCapabilities(new BookSellerCapabilities());
	}

}
