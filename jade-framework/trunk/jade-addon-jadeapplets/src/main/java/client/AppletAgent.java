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

import jade.core.Agent;
import jade.core.behaviours.ReceiverBehaviour;
import jade.lang.acl.MessageTemplate;

/**
 * Implements the applet agent.
 *
 * @author Claudiu Anghel
 * @version 1.0
 */
public class AppletAgent extends Agent {

    /**
     * The applet.
     */
    ClientApplet clientApplet;

    /**
     * Contructor.
     *
     * @param clientApplet the applet.
     */
    public AppletAgent(ClientApplet clientApplet) {
        this.clientApplet = clientApplet;
    }


    /**
     * Adds the behaviour.
     */
    protected void setup() {
        /* listen for messages */
        ReceiverBehaviour receiverBehaviour = new ReceiverBehaviour(this, -1, MessageTemplate.MatchLanguage("PlainText"));
        addBehaviour(receiverBehaviour);
        /* add main behaviour */
        addBehaviour(new AppletAgentBehaviour(this, receiverBehaviour));
    }

    /**
     * Adds a message to be displayed within the applet.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        clientApplet.addMessage(message);
    }

}
