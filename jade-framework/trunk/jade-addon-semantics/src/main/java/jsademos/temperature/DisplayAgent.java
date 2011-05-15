/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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

/*
* DisplayAgent.java
* Created on 15 déc. 2004
* Author : Vincent Pautret
*/
package jsademos.temperature;


import jade.core.AID;
import jade.core.behaviours.WakerBehaviour;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.Tools;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Constant;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.tools.SL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
* Class that defines the display.
* @author Vincent Pautret - France Telecom
* @version Date: 2004/12/15 Revision: 1.0 
*/

public class DisplayAgent extends SemanticAgent {

   /**
    * Unspecifed precision 
    */
  int precision = Precision.UNSPECIFIED;
  
   /**
    * The df agent
    */
   Term dfagent = null;
   
   /**
    * Display of the temperature
    */
   TemperatureDisplay display = null;
   
   /**
    * Selected sensor agent and the corresponding precision 
    */
   Term selectedSensor = null;
   /**
    * Selected condition
    */
   Formula selectedCondition = null;
   /**
    * Selected precision
    */
   Constant selectedPrecision = SL.integer(0);
   
   /**
    * Used patterns
    */
   static final ActionExpression CFP_ACTION 
   = (ActionExpression)SL.term("(action ??receiver (INFORM-REF :content \"((any ?x (temperature ?x)))\" :receiver (set ??agent) :sender ??receiver))");
   static final IdentifyingExpression CFP_IRE 
   = (IdentifyingExpression)SL.term("(any ?x (precision ?x))");
   static final Formula CFP_CONDITION 
   = SL.formula("(precision ??X)");
   static final IdentifyingExpression SUBSCRIBE_IRE 
   = (IdentifyingExpression)SL.term("(iota ?x (temperature ?x))");
   static final IdentifyingExpression SUBSCRIBE_DF_IRE 
   = (IdentifyingExpression)SL.term("(all ?x (sensor ?x))");
   
   /*********************************************************************/
   /**                         CONSTRUCTOR                             **/
   /*********************************************************************/
   /**
    * Creates a new DisplayAgent
    */
   public DisplayAgent() {
       super(new DisplayCapabilities());
       
   }
   
   /*********************************************************************/
   /**                         METHODS     **/
   /*********************************************************************/
   
   /**
    * Selects the suitable sensor according to the received answers (Propose)
    * @param precision the precision
    * @param agent the agent sending the propose
    * @param action the action expression
    * @param condition the condition
    */
   protected void handleProposal(Constant precision, Term agent, ActionExpression action, Formula condition) {
       if ( selectedSensor == null ) {
           selectedSensor = agent;
           selectedCondition = condition;
           selectedPrecision= precision;
       }
       else {
           if (  ( this.precision == Precision.MOST && precision.intValue().longValue() > selectedPrecision.intValue().longValue() )
                   || ( this.precision == Precision.LEAST && precision.intValue().longValue() < selectedPrecision.intValue().longValue() )) {
               ((ActionExpressionNode)action).as_agent(selectedSensor);
               // Rejects the proposition
               getSemanticCapabilities().rejectProposal(action, 
            		   								    selectedCondition,
            		   								    new TrueNode(),
            		   								    selectedSensor);
               selectedSensor = agent;
               selectedCondition = condition;
               selectedPrecision = precision;
           }
           else {
               // Rejects the proposition
               getSemanticCapabilities().rejectProposal(action, 
            		   									condition,
            		   									new TrueNode(),
            		   									agent);
           }
       }
   }
   
   /**
    * Sets the precision according to the required one and the sensors availability
    * @param precision the precision
    */
    protected void setPrecision(int precision) {
       display.precisionPanel.setStatus(false);
       this.precision = precision;
       try {
           // Unsubscribe to the current selectedSensor if necessary
           if ( selectedSensor != null ) {
               getSemanticCapabilities().unsubscribe(SUBSCRIBE_IRE, selectedSensor);
               selectedSensor = null;
           }
           // Send a query-ref to the dfagent
           getSemanticCapabilities().queryRef(SUBSCRIBE_DF_IRE, dfagent);
            
           
           // Sends a CFP to all agents the name of which is given as an argument.
           // Await 1 second so that the sensors have time to subscribe to the
           // DF
           
           addBehaviour(new WakerBehaviour(this, 1000) 
            {                   
               @Override
			protected void handleElapsedTimeout() {
                   ListOfTerm sensors = getSemanticCapabilities().getMyKBase().queryRef(SUBSCRIBE_DF_IRE);
                   if (sensors != null) {
                       for(int i=0; i< sensors.size(); i++) {
                           try {
                               getSemanticCapabilities().callForProposal((ActionExpression)SL
                                       .instantiate(CFP_ACTION, 
                                               "agent", getSemanticCapabilities().getAgentName(),
                                               "receiver", sensors.element(i)),
                                               CFP_IRE,
                                               sensors.element(i));
                           }
                           catch (Exception e) {e.printStackTrace();}
                       }
                   }
               }});
           
           // Add a behaviour to send the subscribe after a while.
           addBehaviour(new WakerBehaviour(this, 2000) {
               @Override
			protected void handleElapsedTimeout() {
                   if ( selectedSensor != null ) {
                       getSemanticCapabilities().subscribe(SUBSCRIBE_IRE, selectedSensor);
                       display.precisionPanel.setStatus(true);
                   }}});
       }
       catch (Exception e) {e.printStackTrace();}
   }

   /**
    * Setup of this agent
    */
   @Override
public void setup() {
       super.setup();
       display = new TemperatureDisplay();
       dfagent = Tools.AID2Term(new AID(getArguments()[0].toString(),true));
   } // End of setup/0
   
   /*********************************************************************/
   /**                         INNER CLASSES   **/
   /*********************************************************************/
   /**
    * The display of this agent
    */
   class TemperatureDisplay extends JFrame {
       
       TemperaturePanel tempPanel = new TemperaturePanel();
       PrecisionChoicePanel precisionPanel = new PrecisionChoicePanel();
       
       /**
        * Creates the display
        */
       TemperatureDisplay() {
           super(DisplayAgent.this.getName());
           getContentPane().setLayout(new BorderLayout());
           getContentPane().add(tempPanel);
           getContentPane().add(precisionPanel, BorderLayout.NORTH);
           setBackground(Color.black);
           setSize(450,270);
           setVisible(true);
           setLocation(100,100);
       } // End of JaugeDisplay    
       
       /**
        * Sets the temperature
        * @param temp the temperature to set
        */
       void setTemperature(Double temp) {
           tempPanel.setTemperature(temp);
           long precision = Long.parseLong(selectedPrecision.intValue().toString());
           if ( precision == 0 ) 
            {
               setSize(450,270);
           }
           else {
               setSize(540+(int)precision*60,270);
           }
       } // End of setTemperature/1
       
   } // End of class ThermometerDisplay
   
   /**
    * Panel that contains the temperature
    */
   class TemperaturePanel extends JPanel {
       
       Color dk;
       String temperature;
       Digit[] digits;
       JPanel mainPanel = new JPanel(new BorderLayout());
       
       /**
        * Temperature Panel
        */
       TemperaturePanel() {
           this.setFont(new Font("TimesRoman",Font.BOLD,40));
           dk = Color.darkGray;
           this.setBackground(Color.black);
           setTemperature(new Double(0.0));
       } // End of TemperaturePanel/0
       
       /**
        * Sets all the digits for the current temperature.
        * @param color the color of the digits
        * @return an array of digits
        */
       Digit[] setDigit(Color color){
           long precision = Long.parseLong(selectedPrecision.intValue().toString());
           Digit[] lc = precision == 0 ? new Digit[5] : new Digit[5+1+(int)precision];
           if (temperature.startsWith("-")) {
               lc[0] = new Digit(11,color,dk);
           } else {
               lc[0] = new Digit(11,dk,dk);
           }
           lc[1] = new Digit(Integer.parseInt(temperature.substring(1,2)),color,dk);
           lc[2] = new Digit(Integer.parseInt(temperature.substring(2,3)),color,dk);
           
           if ( precision == 0 ) {
               lc[3] = new Digit(10,color,dk);
               lc[4] = new Digit(12,color,dk);
           }
           else if ( precision == 1 ) {
               lc[3] = new Digit(13,color,dk);
               lc[4] = new Digit(Integer.parseInt(temperature.substring(4,5)),color,dk);
               lc[5] = new Digit(10,color,dk);
               lc[6] = new Digit(12,color,dk);
           }
           else {
               lc[3] = new Digit(13,color,dk);
               lc[4] = new Digit(Integer.parseInt(temperature.substring(4,5)),color,dk);
               lc[5] = new Digit(Integer.parseInt(temperature.substring(5,6)),color,dk);
               lc[6] = new Digit(10,color,dk);
               lc[7] = new Digit(12,color,dk);
           }
           return lc;
       } // End of setLCD/1
       
       /**
        * Draws the temperature by painting the digits.
        * @param g graphical area
        */
       @Override
	public void paintComponent(Graphics g) {
           super.paintComponent(g);
           for (int i=0; i<digits.length; i++) {
               digits[i].draw(g,20+(i*80),40);  
           }
       } // End of paint/1
       
       /**
        * Sets the temperature
        * @param temp the temperature to set
        */
       void setTemperature(Double temp) {
           String tempAsAString = new DecimalFormat("00.00").format(temp);
           if ( temp.doubleValue() >= 0 ) {
               temperature = "+" + tempAsAString;
           } else {
               temperature = tempAsAString;             
           }
           if (temp.longValue() > 20) {
               digits = setDigit(Color.red);
           } else if (temp.longValue() >= 10) {
        digits = setDigit(Color.green);
           } else if (temp.longValue() >= 0 ) {
        digits = setDigit(Color.cyan);
           } else if (temp.longValue() >= -9){
        digits = setDigit(Color.white);
           } else {
               digits = setDigit(Color.white);
           }
           repaint();
       } // End of setTemperature/1
   } // End of class TemperaturePanel
   
   /**
    * Panel for choosing the precision
    */
   class PrecisionChoicePanel extends JPanel {
       static final String LEAST = "Least";
       static final String MOST = "Most";
       JComboBox combo = new JComboBox(new Object[] {"Specify a precision", LEAST, MOST});
       JLabel status = new JLabel("No subscribed sensor");
       
       public PrecisionChoicePanel() {
           add(new JLabel("Precision : "));
           add(combo);
           add(status);
           combo.addActionListener(new AbstractAction() {
               public void actionPerformed(java.awt.event.ActionEvent e) {
                   if ( combo.getSelectedItem().equals(LEAST)) {
                       setPrecision(Precision.LEAST);
                   }
                   else if ( combo.getSelectedItem().equals(MOST) ){
                       setPrecision(Precision.MOST);
                   }
               }
           });
       }
       
       void setStatus(boolean ok)
       {
           if ( ok ) {
               status.setText("Subscribed to "+Tools.term2AID(selectedSensor).getName());
           }
           else {
               status.setText("Looking for sensor...");
           }
       }
   }
} // End of class ThermometerAgent
