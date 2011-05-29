/*
 * Created on May 13, 2005
 */
package net.sf.jade4spring.infiltrator;

import jade.domain.introspection.AMSSubscriber;
import jade.domain.introspection.IntrospectionVocabulary;

import java.util.Map;

/**
 * @author Jaran Nilsen
 * @since 0.1
 * @version $Revision: 1.2 $
 */
public class AMSListenerBehaviour extends AMSSubscriber {

    /**
     * 
     */
    private static final long serialVersionUID = 8852768881847954210L;

    private static InfiltratorAgent agent;

    private EventHandler eventHandler;

    /**
     * @see jade.domain.introspection.AMSSubscriber#installHandlers(java.util.Map)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void installHandlers(Map handlers) {

        InfiltratorAgent ia = (InfiltratorAgent) AMSListenerBehaviour.getAgent();
        eventHandler = new GeneralEventHandler(ia);

        handlers.put(IntrospectionVocabulary.META_RESETEVENTS, eventHandler);
        handlers.put(IntrospectionVocabulary.BORNAGENT, eventHandler);
        handlers.put(IntrospectionVocabulary.ADDEDCONTAINER, eventHandler);
        handlers.put(IntrospectionVocabulary.REMOVEDCONTAINER, eventHandler);
        handlers.put(IntrospectionVocabulary.DEADAGENT, eventHandler);
        handlers.put(IntrospectionVocabulary.SUSPENDEDAGENT, eventHandler);
        handlers.put(IntrospectionVocabulary.RESUMEDAGENT, eventHandler);
        handlers.put(IntrospectionVocabulary.MOVEDAGENT, eventHandler);
        handlers.put(IntrospectionVocabulary.PLATFORMDESCRIPTION, eventHandler);
        handlers.put(IntrospectionVocabulary.FROZENAGENT, eventHandler);
        handlers.put(IntrospectionVocabulary.THAWEDAGENT, eventHandler);
        handlers.put(IntrospectionVocabulary.CHANGEDAGENTOWNERSHIP, eventHandler);

    }

    /**
     * @param a
     *            Infiltrator agent to set.
     */
    public static void setAgent(InfiltratorAgent a) {

        agent = a;
    }

    /**
     * @return InfiltratorAgent
     */
    public static InfiltratorAgent getAgent() {

        return agent;
    }

}
