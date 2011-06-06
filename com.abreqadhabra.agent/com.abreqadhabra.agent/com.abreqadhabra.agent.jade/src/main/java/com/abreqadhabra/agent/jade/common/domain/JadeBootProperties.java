package com.abreqadhabra.agent.jade.common.domain;

import jade.BootProfileImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.abreqadhabra.agent.jade.common.constants.Constant;

public class JadeBootProperties {

	private String[] bootProperties = null;
	private ArrayList<String> propertyList = new ArrayList<String>();

	public JadeBootProperties() {
	}

	public String[] getBootProperties() {
		bootProperties = (String[]) propertyList
				.toArray(new String[propertyList.size()]);
		return bootProperties;
	}

	private String getKeyString(String key) {
		StringBuffer sb = new StringBuffer("-");
		sb.append(key);
		return sb.toString();
	}

	public void setName(String platformName) {
		platformName = getKeyString(BootProfileImpl.NAME_KEY)
				+ Constant.STRING_SPACE + platformName;
		propertyList.add(platformName);
	}

	public void isContainer(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.CONTAINER_KEY));
		}
	}

	public void setHost(String host) {
		host = getKeyString(BootProfileImpl.MAIN_HOST) + Constant.STRING_SPACE
				+ host;
		propertyList.add(host);
	}

	public void setPort(String port) {
		port = getKeyString(BootProfileImpl.MAIN_PORT) + Constant.STRING_SPACE
				+ port;
		propertyList.add(port);
	}

	public void isGui(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.GUI_KEY));
		}
	}

	public void setLocalHost(String localHost) {
		localHost = getKeyString(BootProfileImpl.LOCAL_HOST)
				+ Constant.STRING_SPACE + localHost;
		propertyList.add(localHost);

	}

	public void setLocalPort(String localPort) {
		localPort = getKeyString(BootProfileImpl.LOCAL_PORT)
				+ Constant.STRING_SPACE + localPort;
		propertyList.add(localPort);

	}

	public void setPlatformId(String platformId) {
		platformId = getKeyString(BootProfileImpl.PLATFORM_ID)
				+ Constant.STRING_SPACE + platformId;
		propertyList.add(platformId);
	}

	public void setContainerName(String containerName) {
		containerName = getKeyString(BootProfileImpl.CONTAINER_NAME)
				+ Constant.STRING_SPACE + containerName;
		propertyList.add(containerName);
	}

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

	public void isNomtp(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.NOMTP_KEY));
		}
	}

	public void isBackupmain(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList
					.add(getKeyString(BootProfileImpl.LOCAL_SERVICE_MANAGER));
		}
	}

	public void isSmhost(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.SMHOST_KEY));
		}
	}

	public void isSmport(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.SMPORT_KEY));
		}
	}

	public void isSmaddrs(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList
					.add(getKeyString(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES));
		}
	}

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

	public void isNomobility(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.NOMOBILITY_KEY));
		}
	}

	public void isVersion(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.VERSION_KEY));
		}
	}

	public void isHelp(boolean b) {
		if (b == Boolean.TRUE.booleanValue()) {
			propertyList.add(getKeyString(BootProfileImpl.HELP_KEY));
		}
	}

	public void setConf(String conf) {
		conf = getKeyString(BootProfileImpl.CONF_KEY) + Constant.STRING_SPACE
				+ conf;
		propertyList.add(conf);
	}

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

	public void setAgents(ArrayList<String> agentList) {
		// TODO Auto-generated method stub

	}

}
