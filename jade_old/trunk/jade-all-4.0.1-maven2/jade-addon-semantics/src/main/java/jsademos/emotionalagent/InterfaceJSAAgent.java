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

package jsademos.emotionalagent;

import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.tools.SL;

/**
 * Agent JSA responsable de la communication entre l'agent JSA émotionnel et l'interface IHM
 * @author Magalie Ochs
 */
public class InterfaceJSAAgent extends SemanticAgent {
	
	
	/** 
	 * Constructeur de l'agent JSA d'interface
	 * Construit l'interface IHM de dialogue et ses semantic capabilities
	 */
	public InterfaceJSAAgent() {
		InterfaceDialogue interfaceDialogue=new InterfaceDialogue(this);
		setSemanticCapabilities(new SemanticCapabilitiesInterface(interfaceDialogue));
	}
	
	/**
	 * Méthode qui transmet la requete de l'utilisateur (formule logique) à l'agent JSA émotionnel
	 * @param f formule que l'agent JSA émotionnel doit interpréter
	 */
	public void informationInterface(Formula f){
		/** Si il y a un conflit de croyance (l'utilisateur a informer l'agent qu'il y avaitu n conflit de croyance)
		 * Alors il informe l'agent JSA émotionnel qu'il y a eu un conflit de croyance et affiche un message particulier ("je suis désolé")
		 * sur l'interface IHM de dialogue
		 */
		if(f.toString().contains("belief_conflict")){
			getSemanticCapabilities().getMyKBase().assertFormula(SL.fromFormula("(belief_conflict)"));
		}
		getSemanticCapabilities().interpret(f);
	}
	
	
	public void setup() {
		super.setup();
	}

	
}
