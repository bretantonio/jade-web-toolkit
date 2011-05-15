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
 * AlreadyReachedGoal.java
 * Created on 14 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter.sips;

import jade.core.behaviours.Behaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.util.leap.ArrayList;

/**
 * Traps the intentions of the semantic agent that he 
 * believes to be already reached, or that he already has.
 * 
 * @author Vincent Pautret - France Telecom
 * @author Vincent LOUIS - France Telecom
 * @since JSA 1.6 (formerly AlreadyReachedGoal class)
 * @version Date: 2005/06/14 Revision: 1.0
 * @version Date: 2008/04/02 Revision: 1.1 - CA : also prevents an agent from reinterpreting an intention that he has already adopted
 */
public class GoalCommitment extends SemanticInterpretationPrinciple {

    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Constructor of the principle
     * @param capabilities capabilities of the owner (the agent) of this
     * semantic interpretation principle
     */
    public GoalCommitment(SemanticCapabilities capabilities) {
        super(capabilities,
        	  "(I ??myself ??phi)",
        	  SemanticInterpretationPrincipleTable.INTENTION_FILTERING);
    } // End of AlreadyReachedGoal/1
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/

    /**
     * @inheritDoc
     */
    @Override
	public ArrayList apply(SemanticRepresentation sr)
    throws SemanticInterpretationPrincipleException {
        try {
            MatchResult matchResult = pattern.match(sr.getSLRepresentation());
            if (matchResult != null) {
            	ArrayList result = new ArrayList();

            	// if the goal is already reached...
                if ((myCapabilities.getMyKBase().query(matchResult.getFormula("phi"))) != null) {
                	// interpret the intention annotated by ACHIEVE_ON_REACHED_KEY, if any
                	Formula formula = (Formula)sr.getAnnotation(SemanticRepresentation.INTERPRET_ON_REACHED_GOAL_KEY);
                	if (formula != null) {
                		SemanticRepresentation newSR = new SemanticRepresentation(formula);
                		newSR.setMessage(sr.getMessage());
                		result.add(newSR);
                	}
                	// wake up the waiting behaviour, if any
                	SemanticBehaviour waitingBehaviour = (SemanticBehaviour)sr.getAnnotation(SemanticRepresentation.WAITING_BEHAVIOUR_KEY);
                	if (waitingBehaviour != null) {
                		waitingBehaviour.setState(SemanticBehaviour.SUCCESS);
                		myCapabilities.getAgent().addBehaviour((Behaviour)waitingBehaviour);
                	}
                }
                // MODIF ! special case added by CA - 2 April 2008
                // or if the agent already has this intention
                else if (myCapabilities.getMyKBase().query(sr.getSLRepresentation()) != null) {
                	// absorb it
                	return new ArrayList();
                }
                // END MODIF
                // otherwise, if the goal is not yet reached...
                else {
                	// prepare the committed intention...
                	sr.setSemanticInterpretationPrincipleIndex(sr.getSemanticInterpretationPrincipleIndex()+1);
                	sr.putAnnotation(SemanticRepresentation.GOAL_KEY, sr.getSLRepresentation());
                	Formula formula = (Formula)sr.getAnnotation(SemanticRepresentation.INTERPRET_BEFORE_GOAL_KEY);
                	if (formula != null) {
                    	// if any, interpret the intention annotated by ACHIEVE_BEFORE_KEY (generally used for feed-backs)
                		SemanticRepresentation newSR = new SemanticRepresentation(formula);
                		newSR.setMessage(sr.getMessage());
                		// and tag it with the committed intention, to interpret just after the goal
                		newSR.addAnnotation(SemanticRepresentation.INTERPRET_ON_PLAN_EXCEPTION_KEY, sr);
                		newSR.addAnnotation(SemanticRepresentation.INTERPRET_ON_PLAN_FAILURE_KEY, sr);
                		newSR.addAnnotation(SemanticRepresentation.INTERPRET_ON_PLAN_SUCCESS_KEY, sr);
                		result.add(newSR);
                		// assert the committed intention
                		potentiallyAssertFormula(sr.getSLRepresentation());
                	}
                	else {
                    	// commit the corresponding goal, to be processed by further planning SIPs
                    	result.add(sr);
                    }
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SemanticInterpretationPrincipleException();
        }
        return null;
    } // End of apply/1

} // End of class AlreadyReachedGoal
