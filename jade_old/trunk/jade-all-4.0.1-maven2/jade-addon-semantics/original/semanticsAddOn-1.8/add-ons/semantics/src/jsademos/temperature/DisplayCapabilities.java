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
* DisplayCapabilities.java
* Created on 13 mai 2005
* Author : Vincent Pautret
*/
package jsademos.temperature;

import jade.core.behaviours.OneShotBehaviour;
import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.StandardCustomization;
import jade.semantics.interpreter.StandardCustomizationAdapter;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.interpreter.sips.adapters.BeliefTransferSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntegerConstantNode;
import jade.semantics.lang.sl.grammar.MetaTermReferenceNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

/**
* Capabilities of the display.
* @author Thierry Martinez - France Telecom
* @version Date: 2005/05/13 Revision: 1.0
*/
public class DisplayCapabilities extends DefaultCapabilities {
   
   /**
    * Definition of the temperature
    */
   SingleNumValueDefinition temperatureDefinition = new SingleNumValueDefinition("temperature");
   
   /**
    * Creates a new Display 
    */
   public DisplayCapabilities() {
       super();
   } // End of DisplayCapabilities/0
   
   @Override
protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {
	   SemanticInterpretationPrincipleTable table = super.setupSemanticInterpretationPrinciples();

	   // BELIEF TRANSFER SIPS
	   // Adopt beliefs about temperature-related facts only when originated by the selected sensor agent
	   table.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this,
			   temperatureDefinition.VALUE_X_PATTERN, new MetaTermReferenceNode("agent")) {
		   @Override
		protected ArrayList doApply(MatchResult matchFormula, MatchResult matchAgent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
			   if ((((DisplayAgent)getAgent()).selectedSensor != null 
					   && matchAgent.term("agent").equals(((DisplayAgent)getAgent()).selectedSensor))) {
				   return acceptResult;
			   }
			   else {
				   return refuseResult;
			   }
		   }
	   });
	
	   table.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this,
			   temperatureDefinition.VALUE_GT_X_PATTERN, new MetaTermReferenceNode("agent")) {
		   @Override
		protected ArrayList doApply(MatchResult matchFormula, MatchResult matchAgent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
			   if ((((DisplayAgent)getAgent()).selectedSensor != null 
					   && matchAgent.term("agent").equals(((DisplayAgent)getAgent()).selectedSensor))) {
				   return acceptResult;
			   }
			   else {
				   return refuseResult;
			   }
		   }
	   });
	   
	   // APPLICATION SPECIFIC SIPS
	   // Update the display when a belief about a new temperature value is adopted
	   table.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, temperatureDefinition.VALUE_X_PATTERN) {
		   @Override
		protected ArrayList doApply(final MatchResult applyResult, ArrayList result, SemanticRepresentation sr) {
			   potentiallyAddBehaviour(new OneShotBehaviour() {
				   @Override
				public void action() {
					   ((DisplayAgent)DisplayCapabilities.this.getAgent()).display.setTemperature(((Constant)applyResult.term("X")).realValue());
				   }
			   });
			   return result;
		   } 
	   });
	   
	   return table;
   }
   
   /**
    * setupStandardCustomization
    */
   @Override
public StandardCustomization setupStandardCustomization() {
       return new StandardCustomizationAdapter() {            
           @Override
		public boolean handleProposal(Term agentI, ActionExpression action, Formula formula) {
               // Handles the proposal only if the action is an InformRef on temperature and 
               // if the condition relates to a precision
               MatchResult matchResult = SL.match(DisplayAgent.CFP_ACTION, action);
               if ( matchResult != null ) {
                   matchResult = SL.match(DisplayAgent.CFP_CONDITION, formula);
                   if ( matchResult != null ) {
                       try {
                           ((DisplayAgent)getAgent()).handleProposal((IntegerConstantNode)matchResult.getTerm("X"), 
                                   agentI, action, formula);
                       }
                       catch(Exception e) {
                           e.printStackTrace();
                       }
                   }
               }
               return true;
           }
       };
   }  
   
   /**
    * setupKbase
    */
   @Override
public KBase setupKbase() {
       FilterKBase kbase = (FilterKBase) super.setupKbase();

	   kbase.addFiltersDefinition(temperatureDefinition);
	   
	   return kbase;     
   }  
} 
