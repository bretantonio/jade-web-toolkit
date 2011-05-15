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
 * QueryRef.java
 * Created on 24 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;

/**
 * This class represents the semantic action: <code>QueryRef</code>. <br>
 * This action is the action of asking another agent for the object referred to 
 * by a referential expression.<br>
 * The content of this action is a descriptor (a referential expression).<br>
 * The sending agent is requesting the receiver to perform an <code>Inform</code>
 * act, containing the object that corresponds to the descriptor.<br>
 * The agent performing the <code>QueryRef</code> act:
 * <ul>
 * <li> does not know which object or set of objects corresponds to the 
 * descriptor,
 * <li> believes that the other agent can inform the querying agent the object 
 * or set of objects that correspond to the descriptor.
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
public class QueryRef extends Request {
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Creates a new <code>QueryRef</code> prototype. By default, the surface content format
     * is set to [IdentifyingExpression]. 
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content (used to control the validity of the content)
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param rationalEffectRecognition pattern used to recognized the
     * rational effect of this action
      */   
    
	public QueryRef(SemanticCapabilities capabilities,
			        int surfacePerformative,
			        Class[] surfaceContentFormat, 
			        String surfaceContentFormatMessage, 
			        Formula rationalEffectRecognition) {
        super(capabilities,
			  surfacePerformative,
			  (surfaceContentFormat == null ? new Class[] {IdentifyingExpression.class} : surfaceContentFormat),
			  (surfaceContentFormatMessage == null ? "an IRE" : surfaceContentFormatMessage),
			  rationalEffectRecognition);
    }

    /**
     * Creates a new <code>QueryRef</code> prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>QUERY_REF</code>.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public QueryRef(SemanticCapabilities capabilities) {
    	this(capabilities, ACLMessage.QUERY_REF, null, null, null);
    }
    
    /**
     * Returns an instance of <code>QueryRef</code>
     * @return an instance of <code>QueryRef</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new QueryRef(getSemanticCapabilities());
    }
    
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    /**
     * @inheritDoc
     */
    @Override
	public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        Content requestContent = new ContentNode(new ListOfContentExpression());
        requestContent.addContentElement(new InformRef(getSemanticCapabilities()).newAction(
                getReceiver(),
                new ListOfTerm(new Term[] {getAuthor()}),
                surfaceContent,
                this).toActionExpression());
        return super.doNewAction(requestContent);
    }

    /**
     * @inheritDoc
     */
    @Override
	public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        InformRef informRef = (InformRef)getSemanticActionTable().getSemanticActionInstance((ActionExpression)rationalEffectMatching.getTerm("action"));
        setReceiver(informRef.getAuthor());
        if (informRef.isReplyTo(this)) {
            setSurfaceContentElement(0, informRef.getContentElement(0));
            return true;
        }
        return false;
    }
} // End of class QueryRef
