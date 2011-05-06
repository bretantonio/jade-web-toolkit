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

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.events.ModelActionListener;
import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.util.Logger;

/**
* @author Sven Lilienthal (ascml@sven-lilienthal.de)
**/

public class LauncherInterface implements ModelActionListener {
	private AgentLauncher al;
	
	public LauncherInterface(AgentLauncher al) {
		this.al = al;
	}
	
	public void modelActionPerformed(ModelActionEvent event) throws ModelActionException {
		if (event.getActionCommand().equals(ModelActionEvent.CMD_START_AGENTINSTANCE)) {
			IRunnableAgentInstance agentInstance = (IRunnableAgentInstance) event.getModel();
			al.getDependencyManager().startThisAgent(agentInstance);
		} else if (event.getActionCommand().equals(ModelActionEvent.CMD_STOP_AGENTINSTANCE)) {
			IRunnableAgentInstance agentInstance = (IRunnableAgentInstance) event.getModel();
			al.getDependencyManager().stopThisAgent(agentInstance);
		} else if (event.getActionCommand().equals(ModelActionEvent.CMD_START_SOCIETYINSTANCE)) {
			IRunnableSocietyInstance societyInstance = (IRunnableSocietyInstance) event.getModel();
			
			// Retrieve list of local dependencies and resolve them
			IRunnableSocietyInstance[] lsocs = societyInstance.getLocalRunnableSocietyInstanceReferences();
			for (int i = 0; i < lsocs.length; i++) {
				IRunnableSocietyInstance sm = lsocs[i];
				ModelActionEvent ae = new ModelActionEvent(ModelActionEvent.CMD_START_SOCIETYINSTANCE, lsocs[i]);
				modelActionPerformed(ae);
			}
			
			if (al.myLogger.isLoggable(Logger.INFO)) {
				al.myLogger.info("Starting "+societyInstance.getFullyQualifiedName());
			}

			// Now we start this society
			al.getDependencyManager().startThisSociety(societyInstance);
		} else if (event.getActionCommand().equals(ModelActionEvent.CMD_STOP_SOCIETYINSTANCE)) {
			IRunnableSocietyInstance instance = (IRunnableSocietyInstance) event.getModel();
			
			// Retrieve list of local dependencies and stop them
			IRunnableSocietyInstance[] lsocs = instance.getLocalRunnableSocietyInstanceReferences();
			for (int i = 0; i < lsocs.length; i++) {
				IRunnableSocietyInstance sm = lsocs[i];
				ModelActionEvent ae = new ModelActionEvent(ModelActionEvent.CMD_STOP_SOCIETYINSTANCE, lsocs[i]);
				modelActionPerformed(ae);
			}

			if (al.myLogger.isLoggable(Logger.INFO)) {
				al.myLogger.info("Stopping "+instance.getFullyQualifiedName());
			}
			
			al.getDependencyManager().stopThisSociety(instance);
		}
	}
}
