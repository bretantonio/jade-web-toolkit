/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

GNU Lesser General Public License

This library is custom software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Custom Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Custom Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/


/*
* SensorAgent.java
* Created on 6 dec. 2004
* Author : Thierry Martinez & Vincent Louis & Vincent Pautret
*/
package jsademos.temperature;

import jade.core.AID;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.Tools;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.tools.SL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
* Class of the Temperature Sensor Agent
* @author Thierry Martinez & Vincent Louis & Vincent Pautret - France Telecom
* @version Date: 2004/12/06 Revision: 1.0 
*/
public class SensorAgent extends SemanticAgent {

	private 

/**
    * Panel of the sensor
    */
   JPanel mainPanel = new JPanel(new BorderLayout());
   
   /**
    * The Df agent
    */
   Term dfagent = null;
   /**
    * 
    */
   Formula onFormula;
   /**
    * 
    */
   Formula offFormula;
   
   /*********************************************************************/
   /**                         CONSTRUCTOR                             **/
   /*********************************************************************/
   /**
    * Creates a new Sensor agent.
    */
   public SensorAgent() {
       super(new SensorCapabilities());
   }
   
   /** ****************************************************************** */
   /** METHODS * */
   /** ****************************************************************** */
   /**
    * Sets the colour of the sensor (yellow or gray) depending of the choice
    * of the display 
    * @param subscribed true if the sensor is subscribed by the display, false
    * if not
    */
   void setSubscribed(boolean subscribed)
   {
       if ( subscribed ) {
           mainPanel.setBackground(Color.YELLOW);
       }
       else {
           mainPanel.setBackground(Color.GRAY);
       }
   }
   
   /**
    * Set up of the agent
    */
   @Override
public void setup() {
       super.setup();
       getSemanticCapabilities().interpret("(precision "+Integer.parseInt((String)getArguments()[0])+")");
       guiSetup();
       setSubscribed(false);
       dfagent = Tools.AID2Term(new AID(getArguments()[1].toString(),true));
       onFormula = SL.formula("(sensor "+getSemanticCapabilities().getAgentName()+")");
       offFormula = SL.formula("(not (sensor "+getSemanticCapabilities().getAgentName()+"))");
   } // End of setup
   
   /**
    * GUI set up of the agent
    */
   
protected void guiSetup() {
       final int precision = Integer.parseInt((String)getArguments()[0]);
       final int tenPowerPrecision = (int)Math.pow(10, precision);
       final JSlider slider = new JSlider(SwingConstants.VERTICAL, -20*tenPowerPrecision, 50*tenPowerPrecision, 0);
       final JFrame frame = new JFrame(getName());
       final JToggleButton toggleButton = new JToggleButton("Off");
       frame.getContentPane().add(mainPanel);
       
       // Add the toggle button on/off
       toggleButton.addActionListener(new AbstractAction("On") {
    	   
           public void actionPerformed(ActionEvent evt) {
               if (toggleButton.isSelected()) {
                   //Informs the df agent that it is "on"
                   toggleButton.setIcon(DemoAgentGui.getIcon("green"));
                   toggleButton.setText("On");
                   getSemanticCapabilities().inform(onFormula, dfagent);
               }
               else {
                   //Informs the df agent that is "off"
                   toggleButton.setIcon(DemoAgentGui.getIcon("red"));
                   toggleButton.setText("Off");
                   getSemanticCapabilities().inform(offFormula, dfagent);                  
               }}});
       mainPanel.add(BorderLayout.NORTH, toggleButton);
       
       // Add the slider
       mainPanel.add(slider);
       //#DOTNET_EXCLUDE_BEGIN
        mainPanel.add(BorderLayout.SOUTH, new JLabel(getName().replaceAll("@test","")));
        //#DOTNET_EXCLUDE_END
        /*#DOTNET_INCLUDE_BEGIN
       mainPanel.add(BorderLayout.SOUTH, new JLabel(getName().Replace("@test","")));
        #DOTNET_INCLUDE_END*/
       java.util.Hashtable labelTable = new java.util.Hashtable();
       final DecimalFormat format = (precision == 0
               ? new DecimalFormat("00") 
                       : (precision == 1 
                               ? new DecimalFormat("00.0")
                                       : new DecimalFormat("00.00")));
       labelTable.put(new Integer(50*tenPowerPrecision), new JLabel(format.format(50)));
       labelTable.put(new Integer(40*tenPowerPrecision), new JLabel(format.format(40)));
       labelTable.put(new Integer(30*tenPowerPrecision), new JLabel(format.format(30)));
       labelTable.put(new Integer(20*tenPowerPrecision), new JLabel(format.format(20)));
       labelTable.put(new Integer(10*tenPowerPrecision), new JLabel(format.format(10)));
       labelTable.put(new Integer(0), new JLabel(format.format(0)));
       labelTable.put(new Integer(-10*tenPowerPrecision), new JLabel(format.format(-10)));
       labelTable.put(new Integer(-20*tenPowerPrecision), new JLabel(format.format(-20)));
       slider.setLabelTable(labelTable);
       slider.setPaintLabels(true);
       slider.setPaintTicks(true);
       slider.addChangeListener(new ChangeListener() {
           public void stateChanged(ChangeEvent evt) {
               getSemanticCapabilities().interpret("(= (iota ?x (temperature ?x)) " + (double)slider.getValue()/tenPowerPrecision + ")");
           }
       });
       slider.setValue(21*tenPowerPrecision);
       frame.setSize(100, 400);
       frame.setVisible(true);
   } // End of guiSetup/0
   
} // End of SensorAgent