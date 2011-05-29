/**
 * 
 */
package net.sf.jade4spring.internal;

import static java.lang.String.format;
import jade.core.AID;
import jade.core.AgentState;
import jade.core.ContainerID;
import jade.domain.FIPAAgentManagement.APDescription;
import jade.domain.introspection.BornAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jade4spring.AgentMeta;
import net.sf.jade4spring.JadeRuntimeException;
import net.sf.jade4spring.NoSuchContainerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Container for all meta information related to registered agents.
 * 
 * @author Felipe Dalevoll Macedo
 * @author Tarjei Romtveit
 * @author Andreas Lundberg
 * 
 */
public class JadeBeanMetaContainer {

    private final Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = new ConcurrentHashMap<ContainerID, ConcurrentHashMap<AID, AgentMeta>>();

    private APDescription description;

    private final Log l = LogFactory.getLog(this.getClass());

    /**
     * Removes all meta informations stored in the container map
     * 
     */
    public void removeAllContainers() {

        containers.clear();
    }

    /**
     * Remove container meta info.
     * 
     * @param containerId
     *            Container to remove info for.
     */
    public void removeContainer(final ContainerID containerId) {

        checkIfContainersContainsIdOtherwiseThrowException(containerId);

        containers.remove(containerId);

        l.debug(format("Container %s gone.", containerId.getName()));
    }

    /**
     * Finds container by container ID.
     * 
     * @param containerId
     * @return Map of associated agent and agent meta
     */
    public Map<AID, AgentMeta> findContainerByContainerId(final ContainerID containerId) {

        return containers.get(containerId);
    }

    /**
     * Validate a container; make sure it has been registered in this Jade Bean.
     * 
     * @param containerId
     *            Container ID to validate.
     * @throws JadeRuntimeException
     *             Thrown if container is invalid.
     */
    private void checkIfContainersContainsIdOtherwiseThrowException(final ContainerID containerId)
            throws JadeRuntimeException {

        boolean containersDoesNotContainID = !containers.containsKey(containerId);
        if (containersDoesNotContainID) {
            throw new JadeRuntimeException(format("%s is not registered.", containerId.getName()));
        }
    }

    /**
     * Validate the agent map for the given container. If the map is not valid,
     * i.e. it's null, then a new map is created.
     * 
     * @param containerId
     *            ContainerID to validate agent list for.
     */
    public void createNewContainerWithIdIfNotExists(final ContainerID containerId) {

        boolean containerDoesNotHaveKey = !containers.containsKey(containerId);
        boolean isContainerValueNull = containers.get(containerId) == null;
        boolean containerDoesNotHaveTheKeyOrTheValueIsNull = containerDoesNotHaveKey
                || isContainerValueNull;
        if (containerDoesNotHaveTheKeyOrTheValueIsNull) {
            containers.put(containerId, new ConcurrentHashMap<AID, AgentMeta>());
            l.debug(format("Container %s added to the platform.", containerId.getName()));
        }
    }

    /**
     * Get ContainerID from the Map of containers for the container with the
     * given name.
     * 
     * @param locationName
     *            Name of the location to the container.
     * @return ContainerID representation of the name.
     * @throws NoSuchContainerException
     *             Thrown if the JadeBean does not have a record of a container
     *             with the given name.
     */
    public ContainerID findContainerByLocationName(final String locationName)
            throws NoSuchContainerException {

        for (ContainerID containerId : containers.keySet()) {

            if (containerId.getName().equals(locationName))
                return containerId;
        }

        throw new NoSuchContainerException(locationName);
    }

    /**
     * Get all containerids in the containers as a list
     * 
     * @return an ArrayList containing all the ContainerID objects found
     */
    public List<ContainerID> getListOfContainerID() {

        Set<ContainerID> containerSet = containers.keySet();
        return new ArrayList<ContainerID>(containerSet);
    }

    /**
     * Get a Map of agents running on the given name. Agents are represented by
     * AID and AgentMeta objects.
     * 
     * @param locationName
     *            The name of the location to get a map of agents for.
     * @return A Map of AID and AgentMeta objects.
     * @throws NoSuchContainerException
     *             Thrown if the container does not exist.
     */
    public Map<AID, AgentMeta> getAgentMetaFromLocationName(final String locationName)
            throws NoSuchContainerException {

        for (ContainerID containerId : containers.keySet()) {

            if (containerId.getName().equals(locationName)) {
                createNewContainerWithIdIfNotExists(containerId);
                return containers.get(containerId);
            }
        }

        throw new NoSuchContainerException(locationName);
    }

    /**
     * Sets stateName to agent meta
     * 
     * @param agent
     *            Agent Id
     * @param stateName
     */
    public void setStateToAgentMeta(final AID agent, final String stateName) {

        AgentMeta meta = getAgentMeta(agent);
        AgentState state = new AgentState();
        state.setName(stateName);

        meta.setState(state);
    }

    /**
     * Get AgentMeta for a given agent on the given container.
     * 
     * @param containerId
     *            Container ID.
     * @param agent
     *            Agent ID.
     * @return AgentMeta for the given agent on the given container.
     * @throws JadeRuntimeException
     *             Thrown if the Agent or Container can not be found.
     */
    public AgentMeta getAgentMeta(final ContainerID containerId, final AID agent)
            throws JadeRuntimeException {

        validateContainerExists(containerId);

        Map<AID, AgentMeta> agentMap = findContainerByContainerId(containerId);

        AgentMeta meta = agentMap.get(agent);

        return meta;

    }

    /**
     * Looks up AgentMeta for the given agent, searching all registered
     * containers. This method should only be used when a ContainerID object is
     * not available.
     * 
     * @see net.sf.jade4spring.JadeBean#getAgentMeta(ContainerID, AID)
     * @param agent
     *            Agent ID.
     * @return AgentMeta for the given agent.
     */
    public AgentMeta getAgentMeta(final AID agent) throws JadeRuntimeException {

        Set<ContainerID> containerKeys = getContainers().keySet();
        AgentMeta meta = null;
        for (ContainerID containerId : containerKeys) {
            meta = getAgentMeta(containerId, agent);
            if (meta != null) {
                l.debug(format("Agent found in container %s.", containerId.getName()));
                break;
            }
        }

        if (meta == null)
            throw new JadeRuntimeException("Agent " + agent.getName()
                    + " not registered on any container.");

        return meta;
    }

    /**
     * Adds an agent with BornAgent event ownership and state, if agent is not
     * registered before. The container is also created if it does not exist.
     * 
     * @param event
     *            Born Agent event object
     * @param agent
     *            Agent Id Object for the new agent
     * @param containerId
     *            The id for the container the agent will be registered in.
     */
    public void addAgentMetaWhenBornAgentEvent(final BornAgent event, final AID agent,
            final ContainerID containerId) {

        createNewContainerWithIdIfNotExists(containerId);

        Map<AID, AgentMeta> agents = findContainerByContainerId(containerId);

        boolean agentIsNotRegistered = !agentIsAlreadyRegistered(agent, containerId, agents);

        if (agentIsNotRegistered) {
            AgentMeta meta = createAgentMetaWithStateAndOwnership(event);

            agents.put(agent, meta);
        }
    }

    private boolean agentIsAlreadyRegistered(final AID agent, final ContainerID containerId,
            final Map<AID, AgentMeta> agents) {

        boolean containerContainsAgent = agents.containsKey(agent);
        if (containerContainsAgent) {

            l.debug(format("Agent %s already registered on %s", agent.getName(), containerId
                    .getName()));
            return true;
        } else {
            return false;
        }

    }

    private AgentMeta createAgentMetaWithStateAndOwnership(final BornAgent event) {

        AgentMeta meta = new AgentMeta();
        AgentState state = new AgentState();
        state.setName(event.getState());
        meta.setState(state);
        meta.setOwnership(event.getOwnership());
        return meta;
    }

    /**
     * Verifies that a container exist or that the agent has been registered on
     * the given container.
     * 
     * @param containerId
     *            Container ID.
     * @param agent
     *            Agent ID.
     * 
     * @throws JadeRuntimeException
     *             Thrown if the container is not found or agent can not be
     *             found on the given container.
     */
    public void validateIfContainerAndAgentExists(final ContainerID containerId, final AID agent)
            throws JadeRuntimeException {

        Map<AID, AgentMeta> agentsMap = findContainerByContainerId(containerId);

        if (agentsMap == null)
            throw new JadeRuntimeException("Can not find the container",
                    new NoSuchContainerException(containerId.getName()));
        if (!agentsMap.containsKey(agent))
            throw new JadeRuntimeException(agent.getName() + " is not registered on "
                    + containerId.getName());
    }

    /**
     * Verifies that a container exist.
     * 
     * @param containerId
     *            Container ID.
     * 
     * @throws JadeRuntimeException
     *             Thrown if the container is not found.
     */
    public void validateContainerExists(final ContainerID containerId) throws JadeRuntimeException {

        Map<AID, AgentMeta> agentsMap = findContainerByContainerId(containerId);

        if (agentsMap == null)
            throw new JadeRuntimeException("Can not find the container",
                    new NoSuchContainerException(containerId.getName()));
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final APDescription description) {

        this.description = description;
    }

    /**
     * @return the description
     */
    public APDescription getDescription() {

        return description;
    }

    public Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> getContainers() {

        return containers;
    }

}
