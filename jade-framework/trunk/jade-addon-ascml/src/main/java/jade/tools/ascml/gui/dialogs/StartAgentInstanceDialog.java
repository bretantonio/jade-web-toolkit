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


package jade.tools.ascml.gui.dialogs;

import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.gui.panels.AbstractMainPanel;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.IAbstractRunnable;

import javax.swing.*;

/**
 *  Presents a dialog to the user asking for a name for the agentInstance.
 */
public class StartAgentInstanceDialog extends AbstractDialog
{
	private IAgentType agentTypeModel;
	private AbstractMainPanel mainPanel;

	public StartAgentInstanceDialog(AbstractMainPanel mainPanel, IAgentType agentTypeModel)
	{
		super(mainPanel.getRepository());
		this.mainPanel = mainPanel;
		this.agentTypeModel = agentTypeModel;

		// toDO: Im Dialog muessten noch Parameter abgefragt werden, die verplichtend sind,
		// toDo: noch keinen Wert zugewiesen bekamen (welche eigentlich durchs AgentInstance-Models belegt werden muessten
	}

	public Object showDialog(JFrame parentFrame)
	{
		Object result = JOptionPane.showInputDialog(parentFrame, "<html>Please provide a name for the AgentInstance !<p>You may give the agent a name of your choice, <br>but beware, names have to be unique !<br></html>",
				"Start AgentInstance...", JOptionPane.QUESTION_MESSAGE);

		if ((result != null) && (!((String)result).trim().equals("")))// start
		{
			String instanceName = ((String)result).trim();
			try
			{
				IAbstractRunnable[] runnableModels = (IAbstractRunnable[])repository.createRunnableAgentInstance(agentTypeModel.getFullyQualifiedName() + "." + instanceName, 1);

				// and select the newly created instance
				mainPanel.selectModel(runnableModels[0]);

				// Throw the start-event
				for (int i=0; i < runnableModels.length; i++)
				{
					ModelActionEvent actionEvent = new ModelActionEvent(ModelActionEvent.CMD_START_AGENTINSTANCE, runnableModels[i]);
					mainPanel.throwModelActionEvent(actionEvent);
				}
				
				return null;
			}
			catch(ModelException exc)
			{
				mainPanel.throwExceptionEvent(exc);
			}
		}
		return null;
	}
}
	
