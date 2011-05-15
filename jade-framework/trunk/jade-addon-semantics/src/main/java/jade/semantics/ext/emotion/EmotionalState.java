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

import jade.semantics.ext.emotion.Emotion.Type;
import jade.semantics.lang.sl.grammar.Term;
import jade.util.leap.ArrayList;

//import java.io.BufferedWriter;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.Socket;

/** Constructeur de l'objet état émotionnel regroupant toutes les émotions de l'agent 
 * 
 */
public class EmotionalState {
	
	/** Nom de l'agent qui a cet état émotionnel */
	private Term agentName; 
	
	/** Observers de l'état émotionel, pemettant par exemple la MaJ de'IHM graphiques */
	private ArrayList observers;
	
	/** Emotions courantes non empathiques de l'agent */
	private Emotion joie;
	private Emotion satisf;
	private Emotion frustr;
	private Emotion tristes;
	private Emotion enerv;
	private Emotion colere; 
	
	/** Liste des émotions empathiques de l'agent */
	private ArrayList emotionEmpathie; 
	
	/** Dernière émotion d'empathie déclenchée */
	private EmotionDeclenchee lastEmpathicEmotion;
	
	/** Taux d'empathie compris entre 0 et 1
	 * Si =0 alors  agent non empathique
	 * Si =1 alors agent complétement empathique : ressent les même émotion avec la même intensité 
	 * que celle qu'il pense que son interlocuteur ressent
	 * */
	private double empathyRate;
	
	/** Coefficient de caricature de l'expression faciale compris entre 0 et 100 
	 * Si égale à 0 alors l'intensité de l'expression faciale est égale à l'intensité de l'émotion
	 * Plus le coefficient est grand plus l'expression faciale est exagérée
	 */
	private int coeffExprFaciale;
	
	/** Dernier effort réalisé par l'agent*/
	private double lastEffort;
	
	/** Historique des émotions déclenchées */
	private ArrayList historiqueEmotionDeclenchee;
		
//	/** Connexion socket avec Eloona pour l'affichage des expressions faciales d'émotions */
//	private PrintWriter connexionEloona;
	
	/** Vraie si l'agent exprime que ses émotions d'empathie, faux si il exprime ses émotions non empathiques */ 
	private boolean agentEmpathique;
	
	/** Vraie si l'agent exprime uniquement les émotions déclenchées empathiques*/
	private boolean expressionEmoDeclenchee;
	
	/** Constructeur d'un état émotionnel 
	 * @param ag agent qui a cet état émotionnel
	 */
	public EmotionalState(Term ag){
		
		agentName=ag;
		observers = new ArrayList();
		/** Création de la liste vide d'émotions d'empathie*/
		emotionEmpathie=new ArrayList();
		
		/** Création des émotions non empathiques initiales de l'agent */
		joie=new Emotion(agentName, Emotion.Type.JOIE, 0);
		satisf=new Emotion(agentName, Emotion.Type.SATISF, 0);
		frustr=new Emotion(agentName, Emotion.Type.FRUSTR, 0);
		tristes=new Emotion(agentName, Emotion.Type.TRISTES, 0);
		enerv=new Emotion(agentName, Emotion.Type.ENERV, 0);
		colere=new Emotion(agentName, Emotion.Type.COLERE, 0);
				 
//		/** Création de la connexion avec Eloona pour l'affichage des expressions faciales d'émotion*/
//		try{
//			Socket s = new Socket("127.0.0.1",18000);
//			OutputStream os = s.getOutputStream();
//			connexionEloona = new PrintWriter(new BufferedWriter(
//                   new OutputStreamWriter(os)), 
//                   true);
//		}catch(Exception e){
//			System.err.println(e);
//		}
	
		/** Création de l'historique des émotions déclenchées */
		historiqueEmotionDeclenchee=new ArrayList();
		
		/** Le taux d'empathie est par défaut égale à 1 */
		empathyRate=1;	
		
		/** On fixe le coefficient de caricature de l'expression faciale à 60 */
		coeffExprFaciale=90;
		
		/** Agent est empathique par défaut :exprime uniquement ses émotions d'empathie */
		agentEmpathique=true;
		/** Agent exprime uniquement les émotions déclenchées empathiques */ 
		expressionEmoDeclenchee=true;
	}
	
	/** Méthode qui met à zéro toutes les émotions de l'agent 
	 * et met à jour l'affichage
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
	
	/** Méthode qui retourne la somme des émotions négatives non empathiques
	 * @return la somme des intensité des émotions négatives non empathiques de l'agent 
	 * **/
	public double sommeEmoNeg(){
		return (this.frustr.getIntensity()+ this.enerv.getIntensity()+this.tristes.getIntensity()+ colere.getIntensity());
	}
	
	/** Méthode qui retourne la somme des émotions positives non empathiques
	 * @return la somme des intensité des émotions positives non empathiques de l'agent 
	 * **/
	public double sommeEmoPos(){
		return (this.joie.getIntensity()+this.satisf.getIntensity());
	}
	
	/** Retourne la somme des émotions d'empathie négatives (neg et colère)
	 * qu'à l'agent JSA pour l'agent agNameOriented 
	 * @param agNameOriented terme désignant l'agent vers qui sont orientées les émotions d'empathie
	 * @return la somme des émotions d'empathie négatives qu'à l'agent envers agNameOriented
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
	
	/** Retourne la somme des émotions d'empathie positives 
	 * qu'à l'agent JSA pour l'agent agNameOriented 
	 * @param agNameOriented terme désignant l'agent vers qui sont orientées les émotions d'empathie
	 * @return la somme des émotions d'empathie positives qu'à l'agent envers agNameOriented
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
	
	/** Mise à jour temporelle de l'ensemble des émotions de l'agent
	 * */ 
	public void updateEmotionTime(){
		
		/** Mise à jour des émotions non empathiques */
		joie.updateEmotionTime();
		satisf.updateEmotionTime();
		frustr.updateEmotionTime();
		tristes.updateEmotionTime();
		enerv.updateEmotionTime();
		colere.updateEmotionTime();
		
		/** Mise à jour des émotions empathiques */
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			emp.updateEmotionTime();
		}
		/** Mise à jour de l'affichae */
		updateObservers();
	}
	
	/** Diminution de la somme des émotions négatives non empathiques de la  valeur passée en paramètre
	 * @param intensiteDim valeur dont doit être diminue la somme des émotions négatives non empathiques
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
	
	/** Diminution de la somme des émotions négatives empathiques de la  valeur passée en paramètre
	 * @param intensiteDim valeur dont doit être diminué la somme des émotions négatives empathiques
	 * @param agNameOriented terme désignant l'agent vers qui est orientée les méotions d'empathie desquelles on souhaite modifier la valeur
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
	
	/** Diminution de la somme des émotions positives non empathiques de la  valeur passée en paramètre
	 * @param intensiteDim valeur dont doit être diminué la somme des émotions positives non empathiques
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
	
	/** Diminution de la somme des émotions positives empathiques de la  valeur passée en paramètre
	 * @param intensiteDim valeur dont doit être diminué la somme des émotions positives empathiques
	 * @param agNameOriented terme désignant l'agent vers qui est orientée les émotions d'empathie desquelles on souhaite modifier la valeur
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
	
	/** Filtre qui calcule l'intensité de l'émotion déclenchée étant donnée l'état émotionnel courant de l'agent 
	 * et diminue les émotions courantes de l'agent suivant cette nouvelle émotion déclenchée 
	 * @param type type de l'émotion qui vient d'être déclenchée
	 * @param intDeclenche valeur de l'intensité de l'émotion qui vient d'être déclenchée
	 * @return intensité de l'émotion déclenchée étant donné l'état émotionnel courant de l'agent
	 * */
	public double filtre(Emotion.Type type, double intDeclenche){
		double newIntensity=0; //nouvelle valeur d'intensité 
		/** Filtre sur une émotion déclenchée positive */
		if(type.equals(Emotion.Type.JOIE) ||type.equals(Emotion.Type.SATISF)){
			double sommeEmotionNeg=this.sommeEmoNeg();
			newIntensity=intDeclenche/(sommeEmotionNeg+1);
			/** Diminution des émotions négatives courantes de l'agent */
			diminutionEmotionNeg(newIntensity);
		}else{
			/** Si l'émotion déclenchée est négative */
			newIntensity=intDeclenche/(this.sommeEmoPos()+1);
			/** Diminution des émotions positives de l'agent */
			diminutionEmotionPos(newIntensity);
		}
		return newIntensity;
	}
	
	/** Calcule l'intensité d'une émotion 
	 * suivant les valeurs des variables d'intensité passées en paramètre 
	 * @param degre_certitude degré de certitude 
	 * @param pot_reaction potentiel de réaction
	 * @param effort effort réalisé
	 * @param imp importance 
	 * @return intensité de l'émotion suivant les valeurs passées en paramètre
	 * */
	public double fonction_intensite(double degre_certitude, double pot_reaction, double effort, double imp){
		
		double max_effort=3; /** On suppose que l'effort maximum est de 5 */	
		// l'intensité d'une impulsion émotionnelle est  comprise entre 0 et 1
		double i = degre_certitude*pot_reaction*(effort/max_effort)*imp;
		return i;
		
	}
	
	/** Retourne le dernier effort réalisé par l'agent 
	 * @return dernier effort réalisé par l'agent
	 */
	public double getLastEffort(){
		return this.lastEffort;
	}
	
	/** Méthode appelé par l'agent pour le déclenchement d'une émotion non empathique 
	 * @param valence valence de l'émotion à déclencher
	 * @param degre_certitude degré de certitude de l'agent
	 * @param pot_reaction potentiel de réaction de l'agent
	 * @param effort réalisé par l'agent 
	 * @param imp importance pour l'agent 
	 * @param action terme désignant l'action ayant déclenché l'émotion */
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
					//connexionEloona.println("C'est énervant !");
					
				}
			}else{
				if(valence.equals("colere")){
					double intEmo=fonction_intensite(1,1, effort, imp);
					updateEmotionNonEmpathie("colere", intEmo);
				}
			}
		}
		/** Mise à jour de l'interface */
		updateObservers();
		
		
	}
	
	
	/** Méthode appelée par l'agent pour le déclenchement d'une émotion empathique 
	 * @param valence valence de l'émotion à déclencher
	 * @param degre_certitude degré de certitude de l'agent vers qui est orientée l'émotion d'empathie
	 * @param pot_reaction potentiel de réaction de l'agent vers qui est orientée l'émotion d'empathie
	 * @param effort réalisé par l'agent vers qui est orientée l'émotion d'empathie
	 * @param imp importance pour l'agent vers qui est orientée l'émotion d'empathie
	 * @param action terme désignant l'action ayant déclenché l'émotion 
	 * @param agNameOriented terme désignant l'agent vers qui est orienté l'émotion d'empathie
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
	
	/** Mise à jour d'une méotion d'empathie suite au déclenchement d'une nouvelle émotion d'empathie 
	 * @param typeS type de l'émotion d'empathie qui vient d'être déclenchée
	 * @param intensiteDecl intensité de l'émotion déclenchée
	 * @param agOrien terme désignant l'agent vers qui est orientée l'émotion d'empathie
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
	
	/** Mise à jour des émotions courantes non empathiques de l'agent 
	 * étant donné une nouvelle émotion déclenchée dont l'intensité est filtré dans la méthode par l'état émotionnel courant de l'agent
	 * @param typeS type de l'émotion déclenchée 
	 * @param intensiteDecl intensité (non filtrée) de l'émotion déclenchée 
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
	
	/** Retourne l'objet Emotion non empathique suivant le type passé en paramètre
	 * @param type type de l'émotion dont on souhaite récupéré l'objet émotion
	 * @return objet émotion du type passé en paramètre */
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
	
	/** Retourne à partir d'un string le type equivalent 
	 * @param s string dont on cherche le type
	 * @return type correspondant au string passé en paramètre
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
	
	/** Return à partir d'un type le string correspondant 
	 * @param type type dont on cherche le string
	 * @return string correspond au type passé en paramètre
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
	
	/** Return à partir de l'objet type le nom complet de l'émotion permettant son affichage 
	 * @param type type de l'émotion donto n cherche le nom complet 
	 * @return string correspondant au nom complet de l'émotion
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
	
	/** Retourne vrai si le string passé en paramètre représente un type d'émotion connue par l'agent, faux sinon
	 * @param s string dont on souhaite connaitre si c'est un type d'émotion connu par l'agent
	 * @return valeur de vérité sur le fait que le string passé en paramètre est un type d'émotion */
	public static boolean isAType(String s){
		if(s.equals("joie") ||s.equals("satisf")||s.equals("frustr") ||s.equals("tristes") ||s.equals("enerv")||s.equals("colere"))
			return true;
		else
			return false;
	}
	
	/** Etant donnée une émotion déclenchée d'empathie, calcule la valeur d'intensité de cette émotion filtrée 
	 * suivant les émotions d'empathie préexistante et mét à jour les émotions courantes empathique des l'agent suivant la valeur d'intensité filtrée
	 * @param type type de l'émotion empathique déclenchée
	 * @param agNameOriented terme désignant l'agent vers qui est orientée l'émotion d'empathie
	 * @param intensiteD intensiét de l'émotion déclenchée (avant filtrage)
	 * @return valeur d'intensité de l'émotion déclenchée filtrée (i.e. modifiée suivant la valeur d'intensité des émotions courantes empathiques de l'agent)
	 */
	public double filtreEmotionEmpathie(Emotion.Type type, Term agNameOriented, double intensiteD){
		double newIntensity=0; //nouvelle valeur d'intensité 
		/** Filtre sur une émotion déclenchée positive */
		if(type.equals(Emotion.Type.JOIE) ||type.equals(Emotion.Type.SATISF)){
			double sommeEmotionNeg=sommeEmoNegEmp(agNameOriented);
			newIntensity=intensiteD/(sommeEmotionNeg+1);
			
			/** Diminution des émotions négatives courantes de l'agent */	
			diminutionEmotionNegEmp(newIntensity, agNameOriented);
		}else{
			/** Si l'émotion déclenchée est négative */
			newIntensity=intensiteD/(sommeEmoPosEmp(agNameOriented)+1);
			/** Diminution des émotions positives de l'agent */
			diminutionEmotionPosEmp(newIntensity, agNameOriented);
		}
		return newIntensity;
	}
	
	/** Ajoute une émotion d'empathie à la liste des méotions d'empathie de l'agent
	 * @param typeS type de l'émotion d'empathie à ajouter 
	 * @param intensiteDecl intensité de l'émotion d'empathie déclenchée (l'intensité est filtrée dans cette méthode)
	 * @param  agNameOriented agent vers qui est orientée l'émotion d'empathie à ajouter 
	 * */
	public void addEmotionEmpathie(String typeS,double intensiteDecl, Term agNameOriented){
		
		Emotion.Type type=stringToType(typeS);
		
		/** La valeur d'intensité de l'émotion déclenchée d'empathie est calculé suivant 
		 * le taux d'empathie de l'agent
		 */
		double intensite=empathyRate*intensiteDecl;
		
		
		/** si l'émotion d'empthie existe déjà, on met l'intensité à jour */
		for(int i=0; i<emotionEmpathie.size(); i++){
			Emotion emp=(Emotion)(emotionEmpathie.get(i));
			boolean b=(agNameOriented.toString().contains(emp.getAgentOriented().toString()) ||
					emp.getAgentOriented().toString().contains(agNameOriented.toString()));		
			if(typeToString(emp.getType()).equals(typeS) & b){
				/** Filtre de l'intensité de l'émotion déclenchée d'empathie */
				double intensityFiltre=filtreEmotionEmpathie(type, agNameOriented, intensite);
				emp.update(intensityFiltre);
				return;
			}
		}
		/** Si l'émotion d'empathie n'existait pas, on la crée et l'ajoute à la liste */
		emotionEmpathie.add(new Emotion(agentName, type, intensite, agNameOriented));
	}
		
	
	/** Méthode permettant l'affichage de l'état émotionnel de l'agent sur une sortie standard 
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
	
	/** Méthode permettant l'affichage (incluant des tags html) de l'état émotionnel non empathique de l'agent 
	 * @return string permettant l'affiche de l'état émotionnel non empathique de l'agent*/
	public String etatEmotionnelNEToString(){
		StringBuffer affichage = new StringBuffer("");
		affichage=affichage.append("<html>");
		affichage=affichage.append("<i> Emotion positive : </i> <br>");
		//affichage=affichage.append("<BLOCKQUOTE> Joie : "+joie.getIntensity()+"<br>");
		affichage=affichage.append("<BLOCKQUOTE> Satisfaction : "+satisf.getIntensity()+" </BLOCKQUOTE><br> <br>");
		affichage=affichage.append("<i>Emotion négative : </i> <br>");
		affichage=affichage.append("<BLOCKQUOTE> Frustration : "+frustr.getIntensity()+"<br>");
		affichage=affichage.append("Tristesse : "+tristes.getIntensity()+"<br>");
		affichage=affichage.append("Irritation : "+enerv.getIntensity()+" <br>");
		affichage=affichage.append("Colère : "+colere.getIntensity()+" </BLOCKQUOTE> <br>");
		affichage=affichage.append("</html>");
		return affichage.toString();
	}
	
	/** Méthode permettant l'affichage (incluant des tags html) de l'état émotionnel empathique de l'agent 
	 * (on suppose ici qu'il existe un unique autre agent vers qui l'agent peut avoir des émotions d'empathie; c'est le cas dans l'interaction humain-machine)
	 * @return string permettant l'affiche de l'état émotionnel empathique de l'agent*/
	public String etatEmotionnelEToString(){
		StringBuffer affichage = new StringBuffer("");
		affichage=affichage.append("<html>");
		affichage=affichage.append("<i> Emotion positive : </i> <br>");
		//affichage=affichage.append("<BLOCKQUOTE> Joie : "+this.getIntensityEmp(Emotion.Type.JOIE)+"<br>");
		affichage=affichage.append("<BLOCKQUOTE> Satisfaction : "+this.getIntensityEmp(Emotion.Type.SATISF)+" </BLOCKQUOTE><br> <br>");
		affichage=affichage.append("<i>Emotion négative : </i> <br>");
		affichage=affichage.append("<BLOCKQUOTE> Frustration : "+this.getIntensityEmp(Emotion.Type.FRUSTR)+"<br>");
		affichage=affichage.append("Tristesse : "+this.getIntensityEmp(Emotion.Type.TRISTES)+"<br>");
		affichage=affichage.append("Enervement : "+this.getIntensityEmp(Emotion.Type.ENERV)+" <br>");
		affichage=affichage.append("Colère : "+this.getIntensityEmp(Emotion.Type.COLERE)+" </BLOCKQUOTE> <br>");
		affichage=affichage.append("</html>");
		
		return affichage.toString();
	}
	
	/** Méthode permettant l'affichage (incluant des tags html) de l'historique des émotions déclenchées de l'agent 
	 * @return string permettant l'affiche de l'historique des émotions déclenchées de l'agent
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
	
	/** Retourne la dernière action ayant déclenchée une émotion 
	 * @return terme désigant la dernière action ayant déclenchée une émotion
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
	
	/** Retourne l'intensité de l'émotion d'empathie du type passé en paramètre 
	* (on suppose ici qu'il existe un unique autre agent vers qui l'agent peut avoir des émotions d'empathie; c'est le cas dans l'interaction humain-machine)
	* @return intensité de l'émotion d'empathie de l'agent du type passé en paramètre
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
