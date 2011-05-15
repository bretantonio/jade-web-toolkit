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
 * InformIf.java
 * Created on 15 nov. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.actions.performatives;

import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.operators.Alternative;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.content.UnparseContentException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

import java.util.Date;

/**
 * This class represents the semantic action: <code>InformIf</code>. <br>
 * The sender informs the receiver whether or not a given proposition is true.<br>
 * The content of this action is a proposition.<br>
 * In fact, this action action is a macro action that represents two possible 
 * courses of action: 
 * <ul>
 * <li><i>sender</i> informs <i>receiver</i> that <i>proposition</i>
 * <li><i>sender</i> informs <i>receiver</i> that not <i>proposition</i>
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
public class InformIf extends Alternative implements CommunicativeActionProto {

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
	/*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Creates a new <code>InformIf</code> action prototype. By default, the
     * rational effect recognition pattern is "(or (B ??receiver ??formula) (B ??receiver (not (??formula))))".
     * By default, the surface content format is [Formula].
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
    
	public InformIf(SemanticCapabilities capabilities,
				    int surfacePerformative,
                    Class[] surfaceContentFormat, 
                    String surfaceContentFormatMessage, 
                    Formula rationalEffectRecognition) {
        super(capabilities);
        this.surfacePerformative = surfacePerformative;
        this.myActionExpressionPattern = (Term)SL.instantiate(COMMUNICATIVE_ACTION_PATTERN,
        		"performative", SL.symbol(ACLMessage.getPerformative(surfacePerformative)));
        rationalEffectRecognitionPattern = (rationalEffectRecognition == null ? SL.formula("(or (B ??receiver ??formula) (B ??receiver (not ??formula)))") : rationalEffectRecognition);
        setPerformative(ACLMessage.INFORM_IF);
        this.surfaceContentFormat = (surfaceContentFormat == null ? new Class[] {Formula.class} : surfaceContentFormat);
        this.surfaceContentFormatMessage = (surfaceContentFormatMessage == null ? "a formula" : surfaceContentFormatMessage);
        this.contentSize = 1;
    }

    /**
     * Creates a new <code>InformIf</code> action prototype. The surface content
     * format, the surface content format message, and the rational effect
     * recognition pattern are the default ones.
     * @param capabilities the {@link SemanticCapabilities} instance, which this
     *                     action prototype belongs to.
     */
    public InformIf(SemanticCapabilities capabilities) {
    	this(capabilities, ACLMessage.INFORM_IF, null, null, null);
    }

    /**
     * Returns an instance of <code>InformIf</code>.
     * @return an instance of <code>InformIf</code>
     */
    public CommunicativeActionProto createInstance() {
        return new InformIf(getSemanticCapabilities());
    }

    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    /**
     * @inheritDoc
     */
    @Override
	public SemanticAction newAction(ActionExpression actionExpression) throws SemanticInterpretationException {
        return CommunicativeActionImpl.newAction(actionExpression, this);
    }
    
    /**
     * @inheritDoc
     */
    public SemanticAction newAction(ACLMessage aclMessage) throws SemanticInterpretationException {
        return CommunicativeActionImpl.newAction(aclMessage, this);
    }    
    /**
     * @inheritDoc
     */
    public SemanticAction newAction(Term author, ListOfTerm receivers, Content content, CommunicativeAction inReplyTo) throws SemanticInterpretationException {
        return CommunicativeActionImpl.newAction(author, receivers, content, inReplyTo, this);
    }
    
    /**
     * @inheritDoc
     */
    public SemanticAction newAction(Content content, CommunicativeAction body) throws SemanticInterpretationException {
        return CommunicativeActionImpl.newAction(content, body, this);
    }
    /**
     * @inheritDoc
     */
    @Override
	public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
        return CommunicativeActionImpl.newAction(rationalEffect, inReplyTo, this);
    }
    /**
     * @inheritDoc
     */
    public SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException {
        Content informNotContent = new ContentNode(new ListOfContentExpression());
        informNotContent.addContentElement(new NotNode((Formula)surfaceContent.getContentElement(0)).getSimplifiedFormula());
        setLeftAction(new Inform(getSemanticCapabilities()).newAction(surfaceContent, this));
        setRightAction(new Inform(getSemanticCapabilities()).newAction(informNotContent, this));
        setContent(surfaceContent);
        return this;
    }
    
    /**
     * @inheritDoc
     */
    public boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception {
        setReceiver(rationalEffectMatching.getTerm("receiver"));
        setSurfaceContentElement(0, rationalEffectMatching.getFormula("formula"));
        return true;
    }
    
    /***********************************************************************
     * PUBLIC GETTERS AND SETTERS
     ***********************************************************************/
    
    /**
     * Returns the performative.
     * @return the performative.
     */
    public int getPerformative() {
        return (performative < -1 ? surfacePerformative : performative);
    }
    
    /**
     * Sets the performative.
     * @param performative The performative to set.
     */
    public void setPerformative(int performative) {
        this.performative = performative;
    }
    
    /**
     * Returns the surfacePerformative.
     * @return the surfacePerformative.
     */
    public int getSurfacePerformative() {
        return surfacePerformative;
    }
    
    /**
     * Sets the surface performative.
     * @param surfacePerformative The surfacePerformative to set.
     */
    public void setSurfacePerformative(int surfacePerformative) {
        this.surfacePerformative = surfacePerformative;
    }
    /**
     * Returns the receiverList.
     * @return the receiverList.
     */
    public ListOfTerm getReceiverList() {
        return receiverList;
    } // End of getReceiverList/0
    
    /**
     * Returns the first receiver of the receiverList.
     * @return the first receiver of the receiverList.
     */
    public Term getReceiver() {
        return receiverList.first();
    } // End of getReceiver/0
    
    /**
     * Set a unique receiver to this communicative action
     * @param receiver a Term that represents the receiver
     */
    public void setReceiver(Term receiver) {
        setReceiverList(new ListOfTerm(new Term[] {receiver}));
    }
    
    /**
     * Sets the receiver list.
     * @param receiverList
     *            The receiverList to set.
     */
    public void setReceiverList(ListOfTerm receiverList) {
        this.receiverList = receiverList;
    } // End of setReceiverList/1
    /**
     * Sets the receiver list.
     * @param receiverList the receiver list
     */
    public void setReceiverList(TermSetNode receiverList) {
        this.receiverList = (receiverList == null ? null : receiverList.as_terms());
    }
    
    
    /**
     * Sets the content with the given content.
     * @param content
     *            The content to set.
     */
    public void setContent(Content content) {
        this.content = (Content)content.getClone();
    } // End of setContent/1
    
    /**
     * Sets the content with a new Content
     */
    public void setContent() {
        content = new ContentNode(new ListOfContentExpression());
    }
    /**
     * Sets the content with a new Content with the specified size
     * @param size size of the content
     */
    public void setContent(int size) {
        content = new ContentNode();
        content.setContentElements(size);
    }
    
    /**
     * Sets the content element at the specifed index with the specified element.
     * @param i an index
     * @param element a node 
     */
    public void setContentElement(int i, Node element) {
        getContent().setContentElement(i, element);
    }
    
    /**
     * Returns the content.
     * @return the content.
     */
    public Content getContent() {
        return (content == null ? surfaceContent : content);
    }
    
    /**
     * @inheritDoc
     */
    public Node getContentElement(int i) {
        return getContent().getContentElement(i);
    }
    /**
     * @inheritDoc
     */
    public int getContentElementNumber() {
        return getContent().contentElementNumber();
    }
    /**
     * @inheritDoc
     */
    public void setSurfaceContent(int size) {
        this.surfaceContent = new ContentNode(new ListOfContentExpression());
        this.surfaceContent.setContentElements(size);
    }
    /**
     * @inheritDoc
     */
    public void setSurfaceContent(Content content) {
        this.surfaceContent = new ContentNode((ListOfContentExpression)((ContentNode)content).as_expressions().getClone());
    }
    /**
     * @inheritDoc
     */
    public void setSurfaceContentElement(int i, Node element) {
        surfaceContent.setContentElement(i, element);
    }
    
    /**
     * @inheritDoc
     */
    public Content getSurfaceContent() {
        return surfaceContent;
    }
    
    /**
     * @inheritDoc
     */
    public Node getSurfaceContentElement(int i) {
        return surfaceContent.getContentElement(i);
    }
    /**
     * @inheritDoc
     */
    public int getSurfaceContentElementNumber() {
        return surfaceContent.contentElementNumber();
    }
    
    
    /**
     * Returns the conversationId.
     * @return the conversationId.
     */
    public String getConversationId() {
        return conversationId;
    }
    /**
     * Sets the conversationId parameter.
     * @param conversationId The conversationId to set.
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    /**
     * @inheritDoc
     */
    public void setConversationId(WordConstantNode conversationId) {
        this.conversationId = (conversationId == null ? null : conversationId.stringValue());
    }
    /**
     * @inheritDoc
     */
    public String getEncoding() {
        return encoding;
    }
    /**
     * @inheritDoc
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    /**
     * @inheritDoc
     */
    public void setEncoding(WordConstantNode encoding) {
        this.encoding = (encoding == null ? null : encoding.stringValue());
    }
    /**
     * @inheritDoc
     */
    public String getInReplyTo() {
        return inReplyTo;
    }
    /**
     * @inheritDoc
     */
    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }
    /**
     * @inheritDoc
     */
    public void setInReplyTo(WordConstantNode inReplyTo) {
        this.inReplyTo = (inReplyTo == null ? null : inReplyTo.stringValue());
    }
    /**
     * @inheritDoc
     */
    public String getLanguage() {
        return language;
    }
    /**
     * @inheritDoc
     */
    public void setLanguage(String language) {
        this.language = language;
    }
    /**
     * @inheritDoc
     */
    public void setLanguage(WordConstantNode language) {
        this.language = (language == null ? null : language.stringValue());
    }
    /**
     * @inheritDoc
     */
    public String getOntology() {
        return ontology;
    }
    /**
     * @inheritDoc
     */
    public void setOntology(String ontology) {
        this.ontology = ontology;
    }
    /**
     * @inheritDoc
     */
    public void setOntology(WordConstantNode ontology) {
        this.ontology = (ontology == null ? null : ontology.stringValue());
    }
    /**
     * @inheritDoc
     */
    public String getProtocol() {
        return protocol;
    }
    /**
     * @inheritDoc
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    /**
     * @inheritDoc
     */
    public void setProtocol(WordConstantNode protocol) {
        this.protocol = (protocol == null ? null : protocol.stringValue());
    }
    /**
     * @inheritDoc
     */
    public Date getReplyBy() {
        return replyBy;
    }
    /**
     * @inheritDoc
     */
    public void setReplyBy(Date replyBy) {
        this.replyBy = replyBy;
    }
    /**
     * @inheritDoc
     */
    public void setReplyBy(DateTimeConstantNode replyBy) {
        this.replyBy = (replyBy == null ? null : replyBy.lx_value());
    }
    /**
     * @inheritDoc
     */
    public ListOfTerm getReplyToList() {
        return replyToList;
    }
    /**
     * @inheritDoc
     */
    public void setReplyToList(ListOfTerm replyToList) {
        this.replyToList = replyToList;
    }
    /**
     * @inheritDoc
     */
    public void setReplyToList(TermSetNode replyToList) {
        this.replyToList = (replyToList == null ? null : replyToList.as_terms());
    }
    /**
     * @inheritDoc
     */
    public String getReplyWith() {
        return replyWith;
    }
    /**
     * @inheritDoc
     */
    public void setReplyWith(String replyWith) {
        this.replyWith = replyWith;
    }
    /**
     * @inheritDoc
     */
    public void setReplyWith(WordConstantNode replyWith) {
        this.replyWith = (replyWith == null ? null : replyWith.stringValue());
    }
    
    /**
     * @inheritDoc
     */
    public SemanticAction buildAction() throws SemanticInterpretationException {
        return CommunicativeActionImpl.buildAction(this);
    }
    /**
     * @inheritDoc
     */
    public ContentNode parseContent(String content, String language) throws SemanticInterpretationException {
        return CommunicativeActionImpl.parseContent(content, language, this);
    }

    /**
     * @inheritDoc
     */
    public ACLMessage toAclMessage() throws UnparseContentException {
        return CommunicativeActionImpl.toAclMessage(this);
    }

    /**
     * @inheritDoc
     */
    @Override
	public ActionExpression toActionExpression() throws SemanticInterpretationException {
        return CommunicativeActionImpl.toActionExpression(this, true);
    }
    
    /**
     * @inheritDoc
     */
    public Term getMyActionExpressionPattern() {
        return myActionExpressionPattern;
    }
    
    /**
     * @inheritDoc
     */
    
	public Class[] getSurfaceContentFormat() {
        return surfaceContentFormat;
    }
    
    /**
     * @inheritDoc
     */
    public int getContentSize() {
        return contentSize;
    }
    
    /**
     * @inheritDoc
     */
    public String getSurfaceContentFormatMessage() {
        return surfaceContentFormatMessage;
    }

    /**
     * @inheritDoc
     */
    public Formula getRationalEffectRecognitionPattern() {
        return rationalEffectRecognitionPattern;
    }
} // End of class InformIf
