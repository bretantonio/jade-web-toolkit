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

package jade.semantics.ext.emotion.actions;

import jade.semantics.ext.emotion.EmotionalCapabilities;
import jade.semantics.interpreter.SemanticCapabilities;
import jade.semantics.lang.sl.grammar.Formula;

/** Represente une action ontologique caractérisé par un degré de certitude de la réalisé,
 * l'importance qu'elle soit réalisée (et qu'elle n'échoue pas) et lep otentiel de réaction en cas d'échec. 
 */ 
public class OntologicalAction extends jade.semantics.actions.OntologicalAction {
	
	/** Degré de certitude de l'agent de réaliser cette action */
	private double degre_certitude_agent;
	/** Degré de certitude de l'utilisateur de réaliser cette action */
	private double degre_certitude_user;
	
	/** Importance pour l'agent de réaliser cette action */
	private double importance_agent;
	
	/** Importance pour l'agent que l'action n'échoue pas */
	private double importance_NonEchec_agent;
	/** Importance pour l'utilisateur de réaliser cette action */
	private double importance_real_user;
	/** Importance pour l'utilisateur que cette action n'échoue pas */
	private double importance_echec_user;
	
	/** Potentiel de réaction de l'utilisateur en cas d'échec */
	private double pot_reaction_user;
	
	/** Constructeur d'une action ontologique caractérisé par un degré de certitude de la réalisé,
	 * l'importance qu'elle soit réalisée (et qu'elle n'échoue pas) et lep otentiel de réaction en cas d'échec. 
	 * @param capabilities semantic capabilities de l'agent
	 * @param actionPattern pattern de l'action
	 * @param postconditionPattern post condition de l'action
	 * @param preconditionPattern pré condition de l'action
	 * @param d_g degré de certitude de l'agent
	 * @param d_gU degré de certitude de l'utilisateur
	 * @param impRU importance pour l'utilisateur que l'action soit réalisée
	 * @param impEU importance pour l'utilisateur que l'action n'échoue pas 
	 * @param prU potentiel de réaction de l'utilisateur
	 */
	public OntologicalAction(SemanticCapabilities capabilities, String actionPattern, Formula postconditionPattern, Formula preconditionPattern, 
			double d_g, 
			double d_gU,
			double impRU,
			double impEU,
			double prU) {
		super(capabilities, actionPattern, postconditionPattern, preconditionPattern);
		degre_certitude_agent=d_g;
		degre_certitude_user=d_gU;
		importance_real_user=impRU;
		importance_echec_user=impEU;
		pot_reaction_user=prU;
		
		/** Valeur par défaut */
		importance_agent=((EmotionalCapabilities)capabilities).getDefault_impA();
		importance_NonEchec_agent=((EmotionalCapabilities)capabilities).getDefault_impNonEchecA();
		
	}
	
	/** Constructeur d'une action ontologique caractérisé par un degré de certitude de la réalisé,
	 * l'importance qu'elle soit réalisée (et qu'elle n'échoue pas) et le potentiel de réaction en cas d'échec par défaut égales à 1. 
	 * @param capabilities semantic capabilities de l'agent
	 * @param actionPattern pattern de l'action
	 * @param postconditionPattern post condition de l'action
	 * @param preconditionPattern pré condition de l'action
	 */
	public OntologicalAction(SemanticCapabilities capabilities, String actionPattern, Formula postconditionPattern, Formula preconditionPattern) {
		super(capabilities, actionPattern, postconditionPattern, preconditionPattern);
		degre_certitude_agent=((EmotionalCapabilities)capabilities).getDefault_deg_cert_agent();
		degre_certitude_user=((EmotionalCapabilities)capabilities).getDefault_deg_cert_user();
		importance_real_user=((EmotionalCapabilities)capabilities).getDefault_imp_satisf();
		importance_echec_user=((EmotionalCapabilities)capabilities).getDefault_imp_echec();
		pot_reaction_user=((EmotionalCapabilities)capabilities).getDefault_p_reac();
		importance_agent=((EmotionalCapabilities)capabilities).getDefault_impA();
		importance_NonEchec_agent=((EmotionalCapabilities)capabilities).getDefault_impNonEchecA();
		
	}
	
	/** Méthode qui retourne le degré de certitude de l'agent
	 * @return le degré de certitude de l'agent
	 */ 
	public double getDegresCertitudeAgent(){
		return degre_certitude_agent;
	}
	
	/** Méthode qui retourne le degré de certitude de l'utilisateur
	 * @return le degré de certitude de l'utilisateur
	 */ 
	public double getDegresCertitudeUser(){
		return degre_certitude_user;
	}
	
	/** Méthode qui retourne l'importance pour l'agent que l'action soit réalisée
	 * @return l'importance pour l'agent que l'action soit réalisée
	 */ 
	public double getImportanceAgent(){
		return importance_agent;
	}
	/** Méthode qui retourne l'importance pour l'agent que l'action n'échoue pas
	 * @return l'importance pour l'agent que l'action n'échoue pas
	 */ 
	public double getImportanceNonEchecAgent(){
		return this.importance_NonEchec_agent;
	}
	/** Méthode qui retourne l'importance pour l'utilisateur que l'action soit réalisée
	 * @return l'importance pour l'utilisateur que l'action soit réalisée
	 */
	public double getImportanceRealUser(){
		return importance_real_user;
	}
	
	/** Méthode qui retourne l'importance pour l'utilisateur que l'action n'échoue pas
	 * @return l'importance pour l'utilisateur que l'action n'échoue pas
	 */
	public double getImportanceEchecUser(){
		return importance_echec_user;
	}
	
	/** Méthode qui retourne le potentiel de réaction de l'utilisateur en cas d'échec de l'action
	 * @return le potentiel de réaction de l'utilisateur en cas d'échec de l'action
	 */
	public double getPotReactionUser(){
		return pot_reaction_user;
	}

}
