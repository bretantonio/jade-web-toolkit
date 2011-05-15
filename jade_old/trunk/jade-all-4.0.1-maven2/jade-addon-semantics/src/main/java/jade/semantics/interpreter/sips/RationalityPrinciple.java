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
 * RationalityPrinciple.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.operators.Alternative;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.PlanningSIPAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.util.leap.ArrayList;

/**
 * This semantic interpretation principle enables the Jade agent to compute some trivial planning 
 * according to the rationality principle. It looks for all the semantic actions
 * available to the Jade agent, the rational effect of which matches the 
 * intention <i>phi</i> of the Jade agent. Then it builds (and adds to the agent)
 * a Jade Behaviour implementing one of these actions (which is represented by
 * an alternative action expression).
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class RationalityPrinciple extends PlanningSIPAdapter {
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public RationalityPrinciple(SemanticCapabilities capabilities) {
        super(capabilities, "??__phi");
    }
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Adds a new intentional behaviour ({@link IntentionalBehaviour}) which encapsulates
     * an alternative behaviour of all actions found from the formula ??phi.
     * @param sr a semantic representation
     * @return if the pattern (I ??agent ??phi)
     * matches with the incoming SR and the agent find 
     * at least one action ??act from ??phi, this method returns an ArrayList with the same SR which
     * SIP index is increased by one. Returns null in other cases. 
     * @throws SemanticInterpretationPrincipleException if any exception occurs
     */
    @Override
	public ActionExpression doApply(MatchResult matchResult, SemanticRepresentation sr) {
    	Formula phi = matchResult.formula("__phi");
    	ArrayList actionList = new ArrayList();
    	myCapabilities.getMySemanticActionTable().getSemanticActionInstances(actionList, phi, sr.getMessage());
    	if (actionList.size() > 0) {
    		SemanticAction alternative = new Alternative(myCapabilities).newAction(actionList);
    		if (alternative != null) {
    			try {
					return alternative.toActionExpression();
				} catch (SemanticInterpretationException e) {
					e.printStackTrace();
					return null;
				}
    		} 
    	}
    	return null;
    }
    
}
