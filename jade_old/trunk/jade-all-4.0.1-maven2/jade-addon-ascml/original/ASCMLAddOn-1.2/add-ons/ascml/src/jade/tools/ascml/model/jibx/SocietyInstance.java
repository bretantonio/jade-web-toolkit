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


package jade.tools.ascml.model.jibx;

import java.util.*;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.absmodel.*;

/**
 *  Model-object containing all required information about a scenario. 
 */
public class SocietyInstance implements ISocietyInstance
{
	protected String name = NAME_UNKNOWN;
	protected String description = "";
	protected String quantity = "1";
	protected String namingScheme = "%N";

	protected ArrayList<IAgentInstance> agentInstanceList = new ArrayList<IAgentInstance>();
	protected ArrayList<ISocietyInstanceReference> societyInstanceReferenceList = new ArrayList<ISocietyInstanceReference>();

	protected IFunctional functional;

	/**
	 * The status indicates if this model has successfully been loaded.
	 * Possible stati are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	private String status = "";

	private ISocietyType parentSociety;

	// ---------------------------------------------------------------------------------

	public SocietyInstance()
	{

	}

	/**
	 *  Get the SocietyType in which this SocietyInstance is declared.
	 *  @return SocietyType in which this SocietyInstance is declared.
	 */
	public ISocietyType getParentSocietyType()
	{
		return parentSociety;
	}
	
	/**
	 *  Set the SocietyType in which this SocietyInstance is declared.
	 *  @param parentSociety  SocietyType in which this SocietyInstance is declared.
	 */
	public void setParentSocietyType(ISocietyType parentSociety)
	{
		this.parentSociety = parentSociety;
	}
	
	/**
	 *  Set the SocietyInstance's name.
	 *  @param name  name of the SocietyInstance.
	 */
	public void setName(String name)
	{
		if ((name == null) || (name.trim().equals("")))
			name = NAME_UNKNOWN;
        if (name.equals(getName()))
			return;

		// check if this society-instance if the 'default' societyinstance of
		// the parent societytype and if so, change the default-name of the type
		if (getParentSocietyType().getDefaultSocietyInstanceName().equals(this.name))
			getParentSocietyType().setDefaultSocietyInstanceName(name);

		this.name = name;
		throwModelChangedEvent(ModelChangedEvent.NAME_CHANGED);
	}
	
	/**
	 *  Get the SocietyInstance's name
	 *  @return name of the SocietyInstance.
	 */
	public String getName()
	{
		if (name == null)
			name = NAME_UNKNOWN;
		return name;
	}

	/**
	 *  Returns the fully-qualified name of the SocietyInstance.
	 *  The name is composed of the fully-qualified name of the SocietyType, to which this
	 *  instance belongs and the name of this SocietyInstance.
	 *  For example my.societyTypeName.SocietyInstanceName (e.g. examples.party.BirthdaySociety.SmallParty)
	 *  would be a correct 'fully qualified' name.
	 *  @return name of the SocietyInstance.
	 */
	public String getFullyQualifiedName()
	{
		return parentSociety.getFullyQualifiedName() + "." + getName();
	}

	/**
	 *  Set the SocietyInstance's description.
	 *  @param description  description of the SocietyInstance.
	 */
	public void setDescription(String description)
	{
		if (description == null)
			description = "";
		if (description.equals(getDescription()))
			return;
		this.description = description;
		throwModelChangedEvent(ModelChangedEvent.DESCRIPTION_CHANGED);
	}

	/**
	 *  Get the SocietyInstance's description
	 *  @return description-String of the SocietyInstance.
	 */
	public String getDescription()
	{
		if (description == null)
			description = "";
		return description;
	}

	/**
	 *  Set the number of SocietyInstances, that should be started.
	 *  When a SocietyInstance is going to start, multiple runnable-models
	 *  may be created out of this SocietyInstance.
	 *  @param quantity  Number of SocietyInstances to start.
	 */
	public void setQuantity(String quantity)
	{
		quantity = quantity.replace(".", "");
		if ((quantity == null) || (quantity.trim().equals("")))
			quantity = "1";
		else
			quantity = quantity.trim();
		if (quantity.equals(getQuantity()))
			return;

		this.quantity = quantity;
	}

	/**
	 *  Get the number of SocietyInstances, that should be started.
	 *  When a SocietyInstance is going to start, multiple runnable-models
	 *  may be created out of this SocietyInstance.
	 *  @return  Number of SocietyInstances to start.
	 */
	public long getQuantity()
	{
		if (quantity == null)
			quantity = "1";
		return new Long(this.quantity);
	}

	/**
	 *  Set the naming-scheme used for naming the instances in case multiple instances of this
	 *  SocietyInstance should be started.
	 *  The naming-scheme is used to name the runnables in order to make sure
	 *  that their names are unique inside the ASCML. The String is appended at
	 *  the Name of the runnable. You may choose any String you like.
	 *  There are a few placeholders that might be used in order order to ensure
	 *  uniqueness of names.
	 *  '%N' is replaced with an ID, starting by 0 and ascending
	 *       to the number of runnable models of this type created so far
	 *  '%T' is replaced at the time of creation of a runnable by the
	 *       current time in milliseconds
	 *  '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 *  Depending on how much runnable-instances you want to create, you should combine
	 *  the placeholders.
	 *  One example for an naming-scheme could be:
	 *  '%N_(time:%Tms)'
	 *  ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 *  @param namingScheme  the naming-scheme used to name runnable SocietyInstances
	 *                       in case multiple instances should be started.
	 */
	public void setNamingScheme(String namingScheme)
	{
		if ((namingScheme == null) || (namingScheme.equals("")))
            namingScheme = "%N";
        if (namingScheme.equals(getNamingScheme()))
			return;
        this.namingScheme = namingScheme.trim();
	}

	/**
	 *  Get the naming-scheme used for naming the instances in case multiple instances of this
	 *  SocietyInstance should be started.
	 *  The naming-scheme is used to name the runnables in order to make sure
	 *  that their names are unique inside the ASCML. The String is appended at
	 *  the Name of the runnable. You may choose any String you like.
	 *  There are a few placeholders that might be used in order order to ensure
	 *  uniqueness of names.
	 *  '%N' is replaced with an ID, starting by 0 and ascending
	 *       to the number of runnable models of this type created so far
	 *  '%T' is replaced at the time of creation of a runnable by the
	 *       current time in milliseconds
	 *  '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 *  Depending on how much runnable-instances you want to create, you should combine
	 *  the placeholders.
	 *  One example for an naming-scheme could be:
	 *  '%N_(time:%Tms)'
	 *  ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 *  @return the naming-scheme used to name runnable SocietyInstances
	 *          in case multiple instances should be started.
	 */
	public String getNamingScheme()
	{
		if (namingScheme == null)
			namingScheme = "";
		if ((getQuantity() > 1) && (namingScheme.equals("")))
			return "%N"; // default scheme
		else
			return this.namingScheme; // user-scheme
	}

	// ----------------------------------------------------------------------------------------

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public String getStatus()
	{
		if (status == null)
			status = STATUS_ERROR;
		return this.status;
	}

	/**
	 * Set the status of this model. The status indicates, whether loading was successful or not.
	 * @param newStatus  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public void setStatus(String newStatus)
	{
		if ((newStatus == null) || newStatus.equals(""))
			newStatus = STATUS_ERROR;
		if (newStatus.equals(getStatus()))
			return;

		this.status = newStatus;
		throwModelChangedEvent(ModelChangedEvent.STATUS_CHANGED);
	}
	
	/**
	 * Set the Functional-model on which the FUNCTIONAL-status relies.
	 * @param functional  The model-object containing the funtional-dependencies and invariants.
	 */
	public void setFunctional(IFunctional functional)
	{
		this.functional = functional;
	}

	/**
	 * Get the Functional-model on which the FUNCTIONAL-status relies.
	 * @return  The model-object containing the funtional-dependencies and invariants.
	 */
	public IFunctional getFunctional()
	{
		return this.functional;
	}

	// --------------------------------------------------------------------------------------

	/**
	 *  Add an AgentInstanceModel to this SocietyInstance.
	 *  @param agentInstance  The AgentInstance to add.
	 */
	public void addAgentInstance(IAgentInstance agentInstance)
	{
		if (agentInstance == null)
			return;

		agentInstanceList.add(agentInstance);
		throwModelChangedEvent(ModelChangedEvent.AGENTINSTANCE_ADDED);
	}

	/**
	 *  Remove an AgentInstance from this society.
	 *  @param model  The AgentInstance to remove
	 */
	public void removeAgentInstance(IAgentInstance model)
	{
		if (agentInstanceList.contains(model))
		{
			agentInstanceList.remove(model);
			throwModelChangedEvent(ModelChangedEvent.AGENTINSTANCE_REMOVED);
		}
	}

	/**
	 *  Get an AgentInstance.
	 *  @param index  The model at the specified index within the inner ArrayList
	 *                containing all the AgentInstances
	 *  @return  The AgentInstance at the specified index
	 */
	public IAgentInstance getAgentInstanceModel(int index)
	{
		return agentInstanceList.get(index);
	}
	
	/**
	 *  Get all AgentInstances contained in this SocietyInstance.
	 *  @return  An array containing AgentInstances.
	 */
	public IAgentInstance[] getAgentInstanceModels()
	{
		AgentInstance[] returnArray = new AgentInstance[agentInstanceList.size()];
		agentInstanceList.toArray(returnArray);
		return returnArray;
	}
	
	/**
	 *  Add a  SocietyInstanceReference to this SocietyInstance.
	 *  @param societyInstanceReference  The SocietyInstanceReference to add.
	 */
	public void addSocietyInstanceReference(ISocietyInstanceReference societyInstanceReference)
	{
		if (societyInstanceReference == null)
			return;

		societyInstanceReferenceList.add(societyInstanceReference);
		throwModelChangedEvent(ModelChangedEvent.SOCIETYINSTANCE_REFERENCE_ADDED);
	}

	/**
	 *  Remove a SocietyInstanceReference from this society.
	 *  @param model  The SocietyInstanceReference to remove.
	 */
	public void removeSocietyInstanceReference(ISocietyInstanceReference model)
	{
		if (societyInstanceReferenceList.contains(model))
		{
			societyInstanceReferenceList.remove(model);
			throwModelChangedEvent(ModelChangedEvent.SOCIETYINSTANCE_REFERENCE_REMOVED);
		}
	}

	/**
	 *  Get a SocietyInstanceReference.
	 *  @param  index  The index of the reference's within the reference-ArrayList.
	 *  @return  The SocietyInstanceReference at the specified index.
	 */
	 public ISocietyInstanceReference getSocietyInstanceReference(int index)
	{
		return societyInstanceReferenceList.get(index);
	}
	
	/**
	 *  Get all SocietyInstanceReferences contained in this SocietyInstance.
	 *  @return  An array containing all the SocietyInstanceReference-models
	 */
	public ISocietyInstanceReference[] getSocietyInstanceReferences()
	{
		SocietyInstanceReference[] returnArray = new SocietyInstanceReference[societyInstanceReferenceList.size()];
		societyInstanceReferenceList.toArray(returnArray);
		return returnArray;
	}

	// ------------------------------------------------------------------------------

	/**
	 * Get the amount of local AgentInstances, that are defined by this SocietyInstance
	 * and all locally referenced subsocieties. The 'quantity' of each instance is hereby considered.
	 * @return  agentinstance-count of local agentinstances.
	 */
	public int getLocalAgentInstanceCount()
	{
        int agentCount = 0;

		// count agentinstances, defined by this model.
		IAgentInstance[] agentInstances = getAgentInstanceModels();
		for (int i=0; i < agentInstances.length; i++)
		{
			agentCount += agentInstances[i].getQuantity();
		}

		// count instances, defined by references societyModels.
		ISocietyInstanceReference[] societyInstanceReferences = getSocietyInstanceReferences();
		for (int i=0; i < societyInstanceReferences.length; i++)
		{

			if (!societyInstanceReferences[i].isRemoteReference())
			{
				ISocietyInstance oneInstance = societyInstanceReferences[i].getLocallyReferencedModel();
				agentCount += (oneInstance.getLocalAgentInstanceCount() * societyInstanceReferences[i].getQuantity());
			}
		}

		// return agentCount multiplied with the 'quantity' of this model.
		return (int)(agentCount * this.getQuantity());
	}

	// -----------------------------------------------------------------------------------


	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed.
	 * If there are many events thrown in very short time-intervals these events will not be
	 * dispatched, if they are of the same kind. Instead a timer counts down and then dispatches
	 * one event.
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		throwModelChangedEvent(eventCode, null);
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed.
	 * If there are many events thrown in very short time-intervals these events will not be
	 * dispatched, if they are of the same kind. Instead a timer counts down and then dispatches
	 * one event.
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 * @param userObject  This object is stored in the ModelChangedEvent. For example: When runnables
	 * are added or removed, these RunnableModels are stored as userObjects to access them later on.
	 */
	public void throwModelChangedEvent(String eventCode, Object userObject)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode, userObject);
		Vector modelChangedListener = getParentSocietyType().getModelChangedListener();
		for (int i=0; i < modelChangedListener.size(); i++)
		{
			((ModelChangedListener)modelChangedListener.elementAt(i)).modelChanged(event);
		}

		/*if (eventThrownTimeStamp + EVENT_INTERVAL > System.currentTimeMillis() || (!lastThrownEventCode.equals(eventCode)))
		{
			// the event may be thrown, because the time-interval is not violated
			reallyThrowModelChangedEvent(eventCode, userObject);
		}
		else
		{
			if (!timerHasBeenStarted)
			{
				System.err.println("SocietyInstanceModel.throwModelChanged: start timer");

				// the time-interval is violated, so check if the timer has been started and if not, start it.
				LongTimeActionStartEvent startEvent = new LongTimeActionStartEvent(LongTimeActionStartEvent.ACTION_WAIT_FOR_MODELCHANGE_POSTING);
				startEvent.addParameter("callback", this);
				startEvent.addParameter("waitfor", new Long(EVENT_INTERVAL));
				startEvent.addParameter("eventcode", eventCode);
				startEvent.addParameter("userobject", userObject);

				Vector timer = getParentSocietyType().getLongTimeActionStartListener();
				LongTimeActionStartListener[] listenerArray = new LongTimeActionStartListener[timer.size()];
				timer.toArray(listenerArray);

				for (int i=0; i < listenerArray.length; i++)
				{
					listenerArray[i].startLongTimeAction(startEvent);
				}
			}
			if (!timerHasBeenStarted)
			{
				System.err.println("SocietyInstanceModel.throwModelChanged: timer already started, throw event away");
			}
		}
         */
		
	}

	/**
	 *  This method returns a short String with the scenario-name.
	 *  It is used by the RepositoryTree for example, to name the nodes.
	 *  @return  String containing the name of this scenario. 
	 */
	public String toString()
	{		
		String str = "";
		if (getQuantity() > 1)
			str += "(" + getQuantity() + ") ";
		str += getName();
		return str;
	}
	
	/**
	 *  This method returns a formatted String showing the scenario-model.
	 *  @return  formatted String showing ALL information about this scenario.
	 */
	public String toFormattedString()
	{
		String str = "";
		
		str += "SocietyInstance : name = " + getName() + "\n";
		ISocietyInstanceReference[] socInstArray = getSocietyInstanceReferences();
		for (int i = 0; i < socInstArray.length; i++)
		{
			
			ISocietyInstanceReference oneReference = socInstArray[i];
			str += "    Reference: " + oneReference + "\n";
		}

		str +=  "     AgentInst.: \n";
		IAgentInstance[] agentInstArray = getAgentInstanceModels();
		for (int i = 0; i < agentInstArray.length; i++)
		{

			IAgentInstance oneInstance = agentInstArray[i];
			str += "          " + oneInstance + "\n";
		}
		return str;		
	}
}
