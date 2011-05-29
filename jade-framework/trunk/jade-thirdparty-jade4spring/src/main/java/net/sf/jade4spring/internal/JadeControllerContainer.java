package net.sf.jade4spring.internal;

import static java.lang.String.format;
import static net.sf.jade4spring.validators.Validator.checkIfNullAndThrowIllegalException;
import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.domain.JADEAgentManagement.KillContainer;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jade.wrapper.StaleProxyException;
import net.sf.jade4spring.JadeBean;
import net.sf.jade4spring.JadeRuntimeException;
import net.sf.jade4spring.NoSuchContainerException;
import net.sf.jade4spring.infiltrator.AMSClientBehaviour;
import net.sf.jade4spring.infiltrator.InfiltratorAgent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * A controller class designated to handle Jade-Agent specific
 * controllers and
 * execute basic commands
 * 
 * @author Tarjei Romtveit
 * @author Andreas Lundberg
 * 
 */
public class JadeControllerContainer {

    private static final String OPERATION_ID_SUSPEND_AGENT = "SuspendAgent(%s)";

    private static final String OPERATION_ID_RESUME_AGENT = "ResumeAgent(%s)";

    private static final String OPERATION_ID_KILL_AGENT = "KillAgent(%s)";

    private static final String OPERATION_ID_KILL_CONTAINER = "KillContainer(%s)";

    public static final String DEFAULT_INFILTRATOR_AGENT_NAME = "J4SInfiltratorAgent";

    private Log l = LogFactory.getLog(JadeControllerContainer.class);

    private PlatformController platformController;

    private AgentController infiltratorAgentController;

    private InfiltratorAgentContainer infiltratorAgentContainer = new InfiltratorAgentContainer();

    private JadeBeanPlatformEventListener platformEventListener;

    private JadeBeanAgentEventListener agentEventListener;

    private JadeBean jadeBean;

    public JadeControllerContainer(JadeBean jadeBean) {

        this.jadeBean = jadeBean;
        JadeBeanMetaContainer jadeBeanMetaContainer = jadeBean.getJadeBeanMetaContainer();
        platformEventListener = new JadeBeanPlatformEventListener(jadeBeanMetaContainer);
        agentEventListener = new JadeBeanAgentEventListener(jadeBeanMetaContainer);
    }

    /**
     * Starts an agent in the local container
     * 
     * @param agentName
     * @param agent
     * @throws JadeRuntimeException
     */
    public void acceptNewAgentAndStart(final String agentName, final Agent agent)
            throws JadeRuntimeException {

        validateMinimalState();
        try {
            AgentContainer agentContainer = (AgentContainer) getPlatformController();
            AgentController ac = agentContainer.acceptNewAgent(agentName, agent);
            ac.start();
        } catch (StaleProxyException ex) {

            logFailedToStartAgentMessage(agentName, agent, ex);
        }

    }

    /**
     * Start the utility agents necessary for the JadeBean to operate
     * at 100%.
     * 
     * @throws JadeRuntimeException
     *             Thrown if an error occurs while starting the
     *             agents.
     */
    public void createAndStartInfiltratorAgent() throws JadeRuntimeException {

        validateMinimalState();
        try {
            createAndStartTheUtilityAgent();
        } catch (ControllerException ex) {

            final String message = "Failed to start an utility agent.";

            l.error(message, ex);
            throw new JadeRuntimeException(message, ex);
        }
    }

    /**
     * Stop the infiltratorAgent by calling doDelete
     */
    public void stopInfiltratorAgent() {

        validateFullState();
        getInfiltratorAgent().doDelete();

    }

    /**
     * 
     * The method starts an agent in a remote or local agent container
     * 
     * @param agentName
     *            The agent name
     * @param qualifiedClassName
     *            The class name associated with the agent
     * @param classInputArguments
     *            Used to initiate the agent class specified in path
     * @param containerId
     *            The container ID that the agent will execute in
     * @throws NoSuchContainerException
     * @throws CodecException
     * @throws OntologyException
     */
    public void startAgentOnContainer(final String agentName, final String qualifiedClassName,
            final Object[] classInputArguments, ContainerID containerId)
            throws NoSuchContainerException, CodecException, OntologyException {

        validateFullState();

        CreateAgent ca = createAgentWithContainerId(agentName, qualifiedClassName, containerId);
        addParametersToAgent(classInputArguments, ca);
        Action action = createActionWithConcept(ca);

        ACLMessage message = createACLMessage(JADEManagementOntology.NAME);
        String operationId = format("CreateAgent(%s[%s])", agentName, qualifiedClassName);
        fillInfiltratorAgentContentAndAddBehaviour(operationId, action, message);
    }

    /**
     * 
     * The method starts an agent in a remote or local agent container
     * 
     * @param agentName
     *            The agent name
     * @param qualifiedClassName
     *            The class name associated with the agent
     * @param containerId
     *            The container ID that the agent will execute in
     * @throws NoSuchContainerException
     * @throws CodecException
     * @throws OntologyException
     */
    public void startAgentOnContainer(final String agentName, final String qualifiedClassName,
            ContainerID containerId) throws NoSuchContainerException, CodecException,
            OntologyException {

        startAgentOnContainer(agentName, qualifiedClassName, null, containerId);
    }

    /**
     * Sends suspend message to infiltrator agent.
     * 
     * @param name
     *            The agent that needs to be suspended
     * @param nametype
     *            The name type of the agent {@link AID}
     * @throws CodecException
     * @throws OntologyException
     */
    public void sendSuspendToInfiltratorAgent(final String name, final boolean nametype)
            throws CodecException, OntologyException {

        validateFullState();

        String agentDescriptionState = AMSAgentDescription.SUSPENDED;
        String operationId = format(OPERATION_ID_SUSPEND_AGENT, name);

        sendModifyToInfiltratorAgent(name, nametype, agentDescriptionState, operationId,
                FIPAManagementOntology.NAME);

    }

    /**
     * Sends resume message to infiltrator agent.
     * 
     * @param name
     *            The agent that needs to be resume
     * @param nametype
     *            The name type of the agent {@link AID}
     * @throws CodecException
     * @throws OntologyException
     */
    public void sendResumeToInfiltratorAgent(String name, boolean nametype) throws CodecException,
            OntologyException {

        validateFullState();

        String agentDescriptionState = AMSAgentDescription.ACTIVE;
        String operationId = format(OPERATION_ID_RESUME_AGENT, name);

        sendModifyToInfiltratorAgent(name, nametype, agentDescriptionState, operationId,
                FIPAManagementOntology.NAME);
    }

    /**
     * Sends kill message to infiltrator agent.
     * 
     * @param name
     *            The agent that needs to be killed
     * @param nametype
     *            The name type of the agent {@link AID}
     * @throws CodecException
     * @throws OntologyException
     */
    public void sendKillToInfiltratorAgent(final String agentName, final boolean nametype)
            throws CodecException, OntologyException {

        validateFullState();

        String operationId = format(OPERATION_ID_KILL_AGENT, agentName);
        throwJadeRuntimeExceptionWhenAgentNameIsEmpty(agentName);

        KillAgent killAgent = createKillAgentAction(agentName, nametype);
        createActionAndPrepareInfiltratorAgent(operationId, killAgent, JADEManagementOntology.NAME);
    }

    /**
     * Sends kill container message to infiltrator agent.
     * 
     * @param containerId
     *            The container ID kill
     * 
     * @throws CodecException
     * @throws OntologyException
     */
    public void sendKillContainerToInfiltratorAgent(final String containerId)
            throws CodecException, OntologyException {

        validateFullState();

        String operationId = format(OPERATION_ID_KILL_CONTAINER, containerId);
        throwJadeRuntimeExceptionWhenAgentNameIsEmpty(containerId);

        KillContainer kc = createKillContainer(containerId);
        createActionAndPrepareInfiltratorAgent(operationId, kc, JADEManagementOntology.NAME);
    }

    private KillContainer createKillContainer(final String containerId) {

        KillContainer kc = new KillContainer();
        kc.setContainer(new ContainerID(containerId, null));
        return kc;
    }

    private void sendModifyToInfiltratorAgent(final String name, final boolean nametype,
            final String agentDescriptionState, final String operationId, final String ontologyName)
            throws CodecException, OntologyException {

        throwJadeRuntimeExceptionWhenAgentNameIsEmpty(name);
        AMSAgentDescription agentDesc = createAMSAgentDescription(name, nametype,
                agentDescriptionState);
        Modify modify = createModifyWithAgentDescription(agentDesc);

        createActionAndPrepareInfiltratorAgent(operationId, modify, ontologyName);
    }

    private void validateMinimalState() {

        checkIfNullAndThrowIllegalException(platformController);

    }

    private void validateFullState() {

        checkIfNullAndThrowIllegalException(platformController);
        checkIfNullAndThrowIllegalException(getInfiltratorAgentController());
        checkIfNullAndThrowIllegalException(getInfiltratorAgentContainer().getInfiltratorAgent());
    }

    private KillAgent createKillAgentAction(final String name, final boolean nametype) {

        AID aid = new AID(name, nametype);

        KillAgent killAgent = new KillAgent();
        killAgent.setAgent(aid);
        return killAgent;
    }

    private void logFailedToStartAgentMessage(final String agentName, final Agent agent,
            final StaleProxyException ex) {

        String message = "Failed to start agent";
        l.error(message, ex);
        throw new JadeRuntimeException(message, ex);
    }

    private void createAndStartTheUtilityAgent() throws StaleProxyException {

        createInfiltratorAgent();
        createNewInfiltratorController();

        getInfiltratorAgentController().start();
    }

    private void createInfiltratorAgent() {

        setInfiltratorAgent(new InfiltratorAgent());
        getInfiltratorAgent().addAgentEventListener(agentEventListener);
        getInfiltratorAgent().addPlatformEventListener(platformEventListener);
    }

    private void createNewInfiltratorController() throws StaleProxyException {

        final ContainerController containerController = (ContainerController) platformController;
        final String infiltratorPrefix = jadeBean.getInfiltratorPrefix();
        setInfiltratorAgentController(containerController.acceptNewAgent(infiltratorPrefix,
                getInfiltratorAgent()));
    }

    private void fillInfiltratorAgentContentAndAddBehaviour(final String operationId,
            final Action action, final ACLMessage message) throws CodecException, OntologyException {

        getInfiltratorAgent().getContentManager().fillContent(message, action);
        getInfiltratorAgent().addBehaviour(
                new AMSClientBehaviour(getInfiltratorAgent(), message, operationId));
    }

    private ACLMessage createACLMessage(String ontologyName) {

        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(getInfiltratorAgentContainer().getAMS());
        message.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        message.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
        message.setOntology(ontologyName);
        return message;
    }

    private Action createActionWithConcept(final Concept concept) {

        Action action = new Action();
        action.setActor(getInfiltratorAgentContainer().getAMS());
        action.setAction(concept);
        return action;
    }

    private void addParametersToAgent(final Object[] params, final CreateAgent ca) {

        if (params != null) {
            for (Object param : params)
                ca.addArguments(param);
        }
    }

    private CreateAgent createAgentWithContainerId(final String name, final String path,
            ContainerID containerId) throws NoSuchContainerException {

        CreateAgent ca = new CreateAgent();
        ca.setContainer(containerId);
        ca.setOwner(null);
        ca.setInitialCredentials(null);

        ca.setAgentName(name);
        ca.setClassName(path);
        return ca;
    }

    private void throwJadeRuntimeExceptionWhenAgentNameIsEmpty(final String name) {

        if (name == null || name.length() == 0)
            throw new JadeRuntimeException("Unable to dispatch request with empty agent name.");
    }

    private AMSAgentDescription createAMSAgentDescription(final String name,
            final boolean nametype, final String agentDescriptionState) {

        AID aid = new AID(name, nametype);

        AMSAgentDescription agentDesc = new AMSAgentDescription();
        agentDesc.setName(aid);
        agentDesc.setState(agentDescriptionState);
        return agentDesc;
    }

    private Modify createModifyWithAgentDescription(final AMSAgentDescription agentDesc) {

        Modify modify = new Modify();
        modify.setDescription(agentDesc);

        return modify;
    }

    private void createActionAndPrepareInfiltratorAgent(final String operationId,
            final AgentAction agentAction, final String ontologyName) throws CodecException,
            OntologyException {

        Action action = createActionWithConcept(agentAction);
        ACLMessage message = createACLMessage(ontologyName);
        fillInfiltratorAgentContentAndAddBehaviour(operationId, action, message);
    }

    public String getContainerName() throws ControllerException {

        validateMinimalState();

        ContainerController containerController = (ContainerController) platformController;
        return containerController.getContainerName();
    }

    public AgentController getAgentController(final String name) throws ControllerException {

        validateMinimalState();

        AgentController agent = platformController.getAgent(name);
        return agent;
    }

    public AgentController getInfiltratorAgentController() {

        return infiltratorAgentController;
    }

    public void setPlatformController(PlatformController platformController) {

        this.platformController = platformController;
    }

    public PlatformController getPlatformController() {

        return platformController;
    }

    public void setInfiltratorAgent(InfiltratorAgent infiltratorAgent) {

        this.getInfiltratorAgentContainer().setInfiltratorAgent(infiltratorAgent);
    }

    public InfiltratorAgent getInfiltratorAgent() {

        return getInfiltratorAgentContainer().getInfiltratorAgent();
    }

    public void setInfiltratorAgentController(AgentController infiltratorAgentController) {

        this.infiltratorAgentController = infiltratorAgentController;
    }

    public void setInfiltratorAgentContainer(InfiltratorAgentContainer infiltratorAgentContainer) {

        this.infiltratorAgentContainer = infiltratorAgentContainer;
    }

    public InfiltratorAgentContainer getInfiltratorAgentContainer() {

        return infiltratorAgentContainer;
    }

    public void setPlatformEventListener(JadeBeanPlatformEventListener platformEventListener) {

        this.platformEventListener = platformEventListener;
    }

    public JadeBeanPlatformEventListener getPlatformEventListener() {

        return platformEventListener;
    }

    public void setAgentEventListener(JadeBeanAgentEventListener agentEventListener) {

        this.agentEventListener = agentEventListener;
    }

    public JadeBeanAgentEventListener getAgentEventListener() {

        return agentEventListener;
    }

}
