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
import java.io.File;

import jade.tools.ascml.gui.dialogs.SimpleFileFilter;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.events.PropertyChangedEvent;

public class RepositoryOptions extends AbstractPanel
                               implements ActionListener
{
	private JTextField textProjectName;
	private JTextField textWorkingDirectory;
	private JList modelLocationList;
	private JList excludeList;

	public RepositoryOptions(AbstractMainPanel mainPanel)
	{
		super(mainPanel);
		
		this.setBackground(Color.WHITE);

		textProjectName = new JTextField("", 20);
		textWorkingDirectory = new JTextField("", 20);

		JButton buttonWorkingPath = new JButton("...");
		buttonWorkingPath.addActionListener(this);
		buttonWorkingPath.setActionCommand("choose_workingpath");
		buttonWorkingPath.setMargin(new Insets(1,3,1,3));
		buttonWorkingPath.setPreferredSize(new Dimension(30,20));
		buttonWorkingPath.setMaximumSize(new Dimension(30,20));
						
		GridBagConstraints con = new GridBagConstraints();
		con.anchor = GridBagConstraints.LINE_START;
		con.insets = new Insets(5, 5, 5, 5);
		con.fill = GridBagConstraints.NONE;

		this.setLayout(new GridBagLayout());
		
		con.gridx	= 0;
		con.gridy	= 0;
		this.add(new JLabel("Project-Name:"), con);
		con.fill	= GridBagConstraints.HORIZONTAL; // input should get all available space
		con.weightx	= 1.0; // input should get all available space
		con.gridx	= 1;
		this.add(textProjectName, con);

// end of first row

		con.gridx	= 0;
		con.gridy	= 1;
		con.weightx	= 0.0;
		con.fill = GridBagConstraints.NONE;
		this.add(new JLabel("Working-Directory:"), con);
		con.gridx	= 1;
		con.weightx	= 1.0;
		con.fill = GridBagConstraints.HORIZONTAL;
		this.add(textWorkingDirectory, con);
		con.gridx	= 2;
		con.weightx	= 0.0;
		con.fill = GridBagConstraints.NONE;
		this.add(buttonWorkingPath, con);

// end of second row
		
		con.gridx = 0;
		con.gridy = 2;
		con.gridwidth = 3;
		
		con.weightx = 1.0; // input should get all available space
		con.weighty = 0.5;
		con.fill = GridBagConstraints.BOTH; // input should get all available space
		this.add(createClasspathPanel(), con);

// end of third row

		con.gridy = 3;
		this.add(createExcludeJarPanel(), con);

// end of fourth row

		// set initial values
		setListData();

		textProjectName.setText(getProject().getName());
		textWorkingDirectory.setText(getProject().getWorkingDirectory());
	}
	
	private JPanel createClasspathPanel()
	{
		modelLocationList = new JList();
		modelLocationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		modelLocationList.setLayoutOrientation(JList.VERTICAL);
		// modelLocationList.setVisibleRowCount(10);
		modelLocationList.setToolTipText("These directories/archives will be recursivly scanned for description-files.");

		JScrollPane classpathListScroller = new JScrollPane(modelLocationList);
		classpathListScroller.setMaximumSize(new Dimension(100, 80));
		
		JButton removeClasspathButton = new JButton("Remove");
		removeClasspathButton.setActionCommand("removeclasspath");
		removeClasspathButton.addActionListener(this);
		JButton addClasspathButton = new JButton("Add");
		addClasspathButton.setActionCommand("addclasspath");
		addClasspathButton.addActionListener(this);
				
		JPanel classpathBottomPanel = new JPanel();
		classpathBottomPanel.setBackground(Color.WHITE);
		classpathBottomPanel.add(removeClasspathButton);
		classpathBottomPanel.add(addClasspathButton);
		
		JPanel classpathPanel = new JPanel(new BorderLayout());
		classpathPanel.setBackground(Color.WHITE);
		classpathPanel.setBorder(BorderFactory.createTitledBorder("   Directories/Archives containing agent-/society-description files   "));
		classpathPanel.add(classpathListScroller, BorderLayout.CENTER);
		classpathPanel.add(classpathBottomPanel, BorderLayout.SOUTH);
		
		return classpathPanel;
	}
	
	private JPanel createExcludeJarPanel()
	{
		excludeList = new JList();
		excludeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		excludeList.setLayoutOrientation(JList.VERTICAL);
		// excludeList.setVisibleRowCount(10);
		excludeList.setToolTipText("Adding directories/archives to the exclude-list may speed up refreshing the repository-tree");
		JScrollPane excludeJarListScroller = new JScrollPane(excludeList);
		excludeJarListScroller.setMaximumSize(new Dimension(100, 80));
		
		JButton removeexcludeJarButton = new JButton("Remove");
		removeexcludeJarButton.setActionCommand("removeexcludejar");
		removeexcludeJarButton.addActionListener(this);
		JButton addexcludeJarButton = new JButton("Add");
		addexcludeJarButton.setActionCommand("addexcludejar");
		addexcludeJarButton.addActionListener(this);
				
		JPanel excludeJarBottomPanel = new JPanel();
		excludeJarBottomPanel.setBackground(Color.WHITE);
		excludeJarBottomPanel.add(removeexcludeJarButton);
		excludeJarBottomPanel.add(addexcludeJarButton);
		
		JPanel excludeJarPanel = new JPanel(new BorderLayout());
		excludeJarPanel.setBackground(Color.WHITE);
		excludeJarPanel.setBorder(BorderFactory.createTitledBorder("   Directories/Archives to exclude in order to speed up searching   "));
		excludeJarPanel.add(excludeJarListScroller, BorderLayout.CENTER);
		excludeJarPanel.add(excludeJarBottomPanel, BorderLayout.SOUTH);
		
		return excludeJarPanel;
	}

	private void setListData()
	{
		String[] modelLocations = getProperties().getModelLocations();
		DefaultListModel listModel = new DefaultListModel();
		for (int i=0; i < modelLocations.length; i++)
		{
			listModel.addElement(modelLocations[i]);
		}
		modelLocationList.setModel(listModel);

		String[] modelExcludes = getProperties().getExcludeSet();
		DefaultListModel excludeListModel = new DefaultListModel();
		for (int i=0; i < modelExcludes.length; i++)
		{
			excludeListModel.addElement(modelExcludes[i]);
		}
		excludeList.setModel(excludeListModel);
	}

	/**
	 * This method is called when attributes of the active project have been changed.
	 * @param event  The change-Event.
	 */
	public void projectChanged(ProjectChangedEvent event)
	{
		textProjectName.setText(getProject().getName());
		textWorkingDirectory.setText(getProject().getWorkingDirectory());
		setListData();
	}

	/**
	 * This method is called when a new project is set to be the active project
	 * @param event  The change-Event.
	 */
	public void propertiesChanged(PropertyChangedEvent event)
	{
		if (event.getEventCode().equals(PropertyChangedEvent.ACTIVE_PROJECT_CHANGED))
		{
			textProjectName.setText(event.getProperties().getActiveProject().getName());
			textWorkingDirectory.setText(event.getProperties().getActiveProject().getWorkingDirectory());
			setListData();
		}
	}

	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent e)
	{
		String action = e.getActionCommand();

		// todo: List-models werden vielleicht doppelt gesetzt, einmal hier und einmal wenn propertiesChanged aufgerufen wird
		if (action.equals("removeclasspath"))
		{
			DefaultListModel listModel = (DefaultListModel)modelLocationList.getModel();
			String itemToRemove = (String)modelLocationList.getSelectedValue();
			listModel.removeElement(itemToRemove);
			String[] locationElements = new String[listModel.size()];
			listModel.copyInto(locationElements);
			getProperties().setModelLocations(locationElements);
		}
		else if (action.equals("addclasspath"))
		{
			final JFileChooser fc = new JFileChooser(getProject().getWorkingDirectory());
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setMultiSelectionEnabled(true);
			fc.setAcceptAllFileFilterUsed(false);
			
			String[] zip = new String[] {"zip"};
			SimpleFileFilter zipFileFilter = new SimpleFileFilter(zip,"ZIP Files (*.zip)");
			fc.addChoosableFileFilter(zipFileFilter);
			
			String[] jar = new String[] {"jar"};
			SimpleFileFilter jarFileFilter = new SimpleFileFilter(jar,"JAR Files (*.jar)");
			fc.addChoosableFileFilter(jarFileFilter);

			String[] archive = new String[] {"jar","zip"};
			SimpleFileFilter archiveZipFileFilter = new SimpleFileFilter(archive,"JAR or ZIP Files (*.jar, *.zip)");
			fc.addChoosableFileFilter(archiveZipFileFilter);

			int returnVal = fc.showDialog(this, "Add to Model-Path");
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				DefaultListModel listModel = (DefaultListModel)modelLocationList.getModel();
				File[] itemsToAdd = fc.getSelectedFiles();
				
				for (int i=0; i < itemsToAdd.length; i++)
				{
					listModel.addElement(itemsToAdd[i].getAbsolutePath());
					modelLocationList.setSelectedIndex(listModel.size());
				}
				
				String[] locationElements = new String[listModel.size()];
				listModel.copyInto(locationElements);
				getProperties().setModelLocations(locationElements);
			}
		}
		else if (action.equals("removeexcludejar"))
		{
			DefaultListModel listModel = (DefaultListModel)excludeList.getModel();
			String itemToRemove = (String)excludeList.getSelectedValue();
			
			listModel.removeElement(itemToRemove);
			String[] excludeArray = new String[listModel.size()];
			listModel.copyInto(excludeArray);
			getProperties().setExcludeSet(excludeArray);
		}
		else if (action.equals("addexcludejar"))
		{
			final JFileChooser fc = new JFileChooser(getProject().getWorkingDirectory());
			fc.setMultiSelectionEnabled(true);
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setAcceptAllFileFilterUsed(false);
			
			String[] zip = new String[] {"zip"};
			SimpleFileFilter zipFileFilter = new SimpleFileFilter(zip,"ZIP Files (*.zip)");
			fc.addChoosableFileFilter(zipFileFilter);
			
			String[] jar = new String[] {"jar"};
			SimpleFileFilter jarFileFilter = new SimpleFileFilter(jar,"JAR Files (*.jar)");
			fc.addChoosableFileFilter(jarFileFilter);

			String[] archive = new String[] {"jar","zip"};
			SimpleFileFilter archiveZipFileFilter = new SimpleFileFilter(archive,"JAR or ZIP Files (*.jar, *.zip)");
			fc.addChoosableFileFilter(archiveZipFileFilter);

			int returnVal = fc.showDialog(this, "Exclude directory/archive");
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				DefaultListModel listModel = (DefaultListModel)excludeList.getModel();
				File[] itemsToAdd = fc.getSelectedFiles();
				
				for (int i=0; i < itemsToAdd.length; i++)
				{
					listModel.addElement(itemsToAdd[i].getAbsolutePath());
					excludeList.setSelectedIndex(listModel.size());
				}
				String[] excludeArray = new String[listModel.size()];
				listModel.copyInto(excludeArray);
				getProperties().setExcludeSet(excludeArray);
			}
		}
		else if (action.equals("choose_workingpath"))
		{
			JFileChooser fc = new JFileChooser(getProject().getWorkingDirectory());
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fc.showDialog(this, "Choose Working-Directory") == JFileChooser.APPROVE_OPTION)
			{
				try
				{
					String newDir = fc.getSelectedFile().getCanonicalPath();
					getProject().setWorkingDirectory(newDir);
				}
				catch(Exception exc)
				{
					exc.printStackTrace();
				}
			}
		}
	}

	/**
	 * This method is called, when the repository-tab is closed.
	 * When it is closed the project-name and working-directory
	 * in the project-object are reset because the user might have changed these.
	 */
	public void exit()
	{
		super.exit();
		if (!getProject().getName().equals(textProjectName.getText()))
		{
			getProject().setName(textProjectName.getText());
		}
		if (!getProject().getWorkingDirectory().equals(textWorkingDirectory.getText()))
		{
			getProject().setWorkingDirectory(textWorkingDirectory.getText());
		}
	}
}
