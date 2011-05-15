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

import jade.proto.SimpleAchieveREInitiator;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;
import jade.core.Location;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.domain.mobility.MobilityOntology;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.util.Logger;

import java.util.Iterator;

/**
 * This behaviour extends <code>SimpleAchieveREInitiator</code> in order to request
 * the AMS to get the location of an agent.
 * After getting the location moves the current agent to that location.
 *
 * @author Claudiu Anghel
 * @version 1.0
 */

public class WhereIsAgent extends SimpleAchieveREInitiator {

    private ACLMessage request;
    
    private static Logger logger = Logger.getMyLogger(WhereIsAgent.class.getName());

    /**
     * Contructor.
     *
     * @param agent the agent that will be moved.
     * @param agentName the name of the agent that gives the location where the received agent will be moved.
     */
    public WhereIsAgent(Agent agent, String agentName) {
        // call the constructor of SimpleAchieveREInitiator
        super(agent, new ACLMessage(ACLMessage.REQUEST));

        request = (ACLMessage) getDataStore().get(REQUEST_KEY);
        // fills all parameters of the request ACLMessage
        request.clearAllReceiver();
        request.addReceiver(agent.getAMS());
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
        request.setOntology(MobilityOntology.NAME);
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

        // creates the content of the ACLMessage and sends the message
        try {
            Action action = new Action();
            action.setActor(agent.getAMS());

            WhereIsAgentAction whereIsAgentAction = new WhereIsAgentAction();
            AID agentAID = new AID();
            agentAID.setName(agentName);
            whereIsAgentAction.setAgentIdentifier(agentAID);
            action.setAction(whereIsAgentAction);
            agent.getContentManager().fillContent(request, action);
        } catch (Exception fe) {
            fe.printStackTrace();
        }

        reset(request);

        if(logger.isLoggable(Logger.CONFIG))
        	logger.log(Logger.CONFIG,"Try to move mobile agent to location of: " + agentName);
    }

    protected void handleNotUnderstood(ACLMessage reply) {
        if(logger.isLoggable(Logger.FINE))
        	logger.log(Logger.FINE,myAgent.getLocalName() + " handleNotUnderstood : " + reply.toString());
    }

    protected void handleRefuse(ACLMessage reply) {
        if(logger.isLoggable(Logger.FINE))
        	logger.log(Logger.FINE,myAgent.getLocalName() + " handleRefuse : " + reply.toString());
    }

    protected void handleFailure(ACLMessage reply) {
        if(logger.isLoggable(Logger.FINE))
        	logger.log(Logger.FINE,myAgent.getLocalName() + " handleFailure : " + reply.toString());
    }

    protected void handleAgree(ACLMessage reply) {
    }

    protected void handleInform(ACLMessage inform) {
        System.out.println(inform.toString());
        Location location = null;
        try {
            Result results = (Result) myAgent.getContentManager().extractContent(inform);
            Iterator locationsIt = results.getItems().iterator();

            if (locationsIt.hasNext()) {
                location = (Location) locationsIt.next();
            }
            // location - represents the location where the given agent is located
            myAgent.doMove(location);
            if(logger.isLoggable(Logger.FINE))
            	logger.log(Logger.FINE,"Agent Moved to the destination !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
