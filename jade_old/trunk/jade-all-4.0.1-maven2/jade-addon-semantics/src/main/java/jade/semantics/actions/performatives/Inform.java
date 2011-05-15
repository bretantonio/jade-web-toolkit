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
 * Inform.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;

/**
 * This class represents the semantic action: <code>Inform</code>. <br>
 * The sender informs the receiver that a given proposition is true.<br>
 * The content of this action is a proposition.<br>
 * <code>Inform</code> indicates that the sending agent:
 * <ul>
 * <li> holds that some proposition is true,
 * <li> intends that the receiving agent also comes to believe that the 
 * propositon is true,
 * <li> does not already believe that the receiver as any knowledge of the thruth
 * of the proposition.
 * </ul>
 * From the receiver's viewpoint, receiving a <code>Inform</code> message 
 * entitles it to believe that:
 * <ul>
 * <li> the sender believes the proposition that is the content of the message,
 * <li> the sender whishes the receiver to believe that proposition also.
 * </ul> 
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 * @since JSA 1.0
 */
public class Inform extends Assertive {
    
    /**
     * Creates a new <code>Inform</code> action prototype. The performative is set to 
     * <code>INFORM</code>. The feasibility precondition pattern,
     * the persistent feasibility pattern and the postcondition pattern are
     * respectively set to:
     * <ul>
     * <li>"(and (B ??sender ??formula) (not (B ??sender (or (or (B ??receiver ??formula) (B ??receiver (not ??formula))) (or (U ??receiver ??formula) (U ??receiver (not ??formula)))))))"
     * <li>"(B ??sender ??formula)"
     * <li>"(B ??sender (B ??receiver ??formula))"
     * </ul>
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param rationalEffectRecognition pattern used to recognized the
     * rational effect of this action
     */    
    
	public Inform(SemanticCapabilities capabilities,
			      int surfacePerformative,
			      Class[] surfaceContentFormat, 
			      String surfaceContentFormatMessage, 
			      Formula rationalEffectRecognition) {
        super(capabilities,
			  surfacePerformative, surfaceContentFormat, surfaceContentFormatMessage, rationalEffectRecognition,
			  SL.formula("(and (B ??sender ??formula) " +
                    "(not (B ??sender (or (or (B ??receiver ??formula) (B ??receiver (not ??formula))) " +
                    "                     (or (U ??receiver ??formula) (U ??receiver (not ??formula)))))))"),
			  SL.formula("(B ??sender ??formula)"),
			  SL.formula("(B ??sender (B ??receiver ??formula))"));
        setPerformative(ACLMessage.INFORM);
    }
    /**
     * Creates a new <code>Inform</code> Action prototype.
     * The feasibility precondition, the persistent feasibility preconditon, and
     * the rational effect are the default ones. The surface performative
     * is set to <code>INFORM</code>.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public Inform(SemanticCapabilities capabilities) {
        this(capabilities, ACLMessage.INFORM, null, null, null);
    }
    
    /**
     * Returns an instance of <code>Inform</code>
     * @return an instance of <code>Inform</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new Inform(getSemanticCapabilities());
    }
} 
