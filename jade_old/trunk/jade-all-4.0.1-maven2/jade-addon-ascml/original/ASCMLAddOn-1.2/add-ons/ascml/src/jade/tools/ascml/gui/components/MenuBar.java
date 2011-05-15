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

import jade.tools.ascml.gui.panels.AbstractMainPanel;
import jade.tools.ascml.gui.panels.About;
import jade.tools.ascml.repository.PropertyManager;

import javax.swing.*;
import java.awt.event.*;

public class MenuBar extends JMenuBar implements ActionListener
{
	private JMenu fileMenu;
	private JMenu projectMenu;
	private JMenu helpMenu;

	private JMenuItem createProjectItem;
	private ProjectMenu changeProjectSubMenu;
	private JMenuItem exitItem;
	private JMenuItem aboutItem;
	private JMenuItem changesItem;
	private JMenuItem helpItem;
	private JMenuItem crossPlatformLookAndFeelItem;
	private JMenuItem systemLookAndFeelItem;
	private JMenuItem gtkLookAndFeelItem;
	private JMenuItem metalLookAndFeelItem;
	private JMenuItem windowsLookAndFeelItem;
	private JMenuItem motifLookAndFeelItem;
	private JMenuItem changeOptionsItem;
	private JMenuItem addAgentTypeItem;
	private JMenuItem addSocietyItem;
	private JMenuItem createAgentItem;
	private JMenuItem createSocietyItem;
	private JMenuItem autoSearchItem;

	private AbstractMainPanel parentPanel;
	/**
	 * A constructor like this should be implemented by any ASCML-gui-component, cause a component
	 * might want to have access to the repository-options or the methods implemented in the parentPanel,
	 * therefore this parentPanel should be passed to this constructor.
	 * @param parentPanel  The panel, which contains this component.
	 * It is needed to have access to the methods for throwing modelActionEvents, as well as to the
	 * repository-objects.
	 */
	public MenuBar(AbstractMainPanel parentPanel)
	{
		this.parentPanel = parentPanel;

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);		
		this.add(fileMenu);

		changeProjectSubMenu = new ProjectMenu(parentPanel);
		fileMenu.add(changeProjectSubMenu);

		createProjectItem = new JMenuItem("Create new Project ...");
		createProjectItem.addActionListener(this);
        fileMenu.add(createProjectItem);

		fileMenu.addSeparator();
		
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(this);

		fileMenu.add(exitItem);

// -------------- End of File-Menu ---------------
		
		changeOptionsItem = new JMenuItem("Change Options");
		changeOptionsItem.addActionListener(this);
		addAgentTypeItem = new JMenuItem("Load an AgentType ...");
		addAgentTypeItem.addActionListener(this);
		addSocietyItem = new JMenuItem("Load a SocietyType ...");
		addSocietyItem.addActionListener(this);
		autoSearchItem = new JMenuItem("Auto-Search for Agents/Societies");
		autoSearchItem.addActionListener(this);
		createSocietyItem = new JMenuItem("Create new SocietyType");
		createSocietyItem.addActionListener(this);
        createAgentItem = new JMenuItem("Create new AgentType");
		createAgentItem.addActionListener(this);

		projectMenu = new JMenu("Project");
		projectMenu.setMnemonic(KeyEvent.VK_P);

		projectMenu.add(createSocietyItem);
		projectMenu.add(createAgentItem);
		projectMenu.addSeparator();
		projectMenu.add(addSocietyItem);
		projectMenu.add(addAgentTypeItem);
		projectMenu.add(autoSearchItem);
		projectMenu.addSeparator();
		projectMenu.add(new ViewMenu(parentPanel));
		projectMenu.add(changeOptionsItem);

		this.add(projectMenu);
		
// -------------- End of Repository-Menu ---------------

		this.add(Box.createHorizontalStrut(20));
		
		aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(this);
		changesItem = new JMenuItem("View ChangeLog");
		changesItem.addActionListener(this);
		helpItem = new JMenuItem("Help");
		helpItem.addActionListener(this);

		crossPlatformLookAndFeelItem = new JMenuItem("Cross-platform");
		crossPlatformLookAndFeelItem.addActionListener(this);

		systemLookAndFeelItem = new JMenuItem("System");
		systemLookAndFeelItem.addActionListener(this);

		gtkLookAndFeelItem = new JMenuItem("GTK+");
		gtkLookAndFeelItem.addActionListener(this);

		metalLookAndFeelItem = new JMenuItem("Java (Metal)");
		metalLookAndFeelItem.addActionListener(this);

		windowsLookAndFeelItem = new JMenuItem("Windows");
		windowsLookAndFeelItem.addActionListener(this);

        motifLookAndFeelItem = new JMenuItem("Motif");
		motifLookAndFeelItem.addActionListener(this);

		JMenu lookAndFeelMenu = new JMenu("Change Look & Feel");

		lookAndFeelMenu.add(systemLookAndFeelItem);
		lookAndFeelMenu.add(crossPlatformLookAndFeelItem);
		lookAndFeelMenu.add(metalLookAndFeelItem);
		lookAndFeelMenu.add(windowsLookAndFeelItem);
		lookAndFeelMenu.add(gtkLookAndFeelItem);
		lookAndFeelMenu.add(motifLookAndFeelItem);

		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.add(lookAndFeelMenu);
        helpMenu.add(changesItem);
		helpMenu.add(helpItem);
		helpMenu.add(aboutItem);
		this.add(helpMenu);
	}

	/**
	 * This method has to be implemented because of the ActionListener-interface.
	 * If one of the menuItems is chosen by the user, the event is handled here.
	 * @param event The actionEvent, that occured.
	 */
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		if (source == exitItem)
		{
            parentPanel.showDialog(AbstractMainPanel.EXIT_DIALOG);
		}
		else if (source == aboutItem)
		{
			parentPanel.setContentPanel(TabbedPaneManager.REPOSITORY_PANE, TabbedPaneManager.ABOUT_TAB);
		}
		else if (source == changesItem)
		{
			parentPanel.setContentPanel(TabbedPaneManager.REPOSITORY_PANE, TabbedPaneManager.CHANGES_TAB);
		}
		else if (source == helpItem)
		{
			parentPanel.setContentPanel(TabbedPaneManager.REPOSITORY_PANE, TabbedPaneManager.HELP_TAB);
		}
		else if (source == systemLookAndFeelItem)
		{
			parentPanel.setLookAndFeel(AbstractMainPanel.SYSTEM_LOOK_AND_FEEL);
		}
		else if (source == crossPlatformLookAndFeelItem)
		{
			parentPanel.setLookAndFeel(AbstractMainPanel.CROSS_PLATFORM_LOOK_AND_FEEL);
		}
		else if (source == gtkLookAndFeelItem)
		{
			parentPanel.setLookAndFeel(AbstractMainPanel.GTK_LOOK_AND_FEEL);
		}
		else if (source == windowsLookAndFeelItem)
		{
			parentPanel.setLookAndFeel(AbstractMainPanel.WINDOWS_LOOK_AND_FEEL);
		}
		else if (source == motifLookAndFeelItem)
		{
			parentPanel.setLookAndFeel(AbstractMainPanel.MOTIF_LOOK_AND_FEEL);
		}
		else if (source == metalLookAndFeelItem)
		{
			parentPanel.setLookAndFeel(AbstractMainPanel.METAL_LOOK_AND_FEEL);
		}
		else if (source == changeOptionsItem)
		{
            parentPanel.setContentPanel(TabbedPaneManager.REPOSITORY_PANE, TabbedPaneManager.OPTIONS_TAB);
		}
		else if (source == addAgentTypeItem)
		{
            parentPanel.showDialog(AbstractMainPanel.ADD_AGENTTYPE_DIALOG);
		}
		else if (source == createAgentItem)
		{
            parentPanel.getRepository().getProject().createAgentType();
		}
		else if (source == addSocietyItem)
		{
            parentPanel.showDialog(AbstractMainPanel.ADD_SOCIETYTYPE_DIALOG);
		}
		else if (source == createSocietyItem)
		{
            parentPanel.getRepository().getProject().createSocietyType();
		}
		else if (source == autoSearchItem)
		{
            parentPanel.setContentPanel(TabbedPaneManager.REPOSITORY_PANE, TabbedPaneManager.AUTOSEARCH_TAB);
		}
		else if (source == createProjectItem)
		{
            parentPanel.showDialog(AbstractMainPanel.ADD_PROJECT_DIALOG);
		}
		else
			System.err.println("WARNING: MenuBar: action-source unknown !");
	}

	public void exit()
	{
        changeProjectSubMenu.exit();
	}
}