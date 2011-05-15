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


package jade.tools.ascml.model.runnable;

import java.util.*;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.dependencymanager.DependencyManager;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.absmodel.IAbstractRunnable;

/**
 *  Model-object containing all required information about a running SocietyInstance. 
 */
public class AbstractRunnable implements IAbstractRunnable
{
	protected String name;
	protected Status status;
	protected String detailedStatus = "";
	protected Object parentModel;
	protected IAbstractRunnable parentRunnable;
	protected IDependency[] dependencies;
	protected DependencyManager dependencyManager;

	protected Vector modelChangedListener;

	/**
	 *  Instantiate a new model and initialize some variables 
	 */
	public AbstractRunnable(String name, Object parentModel, IDependency[] dependencies,
							Vector modelChangedListener, IAbstractRunnable parentRunnable)
	{
		this.name = name;
		this.parentModel = parentModel;
		this.parentRunnable = parentRunnable;
		
		if(dependencies != null)
			this.dependencies = dependencies;
		else
			this.dependencies = new IDependency[0];

		this.modelChangedListener = modelChangedListener;
	}
	
	/**
	 *  Set the name of this agentType.
	 *  @param name  agentType's name.
	 */
	public void setName(String name)
	{
		if(name == null)
		{
			name = "";
		}
		else
		{
			this.name = name;
		}
	}
	
	/**
	 *  Get the name of this runnable.
	 *  @return  runnable's name.
	 */	
	public String getName()
	{
		return name;
	}

	/**
	 *  Get the fully qualified name of this runnable
	 *  @return  runnable's fully qualified name.
	 */
	public String getFullyQualifiedName()
	{
		String fullyQualifiedName = null;
		if (parentModel instanceof IAgentType)
			fullyQualifiedName = ((IAgentType)parentModel).getFullyQualifiedName();
		else if (parentModel instanceof ISocietyInstance)
			fullyQualifiedName = ((ISocietyInstance)parentModel).getFullyQualifiedName();
		else
			System.err.println("AbstractRunnable.getFQName: Warning parentModel not identified (Dirk)");

		return fullyQualifiedName + "." + name;
	}

	/**
	 *  Get the parent-model of this agentInstance.
	 *  @return  model-object of this instance's parent.
	 */
	public Object getParentModel()
	{
		return this.parentModel;
	}

	/**
	 *  Get the parent-model of this agentInstance.
	 *  @return  model-object of this instance's parent.
	 */
	public Object getParentRunnable()
	{
		return this.parentRunnable;
	}

	/**
	 * Set the parent runnable-model of this AbstractRunnable.
	 * This method should only be called right after creation.
	 * @param parentRunnable  runnable-model to which this runnable-model belongs
	 */
	public void setParentRunnable(IAbstractRunnable parentRunnable)
	{
		this.parentRunnable = parentRunnable;
		// inform parentRunnable about the status, set null as 'oldStatus'
		System.err.println("AbstractRunnable.setParentRunnable: name=" + getName() + " parent=" + parentRunnable + " status=" + getStatus());
		parentRunnable.informStatus(this.getName(), this.getStatus(), null);
	}

	/**
	 *  Get the dependencies of this runnable model..
	 *  @return  runnable agentinstance's dependencies or null, if it depends on nothing.
	 */	
	public IDependency[] getDependencies()
	{
		return this.dependencies;
	}

	/**
	 *  Get the dependencies of this runnable model..
	 *  @return  runnable agentinstance's dependencies or null, if it depends on nothing.
	 */
	public Vector<IDependency> getDependencyList()
	{
		Vector<IDependency> dummy = new Vector<IDependency>();

		for (int i=0; i < dependencies.length; i++)
		{
			dummy.add(dependencies[i]);
		}
		return dummy;
	}

	/**
	 *  Add dependencies to this runnable model.
	 *  @param additionalDependencies  The model's dependencies or null, if it depends on nothing.
	 */
	public void addDependencies(IDependency[] additionalDependencies)
	{
		IDependency[] newDependencyArray = new IDependency[dependencies.length + additionalDependencies.length];

		// set the old dependencies within the new array
		for (int i=0; i < dependencies.length; i++)
		{
			newDependencyArray[i] = dependencies[i];
		}

		// add the new dependencie to the new array
		for (int i=0; i < additionalDependencies.length; i++)
		{
			newDependencyArray[i + dependencies.length] = additionalDependencies[i];
		}

		dependencies = newDependencyArray;
	}

	/**
	 *  Set the status of this instance. By setting the new status to NOT_RUNNING, the runnable
	 *  instance is automatically removed from the parent's runnable-list.
	 *  @param newStatus  The instance's new status.
	 */
	public void setStatus(Status newStatus)
	{
		Status oldStatus = this.status;

		// vvv Do we need to check other possibilities?
		if (newStatus == null) {
			newStatus = new jade.tools.ascml.onto.Error();
		}
		if (!newStatus.equals(oldStatus))
		{
			this.status = newStatus;
			System.err.println("AbstractRunnable.setStatus: name=" + getName() + " status=" + getStatus() + " parent=" + parentRunnable);
			if (parentRunnable != null)
				parentRunnable.informStatus(getName(), newStatus, oldStatus);

			throwModelChangedEvent(ModelChangedEvent.STATUS_CHANGED);
		}
	}

	/**
	 * Runnables may inform their parent-Runnables (e.g. the RunnableSociety to which
	 * a RunnableSociety or -AgentInstance belongs) about a status-change of their own.
	 * @param runnableName The name of the runnable-model, whose status changed (only used for debugging)
	 * @param newStatus  New status of the agent
	 * @param oldStatus  Old status of the agent
	 */    	
	public synchronized void informStatus(String runnableName, Status newStatus, Status oldStatus)
	{
		// may be overwritten by underlying models
	}

	/**
	 *  Get the status of this instance.
	 *  @return  instance's status.
	 */	
	public Status getStatus()
	{
		return this.status;
	}

	/**
	 *  Get the detailed information about the status of this instance.
	 *  @return  instance's status in detail.
	 */	
	public String getDetailedStatus()
	{
		return this.detailedStatus;
	}
	
	/**
	 *  Set the detailed information about the status of this instance.
	 *  @param detailedStatus  instance's status in detail.
	 */	
	public void setDetailedStatus(String detailedStatus)
	{
		this.detailedStatus = detailedStatus;
		throwModelChangedEvent(ModelChangedEvent.DETAILED_STATUS_CHANGED);
	}

	private void throwModelChangedEvent(String eventCode)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode);
		for (int i=0; i < modelChangedListener.size(); i++)
		{
			// System.err.println("AbstractRunnable: Going to inform listener about RunnableModelChanged-event...");
			((ModelChangedListener)modelChangedListener.elementAt(i)).modelChanged(event);
		}
	}

	public String toString()
	{
		return getName();
	}

	public String toFormattedString()
	{
		String str = getName();
		return str;
	}
}
