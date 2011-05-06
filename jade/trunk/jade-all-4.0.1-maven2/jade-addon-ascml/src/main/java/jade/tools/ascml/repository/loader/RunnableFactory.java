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


package jade.tools.ascml.repository.loader;

import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.repository.RunnableManager;
import jade.tools.ascml.model.runnable.RunnableAgentInstance;
import jade.tools.ascml.model.runnable.RunnableSocietyInstance;
import jade.tools.ascml.model.runnable.RunnableRemoteSocietyInstanceReference;
import jade.tools.ascml.absmodel.*;

/**
 * 
 */
public class RunnableFactory
{
	public RunnableFactory()
	{
	}

	public synchronized static IAbstractRunnable createRunnable(String name, Object model, RunnableManager manager) throws ModelException
	{
		IAbstractRunnable returnModel = null;

		if (model instanceof IAgentType)
			returnModel = createRunnableAgentInstanceOutOfAgentType(name, (IAgentType)model);
		else if (model instanceof IAgentInstance)
			returnModel = createRunnableAgentInstanceOutOfAgentInstance(name, (IAgentInstance)model);
		else if (model instanceof ISocietyInstance)
			returnModel = createRunnableSocietyInstance(name, (ISocietyInstance)model, manager);
		else
			throw new ModelException("Error creating a runnable for '"+name+"', type of model unknown ("+model.getClass().getName()+").", "A runnable Model couldn't be created, because the type of the model, for which a runnable instance should be created is unknown. Possible model-types are AgentTypes, AgentInstances and SocietyInstances");

		return returnModel;
	}

	private synchronized static RunnableAgentInstance createRunnableAgentInstanceOutOfAgentType(String name, IAgentType model)
	{
		RunnableAgentInstance returnInstance =
			new RunnableAgentInstance(name, model, null, model.getModelChangedListener(), null, model.getClassName(), model.getPlatformType(),
			                               model.getParameters(), model.getParameterSets(),
										   model.getAgentDescriptions(), null);
		return returnInstance;
	}

	private synchronized static IRunnableAgentInstance createRunnableAgentInstanceOutOfAgentInstance(String name, IAgentInstance model)
	{
		RunnableAgentInstance returnInstance = null;

        IAgentType type = model.getType();
		returnInstance = createRunnableAgentInstanceOutOfAgentType(name, type);

		returnInstance.setParameters(model.getParameters());
		returnInstance.setParameterSets(model.getParameterSets());
        returnInstance.setToolOptions(model.getToolOptions());
		returnInstance.addDependencies(model.getDependencies());

		// System.err.println("RunnableFactory, set dependencies for " + name + " dep=" + returnInstance.getDependencies().length);
		return returnInstance;
	}

	private synchronized static IRunnableSocietyInstance createRunnableSocietyInstance(String name, ISocietyInstance model, RunnableManager manager) throws ModelException
	{
		// System.err.println("RunableFactory.createRunnableSocietyInstance: creating society = " + name);
		RunnableSocietyInstance returnInstance = null;

		returnInstance = new RunnableSocietyInstance(name, model, null);

		// create runnableAgentInstances out of agentInstance-models
		IAgentInstance[] agentInstanceModels = model.getAgentInstanceModels();
		for (int i=0; i < agentInstanceModels.length; i++)
		{
			IAgentInstance oneAgentInstance = agentInstanceModels[i];
			IAbstractRunnable[] oneRunnableAgentInstance = (IAbstractRunnable[])manager.createRunnable(oneAgentInstance.getName(), oneAgentInstance, 1);
            // System.err.println("RunnableFactory.createRunnableSocInst: runn-AI=" + oneRunnableAgentInstance);
			for (int j=0; j < oneRunnableAgentInstance.length; j++)
			{
				((RunnableAgentInstance)oneRunnableAgentInstance[j]).setParentRunnable(returnInstance);
				returnInstance.addRunnableAgentInstance((RunnableAgentInstance)oneRunnableAgentInstance[j]);
			}
		}

		// create runnableSocietyInstances out of societyInstanceRef-models
		ISocietyInstanceReference[] socRefs = model.getSocietyInstanceReferences();
		for (int i=0; i < socRefs.length; i++)
		{
			long referenceCount = socRefs[i].getQuantity();
			// System.err.println("RunnableFactory.createRunnableSocietyInstance: SocietyReference "+ socRefs[i] + " " + socRefs[i].getQuantity() + "x");
			for (int j=0; j < referenceCount; j++)
			{
				// System.err.println("RunnableFactory.createRunnableSocietyInstance: process SocietyReference "+ socRefs[i] + " " + j);
				ISocietyInstanceReference oneReference = socRefs[i];
				if (oneReference.isRemoteReference())
				{
					// create the remote runnable reference-object
					RunnableRemoteSocietyInstanceReference oneRunnableReference =
						new RunnableRemoteSocietyInstanceReference(oneReference.getName(), returnInstance, oneReference.getDependencies(), oneReference.getParentSocietyInstance().getParentSocietyType().getModelChangedListener(), oneReference.getTypeName(), oneReference.getInstanceName(), oneReference.getLauncher());
					returnInstance.addRemoteRunnableSocietyInstanceReference(oneRunnableReference);
				}
				else
				{
					// create the local runnable reference-object

					ISocietyInstance referencedSocietyInstance = oneReference.getLocallyReferencedModel();

					// replace the naming scheme for the time of creating the Runnable, than reset it afterwards
					String oldNamingScheme = referencedSocietyInstance.getNamingScheme();
					referencedSocietyInstance.setNamingScheme(oneReference.getNamingScheme());

					// String nameAccordingSocietyScheme = manager.createNameOutOfNamingScheme(referencedSocietyInstance.getName(), oneReference.getNamingScheme(), i);
					IAbstractRunnable[] oneRunnableReference = (IAbstractRunnable[])manager.createRunnable(referencedSocietyInstance.getName(), referencedSocietyInstance, 1);
					for (int k=0; k < oneRunnableReference.length; k++)
					{
						// System.err.println("RunnableFactory.createRunnableSocietyInstance: dependencies from " + oneReference + " = " + oneReference.getDependencies().length);
						// System.err.println("RunnableFactory.createRunnableSocietyInstance: add dependencies from "+oneReference+" to " + oneRunnableReference[k]);
						((RunnableSocietyInstance)oneRunnableReference[k]).setParentRunnable(returnInstance);
						oneRunnableReference[k].addDependencies(oneReference.getDependencies());
						returnInstance.addLocalRunnableSocietyInstanceReference((RunnableSocietyInstance)oneRunnableReference[k]);
					}

					// reset namingscheme
					referencedSocietyInstance.setNamingScheme(oldNamingScheme);
				}
			}
		}

		return returnInstance;
	}
}
