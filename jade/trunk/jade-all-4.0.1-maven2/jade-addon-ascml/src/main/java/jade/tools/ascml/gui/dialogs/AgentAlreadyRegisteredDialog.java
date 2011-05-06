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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.absmodel.IAgentInstance;

public class AgentAlreadyRegisteredDialog extends JDialog implements ActionListener
{
	private JButton cancelButton;
	private JButton startButton;
	private JTextField newName;
	
	private IAgentInstance instanceModel;
	private boolean nameChanged = false;
	
	
	public AgentAlreadyRegisteredDialog(Frame parent, ModelActionException exc)
	{
		super(parent, "Agent already registered", true);
		
		this.instanceModel = (IAgentInstance)exc.getModel();
		
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		newName = new JTextField(25);
		newName.setBackground(Color.WHITE);
		JPanel newNamePanel = new JPanel();
		newNamePanel.setBackground(Color.WHITE);
		newNamePanel.add(new JLabel("New Name: "));
		newNamePanel.add(newName);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		startButton = new JButton("Start Agent");
		startButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(cancelButton);
		buttonPanel.add(startButton);
		
		JPanel contentPane = (JPanel)this.getContentPane();
		contentPane.setBackground(Color.WHITE);
		contentPane.add(new JLabel("<html>&nbsp;There's already an agent named <i>"+instanceModel.getName()+"</i> running.<br>&nbsp;Since each agent has to have an unique name you may either <ul><li>rename the starting agent or </li><li>cancel the startup-process of <i>"+instanceModel.getName()+"</i> </li></ul>.</html>"), BorderLayout.NORTH);
		contentPane.add(newNamePanel, BorderLayout.CENTER);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		this.setSize(400, 200);
		this.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-this.getWidth()/2),
				(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-this.getHeight()/2));
		this.setVisible(true);
	}
		
	public IAgentInstance getModelWithNewName()
	{
		if (nameChanged)
			return instanceModel;
		else
			return null;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == cancelButton)
		{
			this.setVisible(false);
		}
		else if (e.getSource() == startButton)
		{
			if (newName.getText().trim().equals(""))
			{
				JOptionPane.showMessageDialog(this.getParent(),
					"Please provide a new name for your Agent !");
			}
			else
			{
				nameChanged = true;
				instanceModel.setName(newName.getText().trim());
				this.setVisible(false);
			}
		}
	}
}
