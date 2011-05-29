/**
 * 
 */
package net.sf.jade4spring.internal;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.introspection.BornAgent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jade4spring.AgentMeta;
import net.sf.jade4spring.JadeRuntimeException;
import net.sf.jade4spring.NoSuchContainerException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author felipe
 * 
 */
public class JadeBeanMetaContainerTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#removeAllContainers()}
     * .
     */
    @Test
    public void removeAllContainers_withOneContainerID_expectToBeEmpty() {

        JadeBeanMetaContainer jadeBeanMetaContainer = createJadeBeanMetaContainerAndAddOneContainer();

        jadeBeanMetaContainer.removeAllContainers();

        assertTrue(jadeBeanMetaContainer.getContainers().equals(Collections.EMPTY_MAP));
    }

    /**
     * @return
     */
    private JadeBeanMetaContainer createJadeBeanMetaContainerAndAddOneContainer() {

        ContainerID containerId = new ContainerID();
        containerId.setName("AgentContainer1");
        return createJadeBeanMetaContainerAndAddOneContainer(containerId);
    }

    private JadeBeanMetaContainer createJadeBeanMetaContainerAndAddOneContainer(
            final ContainerID containerId) {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);
        return jadeBeanMetaContainer;
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#removeContainer(jade.core.ContainerID)}
     * .
     */
    @Test
    public void removeContainer_withOneContainer_expectItToNotBeFound() {

        ContainerID containerId = new ContainerID();
        containerId.setName("ContainerToBeRemoved");
        JadeBeanMetaContainer jadeBeanMetaContainer = createJadeBeanMetaContainerAndAddOneContainer(containerId);

        jadeBeanMetaContainer.removeContainer(containerId);

        Map<AID, AgentMeta> containerThatShouldBeNull = jadeBeanMetaContainer
                .findContainerByContainerId(containerId);
        assertNull(containerThatShouldBeNull);

    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#removeContainer(jade.core.ContainerID)}
     * .
     */
    @Test(expected = JadeRuntimeException.class)
    public void removeContainer_withoutContainer_expectJadeRuntimeExceptionToBeThrown() {

        ContainerID containerId = new ContainerID();
        containerId.setName("ContainerToBeRemoved");
        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        jadeBeanMetaContainer.removeContainer(containerId);
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#removeContainer(jade.core.ContainerID)}
     * .
     */
    @Test
    public void removeContainer_containersContainMoreThanOneContainer_correctContainerIsRemoved() {

        ContainerID containerIdToBeRemoved = new ContainerID();
        containerIdToBeRemoved.setName("ContainerToBeRemoved");
        JadeBeanMetaContainer jadeBeanMetaContainer = createJadeBeanMetaContainerAndAddOneContainer(containerIdToBeRemoved);

        ContainerID containerIdToBeKept = new ContainerID();
        containerIdToBeKept.setName("ContainerToBeKept");
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerIdToBeKept);

        jadeBeanMetaContainer.removeContainer(containerIdToBeRemoved);

        Map<AID, AgentMeta> containerThatShouldBeNull = jadeBeanMetaContainer
                .findContainerByContainerId(containerIdToBeRemoved);
        assertNull(containerThatShouldBeNull);
        assertNotNull(jadeBeanMetaContainer.findContainerByContainerId(containerIdToBeKept));
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#findContainerByContainerId(jade.core.ContainerID)}
     * .
     */
    @Test
    public void findContainersByContainerId_withOneAddedContainer_exptectItToBefound() {

        ContainerID containerIdTobeFound = new ContainerID();
        containerIdTobeFound.setName("ContainerToBeFound");
        JadeBeanMetaContainer jadeBeanMetaContainer = createJadeBeanMetaContainerAndAddOneContainer(containerIdTobeFound);

        Map<AID, AgentMeta> expectedContainer = jadeBeanMetaContainer.getContainers().get(
                containerIdTobeFound);
        Map<AID, AgentMeta> actualContainer = jadeBeanMetaContainer
                .findContainerByContainerId(containerIdTobeFound);

        assertEquals(expectedContainer, actualContainer);
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#createNewContainerWithIdIfNotExists(jade.core.ContainerID)}
     * .
     */
    @Test
    public void createNewContainerWithIdIfNotExists_withAContainerThatIsNotAdded_expectNewContainerIsCreated() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        ContainerID containerId = new ContainerID();
        containerId.setName("ContainerIdToBeAdded");
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);

        assertNotNull(jadeBeanMetaContainer.findContainerByContainerId(containerId));
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#createNewContainerWithIdIfNotExists(jade.core.ContainerID)}
     * .
     */
    @Test
    public void createNewContainerWithIdIfNotExists_withAOneContainerThatIsNotAdded_expectOnlyOneNewContainerIsCreated() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        ContainerID containerId = new ContainerID();
        containerId.setName("ContainerIdToBeAdded");
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);

        int expectedSize = 1;
        int actualSize = jadeBeanMetaContainer.getContainers().size();
        assertEquals(expectedSize, actualSize);
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#createNewContainerWithIdIfNotExists(jade.core.ContainerID)}
     * .
     */
    @Test
    public void createNewContainerWithIdIfNotExists_containerExistsAndAttemptsToAddTheSameContainerAgain_expectOnlyOneContainerAdded() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        ContainerID containerId = new ContainerID();
        containerId.setName("ExistingContainer");

        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);

        int expectedAmountOfContainers = 1;
        int actualAmountOfContainers = jadeBeanMetaContainer.getContainers().size();
        assertEquals(expectedAmountOfContainers, actualAmountOfContainers);

    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#findContainerByLocationName(java.lang.String)}
     * .
     * 
     * @throws NoSuchContainerException
     */
    @Test
    public void findContainerByLocationName_withOneContainerAdded_expectThatTheContainerIsFoundByLocation()
            throws NoSuchContainerException {

        ContainerID expectedContainerId = new ContainerID();
        String locationName = "locationName";
        expectedContainerId.setName(locationName);
        JadeBeanMetaContainer jadeBeanMetaContainer = createJadeBeanMetaContainerAndAddOneContainer(expectedContainerId);

        ContainerID foundContainerId = jadeBeanMetaContainer
                .findContainerByLocationName(locationName);

        assertEquals(expectedContainerId, foundContainerId);
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#findContainerByLocationName(java.lang.String)}
     * .
     * 
     * @throws NoSuchContainerException
     * 
     * @throws NoSuchContainerException
     */
    @Test(expected = NoSuchContainerException.class)
    public void findContainerByLocationName_withNonExistingContainer_expectNoSuchContainerExceptionToBeThrown()
            throws NoSuchContainerException {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        jadeBeanMetaContainer.findContainerByLocationName("randomLocation");
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#getListOfContainerID()}
     * .
     */
    @Test
    public void getListOfContainerID_withOnlyOneContainerAdded_expectListWithSizeOne() {

        JadeBeanMetaContainer container = createJadeBeanMetaContainerAndAddOneContainer();
        List<ContainerID> listOfContainerID = container.getListOfContainerID();
        int expectedSize = 1;
        int actualListSize = listOfContainerID.size();

        assertEquals(expectedSize, actualListSize);
    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#getAgentMetaFromLocationName(java.lang.String)}
     * .
     * 
     * @throws NoSuchContainerException
     */
    @Test
    public void getAgentMetaFromLocationName_withContainerIDAdded_expectToFindItAndNotReturnNull()
            throws NoSuchContainerException {

        ContainerID containerID = new ContainerID();
        String locationName = "AgentLocation";
        containerID.setName(locationName);

        JadeBeanMetaContainer jadeBeanMetaContainer = createJadeBeanMetaContainerAndAddOneContainer(containerID);
        Map<AID, AgentMeta> foundAgentMeta = jadeBeanMetaContainer
                .getAgentMetaFromLocationName(locationName);

        assertNotNull(foundAgentMeta);

    }

    /**
     * Test method for
     * {@link net.sf.jade4spring.internal.JadeBeanMetaContainer#getAgentMetaFromLocationName(java.lang.String)}
     * 
     * @throws NoSuchContainerException
     */
    @Test(expected = NoSuchContainerException.class)
    public void getAgentMetaFromLoacationName_withoutContainerIdAdded_expectedNoSuchContainerExceptionIsThrown()
            throws NoSuchContainerException {

        JadeBeanMetaContainer beanMetaContainer = new JadeBeanMetaContainer();
        beanMetaContainer.getAgentMetaFromLocationName("");
    }

    /**
     * 
     * @param containerId
     * @param agent
     * @return
     * @throws JadeRuntimeException
     */
    @Test(expected = JadeRuntimeException.class)
    public void getAgentMeta_withNotAddedAID_expectJadeRunTimeException() {

        AID agent = new AID("testAgent", AID.ISGUID);
        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        jadeBeanMetaContainer.getAgentMeta(agent);

    }

    /**
     * 
     * @param containerId
     * @param agent
     * @return
     * @throws JadeRuntimeException
     */
    @Test
    public void getAgentMeta_withAddedAID_expectToFindAgentMeta() {

        AID agent = new AID("testAgent", AID.ISGUID);
        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        ContainerID containerId = newContainerIdWithName("container-name");
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);
        Map<AID, AgentMeta> agentMetaContainer = jadeBeanMetaContainer
                .findContainerByContainerId(containerId);
        agentMetaContainer.put(agent, new AgentMeta());

        AgentMeta actualAgentMeta = jadeBeanMetaContainer.getAgentMeta(agent);

        assertEquals(agentMetaContainer.get(agent), actualAgentMeta);
    }

    /**
     * 
     * @param containerId
     * @param agent
     * @return
     * @throws JadeRuntimeException
     */
    @Test
    public void getAgentMeta_withAddedAIDInSecondContainer_expectSameAgentIsReturned() {

        AID agent = new AID("testAgent", AID.ISGUID);
        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(newContainerIdWithName("a"));
        ContainerID containerId = newContainerIdWithName("container-name");
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);
        Map<AID, AgentMeta> agentMetaContainer = jadeBeanMetaContainer
                .findContainerByContainerId(containerId);
        agentMetaContainer.put(agent, new AgentMeta());

        AgentMeta actualAgentMeta = jadeBeanMetaContainer.getAgentMeta(agent);

        assertSame(agentMetaContainer.get(agent), actualAgentMeta);
    }

    @Test
    public void validateIfContainerAndAgentExists_withNonexistingContainerAndAgent_expectJadeRuntimeExceptionWithNoSuchContainerCause() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerName");
        AID agent = null;

        boolean isCauseNosuchContainerExceptionAndJadeRuntimeExceptionIsThrown = false;
        try {
            jadeBeanMetaContainer.validateIfContainerAndAgentExists(containerId, agent);
        } catch (JadeRuntimeException e) {
            isCauseNosuchContainerExceptionAndJadeRuntimeExceptionIsThrown = e.getCause() instanceof NoSuchContainerException;
        }

        assertTrue(isCauseNosuchContainerExceptionAndJadeRuntimeExceptionIsThrown);
    }

    @Test(expected = JadeRuntimeException.class)
    public void validateIfContainerAndAgentExists_withExistingContainerAndNonexistingAgent_expectJadeRuntimeException() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = jadeBeanMetaContainer
                .getContainers();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerName");
        containers.put(containerId, new ConcurrentHashMap<AID, AgentMeta>());
        AID agent = new AID();
        jadeBeanMetaContainer.validateIfContainerAndAgentExists(containerId, agent);
    }

    @Test
    public void validateContainerExists_withExistingContainer_expectNoExceptionIsThrown() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = jadeBeanMetaContainer
                .getContainers();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerName");
        containers.put(containerId, new ConcurrentHashMap<AID, AgentMeta>());

        try {
            jadeBeanMetaContainer.validateContainerExists(containerId);
        } catch (JadeRuntimeException e) {
            fail("Caught unexpected exception!");
        }
    }

    @Test(expected = JadeRuntimeException.class)
    public void validateContainerExists_withNoExistingContainer_expectJadeRuntimeException() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerName");

        jadeBeanMetaContainer.validateContainerExists(containerId);
    }

    @Test
    public void addAgentMetaWhenBornAgentEvent_withContainerThatNotExistsAndAgentThatExists_expectEqualContainerSize() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();

        ContainerID containerId = new ContainerID();
        containerId.setName("containerName");

        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);

        Map<AID, AgentMeta> agents = jadeBeanMetaContainer.findContainerByContainerId(containerId);
        AID agent = new AID(AID.AGENT_CLASSNAME, AID.ISGUID);
        agents.put(agent, new AgentMeta());

        int expectedSize = agents.size();
        jadeBeanMetaContainer.addAgentMetaWhenBornAgentEvent(new BornAgent(), agent, containerId);
        int actualSize = agents.size();
        assertEquals(expectedSize, actualSize);
    }

    @Test
    public void addAgentMetaWhenBornAgentEvent_withContainerAndAgentThatNotExists_expectAgentToBeAdded() {

        JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();

        ContainerID containerId = new ContainerID();
        containerId.setName("containerName");

        AID agent = new AID(AID.AGENT_CLASSNAME, AID.ISGUID);

        jadeBeanMetaContainer.addAgentMetaWhenBornAgentEvent(new BornAgent(), agent, containerId);

        Map<AID, AgentMeta> agents = jadeBeanMetaContainer.findContainerByContainerId(containerId);

        boolean containsAgent = agents.containsKey(agent);

        assertTrue(containsAgent);

    }

    private ContainerID newContainerIdWithName(final String name) {

        ContainerID containerId = new ContainerID();
        containerId.setName(name);
        return containerId;
    }
}
