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


package jade.tools.ascml.gui;

import jade.tools.ascml.gui.components.MenuBar;
import jade.tools.ascml.gui.panels.ApplicationMainPanel;
import jade.tools.ascml.gui.panels.AbstractMainPanel;
import jade.tools.ascml.gui.dialogs.ExceptionDialog;
import jade.tools.ascml.gui.dialogs.PropertyFileChooseDialog;
import jade.tools.ascml.gui.dialogs.ModelNotFoundDialog;
import jade.tools.ascml.events.ExceptionListener;
import jade.tools.ascml.events.ExceptionEvent;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.exceptions.ASCMLException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;

import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
/**
 *
 */
public class GUI extends WindowAdapter implements ExceptionListener
{

	/** The repository is needed to register the appropiate GUI-elements as listeners for
	 * repository-changes. */
	private Repository repository;

	private JFrame applicationFrame;
	private AbstractMainPanel applicationMainPanel;
	private AbstractMainPanel appletMainPanel;

	private MenuBar menuBar;

	/**
	 * Use this constructor to create all neccessary GUI-classes.
	 */
	public GUI(Repository repository)
	{
		// System.err.println("GUI(): initializing GUI ...");
		this.repository = repository;
        repository.getListenerManager().addExceptionListener(this);
		// System.err.println("GUI(): initializing GUI ... finished");
	}

	/**
	 * By calling this method, all GUI-objects needed for displaying
	 * the ASCML as an application are instantiated, initialized and
	 * brought on the user's screen.
	 * When the ASCML should (also) be shown as an applet, please call the
	 * showAppletGUI()-method.
	 * @param title  the title for the frame
	 */
	public void showMainApplicationGUI(String title)
	{
		// System.err.println("GUI.showMainApplicationGUI: set application frame ... ");
		applicationFrame = new JFrame(title + " - Agent Society Configuration Manager and Launcher");

		applicationFrame.setSize(790, 595);
		applicationFrame.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-applicationFrame.getWidth()/2),
				          (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-applicationFrame.getHeight()/2));

		applicationFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		applicationFrame.addWindowListener(this);
		applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// now set the Application-Main-Panel
		// System.err.println("GUI.showMainApplicationGUI: set application main-panel ...");

		applicationMainPanel = new ApplicationMainPanel(applicationFrame, repository);

		applicationFrame.setContentPane(applicationMainPanel);
		menuBar = new MenuBar(applicationMainPanel);
        applicationFrame.setJMenuBar(menuBar);

		applicationFrame.setIconImage(ImageIconLoader.createImageIcon(ImageIconLoader.ASCML_FRAME_ICON).getImage());
		applicationFrame.setVisible(true);
		applicationFrame.toFront();
		applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		System.out.println("GUI ready.");
	}

	/**
	 * By calling this method, all GUI-objects needed for displaying
	 * the ASCML as an applet are instantiated, initialized and
	 * showed on the user's screen.
	 * When the ASCML should (also) be shown as an application, please call the
	 * showApplicationGUI()-method.
	 */
	public void showAppletGUI()
	{
		// toDo: implement me !!!
		// appletMainPanel = new AppletMainWindow(modelActionListener);
		appletMainPanel = null;
	}

	public void exceptionThrown(ExceptionEvent event)
	{
		JFrame exceptionFrame = new JFrame("An Exception occurred ...");

		exceptionFrame.setSize(600, 400);
		exceptionFrame.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2-exceptionFrame.getWidth()/2),
				          (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2-exceptionFrame.getHeight()/2));

		ASCMLException exc = event.getException();

		// ... maybe a special handling of the exception-situation is necessary
		boolean specialHandlingTookPlace = false;
		if (exc instanceof ResourceNotFoundException)
		{
			if (exc.getErrorCode() == ResourceNotFoundException.PROPERTIES_NOT_FOUND)
			{
				PropertyFileChooseDialog dialog = new PropertyFileChooseDialog(repository);
				dialog.showDialog(exceptionFrame);
				specialHandlingTookPlace = true;
			}
			else if ((exc.getErrorCode() == ResourceNotFoundException.SOCIETYTYPE_MODEL_NOT_FOUND) ||
			         (exc.getErrorCode() == ResourceNotFoundException.AGENTTYPE_MODEL_NOT_FOUND))
			{
				ModelNotFoundDialog dialog = new ModelNotFoundDialog(repository, (String)exc.getUserObject(), (exc.getErrorCode() == ResourceNotFoundException.AGENTTYPE_MODEL_NOT_FOUND));
				dialog.showDialog(exceptionFrame);
				specialHandlingTookPlace = true;
			}
		}

		if (!specialHandlingTookPlace)
		{
			ExceptionDialog exceptionDialog = new ExceptionDialog(exc);
			exceptionDialog.showDialog(exceptionFrame);
		}

		exceptionFrame.setVisible(false);
		exceptionFrame.dispose();
	}

	/**
	 * Call this method before the ASCML is going to shut down.
	 * This method will dispose the GUI-objects and remove them from the user's screen.
	 */
	public void exit()
	{
		if (applicationFrame != null)
		{
			applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			menuBar.exit();
			ApplicationMainPanel mainPanel = (ApplicationMainPanel)applicationFrame.getContentPane();
			mainPanel.exit();
			applicationFrame.setVisible(false);
			applicationFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			applicationFrame.dispose();
		}
		if (appletMainPanel != null)
			appletMainPanel.exit();
	}

	/**
	 * Called, when user presses the x on the top-right edge of the frame in order to close the ascml
	 * @param e
	 */
	public void windowClosing(WindowEvent e)
	{
		ApplicationMainPanel mainPanel = (ApplicationMainPanel)applicationFrame.getContentPane();
		mainPanel.showDialog(AbstractMainPanel.EXIT_DIALOG);
	}
}
