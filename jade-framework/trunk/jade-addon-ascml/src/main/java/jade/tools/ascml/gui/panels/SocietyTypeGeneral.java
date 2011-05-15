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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

import jade.tools.ascml.gui.dialogs.StartSocietyInstanceDialog;
import jade.tools.ascml.gui.dialogs.ExceptionDialog;
import jade.tools.ascml.gui.components.ComponentFactory;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.repository.ModelIntegrityChecker;
import jade.tools.ascml.repository.Project;
import jade.tools.ascml.absmodel.ISocietyType;
import jade.tools.ascml.absmodel.IDocument;
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.model.jibx.SocietyInstance;
import jade.tools.ascml.events.ProjectChangedEvent;

public class SocietyTypeGeneral extends AbstractPanel implements ActionListener
{
	private JLabel iconLabel;

	private JButton buttonApply;
    private JButton buttonSave;
	private JButton buttonStart;
    private JButton buttonCreate;

	private JButton buttonChangeImage;
	private JButton buttonChangeSourcePath;
	private JButton buttonChangeSourceName;

	private JTextField textFieldSourcePath;
	private JTextField textFieldSourceName;
	private JTextField textFieldTypeName;
	private JTextField textFieldTypePackage;

	private JTextArea textAreaDescription;
	private JComboBox comboBoxDefaultInstance;

    private String iconName;

	private ISocietyType model;

	/**
	 * @param model the SocietyTypeModel to show in the dialog.
	 */
	public SocietyTypeGeneral(AbstractMainPanel mainPanel, ISocietyType model)
	{
		super(mainPanel);
		this.model = model;

		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		buttonStart = ComponentFactory.createStartButton("Start Default-Instance");
		buttonStart.addActionListener(this);

		buttonApply = ComponentFactory.createApplyButton("Apply Changes");
		buttonApply.addActionListener(this);

		buttonSave = ComponentFactory.createSaveButton("Save SocietyType");
		buttonSave.addActionListener(this);

		buttonCreate = ComponentFactory.createNewButton("Create SocietyInstance");
		buttonCreate.addActionListener(this);

		// ... and add the content step by step with special constraint-options
		this.add(createAttributePanel(), new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		this.add(buttonApply, new GridBagConstraints(0, 1, 1, 1, 0.5, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(buttonCreate, new GridBagConstraints(1, 1, 1, 1, 0.5, 0.01, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		this.add(buttonSave, new GridBagConstraints(0, 2, 1, 1, 0.5, 0.01, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		this.add(buttonStart, new GridBagConstraints(1, 2, 1, 1, 0.5, 0.01, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		this.add(new JLabel(""), new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		// first create all the components
		iconLabel = new JLabel(ImageIconLoader.scaleImageIcon(model.getIcon(), 32, 32));
        iconName = model.getIconName();

		String sourcePath = model.getDocument().getSourcePath();
		if (sourcePath == IDocument.SOURCE_UNKNOWN)
			sourcePath = Project.getWorkingDirectory();
		textFieldSourcePath = new JTextField(sourcePath, 30);
		textFieldSourcePath.setMinimumSize(new Dimension(320, (int)textFieldSourcePath.getPreferredSize().getHeight()));
		textFieldSourcePath.setBackground(Color.WHITE);

		if (model.getDocument().getSourceName().equals(IDocument.SOURCE_UNKNOWN))
			textFieldSourceName = new JTextField(model.getDocument().getSourceName() + ".society.xml", 30);
		else
			textFieldSourceName = new JTextField(model.getDocument().getSourceName(), 30);
		textFieldSourceName.setMinimumSize(new Dimension(320, (int)textFieldSourceName.getPreferredSize().getHeight()));
		textFieldSourceName.setBackground(Color.WHITE);

		textFieldTypeName = new JTextField(model.getName(), 30);
		textFieldTypeName.setBackground(Color.WHITE);
		textFieldTypeName.setMinimumSize(new Dimension(320, (int)textFieldTypeName.getPreferredSize().getHeight()));

		textFieldTypePackage = new JTextField(model.getPackageName(), 30);
		textFieldTypePackage.setBackground(Color.WHITE);
		textFieldTypePackage.setMinimumSize(new Dimension(320, (int)textFieldTypePackage.getPreferredSize().getHeight()));

		buttonChangeImage = ComponentFactory.createChangeIconButton();
		buttonChangeImage.addActionListener(this);

		buttonChangeSourcePath = ComponentFactory.createThreePointButton();
		buttonChangeSourcePath.addActionListener(this);

		buttonChangeSourceName = ComponentFactory.createThreePointButton();
		buttonChangeSourceName.addActionListener(this);

		String[] comboxBoxContent = new String[model.getSocietyInstances().length];
		int indexOfDefaultInstance = -1;
		for (int i=0; i < model.getSocietyInstances().length; i++)
		{
			comboxBoxContent[i] = model.getSocietyInstance(i).getName();
			if (model.getDefaultSocietyInstanceName().equals(model.getSocietyInstance(i).getName()))
				indexOfDefaultInstance = i;
		}
        if ((model.getSocietyInstances().length == 0) || (model.getDefaultSocietyInstanceName() == ISocietyInstance.NAME_UNKNOWN))
		{
			comboxBoxContent = new String[] {ISocietyInstance.NAME_UNKNOWN};
			indexOfDefaultInstance = 0;
		}

		comboBoxDefaultInstance = new JComboBox(comboxBoxContent);

		if (indexOfDefaultInstance >= 0)
			comboBoxDefaultInstance.setSelectedIndex(indexOfDefaultInstance);
		comboBoxDefaultInstance.setBackground(Color.WHITE);

		textAreaDescription = new JTextArea(model.getDescription(), 5, 20);
		JScrollPane textDescScrollPane = ComponentFactory.createTextAreaScrollPane(textAreaDescription);

		JPanel attributePanel = new JPanel(new GridBagLayout());
		attributePanel.setBackground(Color.WHITE);

		// prepare Main-Panel
		attributePanel.add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(buttonChangeImage, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(new JLabel("<html><h2>General Settings</h2>Here, you see all the main-settings for this SocietyType.</html>"), new GridBagConstraints(1, 0, 2, 2, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Source-Path:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldSourcePath, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(buttonChangeSourcePath, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Source-File:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldSourceName, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(buttonChangeSourceName, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypeName, new GridBagConstraints(1, 4, 2, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Type-Package:"), new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textFieldTypePackage, new GridBagConstraints(1, 5, 2, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Default-Instance:"), new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(comboBoxDefaultInstance, new GridBagConstraints(1, 6, 2, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		attributePanel.add(new JLabel("Description:"), new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		attributePanel.add(textDescScrollPane, new GridBagConstraints(1, 7, 2, 1, 1, 1, GridBagConstraints.LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));

		return attributePanel;
	}

	private void applyChanges()
	{
		String dialogMessage = "";

		// check if type-name or type-package have changed
		// if the type-name has changed automatically also change the source-name
		if (!model.getName().equals(textFieldTypeName.getText()) ||
			!model.getPackageName().equals(textFieldTypePackage.getText()))
		{
			dialogMessage+="<h3>Changing the name or package of a SocietyType leads to a new source-name or -path!</h3><p><i>The names and locations of description-files are constructed out of the SocietyType-name and -package,<br>changing the name or package also changes the name or path of the source-file.</i><p>&nbsp;";
			textFieldSourceName.setText(textFieldTypeName.getText() + ".society.xml");
			model.setName(textFieldTypeName.getText());
			model.setPackageName(textFieldTypePackage.getText());
		}

		// check if source-name or source-path have changed
		// if the source-name has changed automatically also change the type-name
		if (!model.getDocument().getSourceName().equals(textFieldSourceName.getText()) ||
			!model.getDocument().getSourcePath().equals(textFieldSourcePath.getText()))
		{
			dialogMessage+="<h3>Changing the sourcepath or -file of an SocietyType leads to a new SocietyType-name or -package!</h3><p><i>The SocietyType-name has to match the sourcefile-name (+ '.society.xml')<br>and the package has to match the location of this sourcefile within the file-system.</i>";
			textFieldTypeName.setText(textFieldSourceName.getText().substring(0, textFieldSourceName.getText().indexOf(".society.xml")));
			model.setName(textFieldTypeName.getText());
			model.getDocument().setSourcePath(textFieldSourcePath.getText());
			model.getDocument().setSourceName(textFieldSourceName.getText());
		}

		if (!dialogMessage.equals(""))
			JOptionPane.showMessageDialog(parentFrame, "<html>"+dialogMessage+"<p>&nbsp;<p>Please make sure to save the SocietyType, so that the new file will be created.</html>");

		model.setDescription(textAreaDescription.getText());
		model.setDefaultSocietyInstanceName((String)comboBoxDefaultInstance.getSelectedItem());
		model.setIconName(iconName);

		ModelIntegrityChecker checker = new ModelIntegrityChecker();
		boolean ok = checker.checkIntegrity(model);
		if (!ok)
			mainPanel.showDialog(new ExceptionDialog(model.getIntegrityStatus()));

		model.getDocument().setSaved(false);
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getSource() == buttonChangeImage)
		{
			File file = (File)mainPanel.showDialog(AbstractMainPanel.CHOOSE_ICON_DIALOG);
			if (file != null)
			{
				iconName = file+"";
				getRepository().getProject().setWorkingDirectory(file+"");
				iconLabel.setIcon(ImageIconLoader.createImageIcon(iconName, 32, 32));
			}
		}
		else if (evt.getSource() == buttonChangeSourcePath)
		{
			File directory = (File)mainPanel.showDialog(AbstractMainPanel.CHOOSE_DIRECTORY_DIALOG);
			if (directory != null)
			{
				getRepository().getProject().setWorkingDirectory(directory+"");
				String directoryName = directory + "" + File.separator;
				textFieldSourcePath.setText(directoryName);
			}
		}
		else if (evt.getSource() == buttonChangeSourceName)
		{
			File file = (File)mainPanel.showDialog(AbstractMainPanel.CHOOSE_AGENTTYPE_FILE_DIALOG);
			if (file != null)
			{
				String name = file+"";
				textFieldSourceName.setText(name);
				getRepository().getProject().setWorkingDirectory(file+"");
			}
		}
		else if (evt.getSource() == buttonStart)
		{
            mainPanel.showDialog(new StartSocietyInstanceDialog(mainPanel, model.getDefaultSocietyInstance()));
		}
		else if (evt.getSource() == buttonApply)
		{
			applyChanges();
		}
		else if (evt.getSource() == buttonSave)
		{
            ExceptionDialog.isVisible = false;
			applyChanges();
			ExceptionDialog.isVisible = true;
			boolean savingSuccessful = getRepository().getModelManager().saveModel(model);

			if (savingSuccessful)
				JOptionPane.showMessageDialog(parentFrame, "<html><h3>SocietyType saved !</h3>The SocietyType has been saved to:<p>"+model.getDocument().getSource()+"</html>");
		}
		else if (evt.getSource() == buttonCreate)
		{
			ExceptionDialog.isVisible = false;
			applyChanges();
			ExceptionDialog.isVisible = true;
			ISocietyInstance newInstance = new SocietyInstance();
			model.addSocietyInstance(newInstance);
			getRepository().getProject().throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.SOCIETYINSTANCE_SELECTED, newInstance, getRepository().getProject()));
		}
	}
}
