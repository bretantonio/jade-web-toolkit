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
import java.awt.event.*;
import jade.tools.ascml.repository.*;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;

public class ModelNotFoundDialog extends AbstractDialog implements ActionListener
{
	public final static String MESSAGE = "<html><h2>&nbsp;<i>Model not found </i></h2>" +
		                              "&nbsp;&nbsp;The current project points to a model-description, which couldn't be found.<br>&nbsp;&nbsp;Maybe the description-file has been moved or deleted.<br>&nbsp;<br>" +
		                              "&nbsp;&nbsp;You have now serveral options to proceed:" +
									  "<ul>" +
									  "<li>choose the appropiate location of the model with the file-chooser button</li>" +
									  // "<li>use the <i>Auto-Search</i>-button to let the ASCML-agent search for the models.</li>" +
									  "<li>press the <i>Cancel</i>-button to abort loading the model</li>" +
									  "</ul></html>";
	public final static int OK_OPTION = 1;
	public final static int CANCEL_OPTION = -1;

	private JFrame parentFrame;

	private JTextField modelLocationTextField; // output
	private String missingModel; // input
	private boolean isAgentDialog;

	private JPanel dialogPane;

	public ModelNotFoundDialog(Repository repository, String missingModel, boolean isAgentDialog)
	{
		super(repository);
		this.missingModel = missingModel;
		this.isAgentDialog = isAgentDialog;
		initDialogPane();
	}

	private void initDialogPane()
	{
		System.err.println("ModelNotFoundDialog.initDialogPane");
		dialogPane = new JPanel(new GridBagLayout());

		GridBagConstraints con = new GridBagConstraints();
		con.anchor = GridBagConstraints.LINE_START;
		con.fill = GridBagConstraints.HORIZONTAL;
		con.insets = new Insets(3,3,10,3);
		con.weightx = 1;
		con.gridx = 0;
		con.gridy = 0;
		con.gridwidth = 3;
		dialogPane.add(new JLabel(MESSAGE), con);
		con.insets = new Insets(1,3,1,3);
		con.gridwidth = 1;
		con.weighty = 1;

		JButton fileChooserButton = new JButton("...");
		fileChooserButton.addActionListener(this);
		fileChooserButton.setActionCommand("choose");
		modelLocationTextField = new JTextField(missingModel, 40);
		JPanel oneLocationPanel = new JPanel();
		oneLocationPanel.setBorder(BorderFactory.createTitledBorder(missingModel));
		oneLocationPanel.add(modelLocationTextField);
		oneLocationPanel.add(fileChooserButton);

		con.gridy = 1;
		con.gridx = 0;
		con.weightx = 1;
		con.fill = GridBagConstraints.HORIZONTAL;
		dialogPane.add(oneLocationPanel, con);
	}

	public Object showDialog(JFrame parentFrame)
	{
		this.parentFrame = parentFrame;
		Object[] options = { "Retry", "Cancel" };

		int result = JOptionPane.showOptionDialog(parentFrame, dialogPane, "One or more models could not be found ...",
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, null);

		if (result == 0) // retry
		{
			String missingModelLocation = modelLocationTextField.getText().trim();
			try
			{
				repository.getProject().addAgentType(missingModelLocation);
			}
			catch(ModelException me)
			{
				repository.throwExceptionEvent(me);
			}
		}

		return null;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		
		if (cmd.equals("choose"))
		{
			AddModelDialog addModelDialog = new AddModelDialog(repository, isAgentDialog);
			// if the user chose a model using the addModelDialog this model is
			// automatically loaded into the repository
			Object result = addModelDialog.showDialog(parentFrame);

			if (result != null)
			{
				String sourceName;
				if (result instanceof IAgentType)
					sourceName = ((IAgentType)result).getDocument().getSource();
				else
					sourceName = ((ISocietyType)result).getDocument().getSource();

				modelLocationTextField.setText(sourceName);
			}
		}
	}
}

