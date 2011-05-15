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

import java.util.Vector;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.absmodel.dependency.*;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.*;
import jade.util.Logger;

/**
 * This class watches the startup dependencies for all models
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class DependencyStateController extends AbstractDependencyController{
	
	private AgentLauncher launcher;

	public DependencyStateController(AgentLauncher launcher) {
		super(launcher);
		this.launcher=launcher;
	}

	protected Vector<IDependency> getDependenciesFromModel(IAbstractRunnable societyInstanceModel) {
		return new Vector<IDependency>(societyInstanceModel.getDependencyList());
	}

	protected AbstractDependencyRecord getNewRecord(IAbstractRunnable absRunnable) {
		return new DependencyRecord(absRunnable);
	}

	protected void noDependencies(IAbstractRunnable absRunnable) {
		if (launcher.myLogger.isLoggable(Logger.INFO)) {
			launcher.myLogger.info("Setting status of "+absRunnable.getFullyQualifiedName()+" to starting");
		}			
		absRunnable.setStatus(new Starting());
	}

	protected void handleActiveDependency(IDependency oneDep) {
		String depType = oneDep.getType();
		if (depType.equals(IDependency.AGENTINSTANCE_DEPENDENCY)) {
			//This is not possible
			/*
			IAgentInstanceDependency instDep = (IAgentInstanceDependency) oneDep;
			try {				
				IRunnableAgentInstance[] runnableAgents = launcher.getRepository().createRunnableAgentInstance(instDep.getName(), 1);					
				launcher.getDependencyManager().startThisAgent(runnableAgents[0]);
			}
			catch (ModelException e) {
				e.printStackTrace();
			}
			catch (ModelActionException e) {
				e.printStackTrace();
			}*/		
		} else if (depType.equals(IDependency.AGENTTYPE_DEPENDENCY)) {
			IAgentTypeDependency typeDep = ((IAgentTypeDependency)oneDep);
			String fqRunnableName = typeDep.getName().concat(".").concat((Long.toString(System.currentTimeMillis()))); 
			try {
				IRunnableAgentInstance[] runnableAgents = launcher.getRepository().createRunnableAgentInstance(fqRunnableName,typeDep.getQuantityAsInt());
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
		} else if (depType.equals(IDependency.SOCIETYINSTANCE_DEPENDENCY)) {
			ISocietyInstanceDependency instDep = (ISocietyInstanceDependency) oneDep;
			try {
				IRunnableSocietyInstance runnableSociety = launcher.getRepository().createRunnableSocietyInstance(instDep.getFullyQualifiedSocietyInstance());					
				launcher.getDependencyManager().startThisSociety(runnableSociety);
			}
			catch (ModelException e) {
				e.printStackTrace();
			}
			catch (ModelActionException e) {
				e.printStackTrace();
			}
		} else if (depType.equals(IDependency.SOCIETYTYPE_DEPENDENCY)) {
			ISocietyTypeDependency typeDep = ((ISocietyTypeDependency)oneDep);
			String fqRunnableName = typeDep.getName().concat(".").concat(((IAgentTypeDependency)oneDep).getName().concat(Long.toString(System.currentTimeMillis()%(1000*60*60*24*365))));
			try {
				IRunnableSocietyInstance runnableAgents = launcher.getRepository().createRunnableSocietyInstance(fqRunnableName);
				launcher.getDependencyManager().startThisSociety(runnableAgents);
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
		} else if (depType.equals(IDependency.DELAY_DEPENDENCY)) {
			//What is an active delay dependency?
			//Shoud we change system time ;-)
		} else if (depType.equals(IDependency.SERVICE_DEPENDENCY)) {
			//Active Service dependecy are not possible at the moment, as we can't search
			// the repository for a service
		}
	}
}
