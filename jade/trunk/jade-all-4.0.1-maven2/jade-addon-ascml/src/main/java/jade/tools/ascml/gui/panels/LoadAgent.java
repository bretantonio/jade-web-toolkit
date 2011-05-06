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
import jade.tools.ascml.repository.loader.ImageIconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoadAgent extends AbstractPanel implements ActionListener
{

	public JButton loadButton;

	public LoadAgent(AbstractMainPanel mainPanel)
	{
		super(mainPanel);

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());


		this.add(new JLabel(ImageIconLoader.createImageIcon(ImageIconLoader.ADD_AGENTTYPE)), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.LINE_START,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));
		this.add(new JLabel("<html><h2>&nbsp;Load an Agent</h2></html>"), new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.LINE_START,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));

		this.add(new JLabel("<html>The ASCML distinguishes two kinds of agents, <i>agent types</i> and <i>agent instances</i>.<br>" +
						"An agent type may be seen as a template for an agent instance.<br>" +
						"It contains nearly all necessary information for an agent instance to start up,<br>" +
						"except possibly mandatory startup-parameters.<br>" +
						"An agent instance has to provide these parameters, unless they are not <br>" +
						"specified by the agent type.<p>&nbsp;<p>" +
						"When you load an agent type, it is presented in the <i>repository-tree</i> you see on <br>" +
						"the left hand side of this window. When an agent type is selected in the <br>" +
						"repository-tree, some details are presented and you have the <br>" +
						"possibility to start (stop) an agent instance created out of the agent type.<p>&nbsp;<p>" +
						"When you load an agent type look for files with the suffix \"<i>.agent.xml</i>\".</html>"),
				new GridBagConstraints(0,1,2,1,1,0,GridBagConstraints.LINE_START,GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0));

		loadButton = new JButton("Load an Agent");
		loadButton.addActionListener(this);

        this.add(loadButton, new GridBagConstraints(0,2,2,1,1,1,GridBagConstraints.FIRST_LINE_START,GridBagConstraints.NONE, new Insets(5,5,5,5),0,0));
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == loadButton)
		{
			mainPanel.showDialog(AbstractMainPanel.ADD_AGENTTYPE_DIALOG);
		}
	}
}
