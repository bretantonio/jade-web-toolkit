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
import java.awt.*;
import java.awt.event.*;
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.dependency.ISocietyInstanceDependency;
import jade.tools.ascml.absmodel.IProvider;

public class DependencySocietyInstance extends JPanel implements ActionListener
{
	private JTextField textFieldInstanceName;
	private JTextField textFieldTypeName;
	private JTextField textFieldProviderName;
	private JTextField textFieldAddress;

	private JComboBox comboBoxStatus;

	private JButton buttonAddAddress;
	private JButton buttonRemoveAddress;

	private JList listAddresses;

	private ISocietyInstanceDependency dependency;

	/**
	 *
	 * @param parameterChangedListener  Listener to be informed, when the user presses the
	 *                                  Apply-button.
	 */
	public DependencySocietyInstance(ISocietyInstanceDependency dependency)
	{
		this.dependency = dependency;

		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

        this.add(createAttributePanel(), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		textFieldTypeName = new JTextField(dependency.getSocietyType(), 20);
		textFieldTypeName.setPreferredSize(new Dimension(100, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setMinimumSize(new Dimension(100, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setMaximumSize(new Dimension(100, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setBackground(Color.WHITE);

		textFieldInstanceName = new JTextField(dependency.getSocietyInstance(), 20);
		textFieldInstanceName.setPreferredSize(new Dimension(100, (int)textFieldInstanceName.getPreferredSize().getHeight()));
		textFieldInstanceName.setMinimumSize(new Dimension(100, (int)textFieldInstanceName.getPreferredSize().getHeight()));
		textFieldInstanceName.setMaximumSize(new Dimension(100, (int)textFieldInstanceName.getPreferredSize().getHeight()));
		textFieldInstanceName.setBackground(Color.WHITE);

		textFieldProviderName = new JTextField(dependency.getProvider().getName(), 20);
		textFieldProviderName.setPreferredSize(new Dimension(100, (int)textFieldProviderName.getPreferredSize().getHeight()));
		textFieldProviderName.setMinimumSize(new Dimension(100, (int)textFieldProviderName.getPreferredSize().getHeight()));
		textFieldProviderName.setMaximumSize(new Dimension(100, (int)textFieldProviderName.getPreferredSize().getHeight()));
		textFieldProviderName.setBackground(Color.WHITE);

		textFieldAddress = new JTextField(20);
		textFieldAddress.setPreferredSize(new Dimension(100, (int)textFieldAddress.getPreferredSize().getHeight()));
		textFieldAddress.setMinimumSize(new Dimension(100, (int)textFieldAddress.getPreferredSize().getHeight()));
		textFieldAddress.setMaximumSize(new Dimension(100, (int)textFieldAddress.getPreferredSize().getHeight()));
		textFieldAddress.setBackground(Color.WHITE);

        listAddresses = new JList(new DefaultListModel());
		JScrollPane listScrollPane = ComponentFactory.createListScrollPane(listAddresses);
		listScrollPane.setPreferredSize(new Dimension(100, (int)listScrollPane.getPreferredSize().getHeight()));
		listScrollPane.setMinimumSize(new Dimension(100, (int)listScrollPane.getPreferredSize().getHeight()));
		listScrollPane.setMaximumSize(new Dimension(100, (int)listScrollPane.getPreferredSize().getHeight()));

		String[] addresses = dependency.getProvider().getAddresses();
		for (int i=0; i < addresses.length; i++)
		{
			((DefaultListModel)listAddresses.getModel()).addElement(addresses[i]);
		}
		if (listAddresses.getModel().getSize() > 0)
			listAddresses.setSelectedIndex(0);

		buttonAddAddress = ComponentFactory.createAddButton("Add");
		buttonAddAddress.addActionListener(this);

		buttonRemoveAddress = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveAddress.addActionListener(this);

		comboBoxStatus = new JComboBox(new String[] { IDependency.STATE_FUNCTIONAL, IDependency.STATE_NONFUNCTIONAL, IDependency.STATE_STARTING, IDependency.STATE_STOPPING, IDependency.STATE_ERROR });
		comboBoxStatus.setBackground(Color.WHITE);
		comboBoxStatus.setSelectedItem(dependency.getStatus());

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		attributePanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypeName, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Instance-Name:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldInstanceName, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Status:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(comboBoxStatus, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Provider-Name:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldProviderName, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Provider-Addresses:"), new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(listScrollPane, new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));

		attributePanel.add(buttonRemoveAddress, new GridBagConstraints(1, 5, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(1,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Add new Address:"), new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(textFieldAddress, new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));

		attributePanel.add(buttonAddAddress, new GridBagConstraints(1, 7, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(1,5,5,5), 0, 0));

		return attributePanel;
	}

	/**
	 * Get the result of the dependency-editing.
	 * @return  The result of the editing.
	 */
	public IDependency getDependency()
	{
		dependency.setSocietyType(textFieldTypeName.getText());
		dependency.setSocietyInstance(textFieldInstanceName.getText());
		dependency.setStatus((String)comboBoxStatus.getSelectedItem());
		IProvider provider = dependency.getProvider();
		provider.setName(textFieldProviderName.getText());
        String[] addresses = new String[listAddresses.getModel().getSize()];
		for (int i=0; i < addresses.length; i++)
		{
			addresses[i] = (String)listAddresses.getModel().getElementAt(i);
		}
		provider.setAddresses(addresses);
		return dependency;
	}

	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent evt)
	{
        if (evt.getSource() == buttonAddAddress)
		{
			if (!textFieldAddress.getText().equals(""))
			{
				((DefaultListModel)listAddresses.getModel()).addElement(textFieldAddress.getText());
				listAddresses.setSelectedIndex(listAddresses.getModel().getSize()-1);
			}
		}
		if (evt.getSource() == buttonRemoveAddress)
		{
			if (listAddresses.getSelectedIndex() != -1)
				((DefaultListModel)listAddresses.getModel()).removeElementAt(listAddresses.getSelectedIndex());
			if (listAddresses.getModel().getSize() > 0)
				listAddresses.setSelectedIndex(0);
		}
	}

}
