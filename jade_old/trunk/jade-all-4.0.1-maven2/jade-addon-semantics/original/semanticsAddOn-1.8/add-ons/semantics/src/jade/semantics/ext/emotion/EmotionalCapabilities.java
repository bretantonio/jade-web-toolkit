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

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.semantics.actions.OntologicalAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.behaviours.OntoActionBehaviour;
import jade.semantics.ext.emotion.behaviours.EmotionalSemanticInterpreterBehaviour;
import jade.semantics.ext.emotion.sips.EffortInitializationSIP;
import jade.semantics.ext.emotion.sips.EmotionTriggeringDecoratorSIP;
import jade.semantics.interpreter.DefaultCapabilities;
import jade.semantics.interpreter.SemanticInterpretationPrincipleTable;
import jade.semantics.interpreter.SemanticInterpreterBehaviour;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.Tools;
import jade.semantics.interpreter.sips.adapters.ActionDoneSIPAdapter;
import jade.semantics.interpreter.sips.adapters.BeliefTransferSIPAdapter;
import jade.semantics.kbase.ArrayListKBaseImpl;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.filters.FilterKBase;
import jade.semantics.kbase.filters.FilterKBaseImpl;
import jade.semantics.kbase.filters.std.NestedBeliefFilters;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.NotNode;
import jade.semantics.lang.sl.grammar.RealConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.ArrayList;


/** Semantic capabilities de l'agent JSA émotionnel */
public class EmotionalCapabilities extends DefaultCapabilities {

	/** Etat émotionnel courant de l'agent */
	protected EmotionalState emotionalState;
	
	/** Effort courant de l'agent pour tenter de réaliser l'intention courante */
	private int effort;
	
	/** Degré d'incertitude courante de l'agent de réaliser l'intention courante */
	private double incertitudeIntention;
		
	
	
	
	/** Degres de certirude de l'utilisateur qu'une phrase tapé librement soit comprises */
	double  degreCertitudeUM=0.5;
	/** Degrés de certitude de l'utilisateur qu'une phrase pré-selectionné soit comprise */
	double  degreCertitudePD=0.85;
	/** Degres de certitude de l'agent de comprendre l'utilisateur */
	double  degreCertitudeUnderstoodA=0.7;
	/** importancep our l'utilisateur que l'acte soit compris */
	double impActeCompris=0.1;
	/** Importance pour l'agent que l'acte soit compris */
	double impActeComprisA=0.1;
	/** Importance pour l'utilisateur que être compris n'échoue pas */ 
	double impActeNonCompris=0.9;
	/** Importancep our l'agent que comprendre l'utilisateur n'échoue pas */
	double impActeNonComprisA=0.9;
	/** potentiel de reaction pour l'utilisateur en cas d'incompréhension */
	double potentielReactActeNonCompris=0.6;
	/** potentiel de reaction pour l'agent en cas d'incompréhension */
	double potentielReactActeNonComprisA=0.5;
	
	
	/** Valeur par défaut (si non défini par le ocntructeur de l'action */
	
	/**Degré de certitude de l'utilisateur de réaliser cette intention*/
	double default_deg_cert_user=0.8;
	/**Degré de certitude de l'agent de réaliser cette intention*/
	double default_deg_cert_agent=0.8;
	/** Importance pour l'utilisateur de réaliser une action */
	double default_imp_satisf=0.7;
	/*** Importance pour l'utilisateur que l'intention ne soit pas réalisé */
	double default_imp_echec=0.7;
	/** Importance pour l'agent de réaliser cette action */
	double default_impA=0.7;
	/** Importance pour l'agent que la réalisation de  l'action n'échoue pas */
	double default_impNonEchecA=1.0;
	/** Potentiel de réaction de l'utilisateur */
	double default_p_reac=0.0;
	/** Potentiel de réaction de l'agent */
	double default_p_reacA=0.0;
		
	/** Constructeur des semantic capabilities de l'agent JSA émotionnel
	 */
	public EmotionalCapabilities() {
		super();
		/** On crée l'état émotionnel de l'agent */
		emotionalState=new EmotionalState(this.getAgentName());
		/** On crée le comportement */
		/** Initialisation des valeurs*/
		effort=0;
		incertitudeIntention=0;
		
	}
	
	/** Méthode qui retourne le degré de certitude par défaut de l'utilisateur 
	 * @return le degré de certitude par défaut de l'utilisateur
	 */
	public double getDefault_deg_cert_user(){
		return default_deg_cert_user;
	}
	/**Méthode qui retourne le degré de certitude par défaut de l'agent de réaliser une intention
	 * @return le degré de certitude par défaut de l'agent
	 * */
	public double getDefault_deg_cert_agent(){
		return default_deg_cert_agent;
	}
	/**Méthode qui retourne l'importance pour l'utilisateur qu'une intention ne soit pas réalisée
	 * @return l'importance pour l'utilisateur qu'une intention ne soit pas réalisée
	 * */
	public double getDefault_imp_echec(){
		return default_imp_echec;
	}
	
	/**Méthode qui retourne l'importance par défaut pour l'agent qu'une intention soit réalisée
	 * @return l'importance pour l'agent qu'une intention soit réalisée
	 * */
	public double getDefault_impA(){
		return default_impA;
	}
	
	/**Méthode qui retourne l'importance par défaut pour l'agent qu'une intention n'échoue pas
	 * @return l'importance pour l'agent qu'une intention n'échoue pas
	 * */
	public double getDefault_impNonEchecA(){
		return this.default_impNonEchecA;
	}
	
	/**Méthode qui retourne le potentiel de réaction par défaut de l'utilisateur en cas d'échec
	 * @return le potentiel de réaction par défaut de l'utilisateur en cas d'échec
	 * */
	public double getDefault_p_reac(){
		return default_p_reac;
	}
	
	/**Méthode qui retourne le potentiel de réaction par défaut de l'agent en cas d'échec
	 * @return le potentiel de réaction par défaut de l'agent en cas d'échec
	 * */
	public double getDefault_p_reacA(){
		return default_p_reacA;
	}
	
	/**Méthode qui retourne l'importance par défaut pour l'utilisateur qu'une action soit réalisée
	 * @return l'importance par défaut pour l'utilisateur qu'une action soit réalisée
	 * */
	public double getDefault_imp_satisf(){
		return default_imp_satisf;
	}

	/** Méthode qui retourne l'état émotionnel de l'agent
	 * @return l'état émotionnel courant de l'agent
	 */
	public EmotionalState getEmotionalState(){
		return emotionalState;
	}
	
	/** Méthode qui incrémente l'effort courant de l'agent 
	 * */
	public void incrementeEffort(){
		effort++;
	}
	
	/** Méthode qui retourne l'effort courant de l'agent 
	 * @return l'effort courant de l'agent pour réaliser l'intention courante
	 */
	public int getEffort(){
		return effort;
	}
	
	/** Méthode qui met la valeur de l'effort à null 
	 * 
	 */
	public void effortZero(){
		effort=0;
	}
	
	/** Méthode qui met à jour le degré de certitude courant de l'agent suivant le nouveau degré de certitude passé en paramètre
	 * @param i degré de certitude à ajouter au degré de certitude courant
	 */
	public void ajoutIncertitudeIntention(double i){
		incertitudeIntention=(incertitudeIntention+i)/2;
	}
	
	/** Méthode qui met la valeur de du degré de certitude courant de l'agent à null 
	 * 
	 */
	public void incertitudeZero(){
		incertitudeIntention=0;
	}
	
	/** Méthode qui retourne le degré de certitude courant de l'agent 
	 * @return degré de certitude courant de l'agent pour réaliser l'intention courante
	 */
	public double getIncertitude(){
		return incertitudeIntention;
	}
	
	
	
	/* (non-Javadoc)
	 * @see jade.semantics.interpreter.DefaultCapabilities#setupSemanticInterpreterBehaviour(jade.lang.acl.MessageTemplate)
	 */
	protected SemanticInterpreterBehaviour setupSemanticInterpreterBehaviour(MessageTemplate template) {
		return new EmotionalSemanticInterpreterBehaviour(template, this);
	}
	
/***************************************** KBase SETUP **************************************/
	
	/** Création de la base de connaissance 
	 * @return la base de connaisance de l'agent
	 * */
	protected KBase setupKbase() {
		FilterKBase kb = (FilterKBase) super.setupKbase();
		 
		/** Permet de créer des croyances sur des attitudes mentales d'autres agents */
		kb.addFiltersDefinition(new NestedBeliefFilters() {
		 	 public KBase newInstance(Term agent) {
		 	    FilterKBase result = new FilterKBaseImpl(new ArrayListKBaseImpl(agent), null);
		 	    result.addFiltersDefinition(new NestedBeliefFilters() {
		 	    	 public KBase newInstance(Term agent) {
		 	    	   FilterKBase result = new FilterKBaseImpl(new ArrayListKBaseImpl(agent), null);
		 	    	   return result;
		 	    	 }
		 	    });
		 	    return result;
		 	   } 

		 });
		 		
		return kb;
	} //Fin méthode setup de Kbase
	
/***************************************** SIP SETUP *********************************************************/
	
	/** Méthode permettant la mise en place des SIP de l'agent
	 * @return la table des SIP
	 */
	protected SemanticInterpretationPrincipleTable setupSemanticInterpretationPrinciples() {

		SemanticInterpretationPrincipleTable t = super.setupSemanticInterpretationPrinciples();  //new SemanticInterpretationPrincipleTableImpl();
//        t.addSemanticInterpretationPrinciple(new SplitEqualsIREBeliefIntention(this));
//        t.addSemanticInterpretationPrinciple(new SplitBeliefIntention(this));
//        t.addSemanticInterpretationPrinciple(new SplitEqualsIRE(this));
//        t.addSemanticInterpretationPrinciple(new SplitAnd(this));
//        t.addSemanticInterpretationPrinciple(new ActionFeatures(this, false));
//        t.addSemanticInterpretationPrinciple(new GoalCommitment(this));
//        t.addSemanticInterpretationPrinciple(new BeliefTransfer(this));
//        t.addSemanticInterpretationPrinciple(new RequestWhen(this));
//        t.addSemanticInterpretationPrinciple(new IntentionTransfer(this));
//
//        t.addSemanticInterpretationPrinciple(new Refuse(this));
//        t.addSemanticInterpretationPrinciple(new RejectProposal(this));
//        t.addSemanticInterpretationPrinciple(new Agree(this));
//        t.addSemanticInterpretationPrinciple(new Propose(this));
//        t.addSemanticInterpretationPrinciple(new RequestWhenever(this));
//        t.addSemanticInterpretationPrinciple(new Subscribe(this));
//        t.addSemanticInterpretationPrinciple(new Unsubscribe(this));
//        t.addSemanticInterpretationPrinciple(new UnreachableGoal(this));
		
        
			
        
		/** SIP activé lorsque l'agent est informé qu'un conflit de croyance vient d'apparaître */ 
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(belief_conflict)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
					/** On récupère la dernière action sur laquelle il y a eu conflit de croyance */
					Term lastAction=((EmotionalCapabilities)myCapabilities).getEmotionalState().getLastAction();
					/** On récupère l'effort réalisé par l'agent pour réaliser la dernière action */
					double effort=((EmotionalCapabilities)myCapabilities).getEmotionalState().getLastEffort();
					/** Valeur par défaut si non défini par l'action */
					double deg_cert_user=default_deg_cert_user;
					double deg_cert_agent=default_deg_cert_agent;
					double imp_echec=default_imp_echec;
					double p_reac=default_p_reac;
					double p_reacA=default_p_reacA;
					double impA=default_impA;
					double impNonEchec=default_impNonEchecA;
					
					/** On récupère l'action expression pour connaitre le degré de certitude, le potentiel de réaction, et l'importance de l'agent et utilisateur */
					if(lastAction instanceof ActionExpressionNode){
						SemanticAction semAct = myCapabilities.getMySemanticActionTable().getSemanticActionInstance((ActionExpressionNode)lastAction);
						if(semAct instanceof jade.semantics.ext.emotion.actions.OntologicalAction){
							deg_cert_user=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getDegresCertitudeUser();
							deg_cert_agent=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getDegresCertitudeAgent();
							imp_echec=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getImportanceEchecUser();
							p_reac=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getPotReactionUser();
							impA= ((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getImportanceAgent();
							impNonEchec=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getImportanceNonEchecAgent();
						}
					}
				
					/** Déclenchement d'une émotion empathique et non empathique de colère */
					EmotionalCapabilities.this.getEmotionalState().newElicitedEmotionNE("colere",deg_cert_agent,default_p_reacA, effort, impNonEchec, SL.term("belief_conflict"));
					EmotionalCapabilities.this.getEmotionalState().newElicitedEmotion("colere",deg_cert_user,p_reac,(1.0+effort),imp_echec, SL.term("belief_conflict"), SL.term("ihm@test"));                

					// TM TM TM 
					EmotionalCapabilities.this.inform(SL.formula("(colère)"), Tools.AID2Term(sr.getMessage().getSender()));
//	Eloona				EmotionalCapabilities.this.getEmotionalState().transfertTextePlusEmotionEmp("Oh non ! je me suis trompée !");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		
		/** SIP executé lorsque l'agent JSA émotionnel a compris un message d'un autre agent */
		t.addSemanticInterpretationPrinciple(new ActionDoneSIPAdapter(this, "(action ??sender ??action)") {
			/* (non-Javadoc)
			 * @see jade.semantics.interpreter.sips.adapters.ApplicationSpecificSIPAdapter#doApply(jade.semantics.lang.sl.tools.MatchResult, jade.util.leap.ArrayList, jade.semantics.interpreter.SemanticRepresentation)
			 */
			protected ArrayList doApply(final MatchResult applyResult, ArrayList result, SemanticRepresentation sr) {
				if (sr.getMessage() != null && !myCapabilities.getAgentName().equals(applyResult.term("sender"))) {
					potentiallyAddBehaviour(new OneShotBehaviour(myCapabilities.getAgent()) {
						public void action() {
							EmotionalCapabilities.this.getEmotionalState().newElicitedEmotionNE("pos",degreCertitudeUnderstoodA,0.0, 1.0, impActeComprisA, SL.term("understood"));
							EmotionalCapabilities.this.getEmotionalState().newElicitedEmotion(
									"pos",degreCertitudePD,0.0,1.0,impActeCompris, SL.term("understood"), applyResult.term("sender"));                
						}
					});
				}
				return null;
			}
		});
		
		/** Ajoute le planner peremttant d'initialiser l'effort avant la réalisation d'une intention */
		t.addSemanticInterpretationPrinciple(new EffortInitializationSIP(this));
		
		/** Ajoute le planner peremttant le déclenchement d'émotion lors de la réalisation d'une intention */
		t.addSemanticInterpretationPrinciple(new EmotionTriggeringDecoratorSIP(this));
		
		/***** SIp de gestion langue nat *************/
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(echec_ferm_mess)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
// Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("La messagerie ne peut être fermée");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(echec_ouv_mess)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("La messagerie ne peut être ouverte");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(echec_contact)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("Le contact ne peut pas être effectué");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(echec_lect_mess_urg)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("Les messages urgents ne peuvent être lus");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(echec_lect_mess)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("Les messages ne peuvent être lus");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(echec_nb_mess)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("L'information sur le nombre de messages est inaccessible");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		/*********************Realisation */
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(real_ouv_mess)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("La messagerie vient d'être ouverte");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(real_ferm_mess)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("La messagerie vient d'être fermée");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(real_nb_message ??x)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("Vous avez "+match.getTerm("x").toString()+" nouveaux messages");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(real_mess ??num ??exp ??type ??m)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("Le message "+match.getTerm("num").toString()+
//							" de "+match.getTerm("exp").toString()+
//							//" priorité "+match.getTerm("type").toString()+
//							" est "+match.getTerm("m").toString());
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(real_no_mess ??name)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona ((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("Il n'y a pas de message de "+match.getTerm("name").toString());
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(real_name)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("Je m'appelle Eloona");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		
		t.addSemanticInterpretationPrinciple(new BeliefTransferSIPAdapter(this, "(real_age)") {
			protected ArrayList doApply(MatchResult match, MatchResult matchagent, ArrayList acceptResult, ArrayList refuseResult, SemanticRepresentation sr) {
				try{ 
//					 Eloona					((EmotionalCapabilities)myCapabilities).getEmotionalState().transfertTextePlusEmotionEmp("J'ai 26 ans");
				}catch(Exception e){
					System.err.println(e);
				}
				return acceptResult;
			}});
		return t;
	} 

	
/***************************************** Semantic Actions SETUP *********************************************************/

	protected SemanticActionTable setupSemanticActions() {

		SemanticActionTable t = super.setupSemanticActions();
		try{
			
			/** Action permettant d'incrémenter l'effort courant de l'agent après chaque execution d'action */
			t.addSemanticAction(new OntologicalAction(this,
					"(effort)",
					SL.TRUE,
					SL.TRUE)
					{
					
					public void perform(OntoActionBehaviour behaviour) {
						try {
							if ( behaviour.getState() == behaviour.START ) {
								EmotionalCapabilities.this.incrementeEffort();
								behaviour.setState(behaviour.SUCCESS);
							}
						}
						catch (Exception e) {
								e.printStackTrace();
								behaviour.setState(behaviour.EXECUTION_FAILURE);
						}
					}
					
					
				});
			
			/** Action de déclenchement d'une émotion positive */
			t.addSemanticAction(new OntologicalAction(this,
					"(EmotionPos :imp ??i :d_g ??d :act ??act :agOr ??a_o)",
					SL.TRUE,
					SL.TRUE)
					{
					public void perform(OntoActionBehaviour behaviour) {
						try {
							if ( behaviour.getState() == behaviour.START ) {
									/** Importance pour l'agent que l'intention soit réalisée */
									double importance=((RealConstantNode)this.getActionParameter("i")).realValue();
									/** Effort investi par l'agent pour réaliser l'intention */
									double effortReal=((EmotionalCapabilities)capabilities).getEffort();
									/** Degré de certitude de l'agent quand à la réalisation de l'intention */
									double deg_cert=((RealConstantNode)this.getActionParameter("d")).realValue();
									/** Agent vers qui est orientée l'émotion d'empathie : si il est égale à l'agent lui-même alors il n'y a pas d'émotion d'empathie */
									Term agentOriented=this.getActionParameter("a_o");
									/** Action génératrice de l'émotion */
									Term actionGeneratrice=this.getActionParameter("act");
									
									System.err.println("-------> Emotion Positive d_g : "+deg_cert+
											" effort : "+effortReal+
											"imp : "+importance);
									
									double deg_cert_user=default_deg_cert_user;
									double imp_real=default_imp_satisf;
									/** On récupère l'action expression pour connaitre l'importance  et le degré de certitude de l'utilisateur */
									if(actionGeneratrice instanceof ActionExpressionNode){
										SemanticAction semAct = capabilities.getMySemanticActionTable().getSemanticActionInstance((ActionExpressionNode)actionGeneratrice);
										if(semAct instanceof jade.semantics.ext.emotion.actions.OntologicalAction){
											deg_cert_user=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getDegresCertitudeUser();
											imp_real=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getImportanceRealUser();
											deg_cert=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getDegresCertitudeAgent();
											importance=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getImportanceAgent();
										}
									}
									/** Déclenchement d'une émotion non empathique positive */
									
									
									((EmotionalCapabilities)capabilities).getEmotionalState().newElicitedEmotionNE("pos",deg_cert,0,effortReal,importance, actionGeneratrice);
									
									
									/** Déclenchement d'une émotion d'empathie positive */
									if(!agentOriented.equals(((EmotionalCapabilities)capabilities).getAgentName())){
										
										System.err.println("-------> Emotion Positive empathie d_g : "+deg_cert_user+
												"imp : "+imp_real);
										/** Déclenchement de l'émotion d'empathie positive */
										((EmotionalCapabilities)capabilities).getEmotionalState().newElicitedEmotion("pos",deg_cert_user,0.0, 1.0,imp_real,actionGeneratrice, agentOriented);
									}
									
									behaviour.setState(behaviour.SUCCESS);
							}
						}
						catch (Exception e) {
								e.printStackTrace();
								behaviour.setState(behaviour.EXECUTION_FAILURE);
						}
					}
					
				});
			
			/** Action de déclenchement d'une émotion positive */
			t.addSemanticAction(new OntologicalAction(this,
					"(EmotionNeg :potReact ??pr :imp ??i :d_g ??d  :act ??act :agOr ??a_o)",
					SL.TRUE,
					SL.TRUE)
					{
					public void perform(OntoActionBehaviour behaviour) {
						try {
							if ( behaviour.getState() == behaviour.START ) {
									/** Importance pour l'agent que l'action soit réalisée */ 
									double importance=((RealConstantNode)this.getActionParameter("i")).realValue();
									/** Effort réalisé par l'agent */
									double effortReal=((EmotionalCapabilities)capabilities).getEffort();
									/** Potentiel de réaction de l'agent */
									double potentielReaction=((RealConstantNode)this.getActionParameter("pr")).realValue();
									/** Degré de certitude de l'agent */
									double deg_cert=((RealConstantNode)this.getActionParameter("d")).realValue();
									/** Agent vers qui est orienté l'émotion d'empathie, est égale à l'agent lui-même si pas d'émotion d'empathie */
									Term agentOriented=this.getActionParameter("a_o");
									/** Action génératrice de l'émotion */
									Term actionGeneratrice=this.getActionParameter("act");
									
									System.err.println("-------> Emotion Negative d_g : "+deg_cert+
											" effort : "+effortReal+
											" pot_reac : "+potentielReaction+
											"imp : "+importance);
									
									/** Degré de certitude par défaut de l'utilisateur */
									double deg_cert_user=default_deg_cert_user;
									/** Importance que l'intention n'échoue pas par défaut de l'utilisateur */
									double imp_echec=default_imp_echec;
									/** Potentiel de réction par défaut de l'utilisateur */
									double p_reac=default_p_reac;
									
									/** On récupère l'action expression pour connaitre l'importance, le degré de certitude et le potentiel de réaction de l'agent*/
									if(actionGeneratrice instanceof ActionExpressionNode){
										SemanticAction semAct = capabilities.getMySemanticActionTable().getSemanticActionInstance((ActionExpressionNode)actionGeneratrice);
										if(semAct instanceof jade.semantics.ext.emotion.actions.OntologicalAction){
											deg_cert_user=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getDegresCertitudeUser();
											imp_echec=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getImportanceEchecUser();
											p_reac=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getPotReactionUser();
											deg_cert=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getDegresCertitudeAgent();
											importance=((jade.semantics.ext.emotion.actions.OntologicalAction)semAct).getImportanceNonEchecAgent();
										}
									}
									
									/** Déclenchement de l'émotion non empathique négative */
									((EmotionalCapabilities)capabilities).getEmotionalState().newElicitedEmotionNE("neg",deg_cert,potentielReaction,effortReal,importance, actionGeneratrice);
									
									/** Si émotion d'empathie */
									if(!agentOriented.equals(((EmotionalCapabilities)capabilities).getAgentName())){
										System.err.println("-------> Emotion neg empathie d_g : "+deg_cert_user+
												" imp : "+imp_echec+
												" potentiel de reaction "+p_reac);
										/** Déclenchement de l'émotion d'empathie négative */
										((EmotionalCapabilities)capabilities).getEmotionalState().newElicitedEmotion("neg",deg_cert_user,p_reac,1.0,imp_echec, actionGeneratrice, agentOriented);

									}

									behaviour.setState(behaviour.FEASIBILITY_FAILURE);
							}
						}
						catch (Exception e) {
								e.printStackTrace();
								behaviour.setState(behaviour.EXECUTION_FAILURE);
						}
					}
					
				});
				
		/************************* FIN Creation des actions de génération d'impulsions émotionnelles *******/
				
		/************************* Creation OntologicalAction *********************/
		
		/** L'action d'ouverture de la messagerie
		 * Ne peut être executé que si la messagerie n'est pas ouverte  
		 */
		OntologicalAction ouverture_ligne=new jade.semantics.ext.emotion.actions.OntologicalAction(this,
		        "(OUV_MESS)",
		         SL.fromFormula("(mess_ouverte)"),
		         new NotNode(SL.fromFormula("(mess_ouverte)")), //precond
		         0.75, 
		         0.8,
		         0.9,
		         0.9,
		         0.8)
		         {
				public void perform(OntoActionBehaviour behaviour) {
					
					try {
						if ( behaviour.getState() == behaviour.START ) {
							System.err.println("-------------------------------------");
							System.err.println("|      MESSAGERIE OUVERTE		      | "+new NotNode(SL.fromFormula("(mess_ouverte)")));
							System.err.println("-------------------------------------");
							behaviour.setState(behaviour.SUCCESS);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						behaviour.setState(behaviour.FEASIBILITY_FAILURE);
					}
				}
			};		
			t.addSemanticAction(ouverture_ligne);
		
		/** L'action de fermeture de la messagerie
		* Ne peut être executé que si la messagerie est ouverte  
		* Implique que la messagerie n'est pas ouverte
		*/
		OntologicalAction fermeture_messagerie=new jade.semantics.ext.emotion.actions.OntologicalAction(this,
			        "(FERME_MESS)",
			         new NotNode(SL.fromFormula("(mess_ouverte)")),
			         SL.fromFormula("(mess_ouverte)"),
			         0.75,
			         0.8,
			         0.5,
			         0.7,
			         0.8)
			         {
						public void perform(OntoActionBehaviour behaviour) {
						try {
							if ( behaviour.getState() == behaviour.START ) {
								System.err.println("-------------------------------------");
								System.err.println("|      MESSAGERIE FERMEE		      |");
								System.err.println("-------------------------------------");
								behaviour.setState(behaviour.SUCCESS);
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							behaviour.setState(behaviour.FEASIBILITY_FAILURE);
						}
					}
				};		
			t.addSemanticAction(fermeture_messagerie);
		
			/** L'action de connaître le nombre de message
			* Ne peut être executé que si la messagerie est ouverte  
			*/
			OntologicalAction transfert_nb_message=new jade.semantics.ext.emotion.actions.OntologicalAction(this,
				        "(NB_MESS)",
				         SL.TRUE,
				         SL.fromFormula("(mess_ouverte)"),
				         0.75,
				         0.8,
				         0.5,
				         0.7,
				         0.8)
				         {
				public void perform(OntoActionBehaviour behaviour) {
					try {
						if ( behaviour.getState() == behaviour.START ) {
							
							String content=" \"((any ?x1 (nb_message ?x1)))\" ";
							String query1="(INFORM-REF :content"+content+":receiver (set (agent-identifier :name ihm@test)) :sender (agent-identifier :name agent@test))";
							String formul1="(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) "+query1+") true))";
							
							Formula f=SL.fromFormula(formul1);
							capabilities.interpret(f);
							
							System.err.println("-------------------------------------");
							System.err.println("|      Info NB Message		      |");
							System.err.println("-------------------------------------");
							behaviour.setState(behaviour.SUCCESS);
						}
					}
					catch (Exception e) {
					e.printStackTrace();
					behaviour.setState(behaviour.FEASIBILITY_FAILURE);
				}
					}
			};		
			t.addSemanticAction(transfert_nb_message);
		
			/** L'action de connaître les nouveaux messages
			* Ne peut être executé que si la messagerie est ouverte  
			*/
			OntologicalAction lecture_message=new jade.semantics.ext.emotion.actions.OntologicalAction(this,
			        "(LECT_MESS)",
			         SL.TRUE,
			         SL.fromFormula("(mess_ouverte)"),
			         0.75,
			         0.8,
			         0.5,
			         0.7,
			         0.8)
			         {
				public void perform(OntoActionBehaviour behaviour) {
					try {
					if ( behaviour.getState() == behaviour.START ) {
						
						String content2=" \"((all (sequence ?x2 ?y2 ?z2 ?m2) (message ?x2 ?y2 ?z2 ?m2)))\" ";
						String query2="(INFORM-REF :content"+content2+":receiver (set (agent-identifier :name ihm@test)) :sender (agent-identifier :name agent@test))";
						String formul2="(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) "+query2+") true))";
						
						Formula f=SL.fromFormula(formul2);
						capabilities.interpret(f);
						
						System.err.println("-------------------------------------");
						System.err.println("|      lecture Message		      |");
						System.err.println("-------------------------------------");
						behaviour.setState(behaviour.SUCCESS);
					}
				}
				catch (Exception e) {
				e.printStackTrace();
				behaviour.setState(behaviour.FEASIBILITY_FAILURE);
			}
				}
		};		
			t.addSemanticAction(lecture_message);
		
			/** L'action de connaître les messages d'un certain expediteur
			* Ne peut être executé que si la messagerie est ouverte  
			*/
			OntologicalAction lecture_message_p=new jade.semantics.ext.emotion.actions.OntologicalAction(this,
		        "(LECT_MESSP :name ??n)",
		         SL.TRUE,
		         SL.fromFormula("(mess_ouverte)"),
		         0.75,
		         0.8,
		         0.5,
		         0.7,
		         0.8)
		         {
			public void perform(OntoActionBehaviour behaviour) {
				try {
				if ( behaviour.getState() == behaviour.START ) {
					String name = this.getActionParameter("n").toString();
					
					String content2=" \"((all (sequence ?x3 ?y3 ?m3) (message ?x3 "+name+" ?y3 ?m3)))\" ";
					String query2="(INFORM-REF :content"+content2+":receiver (set (agent-identifier :name ihm@test)) :sender (agent-identifier :name agent@test))";
					String formul2="(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) "+query2+") true))";
					
					Formula f=SL.fromFormula(formul2);
					capabilities.interpret(f);
					
					System.err.println("-------------------------------------");
					System.err.println("|      lecture Message	de	"+name+" |");
					System.err.println("-------------------------------------");
					behaviour.setState(behaviour.SUCCESS);
				}
			}
			catch (Exception e) {
			e.printStackTrace();
			behaviour.setState(behaviour.FEASIBILITY_FAILURE);
		}
			}
	};		
			t.addSemanticAction(lecture_message_p);
		
		
			/** L'action de connaître les messages urgent
			* Ne peut être executé que si la messagerie est ouverte  
			*/
			OntologicalAction lecture_message_urgent=new jade.semantics.ext.emotion.actions.OntologicalAction(this,
		        "(LECT_MESS_URG)",
		         SL.TRUE,
		         SL.fromFormula("(mess_ouverte)"),
		         0.75,
		         0.8,
		         0.5,
		         0.9,
		         0.8)
		         {
			public void perform(OntoActionBehaviour behaviour) {
				try {
				if ( behaviour.getState() == behaviour.START ) {
					
					String content2=" \"((all (sequence ?x3 ?y3 ?m3) (message ?x3 ?y3 urjant ?m3)))\" ";
					String query2="(INFORM-REF :content"+content2+":receiver (set (agent-identifier :name ihm@test)) :sender (agent-identifier :name agent@test))";
					String formul2="(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) "+query2+") true))";
					
					Formula f=SL.fromFormula(formul2);
					capabilities.interpret(f);
					
					System.err.println("-------------------------------------");
					System.err.println("|      lecture Message urgent		      |");
					System.err.println("-------------------------------------");
					behaviour.setState(behaviour.SUCCESS);
				}
			}
			catch (Exception e) {
			e.printStackTrace();
			behaviour.setState(behaviour.FEASIBILITY_FAILURE);
		}
			}
	};		
			t.addSemanticAction(lecture_message_urgent);
		
		
		
			/** L'action de demande de mise en contact avec un autre agent 
			* Ne peut être executé que si la messagerie est ouverte  
			*/
			OntologicalAction contact=new jade.semantics.ext.emotion.actions.OntologicalAction(this,
		        "(CONTACT :name ??n)",
		         SL.TRUE,
		         SL.fromFormula("(mess_ouverte)"),
		         0.75,
		         0.8,
		         0.5,
		         0.7,
		         0.0)
		         {
			public void perform(OntoActionBehaviour behaviour) {
				try {
				if ( behaviour.getState() == behaviour.START ) {
					String name = this.getActionParameter("n").toString();
					
					System.err.println("-------------------------------------");
					System.err.println("|      Contact	de	"+name+"  impossible |");
					System.err.println("-------------------------------------");
					behaviour.setState(behaviour.FEASIBILITY_FAILURE);
				}
			}
			catch (Exception e) {
			e.printStackTrace();
			behaviour.setState(behaviour.FEASIBILITY_FAILURE);
		}
			}
	};		
			t.addSemanticAction(contact);
		
		/************************* Fin Creation OntologicalAction *********************/
						
			/** L'action de chanter : action test */
			OntologicalAction sing=new OntologicalAction(this,
                "(SING)",
                SL.TRUE,
                SL.TRUE)
                 {
					public void perform(OntoActionBehaviour behaviour) {
						try {
							if ( behaviour.getState() == behaviour.START ) {
									System.err.println("-------------------------------------");
									System.err.println("|      I'M SINGING IN THE RAIN      |");
									System.err.println("|      JUST SINGING IN THE RAIN     |");
									System.err.println("|      WHAT A GLORIOUS FEELING      |");
									System.err.println("|      I'M HAPPY AGAIN              |");
									System.err.println("-------------------------------------");
									behaviour.setState(behaviour.SUCCESS);
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							behaviour.setState(behaviour.FEASIBILITY_FAILURE);
						}
					}
		};
			t.addSemanticAction(sing);
		
		} catch (Exception e) {
            e.printStackTrace();
        }
		return t;
		
	} //Fin du setup des Semantic Actions

public double getDegreCertitudePD() {
	return degreCertitudePD;
}

public double getDegreCertitudeUM() {
	return degreCertitudeUM;
}

public double getDegreCertitudeUnderstoodA() {
	return degreCertitudeUnderstoodA;
}

public double getImpActeCompris() {
	return impActeCompris;
}

public double getImpActeComprisA() {
	return impActeComprisA;
}

public double getImpActeNonCompris() {
	return impActeNonCompris;
}

public double getImpActeNonComprisA() {
	return impActeNonComprisA;
}

public double getPotentielReactActeNonCompris() {
	return potentielReactActeNonCompris;
}

public double getPotentielReactActeNonComprisA() {
	return potentielReactActeNonComprisA;
}

}

