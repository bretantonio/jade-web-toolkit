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
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.model.jibx.ParameterSet;
import jade.tools.ascml.model.jibx.Parameter;
import jade.tools.ascml.events.ParameterChangedListener;
import jade.tools.ascml.events.ParameterChangedEvent;

public class ParameterDetails extends JPanel implements ActionListener
{
	private JButton buttonApply;
    private JButton buttonAddValue;
	private JButton buttonRemoveValue;

	private JTextField textFieldName;
	private JTextField textFieldValue;

	private JComboBox comboBoxType;

	private JTextArea textAreaDescription;

	private JCheckBox checkBoxOptional;

	private JList listValues;

	private IParameter parameter;
	private IParameterSet parameterSet;

	private ParameterChangedListener parameterChangedListener;

	/**
	 *
	 * @param parameterChangedListener  Listener to be informed, when the user presses the
	 *                                  Apply-button.
	 */
	public ParameterDetails(ParameterChangedListener parameterChangedListener)
	{
		this.parameterChangedListener = parameterChangedListener;

		this.setLayout(new GridBagLayout());
		this.setBackground(Color.WHITE);

		buttonApply = ComponentFactory.createApplyButton("Apply Changes");
		buttonApply.addActionListener(this);

        this.add(createAttributePanel(), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		this.add(buttonApply, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	/**
	 * Set the instance-parameter to edit.
	 * If this method is called, only the name and value-fields are editable and filled with the instance-parameter-values.
	 * All other fields are filled with typeParameter-values.
	 * @param instanceParameter  The instance-parameter to edit.
	 * @param typeParameter  The type-parameter belonging to the instance-parameter
	 */
	public void setInstanceParameter(IParameter instanceParameter, IParameter typeParameter)
	{
        textFieldName.setText(instanceParameter.getName());
		comboBoxType.setEnabled(false);

		if (typeParameter != null)
			textAreaDescription.setText(typeParameter.getDescription());
		else
			textAreaDescription.setText("This parameter does not overwrite an AgentType-parameter");
		textAreaDescription.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textAreaDescription.setEnabled(false);

		if (typeParameter != null)
			checkBoxOptional.setSelected(typeParameter.isOptional());
		else
			checkBoxOptional.setSelected(true);
		checkBoxOptional.setEnabled(false);

		DefaultListModel listModel = (DefaultListModel)listValues.getModel();
		listModel.removeAllElements();
		if (!instanceParameter.getValue().equals(""))
		{
			listModel.addElement(instanceParameter.getValue());
			listValues.setSelectedIndex(0);
		}
	}

	/**
	 * Set the instance-parameter to edit.
	 * If this method is called, only the name and value-fields are editable and filled with the instance-parameter-values.
	 * All other fields are filled with typeParameter-values.
	 * @param instanceParameter  The instance-parameter to edit.
	 * @param typeParameter  The type-parameter belonging to the instance-parameter
	 */
	public void setInstanceParameterSet(IParameterSet instanceParameter, IParameterSet typeParameter)
	{
        textFieldName.setText(instanceParameter.getName());
		comboBoxType.setEnabled(false);

		if (typeParameter != null)
			textAreaDescription.setText(typeParameter.getDescription());
		else
			textAreaDescription.setText("This parameter does not overwrite an AgentType-parameter");
		textAreaDescription.setEnabled(false);
		textAreaDescription.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		if (typeParameter != null)
			checkBoxOptional.setSelected(typeParameter.isOptional());
		else
			checkBoxOptional.setSelected(true);
		checkBoxOptional.setEnabled(false);

		DefaultListModel listModel = (DefaultListModel)listValues.getModel();
		listModel.removeAllElements();
		String[] values = instanceParameter.getValues();
		for (int i=0; i < values.length; i++)
		{
			listModel.addElement(values[i]);
		}
		listValues.setSelectedIndex(0);
	}

	/**
	 * Set the type-parameter to edit.
	 * @param typeParameter  The type-parameter to edit.
	 */
	public void setTypeParameter(IParameter typeParameter)
	{
		textFieldName.setText(typeParameter.getName());
		textAreaDescription.setText(typeParameter.getDescription());
		checkBoxOptional.setSelected(typeParameter.isOptional());
		DefaultListModel listModel = (DefaultListModel)listValues.getModel();
		listModel.removeAllElements();
		if (!typeParameter.getValue().equals(""))
		{
			listModel.addElement(typeParameter.getValue());
			listValues.setSelectedIndex(0);
		}
	}

	/**
	 * Set the type-parameterSet to edit.
	 * @param typeParameterSet  The type-parameterSet to edit.
	 */
	public void setTypeParameterSet(IParameterSet typeParameterSet)
	{
        textFieldName.setText(typeParameterSet.getName());
		textAreaDescription.setText(typeParameterSet.getDescription());
		checkBoxOptional.setSelected(typeParameterSet.isOptional());

		DefaultListModel listModel = (DefaultListModel)listValues.getModel();
		listModel.removeAllElements();
		String[] values = typeParameterSet.getValues();
		for (int i=0; i < values.length; i++)
		{
			listModel.addElement(values[i]);
		}
		listValues.setSelectedIndex(0);
	}

	/**
	 * Get the result of the parameter-editing.
	 * In case the user pressed the apply-button a NEW instance of either a parameter
	 * or a parameterSet (depending on the size of the value-list) is returned.
	 * If the user did not make any changes, the same instance of the parameter or parameterSet
	 * is returned.
	 * @return  The result of the editing (either a Parameter or a ParameterSet).
	 */
	public Object getResult()
	{
		if (parameter != null)
			return parameter;
		else
			return parameterSet;
	}

	private JPanel createAttributePanel()
	{
		buttonAddValue = ComponentFactory.createAddButton("Add Value");
		buttonAddValue.addActionListener(this);

		buttonRemoveValue = ComponentFactory.createRemoveButton("Remove");
		buttonRemoveValue.addActionListener(this);

		textFieldName = new JTextField("", 30);
		textFieldName.setMinimumSize(new Dimension(320, (int)textFieldName.getPreferredSize().getHeight()));
		textFieldName.setBackground(Color.WHITE);

		textFieldValue = new JTextField("", 30);
		textFieldValue.setMinimumSize(new Dimension(320, (int)textFieldName.getPreferredSize().getHeight()));
		textFieldValue.setBackground(Color.WHITE);

		comboBoxType = new JComboBox(new String[] { "String" });
		comboBoxType.setBackground(Color.WHITE);

		// prepare Description
		textAreaDescription = new JTextArea("", 3, 20);
		JScrollPane textDescScrollPane = ComponentFactory.createTextAreaScrollPane(textAreaDescription);

		checkBoxOptional = new JCheckBox("optional");
		// checkBoxOptional.setSelected(agentInstance.hasToolOption(IToolOption.TOOLOPTION_SNIFF));
		checkBoxOptional.setBackground(Color.WHITE);

		listValues = new JList(new DefaultListModel());
		JScrollPane listScrollPane = ComponentFactory.createListScrollPane(listValues);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		// prepare Main-Panel
		attributePanel.add(new JLabel("Parameter-Name:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldName, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Parameter-Type:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(comboBoxType, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Necessity:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(checkBoxOptional, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Description:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textDescScrollPane, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Values:"), new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(listScrollPane, new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));

		attributePanel.add(buttonRemoveValue, new GridBagConstraints(1, 5, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(1,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Add new Value:"), new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,1,5), 0, 0));
		attributePanel.add(textFieldValue, new GridBagConstraints(1, 6, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,1,5), 0, 0));

		attributePanel.add(buttonAddValue, new GridBagConstraints(1, 7, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(1,5,5,5), 0, 0));

		attributePanel.add(buttonApply, new GridBagConstraints(1, 8, 0, 1, 0, 1, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	public void applyChanges()
	{
		if (listValues.getModel().getSize() > 1)
		{
			parameter = null; // set parameter to null, because we now deal with a new parameter-SET

			parameterSet = new ParameterSet();
			parameterSet.setName(textFieldName.getText());
			parameterSet.setDescription(textAreaDescription.getText());
			parameterSet.setOptional(checkBoxOptional.isSelected()+"");
			parameterSet.setType((String)comboBoxType.getSelectedItem());

			String[] values = new String[listValues.getModel().getSize()];

			for (int i=0; i < values.length; i++)
			{
				parameterSet.addValue((String)listValues.getModel().getElementAt(i));
			}

			if (parameterChangedListener != null)
				parameterChangedListener.parameterChanged(new ParameterChangedEvent(parameterSet));
		}
		else
		{
			parameterSet = null;

			parameter = new Parameter();
			parameter.setName(textFieldName.getText());
			parameter.setDescription(textAreaDescription.getText());
			parameter.setOptional(checkBoxOptional.isSelected()+"");
			parameter.setType((String)comboBoxType.getSelectedItem());
			if (listValues.getModel().getSize() > 0)
				parameter.setValue((String)listValues.getModel().getElementAt(0));

			if (parameterChangedListener != null)
				parameterChangedListener.parameterChanged(new ParameterChangedEvent(parameter));
		}
	}

	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonApply)
		{
            applyChanges();
		}
		else if (evt.getSource() == buttonAddValue)
		{
			if (!textFieldValue.getText().trim().equals(""))
			{
				((DefaultListModel)listValues.getModel()).addElement(textFieldValue.getText());
				listValues.setSelectedIndex(listValues.getModel().getSize()-1);
			}
		}
		else if (evt.getSource() == buttonRemoveValue)
		{
			int[] indices = listValues.getSelectedIndices();
			for (int i=0; i < indices.length; i++)
			{
				((DefaultListModel)listValues.getModel()).removeElementAt(indices[i]);
				listValues.setSelectedIndex(0);
			}
		}
	}

}
