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
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.absmodel.IRunnableAgentInstance;
import jade.tools.ascml.absmodel.IRunnableRemoteSocietyInstanceReference;
import jade.tools.ascml.absmodel.IRunnableSocietyInstance;

public class RunnableSocietyInstanceNode extends DefaultMutableTreeNode implements IRepositoryTreeNode
{

	private IRunnableSocietyInstance runnableModel = null;
	private boolean showDetails;
	private DefaultTreeModel treeModel;
	private Repository repository;

	public RunnableSocietyInstanceNode(IRunnableSocietyInstance runnableModel, boolean showDetails, Repository repository, DefaultTreeModel treeModel)
	{
		super(runnableModel);
		this.runnableModel = runnableModel;
		this.showDetails = showDetails;
		this.treeModel = treeModel;
		this.repository = repository;
		repository.getListenerManager().addModelChangedListener(this);
		init();
	}

	private void init()
	{
		if (showDetails)
		{
			// clean up first
            int childCount = this.getChildCount();
            for (int i=0; i < childCount; i++)
            {
                treeModel.removeNodeFromParent((DefaultMutableTreeNode)getChildAt(0));
            }
            treeModel.insertNodeInto(createAgentsNodes(), this, 0);
			treeModel.insertNodeInto(createReferencesNodes(), this, 1);
		}
	}
	
	private DefaultMutableTreeNode createAgentsNodes()
	{
		DefaultMutableTreeNode agentsNode = new DefaultMutableTreeNode(RepositoryTree.AGENT_DESCRIPTION_STRING);
		
		// add new children (running agentinstances) to the "Agents"-node of the running society-instance's root-node 
		IRunnableAgentInstance[] runningAgentInstances = runnableModel.getRunnableAgentInstances();
		for (int i=0; i < runningAgentInstances.length; i++)
		{
			DefaultMutableTreeNode oneRunningAgentInstance = new DefaultMutableTreeNode(runningAgentInstances[i]);
			agentsNode.add(oneRunningAgentInstance);
		}
			
		return agentsNode;
	}
	
	private DefaultMutableTreeNode createReferencesNodes()
	{
		DefaultMutableTreeNode referencesNode = new DefaultMutableTreeNode(RepositoryTree.REFERENCE_DESCRIPTION_STRING);
		
		// add local running SocietyInstance-references to "References"-node
		IRunnableSocietyInstance[] runningSocInstRefs = runnableModel.getLocalRunnableSocietyInstanceReferences();
		for (int i=0; i < runningSocInstRefs.length; i++)
		{
			IRunnableSocietyInstance oneReference = runningSocInstRefs[i];
			RunnableSocietyInstanceNode oneReferenceNode = new RunnableSocietyInstanceNode(oneReference, showDetails, repository, treeModel);
			referencesNode.add(oneReferenceNode);
		}
		
		// add remote running SocietyInstance-references to "References"-node
		IRunnableRemoteSocietyInstanceReference[] remoteSocietyReferences = runnableModel.getRemoteRunnableSocietyInstanceReferences();
		for (int i=0; i < remoteSocietyReferences.length; i++)
		{
			IRunnableRemoteSocietyInstanceReference oneRemoteReference = remoteSocietyReferences[i];
			DefaultMutableTreeNode oneRemoteReferenceNode = new DefaultMutableTreeNode(oneRemoteReference);
			referencesNode.add(oneRemoteReferenceNode);
		}
		
		return referencesNode;
	}
	
	public void modelChanged(ModelChangedEvent event)
	{
		Object model = event.getModel();
		// System.err.println("RunnableSocietyInstanceNode.modelChanged: check if right model ("+model+")");
		if (model == runnableModel)
		{
			String eventCode = event.getEventCode();
			if (eventCode == ModelChangedEvent.DETAILED_STATUS_CHANGED)
			{
				return; // does not affect node-appearance
			}
			else if (eventCode == ModelChangedEvent.STATUS_CHANGED)
			{
				// System.err.println("RunnableSocietyInstanceNode.modelChanged: status change("+((RunnableSocietyInstance)model).getStatus()+"), simply call nodeChange(this)");
				treeModel.nodeChanged(this);
			}
			else
			{
				System.err.println("RunnableSocietyInstanceNode.modelChanged: re-init, eventCode=" + event.getEventCode());
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
