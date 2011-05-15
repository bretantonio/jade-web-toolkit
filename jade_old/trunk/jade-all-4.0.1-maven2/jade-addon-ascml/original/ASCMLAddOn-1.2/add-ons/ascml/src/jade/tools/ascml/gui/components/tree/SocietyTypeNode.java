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
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.absmodel.ISocietyType;

public class SocietyTypeNode extends DefaultMutableTreeNode implements IRepositoryTreeNode
{
    public final static String SUBTREE_LEVEL_SOCIETYTYPE					= "society_type"; // show only the societyTypes
	public final static String SUBTREE_LEVEL_SOCIETYINSTANCE				= "society_instance"; // show societyTypes + societyInstances

	public final static String SUBTREE_LEVEL_TEMPLATES_ONLY					= "templates_only"; // show societyTypes + societyInstances + templates and NOT runninginstances
	public final static String SUBTREE_LEVEL_RUNNINGINSTANCES_ONLY			= "runninginstances_only"; // show societyTypes + societyInstances + runninginstancesand NOT templates
	public final static String SUBTREE_LEVEL_ALL							= "all"; // show all nodes

	private ISocietyType societyModel = null;
	private String subTreeLevelToShow;
	private Repository repository;
    private DefaultTreeModel treeModel;
	/**
	 *
	 * @param subTreeLevelToShow  This is number represents the nodes subtree-depth.
	 * 0 means, only the SocietyTypeNode should be shown, nothing more.
	 * 1 means, the SocietyTypeNode, and the SocietyInstanceNodes should be shown, because these
	 * are 1 level deeper; same with 3, 4, and so on.
	 */
	public SocietyTypeNode(ISocietyType societyModel, String subTreeLevelToShow, Repository repository, DefaultTreeModel treeModel)
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
		if (!subTreeLevelToShow.equals(SUBTREE_LEVEL_SOCIETYTYPE))
		{
			// clean up first
            int childCount = this.getChildCount();
            for (int i=0; i < childCount; i++)
            {
                treeModel.removeNodeFromParent((DefaultMutableTreeNode)getChildAt(0));
            }

			// add new children (SocietyInstances)
			ISocietyInstance[] socInstances = societyModel.getSocietyInstances();
            // toDo: Irgendeine Art von Ordnung, zum Beispiel alphabetisch koennte man vorgeben
			for (int i=0; i < socInstances.length; i++)
			{
				DefaultMutableTreeNode socInstanceNode = new SocietyInstanceNode(socInstances[i], subTreeLevelToShow, repository, treeModel);
				treeModel.insertNodeInto(socInstanceNode, this, this.getChildCount());
			}
		}
	}
	
	public void modelChanged(ModelChangedEvent event)
	{
		Object model = event.getModel();
		if (model == societyModel)
		{
			init();
		}
	}

	public void exit()
	{
		repository.getListenerManager().removeModelChangedListener(this);
	}
}
