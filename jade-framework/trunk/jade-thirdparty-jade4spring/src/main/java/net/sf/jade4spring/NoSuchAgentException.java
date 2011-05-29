/*
 * Created on Jan 15, 2006
 */
package net.sf.jade4spring;

/**
 * Exception class used when an agent which does not exist is being asked for.
 * @author Jaran Nilsen
 * @since 0.2
 * @version ${Revision}
 */
public class NoSuchAgentException extends Exception {

    private String agentName;


    /**
     * Create a new instance of NoSuchAgentException.
     * @param agentName The name of the agent.
     */
    public NoSuchAgentException(String agentName) {

        super();

        this.agentName = agentName;
    }


    /**
     * @return The agent name.
     */
    public String getAgentName() {

        return agentName;
    }

}
