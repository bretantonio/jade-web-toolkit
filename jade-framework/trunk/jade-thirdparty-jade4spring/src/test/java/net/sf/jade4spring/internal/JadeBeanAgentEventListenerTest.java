package net.sf.jade4spring.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jade.core.AID;
import jade.core.AgentState;
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
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jade4spring.AgentMeta;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author Andreas Lundberg
 * 
 */
public class JadeBeanAgentEventListenerTest {

    JadeBeanAgentEventListener agentEventListener;

    JadeBeanMetaContainer jadeBeanMetaContainer;

    @Before
    public void setUp() throws Exception {

        jadeBeanMetaContainer = new JadeBeanMetaContainer();
        agentEventListener = new JadeBeanAgentEventListener(jadeBeanMetaContainer);
    }

    @After
    public void tearDown() throws Exception {

    }

    /********************
     *** agentResumed ***
     ********************/

    @Test
    public void agentResumed_withDefaultSetup_expectAgentToBeSetToSuspendState() {

        String expectedStateName = "active";

        AID agent = new AID(AID.AGENT_CLASSNAME, AID.ISGUID);
        createContainerForAgent(agent);
        ResumedAgent event = new ResumedAgent();
        event.setAgent(agent);

        agentEventListener.agentResumed(event);

        AgentMeta agentMeta = jadeBeanMetaContainer.getAgentMeta(agent);

        assertEquals(expectedStateName, agentMeta.getState().getName());
    }

    @Test
    public void agentResumed_withDefaultSetup_expectGetAgentMetaAndSetStateIsCalledOnceFromJadeBeanContainer() {

        mockJadeBeanMetaContainerAndAddToListener();

        String expectedName = "active";

        AID agent = new AID(AID.AGENT_CLASSNAME, AID.ISGUID);
        createContainerForAgent(agent);
        ResumedAgent event = new ResumedAgent();
        event.setAgent(agent);

        agentEventListener.agentResumed(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).setStateToAgentMeta(agent, expectedName);
    }

    /***********************
     *** agendtSuspended ***
     ***********************/

    @Test
    public void agentSuspended_withDefaultSetup_expectAgentToBeSetToSuspendState() {

        String expectedStateName = "suspended";
        AID agent = new AID(AID.AGENT_CLASSNAME, AID.ISGUID);

        createContainerForAgent(agent);

        SuspendedAgent event = new SuspendedAgent();
        event.setAgent(agent);

        agentEventListener.agentSuspended(event);

        AgentMeta agentMeta = jadeBeanMetaContainer.getAgentMeta(agent);

        assertEquals(expectedStateName, agentMeta.getState().getName());
    }

    @Test
    public void agentSuspended_withDefaultSetup_expectGetAgentMetaAndSetStateIsCalledOnceFromJadeBeanContainer() {

        mockJadeBeanMetaContainerAndAddToListener();

        String expectedName = "suspended";

        AID agent = new AID(AID.AGENT_CLASSNAME, AID.ISGUID);
        createContainerForAgent(agent);
        SuspendedAgent event = new SuspendedAgent();
        event.setAgent(agent);

        agentEventListener.agentSuspended(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).setStateToAgentMeta(agent, expectedName);
    }

    /*******************
     *** agentThawed ***
     *******************/

    @Ignore
    @Test
    public void agentThawed_() {

        mockJadeBeanMetaContainerAndAddToListener();

        ThawedAgent event = null;
        agentEventListener.agentThawed(event);
    }

    /*****************
     *** agentBorn ***
     *****************/

    @Test
    public void agentBorn_withMeta_expectMetaWithEqualStateToBeAddedToAgent() {

        AID agentId = new AID();
        BornAgent event = new BornAgent();
        ContainerID containerId = new ContainerID();

        AgentMeta expectedMeta = createExpectedAgentMeta(agentId, event, containerId);

        agentEventListener.agentBorn(event);

        Map<AID, AgentMeta> actualContainer = jadeBeanMetaContainer.getContainers()
                .get(containerId);

        AgentMeta actualMeta = actualContainer.get(agentId);

        assertEquals(expectedMeta.getState().getName(), actualMeta.getState().getName());

    }

    @Test
    public void agentBorn_withMeta_expectMetaWithEqualOwnershipToBeAddedToAgent() {

        AID agentId = new AID();
        ContainerID containerId = new ContainerID();
        BornAgent event = new BornAgent();

        AgentMeta expectedMeta = createExpectedAgentMeta(agentId, event, containerId);

        agentEventListener.agentBorn(event);

        Map<AID, AgentMeta> actualContainer = jadeBeanMetaContainer.getContainers()
                .get(containerId);

        AgentMeta actualMeta = actualContainer.get(agentId);

        assertEquals(expectedMeta.getOwnership(), actualMeta.getOwnership());

    }

    @Test
    public void agentBorn_withAgentIdAndContainerIdAttachedBornAgainEvent_expectAddAgentMetaWhenBornAgentEventToBeCalledOnce() {

        mockJadeBeanMetaContainerAndAddToListener();

        AID agentId = new AID();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerId");
        BornAgent event = new BornAgent();

        event.setAgent(agentId);
        event.setWhere(containerId);
        agentEventListener.agentBorn(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).addAgentMetaWhenBornAgentEvent(event, agentId,
                containerId);

    }

    /*****************************
     *** changedAgentOwnership ***
     *****************************/

    @Test
    public void changedAgentOwnership_withSimpleOwnershipEvent_expectOwnershipToBeSetOnAgentMeta() {

        String expectedOwnership = "expectedOwnerShip";

        ContainerID fromContainerId = new ContainerID();
        ContainerID toContainerId = new ContainerID();

        String fromName = "fromContainer";
        fromContainerId.setName(fromName);
        toContainerId.setName(expectedOwnership);

        AID agentId = new AID();
        AgentMeta agentMeta = new AgentMeta();
        ConcurrentHashMap<AID, AgentMeta> agents = new ConcurrentHashMap<AID, AgentMeta>();
        agents.put(agentId, agentMeta);

        jadeBeanMetaContainer.getContainers().put(fromContainerId, agents);
        jadeBeanMetaContainer.getContainers().put(toContainerId,
                new ConcurrentHashMap<AID, AgentMeta>());

        ChangedAgentOwnership event = new ChangedAgentOwnership();
        event.setAgent(agentId);
        event.setTo(expectedOwnership);
        event.setWhere(fromContainerId);

        agentEventListener.changedAgentOwnership(event);

        String acturalOwnership = agentMeta.getOwnership();
        assertSame(expectedOwnership, acturalOwnership);

    }

    @Test
    public void changedAgentOwnership_withAgentIdAndContainerIdAttachedChangedAgentOwnershipEvent_expectGetAgentMetaToBeCalledOnce() {

        mockJadeBeanMetaContainerAndAddToListener();

        String expectedOwnership = "expectedOwnerShip";

        AID agentId = new AID();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerId");

        ChangedAgentOwnership event = new ChangedAgentOwnership();
        event.setAgent(agentId);
        event.setTo(expectedOwnership);
        event.setWhere(containerId);

        AgentMeta agentMeta = new AgentMeta();
        when(jadeBeanMetaContainer.getAgentMeta(containerId, agentId)).thenReturn(agentMeta);
        agentEventListener.changedAgentOwnership(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).getAgentMeta(containerId, agentId);

    }

    /*****************
     *** agentDead ***
     *****************/

    @Test
    public void agentDead_whereAgentAndContainerAreSetToEvent_expectValidateIfContainerAndAgentExistsToBeCalledOnce() {

        mockJadeBeanMetaContainerAndAddToListener();

        AID agentId = new AID();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerId");

        DeadAgent event = new DeadAgent();
        event.setWhere(containerId);
        event.setAgent(agentId);

        agentEventListener.agentDead(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).validateIfContainerAndAgentExists(containerId,
                agentId);

    }

    @Test
    public void agentDead_whereAgentAndContainerAreSetToEvent_expectFindContainerByContainerIdToBeCalledOnce() {

        mockJadeBeanMetaContainerAndAddToListener();

        AID agentId = new AID();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerId");

        DeadAgent event = new DeadAgent();
        event.setWhere(containerId);
        event.setAgent(agentId);

        agentEventListener.agentDead(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).findContainerByContainerId(containerId);

    }

    @Test
    public void agentDead_whereAgentAndContainerAreSetToEvent_expectAgentToBeRemoved() {

        AID agentId = new AID();
        ContainerID containerId = new ContainerID();
        containerId.setName("containerId");

        ConcurrentHashMap<AID, AgentMeta> agents = new ConcurrentHashMap<AID, AgentMeta>();
        agents.put(agentId, new AgentMeta());

        jadeBeanMetaContainer.getContainers().put(containerId, agents);

        DeadAgent event = new DeadAgent();
        event.setWhere(containerId);
        event.setAgent(agentId);

        int expectedSize = getContainerSizeByContainerId(containerId) - 1;
        agentEventListener.agentDead(event);
        int actualSize = getContainerSizeByContainerId(containerId);

        assertEquals(expectedSize, actualSize);

    }

    /*******************
     *** agentFrozen ***
     *******************/
    @Ignore
    @Test
    public void agentFrozen_() {

        mockJadeBeanMetaContainerAndAddToListener();

        FrozenAgent event = null;
        agentEventListener.agentFrozen(event);
    }

    /******************
     *** agentMoved ***
     ******************/

    @Test
    public void agentMoved_withFromAndToContainerIdsAndAgentAddedToEvent_expectValidateIfContainerAndAgentExistsIsCalledOnce() {

        mockJadeBeanMetaContainerAndAddToListener();

        ContainerID fromContainerId = new ContainerID();
        fromContainerId.setName("fromContainerId");
        ContainerID toContainerId = new ContainerID();
        toContainerId.setName("toContainerId");
        AID agent = new AID();

        MovedAgent event = new MovedAgent();
        event.setAgent(agent);
        event.setFrom(fromContainerId);
        event.setTo(toContainerId);

        agentEventListener.agentMoved(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).validateIfContainerAndAgentExists(
                fromContainerId, agent);
    }

    @Test
    public void agentMoved_withFromAndToContainerIdsAndAgentAddedToEvent_expectCreateNewContainerWithIdIfNotExistsIsCalledOnce() {

        mockJadeBeanMetaContainerAndAddToListener();

        ContainerID fromContainerId = new ContainerID();
        fromContainerId.setName("fromContainerId");
        ContainerID toContainerId = new ContainerID();
        toContainerId.setName("toContainerId");
        AID agent = new AID();

        MovedAgent event = new MovedAgent();
        event.setAgent(agent);
        event.setFrom(fromContainerId);
        event.setTo(toContainerId);

        agentEventListener.agentMoved(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).createNewContainerWithIdIfNotExists(
                toContainerId);
    }

    @Test
    public void agentMoved_withFromAndToContainerIdsAndAgentAddedToEvent_expectFindContainerByContainerIdIsCalledOnceForBothFromContainerIdAndToContainerId() {

        mockJadeBeanMetaContainerAndAddToListener();

        ContainerID fromContainerId = new ContainerID();
        fromContainerId.setName("fromContainerId");
        ContainerID toContainerId = new ContainerID();
        toContainerId.setName("toContainerId");
        AID agent = new AID();

        MovedAgent event = new MovedAgent();
        event.setAgent(agent);
        event.setFrom(fromContainerId);
        event.setTo(toContainerId);

        agentEventListener.agentMoved(event);

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).findContainerByContainerId(fromContainerId);
        verify(jadeBeanMetaContainer, times(times)).findContainerByContainerId(toContainerId);
    }

    @Test
    public void agentMoved_withFromAndToContainerIdsAndAgentAddedToEvent_expect() {

        ContainerID fromContainerId = new ContainerID();
        fromContainerId.setName("fromContainerId");
        ContainerID toContainerId = new ContainerID();
        toContainerId.setName("toContainerId");
        AID agent = new AID();

        MovedAgent event = new MovedAgent();
        event.setAgent(agent);
        event.setFrom(fromContainerId);
        event.setTo(toContainerId);

        ConcurrentHashMap<AID, AgentMeta> fromAgents = new ConcurrentHashMap<AID, AgentMeta>();
        fromAgents.put(agent, new AgentMeta());

        jadeBeanMetaContainer.getContainers().put(fromContainerId, fromAgents);
        jadeBeanMetaContainer.getContainers().put(toContainerId,
                new ConcurrentHashMap<AID, AgentMeta>());

        int beforeFromContainerSize = getContainerSizeByContainerId(fromContainerId);
        int beforeToContainerSize = getContainerSizeByContainerId(toContainerId);
        agentEventListener.agentMoved(event);
        int afterFromContainerSize = getContainerSizeByContainerId(fromContainerId);
        int afterToContainerSize = getContainerSizeByContainerId(toContainerId);

        assertEquals(beforeFromContainerSize, afterToContainerSize);
        assertEquals(beforeToContainerSize, afterFromContainerSize);

        assertTrue(beforeFromContainerSize != beforeToContainerSize);
        assertTrue(afterFromContainerSize != afterToContainerSize);

    }

    /***********************
     *** Private methods ***
     ***********************/

    private void mockJadeBeanMetaContainerAndAddToListener() {

        jadeBeanMetaContainer = mock(JadeBeanMetaContainer.class);
        agentEventListener.setJadeBeanMetaContainer(jadeBeanMetaContainer);
    }

    private void createContainerForAgent(AID agent) {

        ContainerID containerId = new ContainerID();
        containerId.setName("testContainer");

        ConcurrentHashMap<AID, AgentMeta> agentContainer = new ConcurrentHashMap<AID, AgentMeta>();
        agentContainer.put(agent, new AgentMeta());

        jadeBeanMetaContainer.getContainers().put(containerId, agentContainer);
    }

    private AgentMeta createExpectedAgentMeta(AID agentId, BornAgent event, ContainerID containerId) {

        containerId.setName("containerId");
        event.setState("Born-Agent");

        event.setAgent(agentId);
        event.setWhere(containerId);

        AgentMeta expectedMeta = new AgentMeta();
        AgentState state = new AgentState();
        state.setName(event.getState());
        expectedMeta.setState(state);
        expectedMeta.setOwnership(event.getOwnership());
        return expectedMeta;
    }

    private int getContainerSizeByContainerId(ContainerID fromContainerId) {

        return jadeBeanMetaContainer.findContainerByContainerId(fromContainerId).size();
    }

}
