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


package jade.tools.ascml.gui.components.tree;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import jade.tools.ascml.repository.Project;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.model.runnable.RunnableAgentInstance;
import jade.tools.ascml.model.runnable.RunnableSocietyInstance;
import jade.tools.ascml.model.runnable.RunnableRemoteSocietyInstanceReference;
import jade.tools.ascml.model.jibx.*;
import jade.tools.ascml.absmodel.*;

public class TreeRenderer extends DefaultTreeCellRenderer
{
	private Repository repository;

	public TreeRenderer(Repository repository)
	{
		super();
		this.repository = repository;

		setFont(new Font("Arial", Font.PLAIN, 12));
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel,
	                          boolean expanded, boolean leaf, int row, boolean hasFocus) 
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        String view = repository.getProject().getView();

		Object nodeContent = ((DefaultMutableTreeNode)value).getUserObject();

		if ((nodeContent instanceof ISocietyType) || (nodeContent instanceof ISocietyInstance) || (nodeContent instanceof ISocietyInstanceReference) ||
				(nodeContent instanceof IAgentType) || (nodeContent instanceof IAgentInstance))
		{
			processStaticModel(nodeContent);
		}
        else if ((nodeContent instanceof IRunnableSocietyInstance) || (nodeContent instanceof IRunnableRemoteSocietyInstanceReference) ||
				(nodeContent instanceof IRunnableAgentInstance))
		{
			processRunnableModel(nodeContent);
		}
		else if (nodeContent instanceof ModelException)
		{
			setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.EXCEPTION_MAIN, 14, 14));
		}
		else if (nodeContent instanceof String)
		{
			String nodeString = (String)nodeContent;
            if (nodeString.startsWith(RepositoryTree.REPOSITORY_STRING) && (view == Project.BASIC_VIEW))
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.TREEVIEW_BASIC, 14, 14));
			else if (nodeString.startsWith(RepositoryTree.REPOSITORY_STRING) && (view == Project.EXPERT_VIEW))
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.TREEVIEW_EXPERT, 14, 14));
			else if (nodeString == RepositoryTree.ADD_A_MODEL_HEADER)
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.TREEICON_BASIC_ADD, 14, 14));
			else if (nodeString == RepositoryTree.ADD_AGENTMODEL_STRING)
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.AGENTTYPE, 14, 14));
			else if (nodeString == RepositoryTree.ADD_SOCIETYMODEL_STRING)
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYTYPE, 14, 14));
			else if (nodeString == RepositoryTree.AGENTTYPES_STRING)
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.TREEICON_FOLDER_AGENTTYPES, 14, 14));
			else if (nodeString ==  RepositoryTree.SOCIETYTYPES_STRING)
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.TREEICON_FOLDER_SOCIETYTYPES, 14, 14));
			else if (nodeString ==  RepositoryTree.REFERENCE_DESCRIPTION_STRING)
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.REFERENCE_DESCRIPTION, 14, 14));
			else if (nodeString ==  RepositoryTree.AGENT_DESCRIPTION_STRING)
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.AGENT_DESCRIPTION, 14, 14));
			else if (nodeString ==  RepositoryTree.RUNNINGINSTANCES_STRING)
				setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.RUNNING_INSTANCES, 14, 14));
			else
				setIcon(null);

			setToolTipText(null);
		}
		return this;
	}

	private void processStaticModel(Object nodeContent)
	{
        if (nodeContent instanceof ISocietyType)
		{
			ISocietyType societyType = (ISocietyType)nodeContent;
			ImageIcon typeIcon = societyType.getIcon();
			String status = societyType.getStatus();

			if (status == ISocietyType.STATUS_ERROR)
				typeIcon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_ERROR);
			// else if (status == SocietyType.STATUS_REFERENCE_ERROR)
				// typeIcon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_REFERENCE_ERROR);

			setIcon(ImageIconLoader.scaleImageIcon(typeIcon, 14, 14));
			setToolTipText("SocietyType: "+((ISocietyType)nodeContent).getDocument().getSource());
		}
		else if (nodeContent instanceof ISocietyInstance)
		{
			ISocietyInstance societyInstance = (ISocietyInstance)nodeContent;
			ImageIcon icon = ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE);
			String status = societyInstance.getStatus();

			if (status == ISocietyInstance.STATUS_ERROR)
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_ERROR);
			else if (status == ISocietyInstance.STATUS_REFERENCE_ERROR)
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_REFERENCE_ERROR);

			setIcon(ImageIconLoader.scaleImageIcon(icon, 14, 14));
			setToolTipText("SocietyInstance: "+((ISocietyInstance)nodeContent).getName());
		}
		else if (nodeContent instanceof ISocietyInstanceReference)
		{
			ISocietyInstanceReference referenceModel = (ISocietyInstanceReference)nodeContent;
			ImageIcon icon = null;
			String status = referenceModel.getStatus();

			if (status == ISocietyInstanceReference.STATUS_ERROR)
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_ERROR);
			else if (status == ISocietyInstanceReference.STATUS_REFERENCE_ERROR)
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_REFERENCE_ERROR);
			else if (referenceModel.isRemoteReference())
			{
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE_REFERENCE_REMOTE);
				setToolTipText("remote SocietyInstance-Reference: " + referenceModel.getName());
			}
			else
			{
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE_REFERENCE_LOCAL);
				setToolTipText("SocietyInstance-Reference: " + referenceModel.getName());
			}

			setIcon(ImageIconLoader.scaleImageIcon(icon, 14, 14));
		}
		else if (nodeContent instanceof IAgentInstance)
		{
			IAgentInstance agentInstance = (IAgentInstance)nodeContent;
			ImageIcon icon = null;
			String status = agentInstance.getStatus();

			if (status == IAgentInstance.STATUS_ERROR)
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_ERROR);
			else if (status == IAgentInstance.STATUS_REFERENCE_ERROR)
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_REFERENCE_ERROR);
			else
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.AGENTINSTANCE);

			setIcon(ImageIconLoader.scaleImageIcon(icon, 14, 14));
			setToolTipText("Agent-Instance: " + agentInstance.getName());
		}
		else if (nodeContent instanceof IAgentType)
		{
			IAgentType agentType = (IAgentType)nodeContent;
			ImageIcon icon = agentType.getIcon();
			String status = agentType.getStatus();

			if (status == IAgentType.STATUS_ERROR)
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_ERROR);
			else if (status == IAgentType.STATUS_REFERENCE_ERROR)
				icon = ImageIconLoader.createImageIcon(ImageIconLoader.STATUS_MODEL_REFERENCE_ERROR);

			setIcon(ImageIconLoader.scaleImageIcon(icon, 14, 14));
			setToolTipText("AgentType: " + agentType.getDocument().getSource());
		}

	}

	private void processRunnableModel(Object nodeContent)
	{
		if (nodeContent instanceof IRunnableSocietyInstance)
		{
			RunnableSocietyInstance runnableInstance = (RunnableSocietyInstance)nodeContent;
			Status status = runnableInstance.getStatus();

			setIcon(ImageIconLoader.createRunnableStatusIcon(status, 16, 16));
			setToolTipText("Runnable SocietyInstance: "+runnableInstance.getName()+ " Status:" + status);
		}
		else if (nodeContent instanceof IRunnableRemoteSocietyInstanceReference)
		{
			RunnableRemoteSocietyInstanceReference instanceReference = (RunnableRemoteSocietyInstanceReference)nodeContent;
			Status status = instanceReference.getStatus();

			setIcon(ImageIconLoader.createRunnableStatusIcon(status, 16, 16));

			setToolTipText("remote SocietyInstance: " + instanceReference.getName() + " (" + status + ")");
		}
		else if (nodeContent instanceof IRunnableAgentInstance)
		{
			RunnableAgentInstance agentInstance = (RunnableAgentInstance)nodeContent;

			Status status = agentInstance.getStatus();
			String detailedStatus = agentInstance.getDetailedStatus();

			if (!detailedStatus.equals(""))
				setToolTipText("<html>Agent: <i>" + agentInstance.getName() + " </i><br>Status:<i>" + detailedStatus + "</i></html>");
			else
				setToolTipText("<html>Agent: <i>" + agentInstance.getName() + " </i>Status:<i>" + status + "</i></html>");

			setIcon(ImageIconLoader.createRunnableStatusIcon(status, 16, 16));
		}
	}

	public void exit()
	{
	}

	/**
	 * This method has to be implemented because of Java-Bug No. 4743195.
	 * JLabels containing html-Strings vanish in the tree when using Windows Look & Feel.
	 * @param propertyName  see super-method
	 * @param oldValue  see super-method
	 * @param newValue  see super-method
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
	{
		if (propertyName=="foreground")
		{
			propertyName = "text";
		}
		super.firePropertyChange(propertyName, oldValue, newValue);
	}
}
