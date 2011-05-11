package com.abreqadhabra.agent.jade.examples;

import java.util.HashMap;

import jsademos.booktrading.IsbnHolderAgent;

import com.abreqadhabra.agent.jade.common.constants.Constant;

import demo.MeetingScheduler.MeetingSchedulerAgent;
import examples.Base64.ObjectReaderAgent;
import examples.Base64.ObjectWriterAgent;
import examples.PingAgent.PingAgent;
import examples.behaviours.FSMAgent;
import examples.behaviours.SimpleAgent;
import examples.behaviours.TimeAgent;
import examples.bookTrading.BookBuyerAgent;
import examples.bookTrading.BookSellerAgent;
import examples.content.CDTrader;
import examples.content.Receiver;
import examples.content.Sender;
import examples.hello.HelloWorldAgent;
import examples.jess.JessAgent;
import examples.meeting_dsc_example.MeetingBroker;
import examples.meeting_dsc_example.MeetingParticipant;
import examples.meeting_dsc_example.MeetingRequester;
import examples.messaging.BlockingReceiveAgent;
import examples.messaging.CustomTemplateAgent;
import examples.misc.FSMMessageExchange.FSMExecutor;
import examples.mobile.MobileAgent;
import examples.ontology.EngagerAgent;
import examples.ontology.RequesterAgent;
import examples.party.GuestAgent;
import examples.party.HostAgent;
import examples.persistence.PersistentAgent;
import examples.protocols.BrokerAgent;
import examples.protocols.ContractNetInitiatorAgent;
import examples.protocols.ContractNetResponderAgent;
import examples.protocols.FIPARequestInitiatorAgent;
import examples.protocols.FIPARequestResponderAgent;
import examples.simple_dsc_example.ExampleDSCAgent;
import examples.thanksAgent.ThanksAgent;
import examples.topic.TopicMessageReceiverAgent;
import examples.topic.TopicMessageSenderAgent;
import examples.yellowPages.DFRegisterAgent;
import examples.yellowPages.DFSearchAgent;
import examples.yellowPages.DFSubscribeAgent;


public class JadeExamples {

    private static final int INT_CLASS_NAME_SIMPLE = 0;
    private static final int INT_CLASS_NAME_FULL = 1;

    public static final String EXAMPLE_BASE64 = "base64";
    public static final String EXAMPLE_BEHAVIOURS = "behaviours";
    public static final String EXAMPLE_BEHAVIOURS_PARAM = "The-Lord-of-the-rings";
    public static final String EXAMPLE_BOOKTRADING = "bookTrading";
    public static final String EXAMPLE_CONTENT = "content";
    public static final String EXAMPLE_HELLO = "hello";
    public static final String EXAMPLE_JESS = "jess";
    public static final String EXAMPLE_MESSAGING = "messaging";
    public static final String EXAMPLE_MOBILE = "mobile";
    public static final String EXAMPLE_ONTOLOGY = "ontology";
    public static final String EXAMPLE_PARTY = "party";
    public static final String EXAMPLE_PING = "ping";
    public static final String EXAMPLE_PROTOCOLS = "protocols";
    public static final String EXAMPLE_THANKSAGENT = "thanksAgent";
    public static final String EXAMPLE_TOPIC = "topic";
    public static final String EXAMPLE_YELLOWPAGES = "yellowPages";
    public static final String DEMO_MEETINGSCHEDULER = "MeetingScheduler";
    public static final String EXAMPLE_PERSISTENCE = "persistence";
    public static final String EXAMPLE_MISCELLANEOUS = "misc";
    public static final String EXAMPLE_DSC = "dsc";
    public static final String DEMO_SEMANTICS_00 = "semantics_00";
    public static final String DEMO_SEMANTICS_01 = "semantics_01";
    public static final String DEMO_SEMANTICS_02 = "semantics_02";
    public static final String DEMO_SEMANTICS_03 = "semantics_03";
    private static final String DEMO_SEMANTICS_00_PARAM_00 = "isbn.txt";
    private static final String DEMO_SEMANTICS_00_PARAM_01 = "seller1.txt";
    private static final String DEMO_SEMANTICS_00_PARAM_02 = "seller2.txt";

    public static HashMap<String, String> exampleMap = null;

    public JadeExamples() {
	init();
    }

    @SuppressWarnings("static-access")
    private void init() {
	exampleMap = new HashMap<String, String>();
	initExampleAgentList(this.EXAMPLE_BASE64);
	initExampleAgentList(this.EXAMPLE_BEHAVIOURS);
	initExampleAgentList(this.EXAMPLE_BOOKTRADING);
	initExampleAgentList(this.EXAMPLE_CONTENT);
	initExampleAgentList(this.EXAMPLE_HELLO);
	initExampleAgentList(this.EXAMPLE_JESS);
	initExampleAgentList(this.EXAMPLE_MESSAGING);
	initExampleAgentList(this.EXAMPLE_MOBILE);
	initExampleAgentList(this.EXAMPLE_ONTOLOGY);
	initExampleAgentList(this.EXAMPLE_PARTY);
	initExampleAgentList(this.EXAMPLE_PING);
	initExampleAgentList(this.EXAMPLE_PROTOCOLS);
	initExampleAgentList(this.EXAMPLE_THANKSAGENT);
	initExampleAgentList(this.EXAMPLE_TOPIC);
	initExampleAgentList(this.EXAMPLE_YELLOWPAGES);
	initExampleAgentList(this.DEMO_MEETINGSCHEDULER);
	initExampleAgentList(this.EXAMPLE_PERSISTENCE);
	initExampleAgentList(this.EXAMPLE_MISCELLANEOUS);
	initExampleAgentList(this.EXAMPLE_DSC);
	initExampleAgentList(this.DEMO_SEMANTICS_00);
	initExampleAgentList(this.DEMO_SEMANTICS_01);
	initExampleAgentList(this.DEMO_SEMANTICS_02);
	initExampleAgentList(this.DEMO_SEMANTICS_03);
    }

    @SuppressWarnings("static-access")
    private void initExampleAgentList(String exampleName) {
	StringBuffer agents = new StringBuffer();
	String agentName = null;
	String param = null;
	String agentClassName = null;
	if (this.EXAMPLE_BASE64.equals(exampleName)) {
	    agentName = getClassName(new ObjectWriterAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new ObjectWriterAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new ObjectReaderAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new ObjectReaderAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_BEHAVIOURS.equals(exampleName)) {
	    agentName = getClassName(new SimpleAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new SimpleAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new TimeAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new TimeAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new FSMAgent(), this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new FSMAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_BOOKTRADING.equals(exampleName)) {
	    agentName = getClassName(new BookBuyerAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new BookBuyerAgent(),
		    this.INT_CLASS_NAME_FULL);
	    param = Constant.STRING_OPEN_PARENTHESIS
		    + this.EXAMPLE_BEHAVIOURS_PARAM
		    + Constant.STRING_CLOSE_PARENTHESIS;
	    agentClassName = agentClassName + param;
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new BookSellerAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new BookSellerAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_CONTENT.equals(exampleName)) {
	    agentName = getClassName(new CDTrader(), this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new CDTrader(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new Receiver(), this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new Receiver(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new Sender(), this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new Sender(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_HELLO.equals(exampleName)) {
	    agentName = getClassName(new HelloWorldAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new HelloWorldAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_JESS.equals(exampleName)) {
	    agentName = getClassName(new JessAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new JessAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_MESSAGING.equals(exampleName)) {
	    agentName = getClassName(new BlockingReceiveAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new BlockingReceiveAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new CustomTemplateAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new CustomTemplateAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(
		    new examples.messaging.PingAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(
		    new examples.messaging.PingAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_MOBILE.equals(exampleName)) {
	    agentName = getClassName(new MobileAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new MobileAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_ONTOLOGY.equals(exampleName)) {
	    agentName = getClassName(new EngagerAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new EngagerAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new RequesterAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new RequesterAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_PARTY.equals(exampleName)) {
	    agentName = getClassName(new HostAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new HostAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new GuestAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new GuestAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_PING.equals(exampleName)) {
	    agentName = getClassName(new PingAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new PingAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_PROTOCOLS.equals(exampleName)) {
	    agentName = getClassName(new FIPARequestInitiatorAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new FIPARequestInitiatorAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new FIPARequestResponderAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new FIPARequestResponderAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new ContractNetInitiatorAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new ContractNetInitiatorAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new ContractNetResponderAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new ContractNetResponderAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new BrokerAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new BrokerAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_THANKSAGENT.equals(exampleName)) {
	    agentName = getClassName(new ThanksAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new ThanksAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_TOPIC.equals(exampleName)) {
	    agentName = getClassName(new TopicMessageReceiverAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new TopicMessageReceiverAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new TopicMessageSenderAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new TopicMessageSenderAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.EXAMPLE_YELLOWPAGES.equals(exampleName)) {
	    agentName = getClassName(new DFRegisterAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new DFRegisterAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new DFSearchAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new DFSearchAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new DFSubscribeAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new DFSubscribeAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.DEMO_MEETINGSCHEDULER.equals(exampleName)) {
	    agentName = getClassName(new MeetingSchedulerAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new MeetingSchedulerAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName + "_00", agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agents.append(getExampleAgent(agentName + "_01", agentClassName));
	}
	if (this.EXAMPLE_PERSISTENCE.equals(exampleName)) {
	    agentName = getClassName(new PersistentAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new PersistentAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));

	}
	if (this.EXAMPLE_MISCELLANEOUS.equals(exampleName)) {
	    agentName = getClassName(new FSMExecutor(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new FSMExecutor(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));

	}
	if (this.EXAMPLE_DSC.equals(exampleName)) {
	    agentName = getClassName(new MeetingBroker(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new MeetingBroker(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new MeetingRequester(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new MeetingRequester(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new MeetingParticipant(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new MeetingParticipant(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(new ExampleDSCAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new ExampleDSCAgent(),
		    this.INT_CLASS_NAME_FULL);
	    agents.append(getExampleAgent(agentName, agentClassName));
	}
	if (this.DEMO_SEMANTICS_00.equals(exampleName)) {
	    agentName = getClassName(new IsbnHolderAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(new IsbnHolderAgent(),
		    this.INT_CLASS_NAME_FULL);
	    String agentName00 = agentName;
	    param = Constant.STRING_OPEN_PARENTHESIS
	    + this.DEMO_SEMANTICS_00_PARAM_00
	    + Constant.STRING_CLOSE_PARENTHESIS;
	    agentClassName = agentClassName + param;
	    agents.append(getExampleAgent(agentName, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(
		    new jsademos.booktrading.BookSellerAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(
		    new jsademos.booktrading.BookSellerAgent(),
		    this.INT_CLASS_NAME_FULL);
	    String agentName01 = agentName+ "_01";
	    param = Constant.STRING_OPEN_PARENTHESIS
	    + this.DEMO_SEMANTICS_00_PARAM_01
	    + Constant.STRING_COMMA
	    + agentName00
	    + Constant.STRING_CLOSE_PARENTHESIS;
	    agentClassName = agentClassName + param;
	    agents.append(getExampleAgent(agentName01, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(
		    new jsademos.booktrading.BookSellerAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(
		    new jsademos.booktrading.BookSellerAgent(),
		    this.INT_CLASS_NAME_FULL);
	    String agentName02 = agentName+ "_02";
	    param = Constant.STRING_OPEN_PARENTHESIS
	    + this.DEMO_SEMANTICS_00_PARAM_02
	    + Constant.STRING_COMMA
	    + agentName01
	    + Constant.STRING_CLOSE_PARENTHESIS;
	    agentClassName = agentClassName + param;
	    agents.append(getExampleAgent(agentName02, agentClassName));
	    agents.append(Constant.STRING_SEMICOLON);
	    agentName = getClassName(
		    new jsademos.booktrading.BookBuyerAgent(),
		    this.INT_CLASS_NAME_SIMPLE);
	    agentClassName = getClassName(
		    new jsademos.booktrading.BookBuyerAgent(),
		    this.INT_CLASS_NAME_FULL);
	    param = Constant.STRING_OPEN_PARENTHESIS
	    + agentName00
	    + Constant.STRING_COMMA
	    + agentName01
	    + Constant.STRING_COMMA
	    + agentName02
	    + Constant.STRING_CLOSE_PARENTHESIS;
	    agentClassName = agentClassName + param;
	    agents.append(getExampleAgent(agentName, agentClassName));
	}

	exampleMap.put(exampleName, agents.toString());
    }

    private String getExampleAgent(String agentName, String agentClassName) {
	StringBuffer agents = new StringBuffer();
	agents.append(agentName);
	agents.append(Constant.STRING_COLON);
	agents.append(agentClassName);
	return agents.toString();
    }

    private String getClassName(Object klass, int klassNameType) {
	String className = null;
	if (klassNameType == 0) {
	    className = klass.getClass().getSimpleName();
	} else if (klassNameType == 1) {
	    className = klass.getClass().getName();
	}
	return className;
    }

    public static JadeExamples instance() {
	return new JadeExamples();
    }

    public static String getAgentsProperty(String exampleName) {
	return exampleMap.get(exampleName);
    }

}
