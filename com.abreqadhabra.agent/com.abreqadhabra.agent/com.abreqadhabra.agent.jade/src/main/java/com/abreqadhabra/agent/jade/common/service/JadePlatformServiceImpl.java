package com.abreqadhabra.agent.jade.common.service;

import jade.Boot;
import jade.BootProfileImpl;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.abreqadhabra.agent.jade.common.domain.BootProperties;
import com.abreqadhabra.agent.jade.common.util.BootPropertiesUtil;
import com.thoughtworks.xstream.XStream;

public class JadePlatformServiceImpl implements JadePlatformService {
	/** The log. */
	private Log logger = LogFactory.getLog(getClass());
	XStream xStream = new XStream();

	private String[] bootPropertyArgs = null;
	Runtime jadeRuntime = null;
	AgentContainer agentContainer = null;

	public String[] getBootPropertyArgs() {
		return bootPropertyArgs;
	}

	public Runtime getJadeRuntime() {
		return jadeRuntime;
	}

	public AgentContainer getAgentContainer() {
		return agentContainer;
	}
	
	public void setBootPropertyArgs(String[] bootPropertyArgs) {
		this.bootPropertyArgs = bootPropertyArgs;
	}

	@SuppressWarnings("static-access")
	public HashMap<String, HashMap<String, String>> getContainerInfoMap()
			throws ControllerException {
		HashMap<String, String> jadeRuntimeInfoMap = new HashMap<String, String>();
		jadeRuntimeInfoMap.put("CopyrightNotice",
				jadeRuntime.getCopyrightNotice());
		jadeRuntimeInfoMap.put("Date", jadeRuntime.getDate());
		jadeRuntimeInfoMap.put("Revision", jadeRuntime.getRevision());
		jadeRuntimeInfoMap.put("Version", jadeRuntime.getVersion());
		jadeRuntimeInfoMap.put("VersionInfo", jadeRuntime.getVersionInfo());
		jadeRuntimeInfoMap.put("Name", agentContainer.getName());
		jadeRuntimeInfoMap
				.put("PlatformName", agentContainer.getPlatformName());
		jadeRuntimeInfoMap.put("State", agentContainer.getState().getName());

		String containerName = agentContainer.getContainerName();
		HashMap<String, HashMap<String, String>> jadeContainerInfoMap = new HashMap<String, HashMap<String, String>>();
		jadeContainerInfoMap.put(containerName, jadeRuntimeInfoMap);

		return jadeContainerInfoMap;
	}

	/*
	 * public void startContainer(ArrayList<String> bootPropertyList) { try {
	 * bootPropertyArgs = BootProfileUtil
	 * .createBootPropertyArgs(bootPropertyList); logger.info(bootPropertyArgs);
	 * Properties properties = Boot.parseCmdLineArgs(bootPropertyArgs);
	 * ProfileImpl bootProfile = new ProfileImpl(properties);
	 * 
	 * 
	 * // Start a new JADE runtime system jadeRuntime = Runtime.instance();
	 * jadeRuntime.setCloseVM(true); // #PJAVA_EXCLUDE_BEGIN // Check whether
	 * this is the Main Container or a peripheral // container boolean
	 * isMainContainer = bootProfile.getBooleanProperty(Profile.MAIN, true);
	 * 
	 * if (isMainContainer) { logger.info("Creating Main-Container...");
	 * 
	 * agentContainer = jadeRuntime .createMainContainer(bootProfile); } else {
	 * agentContainer = jadeRuntime .createAgentContainer(bootProfile); }
	 * 
	 * } catch (IOException e) { logger.error(e); } }
	 */
	public void excute() {
		Properties properties = Boot.parseCmdLineArgs(this.bootPropertyArgs);
		ProfileImpl bootProfile = new ProfileImpl(properties);

		// Start a new JADE runtime system
		jadeRuntime = Runtime.instance();
		jadeRuntime.setCloseVM(true);
		// #PJAVA_EXCLUDE_BEGIN
		// Check whether this is the Main Container or a peripheral
		// container
		boolean isMainContainer = bootProfile.getBooleanProperty(Profile.MAIN,
				true);

		if (isMainContainer) {
			logger.info("Creating Main-Container...");

			agentContainer = jadeRuntime.createMainContainer(bootProfile);
		} else {
			agentContainer = jadeRuntime.createAgentContainer(bootProfile);
		}
	}

	
}
