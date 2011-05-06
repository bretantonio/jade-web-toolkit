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


import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;

import java.io.PrintWriter;


/**
 * 
 * @author Magalie Ochs
 * Semantic Capabilities de l'agent JSA d'interface
 */
public class SemanticCapabilitiesInterface extends DefaultCapabilities {

	/* Interface de dialogue */
	private InterfaceDialogue interfaceDialogue;
	
	/* Permet d'envoyer le texte que doit être dit à Eloona */ 
	private PrintWriter connexionEloona;
	
	/* Dernier message énoncé par Eloona */
	private String lastMessage;
	
	
//	public SemanticCapabilitiesInterface (MessageTemplate template) {
//		super(template);
//	
//		
//	}
	
	
	
	
	public SemanticCapabilitiesInterface(InterfaceDialogue inter) {
		super();	
				
		interfaceDialogue=inter;
		this.lastMessage="";
		
//		/* Création de la connexion socket avec Eloona */
//		try{
//			Socket s = new Socket("127.0.0.1",18000);
//		OutputStream os = s.getOutputStream();
//		connexionEloona = new PrintWriter(new BufferedWriter(
//                   new OutputStreamWriter(os)), 
//                true);
//		connexionEloona.println("Bonjour, que désirez vous ?");
//		
//		}catch(Exception e){
//			System.err.println(e);
//		}
		
	
	
	}
	
	/**
	 * Méthode qui renvoie l'interface de dialogue 
	 * @return l'interface de dialogue
	 */
	public InterfaceDialogue getInterfaceDialogue(){
		return interfaceDialogue;
	}
	
	
	/**
	 * Méthode qui permet l'affichage du message texte sur l'interface de dialogue
	 * et transfére le texte à dire à Eloona
	 * @param s message à dire/afficher
	 */
	public void message(String s){
		this.interfaceDialogue.afficheMessageAgent(s);
		//if(!s.equals(this.lastMessage)){
			connexionEloona.println(s);
			//this.lastMessage=s;
	//	}
	}
	
	/**
	 * Méthode qui affiche (en ajoutant au texte déjà sur l'interface) le message passé en paramètre
	 * et transfère ce texte à Eloona
	 * @param s message à dire/afficher
	 */
	public void messageAppend(String s){
		this.interfaceDialogue.afficheMessageAgentAppend(s);
		connexionEloona.println(s);
	}
	
	/**
	 * Mise en place de la Kbase de l'agent
	 */
	protected KBase setupKbase() {
		FilterKBase kb = (FilterKBase) super.setupKbase();
		return kb;
	} 
	
/**************************************** SIP SETUP *********************************************************/
	/**
	 * Méthode permettant la mise en place des SIP de l'agent
	 */
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {

		SemanticInterpretationPrincipleTable t = super.setupSemanticInterpretationPrinciples();

		/** Affiche tous les SR qui sont interpétés*/
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "??phi") {
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				System.err.println(":------------------------------------------------------------");
				System.err.println(":---------> AGENT INTERFACE !!!!!! ApplicationSpecificSIPAdapter:phi="+match.formula("phi"));
				System.err.println(":------------------------------------------------------------");
				return result;
			}});

		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(colère)") {
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				//((SemanticCapabilitiesInterface)myCapabilities).message("Je me suis trompée, zut!!");
				return new ArrayList();
		}});

		/** SIP déclenché lorsqu'une requête de l'utilisateur n'a pas été comprise  
		 * Un message est alors envoyé à l'agent JSA émotionnel pour lui dire qu'un message incompréhensible a été recu
		 * */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(I (agent-identifier :name ihm@test)(NOT-UNDERSTANDABLE))") {
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {

				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("ce contenu de message est incompréhensible!!!");
				msg.addReceiver(new AID("agent@test",AID.ISLOCALNAME));
				msg.setSender(getAgent().getAID());
				getAgent().send(msg);
				
				/* Affichage d'un message sur l'interface de dialogue */
				return new ArrayList();
				
			}});
		
		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface qu'il n'a pas pu executer l'action demandée
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona
		 */
		
		
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(I (agent-identifier :name agent@test) (B (agent-identifier :name ihm@test)(forall ?e (not (B ??agent (feasible ?e (done (action ??ag ??act) true)))))))") {
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				try{ 
					String actS=match.getTerm("act").toString();
					/* Si l'agent JSA émotionnel n'a pas réussi à fermer la messagerie */
					if(actS.equals("(FERME_MESS)")){
						((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(echec_ferm_mess)"), Tools.AID2Term(sr.getMessage().getSender()));
						//((SemanticCapabilitiesInterface)myCapabilities).message("La messagerie ne peut être fermé");
						return new ArrayList();
					}
					
					/* Si l'agent JSA émotionnel n'a pas réussi à ouvrir la messagerie */
					if(actS.equals("(OUV_MESS)")){
						((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(echec_ouv_mess)"), Tools.AID2Term(sr.getMessage().getSender()));
						//((SemanticCapabilitiesInterface)myCapabilities).message("La messagerie ne peut être ouverte");
						return new ArrayList();
					}	
					
					/* Si l'agent JSA émotionnel n'a pas réussi à se mettre en contact avec un autre agent */
					if(actS.startsWith("(CONTACT")){
						((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(echec_contact)"), Tools.AID2Term(sr.getMessage().getSender()));
						
						//((SemanticCapabilitiesInterface)myCapabilities).message("Le contact ne peut pas être effectué");
						return new ArrayList();
					}	
					
					/* Si l'agent JSA émotionnel n'a pas réussi à lire les messages urgents */
					if(actS.equals("(LECT_MESS_URG)")){
						((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(echec_lect_mess_urg)"), Tools.AID2Term(sr.getMessage().getSender()));
					//	((SemanticCapabilitiesInterface)myCapabilities).message("Les messages urgents ne peuvent être lus");
						//((SemanticCapabilitiesInterface)myCapabilities).getInterfaceDialogue().afficheMessageAgent("Les messages urgents ne peuvent être lus");
						return new ArrayList();
					}
					
					/* Si l'agent JSA émotionnel n'a pas réussi à lire les messages */
					if(actS.startsWith("(LECT_MESSP") || actS.equals("(LECT_MESS)")){
						//System.err.println("------------PAS POSSIBLE LECTURE MESSAGE 2---------");
						((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(echec_lect_mess)"), Tools.AID2Term(sr.getMessage().getSender()));
					//	((SemanticCapabilitiesInterface)myCapabilities).message("Les messages ne peuvent être lus");
						return new ArrayList();
					}
					
					/* Si l'agent JSA émotionnel n'a pas réussi à voir combien il y avait de nouveaux messages */
					if(actS.startsWith("(NB_MESS")){
						((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(echec_nb_mess)"), Tools.AID2Term(sr.getMessage().getSender()));
						//((SemanticCapabilitiesInterface)myCapabilities).message("L'information sur le nombre de messages est inaccessible");
						return new ArrayList();
					}
					
				} catch (Exception e) {
		            e.printStackTrace();
		        }
				return new ArrayList();
			}});
		
		
		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface qu'une action demandée vient d'être faite 
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona 
		 * Normalemet ce SIP n'est pas executé car la formule est capté avant par d'autres SIP : un pour chaque action 
		 */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(exists ?e (done (; (action ??agent ??action) ?e)))") {
				protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
					try{ 
						String act=match.getTerm("action").toString();
						if(!act.equals("(NB_MESS)") & !act.equals("(LECT_MESS)") & !act.equals("(LECT_MESS_URG)") & !act.startsWith("(LECT_MESSP")){
							//((SemanticCapabilitiesInterface)myCapabilities).message("L'action "+match.getTerm("action")+" vient d'être réalisée");
						}
					} catch (Exception e) {
			            e.printStackTrace();
			        }
					return new ArrayList();
		}});
		
		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface que l'action demandée d'ouvrir la messagerie vient d'être faite 
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona
		 */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(exists ?e (done (; (action ??agent (OUV_MESS)) ?e)))") {
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				try{ 
					((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(real_ouv_mess)"), Tools.AID2Term(sr.getMessage().getSender()));
					//((SemanticCapabilitiesInterface)myCapabilities).message("La messagerie vient d'être ouverte");	
				} catch (Exception e) {
		            e.printStackTrace();
		        }
				return new ArrayList();
			}});
		
		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface que l'action demandée de fermer la messagerie vient d'être faite 
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona
		 */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(exists ?e (done (; (action ??agent (FERME_MESS)) ?e)))") {
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				try{ 
					((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(real_ferm_mess)"), Tools.AID2Term(sr.getMessage().getSender()));
					//((SemanticCapabilitiesInterface)myCapabilities).message("La messagerie vient d'être fermée" );
				} catch (Exception e) {
		            e.printStackTrace();
		        }
				return new ArrayList();
			}});
		

		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface du nombre de messages 
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona
		 */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(B ??myself (nb_message ??x))") {	
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				try{ 
					((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(real_nb_message "+match.getTerm("x").toString()+")"), Tools.AID2Term(sr.getMessage().getSender()));
					//((SemanticCapabilitiesInterface)myCapabilities).message("Vous avez "+match.getTerm("x").toString()+" nouveaux messages");
				} catch (Exception e) {
		            e.printStackTrace();
		        }
				return new ArrayList();
			}});
		
		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface qu'aucun message dde l'individu demandé a été trouvé 
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona
		 */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(= (all (sequence ?x3 ?y3 ?m3) (message ?x3 ??name ?y3 ?m3)) (set))"){
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				try{ 
					/** !!!!! Il faudrait faire un truc plus propre qui regarde dans la base mais en attendant.....*/
					if(!match.getTerm("name").toString().toLowerCase().contains("robert") & !match.getTerm("name").toString().toLowerCase().contains("bobby")
							& !match.getTerm("name").toString().toLowerCase().contains("vanessa")
							& !match.getTerm("name").toString().toLowerCase().contains("virginnie"))
						((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(real_no_mess "+match.getTerm("name").toString()+")"), Tools.AID2Term(sr.getMessage().getSender()));
				
					//((SemanticCapabilitiesInterface)myCapabilities).message("Il n'y a pas de message de "+match.getTerm("name").toString());
				} catch (Exception e) {
		            e.printStackTrace();
		        }
				return new ArrayList();
			}});
		
		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface d'un message 
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona
		 */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(message ??num ??exp ??type ??m)") {	
			
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				try{ 
					((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(real_mess "+match.getTerm("num").toString()+" "+match.getTerm("exp").toString()+" "+match.getTerm("type").toString()+" "+match.getTerm("m").toString()+")"), Tools.AID2Term(sr.getMessage().getSender()));
					
				/*	((SemanticCapabilitiesInterface)myCapabilities).messageAppend("Le message "+match.getTerm("num").toString()+
							" de "+match.getTerm("exp").toString()+
							" priorité "+match.getTerm("type").toString()+
							" est "+match.getTerm("m").toString());*/
					} catch (Exception e) {
		            e.printStackTrace();
		        }
				return new ArrayList();
			}});
		
		
		
		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface de son nom 
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona
		 */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(nom ??n)") {	
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				try{ 
					((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(real_name)"), Tools.AID2Term(sr.getMessage().getSender()));
					//((SemanticCapabilitiesInterface)myCapabilities).message("Je m'appelle "+match.getTerm("n").toString());
				} catch (Exception e) {
		            e.printStackTrace();
		        }
				return new ArrayList();
			}});
		
		/**
		 * SIP déclenché lorsque l'agent JSA émotionnel informe l'agent JSA d'interface de son age 
		 * Ce SIP s'occupe de transférer le message à afficher/dire à l'interface de dialogue/Eloona
		 */
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "(age ??a)") {		
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				try{ 
					((SemanticCapabilitiesInterface)myCapabilities).inform(SL.fromFormula("(real_age)"), Tools.AID2Term(sr.getMessage().getSender()));
					//((SemanticCapabilitiesInterface)myCapabilities).message("J'ai "+match.getTerm("a").toString()+" ans");
				} catch (Exception e) {
		            e.printStackTrace();
		        }
				return new ArrayList();
			}});
		
		
		t.addSemanticInterpretationPrinciple(new ApplicationSpecificSIPAdapter(this, "??phi") {
			protected ArrayList doApply(MatchResult match, ArrayList result, SemanticRepresentation sr) {
				//System.err.println(":IHM----ApplicationSpecificSIPAdapter:phi="+match.formula("phi"));
				return result;
			}});
		
		
		return t;
	
	} 
	
/**************************************** Semantic Actions SETUP *********************************************************/

	protected SemanticActionTable setupSemanticActions() {
		SemanticActionTable t = super.setupSemanticActions();
		return t;
	} 

}
