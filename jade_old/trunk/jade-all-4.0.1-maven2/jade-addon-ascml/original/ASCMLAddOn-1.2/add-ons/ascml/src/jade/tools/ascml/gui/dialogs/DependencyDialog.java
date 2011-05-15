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


package jade.tools.ascml.gui.dialogs;

import javax.swing.*;
import java.awt.*;
import jade.tools.ascml.gui.panels.DependencyDetails;
import jade.tools.ascml.absmodel.dependency.IDependency;

public class DependencyDialog extends AbstractDialog
{
    private IDependency dependency;

	public DependencyDialog(IDependency dependency)
	{
		super();
        this.dependency = dependency;
	}

	public Object showDialog(JFrame parentFrame)
	{
		DependencyDetails panel = new DependencyDetails(null);

		panel.setDependency(dependency);

		JDialog dialog = new JDialog(parentFrame, "Edit Dependency ...", true);
		dialog.getContentPane().add(panel);
		dialog.setSize(400, 400);
		dialog.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-dialog.getWidth()/2),
 						   (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-dialog.getHeight()/2));
		dialog.setVisible(true);
        dialog.toFront();

		return panel.getResult();
	}
}
