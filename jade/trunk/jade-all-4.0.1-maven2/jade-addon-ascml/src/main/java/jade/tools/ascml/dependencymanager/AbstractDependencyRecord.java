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
import jade.tools.ascml.onto.*;

import java.util.HashSet;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 *
 */
public abstract class AbstractDependencyRecord {
	protected HashSet<String> neededAgentNames;
	protected HashSet<String> runningAgentNames;
	protected HashSet<String> runningSocietyNames;
	protected HashSet<String> neededSocietyNames;
	protected HashSet<String> activeSocietyNames;
	protected HashSet<AbstractDependencyWatcher> runningWatcherTypes;
	protected HashSet<AbstractDependencyWatcher> neededWatcherTypes;	
	protected IAbstractRunnable absRunnable;

	public AbstractDependencyRecord(IAbstractRunnable absRunnable) {
		this.absRunnable = absRunnable;
		neededAgentNames = new HashSet<String>();
		runningAgentNames = new HashSet<String>();
		neededSocietyNames = new HashSet<String>();
		runningSocietyNames = new HashSet<String>();
		activeSocietyNames = new HashSet<String>();
		neededWatcherTypes = new HashSet<AbstractDependencyWatcher>();		
		runningWatcherTypes = new HashSet<AbstractDependencyWatcher>();
	}

	protected abstract void checkStatus();

	public void addWatcherDependency(AbstractDependencyWatcher tcw) {
		runningWatcherTypes.add(tcw);
		if (absRunnable.getStatus().equals(new Functional())) {
			absRunnable.setStatus(new NonFunctional());
		}
	}

	public void addAgentDependency(String name) {
		neededAgentNames.add(name);
		if (absRunnable.getStatus().equals(new Functional())) {
			absRunnable.setStatus(new NonFunctional());
		}
	}

	public void addSocietyDependency(String name, boolean isActive) {
		neededSocietyNames.add(name);
		if (isActive) {
			activeSocietyNames.add(name);
		}
		if (absRunnable.getStatus().equals(new Functional())) {
			absRunnable.setStatus(new NonFunctional());
		}
	}	

	protected void watcherDependencyFulfilled(AbstractDependencyWatcher watcher) {
		runningWatcherTypes.add(watcher);
		neededWatcherTypes.remove(watcher);
		checkStatus();
	}

	protected void watcherDependencyFailed(AbstractDependencyWatcher watcher) {
		neededWatcherTypes.add(watcher);
		runningWatcherTypes.remove(watcher);
		checkStatus();
	}

	public void dependencyChanged(AbstractDependencyWatcher watcher) {
		if (runningWatcherTypes.contains(watcher)) {
			watcherDependencyFulfilled(watcher);
		} else {
			watcherDependencyFailed(watcher);
		}

	}

	public void agentBorn(String agentName) {
		if (neededAgentNames.remove(agentName)) {
			runningAgentNames.add(agentName);
			checkStatus();
		}
	}

	public void agentDied(String agentName) {
		if (runningAgentNames.remove(agentName)) {
			neededAgentNames.add(agentName);
			checkStatus();
		}
	}

	public void updateModel(String societyName, Status socStatus) {
		if (socStatus.equals(new Functional())) {
			neededSocietyNames.remove(societyName);
			runningSocietyNames.add(societyName);
			checkStatus();
		} else if (runningSocietyNames.contains(societyName)) {
			runningSocietyNames.remove(societyName);
			neededSocietyNames.add(societyName);
			checkStatus();
		}
	}
}
