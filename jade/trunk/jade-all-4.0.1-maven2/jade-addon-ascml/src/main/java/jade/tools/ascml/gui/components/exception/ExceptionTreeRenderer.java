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

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.exceptions.ASCMLException;
import jade.tools.ascml.exceptions.ASCMLExceptionDetails;

public class ExceptionTreeRenderer extends DefaultTreeCellRenderer
{
	public ExceptionTreeRenderer()
	{
		super();
		setFont(new Font("Arial", Font.PLAIN, 12));
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel,
	                          boolean expanded, boolean leaf, int row, boolean hasFocus) 
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		Object nodeContent = ((DefaultMutableTreeNode)value).getUserObject();

		if (nodeContent instanceof String)
		{
			setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.EXCEPTION_HEAD, 14, 14));

		}
        else if (nodeContent instanceof ASCMLException)
		{
			setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.EXCEPTION_MAIN, 14, 14));
			setToolTipText(((ASCMLException)nodeContent).getShortMessage());
		}
		else if (nodeContent instanceof Exception)
		{
			setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.EXCEPTION_MAIN, 14, 14));
			setToolTipText(((Exception)nodeContent).getMessage());
		}
		else if (nodeContent instanceof ASCMLExceptionDetails)
		{
			setIcon(ImageIconLoader.createImageIcon(ImageIconLoader.EXCEPTION_DETAILS, 14, 14));
			setToolTipText(((ASCMLExceptionDetails)nodeContent).getShortMessage());
		}
		return this;
	}
}
