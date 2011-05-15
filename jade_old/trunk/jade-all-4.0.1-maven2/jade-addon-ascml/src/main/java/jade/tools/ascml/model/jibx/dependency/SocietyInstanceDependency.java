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


package jade.tools.ascml.model.jibx.dependency;

import jade.tools.ascml.model.jibx.AgentID;
import jade.tools.ascml.model.jibx.Provider;
import jade.tools.ascml.absmodel.dependency.ISocietyInstanceDependency;
import jade.tools.ascml.absmodel.IProvider;

/**
 * 
 */
public class SocietyInstanceDependency extends AbstractDependency implements ISocietyInstanceDependency
{
	protected String status;
	protected String societyType;
	protected String societyInstance;

	/** The name and address of the launcher, who shall launch this SocietyInstance. */
	protected IProvider provider;

	public SocietyInstanceDependency()
	{
		super(SOCIETYINSTANCE_DEPENDENCY);
	}

	public SocietyInstanceDependency(String societyType, String societyInstance, String status)
	{
		super(SOCIETYINSTANCE_DEPENDENCY);
		setSocietyType(societyType);
		setSocietyInstance(societyInstance);
		setStatus(status);
	}

	/**
	 * Get the name of the SocietyType in which the dependend SocietyInstance is defined.
	 * @return  The name of the SocietyType in which the dependend SocietyInstance is defined.
	 */
	public String getSocietyType()
	{
		if ((societyType == null) || (societyType.equals("")))
			societyType = NAME_UNKNOWN;
		return societyType;
	}

	/**
	 * Set the SocietyType in which the dependend SocietyInstance is defined.
	 * @param societyType  The SocietyType in which the dependend SocietyInstance is defined.
	 */
	public void setSocietyType(String societyType)
	{
		if ((societyType == null) || (societyType.equals("")))
			societyType = NAME_UNKNOWN;

		this.societyType = societyType;
	}

	/**
	 * Get the name of the dependend SocietyInstance.
	 * @return  The name of the dependend SocietyInstance.
	 */
	public String getSocietyInstance()
	{
		if ((societyInstance == null) || (societyInstance.equals("")))
			societyInstance = NAME_UNKNOWN;
		return societyInstance;
	}

	/**
	 * Set the name of the dependend SocietyInstance.
	 * @param societyInstance  The name of the dependend SocietyInstance.
	 */
	public void setSocietyInstance(String societyInstance)
	{
		if ((societyInstance == null) || (societyInstance.equals("")))
			societyInstance = NAME_UNKNOWN;

		this.societyInstance = societyInstance;
	}

	/**
	 * Get the fully-qualified name of the dependend SocietyInstance.
	 * @return  The fully-qualified name of the dependend SocietyInstance.
	 */
	public String getFullyQualifiedSocietyInstance()
	{
		return getSocietyType() + "." + getSocietyInstance();
	}

	/**
	 * Get the status of the SocietyInstance to depend on.
	 * @return The status of the SocietyInstance to depend on.
	 */
	public String getStatus()
	{
		if ((status == null) || status.equals(""))
			status = STATE_FUNCTIONAL;
		return status;
	}

	/**
	 * Set the status of the SocietyInstance to depend on.
	 * @param status  The status of the SocietyInstance to depend on.
	 */
	public void setStatus(String status)
	{
		if (status.equalsIgnoreCase(STATE_STARTING))
			this.status = STATE_STARTING;
		else if (status.equalsIgnoreCase(STATE_FUNCTIONAL))
			this.status = STATE_FUNCTIONAL;
		else if (status.equalsIgnoreCase(STATE_NONFUNCTIONAL))
			this.status = STATE_NONFUNCTIONAL;
		else if (status.equalsIgnoreCase(STATE_STOPPING))
			this.status = STATE_STOPPING;
		else if (status.equalsIgnoreCase(STATE_ERROR))
			this.status = STATE_ERROR;
		else
			this.status = STATE_UNKNOWN;
	}

	/**
	 * Set the Provider responsible for the SocietyInstance.
	 * @param provider  The Provider responsible for the SocietyInstance.
	 */
	public void setProvider(IProvider provider)
	{
		this.provider = provider;
	}

	/**
	 * Get the Provider responsible for the SocietyInstance.
	 * @return  The Provider responsible for the SocietyInstance.
	 */
	public IProvider getProvider()
	{
		if (provider == null)
			provider = new Provider();
		return provider;
	}

}
