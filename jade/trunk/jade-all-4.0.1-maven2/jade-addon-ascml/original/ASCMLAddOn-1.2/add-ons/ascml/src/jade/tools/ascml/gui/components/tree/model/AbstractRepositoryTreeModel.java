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

import jade.tools.ascml.gui.components.tree.RepositoryTree;
import jade.tools.ascml.gui.components.tree.IRepositoryTreeNode;
import jade.tools.ascml.events.ProjectListener;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;

/**
 *  Abstract Tree-model-object.
 */

public abstract class AbstractRepositoryTreeModel extends DefaultTreeModel implements ProjectListener
{
	protected RepositoryTree rootTree;
	protected DefaultMutableTreeNode rootNode;
	protected Repository repository;

	protected DefaultMutableTreeNode agentTypesNode = new DefaultMutableTreeNode(RepositoryTree.AGENTTYPES_STRING);
	protected DefaultMutableTreeNode societyTypesNode = new DefaultMutableTreeNode(RepositoryTree.SOCIETYTYPES_STRING);
	protected DefaultMutableTreeNode remoteRepositoriesNode = new DefaultMutableTreeNode(RepositoryTree.REMOTEREPOSITORIES_STRING);

	/**
	 *  Instantiate a new model and initialise some variables
	 */
	public AbstractRepositoryTreeModel(Repository repository, DefaultMutableTreeNode rootNode, RepositoryTree rootTree)
	{
		super(rootNode);
        this.repository = repository;
		this.rootTree = rootTree;
		this.rootNode = rootNode;

		// register as listener in order to get informed about project-changes
		repository.getListenerManager().addProjectListener(this);
		// toDo: repository.addModelChangedListener(this);

		// System.err.println("AbstractRepTreeModel: zeig nur viewable agents: " + showOnlyViewableModels);
		IAgentType[] agents = repository.getProject().getAgentTypes();
		// System.err.println("AbstractRepTreeModel: Anzahl viewable agents: " + agents.length);
		
		ISocietyType[] socs = repository.getProject().getSocietyTypes();
		
		/*
		toDo: uncomment !!!
		for (int i=0; i < agents.length; i++)
		{
			// System.err.println("AbstractRepositoryTreeModel(): add agent" + agents[i] + " show ? " + showOnlyViewableModels);
			this.agentTypeAdded(agents[i], showOnlyViewableModels);
		}

		for (int i=0; i < socs.length; i++)
		{
			// System.err.println("AbstractRepositoryTreeModel(): add society" + socs[i] + " show ? " + showOnlyViewableModels);
			this.societyTypeAdded(socs[i], showOnlyViewableModels);
		}
		*/
	}

	public abstract void projectChanged(ProjectChangedEvent event);

	/**
	 * This method overwrites the removeNodeFromParent-method of the DefaultTreeModel in order
	 * to properly finalize the Model-Nodes (call the exit()-method)
	 * @param nodeToRemove  The node to remove.
	 */
	public void removeNodeFromParent(MutableTreeNode nodeToRemove)
	{
		super.removeNodeFromParent(nodeToRemove);
		if (nodeToRemove instanceof IRepositoryTreeNode)
			((IRepositoryTreeNode)nodeToRemove).exit();
	}

	/**
	 * This method overwrites the insertNodeInto-method of the DefaultTreeModel in order
	 * to insert the model-nodes alphabetically.
	 * @param newChild  Node to insert.
	 * @param parent  ParentNode of the node to insert
	 * @param index  index, indicating where to insert the node. This index is not evaluated, instead
	 *               a new index is searched for by lexicographically comparing the User-Object-Strings.
	 */
	public void insertNodeInto(MutableTreeNode newChild, MutableTreeNode parent, int index)
	{
		// insert alphabetically
		int indexAlpha = parent.getChildCount();

		if (newChild instanceof IRepositoryTreeNode)
		{
			try
			{
				String newNodeString = ((DefaultMutableTreeNode)newChild).getUserObject().toString();

				for (int i=0; i < parent.getChildCount(); i++)
				{
					DefaultMutableTreeNode oneChild = (DefaultMutableTreeNode)parent.getChildAt(i);
					String nodeString = oneChild.getUserObject().toString();
					if (nodeString.compareToIgnoreCase(newNodeString) > 0)
					{
						indexAlpha = i;
						i = Integer.MAX_VALUE-1;
					}
				}
			}
			catch(Exception exc)
			{
                // a NullPointerException may occur when the UserObjects (models) are not
				// properly initialized, but don't care about this.
			}
		}

		super.insertNodeInto(newChild, parent, indexAlpha);
	}

	public void exit()
	{
		rootNode.removeAllChildren();
		// deregister the model as repository-listener
		repository.getListenerManager().removeProjectListener(this);
		// toDO: repository.removeModelListener(this);
	}
}
