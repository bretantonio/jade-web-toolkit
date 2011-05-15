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
 * SemanticActionTable.java
 * Created on 18 mars 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions;

import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.util.leap.ArrayList;

/**
 * Interface of the Semantic Action Table. A semantic action table gathers all 
 * the action known by a semantic agent.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/03/18 Revision: 1.0
 */
public interface SemanticActionTable {
    
	/**
	 * @param protoClass the class of the prototype to return
	 * @return the SemanticAction prototype the class of which is the protoClass.
	 */
	
	public SemanticAction getSemanticActionPrototype(Class protoClass);
	
    /**
     * Returns the semantic capabilities that hold the action table.
     * @return the semantic capabilities that hold the action table 
     */
    public SemanticCapabilities getSemanticCapabilities();
    
    /**
     * Adds a semantic action in the table
     * @param semanticAction a semantic action
     */
    public void addSemanticAction(SemanticAction semanticAction);
    
    /**
     * Removes the semantic action identified by the finder
     * @param finder a finder
     */
    public void removeSemanticAction(Finder finder);
    
    /**
     * Creates an instantiated <code>SemanticAction</code> from the <code>SemanticAction</code> prototype
     * within the table corresponding to an <code>ActionExpression</code>
     * @param actionExpression the <code>ActionExpression</code> representing the <code>SemanticAction</code> to create
     * @throws SemanticInterpretationException if any exception occurs
     * @return an instantiated <code>SemanticAction</code> that implements action
     */
    public SemanticAction getSemanticActionInstance(ActionExpression actionExpression) throws SemanticInterpretationException;
    
    /**
     * Creates an instantiated <code>SemanticAction</code> from the <code>SemanticAction</code> prototype
     * within the table corresponding to an ACL Message
     * @param aclMessage an ACL Message
     * @return an instance of semantic action
     * @throws SemanticInterpretationException if any exception occurs
     */
    public SemanticAction getSemanticActionInstance(ACLMessage aclMessage) throws SemanticInterpretationException;
    
    /**
     * Creates a list of instantiated semantic actions from the <code>SemanticAction</code> prototypes
     * within the table, such that each <code>SemanticAction</code> has a specified rational effect
     * @param actionList the list of actions to complete with the created semanticActions
     * @param rationalEffect the rational effect of the semanticActions to create
     * @param inReplyTo the message to reply to
     */
    public void getSemanticActionInstances(ArrayList actionList, Formula rationalEffect, ACLMessage inReplyTo);
        
    /**
     * Returns the size of the table.
     * @return the size of the table
     */
    public int size();
} // End of interface SemanticActionTable
