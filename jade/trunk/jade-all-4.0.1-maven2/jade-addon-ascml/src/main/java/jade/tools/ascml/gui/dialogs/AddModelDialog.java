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

import jade.tools.ascml.repository.Project;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.exceptions.ModelException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 *  Presents a dialog especially for choosing Societies and AgentTypes.
 *  This dialog-class is instantiated by the GUIEventHandler once the user
 *  chooses 'Add Society' or 'Add AgentType' from the MenuBar or the JTree-contextmenu.
 *  Furthermore this class is instantiated by the ModelNotFoundDialog to let the
 *  user manually choose an appropiate model-file. 
 */
public class AddModelDialog extends AbstractDialog
{
	public final static String AGENTTYPE_FILE_EXTENSION		= ".agent.xml";
	public final static String SOCIETYTYPE_FILE_EXTENSION	= ".society.xml";

	private String fileExtension;
    private boolean isAgentDialog;

	private JFileChooser modelFileChooser;

	/**
	 *
	 * @param repository  The Repository, needed to access the active project,
	 * cause Agents/Societies can only be added to the active project.
	 * @param isAgentDialog  true, if this Add-dialog should ask for adding agents, false, if it
	 * should ask for societies
	 */
	public AddModelDialog(Repository repository, boolean isAgentDialog)
	{
		super(repository);
		this.isAgentDialog = isAgentDialog;

		if (isAgentDialog)
			this.fileExtension = AGENTTYPE_FILE_EXTENSION;
		else
			this.fileExtension = SOCIETYTYPE_FILE_EXTENSION;

		// create and initialize the fileChooser, but do not show it yet...
		modelFileChooser = new JFileChooser(repository.getProject().getWorkingDirectory());
		modelFileChooser.setAcceptAllFileFilterUsed(true);
		FileFilter filter = new FileFilter()
		{
			public String getDescription()
			{
				return "Agent-/Society-Description Files";
			}

			public boolean accept(File f)
			{
				String name = f.getName();
				//return f.isDirectory() || name.endsWith(".properties") || name.endsWith(".xml") ||
				//		(name.endsWith(".class") && name.indexOf("Model")!=-1);
				return f.isDirectory() ||  name.endsWith(fileExtension) ||  name.endsWith(".jar");
			}
		};
		modelFileChooser.addChoosableFileFilter(filter);
		modelFileChooser.setFileFilter(filter);
		modelFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
	}

	/**
	 *
	 * @param parentFrame
	 * @return  The model-object of the model to add
	 */
	public Object showDialog(JFrame parentFrame)
	{
		Project project = repository.getProject();
		// show the dialog
		if(modelFileChooser.showDialog(parentFrame, "Select Model ...") == JFileChooser.APPROVE_OPTION)
		{
			File file = modelFileChooser.getSelectedFile();
			String fileName = extractFileName(file, parentFrame);

			if (fileName != null)
			{
				// store the newly selected path in the project
				project.setWorkingDirectory(fileName);
				
				// add the new model to the project
                if (isAgentDialog)
				{
					try
					{
						return project.addAgentType(fileName);
					}
					catch(ModelException me)
					{
						repository.throwExceptionEvent(me);
						return null;
					}
				}
				else
				{
					try
					{
						return project.addSocietyType(fileName);
					}
					catch(ModelException me)
					{
						repository.throwExceptionEvent(me);
						return null;
					}
				}
			}
		}
		return null;
	}

	private String extractFileName(File file, JFrame parentFrame)
	{
		String fileName = null;
		String jarFileName = null;

		String model = (file != null) ? "" + file : null;

		// is this a jar-file ?
		if( (file != null) && file.getName().endsWith(".jar") )
		{
			// Start looking into the jar-file for description-files
			try
			{
				JarFile jarFile = new JarFile(file);
				Enumeration e = jarFile.entries();
				List models	= new ArrayList();

				// test every file found in the jar-archive, if it's ending
				// corresponds to AGENTTYPE_FILE_EXTENSION or SOCIETYTYPE_FILE_EXTENSION
				while (e.hasMoreElements())
				{
					ZipEntry jarFileEntry = (ZipEntry) e.nextElement();
					if(jarFileEntry.getName().endsWith(AGENTTYPE_FILE_EXTENSION) ||
					   jarFileEntry.getName().endsWith(SOCIETYTYPE_FILE_EXTENSION))
					{
						models.add(jarFileEntry.getName());
					}
				}
				jarFile.close();

				// if more than one model-description-file has been found in the archive
				// a special TreeDialog for browsing the contents of the archive is presented
				// if exactly one file is found, this file is automatically chosen
				if(models.size() > 1)
				{
					Object[] choices = models.toArray(new String[models.size()]);
					JTreeDialog td = new JTreeDialog(parentFrame, "Select Agent-/Society-Model", true,
						"Select an agent-/society-model to load:", (String[])choices, (String)choices[0]);
					td.setVisible(true);
					model = td.getResult();
				}
				else if(models.size() == 1)
				{
					model = (String)models.get(0);
				}
				else
				{
					// if no model-description-file has been found
					model = null;
				}

				// the jarFile-variable is used later on to construct the path
				jarFileName = file.toString();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		} // end of if

		fileName = model;
		if (jarFileName != null)
			fileName = jarFileName + "::" + fileName;

		return fileName;
	}
}
	
