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

package jade.semantics.ext.emotion;

import jade.semantics.lang.sl.grammar.Term;
import java.lang.Math;

/**
 * Un objet Emotion représente une émotion courante
 * @param Type Ensemble des types d'émotions existants pour l'agent
 * @param myAgentName Nom de l'agent qui a l'émotion
 * @param type Type de l'émotion
 * @param intensity Intensité courante de l'émotion
 * @param agentNameOriented Nom de l'agent vers qui est orientée l'émotion dans le cas d'une émotion empathique
 * @param decayRate Taux de décroissance au cours du temps de l'émotion
 * @param epsilon Valeur en dessous de laquelle l'intensité est considéré comme nulle
 */
public class Emotion {

/** Caractéristiques d'une émotion courante */
	
	/** Ensemble des types d'émotions existants pour l'agent */
	public enum Type {JOIE, SATISF, FRUSTR, TRISTES, ENERV, COLERE};
	/** Nom de l'agent qui a l'émotion */
	private Term agentName; 
	/** Type de l'émotion */
	private Type type; 
	/** Intensité courante de l'émotion */
	private double intensity; 
	/** Nom de l'agent vers qui est orientée l'émotion dans le cas d'une émotion empathique */
	private Term agentNameOriented; 
	
	/** Taux de décroissance au cours du temps de l'émotion */
	private float decayRate;
	/** Valeur en dessous de laquelle l'intensité est considéré comme nulle */
	private double epsilon;
	
	
	/** Construction d'une émotion d'empathie
	 * @param aN terme désignant l'agent qui a l'émotion
	 * @param t type de l'émotion d'empathie
	 * @param i intensité de l'émotion
	 * @param aNo terme désignant l'agent vers qui est orientée l'émotion d'empathie 
	 * */ 
	public Emotion(Term aN, Type t, double i, Term aNo){
		agentName=aN;
		type=t;
		intensity=i;
		agentNameOriented=aNo;
		
		/** Valeur par défaut*/
		decayRate=1;
		epsilon=0.05;
	}
	
	/** Construction d'une émotion non empathique
	 * @param aN terme désignant l'agent qui a l'émotion
	 * @param t type de l'émotion 
	 * @param i intensité de l'émotion 
	 * */ 
	public Emotion(Term aN, Type t, float i){
		agentName=aN;
		type=t;
		intensity=i;
		agentNameOriented=null;
		
		/** Valeur par défaut*/
		decayRate=1;
		epsilon=0.05;
	}

	/** Fonction de décroissance de l'émotion au cours du temps. 
	 * Méthode appelée à chaque unité de temps
	 * */ 
	public void updateEmotionTime(){
		
		if(intensity>=epsilon){
			intensity=intensity*(Math.exp(-1*decayRate));
		}else
			intensity=0;
		
	}
	
	/** Mise à jour d'une émotion type suite à une émotion déclenchée de même type 
	 * @param intens intensité de la nouvelle émotion déclenchée
	 * */
	public void update(double intens){
			/** Calcul permettant de conserver l'intensité de l'émotion dans l'intervalle [0, 1[ */  
			double i=(1-intens)*intensity+intens;
			intensity=i;
	}
	
	/** Mise à jour d'une émotion type suite à une émotion déclenchée de valence opposée 
	 * @param intens intensité de la nouvelle émotion déclenchée
	 * */
	public void diminution(double intens){
		double i=(1+intens)*intensity-intens;
		intensity=i;
	}

	/** Retourne le type de l'émotion 
	 * @return type type de l'émotion
	 * */
	public Type getType(){
		return type;
	}
	
	/**Retourne l'intensité de l'émotion 
	* @return intensity intensité de l'émotion
	*/ 
	public double getIntensity(){
		return intensity;
	}
	
	/**Modifie l'intensité de l'émotion tel que sa valeur est égale à la valeur passée en paramètre
	 * @param i nouvelle intensité de l'émotion
	 * */
	public void setIntensity(double i){
		intensity=i;
	}
	
	/**Retorune le terme désignant l'agent vers qui est orientée l'émotion
	 *@return  agentNameOriented terme désignant l'agent vers qui est orientée l'émotion d'empathie 
	 **/
	public Term getAgentOriented(){
		return agentNameOriented;
	}
	
}
