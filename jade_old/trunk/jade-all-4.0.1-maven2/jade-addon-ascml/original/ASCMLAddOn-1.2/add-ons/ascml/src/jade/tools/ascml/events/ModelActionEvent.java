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

import java.util.*;

public class ModelActionEvent
{
    // These constants are used exclusivly by the gui-classes to force
	// the ModelActionListeners to initiate some action

	/* should these be used ???
	public static final String MODEL_CMD_START_AGENT	= "start_agentinstance";
	public static final String MODEL_CMD_RESTART_AGENT	= "restart_agentinstance";
	public static final String MODEL_CMD_STOP_AGENT		= "stop_agentinstance";

	public static final String MODEL_CMD_START_SOCIETYINSTANCE		= "start_societyinstance";
	public static final String MODEL_CMD_RESTART_SOCIETYINSTANCE	= "restart_societyinstance";
	public static final String MODEL_CMD_STOP_SOCIETYINSTANCE		= "stop_societyinstance";
    */

	// These constants are used for communicating with the LauncherInterface.
	public static final String CMD_START_AGENTINSTANCE		= "start_runnable_agentinstance";
	public static final String CMD_STOP_AGENTINSTANCE		= "stop_runnable_agentinstance";
	public static final String CMD_START_SOCIETYINSTANCE	= "start_runnable_societyinstance";
	public static final String CMD_STOP_SOCIETYINSTANCE		= "stop_runnable_societyinstance";

	private String actionCommand;
	private Object model;
	private HashMap parameterMap;

	public ModelActionEvent(String actionCommand, Object model)
	{
		this.actionCommand = actionCommand;
		this.model = model;
		parameterMap = new HashMap();
	}

	public Object getModel()
	{
		return model;
	}

	public String getActionCommand()
	{
		return actionCommand;
	}

	public void setParameter(String key, Object value)
	{
		parameterMap.put(key, value);
	}

	public Object getParameter(String key)
	{
		return parameterMap.get(key);
	}

	public String toString()
	{
		String str = "";
		str += "ActionCommand: " + actionCommand + "\n";
		str += "Model        : " + model.toString() + "\n";

		Iterator keys = parameterMap.keySet().iterator();
		while(keys.hasNext())
		{
			String oneKey = (String)keys.next();
			str += "Parameter   : key = "+oneKey+ " val = "+parameterMap.get(oneKey)+"\n";
		}

		return str;
	}
}
