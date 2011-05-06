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

import jade.semantics.ext.emotion.Emotion.Type;
import jade.semantics.lang.sl.grammar.Term;
import jade.util.leap.ArrayList;

//import java.io.BufferedWriter;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.Socket;

/** Constructeur de l'objet �tat �motionnel regroupant toutes les �motions de l'agent 
 * 
 */
public class EmotionalState {
	
	/** Nom de l'agent qui a cet �tat �motionnel */
	private Term agentName; 
	
	/** Observers de l'�tat �motionel, pemettant par exemple la MaJ de'IHM graphiques */
	private ArrayList observers;
	
	/** Emotions courantes non empathiques de l'agent */
	private Emotion joie;
	private Emotion satisf;
	private Emotion frustr;
	private Emotion tristes;
	private Emotion enerv;
	private Emotion colere; 
	
	/** Liste des �motions empathiques de l'agent */
	private ArrayList emotionEmpathie; 
	
	/** Derni�re �motion d'empathie d�clench�e */
	private EmotionDeclenchee lastEmpathicEmotion;
	
	/** Taux d'empathie compris entre 0 et 1
	 * Si =0 alors  agent non empathique
	 * Si =1 alors agent compl�tement empathique : ressent les m�me �motion avec la m�me intensit� 
	 * que celle qu'il pense que son interlocuteur ressent
	 * */
	private double empathyRate;
	
	/** Coefficient de caricature de l'expression faciale compris entre 0 et 100 
	 * Si �gale � 0 alors l'intensit� de l'expression faciale est �gale � l'intensit� de l'�motion
	 * Plus le coefficient est grand plus l'expression faciale est exag�r�e
	 */
	private int coeffExprFaciale;
	
	/** Dernier effort r�alis� par l'agent*/
	private double lastEffort;
	
	/** Historique des �motions d�clench�es */
	private ArrayList historiqueEmotionDeclenchee;
		
//	/** Connexion socket avec Eloona pour l'affichage des expressions faciales d'�motions */
//	private PrintWriter connexionEloona;
	
	/** Vraie si l'agent exprime que ses �motions d'empathie, faux si il exprime ses �motions non empathiques */ 
	private boolean agentEmpathique;
	
	/** Vraie si l'agent exprime uniquement les �motions d�clench�es empathiques*/
	private boolean expressionEmoDeclenchee;
	
	/** Constructeur d'un �tat �motionnel 
	 * @param ag agent qui a cet �tat �motionnel
	 */
	public EmotionalState(Term ag){
		
		agentName=ag;
		observers = new ArrayList();
		/** Cr�ation de la liste vide d'�motions d'empathie*/
		emotionEmpathie=new ArrayList();
		
		/** Cr�ation des �motions non empathiques initiales de l'agent */
		joie=new Emotion(agentName, Emotion.Type.JOIE, 0);
		satisf=new Emotion(agentName, Emotion.Type.SATISF, 0);
		frustr=new Emotion(agentName, Emotion.Type.FRUSTR, 0);
		tristes=new Emotion(agentName, Emotion.Type.TRISTES, 0);
		enerv=new Emotion(agentName, Emotion.Type.ENERV, 0);
		colere=new Emotion(agentName, Emotion.Type.COLERE, 0);
				 
//		/** Cr�ation de la connexion avec Eloona pour l'affichage des expressions faciales d'�motion*/
//		try{
//			Socket s = new Socket("127.0.0.1",18000);
//			OutputStream os = s.getOutputStream();
//			connexionEloona = new PrintWriter(new BufferedWriter(
//                   new OutputStreamWriter(os)), 
//                   true);
//		}catch(Exception e){
//			System.err.println(e);
//		}
	
		/** Cr�ation de l'historique des �motions d�clench�es */
		historiqueEmotionDeclenchee=new ArrayList();
		
		/** Le taux d'empathie est par d�faut �gale � 1 */
		empathyRate=1;	
		
		/** On fixe le coefficient de caricature de l'expression faciale � 60 */
		coeffExprFaciale=90;
		
		/** Agent est empathique par d�faut :exprime uniquement ses �motions d'empathie */
		agentEmpathique=true;
		/** Agent exprime uniquement les �motions d�clench�es empathiques */ 
		expressionEmoDeclenchee=true;
	}
	
	/** M�thode qui met � z�ro toutes les �motions de l'agent 
	 * et met � jour l'affichage
	 */
	public void initialisation(){
		joie.setIntensity(0);
		satisf.setIntensity(0);
		frustr.setIntensity(0);
		tristes.setIntensity(0);
		enerv.setIntensity(0);
		colere.setIntensity(0);
		emotionEmpathie=new ArrayList();
		updateObservers();
	}
	
	public void addObserver(EmotionalStateObserver observer) {
		observers.add(observer);
		observer.emotionalStateChanged(this);
	}
	
	public Object removeObserver(EmotionalStateObserver observer) {
		return observers.remove(observer);
	}
	
	protected void updateObservers() {
		for(int i=0; i<observers.size(); i++) {
			((EmotionalStateObserver)observers.get(i)).emotionalStateChanged(this);
		}
	}
	
	/** M�thode qui retourne la somme des �motions n�gatives non empathiques
	 * @return la somme des intensit� des �motions n�gatives non empathiques de l'agent 
	 * **/
	public double sommeEmoNeg(){
		return (this.frustr.getIntensity()+ this.enerv.getIntensity()+this.tristes.getIntensity()+ colere.getIntensity());
	}
	
	/** M�thode qui retourne la somme des �motions positives non empathiques
	 * @return la somme des intensit� des �motions positives non empathiques de l'agent 
	 * **/
	public double sommeEmoPos(){
		return (this.joie.getIntensity()+this.satisf.getIntensity());
	}
	
	/** Retourne la somme des �motions d'empathie n�gatives (neg et col�re)
	 * qu'� l'agent JSA pour l'agent agNameOriented 
	 * @param agNameOriented terme d�signant l'agent vers qui sont orient�es les �motions d'empathie
	 * @return la somme des �motions d'empathie n�gatives qu'� l'agent envers agNameOriented
	 * */
	public double sommeEmoNegEmp(Term agNameOriented){
		double result=0;
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agNameOriented.toString().contains(emp.getAgentOriented().toString()) ||
					emp.getAgentOriented().toString().contains(agNameOriented.toString()));
			if(b & (typeToString(emp.getType()).equals("tristes")
						||typeToString(emp.getType()).equals("frustr")
						||typeToString(emp.getType()).equals("enerv")
						||typeToString(emp.getType()).equals("colere"))){
					result+=emp.getIntensity();
			}
		}
		return result;
	}
	
	/** Retourne la somme des �motions d'empathie positives 
	 * qu'� l'agent JSA pour l'agent agNameOriented 
	 * @param agNameOriented terme d�signant l'agent vers qui sont orient�es les �motions d'empathie
	 * @return la somme des �motions d'empathie positives qu'� l'agent envers agNameOriented
	 * */
	public double sommeEmoPosEmp(Term agNameOriented){
		double result=0;
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agNameOriented.toString().contains(emp.getAgentOriented().toString()) ||
					emp.getAgentOriented().toString().contains(agNameOriented.toString()));
			if(b){
				if(typeToString(emp.getType()).equals("satisf") || typeToString(emp.getType()).equals("joie"))
					result+=emp.getIntensity();
			}
		}
		return result;
	}
	
	/** Mise � jour temporelle de l'ensemble des �motions de l'agent
	 * */ 
	public void updateEmotionTime(){
		
		/** Mise � jour des �motions non empathiques */
		joie.updateEmotionTime();
		satisf.updateEmotionTime();
		frustr.updateEmotionTime();
		tristes.updateEmotionTime();
		enerv.updateEmotionTime();
		colere.updateEmotionTime();
		
		/** Mise � jour des �motions empathiques */
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			emp.updateEmotionTime();
		}
		/** Mise � jour de l'affichae */
		updateObservers();
	}
	
	/** Diminution de la somme des �motions n�gatives non empathiques de la  valeur pass�e en param�tre
	 * @param intensiteDim valeur dont doit �tre diminue la somme des �motions n�gatives non empathiques
	 */
	public void diminutionEmotionNeg(double intensiteDim){
		int nbEmoNeg=0;
		if(this.frustr.getIntensity()>0)
			nbEmoNeg++;
		if(this.enerv.getIntensity()>0)
			nbEmoNeg++;
		if(this.tristes.getIntensity()>0)
			nbEmoNeg++;
		if(this.colere.getIntensity()>0)
			nbEmoNeg++;
		
		double intensiteDimEmo=intensiteDim/nbEmoNeg;
		
		if(this.frustr.getIntensity()>= intensiteDimEmo)
			this.frustr.diminution(intensiteDimEmo);
		else{
			double d=intensiteDimEmo-this.frustr.getIntensity();
			this.frustr.diminution(this.frustr.getIntensity());
			intensiteDimEmo=intensiteDimEmo+d/(nbEmoNeg-1);
		}
		if(this.enerv.getIntensity()>= intensiteDimEmo)
			this.enerv.diminution(intensiteDimEmo);
		else{
			double d=intensiteDimEmo-this.enerv.getIntensity();
			this.enerv.diminution(this.enerv.getIntensity());
			intensiteDimEmo=intensiteDimEmo+d/(nbEmoNeg-2);
		}
		
		if(this.tristes.getIntensity()>= intensiteDimEmo)
			this.tristes.diminution(intensiteDimEmo);
		else{
			double d=intensiteDimEmo-this.tristes.getIntensity();
			this.tristes.diminution(this.tristes.getIntensity());
			intensiteDimEmo=intensiteDimEmo+d/(nbEmoNeg-3);
		}
		
		if(this.colere.getIntensity()>= intensiteDimEmo)
			this.colere.diminution(intensiteDimEmo);
		else
			this.colere.diminution(this.colere.getIntensity());
		
	}
	
	/** Diminution de la somme des �motions n�gatives empathiques de la  valeur pass�e en param�tre
	 * @param intensiteDim valeur dont doit �tre diminu� la somme des �motions n�gatives empathiques
	 * @param agNameOriented terme d�signant l'agent vers qui est orient�e les m�otions d'empathie desquelles on souhaite modifier la valeur
	 */
	public void diminutionEmotionNegEmp(double intensiteDim, Term agNameOriented){
		int nbEmoNeg=0;
		
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agNameOriented.toString().contains(emp.getAgentOriented().toString()) ||
					emp.getAgentOriented().toString().contains(agNameOriented.toString()));
			if(b){
				String type=typeToString(emp.getType());
				if(type.equals("frustr")
						|| type.equals("enerv")
						|| type.equals("tristes")
						|| type.equals("colere")){
					if(emp.getIntensity()>0){
						nbEmoNeg++;
					}
				}
			}
		}
		double intensiteDimEmo=intensiteDim/nbEmoNeg;
		
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agNameOriented.toString().contains(emp.getAgentOriented().toString()) ||
					emp.getAgentOriented().toString().contains(agNameOriented.toString()));
			if(b){
				String type=typeToString(emp.getType());
				if(type.equals("frustr")
						|| type.equals("enerv")
						|| type.equals("tristes")
						|| type.equals("colere")){
					if(emp.getIntensity()>= intensiteDimEmo){
						emp.diminution(intensiteDimEmo);
					}else{
						double d=intensiteDimEmo-emp.getIntensity();
						emp.diminution(emp.getIntensity());
						if(nbEmoNeg>0){
							nbEmoNeg--;
							intensiteDimEmo=intensiteDimEmo+d/(nbEmoNeg);
						}
						
					}
				}
			}
		}
		
	}
	
	/** Diminution de la somme des �motions positives non empathiques de la  valeur pass�e en param�tre
	 * @param intensiteDim valeur dont doit �tre diminu� la somme des �motions positives non empathiques
	*/
	public void diminutionEmotionPos(double intensiteDim){
		int nbEmoPos=0;
		if(this.joie.getIntensity()>0)
			nbEmoPos++;
		if(this.satisf.getIntensity()>0)
			nbEmoPos++;
		
		double intensiteDimEmo=intensiteDim/nbEmoPos;
		if(this.joie.getIntensity()>=intensiteDimEmo)
			this.joie.diminution(intensiteDimEmo);
		else{
			double d=intensiteDimEmo-this.joie.getIntensity();
			this.joie.diminution(this.joie.getIntensity());
			intensiteDimEmo+=d;
		}
		if(this.satisf.getIntensity()>=intensiteDimEmo)
			this.satisf.diminution(intensiteDimEmo);
		else
			this.satisf.diminution(this.satisf.getIntensity());
	}
	
	/** Diminution de la somme des �motions positives empathiques de la  valeur pass�e en param�tre
	 * @param intensiteDim valeur dont doit �tre diminu� la somme des �motions positives empathiques
	 * @param agNameOriented terme d�signant l'agent vers qui est orient�e les �motions d'empathie desquelles on souhaite modifier la valeur
	 */
	public void diminutionEmotionPosEmp(double intensiteDim, Term agNameOriented){
		int nbEmoPos=0;
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agNameOriented.toString().contains(emp.getAgentOriented().toString()) || 
					emp.getAgentOriented().toString().contains(agNameOriented.toString()));
			if(b){
				String type=typeToString(emp.getType());
				if(type.equals("joie") || type.equals("satisf")){
					if(emp.getIntensity()>0){
						nbEmoPos++;
					}
				}
			}
		}
		
		double intensiteDimEmo=intensiteDim/nbEmoPos;
		
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agNameOriented.toString().contains(emp.getAgentOriented().toString())||
					emp.getAgentOriented().toString().contains(agNameOriented.toString()));
			if(b){
				String type=typeToString(emp.getType());
				if(type.equals("joie") || type.equals("satisf")){
					if(emp.getIntensity()>=intensiteDimEmo){
						emp.diminution(intensiteDimEmo);
					}else{
						double d=intensiteDimEmo-emp.getIntensity();
						emp.diminution(emp.getIntensity());
						intensiteDimEmo+=d;
					}
				}
			}
		}
		
		
	}
	
	/** Filtre qui calcule l'intensit� de l'�motion d�clench�e �tant donn�e l'�tat �motionnel courant de l'agent 
	 * et diminue les �motions courantes de l'agent suivant cette nouvelle �motion d�clench�e 
	 * @param type type de l'�motion qui vient d'�tre d�clench�e
	 * @param intDeclenche valeur de l'intensit� de l'�motion qui vient d'�tre d�clench�e
	 * @return intensit� de l'�motion d�clench�e �tant donn� l'�tat �motionnel courant de l'agent
	 * */
	public double filtre(Emotion.Type type, double intDeclenche){
		double newIntensity=0; //nouvelle valeur d'intensit� 
		/** Filtre sur une �motion d�clench�e positive */
		if(type.equals(Emotion.Type.JOIE) ||type.equals(Emotion.Type.SATISF)){
			double sommeEmotionNeg=this.sommeEmoNeg();
			newIntensity=intDeclenche/(sommeEmotionNeg+1);
			/** Diminution des �motions n�gatives courantes de l'agent */
			diminutionEmotionNeg(newIntensity);
		}else{
			/** Si l'�motion d�clench�e est n�gative */
			newIntensity=intDeclenche/(this.sommeEmoPos()+1);
			/** Diminution des �motions positives de l'agent */
			diminutionEmotionPos(newIntensity);
		}
		return newIntensity;
	}
	
	/** Calcule l'intensit� d'une �motion 
	 * suivant les valeurs des variables d'intensit� pass�es en param�tre 
	 * @param degre_certitude degr� de certitude 
	 * @param pot_reaction potentiel de r�action
	 * @param effort effort r�alis�
	 * @param imp importance 
	 * @return intensit� de l'�motion suivant les valeurs pass�es en param�tre
	 * */
	public double fonction_intensite(double degre_certitude, double pot_reaction, double effort, double imp){
		
		double max_effort=3; /** On suppose que l'effort maximum est de 5 */	
		// l'intensit� d'une impulsion �motionnelle est  comprise entre 0 et 1
		double i = degre_certitude*pot_reaction*(effort/max_effort)*imp;
		return i;
		
	}
	
	/** Retourne le dernier effort r�alis� par l'agent 
	 * @return dernier effort r�alis� par l'agent
	 */
	public double getLastEffort(){
		return this.lastEffort;
	}
	
	/** M�thode appel� par l'agent pour le d�clenchement d'une �motion non empathique 
	 * @param valence valence de l'�motion � d�clencher
	 * @param degre_certitude degr� de certitude de l'agent
	 * @param pot_reaction potentiel de r�action de l'agent
	 * @param effort r�alis� par l'agent 
	 * @param imp importance pour l'agent 
	 * @param action terme d�signant l'action ayant d�clench� l'�motion */
	public void newElicitedEmotionNE(String valence, double degre_certitude, double pot_reaction, double effort, double imp, Term action){
		this.lastEffort=effort;
		double intensiteEmoDecl=0;
		if(valence.equals("pos")){
			intensiteEmoDecl=fonction_intensite(1-degre_certitude,1,effort, imp);
			if(degre_certitude<=0.5){
				this.historiqueEmotionDeclenchee.add(new EmotionDeclenchee("joie", intensiteEmoDecl,action));
				updateEmotionNonEmpathie("joie", intensiteEmoDecl);
			}else{
				this.historiqueEmotionDeclenchee.add(new EmotionDeclenchee("satisfaction", intensiteEmoDecl,action));
				updateEmotionNonEmpathie("satisf", intensiteEmoDecl);
			}
			
		}else{
			if(valence.equals("neg")){
				if(degre_certitude>0.5){
					double intEmo=fonction_intensite(degre_certitude,1, effort, imp);
					this.historiqueEmotionDeclenchee.add(new EmotionDeclenchee("frustration", intEmo,action));
					updateEmotionNonEmpathie("frustr", intEmo);
					//connexionEloona.println("C'est frustrant !");
					
				}
				if(pot_reaction<=0.5){
						double intEmo=fonction_intensite(1,(1-pot_reaction), effort, imp);
						this.historiqueEmotionDeclenchee.add(new EmotionDeclenchee("tristesse", intEmo,action));
						updateEmotionNonEmpathie("tristes", intEmo);
					//	connexionEloona.println("C'est triste !");
						
				}else{
					double intEmo=fonction_intensite(1,pot_reaction, effort, imp);
					this.historiqueEmotionDeclenchee.add(new EmotionDeclenchee("enervement", intEmo,action));
					updateEmotionNonEmpathie("enerv", intEmo);
					//connexionEloona.println("C'est �nervant !");
					
				}
			}else{
				if(valence.equals("colere")){
					double intEmo=fonction_intensite(1,1, effort, imp);
					updateEmotionNonEmpathie("colere", intEmo);
				}
			}
		}
		/** Mise � jour de l'interface */
		updateObservers();
		
		
	}
	
	
	/** M�thode appel�e par l'agent pour le d�clenchement d'une �motion empathique 
	 * @param valence valence de l'�motion � d�clencher
	 * @param degre_certitude degr� de certitude de l'agent vers qui est orient�e l'�motion d'empathie
	 * @param pot_reaction potentiel de r�action de l'agent vers qui est orient�e l'�motion d'empathie
	 * @param effort r�alis� par l'agent vers qui est orient�e l'�motion d'empathie
	 * @param imp importance pour l'agent vers qui est orient�e l'�motion d'empathie
	 * @param action terme d�signant l'action ayant d�clench� l'�motion 
	 * @param agNameOriented terme d�signant l'agent vers qui est orient� l'�motion d'empathie
	 * */
	public void newElicitedEmotion(String valence, double degre_certitude, double pot_reaction, double effort, double imp, Term action, Term agNameOriented){
		
		double intensiteEmoDecl=0;
		
		if(valence.equals("pos")){
			intensiteEmoDecl=fonction_intensite(1-degre_certitude, 1, effort, imp);
			if(degre_certitude<=0.5){
				EmotionDeclenchee newE=new EmotionDeclenchee("joie", intensiteEmoDecl,action, agNameOriented);
				this.historiqueEmotionDeclenchee.add(newE);
				this.lastEmpathicEmotion=newE;
				addEmotionEmpathie("joie", intensiteEmoDecl,agNameOriented);
			}else{
				EmotionDeclenchee newE=new EmotionDeclenchee("satisfaction", intensiteEmoDecl,action, agNameOriented);
				this.historiqueEmotionDeclenchee.add(newE);
				this.lastEmpathicEmotion=newE;
				addEmotionEmpathie("satisf", intensiteEmoDecl,agNameOriented);
			}
		}else{
			if(valence.equals("neg")){
				if(degre_certitude>0.5){
					double intEmo=fonction_intensite(degre_certitude,1, effort, imp);
					EmotionDeclenchee newE=new EmotionDeclenchee("frustration", intEmo,action, agNameOriented);
					this.historiqueEmotionDeclenchee.add(newE);
					this.lastEmpathicEmotion=newE;
					addEmotionEmpathie("frustr", intEmo, agNameOriented);
					
				}
				if(pot_reaction<=0.5){
					double intEmo=fonction_intensite(1,(1-pot_reaction), effort, imp);
					EmotionDeclenchee newE=new EmotionDeclenchee("tristesse", intEmo,action, agNameOriented);
					this.historiqueEmotionDeclenchee.add(newE);
					this.lastEmpathicEmotion=newE;
					addEmotionEmpathie("tristes", intEmo,agNameOriented);
				
				}else{
					double intEmo=fonction_intensite(1,pot_reaction, effort, imp);
					EmotionDeclenchee newE=new EmotionDeclenchee("enervement", intEmo,action, agNameOriented);
					this.historiqueEmotionDeclenchee.add(newE);
					this.lastEmpathicEmotion=newE;
					addEmotionEmpathie("enerv", intEmo, agNameOriented);
				
				}
		
			}else{
				if(valence.equals("colere")){
					double intEmo=fonction_intensite(1,1, effort, imp);
					this.lastEmpathicEmotion=new EmotionDeclenchee("colere", intEmo,action, agNameOriented);
					addEmotionEmpathie("colere", intEmo, agNameOriented);
				}
			}
		}
		updateObservers();
		
		
	}
	
	/** Mise � jour d'une m�otion d'empathie suite au d�clenchement d'une nouvelle �motion d'empathie 
	 * @param typeS type de l'�motion d'empathie qui vient d'�tre d�clench�e
	 * @param intensiteDecl intensit� de l'�motion d�clench�e
	 * @param agOrien terme d�signant l'agent vers qui est orient�e l'�motion d'empathie
	 * */
	public void updateEmotionEmpathie(String typeS, double intensiteDecl, Term agOrien){
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agOrien.toString().contains(emp.getAgentOriented().toString())||
					emp.getAgentOriented().toString().contains(agOrien.toString()));
			if(typeToString(emp.getType()).equals(typeS) & b)
				emp.update(intensiteDecl);
		}
		
	}
	
	/** Mise � jour des �motions courantes non empathiques de l'agent 
	 * �tant donn� une nouvelle �motion d�clench�e dont l'intensit� est filtr� dans la m�thode par l'�tat �motionnel courant de l'agent
	 * @param typeS type de l'�motion d�clench�e 
	 * @param intensiteDecl intensit� (non filtr�e) de l'�motion d�clench�e 
	 * */
	public void updateEmotionNonEmpathie(String typeS, double intensiteDecl){
		Emotion.Type type=stringToType(typeS);
		double intensite=filtre(type,intensiteDecl);
		switch(type) {		
	      case JOIE :
	      	 joie.update(intensite);
		     break;	
	      case SATISF :
	      	 satisf.update(intensite);
		     break;		
	      case FRUSTR :
	      	 frustr.update(intensite);
		     break;
	      case TRISTES :
	      	tristes.update(intensite);
		     break;	
	      case ENERV :
	      	 enerv.update(intensite);
		     break;
	      case COLERE :
	      	colere.update(intensite);
		    break;
	    }	
	}
	
	/** Retourne l'objet Emotion non empathique suivant le type pass� en param�tre
	 * @param type type de l'�motion dont on souhaite r�cup�r� l'objet �motion
	 * @return objet �motion du type pass� en param�tre */
	public Emotion typeToEmotion(Emotion.Type type){
		 switch(type) {
	      case JOIE :
	      	 return joie;
	      case SATISF :
	      	 return satisf;
	      case FRUSTR :
	      	 return frustr;
	      case TRISTES :
	      	return tristes;
	      case ENERV :
	      	return enerv;
	      case COLERE :
	      	return colere;
	    }	
		 return null;
	}
	
	/** Retourne � partir d'un string le type equivalent 
	 * @param s string dont on cherche le type
	 * @return type correspondant au string pass� en param�tre
	 * */
	public static Type stringToType(String s){
		if(s.equals("joie"))
			return Emotion.Type.JOIE;
		if(s.equals("satisf"))
			return Emotion.Type.SATISF;
		if(s.equals("frustr"))
			return Emotion.Type.FRUSTR;
		if(s.equals("tristes"))
			return Emotion.Type.TRISTES;
		if(s.equals("enerv"))
			return Emotion.Type.ENERV;
		if(s.equals("colere"))
				return Emotion.Type.COLERE;
		return null;
	}
	
	/** Return � partir d'un type le string correspondant 
	 * @param type type dont on cherche le string
	 * @return string correspond au type pass� en param�tre
	 * */
	public static String typeToString(Type type){
		switch(type) {
	      case JOIE :
	      	 return "joie";
	      case SATISF :
	      	return "satisf";
	      case FRUSTR :
	      	return "frustr";
	      case TRISTES :
	      	return "tristes";
	      case ENERV :
	      	return "enerv";
	      case COLERE :
	      	return "colere";
	    }	
		return null;
	}
	
	/** Return � partir de l'objet type le nom complet de l'�motion permettant son affichage 
	 * @param type type de l'�motion donto n cherche le nom complet 
	 * @return string correspondant au nom complet de l'�motion
	 * */
	public static String typeToString2(Type type){
		switch(type) {
	      case JOIE :
	      	 return "joie";
	      case SATISF :
	      	return "satisfaction";
	      case FRUSTR :
	      	return "frustration";
	      case TRISTES :
	      	return "tristesse";
	      case ENERV :
	      	return "enervement";
	      case COLERE :
	      	return "colere";
	      
	    }	
		return null;
	}
	
	/** Retourne vrai si le string pass� en param�tre repr�sente un type d'�motion connue par l'agent, faux sinon
	 * @param s string dont on souhaite connaitre si c'est un type d'�motion connu par l'agent
	 * @return valeur de v�rit� sur le fait que le string pass� en param�tre est un type d'�motion */
	public static boolean isAType(String s){
		if(s.equals("joie") ||s.equals("satisf")||s.equals("frustr") ||s.equals("tristes") ||s.equals("enerv")||s.equals("colere"))
			return true;
		else
			return false;
	}
	
	/** Etant donn�e une �motion d�clench�e d'empathie, calcule la valeur d'intensit� de cette �motion filtr�e 
	 * suivant les �motions d'empathie pr�existante et m�t � jour les �motions courantes empathique des l'agent suivant la valeur d'intensit� filtr�e
	 * @param type type de l'�motion empathique d�clench�e
	 * @param agNameOriented terme d�signant l'agent vers qui est orient�e l'�motion d'empathie
	 * @param intensiteD intensi�t de l'�motion d�clench�e (avant filtrage)
	 * @return valeur d'intensit� de l'�motion d�clench�e filtr�e (i.e. modifi�e suivant la valeur d'intensit� des �motions courantes empathiques de l'agent)
	 */
	public double filtreEmotionEmpathie(Emotion.Type type, Term agNameOriented, double intensiteD){
		double newIntensity=0; //nouvelle valeur d'intensit� 
		/** Filtre sur une �motion d�clench�e positive */
		if(type.equals(Emotion.Type.JOIE) ||type.equals(Emotion.Type.SATISF)){
			double sommeEmotionNeg=sommeEmoNegEmp(agNameOriented);
			newIntensity=intensiteD/(sommeEmotionNeg+1);
			
			/** Diminution des �motions n�gatives courantes de l'agent */	
			diminutionEmotionNegEmp(newIntensity, agNameOriented);
		}else{
			/** Si l'�motion d�clench�e est n�gative */
			newIntensity=intensiteD/(sommeEmoPosEmp(agNameOriented)+1);
			/** Diminution des �motions positives de l'agent */
			diminutionEmotionPosEmp(newIntensity, agNameOriented);
		}
		return newIntensity;
	}
	
	/** Ajoute une �motion d'empathie � la liste des m�otions d'empathie de l'agent
	 * @param typeS type de l'�motion d'empathie � ajouter 
	 * @param intensiteDecl intensit� de l'�motion d'empathie d�clench�e (l'intensit� est filtr�e dans cette m�thode)
	 * @param  agNameOriented agent vers qui est orient�e l'�motion d'empathie � ajouter 
	 * */
	public void addEmotionEmpathie(String typeS,double intensiteDecl, Term agNameOriented){
		
		Emotion.Type type=stringToType(typeS);
		
		/** La valeur d'intensit� de l'�motion d�clench�e d'empathie est calcul� suivant 
		 * le taux d'empathie de l'agent
		 */
		double intensite=empathyRate*intensiteDecl;
		
		
		/** si l'�motion d'empthie existe d�j�, on met l'intensit� � jour */
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agNameOriented.toString().contains(emp.getAgentOriented().toString()) ||
					emp.getAgentOriented().toString().contains(agNameOriented.toString()));		
			if(typeToString(emp.getType()).equals(typeS) & b){
				/** Filtre de l'intensit� de l'�motion d�clench�e d'empathie */
				double intensityFiltre=filtreEmotionEmpathie(type, agNameOriented, intensite);
				emp.update(intensityFiltre);
				return;
			}
		}
		/** Si l'�motion d'empathie n'existait pas, on la cr�e et l'ajoute � la liste */
		emotionEmpathie.add(new Emotion(agentName, type, intensite, agNameOriented));
	}
		
	
	/** M�thode permettant l'affichage de l'�tat �motionnel de l'agent sur une sortie standard 
	 * */
	public void displayEmotion(){
		System.err.println("-------------------- EMOTION ----------------");
		System.err.println("Joie : "+joie.getIntensity());
		System.err.println("Satisf : "+satisf.getIntensity());
		System.err.println("Frustr : "+frustr.getIntensity());
		System.err.println("Tristes : "+tristes.getIntensity());
		System.err.println("Enerv : "+enerv.getIntensity());
		System.err.println("Colere : "+colere.getIntensity());
		System.err.println("-------------------- EMOTION EMPATHIE----------------");
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			System.err.println(typeToString(emp.getType())+" "+emp.getIntensity()+" "+emp.getAgentOriented().toString());
		}
	}
	
	/** M�thode permettant l'affichage (incluant des tags html) de l'�tat �motionnel non empathique de l'agent 
	 * @return string permettant l'affiche de l'�tat �motionnel non empathique de l'agent*/
	public String etatEmotionnelNEToString(){
		StringBuffer affichage = new StringBuffer("");
		affichage=affichage.append("<html>");
		affichage=affichage.append("<i> Emotion positive : </i> <br>");
		//affichage=affichage.append("<BLOCKQUOTE> Joie : "+joie.getIntensity()+"<br>");
		affichage=affichage.append("<BLOCKQUOTE> Satisfaction : "+satisf.getIntensity()+" </BLOCKQUOTE><br> <br>");
		affichage=affichage.append("<i>Emotion n�gative : </i> <br>");
		affichage=affichage.append("<BLOCKQUOTE> Frustration : "+frustr.getIntensity()+"<br>");
		affichage=affichage.append("Tristesse : "+tristes.getIntensity()+"<br>");
		affichage=affichage.append("Irritation : "+enerv.getIntensity()+" <br>");
		affichage=affichage.append("Col�re : "+colere.getIntensity()+" </BLOCKQUOTE> <br>");
		affichage=affichage.append("</html>");
		return affichage.toString();
	}
	
	/** M�thode permettant l'affichage (incluant des tags html) de l'�tat �motionnel empathique de l'agent 
	 * (on suppose ici qu'il existe un unique autre agent vers qui l'agent peut avoir des �motions d'empathie; c'est le cas dans l'interaction humain-machine)
	 * @return string permettant l'affiche de l'�tat �motionnel empathique de l'agent*/
	public String etatEmotionnelEToString(){
		StringBuffer affichage = new StringBuffer("");
		affichage=affichage.append("<html>");
		affichage=affichage.append("<i> Emotion positive : </i> <br>");
		//affichage=affichage.append("<BLOCKQUOTE> Joie : "+this.getIntensityEmp(Emotion.Type.JOIE)+"<br>");
		affichage=affichage.append("<BLOCKQUOTE> Satisfaction : "+this.getIntensityEmp(Emotion.Type.SATISF)+" </BLOCKQUOTE><br> <br>");
		affichage=affichage.append("<i>Emotion n�gative : </i> <br>");
		affichage=affichage.append("<BLOCKQUOTE> Frustration : "+this.getIntensityEmp(Emotion.Type.FRUSTR)+"<br>");
		affichage=affichage.append("Tristesse : "+this.getIntensityEmp(Emotion.Type.TRISTES)+"<br>");
		affichage=affichage.append("Enervement : "+this.getIntensityEmp(Emotion.Type.ENERV)+" <br>");
		affichage=affichage.append("Col�re : "+this.getIntensityEmp(Emotion.Type.COLERE)+" </BLOCKQUOTE> <br>");
		affichage=affichage.append("</html>");
		
		return affichage.toString();
	}
	
	/** M�thode permettant l'affichage (incluant des tags html) de l'historique des �motions d�clench�es de l'agent 
	 * @return string permettant l'affiche de l'historique des �motions d�clench�es de l'agent
	 * */
	public String historiqueEmotionDeclToString(){
		StringBuffer affichage = new StringBuffer("");
		affichage=affichage.append("<html> ");
		
		for(int i=0; i<this.historiqueEmotionDeclenchee.size(); i++){	
			EmotionDeclenchee emoDecl=(EmotionDeclenchee)this.historiqueEmotionDeclenchee.get(i);
			affichage=affichage.append(emoDecl.toString());
			affichage=affichage.append("<br> ");
		}
		affichage=affichage.append(" </html>");
		return affichage.toString();
		
	}
	
	/** Retourne la derni�re action ayant d�clench�e une �motion 
	 * @return terme d�sigant la derni�re action ayant d�clench�e une �motion
	 * */
	public Term getLastAction(){
		for(int i=this.historiqueEmotionDeclenchee.size()-1; i>=0; i--){
			EmotionDeclenchee eD=(EmotionDeclenchee)this.historiqueEmotionDeclenchee.get(i);
			if(eD.getAgentOriented()!=null){
				return eD.getAction();
			}
		}
		return null;
	}
	
	/** Retourne l'intensit� de l'�motion d'empathie du type pass� en param�tre 
	* (on suppose ici qu'il existe un unique autre agent vers qui l'agent peut avoir des �motions d'empathie; c'est le cas dans l'interaction humain-machine)
	* @return intensit� de l'�motion d'empathie de l'agent du type pass� en param�tre
	*/
	public double getIntensityEmp(Emotion.Type type){
		for(int i=0; i<this.emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			if(emp.getType().equals(type)){
				return emp.getIntensity();
			}
		}
		return 0;
	}

}
