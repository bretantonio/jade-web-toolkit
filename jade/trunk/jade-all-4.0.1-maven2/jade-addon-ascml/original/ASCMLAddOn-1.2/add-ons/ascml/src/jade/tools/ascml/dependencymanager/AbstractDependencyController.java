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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jade.core.AID;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.dependency.IAgentInstanceDependency;
import jade.tools.ascml.absmodel.dependency.IAgentTypeDependency;
import jade.tools.ascml.absmodel.dependency.ISocietyInstanceDependency;
import jade.tools.ascml.absmodel.dependency.ISocietyTypeDependency;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.onto.Error;
import jade.util.Logger;

/**
 * This class is used to control functional and normal dependencies
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public abstract class AbstractDependencyController {
	
	protected HashMap <String, HashSet<AbstractDependencyRecord>> agentNameMap;
	protected HashMap <String, MutableInteger> runningAgentTypeCountMap;
	protected HashMap <String, HashSet<AbstractDependencyRecord>> societyNameMap;
	protected HashMap <String, MutableInteger> runningSocietyTypeCountMap;	
	protected HashMap <IAbstractRunnable,AbstractDependencyRecord>	model2DependecyMap;//Only needed because of Garbage Collection
	protected AgentLauncher launcher;
	
	public AbstractDependencyController(AgentLauncher launcher) {
		this.launcher=launcher;
		model2DependecyMap = new HashMap<IAbstractRunnable,AbstractDependencyRecord>();
		runningAgentTypeCountMap = new HashMap <String, MutableInteger>();
		agentNameMap = new HashMap <String, HashSet<AbstractDependencyRecord>>();
		runningSocietyTypeCountMap = new HashMap <String, MutableInteger>();
		societyNameMap = new HashMap <String, HashSet<AbstractDependencyRecord>>();
	}

	/**
	 * If a society changes its state, call this method to update all dependencies, which refer to that society
	 * @param societyInstanceModel The society of which the state changed
	 */
	public void updateSociety(IRunnableSocietyInstance societyInstanceModel) {
		Status socStatus = societyInstanceModel.getStatus();
		if (socStatus.equals(new Functional())) {
			String societyName = societyInstanceModel.getFullyQualifiedName();
			String societyType = ((ISocietyInstance)societyInstanceModel.getParentModel()).getFullyQualifiedName();
			if (runningSocietyTypeCountMap.containsKey(societyType)) {
				MutableInteger socCount = runningSocietyTypeCountMap.get(societyInstanceModel.getFullyQualifiedName());
				socCount.value++;
				synchronized (socCount) {
					socCount.notifyAll();
				}				
			} else if (societyNameMap.containsKey(societyName)) {
				HashSet<AbstractDependencyRecord> socNameDependencySet = societyNameMap.get(societyName);
				Iterator<AbstractDependencyRecord> sdrIt = socNameDependencySet.iterator();
				while (sdrIt.hasNext()) {
					AbstractDependencyRecord oneSdr = sdrIt.next();
					oneSdr.updateModel(societyName,socStatus);
				}
			}
		} else if (socStatus.equals(new Dead()) || socStatus.equals(new Error()) || socStatus.equals(new NonFunctional()) ) {
			String societyName = societyInstanceModel.getFullyQualifiedName();
			String societyType = ((ISocietyInstance)societyInstanceModel.getParentModel()).getFullyQualifiedName();
			if (runningSocietyTypeCountMap.containsKey(societyType)) {
				MutableInteger socCount = runningSocietyTypeCountMap.get(societyInstanceModel.getFullyQualifiedName());				
				socCount.value--;
				synchronized (socCount) {
					socCount.notifyAll();
				}								
			} else if (societyNameMap.containsKey(societyName)) {
				HashSet<AbstractDependencyRecord> socNameDependencySet = societyNameMap.get(societyName);
				Iterator<AbstractDependencyRecord> sdrIt = socNameDependencySet.iterator();
				while (sdrIt.hasNext()) {
					AbstractDependencyRecord oneSdr = sdrIt.next();
					oneSdr.updateModel(societyName,socStatus);
				}
			}			
		}
	}
	
	/**
	 * If a agent changes its state, call this method to update all dependencies, which refer to that agent
	 * @param agentInstanceModel The agent of which the state changed
	 */
	public void updateAgent(IRunnableAgentInstance agentInstanceModel) {
		Status agentStatus = agentInstanceModel.getStatus();
		if (agentStatus.equals(new Functional())) {
			String agentName = agentInstanceModel.getFullyQualifiedName();
			String agentType = agentInstanceModel.getType().getFullyQualifiedName();
			if (runningAgentTypeCountMap.containsKey(agentType)) {
				MutableInteger agentCount = runningAgentTypeCountMap.get(agentInstanceModel.getFullyQualifiedName());
				agentCount.value++;
				synchronized (agentCount) {
					agentCount.notifyAll();
				}
			} else if (agentNameMap.containsKey(agentName)) {
				HashSet<AbstractDependencyRecord> agentNameDependencySet = agentNameMap.get(agentName);
				Iterator<AbstractDependencyRecord> sdrIt = agentNameDependencySet.iterator();
				while (sdrIt.hasNext()) {
					AbstractDependencyRecord oneSdr = sdrIt.next();
					oneSdr.updateModel(agentName,agentStatus);
				}
			}
		} else if (agentStatus.equals(new Dead()) || agentStatus.equals(new Error())) {
			String agentName = agentInstanceModel.getFullyQualifiedName();
			String agentType = agentInstanceModel.getType().getFullyQualifiedName();
			if (runningAgentTypeCountMap.containsKey(agentType)) {
				MutableInteger agentCount = runningAgentTypeCountMap.get(agentInstanceModel.getFullyQualifiedName());
				agentCount.value--;
				synchronized (agentCount) {
					agentCount.notifyAll();
				}
			} else if (agentNameMap.containsKey(agentName)) {
				HashSet<AbstractDependencyRecord> agentNameDependencySet = agentNameMap.get(agentName);
				Iterator<AbstractDependencyRecord> sdrIt = agentNameDependencySet.iterator();
				while (sdrIt.hasNext()) {
					AbstractDependencyRecord oneSdr = sdrIt.next();
					oneSdr.updateModel(agentName,agentStatus);
				}
			}			
		}
	}

	
	/**
	 * If a agent is born updated all dependencies which refer to this agent-type
	 * @param agentType The type of the agent which was born
	 */
	public void agentBorn(String agentType) {
		MutableInteger count;
		if (runningAgentTypeCountMap.containsKey(agentType)) {
			count = runningAgentTypeCountMap.get(agentType);
		} else {
			count = new MutableInteger(0);
		}
		count.value++;
		count.notifyAll();
	}
	
	/**
	 * If a agent is born updated all dependencies which refer to this agent-AID
	 * @param agentAID The AID of the agent which was born
	 */
	public void agentBorn(AID agentAID) {
		//TODO: check agentAID.getLocalName() against IAgentInstanceDependency.getName();
		// also see: agentDied(AID agentAID) (same thing there)
		String agentName = agentAID.getLocalName();
		if (agentNameMap.containsKey(agentName)) {
			HashSet<AbstractDependencyRecord> nameDependencySet = agentNameMap.get(agentName);
			//TODO: What about already running agents?
			//at the moment, only new agents are taken into account when we have
			//agentInstance dependencies
			Iterator<AbstractDependencyRecord> sdrIt = nameDependencySet.iterator();
			while (sdrIt.hasNext()) {
				AbstractDependencyRecord oneSdr = sdrIt.next();
				oneSdr.agentBorn(agentName);		
			}			
		}
	}

	/**
	 * If a agent dies updated all dependencies which refer to this agent-type
	 * @param agentType The type of the agent which died
	 */
	public void agentDied(String agentType) {
		if (runningAgentTypeCountMap.containsKey(agentType)) {
			MutableInteger count = runningAgentTypeCountMap.get(agentType);
			count.value--;
			count.notifyAll();
		}
	}

	/**
	 * If a agent dies updated all dependencies which refer to this agent-AID
	 * @param agentAID The AID of the agent which died
	 */
	public void agentDied(AID agentAID) {
		String agentName = agentAID.getLocalName();
		if (agentNameMap.containsKey(agentName)) {
			HashSet<AbstractDependencyRecord> nameDependencySet = agentNameMap.get(agentName);
			Iterator<AbstractDependencyRecord> sdrIt = nameDependencySet.iterator();
			while (sdrIt.hasNext()) {
				AbstractDependencyRecord oneSdr = sdrIt.next();
				oneSdr.agentDied(agentName);
			}			
		}
	}
	
	protected abstract Vector<IDependency> getDependenciesFromModel(IAbstractRunnable societyInstanceModel);
	protected abstract AbstractDependencyRecord getNewRecord(IAbstractRunnable absRunnable);
	protected abstract void noDependencies(IAbstractRunnable absRunnable);
	protected abstract void handleActiveDependency(IDependency oneDep);
	
	/**
	 * Add an IAbstractRunnable to the controller.
	 * @param absRunnable the IAbstractRunnable of which to watch the dependencies 
	 */
	public void addModel(IAbstractRunnable absRunnable) {
		Vector<IDependency> deps;
		try {
			deps = getDependenciesFromModel(absRunnable);
		} catch (Exception e) {
			noDependencies(absRunnable);
			return;
		}
		Iterator<IDependency> depIterator = deps.iterator();
		if (depIterator.hasNext()) {
			while (depIterator.hasNext()) {
				IDependency oneDep = depIterator.next();
				addOneDependecy(oneDep,absRunnable);
			}
		} else {
			//We don't have any Dependency
			if (launcher.myLogger.isLoggable(Logger.INFO)) {
				launcher.myLogger.info("This one ("+absRunnable.getFullyQualifiedName()+ ") has no dependencies");
			}
			noDependencies(absRunnable);
		}
	}

	/** 
	 * @param oneDep The dependency to add
	 * @param absRunnable the IAbstractRunnable, the dependecy belongs to
	 */
	private void addOneDependecy(IDependency oneDep, IAbstractRunnable absRunnable) {
		AbstractDependencyRecord dependencyRecord;
		if (model2DependecyMap.containsKey(absRunnable)) {
			dependencyRecord = model2DependecyMap.get(absRunnable);
		} else {
			dependencyRecord  = getNewRecord(absRunnable);
			model2DependecyMap.put(absRunnable,dependencyRecord);
		}
		String depType = oneDep.getType();
		if (depType.equals(IDependency.AGENTINSTANCE_DEPENDENCY)) {
			IAgentInstanceDependency instDep = (IAgentInstanceDependency) oneDep;
			if (instDep.getProvider()==null) {
				String agentName = instDep.getName();
				dependencyRecord.addAgentDependency(agentName);
				HashSet<AbstractDependencyRecord> nameDependencySet;
				if (agentNameMap.containsKey(agentName)) {
					nameDependencySet = agentNameMap.get(agentName);
				} else {
					nameDependencySet = new HashSet<AbstractDependencyRecord>();
					agentNameMap.put(agentName,nameDependencySet);
				}
				nameDependencySet.add(dependencyRecord);
			} else {
				//We got a remote dependency
				RemoteInstanceWatcher remoteWatcher = new RemoteInstanceWatcher(dependencyRecord,launcher,instDep);
				dependencyRecord.addWatcherDependency(remoteWatcher);
			}
		} else if (depType.equals(IDependency.AGENTTYPE_DEPENDENCY)) {					
			IAgentTypeDependency typeDep = (IAgentTypeDependency) oneDep;					
			String agentType = typeDep.getName();
			MutableInteger runningCount;
			if (runningAgentTypeCountMap.containsKey(agentType)) {
				runningCount = runningAgentTypeCountMap.get(agentType);
			} else {
				runningCount = new MutableInteger(0);
			}
			TypeCountWatcher tcw = new TypeCountWatcher(dependencyRecord,agentType, absRunnable, typeDep.getQuantityAsInt(), runningCount, typeDep.isActive());
			dependencyRecord.addWatcherDependency(tcw);
		} else if (depType.equals(IDependency.SOCIETYINSTANCE_DEPENDENCY)) {
			ISocietyInstanceDependency socInstDep = (ISocietyInstanceDependency) oneDep;
			if (socInstDep.getProvider()==null) {
				String societyName = socInstDep.getFullyQualifiedSocietyInstance();
				dependencyRecord.addSocietyDependency(societyName, socInstDep.isActive());
				HashSet<AbstractDependencyRecord> nameDependencySet;
				if (societyNameMap.containsKey(societyName)) {
					nameDependencySet = societyNameMap.get(societyName);
				} else {
					nameDependencySet = new HashSet<AbstractDependencyRecord>();
					societyNameMap.put(societyName,nameDependencySet);
				}
				nameDependencySet.add(dependencyRecord);
			} else {
				//FIXME: Remote Society instance dependencies
				RemoteInstanceWatcher remoteWatcher = new RemoteInstanceWatcher(dependencyRecord,launcher,socInstDep);
				dependencyRecord.addWatcherDependency(remoteWatcher);						
			}
		} else if (depType.equals(IDependency.SOCIETYTYPE_DEPENDENCY)) {
			ISocietyTypeDependency socTypeDep = (ISocietyTypeDependency) oneDep;					
			String socType = socTypeDep.getName();					
			MutableInteger runningCount;
			if (runningSocietyTypeCountMap.containsKey(socType)) {
				runningCount = runningSocietyTypeCountMap.get(socType);
			} else {
				runningCount = new MutableInteger(0);
			}
			TypeCountWatcher tcw = new TypeCountWatcher(dependencyRecord,socType, absRunnable, socTypeDep.getQuantityAsInt(), runningCount, socTypeDep.isActive());
			dependencyRecord.addWatcherDependency(tcw);
		} else if (depType.equals(IDependency.DELAY_DEPENDENCY)) {
			//FIXME: Insert the delay dependency
		} else if (depType.equals(IDependency.SERVICE_DEPENDENCY)) {
			//First: Query the XYZ if there is a known service running
			// - Answer is no: wait until it is
			// - Answer is yes: start
		} else {
			return;
		}
		if (oneDep.isActive()) {
			handleActiveDependency(oneDep);
		}		
	}

}
