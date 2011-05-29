/*
 * Created on Jan 10, 2006
 */
package net.sf.jade4spring.infiltrator;

import jade.domain.introspection.BornAgent;
import jade.domain.introspection.ChangedAgentOwnership;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.FrozenAgent;
import jade.domain.introspection.MovedAgent;
import jade.domain.introspection.ResumedAgent;
import jade.domain.introspection.SuspendedAgent;
import jade.domain.introspection.ThawedAgent;


/**
 * Defines event handlers for agent events.
 * @author Jaran Nilsen
 * @since 0.2
 * @version ${Revision}
 */
public interface AgentEventListener {

    /**
     * Event handler for events triggered when an agent has been resumed.
     * @param event Event details.
     */
    public void agentResumed(ResumedAgent event);


    /**
     * Event handler for events triggered when an agent has been suspended.
     * @param event Event details.
     */
    public void agentSuspended(SuspendedAgent event);


    /**
     * Event handler for events triggered when an agen has been thawed.
     * @param event Event details.
     */
    public void agentThawed(ThawedAgent event);


    /**
     * Event handler for events triggered when a new agent has been added to the
     * platform.
     * @param event Event details.
     */
    public void agentBorn(BornAgent event);


    /**
     * Event handler for events triggered when the ownership of an agent has
     * changed.
     * @param event Event details.
     */
    public void changedAgentOwnership(ChangedAgentOwnership event);


    /**
     * Event handler for events triggered when an agent is dead.
     * @param event Event details.
     */
    public void agentDead(DeadAgent event);


    /**
     * Event handler for events triggered when an agent has been frozen.
     * @param event Event details.
     */
    public void agentFrozen(FrozenAgent event);


    /**
     * Event handler for events triggered when an agent has been moved.
     * @param event Event details.
     */
    public void agentMoved(MovedAgent event);
}
