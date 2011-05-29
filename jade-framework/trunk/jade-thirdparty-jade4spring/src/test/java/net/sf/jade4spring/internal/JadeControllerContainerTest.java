package net.sf.jade4spring.internal;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jade.content.ContentManager;
import jade.content.abs.AbsContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.StringCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.domain.FIPANames;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class JadeControllerContainerTest {

    JadeControllerContainer container;

    PlatformController platformController;

    AgentContainer agentContainer;

    AgentController agentController;

    ContainerController containerController;

    InfiltratorAgent infiltratorAgent;

    JadeBean jadeBean;

    @Before
    public void setUp() throws Exception {

        jadeBean = new JadeBean();
        container = new JadeControllerContainer(jadeBean);

        platformController = mock(PlatformController.class);
        agentContainer = mock(AgentContainer.class);
        agentController = mock(AgentController.class);
        containerController = mock(ContainerController.class);

        infiltratorAgent = new InfiltratorAgent();

        container.setInfiltratorAgent(infiltratorAgent);
        setUpCodecAndOntology();

    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * @throws StaleProxyException
     * 
     */
    @Test
    public void acceptNewAgentAndStart_withBasicAgent_expectAcceptNewAgentIsCalledOnce()
            throws StaleProxyException {

        String agentName = "testAgent";
        Agent agent = new Agent();
        setupForAcceptNewAgent(agentName, agent);
        container.acceptNewAgentAndStart(agentName, agent);
        verify(agentContainer, times(1)).acceptNewAgent(agentName, agent);
    }

    /**
     * @throws ControllerException
     * 
     */
    @Test
    public void acceptNewAgentAndStart_withBasicAgent_expectStartToBeCalledOnAgentContainer()
            throws ControllerException {

        String agentName = "testAgent";
        Agent agent = new Agent();
        setupForAcceptNewAgent(agentName, agent);
        container.acceptNewAgentAndStart(agentName, agent);
        verify(agentController, times(1)).start();

    }

    @Test(expected = JadeRuntimeException.class)
    public void acceptNewAgentAndStart_withBasicAgent_expectlogFailedToStartAgentMessage()
            throws StaleProxyException {

        String agentName = "testAgent";
        Agent agent = new Agent();
        setupForAcceptNewAgent(agentName, agent);
        when(agentContainer.acceptNewAgent(agentName, agent)).thenThrow(new StaleProxyException());
        container.acceptNewAgentAndStart(agentName, agent);

    }

    @Test(expected = IllegalStateException.class)
    public void acceptNewAgentAndStart_withMinimalStateWherePlatformControllerIsNull_illegalStateExceptionIsThrown() {

        String agentName = "testAgent";
        Agent agent = new Agent();
        setupContainerWithStates(null, null, null);
        container.acceptNewAgentAndStart(agentName, agent);
    }

    /**************************************
     *** createAndStartInfiltratorAgent ***
     **************************************/

    @Test
    public void createAndStartInfiltratorAgent_withMockedJadeBean_expectExpectInfiltratorToStart()
            throws JadeRuntimeException, StaleProxyException {

        container.setPlatformController(agentContainer);
        when(
                agentContainer.acceptNewAgent(
                        Mockito.eq(JadeControllerContainer.DEFAULT_INFILTRATOR_AGENT_NAME),
                        Mockito.any(InfiltratorAgent.class))).thenReturn(agentController);

        container.createAndStartInfiltratorAgent();

        verify(agentController).start();

    }

    @Test(expected = JadeRuntimeException.class)
    public void createAndStartInfiltratorAgent_withMockedJadeBean_expectJadeRunTimeExceptionToBeThrown()
            throws JadeRuntimeException, StaleProxyException {

        container.setPlatformController(agentContainer);
        when(
                agentContainer.acceptNewAgent(
                        eq(JadeControllerContainer.DEFAULT_INFILTRATOR_AGENT_NAME),
                        any(InfiltratorAgent.class))).thenThrow(new StaleProxyException());
        container.createAndStartInfiltratorAgent();

    }

    @Test(expected = IllegalStateException.class)
    public void createAndStartInfiltratorAgent_withMinimalStateWherePlatformControllerIsNull_illegalStateExceptionIsThrown() {

        setupContainerWithStates(null, null, null);
        container.createAndStartInfiltratorAgent();
    }

    /****************************
     *** stopInfiltratorAgent ***
     ****************************/

    @Test
    public void stopInfiltratorAgent_withDefaultSetup_infiltratorAgentDoDeleteIsCalledOnce() {

        InfiltratorAgent infiltratorAgent = mock(InfiltratorAgent.class);
        setupFullState(infiltratorAgent);

        final int wantedNumberOfInvocations = 1;
        container.stopInfiltratorAgent();

        verify(infiltratorAgent, times(wantedNumberOfInvocations)).doDelete();

    }

    @Test(expected = IllegalStateException.class)
    public void stopInfiltratorAgent_withFullState_illegalStateExceptionIsThrown() {

        setupContainerWithStates(null, null, null);
        container.stopInfiltratorAgent();
    }

    @Test(expected = IllegalStateException.class)
    public void stopInfiltratorAgent_withFullStateWherePlatformControllerIsNull_illegalStateExceptionIsThrown() {

        setupContainerWithStates(null, infiltratorAgent, agentController);
        container.stopInfiltratorAgent();
    }

    @Test(expected = IllegalStateException.class)
    public void stopInfiltratorAgent_withFullStateWhereInfiltratorAgentIsNull_illegalStateExceptionIsThrown() {

        setupContainerWithStates(platformController, null, agentController);
        container.stopInfiltratorAgent();
    }

    @Test(expected = IllegalStateException.class)
    public void stopInfiltratorAgent_withFullStateWhereInfiltratorAgentControllerIsNull_illegalStateExceptionIsThrown() {

        setupContainerWithStates(platformController, infiltratorAgent, null);
        container.stopInfiltratorAgent();
    }

    /*****************************
     *** startAgentOnContainer
     * 
     * @throws NoSuchContainerException
     * @throws OntologyException
     * @throws CodecException
     *             ***
     *****************************/

    @Test(expected = IllegalStateException.class)
    public void startAgentOnContainer_withFullState_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, null, null);
        startAgentOnContainerWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void startAgentOnContainer_withFullStateWherePlatformControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, infiltratorAgent, agentController);
        startAgentOnContainerWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void startAgentOnContainer_withFullStateWhereInfiltratorAgentIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, null, agentController);
        startAgentOnContainerWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void startAgentOnContainer_withFullStateWhereInfiltratorAgentControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, infiltratorAgent, null);
        startAgentOnContainerWithEmptyParameters();
    }

    @Test
    public void startAgentOnContainer_withTestInputParameters_expectFillContentToBeCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);
        startAgentOnContainerWithDefaultParameters(contentManager);

        final int wantedNumberOfInvocations = 1;
        verify(contentManager, times(wantedNumberOfInvocations)).fillContent(any(ACLMessage.class),
                any(Action.class));

    }

    @Test
    public void startAgentOnContainer_withTestInputParameters_expectAddBehaviourToBeCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);
        InfiltratorAgent infiltratorAgent = startAgentOnContainerWithDefaultParameters(contentManager);

        final int wantedNumberOfInvocations = 1;
        verify(infiltratorAgent, times(wantedNumberOfInvocations)).addBehaviour(
                any(AMSClientBehaviour.class));

    }

    @Test(expected = IllegalStateException.class)
    public void sendSuspendToInfiltratorAgent_withFullState_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, null, null);
        sendSuspendAgentWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void sendSuspendToInfiltratorAgent_withFullStateWherePlatformControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, infiltratorAgent, agentController);
        sendSuspendAgentWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void sendSuspendToInfiltratorAgent_withFullStateWhereInfiltratorAgentIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, null, agentController);
        sendSuspendAgentWithEmptyParameters();

    }

    @Test(expected = IllegalStateException.class)
    public void sendSuspendToInfiltratorAgent_withFullStateWhereInfiltratorAgentControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, infiltratorAgent, null);
        sendSuspendAgentWithEmptyParameters();
    }

    @Test
    public void sendSuspendToInfiltratorAgent_withGUIDNameAndDefaultSetUp_expectFillContentToBeCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);

        suspendAgentOnContainerWithDefaultParameters(contentManager);

        int wantedNumberOfInvocations = 1;
        verify(contentManager, times(wantedNumberOfInvocations)).fillContent(any(ACLMessage.class),
                any(Action.class));

    }

    @Test
    public void sendSuspendToInfiltratorAgent_withGUIDNameAndDefaultSetUp_expectAddBehaviourIsCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);

        InfiltratorAgent infiltratorAgent = suspendAgentOnContainerWithDefaultParameters(contentManager);

        int wantedNumberOfInvocations = 1;
        verify(infiltratorAgent, times(wantedNumberOfInvocations)).addBehaviour(
                any(AMSClientBehaviour.class));

    }

    @Test(expected = IllegalStateException.class)
    public void sendResumeToInfiltratorAgent_withFullState_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, null, null);
        sendResumeAgentWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void sendResumeToInfiltratorAgent_withFullStateWherePlatformControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, infiltratorAgent, agentController);
        sendResumeAgentWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void sendResumeToInfiltratorAgent_withFullStateWhereInfiltratorAgentIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, null, agentController);
        sendResumeAgentWithEmptyParameters();

    }

    @Test(expected = IllegalStateException.class)
    public void sendResumeToInfiltratorAgent_withFullStateWhereInfiltratorAgentControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, infiltratorAgent, null);
        sendResumeAgentWithEmptyParameters();
    }

    @Test
    public void sendResumeToInfiltratorAgent_withGUIDNameAndDefaultSetUp_expectFillContentToBeCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);

        resumeAgentOnContainerWithDefaultParameters(contentManager);

        int wantedNumberOfInvocations = 1;
        verify(contentManager, times(wantedNumberOfInvocations)).fillContent(any(ACLMessage.class),
                any(Action.class));

    }

    @Test
    public void sendResumeToInfiltratorAgent_withGUIDNameAndDefaultSetUp_expectAddBehaviourIsCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);

        InfiltratorAgent infiltratorAgent = resumeAgentOnContainerWithDefaultParameters(contentManager);

        int wantedNumberOfInvocations = 1;
        verify(infiltratorAgent, times(wantedNumberOfInvocations)).addBehaviour(
                any(AMSClientBehaviour.class));

    }

    @Test(expected = IllegalStateException.class)
    public void sendKillContainerToInfiltratorAgent_withFullState_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, null, null);
        sendKillContainerWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void sendKillContainerToInfiltratorAgent_withFullStateWherePlatformControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, infiltratorAgent, agentController);
        sendKillContainerWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void sendKillContainerToInfiltratorAgent_withFullStateWhereInfiltratorAgentIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, null, agentController);
        sendKillContainerWithEmptyParameters();

    }

    @Test(expected = IllegalStateException.class)
    public void sendKillContainerToInfiltratorAgent_withFullStateWhereInfiltratorAgentControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, infiltratorAgent, null);
        sendKillContainerWithEmptyParameters();
    }

    @Test
    public void sendKillContainerToInfiltratorAgent_withGUIDNameAndDefaultSetUp_expectFillContentToBeCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);

        killContainerWithDefaultParameters(contentManager);

        int wantedNumberOfInvocations = 1;
        verify(contentManager, times(wantedNumberOfInvocations)).fillContent(any(ACLMessage.class),
                any(Action.class));

    }

    @Test
    public void sendKillContainerToInfiltratorAgent_withGUIDNameAndDefaultSetUp_expectAddBehaviourIsCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);

        InfiltratorAgent infiltratorAgent = killContainerWithDefaultParameters(contentManager);

        int wantedNumberOfInvocations = 1;
        verify(infiltratorAgent, times(wantedNumberOfInvocations)).addBehaviour(
                any(AMSClientBehaviour.class));

    }

    @Test(expected = IllegalStateException.class)
    public void sendKillToInfiltratorAgent_withFullState_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, null, null);
        sendKillAgentToInfiltratorWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void sendKillToInfiltratorAgent_withFullStateWherePlatformControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(null, infiltratorAgent, agentController);
        sendKillAgentToInfiltratorWithEmptyParameters();
    }

    @Test(expected = IllegalStateException.class)
    public void sendKillToInfiltratorAgent_withFullStateWhereInfiltratorAgentIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, null, agentController);
        sendKillAgentToInfiltratorWithEmptyParameters();

    }

    @Test(expected = IllegalStateException.class)
    public void sendKillToInfiltratorAgent_withFullStateWhereInfiltratorAgentControllerIsNull_illegalStateExceptionIsThrown()
            throws CodecException, OntologyException, NoSuchContainerException {

        setupContainerWithStates(platformController, infiltratorAgent, null);
        sendKillAgentToInfiltratorWithEmptyParameters();
    }

    @Test
    public void sendKillToInfiltratorAgent_withGUIDNameAndDefaultSetUp_expectFillContentToBeCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);

        killAgentOnContainerWithDefaultParameters(contentManager);

        int wantedNumberOfInvocations = 1;
        verify(contentManager, times(wantedNumberOfInvocations)).fillContent(any(ACLMessage.class),
                any(Action.class));

    }

    @Test
    public void sendKillToInfiltratorAgent_withGUIDNameAndDefaultSetUp_expectAddBehaviourIsCalledOnce()
            throws CodecException, OntologyException, NoSuchContainerException {

        ContentManager contentManager = mock(ContentManager.class);

        InfiltratorAgent infiltratorAgent = killAgentOnContainerWithDefaultParameters(contentManager);

        int wantedNumberOfInvocations = 1;
        verify(infiltratorAgent, times(wantedNumberOfInvocations)).addBehaviour(
                any(AMSClientBehaviour.class));

    }

    @Test(expected = IllegalStateException.class)
    public void getContainerName_withDefaultWherePlatformControllerIsNull_expectIllegalStateException()
            throws ControllerException {

        container.setPlatformController(null);
        container.getContainerName();

    }

    @Test
    public void getContainerName_withDefaultSetup_expectMethodNeverReturnNull()
            throws ControllerException {

        container.setPlatformController(agentContainer);
        String expectedContainerName = "container1";
        when(agentContainer.getContainerName()).thenReturn(expectedContainerName);
        String actualContainerName = container.getContainerName();

        assertEquals(expectedContainerName, actualContainerName);

    }

    @Test(expected = IllegalStateException.class)
    public void getAgentController_withNonEmptyInputStringAndNoPlaformController_expectIllegalStateException()
            throws ControllerException {

        String name = "testAgent";
        container.getAgentController(name);
    }

    @Test
    public void getAgentController_withNonEmptyInputString_expectGetAgentToBeCalledOnce()
            throws ControllerException {

        container.setPlatformController(platformController);

        String name = "testAgent";
        container.getAgentController(name);
        final int wantedNumberOfInvocations = 1;
        verify(platformController, times(wantedNumberOfInvocations)).getAgent(name);
    }

    @Test
    public void getAgentController_withNonEmptyInputString_expectToRetrieveStateAgentController()
            throws ControllerException {

        container.setPlatformController(platformController);

        String name = "testAgent";

        when(platformController.getAgent(name)).thenReturn(agentController);

        AgentController actualAgentController = container.getAgentController(name);

        assertEquals(actualAgentController, actualAgentController);

    }

    private InfiltratorAgent startAgentOnContainerWithDefaultParameters(
            ContentManager contentManager) throws NoSuchContainerException, CodecException,
            OntologyException {

        InfiltratorAgent infiltratorAgent = createInfiltratorContainerAndAgentAndSetupMock(contentManager);

        Object[] params = { "testParam1", "testParam2" };
        ContainerID containerId = new ContainerID();
        container.startAgentOnContainer("testAgent", "testPath", params, containerId);
        return infiltratorAgent;
    }

    private InfiltratorAgent suspendAgentOnContainerWithDefaultParameters(
            ContentManager contentManager) throws NoSuchContainerException, CodecException,
            OntologyException {

        InfiltratorAgent infiltratorAgent = createInfiltratorContainerAndAgentAndSetupMock(contentManager);
        String name = AID.AGENT_CLASSNAME;
        boolean nametype = AID.ISGUID;
        container.sendSuspendToInfiltratorAgent(name, nametype);
        return infiltratorAgent;
    }

    private InfiltratorAgent resumeAgentOnContainerWithDefaultParameters(
            ContentManager contentManager) throws NoSuchContainerException, CodecException,
            OntologyException {

        InfiltratorAgent infiltratorAgent = createInfiltratorContainerAndAgentAndSetupMock(contentManager);
        String name = AID.AGENT_CLASSNAME;
        boolean nametype = AID.ISGUID;
        container.sendResumeToInfiltratorAgent(name, nametype);
        return infiltratorAgent;
    }

    private InfiltratorAgent killAgentOnContainerWithDefaultParameters(ContentManager contentManager)
            throws NoSuchContainerException, CodecException, OntologyException {

        InfiltratorAgent infiltratorAgent = createInfiltratorContainerAndAgentAndSetupMock(contentManager);
        String name = AID.AGENT_CLASSNAME;
        boolean nametype = AID.ISGUID;
        container.sendKillToInfiltratorAgent(name, nametype);
        return infiltratorAgent;
    }

    private InfiltratorAgent killContainerWithDefaultParameters(ContentManager contentManager)
            throws NoSuchContainerException, CodecException, OntologyException {

        InfiltratorAgent infiltratorAgent = createInfiltratorContainerAndAgentAndSetupMock(contentManager);

        String containerId = "JADE-container-classname";
        container.sendKillContainerToInfiltratorAgent(containerId);
        return infiltratorAgent;
    }

    private InfiltratorAgent createInfiltratorContainerAndAgentAndSetupMock(
            ContentManager contentManager) {

        InfiltratorAgentContainer infiltratorAgentContainer = spy(new InfiltratorAgentContainer());
        container.setInfiltratorAgentContainer(infiltratorAgentContainer);

        InfiltratorAgent infiltratorAgent = mock(InfiltratorAgent.class);
        setupContainerWithStates(platformController, infiltratorAgent, agentController);
        doReturn(new AID()).when(infiltratorAgentContainer).getAMS();

        when(infiltratorAgent.getContentManager()).thenReturn(contentManager);
        return infiltratorAgent;
    }

    private void setUpCodecAndOntology() {

        Codec codec = setupStringCodec();
        Ontology o = JADEManagementOntology.getInstance();
        infiltratorAgent.getContentManager().registerOntology(o, JADEManagementOntology.NAME);
        infiltratorAgent.getContentManager().registerLanguage(codec,
                FIPANames.ContentLanguage.FIPA_SL0);
    }

    private Codec setupStringCodec() {

        Codec codec = new StringCodec("") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public String encode(Ontology arg0, AbsContentElement arg1) throws CodecException {

                return null;
            }

            @Override
            public String encode(AbsContentElement arg0) throws CodecException {

                return null;
            }

            @Override
            public AbsContentElement decode(Ontology arg0, String arg1) throws CodecException {

                return null;
            }

            @Override
            public AbsContentElement decode(String arg0) throws CodecException {

                return null;
            }
        };
        return codec;
    }

    private void setupForAcceptNewAgent(String agentName, Agent agent) throws StaleProxyException {

        container.setPlatformController(agentContainer);
        when(agentContainer.acceptNewAgent(agentName, agent)).thenReturn(agentController);
    }

    private void setupFullState(InfiltratorAgent infiltratorAgent) {

        container.setPlatformController(platformController);
        container.setInfiltratorAgent(infiltratorAgent);
        container.setInfiltratorAgentController(agentController);
    }

    private void setupContainerWithStates(PlatformController platformController,
            InfiltratorAgent infiltratorAgent, AgentController agentController) {

        container.setPlatformController(platformController);
        container.setInfiltratorAgent(infiltratorAgent);
        container.setInfiltratorAgentController(agentController);
    }

    private void startAgentOnContainerWithEmptyParameters() throws NoSuchContainerException,
            CodecException, OntologyException {

        String name = "";
        String path = "";
        Object[] params = null;
        ContainerID containerId = new ContainerID();
        container.startAgentOnContainer(name, path, params, containerId);
    }

    private void sendSuspendAgentWithEmptyParameters() throws NoSuchContainerException,
            CodecException, OntologyException {

        boolean nametype = true;
        String name = "TestAgent";
        container.sendSuspendToInfiltratorAgent(name, nametype);
    }

    private void sendResumeAgentWithEmptyParameters() throws NoSuchContainerException,
            CodecException, OntologyException {

        boolean nametype = true;
        String name = "TestAgent";
        container.sendResumeToInfiltratorAgent(name, nametype);
    }

    private void sendKillAgentToInfiltratorWithEmptyParameters() throws NoSuchContainerException,
            CodecException, OntologyException {

        boolean nametype = true;
        String name = "TestAgent";
        container.sendKillToInfiltratorAgent(name, nametype);
    }

    private void sendKillContainerWithEmptyParameters() throws NoSuchContainerException,
            CodecException, OntologyException {

        String containerId = "containerId1";
        container.sendKillContainerToInfiltratorAgent(containerId);
    }
}
