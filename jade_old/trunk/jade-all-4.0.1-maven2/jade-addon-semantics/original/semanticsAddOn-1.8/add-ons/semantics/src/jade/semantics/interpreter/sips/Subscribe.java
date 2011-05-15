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
 * Subscribe.java
 * Created on 14 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * This principle is intended to be applied when an agent receives a Subscribe 
 * message.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/14 Revision: 1.0
 */
public class Subscribe extends RequestWhenever {
    
    /**
     * Pattern of Formula to create the Formula to observe to deal with the required Subscribe
     */
    private Formula observedPattern = SL.formula("(= ??ire ??y)");
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    /**
     * Creates a new principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public Subscribe(SemanticCapabilities capabilities) {
        super(capabilities, "(forall ??y (not (B ??myself (= ??ire ??y))))");
    } // End of Subscribe/1
    
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    @Override
	protected Formula computePropertyToObserve(MatchResult applyResult) throws WrongTypeException {
        return (Formula)SL.instantiate(observedPattern, "ire", applyResult.getTerm("ire"));
    } // End of computePropertyToObserve/1
        
} // End of class Subscribe
