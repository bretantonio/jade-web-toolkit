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
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;
import java.awt.*;
import java.util.HashMap;

import jade.tools.ascml.gui.panels.AbstractMainPanel;
import jade.tools.ascml.gui.components.tree.model.*;
import jade.tools.ascml.gui.components.TabbedPaneManager;
import jade.tools.ascml.repository.Project;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.*;
import jade.tools.ascml.absmodel.ISocietyInstance;

public class RepositoryTree extends JTree implements TreeSelectionListener, ProjectListener,
		                                             PropertyListener, ModelChangedListener
{
	public final static String REPOSITORY_STRING			= "<html><b><i>Repository"; // html-end tags are inserted in method setRootNodeString
	public final static String AGENTTYPES_STRING			= "<html><b>AgentTypes</b></html>";
	public final static String SOCIETYTYPES_STRING			= "<html><b>SocietyTypes</b></html>";
	public final static String REMOTEREPOSITORIES_STRING	= "<html><b>remote Repositories</b></html>";
	public final static String AGENT_DESCRIPTION_STRING		= "<html><b>Agents</b></html>";
	public final static String REFERENCE_DESCRIPTION_STRING	= "<html><b>References</b></html>";
	public final static String RUNNINGINSTANCES_STRING		= "<html><b>Running Instances</b></html>";

	public static final String ADD_A_MODEL_HEADER			= "<html><b>Add a new Model ...</b></html>";
	public static final String ADD_AGENTMODEL_STRING		= "... load an Agent";
	public static final String ADD_SOCIETYMODEL_STRING		= "... load a Society";

	private HashMap searchForModelsCache;
	private DefaultMutableTreeNode root;
	private AbstractMainPanel parentPanel;
	private Repository repository;

	private PopupHandler popupHandler;

	/**
	 *
	 *  @param parentPanel  The parentPanel is used to show Dialogs
	 *  @param paneManager The paneManager is used to show pane(l)s
	 */
	public RepositoryTree(final AbstractMainPanel parentPanel, TabbedPaneManager paneManager)
	{
		super();
		// System.err.println("RepositoryTree: initializing ...");
		this.parentPanel = parentPanel;
		this.repository = parentPanel.getRepository();

        repository.getListenerManager().addProjectListener(this);
		repository.getListenerManager().addPropertyListener(this);
		repository.getListenerManager().addModelChangedListener(this);

		searchForModelsCache = new HashMap();

		this.addTreeSelectionListener(this);
		popupHandler = new PopupHandler(this, parentPanel);
		this.addMouseListener( popupHandler );

		ToolTipManager.sharedInstance().registerComponent(this);
		this.setBackground(Color.WHITE);
		this.setRowHeight(16);
		this.setCellRenderer(new TreeRenderer(this.parentPanel.getRepository()));
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.root = new DefaultMutableTreeNode();
        setRootNodeString();

		setView(repository.getProject().getView());
	}

	/**
	 * Set a new root-node-String.
	 * The root-node-String contains the name of the active Project, so this method
	 * has to be called, when the active project has been changed.
	 */
	private void setRootNodeString()
	{
        root.setUserObject(REPOSITORY_STRING + " ("+repository.getProject().getName()+")<i></b></html>");
	}

	/**
	 * Sets the view for this repository-tree.
	 * Possible view are defined in this class as constants:
	 * @param newView String-ID for new view. Possible IDs are declared in the
	 * Project-class: BASIC_VIEW and EXPERT_VIEW
	 */
	private void setView(String newView)
	{
		// when a new model is set, the old model has to finalize (e.g. deregister itself as listener, etc.)
		if (getModel() instanceof AbstractRepositoryTreeModel) // only at startup this is not the case
			((AbstractRepositoryTreeModel)getModel()).exit();

		// now, that all nodes has been removed, a new model can be set for the tree
		if (newView.equals(Project.BASIC_VIEW))
		{
			this.setModel(new BasicModel(parentPanel.getRepository(), root, this));
			// this.setSelectedModel(ADD_AGENTMODEL_STRING);
			// this.setSelectedNode(root);
			// parentPanel.setContentPanel(TabbedPaneManager.REPOSITORY_PANE, TabbedPaneManager.ABOUT_TAB);
		}
		else if (newView.equals(Project.EXPERT_VIEW))
		{
			this.setModel(new ExpertModel(parentPanel.getRepository(), root, this));
		}

		// now expand the agentTypes- and the societyTypes-Node, so that all
		// SocietyTypes and AgentTypes are visible
		int rowCount = this.getRowCount();
		for (int i=rowCount; i > 0; i--)
		{
			this.expandRow(i);
		}
	}

    /**
	 * Recursivly search for the node, that contains the given model as it's user-object.
	 * @param rootNode  Check if this node contains the given model, if not, all child-nodes are checked
	 * recursivly; each child-node is a new 'rootNode' while recursivly descending.
	 * @param model  The model to look for
	 * @return  The node containing the given model as it's user-object.
	 */
	public DefaultMutableTreeNode searchModelInTree(DefaultMutableTreeNode rootNode, Object model)
	{
		if (rootNode.getUserObject() == model)
		{
			return rootNode;
		}
		else
		{
			// let the recursive descent begin ...
			int childCount = rootNode.getChildCount();
			for (int i=0; i < childCount; i++)
			{
				// start recursive search
				DefaultMutableTreeNode modelNode = searchModelInTree((DefaultMutableTreeNode)rootNode.getChildAt(i), model);
				if (modelNode != null)
				{
					// node found, let the recursive ascent begin ...
					return modelNode;
				}
			}
			return null;
		}
	}

	// toDo: Don't know if this method is useful ?!?
	private DefaultMutableTreeNode searchSocietyInstanceModelByName(DefaultMutableTreeNode rootNode, String societyTypeName, String societyInstanceName)
	{
		// check if the node contains the appropiate model
		if ((rootNode.getUserObject() instanceof ISocietyInstance) &&
			((ISocietyInstance)rootNode.getUserObject()).getParentSocietyType().getName().equals(societyTypeName) &&
			((ISocietyInstance)rootNode.getUserObject()).getName().equals(societyInstanceName))
		{
			// model found
			return rootNode;
		}
		else
		{
			// let the recursive ascent begin ...
			for (int i=0; i < rootNode.getChildCount(); i++)
			{
				DefaultMutableTreeNode oneNode = (DefaultMutableTreeNode)rootNode.getChildAt(i);
				DefaultMutableTreeNode modelNode = searchSocietyInstanceModelByName(oneNode, societyTypeName, societyInstanceName);
				if (modelNode != null)
					return modelNode;
			}
			return null;
		}
	}

	/**
	 * Select the tree-node, which user-object is the given model.
	 * @param model  The model, that's contained as userObject in the node to select.
	 */
	public void setSelectedModel(Object model)
	{
		// System.err.println("RepositoryTree.setSelectedModel: " + model);
		// recursivly look for the node containing the model
		DefaultMutableTreeNode modelNode = searchModelInTree(root, model);
		// if found, select the node
		if (modelNode != null)
		{
			// System.err.println("RepositoryTree.setSelectedModel: Model found, select & expand it !!!");
			setSelectedNode(modelNode);
			this.expandPath(this.getSelectionPath());
		}
		else
		{
			// System.err.println("RepositoryTree.setSelectedModel: Model NOT found");
		}
	}

	/**
	 * Select the given tree-node.
	 * @param nodeToSelect  The tree-node, that shall be selected.
	 */
	public void setSelectedNode(DefaultMutableTreeNode nodeToSelect)
	{
		this.setSelectionPath(new TreePath(nodeToSelect.getPath()));
	}

	/**
	 * Expands all nodes in the tree.
	 */
	public void expandAllNodes()
	{
		for (int i=0; i < this.getRowCount(); i++)
		{
			this.expandRow(i);
		}
	}

	/**
	 * This method has to be implemented because the MainWindow is a TreeSelectionListener.
	 * Depending on the chosen node in the tree, a specific Panel is presented.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)getLastSelectedPathComponent();
		if (node==null) return;

		Object model = node.getUserObject();
		parentPanel.showModelDetails(model);
	}

	public void projectChanged(ProjectChangedEvent event)
	{
		// System.err.println("RepositoryTree: Project changed, evt-code=" + event.getEventCode());
		if (event.getEventCode().equals(ProjectChangedEvent.VIEW_CHANGED))
		{
			setView(event.getProject().getView());
			repaint();
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.AGENTTYPE_REMOVED))
		{
			((DefaultTreeModel)getModel()).removeNodeFromParent((DefaultMutableTreeNode)searchModelInTree(root, event.getModel()));
			setSelectedNode((DefaultMutableTreeNode)searchModelInTree(root, AGENTTYPES_STRING));
		}
		else if (event.getEventCode().equals(ProjectChangedEvent.SOCIETYTYPE_REMOVED))
		{
			// ((DefaultTreeModel)getModel()).removeNodeFromParent((DefaultMutableTreeNode)searchModelInTree(root, event.getModel()));
			setSelectedNode((DefaultMutableTreeNode)searchModelInTree(root, SOCIETYTYPES_STRING));
		}
	}

	public void propertiesChanged(PropertyChangedEvent event)
	{
		if (event.getEventCode().equals(PropertyChangedEvent.ACTIVE_PROJECT_CHANGED))
		{
			setView(event.getProperties().getActiveProject().getView());
            setRootNodeString();
		}
	}

	/**
	 * Called when the status of a RunnableModel changed.
	 * The tree needs to be repainted in order to show the appropiate icon.
	 * @param event  The RunnableModelChangedEvent thrown when the model has changed
	 */
	public synchronized void modelChanged(ModelChangedEvent event)
	{
		if ((event.getEventCode() == ModelChangedEvent.NAME_CHANGED) ||
			(event.getEventCode() == ModelChangedEvent.ICON_CHANGED) || 
			(event.getEventCode() == ModelChangedEvent.STATUS_CHANGED))
		{
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					repaint();
				}
			});
		}
		/*

		comment because each node now registers itself as a ModelChangedListener


		String eventCode = event.getEventCode();
		if (!(eventCode == ModelChangedEvent.DETAILED_STATUS_CHANGED))
		{
			// modelChangedCounter++;
			// System.err.println("RepositoryTree.modelChanged: model="+event.getModel()+" event=" + event.getEventCode());

			Object model = event.getModel();
			DefaultMutableTreeNode nodeToUpdate;

			if (searchForModelsCache.containsKey(model))
			{
				nodeToUpdate = (DefaultMutableTreeNode)searchForModelsCache.get(model);
				// System.err.println("RepositoryTree.modelChanged: Successfully found userObject ("+model+") in cache");
			}
			else
			{
				nodeToUpdate = searchModelInTree(root, model);
				if (!(model instanceof AbstractRunnable))
					searchForModelsCache.put(model, nodeToUpdate);
			}

            // System.err.println("RepositoryTree.modelChanged: nodeToUpdate=" + nodeToUpdate);
			// System.err.println("RepositoryTree.modelChanged: model-class=" + model.getClass().getName());

			if (nodeToUpdate instanceof IRepositoryTreeNode)
			{
				// System.err.println("RepositoryTree.modelChanged: repositoryNodeToUpdate=" + nodeToUpdate);
                ((IRepositoryTreeNode)nodeToUpdate).modelChanged(event);

				/* // to reflect a possible change in the node-structure (a child may have been added or removed)
				// a call to nodeStructureChange is done
				if (eventCode == ModelChangedEvent.RUNNABLE_ADDED)
				{
					// System.err.println("RepositoryTree.modelChanged: eventCode=" + eventCode);
                    // ((DefaultTreeModel)this.getModel()).nodesWereInserted(nodeToUpdate, new int[] {nodeToUpdate.getChildCount()-1} );
				}
                else if (eventCode == ModelChangedEvent.RUNNABLE_REMOVED)
                {
                    // ((DefaultTreeModel)this.getModel()).nodeStructureChanged(nodeToUpdate);
                }* /
			}
		}
        */
	}

	public void exit()
	{
		repository.getListenerManager().removeProjectListener(this);
		repository.getListenerManager().removePropertyListener(this);
		repository.getListenerManager().removeModelChangedListener(this);
		popupHandler.exit();
	}
}