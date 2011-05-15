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

import jade.tools.ascml.gui.components.tree.*;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.events.PropertyChangedEvent;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;

import javax.swing.tree.*;
import java.util.Vector;

/**
 *  Tree-model-object.
 */

public class BasicModel extends AbstractRepositoryTreeModel
{
	/**
	 *  Instantiate a new model and initialise some variables
	 */
	public BasicModel(Repository repository, DefaultMutableTreeNode rootNode, RepositoryTree rootTree)
	{
		super(repository, rootNode, rootTree);

		// create and add static TreeNodes
		DefaultMutableTreeNode addRootNode = new DefaultMutableTreeNode(RepositoryTree.ADD_A_MODEL_HEADER);
		DefaultMutableTreeNode addAgentNode = new DefaultMutableTreeNode(RepositoryTree.ADD_AGENTMODEL_STRING);
		DefaultMutableTreeNode addSocietyNode = new DefaultMutableTreeNode(RepositoryTree.ADD_SOCIETYMODEL_STRING);

		addRootNode.add(addSocietyNode);
		addRootNode.add(addAgentNode);
		rootNode.add(addRootNode);

		initSocietyTypeNodes();
		initAgentTypeNodes();

		this.nodeStructureChanged(addRootNode);
		this.nodeStructureChanged(rootNode);
		rootTree.makeVisible(new TreePath(this.getPathToRoot(addAgentNode)));
		rootTree.expandPath(new TreePath(this.getPathToRoot(addAgentNode)));
		rootTree.expandPath(new TreePath(this.getPathToRoot(addAgentNode.getParent())));
		rootTree.validate();
	}

	/**
	 * This method initializes all societyTypeNodes by removing the old nodes and creating
	 * a new node (inkl subnodes) for each SocietyType.
	 */
	private void initSocietyTypeNodes()
	{
		// needed as temporary node-cache because they cannot be removed in the for-loop and
		// have to be removed after the loop
		Vector dummyRemoveVector = new Vector();

		for (int i=0; i < getChildCount(rootNode); i++)
		{ // find nodes and call exit-method
			MutableTreeNode oneNode = (MutableTreeNode)getChild(rootNode, i);
			if (oneNode instanceof SocietyTypeNode)
			{
				((SocietyTypeNode)oneNode).exit();
				dummyRemoveVector.add(oneNode);
			}
		}
        for (int i=0; i < dummyRemoveVector.size(); i++)
		{ // remove nodes
			this.removeNodeFromParent((MutableTreeNode)dummyRemoveVector.elementAt(i));
		}

		ISocietyType[] societyTypes = repository.getProject().getSocietyTypes(); // try == showOnlyViewable

		// add the new SocietyNodes
		for (int i=0; i < societyTypes.length; i++)
		{
			DefaultMutableTreeNode societyTypeRootNode = new SocietyTypeNode(societyTypes[i], SocietyTypeNode.SUBTREE_LEVEL_RUNNINGINSTANCES_ONLY, repository, this);
			this.insertNodeInto(societyTypeRootNode, rootNode, getChildCount(rootNode));
		}
	}

	private void initAgentTypeNodes()
	{
		// needed as temporary node-cache because they cannot be removed in the for-loop and
		// have to be removed after the loop
		Vector dummyRemoveVector = new Vector();

		for (int i=0; i < getChildCount(rootNode); i++)
		{ // find nodes and call exit-method
			MutableTreeNode oneNode = (MutableTreeNode)getChild(rootNode, i);
			if (oneNode instanceof AgentTypeNode)
			{
				((AgentTypeNode)oneNode).exit();
				dummyRemoveVector.add(oneNode);
			}
		}
        for (int i=0; i < dummyRemoveVector.size(); i++)
		{ // remove nodes
			this.removeNodeFromParent((MutableTreeNode)dummyRemoveVector.elementAt(i));
		}

		IAgentType[] agentTypes = repository.getProject().getAgentTypes();
		// add the new AgentNodes
		for (int i=0; i < agentTypes.length; i++)
		{
			AgentTypeNode agentTypeNode = new AgentTypeNode(agentTypes[i], repository, this);
			this.insertNodeInto(agentTypeNode, rootNode, getChildCount(rootNode));
			this.nodeStructureChanged(agentTypeNode);
		}
	}

	public void projectChanged(ProjectChangedEvent event)
	{
        if (event.getEventCode().equals(ProjectChangedEvent.AGENTTYPE_ADDED) ||
			event.getEventCode().equals(ProjectChangedEvent.AGENTTYPE_REMOVED))
		{
			// initSocietyTypeNodes();
			initAgentTypeNodes();
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.SOCIETYTYPE_ADDED) ||
				 event.getEventCode().equals(ProjectChangedEvent.SOCIETYTYPE_REMOVED))
		{
			initSocietyTypeNodes();
			// initAgentTypeNodes();
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
