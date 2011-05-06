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
 * UnreachableGoal.java
 * Created on 20 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.IntentionTransferSIPAdapter;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * This principle is intended to be applied to all intentions that have not been 
 * realized. These intentions are considered as not feasible.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/20 Revision: 1.0
 */
public class UnreachableGoal extends SemanticInterpretationPrinciple {
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public UnreachableGoal(SemanticCapabilities capabilities) {
        super(capabilities,
        	  "(I ??myself ??phi)",
        	  SemanticInterpretationPrincipleTable.UNREACHABLE_GOAL);
    }
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    @Override
	public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
        MatchResult matchResult = SL.match(pattern,sr.getSLRepresentation());
        if (matchResult != null) {
            ArrayList result = new ArrayList();
            result.add(new SemanticRepresentation(new NotNode(sr.getSLRepresentation()), sr));
        	Formula newSRformula = (Formula)sr.getAnnotation(SemanticRepresentation.INTERPRET_ON_UNREACHED_GOAL_KEY);
            if (newSRformula != null) {
            	ArrayList reasons = (ArrayList)sr.getAnnotation(SemanticRepresentation.REASON_WHY_UNREACHED_KEY);
            	if (reasons != null && !reasons.isEmpty()) {
            		newSRformula = newSRformula.instantiate(IntentionTransferSIPAdapter.REASON_METAREF, (Node)reasons.get(0));
            	}
            	else {
            		newSRformula = newSRformula.instantiate(IntentionTransferSIPAdapter.REASON_METAREF, SL.TRUE);
            	}
            	SemanticRepresentation newSR = new SemanticRepresentation(newSRformula);
            	newSR.setMessage(sr.getMessage());
                result.add(newSR);
            }
            return result;
        }
        return null;
    } // End of apply/1
    
} // End of class Failure
