package net.sf.jade4spring.internal;

import jade.core.AID;
import jade.core.Agent;
import net.sf.jade4spring.infiltrator.InfiltratorAgent;

/**
 * A container that is mainly utilized to stub final framework methods of the
 * {@link Agent}
 * 
 * @author Tarjei Romtveit
 * @author Andreas Lundberg
 * 
 */
public class InfiltratorAgentContainer {

    private InfiltratorAgent infiltratorAgent;

    /**
     * To be able to stub the final method getAMS an public wrapper getAMS is
     * created
     * 
     * @return
     */
    public AID getAMS() {

        return infiltratorAgent.getAMS();
    }

    public void setInfiltratorAgent(final InfiltratorAgent infiltratorAgent) {

        this.infiltratorAgent = infiltratorAgent;
    }

    public InfiltratorAgent getInfiltratorAgent() {

        return infiltratorAgent;
    }

}
