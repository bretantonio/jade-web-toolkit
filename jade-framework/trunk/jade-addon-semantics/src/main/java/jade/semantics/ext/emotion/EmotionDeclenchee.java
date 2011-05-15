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

/** Objet d�signant une �motion d�clench�e */

public class EmotionDeclenchee {
	
	/** Caract�ristiques d'une �motion d�clench�e */
	
	/** Type de l'�motion */
	private String type;
	/** Intensit� de l'�motion */
	private double intensite;
	/** Action qui a d�clench�e l'�motion */
	private Term action;
	/** Agent vers qui est orient�e l'�motion */
	private Term agNameOriented;
	
	/** Constructeur d'une �motion d�clench�e non empathique 
	 * @param s type de l'�motion
	 * @param inten intensit� de l'�motion
	 * @param act terme d�signant l'action qui a d�clench� l'�motion 
	 * */
	public EmotionDeclenchee(String s, double inten, Term act){
		this.type=s;
		this.intensite=inten;
		this.action=act;
		this.agNameOriented=null;
	}
	
	/** Constructeur d'une �motion d�clench�e empathique 
	 * @param s type de l'�motion
	 * @param inten intensit� de l'�motion
	 * @param act terme d�signant l'action qui a d�clench� l'�motion 
	 * @param ag terme d�signant l'agent vers qui est orient�e l'�motion d'empathie
	 * */
	public EmotionDeclenchee(String s, double inten, Term act, Term ag){
		this.type=s;
		this.intensite=inten;
		this.action=act;
		this.agNameOriented=null;
		this.agNameOriented=ag;
	}
	
	
	/** M�thode permettant d'afficher une �motion d�clench�e (avec balises html)
	 * @return string correspondant � la description texte d'une m�otion d�clench�e
	 */
	public String toString(){
		StringBuffer affichage = new StringBuffer("");
		
		if(agNameOriented==null){
			if(type.equals("joie") || type.equals("satisfaction")){
				affichage=affichage.append("<i> Emotion de "+type+" d'intensit� "+intensite+" </i>");
				affichage=affichage.append("<BLOCKQUOTE> R�alisation de "+action.toString()+"</BLOCKQUOTE>");
			}else{
				affichage=affichage.append("<i> Emotion de "+type+" d'intensit� "+intensite+" </i>");
				affichage=affichage.append("<BLOCKQUOTE> Echec de "+action.toString()+"</BLOCKQUOTE>");
			}
		}else{
			if(type.equals("joie") || type.equals("satisfaction")){
				affichage=affichage.append("<i> Emotion d'empathie de "+type+" d'intensit� "+intensite+" </i>");
				affichage=affichage.append("<BLOCKQUOTE> Envers "+agNameOriented+"<br>"); 
				affichage=affichage.append("R�alisation de "+action.toString()+"</BLOCKQUOTE>");
			}else{
				affichage=affichage.append("<i> Emotion d'empathie de "+type+" d'intensit� "+intensite+" </i>");
				affichage=affichage.append("<BLOCKQUOTE> Envers "+agNameOriented+"<br>");
				affichage=affichage.append("Echec de "+action.toString()+"</BLOCKQUOTE>");
			}
		}
		return affichage.toString();
	}
	
	/** M�thode qui retourne le terme d�signant l'agent vers qui est orient�e l'�motion d'empathie 
	 * @return terme d�signant l'agent vers qui est orient�e l'�motion d'empathie
	 */
	public Term getAgentOriented(){
		return this.agNameOriented;
	}
	
	/** M�thode qui retourne l'action qui a d�clench� l'�motion 
	 * @return action qui a d�clench� l'�motion
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
