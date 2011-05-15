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

import jade.core.ProfileImpl;
import jade.core.Specifier;
import jade.core.Profile;
import jade.wrapper.StaleProxyException;
import jade.util.leap.ArrayList;
import jade.Boot;
import jade.core.Runtime;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Implements the server. Handles the start of the jade platform, the creation of the server container
 * and of the server agent.
 *
 * @author Claudiu Anghel
 * @version 1.0
 */
public class Server {

    /**
     * The server agent's name.
     */
    public static final String SERVER_AGENT_NAME = "ServerAgent";

    /**
     * The server's host name.
     */
    public static String jadeHostName;

    /**
     * The server's port.
     */
    public static int jadePort;

    /**
     * the server instance.
     */
    private static Server server;

    /**
     * the server container.
     */
    private jade.wrapper.AgentContainer serverContainer;


    /**
     * Constructor.
     */
    private Server() {
        try {
            jadeHostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the unique instance of the server.
     * @return the unique instance of the server
     */
    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    /**
     * Starts the server main container.
     *
     * @param jadePlatformPort the jade platform port.
     * @param serverContainerPort the server container's port.
     */
    public void start(int jadePlatformPort, int serverContainerPort) {

        try {
            jadePort = jadePlatformPort;
            /* start Jade */
            String[] args = new String[] {"-gui", "-mtp", "jamr.jademtp.http.MessageTransportProtocol",
                                          "-port", String.valueOf(Server.jadePort) };
            Boot.main(args);

            /* create server container */
            Profile profile = new ProfileImpl(jadeHostName, jadePort, null);

            Specifier s = new Specifier();
            s.setClassName("jamr.jademtp.http.MessageTransportProtocol");
            Object a[] = new Object[1];
            a[0] = "http://" + jadeHostName + ":" + serverContainerPort + "/server";
            s.setArgs(a);

            jade.util.leap.List l = new ArrayList(1);
            l.add(s);
            profile.setSpecifiers(profile.MTPS, l);

            Runtime runtime = Runtime.instance();
            this.serverContainer = runtime.createAgentContainer(profile);

            /* creating server agent */
            ServerAgent serverAgent = new ServerAgent(serverContainer);
            this.serverContainer.acceptNewAgent(SERVER_AGENT_NAME, serverAgent).start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    /**
     * for testing purposes
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java server.Server <jadePlatformPort> <serverContainerPort>");
        }
        else {
            Server.getInstance().start(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }
    }

    /**
     * Gets the full agent name by appending to the agent name the host name, port and '/JADE' string.
     *
     * @param agentName the agent's name
     * @param jadeHostName the host name for the jade platform
     * @param jadePort the port of the jade platform
     * @return the agent's full name
     */
    public static String getFullAgentName(String agentName, String jadeHostName, int jadePort) {
        StringBuffer fullAgentName = new StringBuffer(agentName);
        fullAgentName.append("@");
        fullAgentName.append(jadeHostName);
        fullAgentName.append(":");
        fullAgentName.append(jadePort);
        fullAgentName.append("/JADE");
        return fullAgentName.toString();
    }

}
