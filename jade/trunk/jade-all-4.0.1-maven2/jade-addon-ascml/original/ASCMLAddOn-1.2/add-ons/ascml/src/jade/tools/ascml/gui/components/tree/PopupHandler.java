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


package jade.tools.ascml.gui.components.tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.*;
import jade.tools.ascml.gui.dialogs.StartAgentInstanceDialog;
import jade.tools.ascml.gui.dialogs.StartSocietyInstanceDialog;
import jade.tools.ascml.gui.dialogs.ExceptionDialog;
import jade.tools.ascml.gui.panels.AbstractMainPanel;
import jade.tools.ascml.gui.components.ViewMenu;
import jade.tools.ascml.gui.components.ProjectMenu;
import jade.tools.ascml.gui.components.TabbedPaneManager;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.events.ModelActionEvent;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.model.runnable.RunnableSocietyInstance;
import jade.tools.ascml.model.runnable.RunnableAgentInstance;
import jade.tools.ascml.model.jibx.SocietyInstance;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.onto.Stopping;
import jade.tools.ascml.onto.Functional;
import jade.tools.ascml.absmodel.*;

public class PopupHandler extends MouseAdapter implements ActionListener
{
	private final static String CMD_AUTOSEARCH 						= "autosearch";

	private final static String CMD_REMOVE_AGENTTYPE				= "remove_agenttype";
	private final static String CMD_REMOVE_SOCIETYTYPE				= "remove_society_";

	private final static String CMD_RELOAD_AGENTTYPE				= "reload_agenttype";
	private final static String CMD_RELOAD_SOCIETYTYPE				= "reload_societytype";

	private final static String CMD_STOP_RUNNABLEAGENTINSTANCE		= "stop_runnable_agentinstance";
	private final static String CMD_START_AGENTINSTANCE				= "start_agentinstance";
	private final static String CMD_START_AGENTINSTANCE_DIALOG		= "start_agentinstance_dialog_";
	private final static String CMD_STOP_RUNNABLESOCIETYINSTANCE	= "stop_runnable_societyinstance";
	private final static String CMD_START_SOCIETYINSTANCE_DIALOG	= "start_societyinstance_dialog_";
	private final static String CMD_START_DEFAULTSOCIETYINSTANCE_DIALOG	= "start_default_societyinstance_dialog_";

	private final static String CMD_RESTART_RUNNABLEAGENTINSTANCE	= "restart_runnable_agentinstance";
	private final static String CMD_REMOVE_RUNNABLEAGENTINSTANCE	= "remove_runnable_agentinstance";
	private final static String CMD_RESTART_RUNNABLESOCIETYINSTANCE	= "restart_runnable_societyinstance";
	private final static String CMD_REMOVE_RUNNABLESOCIETYINSTANCE	= "remove_runnable_societyinstance";

	private final static String CMD_ADD_SOCIETYTYPE_DIALOG			= "add_societytype_dialog";
	private final static String CMD_ADD_AGENTTYPE_DIALOG			= "add_agenttype_dialog";
    private final static String CMD_CREATE_AGENTTYPE				= "create_agenttype";
	private final static String CMD_CREATE_SOCIETYTYPE				= "create_societytype";
	private final static String CMD_CREATE_SOCIETYINSTANCE			= "create_societyinstance";

	private final static String CMD_SHOW_STATUS_DETAILS				= "show_status_details";

	private JPopupMenu popup;
	private AbstractMainPanel mainPanel;
	private Repository repository;
	private RepositoryTree repositoryTree;
	private Object popupOnObject; // the userObject, on which the popup-menu relies.

	private ProjectMenu changeProjectSubMenu;

	public PopupHandler(RepositoryTree repositoryTree, AbstractMainPanel parentPanel)
	{
		this.repositoryTree = repositoryTree;
		this.mainPanel = parentPanel;
		this.repository = parentPanel.getRepository();
	}

	public void mouseReleased( MouseEvent e )
	{
		if ( e.isPopupTrigger() ||
			 ( e.isControlDown() && e.getButton() == 1 ) ||
			 ( e.getButton() == 3) )
		{
			repositoryTree = (RepositoryTree)e.getSource();
			int rowHeight = repositoryTree.getRowHeight();
			int y = e.getY();

			// break if nothing was really selected
			if ((y / rowHeight) > repositoryTree.getRowCount())
				return;

			// get selected model
			repositoryTree.setSelectionRow( (y / rowHeight) );
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)repositoryTree.getLastSelectedPathComponent();
			if (node == null)
				return; // in case no node is selected, but context-menu should be shown

			popupOnObject = node.getUserObject();

			popup = new JPopupMenu();
			popup.setOpaque(true);
			popup.setLightWeightPopupEnabled(true);

			if ((popupOnObject instanceof IAgentType))
			{
				IAgentType agentType = (IAgentType)popupOnObject;
				String status = agentType.getStatus();

				popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Agent-Type: " + agentType.getName() + "</i></html>"));
				popup.addSeparator();

				JMenuItem mi = null;
				if (status == IAgentType.STATUS_OK)
				{
					mi = new JMenuItem("Start new Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_START_AGENTINSTANCE_DIALOG);
					popup.add(mi);
				}
				else
				{
					mi = new JMenuItem("Show Status-Details");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_SHOW_STATUS_DETAILS);
					popup.add(mi);
				}

				popup.addSeparator();

				mi = new JMenuItem("Reload into Repository");
				mi.addActionListener(this);
				mi.setActionCommand(CMD_RELOAD_AGENTTYPE);
				popup.add(mi);

				mi = new JMenuItem("Remove from Repository");
				mi.addActionListener(this);
				mi.setActionCommand(CMD_REMOVE_AGENTTYPE);
				popup.add(mi);

				popup.addSeparator();

				mi = new JMenuItem("Create new AgentType");
				mi.addActionListener(this);
				mi.setActionCommand(CMD_CREATE_AGENTTYPE);
				popup.add(mi);
			}
			else if (popupOnObject instanceof IAgentInstance)
			{
				IAgentInstance agentInstance = (IAgentInstance)popupOnObject;
				String status = agentInstance.getStatus();

				popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Agent-Instance: " + agentInstance.getName() + "</i></html>"));
				popup.addSeparator();

				if (status == IAgentInstance.STATUS_OK)
				{
					JMenuItem mi = new JMenuItem("Start Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_START_AGENTINSTANCE);
					popup.add(mi);
				}
				else
				{
					JMenuItem mi = new JMenuItem("Show Status-Details");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_SHOW_STATUS_DETAILS);
					popup.add(mi);
				}
			}
			else if (popupOnObject instanceof ISocietyType)
			{
				ISocietyType societyType = (ISocietyType)popupOnObject;
				String status = societyType.getStatus();

				popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Society: " + societyType.getName() + "</i></html>"));
				popup.addSeparator();

				JMenuItem mi;
				if (status == ISocietyType.STATUS_OK)
				{
					mi = new JMenuItem("Start Default-Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_START_DEFAULTSOCIETYINSTANCE_DIALOG);
					popup.add(mi);
				}
				else
				{
					mi = new JMenuItem("Show Status-Details");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_SHOW_STATUS_DETAILS);
					popup.add(mi);
				}

				popup.addSeparator();

				mi = new JMenuItem("Create new SocietyInstance");
				mi.addActionListener(this);
				mi.setActionCommand(CMD_CREATE_SOCIETYINSTANCE);
				popup.add(mi);

				popup.addSeparator();

				mi = new JMenuItem("Reload into Repository");
				mi.addActionListener(this);
				mi.setActionCommand(CMD_RELOAD_SOCIETYTYPE);
				popup.add(mi);

				mi = new JMenuItem("Remove from Repository");
				mi.addActionListener(this);
				mi.setActionCommand(CMD_REMOVE_SOCIETYTYPE);
				popup.add(mi);

				popup.addSeparator();

				mi = new JMenuItem("Create new SocietyType");
				mi.addActionListener(this);
				mi.setActionCommand(CMD_CREATE_SOCIETYTYPE);
				popup.add(mi);
			}
			else if (popupOnObject instanceof ISocietyInstance)
			{
				ISocietyInstance societyInstance = (ISocietyInstance)popupOnObject;
				String status = societyInstance.getStatus();

				popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Society-Instance: " + societyInstance.getName() + "</i></html>"));
				popup.addSeparator();
                if (status == ISocietyInstance.STATUS_OK)
				{
					JMenuItem mi = new JMenuItem("Start new Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_START_SOCIETYINSTANCE_DIALOG);
					popup.add(mi);
				}
				else
				{
					JMenuItem mi = new JMenuItem("Show Status-Details");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_SHOW_STATUS_DETAILS);
					popup.add(mi);
				}
			}
			else if (popupOnObject instanceof ISocietyInstanceReference)
			{
				ISocietyInstanceReference societyInstanceReference = (ISocietyInstanceReference)popupOnObject;
				String status = societyInstanceReference.getStatus();

				popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Society-Instance-Reference: " + societyInstanceReference.getName() + "</i></html>"));
				popup.addSeparator();

				if (status != ISocietyInstanceReference.STATUS_OK)
				{
					JMenuItem mi = new JMenuItem("Show Status-Details");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_SHOW_STATUS_DETAILS);
					popup.add(mi);
				}
			}
			else if (popupOnObject instanceof IRunnableAgentInstance)
			{
				RunnableAgentInstance runnableModel = (RunnableAgentInstance)popupOnObject;
				Status status = runnableModel.getStatus();
				popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Agent-Instance: " + runnableModel.getName() + " (" + status + ")</i></html>"));
				popup.addSeparator();

                if (status instanceof Functional)
				{
					JMenuItem mi = new JMenuItem("Stop Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_STOP_RUNNABLEAGENTINSTANCE);
					popup.add(mi);
				}
				else if ((status instanceof jade.tools.ascml.onto.Error) || (status instanceof Stopping))
				{
					JMenuItem mi = new JMenuItem("Remove Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_REMOVE_RUNNABLEAGENTINSTANCE);
					popup.add(mi);

					mi = new JMenuItem("Restart Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_RESTART_RUNNABLEAGENTINSTANCE);
					popup.add(mi);
				}
			}
			else if (popupOnObject instanceof IRunnableSocietyInstance)
			{
				RunnableSocietyInstance runnableModel = (RunnableSocietyInstance)popupOnObject;
				Status status = runnableModel.getStatus();

				popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Society-Instance: " + runnableModel.getName() + " (" + status + ")</i></html>"));
				popup.addSeparator();

                if (status instanceof Functional)
				{
					JMenuItem mi = new JMenuItem("Stop Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_STOP_RUNNABLESOCIETYINSTANCE);
					popup.add(mi);
				}
				else if ((status instanceof jade.tools.ascml.onto.Error) || (status instanceof Stopping))
				{
					JMenuItem mi = new JMenuItem("Remove Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_REMOVE_RUNNABLESOCIETYINSTANCE);
					popup.add(mi);

					mi = new JMenuItem("Restart Instance");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_RESTART_RUNNABLESOCIETYINSTANCE);
					popup.add(mi);
				}
			}
			else if (popupOnObject instanceof String)
			{
				String userObjString = (String)popupOnObject;
				if (userObjString.startsWith(RepositoryTree.REPOSITORY_STRING))
				{
					popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Repository Options</i></html>"));
					popup.addSeparator();

					popup.add(new ViewMenu(mainPanel));
        			changeProjectSubMenu = new ProjectMenu(mainPanel);
					popup.add(changeProjectSubMenu);

					popup.addSeparator();

					JMenuItem mi = new JMenuItem("Load a SocietyType ...");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_ADD_SOCIETYTYPE_DIALOG);
					popup.add(mi);
					mi = new JMenuItem("Load an AgentType ...");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_ADD_AGENTTYPE_DIALOG);
					popup.add(mi);
					mi = new JMenuItem("Auto-Search for Agents/Societies");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_AUTOSEARCH);
					popup.add(mi);

					popup.addSeparator();

					mi = new JMenuItem("Create new SocietyType");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_CREATE_SOCIETYTYPE);
					popup.add(mi);
					mi = new JMenuItem("Create new AgentType");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_CREATE_AGENTTYPE);
					popup.add(mi);
				}
				else if (userObjString.equals(RepositoryTree.AGENTTYPES_STRING) || (userObjString.equals(RepositoryTree.ADD_AGENTMODEL_STRING)))
				{
					popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>AgentType-Options</i></html>"));
					popup.addSeparator();
					JMenuItem mi = new JMenuItem("Load an AgentType ...");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_ADD_AGENTTYPE_DIALOG);
					popup.add(mi);
					mi = new JMenuItem("Create new AgentType");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_CREATE_AGENTTYPE);
					popup.add(mi);
				}
				else if (userObjString.equals(RepositoryTree.SOCIETYTYPES_STRING) || (userObjString.equals(RepositoryTree.ADD_SOCIETYMODEL_STRING)))
				{
					popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>SocietyType-Options</i></html>"));
					popup.addSeparator();
					JMenuItem mi = new JMenuItem("Load a SocietyType ...");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_ADD_SOCIETYTYPE_DIALOG);
					popup.add(mi);
					mi = new JMenuItem("Create new SocietyType");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_CREATE_SOCIETYTYPE);
					popup.add(mi);
				}
				else if (userObjString.equals(RepositoryTree.ADD_A_MODEL_HEADER))
				{
					popup.add(new JLabel("<html>&nbsp;&nbsp;&nbsp;<i>Add a new Model ...</i></html>"));
					popup.addSeparator();
					JMenuItem mi = new JMenuItem("Load a SocietyType ...");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_ADD_SOCIETYTYPE_DIALOG);
					popup.add(mi);
					mi = new JMenuItem("Load an AgentType ...");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_ADD_AGENTTYPE_DIALOG);
					popup.add(mi);

					popup.addSeparator();

					mi = new JMenuItem("Create new SocietyType");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_CREATE_SOCIETYTYPE);
					popup.add(mi);
					mi = new JMenuItem("Create new AgentType");
					mi.addActionListener(this);
					mi.setActionCommand(CMD_CREATE_AGENTTYPE);
					popup.add(mi);
				}
			}
			popup.show(repositoryTree, e.getX(), y);
		}
	}

	public void actionPerformed(ActionEvent event)
	{
		String actionCommand = event.getActionCommand();
		if (actionCommand.equals(CMD_AUTOSEARCH))
		{
           mainPanel.setContentPanel(TabbedPaneManager.REPOSITORY_PANE, TabbedPaneManager.AUTOSEARCH_TAB);
		}
		else if (actionCommand.equals(CMD_REMOVE_AGENTTYPE))
		{
            String agentTypeName = ((IAgentType)popupOnObject).getFullyQualifiedName();
			repository.getProject().removeAgentType(agentTypeName);
		}
		else if (actionCommand.equals(CMD_REMOVE_SOCIETYTYPE))
		{
            String societyTypeName = ((ISocietyType)popupOnObject).getFullyQualifiedName();
			repository.getProject().removeSocietyType(societyTypeName);
		}
		else if (actionCommand.equals(CMD_RELOAD_AGENTTYPE))
		{
            String agentTypeName = ((IAgentType)popupOnObject).getFullyQualifiedName();
			repository.getProject().reloadAgentType(agentTypeName);
		}
		else if (actionCommand.equals(CMD_RELOAD_SOCIETYTYPE))
		{
            String societyTypeName = ((ISocietyType)popupOnObject).getFullyQualifiedName();
			repository.getProject().reloadSocietyType(societyTypeName);
		}
		else if (actionCommand.equals(CMD_START_AGENTINSTANCE))
		{
            IAgentInstance agentInstance = (IAgentInstance)popupOnObject;
			try
			{
				// create the model
				IRunnableAgentInstance[] runnableModels = (IRunnableAgentInstance[])repository.createRunnableAgentInstance(agentInstance.getType().getFullyQualifiedName() + "." + agentInstance.getName() + "." + agentInstance, 1);

				// and select the newly created instance
				mainPanel.selectModel(runnableModels[0]);

				// Throw the start-event
				for (int i=0; i < runnableModels.length; i++)
				{
					ModelActionEvent actionEvent = new ModelActionEvent(ModelActionEvent.CMD_START_AGENTINSTANCE, runnableModels[i]);
					mainPanel.throwModelActionEvent(actionEvent);
				}
			}
			catch(ModelException exc)
			{
				repository.throwExceptionEvent(exc);
			}
		}
		else if (actionCommand.equals(CMD_STOP_RUNNABLEAGENTINSTANCE))
		{
            IRunnableAgentInstance runnableModel = (IRunnableAgentInstance)popupOnObject;
			ModelActionEvent actionEvent = new ModelActionEvent(ModelActionEvent.CMD_STOP_AGENTINSTANCE, runnableModel);
			mainPanel.throwModelActionEvent(actionEvent);

			// select the nodes parentModel in the tree and collapse the node to make sure, the stopped node is not visible anymore
			// mainPanel.selectModel(runnableModel.getParentModel());
		}
		else if (actionCommand.equals(CMD_STOP_RUNNABLESOCIETYINSTANCE))
		{
			IRunnableSocietyInstance runnableModel = (IRunnableSocietyInstance)popupOnObject;
			ModelActionEvent actionEvent = new ModelActionEvent(ModelActionEvent.CMD_STOP_SOCIETYINSTANCE, runnableModel);
			mainPanel.throwModelActionEvent(actionEvent);

			// mainPanel.selectModel(runnableModel.getParentModel());
		}
		else if (actionCommand.equals(CMD_START_SOCIETYINSTANCE_DIALOG))
		{
			mainPanel.showDialog(new StartSocietyInstanceDialog(mainPanel, (ISocietyInstance)popupOnObject));
		}
		else if (actionCommand.equals(CMD_START_DEFAULTSOCIETYINSTANCE_DIALOG))
		{
			mainPanel.showDialog(new StartSocietyInstanceDialog(mainPanel, ((ISocietyType)popupOnObject).getDefaultSocietyInstance()));
		}
		else if ((actionCommand.equals(CMD_RESTART_RUNNABLEAGENTINSTANCE)) || (actionCommand.equals(CMD_RESTART_RUNNABLESOCIETYINSTANCE)))
		{
            System.err.println("PopupHandler.actionPerformed: implement me !!!");
		}
		else if ((actionCommand.equals(CMD_REMOVE_RUNNABLEAGENTINSTANCE)) || (actionCommand.equals(CMD_REMOVE_RUNNABLESOCIETYINSTANCE)))
		{
			try
			{
				repository.getRunnableManager().removeRunnable((IAbstractRunnable)popupOnObject);
			}
			catch(ModelException exc)
			{
				repository.throwExceptionEvent(exc);
			}
            // ((DefaultTreeModel)repositoryTree.getModel()).removeNodeFromParent((DefaultMutableTreeNode)repositoryTree.getLastSelectedPathComponent());
		}
		else if (actionCommand.equals(CMD_ADD_SOCIETYTYPE_DIALOG))
		{
            mainPanel.showDialog(AbstractMainPanel.ADD_SOCIETYTYPE_DIALOG);
		}
		else if (actionCommand.equals(CMD_ADD_AGENTTYPE_DIALOG))
		{
            mainPanel.showDialog(AbstractMainPanel.ADD_AGENTTYPE_DIALOG);
		}
		else if (actionCommand.equals(CMD_CREATE_SOCIETYTYPE))
		{
            repository.getProject().createSocietyType();
		}
		else if (actionCommand.equals(CMD_CREATE_AGENTTYPE))
		{
            repository.getProject().createAgentType();
		}
		else if (actionCommand.equals(CMD_CREATE_SOCIETYINSTANCE))
		{
            ISocietyInstance newInstance = new SocietyInstance();
			((ISocietyType)popupOnObject).addSocietyInstance(newInstance);
			repository.getProject().throwProjectChangedEvent(new ProjectChangedEvent(ProjectChangedEvent.SOCIETYINSTANCE_SELECTED, newInstance, repository.getProject()));
		}
		else if (actionCommand.startsWith(CMD_START_AGENTINSTANCE_DIALOG))
		{
			mainPanel.showDialog(new StartAgentInstanceDialog(mainPanel, (IAgentType)popupOnObject));
		}
		else if (actionCommand.equals(CMD_SHOW_STATUS_DETAILS))
		{
            ModelException exc = null;
			if (popupOnObject instanceof ISocietyType)
				exc = ((ISocietyType)popupOnObject).getIntegrityStatus();
			else if (popupOnObject instanceof IAgentType)
				exc = ((IAgentType)popupOnObject).getIntegrityStatus();
			else if (popupOnObject instanceof ISocietyInstance)
				exc = ((ISocietyInstance)popupOnObject).getParentSocietyType().getIntegrityStatus();
			else if (popupOnObject instanceof IAgentInstance)
				exc = ((IAgentInstance)popupOnObject).getParentSocietyInstance().getParentSocietyType().getIntegrityStatus();
			else if (popupOnObject instanceof ISocietyInstanceReference)
				exc = ((ISocietyInstanceReference)popupOnObject).getParentSocietyInstance().getParentSocietyType().getIntegrityStatus();

			mainPanel.showDialog(new ExceptionDialog(exc));
		}
	}

	public void exit()
	{
        if (changeProjectSubMenu != null)
			changeProjectSubMenu.exit();
	}
}
