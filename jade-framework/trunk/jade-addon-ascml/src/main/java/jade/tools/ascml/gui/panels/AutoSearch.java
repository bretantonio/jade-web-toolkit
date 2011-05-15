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
import java.util.*;
import jade.tools.ascml.gui.dialogs.ExceptionDialog;
import jade.tools.ascml.gui.dialogs.ProgressDialog;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.events.ProgressUpdateListener;
import jade.tools.ascml.events.ProgressUpdateEvent;
import jade.tools.ascml.events.LongTimeActionStartEvent;

public class AutoSearch extends AbstractPanel
                        implements ActionListener, ProgressUpdateListener
{
    private final static String CMD_ADD_SELECTED	= "add_selected";
	private final static String CMD_SELECT_ALL		= "select_all";
	private final static String CMD_DESELECT_ALL	= "deselect_all";
	private final static String CMD_AUTOSEARCH		= "auto_search";
	private final static String CMD_ADD_AGENTTYPE	= "add_agenttype_";
	private final static String CMD_ADD_SOCIETYTYPE	= "add_societytype_";

	private JCheckBox[] checkBoxes;
	private JButton[] addButtons;
	private String[] modelLocations;

	/**
	 *  
	 */
	public AutoSearch(AbstractMainPanel mainPanel)
	{
		super(mainPanel);
		this.setBackground(Color.WHITE);
				
		this.setLayout(new GridBagLayout());
		init();
	}
	
	private void init()
	{
		// clean up first
		this.removeAll();

		Object[] models = getRepository().getModelIndex().getModelSources();
		if (models.length == 0)
		{
			String dialogDescription = "<html><h2>&nbsp;Auto-Search</h2>&nbsp;&nbsp;<i>No new models found so far !</i><br>&nbsp;&nbsp;In the model-path you have specified could no models be found. <br>&nbsp;&nbsp;You have the following options to procede: <ul><li>Press the <i>Refresh Now !</i>-button below to start a new search</li><li>Go to the <i>Project-Options</i> and change your model-path</li><li>Manually add a model to your Repository by choosing <i> Add ... </i><br>from the Project-menu</li></ul></html>";
			this.add(new JLabel(dialogDescription), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		}
		else // at least one model has been found
		{
			String dialogDescription = "<html><h2>&nbsp;Auto-Search</h2>&nbsp;&nbsp;You may add particular models by clicking on one of the \"<i>Add</i>\"-Buttons.<br>&nbsp;&nbsp; Select some checkboxes (manually or by clicking on the \"<i>Select All</i>\"-button) <br>&nbsp;&nbsp; and click the \"<i>Add Selected</i>\"-button to add all of them to the Repository.</html>";
			this.add(new JLabel(dialogDescription), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));

			JPanel resultPanel = createResultPanel(models);

			JScrollPane resultScrollPane = new JScrollPane(resultPanel);
			this.add(resultScrollPane, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0, 0));

			JPanel buttonPanel = createButtonPanel();
			this.add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
		} // end else

		JPanel refreshIndexPanel = createRefreshIndexPanel();
		this.add(refreshIndexPanel, new GridBagConstraints(0, 3, 1, 1, 1, 0.01, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0, 0));
	}

	private JPanel createResultPanel(Object[] models)
	{
		// create the Panel, in which the result is presented
		JPanel resultPanel = new JPanel(new GridBagLayout());
		resultPanel.setBackground(Color.WHITE);

		checkBoxes = new JCheckBox[models.length];
		addButtons = new JButton[models.length];
		modelLocations = new String[models.length];

		// create a new sorted (in natural order) Set
		TreeSet sortedSet = new TreeSet();
		for (int i=0; i <  models.length; i++)
		{
			String locationString = models[i].toString();
			sortedSet.add(locationString);
		}

		// now iterate through the Vector and add simple panels to the resultPanel
		Iterator<String> setIterator = sortedSet.iterator();
        int i=0;
		while(setIterator.hasNext())
		{
			String locationString = setIterator.next();

			JLabel iconLabel;
			addButtons[i] = new JButton("Add");
			if (locationString.endsWith(".agent.xml"))
			{
				addButtons[i].setActionCommand(CMD_ADD_AGENTTYPE + locationString);
				iconLabel = new JLabel(ImageIconLoader.createImageIcon(ImageIconLoader.AGENTTYPE, 14, 14));
			}
			else
			{
				addButtons[i].setActionCommand(CMD_ADD_SOCIETYTYPE + locationString);
				iconLabel = new JLabel(ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYTYPE, 14, 14));
			}
			addButtons[i].setToolTipText("Add this model-description-file to the repository.");
			addButtons[i].addActionListener(this);
			addButtons[i].setMargin(new Insets(1,2,1,2));
			addButtons[i].setPreferredSize(new Dimension(40,18));
			addButtons[i].setMaximumSize(new Dimension(40,18));

			checkBoxes[i] = new JCheckBox(locationString);
			checkBoxes[i].setBackground(Color.WHITE);

			modelLocations[i] = locationString;

			resultPanel.add(iconLabel, new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
			resultPanel.add(addButtons[i], new GridBagConstraints(1, i, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));
			resultPanel.add(checkBoxes[i], new GridBagConstraints(2, i, 1, 1, 1, 0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0,2,0,2), 0, 0));

			i++;
		}

		return resultPanel;
	}

	private JPanel createButtonPanel()
	{
        JButton selectAllButton = new JButton("Select All");
		selectAllButton.setToolTipText("Select all of the above agent- and society-description-files.");
		selectAllButton.addActionListener(this);
		selectAllButton.setActionCommand(CMD_SELECT_ALL);

		JButton deselectAllButton = new JButton("Deselect All");
		deselectAllButton.setToolTipText("De-Select all of the above agent- and society-description-files.");
		deselectAllButton.addActionListener(this);
		deselectAllButton.setActionCommand(CMD_DESELECT_ALL);

		JButton addSelectedButton = new JButton("Add Selected");
		addSelectedButton.setToolTipText("Add the selected description-files to the repository.");
		addSelectedButton.addActionListener(this);
		addSelectedButton.setActionCommand(CMD_ADD_SELECTED);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(addSelectedButton);
		buttonPanel.add(selectAllButton);
		buttonPanel.add(deselectAllButton);

		return buttonPanel;
	}

	private JPanel createRefreshIndexPanel()
	{
		JPanel refreshIndexPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		refreshIndexPanel.setBackground(Color.WHITE);

		GregorianCalendar time = new GregorianCalendar();
		time.setTimeInMillis(getRepository().getModelIndex().getLastIndexRefreshTime());

		JLabel lastUpdate;
		if (getRepository().getModelIndex().getLastIndexRefreshTime() == 0)
			lastUpdate = new JLabel("<html>Result-index has not yet been built</html>");
		else
		{
			String hour = "" + time.get(Calendar.HOUR_OF_DAY);
			String minute = "" + time.get(Calendar.MINUTE);
			if (minute.length() == 1)
				minute = "0" + minute;
			String seconds = "" + time.get(Calendar.SECOND);
			if (seconds.length() == 1)
				seconds = "0" + seconds;
			lastUpdate = new JLabel("<html>Last update: <b>" + hour + ":" + minute + ":" + seconds + " o'clock</b></html>");
		}
		JButton updateButton = new JButton("Rebuild Index");
		updateButton.setToolTipText("Automatically search for agent- and society-description-files in the model-paths.");
		updateButton.setActionCommand(CMD_AUTOSEARCH);
		updateButton.addActionListener(this);

		refreshIndexPanel.add(updateButton);
		refreshIndexPanel.add(lastUpdate);

		return refreshIndexPanel;
	}

	// ---------- actionListener-methods --------------
	public void actionPerformed(ActionEvent e)
	{
		/*String[] selectedIndices = table.getSelectedRows();
		for (int i=0; i < selectedIndices.length; i++)
		{
			String fileName = tableModel.getValueAt(selectedIndices[i], 0);
		}*/
		String cmd = e.getActionCommand();
		if (cmd.equals(CMD_SELECT_ALL))
		{
			for (int i=0; i < checkBoxes.length; i++)
			{
				checkBoxes[i].setSelected(true);
			}
		}
		if (cmd.equals(CMD_DESELECT_ALL))
		{
			for (int i=0; i < checkBoxes.length; i++)
			{
				checkBoxes[i].setSelected(false);
			}
		}
		else if (cmd.equals(CMD_ADD_SELECTED))
		{
			Vector<String> agentsToAdd = new Vector();
			Vector<String> societiesToAdd = new Vector();

			for (int i=0; i < checkBoxes.length; i++)
			{
				if (checkBoxes[i].isSelected())
				{
					String locationString = modelLocations[i];
					if (locationString.endsWith("agent.xml"))
						agentsToAdd.add(locationString);
					else
						societiesToAdd.add(locationString);
				}
			}

			try
			{
				if (agentsToAdd.size() > 0)
					getProject().addAgentTypes(agentsToAdd);
			}
			catch(ModelException exc)
			{
				mainPanel.showDialog(new ExceptionDialog(exc));
			}
			try
			{
				if (societiesToAdd.size() > 0)
					getProject().addSocietyTypes(societiesToAdd);
			}
			catch(ModelException exc)
			{
				mainPanel.showDialog(new ExceptionDialog(exc));
			}
		}
		else if (cmd.startsWith(CMD_ADD_AGENTTYPE))
		{
            String agentLocation = cmd.substring(CMD_ADD_AGENTTYPE.length());
			try
			{
				getProject().addAgentType(agentLocation);
			}
			catch(ModelException exc)
			{
				mainPanel.showDialog(new ExceptionDialog(exc));
			}
		}
		else if (cmd.startsWith(CMD_ADD_SOCIETYTYPE))
		{
            String societyLocation = cmd.substring(CMD_ADD_SOCIETYTYPE.length());
			try
			{
				getProject().addSocietyType(societyLocation);
			}
			catch(ModelException exc)
			{
				mainPanel.showDialog(new ExceptionDialog(exc));
			}
		}
		else if (cmd.equals(CMD_AUTOSEARCH))
		{
			ProgressDialog dialog = new ProgressDialog(getRepository(), "Searching for model-description-files...", "Searching, please wait ...");
			dialog.showDialog(mainPanel.parentFrame);

			getRepository().getListenerManager().addProgressUpdateListener(this);
			getRepository().throwLongTimeActionStartEvent(new LongTimeActionStartEvent(LongTimeActionStartEvent.ACTION_REBUILD_MODEL_INDEX));
		}
	}

	public void updateProgress(ProgressUpdateEvent event)
	{
		if (event.getEventCode() == ProgressUpdateEvent.PROGRESS_FINISHED)
		{
			JFrame pleaseWaitFrame = null;
			if (getRepository().getModelIndex().getModelSources().length > 100)
			{
				pleaseWaitFrame = new JFrame("Please Wait");
				pleaseWaitFrame.getContentPane().setLayout(new FlowLayout());
				pleaseWaitFrame.getContentPane().add(new JLabel("<html><h2>Please Wait ...</h2>while the results are rendered.</html>)"));
				pleaseWaitFrame.getContentPane().setBackground(Color.WHITE);
				pleaseWaitFrame.setIconImage(ImageIconLoader.createImageIcon(ImageIconLoader.ASCML_LOGO).getImage());
				pleaseWaitFrame.setSize(300, 100);
				pleaseWaitFrame.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-pleaseWaitFrame.getWidth()/2),
						(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-pleaseWaitFrame.getHeight()/2));

				pleaseWaitFrame.setVisible(true);
			}

			init();
			this.validate();
			this.repaint();

			if (getRepository().getModelIndex().getModelSources().length > 100)
			{
				pleaseWaitFrame.setVisible(false);
			}
		}
	}
}
