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
 * ManAgent.java
 * Created on 26 nov. 2004
 * Author : Thierry Martinez & Vincent Pautret
 */
package jsademos.temperature;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.VariableNode;
import jade.semantics.lang.sl.parser.SLParser;
import jade.semantics.lang.sl.tools.SL;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;


/**
 * Class of the Man Agent.
 * Used to define the Son Agent.
 * @author Thierry Martinez & Vincent Pautret - France Telecom
 * @version Date: 2004/11/26 Revision: 1.0
 */
public class ManAgent extends SemanticAgent {
    
    /**
     * The display of the agent
     */
    DemoAgentGui gui = null;
    
    /**
     * Table of clothing items
     */
    Set clothings;
    
    /***
     * Panel used to display the clothing icons
     */
    JPanel manPanel;
    
    /**
     * AID of the display
     */
    AID displayAID = null;
    
    
    /**
     * All clothing query pattern
     */
    final IdentifyingExpression ALL_CLOTHING_PATTERN = 
        (IdentifyingExpression)SL.term("(all ?x (wearing ??agent ?x))");
    
    /**
     * RequestWhenever content pattern
     */
    final Content REQUEST_WHENEVER_SUBSCRIBE_PATTERN = 
        SL.content("((action ??thermometer (INFORM :content ??content :receiver (set ??me) :sender ??thermometer)) ??phi)");
    
    /**
     * Inform unsubscribe pattern
     */
    final Content INFORM_UNSUBSCRIBE_PATTERN = 
        SL.content("((or " +
                "    (or (forall ??e (not (done ??e (not (B ??thermometer ??phi )))))" +
                "        (not (B ??thermometer ??phi )))" +
                " (not (I ??me (done " +
                "(action ??thermometer (INFORM :content ??content :receiver (set ??me) :sender ??thermometer)) true)))" +
        "))");
    
    /*********************************************************************/
    /**                         CONSTRUCTOR                             **/
    /*********************************************************************/
    /**
     * Creates a new ManAgent
     */
    public ManAgent() {
        super(new ManAgentCapabilities());
    }
    
    /*********************************************************************/
    /**				 			METHODS		**/
    /*********************************************************************/
    
    /**
     * Setup of this agent
     */
    @Override
	public void setup() {
        testArgs();
        super.setup();
        clothings = new SortedSetImpl();
        displayAID = new AID((getArguments()[1]).toString(),true);
        ((ManAgentCapabilities)getSemanticCapabilities())
        	.setMotherFromAID(new AID(getArguments()[2].toString(), AID.ISGUID));
        setupGui();
		
    } // End of setup/0
    
    
    /*********************************************************************/
    /**				 			INTERNALS	**/
    /*********************************************************************/
    
    /**
     * Repaints the man tempPanel when a clothing is put on
     * @param clothing the clothing to be put on
     */
    protected void putOn(String clothing) {
        clothings.add(clothing);
        if (manPanel != null) manPanel.repaint();
    } // End of putOn/1
    
    /**
     * Repaints the man tempPanel when a clothing is taken off
     * @param clothing
     */
    protected void takeOff(String clothing) {
        clothings.remove(clothing);
        if (manPanel != null) manPanel.repaint();
    } // End of takeOff/1
    
    /**
     * Tests the arguments
     */
    private void testArgs() {
        Object[] args = getArguments();
        if ( args == null || args.length < 3 ) {
            System.err.println("ERROR : agent should be run like this "
                    +"<agent name>:DemoAgent\\(<file_name agent_to_subscribe mother_agent_name [showkb]>\\)");
            System.err.print("ERROR : current args are :");
            for (int i=0; i<args.length; i++) {System.err.print(args[i]+" ");}
            System.err.println();
            System.exit(1);
        }
        System.err.print("setup of DemoAgent(");
        for (int i=0; i<args.length; i++) {System.err.print(args[i]+" ");}
        System.err.println(")");
    } 
    
    /**
     * Sets the gui within the given tempPanel
     */
    private void setupGui(){
        Object[] args = getArguments();
        
        gui = new DemoAgentGui(getName(), args[0].toString(), this, true,  args.length >= 4 && args[3].equals("showkb"));
        
        try {
            manPanel = (new JPanel() {
                @Override
				public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(DemoAgentGui.getIcon("man").getImage(), 0, 0, null);
                    Object[] array = clothings.toArray();
                    for (int i = array.length - 1; i >=0 ; i--) {
                        String clothing = (String)array[i];
                        ImageIcon icon = DemoAgentGui.getIcon(clothing);
                        if ( icon != null ) {
                        	g.drawImage(icon.getImage(), 0, 0, null);
                        }
                    }
                }});
        }
        catch (Exception e) {e.printStackTrace();}
        
        manPanel.setPreferredSize(new Dimension(250,291));
        gui.getCustomPanel().add(manPanel);
        
        final JToggleButton toggleButton = new JToggleButton("Subscribe");
        toggleButton.setIcon(DemoAgentGui.getIcon("red"));
        toggleButton.addActionListener(new AbstractAction("Subscribing") {
            public void actionPerformed(ActionEvent evt) {
                if (toggleButton.isSelected()) {
                    toggleButton.setIcon(DemoAgentGui.getIcon("green"));
                    toggleButton.setText("UnSubscribe");
                    manageTempSubscription("(temperature_gt 0)", true);
                    manageTempSubscription("(temperature_gt 10)", true);
                    manageTempSubscription("(temperature_gt 15)", true);
                    manageTempSubscription("(temperature_gt 20)", true);
                    manageTempSubscription("(not (temperature_gt 0))", true);
                    manageTempSubscription("(not (temperature_gt 10))", true);
                    manageTempSubscription("(not (temperature_gt 15))", true);
                    manageTempSubscription("(not (temperature_gt 20))", true);
                } 
                else {
                    toggleButton.setIcon(DemoAgentGui.getIcon("red"));
                    toggleButton.setText("Subscribe");
                    manageTempSubscription("(temperature_gt 0)", false);
                    manageTempSubscription("(temperature_gt 10)", false);
                    manageTempSubscription("(temperature_gt 15)", false);
                    manageTempSubscription("(temperature_gt 20)", false);
                    manageTempSubscription("(not (temperature_gt 0))", false);
                    manageTempSubscription("(not (temperature_gt 10))", false);
                    manageTempSubscription("(not (temperature_gt 15))", false);
                    manageTempSubscription("(not (temperature_gt 20))", false);
                }
            }});
        gui.getCustomPanel().add(BorderLayout.SOUTH, toggleButton);
        gui.pack();
    } // End of setupGui/0
    
    /**
     * Sends messages. Used by the subscribe button.
     * @param formulaStr a formula
     * @param subscribe true to send a RequestWhenever, false to send an Unsubscribe
     */
    protected void manageTempSubscription(String formulaStr, boolean subscribe) {
        try {
            ACLMessage msg = new ACLMessage(subscribe ? ACLMessage.REQUEST_WHENEVER : ACLMessage.INFORM);
            msg.setSender(getAID());
            msg.addReceiver(displayAID);
            Term receiver = SLParser.getParser().parseTerm(displayAID.toString());
            Formula formula = SLParser.getParser().parseFormula(formulaStr);
            Content content = SLParser.getParser().parseContent("("+formula+")");
            if (subscribe) {
                msg.setContent(SL.instantiate(REQUEST_WHENEVER_SUBSCRIBE_PATTERN, 
                        "me", getSemanticCapabilities().getAgentName(),
                        "thermometer", receiver,
                        "content", SL.string(content.toString()),
                        "phi", formula).toString());
            } else {
                Content result = (Content)INFORM_UNSUBSCRIBE_PATTERN.getClone();
                SL.set(result, "e", new VariableNode("e"));
                SL.set(result, "me", getSemanticCapabilities().getAgentName());
                SL.set(result, "thermometer", receiver);
                SL.set(result, "content", SL.string(content.toString()));
                SL.set(result, "phi", formula);
                SL.substituteMetaReferences(result);
                msg.setContent(result.toString());
            }
            send(msg);
            System.err.println("["+msg+"] was just sent");
        } catch(Exception e) {e.printStackTrace();}
    } // End of manageTempSubscription/2
} // End of class ManAgent