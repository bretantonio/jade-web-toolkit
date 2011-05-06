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

import jade.tools.ascml.absmodel.dependency.IAgentInstanceDependency;
import jade.tools.ascml.absmodel.dependency.ISocietyInstanceDependency;
import jade.tools.ascml.launcher.AgentLauncher;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class RemoteInstanceWatcher extends AbstractDependencyWatcher{
	
	private AgentLauncher launcher;

	public RemoteInstanceWatcher(AbstractDependencyRecord parentDeps, AgentLauncher launcher, IAgentInstanceDependency instDep) {
		super(parentDeps,instDep.getName());
		this.launcher=launcher;
		t.run();
	}

	public RemoteInstanceWatcher(AbstractDependencyRecord parentDeps, AgentLauncher launcher, ISocietyInstanceDependency socInstDep) {
		super(parentDeps,socInstDep.getFullyQualifiedSocietyInstance());
		this.launcher=launcher;
		t.run();
	}

	public void run() {
		//FIXME: Remote Dependecies
		// 1. Test if the remote ASCML has the model
		// 2. Check the status of the model
		// 3. If it isn't running start it
		// 4. Wait for it to be started and update the dependecy
	}

}
