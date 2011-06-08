/*
 *
 */
package com.abreqadhabra.agent.jade.standalone.bin;

import jade.BootProfileImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.abreqadhabra.agent.jade.common.constants.Constant;
import com.abreqadhabra.agent.jade.common.domain.BootProperties;

public class TestApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestApplication t = new TestApplication();
		HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
		/* -container */
		propertiesMap.put(BootProfileImpl.CONTAINER_KEY, true);
		/* -host */
		propertiesMap.put(BootProfileImpl.MAIN_HOST, "127.0.0.1");
		/* -port */
		propertiesMap.put(BootProfileImpl.MAIN_PORT, "9999");
		/* -gui */
		propertiesMap.put(BootProfileImpl.GUI_KEY, true);
		/* -local-host */
		propertiesMap.put(BootProfileImpl.LOCAL_HOST, true);
		/* -local-port */
		propertiesMap.put(BootProfileImpl.LOCAL_PORT, true);
		/* -platform-id */
		propertiesMap.put(BootProfileImpl.PLATFORM_ID, "PLATFORM_ID");
		/* -name */
		propertiesMap.put(BootProfileImpl.NAME_KEY, "NAME_KEY");
		/* -container-name */
		propertiesMap.put(BootProfileImpl.CONTAINER_NAME, "CONTAINER_NAME");
		/* -services */
		ArrayList<String> serviceList = new ArrayList<String>();
		if (serviceList.size() != 0) {
			propertiesMap.put(BootProfileImpl.SERVICES, serviceList);
		}
		/* -mtps */
		ArrayList<String> mtpList  = new ArrayList<String>();
		if (mtpList.size() != 0) {
			propertiesMap.put(BootProfileImpl.MTPS, mtpList);
		}
		/* -nomtp */
		propertiesMap.put(BootProfileImpl.NOMTP_KEY, true);
		/* -backupmain */
		propertiesMap.put(BootProfileImpl.LOCAL_SERVICE_MANAGER, true);
		/* -smhost */
		propertiesMap.put(BootProfileImpl.SMHOST_KEY, true);
		/* -smport */
		propertiesMap.put(BootProfileImpl.SMPORT_KEY, true);
		/* -smaddrs */
		propertiesMap.put(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES, true);
		/* -aclcodecs */
		ArrayList<String> aclcodecList  = new ArrayList<String>();
		if (aclcodecList.size() != 0) {
			propertiesMap.put(BootProfileImpl.ACLCODEC_KEY, aclcodecList);
		}
		/* -nomobility */
		propertiesMap.put(BootProfileImpl.NOMOBILITY_KEY, true);
		/* -version */
		propertiesMap.put(BootProfileImpl.VERSION_KEY, true);
		/* -help */
		propertiesMap.put(BootProfileImpl.HELP_KEY, true);
		/* -conf */
		propertiesMap.put(BootProfileImpl.CONF_KEY, "CONF_KEY");
		/* -<property-name><property-value> */
		HashMap<String, String> propertyMap = new HashMap<String, String> ();
		if (mtpList.size() != 0) {
			propertiesMap.put(BootProperties.OTHER_PROPERTIES, mtpList);
		}
		/* -agents */
		ArrayList<String> agentList  = new ArrayList<String>();
		if (agentList.size() != 0) {
			propertiesMap.put(BootProperties.AGENTS, agentList);
		}

		System.out.println(propertiesMap);


		String[] bootPropertiesArray = t.getBootPropertyArgs(propertiesMap);
		int bootPropertiesArrayLength = bootPropertiesArray.length;
		System.out.println("JadeBootProperties Length: "
				+ bootPropertiesArrayLength);
		for (int i = 0; i < bootPropertiesArray.length; i++) {
			System.out.println(bootPropertiesArray[i]);
		}

	}

	@SuppressWarnings("unchecked")
	private String[] getBootPropertyArgs(HashMap<String, Object> propertiesMap) {
		BootProperties bootProperties = new BootProperties();
		/* -container */
		if (propertiesMap.containsKey(BootProfileImpl.CONTAINER_KEY)) {
			bootProperties.isContainer((Boolean) propertiesMap
					.get(BootProfileImpl.CONTAINER_KEY));
		}
		/* -host */
		if (propertiesMap.containsKey(BootProfileImpl.MAIN_HOST)) {
			bootProperties.setHost((String)propertiesMap
					.get(BootProfileImpl.MAIN_HOST));
		}
		/* -port */
		if (propertiesMap.containsKey(BootProfileImpl.MAIN_PORT)) {
			bootProperties.setPort((String)propertiesMap
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
			bootProperties.setPlatformId((String)propertiesMap
					.get(BootProfileImpl.PLATFORM_ID));
		}
		/* -name */
		if (propertiesMap.containsKey(BootProfileImpl.NAME_KEY)) {
			bootProperties.setName((String)propertiesMap
					.get(BootProfileImpl.NAME_KEY));
		}
		/* -container-name */
		if (propertiesMap.containsKey(BootProfileImpl.CONTAINER_NAME)) {
			bootProperties.setContainerName((String)propertiesMap
					.get(BootProfileImpl.CONTAINER_NAME));
		}
		/* -services */
		if (propertiesMap.containsKey(BootProfileImpl.SERVICES)) {
			bootProperties.setServices((ArrayList<String>)propertiesMap
					.get(BootProfileImpl.SERVICES));
		}
		/* -mtps */
		if (propertiesMap.containsKey(BootProfileImpl.MTPS)) {
			bootProperties.setMtps((ArrayList<String>)propertiesMap
					.get(BootProfileImpl.MTPS));
		}
		/* -nomtp */
		if (propertiesMap.containsKey(BootProfileImpl.NOMTP_KEY)) {
			bootProperties.isNomtp((Boolean)propertiesMap
					.get(BootProfileImpl.NOMTP_KEY));
		}
		/* -backupmain */
		if (propertiesMap.containsKey(BootProfileImpl.LOCAL_SERVICE_MANAGER)) {
			bootProperties.isBackupmain((Boolean)propertiesMap
					.get(BootProfileImpl.LOCAL_SERVICE_MANAGER));
		}
		/* -smhost */
		if (propertiesMap.containsKey(BootProfileImpl.SMHOST_KEY)) {
			bootProperties.isSmhost((Boolean)propertiesMap
					.get(BootProfileImpl.SMHOST_KEY));
		}
		/* -smport */
		if (propertiesMap.containsKey(BootProfileImpl.SMPORT_KEY)) {
			bootProperties.isSmport((Boolean)propertiesMap
					.get(BootProfileImpl.SMPORT_KEY));
		}
		/* -smaddrs */
		if (propertiesMap.containsKey(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES)) {
			bootProperties.isSmaddrs((Boolean)propertiesMap
					.get(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES));
		}
		/* -aclcodecs */
		if (propertiesMap.containsKey(BootProfileImpl.ACLCODEC_KEY)) {
			bootProperties.setAclcodecs((ArrayList<String>)propertiesMap
					.get(BootProfileImpl.ACLCODEC_KEY));
		}
		/* -nomobility */
		if (propertiesMap.containsKey(BootProfileImpl.NOMOBILITY_KEY)) {
			bootProperties.isNomobility((Boolean)propertiesMap
					.get(BootProfileImpl.NOMOBILITY_KEY));
		}
		/* -version */
		if (propertiesMap.containsKey(BootProfileImpl.VERSION_KEY)) {
			bootProperties.isVersion((Boolean)propertiesMap
					.get(BootProfileImpl.VERSION_KEY));
		}
		/* -help */
		if (propertiesMap.containsKey(BootProfileImpl.HELP_KEY)) {
			bootProperties.isHelp((Boolean)propertiesMap
					.get(BootProfileImpl.HELP_KEY));
		}
		/* -conf */
		if (propertiesMap.containsKey(BootProfileImpl.CONF_KEY)) {
			bootProperties.setHost((String)propertiesMap
					.get(BootProfileImpl.CONF_KEY));
		}
		/* -<property-name><property-value> */
		if (propertiesMap.containsKey(BootProperties.OTHER_PROPERTIES)) {
			bootProperties.setOtherProperties((HashMap<String, String>)propertiesMap
					.get(BootProperties.OTHER_PROPERTIES));
		}
		/* -agents */
		if (propertiesMap.containsKey(BootProperties.AGENTS)) {
			bootProperties.setAgents((ArrayList<String>)propertiesMap
					.get(BootProperties.AGENTS));
		}
		return bootProperties.getBootProperties();

	}

	/**
	 * Sets the jade boot properties.
	 */
	private void setJadeBootProperties(HashMap<String, Object> propertiesMap) {
		BootProperties bootProperties = new BootProperties();

		/* -container */
		if (propertiesMap.containsKey(BootProfileImpl.CONTAINER_KEY)) {
			bootProperties.isContainer((Boolean) propertiesMap
					.get(BootProfileImpl.CONTAINER_KEY));
		}
		/*-host*/
		bootProperties.setHost("host");
		/*-port*/
		bootProperties.setPort("port");
		/*-gui*/
		bootProperties.isGui(true);
		/*-local-host*/
		bootProperties.isLocalHost(false);
		/*-local-port*/
		bootProperties.isLocalPort(false);
		/*-platform-id*/
		bootProperties.setPlatformId("platform-id");
		/*-name*/
		bootProperties.setName("name");
		/*-container-name*/
		bootProperties.setContainerName("container-name");
		/*-services*/
		ArrayList<String> serviceList = new ArrayList<String>();
		/* Inactive by default */
		serviceList.add("jade.core.messaging.PersistentDeliveryService");
		serviceList.add("jade.core.replication.MainReplicationService");
		serviceList.add("jade.core.replication.AddressNotificationService");
		serviceList.add("Jade.core.nodeMonitoring.UDPNodeMonitoringService");
		serviceList.add("jade.core.faultRecovery.FaultRecoveryService");
		serviceList.add("jade.core.messaging.TopicManagementService");
		serviceList.add("jade.imtp.leap.nio.BEManagementService");
		bootProperties.setServices(serviceList);

		/*
		 * -mtps mtp-specifier = [in-address:]<mtp-class>[(comma-separated
		 * args)]
		 */
		ArrayList<String> mtpList = new ArrayList<String>();
		bootProperties.setMtps(mtpList);
		/* -nomtp */
		bootProperties.isNomtp(true);
		/* -backupmain */
		bootProperties.isBackupmain(true);
		/*-smhost*/
		bootProperties.isSmhost(true);
		/*-smport*/
		bootProperties.isSmport(true);
		/*-smaddrs*/
		bootProperties.isSmaddrs(true);
		/*-aclcodecs*/
		ArrayList<String> aclcodecList = new ArrayList<String>();
		bootProperties.setAclcodecs(aclcodecList);
		/*-nomobility*/
		bootProperties.isNomobility(true);
		/*-version*/
		bootProperties.isVersion(true);
		/*-help*/
		bootProperties.isHelp(true);
		/*-conf*/
		bootProperties.setConf("conf");
		/*-<property-name> <property-value>*/
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("property-name1", "property-value1");
		propertyMap.put("property-name2", "property-value2");
		bootProperties.setOtherProperties(propertyMap);
		/*
		 * -agents <semicolon separated list of agent-specifiers> where
		 * agent-specifier = <agent-name>:<agent-class>[(comma separated args)]
		 */
		ArrayList<String> agentList = new ArrayList<String>();
		bootProperties.setAgents(agentList);

		String[] bootPropertiesArray = bootProperties.getBootProperties();
		int bootPropertiesArrayLength = bootPropertiesArray.length;
		System.out.println("JadeBootProperties Length: "
				+ bootPropertiesArrayLength);
		for (int i = 0; i < bootPropertiesArray.length; i++)
			System.out.println(bootPropertiesArray[i]);
	}

}
