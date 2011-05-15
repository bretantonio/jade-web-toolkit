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


package jade.tools.ascml.gui.components;

import jade.tools.ascml.repository.PropertyManager;
import jade.tools.ascml.repository.Project;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.PropertyChangedEvent;
import jade.tools.ascml.events.PropertyListener;
import jade.tools.ascml.gui.panels.AbstractPanel;
import jade.tools.ascml.exceptions.ModelException;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Iterator;

public class ProjectMenu extends JMenu implements PropertyListener, ActionListener
{
	private static final String CHANGE_PROJECT = "change_project:";
	private Repository repository;

	/**
	 * Initialize the Menu itself. The menuItems are added later on, once a
	 * RepositoryPropertiesChanged-event occured.
	 * @param parentPanel  This menu's parentPanel. The parentPanel is needed to have access
	 * to the repository, which itself is needed for setting project-changes.
	 * The setActiveProject-method is called upon the repository-object, when the user chooses
	 * another active project.
	 */
	public ProjectMenu(AbstractPanel parentPanel)
	{
		super("Change Project");
        this.repository = parentPanel.getRepository();
		repository.getListenerManager().addPropertyListener(this);
		buildProjectMenu(parentPanel.getProperties());
	}

	private void buildProjectMenu(PropertyManager properties)
	{
		this.removeAll();

		ButtonGroup radioButtons = new ButtonGroup();

		String[] projectNames = properties.getProjectNames();
		String activeProjectName = properties.getActiveProject().getName();

		for (int i=0; i < projectNames.length; i++)
		{
			JRadioButtonMenuItem oneRadioButton = new JRadioButtonMenuItem(projectNames[i]);
			oneRadioButton.addActionListener(this);
			oneRadioButton.setActionCommand(CHANGE_PROJECT + projectNames[i]);
			if (projectNames[i] == activeProjectName)
			{
				// System.err.println("ProjectMenu: active Project: " + activeProjectName);
				oneRadioButton.setSelected(true);
			}

			radioButtons.add(oneRadioButton);
			this.add(oneRadioButton);
		}
	}

	/**
	 * This method has to be implemented because of the PropertyListener-interface.
	 * It is called, when the properties have changed. If the change affected the project-properties
	 * (for example if a project has been added/removed, the Menu has to be updated.
	 * @param event  The RepositoryPropertiesChanged-event.
	 */
	public void propertiesChanged(PropertyChangedEvent event)
	{
		if ((event.getEventCode() == PropertyChangedEvent.PROJECTS_CHANGED) ||
				(event.getEventCode() == PropertyChangedEvent.ACTIVE_PROJECT_CHANGED))
		{
			buildProjectMenu(event.getProperties());
		}
	}

	/**
	 * This method has to be implemented because of the PropertyListener-interface.
	 * It is called, when the properties have changed. If the change affected the project-properties
	 * (for example if a project has been added/removed, the Menu has to be updated.
	 * @param event  The RepositoryPropertiesChanged-event.
	 */
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		if (command.startsWith(CHANGE_PROJECT))
		{
			String projectName = command.substring(CHANGE_PROJECT.length(), command.length());
		    // User chose to change project, so set new project in the repository
			repository.setActiveProject(projectName);
		}
	}

	public void exit()
	{
		repository.getListenerManager().removePropertyListener(this);
	}
}
