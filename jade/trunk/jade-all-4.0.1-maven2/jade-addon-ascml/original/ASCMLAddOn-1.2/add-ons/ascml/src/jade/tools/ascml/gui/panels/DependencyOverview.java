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
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.events.DependencyChangedListener;
import jade.tools.ascml.events.DependencyChangedEvent;

public class DependencyOverview extends AbstractPanel implements ActionListener, DependencyChangedListener
{
	private JButton buttonAddDependency;
	private JButton buttonRemoveDependency;

	private JTable tableDependencies;
	private DependencyDetails panelDependencyDetails;

	private Object model;

	private IDependency dependency;

	public DependencyOverview(AbstractMainPanel mainPanel, Object agentInstanceOrSocietyInstanceReference)
	{
		super(mainPanel);
		this.model = agentInstanceOrSocietyInstanceReference;

		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);
        panelDependencyDetails = new DependencyDetails(this);
        panelDependencyDetails.setDependency(null);
		
        this.add(createLeftSide(), new GridBagConstraints(0, 0, 1, 1, 0.5, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(panelDependencyDetails, new GridBagConstraints(1, 0, 1, 1, 0.5, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createLeftSide()
	{
		buttonAddDependency = ComponentFactory.createAddButton("Create");
		buttonAddDependency.addActionListener(this);

		buttonRemoveDependency = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveDependency.addActionListener(this);

		JPanel panelDependencyButtons = new JPanel();
		panelDependencyButtons.setBackground(Color.WHITE);
		panelDependencyButtons.add(buttonRemoveDependency);
		panelDependencyButtons.add(buttonAddDependency);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		attributePanel.add(createDependencyTablePane(), new GridBagConstraints(0, 0, 1, 1, 0.4, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,2,1,2), 0, 0));
		attributePanel.add(panelDependencyButtons, new GridBagConstraints(0, 1, 1, 1, 0.6, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

		return attributePanel;
	}

	private JScrollPane createDependencyTablePane()
	{
		tableDependencies = new JTable(createDependencyTableModel());
		JScrollPane tableScrollPane = ComponentFactory.createTableScrollPane(tableDependencies);

		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = tableDependencies.getSelectionModel();
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

					if (model instanceof IAgentInstance)
						panelDependencyDetails.setDependency(((IAgentInstance)model).getDependencies()[selectedRow]);
					else
						panelDependencyDetails.setDependency(((ISocietyInstanceReference)model).getDependencies()[selectedRow]);
				}
			}
		});

		return tableScrollPane;
	}

	private DefaultTableModel createDependencyTableModel()
	{
		String[] tableHeaderEntries = new String[] {"Type of Dependency"};
		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);

		if (model != null)
		{
			IDependency[] dependencies;
			if (model instanceof IAgentInstance)
				dependencies = ((IAgentInstance)model).getDependencies();
			else
				dependencies = ((ISocietyInstanceReference)model).getDependencies();

			for(int i=0; (i < dependencies.length); i++)
			{
				String[] oneRow = new String[1];

				oneRow[0] = dependencies[i].getType();
				tableModel.addRow(oneRow);
			}
		}

		return tableModel;
	}

	public void dependencyChanged(DependencyChangedEvent evt)
	{
		IDependency dependencyObject = evt.getDependency();

		// user pressed apply-button on the dependencydetail-panel.
		if (tableDependencies.getSelectedRow() == -1)
		{
			// add the dependency to the model, because this dependency has been newly created
			if (model instanceof IAgentInstance)
			{
				((IAgentInstance)model).addDependency(dependencyObject);
			}
			else
			{
				((ISocietyInstanceReference)model).addDependency(dependencyObject);
			}
		}
		else
		{
			int selectedRow = tableDependencies.getSelectedRow();
			// remove the selected dependency from the model, and add the new dependency
			if (model instanceof IAgentInstance)
			{
				((IAgentInstance)model).removeDependency(selectedRow);
				((IAgentInstance)model).addDependency(dependencyObject);
			}
			else
			{
				((ISocietyInstanceReference)model).removeDependency(selectedRow);
				((ISocietyInstanceReference)model).addDependency(dependencyObject);
			}
		}

		tableDependencies.setModel(createDependencyTableModel());
		tableDependencies.getSelectionModel().setSelectionInterval(tableDependencies.getRowCount()-1, tableDependencies.getRowCount()-1);
	}

	public void actionPerformed(ActionEvent evt)
	{
        if (evt.getSource() == buttonAddDependency)
		{
			tableDependencies.getSelectionModel().removeSelectionInterval(tableDependencies.getSelectedRow(), tableDependencies.getSelectedRow());
			panelDependencyDetails.setDependency(null);
		}
		else if (evt.getSource() == buttonRemoveDependency)
		{
			int selectedRow = tableDependencies.getSelectionModel().getMinSelectionIndex();
			if (model instanceof IAgentInstance)
			{
				((IAgentInstance)model).removeDependency(selectedRow);
			}
			else
			{
				((ISocietyInstanceReference)model).removeDependency(selectedRow);
			}
			tableDependencies.setModel(createDependencyTableModel());
			if (tableDependencies.getRowCount() > 0)
				tableDependencies.getSelectionModel().setSelectionInterval(0,0);
		}
	}

}
