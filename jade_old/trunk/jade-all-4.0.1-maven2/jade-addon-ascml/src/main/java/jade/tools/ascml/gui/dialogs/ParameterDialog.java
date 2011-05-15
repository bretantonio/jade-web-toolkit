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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import jade.tools.ascml.gui.panels.ParameterDetails;
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.absmodel.IParameter;
import jade.tools.ascml.absmodel.IParameterSet;

public class ParameterDialog extends AbstractDialog implements ActionListener
{
	private JDialog dialog;
    private Object instanceParameter;
	private Object typeParameter;

	public ParameterDialog(Object instanceParameter, Object typeParameter)
	{
		super();
        this.instanceParameter = instanceParameter;
		this.typeParameter = typeParameter;
	}

	public Object showDialog(JFrame parentFrame)
	{
		ParameterDetails parameterPanel = new ParameterDetails(null);

		if ((instanceParameter == null) && (typeParameter instanceof IParameter))
			parameterPanel.setTypeParameter((IParameter)typeParameter);
		else if ((instanceParameter == null) && (typeParameter instanceof IParameterSet))
			parameterPanel.setTypeParameterSet((IParameterSet)typeParameter);
		else if (instanceParameter instanceof IParameter)
			parameterPanel.setInstanceParameter((IParameter)instanceParameter, (IParameter)typeParameter);
		else if (instanceParameter instanceof IParameterSet)
			parameterPanel.setInstanceParameterSet((IParameterSet)instanceParameter, (IParameterSet)typeParameter);

		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);
		JButton closeButton = ComponentFactory.createCloseButton("Close Dialog");
		closeButton.addActionListener(this);
		mainPanel.add(parameterPanel, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		mainPanel.add(closeButton, new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,6,6), 0, 0));

		dialog = new JDialog(parentFrame, "Edit Parameter ...", true);
		dialog.getContentPane().add(mainPanel);
		dialog.setSize(350, 420);
		dialog.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-dialog.getWidth()/2),
 						   (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-dialog.getHeight()/2));
		dialog.setVisible(true);
        dialog.toFront();

		// returns a NEW instance of either Parameter or ParameterSet
		return parameterPanel.getResult();
	}

	public void actionPerformed(ActionEvent evt)
	{
		dialog.setVisible(false);
	}
}
