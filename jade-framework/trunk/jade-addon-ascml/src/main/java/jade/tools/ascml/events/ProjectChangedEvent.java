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

import jade.tools.ascml.repository.Project;

public class ProjectChangedEvent
{
	/** These constants are valid 'event-codes'. */
	public static final String SOCIETYTYPE_ADDED			= "societytype_added";
    public static final String SOCIETYTYPE_REMOVED			= "societytype_removed";

	public static final String AGENTTYPE_ADDED				= "agenttype_added";
    public static final String AGENTTYPE_REMOVED			= "agenttype_removed";

	/** This constant is used for selecting a SocietyType in the repository-tree **/
	public static final String SOCIETYTYPE_SELECTED 		= "societytype_selected";

	/** This constant is used for selecting an AgentType in the repository-tree **/
	public static final String AGENTTYPE_SELECTED 			= "agenttype_selected";

	/** This constant is used for selecting a SocietyInstance in the repository-tree **/
	public static final String SOCIETYINSTANCE_SELECTED 	= "societyinstance_selected";

	/** This constant is used for selecting an AgentInstance in the repository-tree **/
	public static final String AGENTINSTANCE_SELECTED 		= "agentinstance_selected";

	/** Runnable removed, QUICK AND DIRTY (caught by TreeModels) */
	public static final String RUNNABLE_REMOVED = "runnable_removed";

	/** This constant is used for selecting a SocietyInstance-reference in the repository-tree **/
	public static final String SOCIETYINSTANCE_REFERENCE_SELECTED 	= "societyinstance_reference_selected";

	public static final String REMOTE_REPOSITORIES_CHANGED	= "remote_repositories_changed";
	public static final String VIEW_CHANGED					= "view_changed";
	public static final String WORKING_DIRECTORY_CHANGED	= "working_directory_changed";

	private Project project;
	private String eventCode;
	private Object model;

	public ProjectChangedEvent(String eventCode, Object model, Project project)
	{
		this(eventCode, project);
		this.model = model;
	}

	public ProjectChangedEvent(String eventCode, Project project)
	{
		this.eventCode = eventCode;
		this.project = project;
	}

	public Project getProject()
	{
		return project;
	}

	public String getEventCode()
	{
		return eventCode;
	}

	public Object getModel()
	{
		return model;
	}

	public String toString()
	{
		String str = "";
		str += "ProjectChangedEvent : " + project.toString() + "\n";
		return str;
	}
}
