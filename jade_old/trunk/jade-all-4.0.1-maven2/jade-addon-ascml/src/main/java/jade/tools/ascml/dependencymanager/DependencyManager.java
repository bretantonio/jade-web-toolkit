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

package jade.tools.ascml.dependencymanager;

import jade.core.AID;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.absmodel.IRunnableAgentInstance;
import jade.tools.ascml.absmodel.IRunnableSocietyInstance;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.Dead;
import jade.tools.ascml.onto.Error;
import jade.tools.ascml.onto.Functional;
import jade.tools.ascml.onto.Stopping;
import jade.tools.ascml.repository.Repository;
import jade.util.Logger;
//TODO: Can we use this here: jade.tools.ascml.events.ProgressUpdateEvent?

/**
 * This class is responsible for updating the status of all models.
 * It has to main parts:
 * The {@link jade.tools.ascml.dependencymanager.FunctionalStateController FunctionalStateController}
 * and the {@link jade.tools.ascml.dependencymanager.DependencyStateController DependencyStateController}
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class DependencyManager implements ModelChangedListener {

	private AgentLauncher launcher;
	private Repository myRepository;
	private DependencyManangerLibrary myLibrary;
	private FunctionalStateController myFunctionalController;
	private RunnableStarter myRunnableStarter;
	private DependencyStateController myDependencyStateController;

	public DependencyManager(AgentLauncher launcher) {
		this.launcher = launcher;
		myRepository = launcher.getRepository();
		myLibrary = new DependencyManangerLibrary();
		myFunctionalController = new FunctionalStateController(launcher);
		myRunnableStarter = new RunnableStarter(launcher, myFunctionalController);
		launcher.getRepository().getListenerManager().addModelChangedListener(myRunnableStarter);
		myDependencyStateController = new DependencyStateController(launcher);
	}

	/**
	 * This method is called by the ListenerManager of the repository whenever a model changes.
	 * We are only interested in STATUS_CHANGED messages
	 */
	public void modelChanged(ModelChangedEvent evt) {
		if (evt.getEventCode() == ModelChangedEvent.STATUS_CHANGED) {
			if (evt.getModel() instanceof IAbstractRunnable) {
				try {
					IAbstractRunnable absRunnable = (IAbstractRunnable)evt.getModel();
					if (launcher.myLogger.isLoggable(Logger.INFO)) {
						launcher.myLogger.info("Status update for: "+absRunnable.getFullyQualifiedName()+ "\nNew Status: "+absRunnable.getStatus());
					}
					if (absRunnable instanceof IRunnableSocietyInstance) {
						IRunnableSocietyInstance societyInstanceModel = (IRunnableSocietyInstance) absRunnable;
						myFunctionalController.updateSociety(societyInstanceModel);
						myDependencyStateController.updateSociety(societyInstanceModel);
					} else if (absRunnable instanceof IRunnableAgentInstance) {
						IRunnableAgentInstance agentInstanceModel = (IRunnableAgentInstance) absRunnable;
						myFunctionalController.updateAgent(agentInstanceModel);
						myDependencyStateController.updateAgent(agentInstanceModel);
					} else {
						if (launcher.myLogger.isLoggable(Logger.WARNING)) {
							launcher.myLogger.warning("Unknown Model: "+absRunnable.getClass().getName()+"\nStatus update for: "+absRunnable.getFullyQualifiedName()+ "\nNew Status: "+absRunnable.getStatus());
						}						
					}
				} catch (ClassCastException cce) {
					System.err.println("DependencyManager: ClassCastException:");
					System.err.println(cce.toString());
					cce.printStackTrace();
					System.err.println("Model was of type: "+evt.getModel().getClass().getName());				
				}
			}
		}
	}
	
	/**
	 * This method is called by the AgentLauncher whenever a agent is born
	 * @param agentAID The agent-AID of the new born agent
	 */
	public void agentBorn(AID agentAID) {
		if (myLibrary.hasAgent(agentAID)) {
			IRunnableAgentInstance agent = myLibrary.getAgent(agentAID);
			agent.setStatus(new Functional());					
		}
		myFunctionalController.agentBorn(agentAID);
	}

	/**
	 * This method is called by the AgentLauncher whenever a agent dies
	 * @param agentAID The agent-AID of the died agent
	 */	
	public void agentDied(AID agentAID) {
		if (myLibrary.hasAgent(agentAID)) {
			IRunnableAgentInstance agent = myLibrary.getAgent(agentAID);
			if (agent.getStatus().equals(new Stopping())) {
				agent.setStatus(new Dead());
			} else {
				agent.setStatus(new Error());
			}
			myLibrary.delAgent(agentAID);
		}
		myFunctionalController.agentDied(agentAID);
	}

	/**
	 * Call this method to start a specific IRunnableAgentInstance
	 * @param agentInstanceModel The IRunnableAgentInstance to be started
	 * @throws ModelActionException Throws a ModelActionException if the IRunnableAgentInstance is already in the library, which means, we already started it
	 */
	public void startThisAgent(IRunnableAgentInstance agentInstanceModel) throws ModelActionException {
		if (myLibrary.hasAgent(agentInstanceModel.getName())) {
			throw new ModelActionException("Agent is already in library", agentInstanceModel);
		}
		myLibrary.addAgent(agentInstanceModel.getName(), agentInstanceModel);
		myDependencyStateController.addModel(agentInstanceModel);
	}

	/**
	 * Call this method to start a specific IRunnableSocietyInstance
	 * @param societyInstanceModel The IRunnableSocietyInstance to be started
	 * @throws ModelActionException Throws a ModelActionException if the IRunnableSocietyInstance is already in the library, which means, we already started it
	 */
	public void startThisSociety(IRunnableSocietyInstance societyInstanceModel) throws ModelActionException {
		if (myLibrary.hasSociety(societyInstanceModel.getName())) {
			throw new ModelActionException("Found society in library", societyInstanceModel);
		}
		myLibrary.addSociety(societyInstanceModel.getName(), societyInstanceModel);
		
		if (launcher.myLogger.isLoggable(Logger.INFO)) {
			launcher.myLogger.info("Now adding "+societyInstanceModel.getFullyQualifiedName()+ " to the DependencyStateController");
		}
		
		myDependencyStateController.addModel(societyInstanceModel);
	}

	/**
	 * Call this method to stop a specific IRunnableAgentInstance
	 * @param agentInstance The IRunnableAgentInstance to be stopped
	 */
	public void stopThisAgent(IRunnableAgentInstance agentInstance) {
		agentInstance.setStatus(new Stopping());
	}
	
	/**
	 * Call this method to stop a specific IRunnableSocietyInstance
	 * @param societyInstanceModel The IRunnableSocietyInstance to be stopped
	 */
	public void stopThisSociety(IRunnableSocietyInstance societyInstanceModel) {
		societyInstanceModel.setStatus(new Stopping());
	}
}
