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
 * Propagate.java
 * Created on 09 may 2006
 * Author : Vincent Louis
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.actions.operators.Sequence;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.UnexpectedContentSIException;
import jade.semantics.lang.sl.content.UnparseContentException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.FunctionalTermParamNode;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.semantics.lang.sl.tools.SL.WrongTypeException;

import java.util.Date;

/**
 * This class represents the semantic action: <code>Propagate</code>. <br>
 * The sender performs a communicative act towards the receiver and requests
 * him to propagate this act towards other agents identified by an IRE.<br>
 * The content of this action is a triplet consisting of an IRE, a
 * communicative act and a proposition.<br>
 * This action is not primitive and is internally handled, as specified in
 * the FIPA Communicative Act Library, as a sequence of the communicative act
 * to propagate and an Inform act expressing the intention of the sender to
 * propagate this communicative act to some other agents
 *  
 * <p>
 * This class is not intended to be directly used by developers. It is loaded
 * in semantic agents' semantic action table by the
 * {@link jade.semantics.actions.DefaultSemanticActionLoader}.
 * </p>
 * @author Vincent Louis - France Telecom
 * @version Date: 2006/05/09 Revision: 1.0
 * @since JSA 1.5 
 */
public class Propagate extends Sequence implements CommunicativeActionProto {

    /**
     * The action expression pattern to recognize the communicative action
     * (built from COMMUNICATIVE_ACTION_PATTERN)
     */
    private Term myActionExpressionPattern;
    
    /**
     * The rational effect pattern to recognize for the communicative action
     */
    protected Formula rationalEffectRecognitionPattern;

    /**
     * Performative ID of the communicative action (all communicative
     * actions have a performative)
     * @see jade.lang.acl.ACLMessage
     */
    private int performative;
    
    /**
     * Surface form performative 
     */
    private int surfacePerformative;

    /**
     * The content of the communicative action (all communicative actions
     * have a content)
     */
    private Content content;
    
    /**
     * Size of the content
     */
    protected int contentSize;
    
    /**
     * Surface content
     */
    private Content surfaceContent;
    /**
     * Format of the surface content
     */
    
	private Class[] surfaceContentFormat;
    /**
     * Message 
     */
    private String surfaceContentFormatMessage;

    /**
     * The list of receivers of the communicative action (all communicative
     * actions have a least one receiver)
     */
    private ListOfTerm receiverList;
    
    /**
     * replyto parameter
     */
    private ListOfTerm replyToList;
    
    /**
     * conversationId parameter
     */
    private String conversationId;
    
    /**
     * encoding parameter
     */
    private String encoding;
    
    /**
     * inReplyTo parameter
     */
    private String inReplyTo;
    
    /**
     * language parameter
     */
    private String language;
    
    /**
     * ontology parameter
     */
    private String ontology;
    
    /**
     * protocol parameter
     */
    private String protocol;
    
    /**
     * replyBy parameter
     */
    private Date replyBy;
    
    /**
     * replyWith parameter
     */
    private String replyWith;
    
    static final private Content INFORM_CONTENT_PATTERN = SL.content(
    		"(I ??sender (exists ??y (and (B ??receiver (= ??ire ??y))" +
    		"                             (done ??propagate (B ??receiver ??condition)))))");

    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

	
	public Propagate(SemanticCapabilities capabilities,
			         int surfacePerformative,
			         Class[] surfaceContentFormat, 
			         String surfaceContentFormatMessage, 
			         Formula rationalEffectRecognition) {
        super(capabilities);
        this.surfacePerformative = surfacePerformative;
        this.myActionExpressionPattern = (Term)SL.instantiate(COMMUNICATIVE_ACTION_PATTERN,
        		"performative", SL.symbol(ACLMessage.getPerformative(surfacePerformative)));
        // The rational effect recognition is not yet implemented
        rationalEffectRecognitionPattern = (rationalEffectRecognition == null ? SL.formula("true") : rationalEffectRecognition);
        setPerformative(ACLMessage.PROPAGATE);
        this.surfaceContentFormat = (surfaceContentFormat == null ? new Class[] {IdentifyingExpression.class, ActionExpressionNode.class, Formula.class} : surfaceContentFormat);
        this.surfaceContentFormatMessage = (surfaceContentFormatMessage == null ? "an IRE, a communicative act and a formula" : surfaceContentFormatMessage);
        this.contentSize = 3;
    }

    /**
     * Creates a new <code>Propagate</code> action prototype. The surface content
     * format, the surface content format message, and the rational effect
     * recognition pattern are the default ones.
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     */
	public Propagate(SemanticCapabilities capabilities) {
		this(capabilities, ACLMessage.PROPAGATE, null, null, null);
		// TODO Auto-generated constructor stub
	}

    /**
     * Returns an instance of <code>Propagate</code>.
     * @return an instance of <code>Propagate</code>
     */
	public CommunicativeActionProto createInstance() {
		return new Propagate(getSemanticCapabilities());
	}

    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
 
	/**
     * @inheritDoc
     */
	public SemanticAction newAction(ACLMessage aclMessage)
	throws SemanticInterpretationException {
		return CommunicativeActionImpl.newAction(aclMessage, this);
	}
	
	/**
     * @inheritDoc
     */
	public SemanticAction newAction(Term author, ListOfTerm receivers,
			Content content, CommunicativeAction inReplyTo)
	throws SemanticInterpretationException {
		return CommunicativeActionImpl.newAction(author, receivers, content, inReplyTo, this);
	}
	
	/**
     * @inheritDoc
     */
	public SemanticAction newAction(Content content, CommunicativeAction body)
	throws SemanticInterpretationException {
		return CommunicativeActionImpl.newAction(content, body, this);
	}
	
    /**
     * @inheritDoc
     */
    public SemanticAction newAction(ActionExpression actionExpression) 
    throws SemanticInterpretationException 
    {
        return CommunicativeActionImpl.newAction(actionExpression, this);
    }

    /**
     * @inheritDoc
     */
    public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
        return CommunicativeActionImpl.newAction(rationalEffect, inReplyTo, this);
    }

    /**
     * @inheritDoc
     */
    public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
    	ActionExpressionNode actToPropagate = (ActionExpressionNode)surfaceContent.getContentElement(1);
    	if (!getAuthor().equals(actToPropagate.as_agent())) {
    		throw new UnexpectedContentSIException(getSurfacePerformative(),
    				"an act from the sender [" + getAuthor() + "]", actToPropagate.as_agent().toString());
    	}
       	CommunicativeAction communicativeAct = null;
       	try {
			communicativeAct = (CommunicativeAction)getSemanticActionTable().getSemanticActionInstance(
					((ActionExpressionNode)surfaceContent.getContentElement(1)));
		} catch (ClassCastException cce) {
			throw new UnexpectedContentSIException(getSurfacePerformative(),
					"a communicative action", surfaceContent.getContentElement(1).toString());
		}
		if (actToPropagate == null) { // NOTE: this should never happen
			throw new SemanticInterpretationException("not-understood-action-expression", actToPropagate);
		}
		communicativeAct.setReceiver(getReceiver());
		setLeftAction(communicativeAct);
		
		Content propagateContent = (Content)surfaceContent.getClone();
		actToPropagate = ((ActionExpressionNode)propagateContent.getContentElement(1));
		actToPropagate.as_agent(getReceiver());
		((FunctionalTermParamNode)actToPropagate.as_term()).setParameter("receiver",
				new TermSetNode(new ListOfTerm(new Term[] {getReceiver()})));
		ActionExpression propagate = CommunicativeActionImpl.toActionExpression(this, true, getReceiver(), new VariableNode("y"), propagateContent);
		Content informContent = null;
		try {
			informContent = (Content)INFORM_CONTENT_PATTERN.getClone();
			SL.set(informContent, "sender", getAuthor());
			SL.set(informContent, "receiver", getReceiver());
			SL.set(informContent, "ire", surfaceContent.getContentElement(0));
			SL.set(informContent, "condition", surfaceContent.getContentElement(2));
			SL.set(informContent, "y", new VariableNode("y"));
			SL.set(informContent, "propagate", propagate);
            SL.substituteMetaReferences(informContent);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SemanticInterpretationException("unexpected-internal-error", new WordConstantNode());
		}
		CommunicativeAction informProto = 
			(CommunicativeAction)getSemanticActionTable().getSemanticActionPrototype(Inform.class);
		setRightAction(informProto.newAction(informContent, this));
		
		setContent(surfaceContent);
		return this;
    }

	
	
	public SemanticAction buildAction() throws SemanticInterpretationException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentNode parseContent(String content, String language)
			throws SemanticInterpretationException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setFeaturesFromRationalEffect(
			MatchResult rationalEffectMatching) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public Term getMyActionExpressionPattern() {
		// TODO Auto-generated method stub
		return null;
	}

	public SemanticActionTable getSemanticActionTable() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Class[] getSurfaceContentFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getSurfaceContentFormatMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Formula getRationalEffectRecognitionPattern() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPerformative() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPerformative(int performative) {
		// TODO Auto-generated method stub

	}

	public int getSurfacePerformative() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSurfacePerformative(int surfacePerformative) {
		// TODO Auto-generated method stub

	}

	public ListOfTerm getReceiverList() {
		// TODO Auto-generated method stub
		return null;
	}

	public Term getReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setReceiver(Term receiver) {
		// TODO Auto-generated method stub

	}

	public void setReceiverList(ListOfTerm receiverList) {
		// TODO Auto-generated method stub

	}

	public void setReceiverList(TermSetNode receiverList) {
		// TODO Auto-generated method stub

	}

	public void setContent(Content content) {
		// TODO Auto-generated method stub

	}

	public void setContent() {
		// TODO Auto-generated method stub

	}

	public void setContent(int size) {
		// TODO Auto-generated method stub

	}

	public void setContentElement(int i, Node element) {
		// TODO Auto-generated method stub

	}

	public Content getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getContentElement(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentElementNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setSurfaceContent(int size) {
		// TODO Auto-generated method stub

	}

	public void setSurfaceContent(Content content) {
		// TODO Auto-generated method stub

	}

	public void setSurfaceContentElement(int i, Node element) {
		// TODO Auto-generated method stub

	}

	public Content getSurfaceContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node getSurfaceContentElement(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSurfaceContentElementNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getConversationId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setConversationId(String conversationId) {
		// TODO Auto-generated method stub

	}

	public void setConversationId(WordConstantNode conversationId) {
		// TODO Auto-generated method stub

	}

	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setEncoding(String encoding) {
		// TODO Auto-generated method stub

	}

	public void setEncoding(WordConstantNode encoding) {
		// TODO Auto-generated method stub

	}

	public String getInReplyTo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setInReplyTo(String inReplyTo) {
		// TODO Auto-generated method stub

	}

	public void setInReplyTo(WordConstantNode inReplyTo) {
		// TODO Auto-generated method stub

	}

	public String getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setLanguage(String language) {
		// TODO Auto-generated method stub

	}

	public void setLanguage(WordConstantNode language) {
		// TODO Auto-generated method stub

	}

	public String getOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setOntology(String ontology) {
		// TODO Auto-generated method stub

	}

	public void setOntology(WordConstantNode ontology) {
		// TODO Auto-generated method stub

	}

	public String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProtocol(String protocol) {
		// TODO Auto-generated method stub

	}

	public void setProtocol(WordConstantNode protocol) {
		// TODO Auto-generated method stub

	}

	public Date getReplyBy() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setReplyBy(Date replyBy) {
		// TODO Auto-generated method stub

	}

	public void setReplyBy(DateTimeConstantNode replyBy) {
		// TODO Auto-generated method stub

	}

	public ListOfTerm getReplyToList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setReplyToList(ListOfTerm replyToList) {
		// TODO Auto-generated method stub

	}

	public void setReplyToList(TermSetNode replyToList) {
		// TODO Auto-generated method stub

	}

	public String getReplyWith() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setReplyWith(String replyWith) {
		// TODO Auto-generated method stub

	}

	public void setReplyWith(WordConstantNode replyWith) {
		// TODO Auto-generated method stub

	}

	public ACLMessage toAclMessage() throws UnparseContentException {
		// TODO Auto-generated method stub
		return null;
	}

}
