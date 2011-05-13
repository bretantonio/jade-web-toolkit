package com.abreqadhabra.agent.jade.common.util;

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


public class BootProfileUtil {

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

}
