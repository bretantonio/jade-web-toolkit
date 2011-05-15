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
import java.io.*;
import jade.tools.ascml.exceptions.ASCMLException;
import jade.tools.ascml.exceptions.ASCMLException;
import jade.tools.ascml.repository.Project;
import jade.tools.ascml.gui.components.exception.ExceptionPanel;

public class ExceptionDialog extends AbstractDialog
{
    private ASCMLException exception;

	/**
	 * This public static variable may be used to prevent the ExceptionDialog to be shown.
	 * Setting it to false prevents the dialog.
	 */
    public static boolean isVisible = true;


	public ExceptionDialog(ASCMLException exception)
	{
		super();
        this.exception = exception;
	}

	public Object showDialog(JFrame parentFrame)
	{
		// JTextArea textArea = new JTextArea(exceptionMessage, 20, 60);
		// JScrollPane scrollPane = new JScrollPane(textArea);

		ExceptionPanel exceptionPanel = new ExceptionPanel(exception);
		JDialog dialog = new JDialog(parentFrame, "Exception Details ...", true);
		dialog.getContentPane().add(exceptionPanel);
		dialog.setSize(680, 440);
		dialog.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-dialog.getWidth()/2),
 						   (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-dialog.getHeight()/2));
		dialog.setVisible(isVisible);
        dialog.toFront();
		return null;
	}
}
