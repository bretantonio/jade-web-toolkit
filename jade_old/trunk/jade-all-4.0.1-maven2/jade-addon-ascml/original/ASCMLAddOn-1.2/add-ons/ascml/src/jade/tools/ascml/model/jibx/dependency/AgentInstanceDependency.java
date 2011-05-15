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

import jade.tools.ascml.absmodel.dependency.IAgentInstanceDependency;
import jade.tools.ascml.absmodel.IAgentID;
import jade.tools.ascml.absmodel.IProvider;
import jade.tools.ascml.model.jibx.Provider;

/**
 * 
 */
public class AgentInstanceDependency extends AbstractDependency implements IAgentInstanceDependency
{
	protected String status;
	protected String name;

	/** The name and address of the launcher, who shall launch this SocietyInstance. */
	protected IProvider provider;

	public AgentInstanceDependency()
	{
		super(AGENTINSTANCE_DEPENDENCY);
	}

	public AgentInstanceDependency(String name, String status)
	{
		super(AGENTINSTANCE_DEPENDENCY);
		setName(name);
		setStatus(status);
	}

	/**
	 * Get the name of the AgentInstance to depend on.
	 * @return The name of the AgentInstance to depend on.
	 */
	public String getName()
	{
		if ((name == null) || name.equals(""))
			name = NAME_UNKNOWN;
		return name;
	}

	/**
	 * Set the name of the AgentInstance to depend on.
	 * @param name  The name of the AgentInstance to depend on.
	 */
	public void setName(String name)
	{
		if ((name == null) || name.equals(""))
			name = NAME_UNKNOWN;		
		this.name = name;
	}

	/**
	 * Get the status of the AgentInstance to depend on.
	 * @return The status of the AgentInstance to depend on.
	 */
	public String getStatus()
	{
		if ((status == null) || status.equals(""))
			status = STATE_FUNCTIONAL;
		return status;
	}

	/**
	 * Set the status of the AgentInstance to depend on.
	 * @param status  The status of the AgentInstance to depend on.
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
	 * Set the Provider responsible for the AgentInstance.
	 * @param provider  The Provider responsible for the AgentInstance.
	 */
	public void setProvider(IProvider provider)
	{
		this.provider = provider;
	}

    /**
	 * Set the Provider responsible for the AgentInstance.
	 * @return  The Provider responsible for the AgentInstance.
	 */
	public IProvider getProvider()
	{
		if (provider == null)
			provider = new Provider();
		return provider;
	}

}
