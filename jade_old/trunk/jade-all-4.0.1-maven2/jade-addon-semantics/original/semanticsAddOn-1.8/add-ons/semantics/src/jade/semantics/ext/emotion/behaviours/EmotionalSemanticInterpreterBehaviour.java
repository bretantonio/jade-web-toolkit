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

package jade.semantics.ext.emotion.behaviours;

import jade.lang.acl.MessageTemplate;
import jade.semantics.ext.emotion.EmotionalCapabilities;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.interpreter.SemanticInterpreterBehaviour;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;
import jsademos.emotionalagent.MyEmotionalAgent;

/**** Represente le comportement d'un agent JSA émotionnel */
public class EmotionalSemanticInterpreterBehaviour  extends SemanticInterpreterBehaviour{
		
	/**
	 * 
	 * @param msgTemplate
	 * @param sc
	 */
	public EmotionalSemanticInterpreterBehaviour(MessageTemplate msgTemplate, SemanticCapabilities sc){
		super(msgTemplate, sc);			   
	}
	/** Méthode qui déclenche des méotions négatives si l'agent n'a pas compris une phrase 
	 * @param list liste des énoncés
	 * @return vrai si un énoncé a été incompris, faux sinon */
	protected boolean checkNotUnderstandable(ArrayList list) {
		boolean result = super.checkNotUnderstandable(list);
		if(result){
			System.err.println("----- >   Generation emotion NEG - EFFET INTENTIONNEL : message non compréhensible");

			/** On réacupère les valeurs de degré de certitude, importance et potentiel de réaction pour le déclenchement de l'émotion */
			EmotionalCapabilities ec = (EmotionalCapabilities)((MyEmotionalAgent)this.myAgent).getSemanticCapabilities();
			double degresCertitude = ec.getDegreCertitudeUM();
			double degresCertitudeA = ec.getDegreCertitudeUnderstoodA();
			double impActeNonCompris = ec.getImpActeNonCompris();
			double impActeNonComprisA = ec.getImpActeNonComprisA();
			double potentielReactActeNonCompris = ec.getPotentielReactActeNonCompris();
			double potentielReactActeNonComprisA = ec.getPotentielReactActeNonComprisA();


			ec.getEmotionalState().newElicitedEmotionNE("neg",degresCertitudeA,potentielReactActeNonComprisA, 1.0,impActeNonComprisA, SL.term("not understood"));
			/** Une méotion d'empathie est automatiquement déclenchée car l'agent interlocuteur a l'intention d'être compris */
			ec.getEmotionalState().newElicitedEmotion("neg",degresCertitude,potentielReactActeNonCompris,1.0,impActeNonCompris, SL.term("not understood"), SL.term("ihm@test"));                

//	eloona		ec.getEmotionalState().transfertTextePlusEmotionEmp("Je ne comprends pas ce que vous désirez");

		}

		return result;

	} 

}
