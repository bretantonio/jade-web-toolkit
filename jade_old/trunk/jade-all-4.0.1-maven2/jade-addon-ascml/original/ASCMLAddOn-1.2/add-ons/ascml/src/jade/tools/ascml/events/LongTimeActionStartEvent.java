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

import java.util.HashMap;

/**
 *
 */
public class LongTimeActionStartEvent
{
	public static String ACTION_REBUILD_MODEL_INDEX             = "rebuild_model_index";
	public static String ACTION_START_SOCIETYINSTANCE           = "start_societyinstance";
    public static String ACTION_STOP_SOCIETYINSTANCE            = "stop_societyinstance";
    public static String ACTION_WAIT_FOR_MODELCHANGE_POSTING    = "wait_for_modelchanged_posting";
	
	private String actionID;
    private HashMap parameterMap;
	private boolean actionInProgress;

	public LongTimeActionStartEvent(String actionID)
	{
        this.actionID = actionID;
		parameterMap = new HashMap();
		actionInProgress = false;
	}

	public String getActionID()
	{
		return actionID;
	}

	public void addParameter(Object key, Object value)
	{
		parameterMap.put(key, value);
	}

	public Object getParameter(Object key)
	{
		return parameterMap.get(key);
	}

	public synchronized void setActionInProgress(boolean isInProgress)
	{
		this.actionInProgress = isInProgress;
	}

	public synchronized boolean isActionInProgress()
	{
		return this.actionInProgress;
	}
}
