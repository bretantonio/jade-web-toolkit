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
import jade.tools.ascml.absmodel.dependency.ISocietyInstanceDependency;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.*;

import java.util.HashSet;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class FunctionalRecord extends AbstractDependencyRecord {
	
	private AgentLauncher launcher;

	public FunctionalRecord(IAbstractRunnable absRunnable, AgentLauncher launcher) {
		super(absRunnable);
		this.launcher = launcher;
	}

	protected void checkStatus() {
		if (neededWatcherTypes.size() == 0 && neededAgentNames.size() == 0 && neededSocietyNames.size() == 0) {
			if (!absRunnable.getStatus().equals(new Functional())) {
				absRunnable.setStatus(new Functional());
				absRunnable.setDetailedStatus("The instance successfully started and is now running.");				
			}
		} else {
			if (absRunnable.getStatus().equals(new Functional())) {
				if (neededWatcherTypes.size()>0) {
					//This takes care of agent and society type dependecies
					for (AbstractDependencyWatcher oneWatcher : neededWatcherTypes) {
						if (oneWatcher.isActive()) {
							if (oneWatcher instanceof TypeCountWatcher) {
								TypeCountWatcher tcw = (TypeCountWatcher) oneWatcher;
								String fqRunnableName = tcw.getAgentType().concat(".").concat((Long.toString(System.currentTimeMillis())));
								int neededAgents = tcw.getNeededAgents();
								try {
									IRunnableAgentInstance[] runnableAgents = launcher.getRepository().createRunnableAgentInstance(fqRunnableName,neededAgents);
									for (int i=0; i<runnableAgents.length; i++) {						
										launcher.getDependencyManager().startThisAgent(runnableAgents[i]);
									}
								}
								catch (NumberFormatException e) {
									e.printStackTrace();
								}
								catch (ModelException e) {
									e.printStackTrace();
								}
								catch (ModelActionException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				if (neededSocietyNames.size()>0) {
					for (String fqnName : neededSocietyNames) {
						if (activeSocietyNames.contains(fqnName)) {
							try {
								IRunnableSocietyInstance runnableSociety = launcher.getRepository().createRunnableSocietyInstance(fqnName);					
								launcher.getDependencyManager().startThisSociety(runnableSociety);
							}
							catch (ModelException e) {
								e.printStackTrace();
							}
							catch (ModelActionException e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (neededWatcherTypes.size() == 0 && neededAgentNames.size() == 0 && neededSocietyNames.size() == 0) {
					return;
				} else {
					absRunnable.setStatus(new NonFunctional());
				}
			} else if (absRunnable.getStatus().equals(new Stopping())) {
				if (runningAgentNames.size() == 0 && runningSocietyNames.size() == 0 && runningWatcherTypes.size() == 0) {
					absRunnable.setStatus(new Dead());
				}
			}
		}
	}
}
