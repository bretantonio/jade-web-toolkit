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

import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.absmodel.IRunnableRemoteSocietyInstanceReference;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.onto.Functional;
import jade.tools.ascml.onto.Starting;
import jade.tools.ascml.onto.Status;

/**
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 *
 */
public class RemoteInquirerAndStarter implements ModelChangedListener {
	
	protected IRunnableRemoteSocietyInstanceReference remoteSociety;
	protected AgentLauncher launcher;
	
	public RemoteInquirerAndStarter (AgentLauncher launcher, IRunnableRemoteSocietyInstanceReference remoteSociety) {
		this.remoteSociety = remoteSociety;
		this.launcher = launcher;
	}


	public void modelChanged(ModelChangedEvent event) {
		String eventCode = event.getEventCode();
		if ((eventCode == ModelChangedEvent.STATUS_CHANGED)) {
			Object eventModel = event.getModel();
			if (eventModel instanceof IRunnableRemoteSocietyInstanceReference) {
				IRunnableRemoteSocietyInstanceReference changedSociety = (IRunnableRemoteSocietyInstanceReference) eventModel;
				if (changedSociety == remoteSociety) {
					Status socStatus = remoteSociety.getStatus();
					if (!(socStatus.equals(new Functional()) || socStatus.equals(new Starting()))) {
						//Ok, the remote society isn't functional or starting, then we have to start it
						launcher.startRemoteSociety(remoteSociety);
					}
					//After we have updated the status, we are of no more use
					launcher.getRepository().getListenerManager().removeModelChangedListener(this);
				}
			}
		}
	}

}
