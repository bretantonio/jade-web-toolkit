package com.abreqadhabra.agent.jade.standalone.bin;

import jade.core.Runtime;
import jade.wrapper.AgentContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.abreqadhabra.agent.jade.common.constants.Constant;
import com.abreqadhabra.agent.jade.common.domain.CmdLineArgs;
import com.abreqadhabra.agent.jade.common.service.JadePlatformService;
import com.abreqadhabra.agent.jade.examples.JadeExamples;

public class SpringStandaloneApplication {

	/** The log. */
	private Log log = LogFactory.getLog(getClass());
	/** The Jade Agent Platforms service. */
	JadePlatformService jadeAgentPlatformsService;
	String[] bootPropertyArgs = null;

	private void excute() {
		ApplicationContext context = new FileSystemXmlApplicationContext(
				Constant.SPRING_STANDALONE_APPLICATION.CONTEXT_CONFIG_LOCATION);
		this.jadeAgentPlatformsService = (JadePlatformService) context
				.getBean(Constant.SPRING_STANDALONE_APPLICATION.JADE_PLATFORM_SERVICE_BEAN_NAME);
		this.jadeAgentPlatformsService.setBootPropertyArgs(bootPropertyArgs);
		this.jadeAgentPlatformsService.excutePlatform();
	}

	public static void main(String[] args) {
		SpringStandaloneApplication application = new SpringStandaloneApplication();
		application.bootPropertyArgs = application.initBootPropertiesies();
		application.excute();
	}

	private String[] initBootPropertiesies() {

		Vector<String> cmdLineArgsVector = new Vector<String>();

		bootPropertyArgs = new String[cmdLineArgsVector.size()];
		cmdLineArgsVector.copyInto(bootPropertyArgs);

		CmdLineArgs cmdLineArgs = new CmdLineArgs();
		cmdLineArgs.setContainer(false);
		// cmdLineArgs.setHost("127.0.0.1");
		// cmdLineArgs.setPort("9999");
		cmdLineArgs.setGui(true);
		// cmdLineArgs.setLocalHost("127.0.0.1");
		// cmdLineArgs.setLocalPort("9999");
		// cmdLineArgs.setPlatformId("PLATFORM_ID");
		// cmdLineArgs.setName("NAME_KEY");
		// cmdLineArgs.setContainerName("CONTAINER_NAME");
		ArrayList<String> serviceList = new ArrayList<String>();
		/* Inactive by default */
		serviceList.add("jade.core.messaging.PersistentDeliveryService");
		serviceList.add("jade.core.replication.MainReplicationService");
		serviceList.add("jade.core.replication.AddressNotificationService");
		serviceList.add("jade.core.nodeMonitoring.UDPNodeMonitoringService");
		serviceList.add("jade.core.faultRecovery.FaultRecoveryService");
		serviceList.add("jade.core.messaging.TopicManagementService");
		serviceList.add("jade.imtp.leap.nio.BEManagementService");
		cmdLineArgs.setServices(serviceList);
		ArrayList<String> mtpList = new ArrayList<String>();
		mtpList.add("jade.mtp.iiop.MessageTransportProtocol");
		// cmdLineArgs.setMtps(mtpList);
		cmdLineArgs.setNomtp(true);
		cmdLineArgs.setBackupmain(false);
		// cmdLineArgs.setSmhost("localhost");
		// cmdLineArgs.setSmport("8888");
		// cmdLineArgs.setSmaddrs("127.0.0.1");
		ArrayList<String> aclcodecList = new ArrayList<String>();
		aclcodecList.add("jamr.jadeacl.xml.XMLACLCodec");
		cmdLineArgs.setAclcodecs(aclcodecList);
		// cmdLineArgs.setNomobility(true);//재확인
		// cmdLineArgs.setVersion(true);//재확인
		// cmdLineArgs.setHelp(true);
		//cmdLineArgs.setConf("jade.properties");//재확인
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		cmdLineArgs.setOtherProperties(propertyMap);
		ArrayList<String> agentList = new ArrayList<String>();
		agentList.add("spa:jade.tools.SocketProxyAgent.SocketProxyAgent");
		cmdLineArgs.setAgents(agentList);

		String[] bootPropertiesArgs = cmdLineArgs.getBootPropertyArgs();

		if (log.isDebugEnabled()) {
			int bootPropertiesArrayLength = bootPropertiesArgs.length;

			log.debug("CmdLineArgs Length: " + bootPropertiesArrayLength);
			for (int i = 0; i < bootPropertiesArgs.length; i++) {
				log.debug(bootPropertiesArgs[i]);
			}
		}

		return bootPropertiesArgs;
	}

	@SuppressWarnings("static-access")
	private static ArrayList<String> getBootPropertyList() {
		ArrayList<String> bootPropertyList = new ArrayList<String>();
		bootPropertyList.add("-gui");
		// bootPropertyList.add("-platform");
		bootPropertyList.add("-services");
		StringBuffer services = new StringBuffer();
		services.append("jade.core.persistence.PersistenceService");
		services.append(";");
		services.append("jade.core.event.NotificationService");
		services.append(";");

		bootPropertyList.add(services.toString());
		bootPropertyList.add("-meta-db");
		bootPropertyList.add("JADE_Persistence.properties");

		bootPropertyList.add("-agents");
		JadeExamples jadeExample = JadeExamples.instance();
		bootPropertyList.add(jadeExample
				.getAgentsProperty(JadeExamples.EXAMPLE_BASE64));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_BEHAVIOURS));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_BOOKTRADING));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_CONTENT));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_HELLO));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_JESS));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_MESSAGING));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_MOBILE));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_ONTOLOGY));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_PARTY));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_PING));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_PROTOCOLS));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_THANKSAGENT));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_TOPIC));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_YELLOWPAGES));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.DEMO_MEETINGSCHEDULER));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_PERSISTENCE));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_MISCELLANEOUS));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_DSC));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.DEMO_SEMANTICS_00));
		return bootPropertyList;
	}

}
