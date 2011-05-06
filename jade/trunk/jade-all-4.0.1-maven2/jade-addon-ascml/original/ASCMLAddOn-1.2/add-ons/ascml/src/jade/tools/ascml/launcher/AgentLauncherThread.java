/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package jade.tools.ascml.launcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.onto.basic.Action;
import jade.core.ContainerID;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.events.ProgressUpdateEvent;
import jade.tools.ascml.onto.Error;
import jade.util.Logger;

/**
 * Starts an IRunnableAgentInstance
 * If everything works fine getResult returns, 
 * otherwise getResult throws a ModelActionException
 * containing the reason why starting failed.
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class AgentLauncherThread implements Runnable {

	private IRunnableAgentInstance aModel;
	private Thread t;
	private AgentLauncher launcher;
	private ModelActionException mae;
	private StringBuffer synchobject;
	private HashSet<IToolOption> socToolOptions;

	/**
	 * Starts a new AgentInstaceModel
	 *
	 * @param aModel      RunnableAgentInstanceModel to be started
	 * @param launcher          The AgentLauncher-agent
	 * @param socToolOptions 
	 */
	public AgentLauncherThread(IRunnableAgentInstance aModel, AgentLauncher launcher, HashSet<IToolOption> socToolOptions) {
		// System.out.println("Creating AgentLauncherThread for Agent "+ aModel);
		this.aModel = aModel;
		this.launcher = launcher;
		this.socToolOptions = socToolOptions;
		if (this.socToolOptions == null) {
			this.socToolOptions = new HashSet<IToolOption>();
		}
		synchobject = new StringBuffer();
		t = new Thread(this, "LaunchThread ASCML for " + aModel.getName());
		t.start();
	}

	/**
	 * This method starts a new negotiation agent
	 *
	 * @param theAgent RunnableAgentInstanceModel to start
	 * @param result   If starting fails, result is not empty but contains the
	 *                 reason for failing
	 */
	private void newAgent(IRunnableAgentInstance theAgent, StringBuffer result) throws Exception {
		String agentName = theAgent.getName();
		String className = theAgent.getType().getPackageName()+"."+theAgent.getClassName();
		int timeout = 15000;
		CreateAgent ca = new CreateAgent();
		String containerName = launcher.getContainerController().getContainerName();
		ca.setAgentName(agentName);
		ca.setClassName(className);
		ca.setContainer(new ContainerID(containerName, null));

		// iterate through all arguments contained in the agentModel
		IParameter[] params = theAgent.getParameters();
		if (params.length==0) {
			if (launcher.myLogger.isLoggable(Logger.INFO)) {
				launcher.myLogger.info("No parameters for "+agentName);
			}
		}
		for (int i = 0; i < params.length; i++) {
			// get the argument-name and add the argument's value to the
			// agent-to-start
			String argName = params[i].getName();

			// Depending on the platform type, the parameters passed to the runtime-instance of the agent
			// are passed as key-value pairs (for JADE-platform-agents) or as value only (Jadex)
			String platformType = theAgent.getPlatformType();
			String arg;
			if ((platformType == IAgentType.PLATFORM_TYPE_JADEX) || argName.trim().equals(""))
				arg = (String) params[i].getValue();
			else
				arg = argName + "=" + params[i].getValue();

			ca.addArguments(arg);
			if (launcher.myLogger.isLoggable(Logger.INFO)) {
				launcher.myLogger.info("Adding parameter: "+arg+" to "+agentName);
			}
		}
		IParameterSet[] paramsets = theAgent.getParameterSets();
		if (paramsets.length==0) {
			if (launcher.myLogger.isLoggable(Logger.INFO)) {
				launcher.myLogger.info("No parametersets for "+agentName);
			}
		}		
		for (int i = 0; i < paramsets.length; i++) {
			// get the argument-name and add the argument's value to the
			// agent-to-start
			// System.err.println("AgentLauncherThread: Ist dies der richtige Param-Value ?");
			Object[] values = theAgent.getParameterSet(paramsets[i].getName()).getValues();// paramsets[i].getValues();
			StringBuffer value = new StringBuffer();
			for (int j = 0; j < values.length; j++) {
				value.append((String) values[j] + " ");
			}
			ca.addArguments(value.toString());
			if (launcher.myLogger.isLoggable(Logger.INFO)) {
				launcher.myLogger.info("Adding parameters: "+value.toString()+" to agentName");
			}
			
		}
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setLanguage(launcher.codec.getName());
		msg.setOntology(JADEManagementOntology.NAME);
		msg.addReceiver(launcher.getAMS());
		msg.setSender(launcher.getAID());
		msg.setProtocol(jade.domain.FIPANames.InteractionProtocol.FIPA_REQUEST);
		Action contentAction = new Action();
		contentAction.setAction(ca);
		contentAction.setActor(launcher.getAID());
		ContentManager manager = launcher.getContentManager();
		try {
			manager.fillContent(msg, (ContentElement) contentAction);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (launcher.myLogger.isLoggable(Logger.INFO)) {
			launcher.myLogger.info("Actually starting " + agentName + " of class "+className+" right now");
		}
		launcher.addAMSBehaviour(msg, result, agentName);
		synchronized (result) {
			try {
				result.wait(timeout);
			}
			catch (InterruptedException ie) {}
		}

	}

	/**
	 * Waits for the agent to be started, throws ModelActionException if
	 * starting fails
	 *
	 * @throws ModelActionException Contains the reason why starting failed
	 */
	public void getResult() throws ModelActionException {
		try {
			t.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (mae != null) {
			throw mae;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		//For debugging purposes only: set this to false to test startup of toolagents:
		boolean startAgent = true;
		int timeout = 15000;

		for (IToolOption option : aModel.getToolOptions()) {
			launcher.doToolOption(option, aModel, synchobject);
			synchronized (synchobject) {
				try {
					synchobject.wait(timeout);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		for (IToolOption option : socToolOptions) {
			launcher.doToolOption(option, aModel, synchobject);
			synchronized (synchobject) {
				try {
					synchobject.wait(timeout);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (startAgent) {
			StringBuffer result = new StringBuffer(1024);
			try {
				// System.out.println("About to start new Agent NOW ");
				launcher.getRepository().throwProgressUpdateEvent(
						new ProgressUpdateEvent("Starting agent: " + aModel.getName(), ProgressUpdateEvent.PROGRESS_ADVANCE));
				newAgent(aModel, result);
				if (result.length() != 0) {
					mae = new ModelActionException("Error while starting the agent named '" + aModel.getName() + "'.",
							"For some reason, the agent couldn't be started, please take a look at the system's error-message: " + result.toString(), aModel);
				}
			}
			catch (Exception e) {
				synchronized (this) {
					e.printStackTrace();
					mae = new ModelActionException("Error while starting the agent named '" + aModel.getName() + "'.",
							"For some reason, the agent couldn't be started, please take a look at the system's error-message: " + result.toString(), e, aModel);
				}
			}
			if (mae != null) {
				Error ontoError = new Error();
				ontoError.setDetailedStatus(mae.getMessage());
				aModel.setStatus(ontoError);
				aModel.setDetailedStatus(result.toString());
			}
		}
	}
}
