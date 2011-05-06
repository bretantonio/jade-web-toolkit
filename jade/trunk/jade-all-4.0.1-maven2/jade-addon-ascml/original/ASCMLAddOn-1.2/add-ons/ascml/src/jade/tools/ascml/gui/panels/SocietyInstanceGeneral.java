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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import jade.tools.ascml.gui.dialogs.StartSocietyInstanceDialog;
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.absmodel.IAgentInstance;
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.absmodel.ISocietyInstanceReference;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.model.jibx.Launcher;

public class SocietyInstanceGeneral extends AbstractPanel implements ActionListener
{
	private JLabel iconLabel;

	private JButton buttonStart;
	private JButton buttonApply;

	private JTextField textFieldName;
	private JTextField textFieldScheme;

	private JTextArea textAreaDescription;

	private JSpinner spinnerQuantity;

	private ISocietyInstance model;

	public SocietyInstanceGeneral(AbstractMainPanel mainPanel, ISocietyInstance model)
	{
		super(mainPanel);
		this.model = model;
				
		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

		buttonStart = ComponentFactory.createStartButton("Start Instance");
		buttonStart.addActionListener(this);

		buttonApply = ComponentFactory.createApplyButton("Apply Changes");
		buttonApply.addActionListener(this);

        this.add(createAttributePanel(), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(buttonApply, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(buttonStart, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.01, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		this.add(createAgentInstanceTablePane(), new GridBagConstraints(0, 2, 2, 1, 1, 0.5, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(createSocietyInstanceReferenceTablePane(), new GridBagConstraints(0, 3, 2, 1, 1, 0.5, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
	}
	
	private JPanel createAttributePanel()
	{
		// first create all the components
		iconLabel = new JLabel(ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE, 32, 32));

		textFieldName = new JTextField(model.getName(), 30);
		textFieldName.setMinimumSize(new Dimension(320, (int)textFieldName.getPreferredSize().getHeight()));
		textFieldName.setBackground(Color.WHITE);

		textFieldScheme = new JTextField(model.getNamingScheme(), 10);
		textFieldScheme.setMinimumSize(new Dimension(100, (int)textFieldScheme.getPreferredSize().getHeight()));
		textFieldScheme.setBackground(Color.WHITE);

		textAreaDescription = new JTextArea(model.getDescription(), 3, 20);
		JScrollPane textDescScrollPane = ComponentFactory.createTextAreaScrollPane(textAreaDescription);

		spinnerQuantity = ComponentFactory.createQuantitySpinner(model.getQuantity());
		
		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		// prepare Main-Panel
		attributePanel.add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("<html><h2>General Settings</h2>Here, you see all the main-settings for this SocietyInstance.</html>"), new GridBagConstraints(1, 0, 3, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Instance-Name:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldName, new GridBagConstraints(1, 1, 3, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Description:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textDescScrollPane, new GridBagConstraints(1, 2, 3, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Quantity:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(spinnerQuantity, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("Naming-Scheme:"), new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldScheme, new GridBagConstraints(3, 3, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	private JScrollPane createAgentInstanceTablePane()
	{
		String[] tableHeaderEntries = new String[] {"AgentInstance", "AgentType"};

		IAgentInstance[] agentInstances = model.getAgentInstanceModels();
		DefaultTableModel agentInstanceTableModel = new DefaultTableModel(tableHeaderEntries, 0);

		for(int i=0; i<agentInstances.length; i++)
		{
			String[] oneRow = new String[2];

			oneRow[0] = agentInstances[i].getName();
			if (agentInstances[i].getType() != null)
				oneRow[1] = agentInstances[i].getType().getFullyQualifiedName();
			else
				oneRow[1] = IAgentType.NAME_UNKNOWN;
			
			agentInstanceTableModel.addRow(oneRow);
		}

		JTable agentInstanceTable = new JTable(agentInstanceTableModel);
		agentInstanceTable.setRowSelectionAllowed(false);
		agentInstanceTable.setColumnSelectionAllowed(false);

		JPanel agentInstancePanel = new JPanel(new BorderLayout());
		agentInstancePanel.setBorder(BorderFactory.createTitledBorder(" AgentInstances "));
		agentInstancePanel.setBackground(Color.WHITE);
		agentInstancePanel.add(agentInstanceTable.getTableHeader(), BorderLayout.PAGE_START);
		agentInstancePanel.add(agentInstanceTable, BorderLayout.CENTER);

		JScrollPane agentInstanceScrollPane = new JScrollPane(agentInstancePanel);
		agentInstanceScrollPane.setWheelScrollingEnabled(true);
		agentInstanceScrollPane.setBorder(BorderFactory.createEmptyBorder());

		return agentInstanceScrollPane;
	}

	private JScrollPane createSocietyInstanceReferenceTablePane()
	{
		String[] tableHeaderEntries = new String[] {"SocietyType", "SocietyInstance", "Launcher"};
		ISocietyInstanceReference[] socInstRefs = model.getSocietyInstanceReferences();

		DefaultTableModel societyRefTableModel = new DefaultTableModel(tableHeaderEntries, 0);

		for (int i = 0; i < socInstRefs.length; i++)
		{
			ISocietyInstanceReference oneSocInstRef = socInstRefs[i];

			String[] oneRow = new String[3];
			String typeName = oneSocInstRef.getTypeName();
			String instanceName = oneSocInstRef.getInstanceName();
			String launcherName = oneSocInstRef.getLauncher().getName();

			if (launcherName.equals(Launcher.NAME_UNKNOWN))
			{
				launcherName = "local ASCML";
			}
			else
			{
				String[] launcherAddresses = oneSocInstRef.getLauncher().getAddresses();
				if (launcherAddresses.length > 0)
					launcherName += "(";
				for (int j=0; j < launcherAddresses.length-1; j++)
				{
					launcherName += launcherAddresses[j] + ",";
				}
				if (launcherAddresses.length > 0)
				{
					launcherName += launcherAddresses[launcherAddresses.length-1];
					launcherName += ")";
				}
			}
			oneRow[0] = typeName;
			oneRow[1] = instanceName;
			oneRow[2] = launcherName;
			societyRefTableModel.addRow(oneRow);
		}

		JTable societyRefTable = new JTable(societyRefTableModel);
		societyRefTable.setRowSelectionAllowed(false);
		societyRefTable.setColumnSelectionAllowed(false);

		JPanel societyRefPanel = new JPanel(new BorderLayout());
		societyRefPanel.setBorder(BorderFactory.createTitledBorder(" SocietyInstance-References "));
		societyRefPanel.setBackground(Color.WHITE);
		societyRefPanel.add(societyRefTable.getTableHeader(), BorderLayout.PAGE_START);
		societyRefPanel.add(societyRefTable, BorderLayout.CENTER);

		JScrollPane societyRefScrollPane = new JScrollPane(societyRefPanel);
		societyRefScrollPane.setWheelScrollingEnabled(true);
		societyRefScrollPane.setBorder(BorderFactory.createEmptyBorder());

		return societyRefScrollPane;
	}

	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonStart)
		{
            mainPanel.showDialog(new StartSocietyInstanceDialog(mainPanel, model));
		}
		else if (evt.getSource() == buttonApply)
		{
            model.setName(textFieldName.getText());
			model.setDescription(textAreaDescription.getText());
			model.setQuantity(((Number)spinnerQuantity.getValue()).intValue()+"");
			model.setNamingScheme(textFieldScheme.getText());
		}
	}

}
