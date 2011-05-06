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
 * created on 12 mars 07 by Thierry Martinez
 */

package jade.semantics.actions;

import jade.semantics.actions.performatives.AcceptProposal;
import jade.semantics.actions.performatives.Agree;
import jade.semantics.actions.performatives.CallForProposal;
import jade.semantics.actions.performatives.Cancel;
import jade.semantics.actions.performatives.Confirm;
import jade.semantics.actions.performatives.Disconfirm;
import jade.semantics.actions.performatives.Failure;
import jade.semantics.actions.performatives.Inform;
import jade.semantics.actions.performatives.NotUnderstood;
import jade.semantics.actions.performatives.Propose;
import jade.semantics.actions.performatives.QueryIf;
import jade.semantics.actions.performatives.QueryRef;
import jade.semantics.actions.performatives.Refuse;
import jade.semantics.actions.performatives.RejectProposal;
import jade.semantics.actions.performatives.Request;
import jade.semantics.actions.performatives.RequestWhen;
import jade.semantics.actions.performatives.RequestWhenever;
import jade.semantics.actions.performatives.Subscribe;
import jade.semantics.interpreter.SemanticInterpretationException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

public class CommunicativeActionFactory {
	
	   protected SemanticActionTable semanticActionTable;

	   protected Term agentName;
	   
	   /**
	    * Build a communicative action factory  
	    * @param table the semantic action table that holds the communicative actions
	    * @param name the name of the sender agent ; that is myself
	    */
	    public CommunicativeActionFactory(SemanticActionTable table, Term name) {
		   semanticActionTable = table;
		   agentName = name;
	    }
	   
	   /**
	    * Creates a communicative action: AcceptProposal. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param condition a formula (second element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action AcceptProposal
	    */
	    public AcceptProposal createAcceptProposal(ActionExpression action, Formula condition, Term receiver) {
	        return createAcceptProposal(action, condition, new Term[] {receiver});  
	    }
		
	   /**
	    * Creates a communicative action: AcceptProposal. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param condition a formula (second element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action AcceptProposal
	    */
	    public AcceptProposal createAcceptProposal(ActionExpression action, Formula condition, Term[] receivers) {
	       AcceptProposal prototype = 
			   (AcceptProposal)semanticActionTable.getSemanticActionPrototype(AcceptProposal.class);
	       try {
	           Content content = new ContentNode();
	           content.setContentElements(2);
	           content.setContentElement(0, action);
	           content.setContentElement(1, condition);            
	           return (AcceptProposal)prototype.newAction(agentName, 
	                   new ListOfTerm(receivers),
	                   content, null);
	       }
	       catch(SemanticInterpretationException sie) {
	           sie.printStackTrace();
	       }
	       return null;
		} 

	    /**
	    * Creates a communicative action: Agree. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param condition a formula (second element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action Agree
	    */
	    public Agree createAgree(ActionExpression action, Formula condition, Term receiver) {
	        return createAgree(action, condition, new Term[] {receiver});
	    }

	   /**
	    * Creates a communicative action: Agree. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param condition a formula (second element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action Agree
	    */  
	    public Agree createAgree(ActionExpression action, Formula condition, Term[] receivers) {  
		      Agree prototype = 
				   (Agree)semanticActionTable.getSemanticActionPrototype(Agree.class);
				
	       try {
	           Content content = new ContentNode();
	           content.setContentElements(2);
	           content.setContentElement(0, action);
	           content.setContentElement(1, condition);            
	           return (Agree)prototype.newAction(agentName, 
	                   new ListOfTerm(receivers),
	                   content, null);
	       }
	       catch(SemanticInterpretationException sie) {
	           sie.printStackTrace();
	       }
	       return null;
		} 
	   
	   /**
	    * Creates a communicative action: Cancel. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (element of the content of this 
	    * kind of message)
	    * @param receiver a receiver
	    * @return a communicative action Cancel
	    */
	    public Cancel createCancel(ActionExpression action, Term receiver) {
	        return createCancel(action, new Term[] {receiver}); 
	    }

	   /**
	    * Creates a communicative action: Cancel. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (element of the content of this 
	    * kind of message)
	    * @param receivers list of receivers
	    * @return a communicative action Cancel
	    */  
	    public Cancel createCancel(ActionExpression action, Term[] receivers) {
		   Cancel prototype = 
				   (Cancel)semanticActionTable.getSemanticActionPrototype(Cancel.class);
	       try {
	           Content content = new ContentNode();
	           content.setContentElements(1);
	           content.setContentElement(0, action);
	           return (Cancel)prototype.newAction(agentName, 
	                   new ListOfTerm(receivers),
	                   content, null);
	       }
	       catch(SemanticInterpretationException sie) {
	           sie.printStackTrace();
	       }
	       return null;
		} 
	      
	   /**
	    * Creates a communicative action: CFP. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param ire an Identifying Expression (second element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action CFP
	    */
	    public CallForProposal createCFP(ActionExpression action, IdentifyingExpression ire, Term receiver) {
	        return createCFP(action, ire, new Term[] {receiver} );  
	    }

	   /**
	    * Creates a communicative action: CFP. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param ire an Identifying Expression (second element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action CFP
	    */  
	    public CallForProposal createCFP(ActionExpression action, IdentifyingExpression ire, Term[] receivers) {
			CallForProposal prototype = 
				   (CallForProposal)semanticActionTable.getSemanticActionPrototype(CallForProposal.class);

			try {
	           Content cfpContent = new ContentNode();
	           cfpContent.setContentElements(2);
	           cfpContent.setContentElement(0, action);
	           cfpContent.setContentElement(1, ire);            
	           return (CallForProposal)prototype.newAction(agentName, 
	                   new ListOfTerm(receivers),
	                   cfpContent, null);
	       }
	       catch(SemanticInterpretationException sie) {
	           sie.printStackTrace();
	       }
	       return null;
	   } 
	   
	   /**
	    * Creates a communicative action: Confirm. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param formula a formula (element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action Confirm
	    */
	    public Confirm createConfirm(Formula formula, Term receiver) {
	        return createConfirm(formula, new Term[] {receiver});   
	    }

	   /**
	    * Creates a communicative action: Confirm. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param formula a formula (element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action Confirm
	    */  
	    public Confirm createConfirm(Formula formula, Term[] receivers) {
			Confirm prototype = 
				   (Confirm)semanticActionTable.getSemanticActionPrototype(Confirm.class);
	       try {
	           Content content = new ContentNode();
	           content.setContentElements(1);
	           content.setContentElement(0, formula);
	           return (Confirm)prototype.newAction(agentName, 
	                   new ListOfTerm(receivers),
	                   content, null);
	       }
	       catch(SemanticInterpretationException sie) {
	           sie.printStackTrace();
	       }
	       return null;
		} 
	   
	   /**
	    * Creates a communicative action: Disconfirm. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param formula a formula (element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action Disconfirm
	    */
	    public Disconfirm createDisconfirm(Formula formula, Term receiver) {
	        return createDisconfirm(formula, new Term[] {receiver});
	    }

	   /**
	    * Creates a communicative action: Disconfirm. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param formula a formula (element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action Disconfirm
	    */  
	    public Disconfirm createDisconfirm(Formula formula, Term[] receivers) {
			Disconfirm prototype = 
				   (Disconfirm)semanticActionTable.getSemanticActionPrototype(Disconfirm.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(1);
	            content.setContentElement(0, formula);
	            return (Disconfirm)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	    * Creates a communicative action: Failure. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param formula a formula (second element of the content of this kind of
	    * message)
	    * @param receiver a receievr
	    * @return a communicative action Failure
	     */
	     public Failure createFailure(ActionExpression action, Formula formula, Term receiver) {
	        return createFailure(action, formula, new Term[] {receiver});
	     }

	    /**
	    * Creates a communicative action: Failure. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param formula a formula (second element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action Failure
	     */  
	     public Failure createFailure(ActionExpression action, Formula formula, Term[] receivers) {
			 Failure prototype = 
				 (Failure)semanticActionTable.getSemanticActionPrototype(Failure.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(2);
	            content.setContentElement(0, action);
	            content.setContentElement(1, formula);
	            return (Failure)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
		 } 
	    
	    /**
	    * Creates a communicative action: Inform. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param formula a formula (element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action CFP
	     */
		public Inform createInform(Formula formula, Term receiver) {
	        return createInform(formula, new Term[] {receiver});    
		}

	    /**
	    * Creates a communicative action: Inform. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param formula a formula (element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action CFP
	     */ 
	    public Inform createInform(Formula formula, Term[] receivers) {
			Inform prototype = 
				(Inform)semanticActionTable.getSemanticActionPrototype(Inform.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(1);
	            content.setContentElement(0, formula);
	            return (Inform)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	    * Creates a communicative action: NotUnderstood. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param reason a formula (second element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action NotUnderstood
	     */
	    public NotUnderstood createNotUnderstood(ActionExpression action, Formula reason, Term receiver) {
	        return createNotUnderstood(action, reason, new Term[] {receiver});
	    }

	    /**
	    * Creates a communicative action: NotUnderstood. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param reason a formula (second element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action NotUnderstood
	     */ 
	    public NotUnderstood createNotUnderstood(ActionExpression action, Formula reason, Term[] receivers) {
			NotUnderstood prototype = 
				(NotUnderstood)semanticActionTable.getSemanticActionPrototype(NotUnderstood.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(2);
	            content.setContentElement(0, action);
	            content.setContentElement(1, reason);
	            return (NotUnderstood)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	    * Creates a communicative action: Propose. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param condition a formula (second element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action Propose
	     */
	    public Propose createPropose(ActionExpression action, Formula condition, Term receiver) {
	        return createPropose(action, condition, new Term[] {receiver});
	    }

	    /**
	    * Creates a communicative action: Propose. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param condition a formula (second element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action Propose
	     */ 
	    public Propose createPropose(ActionExpression action, Formula condition, Term[] receivers) {
			Propose prototype = 
				(Propose)semanticActionTable.getSemanticActionPrototype(Propose.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(2);
	            content.setContentElement(0, action);
	            content.setContentElement(1, condition);
	            return (Propose)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	    * Creates a communicative action: QueryIf. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param formula a formula (element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action QueryIf
	     */
	    public QueryIf createQueryIf(Formula formula, Term receiver) {
	        return createQueryIf( formula, new Term[] {receiver});
	    }

	    /**
	    * Creates a communicative action: QueryIf. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param formula a formula (element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action QueryIf
	     */
	    public QueryIf createQueryIf(Formula formula, Term[] receivers) {
			QueryIf prototype = 
				(QueryIf)semanticActionTable.getSemanticActionPrototype(QueryIf.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(1);
	            content.setContentElement(0, formula);
	            return (QueryIf)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	    * Creates a communicative action: QueryRef. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param ire an Identifying Expression (element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action QueryRef
	     */
	    public QueryRef createQueryRef(IdentifyingExpression ire, Term receiver) {
	        return createQueryRef( ire, new Term[] {receiver});
	    }
		
	    /**
	    * Creates a communicative action: QueryRef. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param ire an Identifying Expression (element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action QueryRef
	     */ 
	    public QueryRef createQueryRef(IdentifyingExpression ire, Term[] receivers) {
			QueryRef prototype = 
				(QueryRef)semanticActionTable.getSemanticActionPrototype(QueryRef.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(1);
	            content.setContentElement(0, ire);
	            return (QueryRef)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	        
	    /**
	    * Creates a communicative action: Refuse. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param reason a formula (second element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action Refuse
	     */
	    public Refuse createRefuse(ActionExpression action, Formula reason, Term receiver) {
	        return createRefuse(action, reason, new Term[] {receiver});
	    }

	    /**
	    * Creates a communicative action: Refuse. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param reason a formula (second element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action Refuse
	     */ 
	    public Refuse createRefuse(ActionExpression action, Formula reason, Term[] receivers) {
			Refuse prototype = 
				(Refuse)semanticActionTable.getSemanticActionPrototype(Refuse.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(2);
	            content.setContentElement(0, action);
	            content.setContentElement(1, reason);
	            return (Refuse)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	    * Creates a communicative action: RejectProposal. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param condition a formula (second element of the content of this kind of
	    * message)
	    * @param reason a formula (third element of the content of this kind of
	    * message)
	    * @param receiver a receiver
	    * @return a communicative action RejectProposal
	     */
	    public RejectProposal createRejectProposal(ActionExpression action, Formula condition, Formula reason, Term receiver) {
	        return createRejectProposal(action, condition, reason, new Term[] {receiver});
	    }

	    /**
	    * Creates a communicative action: RejectProposal. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (first element of the content of this 
	    * kind of message)
	    * @param condition a formula (second element of the content of this kind of
	    * message)
	    * @param reason a formula (third element of the content of this kind of
	    * message)
	    * @param receivers list of receivers
	    * @return a communicative action RejectProposal
	     */ 
	    public RejectProposal createRejectProposal(ActionExpression action, Formula condition, Formula reason, Term[] receivers) {
			RejectProposal prototype = 
				(RejectProposal)semanticActionTable.getSemanticActionPrototype(RejectProposal.class);
	        try {
	            Content rejectProposalContent = new ContentNode();
	            rejectProposalContent.setContentElements(3);
	            rejectProposalContent.setContentElement(0, action);
	            rejectProposalContent.setContentElement(1, condition);
	            rejectProposalContent.setContentElement(2, reason);
	            return (RejectProposal)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    rejectProposalContent,
	                    null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	    * Creates a communicative action: Request. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (element of the content of this 
	    * kind of message)
	    * @param receiver a receiver
	    * @return a communicative action Request
	     */
	    public Request createRequest(ActionExpression action, Term receiver) {
	        return createRequest(action, new Term[] {receiver});    
	    }   

	    /**
	    * Creates a communicative action: Request. This method should be use 
	    * only to send this kind of ACL Message.
	    * @param action an Action Expression (element of the content of this 
	    * kind of message)
	    * @param receivers list of receivers
	    * @return a communicative action Request
	     */ 
	    public Request createRequest(ActionExpression action, Term[] receivers) {
			Request prototype = 
				(Request)semanticActionTable.getSemanticActionPrototype(Request.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(1);
	            content.setContentElement(0, action);
	            return (Request)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	     /**
	     * Creates a communicative action: RequestWhen. This method should be use 
	     * only to send this kind of ACL Message.
	     * @param action an Action Expression (first element of the content of this 
	     * kind of message)
	     * @param condition a formula (second element of the content of this kind of
	     * message)
	     * @param receiver a receiver
	     * @return a communicative action RequestWhen
	     */
	    public RequestWhen createRequestWhen(ActionExpression action, Formula condition, Term receiver) {
	        return createRequestWhen(action, condition, new Term[] {receiver});
	    }

	    /**
	     * Creates a communicative action: RequestWhen. This method should be use 
	     * only to send this kind of ACL Message.
	     * @param action an Action Expression (first element of the content of this 
	     * kind of message)
	     * @param condition a formula (second element of the content of this kind of
	     * message)
	     * @param receivers list of receivers
	     * @return a communicative action RequestWhen
	     */ 
	    public RequestWhen createRequestWhen(ActionExpression action, Formula condition, Term[] receivers) {
			RequestWhen prototype = 
				(RequestWhen)semanticActionTable.getSemanticActionPrototype(RequestWhen.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(2);
	            content.setContentElement(0, action);
	            content.setContentElement(1, condition);
	            return (RequestWhen)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	     * Creates a communicative action: RequestWhenever. This method should be use 
	     * only to send this kind of ACL Message.
	     * @param action an Action Expression (first element of the content of this 
	     * kind of message)
	     * @param condition a formula (second element of the content of this kind of
	     * message)
	     * @param receiver a receiver
	     * @return a communicative action RequestWhenever
	     */
	    public RequestWhenever createRequestWhenever(ActionExpression action, Formula condition, Term receiver) {
	        return createRequestWhenever(action, condition, new Term[] {receiver});
	    }

	    /**
	     * Creates a communicative action: RequestWhenever. This method should be use 
	     * only to send this kind of ACL Message.
	     * @param action an Action Expression (first element of the content of this 
	     * kind of message)
	     * @param condition a formula (second element of the content of this kind of
	     * message)
	     * @param receivers list of receivers
	     * @return a communicative action RequestWhenever
	     */ 
	    public RequestWhenever createRequestWhenever(ActionExpression action, Formula condition, Term[] receivers) {
			RequestWhenever prototype = 
				(RequestWhenever)semanticActionTable.getSemanticActionPrototype(RequestWhenever.class);
	        try {
	            Content content = new ContentNode();
	            content.setContentElements(2);
	            content.setContentElement(0, action);
	            content.setContentElement(1, condition);
	            return (RequestWhenever)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    content, null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	     * Creates a communicative action: Subscribe. This method should be use 
	     * only to send this kind of ACL Message.
	     * @param ire an Identifying expression (element of the content of this kind of
	     * message)
	     * @param receiver a receiver
	     * @return a communicative action Subscribe
	     */
	    public Subscribe createSubscribe(IdentifyingExpression ire, Term receiver) {
	        return createSubscribe(ire, new Term[] {receiver});
	    }

	    /**
	     * Creates a communicative action: Subscribe. This method should be use 
	     * only to send this kind of ACL Message.
	     * @param ire an Identifying expression (element of the content of this kind of
	     * message)
	     * @param receivers list of receivers
	     * @return a communicative action Subscribe
	     */ 
	    public Subscribe createSubscribe(IdentifyingExpression ire, Term[] receivers) {
			Subscribe prototype = 
				(Subscribe)semanticActionTable.getSemanticActionPrototype(Subscribe.class);
	        try {
	            Content subscribeContent = new ContentNode();
	            subscribeContent.setContentElements(1);
	            subscribeContent.setContentElement(0, ire);
	            return (Subscribe)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    subscribeContent,
	                    null);
	        }
	        catch(SemanticInterpretationException sie) {
	            sie.printStackTrace();
	        }
	        return null;
	    }

	    /**
	     * Prototype of Unsubscribe action (Inform)
	     */
	    static Inform UNSUBSCRIBE_PROTOTYPE = null;
	    /**
	     * Content of an Unsubscribe action
	     */
	    static final Content UNSUBSCRIBE_CONTENT1 = SL.content("((not (I ??agent (done (action ??receiver (INFORM-REF :sender ??receiver :receiver (set ??agent) :content ??ire))))))");
	    
	    /**
	     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
	     * only to send this kind of ACL Message.
	     * @param ire an Identifying Expression (element of the content of this kind of
	     * message)
	     * @param receiver a receiver
	     * @return a communicative action Unsubscribe (Inform)
	     */
	    public Inform createUnsubscribe(IdentifyingExpression ire, Term receiver) {
	        return createUnsubscribe(ire, new Term[] {receiver});   
	    }

	    /**
	     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
	     * only to send this kind of ACL Message.
	     * @param ire an Identifying Expression (element of the content of this kind of
	     * message)
	     * @param receivers list of receivers
	     * @return a communicative action Unsubscribe (Inform)
	     */ 
	    public Inform createUnsubscribe(IdentifyingExpression ire, Term[] receivers) {
			Inform prototype = 
				(Inform)semanticActionTable.getSemanticActionPrototype(Inform.class);
	        try {
	            return ((Inform)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    (Content)SL.instantiate(UNSUBSCRIBE_CONTENT1, 
	                            "ire", SL.string("("+ire+")"),
	                            "agent", agentName,
	                            "receiver", receivers[0]),
	                            null));
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	     * Content of an Unsubscribe action
	     */
	    static final Content UNSUBSCRIBE_CONTENT2 = SL.content("((not (I ??agent (done ??action))))");
	    
	    /**
	     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
	     * only to send this kind of ACL Message.
	     * @param action an Action Expression (element of the content of this kind of
	     * message)
	     * @param receiver a receiver
	     * @return a communicative action Unsubscribe (Inform)
	     */
	    public Inform createUnsubscribe(ActionExpression action, Term receiver) {
	        return createUnsubscribe(action, new Term[] {receiver});    
	    }   

	    /**
	     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
	     * only to send this kind of ACL Message.
	     * @param action an Action Expression (element of the content of this kind of
	     * message)
	     * @param receivers list of receivers
	     * @return a communicative action Unsubscribe (Inform)
	     */ 
	    public Inform createUnsubscribe(ActionExpression action, Term[] receivers) {
			Inform prototype = 
				(Inform)semanticActionTable.getSemanticActionPrototype(Inform.class);
	        try {
	            return (Inform)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    (Content)SL.instantiate(UNSUBSCRIBE_CONTENT2, 
	                            "agent", agentName,
	                            "action", action), 
	                            null);
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    } 
	    
	    /**
	     * Content of an Unsubscribe action
	     */
	    static final Content UNSUBSCRIBE_CONTENT3 = SL.content("((or " +
	            "     (forall ?e (not (done ?e (not (B ??receiver ??property)))))" +
	            "     (or   (not (B ??receiver ??property))" +
	    "(not (I ??agent (done ??action))))))");
	    
	    /**
	     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
	     * only to send this kind of ACL Message.
	     * @param action an Action Expression (first element of the content of this kind of
	     * message)
	     * @param property a Formula (second element of the content of this kind of
	     * message)
	     * @param receiver a receiver
	     * @return a communicative action Unsubscribe (Inform)
	     */
	    public Inform createUnsubscribe(ActionExpression action, Formula property, Term receiver) {
	        return createUnsubscribe(action, property, new Term[] {receiver});  
	    }

	    /**
	     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
	     * only to send this kind of ACL Message.
	     * @param action an Action Expression (first element of the content of this kind of
	     * message)
	     * @param property a Formula (second element of the content of this kind of
	     * message)
	     * @param receivers list of receivers
	     * @return a communicative action Unsubscribe (Inform)
	     */ 
	    public Inform createUnsubscribe(ActionExpression action, Formula property, Term[] receivers) {
			Inform prototype = 
				(Inform)semanticActionTable.getSemanticActionPrototype(Inform.class);
	         try {
	            return (Inform)prototype.newAction(agentName, 
	                    new ListOfTerm(receivers),
	                    (Content)SL.instantiate(UNSUBSCRIBE_CONTENT3, 
	                            "agent", agentName,
	                            "property", property,
	                            "action", action,
	                            "receiver", receivers[0]), null);
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    } 

}
