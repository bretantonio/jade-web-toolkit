/*
 * Created on Jan 12, 2006
 */
package net.sf.jade4spring.infiltrator;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * AMS Client behaviour for the InfiltratorAgent. Used when performing agent
 * operations such as start and stop.
 * @author Jaran Nilsen
 * @since 0.2
 * @version $Revision: 1.1 $
 */
public class AMSClientBehaviour extends SimpleAchieveREInitiator {

    private Log l = LogFactory.getLog(getClass());

    private String operationId;


    /**
     * Create a new instance of AMSClientBehaviour.
     * @param agent The agent which the behaviour belongs to.
     * @param message The ACLMessage for the AMS.
     * @param operationId An identification string which will be used when
     *            logging reponse.
     */
    public AMSClientBehaviour(Agent agent, ACLMessage message,
                              String operationId) {

        super(agent, message);
        this.operationId = operationId;

    }


    /**
     * @see jade.proto.SimpleAchieveREInitiator#handleFailure(jade.lang.acl.ACLMessage)
     */
    protected void handleFailure(ACLMessage reply) {

        l.error("Failed to perform operation " + operationId + " (Reply: "
            + reply.toString() + ")");
    }


    /**
     * @see jade.proto.SimpleAchieveREInitiator#handleInform(jade.lang.acl.ACLMessage)
     */
    protected void handleInform(ACLMessage reply) {

        l.info("Operation " + operationId + " completed.");
    }


    /**
     * @see jade.proto.SimpleAchieveREInitiator#handleNotUnderstood(jade.lang.acl.ACLMessage)
     */
    protected void handleNotUnderstood(ACLMessage reply) {

        l.warn("Operation " + operationId + " not understood by AMS (Reply: "
            + reply.toString() + ")");
    }


    /**
     * @see jade.proto.SimpleAchieveREInitiator#handleOutOfSequence(jade.lang.acl.ACLMessage)
     */
    protected void handleOutOfSequence(ACLMessage reply) {

        l.error("Operation " + operationId
            + " ended with out-of-sequence response from AMS (Reply: "
            + reply.toString() + ")");
    }


    /**
     * @see jade.proto.SimpleAchieveREInitiator#handleRefuse(jade.lang.acl.ACLMessage)
     */
    protected void handleRefuse(ACLMessage reply) {

        l.error("Operation " + operationId + " was refused by AMS (Reply: "
            + reply.toString() + ")");
    }

}
