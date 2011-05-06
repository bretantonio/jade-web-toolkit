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
 * ActionFeatures.java
 * Created on 4 nov. 2004
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
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
 * Class that represents the Action Features Semantic Interpretation Principle.
 * This principle is intended to be applied to the initial formula representing the
 * fact that the Jade agent has perceived an incoming ACL message.  
 * It produces four Semantic Representations :
 * <ul>
 * <li> one for checking the consistency.
 * <li> one stating that the Jade agent believes the persistent feasibility 
 * precondition of the received message is satisfied.
 * <li> one stating the Jade agent believes the intentional effect of the 
 * received message.
 * <li> the last is the postcondition of an action, the performance of which has just 
 * been observed by the Jade agent.
 * </ul>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class ActionFeatures extends SemanticInterpretationPrinciple {
    
    /**
     * Pattern used to build the feasibility precondition 
     */
    private Formula feasibilityPreconditonPattern; 
    
    /**
     * Pattern used to build the persistent precondition 
     */
    private Formula persistentPreconditionPattern;
    
    /**
     * Pattern used to build the intentional effect 
     */
    private Formula intentionalEffectPattern;
    
    /**
     * Pattern used to build the postcondition 
     */
    private Formula postConditionPattern;
    
    /**
     * Flag used to generate the full feasibility precondition
     */
    private boolean activeFP;
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Constructor of the principle
     * @param capabilities the capabilities of a semantic agent
     */
    public ActionFeatures(SemanticCapabilities capabilities) {
    	this(capabilities, true);
    }
    
    /**
     * Constructor of the principle
     * @param capabilities the capabilities of a semantic agent
     * @param activeFP true if the FP must be handled
     */    
    public ActionFeatures(SemanticCapabilities capabilities, boolean activeFP) {
        super(capabilities,
        	  "(B ??myself (done ??act true))",
        	  SemanticInterpretationPrincipleTable.ACTION_FEATURE);
        this.activeFP = activeFP;
        feasibilityPreconditonPattern = SL.formula("(B " + myCapabilities.getAgentName() + " (done ??act ??fp))");
        persistentPreconditionPattern = SL.formula("(B " + myCapabilities.getAgentName() + " ??p)");
        intentionalEffectPattern = SL.formula("(B " + myCapabilities.getAgentName() + " (I ??sender ??re))");
        postConditionPattern = SL.formula("(B " + myCapabilities.getAgentName() + " ??pc)");
    }
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * 
     * Produces four semantic representations : the feasibility precondition, 
     * the persistent feasibility precondition, the rational effect and the
     * postcondition.
     * @inheritDoc 
     */
    @Override
	public ArrayList apply(SemanticRepresentation sr) throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = SL.match(pattern,sr.getSLRepresentation());
            if (matchResult != null) {
                ActionExpression action = (ActionExpression)matchResult.getTerm("act");
                SemanticAction act = myCapabilities.getMySemanticActionTable().getSemanticActionInstance(action);
                if (act != null) {
                	ArrayList listOfSR = new ArrayList();
                    
                    // Persistent Precondition 
                	if (!act.getActor().equals(myCapabilities.getAgentName()) && activeFP) { //*******************
                		listOfSR.add(new SemanticRepresentation(
                				((Formula)SL.instantiate(persistentPreconditionPattern,
                						"p", act.getPersistentFeasibilityPrecondition())),
                				sr));
                	} //*********************
                	
                    // Intentional Effect 
                	if (!act.getActor().equals(myCapabilities.getAgentName()) && !(act.getRationalEffect() instanceof TrueNode)) {
                		listOfSR.add(new SemanticRepresentation(
                				((Formula)SL.instantiate(intentionalEffectPattern,
                						"sender", ((ActionExpressionNode)action).as_agent(),
                						"re", act.getRationalEffect())),
                						sr));
                	}
                    // Postconditions 
                	if (activeFP && !(act.getPostCondition() instanceof TrueNode)) { //******************
                		listOfSR.add(new SemanticRepresentation(
                				((Formula)SL.instantiate(postConditionPattern, 
                						"pc", act.getPostCondition())),
                				sr));
                	} //***********************
                	
                    // Feasibility Precondition 
                	if (activeFP && !(act.getFeasibilityPrecondition() instanceof TrueNode)) {
                        listOfSR.add(new SemanticRepresentation(
                        		((Formula)SL.instantiate(feasibilityPreconditonPattern,
                        				"act", action,
                        				"fp", act.getFeasibilityPrecondition())),
                        		sr));
                	}
                	else {
                		listOfSR.add(new SemanticRepresentation(sr, sr.getSemanticInterpretationPrincipleIndex() + 1));
                	}

                	//action = null;
                    return listOfSR;
                }
            } 
        } catch (SemanticInterpretationException sie) {
//            ArrayList listOfSR = new ArrayList();
//            listOfSR.add(myCapabilities.getSemanticInterpreterBehaviour().createNotUnderstandableSR(sie.getReason(), sie.getObject(), sr.getMessage()));
//            return listOfSR;
        	return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1
    
} // End of class ActionFeatures
