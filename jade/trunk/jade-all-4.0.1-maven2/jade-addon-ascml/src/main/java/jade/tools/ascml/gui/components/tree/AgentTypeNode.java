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
import java.util.*;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.IAbstractRunnable;

public class AgentTypeNode extends DefaultMutableTreeNode implements IRepositoryTreeNode
{
	private IAgentType model = null;
	private Repository repository;
    private DefaultTreeModel treeModel;

	public AgentTypeNode(IAgentType model, Repository repository, DefaultTreeModel treeModel)
	{
		super(model);
		this.model = model;
		this.repository = repository;
        this.treeModel = treeModel;
		repository.getListenerManager().addModelChangedListener(this);
		init();
	}

	private void init()
	{
		// clean up
		this.removeAllChildren();
		
		// add new children (running instances)
		IAbstractRunnable[] runningInstances = repository.getRunnableManager().getRunnables(model);
		for (int i=0; i < runningInstances.length; i++)
		{
			addRunnableInstanceNode(runningInstances[i]);
		}
	}

	private void addRunnableInstanceNode(IAbstractRunnable runnableInstance)
	{
		DefaultMutableTreeNode oneInstanceNode = new DefaultMutableTreeNode(runnableInstance);
		// System.err.println("AgentTypeNode.addRunnableInstanceNode: " + runnableInstance);
        treeModel.insertNodeInto(oneInstanceNode, this, 0);
	}

	private void removeRunnableInstanceNode(IAbstractRunnable runnableInstanceToRemove)
	{
		Enumeration allRunnableNodes = this.children();
		while (allRunnableNodes.hasMoreElements())
		{
			DefaultMutableTreeNode oneNode = ((DefaultMutableTreeNode)allRunnableNodes.nextElement());
			IAbstractRunnable oneRunnableInstance = (IAbstractRunnable)oneNode.getUserObject();
			if (runnableInstanceToRemove == oneRunnableInstance)
			{
                // System.err.println("AgentTypeNode.removeRunnableInstanceNode: " + runnableInstanceToRemove);
                treeModel.removeNodeFromParent(oneNode);
				return;
			}
		}
	}

	public void modelChanged(ModelChangedEvent event)
	{
		Object model = event.getModel();
		String eventCode = event.getEventCode();

		if (this.model == model)
		{
			if (eventCode == ModelChangedEvent.RUNNABLE_ADDED)
				addRunnableInstanceNode((IAbstractRunnable)event.getUserObject());
			else if (eventCode == ModelChangedEvent.RUNNABLE_REMOVED)
				removeRunnableInstanceNode((IAbstractRunnable)event.getUserObject());
		}
	}

	public void exit()
	{
		repository.getListenerManager().removeModelChangedListener(this);
	}
}
