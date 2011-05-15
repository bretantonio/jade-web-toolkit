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
 * Failure.java
 * Created on 18 mai 2005
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Variable;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

/**
 * The action of telling another agent that an action was attempted but the 
 * attempt failed. failure is an abbreviation for informing that an act was
 * considered feasible by the sender, but was not completed for some given 
 * reason. The first part of the content is the action not feasible. The second
 * part is the reason for the failure, which is represented by a proposition. 
 * It may be the constant <code>true</code>. 
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Pautret - France Telecom
 * @version Date: 2005/05/18 Revision: 1.0
 * @since JSA 1.0
 */
public class Failure extends ActionReasonInform {
    /**
     * Creates a new <code>Failure</code> prototype. By default, the inform content
     * is set to "(and (failure ??action) (and (not (done ??action)) (and (not (I ??sender (done ??action))) ??reason)))".
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     */
     
	public Failure(SemanticCapabilities capabilities,
			        int surfacePerformative, 
			        Class[] surfaceContentFormat, 
			        String surfaceContentFormatMessage) {
        super(capabilities,
			  surfacePerformative, 
			  surfaceContentFormat, 
			  surfaceContentFormatMessage,
			  SL.formula("(and (forall ??__e (not (B ??sender (feasible ??__e (done ??action true)))))" +
			  				 " ??reason)"));
//              SL.formula("(and (failure ??action) (and (not (done ??action)) (and (not (I ??sender (done ??action))) ??reason)))"));
    }
    
    /**
     * Creates a new <code>Failure</code> prototype.
     * The surface content format, and the surface content format message, 
     * are the default ones. 
     * The surface performative is set to <code>FAILURE</code>.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public Failure(SemanticCapabilities capabilities) {
        this(capabilities, ACLMessage.FAILURE, null, null);
    }
    
    /**
     * Returns an instance of <code>Failure</code>
     * @return an instance of <code>Failure</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new Failure(getSemanticCapabilities());
    }
    
    /*********************************************************************/
    /**                         METHODS                                 **/
    /*********************************************************************/
    
    private Variable var = null;
    
    /**
     * {@inheritDoc}
     */
    @Override
	protected Formula instantiateInformContentPattern(Content surfaceContent) throws WrongTypeException {
        Formula result = super.instantiateInformContentPattern(surfaceContent).instantiate(
                "sender", getAuthor());
        if (var != null) {
        	result = result.instantiate("__e", var);
        	var = null;
        }
        return result;
    }   
    
    @Override
	public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        if (getAuthor().equals(rationalEffectMatching.getTerm("sender"))) {
            setReceiver(rationalEffectMatching.getTerm("receiver"));
            setSurfaceContentElement(0, rationalEffectMatching.getTerm("action"));
            setSurfaceContentElement(1,	rationalEffectMatching.getFormula("reason"));
            var = rationalEffectMatching.getVariable("__e");
            return true;
        }
        return false;
    } // End of setFeaturesFromRationalEffect/1

}
