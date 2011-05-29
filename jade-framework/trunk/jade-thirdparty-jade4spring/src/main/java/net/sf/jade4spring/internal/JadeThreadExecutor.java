package net.sf.jade4spring.internal;

import jade.core.ProfileImpl;
import jade.core.Runtime;

import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.jade4spring.JadeBean;
import net.sf.jade4spring.JadeThread;

/**
 * 
 * @author tarjei
 * 
 */

public class JadeThreadExecutor {

    /**
     * 
     */
    private static final boolean DEFAULT_CLOSE_VM = false;

    private Thread containerThread;

    private Runtime runtime;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    public JadeThreadExecutor() {

        createRuntimeWithSetCloseVM();
    }

    public void interruptThread() {

        validateState();
        containerThread.interrupt();

    }

    private void validateState() {

        boolean notInitialized = !getInitialized().get();
        if (notInitialized)
            throw new IllegalStateException(
                    "The JadeThreadExecutor is not correctly initialized. Run createJadeThreadWithProfileAndStartContainer to initialize");
    }

    private void createRuntimeWithSetCloseVM() {

        runtime = Runtime.instance();
        runtime.setCloseVM(DEFAULT_CLOSE_VM);
    }

    private void startContainerWithJadeThread(final JadeThread jadeThread) {

        setContainerThread(new Thread(jadeThread));
        getContainerThread().start();
        getInitialized().set(true);
    }

    private JadeThread createJadeThreadWithBootProfileAndJadeBean(final ProfileImpl bootProfile,
            final JadeBean jadeBean) {

        JadeThread jadeThread = new JadeThread();
        jadeThread.setJadeBean(jadeBean);
        jadeThread.setRuntime(runtime);
        jadeThread.setBootProfile(bootProfile);
        return jadeThread;
    }

    public JadeThread createJadeThreadWithProfileAndStartContainer(final ProfileImpl bootProfile,
            final JadeBean jadeBean) {

        JadeThread jadeThread = createJadeThreadWithBootProfileAndJadeBean(bootProfile, jadeBean);
        startContainerWithJadeThread(jadeThread);
        return jadeThread;
    }

    public boolean isInitialized() {

        return initialized.get();
    }

    /**
     * @param containerThread
     *            the containerThread to set
     */
    public void setContainerThread(Thread containerThread) {

        this.containerThread = containerThread;
    }

    /**
     * @return the containerThread
     */
    public Thread getContainerThread() {

        return containerThread;
    }

    /**
     * @param initialized
     *            the initialized to set
     */
    public void setInitialized(AtomicBoolean initialized) {

        this.initialized = initialized;
    }

    /**
     * @return the initialized
     */
    public AtomicBoolean getInitialized() {

        return initialized;
    }

}
