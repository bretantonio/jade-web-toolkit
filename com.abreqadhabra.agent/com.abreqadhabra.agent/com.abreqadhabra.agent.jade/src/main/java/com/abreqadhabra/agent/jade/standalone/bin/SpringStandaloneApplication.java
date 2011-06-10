package com.abreqadhabra.agent.jade.standalone.bin;

import jade.BootProfileImpl;
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
import com.abreqadhabra.agent.jade.common.util.BootPropertiesUtil;
import com.abreqadhabra.agent.jade.examples.JadeExamples;

public class SpringStandaloneApplication {

	/** The log. */
	private Log logger = LogFactory.getLog(getClass());
	/** The Jade Agent Platforms service. */
	JadePlatformService jadeAgentPlatformsService;
	Runtime jadeRuntime = null;
	AgentContainer agentContainer = null;
	
	String[] bootPropertyArgs = null;
	
	
	public void setBootPropertyArgs(String[] bootPropertyArgs) {
		this.bootPropertyArgs = bootPropertyArgs;
	}

	private void excute() {
		ApplicationContext context = new FileSystemXmlApplicationContext(
				Constant.SPRING_STANDALONE_APPLICATION.CONTEXT_CONFIG_LOCATION);
		this.jadeAgentPlatformsService = (JadePlatformService) context
				.getBean(Constant.SPRING_STANDALONE_APPLICATION.JADE_PLATFORM_SERVICE_BEAN_NAME);
		this.jadeAgentPlatformsService.setBootPropertyArgs(bootPropertyArgs);
		this.jadeAgentPlatformsService.excute();
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
		//cmdLineArgs.setHost("127.0.0.1");
		//cmdLineArgs.setPort("9999");
		cmdLineArgs.setGui(true);
		//cmdLineArgs.setLocalHost("127.0.0.1");
		//cmdLineArgs.setLocalPort("9999");
		//cmdLineArgs.setPlatformId("PLATFORM_ID");
		cmdLineArgs.setName("NAME_KEY");
		
		HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
		/* -container */
		propertiesMap.put(BootProfileImpl.CONTAINER_KEY, false);
		/* -host */
		//propertiesMap.put(BootProfileImpl.MAIN_HOST, "<127.0.0.1>");
//		/* -port */
		propertiesMap.put(BootProfileImpl.MAIN_PORT, "9999");
//		/* -gui */
//		propertiesMap.put(BootProfileImpl.GUI_KEY, true);
//		/* -local-host */
//		propertiesMap.put(BootProfileImpl.LOCAL_HOST, true);
//		/* -local-port */
//		propertiesMap.put(BootProfileImpl.LOCAL_PORT, true);
//		/* -platform-id */
//		propertiesMap.put(BootProfileImpl.PLATFORM_ID, "PLATFORM_ID");
//		/* -name */
//		propertiesMap.put(BootProfileImpl.NAME_KEY, "NAME_KEY");
//		/* -container-name */
//		propertiesMap.put(BootProfileImpl.CONTAINER_NAME, "CONTAINER_NAME");
//		/* -services */
//		ArrayList<String> serviceList = new ArrayList<String>();
//		if (serviceList.size() != 0) {
//			propertiesMap.put(BootProfileImpl.SERVICES, serviceList);
//		}
//		/* -mtps */
//		ArrayList<String> mtpList = new ArrayList<String>();
//		if (mtpList.size() != 0) {
//			propertiesMap.put(BootProfileImpl.MTPS, mtpList);
//		}
//		/* -nomtp */
//		propertiesMap.put(BootProfileImpl.NOMTP_KEY, true);
//		/* -backupmain */
//		propertiesMap.put(BootProfileImpl.LOCAL_SERVICE_MANAGER, true);
//		/* -smhost */
//		propertiesMap.put(BootProfileImpl.SMHOST_KEY, true);
//		/* -smport */
//		propertiesMap.put(BootProfileImpl.SMPORT_KEY, true);
//		/* -smaddrs */
//		propertiesMap.put(BootProfileImpl.REMOTE_SERVICE_MANAGER_ADDRESSES,
//				true);
//		/* -aclcodecs */
//		ArrayList<String> aclcodecList = new ArrayList<String>();
//		if (aclcodecList.size() != 0) {
//			propertiesMap.put(BootProfileImpl.ACLCODEC_KEY, aclcodecList);
//		}
//		/* -nomobility */
//		propertiesMap.put(BootProfileImpl.NOMOBILITY_KEY, true);
//		/* -version */
//		propertiesMap.put(BootProfileImpl.VERSION_KEY, true);
//		/* -help */
//		propertiesMap.put(BootProfileImpl.HELP_KEY, true);
//		/* -conf */
//		propertiesMap.put(BootProfileImpl.CONF_KEY, "CONF_KEY");
//		/* -<property-name><property-value> */
//		HashMap<String, String> propertyMap = new HashMap<String, String>();
//		if (mtpList.size() != 0) {
//			propertiesMap.put(BootProperties.OTHER_PROPERTIES, mtpList);
//		}
//		/* -agents */
//		ArrayList<String> agentList = new ArrayList<String>();
//		if (agentList.size() != 0) {
//			propertiesMap.put(BootProperties.AGENTS, agentList);
//		}

		System.out.println(propertiesMap);

		String[] bootPropertiesArgs = cmdLineArgs.getBootPropertyArgs();
		
		int bootPropertiesArrayLength = bootPropertiesArgs.length;
		System.out.println("bootPropertiesArgs Length: "
				+ bootPropertiesArrayLength);
		for (int i = 0; i < bootPropertiesArgs.length; i++) {
			System.out.println(bootPropertiesArgs[i]);
		}
		
		return bootPropertiesArgs;
	}

	/*
	private void startContainer() throws ControllerException {
		ArrayList<String> bootPropertyList = getBootPropertyList();

		logger.info(new XStream().toXML(bootPropertyList));

		this.jadeAgentPlatformsService.startContainer(bootPropertyList);
		logger.info(new XStream().toXML(this.jadeAgentPlatformsService
				.getBootPropertyArgs()));

		jadeRuntime = this.jadeAgentPlatformsService.getJadeRuntime();
		agentContainer = (AgentContainer) this.jadeAgentPlatformsService
				.getAgentContainer();
		logger.info(new XStream().toXML(this.jadeAgentPlatformsService
				.getContainerInfoMap()));

		PlatformController platformController = agentContainer
				.getPlatformController();
		AgentController agentController = platformController.getAgent("ams");
		logger.info(new XStream().toXML(agentController.getName()));
		logger.info(new XStream().toXML(agentController.getState().getName()));
		
		new PersistenceManagerGUI().showCorrect();
	}
*/
	@SuppressWarnings("static-access")
	private static ArrayList<String> getBootPropertyList() {
		ArrayList<String> bootPropertyList = new ArrayList<String>();
		bootPropertyList.add("-gui");
		//bootPropertyList.add("-platform");
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
		 bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_BASE64));
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
		//bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_MISCELLANEOUS));
		// bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.EXAMPLE_DSC));
		//bootPropertyList.add(jadeExample.getAgentsProperty(JadeExamples.DEMO_SEMANTICS_00));
		return bootPropertyList;
	}

}
