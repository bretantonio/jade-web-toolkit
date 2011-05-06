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

import javax.swing.*;
import java.awt.event.*;

import jade.tools.ascml.gui.panels.AbstractMainPanel;
import jade.tools.ascml.events.ProjectListener;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.repository.Project;

public class ViewMenu extends JMenu implements ActionListener, ProjectListener
{
	private JRadioButtonMenuItem miRadioExpert;
	private JRadioButtonMenuItem miRadioBasic;
	private AbstractMainPanel parentPanel;

	public ViewMenu(AbstractMainPanel parentPanel)
	{		
		super("Change View");
        this.parentPanel = parentPanel;
        this.parentPanel.getRepository().getListenerManager().addProjectListener(this);

		ButtonGroup radioButtons = new ButtonGroup();

		miRadioExpert = new JRadioButtonMenuItem("Expert-View");
		miRadioExpert.addActionListener(this);

		miRadioBasic = new JRadioButtonMenuItem("Basic-View");
		miRadioBasic.addActionListener(this);
		
		radioButtons.add(miRadioBasic);
		radioButtons.add(miRadioExpert);

		this.add(miRadioBasic);
		this.add(miRadioExpert);

		selectRadioButton(parentPanel.getProject().getView());
	}

	private void selectRadioButton(String treeView)
	{
		if (treeView.equals(Project.BASIC_VIEW))
			miRadioBasic.setSelected(true);
		else if (treeView.equals(Project.EXPERT_VIEW))
			miRadioExpert.setSelected(true);
	}

	/**
	 * This method has to be implemented, because the ViewMenu is a ProjectListener.
	 * In case a new view is set, a ProjectChangedEvent is triggered and this method will be
	 * called by the Project.
	 * @param event  The event-object; this contains the Project-object.
	 */
	public void projectChanged(ProjectChangedEvent event)
	{
		if (event.getEventCode() == ProjectChangedEvent.VIEW_CHANGED)
			selectRadioButton(event.getProject().getView());
	}

	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		if (source == miRadioBasic)
		{
			parentPanel.setView(Project.BASIC_VIEW);
		}
		else if (source == miRadioExpert)
		{
            parentPanel.setView(Project.EXPERT_VIEW);
		}
	}

	/**
	 * Finalize the ViewMenu. The Menu is deregistered as project-listener.
	 */
	public void exit()
	{
		this.parentPanel.getRepository().getListenerManager().removeProjectListener(this);
	}
}
