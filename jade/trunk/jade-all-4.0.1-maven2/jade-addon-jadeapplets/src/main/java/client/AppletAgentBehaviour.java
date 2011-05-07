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

import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.ReceiverBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

/**
 * Implements the behaviour of the applet agent.
 * When receives a 'Do delete' message kills the applet agent.
 *
 * @author Claudiu Anghel
 * @version 1.0
 */
public class AppletAgentBehaviour extends SimpleBehaviour {
    AppletAgent appletAgent;
    ReceiverBehaviour receiverBehaviour;

    private boolean finished = false;
    
    //logging
    private static Logger logger = Logger.getMyLogger(AppletAgentBehaviour.class.getName());

    /**
     * Constructor.
     *
     * @param appletAgent the applet agent.
     * @param receiverBehaviour the receiver behaviour.
     */
    public AppletAgentBehaviour(AppletAgent appletAgent, ReceiverBehaviour receiverBehaviour) {
      super(appletAgent);
      this.appletAgent = appletAgent;
      this.receiverBehaviour = receiverBehaviour;
    }

    public void action() {

        if (receiverBehaviour.done()) {
            try {
                ACLMessage msg = receiverBehaviour.getMessage();
                if (msg != null && msg.getContent().equals("Do delete")) {
                    if(logger.isLoggable(Logger.FINE))
                    	logger.log(Logger.FINE,"Received message to delete applet agent.");
                    appletAgent.setMessage("Received message to delete applet agent.");
                    appletAgent.doDelete();
                    if(logger.isLoggable(Logger.FINE))
                    	logger.log(Logger.FINE,"AppletAgent killed.");
                    appletAgent.setMessage("AppletAgent killed.");
                }
            } catch (Exception e1) {
                if(logger.isLoggable(Logger.WARNING))
                	logger.log(Logger.WARNING,"AppletAgentBehaviour1: " + e1.getMessage());
            }

            finished = true;

        } else {
            try {
                receiverBehaviour.getMessage();

            } catch (ReceiverBehaviour.NotYetReady e2) {
            } catch (Exception e3) {
                if(logger.isLoggable(Logger.WARNING))
                	logger.log(Logger.WARNING,"AppletAgentBehaviour3: " + e3.getMessage());
            }
            block(2000);
        }

    }

    public boolean done() {
        return finished;
    }

}






