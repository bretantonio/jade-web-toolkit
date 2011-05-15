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

package jade.semantics.ext.emotion.tools;

import jade.semantics.ext.emotion.EmotionalState;
import jade.semantics.ext.emotion.EmotionalStateObserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/** Interface texte et graphique permettant l'affichage des émotions */
public class InterfaceTexteEmotion extends JFrame implements EmotionalStateObserver {

	private JLabel texteEmotionsNE;
	private JLabel texteEmotionsE;
	
	private JPanel panelPrincipal;
	
	private JPanel interfaceGraph;
	private JPanel interfaceCourbe;
	
//	private JButton intialisationButton;
	
	private JLabel texteHistoEmo;
	
//	private EmotionalState emotionsAgent;
	
	public InterfaceTexteEmotion (){
		super("Etat émotionnel de l'agent");
//		emotionsAgent=e;
		interfaceGraph=new InterfaceGraphEmotion(false);
		interfaceCourbe=new InterfaceGraphEmotion(true);
		
		interfaceGraph.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
//		intialisationButton=new JButton("Initialisation");
		
		
		JTabbedPane onglets=new JTabbedPane(SwingConstants.TOP);
		panelPrincipal=constructionPanel();
		onglets.addTab("Valeurs numériques", panelPrincipal);
		onglets.addTab("Histogrammes", interfaceGraph); 
		
		onglets.addTab("Courbes", interfaceCourbe); 
		
		texteHistoEmo=new JLabel();
		JScrollPane texteHistoEmoScroll = new JScrollPane(texteHistoEmo,
		        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
		        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		onglets.addTab("Historique des émotions déclenchées", texteHistoEmoScroll );
		
		onglets.setOpaque(true);
		JPanel panelOnglets=new JPanel();
		GridLayout gLOnglet = new GridLayout(1, 1);
		panelOnglets.setLayout(gLOnglet);
		panelOnglets.add(onglets);
		
		getContentPane().add(panelOnglets);
		
//		setAffichageEmotionsNE(e.etatEmotionnelNEToString());
//		setAffichageEmotionsE(e.etatEmotionnelEToString());
		affichage();
		
		
		
	}
	
	public void affichage(){
		// largeur, hauteur
		this.setSize(400, 600);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width - this.getWidth(), dim.height/2 - getHeight()/2);
		setVisible(true);
	}
	
	public JPanel constructionPanel(){
		
		JPanel mainPanelFin=new JPanel();
		BorderLayout bl = new BorderLayout();
		mainPanelFin.setLayout(bl);
		
		
		JPanel mainPanel=new JPanel();
		GridLayout gL = new GridLayout(2, 1);
		mainPanel.setLayout(gL);
		
		
		texteEmotionsNE=new JLabel();
		TitledBorder tb1=BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Emotions de l'agent");
		tb1.setTitleColor(Color.BLACK);
		tb1.setTitleFont(new Font(" Arial ",Font.BOLD,20)); 
		texteEmotionsNE.setBorder(tb1);
		
		texteEmotionsE=new JLabel();
		TitledBorder tb2=BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Emotions d'empathie de l'agent");
		tb2.setTitleColor(Color.BLACK);
		tb2.setTitleFont(new Font(" Arial ",Font.BOLD,20)); 
		texteEmotionsE.setBorder(tb2);
		
//		intialisationButton.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				emotionsAgent.initialisation();
//			}
//		});
	
		mainPanel.add(texteEmotionsNE);
		mainPanel.add(texteEmotionsE);
		
		mainPanelFin.add("Center", mainPanel);
//		mainPanelFin.add("South", this.intialisationButton);
		
		return mainPanelFin;
		
	}
	
	
	/** Méthode permettant la mise à jour de l'affiche des émotions (texte, graphiques et expressions faciales)
	 * 
	 */
	public void emotionalStateChanged(EmotionalState e){
		((InterfaceGraphEmotion)interfaceGraph).emotionalStateChanged(e);
		((InterfaceGraphEmotion)interfaceCourbe).emotionalStateChanged(e);
//		((InterfaceGraphEmotion)interfaceGraph).updateDataSet(e);
//		((InterfaceGraphEmotion)interfaceGraph).updateDataSetEmp(e);
//		((InterfaceGraphEmotion)interfaceCourbe).updateDataSetSerie(e);
//		((InterfaceGraphEmotion)interfaceCourbe).updateDataSetSerieEmp(e);
		texteEmotionsNE.setText(e.etatEmotionnelNEToString());
		texteEmotionsE.setText(e.etatEmotionnelEToString());
		texteHistoEmo.setText(e.historiqueEmotionDeclToString());
		
//		/** Si l'agent exprime uniquement des émotions d'empathie */
//		if(agentEmpathique)
//			/** On affiche les émotions d'empathie de l'agent uniquement */
//			if(!expressionEmoDeclenchee){
//				this.transfertEloona(this.expressionEmotionEmp());
//			}else{
//				this.transfertEloona(this.expressionEmotionEmpDecl());
//			}
//		else
//			this.transfertEloona(this.expressionEmotion());

	}

	public void setAffichageEmotionsNE(String s){	
		texteEmotionsNE.setText(s);
	}
	
	public void setAffichageEmotionsE(String s){
		texteEmotionsE.setText(s);
	}
	
	public void setAffichageHistoEmo(String s){
		texteHistoEmo.setText(s);
	}
	
	
	
	
	
	
//	public void transfertEloona(String s){
//	connexionEloona.println(s);
//	System.err.println("----------Texte d'Eloona ------------");
//	if(this.lastEmpathicEmotion!=null){
//		System.err.println("Emotion empathie déclenchée "+this.lastEmpathicEmotion.getType());
//	}else{
//		System.err.println("Emotion empathie déclenchée null");
//	}
//	System.err.println("Eloona : "+s);
//	
//}
//
//public void transfertTextePlusEmotionEmp(String texte){
//	/* expression des émotions déclenchées empathques */
//	String result=this.expressionEmotionEmpDecl().concat(texte);
//	//String result=this.expressionEmotionEmp().concat(texte);
//	this.transfertEloona(result);
//}
///** Méthode qui renvoie l'intensité de l'expression faciale d'émotion à partir de l'intensité de l'émotion 
// * @param i intensité de l'émotion comprise entre 0 et 1
// * @return intensité de l'expression faciale comprise entre 50 et 100
// */
//public int intensiteEmoExprFaciale(double i){
//	/* Intensité de l'émotion comprise entre 0 et 1
//	 * Intensité de l'expression faciale comprise entre 0 et 100
//	 * On force l'intensité de l'émotion a être comprise entre le coeffExprFaciale et 100 pour qu'elle soit visible  */
//	int res=(int)(i*100)+coeffExprFaciale;
//	if (res >100){
//		return 100;
//	}else
//		return res;
//}
//
///* Renvoie le marqueur correspondant à l'expression faciale opposée à celle correspondant 
// * à son émotion déclenchée
// * Permet de modéliser la version émotionnelle non congruente de l'application 
// */
//public String exprEmotionNonCongruente(){
//	if(this.lastEmpathicEmotion!=null){
//		if(this.lastEmpathicEmotion.getType().equals("joie") ||
//				this.lastEmpathicEmotion.getType().equals("satisfaction")){
//			double r=Math.random();
//			if(r<0.25){
//				return "<mark name=\"Emotion frustration3 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//			}
//			if(r>=0.25 & r<0.5){
//				return "<mark name=\"Emotion irritation3 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//			}
//			if(r>=0.5 & r<0.75){
//				return "<mark name=\"Emotion sad2 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//			}else{
//				return "<mark name=\"Emotion Angry2 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//			}
//		}else{
//			return "<mark name=\"Emotion Happy 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//		}
//	}else{
//		return "<mark name=\"Emotion None 100 100\"/>";
//	}
//}
//public String expressionEmotionEmpDecl(){
//	if(this.lastEmpathicEmotion!=null){
//	if(this.lastEmpathicEmotion.getType().equals("joie")){
//		System.err.println("EXPRESSION JOIE ");
//		return "<mark name=\"Emotion Happy 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//	}
//	if(this.lastEmpathicEmotion.getType().equals("satisfaction")){
//		System.err.println("EXPRESSION SATISF ");
//		return "<mark name=\"Emotion satisfaction4 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//	}
//	if(this.lastEmpathicEmotion.getType().equals("frustration")){
//		System.err.println("EXPRESSION FRUSTR ");
//		return "<mark name=\"Emotion frustration3 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//	}
//	if(this.lastEmpathicEmotion.getType().equals("enervement")){
//		System.err.println("EXPRESSION ENERV ");
//		return "<mark name=\"Emotion irritation3 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//	}
//	if(this.lastEmpathicEmotion.getType().equals("tristesse")){
//		System.err.println("EXPRESSION TRISTES ");
//		return "<mark name=\"Emotion sad2 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//	}
//	if(this.lastEmpathicEmotion.getType().equals("colere")){
//		System.err.println("EXPRESSION COLERE ");
//		return "<mark name=\"Emotion Angry2 100 "+intensiteEmoExprFaciale(this.lastEmpathicEmotion.getIntensity())+"\"/> ";
//	}
//	}
//	System.err.println("EXPRESSION NONE ");
//	return "<mark name=\"Emotion None 100 100\"/>";
//	
//}
///** Transfert le tag à Eloona pour l'affiche de l'expression faciale empathique dep lus haute intensité de l'agent 
// * */
//public String expressionEmotionEmp(){
//	/** Affichage de l'expression faciale de l'émotion d'empathie de plus haute intensité */
//	double emoNeg=this.getIntensityEmp(Type.ENERV)+
//		this.getIntensityEmp(Type.FRUSTR)+
//		this.getIntensityEmp(Type.TRISTES)+this.getIntensityEmp(Type.COLERE);
//	
//	double emoPos=this.getIntensityEmp(Type.SATISF)+this.getIntensityEmp(Type.JOIE);
//	
//	if(emoPos>emoNeg){
//		if(this.getIntensityEmp(Type.SATISF)>this.getIntensityEmp(Type.JOIE)){
//			System.err.println("EXPRESSION FACIALE Satisfaction "+intensiteEmoExprFaciale(this.getIntensityEmp(Type.SATISF)));	
//			return "<mark name=\"Emotion satisfaction4 100 "+intensiteEmoExprFaciale(this.getIntensityEmp(Type.SATISF))+"\"/>";
//		
//		}else{
//			System.err.println("EXPRESSION FACIALE HAPPY "+intensiteEmoExprFaciale(this.getIntensityEmp(Type.JOIE)));
//			return "<mark name=\"Emotion Happy 100 "+intensiteEmoExprFaciale(this.getIntensityEmp(Type.JOIE))+"\"/> ";
//			
//		}
//	}else{
//		if(emoPos<emoNeg){	
//			double intTristess=this.getIntensityEmp(Type.TRISTES);
//			double intFrustr=this.getIntensityEmp(Type.FRUSTR);
//			double intEnerv=this.getIntensityEmp(Type.ENERV);
//			double intColere=this.getIntensityEmp(Type.COLERE);
//			
//			if(intColere>intTristess & intColere>intFrustr & intColere>intEnerv){
//				System.err.println("EXPRESSION FACIALE ANGRY "+intensiteEmoExprFaciale(intColere));
//				return "<mark name=\"Emotion Angry2 100 "+intensiteEmoExprFaciale(intColere)+"\"/>";
//			}
//			if(intTristess>intFrustr){
//				if(intTristess>intEnerv){
//					System.err.println("EXPRESSION FACIALE sad "+intensiteEmoExprFaciale(intTristess));		
//					return "<mark name=\"Emotion sad2 100 "+intensiteEmoExprFaciale(intTristess)+"\"/>";
//				}else{
//					System.err.println("EXPRESSION FACIALE irritation "+intensiteEmoExprFaciale(intEnerv));		
//					return "<mark name=\"Emotion irritation3 100 "+intensiteEmoExprFaciale(intEnerv)+"\"/>";
//				}
//			}else{
//				if(intFrustr>intEnerv){
//					System.err.println("EXPRESSION FACIALE frustration2 "+intensiteEmoExprFaciale(intFrustr));		
//					return "<mark name=\"Emotion frustration3 100 "+intensiteEmoExprFaciale(intFrustr)+"\"/>";
//				}else{
//					System.err.println("EXPRESSION FACIALE irritation "+intensiteEmoExprFaciale(intEnerv));
//					return "<mark name=\"Emotion irritation3 100 "+intensiteEmoExprFaciale(intEnerv)+"\"/>";
//				}
//			}
//		}else{
//			System.err.println("EXPRESSION NONE ");
//			return "<mark name=\"Emotion None 100 100\"/>";
//		}
//	}
//}
//
//
///** Transfert le tag à Eloona pour l'affiche de l'expression faciale non empathique de plus haute intensité de l'agent 
// * */
//public String expressionEmotion(){
//	/** Affichage de l'expression faciale de l'émotion de plus haute intensité de l'agent */
//	double emoNeg=this.sommeEmoNeg();
//	double emoPos=this.sommeEmoPos();
//	
//	if(emoPos>emoNeg){
//		if(this.satisf.getIntensity()>this.joie.getIntensity()){
//			System.err.println("EXPRESSION FACIALE Satisfaction "+intensiteEmoExprFaciale(this.satisf.getIntensity()));	
//			return "<mark name=\"Emotion satisfaction4 100 "+intensiteEmoExprFaciale(this.satisf.getIntensity())+"\"/> ";
//		
//		}else{
//			System.err.println("EXPRESSION FACIALE HAPPY "+intensiteEmoExprFaciale(this.joie.getIntensity()));
//			return "<mark name=\"Emotion Happy 100 "+intensiteEmoExprFaciale(this.joie.getIntensity())+"\"/> ";
//		}
//	}else{
//		if(emoPos<emoNeg){	
//			double intTristess=this.tristes.getIntensity();
//			double intFrustr=this.frustr.getIntensity();
//			double intEnerv=this.enerv.getIntensity();
//			double intColere=this.colere.getIntensity();
//			
//			if(intColere>intTristess & intColere>intFrustr & intColere>intEnerv){
//				System.err.println("EXPRESSION FACIALE ANGRY "+intensiteEmoExprFaciale(this.colere.getIntensity()));
//				return "<mark name=\"Emotion Angry2 100 "+intensiteEmoExprFaciale(this.colere.getIntensity())+"\"/>";
//			}
//			if(intTristess>intFrustr){
//				if(intTristess>intEnerv){
//					System.err.println("EXPRESSION FACIALE sad "+(int)(intTristess*100));		
//					return "<mark name=\"Emotion sad2 100 "+intensiteEmoExprFaciale(intTristess)+"\"/>";
//			
//				}else{
//					System.err.println("EXPRESSION FACIALE irritation "+intensiteEmoExprFaciale(intEnerv));		
//					return "<mark name=\"Emotion irritation3 100 "+intensiteEmoExprFaciale(intEnerv)+"\"/>";
//					
//				}
//			}else{
//				if(intFrustr>intEnerv){
//					System.err.println("EXPRESSION FACIALE frustration "+intensiteEmoExprFaciale(intFrustr));		
//					return "<mark name=\"Emotion frustration3 100 "+intensiteEmoExprFaciale(intFrustr)+"\"/>";
//				}else{
//					System.err.println("EXPRESSION FACIALE irritation "+intensiteEmoExprFaciale(intEnerv));
//					return "<mark name=\"Emotion irritation3 100 "+intensiteEmoExprFaciale(intEnerv)+"\"/>";
//				}
//			}
//		}else{
//			System.err.println("EXPRESSION NONE ");
//			return "<mark name=\"Emotion None 100 100\"/>";
//			
//		}
//	}
//}

	
	
	
	
	
	
}
