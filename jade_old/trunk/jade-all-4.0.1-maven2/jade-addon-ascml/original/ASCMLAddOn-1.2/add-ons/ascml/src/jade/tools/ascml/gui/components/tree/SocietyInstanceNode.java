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

import javax.swing.tree.*;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.absmodel.*;

import java.util.Enumeration;

public class SocietyInstanceNode extends DefaultMutableTreeNode implements IRepositoryTreeNode
{
	private ISocietyInstance societyModel = null;
	private String subTreeLevelToShow;
	private Repository repository;
    private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode runnableRootNode;

	/**
	 *
	 * @param subTreeLevelToShow  This is number represents the nodes subtree-depth.
	 * 0 means, only the SocietyTypeNode should be shown, nothing more.
	 * 1 means, the SocietyTypeNode, and the SocietyInstanceNodes should be shown, because these
	 * are 1 level deeper; same with 3, 4, and so on.
	 */
	public SocietyInstanceNode(ISocietyInstance societyModel, String subTreeLevelToShow, Repository repository, DefaultTreeModel treeModel)
	{
		super(societyModel);
		this.societyModel = societyModel;
		this.subTreeLevelToShow = subTreeLevelToShow;
		this.repository = repository;
        this.treeModel = treeModel;
		repository.getListenerManager().addModelChangedListener(this);
		init();
	}

	private void init()
	{
		if (!subTreeLevelToShow.equals(SocietyTypeNode.SUBTREE_LEVEL_SOCIETYINSTANCE))
		{
			// clean up first
            int childCount = this.getChildCount();
            for (int i=0; i < childCount; i++)
            {
                treeModel.removeNodeFromParent((DefaultMutableTreeNode)getChildAt(0));
            }

			if (subTreeLevelToShow.equals(SocietyTypeNode.SUBTREE_LEVEL_RUNNINGINSTANCES_ONLY))
			{
				// When only the template-nodes should be shown, than a special handling is needed.
				// In this case, all the templates (agentInstances and SocietyReferences are
				// directly connected with the societyInstance, otherwise separating String-nodes are added
				// between them
				runnableRootNode = this;
				createRunnableInstanceNodes(false);
			}
			else if (subTreeLevelToShow.equals(SocietyTypeNode.SUBTREE_LEVEL_TEMPLATES_ONLY))
			{
				createTemplateNodes(this, false);
			}
			else // show both with details
			{
                // System.err.println("SocietyInstanceNode.init: create runnable & templates-node");
				createTemplateNodes(this, true);

				runnableRootNode = new DefaultMutableTreeNode(RepositoryTree.RUNNINGINSTANCES_STRING);

				createRunnableInstanceNodes(true);
				treeModel.insertNodeInto(runnableRootNode, this, 1);
			}
		}
	}

	private void createRunnableInstanceNodes(boolean showDetails)
	{
		IAbstractRunnable[] runningInstances = repository.getRunnableManager().getRunnables(societyModel);
		for (int i=0; i < runningInstances.length; i++)
		{
			IRunnableSocietyInstance oneRunningInstance = (IRunnableSocietyInstance)runningInstances[i];

			// create the root-node of a running society-instance. This root-Node contains a String
			// representing the name of the running societyInstance. This name is given by the user
			// Also create the child-nodes "Agents" and "References" and add them to the root-node
			RunnableSocietyInstanceNode oneRunnableSocInstNode = new RunnableSocietyInstanceNode(oneRunningInstance, showDetails, repository, treeModel);
			treeModel.insertNodeInto(oneRunnableSocInstNode, runnableRootNode, runnableRootNode.getChildCount());
		}
	}

	private void createTemplateNodes(DefaultMutableTreeNode rootNode, boolean splitAgentsAndReferences)
	{
		if (splitAgentsAndReferences)
		{
			DefaultMutableTreeNode agentsNode = new DefaultMutableTreeNode(RepositoryTree.AGENT_DESCRIPTION_STRING);
			addAgentInstanceTemplateNodes(agentsNode);
            treeModel.insertNodeInto(agentsNode, rootNode, 0);

			DefaultMutableTreeNode referencesNode = new DefaultMutableTreeNode(RepositoryTree.REFERENCE_DESCRIPTION_STRING);
			addSocietyInstanceReferenceTemplateNodes(referencesNode);
            treeModel.insertNodeInto(referencesNode, rootNode, 1);
		}
		else
		{
			addAgentInstanceTemplateNodes(rootNode);
			addSocietyInstanceReferenceTemplateNodes(rootNode);
		}
	}

	private void addAgentInstanceTemplateNodes(DefaultMutableTreeNode rootNode)
	{
		IAgentInstance[] agentInstances = societyModel.getAgentInstanceModels();
		for (int i=0; i < agentInstances.length; i++)
		{
			DefaultMutableTreeNode agentInstanceNode = new DefaultMutableTreeNode(agentInstances[i]);
			treeModel.insertNodeInto(agentInstanceNode, rootNode, 0);
		}
	}

	private void addSocietyInstanceReferenceTemplateNodes(DefaultMutableTreeNode rootNode)
	{
		ISocietyInstanceReference[] socInstRefs = societyModel.getSocietyInstanceReferences();
		for (int i=0; i < socInstRefs.length; i++)
		{
			ISocietyInstanceReference oneReference = socInstRefs[i];
			DefaultMutableTreeNode oneReferenceNode = new DefaultMutableTreeNode(oneReference);
			treeModel.insertNodeInto(oneReferenceNode, rootNode, 0);
		}
	}

	public void modelChanged(ModelChangedEvent event)
	{
		Object model = event.getModel();
		if (model == societyModel)
		{
            String eventCode = event.getEventCode();
			if (eventCode == ModelChangedEvent.RUNNABLE_ADDED)
			{
				boolean showDetails = (subTreeLevelToShow == SocietyTypeNode.SUBTREE_LEVEL_RUNNINGINSTANCES_ONLY) ||
				        ((subTreeLevelToShow != SocietyTypeNode.SUBTREE_LEVEL_TEMPLATES_ONLY) && (subTreeLevelToShow != SocietyTypeNode.SUBTREE_LEVEL_RUNNINGINSTANCES_ONLY));

				IRunnableSocietyInstance runnableModel = (IRunnableSocietyInstance)event.getUserObject();
				RunnableSocietyInstanceNode oneRunnableSocInstNode = new RunnableSocietyInstanceNode(runnableModel, showDetails, repository, treeModel);
				treeModel.insertNodeInto(oneRunnableSocInstNode, runnableRootNode, 0);
			}
			else if (eventCode == ModelChangedEvent.RUNNABLE_REMOVED)
			{
				IRunnableSocietyInstance runnableModel = (IRunnableSocietyInstance)event.getUserObject();
				Enumeration nodes = runnableRootNode.breadthFirstEnumeration();
				while (nodes.hasMoreElements())
				{
					DefaultMutableTreeNode oneNode = (DefaultMutableTreeNode)nodes.nextElement();
					if (oneNode.getUserObject() == runnableModel)
						treeModel.removeNodeFromParent(oneNode);
				}
			}
			else
			{
				// System.err.println("SocietyInstanceNode.modelChanged: complete re-init, eventCode=" + event.getEventCode());
				init();
                treeModel.nodeChanged(this);
			}
		}
	}

	public void exit()
	{
		repository.getListenerManager().removeModelChangedListener(this);
	}
}
