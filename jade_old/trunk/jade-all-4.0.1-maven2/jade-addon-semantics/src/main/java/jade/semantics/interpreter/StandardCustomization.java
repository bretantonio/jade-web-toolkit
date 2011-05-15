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
 * StandardCustomization.java
 * Created on 16 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;


import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;

/**
 * Provides methods to customize the semantic interpretation principles. These
 * methods offer to the developer to trigger some specific code.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/16 Revision: 1.0 
 * @deprecated This interface will be no longer used in future versions.
 *             The provided methods are progressively replaced with appropriate
 *             SIP adapters, see the package
 *             {@link jade.semantics.interpreter.sips.adapters}.
 */
@Deprecated
public interface StandardCustomization {
    
//    /**
//     * Returns true if the agent accepts the belief transfer, false if not.
//     * @param formula the fact to believe
//     * @param agent the agent that intends the Jade agent to adopt this belief
//     * @return true if the fact to believe can be asserted in the belief base. 
//     * @deprecated Use the BeliefTransferSIPAdapter instead
//     * @see jade.semantics.sips.adapters.BeliefTransferSIPAdapter
//     */
//    public boolean acceptBeliefTransfer(Formula formula, Term agent);
//    
//    /**
//     * Returns true if the agent accepts the intention transfer, false if not.
//     * @param formula the fact to intend
//     * @param agent the agent that intends the Jade agent to adopt this intention.
//     * @return true if the fact can be asserted in the belief base. 
//     * @deprecated Use the IntentionTransferSIPAdapter instead
//     * @see jade.semantics.sips.adapters.IntentionTransferSIPAdapter
//     */
//    public boolean acceptIntentionTransfer(Formula formula, Term agent);
//    
//    /**
//     * Returns a list of element that corresponds to the answer to the query. One and only one
//     * solution is awaited.
//     * @param variable the variable used within the formula
//     * @param formula the condition of the CFP
//     * @param action the requested action to be performed 
//     * @param agent the agent who will perform the action if accepted
//     * @return the list of elements that contains the values to make the proposal
//     */
//    public ListOfTerm handleCFPIota(Variable variable, Formula formula, ActionExpression action, Term agent);
//    
//    /**
//     * Returns a list of element that corresponds to the answer to the query. One 
//     * solution is awaited.
//     * @param variable the variable used within the formula
//     * @param formula the condition of the CFP
//     * @param action the requested action to be performed 
//     * @param agent the agent who will perform the action if accepted
//     * @return the list of elements that contains the values to make the proposal
//     */
//    public ListOfTerm handleCFPAny(Variable variable, Formula formula, ActionExpression action, Term agent);
//    
//    /**
//     * Returns a list of element that corresponds to the answer to the query. All the
//     * solutions are awaited.
//     * @param variable the variable used within the formula
//     * @param formula the condition of the CFP
//     * @param action the requested action to be performed 
//     * @param agent the agent who will perform the action if accepted
//     * @return the list of elements that contains the values to make the proposal
//     */
//    public ListOfTerm handleCFPAll(Variable variable, Formula formula, ActionExpression action, Term agent);
//    
//    /**
//     * Returns a list of element that corresponds to the answer to the query. Some 
//     * solutions are awaited, i.e. all the solutions the agent believes
//     * @param variable the variable used within the formula
//     * @param formula the condition of the CFP
//     * @param action the requested action to be performed 
//     * @param agent the agent who will perform the action if accepted
//     * @return the list of elements that contains the values to make the proposal
//     */
//    public ListOfTerm handleCFPSome(Variable variable, Formula formula, ActionExpression action, Term agent);
    
    /**
     * Returns true if this method traps the specified formula when an agent <i>agent</i> 
     * is no longer committed to do an action <i>action</i> under the condition <i>formula</i>. 
     * @param agent a semantic agent
     * @param action the action
     * @param formula the condition
     * @return true if the formula is trapped, false if not.
     */
    public boolean handleRefuse(Term agent, ActionExpression action, Formula formula);
    
    /**
     * Returns true if the specified formula is trapped when an agent <i>agentI</i> 
     * is no longer interested to do an action <i>action</i> under the condition 
     * <i>formula</i>.
     * @param agentI a semantic agent
     * @param action the action
     * @param formula the condition
     * @return true if the formula is trapped, false if not.
     */
    public boolean handleRejectProposal(Term agentI, ActionExpression action, Formula formula);
    
    /**
     * Returns true if the specified formula is trapped when an agent <i>agent</i> 
     * is committed to do an action <i>action</i> under the condition <i>formula</i>.
     * @param agent a semantic agent
     * @param action the action
     * @param formula the condition
     * @return true if the formula is trapped, false if not.
     */
    public boolean handleAgree(Term agent, ActionExpression action, Formula formula);
    
    /**
     * Returns true if the specified formula is trapped when an agent <i>agentI</i> 
     * is making a proposal (of doing an action <i>action</i> under the 
     * condition <i>formula</i>) towards the Jade agent.
     * @param agentI a semantic agent
     * @param action the action
     * @param formula the condition
     * @return true if the formula is trapped, false if not.
     */
    public boolean handleProposal(Term agentI, ActionExpression action, Formula formula);
    
    /**
     * Notifies the agent that it has just receive a subscribe, requestWhen or
     * requestwhenever act from the subscriber on the formula "observed" 
     * with the goal "goal".
     * @param subscriber a term representing an agent
     * @param obsverved the observed formula
     * @param goal the goal 
     */
    public void notifySubscribe(Term subscriber, Formula obsverved, Formula goal);
    
    /**
     * Notifies the agent that it has just receive an unsubscribe from the subscriber
     * on the formula "observed" with the goal "goal".
     * @param subscriber a term representing an agent
     * @param obsverved the observed formula
     * @param goal the goal 
     */
    public void notifyUnsubscribe(Term subscriber, Formula obsverved, Formula goal);
} // End of interface StandardCustomization
