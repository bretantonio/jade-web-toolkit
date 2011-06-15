package com.abreqadhabra.agent.jade.common.domain;

import jade.BootProfileImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.abreqadhabra.agent.jade.common.constants.Constant;

public class CmdLineArgs {

	Vector<Object> cmdLineArgsVector = new Vector<Object>();
	String[] bootPropertyArgs = null;

	boolean container = false;
	String host = null;
	String port = null;
	boolean gui = false;
	String localHost = null;
	String localPort = null;
	String platformId = null;
	String name = null;
	String containerName = null;
	String services = null;
	ArrayList<String> serviceList = new ArrayList<String>();
	String mtps = null;
	ArrayList<String> mtpList = new ArrayList<String>();
	boolean nomtp = false;
	boolean backupmain = false;
	String smhost = null;
	String smport = null;
	String smaddrs = null;
	String aclcodecs = null;
	ArrayList<String> aclcodecList = new ArrayList<String>();
	boolean nomobility = false;
	boolean version = false;
	boolean help = false;
	String conf = null;
	String otherProperties = null;
	HashMap<String, String> propertyMap = new HashMap<String, String>();
	String agents = null;
	ArrayList<String> agentList = new ArrayList<String>();

	public boolean isContainer() {
		return container;
	}

	public void setContainer(boolean container) {
		if (container == Boolean.TRUE.booleanValue()) {
			cmdLineArgsVector
					.addElement(getKeyString(BootProfileImpl.CONTAINER_KEY));
		}
		this.container = container;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.MAIN_HOST));
		cmdLineArgsVector.addElement(host);
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.MAIN_PORT));
		cmdLineArgsVector.addElement(port);
		this.port = port;
	}

	public boolean getGui() {
		return gui;
	}

	public void setGui(boolean gui) {
		if (gui == Boolean.TRUE.booleanValue()) {
			cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.GUI_KEY));
		}
		this.gui = gui;
	}

	public String getLocalHost() {
		return localHost;
	}

	public void setLocalHost(String localHost) {
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.LOCAL_HOST));
		cmdLineArgsVector.addElement(localHost);
		this.localHost = localHost;
	}

	public String getLocalPort() {
		return localPort;
	}

	public void setLocalPort(String localPort) {
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.LOCAL_PORT));
		cmdLineArgsVector.addElement(localPort);
		this.localPort = localPort;
	}

	public String getPlatformId() {
		return platformId;
	}

	public void setPlatformId(String platformId) {
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.PLATFORM_ID));
		cmdLineArgsVector.addElement(platformId);
		this.platformId = platformId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.NAME_KEY));
		cmdLineArgsVector.addElement(name);
		this.name = name;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		cmdLineArgsVector
				.addElement(getKeyString(BootProfileImpl.CONTAINER_NAME));
		cmdLineArgsVector.addElement(containerName);
		this.containerName = containerName;
	}

	public String getServices() {
		return services;
	}

	public void setServices(ArrayList<String> serviceList) {
		StringBuffer services = new StringBuffer();
		for (int i = 0; i < serviceList.size(); i++) {
			services.append(serviceList.get(i));
			services.append(Constant.STRING_SEMICOLON);
		}
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.SERVICES));
		cmdLineArgsVector.addElement(services.toString());
		this.services = services.toString();
	}

	public String getMtps() {
		return mtps;
	}

	public void setMtps(ArrayList<String> mtpList) {
		StringBuffer mtps = new StringBuffer();
		for (int i = 0; i < mtpList.size(); i++) {
			mtps.append(mtpList.get(i));
			mtps.append(Constant.STRING_SEMICOLON);
		}
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.MTPS));
		cmdLineArgsVector.addElement(mtps.toString());
		this.mtps = mtps.toString();
	}

	public boolean getNomtp() {
		return nomtp;
	}

	public void setNomtp(boolean nomtp) {
		if (nomtp == Boolean.TRUE.booleanValue()) {
			cmdLineArgsVector
					.addElement(getKeyString(BootProfileImpl.NOMTP_KEY));
		}
		this.nomtp = nomtp;
	}

	public boolean getBackupmain() {
		return backupmain;
	}

	public void setBackupmain(boolean backupmain) {
		if (backupmain == Boolean.TRUE.booleanValue()) {
			cmdLineArgsVector
					.addElement(getKeyString(BootProfileImpl.LOCAL_SERVICE_MANAGER));
		}
		this.backupmain = backupmain;
	}

	public String getSmhost() {
		return smhost;
	}

	public void setSmhost(String smhost) {
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.SMHOST_KEY));
		cmdLineArgsVector.addElement(smhost.toString());
		this.smhost = smhost;
	}

	public String getSmport() {
		return smport;
	}

	public void setSmport(String smport) {
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.SMPORT_KEY));
		cmdLineArgsVector.addElement(smport.toString());
		this.smport = smport;
	}

	public String getSmaddrs() {
		return smaddrs;
	}

	public void setSmaddrs(String smaddrs) {
		cmdLineArgsVector
				.addElement(getKeyString(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES));
		cmdLineArgsVector.addElement(smaddrs.toString());
		this.smaddrs = smaddrs;
	}

	public String getAclcodecs() {
		return aclcodecs;
	}

	public void setAclcodecs(ArrayList<String> aclcodecList) {
		StringBuffer aclcodecs = new StringBuffer();
		for (int i = 0; i < aclcodecList.size(); i++) {
			aclcodecs.append(aclcodecList.get(i));
			aclcodecs.append(Constant.STRING_SEMICOLON);
		}
		cmdLineArgsVector
				.addElement(getKeyString(BootProfileImpl.ACLCODEC_KEY));
		cmdLineArgsVector.addElement(aclcodecList.toString());
		this.aclcodecs = aclcodecs.toString();
	}

	public boolean getNomobility() {
		return nomobility;
	}

	public void setNomobility(boolean nomobility) {
		if (nomobility == Boolean.TRUE.booleanValue()) {
			cmdLineArgsVector
					.addElement(getKeyString(BootProfileImpl.NOMOBILITY_KEY));
		}
		this.nomobility = nomobility;
	}

	public boolean getVersion() {
		return version;
	}

	public void setVersion(boolean version) {
		if (version == Boolean.TRUE.booleanValue()) {
			cmdLineArgsVector
					.addElement(getKeyString(BootProfileImpl.VERSION_KEY));
		}
		this.version = version;
	}

	public boolean getHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		if (help == Boolean.TRUE.booleanValue()) {
			cmdLineArgsVector
					.addElement(getKeyString(BootProfileImpl.HELP_KEY));
		}		
		this.help = help;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public String getOtherProperties() {
		return otherProperties;
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
		this.otherProperties = otherProperties.toString();
	}

	public String getAgents() {
		return agents;
	}

	public void setAgents(ArrayList<String> agentList) {
		StringBuffer agents = new StringBuffer();
		for (int i = 0; i < agentList.size(); i++) {
			agents.append(agentList.get(i));
			agents.append(Constant.STRING_SEMICOLON);
		}
		cmdLineArgsVector.addElement(getKeyString(BootProfileImpl.AGENTS));
		cmdLineArgsVector.addElement(agents.toString());
		this.agents = agents.toString();
	}

	public String[] getBootPropertyArgs() {
		bootPropertyArgs = new String[cmdLineArgsVector.size()];
		cmdLineArgsVector.copyInto(bootPropertyArgs);
		return this.bootPropertyArgs;
	}

	private String getKeyString(String key) {
		StringBuffer sb = new StringBuffer(Constant.STRING_HYPHEN);
		sb.append(key);
		return sb.toString();
	}

}
