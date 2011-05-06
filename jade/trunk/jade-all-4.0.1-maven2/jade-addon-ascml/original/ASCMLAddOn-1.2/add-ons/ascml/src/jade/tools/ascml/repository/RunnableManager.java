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

import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.repository.loader.RunnableFactory;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.events.ProjectChangedEvent;
import jade.tools.ascml.model.runnable.AbstractRunnable;
import jade.tools.ascml.onto.Known;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.onto.Dead;
import jade.tools.ascml.onto.Stopping;
import jade.tools.ascml.absmodel.*;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;

/**
 * 
 */
public class RunnableManager implements ModelChangedListener
{
    private HashMap runnableMapByName;
    private HashMap runnableMapByModel;
    private Repository repository;

	public RunnableManager(Repository repository)
	{
		this.repository = repository;

		// this class has to register as ModelChangedListener to be informed
		// once a runnableModel has stopped
		repository.getListenerManager().addModelChangedListener(this);

		runnableMapByModel = new HashMap();
		runnableMapByName = new HashMap();
	}

    public IAbstractRunnable[] createRunnable(String name, Object model, int modelCount) throws ModelException
	{
		// modelCount is int (and not long) because it's needed for the array-sizes
		//int modelCount = 1;

		String namingScheme = "";
		if (model instanceof ISocietyInstance)
		{
			// System.err.println("RunnableManager.createRunnable: eine society aus " + model);
			modelCount = (int)((ISocietyInstance)model).getQuantity();
			namingScheme = ((ISocietyInstance)model).getNamingScheme().trim(); // trim to get a copy
		}
		else if (model instanceof IAgentInstance)
		{
			// System.err.println("RunnableManager.createRunnable: ein agent aus " + model);
			modelCount = (int)((IAgentInstance)model).getQuantity();
			namingScheme = ((IAgentInstance)model).getNamingScheme().trim(); // trim to get a copy
		}

		IAbstractRunnable[] runnableModels = new IAbstractRunnable[modelCount];
		// System.err.println("RunnableManager.createRunnable: name=" + name + " modelCount=" + modelCount);
		for (int i=0; i < modelCount; i++)
		{
            // System.err.println("RunnableManager.createRunnable: namingScheme=" + namingScheme);

			String nameAccordingScheme = createNameOutOfNamingScheme(name, namingScheme);
			// System.err.println("RunnableManager.createRunnable: nameAccordingScheme=" + nameAccordingScheme);
			IAbstractRunnable oneRunnableModel = RunnableFactory.createRunnable(nameAccordingScheme, model, this);
			// System.err.println("RunnableManager.createRunnable: addRunnable=" + oneRunnableModel);
			addRunnable(oneRunnableModel);
			runnableModels[i] = oneRunnableModel;
		}
		
		return runnableModels;
	}

    /**
     * Replaces all occurrences of 'replace' in String 'original' with 'replaceWith'.
     * This is an alternative to the String's replaceAll-method, because the
     * performance of this method is much better and the need for an optimized version of
     * replaceAll rises with the number of agent-instances to start.
     * @param original  The original String to search&replace in.
     * @param replace  The substring, that shall be replaced
     * @param replaceWith  The substring, that is replaced with.
     * @return  A new instance of String, where all occurences of the 'replace'-String
     * are replaced by 'replaceWith'.
     */
    private String replaceSubString(String original, String replace, String replaceWith)
    {
        int index = 0;

        while ((index = original.indexOf(replace, index)) >= 0)
        {
            original = original.substring(0, index) + replaceWith + original.substring(index + replace.length());
            // index += replaceWith.length();
        }
        return original;
    }
	
	public String createNameOutOfNamingScheme(String name, String namingScheme)
	{
		String nameAccordingScheme = name + namingScheme;

		if ((namingScheme.indexOf("%N") != -1) || (namingScheme.indexOf("%n") != -1))
		{
			int count = 0;
			IAbstractRunnable[] runnables = getRunnables();
			String namePrefix = name + "_";

			// this part consumes a lot of time, when a lot of agents are started.
			for (int i=0; i < runnables.length; i++)
			{
				if (runnables[i].getName().startsWith(namePrefix))
				{
					count++;
				}
			}

            String replaceWithString = "_" + count;
            nameAccordingScheme = replaceSubString(nameAccordingScheme, "%N", replaceWithString);
            nameAccordingScheme = replaceSubString(nameAccordingScheme, "%n", replaceWithString);
		}
		if ((namingScheme.indexOf("%T") != -1) || (namingScheme.indexOf("%t") != -1))
		{
			// first make sure, that no two names are equal, just because they were started in the same interval.
			long millis = System.currentTimeMillis();
			while (millis == System.currentTimeMillis())
			{
				millis = System.currentTimeMillis();
			}
            String replaceWithString = "_" + millis;
            nameAccordingScheme = replaceSubString(nameAccordingScheme, "%T", replaceWithString);
            nameAccordingScheme = replaceSubString(nameAccordingScheme, "%t", replaceWithString);
		}
		if ((namingScheme.indexOf("%R") != -1) || (namingScheme.indexOf("%r") != -1))
		{
			int randomNumber = (int)(Math.random()*1000000000) % 1000000000; // one billion possibilities

            String replaceWithString = "_" + randomNumber;
            nameAccordingScheme = replaceSubString(nameAccordingScheme, "%R", replaceWithString);
            nameAccordingScheme = replaceSubString(nameAccordingScheme, "%r", replaceWithString);
		}

		return nameAccordingScheme;
	}

	public void addRunnable(IAbstractRunnable runnableModel) throws ModelException
	{
		// first check, if a runnable with the given name is already present.
		// If so, throw an exception cause runnables must have unique names.
		// If not, the runnablemodel is added to two different HashMaps
		// The first map has the runnable-names as keys, the second map the parentObjects.
		if (!runnableMapByName.containsKey(runnableModel.getFullyQualifiedName()))
		{
		    runnableMapByName.put(runnableModel.getFullyQualifiedName(), runnableModel);
		}
		else
		{
			throw new ModelException("RunnableModel named '"+runnableModel.getFullyQualifiedName()+"' already present.", "There is already a runnable model with the given name. Runnable models must have unique names !");
		}

		// there is no model with the given name yet, so add it to the map, which has
		// parentModels as keys.
		if (runnableMapByModel.containsKey(runnableModel.getParentModel()))
		{
            Vector runnableModels = (Vector)runnableMapByModel.get(runnableModel.getParentModel());
			runnableModels.add(runnableModel);
		}
		else
		{
			Vector runnableModels = new Vector();
			runnableModels.add(runnableModel);
			runnableMapByModel.put(runnableModel.getParentModel(), runnableModels);
		}

		Object parentModel = runnableModel.getParentModel();
		if (parentModel instanceof ISocietyInstance)
		{
			((ISocietyInstance)parentModel).throwModelChangedEvent(ModelChangedEvent.RUNNABLE_ADDED, runnableModel);
		}
		else if (parentModel instanceof IAgentInstance)
		{
			((IAgentInstance)parentModel).throwModelChangedEvent(ModelChangedEvent.RUNNABLE_ADDED, runnableModel);
		}
	}

	public void removeRunnable(IAbstractRunnable runnableModel) throws ModelException
	{
        if (runnableMapByName.containsKey(runnableModel.getFullyQualifiedName()))
		{
            Object parentModel = runnableModel.getParentModel();
			Vector runnableModels = (Vector)runnableMapByModel.get(parentModel);
			// System.err.println("RunnableManager.removeRunnable: parentRunnable=" + parentModel);
			// System.err.println("RunnableManager.removeRunnable: parentModel=" + parentModel);
			// System.err.println("RunnableManager.removeRunnable: remove by parentModel=" + runnableModels);
			runnableModels.remove(runnableModel);

			runnableMapByName.remove(runnableModel.getFullyQualifiedName());
		}
		else
		{
			throw new ModelException("RunnableModel named '"+runnableModel.getFullyQualifiedName()+"' could not be found.", "The runnableModel you tried to remove could not be found.");
		}

		Object parentModel = runnableModel.getParentModel();
		if (parentModel instanceof ISocietyInstance)
		{
			((ISocietyInstance)parentModel).throwModelChangedEvent(ModelChangedEvent.RUNNABLE_REMOVED, runnableModel);
		}
		else if (parentModel instanceof IAgentInstance)
		{
			((IAgentInstance)parentModel).throwModelChangedEvent(ModelChangedEvent.RUNNABLE_REMOVED, runnableModel);
		}
		else if (parentModel instanceof IAgentType)
		{
			((IAgentType)parentModel).throwModelChangedEvent(ModelChangedEvent.RUNNABLE_REMOVED, runnableModel);
		}
		// why is no event thrown when stopping single RunnableAgentInstance. Their parent is an AgentType
	}

	public void removeRunnable(String runnableModelName) throws ModelException
	{
        // System.err.println("RunnableManager.removeRunnable: " + runnableModelName);
		// System.err.println("Index vorher =");
		// System.err.println("RunnableMapByModel: " + runnableMapByModel);
		// System.err.println(this);
		if (runnableMapByName.containsKey(runnableModelName))
		{
			IAbstractRunnable runnableModel = (IAbstractRunnable)runnableMapByName.get(runnableModelName);
			removeRunnable(runnableModel);
		}
		else
		{
			throw new ModelException("RunnableModel named '" + runnableModelName + "' could not be found.", "The runnableModel you tried to remove could not be found.");
		}
		// System.err.println("Index nachher =");
		System.err.println(this);
	}

	public void removeAllRunnables(Object parentModel) throws ModelException
	{
        ModelException me = new ModelException("Error removing RunnableModels from '"+parentModel+"'.");
		IAbstractRunnable[] runnableModels = getRunnables(parentModel);
		for (int i=0; i < runnableModels.length; i++)
		{
			try
			{
				removeRunnable(runnableModels[i]);
			}
			catch(ModelException exc)
			{
				me.addNestedException(exc);
			}
		}
		if (me.hasNestedExceptions())
			throw me;
	}

	public IAbstractRunnable getRunnable(String runnableModelName)
	{
		// System.err.println("RunnableManager.getRunnable: modelName=" + runnableModelName);
		return (IAbstractRunnable)runnableMapByName.get(runnableModelName);
	}

	public IAbstractRunnable[] getRunnables(Object parentModel)
	{
		Vector runnableModels = (Vector)runnableMapByModel.get(parentModel);
		if (runnableModels == null)
		{
			runnableModels = new Vector();
		}
		AbstractRunnable[] returnArray = new AbstractRunnable[runnableModels.size()];
		runnableModels.toArray(returnArray);
		return returnArray;
	}

	public IAbstractRunnable[] getRunnables()
	{
		IAbstractRunnable[] returnArray = new IAbstractRunnable[runnableMapByName.size()];
		Iterator iter = runnableMapByName.keySet().iterator();
		int i=0;
		while (iter.hasNext())
		{
			returnArray[i++] = (IAbstractRunnable)runnableMapByName.get(iter.next());
		}
		return returnArray;
	}

	/* This method gets invoked on every model change */
	public void modelChanged(ModelChangedEvent event)
	{
		if ((event.getEventCode() == ModelChangedEvent.STATUS_CHANGED) && (event.getModel() instanceof IAbstractRunnable))
		{
			IAbstractRunnable model = (IAbstractRunnable)event.getModel();
			Status status = model.getStatus();

			// check if runnable is dead and if so remove it from the index.
			// if ((status instanceof Dead) && !(model instanceof IRunnableRemoteSocietyInstanceReference))

			// HACK, as long as RunnableSocietyInstance-status is nowhere set to Dead
			if ((model instanceof IRunnableSocietyInstance) && (status instanceof Stopping))
				status = new Dead();
			if ((status instanceof Dead) && !(model instanceof IRunnableRemoteSocietyInstanceReference))
			{
				try
				{
					String modelName = model.getFullyQualifiedName();

					// System.err.println("RunnableManager.modelChanged: try to remove " + modelName);
					removeRunnable(modelName);
				}
				catch(ModelException exc)
				{
					repository.throwExceptionEvent(exc);
				}
			}
		}
	}

	public String toString()
	{
		IAbstractRunnable[] models = getRunnables();
		String str = "";
		str += "|--- RunnableIndex  --------------------\n";
		if (models.length == 0)
			str += "| empty\n";
		for (int i=0; i < models.length; i++)
		{
			str += "| " + models[i].getFullyQualifiedName() + "\n";
		}
		str += "|---------------------------------------\n";
		return str;
	}

	public void exit()
	{

	}
}
