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
 * IntentionInterpretation.java
 * Created on 14 June 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.BelieveNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This SIP transforms the intention of another agent to do an action into the
 * intention of the same agent to reach the rational effect of this action.
 * If the intended action has no rational effect (e.g. for usual ontological
 * actions), this SIP is not applicable, and so does nothing.
 *  
 * @author Vincent Louis - France Telecom
 * @version Date: 2007/03/07
 * @since Version 1.4
 */
public class IntentionInterpretation extends SemanticInterpretationPrinciple {

    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public IntentionInterpretation(SemanticCapabilities capabilities) {
        super(capabilities,
        	  "(I ??__other (done ??__act))",
        	  SemanticInterpretationPrincipleTable.COOPERATION);
    }
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * {@inheritDoc}
     */
    @Override
	public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
    	MatchResult matchResult = SL.match(pattern,sr.getSLRepresentation());
    	if (matchResult != null) {
    		SemanticAction act = null;
    		try {
    			act = myCapabilities.getMySemanticActionTable().getSemanticActionInstance(
    					(ActionExpression)matchResult.term("__act"));
    		} catch (SemanticInterpretationException e) {
    		}
    		if (act != null) {
    			Formula rationalEffect = act.getRationalEffect();
    			if (rationalEffect != null && !(rationalEffect instanceof TrueNode)) {
    				SemanticRepresentation outputSR = new SemanticRepresentation(
    						new BelieveNode(myCapabilities.getAgentName(),
    								new IntentionNode(matchResult.term("__other"), rationalEffect)), sr);
    				outputSR.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+1);
    				ArrayList result = new ArrayList(1);
    				result.add(outputSR);
    				return result;
    			}
    		}
    	}
        return null;
    }   
}
