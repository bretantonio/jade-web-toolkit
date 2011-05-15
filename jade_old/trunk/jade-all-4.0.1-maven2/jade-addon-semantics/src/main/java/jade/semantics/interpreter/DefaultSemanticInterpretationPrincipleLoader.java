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
* added PlanExecutionSIP, Carole Adam, 9 November 2007
*/

package jade.semantics.interpreter;

import jade.semantics.interpreter.sips.ActionFeatures;
import jade.semantics.interpreter.sips.ActionPerformance;
import jade.semantics.interpreter.sips.Agree;
import jade.semantics.interpreter.sips.GoalCommitment;
import jade.semantics.interpreter.sips.BeliefTransfer;
import jade.semantics.interpreter.sips.IntentionTransfer;
import jade.semantics.interpreter.sips.PlanExecutionSIP;
import jade.semantics.interpreter.sips.Propose;
import jade.semantics.interpreter.sips.RationalityPrinciple;
import jade.semantics.interpreter.sips.Refuse;
import jade.semantics.interpreter.sips.RejectProposal;
import jade.semantics.interpreter.sips.RequestWhen;
import jade.semantics.interpreter.sips.RequestWhenever;
import jade.semantics.interpreter.sips.SplitActionSequence;
import jade.semantics.interpreter.sips.SplitAnd;
import jade.semantics.interpreter.sips.SplitBeliefIntention;
import jade.semantics.interpreter.sips.SplitEqualsIRE;
import jade.semantics.interpreter.sips.SplitEqualsIREBeliefIntention;
import jade.semantics.interpreter.sips.Subscribe;
import jade.semantics.interpreter.sips.UnreachableGoal;
import jade.semantics.interpreter.sips.Unsubscribe;

public class DefaultSemanticInterpretationPrincipleLoader implements SemanticInterpretationPrincipleLoader {
	
	SemanticCapabilities capabilities;
	
	public DefaultSemanticInterpretationPrincipleLoader(SemanticCapabilities capabilities) {
		this.capabilities = capabilities;
	}
	
	public void load(SemanticInterpretationPrincipleTable table) {
		   table.addSemanticInterpretationPrinciple(new SplitEqualsIREBeliefIntention(capabilities));
		   table.addSemanticInterpretationPrinciple(new SplitBeliefIntention(capabilities));
		   table.addSemanticInterpretationPrinciple(new SplitEqualsIRE(capabilities));
		   table.addSemanticInterpretationPrinciple(new SplitAnd(capabilities));
	       //#PJAVA_EXCLUDE_BEGIN
		   table.addSemanticInterpretationPrinciple(new ActionFeatures(capabilities, true));
	       //#PJAVA_EXCLUDE_END
	       /*#PJAVA_INCLUDE_BEGIN
	       this.addSemanticInterpretationPrinciple(new ActionFeatures(capabilities, false));
	       #PJAVA_INCLUDE_END*/
		   table.addSemanticInterpretationPrinciple(new GoalCommitment(capabilities));
		   
//	       table.addSemanticInterpretationPrinciple(new IntentionInterpretation(capabilities));

		   table.addSemanticInterpretationPrinciple(new BeliefTransfer(capabilities));
		   table.addSemanticInterpretationPrinciple(new RequestWhen(capabilities));
		   table.addSemanticInterpretationPrinciple(new IntentionTransfer(capabilities));
		   table.addSemanticInterpretationPrinciple(new RationalityPrinciple(capabilities));
		   table.addSemanticInterpretationPrinciple(new ActionPerformance(capabilities));
//		   table.addSemanticInterpretationPrinciple(new Planning(capabilities));
	       table.addSemanticInterpretationPrinciple(new Refuse(capabilities));
	       table.addSemanticInterpretationPrinciple(new RejectProposal(capabilities));
	       table.addSemanticInterpretationPrinciple(new Agree(capabilities));
	       table.addSemanticInterpretationPrinciple(new Propose(capabilities));
	       table.addSemanticInterpretationPrinciple(new RequestWhenever(capabilities));
	       table.addSemanticInterpretationPrinciple(new Subscribe(capabilities));
	       table.addSemanticInterpretationPrinciple(new Unsubscribe(capabilities));
	       //table.addSemanticInterpretationPrinciple(new UnreachableGoal(capabilities));
	       table.addSemanticInterpretationPrinciple(new UnreachableGoal(capabilities));
	       table.addSemanticInterpretationPrinciple(new PlanExecutionSIP(capabilities));
	       
	       table.addSemanticInterpretationPrinciple(new SplitActionSequence(capabilities));
	       
	      // institutional SIPs transferred to InstitutionalCapabilities
	      // Carole Adam, 28 Novembeer 2007
	}

}

