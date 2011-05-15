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
 * SplitAnd.java
 * Created on 14 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.AndNode;
import jade.semantics.lang.sl.grammar.ListOfFormula;
import jade.util.leap.ArrayList;

/**
 * This principle is intended to be applied to an AND formula.  
 * It produces two Semantic Representations:
 * <ul>
 * <li> the left part of the conjonction;
 * <li> the right part of the conjonction.
 * </ul>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/28 Revision: 1.0 
 */
public class SplitAnd extends SemanticInterpretationPrinciple {
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public SplitAnd(SemanticCapabilities capabilities) {
        super(capabilities,
        	  "(and ??phi ??psi)", // unused in the current implementation (Node classes used instead)
        	  SemanticInterpretationPrincipleTable.SPLIT_FORMULA);
    }
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * Splits the SR into two SR if the SL representation of the SR is an AND formula.
     * The first new SR is the left part of the conjunction, and the second one 
     * the right part of the conjunction.
     * @inheritDoc
     */
    @Override
	public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
    	
    	if (sr.getSLRepresentation() instanceof AndNode) {
    		ListOfFormula andLeaves = ((AndNode)sr.getSLRepresentation()).getLeaves();
            ArrayList listOfSR = new ArrayList();
            for (int i=0; i<andLeaves.size(); i++) {
            	listOfSR.add(new SemanticRepresentation(andLeaves.element(i), sr));
            }
            return listOfSR;
    	}
        return null;
    }
}
