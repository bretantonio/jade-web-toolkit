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
 * SemanticActionTableImpl.java
 * Created on 2 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions;

import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.Finder;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;


/**
 * Class that implements the interface <code>SemanticActionTable</code>. This 
 * implementation extends <code>jade.leap.ArrayList</code>.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class SemanticActionTableImpl extends ArrayList implements SemanticActionTable {
	
    /**
     * The semantic capabilities that hold the action table. 
     */
    private SemanticCapabilities myCapabilities;
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new <code>SemanticActionTable</code>.
     * @param capabilities the semantic capabilities that hold the action table 
     */
    public SemanticActionTableImpl(SemanticCapabilities capabilities) {
    	this(capabilities, null);
    }
    
    /**
     * Creates a new <code>SemanticActionTable</code>.
     * @param capabilities the semantic capabilities that hold the action table 
     * @param loader a particular loader to add semantic actions within the table 
     */
    public SemanticActionTableImpl(SemanticCapabilities capabilities, SemanticActionLoader loader) {
        super();
        myCapabilities = capabilities;
        if ( loader != null ) {loader.load(this);}
    } 
    
    /**************************************************************************/
    /**									PUBLIC METHODS						 **/
    /**************************************************************************/
    
    /**
     * Returns the semantic capabilities that hold the action table
     * @return the semantic capabilities that hold the action table 
     */
    public SemanticCapabilities getSemanticCapabilities() {
        return myCapabilities;
    }
    
    /**
     * Adds a semanticAction into the table.
     * @param action the semanticAction to add
     */
    public void addSemanticAction(SemanticAction action) {
        add(action);
    } 
    
    /**
     * Removes some semantic actions from the table.
     * @param actionIdentifier the identifier that identifies the semantic actions to remove
     */
    public  void removeSemanticAction(Finder actionIdentifier) {
        for (int i = size()-1 ; i >= 0 ; i--) {
            if (actionIdentifier.identify(get(i))) {
                remove(i);
            }
        }
    } 
       
    /**
     * Creates an instantiated <code>SemanticAction</code> from the 
     * <code>SemanticAction</code> prototype
     * within the table corresponding to an <code>ActionExpression</code>
     * @param action the actionExpression representing the semanticAction to create
     * @throws SemanticInterpretationException
     * @return an instantiated semanticAction that implements action
     */
    public SemanticAction getSemanticActionInstance(ActionExpression action) throws SemanticInterpretationException {
    	SemanticAction result = action.sm_action();
    	if (result == null) {
        	Iterator actionIterator = this.iterator();
        	while(result == null && actionIterator.hasNext()) {
        		result = ((SemanticAction)actionIterator.next()).newAction(action);
        	}
        	if (result == null) {
        		System.err.println("getSemanticActionInstance on action="+action);
        		throw new SemanticInterpretationException("unknown-message", SL.string(action.toString()));
        	}
        }
        return result;
    } // End of newAction/1
    
    /**
     * Creates an instantiated <code>SemanticAction</code> from the <code>SemanticAction</code> prototype
     * within the table corresponding to an ACL Message
     * @param aclMessage an ACL message
     * @throws SemanticInterpretationException
     * @return an instantiated semanticAction that implements action
     */
    public SemanticAction getSemanticActionInstance(ACLMessage aclMessage) throws SemanticInterpretationException {
        SemanticAction result = null;
        Iterator actionIterator = this.iterator();
        while(result == null && actionIterator.hasNext()) {
            Object elem = actionIterator.next();
            if (elem instanceof CommunicativeAction) {
                result = ((CommunicativeAction)elem).newAction(aclMessage);
            }
        }
        if (result == null) {
            throw new SemanticInterpretationException("unknown-message", SL.string(aclMessage.toString()));
        }
        return result;
    } // End of newAction/1
    
    /**
     * Creates a list of instantiated semantic actions from the <code>SemanticAction</code> prototypes
     * within the table, such that each <code>SemanticAction</code> has a specified rational effect
     * @param actionList the list of actions to complete with the created semanticActions
     * @param rationalEffect the rational effect of the semantic actions to create
     * @param inReplyTo the message to reply to
     */
    public void getSemanticActionInstances(ArrayList actionList, Formula rationalEffect, ACLMessage inReplyTo) {
        Iterator actionIterator = this.iterator(); 
        while(actionIterator.hasNext()) {
            SemanticAction action = ((SemanticAction)actionIterator.next()).newAction(rationalEffect, inReplyTo);
            if (action != null && !actionList.contains(action)) {
                actionList.add(action);
            }
        }
    } // End of newAction/3
    
	
	public SemanticAction getSemanticActionPrototype(Class protoClass)
	{
		SemanticAction result = null;
        Iterator actionIterator = this.iterator(); 
        while(result == null && actionIterator.hasNext()) {
            SemanticAction proto = (SemanticAction)actionIterator.next();
            if (proto.getClass() == protoClass) {
                result = proto;
            }
        }
		return result;
	}
	
	/**
     * For debugging purpose only
     */
    public void viewData() {
        System.err.println(this.toString());
    } 
    
    /**
     * For debugging purpose only
     * @return the string that represents the actions of the table
     */
    @Override
	public String toString() {
        String result = ("------------------SEMANTIC ACTION TABLE CONTENT\n");
        for (int i=0 ; i<this.size() ; i++) {
            result = result + "(" + i + ") " + this.get(i).getClass() + "\n";
        }
        return result + "-----------------------------------------------";
    } 
    
} // End of class SemanticActionTableImpl
