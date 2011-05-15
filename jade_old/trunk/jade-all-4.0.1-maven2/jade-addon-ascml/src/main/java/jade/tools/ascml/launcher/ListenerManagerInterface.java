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
import jade.tools.ascml.events.*;

/**
* @author Sven Lilienthal (ascml@sven-lilienthal.de)
**/

public class ListenerManagerInterface implements ToolTakeDownListener, ExceptionListener, ModelChangedListener, LongTimeActionStartListener {

	AgentLauncher al;

	public ListenerManagerInterface(AgentLauncher launcher) {
		al = launcher;
	}

	/**
	 * This method is called, when the ASCML should be taken down. The Launcher
	 * implements the ToolTakeDownListener-interface (by implementing this
	 * method) and registers itself at the GUI-components to be informed when
	 * the user closes the GUI
	 * 
	 * @param event
	 *            The ToolTakeDown-event (till now, no logic is implemented in
	 *            this event but later on, some properties might be passed to
	 *            the launcher.
	 * @see jade.core.Agent#takeDown()
	 */
	public void toolTakeDown(ToolTakeDownEvent event) {
		al.toolTakeDown();
	}

	/**
	 * This method is called, when an Exception occurs in some of the GUI- or
	 * repository-elements. Normally all Exceptions are handled by the GUI (an
	 * ExceptionDialog is presented to the user) but in case the launcher also
	 * needs to take some action, the exceptionHandling might be implemented in
	 * the method.
	 * 
	 * @param exc
	 *            The ExceptionEvent containing the exception-object.
	 */
	public void exceptionThrown(ExceptionEvent exc) {
	// HACK, a more sophisticated exceptionHandling is needed, depending on
	// the kind of exception the launcher has to be shut down...
	// this.doDelete();

	// Exception e = exc.getException();
	// e.printStackTrace();
	}

	/**
	 * Start an action, that is possibly going to last some time. The reason for
	 * starting long-lasting actions this way is, that most of these actions are
	 * working on elements which are displayed on the gui. And in order to be
	 * independent of the gui's event-thread these actions are executed in the
	 * agent's thread. Note: Using JADE's GUIAgent class is not possible,
	 * because the ASCML needs to extend ToolAgent.
	 * 
	 * @param event
	 *            The event, that contains the ID of the long-time action.
	 */
	public void startLongTimeAction(LongTimeActionStartEvent event) {
		al.addBehaviour(new LongTimeActionBehaviour(al.getRepository(), event));
	}

	/**
	 * This method is implemented, because the AgentLauncher is registered as a
	 * ModelChangedListener. It is called everytime a model changed.
	 * 
	 * @param event
	 *            The ModelChangedEvent, which contains the eventCode and the
	 *            model itself.
	 */
	public synchronized void modelChanged(ModelChangedEvent event) {
		String eventCode = event.getEventCode();
		if ((eventCode == ModelChangedEvent.STATUS_CHANGED) || (eventCode == ModelChangedEvent.RUNNABLE_REMOVED)) {
			Object eventModel;
			if (eventCode == ModelChangedEvent.STATUS_CHANGED) {
				eventModel = event.getModel();
			} else {
				eventModel = event.getUserObject();
			}
			if (eventModel instanceof IAbstractRunnable) {
				IAbstractRunnable absRunnable = (IAbstractRunnable) eventModel;
				al.getSubscriptionManager().notify(absRunnable);
			}
		}
	}
}
