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

import jade.tools.ascml.repository.PropertyManager;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.ToolTakeDownEvent;
import jade.tools.ascml.events.ToolTakeDownListener;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Vector;

/**
 *  Presents a dialog especially for choosing Societies and AgentTypes.
 *  This dialog-class is instantiated by the GUIEventHandler once the user
 *  chooses 'Add Society' or 'Add AgentType' from the MenuBar or the JTree-contextmenu.
 *  Furthermore this class is instantiated by the ModelNotFoundDialog to let the
 *  user manually choose an appropiate model-file. 
 */
public class PropertyFileChooseDialog extends AbstractDialog
{
	private static String MESSAGE = "<html>The ASCML wasn't able to find it's property-file<br> You have the following options ...<ul><li>Choose an existing property-file from disc</li><li>Create a new default property-file in the current working-directory</li><li>Cancel the startup and quit the ASCML</li></ul></html>";

	private JFrame parentFrame;

	public PropertyFileChooseDialog(Repository repository)
	{
		super(repository);
	}

	public Object showDialog(JFrame parentFrame)
	{
		this.parentFrame = parentFrame;
		Object[] options = { "Choose Property-file", "Create Property-file", "Cancel & Quit" };
		int result = JOptionPane.showOptionDialog(parentFrame, MESSAGE, "Choose/Create a Property-File",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, null);

		if (result == 0) // choose
		{
            String newPropertyLocation = showChooseDialog();
			repository.getProperties().setSource(newPropertyLocation);
			repository.getProperties().readProperties();
		}
		else if (result == 1) // create
		{
			String newPropertyLocation = System.getProperty("user.dir") + File.separatorChar + PropertyManager.DEFAULT_PROPERTY_FILE;
			repository.getProperties().createNewPropertyFile(newPropertyLocation);
			repository.getProperties().readProperties();
		}
		else if (result == 2) // quit
		{
			repository.throwToolTakeDownEvent();
		}

		return null;
	}


	private String showChooseDialog()
	{
		JFileChooser modelchooser = new JFileChooser(System.getProperty("user.dir"));
		modelchooser.setAcceptAllFileFilterUsed(true);
		FileFilter filter = new FileFilter()
		{
			public String getDescription()
			{
				return "ASCML Property-File ('"+PropertyManager.DEFAULT_PROPERTY_FILE+" | *.xml')";
			}

			public boolean accept(File f)
			{
				String name = f.getName();
				//return f.isDirectory() || name.endsWith(".properties") || name.endsWith(".xml") ||
				//		(name.endsWith(".class") && name.indexOf("Model")!=-1);
				return f.isDirectory() || name.endsWith(".xml");
			}
		};
		modelchooser.addChoosableFileFilter(filter);
		modelchooser.setFileFilter(filter);
		modelchooser.setDialogType(JFileChooser.OPEN_DIALOG);
		
		String resultFileName = null;
		
		if(modelchooser.showDialog(parentFrame, "Select Property-File")==JFileChooser.APPROVE_OPTION)
		{
			File file = modelchooser.getSelectedFile();
			resultFileName = file.toString();
		}

		return resultFileName;		
	}
}
	

