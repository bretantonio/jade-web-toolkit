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


package jade.tools.ascml.gui.components.exception;

import jade.tools.ascml.exceptions.ASCMLException;
import jade.tools.ascml.gui.components.tree.model.BasicModel;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;

/**
 * 
 */
public class ExceptionTree extends JTree
{
	private DefaultMutableTreeNode root;

	public ExceptionTree(ASCMLException rootException, TreeSelectionListener selectionListener)
	{
		this.setBackground(Color.WHITE);
		this.setRowHeight(16);
		this.setCellRenderer(new ExceptionTreeRenderer());
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.root = new DefaultMutableTreeNode("An Exception occurred ...");
		this.addTreeSelectionListener(selectionListener);

		this.setModel(new ExceptionTreeModel(root, rootException));
	}
}
