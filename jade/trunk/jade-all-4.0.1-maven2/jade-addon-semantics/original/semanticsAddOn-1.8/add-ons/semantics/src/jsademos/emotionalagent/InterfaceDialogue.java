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


import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/** Fenêtre de dialogue */
public class InterfaceDialogue extends JFrame{
	
	/** AgentJSA d'interface qui met à jour l'interface */
	private InterfaceJSAAgent jSAInterfaceAgent;
	/** Nom de l'agent JSA qui met à jour l'interface */
	private Term nomJsaIhm;
	
	/** Ensemble des couples (phrase, Formule SL) */
	private Vector<Interpretation> listeMessagePossible = new Vector<Interpretation>();
	/** Interpetation selectionné par l'u */
	private Interpretation currentMessage;
	
	/** Liste des phrases qu'il est possible d'envoyer à l'agent, afficher sur l'interface */
	private JList listeMessagePossibleAffichee;
	
	/** Zone d'affichage du message selectionné par l'u */
	private JTextField messageAEnvoyer;
	private String sMessageAEnvoyer;
	
	/** Message de réponse de l'agent */
	//private JLabel messageDeAgent;
	
	private JPanel panelPrincipal;
	private JButton sendButton;
	private JButton effacer;
	
	public InterfaceDialogue (InterfaceJSAAgent j){
		super("JSA Emotionnel");
		
		jSAInterfaceAgent=j;
		nomJsaIhm=SL.fromTerm("(agent-identifier :name ihm@test)");
		
		panelPrincipal=constructionMainPanel();
		
		getContentPane().add(panelPrincipal);
		
		/** Active les évènement reliés aux boutons */
		setActionEvent();
		/** Intégre les interprétations */
		integrationIntepretations();
		
		affichage();
		
		
	}
	
	/** Méthode qui affiche la fenêtre 
	 * 
	 */
	public void affichage(){
		//Largeur hauteur
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		//this.setSize(dim.width/2, dim.height-50);
		//System.err.println("------TAILLE : width"+dim.width/2+" height  "+dim.height/2);
		this.setSize(dim.width/2, dim.height-50);
		setLocation(0, 0);
		setVisible(true);
	}
	
	/** Méthode qui met en place les évènements reliés aux boutons send, effacer
	 * et selection d'une phrase 
	 */
	public void setActionEvent(){
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!messageAEnvoyer.getText().equals("")){
					Formula f=stringToFormula(messageAEnvoyer.getText(), listeMessagePossible);
					jSAInterfaceAgent.informationInterface(f);
					currentMessage=null;
					//messageDeAgent.setText("");
				}
			}
		});
		
		effacer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				messageAEnvoyer.setText("");
				sMessageAEnvoyer="";
				currentMessage=null;
			}
		});
		
		listeMessagePossibleAffichee.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if ( e.getValueIsAdjusting() ) {
					messageAEnvoyer.setText(listeMessagePossibleAffichee.getSelectedValue().toString());
					currentMessage = (Interpretation)listeMessagePossibleAffichee.getSelectedValue();
				}
			}
		});
	}
	
	/** Méthode qui contruit le Panel principal */
	public JPanel constructionMainPanel(){
		// Panel Principal
		JPanel mainPanel=new JPanel();
		//GridLayout gL = new GridLayout(2, 1, 1, 25);
		GridLayout gL = new GridLayout(1, 1, 25, 25);
		mainPanel.setLayout(gL);
		
		mainPanel.setBorder(new EmptyBorder(130, 10, 130, 10));
		
		/********** Bloc message a envoyer */
		JPanel blocMessageAEnvoyer=new JPanel();
		GridLayout gL1 = new GridLayout(2, 1);
		blocMessageAEnvoyer.setLayout(gL1);
		
		JPanel selectionMessage=new JPanel();
		//GridLayout gL2 = new GridLayout(2, 1, 0, 0);
		BorderLayout gL2=new BorderLayout(10,10);
		selectionMessage.setLayout(gL2);
		
		JLabel texteSelectionMessage = new JLabel(
    		"Sélectionner le message à envoyer à l'agent :");
		listeMessagePossibleAffichee = new JList();
		JScrollPane listeMessagePossibleA = new JScrollPane(listeMessagePossibleAffichee,
	        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
	        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//listeMessagePossibleA.setSize(50, 100);
		
		selectionMessage.add("North",texteSelectionMessage);
		selectionMessage.add("Center",listeMessagePossibleA);
		selectionMessage.setBorder(new EmptyBorder(50, 10, 10, 10));
		
		JPanel affichageSelectionMessage=new JPanel();
		GridLayout gL3 = new GridLayout(3, 1);
		affichageSelectionMessage.setLayout(gL3);
		
		JLabel texteMessageAEnvoyer = new JLabel(
    		"Message à envoyer à l'agent :");
		messageAEnvoyer=new JTextField();
		messageAEnvoyer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		messageAEnvoyer.setHorizontalAlignment(JLabel.CENTER);
		messageAEnvoyer.setOpaque(true);
		messageAEnvoyer.setBackground(Color.WHITE);
		messageAEnvoyer.setPreferredSize(new Dimension(25,100));
		
		sendButton = new JButton("Envoyer");
		effacer=new JButton ("Effacer");
		JPanel panelBouton=new JPanel();
		GridLayout gridButton = new GridLayout(1, 2);
		panelBouton.setLayout(gridButton);
		panelBouton.setBorder(new EmptyBorder(2, 40, 10, 40));
		panelBouton.add(sendButton);
		panelBouton.add(effacer);
		
		affichageSelectionMessage.add(texteMessageAEnvoyer);
		affichageSelectionMessage.add(messageAEnvoyer);
		affichageSelectionMessage.add(panelBouton);
		
		selectionMessage.setBorder(new EmptyBorder(1, 10, 1, 10));
		affichageSelectionMessage.setBorder(new EmptyBorder(10, 10, 10, 10));
		blocMessageAEnvoyer.add(selectionMessage);
		blocMessageAEnvoyer.add(affichageSelectionMessage);
		TitledBorder tb=BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Message à envoyer à l'agent");
		tb.setTitleColor(Color.BLACK);
		tb.setTitleFont(new Font(" Arial ",Font.BOLD,16));
		blocMessageAEnvoyer.setBorder(tb);
		
		/*
		JPanel blocMessageDeAgent=new JPanel();
		GridLayout gL4 = new GridLayout(1, 1);
		blocMessageDeAgent.setLayout(gL4);

		
		messageDeAgent=new JLabel ();
		TitledBorder tb2=BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Message de l'agent");
		tb2.setTitleColor(Color.BLACK);
		tb2.setTitleFont(new Font(" Arial ",Font.BOLD,16)); 
		messageDeAgent.setBorder(tb2);
		messageDeAgent.setText("Bonjour, que desirez vous ?");
		messageDeAgent.setHorizontalAlignment(JLabel.CENTER);
		messageDeAgent.setVerticalAlignment(JLabel.CENTER);
		messageDeAgent.setOpaque(true);
		messageDeAgent.setForeground(Color.BLACK);
		messageDeAgent.setBackground(Color.WHITE);
		messageDeAgent.setPreferredSize(new Dimension(25,100));
		
		blocMessageDeAgent.add(messageDeAgent);
		*/
		
		//TitledBorder tb=BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Message à envoyer à l'agent");
		//tb.setTitleColor(Color.BLACK);
		//tb.setTitleFont(new Font(" Arial ",Font.BOLD,16));
		//mainPanel.setBorder(tb);
		mainPanel.add(blocMessageAEnvoyer);
		//mainPanel.add(blocMessageDeAgent);
		
		return mainPanel;
		
	}
	
	/** Méthode qui ajoute une interprétation (phrase, formule) à l'interface 
	 * */
	public void addInterpretation(Interpretation i) {
		listeMessagePossible.add(i);
		listeMessagePossibleAffichee.setListData(listeMessagePossible);
	}
	
	/** Méthode qui met en place un ensemble d'interprétations 
	 * */
	public void integrationIntepretations(){
		this.addInterpretation(new Interpretation("Peux-tu ouvrir ma messagerie ?", SL.fromFormula("(I "+ nomJsaIhm+" (done (action (agent-identifier :name agent@test) (OUV_MESS))))")));
		this.addInterpretation(new Interpretation("Y-a-t-il des nouveaux messages pour moi ?", SL.fromFormula("(I "+ nomJsaIhm+" (done (action (agent-identifier :name agent@test) (NB_MESS))))")));
		this.addInterpretation(new Interpretation("Pourrais-tu me lire mes nouveaux messages ?", SL.fromFormula("(I "+ nomJsaIhm+" (done (action (agent-identifier :name agent@test) (LECT_MESS))))")));
		this.addInterpretation(new Interpretation("Pourrais-tu me lire mes messages urgents ?", SL.fromFormula("(I "+ nomJsaIhm+" (done (action (agent-identifier :name agent@test) (LECT_MESS_URG))))")));
		this.addInterpretation(new Interpretation("Y-a-t-il des messages de ... ?", SL.fromFormula("(I (agent-identifier :name ihm@test)(NOT-UNDERSTANDABLE))")));
		this.addInterpretation(new Interpretation("Peux-tu me mettre en contact avec ... ?", SL.fromFormula("(I (agent-identifier :name ihm@test)(NOT-UNDERSTANDABLE))")));
		
		String content2=" \"((belief_conflict))\" ";
		String query2="(INFORM :content"+content2+":receiver (set (agent-identifier :name agent@test)) :sender (agent-identifier :name ihm@test))";
		String formul2="(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name ihm@test) "+query2+") true))";
		this.addInterpretation(new Interpretation("Non, ce n'est pas ca que je voulais", SL.fromFormula(formul2)));
		
		this.addInterpretation(new Interpretation("Je voudrais fermer ma messagerie", SL.fromFormula("(I "+ nomJsaIhm+" (done (action (agent-identifier :name agent@test) (FERME_MESS))))")));
		this.repaint();
	}
	
	/** Méthode qui retourne la formule correspond à la phrase passé en paramètre
	 * @param s phrase dont on souhaite la formule
	 * @param v ensemble des interprétations (couple phrase, formule)
	 */
	public Formula stringToFormula(String s, Vector v){
		
		for(int i=0; i<v.size(); i++){
			Interpretation inter=(Interpretation)v.get(i);
			if(s.equals(inter.phrase)){
				return inter.formule;
			}
		}
		s=s.toLowerCase();
		
		if(s.startsWith("y-a-t-il des messages de ")){
			if(s.contains("Victor") || s.contains("victor")){
				Formula f=SL.fromFormula("(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) (LECT_MESSP :name virginnie))))");
				return f;
			}else{
				String name=s.substring(24, s.length()-2);
				Formula f=SL.fromFormula("(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) (LECT_MESSP :name "+name+"))))");
				return f;
			}
		}
		if(s.startsWith("peux-tu me mettre en contact avec")){
			String name=s.substring(33, s.length()-2);
			Formula f=SL.fromFormula("(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) (CONTACT :name "+name+"))))");
			return f;
			
		}
		if(s.startsWith("comment t'appelles") || s.startsWith("comment tu t'appelles") || s.startsWith("quel est ton nom") || s.startsWith("ton nom")|| s.contains("nom")){
			String content=" \"((any ?n (nom ?n)))\" ";
			String query1="(INFORM-REF :content"+content+":receiver (set (agent-identifier :name ihm@test)) :sender (agent-identifier :name agent@test))";
			String formul1="(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) "+query1+") true))";
			Formula f=SL.fromFormula(formul1);
			return f;
		}
		
		if(s.startsWith("quel age") || s.startsWith("quel est ton age") || s.startsWith("ton age")|| s.contains("âge")){
			String content=" \"((any ?a (age ?a)))\" ";
			String query1="(INFORM-REF :content"+content+":receiver (set (agent-identifier :name ihm@test)) :sender (agent-identifier :name agent@test))";
			String formul1="(I (agent-identifier :name ihm@test) (done (action (agent-identifier :name agent@test) "+query1+") true))";
			Formula f=SL.fromFormula(formul1);
			return f;
			
		}
		
		return SL.fromFormula("(I (agent-identifier :name ihm@test)(NOT-UNDERSTANDABLE))");
	}
	
	/** Méthode qui affiche dans la fenêtre le message de l'agent 
	 * @param s message à afficher
	 */
	public void afficheMessageAgent(String s){
		
		StringBuffer affichage = new StringBuffer("");
		affichage=affichage.append("<html>");
		affichage.append(s);
		//messageDeAgent.setText(affichage.toString());
		affichage.append("</html>");
			
	}
	
	/** Méthode qui ajoute au message courant dans la fenêtre le message passé en paramètre 
	 * @param s message à afficher
	 */
	public void afficheMessageAgentAppend(String s){
		
		StringBuffer affichage = new StringBuffer("");
		affichage=affichage.append("<html>");
		//affichage.append(messageDeAgent.getText());
		affichage=affichage.append("<br>");
		affichage.append(s);
		
		
		//messageDeAgent.setText(affichage.toString());
		affichage.append("</html>");
			
	}
	
	
	/** Couple (phrase,formule), 
	 * la formule étant la traduction en logique de la phrase
	 */
	static public class Interpretation {
		String phrase;
		Formula formule;
		
		public Interpretation(String phrase, Formula formule) {
			this.phrase = phrase;
			this.formule = formule;
		}

		public String toString() {
			return phrase;
		}
	}
	
}
