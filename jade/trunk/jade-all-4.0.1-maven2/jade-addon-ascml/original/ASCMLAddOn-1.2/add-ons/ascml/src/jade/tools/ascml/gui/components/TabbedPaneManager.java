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


package jade.tools.ascml.gui.components;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.Vector;

import jade.tools.ascml.gui.panels.*;
import jade.tools.ascml.gui.components.tree.RepositoryTree;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.absmodel.*;

public class TabbedPaneManager extends JTabbedPane implements ChangeListener
{
	public final static String REPOSITORY_PANE	= "repository_pane";
	public final static String LOAD_PANE		= "load_pane";
	public final static String ERROR_PANE		= "error_pane";

	public final static int LOAD_SOCIETY_TAB	= 0; // integer used as pane-index
	public final static int LOAD_AGENT_TAB		= 1;

	public final static int OPTIONS_TAB		= 0; // integer used as pane-index
	public final static int AUTOSEARCH_TAB	= 1;
	public final static int HELP_TAB		= 2;
	public final static int CHANGES_TAB		= 3;
	public final static int ABOUT_TAB		= 4;

	private AbstractMainPanel mainPanel;

	/**
	 * The constructor initializes the pane to view, by default, this is the Repository-Pane
	 * @param mainPanel  The mainPanel of the application.
	 */
	public TabbedPaneManager(AbstractMainPanel mainPanel)
	{
		super();
		this.mainPanel = mainPanel;

		addChangeListener(this);
		this.setBackground(Color.WHITE);
		this.setOpaque(true);
		
		setRepositoryPanes();
	}
	
	private void addTab(String title, Icon icon, JPanel component, String tip)
	{
		super.addTab(title, icon, component, tip);
	}

    private void finalizeOldTabs()
	{
        // finalize all tabs currently in view
		Component[] components = this.getComponents();
		for (int i=0; i < components.length; i++)
		{
			// components may be AbstractPanels or simple JPanels, in case of AbstractPanels,
			// their exit()-method has to be called, to deregister them as listeners for several objects.
			if (components[i] instanceof AbstractPanel)
			{
				((AbstractPanel)components[i]).exit();
			}
		}
		this.removeAll();
	}

	private void setAgentTypePanes(IAgentType model)
	{
		finalizeOldTabs();
		this.addTab("General Settings", ImageIconLoader.createImageIcon(ImageIconLoader.AGENTTYPE, 16, 16), new AgentTypeGeneral(mainPanel, model), "General information about this agentType");
		this.addTab("Parameter", ImageIconLoader.createImageIcon(ImageIconLoader.AGENTTYPE, 16, 16), new ParameterOverview(mainPanel, model), "Parameters specified for this agentType");
		// this.addTab("Parameter-Sets", null, new ParameterSet(mainPanel, model), "Parameter-Sets specified for this agentType");
		
		this.setSelectedIndex(0);
	}

	private void setAgentInstancePanes(IAgentInstance model)
	{
		finalizeOldTabs();
		this.addTab("General Settings", ImageIconLoader.createImageIcon(ImageIconLoader.AGENTINSTANCE, 16, 16), new AgentInstanceGeneral(mainPanel, model, model.getParentSocietyInstance()), "General information about this agentType");
		this.addTab("Parameter", ImageIconLoader.createImageIcon(ImageIconLoader.AGENTINSTANCE, 16, 16), new ParameterOverview(mainPanel, model), "Parameters specified for this agentType");
		// this.addTab("Parameter-Sets", null, new ParameterSet(mainPanel, model), "Parameter-Sets specified for this agentType");
		this.addTab("Dependencies", ImageIconLoader.createImageIcon(ImageIconLoader.AGENTINSTANCE, 16, 16), new DependencyOverview(mainPanel, model), "Shows all dependencies definded for this reference");

		this.setSelectedIndex(0);
	}

	private void setSocietyTypePanes(ISocietyType model)
	{
		finalizeOldTabs();
		this.addTab("SocietyType Settings", ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYTYPE, 16, 16), new SocietyTypeGeneral(mainPanel, model), "General information about this societyType");

		/*
		ISocietyInstance[] socInstances = model.getSocietyInstances();
		for (int i=0; i < socInstances.length; i++)
		{
			this.addTab(socInstances[i].getName(), ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE, 16, 16), new SocietyInstanceGeneral(mainPanel, socInstances[i]), "General information about this societyInstance");
		}
        */
		this.setSelectedIndex(0);
	}

	private void setSocietyInstancePanes(ISocietyInstance model)
	{
		finalizeOldTabs();
		this.addTab("Main-Settings", ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE, 16, 16), new SocietyInstanceGeneral(mainPanel, model), "View and edit the main settings of this SocietyInstance");
		this.addTab("AgentInstances", ImageIconLoader.createImageIcon(ImageIconLoader.AGENTINSTANCE, 16, 16), new AgentInstanceGeneral(mainPanel, null, model), "View and edit all contained AgentInstances");
		this.addTab("SocietyInstance-references", ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE_REFERENCE_LOCAL, 16, 16), new SocietyInstanceReferenceGeneral(mainPanel, null, model), "View and edit the references to other SocietyInstances");
		// this.addTab("Functional", ImageIconLoader.createImageIcon(ImageIconLoader.AGENTINSTANCE, 16, 16), null, "View and edit the dependencies defining the functional-state");
		this.setSelectedIndex(0);
	}

	private void setSocietyInstanceReferencePanes(ISocietyInstanceReference model)
	{
		finalizeOldTabs();
		this.addTab("SocietyInstance-reference", ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE, 16, 16), new SocietyInstanceReferenceGeneral(mainPanel, model, model.getParentSocietyInstance()), "General information about the referenced societyinstance");
		this.addTab("Dependencies", ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYINSTANCE, 16, 16), new DependencyOverview(mainPanel,model), "Shows all dependencies definded for this reference");

		this.setSelectedIndex(0);
	}

	private void setLoadPanes()
	{
		finalizeOldTabs();
		this.addTab("Load a Society", ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYTYPE, 16, 16), new LoadSociety(mainPanel), "Load a Society from a datasource");
		this.addTab("Load an Agent", ImageIconLoader.createImageIcon(ImageIconLoader.AGENTTYPE, 16, 16), new LoadAgent(mainPanel), "Load an Agent from a datasource");
	}

	private void setRunnablePanes(IAbstractRunnable model)
	{
		finalizeOldTabs();
		this.addTab("Runnable Instance ("+model.getName()+")", ImageIconLoader.createImageIcon(ImageIconLoader.RUNNING_INSTANCES, 16, 16), new RunnableGeneral(mainPanel, model), "Show Details about a runnable Instance");
	}

	private void setRepositoryPanes()
	{
		finalizeOldTabs();
		this.addTab("Project-Options", null, new RepositoryOptions(mainPanel), "Change some Repository-options, paths for example");
		this.addTab("Auto-Search", null, new AutoSearch(mainPanel), "Automatically Search for Agent- and Society-files");
		this.addTab("Help", null, new Help(mainPanel), "Get help using the ASCML");
		this.addTab("Changes", null, new Changes(mainPanel), "View a summary of the last changes to the ASCML");
		this.addTab("About", null, new About(mainPanel), "ASCML-Version and -Contributers");

		this.setSelectedIndex(4);
	}

	public void setObjectToView(Object model)
	{
		if(model instanceof IAgentType)
		{
			setAgentTypePanes((IAgentType)model);
		}
		if(model instanceof IAgentInstance)
		{
			setAgentInstancePanes((IAgentInstance)model);
		}
		else if(model instanceof IAbstractRunnable)
		{
			setRunnablePanes((IAbstractRunnable)model);
		}
		else if(model instanceof ISocietyType)
		{
			setSocietyTypePanes((ISocietyType)model);
		}
		else if(model instanceof ISocietyInstance)
		{
			setSocietyInstancePanes((ISocietyInstance)model);
		}
		else if(model instanceof ISocietyInstanceReference)
		{
			setSocietyInstanceReferencePanes((ISocietyInstanceReference)model);
		}
		else if (model instanceof String)
		{
			// if the model is an instance of String, the node containing
			// the model is a static node, a node which belongs to the tree,
			// and has nothing more to do, than to create different hierarchy-levels.
            // when the user selects such a node, a 'related' panel should be shown
			if (((String)model).startsWith(RepositoryTree.REPOSITORY_STRING))
			{
				setPane(REPOSITORY_PANE, OPTIONS_TAB);
			}
			else if (model.equals(RepositoryTree.ADD_A_MODEL_HEADER))
			{
				setPane(REPOSITORY_PANE, AUTOSEARCH_TAB);
			}
			else if (model.equals(RepositoryTree.ADD_AGENTMODEL_STRING) || model.equals(RepositoryTree.AGENTTYPES_STRING))
			{
				setPane(LOAD_PANE, LOAD_AGENT_TAB);
			}
			else if (model.equals(RepositoryTree.ADD_SOCIETYMODEL_STRING) || model.equals(RepositoryTree.SOCIETYTYPES_STRING))
			{
				setPane(LOAD_PANE, LOAD_SOCIETY_TAB);
			}
		}

		for (int i=0; i < this.getTabCount(); i++)
		{
			this.setBackgroundAt(i, new Color(255, 255, 255));
		}
	}

	/**
	 * Set a whole pane (consisting of multiple tabs).
	 *
	 * @param paneIdentifier  The String-identifier of the pane to view
	 * This should be one of the following constants: REPOSITORY, ERROR.
	 * @param tabIdentifier  Either 0, if the default-tab should be viewed,
	 * or the int-identifier of the tab to view.
	 * This should be one of the following constants: OPTIONS_TAB, AUTOSEARCH_TAB, HELP_TAB, ABOUT_TAB
	 */
	public void setPane(String paneIdentifier, int tabIdentifier)
	{
		if (paneIdentifier.equals(REPOSITORY_PANE))
		{
			setRepositoryPanes();
			setTabToView(tabIdentifier);
		}
		else if (paneIdentifier.equals(LOAD_PANE))
		{
			setLoadPanes();
			setTabToView(tabIdentifier);
		}
		else if (paneIdentifier.equals(ERROR_PANE))
		{
			// toDO: implement me !!! Think of errors/exceptions may be listed each in its own tab ?
		}
	}

	/**
	 * Set the tab, that should be viewed in the pane.
	 *
	 * @param tabIdentifier  Either 0, if the default-tab should be viewed,
	 * or the int-identifier of the tab to view.
	 * This should be one of the following constants: OPTIONS_TAB, AUTOSEARCH_TAB, HELP_TAB, ABOUT_TAB
	 */
	public void setTabToView(int tabIdentifier)
	{
        this.setSelectedIndex(tabIdentifier);
	}

	public void stateChanged(ChangeEvent evt)
	{
		/*System.err.println("TabbedPaneManager: state changed, selected index=" + getSelectedIndex());
		for (int i=0; i < getTabCount(); i++)
		{
			setBackgroundAt(i, Color.YELLOW);
		}
		if (getSelectedIndex() != -1)
			setBackgroundAt(getSelectedIndex(), Color.GREEN);
		*/
	}

	public void exit()
	{
		finalizeOldTabs();
	}

}
