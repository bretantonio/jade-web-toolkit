/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France Télécom

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
 * created on 14 déc. 07 by Vincent Louis
 */

/**
 * 
 */
package jade.semantics.ext.emotion.sips;

import jade.semantics.actions.SemanticAction;
import jade.semantics.ext.emotion.EmotionalCapabilities;
import jade.semantics.ext.emotion.actions.OntologicalAction;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.sips.adapters.PlanProcessingSIPAdapter;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.AlternativeActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IntentionNode;
import jade.semantics.lang.sl.grammar.SequenceActionExpressionNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;

/**
 * @author Vincent Louis - France Telecom
 *
 */
public class EmotionTriggeringDecoratorSIP extends PlanProcessingSIPAdapter {

	private EmotionalCapabilities agentCapabilities;
	
	/** Importance par défaut pour l'agent qu'une action soit réalisée*/
	private double importanceIntention;
	/** Importance par défaut pour l'agent qu'une action n'échoue pas*/
	private double importanceNotIntention;
	/** Vraie si c'est une intention de osn interlocuteur aussi */
	private Term intentionPartageeAgent;

	private ActionExpressionNode effort;
	
	/**
	 * 
	 */
	public EmotionTriggeringDecoratorSIP(EmotionalCapabilities sc) {
		super(sc, "??plan");
		agentCapabilities=sc;
		
		/** Importance pour l'agent de réaliser l'action */
		importanceIntention = sc.getDefault_impA();
		importanceNotIntention = sc.getDefault_impNonEchecA();
		effort = new ActionExpressionNode(agentCapabilities.getAgentName(), SL.term("(effort)"));
	}
	
	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.sips.adapters.PlanProcessingSIPAdapter#doApply(jade.semantics.lang.sl.tools.MatchResult, jade.semantics.interpreter.SemanticRepresentation)
	 */
	public ActionExpression doApply(MatchResult matchResult,
			SemanticRepresentation sr)
			throws SemanticInterpretationPrincipleException {
		Formula transferredIntention = (Formula)sr.getAnnotation(SemanticRepresentation.TRANSFERRED_INTENTION_KEY);
		if (transferredIntention == null) {
			intentionPartageeAgent = myCapabilities.getAgentName();
		}
		else {
			intentionPartageeAgent = ((IntentionNode)transferredIntention).as_agent();
		}
		return decoreEmotion(matchResult.term("plan"), 0, sr);
	}
	
	
	public ActionExpression decoreEmotion(Term plan, double fatherPotential, SemanticRepresentation sr){
		
		if (plan instanceof ActionExpressionNode) {
			ActionExpressionNode genEmoPos;
			ActionExpressionNode genEmoNeg;
			
			ActionExpression result = new SequenceActionExpressionNode(plan, effort);
			
			genEmoPos=new ActionExpressionNode(
					agentCapabilities.getAgentName(),
					SL.term("(EmotionPos :imp "+importanceIntention+
							" :d_g "+degresIncertitude((ActionExpressionNode)plan)+
							" :act "+plan+
							" :agOr "+intentionPartageeAgent+")").getSimplifiedTerm());
			genEmoNeg=new ActionExpressionNode(
					agentCapabilities.getAgentName(),
					SL.term("(EmotionNeg :potReact "+fatherPotential+" :imp "+importanceNotIntention+
							" :d_g "+degresIncertitude((ActionExpressionNode)plan)+
							" :act "+plan+
							" :agOr "+intentionPartageeAgent+")").getSimplifiedTerm());		

			result = new SequenceActionExpressionNode(result ,genEmoPos);
			result = new AlternativeActionExpressionNode (result, genEmoNeg);
			
			return result;
		}
		else if (plan instanceof AlternativeActionExpressionNode) {
			return new AlternativeActionExpressionNode(
					decoreEmotion(((AlternativeActionExpressionNode)plan).as_left_action(), 1, sr),
					decoreEmotion(((AlternativeActionExpressionNode)plan).as_right_action(), fatherPotential, sr));
		}
		else if (plan instanceof SequenceActionExpressionNode) {
			return new SequenceActionExpressionNode(
					decoreEmotion(((SequenceActionExpressionNode)plan).as_left_action(), fatherPotential, sr),
					decoreEmotion(((SequenceActionExpressionNode)plan).as_right_action(), fatherPotential, sr));
		}
		else {
			return null;
		}
	}
	
	public double degresIncertitude(ActionExpression actExp){
		try{
			if(actExp instanceof ActionExpressionNode){
				SemanticAction semAct = myCapabilities.getMySemanticActionTable().getSemanticActionInstance(actExp);
				if(semAct instanceof OntologicalAction){
					return((OntologicalAction)semAct).getDegresCertitudeAgent();
				}else{
					return ((EmotionalCapabilities)myCapabilities).getDefault_deg_cert_agent();
				}
			}else{
				if(actExp instanceof SequenceActionExpressionNode){
					ActionExpression leftPart=(ActionExpression)((SequenceActionExpressionNode)actExp).as_left_action();
					ActionExpression rightPart=(ActionExpression)((SequenceActionExpressionNode)actExp).as_right_action();
					return (degresIncertitude(leftPart)+degresIncertitude(rightPart))/2;
				}else{
					if(actExp instanceof AlternativeActionExpressionNode){
						ActionExpression leftPart=(ActionExpression)((AlternativeActionExpressionNode)actExp).as_left_action();
						ActionExpression rightPart=(ActionExpression)((AlternativeActionExpressionNode)actExp).as_right_action();
						return (degresIncertitude(leftPart)+degresIncertitude(rightPart))/2;
					}
				}
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
		return 1;
	}


}

