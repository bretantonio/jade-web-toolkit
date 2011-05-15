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
import jade.tools.ascml.model.jibx.dependency.AbstractDependency;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.absmodel.ISocietyInstanceReference;
import jade.tools.ascml.launcher.AgentLauncher;

/**
 *  This class describes the properties of an societyInstance-reference.
 */
public class SocietyInstanceReference implements ISocietyInstanceReference
{

	/** The name. */
	protected String name;
	
	/** The name of the references SocietyType. */
	protected String typeName;

	/** The name of the references SocietyInstance. */
	protected String instanceName;

	/**
	 * This object may be null, in case this referenceModel points to a remote reference
	 * or to a local reference, which cannot be found in the local repository.
	 */
	protected ISocietyInstance locallyReferencedModel;

	/**
	 *  The SocietyInstance-model in which this reference is declared.
	 */
	protected ISocietyInstance parentSocietyInstance;

	/** The name and address of the launcher, who shall launch this SocietyInstance. */
	protected Launcher launcher;

	/** The reference's status. */
	protected String status;

	/** The dependencies, that must be fulfilled in order to launch the SocietyInstance */
	private ArrayList<IDependency> dependencyList = new ArrayList<IDependency>();
	
	/** The number of instances that should be launched. */
	protected String quantity;
	
	/** The naming-scheme used to name the instances in case quantity is > 1. */
	protected String namingScheme;

	// -------------------------------------------------------------------------------------

	public SocietyInstanceReference()
	{

	}

	public SocietyInstanceReference(ISocietyInstance parentSocietyInstance)
	{
		setParentSocietyInstance(parentSocietyInstance);
	}

	/**
	 *  Get the SocietyInstance to which this SocietyInstanceReference belongs.
	 *  @return  SocietyInstance to which this SocietyInstanceReference belongs.
	 */
	public ISocietyInstance getParentSocietyInstance()
	{
		return parentSocietyInstance;
	}

	/**
	 *  Set the SocietyInstance to which this SocietyInstanceReference belongs.
	 *  @param parentSocietyInstance SocietyInstance to which this SocietyInstanceReference belongs.
	 */
	public void setParentSocietyInstance(ISocietyInstance parentSocietyInstance)
	{
		this.parentSocietyInstance = parentSocietyInstance;
	}

	/**
	 * Set the SocietyInstanceReference's name.
	 * @param name  The name of this SocietyInstanceReference.
	 */
	public void setName(String name)
	{
		if ((name == null) || (name.trim().equals("")))
			name = NAME_UNKNOWN;
        if (name.equals(getName()))
			return;
		this.name = name;
		throwModelChangedEvent(ModelChangedEvent.NAME_CHANGED);
	}

	/**
	 * Get the SocietyInstanceReference's name.
	 * @return The name of this SocietyInstanceReference.
	 */
	public String getName()
	{
		if (name == null)
			return NAME_UNKNOWN;
		return name;
	}
	
	/**
	 *  Get the fully-qualified name of the SocietyInstanceReference.
	 *  The name is composed of the fully-qualified name of the SocietyInstance, to which this
	 *  reference points and the name of this reference.
	 *  For example my.societyTypeName.SocietyInstanceName.ReferenceName (e.g. examples.party.BirthdaySociety.SmallParty.ErnasParty)
	 *  would be a correct 'fully-qualified' name.
	 *  @return  fully-qualified name of the SocietyInstanceReference.
	 */
	public String getFullyQualifiedName()
	{
		return getTypeName() + "." + getInstanceName() + "." + getName();
	}

	/**
	 * Set the name of the referenced SocietyType.
	 * @param typeName  The name of the referenced SocietyType.
	 */
	public void setTypeName(String typeName)
	{
		if ((typeName == null) || (typeName.trim().equals("")))
			typeName = TYPE_UNKNOWN;
		this.typeName = typeName;
	}

	/**
	 * Get the name of the referenced SocietyType.
	 * @return  The name of the referenced SocietyType.
	 */
	public String getTypeName()
	{
		if (typeName == null)
			typeName = NAME_UNKNOWN;
		return typeName;
	}

	/**
	 * Set the name of the referenced SocietyInstance.
	 * @param instanceName  The name of the referenced SocietyInstance.
	 */
	public void setInstanceName(String instanceName)
	{
		if ((instanceName == null) || (instanceName.trim().equals("")))
			instanceName = INSTANCE_UNKNOWN;
		this.instanceName = instanceName;
	}

	/**
	 * Get the name of the referenced SocietyInstance.
	 * @return  The name of the referenced SocietyInstance.
	 */
	public String getInstanceName()
	{
		if (instanceName == null)
			instanceName = NAME_UNKNOWN;
		return instanceName;
	}

	/**
	 * Set the number of SocietyInstanceReferences to be started.
	 * @param quantity  Number of SocietyInstanceReferences to be started.
	 */
	public void setQuantity(String quantity)
	{
		quantity = quantity.replace(".", "");
		if ((quantity == null) || (quantity.trim().equals("")))
			this.quantity = "1";
		else
			this.quantity = quantity.trim();
	}

	/**
	 * Get the number of SocietyInstanceReferences to be started.
	 * @return  Number of SocietyInstanceReferences to be started.
	 */
	public long getQuantity()
	{
		if (quantity == null)
			quantity = "1";
		return new Long(quantity);
	}

	/**
	 * Set the naming-scheme used for naming the SocietyInstanceReferences in case multiple instances of this SocietyInstanceReference should be started.
	 * The naming-scheme is used to name the runnables in order to make sure that their names are unique inside the ASCML.
	 * The String is appended at the name of the runnable. You may choose any String you like.
	 * There are a few placeholders that might be used in order order to ensure uniqueness of names.
	 * '%N' is replaced with an ID, starting by 0 and ascending to the number of runnable models of this type created so far.
	 * '%T' is replaced at the time of creation of a runnable by the current time in milliseconds.
	 * '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 * Depending on how much runnable-instances you want to create, you should combine the placeholders.
	 * One example for an naming-scheme could be: '%N_(time:%Tms)' ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 * @param namingScheme  The naming-scheme used to name runnable SocietyInstancesReferences in case multiple instances should be started.
	 */
	public void setNamingScheme(String namingScheme)
	{
		if (namingScheme == null)
			this.namingScheme = "";
		else
			this.namingScheme = namingScheme.trim();
	}

	/**
	 * Get the naming-scheme used for naming the SocietyInstanceReferences in case multiple instances of this SocietyInstanceReference should be started.
	 * The naming-scheme is used to name the runnables in order to make sure that their names are unique inside the ASCML.
	 * The String is appended at the name of the runnable. You may choose any String you like.
	 * There are a few placeholders that might be used in order order to ensure uniqueness of names.
	 * '%N' is replaced with an ID, starting by 0 and ascending to the number of runnable models of this type created so far.
	 * '%T' is replaced at the time of creation of a runnable by the current time in milliseconds.
	 * '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 * Depending on how much runnable-instances you want to create, you should combine the placeholders.
	 * One example for an naming-scheme could be: '%N_(time:%Tms)' ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 * @return The naming-scheme used to name runnable SocietyInstancesReferences in case multiple instances should be started.
	 */
	public String getNamingScheme()
	{
		if ((namingScheme == null) || (namingScheme.equals("")))
			namingScheme = "%N";

		return this.namingScheme; // user-scheme
	}

	// -------------------------------------------------------------------------------

	/**
	 * Set the launcher to start the referenced SocietyInstance in case this is a remote-reference.
	 * @param launcher  The Launcher to start the referenced SocietyInstance.
	 */
	public void setLauncher(Launcher launcher)
	{
		this.launcher = launcher;
	}

	/**
	 * Get the launcher to start the referenced SocietyInstance in case this is a remote-reference.
	 * @return  The Launcher to start the referenced SocietyInstance.
	 */
	public Launcher getLauncher()
	{
		if (launcher == null)
			launcher = new Launcher();
		return launcher;
	}

	/**
	 * Returns wheter this reference points to a local or a remote reference.
	 * If the launcher is set (and it's name), this is a remote reference, otherwise it's a local reference.
	 * @return  true, if remote-reference, false if reference is local.
	 */
	public boolean isRemoteReference()
	{
		if (launcher == null)
			return false;
		if ((launcher.getName().equals(Launcher.NAME_UNKNOWN) || launcher.getName().equals("") || launcher.getName().equals(AgentLauncher.ASCML_NAME)) && (launcher.getAddresses().length == 0))
			return false;
		return true;
	}

	/**
	 * Set the SocietyInstance locally referenced by this SocietyInstanceReference.
	 * In case the reference points to a remote SocietyInstance nothing has to be set.
	 * @param locallyReferencedModel  The local SocietyInstanceModel, referenced by this ReferenceModel
	 */
	public void setLocallyReferencedSocietyInstance(ISocietyInstance locallyReferencedModel)
	{
		if (isRemoteReference())
			System.err.println("WARNING: SocietyInstanceReference.setLocallyReferencedSocietyInstance called, but this is a remote reference!");
		this.locallyReferencedModel = locallyReferencedModel;
	}

	/**
	 * Get the SocietyInstance locally referenced by this SocietyInstanceReference in case this is a local-reference.
	 * In case the reference points to a remote SocietyInstance nothing has to be set.
	 * @return  The locally referenced SocietyInstance or null, if reference points to a remote SocietyInstance.
	 */
	public ISocietyInstance getLocallyReferencedModel()
	{
		return locallyReferencedModel;
	}

	/**
	 * Get the status of this SocietyInstanceReference.
	 * The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR
	 */
	public String getStatus()
	{
		if (status == null)
			status = STATUS_ERROR;
		return status;
	}

	/**
	 * Set the status of this SocietyInstanceReference.
	 * The status indicates, whether loading was successful or not.
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
	 *  Get all of the SocietyInstanceReference's dependency-models.
	 *  @return An array containing all of the SocietyInstanceReference's dependency-models.
	 */
	public IDependency[] getDependencies()
	{
		AbstractDependency[] returnArray = new AbstractDependency[dependencyList.size()];
		dependencyList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Get all of the SocietyInstanceReference's dependency-models.
	 *  @return A list containing all of the SocietyInstanceReference's dependency-models
	 */
	public List<IDependency> getDependencyList()
	{
		return dependencyList;
	}

	/**
	 * Add a dependency to this SocietyInstanceReference's dependencies.
	 * @param dependency  The Dependency to add.
	 */
	public void addDependency(IDependency dependency)
	{
		dependencyList.add(dependency);
	}

	/**
	 * Remove a dependency from this SocietyInstanceReference's dependencies.
	 * @param dependency  The Dependency to remove.
	 */
	public void removeDependency(IDependency dependency)
	{
		dependencyList.remove(dependency);
	}

	/**
	 * Remove a dependency from this reference's dependency-list.
	 * @param dependencyIndex  The index of the Dependency-model
	 *                         to remove within the inner dependency-list.
	 */
	public void removeDependency(int dependencyIndex)
	{
		dependencyList.remove(dependencyIndex);
	}

	// --------------------------------------------------------------------------------

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		if (parentSocietyInstance != null)
			getParentSocietyInstance().throwModelChangedEvent(eventCode, this);
	}

	public String toString()
	{
		String str = "";
        if (getQuantity() > 1)
			str += "(" + getQuantity() + ") ";

		String typeName = getTypeName();
		if (isRemoteReference())
		{
			str = getName() + " -> " + getInstanceName() + "@" + getLauncher().getName();
		}
		else
		{
			if ((this.getLocallyReferencedModel() != null) && typeName.equals(this.getLocallyReferencedModel().getParentSocietyType().getName()))
				str = getName() + " -> " + getInstanceName();
			else
				str = getName() + " -> " + typeName + "." + getInstanceName();
		}
		return str;
	}
}
