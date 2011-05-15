/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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
* ManAgentCapabilities.java
* Created on 16 mai 2005
* Author : Vincent Pautret
*/
package jsademos.temperature;

import jade.core.AID;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.behaviours.SemanticBehaviour;
import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.IntentionTransferSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.kbase.observers.EventCreationObserver;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.MetaFormulaReferenceNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
* Capabilities of the Son Agent.
* @author Vincent Pautret - France Telecom
* @version Date: 2005/05/16 Revision: 1.0
*/
public class ManAgentCapabilities extends DefaultCapabilities {
   
   /**
    * AID of the mother
    */
   Term motherTerm = null;
      
   /*********************************************************************/
   /**                         CONSTRUCTOR                             **/
   /*********************************************************************/
   /**
    * Creates a new Capabilities
    */
   public ManAgentCapabilities() {
       super();
   } // End of ManAgentCapabilities/0
   
   /*********************************************************************/
   /**                         LOCAL METHODS                           **/
   /*********************************************************************/
   
   /**
    * Sets the AID of the mother agent
    * @param mother the mother agent name
    */
   public void setMotherFromAID(AID mother) {
       motherTerm = SL.term(mother.toString());
   } // End of ManAgentCapabilities/1
   
   /*********************************************************************/
   /**                         METHODS     **/
   /*********************************************************************/
   
   /**
    * Belief base setup.
    * Adds Observer on differents levels of temperature and initial belief
    * of the son agent.
    */
   @Override
public KBase setupKbase() {
       FilterKBase kb = (FilterKBase) super.setupKbase();
       try {
		   kb.addFiltersDefinition(new SingleNumValueDefinition("temperature"));
//		   kb.addFiltersDefinition(new NestedBeliefFilters() {
//			  /* (non-Javadoc)
//			 * @see jade.semantics.kbase.filter.NestedBeliefFilters#newInstance()
//			 */
//			public KBase newInstance(Term agent) {
//				FilterKBase result = new FilterKBaseImpl(agent, null, null);
//				result.addFiltersDefinition(new SingleNumValueDefinition("temperature"));
//				return result;
//			} 
//		   });
           
           //Adds Observers on temperature levels.
           //If the Son believes that the temperature is greater than (20,15,10,0), 
           //the son adopts the intention to put or take-off clothing
           
           kb.addObserver(new EventCreationObserver(kb,
                   SL.formula("(temperature_gt 20)"),
                   SL.formula("(and (I ??myself (not (wearing ??myself trousers)))" +
                                       "(I ??myself (not (wearing ??myself pullover)))" +
                                       "(I ??myself (not (wearing ??myself coat)))" +
                                       "(I ??myself (not (wearing ??myself cap))))"),
                   getSemanticInterpreterBehaviour()));
		   
           kb.addObserver(new EventCreationObserver(kb,
                   SL.formula("(temperature_gt 15.0)"),
                   SL.formula("(and (I ??myself (not (wearing ??myself pullover)))" +
                                       "(I ??myself (not (wearing ??myself coat)))" +
                                       "(I ??myself (not (wearing ??myself cap))))"),
                   getSemanticInterpreterBehaviour()));
           
           kb.addObserver(new EventCreationObserver(kb,
                   SL.formula("(temperature_gt 10)"),
                   SL.formula("(and (I ??myself (not (wearing ??myself coat)))" +
                                       "(I ??myself (not (wearing ??myself cap))))))"),
                   getSemanticInterpreterBehaviour()));
           
           kb.addObserver(new EventCreationObserver(kb,
                   SL.formula("(temperature_gt 0)"),
                   SL.formula("(I ??myself (not (wearing ??myself cap)))"),
                   getSemanticInterpreterBehaviour()));
           
           //Adds Observers on temperature levels.
           //If the Son believes that the temperature is lower (not greater) than (20,15,10,0), 
           //the son adopts the intention to put or take-off clothing

           kb.addObserver(new EventCreationObserver(kb,
                   SL.formula("(not (temperature_gt 20))"),
                   SL.formula("(I ??myself (wearing ??myself trousers))"),
                   getSemanticInterpreterBehaviour()));
           
           kb.addObserver(new EventCreationObserver(kb,
                   SL.formula("(not (temperature_gt 15))"),
                   SL.formula("(and (I ??myself (wearing ??myself pullover))" +
                                       "(I ??myself (wearing ??myself trousers)))"),
                   getSemanticInterpreterBehaviour()));
           
           kb.addObserver(new EventCreationObserver(kb,
                   SL.formula("(not (temperature_gt 10))"),
                   SL.formula("(and (I ??myself (wearing ??myself coat))" +
                                       "(I ??myself (wearing ??myself pullover))" +
                                       "(I ??myself (wearing ??myself trousers)))"),
                   getSemanticInterpreterBehaviour()));
           
           kb.addObserver(new EventCreationObserver(kb,
                   SL.formula("(not (temperature_gt 0))"),
                   SL.formula("(and (I ??myself (wearing ??myself cap))" +
                                       "(I ??myself (wearing ??myself coat))" +
                                       "(I ??myself (wearing ??myself pullover))" +
                                       "(I ??myself (wearing ??myself trousers)))"),
                   getSemanticInterpreterBehaviour()));
           
           // Initial belief setup 
           // -----------------------
           kb.addClosedPredicate(SL.formula("(wearing "+getAgentName()+" ??c)"));

		   Formula initialKPattern = 
               SL.formula("(not (wearing ??myself ??clothing))");
 
		   // In the initial state, the son does not wear clothing
           interpret(initialKPattern.instantiate("clothing", SL.word("cap")));
           interpret(initialKPattern.instantiate("clothing", SL.word("coat")));
           interpret(initialKPattern.instantiate("clothing", SL.word("trousers")));
           interpret(initialKPattern.instantiate("clothing", SL.word("pullover")));
       }
       catch(Exception e) {e.printStackTrace();}
	   
	   return kb;
   } 
   
   @Override
protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
	   SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();
	
	   // INTENTION TRANSFER SIPS
	   // Only adopt goals originated by the mother agent
	   table.addSemanticInterpretationPrinciple(new IntentionTransferSIPAdapter(this,
			   new MetaFormulaReferenceNode("phi"), new MetaTermReferenceNode("agent")) {
		   @Override
		protected ArrayList doApply(MatchResult matchFormula,
				                       MatchResult matchAgent,
				                       ArrayList acceptResult,
				                       ArrayList refuseResult,
				                       SemanticRepresentation sr) {
			   Term agent = matchAgent.term("agent");
			   if (agent.equals(motherTerm)) {
				   return acceptResult;
			   }
			   return refuseResult;
		   }
	   	});
	   
//	   table.addSemanticInterpretationPrinciple(new QueryRefPreparationSIPAdapter(this) {
//		
//		/* (non-Javadoc)
//		 * @see jade.semantics.interpreter.sips.adapters.QueryRefPreparationSIPAdapter#notify(jade.semantics.lang.sl.tools.MatchResult, jade.semantics.interpreter.SemanticRepresentation)
//		 */
//		protected void notify(MatchResult applyResult, SemanticRepresentation sr) {
//			System.out.println("########## QUERY PREPARE ON " + sr.getSLRepresentation());
//			super.notify(applyResult, sr);
//		}   
//		/* (non-Javadoc)
//		 * @see jade.semantics.interpreter.sips.adapters.QueryRefPreparationSIPAdapter#prepareQuery(jade.semantics.lang.sl.grammar.IdentifyingExpression, jade.semantics.lang.sl.grammar.Term)
//		 */
//		protected void prepareQuery(IdentifyingExpression query, Term agent) {
//			System.out.println("########## QUERY PREPARE ON " + query);
//			
//		} 
//	   });
	   
	   return table;
   }	
      
   /**
    * Action table setup
    * Adds ontological actions.
    */
   @Override
public SemanticActionTable setupSemanticActions() {
       // Adds the semantic actions
	   SemanticActionTable table = super.setupSemanticActions();
         
       // Ontological actions 
       // -------------------
       //Adds the action to put a clothing.
        //#DOTNET_EXCLUDE_BEGIN
       table.addSemanticAction(new OntologicalAction(this,
                "(PUT-ON :clothing ??clothing)",
               SL.formula("(wearing ??actor ??clothing)"),
               SL.formula("(not (wearing ??actor ??clothing))")) {
           @Override
		public void perform(OntoActionBehaviour behaviour) {
               ((ManAgent)getAgent()).putOn(getActionParameter("clothing").toString());
               behaviour.setState(SemanticBehaviour.SUCCESS);
           }
       });
        //#DOTNET_EXCLUDE_END
        /*#DOTNET_INCLUDE_BEGIN
        getMySemanticActionTable().addSemanticAction( new OntologicalAction1(getMySemanticActionTable(),
            "(PUT-ON :clothing ??clothing)",
            SL.formula("(wearing ??sender ??clothing)"),
            SL.formula("(not (wearing ??sender ??clothing))"), myAgent) );//Add the action to take off a clothing.
        #DOTNET_INCLUDE_END*/
       //Add the action to take off a clothing.
       
        //#DOTNET_EXCLUDE_BEGIN
	   table.addSemanticAction(new OntologicalAction(this,
               "(TAKE-OFF :clothing ??clothing)",
               SL.formula("(not (wearing ??actor ??clothing))"),
               SL.formula("(wearing ??actor ??clothing)")){
           @Override
		public void perform(OntoActionBehaviour behaviour) {
               ((ManAgent)getAgent()).takeOff(getActionParameter("clothing").toString());
               behaviour.setState(SemanticBehaviour.SUCCESS);
           }
       });
        //#DOTNET_EXCLUDE_END
        /*#DOTNET_INCLUDE_BEGIN
       getMySemanticActionTable().addSemanticAction(new OntologicalAction2(getMySemanticActionTable(),
               "(TAKE-OFF :clothing ??clothing)",
               SL.formula("(not (wearing ??sender ??clothing))"),
               SL.formula("(wearing ??sender ??clothing)"), myAgent));
        #DOTNET_INCLUDE_END*/
       //Adds the action to wait a given time.

        //#DOTNET_EXCLUDE_BEGIN
	   table.addSemanticAction(new OntologicalAction(this,
               "(WAIT :time ??time)",
               SL.formula("true"),
               SL.formula("true")){
           private long wakeupTime = -1, blockTime;

           @Override
		public void perform(OntoActionBehaviour behaviour) {
               switch (behaviour.getState()) {
               case SemanticBehaviour.START: {
                    if (wakeupTime == -1) {
                        wakeupTime = System.currentTimeMillis()+Long.parseLong((getActionParameter("time").toString()));
                    }
                 // in this state the behaviour blocks itself
                 blockTime = wakeupTime - System.currentTimeMillis();
                 if (blockTime > 0) 
                   behaviour.block(blockTime);
                 behaviour.setState(1000);
                 break;
               }
               case 1000: {
                 // in this state the behaviour can be restarted for two reasons
                 // 1. the timeout is elapsed (then the handler method is called 
                 //                            and the behaviour is definitively finished) 
                 // 2. a message has arrived for this agent (then it blocks again and
                 //                            the FSM remains in this state)
                 blockTime = wakeupTime - System.currentTimeMillis();
                 if (blockTime <= 0) {
                   // timeout is expired
                   behaviour.setState(SemanticBehaviour.SUCCESS);
                 } else 
                   behaviour.block(blockTime);
                 break;
               }
               default : {
                 behaviour.setState(SemanticBehaviour.EXECUTION_FAILURE);
                 break;
               }
               } // end of switch
           }
       });
        //#DOTNET_EXCLUDE_END
        /*#DOTNET_INCLUDE_BEGIN
       getMySemanticActionTable().addSemanticAction(new OntologicalAction3(getMySemanticActionTable(),
               "(WAIT :time ??time)",
               SL.formula("true"),
               SL.formula("true"), myAgent));
        #DOTNET_INCLUDE_END*/
	   
	   return table;
       
   } // End of setupSemanticActions/0
   
} // End of class ManAgentCapabilities

/*#DOTNET_INCLUDE_BEGIN
public class OntologicalAction1 extends OntologicalAction
{
    private SemanticAgent myAgent;

    public OntologicalAction1(SemanticActionTable table,
        String actionPattern, 
        Formula postconditionPattern,
        Formula preconditionPattern, SemanticAgent ag) 
    {
        super(table, actionPattern, postconditionPattern, preconditionPattern);
        myAgent = ag;
    }

    public void perform(OntoActionBehaviour behaviour) 
    {
        ((ManAgent)myAgent).putOn(getActionParameter("clothing").toString());
        behaviour.setState(SemanticBehaviour.SUCCESS);
    }
}

public class OntologicalAction2 extends OntologicalAction
{
    private SemanticAgent myAgent;

    public OntologicalAction1(SemanticActionTable table,
        String actionPattern, 
        Formula postconditionPattern,
        Formula preconditionPattern, SemanticAgent ag) 
    {
        super(table, actionPattern, postconditionPattern, preconditionPattern);
        myAgent = ag;
    }

    public void perform(OntoActionBehaviour behaviour) 
    {
        ((ManAgent)myAgent).takeOff(getActionParameter("clothing").toString());
        behaviour.setState(SemanticBehaviour.SUCCESS);
    }
}

public class OntologicalAction2 extends OntologicalAction
{
    private SemanticAgent myAgent;

    private long wakeupTime = -1, blockTime;
    
    public OntologicalAction2(SemanticActionTable table,
        String actionPattern, 
        Formula postconditionPattern,
        Formula preconditionPattern, SemanticAgent ag) 
    {
        super(table, actionPattern, postconditionPattern, preconditionPattern);
        myAgent = ag;
    }

    public void perform(OntoActionBehaviour behaviour) 
    {
           
               switch (behaviour.getState()) {
               case SemanticBehaviour.START: {
                    if (wakeupTime == -1) {
                        wakeupTime = System.currentTimeMillis()+Long.parseLong((getActionParameter("time").toString()));
                    }
                 // in this state the behaviour blocks itself
                 blockTime = wakeupTime - System.currentTimeMillis();
                 if (blockTime > 0) 
                   behaviour.block(blockTime);
                 behaviour.setState(1000);
                 break;
               }
               case 1000: {
                 // in this state the behaviour can be restarted for two reasons
                 // 1. the timeout is elapsed (then the handler method is called 
                 //                            and the behaviour is definitively finished) 
                 // 2. a message has arrived for this agent (then it blocks again and
                 //                            the FSM remains in this state)
                 blockTime = wakeupTime - System.currentTimeMillis();
                 if (blockTime <= 0) {
                   // timeout is expired
                   behaviour.setState(SemanticBehaviour.SUCCESS);
                 } else 
                   behaviour.block(blockTime);
                 break;
               }
               default : {
                 behaviour.setState(SemanticBehaviour.EXECUTION_FAILURE);
                 break;
               }
               } // end of switch
    }
}
#DOTNET_INCLUDE_END*/
