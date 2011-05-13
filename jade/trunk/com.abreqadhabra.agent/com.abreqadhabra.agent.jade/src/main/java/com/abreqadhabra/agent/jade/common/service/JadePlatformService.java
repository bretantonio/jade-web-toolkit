package com.abreqadhabra.agent.jade.common.service;

import jade.core.Runtime;
import jade.wrapper.ControllerException;

import java.util.ArrayList;
import java.util.HashMap;

public interface JadePlatformService {
	void startContainer(ArrayList<String> bootPropertyList) ;
	String[] getBootPropertyArgs();
	Runtime getJadeRuntime();
	jade.wrapper.AgentContainer getAgentContainer();
	HashMap<String, HashMap<String, String>> getContainerInfoMap() throws ControllerException;

}
