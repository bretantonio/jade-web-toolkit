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

import jade.tools.ascml.repository.loader.ModelIndex;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.exceptions.ASCMLException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.events.*;
import jade.tools.ascml.model.runnable.RunnableSocietyInstance;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.onto.Known;
import jade.tools.ascml.absmodel.*;

import java.util.Vector;

public class Repository
{
	private ModelManager modelManager;
	private PropertyManager propertyManager;
	private ListenerManager listenerManager;
	private RunnableManager runnableManager;

	public Repository()
	{
		this.listenerManager = new ListenerManager();
		this.runnableManager = new RunnableManager(this);
	}

	public void init(String propertyLocation)
	{
//		System.err.println("Repository(): initializing Properties ... '" + propertyLocation + "'");
		// initialize properties
		propertyManager = new PropertyManager(this);
		propertyManager.setSource(propertyLocation);
		propertyManager.readProperties();

		try
		{
			// The modelmanager has to be instantiated after the properties have been loaded, because it needs
			// information about the ModelFactory to use
			modelManager = new ModelManager(this);
			propertyManager.setModelManager(modelManager);
		}
		catch (ResourceNotFoundException exc)
		{
			System.err.println("Repository.init: propertyfile '" + propertyLocation + "' not found.");
			throwExceptionEvent(exc);
		}

		// now initialize all projects

		String[] projectNames = propertyManager.getProjectNames();
		for (int i=0; i < projectNames.length; i++)
		{
			Project oneProject = propertyManager.getProject(projectNames[i]);
			if ((oneProject != null) && !oneProject.isInitialized())  // returnProject == null, if no propertyFile has been found
			{
				// System.err.println("Repository.initProperties: Project "+oneProject+" has not been initialized yet !!!");
				try
				{
					oneProject.init(modelManager);
				}
				catch(ModelException me)
				{
					throwExceptionEvent(me);
				}
			}
		}
		System.out.println("Repository ready.");
	}

	/** Interface-methods used by the AgentLauncher **/
	public IAgentType getAgentType(String name, boolean throwException) throws ModelException
	{
	    // try
		// {
			return (IAgentType)getModelIndex().getModel(name);
		/*}
		catch(ModelException e)
		{
			if (throwException)
			{
				throwExceptionEvent(e);
				throw e;
			}
		}
		catch(ResourceNotFoundException e)
		{
			if (throwException)
				throwExceptionEvent(e);
		}
		return null;*/
	}

	public ISocietyType getSocietyType(String name, boolean throwException) throws ModelException
	{
		// try
		// {
			return (ISocietyType)getModelIndex().getModel(name);
		/*}
		catch(ModelException e)
		{
			if (throwException)
			{
				throwExceptionEvent(e);
				throw e;
			}
		}
		catch(ResourceNotFoundException e)
		{
			if (throwException)
				throwExceptionEvent(e);
		}
		return null;
		*/
	}

	/**
	 *	Get a SocietyInstance by it's fully qualified name.
	 *  The fully qualified name has to have the following format: 'fullyQualifiedSocietyType.instanceName'.
	 *  @param  fullyQualifiedInstanceName Type and name of the agentInstance to create
	 *  @return  A runnable agentInstance.
	 *  @throws  ModelException  if either the agentType could not be found,
	 *                           no runnable instance could be created
	 *                           or the name is not fully qualified
	 */
	public ISocietyInstance getSocietyInstance(String fullyQualifiedInstanceName) throws ModelException
	{
		// System.err.println("Repository.getSocietyInstance: " + fullyQualifiedInstanceName);
		String typeName = fullyQualifiedInstanceName.substring(0, fullyQualifiedInstanceName.lastIndexOf('.'));
		String instanceName = fullyQualifiedInstanceName.substring(fullyQualifiedInstanceName.lastIndexOf('.')+1, fullyQualifiedInstanceName.length());

		ISocietyType societyType = getSocietyType(typeName, false);
		if (societyType == null)
		{
			// societytype couldn't be found
			// maybe it's not fully qualified, so look for societytypes with
			// the given name, even if it's not fully qualified.

			ModelIndex modelIndex = getModelIndex();
			ISocietyType[] societies = modelIndex.getSocietyTypeObjects();
			for (int i=0; i < societies.length; i++)
			{
				String societyTypeName = societies[i].getName();
				if (societyTypeName.indexOf(typeName) > 0)
				{
					societyType = societies[i];
					i = Integer.MAX_VALUE - 1; // break out of for-loop
				}
			}

			if (societyType == null)
			{
				System.err.println(getModelIndex());
				throw new ModelException("Remote request couldn't be processed. Reference not found: type=" + typeName + ", instance=" + instanceName + " not found.", "There has been a remote request to start a societyinstance. The societytype, that should contain this instance could not be found in the repository. Please check the spelling and make sure, that the societytype is loaded into the repository.");
			}
		}

		// societytype has been found, now get the societyinstance
		ISocietyInstance societyInstance = societyType.getSocietyInstance(instanceName);
		if (societyInstance == null)
			throw new ModelException("Remote request couldn't be processed. SocietyInstance named " + instanceName + " not found.", "There has been a remote request to start a societyinstance. The societytype has been found, but it doesn't contains the requested societyinstance. Please check the spelling of the societyinstance-name.");

		return societyInstance;
	}

	/**
	 *	Get a runnable societyInstance by it's fully qualified name.
	 *  The fully qualified name has to have the following format: 'fullyQualifiedSocietyInstance.instanceName'.
	 *  For example: examples.party.PartySociety.ToolTime-Party.MyBigParty creates a
	 *  RunnableSocietyInstance-object named 'MyBigParty'.
	 *  MyBigParty has the ToolTime-Party as a SocietyInstance-template, which itself is defined in
	 *  the societyType 'examples.party.PartySociety'.
	 *  @param  fullyQualifiedInstanceName SocietyType-, SocietyInstance and the instance-name of the societyInstance to create
	 *  @return  A runnable societyInstance.
	 *  @throws  ModelException  if either the societyType could not be found,
	 *                           the societyInstance is not declared in the societyType,
	 *                           no runnable societyInstance could be created
	 *                           or the name is not fully qualified.
	 */
	public RunnableSocietyInstance createRunnableSocietyInstance(String fullyQualifiedInstanceName) throws ModelException
	{
		// System.err.println("Repository.createRunnableSocietyInstance: WARNING, maybe > 1 runnableModel has been created, but only 1st is returned.");
		String socInstName = fullyQualifiedInstanceName.substring(0, fullyQualifiedInstanceName.lastIndexOf('.'));
		String runnableSocInstName = fullyQualifiedInstanceName.substring(fullyQualifiedInstanceName.lastIndexOf('.')+1, fullyQualifiedInstanceName.length());

		try
		{
			ISocietyInstance socInst = getSocietyInstance(socInstName);
			IAbstractRunnable[] runnableInstance = (IAbstractRunnable[])getRunnableManager().createRunnable(runnableSocInstName, socInst, 1);
			return (RunnableSocietyInstance)runnableInstance[0];
		}
		catch(ModelException me)
		{
			// show the exception and then throw it
			throwExceptionEvent(me);
			throw me;
		}
	}

	/**
	 *	Get a runnable agentInstance by it's fully qualified name.
	 *  The fully qualified name has to have the following format: 'fullyQualifiedAgentType.instanceName'.
	 *  For example: examples.party.Host.Erna creates a RunnableAgentInstance-object named 'Erna'
	 *  out of the agentType-template 'examples.party.Host'.
	 *  @param  fullyQualifiedInstanceName  AgentType- and instance-name of the agentInstance to create
	 *  @return  A runnable agentInstance.
	 *  @throws  ModelException  if either the agentType could not be found,
	 *                           no runnable agentInstance could be created
	 *                           or the name is not fully qualified.
	 */
	public IRunnableAgentInstance[] createRunnableAgentInstance(String fullyQualifiedInstanceName, int modelCount) throws ModelException
	{
		// System.err.println("Repository.createRunnableAgentInstance: WARNING, maybe > 1 runnableModel has been created, but only 1st is returned.");
		String agentTypeName = fullyQualifiedInstanceName.substring(0, fullyQualifiedInstanceName.lastIndexOf('.'));
		String runnableAgentInstanceName = fullyQualifiedInstanceName.substring(fullyQualifiedInstanceName.lastIndexOf('.')+1, fullyQualifiedInstanceName.length());

		IAgentType agentType = getAgentType(agentTypeName, true);
		IAbstractRunnable[] abstractRunnables = getRunnableManager().createRunnable(runnableAgentInstanceName, agentType, modelCount);

		IRunnableAgentInstance[] runnableInstances = new IRunnableAgentInstance[abstractRunnables.length];
		for (int i=0; i < runnableInstances.length; i++)
		{
			runnableInstances[i] = (IRunnableAgentInstance)abstractRunnables[i];
		}
		return runnableInstances;
	}

	public Status getRunnableStatus(String fullyQualifiedName)
	{
		//System.err.println("Repository: got a status request for: " + fullyQualifiedName);
		IAbstractRunnable runnable = getRunnableManager().getRunnable(fullyQualifiedName);
		if (runnable == null) {
			//System.err.println("Repository: getRunnable() returned null");
			// If the model is in the index returned by ModelManager.getModelIndex it is available, else it is Error
			// Cut the fqn into pieces
			try {
			    String socInstName = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('.'));
			    if(getSocietyInstance(socInstName)!=null) {
				return new Known();
			    }
			} catch(Exception e) {
			    System.err.println(e);
			}
			System.err.println(getModelIndex());
			return new jade.tools.ascml.onto.Error();
		}
		else {
			return runnable.getStatus();
		}
	}

	/** Methods used by GUI-classes */
	public Project getProject()
	{
		return propertyManager.getActiveProject();
	}

	public void setActiveProject(String name)
	{
		propertyManager.setActiveProject(name);
	}

	public Project getProject(String name)
	{
		return propertyManager.getProject(name);
	}

	/**
	 * Throw an ExceptionEvent to all ExceptionListeners.
	 * Exceptions that occur within the repository-package a mostly
	 * not directly thrown to the method-calling objects. Instead these
	 * Exceptions are passed on to this method in order to dispatch it
	 * to general ExceptionListeners.
	 * @param exc The Exception to pass on to the listeners
	 */
	public void throwExceptionEvent(ASCMLException exc)
	{
		System.err.println(exc.toLongString());
		exc.printStackTrace();

		Vector exceptionListener = listenerManager.getExceptionListener();
		// copy into an array, because listener may remove themselves in exceptionThrown-method
		ExceptionListener[] listenerArray = new ExceptionListener[exceptionListener.size()];
		exceptionListener.toArray(listenerArray);

		ExceptionEvent exceptionEvent = new ExceptionEvent(exc);

		for (int i=0; i < listenerArray.length; i++)
		{
			listenerArray[i].exceptionThrown(exceptionEvent);
		}
	}

	public void throwProgressUpdateEvent(final ProgressUpdateEvent evt)
	{
		Vector progressUpdateListener = listenerManager.getProgressUpdateListener();
        // copy into an array, because listener may remove themselves in updateProgress-method
		ProgressUpdateListener[] listenerArray = new ProgressUpdateListener[progressUpdateListener.size()];
		progressUpdateListener.toArray(listenerArray);

		for (int i=0; i < listenerArray.length; i++)
		{
            listenerArray[i].updateProgress(evt);
		}
	}

	public void throwLongTimeActionStartEvent(LongTimeActionStartEvent event)
	{
		Vector longTimeActionStarter = listenerManager.getLongTimeActionStartListener();
        // copy into an array, because listener may remove themselves in startLongTimeAction-method
		LongTimeActionStartListener[] listenerArray = new LongTimeActionStartListener[longTimeActionStarter.size()];
		longTimeActionStarter.toArray(listenerArray);

		for (int i=0; i < listenerArray.length; i++)
		{
			listenerArray[i].startLongTimeAction(event);
		}
	}

	public void throwToolTakeDownEvent()
	{
		Vector exitListener = getListenerManager().getToolTakeDownListener();
		// copy into an array, because listener may remove themselves in toolTakeDown-method
		ToolTakeDownListener[] listenerArray = new ToolTakeDownListener[exitListener.size()];
		exitListener.toArray(listenerArray);
		for (int i=0; i < listenerArray.length; i++)
		{
			listenerArray[i].toolTakeDown(new ToolTakeDownEvent());
		}
	}

	public ModelIndex getModelIndex()
	{
		return modelManager.getModelIndex();
	}

	/**
	 * Get the ModelManager (use it for example to rebuild the ModelIndex (AutoSearch)
	 * @return  The ModelManager-object
	 */
	public ModelManager getModelManager()
	{
		return modelManager;
	}

	/**
	 * Get the RunnableManager
	 * @return  The RunnableManager-object
	 */
	public RunnableManager getRunnableManager()
	{
		return runnableManager;
	}

	/**
	 * Get the ListenerManager.
	 * @return  The ListenerManager-object
	 */
	public ListenerManager getListenerManager()
	{
		return listenerManager;
	}

	public PropertyManager getProperties()
	{
		return propertyManager;
	}

	public void exit()
	{
		try
		{
			propertyManager.exit();
		}
		catch(ASCMLException e)
		{
			throwExceptionEvent(e);
		}
		modelManager.exit();
		listenerManager.exit();
		runnableManager.exit();
	}
}
