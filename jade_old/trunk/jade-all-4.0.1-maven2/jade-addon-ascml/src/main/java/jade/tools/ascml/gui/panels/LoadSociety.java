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


package jade.tools.ascml.gui.panels;

import jade.tools.ascml.gui.components.tree.TreeIconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoadSociety extends AbstractPanel implements ActionListener
{

	public JButton loadButton;

	public LoadSociety(AbstractMainPanel mainPanel)
	{
		super(mainPanel);

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		this.add(new JLabel(TreeIconLoader.basicAddSocietyIcon), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.LINE_START,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));
		this.add(new JLabel("<html><h2>&nbsp;Load a Society</h2></html>"), new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.LINE_START,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));

		this.add(new JLabel("<html>A <i>society</i> (or <i>society type</i>) in terms of the ASCML represents a multi-agent<br>" +
				"application. This application has at least one specified configuration,<br>" +
				"called <i>society instance</i>. A society instance is defined by a set of agent-instances <br>" +
				"and possibly some constraints between them, e.g. startup-dependencies.<p>&nbsp;<p>" +
				"When you load a society into the repository, all the contained society instances <br>" +
				"are loaded as well and are presented in the <i>repository-tree</i> you see on <br>" +
				"the left hand side of this window.<p>&nbsp;<p>" +
				"When a society instance is selected in the repository-tree, some details about the <br>" +
				"represented application-configuration are presented and you have the possibility <br>" +
				"to start (stop) the instance. Starting (stopping) an instance means, that all <br>" +
				"contained agent-instances are started (stopped) as well.<p>&nbsp;<p>" +
				"When you load a society look for files with the suffix \"<i>.society.xml</i>\".</html>"),
			new GridBagConstraints(0,1,2,1,1,0,GridBagConstraints.LINE_START,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0));


		loadButton = new JButton("Load a Society");
		loadButton.addActionListener(this);

		this.add(loadButton, new GridBagConstraints(0,2,2,1,1,1,GridBagConstraints.FIRST_LINE_START,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == loadButton)
		{
			mainPanel.showDialog(AbstractMainPanel.ADD_SOCIETYTYPE_DIALOG);
		}
	}
}
