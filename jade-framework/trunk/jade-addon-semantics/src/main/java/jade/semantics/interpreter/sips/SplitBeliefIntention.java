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
 * BeliefTransfer.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * When an external agent intends the semantic agent to adopt a non-atomic
 * belief (i.e. a beliefs expressed as a conjunction), this semantic interpretation
 * principles splits this intention into several more elementary intentions
 * about each atomic belief (i.e. each conjunct of the initial belief to adopt).
 *  
 * @author Vincent Louis - France Telecom
 * @version Date: 2006/07/31 Revision: 1.0 
 */
public class SplitBeliefIntention extends SemanticInterpretationPrinciple {
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public SplitBeliefIntention(SemanticCapabilities capabilities) {
        super(capabilities,
        	  "(B ??myself (I ??sender ??goal))",
        	  SemanticInterpretationPrincipleTable.SPLIT_FORMULA);
    }
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    /**
     * Creates a list of new semantic representations if it is applicable.
     * @inheritDoc
     */
    @Override
	public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
    	MatchResult matchResult = SL.match(pattern,sr.getSLRepresentation());
    	if (matchResult != null) {
    		Formula goal = matchResult.formula("goal");
    		Formula formulaToBelieve = goal.isBeliefFrom(myCapabilities.getAgentName());
    		if (formulaToBelieve != null) {
    			return doApply(formulaToBelieve, matchResult.term("sender"), sr);
    		}
    	} 
        return null;
    }
    
    public ArrayList doApply(Formula formulaToBelieve, Term sender, SemanticRepresentation inputSR)
    throws SemanticInterpretationPrincipleException
    {
		if (formulaToBelieve instanceof AndNode) {
			ArrayList listOfSR = new ArrayList();
			ListOfFormula factsToBelieve = ((AndNode)formulaToBelieve).getLeaves();
			for (int i=0; i<factsToBelieve.size(); i++) {
				listOfSR.add(new SemanticRepresentation(
						(Formula)SL.instantiate(pattern,
								"sender", sender,
								"goal", new BelieveNode(myCapabilities.getAgentName(), factsToBelieve.element(i))),
								inputSR));
			}
			return listOfSR;
		}
    	return null;
    }
}
