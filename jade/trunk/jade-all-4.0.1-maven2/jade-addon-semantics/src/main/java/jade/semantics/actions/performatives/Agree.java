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
 * Agree.java
 * Created on 24 févr. 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.tools.SL;

/**
 * This class implements the prototype of the <code>AGREE</code> FIPA-ACL
 * communicative act.
 * <br>
 * <code>AGREE</code> is the general-purpose agreement to a previously submitted <code>REQUEST</code>
 * to perform some action. The agent sending the agreement informs the receiver
 * that it does intend to perform the action, but not until the given precondition
 * is true.
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * 
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/02/24 Revision: 1.0
 * @since JSA 1.0 
 */
public class Agree extends ActConditionInform {
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/

    /**
     * Create a new <code>Agree</code> prototype. According to the FIPA-ACL
     * formal specifications, the corresponding <code>INFORM</code> content
     * is set to <code>(I ??sender (done ??act ??condition))</code>.
     * 
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     * @param surfacePerformative the surface form (specified with the constants
     *                            from the {@link ACLMessage} class).
     * @param surfaceContentFormat the list of class expected in the surface
     * content.
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs.
     */
    
	public Agree(SemanticCapabilities capabilities,
			     int surfacePerformative, 
			     Class[] surfaceContentFormat, 
			     String surfaceContentFormatMessage) {
        super(capabilities, 
			  surfacePerformative, 
			  surfaceContentFormat, 
			  surfaceContentFormatMessage, 
			  true,
			  SL.formula("(I ??sender (done ??act ??condition))"));
    } 

    /**
     * Creates a new <code>Agree</code> prototype.
     * The surface content format, and the surface content format message 
     * are the default ones. 
     * The surface performative is set to <code>AGREE</code>.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public Agree(SemanticCapabilities capabilities) {
        this(capabilities, ACLMessage.AGREE, null, null);
    } 
    
    /**
     * Returns an instance of <code>Agree</code>
     * @return an instance of <code>Agree</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new Agree(getSemanticCapabilities());
    } // End of createInstance/0
} // End of class Agree
