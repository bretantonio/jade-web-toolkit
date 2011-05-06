/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop 
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2004 France Télécom
 JSA - JADE Semantics Add-on is a framework to develop cognitive
 agents in compliance with the FIPA-ACL formal specifications.

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
 * RequestWhen.java
 * Created on 24 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * This principle is intended to be applied when an agent receives a RequestWhen 
 * message.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/24 Revision: 1.0
 */
public class RequestWhen extends Subscription {
    
    /**
     * Pattern used to create the event to execute
     */
    private Formula eventPattern;
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     * @param subscribeProperty precondition to perform the action
     */
    public RequestWhen(SemanticCapabilities capabilities, String subscribeProperty) {
    	// FIXME : the class index has been added as an argument of the super constructor 
    	// to set the good rank for this sip (TM TM TM, 19/09/07)
        super(capabilities, "(I ??subscriber " +
                "(done ??act (and (B ??myself " + subscribeProperty + ") " +
                "         (exists ??e (done ??e (not (B ??myself " + subscribeProperty + ")))) )))", true,
                SemanticInterpretationPrincipleTable.REQUEST_WHEN);
        eventPattern = SL.formula("(done ??act)");
    } 
    
    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     */
    public RequestWhen(SemanticCapabilities capabilities) {
        this(capabilities, "??condition");
    } 
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    /**
     * @inheritDoc
     */
    @Override
	protected Formula computePropertyToObserve(MatchResult applyResult) throws WrongTypeException {
        return applyResult.getFormula("condition"); 
    } 
    
    /**
     * @inheritDoc 
     */
    @Override
	protected Formula computeEventToExecute(MatchResult applyResult)throws WrongTypeException {
        return (Formula)SL.instantiate(eventPattern, "act", applyResult.getTerm("act"));
    }
} 
