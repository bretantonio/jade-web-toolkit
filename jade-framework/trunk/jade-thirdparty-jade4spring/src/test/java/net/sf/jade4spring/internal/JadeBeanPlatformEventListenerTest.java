package net.sf.jade4spring.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.FIPAAgentManagement.APDescription;
import jade.domain.introspection.AddedContainer;
import jade.domain.introspection.PlatformDescription;
import jade.domain.introspection.RemovedContainer;
import jade.domain.introspection.ResetEvents;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jade4spring.AgentMeta;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Andreas Lundberg
 * 
 */
public class JadeBeanPlatformEventListenerTest {

    JadeBeanPlatformEventListener jadeBeanPlatformEventListener;

    JadeBeanMetaContainer jadeBeanMetaContainer;

    @Before
    public void setUp() throws Exception {

        jadeBeanMetaContainer = new JadeBeanMetaContainer();
        jadeBeanPlatformEventListener = new JadeBeanPlatformEventListener(jadeBeanMetaContainer);
    }

    @After
    public void tearDown() throws Exception {

    }

    /**********************
     *** containerAdded ***
     **********************/

    @Test
    public void containerAdded_withContainerId_expectOneNewContainerIsAdded() {

        ContainerID id = new ContainerID();
        id.setName("container1");

        AddedContainer event = new AddedContainer();
        event.setContainer(id);

        Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = jadeBeanMetaContainer
                .getContainers();

        int expectedSizeAfterAdd = containers.size() + 1;
        jadeBeanPlatformEventListener.containerAdded(event);
        int actualSizeAfterAdd = containers.size();

        assertEquals(expectedSizeAfterAdd, actualSizeAfterAdd);

    }

    @Test
    public void containerAdded_withContainerId_expectEqualContainerToBeAdded() {

        ContainerID id = new ContainerID();
        id.setName("container1");

        AddedContainer event = new AddedContainer();
        event.setContainer(id);

        jadeBeanPlatformEventListener.containerAdded(event);

        Map<AID, AgentMeta> expectedContainer = jadeBeanMetaContainer.getContainers().get(id);
        Map<AID, AgentMeta> actualContainer = jadeBeanMetaContainer.findContainerByContainerId(id);

        assertEquals(expectedContainer, actualContainer);

    }

    @Test
    public void containerAdded_withMinimalSetup_expectCreateNewContainerWithIdIfNotExistsIsCalledOnceFromJadeBeanMetaContainer() {

        mockJadeBeanMetaContainerAndAddToListener();

        jadeBeanPlatformEventListener.containerAdded(new AddedContainer());

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).createNewContainerWithIdIfNotExists(
                any(ContainerID.class));
    }

    /************************
     *** containerRemoved ***
     ************************/

    @Test
    public void containerRemoved_withExistingContainer_expectOneContainerToBeRemoved() {

        ContainerID id = new ContainerID();
        id.setName("containerId");

        Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = jadeBeanMetaContainer
                .getContainers();
        containers.put(id, new ConcurrentHashMap<AID, AgentMeta>());

        RemovedContainer event = new RemovedContainer();
        event.setContainer(id);

        int expectedSizeAfterRemove = containers.size() - 1;
        jadeBeanPlatformEventListener.containerRemoved(event);
        int actualSizeAfterRemove = containers.size();

        assertEquals(expectedSizeAfterRemove, actualSizeAfterRemove);
    }

    @Test
    public void containerRemoved_withExistingContainer_expectExistingContainerToBeRemoved() {

        ContainerID id = new ContainerID();
        id.setName("containerId");

        jadeBeanMetaContainer.getContainers().put(id, new ConcurrentHashMap<AID, AgentMeta>());

        assertNotNull(jadeBeanMetaContainer.findContainerByContainerId(id));

        RemovedContainer event = new RemovedContainer();
        event.setContainer(id);

        jadeBeanPlatformEventListener.containerRemoved(event);

        assertNull(jadeBeanMetaContainer.findContainerByContainerId(id));
    }

    @Test
    public void containerRemoved_withMinimalSetup_expectRemoveContainerIsCalledOnceFromJadeBeanMetaContainer() {

        mockJadeBeanMetaContainerAndAddToListener();

        jadeBeanPlatformEventListener.containerRemoved(new RemovedContainer());

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).removeContainer(any(ContainerID.class));
    }

    /***********************************
     *** receivedPlatformDescription ***
     ***********************************/

    @Test
    public void receivedPlatformDescription_withDescription_expectJadeMetaContainetToContainSameDescription() {

        APDescription expectedDescription = new APDescription();
        expectedDescription.setName("description");

        PlatformDescription description = new PlatformDescription();
        description.setPlatform(expectedDescription);
        jadeBeanPlatformEventListener.receivedPlatformDescription(description);

        APDescription actualDescription = jadeBeanMetaContainer.getDescription();

        assertSame(expectedDescription, actualDescription);
    }

    @Test
    public void receivedPlatformDescription_withMinimalSetup_expectSetDescriptionIsCalledOnceFromJadeBeanContainer() {

        mockJadeBeanMetaContainerAndAddToListener();

        jadeBeanPlatformEventListener.receivedPlatformDescription(new PlatformDescription());

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).setDescription(any(APDescription.class));
    }

    /*****************
     *** resetMeta ***
     *****************/

    @Test
    public void resetMeta__withOneContainer_expectRemoveAllContainers() {

        int expectedNumberOfContainers = 0;

        int[] ids = { 1 };
        addContainersByIds(ids);

        Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = jadeBeanMetaContainer
                .getContainers();

        // To make sure addContainersById works
        assertEquals(ids.length, containers.size());

        jadeBeanPlatformEventListener.resetMeta(new ResetEvents());

        int actualNumberOfContainers = containers.size();

        assertEquals(expectedNumberOfContainers, actualNumberOfContainers);
    }

    @Test
    public void resetMeta__withThreeContainers_expectRemoveAllContainers() {

        int expectedNumberOfContainers = 0;

        int[] ids = { 1, 2, 3 };
        addContainersByIds(ids);

        Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = jadeBeanMetaContainer
                .getContainers();

        // To make sure addContainersById works
        assertEquals(ids.length, containers.size());

        jadeBeanPlatformEventListener.resetMeta(new ResetEvents());

        int actualNumberOfContainers = containers.size();

        assertEquals(expectedNumberOfContainers, actualNumberOfContainers);
    }

    @Test
    public void resetMeta__withFiveContainers_expectRemoveAllContainers() {

        int expectedNumberOfContainers = 0;

        int[] ids = { 1, 2, 3, 4, 5 };
        addContainersByIds(ids);

        Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = jadeBeanMetaContainer
                .getContainers();

        // To make sure addContainersById works
        assertEquals(ids.length, containers.size());

        jadeBeanPlatformEventListener.resetMeta(new ResetEvents());

        int actualNumberOfContainers = containers.size();

        assertEquals(expectedNumberOfContainers, actualNumberOfContainers);
    }

    @Test
    public void resetMeta__withMinimalSetup_expectRemoveAllContainersIsCalledOnceFromJadeBeanContainer() {

        mockJadeBeanMetaContainerAndAddToListener();

        jadeBeanPlatformEventListener.resetMeta(new ResetEvents());

        int times = 1;
        verify(jadeBeanMetaContainer, times(times)).removeAllContainers();
    }

    /***********************
     *** Private methods ***
     ***********************/

    private void mockJadeBeanMetaContainerAndAddToListener() {

        jadeBeanMetaContainer = mock(JadeBeanMetaContainer.class);
        jadeBeanPlatformEventListener.setJadeBeanMetaContainer(jadeBeanMetaContainer);
    }

    private void addContainersByIds(int... ids) {

        Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> containers = jadeBeanMetaContainer
                .getContainers();
        for (int id : ids) {
            ContainerID containerID = new ContainerID();
            containerID.setName("" + id);
            containers.put(containerID, new ConcurrentHashMap<AID, AgentMeta>());
        }
    }
}
