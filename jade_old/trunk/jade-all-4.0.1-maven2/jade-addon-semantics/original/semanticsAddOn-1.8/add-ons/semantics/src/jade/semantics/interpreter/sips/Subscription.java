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
 * Subscription.java
 * Created on 24 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.kbase.observers.EventCreationObserver;
import jade.semantics.kbase.observers.Observer;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;
import jade.util.leap.ArrayList;

/**
 * Used to gather the principles which deal with the same domain.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/06/24 Revision: 1.0
 */
public abstract class Subscription extends SemanticInterpretationPrinciple {
    
    /**
     * Pattern used to build the formula to monitor for the requested subscription
     */
    private Formula observedPattern;
    
    /**
     * Pattern used to buid the internal event to trigger for the requested subscription 
     */
    private Formula eventPattern;
    
    /**
     * True if the subscription should be done only one time (requestWhen)
     */
    private boolean isOneShot;
    
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    
    
    /**
     * Creates a new principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     * @param subscribe
     * @param isOneShot only one time or not
     */
    public Subscription(SemanticCapabilities capabilities, String subscribe, boolean isOneShot) {
        super(capabilities,
        	  "(B ??myself " + subscribe + ")",
        	  SemanticInterpretationPrincipleTable.SUBSCRIBE);

        observedPattern = SL.formula("(B "+ myCapabilities.getAgentName() +" ??property)");
        eventPattern = SL.formula("(B "+ myCapabilities.getAgentName() +" (I ??subscriber ??goal))");
        this.isOneShot = isOneShot;
    } 
    
    
    /**
     * Creates a new principle
     * @param capabilities capabilities of the owner (the agent) of this 
     * semantic interpretation principle
     * @param subscribe
     * @param isOneShot only one time or not
     * @param classindex the index of the sip within the sip table
     */
    public Subscription(SemanticCapabilities capabilities, String subscribe, boolean isOneShot, int classindex) {
        super(capabilities,
        	  "(B ??myself " + subscribe + ")",
        	  classindex);

        observedPattern = SL.formula("(B "+ myCapabilities.getAgentName() +" ??property)");
        eventPattern = SL.formula("(B "+ myCapabilities.getAgentName() +" (I ??subscriber ??goal))");
        this.isOneShot = isOneShot;
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

    	// FIXME
     	MatchResult applyResult = SL.match(pattern, sr.getSLRepresentation());
  
//     	if ( this instanceof RequestWhenever && myCapabilities.getAgent().getName().equals("display@test") ) 
//     		System.err.println("!!!!!!!!!!!!!!"+pattern+" -------- "+sr.getSLRepresentation()+" ------------> "+applyResult);
    	
    	if (applyResult != null) {
     		try {
    			Term subscriber = applyResult.getTerm("subscriber");
    			if (!subscriber.equals(myCapabilities.getAgentName())) {
    				Formula observedFormula = (Formula)SL.instantiate(
    						observedPattern,
    						"property", computePropertyToObserve(applyResult));
    				
    				Formula subscribedFormula = (Formula)SL.instantiate(
    						eventPattern,
    						"subscriber", subscriber,
    						"goal", computeEventToExecute(applyResult));
    				
					SemanticRepresentation subscribedEvent = new SemanticRepresentation(subscribedFormula, sr); 
    				Observer obs = new EventCreationObserver(myCapabilities.getMyKBase(), observedFormula, subscribedEvent, myCapabilities.getSemanticInterpreterBehaviour(), isOneShot);
    				myCapabilities.getMyKBase().addObserver(obs);
					obs.update(null); 
    				myCapabilities.getMyStandardCustomization().notifySubscribe(subscriber, observedFormula, applyResult.getFormula("goal"));
    				return new ArrayList();
    			}
    		} catch (SL.WrongTypeException e) {
    			e.printStackTrace();
    			throw new SemanticInterpretationPrincipleException();
    		}
    	}
    	return null;    
    }
    
    /**
     * Builds the observed property with the elements of the matching result.
     * @param applyResult matching result 
     * @return a formula representing the property to observe
     * @throws WrongTypeException if it raised during the SL formulae analyzis
     */
    abstract protected Formula computePropertyToObserve(MatchResult applyResult) throws WrongTypeException;
    
    /**
     * Builds the event to execute with the elements of the matching result.
     * @param applyResult matching result
     * @return a formula representing the event to execute
     * @throws WrongTypeException if it raised during the SL formulae analyzis
     */
    abstract protected Formula computeEventToExecute(MatchResult applyResult)throws WrongTypeException;
    
}
