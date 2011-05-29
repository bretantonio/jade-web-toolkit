package net.sf.jade4spring.internal;

import static junit.framework.Assert.assertEquals;
import jade.core.Profile;
import jade.core.ProfileImpl;

import java.io.IOException;

import org.junit.Test;

public class Jade4SpringUtilsTest {

    @Test(expected = IOException.class)
    public void testCreateBootProfileFromPropertiesFileOrClassPathResource_withPropertiesFileNotExist_expectedAnIOException()
            throws IOException {

        String propertiesFile = "";
        Jade4SpringUtils.createBootProfileFromPropertiesFileOrClassPathResource(propertiesFile);
    }

    @Test(expected = IOException.class)
    public void testCreateBootProfileFromPropertiesFileOrClassPathResource_withPropertiesFileOnClasspathNotExist_expectedAnIOException()
            throws IOException {

        String propertiesFile = "classpath:/donotexists.properties";
        Jade4SpringUtils.createBootProfileFromPropertiesFileOrClassPathResource(propertiesFile);
    }

    @Test
    public void testCreateBootProfileFromPropertiesFileOrClassPathResource_withPropertiesClassPathFileExist_expectedParametersPresent()
            throws IOException {

        String propertiesFile = "classpath:/net/sf/jade4spring/jade.properties";
        ProfileImpl profile = Jade4SpringUtils
                .createBootProfileFromPropertiesFileOrClassPathResource(propertiesFile);

        assertAllParametersPresent(profile);

    }

    @Test
    public void testCreateBootProfileFromPropertiesFileOrClassPathResource_withPropertiesFileExist_expectedParametersPresent()
            throws IOException {

        String propertiesFile = "src/test/resources/net/sf/jade4spring/jade.properties";
        ProfileImpl profile = Jade4SpringUtils
                .createBootProfileFromPropertiesFileOrClassPathResource(propertiesFile);
        assertAllParametersPresent(profile);

    }

    private void assertAllParametersPresent(final ProfileImpl profile) {

        String expectedHostName = "127.0.0.1";
        String actualHostName = profile.getParameter("host", "");
        assertEquals(expectedHostName, actualHostName);

        String expectedContainer = "false";
        String actualContainer = profile.getParameter("container", "");
        assertEquals(expectedContainer, actualContainer);

        String expectedLocalHost = "127.0.0.1";
        String actualLocalHost = profile.getParameter("local-host", "");
        assertEquals(expectedLocalHost, actualLocalHost);

        String expectedLocalPort = "42424";
        String actualLocalPort = profile.getParameter("local-port", "");
        assertEquals(expectedLocalPort, actualLocalPort);

        String expectedService = "jade.core.mobility.AgentMobilityService;jade.core.event.NotificationService";
        String actualService = profile.getParameter("services", "");
        assertEquals(expectedService, actualService);

        String actualMtps = profile.getParameter(Profile.MTPS, "");
        assertEquals("", actualMtps);
    }
}
