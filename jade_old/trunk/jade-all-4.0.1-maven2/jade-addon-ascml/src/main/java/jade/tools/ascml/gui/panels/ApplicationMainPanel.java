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
import jade.tools.ascml.gui.components.tree.RepositoryTree;
import jade.tools.ascml.gui.components.StatusBar;
import jade.tools.ascml.gui.components.TabbedPaneManager;
import jade.tools.ascml.gui.dialogs.*;
import jade.tools.ascml.repository.*;

public class ApplicationMainPanel extends AbstractMainPanel
{
	private StatusBar statusBar;
	private RepositoryTree repositoryTree;
	private TabbedPaneManager paneManager;

	/**
	 * The constructor initializes all graphical elements contained
	 * in the main window and initializes the tree.
	 */
	public ApplicationMainPanel(JFrame parentFrame, Repository repository)
	{
		super(parentFrame, repository);
		// create the status-bar
		statusBar = new StatusBar();

		this.add(statusBar, BorderLayout.SOUTH);
		this.validate(); // show the status-bar

		// create the repository-tree and the tabbedPane-manager
		paneManager = new TabbedPaneManager(this);
		repositoryTree = new RepositoryTree(this, paneManager);

        // create a split-pane with the repository-tree on the left and
		// the paneManager on the right site.
		JSplitPane splitPane =
				new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(repositoryTree), paneManager);
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerLocation(200);
		splitPane.setDividerSize(3);

		this.add(splitPane, BorderLayout.CENTER);

		super.finishedInit();
	}

	/**
	 * Set a new panel in the content-area of the application-window.
	 * The content-area might be any container-component withinin the main-JPanel.
	 * In the content-area panels which present detailed information about something
	 * should be shown. For example, the ASCML Main-GUI (normally this class by default)
	 * is parted into four areas, the menuBar, the statusBar, the repository-tree and the content-area.
	 * An ASCML-gui-component may change only the content-area cause the other three parts are static.
	 * @param paneIdentifier  The String-identifier of the pane to view
	 * This should be one of the following constants(declared in TabbedPaneManager):
	 * REPOSITORY_PANE, LOAD_PANE, ERROR_PANE.
	 * @param tabIdentifier  Either 0, if the default-tab should be viewed,
	 * or the int-identifier of the tab to view.
	 * This should be one of the following constants (declared in TabbedPaneManager):
	 * OPTIONS_TAB, AUTOSEARCH_TAB, HELP_TAB, ABOUT_TAB
	 */
	public void setContentPanel(String paneIdentifier, int tabIdentifier)
	{
		paneManager.setPane(paneIdentifier, tabIdentifier);
		parentFrame.pack();
	}

	/**
	 * Show a standard-Dialog. Standard-Dialogs are predefined dialogs which take
	 * action for themselves. This means all neccessary logic to handle and process
	 * user-inputs lies in the dialogs, so no further processing by any requester
	 * has to be done.
	 * @param dialogIdentifier  possible dialog-identifiers are specified in the AbstractMainPanel
	 */
	public Object showDialog(String dialogIdentifier)
	{
		if (dialogIdentifier == ADD_AGENTTYPE_DIALOG)
		{
			return showDialog(new AddModelDialog(getRepository(), true)); // true == AgentType-dialog
		}
		else if (dialogIdentifier == ADD_SOCIETYTYPE_DIALOG)
		{
			return showDialog(new AddModelDialog(getRepository(), false)); // false == SocietyTypeDialog
		}
		else if (dialogIdentifier == ADD_PROJECT_DIALOG)
		{
			return showDialog(new AddProjectDialog(getRepository()));
		}
		else if (dialogIdentifier == EXIT_DIALOG)
		{
			return showDialog(new ExitDialog(this));
		}
		else if (dialogIdentifier == CHOOSE_ICON_DIALOG)
		{
			return showDialog(new ChooseIconDialog(getRepository()));
		}
		else if (dialogIdentifier == CHOOSE_DIRECTORY_DIALOG)
		{
			return showDialog(new ChooseDirectoryDialog(getRepository()));
		}
		else if (dialogIdentifier == CHOOSE_AGENTTYPE_FILE_DIALOG)
		{
			return showDialog(new ChooseAgentTypeFileDialog(getRepository()));
		}
		else if (dialogIdentifier == CHOOSE_SOCIETYTYPE_FILE_DIALOG)
		{
			return showDialog(new ChooseSocietyTypeFileDialog(getRepository()));
		}
		else if (dialogIdentifier == CHOOSE_JAVA_FILE_DIALOG)
		{
			return showDialog(new ChooseJavaFileDialog(getRepository()));
		}
		System.err.println("WARNING:ApplicationMainPanel tried to show a Dialog which is not a standard-Dialog. Have a look at ApplicationMainPanel");
		return null;
	}

	/**
	 * Show the details about a model-object. This method should be called, when detailed information
	 * about the model should be shown on the screen.
	 * For example, this ApplicationMainPanel dispachtes the model, that should be shown, to the
	 * TabbedPaneManager. This manager then decides on which set of pane's the information should be shown.
	 * @param model The model-object that should be viewed
	 */
	public void showModelDetails(Object model)
	{
		paneManager.setObjectToView(model);
	}

	/**
	 * Select a model-object. This method should be called, when a model should be selected
	 * in the repository-view.
	 * For example, this ApplicationMainPanel dispachtes the model, that should be selected, to the
	 * RepositoryTree. The RepositoryTree-object then selects the appropiate model.
	 * @param model The model-object that should be selected
	 */
	public void selectModel(Object model)
	{
		repositoryTree.setSelectedModel(model);
	}

	public void exit()
	{
		statusBar.setLabel("Goodbye, the ASCML is going to shut down ...");
		repositoryTree.exit();
		paneManager.exit();
		super.exit();
	}

}
