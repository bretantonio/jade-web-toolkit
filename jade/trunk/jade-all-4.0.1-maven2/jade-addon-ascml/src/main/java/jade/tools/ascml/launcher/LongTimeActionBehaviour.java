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

import jade.core.behaviours.OneShotBehaviour;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.*;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.absmodel.ISocietyInstance;

/**
 * 
 */
public class LongTimeActionBehaviour extends OneShotBehaviour
{
	private Repository repository;
    private LongTimeActionStartEvent event;

	public LongTimeActionBehaviour(Repository repository, LongTimeActionStartEvent event)
	{
		this.repository = repository;
		this.event = event;
	}

	public void action()
	{
        if (!event.isActionInProgress())
		{
			event.setActionInProgress(true);
			if (event.getActionID() == LongTimeActionStartEvent.ACTION_REBUILD_MODEL_INDEX)
			{
				repository.getModelManager().rebuildModelIndex();
				repository.throwProgressUpdateEvent(new ProgressUpdateEvent("Finished ... ", ProgressUpdateEvent.PROGRESS_FINISHED));
			}
			else if (event.getActionID() == LongTimeActionStartEvent.ACTION_START_SOCIETYINSTANCE)
			{
				ModelActionListener listener = (ModelActionListener)event.getParameter("listener");
				ModelActionEvent actionEvent = (ModelActionEvent)event.getParameter("event");

				try
				{
					// System.err.println("LongTimeActionBehaviour: throw modelActionPerformed");
					listener.modelActionPerformed(actionEvent);
				}
				catch (ModelActionException e)
				{
					repository.throwExceptionEvent(e);
				}

				// don't throw Finish-event, because it is thrown by the ProgressDialog itself once
				// all agentinstances have been started
			}
            else if (event.getActionID() == LongTimeActionStartEvent.ACTION_STOP_SOCIETYINSTANCE)
			{
				ModelActionListener listener = (ModelActionListener)event.getParameter("listener");
				ModelActionEvent actionEvent = (ModelActionEvent)event.getParameter("event");

				try
				{
					listener.modelActionPerformed(actionEvent);
				}
				catch (ModelActionException e)
				{
					repository.throwExceptionEvent(e);
				}

                repository.throwProgressUpdateEvent(new ProgressUpdateEvent("Finished ... ", ProgressUpdateEvent.PROGRESS_FINISHED));

				// don't throw Finish-event, because it is thrown by the ProgressDialog itself once
				// all agentinstances have been started
			}
/*            else if (event.getActionID() == LongTimeActionStartEvent.ACTION_WAIT_FOR_MODELCHANGE_POSTING)
			{
				System.err.println("LongTimeActionBehaviour.action: ACTION_WAIT_FOR_MODELCHANGE_POSTING");

				// extract parameters
				ISocietyInstance societyInstance = (ISocietyInstance)event.getParameter("callback");
				long waitFor = ((Long)event.getParameter("waitfor")).intValue();
				String eventCode = (String)event.getParameter("eventcode");
				Object userObject = event.getParameter("userobject");

                this.block(waitFor);

				System.err.println("LongTimeActionBehaviour.action: FINISH_MODELCHANGE_POSTING");
				societyInstance.reallyThrowModelChangedEvent(eventCode, userObject);
			}*/

		}
	}
}
