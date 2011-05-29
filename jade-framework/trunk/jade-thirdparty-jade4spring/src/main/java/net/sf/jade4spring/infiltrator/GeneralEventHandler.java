/*
 * Created on May 13, 2005
 */
package net.sf.jade4spring.infiltrator;

import jade.domain.introspection.Event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Base class for all event handlers.
 * @author Jaran Nilsen
 * @since 0.1
 * @version $Revision: 1.1 $
 */
public class GeneralEventHandler implements
    jade.domain.introspection.AMSSubscriber.EventHandler {

    protected Log l = LogFactory.getLog(getClass());

    protected InfiltratorAgent agent;


    /**
     * Create a new instance of GeneralEventHandler.
     * @param agent The agent where platform information is located.
     */
    public GeneralEventHandler(InfiltratorAgent agent) {

        this.agent = agent;
    }


    /**
     * Handle the incoming event. The event is passed on to listeners by the 
     * InfiltratorAgent.
     * @see jade.domain.introspection.AMSSubscriber$EventHandler#handle(jade.domain.introspection.Event)
     */
    public synchronized void handle(Event event) {

        agent.dispatchEvent(event);
    }

}
