package com.abreqadhabra.agent.jade.standalone.bin;

import java.util.ArrayList;
import java.util.HashMap;

import com.abreqadhabra.agent.jade.common.domain.JadeBootProperties;

public class TestApplication {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestApplication t = new TestApplication();
		t.setJadeBootProperties();

	}

	private void setJadeBootProperties() {
		JadeBootProperties bootProperties = new JadeBootProperties();

		// -container
		bootProperties.isContainer(true);
		// -host
		bootProperties.setHost("host");
		// -port
		bootProperties.setPort("port");
		// -gui
		bootProperties.isGui(true);
		// -local-host
		bootProperties.setLocalHost("local-host");
		// -local-port
		bootProperties.setLocalPort("local-port");
		// -platform-id
		bootProperties.setPlatformId("platform-id");
		// -name
		bootProperties.setName("name");
		// -container-name
		bootProperties.setContainerName("container-name");

		//-services
		ArrayList<String> serviceList = new ArrayList<String>();
		//Inactive by default
		serviceList.add("jade.core.messaging.PersistentDeliveryService");
		serviceList.add("jade.core.replication.MainReplicationService");
		serviceList.add("jade.core.replication.AddressNotificationService");
		serviceList.add("Jade.core.nodeMonitoring.UDPNodeMonitoringService");
		serviceList.add("jade.core.faultRecovery.FaultRecoveryService");
		serviceList.add("jade.core.messaging.TopicManagementService");
		serviceList.add("jade.imtp.leap.nio.BEManagementService");
		bootProperties.setServices(serviceList);

		//-mtps
		// mtp-specifier = [in-address:]<mtp-class>[(comma-separated args)]
		ArrayList<String> mtpList = new ArrayList<String>();
		bootProperties.setMtps(mtpList);
		//-nomtp
		bootProperties.isNomtp(true);
		//-backupmain
		bootProperties.isBackupmain(true);
		//-smhost
		bootProperties.isSmhost(true);
		//-smport
		bootProperties.isSmport(true);
		//-smaddrs
		bootProperties.isSmaddrs(true);
		//-aclcodecs
		ArrayList<String> aclcodecList = new ArrayList<String>();
		bootProperties.setAclcodecs(aclcodecList);
		//-nomobility
		bootProperties.isNomobility(true);
		//-version
		bootProperties.isVersion(true);
		//-help
		bootProperties.isHelp(true);
		//-conf
		bootProperties.setConf("conf");
		//-<property-name> <property-value>
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("property-name1", "property-value1");
		propertyMap.put("property-name2", "property-value2");
		bootProperties.setOtherProperties(propertyMap);
		//-agents <semicolon separated list of agent-specifiers> where agent-specifier = <agent-name>:<agent-class>[(comma separated args)]
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
