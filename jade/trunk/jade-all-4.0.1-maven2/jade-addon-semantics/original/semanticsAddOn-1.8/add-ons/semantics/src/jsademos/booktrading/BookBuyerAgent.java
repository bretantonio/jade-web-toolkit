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
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ActionDoneSIPAdapter;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

public class BookBuyerAgent extends SemanticAgent {
	
	//---------------------------------------------------------------
	//                      CONSTANTS 
	//---------------------------------------------------------------
	static final IdentifyingExpression FOR_SALE_IRE = (IdentifyingExpression)
		SL.term("(all ?i (for_sale ?i ??agent))");

	static final Formula EQUALS_FOR_SALE_IRE = 
		SL.formula("(= (all ?i (for_sale ?i ??agent)) ??set)");

	static final IdentifyingExpression TITLE_IRE = (IdentifyingExpression)
		SL.term("(any ?t (title ??isbn ?t))");

	static final IdentifyingExpression SELLERS_IRE = (IdentifyingExpression)
		SL.term("(some ?a (for_sale ??isbn ?a))");

	static final IdentifyingExpression PURCHASE_IRE = (IdentifyingExpression)
		SL.term("(some ?x (selling_price ??isbn ?x ??agent))");
	
	boolean isAcceptable(Constant isbn, Constant price) {
		return (this.isbn.equals(isbn)) && 
			   (buying_price.realValue().doubleValue() >= price.realValue().doubleValue());
	}
	
	//---------------------------------------------------------------
	//                      INNER CLASSES 
	//---------------------------------------------------------------
	class BookBuyerCapabilities extends DefaultCapabilities {

		class PriceProposalSIP extends ApplicationSpecificSIPAdapter {
			PriceProposalSIP() {
				super(BookBuyerCapabilities.this, "(selling_price ??isbn ??price ??seller)"); 
			}

			@Override
			protected ArrayList doApply(MatchResult applyResult,
					                    ArrayList result,
                                        SemanticRepresentation sr) {
				final Constant isbn = (Constant) applyResult.term("isbn");
				final Constant price = (Constant)applyResult.term("price");
				if (isAcceptable(isbn,price)) {
					result.add(new SemanticRepresentation(Ontology.I_DONE_SELL_ACTION
							.instantiate("actor", applyResult.term("seller"))
							.instantiate("isbn", isbn)
							.instantiate("price", price)
							.instantiate("buyer", getAgentName())));
					removeBehaviour(adjustPriceBehaviour);
					getMySemanticInterpretationTable().removeSemanticInterpretationPrinciple(this);
					for (int i=0; i<isbn_sellers.size(); i++) {
						Term ag = (Term)isbn_sellers.get(i);
						IdentifyingExpression pie = (IdentifyingExpression)PURCHASE_IRE
						        .instantiate("agent", ag)
								.instantiate("isbn", BookBuyerAgent.this.isbn);
						getSemanticCapabilities().unsubscribe(pie, ag);
					}
				}
				else {
					if ( best_price == null ||
						 best_price.realValue().doubleValue() > price.realValue().doubleValue() ) {
						best_price = price;
						best_seller = applyResult.term("seller");
					}
				}
				return result;
			}
		}
				
		@Override
		protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
			SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();
						
			// Book bought SIP
			// ---------------
			table.addSemanticInterpretationPrinciple(new ActionDoneSIPAdapter(this, Ontology.SELL_ACTION_EXPRESSION) {

				@Override
				protected ArrayList doApply(MatchResult applyResult, ArrayList result, SemanticRepresentation sr) {
					Constant isbn = (Constant) applyResult.term("isbn");
					Constant price = (Constant)applyResult.term("price");
					Term actor = applyResult.term("actor");
					myGui.addToBoughtBooksList(isbn, title, price, actor);
					return result;
			    }
			});

			// Book for sale SIPS
			// -----------------
			table.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(title ??isbn ??title)") {

				@Override
				protected ArrayList doApply(MatchResult applyResult,
						                    ArrayList result,
						                    SemanticRepresentation sr) {
					potentiallyAddBehaviour(new OneShotBehaviour() {
						@Override
						public void action() {myGui.refreshBooksForSaleList();}});
					return result;
			    }
			});
						
			table.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, EQUALS_FOR_SALE_IRE) {

				@Override
				protected ArrayList doApply(MatchResult applyResult,
						                    ArrayList result,
						                    SemanticRepresentation sr) {
					potentiallyAddBehaviour(new OneShotBehaviour() {@Override
					public void action() {myGui.refreshBooksForSaleList();}});
					return result;
				}});
					
			table.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, Ontology.FOR_SALE_PREDICATE) {

				@Override
				protected ArrayList doApply(final MatchResult applyResult,
						                    ArrayList result,
						                    SemanticRepresentation sr) {
					potentiallyAddBehaviour(new OneShotBehaviour() {
						@Override
						public void action() {
							queryRef((IdentifyingExpression)TITLE_IRE.instantiate("isbn", applyResult.term("isbn")), isbnHolder);
						}});
					return result;
			    }});

			return table;
		}
		
		void purchase(Constant isbn, Constant title, Constant price)
		{
			BookBuyerAgent.this.isbn = isbn;
			BookBuyerAgent.this.buying_price = price;
			BookBuyerAgent.this.title = title;
			addBehaviour(new PurchaseBehaviour(BookBuyerAgent.this, isbn, buying_price));
		}

	}
	
	class PurchaseBehaviour extends WakerBehaviour {
				
		public PurchaseBehaviour(Agent agent, Constant isbn, Constant buying_price) {			
			super(agent, 200);
		}
		
		@Override
		protected void onWake() {
			IdentifyingExpression sie = (IdentifyingExpression)SELLERS_IRE.instantiate("isbn", isbn);
			isbn_sellers = getSemanticCapabilities().getMyKBase().queryRef(sie);
			if ( isbn_sellers != null ) {
				for (int i=0; i<isbn_sellers.size(); i++) {
					Term ag = (Term)isbn_sellers.get(i);
					IdentifyingExpression pie = (IdentifyingExpression)PURCHASE_IRE
						.instantiate("agent", ag)
						.instantiate("isbn", BookBuyerAgent.this.isbn);
					getSemanticCapabilities().queryRef(pie, ag);
					getSemanticCapabilities().subscribe(pie, ag);
				}		
			}
			addBehaviour(adjustPriceBehaviour = new AdjustPriceBehaviour(myAgent, 4000));
			getSemanticCapabilities().getMySemanticInterpretationTable()
				.addSemanticInterpretationPrinciple(((BookBuyerCapabilities)getSemanticCapabilities()).new PriceProposalSIP());
		}
	}
	
	class AdjustPriceBehaviour extends TickerBehaviour {
		
		long period;
		
		public AdjustPriceBehaviour(Agent agent, long period) {
			super(agent, period);
			this.period = period;
		}
		
		@Override
		protected void onTick() {
			long i = Math.round(buying_price.realValue().doubleValue() * 0.1); 
			buying_price = SL.real(buying_price.realValue().doubleValue()+i);
			myGui.revisePrice(buying_price.realValue());
			if ( best_price != null && 
			     best_price.realValue().doubleValue() <= buying_price.realValue().doubleValue() ) {
				 getSemanticCapabilities().queryRef((IdentifyingExpression)
						 PURCHASE_IRE.instantiate("isbn", isbn)
						             .instantiate("agent", best_seller),
						 best_seller);
			}
		}	
	}
	
	//---------------------------------------------------------------
	//                      FIELDS 
	//---------------------------------------------------------------
	IBookBuyerAgentGui myGui;
	
	Term isbnHolder = null;

	Constant   isbn = null;
	Constant   buying_price = null;
	Constant   title = null;
	Term[]     sellers = null;
	Term       best_seller = null;
	Constant   best_price = null;
	ListOfTerm isbn_sellers = null;
	Behaviour  adjustPriceBehaviour = null;
		
	//---------------------------------------------------------------
	//                      METHODS 
	//---------------------------------------------------------------
	@Override
	public void setup() {
		super.setup();
		
		myGui = new BookBuyerAgentGui((BookBuyerCapabilities)getSemanticCapabilities());
		
		// Taking arguments into account
		isbnHolder = Tools.AID2Term(new AID((String)getArguments()[0], AID.ISLOCALNAME));
		sellers = new Term[getArguments().length-1];
		for (int i=0; i<sellers.length; i++) {
			sellers[i] = Tools.AID2Term(new AID((String)getArguments()[i+1], AID.ISLOCALNAME));
		}

		// Adding a behaviour to query and susbcribe for the books to sale
		addBehaviour(new WakerBehaviour(this, 1000) {
			@Override
			protected void onWake() {
				for (int i=0; i<sellers.length; i++) {
					IdentifyingExpression ie = (IdentifyingExpression)FOR_SALE_IRE
													.instantiate("agent", sellers[i]);
					getSemanticCapabilities().queryRef(ie, sellers[i]);
					getSemanticCapabilities().subscribe(ie, sellers[i]);
				}
			}
		});
	}
	
	//---------------------------------------------------------------
	//                      CONSTRUCTOR 
	//---------------------------------------------------------------
	public BookBuyerAgent() {
		setSemanticCapabilities(new BookBuyerCapabilities());
	}
}
