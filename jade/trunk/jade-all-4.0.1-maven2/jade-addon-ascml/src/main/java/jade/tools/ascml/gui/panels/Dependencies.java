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

import jade.tools.ascml.absmodel.dependency.*;
import jade.tools.ascml.absmodel.IServiceDescription;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Iterator;

public class Dependencies extends AbstractPanel implements ChangeListener, ActionListener
{
	private final static int MAX_DELAY = 3600000; // one hour

	private JSpinner delaySpinner;
	private JCheckBox activeCheckBox;

    private JTable dependencyTable;

	private Vector<IDependency> models;

	public Dependencies(AbstractMainPanel mainPanel, Vector<IDependency> models)
	{
		super(mainPanel);
		this.models = models;

		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

		this.add(new JLabel("<html><h2>&nbsp;<i>Dependencies</i></h2></html>"), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		if (this.models.size() > 0)
		{
			this.add(new JLabel("<html><h3>&nbsp;The following dependencies have been specified:</h3></html>"), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
			this.add(createDependencyTable(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		}
		else
			this.add(new JLabel("<html><h3>&nbsp;No dependencies specified</h3></html>"), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		// first prepare all the components to display

		// prepare delay-spinner
		int delay = 0;
		if (models.firstElement().getType() == IDependency.DELAY_DEPENDENCY)
			delay = ((IDelayDependency)models.firstElement()).getQuantityAsInt();

		SpinnerModel spinnerModel = new SpinnerNumberModel(delay, 0, MAX_DELAY, 100);
		delaySpinner = new JSpinner(spinnerModel);
		delaySpinner.addChangeListener(this);
		delaySpinner.setToolTipText("Set the amount of milliseconds to wait before the societyinstance is going to start (or before checking the other dependencies). Maximum is one hour ("+MAX_DELAY+" ms) ");

		activeCheckBox = new JCheckBox("actively engage in fulfilling", models.firstElement().isActive());
		activeCheckBox.setBackground(Color.WHITE);
		activeCheckBox.addActionListener(this);
		activeCheckBox.setToolTipText("If checked, the ASCML actively engages in fulfilling this dependency by forcing the startup of the appropiate models.");

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);
		attributePanel.add(new JLabel("Delay (in ms) :"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(delaySpinner, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(activeCheckBox, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	private JScrollPane createDependencyTable()
	{
		String[] tableHeaderEntries = new String[] {"Type", "Attributes", "Provider"};
		DefaultTableModel dependencyTableModel = new DefaultTableModel(tableHeaderEntries, 0);

		Iterator<IDependency> modelIt = models.iterator();
		while (modelIt.hasNext())
		{
			IDependency oneModel = modelIt.next();
			String[] oneRow = new String[3];
            oneRow[0] =oneModel.getType();

			if (oneModel instanceof IAgentInstanceDependency)
			{
				IAgentInstanceDependency oneDependency = (IAgentInstanceDependency)oneModel;
				oneRow[1] = "<html><b>name</b>=" + oneDependency.getName() + "; <b>status</b>=" + oneDependency.getStatus() + "</html>";
				oneRow[2] = "<html><b>name</b>=" + oneDependency.getProvider().getName() + "; <b>addresses</b>=" + oneDependency.getProvider().getAddresses() + "</html>";
			}
            else if (oneModel instanceof IAgentTypeDependency)
			{
				IAgentTypeDependency oneDependency = (IAgentTypeDependency)oneModel;
				oneRow[1] = "<html><b>name</b>=" + oneDependency.getName() + "; <b>quantity</b>=" + oneDependency.getQuantity() + "</html>";
				oneRow[2] = "local ASCML";
			}
            else if (oneModel instanceof IServiceDependency)
			{
				IServiceDependency oneDependency = (IServiceDependency)oneModel;
				IServiceDescription description = oneDependency.getServiceDescription();
				oneRow[1] = "<html><b>name</b>=" + description.getName() + "; <b>type</b>=" + description.getType() + "; <b>ownership</b>=" + description.getOwnership();
				if (description.getProtocols().length > 0)
					oneRow[1] += "; <b>protocols</b>=" + description.getProtocols();
				if (description.getOntologies().length > 0)
					oneRow[1] += "; <b>ontologies</b>=" + description.getOntologies();
				if (description.getLanguages().length > 0)
					oneRow[1] += "; <b>languages</b>=" + description.getLanguages();
				if (description.getProperties().length > 0)
					oneRow[1] += "; <b>properties</b>=" + description.getProperties();
				oneRow[1] += "</html>";
				oneRow[2] = "local ASCML";
			}
			else if (oneModel instanceof ISocietyInstanceDependency)
			{
				ISocietyInstanceDependency oneDependency = (ISocietyInstanceDependency)oneModel;
				oneRow[1] = "<html><b>instance-name</b>=" + oneDependency.getSocietyInstance() + "; <b>type-name</b>=" + oneDependency.getSocietyType() + "; <b>status</b>=" + oneDependency.getStatus() + "</html>";
				oneRow[2] = "<html><b>name</b>=" + oneDependency.getProvider().getName() + "; <b>addresses</b>=" + oneDependency.getProvider().getAddresses() + "</html>";
			}
			else if (oneModel instanceof ISocietyTypeDependency)
			{
				ISocietyTypeDependency oneDependency = (ISocietyTypeDependency)oneModel;
				oneRow[1] = "<html><b>name</b>=" + oneDependency.getName() + "; <b>quantity</b>=" + oneDependency.getQuantity() + "</html>";
				oneRow[2] = "local ASCML";
			}
			else if (oneModel instanceof IDelayDependency)
			{
				IDelayDependency oneDependency = (IDelayDependency)oneModel;
				oneRow[1] = "<html><b>quantity</b>=" + oneDependency.getQuantity() + " (milliseconds)";
				oneRow[2] = "local ASCML";
			}

			dependencyTableModel.addRow(oneRow);
		}

		dependencyTable = new JTable(dependencyTableModel);
		dependencyTable.setRowSelectionAllowed(false);
		dependencyTable.setColumnSelectionAllowed(false);
		initColumnSizes();

		JPanel dependencyTablePanel = new JPanel(new BorderLayout());
		dependencyTablePanel.setBorder(BorderFactory.createTitledBorder(" Dependencies "));
		dependencyTablePanel.setBackground(Color.WHITE);
		dependencyTablePanel.add(dependencyTable.getTableHeader(), BorderLayout.PAGE_START);
		dependencyTablePanel.add(dependencyTable, BorderLayout.CENTER);

		JScrollPane dependencyTableScrollPane = new JScrollPane(dependencyTablePanel);
		dependencyTableScrollPane.setWheelScrollingEnabled(true);
		dependencyTableScrollPane.setBorder(BorderFactory.createEmptyBorder());

		return dependencyTableScrollPane;
	}

	/*
	* This method picks good column sizes.
	* If all column heads are wider than the column's cells'
	* contents, then you can just use column.sizeWidthToFit().
	*/
	private void initColumnSizes()
	{
		DefaultTableModel model = (DefaultTableModel)dependencyTable.getModel();
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;

		TableCellRenderer headerRenderer = dependencyTable.getTableHeader().getDefaultRenderer();

		for (int i = 0; i < dependencyTable.getColumnCount(); i++)
		{
			column = dependencyTable.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(null, column.getHeaderValue(), false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			Class columnClass = model.getColumnClass(i);
			TableCellRenderer tableRenderer = dependencyTable.getDefaultRenderer(columnClass);
			comp = tableRenderer.getTableCellRendererComponent(dependencyTable, dependencyTable.getModel().getValueAt(0, i), false, false, 0, i);
			cellWidth = comp.getPreferredSize().width;

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == activeCheckBox)
		{
            models.firstElement().setActive(activeCheckBox.isSelected());
		}
	}

	public void stateChanged(ChangeEvent evt)
	{
		if (evt.getSource() == delaySpinner)
		{
            if (models.firstElement().getType() == IDependency.DELAY_DEPENDENCY)
				((IDelayDependency)models.firstElement()).setQuantity(((Integer)delaySpinner.getValue()).intValue());
		}
	}

}
