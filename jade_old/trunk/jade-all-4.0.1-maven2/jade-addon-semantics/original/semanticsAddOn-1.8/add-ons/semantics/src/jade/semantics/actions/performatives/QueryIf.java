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
 * QueryIf.java
 * Created on 8 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.operators.Alternative;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;


/**
 * This class represents the semantic action: <code>QueryIf</code>. <br>
 * This action is the action of asking another agent whether or not a given 
 * proposition is true.<br>
 * The content of this action is a proposition.<br>
 * The sending agent is requesting the receiver to perform an <code>Inform</code>
 * act, to inform it of the thruth of the proposition.<br>
 * The agent performing the <code>QueryIf</code> act:
 * <ul>
 * <li> has no knowledge of the thruth value of the proposition
 * <li> believes that the other agent can inform the querying agent if it knows 
 * the thruth of the proposition.
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
public class QueryIf extends Request {
    
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Creates a new <code>QueryIf</code> prototype. By default, the surface content format
     * is set to [Formula]. 
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
    
	public QueryIf(SemanticCapabilities capabilities,
			       int surfacePerformative,
			       Class[] surfaceContentFormat, 
			       String surfaceContentFormatMessage, 
			       Formula rationalEffectRecognition) {
        super(capabilities,
			  surfacePerformative,
			  (surfaceContentFormat == null ? new Class[] {Formula.class} : surfaceContentFormat),
			  (surfaceContentFormatMessage == null ? "a formula" : surfaceContentFormatMessage),
			  rationalEffectRecognition);
    }
    
    /**
     * Creates a new <code>QueryIf</code> prototype.
     * The surface content format, the surface content format message, and
     * the rational effect recognition pattern are the default ones. 
     * The surface performative is set to <code>QUERY_IF</code>.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public QueryIf(SemanticCapabilities capabilities) {
    	this(capabilities, ACLMessage.QUERY_IF, null, null, null);
    }
    
    /**
     * Returns an instance of <code>QueryIf</code>
     * @return an instance of <code>QueryIf</code>
     */
    @Override
	public CommunicativeActionProto createInstance() {
        return new QueryIf(getSemanticCapabilities());
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
        requestContent.addContentElement(new InformIf(getSemanticCapabilities()).newAction(
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
        Alternative informIf = (Alternative)getSemanticActionTable().getSemanticActionInstance((ActionExpression)rationalEffectMatching.getTerm("action"));
        if (((Inform)informIf.getLeftAction()).syntacticallyEquals(informIf.getRightAction(), false)) {
            Inform inform1 = (Inform)informIf.getLeftAction();
            Inform inform2 = (Inform)informIf.getLeftAction();
            setReceiver(inform1.getAuthor());
            if (inform1.isReplyTo(this)) {
                Formula formula1 = ((Formula)inform1.getContentElement(0)).getSimplifiedFormula();
                Formula notFormula2 = new NotNode(((Formula)inform2.getContentElement(0))).getSimplifiedFormula();
                if (formula1.equals(notFormula2)) {
                    setSurfaceContentElement(0, formula1);
                    return true;
                }
            }
        }
        return false;
    }
} // End of class QueryIf
