package net.sf.jade4spring.internal;

import static org.junit.Assert.assertTrue;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import junit.framework.Assert;
import net.sf.jade4spring.JadeBean;
import net.sf.jade4spring.JadeThread;

import org.junit.Test;

/**
 * 
 * @author tarjei
 * @author xiaobo
 * 
 */

public class JadeThreadExecutorTest {

    @Test(expected = IllegalStateException.class)
    public void testInterruptThread_withoutInitailizing_expectedIllegalStateException() {

        JadeThreadExecutor jadeThreadExecutor = new JadeThreadExecutor();
        jadeThreadExecutor.interruptThread();
    }

    @Test
    public void testInterruptThread_withInitailizied_expectedThreadToBeInterruped() {

        JadeThreadExecutor jadeThreadExecutor = createJadeThreadWithDefaultProfileAndReturnExecutor();

        Thread thread = new Thread();
        thread.start();
        jadeThreadExecutor.setContainerThread(thread);

        jadeThreadExecutor.interruptThread();

        boolean interrupted = thread.isInterrupted();
        assertTrue(interrupted);

    }

    @Test
    public void testCreateJadeThreadWithProfileAndStartContainer_withDefaultProfileAndJadeBean_expectBootProfileToBeSet() {

        ProfileImpl expectedBootProfile = createDefaultBootProfile();
        JadeThread jadeThread = executeCreateJadeThreadWithProfileAndStartContainer(expectedBootProfile);

        ProfileImpl actualBootProfile = jadeThread.getBootProfile();
        Assert.assertEquals(expectedBootProfile, actualBootProfile);

    }

    @Test
    public void testCreateJadeThreadWithProfileAndStartContainer_withDefaultProfileAndJadeBean_expectJadeBeanToBeSet() {

        ProfileImpl expectedBootProfile = createDefaultBootProfile();
        JadeThread jadeThread = executeCreateJadeThreadWithProfileAndStartContainer(expectedBootProfile);

        JadeBean jadeBean = jadeThread.getJadeBean();
        Assert.assertNotNull(jadeBean);

    }

    @Test
    public void testCreateJadeThreadWithProfileAndStartContainer_withDefaultProfileAndJadeBean_expectRunTimeToBeSet() {

        ProfileImpl expectedBootProfile = createDefaultBootProfile();
        JadeThread jadeThread = executeCreateJadeThreadWithProfileAndStartContainer(expectedBootProfile);

        Runtime runtime = jadeThread.getRuntime();
        Assert.assertNotNull(runtime);

    }

    @Test
    public void testCreateJadeThreadWithProfileAndStartContainer_withDefaultProfileAndJadeBean_expectThreadToBeAlive() {

        JadeThreadExecutor jadeThreadExecutor = createJadeThreadWithDefaultProfileAndReturnExecutor();

        boolean isAlive = jadeThreadExecutor.getContainerThread().isAlive();
        Assert.assertTrue(isAlive);

        interruptThread(jadeThreadExecutor);

    }

    @Test
    public void testCreateJadeThreadWithProfileAndStartContainer_withDefaultProfileAndJadeBean_expectJadeThreadExecutorToBeInitialized() {

        JadeThreadExecutor jadeThreadExecutor = createJadeThreadWithDefaultProfileAndReturnExecutor();

        boolean initialized = jadeThreadExecutor.isInitialized();
        Assert.assertTrue(initialized);

        interruptThread(jadeThreadExecutor);

    }

    private JadeThreadExecutor createJadeThreadWithDefaultProfileAndReturnExecutor() {

        ProfileImpl expectedBootProfile = createDefaultBootProfile();
        JadeThreadExecutor jadeThreadExecutor = new JadeThreadExecutor();
        JadeBean jadeBean = new JadeBean();
        jadeThreadExecutor.createJadeThreadWithProfileAndStartContainer(expectedBootProfile,
                jadeBean);
        return jadeThreadExecutor;
    }

    private JadeThread executeCreateJadeThreadWithProfileAndStartContainer(
            ProfileImpl expectedBootProfile) {

        JadeThreadExecutor jadeThreadExecutor = new JadeThreadExecutor();
        JadeBean jadeBean = new JadeBean();
        JadeThread jadeThread = jadeThreadExecutor.createJadeThreadWithProfileAndStartContainer(
                expectedBootProfile, jadeBean);
        return jadeThread;
    }

    private ProfileImpl createDefaultBootProfile() {

        ProfileImpl bootProfile = new ProfileImpl();
        bootProfile.setParameter("container", "false");
        bootProfile.setParameter("host", "127.0.0.1");
        bootProfile.setParameter("local-host", "127.0.0.1");
        bootProfile.setParameter("local-port", "4242");
        return bootProfile;
    }

    private void interruptThread(final JadeThreadExecutor jadeThreadExecutor) {

        jadeThreadExecutor.interruptThread();
    }

}
