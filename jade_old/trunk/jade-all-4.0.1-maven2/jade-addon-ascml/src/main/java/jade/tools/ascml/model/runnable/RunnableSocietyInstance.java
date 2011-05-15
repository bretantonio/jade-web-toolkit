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

import jade.tools.ascml.onto.Born;
import jade.tools.ascml.onto.Status;
import jade.tools.ascml.onto.Starting;
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.absmodel.IAbstractRunnable;
import jade.tools.ascml.absmodel.IRunnableSocietyInstance;

import java.util.*;

/**
 *  Model-object containing all required information about a runnable SocietyInstance. 
 */
public class RunnableSocietyInstance extends AbstractRunnable implements IRunnableSocietyInstance
{
	private Vector runnableAgentInstances			= new Vector();
	private Vector localRunnableSocietyInstanceRefs	= new Vector();
	private Vector remoteSocietyInstanceRefs		= new Vector();
	
	private HashMap<String, Vector> debugStatusMap = new HashMap<String, Vector>();
	private boolean debug = true;
	
	
	/**
	 *  Instantiate a new model and initialize some variables 
	 */
	public RunnableSocietyInstance(String name, ISocietyInstance parentModel, IAbstractRunnable parentRunnable)
	{
		super(name, parentModel, null, parentModel.getParentSocietyType().getModelChangedListener(), parentRunnable); // SocietyInstances themselves have no dependencies, therefore 'null'

		this.status = new Born();
		this.detailedStatus = "Runnable societyinstance has been created";
	}
	
	/**
	 * Runnable AgentInstances have to inform their parent-RunnableSocietyInstance whenever
	 * their status changed.
	 * @param runnableName The name of the runnable-model, whose status changed (only used for debugging)
	 * @param childNewStatus  New status of the agent
	 * @param childOldStatus  Old status of the agent
	 */
	public synchronized void informStatus(String runnableName, Status childNewStatus, Status childOldStatus)
	{
		// not needed anymore since the status is now set by the DependencyManager
		
		// System.err.println("RunnableSocietyInstance.informStatus: oldStatus=" + childOldStatus + " newStatus=" + childNewStatus);
		/*if (childNewStatus==null)
			System.err.println("RunnableSocietyInstance.informStatus("+this.getFullyQualifiedName() +"): oldStatus=" + childOldStatus + " newStatus=" + childNewStatus);
		if ((childNewStatus==null) || (childNewStatus.equals(childOldStatus)))
			return;

		// now check, if the status has changed and if so, update the status
		// and throw the appropiate event.
		Status oldStatus = getStatus();


		Status newSocStatus = null;
		newSocStatus =


		if (newSocStatus==null)
			newSocStatus=oldStatus;

		if (debug)
		{
			if (debugStatusMap.containsKey(childNewStatus.toString()))
			{
				Vector modelStatusVector = debugStatusMap.get(childNewStatus.toString());
				modelStatusVector.add(runnableName);
			}
			else
			{
				Vector modelStatusVector = new Vector();
				modelStatusVector.add(runnableName);
				debugStatusMap.put(childNewStatus.toString(), modelStatusVector);
			}
			if (childOldStatus != null)
			{
				Vector modelStatusVector = debugStatusMap.get(childOldStatus.toString());
				modelStatusVector.remove(runnableName);
			}

			if ((oldStatus==null) || (!oldStatus.equals(newSocStatus)))
				System.err.println(getName() + " informStatus: inform lead to new status: " + newSocStatus);
			else
				System.err.println(getName() + " informStatus: despite inform old status: " + newSocStatus);

		}

		if ((oldStatus==null) || (!oldStatus.equals(newSocStatus)))
			super.setStatus(newSocStatus);
		*/
	}

	/**
	 * Get the status of this RunnableSocietyInstance.
	 * The status results of the status of all contained RunnableAgentInstances
	 * plus all referenced RunnableSocietyInstances (and their agents and references).
	 * @return
	 */
	public Status getStatus()
	{
		return super.getStatus();
	}
	
	/**
	 * Get the amount of local runnable agentinstances, that are defined by this model
	 * and all local runnable subsocieties.
	 * @return  runnable agentinstance-count..
	 */
	public int getLocalAgentInstanceCount()
	{
		int agentCount = getRunnableAgentInstances().length;
		IRunnableSocietyInstance[] references = getLocalRunnableSocietyInstanceReferences();
		
		for (int i=0; i < references.length; i++)
		{
			agentCount += references[i].getLocalAgentInstanceCount();
		}
		
		return agentCount;
	}
//	------------------------------------------------------
	
	public void addRunnableAgentInstance(RunnableAgentInstance runnableInstance)
	{
		runnableAgentInstances.add(runnableInstance);
	}
	
	public RunnableAgentInstance[] getRunnableAgentInstances()
	{
		if (runnableAgentInstances == null)
			return new RunnableAgentInstance[0];
		RunnableAgentInstance[] returnArray = new RunnableAgentInstance[runnableAgentInstances.size()];
		runnableAgentInstances.toArray(returnArray);
		return returnArray;
	}
	
//	------------------------------------------------------
	
	public void addLocalRunnableSocietyInstanceReference(RunnableSocietyInstance runnableInstance)
	{
		localRunnableSocietyInstanceRefs.add(runnableInstance);
	}
	
	public RunnableSocietyInstance[] getLocalRunnableSocietyInstanceReferences()
	{
		if (localRunnableSocietyInstanceRefs == null)
			return new RunnableSocietyInstance[0];
		
		RunnableSocietyInstance[] returnArray = new RunnableSocietyInstance[localRunnableSocietyInstanceRefs.size()];
		localRunnableSocietyInstanceRefs.toArray(returnArray);
		return returnArray;
	}
	
//	------------------------------------------------------
	
	public void addRemoteRunnableSocietyInstanceReference(RunnableRemoteSocietyInstanceReference oneReference)
	{
		remoteSocietyInstanceRefs.add(oneReference);
	}
	
	public RunnableRemoteSocietyInstanceReference[] getRemoteRunnableSocietyInstanceReferences()
	{
		if (remoteSocietyInstanceRefs == null)
			return new RunnableRemoteSocietyInstanceReference[0];
		
		RunnableRemoteSocietyInstanceReference[] returnArray = new RunnableRemoteSocietyInstanceReference[remoteSocietyInstanceRefs.size()];
		remoteSocietyInstanceRefs.toArray(returnArray);
		return returnArray;
	}
	
	public String toFormattedString()
	{
		String str = "";
		str += "RunnableSocietyInstance: name = " + getName() + "\n";
		str += "runnableAgentInstances=" + runnableAgentInstances + "\n";
		str += "localRunnableSocietyInstanceRefs=" + localRunnableSocietyInstanceRefs + "\n";
		str += "remoteSocietyInstanceRefs=" + remoteSocietyInstanceRefs + "\n";
		return str;
	}
}
