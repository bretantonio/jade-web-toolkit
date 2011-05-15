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


package jade.tools.ascml.repository;

import jade.tools.ascml.events.*;

import java.util.Vector;

/**
 * 
 */
public class ListenerManager
{
    private Vector propertyListener;
	private Vector projectListener;
	private Vector exceptionListener;
	private Vector modelChangedListener;
	private Vector modelActionListener;
	private Vector toolTakeDownListener;
	private Vector progressUpdateListener;
	private Vector longTimeActionStarter;

	public ListenerManager()
	{
		propertyListener		= new Vector();
		projectListener			= new Vector();
		exceptionListener		= new Vector();
		modelChangedListener	= new Vector();
		modelActionListener		= new Vector();
		toolTakeDownListener	= new Vector();
		progressUpdateListener	= new Vector();
		longTimeActionStarter	= new Vector();
	}

	/**
	 * Add a ProjectListener to the set of listeners.
	 * This listener will be informed in case some project-properties are changed.
	 * @param listener  ProjectListener-object that is informed in case of
	 * ProjectChangedEvents.
	 */
	public void addProjectListener(ProjectListener listener)
	{
		if (!this.projectListener.contains(listener))
			this.projectListener.add(listener);
	}

	/**
	 * Remove a ProjectListener from the set of listeners.
	 * @param listener  ProjectListener-object to remove
	 */
	public void removeProjectListener(ProjectListener listener)
	{
		if (this.projectListener.contains(listener))
			this.projectListener.remove(listener);
	}

	/**
	 * Get the Vector containing ProjectListener
	 * @return A Vector containing all the ProjectListener
	 */
	public Vector getProjectListener()
	{
		return projectListener;
	}

	/**
	 * Add an ExceptionListener to the set of listeners.
	 * This listener will be informed in case an exception occurs.
	 * @param listener  ExceptionListener-object that is informed in case of ExceptionEvents.
	 */
	public void addExceptionListener(ExceptionListener listener)
	{
		if (!this.exceptionListener.contains(listener))
			this.exceptionListener.add(listener);
	}

	/**
	 * Remove a ExceptionListener from the set of listeners.
	 * @param listener  ExceptionListener-object to remove
	 */
	public void removeExceptionListener(ExceptionListener listener)
	{
		if (this.exceptionListener.contains(listener))
			this.exceptionListener.remove(listener);
	}

	/**
	 * Get the Vector containing ExceptionListener
	 * @return A Vector containing all the ExceptionListener
	 */
	public Vector getExceptionListener()
	{
		return exceptionListener;
	}

	/**
	 * Add a RepositoryPropertyListener to the set of listeners.
	 * This listener will be informed in case some properties are changed.
	 * @param listener  RepositoryPropertyListener-object that is informed in case of
	 * PropertiesChangedEvents.
	 */
	public void addPropertyListener(PropertyListener listener)
	{
		if (!this.propertyListener.contains(listener))
			this.propertyListener.add(listener);
	}

	/**
	 * Remove a RepositoryPropertyListener from the set of listeners.
	 * @param listener  RepositoryPropertyListener-object to remove
	 */
	public void removePropertyListener(PropertyListener listener)
	{
		if (this.propertyListener.contains(listener))
			this.propertyListener.remove(listener);
	}

	/**
	 * Get the Vector containing PropertyListener
	 * @return A Vector containing all the PropertyListener
	 */
	public Vector getPropertyListener()
	{
		return propertyListener;
	}

	/**
	 * Add a RunnableModelListener to the set of listeners.
	 * This changedListener will be informed in case properties of a runnable-model are changed.
	 * @param changedListener  RunnableModelListener-object that is informed in case of
	 * RunnableModelChangedEvents.
	 */
	public void addModelChangedListener(ModelChangedListener changedListener)
	{
		if (!this.modelChangedListener.contains(changedListener))
			this.modelChangedListener.add(changedListener);
	}

	/**
	 * Remove a RunnableModelListener from the set of listeners.
	 * @param changedListener  RunnableModelListener-object to remove
	 */
	public void removeModelChangedListener(ModelChangedListener changedListener)
	{
		if (this.modelChangedListener.contains(changedListener))
			this.modelChangedListener.remove(changedListener);
	}

	/**
	 * Get all ModelChangedListeners.
	 * @return A Vector containing all objects, which want to be informed about ModelChanged-Events
	 */
	public Vector getModelChangedListener()
	{
		return modelChangedListener;
	}

	/**
	 * Add a ModelActionListener to the set of listeners.
	 * This actionListener will be informed in case properties of a ModelActionEvents.
	 * @param actionListener  ModelActionListener-object that is informed in case of
	 * ModelActionChangedEvents.
	 */
	public void addModelActionListener(ModelActionListener actionListener)
	{
		if (!this.modelActionListener.contains(actionListener))
			this.modelActionListener.add(actionListener);
	}

	/**
	 * Remove a ModelActionListener from the set of listeners.
	 * @param actionListener  ModelActionListener-object to remove
	 */
	public void removeModelActionListener(ModelActionListener actionListener)
	{
		if (this.modelActionListener.contains(actionListener))
			this.modelActionListener.remove(actionListener);
	}

	/**
	 * Get all ModelActionListeners.
	 * @return A Vector containing all objects, which want to be informed about ModelActionEvents
	 */
	public Vector getModelActionListener()
	{
		return modelActionListener;
	}

	/**
	 * Add a ToolTakeDownListener to the set of listeners.
	 * This toolTakeDownListener will be informed when the ASCML is going to shut down.
	 * @param toolTakeDownListener  ToolTakeDownListener-object that is informed when the ASCML shuts down
	 */
	public void addToolTakeDownListener(ToolTakeDownListener toolTakeDownListener)
	{
		if (!this.toolTakeDownListener.contains(toolTakeDownListener))
			this.toolTakeDownListener.add(toolTakeDownListener);
	}

	/**
	 * Remove a ToolTakeDownListener from the set of listeners.
	 * @param toolTakeDownListener  ToolTakeDownListener-object to remove
	 */
	public void removeToolTakeDownListener(ToolTakeDownListener toolTakeDownListener)
	{
		if (this.toolTakeDownListener.contains(toolTakeDownListener))
			this.toolTakeDownListener.remove(toolTakeDownListener);
	}

	/**
	 * Get all ToolTakeDownListeners.
	 * @return A Vector containing all objects, which want to be informed about ToolTakeDownEvents
	 */
	public Vector getToolTakeDownListener()
	{
		return toolTakeDownListener;
	}

	/**
	 * Add a ProgressUpdateListener to the set of listeners.
	 * This progressUpdateListener will be informed when a longsome progress advances
	 * @param progressUpdateListener  ProgressUpdateListener-object that is informed when the progress advances
	 */
	public void addProgressUpdateListener(ProgressUpdateListener progressUpdateListener)
	{
		if (!this.progressUpdateListener.contains(progressUpdateListener))
			this.progressUpdateListener.add(progressUpdateListener);
	}

	/**
	 * Remove a ProgressUpdateListener from the set of listeners.
	 * @param progressUpdateListener  ProgressUpdateListener-object to remove
	 */
	public void removeProgressUpdateListener(ProgressUpdateListener progressUpdateListener)
	{
		if (this.progressUpdateListener.contains(progressUpdateListener))
			this.progressUpdateListener.remove(progressUpdateListener);
	}

	/**
	 * Get all ProgressUpdateListener.
	 * @return A Vector containing all objects, which want to be informed about ProgressUpdateEvents
	 */
	public Vector getProgressUpdateListener()
	{
		return progressUpdateListener;
	}

	public void addLongTimeActionStartListener(LongTimeActionStartListener actionStarter)
	{
		if (!this.longTimeActionStarter.contains(actionStarter))
			this.longTimeActionStarter.add(actionStarter);
	}

	public void removeLongTimeActionStartListener(LongTimeActionStartListener actionStarter)
	{
		if (this.longTimeActionStarter.contains(actionStarter))
			this.longTimeActionStarter.remove(actionStarter);
	}

	public Vector getLongTimeActionStartListener()
	{
        return longTimeActionStarter;
	}

	public void exit()
	{
		
	}
}
