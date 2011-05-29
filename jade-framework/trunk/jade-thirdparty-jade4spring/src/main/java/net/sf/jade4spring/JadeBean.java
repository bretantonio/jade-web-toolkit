/*
 * Created on Dec 28, 2005
 */
package net.sf.jade4spring;

import static java.lang.String.format;
import static net.sf.jade4spring.internal.Jade4SpringUtils.createBootProfileFromPropertiesFileOrClassPathResource;
import jade.content.ContentException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.jade4spring.internal.JadeBeanMetaContainer;
import net.sf.jade4spring.internal.JadeControllerContainer;
import net.sf.jade4spring.internal.JadeThreadExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Java bean used to start up a JADE container from the Spring
 * application
 * context.
 * 
 * @author Jaran Nilsen
 * @since 0.1
 * @version ${Revision}
 * 
 * @author Andreas Lundberg
 * @author Tarjei Romtveit
 */
public class JadeBean {

    private static final int SHUTDOWN_WAIT_INTERVAL = 100;

    /**
     * 
     */
    public static final String DEFAULT_PROPERTIES_LOCATION = "classpath:/jade.properties";

    /**
     * 
     */
    private static final int DEFAULT_SLEEP_ON_ALIVE_CHECK = 100;

    public static final int DEFAULT_TIME_OUT = 10000;

    private Log l = LogFactory.getLog(getClass());

    private String propertiesFile = DEFAULT_PROPERTIES_LOCATION;

    private AtomicBoolean containerActive = new AtomicBoolean(false);

    private Map<String, Agent> autostartAgents = new ConcurrentHashMap<String, Agent>();

    private boolean shutdownComplete = false;

    private boolean utilityAgents = true;

    private JadeBeanMetaContainer jadeBeanMetaContainer = new JadeBeanMetaContainer();

    private JadeThreadExecutor jadeThreadExecutor = new JadeThreadExecutor();

    private JadeControllerContainer jadeControllerContainer = new JadeControllerContainer(this);

    private int containerInitializingTimeOut = DEFAULT_TIME_OUT;

    private String infiltratorPrefix = JadeControllerContainer.DEFAULT_INFILTRATOR_AGENT_NAME;

    /**
     * Start the JADE container.
     */
    public void startContainer() throws JadeRuntimeException {

        l.info("Starting JADE container...");
        validateState();

        ProfileImpl bootProfile = createBootProfileThrowRuntimeExceptionIfFailed();
        JadeThread jadeThread = jadeThreadExecutor.createJadeThreadWithProfileAndStartContainer(
                bootProfile, this);

        long waitStart = System.currentTimeMillis();
        while (jadeThread.isInitializing()) {

            boolean elapsedTimeIsLargerThanSetTimeOut = isElapsedTimeLargerThanTimeOut(waitStart);
            if (elapsedTimeIsLargerThanSetTimeOut) {
                logTheTimeout();
                // The jadeThread or subthreads have failed. Therefore
                // return the method
                return;
            }

            sleepOnAliveCheck();

        }

        if (!getContainerActive()) {

            l.info("Aboring agents autostartup due to inactive container.");
            return;
        }

        startAllAutoStartAgents();

    }

    private boolean isElapsedTimeLargerThanTimeOut(long waitStart) {

        long elapsedTimeSinceStart = System.currentTimeMillis() - waitStart;
        boolean elapsedTimeIsLargerThanSetTimeOut = elapsedTimeSinceStart > containerInitializingTimeOut;
        return elapsedTimeIsLargerThanSetTimeOut;
    }

    private void sleepOnAliveCheck() {

        try {
            Thread.sleep(DEFAULT_SLEEP_ON_ALIVE_CHECK);
        } catch (InterruptedException ex) {
            interrruptContainerThreadAndThrowJadeRuntimeException(ex);
        }
    }

    private void startAllAutoStartAgents() {

        boolean theUtilityAgentFlagIsSet = utilityAgents;
        if (theUtilityAgentFlagIsSet)
            jadeControllerContainer.createAndStartInfiltratorAgent();

        autoStartAgents();
    }

    /**
     * 
     */
    private void validateState() {

        validateTimeOut();
    }

    private void validateTimeOut() {

        if (containerInitializingTimeOut < DEFAULT_SLEEP_ON_ALIVE_CHECK) {
            throw new IllegalStateException(String.format(
                    "The timOut parameter should be set higher than %d ms",
                    DEFAULT_SLEEP_ON_ALIVE_CHECK));
        }
    }

    private void interrruptContainerThreadAndThrowJadeRuntimeException(final InterruptedException ex) {

        jadeThreadExecutor.interruptThread();

        String message = "JADE container initialization was interrupted.";
        l.error(message, ex);

        throw new JadeRuntimeException(message, ex);
    }

    private void logTheTimeout() {

        l.info(format(
                "The JadeBean timed out after %d ms. This is probably caused by a failure in the JadeThread",
                containerInitializingTimeOut));
    }

    private ProfileImpl createBootProfileThrowRuntimeExceptionIfFailed() {

        ProfileImpl bootProfile;
        try {
            l.info(format("Loading properties from file: %s ", propertiesFile));
            bootProfile = createBootProfileFromPropertiesFileOrClassPathResource(propertiesFile);
        } catch (Exception ex) {

            throw new JadeRuntimeException(
                    "Jade4Spring has failed to initialize the JADE container.", ex);
        }
        return bootProfile;
    }

    /**
     * Start the agents that has been added via the Spring context.
     */
    private void autoStartAgents() {

        for (String agentName : autostartAgents.keySet()) {

            Agent agent = autostartAgents.get(agentName);

            jadeControllerContainer.acceptNewAgentAndStart(agentName, agent);
        }

    }

    /**
     * Stop the JADE container.
     */
    public void stopContainer() {

        if (!getContainerActive()) {

            l.info("Container was not running. Good bye!");
            jadeThreadExecutor.interruptThread();

            return;
        }

        resetContainerBeforeShuttingDown();

        try {
            jadeControllerContainer.stopInfiltratorAgent();
        } catch (Exception e) {
            l.error("The infiltratorAgent threw an exception when it tried to shutdown", e);
        } finally {
            setContainerActive(false);

            l.info("Waiting for container shutdown to complete...");

            waitUntilShutdownIsComplete();
            jadeThreadExecutor.interruptThread();

            l.info("JadeBean done. See you later!");
        }

    }

    private void waitUntilShutdownIsComplete() {

        while (!getShutdownComplete()) {

            try {
                Thread.sleep(SHUTDOWN_WAIT_INTERVAL);
            } catch (InterruptedException ex) {

                setShutdownComplete(true);
            }
        }
    }

    private void resetContainerBeforeShuttingDown() {

        setShutdownComplete(false);
    }

    /**
     * Start a new JADE agent.
     * 
     * @param name
     * @param path
     * @param params
     * @throws JadeRuntimeException
     */
    public void startAgent(final String name, final String path, final Object[] params)
            throws JadeRuntimeException {

        startAgent(getContainerName(), name, path, params);
    }

    /**
     * Start an agent on a different container.
     * 
     * @param location
     *            Name of the container where the agent should be
     *            started.
     * @param name
     *            Name of the agent.
     * @param path
     *            The path of the agent class.
     * @param params
     *            Array of parameters for the agent.
     * @throws JadeRuntimeException
     *             Thrown if an error occurs while starting the agent.
     */
    public void startAgent(final String location, final String name, final String path,
            final Object[] params) throws JadeRuntimeException {

        try {
            // Added workaround to be able to start agent, and not get
            // NoSuchContainerException every time.
            ContainerID containerId = new ContainerID();
            containerId.setName(location);
            jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);
            // END workaround

            containerId = jadeBeanMetaContainer.findContainerByLocationName(location);
            jadeControllerContainer.startAgentOnContainer(name, path, params, containerId);

        } catch (Exception ex) {

            String errorMessage = format("Failed to execute CreateAgent command for %s [%s]", name,
                    path);
            logFailedToExecuteMessageAndThrowJadeRuntimeException(errorMessage, ex);
        }

    }

    private void logFailedToExecuteMessageAndThrowJadeRuntimeException(final String errorMessage,
            final Exception ex) {

        l.error(errorMessage, ex);
        throw new JadeRuntimeException(errorMessage, ex);
    }

    /**
     * Suspend an agent on a given container.
     * 
     * @param name
     *            Name of agent to suspend.
     * @param nametype
     *            Boolean value indicating if the name is a local or
     *            global
     *            name.
     * @throws JadeRuntimeException
     *             Thrown if an error occurs when dispatcing suspend
     *             command.
     */
    public void suspendAgent(final String name, final boolean nametype) throws JadeRuntimeException {

        try {
            jadeControllerContainer.sendSuspendToInfiltratorAgent(name, nametype);
        } catch (ContentException e) {

            String errorMessage = format("Failed to execute SuspendAgent for %s", name);
            logFailedToExecuteMessageAndThrowJadeRuntimeException(errorMessage, e);
        }
    }

    /**
     * @see net.sf.jade4spring.JadeBean#suspendAgent(String, boolean)
     *      Using
     *      default localname value; false.
     */
    public void suspendAgent(final String name) throws JadeRuntimeException {

        suspendAgent(name, AID.ISGUID);
    }

    /**
     * Resume an agent on a remote container.
     * 
     * @param name
     *            Agent to resume.
     * @param nametype
     *            Agent name is Local or GUID.
     * @throws JadeRuntimeException
     *             Thrown if the request can not be dispatched.
     */
    public void resumeAgent(final String name, final boolean nametype) throws JadeRuntimeException {

        try {
            jadeControllerContainer.sendResumeToInfiltratorAgent(name, nametype);
        } catch (ContentException e) {

            String errorMessage = format("Failed to execute ResumeAgent for %s", name);
            logFailedToExecuteMessageAndThrowJadeRuntimeException(errorMessage, e);
        }

    }

    /**
     * @see net.sf.jade4spring.JadeBean#resumeAgent(String, boolean)
     */
    public void resumeAgent(final String name) throws JadeRuntimeException {

        resumeAgent(name, AID.ISGUID);
    }

    /**
     * Kill an agent on a remote container.
     * 
     * @param agentName
     *            Agent name.
     * @param nametype
     *            Agent name is Local or GUID.
     * @throws JadeRuntimeException
     *             Thrown if an error occurs while dispatching
     *             request.
     */
    public void killAgent(final String agentName, final boolean nametype)
            throws JadeRuntimeException {

        try {
            jadeControllerContainer.sendKillToInfiltratorAgent(agentName, nametype);
        } catch (ContentException e) {
            String errorMessage = format("Failed to execute KillAgent for %s", agentName);
            logFailedToExecuteMessageAndThrowJadeRuntimeException(errorMessage, e);
        }
    }

    /**
     * Kills an agent with the Global Unique ID name. This means that
     * you must pass both agentName@host:port/JADE
     * 
     * @see net.sf.jade4spring.JadeBean#killAgent(String, boolean)
     */
    public void killAgent(final String agentName) throws JadeRuntimeException {

        killAgent(agentName, AID.ISGUID);
    }

    /**
     * Get the name of the container.
     * 
     * @return Name of the container.
     */
    public String getContainerName() throws JadeRuntimeException {

        try {
            return jadeControllerContainer.getContainerName();
        } catch (ControllerException ex) {

            l.error("Failed to get name of container.", ex);

            return null;
        }
    }

    /**
     * Get AgentController for the given agent.
     * 
     * @param name
     *            Name of the agent.
     * @return AgentController for the agent.
     * @throws JadeBeanException
     *             Thrown if agent controller for the given agent can
     *             not be
     *             found.
     */
    public AgentController getAgentController(final String name) throws JadeBeanException {

        try {

            return jadeControllerContainer.getAgentController(name);
        } catch (ControllerException ex) {

            String message = format("Failed to retrieve controller for %s", name);
            logErrorMessageAndThrowJadeBeanException(ex, message);
            return null;
        }

    }

    public void logErrorMessageAndThrowJadeBeanException(Exception ex, String message)
            throws JadeBeanException {

        l.error(message, ex);
        throw new JadeBeanException(message, ex);
    }

    /**
     * Add a new container description to the container map.
     * 
     * @param containerId
     *            Container description.
     */
    protected void addContainer(final ContainerID containerId) {

        jadeBeanMetaContainer.createNewContainerWithIdIfNotExists(containerId);
    }

    /**
     * Requests the AMS to shutdown a container.
     * 
     * @param containerId
     *            ID of the container to shutdown.
     */
    public void killContainer(final String containerId) {

        try {

            jadeControllerContainer.sendKillContainerToInfiltratorAgent(containerId);
        } catch (Exception ex) {

            String message = "Failed to kill container " + containerId;
            l.error(message, ex);
            throw new JadeRuntimeException(message, ex);

        }
    }

    /**
     * Get a Map of agents running on the given location. Agents are
     * represented
     * by AID and AgentMeta objects.
     * 
     * @param location
     *            The location to get a map of agents for.
     * @return A Map of AID and AgentMeta objects.
     * @throws NoSuchContainerException
     *             Thrown if the container does not exist.
     */
    public Map<AID, AgentMeta> getAgentsOnLocation(final String location)
            throws NoSuchContainerException {

        return jadeBeanMetaContainer.getAgentMetaFromLocationName(location);
    }

    /**
     * @return the utilityAgents
     */
    public boolean isUtilityAgents() {

        return utilityAgents;
    }

    /**
     * @param utilityAgents
     *            the utilityAgents to set
     */
    public void setUtilityAgents(final boolean utilityAgents) {

        this.utilityAgents = utilityAgents;
    }

    /**
     * @param jadeBeanMetaContainer
     *            the jadeBeanMetaContainer to set
     */
    public void setJadeBeanMetaContainer(final JadeBeanMetaContainer jadeBeanMetaContainer) {

        this.jadeBeanMetaContainer = jadeBeanMetaContainer;
    }

    /**
     * @return the jadeBeanMetaContainer
     */
    public JadeBeanMetaContainer getJadeBeanMetaContainer() {

        return jadeBeanMetaContainer;
    }

    /**
     * @return Returns the shutdownComplete.
     */
    public synchronized boolean getShutdownComplete() {

        return shutdownComplete;
    }

    /**
     * @param shutdownComplete
     *            The shutdownComplete to set.
     */
    public synchronized void setShutdownComplete(final boolean shutdownComplete) {

        this.shutdownComplete = shutdownComplete;
    }

    /**
     * @return Returns the containerActive.
     */
    public boolean getContainerActive() {

        return containerActive.get();
    }

    /**
     * @param containerActive
     *            The containerActive to set.
     */
    public void setContainerActive(final boolean containerActive) {

        this.containerActive.set(containerActive);
    }

    /**
     * @return Returns the propertiesFile.
     */
    public String getPropertiesFile() {

        return propertiesFile;
    }

    /**
     * @param propertiesFile
     *            The propertiesFile to set.
     */
    public void setPropertiesFile(final String configPath) {

        this.propertiesFile = configPath;
    }

    /**
     * Get a Map with all containers available on the current
     * platform.
     * 
     * @return Map containing all containers and agents running on the
     *         current
     *         platform.
     */
    public Map<ContainerID, ConcurrentHashMap<AID, AgentMeta>> getPlatformLocations() {

        return jadeBeanMetaContainer.getContainers();
    }

    /**
     * Get a list containing ContainerIDs representing the containers
     * of the
     * platform.
     * 
     * @return List of ContainerID objects.
     */
    public List<ContainerID> getPlainLocationsList() {

        return jadeBeanMetaContainer.getListOfContainerID();
    }

    /**
     * @return Returns the autostartAgents.
     */
    public Map<String, Agent> getAutostartAgents() {

        return autostartAgents;
    }

    /**
     * @param autostartAgents
     *            The autostartAgents to set.
     */
    public void setAutostartAgents(final Map<String, Agent> autostartAgents) {

        this.autostartAgents = autostartAgents;
    }

    public void setJadeControllerContainer(JadeControllerContainer jadeControllerContainer) {

        this.jadeControllerContainer = jadeControllerContainer;
    }

    public JadeControllerContainer getJadeControllerContainer() {

        return jadeControllerContainer;
    }

    public void setInfiltratorPrefix(String infiltratorPrefix) {

        this.infiltratorPrefix = infiltratorPrefix;
    }

    public String getInfiltratorPrefix() {

        return infiltratorPrefix;
    }

}
