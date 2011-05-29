package net.sf.jade4spring.internal;

import static java.lang.String.format;
import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.ChangedAgentOwnership;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.FrozenAgent;
import jade.domain.introspection.MovedAgent;
import jade.domain.introspection.ResumedAgent;
import jade.domain.introspection.SuspendedAgent;
import jade.domain.introspection.ThawedAgent;

import java.util.Map;

import net.sf.jade4spring.AgentMeta;
import net.sf.jade4spring.infiltrator.AgentEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JadeBeanAgentEventListener implements AgentEventListener {

    private JadeBeanMetaContainer jadeBeanMetaContainer;

    private Log l = LogFactory.getLog(getClass());

    public JadeBeanAgentEventListener(JadeBeanMetaContainer jadeBeanMetaContainer) {

        this.setJadeBeanMetaContainer(jadeBeanMetaContainer);
    }

    /**
     * @see net.sf.jade4spring.infiltrator.AgentEventListener#agentResumed(jade.domain.introspection.ResumedAgent)
     */
    public void agentResumed(final ResumedAgent event) {

        String stateName = "active";
        AID agent = event.getAgent();
        jadeBeanMetaContainer.setStateToAgentMeta(agent, stateName);

        l.info(format("Agent %s resumed.", agent.getName()));
    }

    /**
     * @see net.sf.jade4spring.infiltrator.AgentEventListener#agentSuspended(jade.domain.introspection.SuspendedAgent)
     */
    public void agentSuspended(final SuspendedAgent event) {

        String stateName = "suspended";
        AID agent = event.getAgent();
        jadeBeanMetaContainer.setStateToAgentMeta(agent, stateName);

        l.info(format("Agent %s suspended.", agent.getName()));
    }

    /**
     * @see net.sf.jade4spring.infiltrator.AgentEventListener#agentThawed(jade.domain.introspection.ThawedAgent)
     */
    public void agentThawed(final ThawedAgent event) {

        l.info(format("Agent %s thawed.", event.getAgent().getName()));
    }

    /**
     * @see net.sf.jade4spring.infiltrator.AgentEventListener#agentBorn(jade.domain.introspection.BornAgent)
     */
    public void agentBorn(final BornAgent event) {

        AID agent = event.getAgent();
        ContainerID containerId = event.getWhere();

        jadeBeanMetaContainer.addAgentMetaWhenBornAgentEvent(event, agent, containerId);

        l.info(format("Agent %s born.", agent.getName()));
    }

    /**
     * @see net.sf.jade4spring.infiltrator.AgentEventListener#changedAgentOwnership(jade.domain.introspection.ChangedAgentOwnership)
     */
    public void changedAgentOwnership(final ChangedAgentOwnership event) {

        AID agent = event.getAgent();
        ContainerID containerId = event.getWhere();
        String containerTo = event.getTo();

        AgentMeta meta = jadeBeanMetaContainer.getAgentMeta(containerId, agent);

        meta.setOwnership(containerTo);

        l.info(format("Ownership for agent %s changed from %s to %s.", agent.getName(),
                event.getFrom(), containerTo));
    }

    /**
     * @see net.sf.jade4spring.infiltrator.AgentEventListener#agentDead(jade.domain.introspection.DeadAgent)
     */
    public void agentDead(final DeadAgent event) {

        ContainerID containerId = event.getWhere();
        AID agent = event.getAgent();
        jadeBeanMetaContainer.validateIfContainerAndAgentExists(containerId, agent);

        Map<AID, AgentMeta> agents = jadeBeanMetaContainer.findContainerByContainerId(containerId);
        agents.remove(agent);

        l.info(format("Agent %s died.", agent.getName()));
    }

    /**
     * @see net.sf.jade4spring.infiltrator.AgentEventListener#agentFrozen(jade.domain.introspection.FrozenAgent)
     */
    public void agentFrozen(final FrozenAgent event) {

        l.info(format("Agent %s frozen.", event.getAgent().getName()));
    }

    /**
     * @see net.sf.jade4spring.infiltrator.AgentEventListener#agentMoved(jade.domain.introspection.MovedAgent)
     */
    public void agentMoved(final MovedAgent event) {

        AID agent = event.getAgent();

        ContainerID containerFrom = getContainerFromAndValidateAgent(event, agent);
        ContainerID containerTo = getContainerToAndValidateContainerAndAgent(event);
        moveAgentToANewContainer(agent, containerFrom, containerTo);

        l.info(format("Agent %s moved from %s to %s .", agent.getName(), containerFrom.getName(),
                containerTo.getName()));
    }

    private ContainerID getContainerFromAndValidateAgent(final MovedAgent event, final AID agent) {

        ContainerID containerFrom = event.getFrom();
        jadeBeanMetaContainer.validateIfContainerAndAgentExists(containerFrom, agent);
        return containerFrom;
    }

    private ContainerID getContainerToAndValidateContainerAndAgent(final MovedAgent event) {

        ContainerID containerTo = event.getTo();
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerTo);
        return containerTo;
    }

    private void moveAgentToANewContainer(final AID agent, final ContainerID containerFrom,
            final ContainerID containerTo) {

        Map<AID, AgentMeta> agentsFrom = jadeBeanMetaContainer
                .findContainerByContainerId(containerFrom);
        Map<AID, AgentMeta> agentsTo = jadeBeanMetaContainer
                .findContainerByContainerId(containerTo);
        AgentMeta currentMeta = agentsFrom.get(agent);
        agentsFrom.remove(agent);
        agentsTo.put(agent, currentMeta);
    }

    public void setJadeBeanMetaContainer(JadeBeanMetaContainer jadeBeanMetaContainer) {

        this.jadeBeanMetaContainer = jadeBeanMetaContainer;
    }

}
