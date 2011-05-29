package com.abreqadhabra.agent.jade.standalone.bin;

import jade.core.Runtime;
import jade.tools.persistence.PersistenceManagerGUI;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.abreqadhabra.agent.jade.common.constants.Constant;
import com.abreqadhabra.agent.jade.common.service.JadePlatformService;
import com.abreqadhabra.agent.jade.examples.JadeExamples;
import com.thoughtworks.xstream.XStream;

public class SpringStandaloneApplication {

	/** The log. */
	private Log logger = LogFactory.getLog(getClass());
	/** The Jade Agent Platforms service. */
	JadePlatformService jadeAgentPlatformsService;
	Runtime jadeRuntime = null;
	AgentContainer agentContainer = null;

	public SpringStandaloneApplication() {
		initController();
	}

	private void initController() {
		ApplicationContext context = new FileSystemXmlApplicationContext(
				Constant.SPRING_STANDALONE_APPLICATION.CONTEXT_CONFIG_LOCATION);
		this.jadeAgentPlatformsService = (JadePlatformService) context
				.getBean(Constant.SPRING_STANDALONE_APPLICATION.JADE_PLATFORM_SERVICE_BEAN_NAME);
	}

	public static void main(String[] args) {
		SpringStandaloneApplication application = new SpringStandaloneApplication();
		application.initController();
		try {
			application.startContainer();
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
