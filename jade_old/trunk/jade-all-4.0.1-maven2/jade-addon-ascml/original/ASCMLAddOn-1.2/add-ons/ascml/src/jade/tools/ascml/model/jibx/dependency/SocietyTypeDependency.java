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

import jade.tools.ascml.absmodel.dependency.ISocietyTypeDependency;

/**
 * 
 */
public class SocietyTypeDependency extends AbstractDependency implements ISocietyTypeDependency
{
	protected String quantity;
	protected String name;

	public SocietyTypeDependency()
	{
		super(SOCIETYTYPE_DEPENDENCY);
	}

	public SocietyTypeDependency(String name, String quantity)
	{
		super(SOCIETYTYPE_DEPENDENCY);
		setName(name);
		setQuantity(quantity);
	}

	/**
	 * Get the name of the SocietyType to depend on.
	 * @return The name of the SocietyType to depend on.
	 */
	public String getName()
	{
		if ((name == null) || name.equals(""))
			name = NAME_UNKNOWN;
		return name;
	}

	/**
	 * Set the name of the SocietyType to depend on.
	 * @param name  The name of the SocietyType to depend on.
	 */
	public void setName(String name)
	{
		if ((name == null) || name.equals(""))
			name = NAME_UNKNOWN;

		this.name = name;
	}

	/**
	 * Get the amount of Runnables of a SocietyType, needed to fulfill this dependency.
	 * @return  The amount of Runnables of a SocietyType, needed to fulfill this dependency.
	 */
	public String getQuantity()
	{
		if ((quantity == null) || quantity.equals(""))
			quantity = "1";
		return quantity;
	}

	/**
	 * Get the amount of Runnables of a SocietyType, needed to fulfill this dependency.
	 * @return  The amount of Runnables of a SocietyType, needed to fulfill this dependency.
	 */
	public int getQuantityAsInt()
	{
		return Integer.parseInt(getQuantity());
	}

	/**
	 * Set the amount of Runnables of an AgentType, needed to fulfill this dependency.
	 * @param quantity  The amount of Runnables of an AgentType, needed to fulfill this dependency.
	 */
	public void setQuantity(String quantity)
	{
		if ((quantity != null) && (!quantity.trim().equals("")))
		{
			if (quantity.equalsIgnoreCase(ALL))
				quantity = ALL;
			this.quantity = quantity;
		}
		else
			this.quantity = "1";
	}

	/**
	 * Set the amount of Runnables of an AgentType, needed to fulfill this dependency.
	 * @param quantity  The amount of Runnables of an AgentType, needed to fulfill this dependency.
	 */
	public void setQuantity(int quantity)
	{
		setQuantity("" + quantity);
	}
}
