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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.awt.event.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.gui.dialogs.LauncherDialog;
import jade.tools.ascml.gui.dialogs.DependencyDialog;
import jade.tools.ascml.gui.dialogs.JTreeDialog;
import jade.tools.ascml.model.jibx.SocietyInstanceReference;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.repository.ModelIntegrityChecker;

public class SocietyInstanceReferenceGeneral extends AbstractPanel implements ActionListener
{
	private JButton buttonApply;
	private JButton buttonAddSocietyInstanceReference;
	private JButton buttonRemoveSocietyInstanceReference;
    private JButton buttonAddLauncher;
	private JButton buttonEditLauncher;
	private JButton buttonRemoveLauncher;
    private JButton buttonAddDependency;
	private JButton buttonEditDependency;
	private JButton buttonRemoveDependency;
    private JButton buttonChooseSocietyType;
	private JButton buttonChooseSocietyInstance;

	private JTextField textFieldReferenceName;
	private JTextField textFieldTypeName;
	private JTextField textFieldInstanceName;
	private JTextField textFieldScheme;
	private JTextField textFieldLauncherName;

	private JSpinner spinnerQuantity;

	private JTable tableSocietyInstanceReferences;
	private JTable tableLauncher;
	private JTable tableDependencies;

	private ISocietyInstance societyInstance;
	private ISocietyInstanceReference societyInstanceReference;

	public SocietyInstanceReferenceGeneral(AbstractMainPanel parentPanel, ISocietyInstanceReference societyInstanceReference, ISocietyInstance societyInstance)
	{
		super(parentPanel);
		this.societyInstance = societyInstance;

		if (societyInstanceReference != null)
        	this.societyInstanceReference = societyInstanceReference;
        else if (societyInstance.getSocietyInstanceReferences().length > 0)
			this.societyInstanceReference = societyInstance.getSocietyInstanceReferences()[0];
		else
			this.societyInstanceReference = new SocietyInstanceReference(societyInstance);

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		this.add(createLeftSide(), new GridBagConstraints(0, 0, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(5,5,5,5), 0, 0));
		this.add(createRightSide(), new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		// initially select the model, to view
		if (tableSocietyInstanceReferences.getRowCount() > 0)
		{
			for (int i=0; i < tableSocietyInstanceReferences.getRowCount(); i++)
			{
				String societyInstanceReferenceName = (String)((DefaultTableModel)tableSocietyInstanceReferences.getModel()).getValueAt(i, 0);
				if (societyInstanceReferenceName.equals(this.societyInstanceReference.getName()))
					tableSocietyInstanceReferences.getSelectionModel().setSelectionInterval(i,i);
			}
			if (tableSocietyInstanceReferences.getSelectedRow() == -1) // no row selected yet
				tableSocietyInstanceReferences.getSelectionModel().setSelectionInterval(0,0);
		}

		setSocietyInstanceReferenceData();
	}

	private JPanel createLeftSide()
	{
		buttonAddSocietyInstanceReference = ComponentFactory.createAddButton("Create");
		buttonAddSocietyInstanceReference.addActionListener(this);

		buttonRemoveSocietyInstanceReference = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveSocietyInstanceReference.addActionListener(this);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		attributePanel.add(new JLabel("<html>Click on an SocietyIstance-reference<br>below to edit the settings !</html>"), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(createSocietyInstanceReferenceTablePane(), new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(buttonRemoveSocietyInstanceReference, new GridBagConstraints(0, 2, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0,5,5,5), 0, 0));
		attributePanel.add(buttonAddSocietyInstanceReference, new GridBagConstraints(1, 2, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,5,5,5), 0, 0));

		attributePanel.add(new JLabel(""), new GridBagConstraints(0, 3, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	private JPanel createRightSide()
	{
		// first create all the components
        buttonApply = ComponentFactory.createApplyButton("Apply Changes");
		buttonApply.addActionListener(this);

		buttonAddLauncher = ComponentFactory.createAddButton("Create");
		buttonAddLauncher.addActionListener(this);

		buttonEditLauncher = ComponentFactory.createEditButton("Edit");
		buttonEditLauncher.addActionListener(this);

		buttonRemoveLauncher = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveLauncher.addActionListener(this);

		JPanel panelLauncherButtons = new JPanel();
		panelLauncherButtons.setBackground(Color.WHITE);
		panelLauncherButtons.add(buttonRemoveLauncher);
		panelLauncherButtons.add(buttonEditLauncher);
		panelLauncherButtons.add(buttonAddLauncher);

		buttonAddDependency = ComponentFactory.createAddButton("Create");
		buttonAddDependency.addActionListener(this);

		buttonEditDependency = ComponentFactory.createEditButton("Edit");
		buttonEditDependency.addActionListener(this);

		buttonRemoveDependency = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveDependency.addActionListener(this);

		JPanel panelDependencyButtons = new JPanel();
		panelDependencyButtons.setBackground(Color.WHITE);
		panelDependencyButtons.add(buttonRemoveDependency);
		panelDependencyButtons.add(buttonEditDependency);
		panelDependencyButtons.add(buttonAddDependency);

		buttonChooseSocietyType = ComponentFactory.createThreePointButton();
		buttonChooseSocietyType.addActionListener(this);

		buttonChooseSocietyInstance = ComponentFactory.createThreePointButton();
		buttonChooseSocietyInstance.addActionListener(this);

		textFieldReferenceName = new JTextField();
		textFieldReferenceName.setMinimumSize(new Dimension(150, (int)textFieldReferenceName.getPreferredSize().getHeight()));
		textFieldReferenceName.setBackground(Color.WHITE);

		textFieldTypeName = new JTextField();
		textFieldTypeName.setPreferredSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setMinimumSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setMaximumSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setBackground(Color.WHITE);

		textFieldInstanceName = new JTextField();
		textFieldInstanceName.setPreferredSize(new Dimension(150, (int)textFieldInstanceName.getPreferredSize().getHeight()));
		textFieldInstanceName.setMinimumSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldInstanceName.setMaximumSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldInstanceName.setBackground(Color.WHITE);

		textFieldLauncherName = new JTextField();
		textFieldLauncherName.setMinimumSize(new Dimension(150, (int)textFieldLauncherName.getPreferredSize().getHeight()));
		textFieldLauncherName.setBackground(Color.WHITE);

		textFieldScheme = new JTextField();
		textFieldScheme.setMinimumSize(new Dimension(100, (int)textFieldScheme.getPreferredSize().getHeight()));
		textFieldScheme.setBackground(Color.WHITE);

		spinnerQuantity = ComponentFactory.createQuantitySpinner(1);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);
		
		// prepare Main-Panel
		attributePanel.add(new JLabel("Reference-Name:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldReferenceName, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Type-Reference:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypeName, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        attributePanel.add(buttonChooseSocietyType, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Instance-Reference:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldInstanceName, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
        attributePanel.add(buttonChooseSocietyInstance, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Quantity:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(spinnerQuantity, new GridBagConstraints(1, 3, 2, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Naming-Scheme:"), new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldScheme, new GridBagConstraints(1, 4, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Launcher-Name:"), new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldLauncherName, new GridBagConstraints(1, 5, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(createLauncherTablePane(), new GridBagConstraints(0, 6, 3, 1, 1, 0.5, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,2,1,2), 0, 0));

		attributePanel.add(panelLauncherButtons, new GridBagConstraints(0, 7, 3, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

		attributePanel.add(createDependencyTablePane(), new GridBagConstraints(0, 8, 3, 1, 1, 0.5, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,2,1,2), 0, 0));

		attributePanel.add(panelDependencyButtons, new GridBagConstraints(0, 9, 3, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

		attributePanel.add(buttonApply, new GridBagConstraints(1, 10, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,2,5,2), 0, 0));


		return attributePanel;
	}

	private void setSocietyInstanceReferenceData()
	{
		if (societyInstanceReference != null)
		{
			textFieldReferenceName.setText(societyInstanceReference.getName());
			textFieldScheme.setText(societyInstanceReference.getNamingScheme());
			if (societyInstanceReference.getTypeName() == null)
				textFieldTypeName.setText("Unknown");
			else
				textFieldTypeName.setText((societyInstanceReference.getTypeName()));

			if (societyInstanceReference.getInstanceName() == null)
				textFieldInstanceName.setText("Unknown");
			else
				textFieldInstanceName.setText((societyInstanceReference.getInstanceName()));

			if (societyInstanceReference.getLauncher().getName() == null)
				textFieldLauncherName.setText("local ASCML");
			else
				textFieldLauncherName.setText((societyInstanceReference.getLauncher().getName()));

			spinnerQuantity.setValue(societyInstanceReference.getQuantity());

			tableLauncher.setModel(createLauncherTableModel());

			if (tableLauncher.getModel().getRowCount() > 0)
			{
				tableLauncher.getSelectionModel().setSelectionInterval(0,0);
			}
		}
	}

	private JScrollPane createSocietyInstanceReferenceTablePane()
	{
		tableSocietyInstanceReferences = new JTable(createSocietyInstanceReferenceTableModel());
		tableSocietyInstanceReferences.setToolTipText("Click on a row to edit the SocietyInstance-reference !");
        JScrollPane tableScrollPane = ComponentFactory.createTableScrollPane(tableSocietyInstanceReferences);

		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = tableSocietyInstanceReferences.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) return;

				ListSelectionModel lsm = (ListSelectionModel)e.getSource();
				if (!lsm.isSelectionEmpty())
				{
					int selectedRow = lsm.getMinSelectionIndex();
					societyInstanceReference = societyInstance.getSocietyInstanceReferences()[selectedRow];
					setSocietyInstanceReferenceData();
				}
			}
		});

		return tableScrollPane;
	}

	private DefaultTableModel createSocietyInstanceReferenceTableModel()
	{
		String[] tableHeaderEntries = new String[] {"Name", "Reference"};
		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);

		ISocietyInstanceReference[] societyInstanceReferences = societyInstance.getSocietyInstanceReferences();

		for(int i=0; i < societyInstanceReferences.length; i++)
		{
			String[] oneRow = new String[2];

			oneRow[0] = societyInstanceReferences[i].getName();
			if (societyInstanceReferences[i].getTypeName() == null)
				oneRow[1] = "Unknown";
			else
				oneRow[1] = societyInstanceReferences[i].getTypeName() + "." + societyInstanceReferences[i].getInstanceName();
			tableModel.addRow(oneRow);
		}

		return tableModel;
	}

	private JScrollPane createLauncherTablePane()
	{
		tableLauncher = new JTable(createLauncherTableModel());
		JScrollPane tableScrollPane = ComponentFactory.createTableScrollPane(tableLauncher);

		if (tableLauncher.getModel().getRowCount() > 0)
			tableLauncher.getSelectionModel().setSelectionInterval(0,0);

		return tableScrollPane;
	}

	private DefaultTableModel createLauncherTableModel()
	{
		String[] tableHeaderEntries = new String[] {"Launcher-Addresses"};
		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);

		if (societyInstanceReference != null)
		{
			ILauncher launcher = societyInstanceReference.getLauncher();
			String[] addresses = launcher.getAddresses();
			for(int i=0; i < addresses.length; i++)
			{
				String[] oneRow = new String[1];

				oneRow[0] = addresses[i];
				tableModel.addRow(oneRow);
			}
		}

		return tableModel;
	}

	private JScrollPane createDependencyTablePane()
	{
		tableDependencies = new JTable(createDependencyTableModel());
		JScrollPane tableScrollPane = ComponentFactory.createTableScrollPane(tableDependencies);

		if (tableDependencies.getModel().getRowCount() > 0)
			tableDependencies.getSelectionModel().setSelectionInterval(0,0);

		return tableScrollPane;
	}

	private DefaultTableModel createDependencyTableModel()
	{
		String[] tableHeaderEntries = new String[] {"Type of Dependency"};
		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);

		if (societyInstanceReference != null)
		{
			IDependency[] dependencies = societyInstanceReference.getDependencies();
			for(int i=0; i < dependencies.length; i++)
			{
				String[] oneRow = new String[1];

				oneRow[0] = dependencies[i].getType();
				tableModel.addRow(oneRow);
			}
		}

		return tableModel;
	}

	public void actionPerformed(ActionEvent evt)
	{
        if (evt.getSource() == buttonAddLauncher)
		{
			LauncherDialog dialog = new LauncherDialog("");
			Object result = dialog.showDialog(parentFrame);

			if ((result != null) && !result.equals(""))
			{
				societyInstanceReference.getLauncher().addAddress((String)result);
			}
			tableLauncher.setModel(createLauncherTableModel());
			tableLauncher.getSelectionModel().setSelectionInterval(tableLauncher.getModel().getRowCount()-1,tableLauncher.getModel().getRowCount()-1);
		}
		else if (evt.getSource() == buttonRemoveLauncher)
		{
			int selectedRow = tableLauncher.getSelectedRow();
			societyInstanceReference.getLauncher().removeAddress((String)tableLauncher.getModel().getValueAt(selectedRow,0));
			tableLauncher.setModel(createLauncherTableModel());
			if (tableLauncher.getModel().getRowCount() > 0)
				tableLauncher.getSelectionModel().setSelectionInterval(0,0);
		}
		else if (evt.getSource() == buttonEditLauncher)
		{
			if (tableLauncher.getModel().getRowCount() > 0)
			{
				String address = (String)tableLauncher.getModel().getValueAt(tableLauncher.getSelectedRow(),0);
				LauncherDialog dialog = new LauncherDialog(address);
				Object result = dialog.showDialog(parentFrame);

				if ((result != null) && !result.equals(""))
				{
					societyInstanceReference.getLauncher().removeAddress(address);
					societyInstanceReference.getLauncher().addAddress((String)result);
				}
				tableLauncher.setModel(createLauncherTableModel());
				tableLauncher.getSelectionModel().setSelectionInterval(tableLauncher.getModel().getRowCount()-1,tableLauncher.getModel().getRowCount()-1);
			}
		}
		else if (evt.getSource() == buttonAddDependency)
		{
			DependencyDialog dialog = new DependencyDialog(null);
			IDependency result = (IDependency)dialog.showDialog(parentFrame);

			if (result != null)
			{
				societyInstanceReference.addDependency(result);
			}
			tableDependencies.setModel(createDependencyTableModel());
			tableDependencies.getSelectionModel().setSelectionInterval(tableDependencies.getModel().getRowCount()-1,tableDependencies.getModel().getRowCount()-1);
		}
		else if (evt.getSource() == buttonRemoveDependency)
		{
			int selectedRow = tableDependencies.getSelectedRow();
			if (tableDependencies.getModel().getRowCount() > 0)
			{
                societyInstanceReference.removeDependency(selectedRow);

				tableDependencies.setModel(createDependencyTableModel());
				if (tableDependencies.getModel().getRowCount() > 0)
					tableDependencies.getSelectionModel().setSelectionInterval(0,0);
			}
		}
		else if (evt.getSource() == buttonEditDependency)
		{
			if (tableDependencies.getModel().getRowCount() > 0)
			{
				int selectedRow = tableDependencies.getSelectedRow();

				DependencyDialog dialog = new DependencyDialog(societyInstanceReference.getDependencies()[selectedRow]);

				IDependency result = (IDependency)dialog.showDialog(parentFrame);
				if (result != null)
				{
					societyInstanceReference.removeDependency(selectedRow);
					societyInstanceReference.addDependency(result);
				}
				tableDependencies.setModel(createDependencyTableModel());
				tableDependencies.getSelectionModel().setSelectionInterval(tableDependencies.getModel().getRowCount()-1,tableDependencies.getModel().getRowCount()-1);
			}
		}
		else if (evt.getSource() == buttonChooseSocietyType)
		{
			JTreeDialog treeDialog = ComponentFactory.createSocietyTypeTreeDialog(parentFrame, getRepository().getModelIndex());
			treeDialog.setVisible(true);
			String fullyQualifiedModelName = (String)treeDialog.getResult();
			if ((fullyQualifiedModelName != null) && !fullyQualifiedModelName.equals(""))
				textFieldTypeName.setText(fullyQualifiedModelName);
		}
		else if (evt.getSource() == buttonChooseSocietyInstance)
		{
			JTreeDialog treeDialog = ComponentFactory.createSocietyInstanceTreeDialog(parentFrame, getRepository().getModelIndex(), textFieldTypeName.getText());
			treeDialog.setVisible(true);
			String fullyQualifiedModelName = (String)treeDialog.getResult();
			if ((fullyQualifiedModelName != null) && !fullyQualifiedModelName.equals("") && !fullyQualifiedModelName.equals(ISocietyInstance.NAME_UNKNOWN))
				textFieldInstanceName.setText(fullyQualifiedModelName);
		}
		else if (evt.getSource() == buttonApply)
		{
			societyInstanceReference.setName(textFieldReferenceName.getText());
			societyInstanceReference.setNamingScheme(textFieldScheme.getText());
			societyInstanceReference.setQuantity(((Number)spinnerQuantity.getValue()).intValue()+"");
			societyInstanceReference.setTypeName(textFieldTypeName.getText());
			societyInstanceReference.setInstanceName(textFieldInstanceName.getText());
            societyInstanceReference.getLauncher().setName(textFieldLauncherName.getText());

			// try to set locally referenced SocietyInstance
			if (!societyInstanceReference.isRemoteReference())
			{
				ISocietyType referencedTypeModel = (ISocietyType)getRepository().getModelIndex().getModel(societyInstanceReference.getTypeName());
				if (referencedTypeModel != null)
				{
					ISocietyInstance referencedInstanceModel = referencedTypeModel.getSocietyInstance(societyInstanceReference.getInstanceName());
					societyInstanceReference.setLocallyReferencedSocietyInstance(referencedInstanceModel);
				}
				else
					societyInstanceReference.setLocallyReferencedSocietyInstance(null);
			}

			if (tableSocietyInstanceReferences.getSelectedRow() == -1)
			{
				// a new model has been created
				societyInstanceReference.setParentSocietyInstance(societyInstance);
				societyInstance.addSocietyInstanceReference(societyInstanceReference);

				// tableAgentInstances.setModel(createAgentInstanceTableModel());
				// tableAgentInstances.getSelectionModel().setSelectionInterval(tableAgentInstances.getRowCount()-1, tableAgentInstances.getRowCount()-1);
			}

			ModelIntegrityChecker checker = new ModelIntegrityChecker();
			checker.checkIntegrity(societyInstance.getParentSocietyType());

			getRepository().getProject().throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.SOCIETYINSTANCE_REFERENCE_SELECTED, societyInstanceReference, getRepository().getProject()));
		}
		else if (evt.getSource() == buttonAddSocietyInstanceReference)
		{
			societyInstanceReference = new SocietyInstanceReference(societyInstance);
			tableSocietyInstanceReferences.getSelectionModel().removeSelectionInterval(0, tableSocietyInstanceReferences.getSelectedRow());
			setSocietyInstanceReferenceData();
		}
		else if (evt.getSource() == buttonRemoveSocietyInstanceReference)
		{
			societyInstance.removeSocietyInstanceReference(societyInstance.getSocietyInstanceReference(tableSocietyInstanceReferences.getSelectedRow()));
			// maybe it's better to just throw a ProjectChangedEvent, AGENTINSTANCE_SELECTED,
			// because this way the whole panel is newly instantiated.

			if (societyInstance.getSocietyInstanceReferences().length > 0)
			{
				societyInstanceReference = societyInstance.getSocietyInstanceReference(0);
				tableSocietyInstanceReferences.setModel(createSocietyInstanceReferenceTableModel());
				tableSocietyInstanceReferences.getSelectionModel().setSelectionInterval(0,0);
			}
			else
			{
				societyInstanceReference = new SocietyInstanceReference(societyInstance);
				tableSocietyInstanceReferences.setModel(createSocietyInstanceReferenceTableModel());
			}
			setSocietyInstanceReferenceData();
		}
	}
}
