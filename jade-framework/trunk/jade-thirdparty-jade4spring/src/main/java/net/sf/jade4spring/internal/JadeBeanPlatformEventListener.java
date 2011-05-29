package net.sf.jade4spring.internal;

import jade.core.ContainerID;
import jade.domain.introspection.AddedContainer;
import jade.domain.introspection.PlatformDescription;
import jade.domain.introspection.RemovedContainer;
import jade.domain.introspection.ResetEvents;
import net.sf.jade4spring.infiltrator.PlatformEventListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Andreas Lundberg
 * 
 */
public class JadeBeanPlatformEventListener implements PlatformEventListener {

    private JadeBeanMetaContainer jadeBeanMetaContainer;

    private Log l = LogFactory.getLog(getClass());

    public JadeBeanPlatformEventListener(JadeBeanMetaContainer jadeBeanMetaContainer) {

        this.setJadeBeanMetaContainer(jadeBeanMetaContainer);
    }

    /**
     * @see net.sf.jade4spring.infiltrator.PlatformEventListener#containerAdded(jade.domain.introspection.AddedContainer)
     */
    public void containerAdded(final AddedContainer event) {

        ContainerID containerFromEvent = event.getContainer();
        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerFromEvent);
    }

    /**
     * @see net.sf.jade4spring.infiltrator.PlatformEventListener#containerRemoved(jade.domain.introspection.RemovedContainer)
     */
    public void containerRemoved(final RemovedContainer event) {

        ContainerID containerId = event.getContainer();
        jadeBeanMetaContainer.removeContainer(containerId);
    }

    /**
     * @see net.sf.jade4spring.infiltrator.PlatformEventListener#receivedPlatformDescription(jade.domain.introspection.PlatformDescription)
     */
    public void receivedPlatformDescription(final PlatformDescription event) {

        jadeBeanMetaContainer.setDescription(event.getPlatform());

        l.debug("Platform description recieved.");
    }

    /**
     * @see net.sf.jade4spring.infiltrator.PlatformEventListener#resetMeta(jade.domain.introspection.ResetEvents)
     */
    public void resetMeta(final ResetEvents event) {

        jadeBeanMetaContainer.removeAllContainers();

        l.debug("Platform meta data reset.");
    }

    public void setJadeBeanMetaContainer(JadeBeanMetaContainer jadeBeanMetaContainer) {

        this.jadeBeanMetaContainer = jadeBeanMetaContainer;
    }

    public JadeBeanMetaContainer getJadeBeanMetaContainer() {

        return jadeBeanMetaContainer;
    }

}
