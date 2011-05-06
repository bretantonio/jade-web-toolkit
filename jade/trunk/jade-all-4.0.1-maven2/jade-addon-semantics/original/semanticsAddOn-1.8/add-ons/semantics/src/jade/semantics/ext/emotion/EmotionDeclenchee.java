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

/** Objet désignant une émotion déclenchée */

public class EmotionDeclenchee {
	
	/** Caractéristiques d'une émotion déclenchée */
	
	/** Type de l'émotion */
	private String type;
	/** Intensité de l'émotion */
	private double intensite;
	/** Action qui a déclenchée l'émotion */
	private Term action;
	/** Agent vers qui est orientée l'émotion */
	private Term agNameOriented;
	
	/** Constructeur d'une émotion déclenchée non empathique 
	 * @param s type de l'émotion
	 * @param inten intensité de l'émotion
	 * @param act terme désignant l'action qui a déclenché l'émotion 
	 * */
	public EmotionDeclenchee(String s, double inten, Term act){
		this.type=s;
		this.intensite=inten;
		this.action=act;
		this.agNameOriented=null;
	}
	
	/** Constructeur d'une émotion déclenchée empathique 
	 * @param s type de l'émotion
	 * @param inten intensité de l'émotion
	 * @param act terme désignant l'action qui a déclenché l'émotion 
	 * @param ag terme désignant l'agent vers qui est orientée l'émotion d'empathie
	 * */
	public EmotionDeclenchee(String s, double inten, Term act, Term ag){
		this.type=s;
		this.intensite=inten;
		this.action=act;
		this.agNameOriented=null;
		this.agNameOriented=ag;
	}
	
	
	/** Méthode permettant d'afficher une émotion déclenchée (avec balises html)
	 * @return string correspondant à la description texte d'une méotion déclenchée
	 */
	public String toString(){
		StringBuffer affichage = new StringBuffer("");
		
		if(agNameOriented==null){
			if(type.equals("joie") || type.equals("satisfaction")){
				affichage=affichage.append("<i> Emotion de "+type+" d'intensité "+intensite+" </i>");
				affichage=affichage.append("<BLOCKQUOTE> Réalisation de "+action.toString()+"</BLOCKQUOTE>");
			}else{
				affichage=affichage.append("<i> Emotion de "+type+" d'intensité "+intensite+" </i>");
				affichage=affichage.append("<BLOCKQUOTE> Echec de "+action.toString()+"</BLOCKQUOTE>");
			}
		}else{
			if(type.equals("joie") || type.equals("satisfaction")){
				affichage=affichage.append("<i> Emotion d'empathie de "+type+" d'intensité "+intensite+" </i>");
				affichage=affichage.append("<BLOCKQUOTE> Envers "+agNameOriented+"<br>"); 
				affichage=affichage.append("Réalisation de "+action.toString()+"</BLOCKQUOTE>");
			}else{
				affichage=affichage.append("<i> Emotion d'empathie de "+type+" d'intensité "+intensite+" </i>");
				affichage=affichage.append("<BLOCKQUOTE> Envers "+agNameOriented+"<br>");
				affichage=affichage.append("Echec de "+action.toString()+"</BLOCKQUOTE>");
			}
		}
		return affichage.toString();
	}
	
	/** Méthode qui retourne le terme désignant l'agent vers qui est orientée l'émotion d'empathie 
	 * @return terme désignant l'agent vers qui est orientée l'émotion d'empathie
	 */
	public Term getAgentOriented(){
		return this.agNameOriented;
	}
	
	/** Méthode qui retourne l'action qui a déclenché l'émotion 
	 * @return action qui a déclenché l'émotion
	 */
	public Term getAction(){
		return this.action;
	}
	
	public String getType(){
		return this.type;
	}
	
	public double getIntensity(){
		return this.intensite;
	}
}
