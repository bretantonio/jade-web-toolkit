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
package server;

import jade.core.behaviours.SimpleBehaviour;
import jade.core.Agent;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPANames;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import mobileagent.MobileAgent;
import mobileagent.WhereIsAgent;
import client.ClientApplet;
import jade.util.Logger;

/**
 * Implements the Server Agent's Behaviour.
 * Waits to receive a send mobile agent message. After receiving such a message creates a new mobile
 * agent that will be moved to the applet container and then moved back to the server container.
 *
 * @author Claudiu Anghel
 * @version 1.0
 */
public class ServerAgentBehaviour extends SimpleBehaviour {
    Agent agent;

    private boolean finished = false;

    private AgentContainer serverContainer;
    
    //logging service
    private static Logger logger = Logger.getMyLogger(ServerAgentBehaviour.class.getName());

    public ServerAgentBehaviour(Agent a, AgentContainer serverContainer) {
        super(a);
        this.agent = a;
        this.serverContainer = serverContainer;

    }

    public void action() {

        MessageTemplate m1 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

        /*
        The agent waits for a specific message.
        If it doesn't arrive the behaviour is suspended until a new message arrives.
        */
        ACLMessage msg = agent.receive(m1);

        if (msg != null && msg.getContent().equals("Send Mobile Agent")){
            if(logger.isLoggable(Logger.FINE))
            	logger.log(Logger.FINE,"Message 'Send Mobile Agent' received.");
            try {
                MobileAgent mobileAgent = new MobileAgent(MobileAgent.ON_SERVER);
                this.serverContainer.acceptNewAgent(MobileAgent.MOBILE_AGENT_NAME, mobileAgent).start();
                // register the SL0 content language
                ContentManager contentManager = mobileAgent.getContentManager();
                contentManager.registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
                // register the mobility ontology
                contentManager.registerOntology(jade.domain.mobility.MobilityOntology.getInstance());
                if(logger.isLoggable(Logger.FINE))
                	logger.log(Logger.FINE,"New Mobile Agent created.");

                String fullAgentName = Server.getFullAgentName(ClientApplet.APPLET_AGENT_NAME, Server.jadeHostName, Server.jadePort);
                mobileAgent.addBehaviour(new WhereIsAgent(mobileAgent, fullAgentName));
                finished = true;
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean done() {
      return finished;
    }
}
