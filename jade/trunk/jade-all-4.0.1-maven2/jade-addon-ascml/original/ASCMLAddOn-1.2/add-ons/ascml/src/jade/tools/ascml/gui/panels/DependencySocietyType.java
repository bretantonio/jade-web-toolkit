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
import jade.tools.ascml.absmodel.dependency.ISocietyTypeDependency;

public class DependencySocietyType extends JPanel implements ActionListener
{
	private JSpinner spinnerQuantity;
    private JTextField textFieldTypeName;
	private JCheckBox checkBoxActive;

	private ISocietyTypeDependency dependency;

	/**
	 *
	 * @param parameterChangedListener  Listener to be informed, when the user presses the
	 *                                  Apply-button.
	 */
	public DependencySocietyType(ISocietyTypeDependency dependency)
	{
		this.dependency = dependency;

		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

        this.add(createAttributePanel(), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		textFieldTypeName = new JTextField(dependency.getName(), 20);
		textFieldTypeName.setMinimumSize(new Dimension(150, (int)textFieldTypeName.getPreferredSize().getHeight()));
		textFieldTypeName.setBackground(Color.WHITE);

		spinnerQuantity = ComponentFactory.createQuantitySpinner(dependency.getQuantityAsInt());

		checkBoxActive = new JCheckBox("is active ?");
		checkBoxActive.setBackground(Color.WHITE);
		checkBoxActive.setSelected(dependency.isActive());

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		attributePanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypeName, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Quantity:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(spinnerQuantity, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(checkBoxActive, new GridBagConstraints(2, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	/**
	 * Get the result of the dependency-editing.
	 * @return  The result of the editing.
	 */
	public IDependency getDependency()
	{
		dependency.setActive(checkBoxActive.isSelected());
		dependency.setName(textFieldTypeName.getText());
		dependency.setQuantity(((Number)spinnerQuantity.getValue()).intValue()+"");
		return dependency;
	}

	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent evt)
	{

	}

}
