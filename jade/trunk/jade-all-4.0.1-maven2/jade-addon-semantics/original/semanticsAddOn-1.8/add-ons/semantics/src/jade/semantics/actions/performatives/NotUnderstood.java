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
 * NotUnderstood.java
 * Created on 18 mai 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.tools.SL;

/**
 * The sender of the act (for example agt1) informs the receiver (for example
 * agt2) that it perceived that agt2 performed some action, but that agt1 did
 * not understand what agt2 just did. A particular common case is
 * that agt1 tells agt2 that agt1 did not understand the message that agt2 has
 * just sent to agt1. The first element of the message content is the action
 * agt1 has not understood. The second element is a proposition representing
 * the reason for the failure to understand.
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/18 Revision: 1.0
 * @since JSA 1.0
 */
public class NotUnderstood extends ActionReasonInform {
    
    /**
     * Creates a new <code>NotUnderstood</code> prototype. By default, the inform content
     * is set to "(and (not-understood ??action) ??reason)".
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     */
    
	public NotUnderstood(SemanticCapabilities capabilities,
			             int surfacePerformative, 
			             Class[] surfaceContentFormat, 
			             String surfaceContentFormatMessage) {
        super(capabilities,
			  surfacePerformative, 
			  surfaceContentFormat, 
			  surfaceContentFormatMessage,
			  SL.formula("(and (not-understood ??action) ??reason)"));
    }
    
    /**
     * Creates a new <code>NotUnderstood</code> prototype.
     * The surface content format, and the surface content format message, 
     * are set to <code>null</code>. 
     * The surface performative is set to <code>NOT_UNDERSTOOD</code>.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public NotUnderstood(SemanticCapabilities capabilities) {
        this(capabilities, ACLMessage.NOT_UNDERSTOOD, null, null);
    }
    
    /**
     * Returns an instance of <code>NotUnderstood</code>.
     * @return an instance of <code>NotUnderstood</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new NotUnderstood(getSemanticCapabilities());
    }
}
