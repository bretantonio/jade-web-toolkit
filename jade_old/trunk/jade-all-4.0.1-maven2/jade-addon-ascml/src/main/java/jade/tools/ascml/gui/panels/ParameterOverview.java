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
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import jade.tools.ascml.gui.dialogs.StartSocietyInstanceDialog;
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.model.jibx.Launcher;
import jade.tools.ascml.model.jibx.Parameter;
import jade.tools.ascml.events.ParameterChangedListener;
import jade.tools.ascml.events.ParameterChangedEvent;

public class ParameterOverview extends AbstractPanel implements ActionListener, ParameterChangedListener
{
	private JButton buttonAddParameter;
	private JButton buttonRemoveParameter;

	private JTable tableParameter;
	private ParameterDetails panelParameterDetails;

	private Object agentModel;

	private Object parameter;

	public ParameterOverview(AbstractMainPanel mainPanel, Object agentTypeOrInstance)
	{
		super(mainPanel);
		this.agentModel = agentTypeOrInstance;

		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);
        panelParameterDetails = new ParameterDetails(this);
        panelParameterDetails.setPreferredSize(new Dimension(330, (int)panelParameterDetails.getPreferredSize().getHeight()));
		panelParameterDetails.setMinimumSize(new Dimension(330, (int)panelParameterDetails.getPreferredSize().getHeight()));
		panelParameterDetails.setMaximumSize(new Dimension(330, (int)panelParameterDetails.getPreferredSize().getHeight()));

        this.add(createLeftSide(), new GridBagConstraints(0, 0, 1, 1, 0.5, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(panelParameterDetails, new GridBagConstraints(1, 0, 1, 1, 0.5, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createLeftSide()
	{
		buttonAddParameter = ComponentFactory.createAddButton("Create");
		buttonAddParameter.addActionListener(this);

		buttonRemoveParameter = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveParameter.addActionListener(this);

		JPanel panelParameterButtons = new JPanel();
		panelParameterButtons.setBackground(Color.WHITE);
		panelParameterButtons.add(buttonRemoveParameter);
		panelParameterButtons.add(buttonAddParameter);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		attributePanel.add(createParameterTablePane(), new GridBagConstraints(0, 0, 1, 1, 0.4, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,2,1,2), 0, 0));
		attributePanel.add(panelParameterButtons, new GridBagConstraints(0, 1, 1, 1, 0.6, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,2,5,2), 0, 0));

		return attributePanel;
	}

	private JScrollPane createParameterTablePane()
	{

		tableParameter = new JTable(createParameterTableModel());
		JScrollPane tableScrollPane = ComponentFactory.createTableScrollPane(tableParameter);

		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = tableParameter.getSelectionModel();
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
					String paramName = (String)tableParameter.getModel().getValueAt(selectedRow, 0);
					if (agentModel instanceof IAgentType)
					{
						if (selectedRow < ((IAgentType)agentModel).getParameters().length)
						{
							IParameter parameter = ((IAgentType)agentModel).getParameter(paramName);
							panelParameterDetails.setTypeParameter(parameter);
						}
						else
						{
							IParameterSet parameterSet = ((IAgentType)agentModel).getParameterSet(paramName);
							panelParameterDetails.setTypeParameterSet(parameterSet);
						}
					}
					else
					{
						if (selectedRow < ((IAgentInstance)agentModel).getParameters().length)
						{
							IParameter parameter = ((IAgentInstance)agentModel).getParameter(paramName);
							if (((IAgentInstance)agentModel).getType() != null)
								panelParameterDetails.setInstanceParameter(parameter, ((IAgentInstance)agentModel).getType().getParameter(parameter.getName()));
							else
								panelParameterDetails.setInstanceParameter(parameter, null);
						}
						else
						{
							IParameterSet parameterSet = ((IAgentInstance)agentModel).getParameterSet(paramName);
							if (((IAgentInstance)agentModel).getType() != null)
								panelParameterDetails.setInstanceParameterSet(parameterSet, ((IAgentInstance)agentModel).getType().getParameterSet(parameterSet.getName()));
							else
								panelParameterDetails.setInstanceParameterSet(parameterSet, null);
						}
					}
				}
			}
		});

		if (tableParameter.getModel().getRowCount() > 0)
		{
			tableParameter.getSelectionModel().setSelectionInterval(0,0);
		}

		return tableScrollPane;
	}

	private DefaultTableModel createParameterTableModel()
	{
		String[] tableHeaderEntries = new String[] {"Name", "Value"};
		DefaultTableModel tableModel = new DefaultTableModel(tableHeaderEntries, 0);

		IParameter[] parameters;
		if (agentModel instanceof IAgentType)
			parameters = ((IAgentType)agentModel).getParameters();
		else
			parameters = ((IAgentInstance)agentModel).getParameters();

		for(int i=0; i < parameters.length; i++)
		{
			String[] oneRow = new String[2];

			oneRow[0] = parameters[i].getName();
			oneRow[1] = parameters[i].getValue();
			tableModel.addRow(oneRow);
		}

		IParameterSet[] parameterSets;
		if (agentModel instanceof IAgentType)
			parameterSets = ((IAgentType)agentModel).getParameterSets();
		else
			parameterSets = ((IAgentInstance)agentModel).getParameterSets();

		for(int i=0; i < parameterSets.length; i++)
		{
			String[] oneRow = new String[2];

			oneRow[0] = parameterSets[i].getName();
			oneRow[1] = parameterSets[i].getValueList().toString();
			tableModel.addRow(oneRow);
		}

		return tableModel;
	}

	public void parameterChanged(ParameterChangedEvent evt)
	{
		Object parameterObject = evt.getParameter();

		// user pressed apply-button on the parameter-panel.
		if (tableParameter.getSelectedRow() == -1)
		{
			// add the parameter to the agentModel, because this parameter has been newly created
			if (agentModel instanceof IAgentType)
			{
				if (parameterObject instanceof IParameter)
					((IAgentType)agentModel).addParameter((IParameter)parameterObject);
				else
					((IAgentType)agentModel).addParameterSet((IParameterSet)parameterObject);
			}
			else
			{
				if (parameterObject instanceof IParameter)
					((IAgentInstance)agentModel).addParameter((IParameter)parameterObject);
				else
					((IAgentInstance)agentModel).addParameterSet((IParameterSet)parameterObject);
			}
		}
		else
		{
			int selectedRow = tableParameter.getSelectedRow();
			String paramName = (String)tableParameter.getModel().getValueAt(selectedRow, 0);

			// remove the selected parameter from the agentModel, and add the new parameter
			if (agentModel instanceof IAgentType)
			{
				if (selectedRow < ((IAgentType)agentModel).getParameters().length)
					((IAgentType)agentModel).removeParameter(paramName);
				else
					((IAgentType)agentModel).removeParameterSet(paramName);

				if (parameterObject instanceof IParameter)
					((IAgentType)agentModel).addParameter((IParameter)parameterObject);
				else
					((IAgentType)agentModel).addParameterSet((IParameterSet)parameterObject);
			}
			else
			{
				if (selectedRow < ((IAgentInstance)agentModel).getParameters().length)
					((IAgentInstance)agentModel).removeParameter(paramName);
				else
					((IAgentInstance)agentModel).removeParameterSet(paramName);

				if (parameterObject instanceof IParameter)
					((IAgentInstance)agentModel).addParameter((IParameter)parameterObject);
				else
					((IAgentInstance)agentModel).addParameterSet((IParameterSet)parameterObject);
			}
		}

		tableParameter.setModel(createParameterTableModel());
		tableParameter.getSelectionModel().setSelectionInterval(tableParameter.getRowCount()-1, tableParameter.getRowCount()-1);
	}

	public void actionPerformed(ActionEvent evt)
	{
        if (evt.getSource() == buttonAddParameter)
		{
			tableParameter.getSelectionModel().removeSelectionInterval(tableParameter.getSelectedRow(), tableParameter.getSelectedRow());
			if (agentModel instanceof IAgentType)
				panelParameterDetails.setTypeParameter(new Parameter());
			else
				panelParameterDetails.setInstanceParameter(new Parameter(), null);
		}
		else if (evt.getSource() == buttonRemoveParameter)
		{
			int selectedRow = tableParameter.getSelectionModel().getMinSelectionIndex();
			String paramName = (String)tableParameter.getModel().getValueAt(selectedRow, 0);
			if (agentModel instanceof IAgentType)
			{
				if (selectedRow < ((IAgentType)agentModel).getParameters().length)
					((IAgentType)agentModel).removeParameter(paramName);
				else
					((IAgentType)agentModel).removeParameterSet(paramName);
			}
			else
			{
				if (selectedRow < ((IAgentInstance)agentModel).getParameters().length)
					((IAgentInstance)agentModel).removeParameter(paramName);
				else
					((IAgentInstance)agentModel).removeParameterSet(paramName);
			}
			tableParameter.setModel(createParameterTableModel());
			if (tableParameter.getRowCount() > 0)
				tableParameter.getSelectionModel().setSelectionInterval(0,0);
		}
	}

}
