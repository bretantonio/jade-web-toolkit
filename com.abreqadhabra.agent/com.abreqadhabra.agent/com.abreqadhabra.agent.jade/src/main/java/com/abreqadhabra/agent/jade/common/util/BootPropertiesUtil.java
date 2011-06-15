package com.abreqadhabra.agent.jade.common.util;

import jade.BootProfileImpl;
import jade.core.ProfileImpl;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.springframework.core.io.ClassPathResource;

import com.abreqadhabra.agent.jade.common.constants.Constant;
import com.abreqadhabra.agent.jade.common.domain.BootProperties;


public class BootPropertiesUtil {

    public static String[] createBootPropertyArgs(
	    ArrayList<String> bootPropertyList) throws IOException {
	String[] bootPropertyArgs = null;
	Properties props = new Properties();
	if (bootPropertyList != null) {
	    bootPropertyArgs = (String[]) bootPropertyList
		    .toArray(new String[bootPropertyList.size()]);
	} else {
	    props = readPropertiesFromClassPathOrFile(Constant.BOOT_PROFILE_UTIL.DEFAULT_PROPERTIES_LOCATION);
	    bootPropertyArgs = (String[]) props.values().toArray(
		    new String[props.size()]);
	}

	return bootPropertyArgs;
    }

    public static ProfileImpl createBootProfileFromHashMap(
	    HashMap<String, String> profileHashMap) throws IOException {
	Properties props = new Properties();
	if (!profileHashMap.isEmpty()) {
	    @SuppressWarnings("rawtypes")
	    Iterator iterator = profileHashMap.keySet().iterator();
	    while (iterator.hasNext()) {
		String key = (String) iterator.next();
		props.setProperty(key, profileHashMap.get(key));
	    }
	} else {
	    props = readPropertiesFromClassPathOrFile(Constant.BOOT_PROFILE_UTIL.DEFAULT_PROPERTIES_LOCATION);
	}

	ProfileImpl profile = new ProfileImpl(props);
	return profile;
    }

    /**
     * 
     * Read properties file from the path supplied. The method is able to load
     * both from file system and the classpath. To indicate that it should read
     * the file from classpath, use classpath:/ as prefix.
     * 
     * @param propertiesFile
     * @return a populated instance of {@link jade.util.ExtendedProperties}
     * @throws IOException
     */
    private static ExtendedProperties readPropertiesFromClassPathOrFile(
	    final String propertiesFile) throws IOException {
	InputStream in = null;
	try {
	    in = readPropertiesFile(propertiesFile);
	    final ExtendedProperties properties = new ExtendedProperties();
	    properties.load(in);
	    return properties;
	} finally {
	    if (in != null) {
		in.close();
	    }
	}
    }

    public static InputStream readPropertiesFile(final String propertiesFile)
	    throws IOException {
	InputStream in = null;
	if (propertiesFile.startsWith(Constant.CLASSPATH_IDENTIFIER)) {

	    ClassPathResource classPathResource = new ClassPathResource(
		    getPropertiesFileName(propertiesFile));
	    in = classPathResource.getInputStream();
	} else {

	    in = new FileInputStream(propertiesFile);
	}
	return in;
    }

    private static String getPropertiesFileName(final String propertiesFile) {
	return propertiesFile.substring(Constant.CLASSPATH_IDENTIFIER.length());
    }

    public static InputStreamReader getInputStreamReader(Class<?> klass,
	    String fileName) throws IOException {
	String classPath = (klass.getPackage().getName()).replace(
		Constant.STRING_PERIOD, Constant.STRING_SLASH);
	String classPathResourceFile = Constant.CLASSPATH_IDENTIFIER
		+ classPath + Constant.STRING_SLASH + fileName;
	InputStream in = (InputStream) readPropertiesFile(classPathResourceFile);
	return new InputStreamReader(in);
    }
    
    @SuppressWarnings("unchecked")
	public static String[] getBootPropertyArgs(HashMap<String, Object> propertiesMap) {
		BootProperties bootProperties = new BootProperties();
		/* -container */
		if (propertiesMap.containsKey(BootProfileImpl.CONTAINER_KEY)) {
			bootProperties.isContainer((Boolean) propertiesMap
					.get(BootProfileImpl.CONTAINER_KEY));
		}
		/* -host */
		if (propertiesMap.containsKey(BootProfileImpl.MAIN_HOST)) {
			bootProperties.setHost((String) propertiesMap
					.get(BootProfileImpl.MAIN_HOST));
		}
		/* -port */
		if (propertiesMap.containsKey(BootProfileImpl.MAIN_PORT)) {
			bootProperties.setPort((String) propertiesMap
					.get(BootProfileImpl.MAIN_PORT));
		}
		/* -gui */
		if (propertiesMap.containsKey(BootProfileImpl.GUI_KEY)) {
			bootProperties.isGui((Boolean) propertiesMap
					.get(BootProfileImpl.GUI_KEY));
		}
		/* -local-host */
		if (propertiesMap.containsKey(BootProfileImpl.LOCAL_HOST)) {
			bootProperties.isLocalHost((Boolean) propertiesMap
					.get(BootProfileImpl.LOCAL_HOST));
		}
		/* -local-port */
		if (propertiesMap.containsKey(BootProfileImpl.LOCAL_PORT)) {
			bootProperties.isLocalPort((Boolean) propertiesMap
					.get(BootProfileImpl.LOCAL_PORT));
		}
		/* -platform-id */
		if (propertiesMap.containsKey(BootProfileImpl.PLATFORM_ID)) {
			bootProperties.setPlatformId((String) propertiesMap
					.get(BootProfileImpl.PLATFORM_ID));
		}
		/* -name */
		if (propertiesMap.containsKey(BootProfileImpl.NAME_KEY)) {
			bootProperties.setName((String) propertiesMap
					.get(BootProfileImpl.NAME_KEY));
		}
		/* -container-name */
		if (propertiesMap.containsKey(BootProfileImpl.CONTAINER_NAME)) {
			bootProperties.setContainerName((String) propertiesMap
					.get(BootProfileImpl.CONTAINER_NAME));
		}
		/* -services */
		if (propertiesMap.containsKey(BootProfileImpl.SERVICES)) {
			bootProperties.setServices((ArrayList<String>) propertiesMap
					.get(BootProfileImpl.SERVICES));
		}
		/* -mtps */
		if (propertiesMap.containsKey(BootProfileImpl.MTPS)) {
			bootProperties.setMtps((ArrayList<String>) propertiesMap
					.get(BootProfileImpl.MTPS));
		}
		/* -nomtp */
		if (propertiesMap.containsKey(BootProfileImpl.NOMTP_KEY)) {
			bootProperties.isNomtp((Boolean) propertiesMap
					.get(BootProfileImpl.NOMTP_KEY));
		}
		/* -backupmain */
		if (propertiesMap.containsKey(BootProfileImpl.LOCAL_SERVICE_MANAGER)) {
			bootProperties.isBackupmain((Boolean) propertiesMap
					.get(BootProfileImpl.LOCAL_SERVICE_MANAGER));
		}
		/* -smhost */
		if (propertiesMap.containsKey(BootProfileImpl.SMHOST_KEY)) {
			bootProperties.isSmhost((Boolean) propertiesMap
					.get(BootProfileImpl.SMHOST_KEY));
		}
		/* -smport */
		if (propertiesMap.containsKey(BootProfileImpl.SMPORT_KEY)) {
			bootProperties.isSmport((Boolean) propertiesMap
					.get(BootProfileImpl.SMPORT_KEY));
		}
		/* -smaddrs */
		if (propertiesMap
				.containsKey(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES)) {
			bootProperties.isSmaddrs((Boolean) propertiesMap
					.get(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES));
		}
		/* -aclcodecs */
		if (propertiesMap.containsKey(BootProfileImpl.ACLCODEC_KEY)) {
			bootProperties.setAclcodecs((ArrayList<String>) propertiesMap
					.get(BootProfileImpl.ACLCODEC_KEY));
		}
		/* -nomobility */
		if (propertiesMap.containsKey(BootProfileImpl.NOMOBILITY_KEY)) {
			bootProperties.isNomobility((Boolean) propertiesMap
					.get(BootProfileImpl.NOMOBILITY_KEY));
		}
		/* -version */
		if (propertiesMap.containsKey(BootProfileImpl.VERSION_KEY)) {
			bootProperties.isVersion((Boolean) propertiesMap
					.get(BootProfileImpl.VERSION_KEY));
		}
		/* -help */
		if (propertiesMap.containsKey(BootProfileImpl.HELP_KEY)) {
			bootProperties.isHelp((Boolean) propertiesMap
					.get(BootProfileImpl.HELP_KEY));
		}
		/* -conf */
		if (propertiesMap.containsKey(BootProfileImpl.CONF_KEY)) {
			bootProperties.setHost((String) propertiesMap
					.get(BootProfileImpl.CONF_KEY));
		}
		/* -<property-name><property-value> */
		if (propertiesMap.containsKey(BootProperties.OTHER_PROPERTIES)) {
			bootProperties
					.setOtherProperties((HashMap<String, String>) propertiesMap
							.get(BootProperties.OTHER_PROPERTIES));
		}
		/* -agents */
		if (propertiesMap.containsKey(BootProperties.AGENTS)) {
			bootProperties.setAgents((ArrayList<String>) propertiesMap
					.get(BootProperties.AGENTS));
		}
		return bootProperties.getBootProperties();
	}


}
