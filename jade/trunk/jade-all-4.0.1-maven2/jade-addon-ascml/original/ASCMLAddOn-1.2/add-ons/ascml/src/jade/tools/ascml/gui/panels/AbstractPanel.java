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

import jade.tools.ascml.events.*;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.repository.Repository;
import jade.tools.ascml.repository.Project;
import jade.tools.ascml.repository.PropertyManager;
import jade.tools.ascml.gui.dialogs.ExceptionDialog;
import jade.tools.ascml.gui.dialogs.ProgressDialog;
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.absmodel.IRunnableSocietyInstance;
import jade.tools.ascml.model.runnable.RunnableSocietyInstance;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractPanel extends JPanel
		                            implements ProjectListener, PropertyListener, ModelChangedListener
{
	// ----------------- End of constant-declaration -----------------

	protected AbstractMainPanel mainPanel;
	public JFrame parentFrame; // this variable is explicitly set by main-subclass (only available in mainPanels).


	/**
	 * This abstract class should be implemented by any ASCML-panel cause it
	 * contains methods for throwing ModelActionEvents and accessing the repository
	 */
	public AbstractPanel(AbstractMainPanel mainPanel)
	{
		this();
		setMainPanel(mainPanel);
	}

	/**
	 * This constructor may only be used by AbstractMainPanel-class itself !!!
	 */
	public AbstractPanel()
	{
		this.setLayout(new BorderLayout());
		this.setBackground(Color.WHITE);
	}

	/**
	 * This constructor may only be used by AbstractMainPanel-class itself !!!
	 */
	protected void setMainPanel(AbstractMainPanel mainPanel)
	{
		this.mainPanel = mainPanel;
		getRepository().getListenerManager().addProjectListener(this);
		getRepository().getListenerManager().addPropertyListener(this);
		getRepository().getListenerManager().addModelChangedListener(this);
	}

	/**
	 * Panels, that might want to access the repository may call this method
	 * to have access to the global repository-instance.
	 * @return The global Repository-object.
	 */
	public Repository getRepository()
	{
		return mainPanel.getRepository();
	}

	/**
	 * Get the active Project-object. This is just a convenience-method cause the
	 * Project-object is also accessible from the Repository-object.
	 * @return  The active project.
	 */
	public Project getProject()
	{
		return getRepository().getProject();
	}

	/**
	 * Get the Properties-object. This is just a convenience-method cause the
	 * Properties-object is also accessible from the Repository-object.
	 * @return  The Repository-Properties.
	 */
	public PropertyManager getProperties()
	{
		return getRepository().getProperties();
	}

	/**
	 * This method may be called by ASCML-gui-objects like panels, components, and dialogs
	 * to inform the modelActionListeners about a new ModelActionEvent.
	 * For example, once an agentType shall be startet, the panel, which took the initial
	 * user-input, has to create an appropiate ModelActionEvent and call this method, the
	 * event is then passed on to the handler-objects.
	 * If a handler fails by executing the model-action a ModelActionException is thrown
	 * and presented to the user. The panel or dialog responsible for presenting and handling
	 * the error-message may be passed as an argument to this method
	 * @param mae  The ModelActionEvent, that is passed to the action-handlers
	 */
	public void throwModelActionEvent(final ModelActionEvent mae)
	{
		Vector listenerVector = getRepository().getListenerManager().getModelActionListener();
        ModelActionListener[] modelActionListener = new ModelActionListener[listenerVector.size()];
        listenerVector.toArray(modelActionListener);

		for (int i=0; i < modelActionListener.length; i++)
		{
			try
			{
				// toDO: make sure, that progressDialog is presented only once in case more than one actionlistener registered
                if (mae.getActionCommand() == ModelActionEvent.CMD_START_SOCIETYINSTANCE)
				{
					// Count all the agents, that should be started locally
					ISocietyInstance societyInstance = ((ISocietyInstance)((RunnableSocietyInstance)mae.getModel()).getParentModel());
					// int agentCount = societyInstance.getLocalAgentInstanceCount();
					int agentCount = ((IRunnableSocietyInstance)mae.getModel()).getLocalAgentInstanceCount();
					// String name = societyInstance.getName();
					String name = ((RunnableSocietyInstance)mae.getModel()).getName();
                    if (agentCount > 0)
					{
						ProgressDialog dialog = new ProgressDialog(getRepository(), agentCount, "Starting '"+name+"'...", "Starting, please wait ...");
						dialog.showDialog(mainPanel.parentFrame);
					}

					boolean callInEventThread = false; // should be true for local starting of many many agents, false otherwise
                    System.err.println("AbstractPanel.throwModelActionEvent: Call START in event-Thread = " + callInEventThread);

                    if (callInEventThread)
                    {
                        modelActionListener[i].modelActionPerformed(mae);
                        getRepository().throwProgressUpdateEvent(new ProgressUpdateEvent("Finished ... ", ProgressUpdateEvent.PROGRESS_FINISHED));
                    }
                    else
                    {
                        // System.err.println("AbstractPanel: throw LongTimeActionStartEvent");
						LongTimeActionStartEvent event = new LongTimeActionStartEvent(LongTimeActionStartEvent.ACTION_START_SOCIETYINSTANCE);
						event.addParameter("listener", modelActionListener[i]);
						event.addParameter("event", mae);
						getRepository().throwLongTimeActionStartEvent(event);
                    }
				}
                else if (mae.getActionCommand() == ModelActionEvent.CMD_STOP_SOCIETYINSTANCE)
				{
                    // ((ModelActionListener)modelActionListener.elementAt(i)).modelActionPerformed(mae);
                    // Count all the agents, that should be stopped
					ISocietyInstance societyInstance = ((ISocietyInstance)((RunnableSocietyInstance)mae.getModel()).getParentModel());
					String name = societyInstance.getName();

                    // ProgressDialog dialog = new ProgressDialog(getRepository(), "Stopping '"+name+"'...", "Stopping, please wait ...");
                    // dialog.showDialog(mainPanel.parentFrame);

					boolean callInEventThread = true; // should be true for local stopping of many many agents, false otherwise
                    System.err.println("AbstractPanel.throwModelActionEvent: Call STOP in event-Thread = " + callInEventThread);

                    if (callInEventThread)
                    {
                        modelActionListener[i].modelActionPerformed(mae);
                        getRepository().throwProgressUpdateEvent(new ProgressUpdateEvent("Finished ... ", ProgressUpdateEvent.PROGRESS_FINISHED));
                    }
                    else
                    {
                        LongTimeActionStartEvent event = new LongTimeActionStartEvent(LongTimeActionStartEvent.ACTION_STOP_SOCIETYINSTANCE);
                        event.addParameter("listener", modelActionListener[i]);
                        event.addParameter("event", mae);
                        getRepository().throwLongTimeActionStartEvent(event);
                    }
				}
				else
				{
					modelActionListener[i].modelActionPerformed(mae);
				}
			}
			catch(ModelActionException exc)
			{
				mainPanel.showDialog(new ExceptionDialog(exc));
			}
		}
	}

	public void projectChanged(ProjectChangedEvent event)
	{
		// may be overwritten by subclasses
	}

	public void propertiesChanged(PropertyChangedEvent event)
	{
		// may be overwritten by subclasses
	}

	public void modelChanged(ModelChangedEvent event)
	{
		// may be overwritten by subclasses
	}

	public void exit()
	{
		// deregister as listener
		getRepository().getListenerManager().removeProjectListener(this);
        getRepository().getListenerManager().removePropertyListener(this);
		getRepository().getListenerManager().removeModelChangedListener(this);
	}
}
