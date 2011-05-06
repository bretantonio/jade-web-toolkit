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


package jade.tools.ascml.events;

public class ProgressUpdateEvent
{
	public final static String PROGRESS_FINISHED	= "progress_finished";
	public final static String PROGRESS_ADVANCE		= "progress_advance";

	private String eventCode;
	private String currentProgress;

	public ProgressUpdateEvent(String currentProgress, String eventCode)
	{
        this(eventCode);
		setCurrentProgress(currentProgress);
	}

	public ProgressUpdateEvent(String eventCode)
	{
        this.eventCode = eventCode;
	}

	public String getEventCode()
	{
		return eventCode;
	}

	public void setCurrentProgress(String currentProgress)
	{
		this.currentProgress = currentProgress;
	}

	public String getCurrentProgress()
	{
		return currentProgress;
	}
}
