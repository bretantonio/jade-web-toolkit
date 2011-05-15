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
 * ActionPerformance.java
 * Created on 4 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.PlanningSIPAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

/**
 * This semantic interpretation principle consists in adding to the Jade agent a Jade Behaviour
 * performing the targeted semantic action <i>act</i>.   
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class ActionPerformance extends PlanningSIPAdapter {
	
	Formula done_ACT_PHI = SL.formula("(done ??act ??phi)");
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities agent that owns of the semantic interpretation principle
     */
    public ActionPerformance(SemanticCapabilities capabilities) {
        super(capabilities, "(done ??__act ??__phi)");
    }
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * Adds a new intentional behaviour {@link IntentionalBehaviour}if it is applicable.
     * @param sr a semantic representation
     * @return if the pattern (I ??agent (done ??act ??phi))
     * matches with the incoming SR, and the current agent believes ??phi and the agent find 
     * an action ??act, this method returns an ArrayLIst with the same SR which
     * SIP index is increased by one. Returns null in other cases. 
     * @throws SemanticInterpretationPrincipleException if any exception occurs
     */
    @Override
	public ActionExpression doApply(MatchResult matchResult, SemanticRepresentation sr) {
    	ActionExpression actExp = (ActionExpression)matchResult.term("__act");
    	Formula phi = matchResult.formula("__phi");
    	
    	if (actExp != null && myCapabilities.getAgentName().equals(actExp.getFirstStep().getActor())
    			&& (phi == null || myCapabilities.getMyKBase().query(phi) != null)) {
    		return actExp;
    	}
        return null;
    }
}
