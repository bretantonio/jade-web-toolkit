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

import jade.tools.ascml.repository.PropertyManager;

public class PropertyChangedEvent
{
	public static final String MODELLOCATIONS_CHANGED	= "model_locations_changed";
	public static final String EXCLUDELOCATIONS_CHANGED	= "exclude_locations_changed";
	public static final String PROJECTS_CHANGED			= "projects_changed";
	public static final String ACTIVE_PROJECT_CHANGED	= "active_project_changed";

	private PropertyManager properties;
	private String eventCode;

	public PropertyChangedEvent(String eventCode, PropertyManager properties)
	{
		this.properties = properties;
		this.eventCode = eventCode;
	}
	
	public PropertyManager getProperties()
	{
		return properties;
	}

	public String getEventCode()
	{
		return eventCode;
	}

	public String toString()
	{
		String str = "";
		str += "PropertyChangedEvent : " + eventCode + "\n";
		return str;
	}
}
