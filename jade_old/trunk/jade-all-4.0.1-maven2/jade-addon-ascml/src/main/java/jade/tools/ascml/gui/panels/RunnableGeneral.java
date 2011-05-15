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
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.absmodel.IAbstractRunnable;

public class RunnableGeneral extends AbstractPanel implements ActionListener, ModelChangedListener
{
    private ImageIcon lifeCycleIcon;
    private JLabel statusIconLabel;
	private IAbstractRunnable model;
    private JTextField textName;
    private JTextField textParentName;
    private JTextArea textDetailedStatus;

	/**
	 * @param model the AgentModel to show in the dialog, this may be an
	 *              AgentInstanceModel or an AgentTypeModel
	 */
	public RunnableGeneral(AbstractMainPanel mainPanel, IAbstractRunnable model)
	{
		super(mainPanel);
		this.model = model;
		init();
	}

	private void init()
	{
		this.removeAll();
		this.setBackground(Color.WHITE);
		this.setLayout(new GridBagLayout());

		// ... and add the content (content is layout out in it's own panel)
		this.add(createAttributePanel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createAttributePanel()
	{
		Status status = model.getStatus();

		// prepare instance name
		textName = new JTextField(model.getName(), 20);
		textName.setEditable(false);
		textName.setBackground(Color.WHITE);

		// prepare parentModel's name
		textParentName = new JTextField(model.getParentModel().toString(), 20);
		textParentName.setEditable(false);
		textParentName.setBackground(Color.WHITE);

		// prepare detailed status
		textDetailedStatus = new JTextArea(model.getDetailedStatus(), 5, 20);
		textDetailedStatus.setFont(new Font("Arial", Font.PLAIN, 12));
		textDetailedStatus.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		textDetailedStatus.setEditable(false);
		textDetailedStatus.setLineWrap(true);
		textDetailedStatus.setWrapStyleWord(true);
		textDetailedStatus.setBackground(Color.WHITE);

		initLifeCycleIcon(status);

		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);

        statusIconLabel = new JLabel(ImageIconLoader.createRunnableStatusIcon(status, 50, 50));
		mainPanel.add(statusIconLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel("<html><h2>Runnable Instance</h2>Here, you see the status of this runnable Model.</html>"), new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel("Instance-Name:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(textName, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel("Type-Name:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(textParentName, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel("Status-Details:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(textDetailedStatus, new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		mainPanel.add(new JLabel(lifeCycleIcon), new GridBagConstraints(1, 4, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(5,5,5,5), 0, 0));

		return mainPanel;
	}

    private void initLifeCycleIcon(Status status)
    {
        if (status instanceof Starting)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_STARTING);
        }
        else if (status instanceof Functional)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_RUNNING);
        }
        else if (status instanceof NonFunctional)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_NOT_RUNNING);
        }
        else if (status instanceof Stopping)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_STOPPING);
        }
        else if (status instanceof jade.tools.ascml.onto.Error)
        {
            lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_ERROR);
        }
		else {
			// Having no status means, this one was create just right now 
			lifeCycleIcon = ImageIconLoader.createImageIcon(ImageIconLoader.LIFECYCLE_STARTING);
        }
		
		if (lifeCycleIcon!=null)
			ImageIconLoader.scaleImageIcon(lifeCycleIcon, 300, 138);
    }

    public void updateAllComponents(Status status)
    {
        // the elements may be null at first call
        if (textName != null)
        {
            textName.setText(model.getName());
            textParentName.setText(model.getParentModel().toString());
            textDetailedStatus.setText(model.getDetailedStatus());
            initLifeCycleIcon(status);
            statusIconLabel.setIcon(ImageIconLoader.createRunnableStatusIcon(status, 50, 50));

            this.repaint();
        }
    }

	public void actionPerformed(ActionEvent evt)
	{

	}

	public void modelChanged(ModelChangedEvent event)
	{

        // System.err.println("RunnableGeneral.modelChanged: (" + counter + ") model = "+event.getModel() + " event=" + event.getEventCode());

        if ((event.getModel() == model))
		{
			updateAllComponents(model.getStatus());
		}
	}
}
