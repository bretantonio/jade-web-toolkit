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
import java.io.*;
import jade.tools.ascml.exceptions.ASCMLException;

public class ViewException extends JPanel
{
		
	public ViewException(ASCMLException exc)
	{
		super();
		/*this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());

		String exceptionMsg = "";
		if (exc.getNestedException() != null)
		{
			Exception f = exc.getNestedException();

			// add the stacktrace of the nested-exception to the message-string
			StringWriter sw = new StringWriter();
			f.printStackTrace(new PrintWriter(sw));
			exceptionMsg += sw.toString() + "<hr>";
		}

		StringWriter sw = new StringWriter();
		exc.printStackTrace(new PrintWriter(sw));
		exceptionMsg += sw.toString();

		JTextArea stackTrace = new JTextArea(exceptionMsg, 20,60);
		this.add(new JLabel("<html> <table width=100%><tr><td><font color=#000055<i>" + exc.getMessage() + "</i></font></td></tr></table><hr width=75%></html>"), BorderLayout.NORTH);
		this.add(stackTrace, BorderLayout.CENTER);
		*/
	}
}
