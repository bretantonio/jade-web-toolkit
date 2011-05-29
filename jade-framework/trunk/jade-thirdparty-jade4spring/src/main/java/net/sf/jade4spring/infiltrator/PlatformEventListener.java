/*
 * Created on Jan 10, 2006
 */
package net.sf.jade4spring.infiltrator;

import jade.domain.introspection.AddedContainer;
import jade.domain.introspection.PlatformDescription;
import jade.domain.introspection.RemovedContainer;
import jade.domain.introspection.ResetEvents;


/**
 * Defines event handlers for platform events.
 * @author Jaran Nilsen
 * @since 0.2
 * @version ${Revision}
 */
public interface PlatformEventListener {

    /**
     * Event handler for events triggered when a new container is added to the
     * platform.
     * @param event Event details.
     */
    public void containerAdded(AddedContainer event);


    /**
     * Event handler for events triggered when a container is removed from the
     * platform.
     * @param event Event details.
     */
    public void containerRemoved(RemovedContainer event);


    /**
     * Event handler for events triggered when new platform description has been
     * received.
     * @param event Event details.
     */
    public void receivedPlatformDescription(PlatformDescription event);


    /**
     * Event handler for events triggered when a reset meta event has occured.
     * @param event Event details.
     */
    public void resetMeta(ResetEvents event);

}
