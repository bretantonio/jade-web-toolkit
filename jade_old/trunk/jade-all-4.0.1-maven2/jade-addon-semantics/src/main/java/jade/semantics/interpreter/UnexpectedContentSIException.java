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
 * UnexpectedContentSIException.java
 * Created on 31 mai 2005
 * Author : Vincent Louis
 */
package jade.semantics.interpreter;

import jade.lang.acl.ACLMessage;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.tools.SL;

/**
 * Exception raised when the content of a message is unexpected.
 * @author Vincent Louis - France Telecom
 * @version Date: 2005/05/31 Revision: 1.0 
 */
public class UnexpectedContentSIException extends SemanticInterpretationException {
	
	
    /**
     * Creates a new exception
     * @param performative the performative which have the bad content
     * @param expected the expected content
     * @param found the found content
     */
    public UnexpectedContentSIException(int performative, String expected, String found) {
        super("unexpected-content");
        TermSetNode set = new TermSetNode(new ListOfTerm());
        set.as_terms().add(SL.string(ACLMessage.getPerformative(performative) + " requires " + expected));
        set.as_terms().add(SL.string("found: " + found));
        setObject(set);
    }
    
} // End of class UnexpectedContentSIException
