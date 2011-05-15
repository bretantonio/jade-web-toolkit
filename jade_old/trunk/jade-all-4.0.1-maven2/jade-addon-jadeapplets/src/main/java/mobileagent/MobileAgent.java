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
package mobileagent;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.domain.FIPANames;
import client.ClientApplet;
import server.Server;
import jade.util.Logger;

/**
 * Implements the mobile agent.
 *
 * @author Claudiu Anghel
 * @version 1.0
 */
public class MobileAgent extends Agent {

    /**
     * The agent's state; can be ON_SERVER or ON_CLIENT.
     */
    private String state;

    /**
     * The agent's name.
     */
    public static final String MOBILE_AGENT_NAME = "MobileAgent";

    /**
     * The state value for on server.
     */
    public static final String ON_SERVER = "ON_SERVER";

    /**
     * The state value for on client.
     */
    public static final String ON_CLIENT = "ON_CLIENT";
    
    //logging
    private static Logger logger = Logger.getMyLogger(MobileAgent.class.getName());

    /**
     * Constructor.
     *
     * @param state the initial state.
     */
    public MobileAgent(String state) {
        this.state = state;
    }

    /**
     * Performed after the agent was moved to the destination.
     */
    protected void afterMove() {
        if (state.equals(ON_SERVER))  {
            state = ON_CLIENT;
            if(logger.isLoggable(Logger.FINE))
            	logger.log(Logger.FINE,"Mobile Agent moved to applet container.");
            ContentManager contentManager = getContentManager();
            contentManager.registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
            // register the mobility ontology
            contentManager.registerOntology(jade.domain.mobility.MobilityOntology.getInstance());
            addBehaviour(new WhereIsAgent(this, Server.getFullAgentName(Server.SERVER_AGENT_NAME,
                    ClientApplet.jadeHostName, ClientApplet.jadePort)));
        }
        else {
            state = ON_SERVER;
            if(logger.isLoggable(Logger.FINE))
            	logger.log(Logger.FINE,"Mobile Agent moved back to server container.");
            sendMessage();
            doDelete();
            if(logger.isLoggable(Logger.FINE))
	            logger.log(Logger.FINE,"Mobile agent deleted.");
        }
    }

    /**
     * Sends a message to the applet agent requesting the deletion of the agent.
     */
    private void sendMessage() {
        ACLMessage msgToAppletAgent = new ACLMessage(ACLMessage.INFORM);
        msgToAppletAgent.setLanguage("PlainText");
        msgToAppletAgent.setSender(getAID());

        AID appletAgentAID = new AID();
        appletAgentAID.setName(Server.getFullAgentName(ClientApplet.APPLET_AGENT_NAME, Server.jadeHostName, Server.jadePort));

        msgToAppletAgent.addReceiver(appletAgentAID);
        msgToAppletAgent.setContent("Do delete");
        send(msgToAppletAgent);

        if(logger.isLoggable(Logger.FINE))
        	logger.log(Logger.FINE,"Message 'Do delete' sent to applet agent.");
    }

}
