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
 * CommunicativeActionImpl.java
 * Created on 9 dec. 2004
 * Author : Vincent Louis
 */
package jade.semantics.actions.performatives;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionImpl;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.CommunicativeActionBehaviour;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.UnexpectedContentSIException;
import jade.semantics.lang.sl.content.ContentParser;
import jade.semantics.lang.sl.content.ParseContentException;
import jade.semantics.lang.sl.content.UnparseContentException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.DateTimeConstantNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.ListOfContentExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Node;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.Iterator;

import java.util.Date;

/**
 * This class is an implementation of the <code>CommunicativeAction</code>
 * interface specific to the use of FIPA performatives. It is the super class of
 * the semantic implementation of all FIPA performatives.
 * @author Vincent LOUIS - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0
 */
public abstract class CommunicativeActionImpl extends SemanticActionImpl  implements CommunicativeAction, CommunicativeActionProto {
	
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
     * Pattern used to build the rational effect
     */
    protected Formula rationalEffectPattern; 

    /**
     * Pattern used to build the feasibility precondition
     */
    protected Formula feasibilityPreconditionPattern; 

    /**
     * Pattern used to build the persistent feasibility precondition
     */
    protected Formula persistentFeasibilityPreconditionPattern; 

    /**
     * Pattern used to build the postcondition 
     */
    protected Formula postConditionPattern;

    
    /**
     * Performative ID of the communicative action (all communicative
     * actions have a performative)
     *@see jade.lang.acl.ACLMessage
     */
    private int performative;
    
    /**
     * The surface form of this action 
     */
    private int surfacePerformative;

    /**
	 * The content of the communicative action (all communicative actions
     * have a content)
	 */
	private Content content;
    
    /**
     * the format of the surface content 
     */
    
	private Class[] surfaceContentFormat;
    
    /**
     * The message to send if an unexpected exception occurs
     */
    private String surfaceContentFormatMessage;
    /**
     * Size of the content
     */
    protected int contentSize;
    
    /**
     * The surface content
     */
    private Content surfaceContent;

    /**
     * The list of receivers of the communicative action (all communicative
     * actions have a least one receiver)
     */
    private ListOfTerm receiverList;
    
    /**
     * The reply-to-list parameter of a message.
     */
    private ListOfTerm replyToList;
    
    /**
     * The conversation-id parameter of a message.
     */
    private String conversationId;
    
    /**
     * The encoding parameter of a message.
     */
    private String encoding;
    
    /**
     * The in-reply-to parameter of a message.
     */
    private String inReplyTo;
    
    /**
     * The language parameter of a message.
     */
    private String language;
    
    /**
     * The ontology parameter of a message.
     */
    private String ontology;
    
    /**
     * The protocol parameter of a message.
     */
    private String protocol;
    
    /**
     * The reply-by parameter of a message.
     */
    private Date replyBy;
    
    /**
     * The reply-with parameter of a message.
     */
    private String replyWith;


    /***************************************************************************
	 * CONSTRUCTOR /
	 **************************************************************************/

	/**
	 * Creates a new Communicative Action prototype. A call to one of the
     * <code>newAction</code> methods creates instances of this Ontologic
     * Action prototype such that :
     * <ul>
     * <li> the <code>getFeasibilityPrecondition</code>, 
     * <code>getPersistentFeasibilityPrecondition</code>, <code>getRationalEffect</code>and
     * <code>getPostCondition</code> methods return formulae that comply with
     * the corresponding patterns given to this constructor;,</li>
     * <li> the surface form of the message (i.e. the effective message sent) 
     * and the surface content (the effective content sent) are given by the 
     * the surface parameters givent to this constructor;</li>
     * <li> the <code>computeBehaviour</code> method returns a
     * <code>PrimitiveBehaviour</code>.
     * </li>
     * </ul>
     * @param table the SemanticActionTable, which this action prototype belongs
     * to
     * @param surfacePerformative the surface form
     * @param surfaceContentFormat the list of class expected in the surface
     * content (used to control the validity of the content)
     * @param surfaceContentFormatMessage the message to send when an 
     * unexpected exception occurs
     * @param rationalEffectRecognitionPattern pattern used to recognized the
     * rational effect of this action
     * @param rationalEffectPattern pattern used to instantiate the SL formula 
     * representing the rational effect of this action.
     * @param feasibilityPreconditionPattern pattern used to instantiate the SL 
     * formula representing the feasibility precondition of this action.
     * @param persistentFeasibilityPreconditionPattern pattern used to 
     * instantiate the SL formula representing the persistent feasibility 
     * precondition of this action.
     * @param postConditionPattern pattern used to instantiate the SL formula 
     * representing the postcondition of this action.
	 */
	
	public CommunicativeActionImpl(SemanticCapabilities capabilities,
			int surfacePerformative,
            Class[] surfaceContentFormat, String surfaceContentFormatMessage, Formula rationalEffectRecognitionPattern,
            Formula rationalEffectPattern,
            Formula feasibilityPreconditionPattern,
            Formula persistentFeasibilityPreconditionPattern,
            Formula postConditionPattern) {
		super(capabilities);
        this.rationalEffectPattern = rationalEffectPattern;
        this.feasibilityPreconditionPattern = feasibilityPreconditionPattern;
        this.persistentFeasibilityPreconditionPattern = persistentFeasibilityPreconditionPattern;
        this.postConditionPattern = postConditionPattern;
        this.surfacePerformative = surfacePerformative;
        this.myActionExpressionPattern = (Term)SL.instantiate(COMMUNICATIVE_ACTION_PATTERN,
        		"performative", SL.symbol(ACLMessage.getPerformative(surfacePerformative)));
        this.surfaceContentFormat = surfaceContentFormat;
        this.surfaceContentFormatMessage = surfaceContentFormatMessage;
        this.rationalEffectRecognitionPattern = (rationalEffectRecognitionPattern == null ? (Formula)rationalEffectPattern.getClone() : rationalEffectRecognitionPattern);
	} // End of CommunicativeActionImpl/9
    
    /**
     * Creates a new instance of communicative action. This abstract class 
     * should be implemented by each subclass.
     * @return a new instance of communicative action
     */
    public abstract CommunicativeActionProto createInstance();

	/***************************************************************************
	 * PUBLIC METHODS /
	 **************************************************************************/
    /**
     * Creates a new instance of this prototype of semantic action from
     * the specified action expression.
     * 
     * @param actionExpression
     *          an expression of action that specifies the instance to create
     * @return a new instance of the semantic action, the action expression of
     * which is specified, or null if no instance of the semantic action with
     * the specified action expression can be created
     * @throws SemanticInterpretationException if any exception occurs
     */
    @Override
	public SemanticAction newAction(ActionExpression actionExpression) throws SemanticInterpretationException {
        return newAction(actionExpression, this);
    } // End of newAction/1
    
    /**
     * Creates a new instantiated instance of the action based on the specified 
     * action expression.
     * @param actionExpression an action expression
     * @param act the kind of action to be create
     * @return a semantic action 
     * @throws SemanticInterpretationException if any exception occurs
     */
    static public SemanticAction newAction(ActionExpression actionExpression, CommunicativeActionProto act) 
    throws SemanticInterpretationException {
        MatchResult matchResult = SL.match(act.getMyActionExpressionPattern(), actionExpression);
        if (matchResult != null) {
            if (actionExpression.sm_action() != null) {
                return actionExpression.sm_action();
            }
            CommunicativeActionProto result = act.createInstance();
            try {
                result.setAuthor(matchResult.getTerm("sender"));
                result.setReceiverList((TermSetNode)matchResult.getTerm("receiver"));
                result.setLanguage((WordConstantNode)matchResult.getTerm("language"));
                result.setOntology((WordConstantNode)matchResult.getTerm("ontology"));
                result.setEncoding((WordConstantNode)matchResult.getTerm("encoding"));
                result.setConversationId((WordConstantNode)matchResult.getTerm("conversation-id"));
                result.setProtocol((WordConstantNode)matchResult.getTerm("protocol"));
                result.setReplyWith((WordConstantNode)matchResult.getTerm("reply-with"));
                result.setInReplyTo((WordConstantNode)matchResult.getTerm("in-reply-to"));
                result.setReplyBy((DateTimeConstantNode)matchResult.getTerm("reply-by"));
                result.setReplyToList((TermSetNode)matchResult.getTerm("reply-to"));
                Term content = matchResult.getTerm("content");
                if ( content instanceof StringConstantNode ) {
                	result.setSurfaceContent(act.parseContent(((StringConstantNode)matchResult.getTerm("content")).stringValue(), result.getLanguage()));
                }
                else {//assumes it's a Content node
                	result.setSurfaceContent((Content)content);
                }
            }
            catch (SemanticInterpretationException sie) {
                throw sie;
            }
            catch (Exception e) { // May be NullPointerException, ClassCastException or WrongTypeException
                e.printStackTrace();
                throw new SemanticInterpretationException("ill-formed-message", SL.word(""));
            }
            SemanticAction finalResult = result.buildAction();
            actionExpression.sm_action(finalResult);
            return finalResult;
        } 
        return null;
    } // End of newAction/2
    
    /**
     * @inheritDoc
     */
    public SemanticAction newAction(ACLMessage aclMessage) throws SemanticInterpretationException {
        return newAction(aclMessage, this);
    } // End of newAction/1
    
    /**
     * Creates an instance of the Communicative Action from an ACLMessage
     * @param aclMessage
     * @param act the kind of action to be create
     * @throws SemanticInterpretationException if any exception occurs
     * @return a semantic action
     */
    static public SemanticAction newAction(ACLMessage aclMessage, CommunicativeActionProto act) throws SemanticInterpretationException {
		if (aclMessage.getPerformative() == act.getSurfacePerformative()) {
            CommunicativeActionProto result = act.createInstance();
            result.setAuthor(Tools.AID2Term(aclMessage.getSender()));
            Iterator aidIterator = aclMessage.getAllReceiver();
            if (aidIterator.hasNext()) {
                result.setReceiverList(new ListOfTerm());
                while (aidIterator.hasNext()) {
                    result.getReceiverList().add(Tools.AID2Term((AID)aidIterator.next()));
                }
            }
            result.setLanguage(aclMessage.getLanguage());
            result.setOntology(aclMessage.getOntology());
            result.setEncoding(aclMessage.getEncoding());
            result.setConversationId(aclMessage.getConversationId());
            result.setProtocol(aclMessage.getProtocol());
            result.setReplyWith(aclMessage.getReplyWith());
            result.setInReplyTo(aclMessage.getInReplyTo());
            result.setReplyBy(aclMessage.getReplyByDate());
            if ((aidIterator = aclMessage.getAllReplyTo()).hasNext()) {
                result.setReplyToList(new ListOfTerm());
                while (aidIterator.hasNext()) {
                    result.getReplyToList().add(Tools.AID2Term((AID)aidIterator.next()));
                }
            }
            result.setSurfaceContent(act.parseContent(aclMessage.getContent(), result.getLanguage()));
            return result.buildAction();
        }
        return null;
    } 

    /**
     * Creates an instance of the Communicative Action from a sender, receivers and content,
     * such that it is a consistent reply to another communicative action.
     * @param author a term that represents the author of the action
     * @param receivers a list of terms that represents the receiver list
     * @param content the content of the action
     * @param inReplyTo a communicative action 
     * @throws SemanticInterpretationException if any exception occurs
     * @return a semantic action
     */
    public SemanticAction newAction(Term author, ListOfTerm receivers, Content content, CommunicativeAction inReplyTo) throws SemanticInterpretationException {
        return newAction(author, receivers, content, inReplyTo, this);
    } // End of newAction/4
    /**
     * Creates an instance of the Communicative Action from a sender, receivers and content,
     * such that it is a consistent reply to another communicative action
     * @param author a term that represents the author of the action
     * @param receivers a list of terms that represents the receiver list
     * @param content the content of the action
     * @param inReplyTo a communicative action 
     * @param act the kind of action to be create
     * @return a semantic action
     * @throws SemanticInterpretationException if any exception occurs
     */
    static public SemanticAction newAction(Term author, ListOfTerm receivers, Content content, CommunicativeAction inReplyTo, CommunicativeActionProto act) throws SemanticInterpretationException {
        CommunicativeActionProto result = act.createInstance();
        result.setAuthor(author);
        result.setReceiverList(receivers);
        result.setSurfaceContent(content);
        if (inReplyTo != null) {
            result.setLanguage(inReplyTo.getLanguage());
            result.setOntology(inReplyTo.getOntology());
            result.setProtocol(inReplyTo.getProtocol());
            result.setInReplyTo(inReplyTo.getReplyWith());
            result.setConversationId(inReplyTo.getConversationId());
            if (inReplyTo.getReplyToList() != null) result.setReceiverList(inReplyTo.getReplyToList());
        }
        return result.buildAction();
    } // End of newAction/5
    
    /**
     * @inheritDoc
     */
    public SemanticAction newAction(Content content, CommunicativeAction body) throws SemanticInterpretationException {
        return newAction(content, body, this);
    } // End of newAction/2
    
    /**
     * Creates an instance of the Communicative Action from another 
     * communicative action and a specific content
     * @param content content of the action
     * @param body the communicative action
     * @param act the kind of action to be create
     * @return a semantic action
     * @throws SemanticInterpretationException if any exception occurs
     */
    static public SemanticAction newAction(Content content, CommunicativeAction body, CommunicativeActionProto act) throws SemanticInterpretationException {
        CommunicativeActionProto result = act.createInstance();
        result.setAuthor(body.getActor());
        result.setReceiverList(body.getReceiverList());
        result.setSurfaceContent(content);
        result.setLanguage(body.getLanguage());
        result.setOntology(body.getOntology());
        result.setProtocol(body.getProtocol());
        result.setInReplyTo(body.getInReplyTo());
        result.setConversationId(body.getConversationId());
        result.setReplyWith(body.getReplyWith());
        result.setReplyBy(body.getReplyBy());
        result.setReplyToList(body.getReplyToList());
        result.setEncoding(body.getEncoding());
        return result.buildAction();
    } // End of newAction/3

    /**
     * Creates an instance of the Communicative Action from an instantiation of 
     * its Rational Effect
     * @param rationalEffect rational effect of the action
     * @param inReplyTo a communicative action
     * @return a semantic action
     */
    @Override
	public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo) {
        return newAction(rationalEffect, inReplyTo, this);
    } // End of newAction/2

    /**
     * Creates an instance of the Communicative Action from an instantiation of its Rational Effect
     * @param rationalEffect rational effect of the action
     * @param inReplyTo a communicative action
     * @param act the kind of action to be create
     * @return a semantic action
     */
    static public SemanticAction newAction(Formula rationalEffect, ACLMessage inReplyTo, CommunicativeActionProto act) {
    	MatchResult matchResult = SL.match(act.getRationalEffectRecognitionPattern(), rationalEffect);
    	if (matchResult != null) {
            CommunicativeActionProto result = act.createInstance();
            result.setAuthor(act.getSemanticCapabilities().getAgentName());
            if (inReplyTo != null) {
                result.setLanguage(inReplyTo.getLanguage());
                result.setOntology(inReplyTo.getOntology());
                result.setProtocol(inReplyTo.getProtocol());
                result.setInReplyTo(inReplyTo.getReplyWith());
                result.setConversationId(inReplyTo.getConversationId());
            }
            result.setSurfaceContent(act.getSurfaceContentFormat().length);
            try {
                if (result.setFeaturesFromRationalEffect(matchResult)) {
                    if (inReplyTo != null) {
                        Iterator aidIterator = inReplyTo.getAllReplyTo();
                        if (aidIterator.hasNext()) {
                            result.setReceiverList(new ListOfTerm());
                            while (aidIterator.hasNext()) {
                                result.getReceiverList().add(Tools.AID2Term((AID)aidIterator.next()));
                            }
                        }
                    }
                    return result.buildAction();
                }
            }
            catch (Exception e) { // Exception from setFeaturesFromRationalEffect() or SemanticInterpretationException from buildAction()
//            	System.err.println("##### " + act.getClass());
//            	System.err.println("##### " + rationalEffect);
//            	e.printStackTrace();
                return null;
            }
        }
        return null;
    } 
    
    
    /**
     * @inheritDoc
     */
    public SemanticAction buildAction() throws SemanticInterpretationException {
        return buildAction(this);
    }
    
    /**
     * Checks the validity of the builded action and sets the content of it.
     * @param act the kind of action to be create
     * @return a semantic action
     * @throws SemanticInterpretationException if any exception occurs 
     */
    static public SemanticAction buildAction(CommunicativeActionProto act) throws SemanticInterpretationException {
        if (act.getActor() != null && act.getReceiverList() != null && act.getReceiverList().size() > 0) {
            if (act.getSurfaceContentElementNumber() == act.getSurfaceContentFormat().length) {
                boolean formatOK = true;
                int i = 0;
                while (formatOK && i<act.getSurfaceContentFormat().length) {
                    formatOK &= act.getSurfaceContentFormat()[i].isInstance(act.getSurfaceContentElement(i));
                    i++;
                }
                if (formatOK) {
                    if (act.getPerformative() != act.getSurfacePerformative()) {
                        act.setContent(act.getContentSize());
                    }
                    return act.doNewAction(act.getSurfaceContent());
                }
                //else {
                    throw new UnexpectedContentSIException(act.getPerformative(), act.getSurfaceContentFormatMessage(), act.getContent().toString());
                //}
            }
            //else {
                throw new UnexpectedContentSIException(act.getPerformative(),
                        act.getSurfaceContentFormat().length + " Content Expressions", String.valueOf(act.getContentElementNumber()));
            //}
        }
        //else {
            throw new SemanticInterpretationException("missing-parametter", SL.string("sender or receiver"));
        //}
    } // End of buildAction/1
    
    /**
     * @inheritDoc
     */
    public ContentNode parseContent(String content, String language) throws SemanticInterpretationException {
        return parseContent(content, language, this);
    }
    
    /**
     * Parse the content with the appropriate parser depending of the specified
     * language.
     * @param content the content to be parsed
     * @param language the language 
     * @param act the kind of action to be create
     * @return a content a ContentNode resulting of the parsing
     * @throws SemanticInterpretationException if any exception occurs
     */
    static public ContentNode parseContent(String content, String language, CommunicativeActionProto act) throws SemanticInterpretationException { 
        try {
            ContentParser contentParser = act.getSemanticCapabilities().getContentParser(language);
            if (contentParser == null) throw new SemanticInterpretationException("unknown-language", SL.string(language));
			return (ContentNode)contentParser.parseContent(content);
        } 
        catch (ParseContentException pce) {
            throw new SemanticInterpretationException("cannot-parse-content", SL.string(content));
        }
    } // End of parseContent/3
    
    /********************************************************************************
     * METHODS TO OVERRIDE
     ********************************************************************************/
    /**
     * @inheritDoc
     */
    public abstract SemanticAction doNewAction(Content surfaceContent) throws SemanticInterpretationException;
    
    /**
     * @inheritDoc
     */
    public abstract boolean setFeaturesFromRationalEffect(MatchResult rationalEffectMatching) throws Exception;

    /***********************************************************************
     * OTHER METHODS
     ***********************************************************************/
    
    /**
     * Returns a <code>PrimitiveBehaviour</code>.
     * @return a primitive behaviour 
     */
    @Override
	public Behaviour computeBehaviour() {
        return new CommunicativeActionBehaviour(this);
    }
    
    /**
     * Returns the action expression representation of this action.
     * @param surface true if the surface content should be used, false if not
     * @return an action expression representing the semantic action
     * @throws SemanticInterpretationException if any exception occurs
     */
    public ActionExpression toActionExpression(boolean surface) {// throws SemanticInterpretationException {
        return toActionExpression(this, surface, null, (ListOfTerm)null, null);
    }
    
    /**
     * Returns the action expression representation of a communicative action.
     * @param act the CommunicativeActionProto instance to transform into an action expression
     * @param surface if true, the surface performative and content will be used
     *                to create the action expression. Otherwise, the primitive
     *                performative and content will be used.
     * @param sender if not null, replace the actual sender of act with this value
     * @param receivers if not null, replace the actual list of receivers of act with
     *                  this value (must be a list of terms representing agents)
     * @param content if not null, replace the actual content of act with this value
     * @return an action expression representing the given CommunicativeActionProto
     * @throws SemanticInterpretationException if any exception occurs
     */
    static public ActionExpression toActionExpression(CommunicativeActionProto act, boolean surface, Term sender, ListOfTerm receivers, Content content) 
    //throws SemanticInterpretationException 
    {
        ActionExpression result = (ActionExpression)COMMUNICATIVE_ACTION_PATTERN.getClone();
        try {
            SL.set(result, "performative", (surface ?
                    SL.symbol(ACLMessage.getPerformative(act.getSurfacePerformative()))
                    : SL.symbol(ACLMessage.getPerformative(act.getPerformative()))));
            SL.set(result, "sender", sender == null ? act.getActor() : sender);
            SL.set(result, "receiver", new TermSetNode(receivers == null ? act.getReceiverList() : receivers));
            // FIXME following line replaced by TM, 2 oct 2007
//            SL.set(result, "content", new StringConstantNode(
//                    act.getSemanticCapabilities().getContentParser(act.getLanguage()).unparseContent(
//                            content == null ? (surface ? act.getSurfaceContent() : act.getContent()) : content)));
            SL.set(result, "content", content == null ? (surface ? act.getSurfaceContent() : act.getContent()) : content);
            if (act.getLanguage() != null) SL.set(result, "language", SL.word(act.getLanguage()));
            if (act.getOntology() != null) SL.set(result, "ontology", SL.word(act.getOntology()));
            if (act.getEncoding() != null) SL.set(result, "encoding", SL.word(act.getEncoding()));
            if (act.getConversationId() != null) SL.set(result, "conversation-id", SL.word(act.getConversationId()));
            if (act.getProtocol() != null) SL.set(result, "protocol", SL.word(act.getProtocol()));
            if (act.getReplyWith() != null) SL.set(result, "reply-with", SL.word(act.getReplyWith()));
            if (act.getInReplyTo() != null) SL.set(result, "in-reply-to", SL.word(act.getInReplyTo()));
            if (act.getReplyBy() != null) SL.set(result, "reply-by", SL.date(act.getReplyBy()));
            if ((act.getReplyToList() != null) && (act.getReplyToList().size() > 0)) SL.set(result, "reply-to", new TermSetNode(act.getReplyToList())); 
            SL.substituteMetaReferences(result);
            SL.removeOptionalParameter(result);
            //result.sm_simplified_term(result);
            result.sm_action(act);
            return result;
        }
        // FIXME Same as previous fixme within this method
//        catch (UnparseContentException uce) {
//            throw new SemanticInterpretationException("cannot-unparse-content", new StringConstantNode(act.getSurfaceContent().toSLString()));
//        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Returns the action expression representation of a communicative action.
     * @param act the CommunicativeActionProto instance to transform into an action expression
     * @param surface if true, the surface performative and content will be used
     *                to create the action expression. Otherwise, the primitive
     *                performative and content will be used.
     * @param sender if not null, replace the actual sender of act with this value
     * @param receiver if not null, replace the actual list of receivers of act with
     *                 a unique agent identified by this value
     * @param content if not null, replace the actual content of act with this value
     * @return an action expression representing the given CommunicativeActionProto
     * @throws SemanticInterpretationException if any exception occurs
     */
    static public ActionExpression toActionExpression(CommunicativeActionProto act, boolean surface, Term sender, Term receiver, Content content) { //throws SemanticInterpretationException {
    	return toActionExpression(act, surface, sender, new ListOfTerm(new Term[] {receiver}), content);
    }
    
    /**
     * Returns the action expression representation of a communicative action.
     * @param act the CommunicativeActionProto instance to transform into an action expression
     * @param surface if true, the surface performative and content will be used
     *                to create the action expression. Otherwise, the primitive
     *                performative and content will be used.
     * @return an action expression representing the given CommunicativeActionProto
     * @throws SemanticInterpretationException if any exception occurs
     */
    static public ActionExpression toActionExpression(CommunicativeActionProto act, boolean surface) 
    //throws SemanticInterpretationException 
    {
    	return toActionExpression(act, surface, null, (ListOfTerm)null, null);
    }

    /**
     * @inheritDoc
     */
    @Override
	public ActionExpression toActionExpression() throws SemanticInterpretationException {
        return toActionExpression(true);
    }
    
    /**
     * @inheritDoc 
     */
    public ACLMessage toAclMessage() throws UnparseContentException {
        return toAclMessage(this);
    }
    
    /**
     * Builds an ACL Message from the specified communicative action 
     * @param act a communicative action
     * @return an ACL message
     * @throws UnparseContentException if an error of parsing occurs 
     */
    static public ACLMessage toAclMessage(CommunicativeActionProto act) throws UnparseContentException {
        ACLMessage result = new ACLMessage(act.getSurfacePerformative());
        result.setSender(Tools.term2AID(act.getActor()));
        for (int i = 0 ; i < act.getReceiverList().size() ; i++) {
            result.addReceiver(Tools.term2AID(act.getReceiverList().element(i)));
        }
        result.setContent(act.getSemanticCapabilities().getContentParser(act.getLanguage()).
        		unparseContent(act.getSurfaceContent()));
        result.setLanguage(act.getLanguage() == null ? "fipa-sl" : act.getLanguage());
        result.setOntology(act.getOntology());
        result.setEncoding(act.getEncoding());
        result.setConversationId(act.getConversationId());
        result.setProtocol(act.getProtocol());
        result.setReplyWith(act.getReplyWith());
        result.setInReplyTo(act.getInReplyTo());
        result.setReplyByDate(act.getReplyBy());
        if (act.getReplyToList() != null) {
            for (int i = 0 ; i < act.getReplyToList().size() ; i++) {
                result.addReplyTo(Tools.term2AID(act.getReplyToList().element(i)));
            }
        }
        return result;
    }
    
    /**
     * Tests if two actions are semantically equals, i.e. they have the same
     * performative, the same author, the same receiver list and the same 
     * content.
     * @param action an action to be compared.
     * @return true if this action is semanticalyy equals to the action given in
     * parameter, false if not.
     */
    public boolean semanticallyEquals(SemanticAction action) {
        if (action instanceof CommunicativeActionImpl) {
            CommunicativeActionImpl communicativeAction = (CommunicativeActionImpl)action;
            return (performative == communicativeAction.performative)
                    && (getAuthor().equals(communicativeAction.getAuthor()))
                    && (receiverList.equals(communicativeAction.receiverList))
                    && (content.equals(communicativeAction.content));
        }
        return false;
    }
    /**
     * Tests if two actions are syntactically equals, i.e. they have the same
     * performative, the same author, the same receiver list, 
     * and if all the parameters are equal. If the specified boolean is true, 
     * the content should be the same too.
     * @param action an action to be compared
     * @param includeContent true if the contents should be compared
     * @return true if this action is syntactically equals to the action given 
     * in parameter, false if not.
     */
    public boolean syntacticallyEquals(SemanticAction action, boolean includeContent) {
        if (action instanceof CommunicativeActionImpl) {
            CommunicativeActionImpl communicativeAction = (CommunicativeActionImpl)action;
            return (performative == communicativeAction.performative)
                    && (getAuthor().equals(communicativeAction.getAuthor()))
                    && receiverList.equals(communicativeAction.receiverList)
                    && (!includeContent || content.equals(communicativeAction.content))
                    && (language == null ? communicativeAction.language == null : language.equals(communicativeAction.language))
                    && (ontology == null ? communicativeAction.ontology == null : ontology.equals(communicativeAction.ontology))
                    && (encoding == null ? communicativeAction.encoding == null : encoding.equals(communicativeAction.encoding))
                    && (protocol == null ? communicativeAction.protocol == null : protocol.equals(communicativeAction.protocol))
                    && (conversationId == null ? communicativeAction.conversationId == null : conversationId.equals(communicativeAction.conversationId))
                    && (replyWith == null ? communicativeAction.replyWith == null : replyWith.equals(communicativeAction.replyWith))
                    && (inReplyTo == null ? communicativeAction.inReplyTo == null : inReplyTo.equals(communicativeAction.inReplyTo))
                    && (replyBy == null ? communicativeAction.replyBy == null : replyBy.equals(communicativeAction.replyBy))
                    && (replyToList == null ? communicativeAction.replyToList == null : replyToList.equals(communicativeAction.replyToList));
        }
        return false;
    }
    
    /**
     * True if this action is a reply to the action given in parameter,
     * false if not 
     * @param action an action to be compared
     * @return true if this action is a reply to the action given in parameter,
     * false if not.
     */
    public boolean isReplyTo(CommunicativeActionImpl action) {
        return receiverList.contains(action.getAuthor())
                && (replyToList == null ? getAuthor().equals(action.getReceiver()) : replyToList.equals(action.receiverList))
                && (language == null ? action.language == null : language.equals(action.language))
                && (ontology == null ? action.ontology == null : ontology.equals(action.ontology))
                && (encoding == null ? action.encoding == null : encoding.equals(action.encoding))
                && (protocol == null ? action.protocol == null : protocol.equals(action.protocol))
                && (conversationId == null ? action.conversationId == null : conversationId.equals(action.conversationId))
                && (replyWith == null ? action.inReplyTo == null : replyWith.equals(action.inReplyTo));
    }
    
    /**
     * Checks if two CommunicativeAction are equal, by checking the equality
     * of their performatives (i.e. their classes), their senders, their
     * receivers and their contents.
     * Other parameters are not taken into account.
     * @param o an object
     * @return true if two CommunicativeAction are equal, false if not or if the
     * given parameter is not a CommunicativeAction.
     */
    @Override
	public boolean equals(Object o) {
        if (o instanceof SemanticAction) {
            return semanticallyEquals((SemanticAction)o);
        }
        return false;
    } // End of equals/1

    /***********************************************************************
     * PUBLIC GETTERS AND SETTERS
     ***********************************************************************/

    /**
     * @return Returns the performative.
     */
    public int getPerformative() {
        return (performative < -1 ? surfacePerformative : performative);
    }

    /**
     * @param performative The performative to set.
     */
    public void setPerformative(int performative) {
        this.performative = performative;
    }

    /**
     * Returns the surface Performative.
     * @return Returns the surface Performative.
     */
    public int getSurfacePerformative() {
        return surfacePerformative;
    }
    
    /**
     * Sets the surface performative
     * @param surfacePerformative The surfacePerformative to set.
     */
    public void setSurfacePerformative(int surfacePerformative) {
        this.surfacePerformative = surfacePerformative;
    }
    /**
	 * @return Returns the receiverList.
	 */
	public ListOfTerm getReceiverList() {
		return receiverList;
	} 

	/**
	 * @return Returns the fisrt receiver of the receiverList.
	 */
	public Term getReceiver() {
		return receiverList.first();
	} 

    /**
     * Set a unique receiver to this communicative action
     * @param receiver
     */
    public void setReceiver(Term receiver) {
        setReceiverList(receiver != null ? new ListOfTerm(new Term[] {receiver}) : new ListOfTerm());
    }
    
	/**
     * Sets a list of receivers.
	 * @param receiverList The receiverList to set.
	 */
	public void setReceiverList(ListOfTerm receiverList) {
		this.receiverList = receiverList;    	
	} 
    
    /**
     * Sets a list of receivers.
     * @see CommunicativeAction#setReceiverList(TermSetNode)
     */
    public void setReceiverList(TermSetNode receiverList) {
        this.receiverList = (receiverList == null ? null : receiverList.as_terms());
    }


    /**
     * Sets the content with the given content
     * @param content The content to set.
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
     * @inheritDoc
     */
    public void setContent(int size) {
       content = new ContentNode();
       content.setContentElements(size);
    }
    
    /**
     * @inheritDoc
     */
    public void setContentElement(int i, Node element) {
        getContent().setContentElement(i, element);
    }

	/**
	 * @return Returns the content.
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
     * @return the number of elements in the content
     */
    public int getContentElementNumber() {
        return getContent().contentElementNumber();
    }
    
    /**
     * Sets the surface content with a new Content with the given size
     * @param size size of the content
     */
    public void setSurfaceContent(int size) {
        this.surfaceContent = new ContentNode(new ListOfContentExpression());
        this.surfaceContent.setContentElements(size);
    }
    
    /**
     * Sets the surface content with a clone of the given content.
     * @param content a content 
     */
    public void setSurfaceContent(Content content) {
        this.surfaceContent = (Content)content.getClone();
    }
    /**
     * Sets the surface content with an element and an index.
     * @param i an index
     * @param element a element of the content
     */
    public void setSurfaceContentElement(int i, Node element) {
        surfaceContent.setContentElement(i, element);
    }
    
    /**
     * @return the surface content
     */
    public Content getSurfaceContent() {
        return surfaceContent;
    }
    
    /**
     * @param i an index
     * @return the element of the surface content at the specifed index
     */
    public Node getSurfaceContentElement(int i) {
        return surfaceContent.getContentElement(i);
    }
    /**
     * @return the number of element in the surface content
     */
    public int getSurfaceContentElementNumber() {
        return surfaceContent.contentElementNumber();
    }

    
    /**
     * @return Returns the conversationId.
     */
    public String getConversationId() {
        return conversationId;
    }
    /**
     * @param conversationId The conversationId to set.
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    /**
     * Sets the conversation-id value with the value given in parameter
     * @param conversationId the conversation-id
     */
    public void setConversationId(WordConstantNode conversationId) {
        this.conversationId = (conversationId == null ? null : conversationId.stringValue());
    }
    /**
     * @return Returns the encoding.
     */
    public String getEncoding() {
        return encoding;
    }
    /**
     * @param encoding The encoding to set.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    /**
     * Sets the encoding value with the value given in parameter
     * @param encoding the encoding
     */
    public void setEncoding(WordConstantNode encoding) {
        this.encoding = (encoding == null ? null : encoding.stringValue());
    }
    /**
     * @return Returns the inReplyTo.
     */
    public String getInReplyTo() {
        return inReplyTo;
    }
    /**
     * @param inReplyTo The inReplyTo to set.
     */
    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }
    /**
     * Sets the in-reply-to field with the value given in parameter
     * @param inReplyTo new value of in-reply-to 
     */
    public void setInReplyTo(WordConstantNode inReplyTo) {
        this.inReplyTo = (inReplyTo == null ? null : inReplyTo.stringValue());
    }
    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return language;
    }
    /**
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        this.language = language;
    }
    /**
     * Sets the language value with the value given in parameter
     * @param language the language
     */
    public void setLanguage(WordConstantNode language) {
        this.language = (language == null ? null : language.stringValue());
    }
    /**
     * @return Returns the ontology.
     */
    public String getOntology() {
        return ontology;
    }
    /**
     * @param ontology The ontology to set.
     */
    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    /**
     * Sets the ontology value with the value given in parameter
     * @param ontology the ontology
     */
    public void setOntology(WordConstantNode ontology) {
        this.ontology = (ontology == null ? null : ontology.stringValue());
    }
    /**
     * @return Returns the protocol.
     */
    public String getProtocol() {
        return protocol;
    }
    /**
     * @param protocol The protocol to set.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    /**
     * Sets the protocol value with the value given in parameter
     * @param protocol the protocol
     */
    public void setProtocol(WordConstantNode protocol) {
        this.protocol = (protocol == null ? null : protocol.stringValue());
    }
    /**
     * @return Returns the replyBy.
     */
    public Date getReplyBy() {
        return replyBy;
    }
    /**
     * @param replyBy The replyBy to set.
     */
    public void setReplyBy(Date replyBy) {
        this.replyBy = replyBy;
    }
    /**
     * Sets the replyBy value with the value given in parameter
     * @param replyBy the replyBy
     */
    public void setReplyBy(DateTimeConstantNode replyBy) {
        this.replyBy = (replyBy == null ? null : replyBy.lx_value());
    }
    /**
     * @return Returns the replyToList.
     */
    public ListOfTerm getReplyToList() {
        return replyToList;
    }
    /**
     * @param replyToList The replyToList to set.
     */
    public void setReplyToList(ListOfTerm replyToList) {
        this.replyToList = replyToList;
    }
    /**
     * Sets the replyToList value with the value given in parameter
     * @param replyToList the replyToList
     */
    public void setReplyToList(TermSetNode replyToList) {
        this.replyToList = (replyToList == null ? null : replyToList.as_terms());
    }
    /**
     * @return Returns the replyWith.
     */
    public String getReplyWith() {
        return replyWith;
    }
    /**
     * @param replyWith The replyWith to set.
     */
    public void setReplyWith(String replyWith) {
        this.replyWith = replyWith;
    }
    /**
     * Sets the reply-with value with the value given in parameter
     * @param replyWith the reply-with
     */
    public void setReplyWith(WordConstantNode replyWith) {
        this.replyWith = (replyWith == null ? null : replyWith.stringValue());
    }
    
    
    /**
     * @return Returns the myActionExpressionPattern.
     */
    public Term getMyActionExpressionPattern() {
        return myActionExpressionPattern;
    }

	/**
     * @return the semanticActiontable where is stored this action
     */
    public SemanticActionTable getSemanticActionTable() {
        return getSemanticCapabilities().getMySemanticActionTable();
    }
    
    /**
     * @return the list of classes of the surface content
     */
    
	public Class[] getSurfaceContentFormat() {
        return surfaceContentFormat;
    }
    
    /**
     * @return the size of the content
     */
    public int getContentSize() {
        return contentSize;
    }
    
    /**
     * @return the surface content format message
     */
    public String getSurfaceContentFormatMessage() {
        return surfaceContentFormatMessage;
    }
    
    /**
     * @return the rational effect recognition pattern
     */
    public Formula getRationalEffectRecognitionPattern() {
        return rationalEffectRecognitionPattern;
    }
} // End of class CommunicativeAction
