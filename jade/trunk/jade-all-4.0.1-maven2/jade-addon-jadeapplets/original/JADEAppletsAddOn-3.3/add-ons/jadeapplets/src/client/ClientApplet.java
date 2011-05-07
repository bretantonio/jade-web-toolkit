/**
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 *
 * GNU Lesser General Public License
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */
package client;

import server.Server;

import java.applet.Applet;
import java.awt.*;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Specifier;
import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import javax.swing.*;

/**
 * Creates a jade agents container within an applet and an agent within this container.
 * The applet agent sends a message to the server agent requesting the migration of a mobile agent.
 *
 * @author Claudiu Anghel
 * @version 1.0
 */
public class ClientApplet extends Applet  {

    /**
     * Applet agent' name.
     */
    public static final String APPLET_AGENT_NAME = "AppletAgent";

    /**
     * The applet agents container.
     */
    private jade.wrapper.AgentContainer appletContainer;

    /**
     * The message panel.
     */
    MessagePanel messagePanel;

    /**
     * The message buffer displayed within the applet.
     */
    private StringBuffer messageBuffer = new StringBuffer();

    /**
     * The host name of the jade main platform.
     */
    public static String jadeHostName;

    /**
     * The port of the jade main platform.
     */
    public static int jadePort;
    
    //logging
    private static Logger logger = Logger.getMyLogger(ClientApplet.class.getName());

    /**
     * Initializes the applet.
     */
    public void init() {
        super.init();
        messagePanel = new MessagePanel();
        this.setLayout(new BorderLayout());
        this.add("Center", messagePanel);
        jadeHostName = getParameter("jadeHostName");
        jadePort = Integer.parseInt(getParameter("jadePort"));
        int clientPort = Integer.parseInt(getParameter("appletContainerPort"));
        createAppletAgent(clientPort);
    }

    /**
     * Creates a jade agents container an an agent within the applet.
     * @param clientPort the port for the applet container mtp.
     */
    private void createAppletAgent(int clientPort) {
        /* start agents container */

        Profile profile = new ProfileImpl(jadeHostName, jadePort, null);

        Specifier s = new Specifier();
        s.setClassName("jamr.jademtp.http.MessageTransportProtocol");
        Object a[] = new Object[1];

        a[0] = "http://" + jadeHostName + ":" + clientPort + "/test";
        s.setArgs(a);

        jade.util.leap.List l = new ArrayList(1);
        l.add(s);
        profile.setSpecifiers(profile.MTPS, l);

        jade.core.Runtime r = jade.core.Runtime.instance();
        appletContainer = r.createAgentContainer(profile);


        /* instantiate a new PersonalAgent object and add it to */
        try {
            AppletAgent appletAgent = new AppletAgent(this);
            appletContainer.acceptNewAgent(APPLET_AGENT_NAME, appletAgent).start();
            if(logger.isLoggable(Logger.FINE))
            	logger.log(Logger.FINE,"Applet agent created.");
            appletAgent.setMessage("Applet agent created.");
            sendMessage(appletAgent);
        } catch (Exception e) {
            if(logger.isLoggable(Logger.WARNING))
            	logger.log(Logger.WARNING,e.getMessage());
        }

    }

    /**
     * Sends a message to server agent requesting the migration of a mobile agent.
     *
     * @param appletAgent the applet agent.
     */
    private void sendMessage(AppletAgent appletAgent) {
        ACLMessage msgToSA = new ACLMessage(ACLMessage.INFORM);
        msgToSA.setLanguage("PlainText");
        msgToSA.setSender(appletAgent.getAID());

        AID saAID = new AID();
        saAID.setName(Server.getFullAgentName(Server.SERVER_AGENT_NAME, jadeHostName, jadePort));

        msgToSA.addReceiver(saAID);
        msgToSA.setContent("Send Mobile Agent");
        appletAgent.send(msgToSA);

        if(logger.isLoggable(Logger.FINE))
        	logger.log(Logger.FINE,"Message 'Send Mobile Agent' sent to server agent.");
        appletAgent.setMessage("Message 'Send Mobile Agent' sent to server agent.");
    }

    /**
     * Used for displaying messages in the applet.
     */
    class MessagePanel extends JPanel {
        public static final int ROWS = 3;

        JTextArea textArea;
        Font font = new Font("Arial", Font.PLAIN, 11);

        public MessagePanel() {
            createGUI();
        }

        private void createGUI() {
            setLayout(new BorderLayout());
            setBackground(Color.white);

            textArea = new JTextArea(ROWS, 0);
            textArea.setFont(font);
            textArea.setEditable(false);

            this.add("Center", textArea);
        }

        /**
         * Clears all messages.
         */
        public void clear() {
            textArea.setText("");
        }

        /**
         * Writes a message.
         */
        public void setMessage(String message) {
            textArea.setText(message);
        }
    }

    /**
     * Adds a message to be displayed within the applet.
     * @param message the message.
     */
    public void addMessage(String message) {
        messageBuffer.append(message);
        messageBuffer.append("\n");
        messagePanel.setMessage(messageBuffer.toString());
    }
}
