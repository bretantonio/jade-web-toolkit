package com.abreqadhabra.agent.jade.common.domain;

import jade.BootProfileImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.abreqadhabra.agent.jade.common.constants.Constant;

// TODO: Auto-generated Javadoc
/**
 * The Class JadeBootProperties.
 */
public class BootProperties {

	public static final String OTHER_PROPERTIES = "other_properties";

	public static final String AGENTS = "agent";

	/** The boot properties. */
	private String[] bootProperties = null;

	/** The property list. */
	private ArrayList<String> propertyList = new ArrayList<String>();

	/**
	 * Instantiates a new jade boot properties.
	 */
	public BootProperties() {
	}

	/**
	 * Gets the boot properties.
	 *
	 * @return the boot properties
	 */
	public String[] getBootProperties() {
		bootProperties = (String[]) propertyList
				.toArray(new String[propertyList.size()]);
		return bootProperties;
	}

	/**
	 * Gets the key string.
	 *
	 * @param key the key
	 * @return the key string
	 */
	private String getKeyString(String key) {
		StringBuffer sb = new StringBuffer("-");
		sb.append(key);
		return sb.toString();
	}

	/**
	 * Sets the name.
	 *
	 * @param platformName the new name
	 */
	public void setName(String platformName) {
		platformName = getKeyString(BootProfileImpl.NAME_KEY)
				+ Constant.STRING_SPACE + platformName;
		propertyList.add(platformName);
	}

	/**
	 * Checks if is container.
	 *
	 * @param b the b
	 */
	public void isContainer(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.CONTAINER_KEY));
		}
	}

	/**
	 * Sets the host.
	 *
	 * @param host the new host
	 */
	public void setHost(String host) {
		host = getKeyString(BootProfileImpl.MAIN_HOST) + Constant.STRING_SPACE
				+ host;
		propertyList.add(host);
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(String port) {
		port = getKeyString(BootProfileImpl.MAIN_PORT) + Constant.STRING_SPACE
				+ port;
		propertyList.add(port);
	}

	/**
	 * Checks if is gui.
	 *
	 * @param b the b
	 */
	public void isGui(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.GUI_KEY));
		}
	}

	/**
	 * Sets the local host.
	 *
	 * @param localHost the new local host
	 */
	public void isLocalHost(boolean b) {
		propertyList.add(getKeyString(BootProfileImpl.LOCAL_HOST));
	}

	/**
	 * Sets the local port.
	 *
	 * @param localPort the new local port
	 */
	public void isLocalPort(boolean b) {
		propertyList.add(getKeyString(BootProfileImpl.LOCAL_PORT));
	}

	/**
	 * Sets the platform id.
	 *
	 * @param platformId the new platform id
	 */
	public void setPlatformId(String platformId) {
		platformId = getKeyString(BootProfileImpl.PLATFORM_ID)
				+ Constant.STRING_SPACE + platformId;
		propertyList.add(platformId);
	}

	/**
	 * Sets the container name.
	 *
	 * @param containerName the new container name
	 */
	public void setContainerName(String containerName) {
		containerName = getKeyString(BootProfileImpl.CONTAINER_NAME)
				+ Constant.STRING_SPACE + containerName;
		propertyList.add(containerName);
	}

	/**
	 * Sets the services.
	 *
	 * @param serviceList the new services
	 */
	public void setServices(ArrayList<String> serviceList) {
		StringBuffer services = new StringBuffer();
		services = services.append(getKeyString(BootProfileImpl.SERVICES)
				+ Constant.STRING_SPACE);
		for (int i = 0; i < serviceList.size(); i++) {
			services.append(serviceList.get(i));
			services.append(Constant.STRING_SEMICOLON);
		}
		propertyList.add(services.toString());
	}

	/**
	 * Sets the mtps.
	 *
	 * @param mtpList the new mtps
	 */
	public void setMtps(ArrayList<String> mtpList) {
		StringBuffer mtps = new StringBuffer();
		mtps = mtps.append(getKeyString(BootProfileImpl.MTPS)
				+ Constant.STRING_SPACE);
		for (int i = 0; i < mtpList.size(); i++) {
			mtps.append(mtpList.get(i));
			mtps.append(Constant.STRING_SEMICOLON);
		}
		propertyList.add(mtps.toString());
	}

	/**
	 * Checks if is nomtp.
	 *
	 * @param b the b
	 */
	public void isNomtp(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.NOMTP_KEY));
		}
	}

	/**
	 * Checks if is backupmain.
	 *
	 * @param b the b
	 */
	public void isBackupmain(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList
					.add(getKeyString(BootProfileImpl.LOCAL_SERVICE_MANAGER));
		}
	}

	/**
	 * Checks if is smhost.
	 *
	 * @param b the b
	 */
	public void isSmhost(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.SMHOST_KEY));
		}
	}

	/**
	 * Checks if is smport.
	 *
	 * @param b the b
	 */
	public void isSmport(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.SMPORT_KEY));
		}
	}

	/**
	 * Checks if is smaddrs.
	 *
	 * @param b the b
	 */
	public void isSmaddrs(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList
					.add(getKeyString(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES));
		}
	}

	/**
	 * Sets the aclcodecs.
	 *
	 * @param aclcodecList the new aclcodecs
	 */
	public void setAclcodecs(ArrayList<String> aclcodecList) {
		StringBuffer aclcodecs = new StringBuffer();
		aclcodecs = aclcodecs.append(getKeyString(BootProfileImpl.ACLCODEC_KEY)
				+ Constant.STRING_SPACE);
		for (int i = 0; i < aclcodecList.size(); i++) {
			aclcodecs.append(aclcodecList.get(i));
			aclcodecs.append(Constant.STRING_SEMICOLON);
		}
		propertyList.add(aclcodecs.toString());
	}

	/**
	 * Checks if is nomobility.
	 *
	 * @param b the b
	 */
	public void isNomobility(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.NOMOBILITY_KEY));
		}
	}

	/**
	 * Checks if is version.
	 *
	 * @param b the b
	 */
	public void isVersion(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.VERSION_KEY));
		}
	}

	/**
	 * Checks if is help.
	 *
	 * @param b the boolean
	 */
	public void isHelp(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.HELP_KEY));
		}
	}

	/**
	 * Sets the conf.
	 *
	 * @param conf the new conf
	 */
	public void setConf(String conf) {
		conf = getKeyString(BootProfileImpl.CONF_KEY) + Constant.STRING_SPACE
				+ conf;
		propertyList.add(conf);
	}

	/**
	 * Sets the other properties.
	 *
	 * @param propertyMap the property map
	 */
	public void setOtherProperties(HashMap<String, String> propertyMap) {
		StringBuffer otherProperties = new StringBuffer();
		@SuppressWarnings("rawtypes")
		Iterator iterator = propertyMap.keySet().iterator();
		while (iterator.hasNext()) {
			String propertyName = (String) iterator.next();
			String propertyValue = propertyMap.get(propertyName);
			otherProperties.append(getKeyString(propertyName));
			otherProperties.append(Constant.STRING_SPACE);
			otherProperties.append(propertyValue);
			otherProperties.append(Constant.STRING_SPACE);
		}
		propertyList.add(otherProperties.toString());
	}

	/**
	 * Sets the agents.
	 *
	 * @param agentList the new agents
	 */
	public void setAgents(ArrayList<String> agentList) {
		// TODO Auto-generated method stub

	}

}
