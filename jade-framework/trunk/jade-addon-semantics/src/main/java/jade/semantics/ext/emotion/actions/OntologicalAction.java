/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
JSA - JADE Semantics Add-on is a framework to develop cognitive
agents in compliance with the FIPA-ACL formal specifications.

Copyright (C) 2008 France T�l�com

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

/** Represente une action ontologique caract�ris� par un degr� de certitude de la r�alis�,
 * l'importance qu'elle soit r�alis�e (et qu'elle n'�choue pas) et lep otentiel de r�action en cas d'�chec. 
 */ 
public class OntologicalAction extends jade.semantics.actions.OntologicalAction {
	
	/** Degr� de certitude de l'agent de r�aliser cette action */
	private double degre_certitude_agent;
	/** Degr� de certitude de l'utilisateur de r�aliser cette action */
	private double degre_certitude_user;
	
	/** Importance pour l'agent de r�aliser cette action */
	private double importance_agent;
	
	/** Importance pour l'agent que l'action n'�choue pas */
	private double importance_NonEchec_agent;
	/** Importance pour l'utilisateur de r�aliser cette action */
	private double importance_real_user;
	/** Importance pour l'utilisateur que cette action n'�choue pas */
	private double importance_echec_user;
	
	/** Potentiel de r�action de l'utilisateur en cas d'�chec */
	private double pot_reaction_user;
	
	/** Constructeur d'une action ontologique caract�ris� par un degr� de certitude de la r�alis�,
	 * l'importance qu'elle soit r�alis�e (et qu'elle n'�choue pas) et lep otentiel de r�action en cas d'�chec. 
	 * @param capabilities semantic capabilities de l'agent
	 * @param actionPattern pattern de l'action
	 * @param postconditionPattern post condition de l'action
	 * @param preconditionPattern pr� condition de l'action
	 * @param d_g degr� de certitude de l'agent
	 * @param d_gU degr� de certitude de l'utilisateur
	 * @param impRU importance pour l'utilisateur que l'action soit r�alis�e
	 * @param impEU importance pour l'utilisateur que l'action n'�choue pas 
	 * @param prU potentiel de r�action de l'utilisateur
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
		
		/** Valeur par d�faut */
		importance_agent=((EmotionalCapabilities)capabilities).getDefault_impA();
		importance_NonEchec_agent=((EmotionalCapabilities)capabilities).getDefault_impNonEchecA();
		
	}
	
	/** Constructeur d'une action ontologique caract�ris� par un degr� de certitude de la r�alis�,
	 * l'importance qu'elle soit r�alis�e (et qu'elle n'�choue pas) et le potentiel de r�action en cas d'�chec par d�faut �gales � 1. 
	 * @param capabilities semantic capabilities de l'agent
	 * @param actionPattern pattern de l'action
	 * @param postconditionPattern post condition de l'action
	 * @param preconditionPattern pr� condition de l'action
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
	
	/** M�thode qui retourne le degr� de certitude de l'agent
	 * @return le degr� de certitude de l'agent
	 */ 
	public double getDegresCertitudeAgent(){
		return degre_certitude_agent;
	}
	
	/** M�thode qui retourne le degr� de certitude de l'utilisateur
	 * @return le degr� de certitude de l'utilisateur
	 */ 
	public double getDegresCertitudeUser(){
		return degre_certitude_user;
	}
	
	/** M�thode qui retourne l'importance pour l'agent que l'action soit r�alis�e
	 * @return l'importance pour l'agent que l'action soit r�alis�e
	 */ 
	public double getImportanceAgent(){
		return importance_agent;
	}
	/** M�thode qui retourne l'importance pour l'agent que l'action n'�choue pas
	 * @return l'importance pour l'agent que l'action n'�choue pas
	 */ 
	public double getImportanceNonEchecAgent(){
		return this.importance_NonEchec_agent;
	}
	/** M�thode qui retourne l'importance pour l'utilisateur que l'action soit r�alis�e
	 * @return l'importance pour l'utilisateur que l'action soit r�alis�e
	 */
	public double getImportanceRealUser(){
		return importance_real_user;
	}
	
	/** M�thode qui retourne l'importance pour l'utilisateur que l'action n'�choue pas
	 * @return l'importance pour l'utilisateur que l'action n'�choue pas
	 */
	public double getImportanceEchecUser(){
		return importance_echec_user;
	}
	
	/** M�thode qui retourne le potentiel de r�action de l'utilisateur en cas d'�chec de l'action
	 * @return le potentiel de r�action de l'utilisateur en cas d'�chec de l'action
	 */
	public double getPotReactionUser(){
		return pot_reaction_user;
	}

}
