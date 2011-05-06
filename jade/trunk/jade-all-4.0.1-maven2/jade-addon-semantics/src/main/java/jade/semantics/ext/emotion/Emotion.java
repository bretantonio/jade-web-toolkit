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

package jade.semantics.ext.emotion;

import jade.semantics.lang.sl.grammar.Term;
import java.lang.Math;

/**
 * Un objet Emotion repr�sente une �motion courante
 * @param Type Ensemble des types d'�motions existants pour l'agent
 * @param myAgentName Nom de l'agent qui a l'�motion
 * @param type Type de l'�motion
 * @param intensity Intensit� courante de l'�motion
 * @param agentNameOriented Nom de l'agent vers qui est orient�e l'�motion dans le cas d'une �motion empathique
 * @param decayRate Taux de d�croissance au cours du temps de l'�motion
 * @param epsilon Valeur en dessous de laquelle l'intensit� est consid�r� comme nulle
 */
public class Emotion {

/** Caract�ristiques d'une �motion courante */
	
	/** Ensemble des types d'�motions existants pour l'agent */
	public enum Type {JOIE, SATISF, FRUSTR, TRISTES, ENERV, COLERE};
	/** Nom de l'agent qui a l'�motion */
	private Term agentName; 
	/** Type de l'�motion */
	private Type type; 
	/** Intensit� courante de l'�motion */
	private double intensity; 
	/** Nom de l'agent vers qui est orient�e l'�motion dans le cas d'une �motion empathique */
	private Term agentNameOriented; 
	
	/** Taux de d�croissance au cours du temps de l'�motion */
	private float decayRate;
	/** Valeur en dessous de laquelle l'intensit� est consid�r� comme nulle */
	private double epsilon;
	
	
	/** Construction d'une �motion d'empathie
	 * @param aN terme d�signant l'agent qui a l'�motion
	 * @param t type de l'�motion d'empathie
	 * @param i intensit� de l'�motion
	 * @param aNo terme d�signant l'agent vers qui est orient�e l'�motion d'empathie 
	 * */ 
	public Emotion(Term aN, Type t, double i, Term aNo){
		agentName=aN;
		type=t;
		intensity=i;
		agentNameOriented=aNo;
		
		/** Valeur par d�faut*/
		decayRate=1;
		epsilon=0.05;
	}
	
	/** Construction d'une �motion non empathique
	 * @param aN terme d�signant l'agent qui a l'�motion
	 * @param t type de l'�motion 
	 * @param i intensit� de l'�motion 
	 * */ 
	public Emotion(Term aN, Type t, float i){
		agentName=aN;
		type=t;
		intensity=i;
		agentNameOriented=null;
		
		/** Valeur par d�faut*/
		decayRate=1;
		epsilon=0.05;
	}

	/** Fonction de d�croissance de l'�motion au cours du temps. 
	 * M�thode appel�e � chaque unit� de temps
	 * */ 
	public void updateEmotionTime(){
		
		if(intensity>=epsilon){
			intensity=intensity*(Math.exp(-1*decayRate));
		}else
			intensity=0;
		
	}
	
	/** Mise � jour d'une �motion type suite � une �motion d�clench�e de m�me type 
	 * @param intens intensit� de la nouvelle �motion d�clench�e
	 * */
	public void update(double intens){
			/** Calcul permettant de conserver l'intensit� de l'�motion dans l'intervalle [0, 1[ */  
			double i=(1-intens)*intensity+intens;
			intensity=i;
	}
	
	/** Mise � jour d'une �motion type suite � une �motion d�clench�e de valence oppos�e 
	 * @param intens intensit� de la nouvelle �motion d�clench�e
	 * */
	public void diminution(double intens){
		double i=(1+intens)*intensity-intens;
		intensity=i;
	}

	/** Retourne le type de l'�motion 
	 * @return type type de l'�motion
	 * */
	public Type getType(){
		return type;
	}
	
	/**Retourne l'intensit� de l'�motion 
	* @return intensity intensit� de l'�motion
	*/ 
	public double getIntensity(){
		return intensity;
	}
	
	/**Modifie l'intensit� de l'�motion tel que sa valeur est �gale � la valeur pass�e en param�tre
	 * @param i nouvelle intensit� de l'�motion
	 * */
	public void setIntensity(double i){
		intensity=i;
	}
	
	/**Retorune le terme d�signant l'agent vers qui est orient�e l'�motion
	 *@return  agentNameOriented terme d�signant l'agent vers qui est orient�e l'�motion d'empathie 
	 **/
	public Term getAgentOriented(){
		return agentNameOriented;
	}
	
}
