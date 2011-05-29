/*
 * Created on Jan 12, 2006
 */
package net.sf.jade4spring;

import jade.core.AgentState;


/**
 * Holds meta info about an agent, such as agent state.
 * @author Jaran Nilsen
 * @since 0.2
 * @version $Revision: 1.1 $
 */
public class AgentMeta {

    private AgentState state;

    private String ownership;


    /**
     * @return Returns the ownership.
     */
    public String getOwnership() {

        return ownership;
    }


    /**
     * @param ownership The ownership to set.
     */
    public void setOwnership(String ownership) {

        this.ownership = ownership;
    }


    /**
     * @return Returns the state.
     */
    public AgentState getState() {

        return state;
    }


    /**
     * @param state The state to set.
     */
    public void setState(AgentState state) {

        this.state = state;
    }

}
