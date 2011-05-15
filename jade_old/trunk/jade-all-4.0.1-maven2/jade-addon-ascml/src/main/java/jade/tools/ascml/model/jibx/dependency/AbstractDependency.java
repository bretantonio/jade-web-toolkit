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

import jade.tools.ascml.absmodel.dependency.IDependency;

/**
 * 
 */
public abstract class AbstractDependency implements IDependency
{
	protected String dependencyType;
	protected String active = "false";

	public AbstractDependency(String type)
	{
		this.dependencyType = type;
	}

	/**
	 * Get the type of the depenendency.
	 * Possible values are AGENTTYPE_DEPENDENCY, AGENTINSTANCE_DEPENDENCY,
	 * SOCIETYTYPE_DEPENDENCY, SOCIETYINSTANCE_DEPENDENCY, SERVICE_DEPENDENCY, DELAY_DEPENDENCY.
	 * @return  The type of the dependency.
	 */
	public String getType()
	{
		return dependencyType;
	}

	/**
	 * Return whether this is an active dependency or not.
	 * The ASCML activly engages in fulfilling an active dependency or
	 * just waits for a not-active dependency to hold.
	 * @return  true, if this is an active dependency, false otherwise.
	 */
	public boolean isActive()
	{
		return Boolean.parseBoolean(active);
	}

	/**
	 * Set whether this is an active dependency or not.
	 * The ASCML activly engages in fulfilling an active dependency or
	 * just waits for a not-active dependency to hold.
	 * @param active  true, if this is an active dependency, false otherwise.
	 */
	public void setActive(boolean active)
	{
		this.active = ""+active;
	}

	/**
	 * Set whether this is an active dependency or not.
	 * The ASCML activly engages in fulfilling an active dependency or
	 * just waits for a not-active dependency to hold.
	 * @param active  true, if this is an active dependency, false otherwise.
	 */
	public void setActive(String active)
	{
		this.active = active;
	}

	public String toString()
	{
		String str = getType() + "(active="+active+")";
		return str;
	}
}
