package net.sf.jade4spring;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * This is an unmplemented JUnit test class for the JadeBean class.
 * The methods are tested, but are present in the
 * jade4spring-integration-tests project
 * 
 * @author andreas
 * 
 */
@Ignore
public class JadeBeanTest {

    private JadeBean jadeBean;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    /**********************
     *** startContainer ***
     **********************/

    @Test
    public void startContainer_() {

        jadeBean.startContainer();
    }

    /*********************
     *** stopContainer ***
     *********************/

    @Test
    public void stopContainer_() {

        jadeBean.stopContainer();
    }

    /******************
     *** startAgent ***
     ******************/

    @Test
    public void startAgent_() {

        String name = null;
        String path = null;
        Object[] params = null;
        jadeBean.startAgent(name, path, params);

    }

    @Test
    public void startAgent_with_expect() {

        String name = null;
        String path = null;
        Object[] params = null;

        String location = null;
        jadeBean.startAgent(location, name, path, params);
    }

    /*****************
     *** killAgent ***
     *****************/

    @Test
    public void killAgent_() {

        String name = null;
        jadeBean.killAgent(name);
    }

    @Test
    public void killAgent_withNameType() {

        String name = null;
        boolean nametype = false;
        jadeBean.killAgent(name, nametype);
    }

    /********************
     *** suspendAgent ***
     ********************/

    @Test
    public void suspendAgent_() {

        String name = null;
        jadeBean.suspendAgent(name);
    }

    @Test
    public void suspendAgent_withNameType() {

        String name = null;
        boolean nameType = false;
        jadeBean.suspendAgent(name, nameType);
    }

    /********************
     *** resumeAgent ***
     ********************/

    @Test
    public void resumeAgent_() {

        String name = null;
        jadeBean.resumeAgent(name);
    }

    @Test
    public void resumeAgent_withNameType() {

        String name = null;
        boolean nameType = false;
        jadeBean.resumeAgent(name, nameType);
    }

    /**************************
     *** getAgentController ***
     **************************/

    @Test
    public void getAgentController_() throws JadeBeanException {

        String name = null;
        jadeBean.getAgentController(name);
    }

    /************************
     *** getContainerName ***
     ************************/

    @Test
    public void getContainerName() {

        jadeBean.getContainerName();
    }

    /*********************
     *** killContainer ***
     *********************/

    @Test
    public void killContainer_() {

        String containerId = null;
        jadeBean.killContainer(containerId);
    }

    /***************************
     *** getAgentsOnLocation ***
     ***************************/

    @Test
    public void getAgentsOnLocation_() {

        jadeBean.getAutostartAgents();
    }

}
