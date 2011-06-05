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
		bootProperties.getMainOptions().isContainer(true);
		// -host
		bootProperties.getMainOptions().setHost("host");
		// -port
		bootProperties.getMainOptions().setPort("port");
		// -gui
		bootProperties.getMainOptions().isGui(true);
		// -local-host
		bootProperties.getMainOptions().setLocalHost("local-host");
		// -local-port
		bootProperties.getMainOptions().setLocalPort("local-port");
		// -platform-id
		bootProperties.getMainOptions().setPlatformId("platform-id");
		// -name
		bootProperties.getMainOptions().setName("name");
		// -container-name
		bootProperties.getMainOptions().setContainerName("container-name");

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
		bootProperties.getMainOptions().setServices(serviceList);

		//-mtps
		// mtp-specifier = [in-address:]<mtp-class>[(comma-separated args)]
		ArrayList<String> mtpList = new ArrayList<String>();
		bootProperties.getMainOptions().setMtps(mtpList);
		//-nomtp
		bootProperties.getMainOptions().isNomtp(true);
		//-backupmain
		bootProperties.getMainOptions().isBackupmain(true);
		//-smhost
		bootProperties.getMainOptions().isSmhost(true);
		//-smport
		bootProperties.getMainOptions().isSmport(true);
		//-smaddrs
		bootProperties.getMainOptions().isSmaddrs(true);
		//-aclcodecs
		ArrayList<String> aclcodecList = new ArrayList<String>();
		bootProperties.getMainOptions().setAclcodecs(aclcodecList);
		//-nomobility
		bootProperties.getMainOptions().isNomobility(true);
		//-version
		bootProperties.getMainOptions().isVersion(true);
		//-help
		bootProperties.getMainOptions().isHelp(true);
		//-conf
		bootProperties.getMainOptions().setConf("conf");
		//-<property-name> <property-value>
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("property-name1", "property-value1");
		propertyMap.put("property-name2", "property-value2");
		bootProperties.getMainOptions().setOtherProperties(propertyMap);

		String[] bootPropertiesArray = bootProperties.getBootProperties();
		int bootPropertiesArrayLength = bootPropertiesArray.length;
		System.out.println("JadeBootProperties Length: "
				+ bootPropertiesArrayLength);
		for (int i = 0; i < bootPropertiesArray.length; i++)
			System.out.println(bootPropertiesArray[i]);
	}

}
