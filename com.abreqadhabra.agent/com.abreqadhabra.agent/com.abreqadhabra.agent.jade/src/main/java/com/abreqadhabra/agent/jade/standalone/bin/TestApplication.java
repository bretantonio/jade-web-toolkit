/*
 * 
 */
package com.abreqadhabra.agent.jade.standalone.bin;

import jade.BootProfileImpl;

import java.util.ArrayList;
import java.util.HashMap;

import com.abreqadhabra.agent.jade.common.domain.JadeBootProperties;

public class TestApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestApplication t = new TestApplication();
		HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
		/* -container */
		propertiesMap.put(BootProfileImpl.CONTAINER_KEY, false);
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
		propertiesMap.put(BootProfileImpl.CONTAINER_KEY, "CONTAINER_KEY");
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
		propertiesMap.put(BootProfileImpl.NOMTP_KEY, false);

		/* -backupmain */
		/* -smhost */
		/* -smport */
		/* -smaddrs */
		/* -aclcodecs */
		/* -nomobility */
		/* -version */
		/* -help */
		/* -conf */
		/* -<property-name><property-value> */
		/* -agents */
		
		String[] bootPropertiesArray = t.getBootPropertyArgs(propertiesMap);
		int bootPropertiesArrayLength = bootPropertiesArray.length;
		System.out.println("JadeBootProperties Length: "
				+ bootPropertiesArrayLength);
		for (int i = 0; i < bootPropertiesArray.length; i++) {
			System.out.println(bootPropertiesArray[i]);
		}

	}

	private String[] getBootPropertyArgs(HashMap<String, Object> propertiesMap) {
		JadeBootProperties bootProperties = new JadeBootProperties();
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
		/* -local-port */
		/* -platform-id */
		/* -name */
		/* -container-name */
		/* -services */
		/* -mtps */
		/* -nomtp */
		/* -backupmain */
		/* -smhost */
		/* -smport */
		/* -smaddrs */
		/* -aclcodecs */
		/* -nomobility */
		/* -version */
		/* -help */
		/* -conf */
		/* -<property-name><property-value> */
		/* -agents */
		return bootProperties.getBootProperties();

	}

	/**
	 * Sets the jade boot properties.
	 */
	private void setJadeBootProperties(HashMap<String, Object> propertiesMap) {
		JadeBootProperties bootProperties = new JadeBootProperties();

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
		bootProperties.setLocalHost("local-host");
		/*-local-port*/
		bootProperties.setLocalPort("local-port");
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
