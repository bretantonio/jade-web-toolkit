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

import jade.tools.ascml.absmodel.IAbstractRunnable;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class TypeCountWatcher extends AbstractDependencyWatcher{
	
	private int needed;
	private MutableInteger running;
	private String agentType;
	private IAbstractRunnable absRunnable;
	private boolean currentStatus;
	
	public TypeCountWatcher(AbstractDependencyRecord parentDeps, String agentType,IAbstractRunnable absRunnable,int needed, MutableInteger running, boolean isActive) {
		super(parentDeps,agentType);
		this.isActive = isActive;
		this.agentType = agentType;
		this.absRunnable = absRunnable;
		this.needed = needed;
		this.running = running;
		currentStatus = false;
		t.start();
	}
	
	private boolean isFulfilled() {
		//synchronized (running) {
			return (running.value>=needed);
		//}
	}
	
	public String getAgentType() {
		return agentType;
	}
	
	public int getNeededAgents() {
		return (needed - running.value);
	}
	
	public void run() {
		while (!takeDown) {
			try {
				synchronized (running) {
					System.err.println(agentType+" is now waiting on running");
					running.wait();
					System.err.println(agentType+" finished waiting on running");
				}
			}
			catch (InterruptedException e) {
			}
			if (isFulfilled()!=currentStatus) {
				changed();
			}
		}
	}
}
