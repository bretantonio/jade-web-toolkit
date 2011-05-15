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


package jade.tools.ascml.gui.components.exception;

import jade.tools.ascml.exceptions.ASCMLException;
import jade.tools.ascml.exceptions.ASCMLExceptionDetails;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * 
 */
public class ExceptionPanel extends JPanel implements TreeSelectionListener
{

	private ExceptionTree tree;
	private JLabel typeLabel;
	private JLabel errorCodeLabel;
	private JTextArea detailsTextArea;
	private JTextArea stackTraceTextArea;

	public ExceptionPanel(ASCMLException exception)
	{
		tree 				= new ExceptionTree(exception, this);
		typeLabel			= new JLabel();
		errorCodeLabel		= new JLabel();

		detailsTextArea	= new JTextArea(exception.getExceptionDetails().toString());
		detailsTextArea.setEditable(false);
		detailsTextArea.setLineWrap(true);
		detailsTextArea.setWrapStyleWord(true);
		detailsTextArea.setBackground(Color.WHITE);
		detailsTextArea.setFont(new Font("Arial", Font.PLAIN, 12));

		stackTraceTextArea	= new JTextArea(exception.getStackTraceString());
		stackTraceTextArea.setEditable(false);
        stackTraceTextArea.setLineWrap(true);
		stackTraceTextArea.setWrapStyleWord(true);
		stackTraceTextArea.setBackground(Color.WHITE);
		stackTraceTextArea.setFont(new Font("Arial", Font.PLAIN, 12));

		createMainPanel();

		tree.addTreeSelectionListener(this);
		for (int i=0; i < tree.getRowCount(); i++)
		{
			tree.expandRow(i);
		}
		tree.setSelectionRow(1);
	}

	private void createMainPanel()
	{
		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		this.add(typeLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(errorCodeLabel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), new GridBagConstraints(0, 1, 2, 1, 1, 0.5, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(new JScrollPane(detailsTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), new GridBagConstraints(0, 2, 2, 1, 1, 0.2, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
		this.add(new JScrollPane(stackTraceTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), new GridBagConstraints(0, 3, 2, 1, 1, 0.3, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
	}

	public void valueChanged(TreeSelectionEvent newSelection)
	{
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (node==null) return;

		if (node.getUserObject() instanceof ASCMLException)
		{
			ASCMLException exception = (ASCMLException)node.getUserObject();
			typeLabel.setText(exception.getClass().getName());
			errorCodeLabel.setText("ErrorCode: " + exception.getErrorCode());
			detailsTextArea.setText(exception.getLongMessage());
			detailsTextArea.setCaretPosition(0);
			stackTraceTextArea.setText(exception.getStackTraceString());
			stackTraceTextArea.setCaretPosition(0);
		}
		else if (node.getUserObject() instanceof ASCMLExceptionDetails)
		{
			ASCMLExceptionDetails details = (ASCMLExceptionDetails)node.getUserObject();
			detailsTextArea.setText(details.getLongMessage());
			detailsTextArea.setCaretPosition(0);
			stackTraceTextArea.setText(details.getStackTraceString());
			stackTraceTextArea.setCaretPosition(0);
		}
		else if (node.getUserObject() instanceof Exception) // general ExceptionHandling
		{
			StringWriter sw = new StringWriter();
			((Exception)node.getUserObject()).printStackTrace(new PrintWriter(sw));
			detailsTextArea.setText(ASCMLException.NO_DETAILS);
			detailsTextArea.setCaretPosition(0);
			stackTraceTextArea.setText(sw.toString());
			stackTraceTextArea.setCaretPosition(0);
		}
	}
}
