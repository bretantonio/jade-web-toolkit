/*
 * Created on Dec 28, 2005
 */
package net.sf.jade4spring;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jade.wrapper.PlatformState;
import net.sf.jade4spring.internal.JadeControllerContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The thread class running the platform.
 * 
 * @author Jaran Nilsen
 * @since 0.1
 * @version ${Revision}
 */
public class JadeThread implements Runnable {

    /**
     * Number of milliseconds to wait between each loop when the container is
     * running.
     */
    public static final long JADE_THEAD_LOOP_MS = 100;

    private Log l = LogFactory.getLog(getClass());

    private JadeBean jadeBean;

    private Runtime runtime;

    private ProfileImpl bootProfile;

    private boolean maincontainer;

    private boolean initializing = true;

    /**
     * @return the initializing
     */
    public boolean isInitializing() {

        return initializing;
    }

    /**
     * @param initializing
     *            the initializing to set
     */
    public void setInitializing(final boolean initializing) {

        this.initializing = initializing;
    }

    /**
     * @return Returns the runtime.
     */
    public Runtime getRuntime() {

        return runtime;
    }

    /**
     * @param runtime
     *            The runtime to set.
     */
    public void setRuntime(final Runtime runtime) {

        this.runtime = runtime;
    }

    /**
     * @return Returns the jadeBean.
     */
    public JadeBean getJadeBean() {

        return jadeBean;
    }

    /**
     * @param jadeBean
     *            The jadeBean to set.
     */
    public void setJadeBean(final JadeBean jadeBean) {

        this.jadeBean = jadeBean;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        initializing = true;

        PlatformController platformController = null;

        try {
            maincontainer = !bootProfile.getBooleanProperty("container", false);

            // Create main container
            if (maincontainer) {

                l.info("Creating main container...");
                platformController = runtime.createMainContainer(bootProfile);
            }
            // Create agent container
            else {
                l.info("Creating container...");
                platformController = runtime.createAgentContainer(bootProfile);
            }

            if (platformController == null) {
                l.error("Failed to create agent container. Container thread " + "will now exit.");

                initializing = false;
                return;
            }

            JadeControllerContainer jadeControllerContainer = jadeBean.getJadeControllerContainer();
            jadeControllerContainer.setPlatformController(platformController);

            // Set parameter indicating that the container is active.
            jadeBean.setContainerActive(true);

            l.info("JADE container created.");

            initializing = false;

            while (jadeBean.getContainerActive()) {

                try {

                    // Check for interrupts
                    if (Thread.interrupted()) {
                        jadeBean.setContainerActive(false);
                        continue;
                    }

                    Thread.sleep(JadeThread.JADE_THEAD_LOOP_MS);
                } catch (InterruptedException ex) {

                    jadeBean.setContainerActive(false);
                }
            }

        } catch (Exception ex) {

            initializing = false;
            l.error("Container starup failed.", ex);
        }

        try {
            if (platformController != null)
                platformController.kill();

            l.info("Waiting for container to shut down.");

            while (platformController.getState().getCode() != PlatformState.cPLATFORM_STATE_KILLED) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }

            l.info("Container shutdown complete.");
            l.info("Shutting down runtime...");

            if (runtime != null)
                runtime.shutDown();
            l.info("Runtime shutdown complete.");
        } catch (ControllerException e) {

            l.error("Exception when killing container.", e);
        }

        jadeBean.setShutdownComplete(true);

        l.info("Shutdown procedure complete.");

    }

    /**
     * @return Returns the bootProfile.
     */
    public ProfileImpl getBootProfile() {

        return bootProfile;
    }

    /**
     * @param bootProfile
     *            The bootProfile to set.
     */
    public void setBootProfile(final ProfileImpl bootProfile) {

        this.bootProfile = bootProfile;
    }

    /**
     * @return Returns the maincontainer.
     */
    public boolean isMaincontainer() {

        return maincontainer;
    }

}
