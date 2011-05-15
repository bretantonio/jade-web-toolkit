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

public class ModelChangedEvent
{
	/** indicates, that the status of a RunnableModel has changed */
	public static final String STATUS_CHANGED = "status_changed";

	/** indicates, that the detailed status-message of a RunnableModel has changed */
	public static final String DETAILED_STATUS_CHANGED = "detailed_status_changed";

	/** indicates, that a RunnableModel has been added */
	public static final String RUNNABLE_ADDED = "runnable_added";

	/** indicates, that a RunnableModel has been added */
	public static final String RUNNABLE_REMOVED = "runnable_removed";

	/** indicates, that an AgentInstance has been added to a SocietyInstance */
	public static final String AGENTINSTANCE_ADDED = "agentinstance_added";

	/** indicates, that an AgentInstance has been removed from a SocietyInstance */
	public static final String AGENTINSTANCE_REMOVED = "agentinstance_removed";

	/** indicates, that a SocietyInstance has been added to a SocietyType */
	public static final String SOCIETYINSTANCE_ADDED = "societyinstance_added";

	/** indicates, that a SocietyInstance has been removed from a SocietyType */
	public static final String SOCIETYINSTANCE_REMOVED = "societyinstance_removed";

	/** indicates, that a SocietyInstance-reference has been added to a SocietyInstance */
	public static final String SOCIETYINSTANCE_REFERENCE_ADDED = "societyinstance_reference_added";

	/** indicates, that a SocietyInstance-reference has been removed from a SocietyInstance */
	public static final String SOCIETYINSTANCE_REFERENCE_REMOVED = "societyinstance_reference_removed";

	/** indicates, that the type-name (and therefore the type) of an agentinstance changed */
	public static final String TYPENAME_CHANGED = "typename_changed";

	/** indicates, that the type of an agentinstance changed */
	public static final String TYPE_CHANGED = "type_changed";

	/** indicates, that the name of the model has changed */
	public static final String NAME_CHANGED = "name_changed";

	/** indicates, that the package-name of the model has changed */
	public static final String PACKAGE_CHANGED = "package_changed";

	/** indicates, that the class of the model has changed */
	public static final String CLASS_CHANGED = "class_changed";

	/** indicates, that the description of the model has changed */
	public static final String DESCRIPTION_CHANGED = "description_changed";

	/** indicates, that the platform-type of the model has changed */
	public static final String PLATFORM_CHANGED = "platform_changed";

	/** indicates, that the icon of the model has changed */
	public static final String ICON_CHANGED = "platform_changed";

	/** indicates, that the default-SocietyInstance of a SocietyType has changed */
	public static final String DEFAULT_INSTANCE_CHANGED = "default_instance_changed";

	private Object model;
	private Object userObject;
	private String eventCode;

	public ModelChangedEvent(Object model, String eventCode)
	{
		this.model = model;
		this.eventCode = eventCode;
	}

	public ModelChangedEvent(Object model, String eventCode, Object userObject)
	{
		this(model, eventCode);
		this.userObject = userObject;
	}

	public Object getModel()
	{
		return model;
	}

	public Object getUserObject()
	{
		return userObject;
	}

	public String getEventCode()
	{
		return eventCode;
	}

	public String toString()
	{
		String str = "";
		str += "ModelChangedEvent : " + model.toString() + "\n";
		return str;
	}
}
