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

import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

public class Ontology {

	public static final Formula TITLE_PREDICATE 
		= SL.formula("(title ??isbn ??title)");
	public static final Formula SELLING_PRICE_PREDICATE 
		= SL.formula("(selling_price ??isbn ??price ??agent)");
	public static final Formula MIN_SELLING_PRICE_PREDICATE 
		= SL.formula("(min_selling_price ??isbn ??min_price ??agent)");
	public static final Formula SELLING_DELAY_PREDICATE 
		= SL.formula("(selling_delay ??isbn ??delay)");
	public static final Formula FOR_SALE_PREDICATE 
		= SL.formula("(for_sale ??isbn ??agent)");	
	public static final Formula NOT_FOR_SALE_PREDICATE 
		= new NotNode(FOR_SALE_PREDICATE);	
			
	public static final Term SELL_ACTION_TERM 
		= SL.term("(SELL_BOOK :buyer ??buyer :isbn ??isbn :price ??price)");
	
	public static final ActionExpression SELL_ACTION_EXPRESSION
		= new ActionExpressionNode(new MetaTermReferenceNode("actor"), SELL_ACTION_TERM);
	
	public static final Formula I_DONE_SELL_ACTION
	    = SL.formula("(I ??buyer (done "+SELL_ACTION_EXPRESSION+"))");
}
