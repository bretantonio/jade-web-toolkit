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
 * StandardCustomizationAdapter.java
 * Created on 17 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;


import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;

/**
 * Implementation of the StandardCustomization interface.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/17 Revision: 1.0 
 */
public class StandardCustomizationAdapter implements StandardCustomization {

//    /**
//     * @inheritDoc
//     * @return By default, returns true.
//     */
//    public boolean acceptBeliefTransfer(Formula formula, Term agent) {
//        return true;
//    } // End of acceptBeliefTransfer/2
//    
//    /**
//     * @inheritDoc
//     * @return By default, returns true.
//     */
//    public boolean acceptIntentionTransfer(Formula formula, Term agent) {
//        return true;
//    } // End of acceptIntentionTransfer/2
//
//    /**
//     * @inheritDoc
//     * @return By default, returns null.
//     */
//    public ListOfTerm handleCFPIota(Variable variable, Formula formula,
//            ActionExpression action, Term agent) {
//        return null;
//    } // End of handleCFPIota/4
//
//    /**
//     * @inheritDoc
//     * @return By default, returns null.
//     */
//    public ListOfTerm handleCFPAny(Variable variable, Formula formula,
//            ActionExpression action, Term agent) {
//        return null;
//    } // End of handleCFPAny/4
//
//    /**
//     * @inheritDoc
//     * @return By default, returns null.
//     */
//    public ListOfTerm handleCFPAll(Variable variable, Formula formula,
//            ActionExpression action, Term agent) {
//        return null;
//    } // End of handleCFPAll/4
//
//    /**
//     * @inheritDoc
//     * @return By default, returns null.
//     */
//    public ListOfTerm handleCFPSome(Variable variable, Formula formula,
//            ActionExpression action, Term agent) {
//        return null;
//    } // End of handleCFPSome/4
    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean handleRefuse(Term agent, ActionExpression action,
            Formula formula) {
        return false;
    } // End of trapCancelAction/3

    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean handleRejectProposal(Term agentI, ActionExpression action,
            Formula formula) {
        return false;
    } // End of trapCancelMyAction/3

    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean handleAgree(Term agent, ActionExpression action,
            Formula formula) {
        return false;
    } // End of trapDoAction/3

    /**
     * @inheritDoc
     * @return By default, returns true.
     */
    public boolean handleProposal(Term agentI, ActionExpression action,
            Formula formula) {
        return false;
    } // End of trapProposal/3
	
	/**
     * By default, does nothing
     * @inheritDoc 
	 * 
	 */
	public void notifySubscribe(Term subscriber, Formula obsverved, Formula goal)
	{
	}
	
	/**
     * By default, does nothing
     * @inheritDoc 
	 * 
	 */
	public void notifyUnsubscribe(Term subscriber, Formula obsverved, Formula goal)
	{
	}


} // End of class StandardCustomizationAdapter
