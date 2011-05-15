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


package jade.tools.ascml.gui.components.tree.model;

import javax.swing.tree.*;
import jade.tools.ascml.gui.components.tree.*;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;

/**
 *  Tree-model-object.
 */

public class ExpertModel extends AbstractRepositoryTreeModel
{
	/**
	 *  Instantiate a new model and initialise some variables
	 */
	public ExpertModel(Repository repository, DefaultMutableTreeNode rootNode, RepositoryTree rootTree)
	{
		super(repository, rootNode, rootTree);

		rootNode.add(societyTypesNode); // variables defined in superclass
		rootNode.add(agentTypesNode);
		// rootNode.add(remoteRepositoriesNode);

		initSocietyTypeNodes();
		initAgentTypeNodes();

		// rootTree.makeVisible(new TreePath(this.getPathToRoot(remoteRepositoriesNode)));
		// this.nodeStructureChanged(remoteRepositoriesNode);
	}
	
	private void initAgentTypeNodes()
	{
		// remove old nodes
		agentTypesNode.removeAllChildren();

		// add new ones
		IAgentType[] agentTypes = repository.getProject().getAgentTypes();

		// add the new AgentNodes
		for (int i=0; i < agentTypes.length; i++)
		{
			addAgentType(agentTypes[i]);
		}
	}

	private void addAgentType(IAgentType agentType)
	{
		DefaultMutableTreeNode agentTypeRootNode = new AgentTypeNode(agentType, repository, this);
		this.insertNodeInto(agentTypeRootNode, agentTypesNode, getChildCount(agentTypesNode));

		this.nodeStructureChanged(agentTypesNode);
		rootTree.validate();
	}

	private void initSocietyTypeNodes()
	{
		// remove old nodes
		societyTypesNode.removeAllChildren();

		ISocietyType[] societyTypes = repository.getProject().getSocietyTypes(); // try == showOnlyViewable

		// add the new SocietyNodes
		for (int i=0; i < societyTypes.length; i++)
		{
			addSocietyType(societyTypes[i]);
		}
	}

	private void addSocietyType(ISocietyType societyType)
	{
		DefaultMutableTreeNode societyTypeRootNode = new SocietyTypeNode(societyType, SocietyTypeNode.SUBTREE_LEVEL_ALL, repository, this);
		this.insertNodeInto(societyTypeRootNode, societyTypesNode, getChildCount(societyTypesNode));

		// this.nodeStructureChanged(societyTypesNode);
		// rootTree.validate();
	}

	private void removeSocietyType(ISocietyType societyType)
	{
		DefaultMutableTreeNode modelNode = rootTree.searchModelInTree(societyTypesNode, societyType);
		this.removeNodeFromParent(modelNode);

		// this.nodeStructureChanged(societyTypesNode);
		// rootTree.validate();
	}

	public void projectChanged(ProjectChangedEvent event)
	{
        if (event.getEventCode().equals(ProjectChangedEvent.AGENTTYPE_ADDED) ||
			event.getEventCode().equals(ProjectChangedEvent.AGENTTYPE_REMOVED))
		{
            initAgentTypeNodes();
			rootTree.setSelectedModel(event.getModel());
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.SOCIETYTYPE_ADDED))
		{
			addSocietyType((ISocietyType)event.getModel());
			rootTree.setSelectedModel(event.getModel());
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.SOCIETYTYPE_REMOVED))
		{
			removeSocietyType((ISocietyType)event.getModel());
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.SOCIETYTYPE_SELECTED))
		{
			rootTree.setSelectedModel(event.getModel());
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.AGENTTYPE_SELECTED))
		{
			rootTree.setSelectedModel(event.getModel());
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.SOCIETYINSTANCE_SELECTED))
		{
			rootTree.setSelectedModel(event.getModel());
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.AGENTINSTANCE_SELECTED))
		{
			rootTree.setSelectedModel(event.getModel());
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.SOCIETYINSTANCE_REFERENCE_SELECTED))
		{
			rootTree.setSelectedModel(event.getModel());
		}
	}
}
