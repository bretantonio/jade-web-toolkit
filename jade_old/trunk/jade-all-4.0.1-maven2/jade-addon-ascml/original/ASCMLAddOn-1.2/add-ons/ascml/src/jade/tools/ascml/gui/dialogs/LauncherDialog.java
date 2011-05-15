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

import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.gui.panels.AbstractMainPanel;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.IAbstractRunnable;

import javax.swing.*;

/**
 *  Presents a dialog to the user asking for a name for the agentInstance.
 */
public class LauncherDialog extends AbstractDialog
{
	private String address;
	public LauncherDialog(String address)
	{
		super();
		if ((address == null) || address.equals(""))
			this.address = "http://127.0.0.1:7778/acc";
		else
			this.address = address;
	}

	public Object showDialog(JFrame parentFrame)
	{
		Object result = JOptionPane.showInputDialog(parentFrame, "Please specify an address for the launcher !", address);

		return result;
	}
}
	
