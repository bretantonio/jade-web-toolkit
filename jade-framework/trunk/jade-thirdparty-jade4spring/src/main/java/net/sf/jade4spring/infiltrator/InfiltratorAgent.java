/*
 * Created on Dec 30, 2005
 */
package net.sf.jade4spring.infiltrator;

import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.SenderBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.event.ContainerEvent;
import jade.core.event.ContainerListener;
import jade.core.event.NotificationHelper;
import jade.core.event.NotificationService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.AddedContainer;
import jade.domain.introspection.BornAgent;
import jade.domain.introspection.ChangedAgentOwnership;
import jade.domain.introspection.ChangedAgentState;
import jade.domain.introspection.DeadAgent;
import jade.domain.introspection.Event;
import jade.domain.introspection.FrozenAgent;
import jade.domain.introspection.IntrospectionOntology;
import jade.domain.introspection.MovedAgent;
import jade.domain.introspection.PlatformDescription;
import jade.domain.introspection.RemovedContainer;
import jade.domain.introspection.ResetEvents;
import jade.domain.introspection.ResumedAgent;
import jade.domain.introspection.SuspendedAgent;
import jade.domain.introspection.ThawedAgent;
import jade.domain.mobility.MobilityOntology;
import jade.domain.persistence.PersistenceOntology;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Agent which is running inside the container running via the JadeBean,
 * providing the JadeBean with information and control of the container.
 *
 * @author Jaran Nilsen
 * @since 0.1
 * @version $Revision: 1.6 $
 */
public class InfiltratorAgent extends Agent {

    private Log l = LogFactory.getLog(getClass());

    private ACLMessage subscribe = new ACLMessage(ACLMessage.SUBSCRIBE);

    private ACLMessage cancellation = new ACLMessage(ACLMessage.CANCEL);

    private ACLMessage request = new ACLMessage(ACLMessage.REQUEST);

    private SequentialBehaviour subscriberbehaviour = new SequentialBehaviour();

    private List<PlatformEventListener> platformListeners = new ArrayList<PlatformEventListener>();

    private List<AgentEventListener> agentListeners = new ArrayList<AgentEventListener>();

    private ContainerListener containerListener = null;

    /**
     * @see jade.core.Agent#setup()
     */
    @Override
    protected void setup() {

        l.info("Infiltrator agent initializing...");
        super.setup();

        // Register the supported ontologies
        getContentManager().registerOntology(JADEManagementOntology.getInstance());
        getContentManager().registerOntology(IntrospectionOntology.getInstance());
        getContentManager().registerOntology(FIPAManagementOntology.getInstance());
        getContentManager().registerOntology(MobilityOntology.getInstance());
        getContentManager().registerOntology(PersistenceOntology.getInstance());

        // register the supported languages
        SLCodec codec = new SLCodec();
        getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL1);
        getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL2);
        getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL);

        // Set up subscription message.
        subscribe.setSender(getAID());
        subscribe.clearAllReceiver();
        subscribe.addReceiver(getAMS());
        subscribe.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
        subscribe.setOntology(IntrospectionOntology.NAME);
        subscribe.setReplyWith(AMSSubscriber.AMS_SUBSCRIPTION);
        subscribe.setConversationId(getLocalName());
        String content = AMSSubscriber.PLATFORM_EVENTS;
        subscribe.setContent(content);

        // Set up cancel message.
        cancellation.setSender(getAID());
        cancellation.clearAllReceiver();
        cancellation.addReceiver(getAMS());
        cancellation.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
        cancellation.setOntology(IntrospectionOntology.NAME);
        cancellation.setReplyWith(AMSSubscriber.AMS_CANCELLATION);
        cancellation.setConversationId(getLocalName());

        // Set up request message.
        request.setSender(getAID());
        request.clearAllReceiver();
        request.addReceiver(getAMS());
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);

        subscriberbehaviour.addSubBehaviour(new SenderBehaviour(this, subscribe));

        AMSListenerBehaviour.setAgent(this);
        subscriberbehaviour.addSubBehaviour(new AMSListenerBehaviour());

        addBehaviour(subscriberbehaviour);

        // Register to be notified about the REATTACHED event in order
        // to handle
        // Main Container faults
        try {
            NotificationHelper helper = (NotificationHelper) getHelper(NotificationService.NAME);
            containerListener = new ContainerListener() {

                public void bornAgent(final ContainerEvent ev) {

                }

                public void deadAgent(final ContainerEvent ev) {

                }

                public void reattached(final ContainerEvent ev) {

                    l.info("Main container lost subscription. Resubscribing.");
                    send(subscribe);
                }

                public void leadershipAcquired(final ContainerEvent arg0) {

                    // TODO Auto-generated method stub

                }

                public void reconnected(final ContainerEvent arg0) {

                    // TODO Auto-generated method stub

                }
            };
            helper.registerContainerListener(containerListener);
        } catch (ServiceException se) {
            // Just print a warning since this does not affect the
            // normal
            // operation of a ToolAgent
            l.warn("NotificationService not installed.");
        }

        l.info("Infiltrator agent ready.");

    }

    /**
     * @see jade.core.Agent#takeDown()
     */
    @Override
    protected void takeDown() {

        send(cancellation);
        l.info(getLocalName() + " exiting. Bye bye!");

        super.takeDown();
    }

    /**
     * Add PlatformEventListener to agent.
     *
     * @param listener
     *            PlatformEventListener implementation.
     */
    public void addPlatformEventListener(final PlatformEventListener listener) {

        if (!platformListeners.contains(listener))
            platformListeners.add(listener);
    }

    /**
     * Add AgentEventListener to agent.
     *
     * @param listener
     *            AgentEventListener implementation.
     */
    public void addAgentEventListener(final AgentEventListener listener) {

        if (!agentListeners.contains(listener))
            agentListeners.add(listener);
    }

    /**
     * Dispatch event to platformListeners.
     *
     * @param event
     *            Event.
     */
    public void dispatchEvent(final Event event) {

        if (event instanceof AddedContainer || event instanceof RemovedContainer
                || event instanceof PlatformDescription || event instanceof ResetEvents)
            dispatchToPlatformListeners(event);

        else if (event instanceof BornAgent || event instanceof DeadAgent
                || event instanceof SuspendedAgent || event instanceof ResumedAgent
                || event instanceof FrozenAgent || event instanceof ThawedAgent
                || event instanceof ChangedAgentOwnership || event instanceof ChangedAgentState
                || event instanceof MovedAgent)
            dispatchToAgentListeners(event);

    }

    /**
     * Dispatch an event to agent listeners.
     *
     * @param event
     *            Event to dispatch.
     */
    private void dispatchToAgentListeners(final Event event) {

        for (Iterator i = agentListeners.iterator(); i.hasNext();) {

            AgentEventListener listener = (AgentEventListener) i.next();

            if (event instanceof BornAgent)
                listener.agentBorn((BornAgent) event);
            else if (event instanceof DeadAgent)
                listener.agentDead((DeadAgent) event);
            else if (event instanceof FrozenAgent)
                listener.agentFrozen((FrozenAgent) event);
            else if (event instanceof ThawedAgent)
                listener.agentThawed((ThawedAgent) event);
            else if (event instanceof MovedAgent)
                listener.agentMoved((MovedAgent) event);
            else if (event instanceof SuspendedAgent)
                listener.agentSuspended((SuspendedAgent) event);
            else if (event instanceof ResumedAgent)
                listener.agentResumed((ResumedAgent) event);
            else
                l.error("Unknown event type received: " + event.getClass().getName());
        }
    }

    /**
     * Dispatch an event to platform listeners.
     *
     * @param event
     *            Event to dispatch.
     */
    private void dispatchToPlatformListeners(final Event event) {

        for (Iterator i = platformListeners.iterator(); i.hasNext();) {

            PlatformEventListener listener = (PlatformEventListener) i.next();

            if (event instanceof AddedContainer)
                listener.containerAdded((AddedContainer) event);
            else if (event instanceof RemovedContainer)
                listener.containerRemoved((RemovedContainer) event);
            else if (event instanceof PlatformDescription)
                listener.receivedPlatformDescription((PlatformDescription) event);
            else if (event instanceof ResetEvents)
                listener.resetMeta((ResetEvents) event);
            else
                l.error("Unknown event type received: " + event.getClass().getName());
        }

    }

}
