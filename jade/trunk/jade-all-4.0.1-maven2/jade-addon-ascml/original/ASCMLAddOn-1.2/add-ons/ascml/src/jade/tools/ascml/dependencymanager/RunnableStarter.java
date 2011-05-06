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

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.launcher.AgentKillThread;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.launcher.AgentLauncherThread;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.onto.Error;
import jade.util.Logger;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class RunnableStarter implements ModelChangedListener {
	
	private AgentLauncher launcher;
	private FunctionalStateController myFunctionalController;
	
	public RunnableStarter(AgentLauncher launcher,FunctionalStateController myFunctionalController) {
		this.myFunctionalController=myFunctionalController;
		this.launcher=launcher;
	}
	
	public void modelChanged(ModelChangedEvent evt) {
		if (launcher.myLogger.isLoggable(Logger.INFO)) {
			launcher.myLogger.info("Received update: "+evt.getEventCode());
		}	
		if (evt.getEventCode() == ModelChangedEvent.STATUS_CHANGED) {
			if (evt.getModel() instanceof IAbstractRunnable) {			
				try {
					IAbstractRunnable absRunnable = (IAbstractRunnable)evt.getModel();
					if (launcher.myLogger.isLoggable(Logger.INFO)) {
						launcher.myLogger.info("Received starting for "+absRunnable.getFullyQualifiedName());
					}					
					if (absRunnable.getStatus().equals(new Starting())) {
						startThisModel(absRunnable);
					} else if (absRunnable.getStatus().equals(new Stopping())) {
						stopThisModel(absRunnable);
					}
				} catch (ClassCastException cce) {
					System.err.println("DependencyManager: ClassCastException:");
					System.err.println(cce.toString());
				}
			}
		}
	}

	private void stopThisModel(IAbstractRunnable absRunnable) {
		if (absRunnable instanceof IRunnableAgentInstance) {
			IRunnableAgentInstance agentInstance = (IRunnableAgentInstance)absRunnable;
            AgentKillThread akt = new AgentKillThread(agentInstance, launcher);
            try {
                akt.getResult();
            } catch (ModelActionException mae) {
				agentInstance.setStatus(new Error());
				agentInstance.setDetailedStatus(mae.getMessage());
            }
		} else if (absRunnable instanceof IRunnableSocietyInstance) {
			stopThisSociety((IRunnableSocietyInstance)absRunnable);
		}
	}

	private void stopThisSociety(IRunnableSocietyInstance societyInstance) {
		IRunnableAgentInstance[] agentInstances = societyInstance.getRunnableAgentInstances();
		for (int i = 0; i < agentInstances.length; i++) {
			agentInstances[i].setStatus(new Stopping());
		}
		IRunnableRemoteSocietyInstanceReference[] remoteSocieties = societyInstance.getRemoteRunnableSocietyInstanceReferences();
		for (int i=0;i<remoteSocieties.length;i++) {
			launcher.stopRemoteSociety(remoteSocieties[i]);
		}		
	}

	private void startThisModel(IAbstractRunnable absRunnable) {
		if (absRunnable instanceof IRunnableAgentInstance) {
			IRunnableAgentInstance agentInstance = (IRunnableAgentInstance)absRunnable;
			AgentLauncherThread new_thread = new AgentLauncherThread(agentInstance, launcher, null);		
		} else if (absRunnable instanceof IRunnableSocietyInstance) {
			IRunnableSocietyInstance societyInstance = (IRunnableSocietyInstance)absRunnable;
			myFunctionalController.addModel(societyInstance);
			IRunnableAgentInstance[] agentInstances = societyInstance.getRunnableAgentInstances();
			for (int i=0;i<agentInstances.length;i++) {
				try {
					launcher.getDependencyManager().startThisAgent(agentInstances[i]);
				}
				catch (ModelActionException e) {
					agentInstances[i].setStatus(new Error());
					agentInstances[i].setDetailedStatus(e.getMessage());
				}
			}
			IRunnableRemoteSocietyInstanceReference[] remoteSocieties = societyInstance.getRemoteRunnableSocietyInstanceReferences();
			for (int i=0;i<remoteSocieties.length;i++) {
				//TODO: Add some mechanism to watch the status of remote societies in the gui
				launcher.inquirerAndStartRemoteSociety(remoteSocieties[i]);
			}
		}	
	}

}
