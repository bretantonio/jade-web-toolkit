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

import jade.tools.ascml.absmodel.dependency.IDelayDependency;

/**
 * 
 */
public class DelayDependency extends AbstractDependency implements IDelayDependency
{
	protected String quantity;

	public DelayDependency()
	{
		super(DELAY_DEPENDENCY);
	}

	public DelayDependency(String quantity)
	{
		super(DELAY_DEPENDENCY);
		setQuantity(quantity);
	}

	/**
	 * Get the amount of milliseconds to wait till this dependency is fulfilled.
	 * @return  The amount of milliseconds to wait till this dependency is fulfilled.
	 */
	public String getQuantity()
	{
		if ((quantity == null) || quantity.equals(""))
			quantity = "1";
		return quantity;
	}

	/**
	 * Get the amount of milliseconds to wait till this dependency is fulfilled.
	 * @return  The amount of milliseconds to wait till this dependency is fulfilled.
	 */
	public int getQuantityAsInt()
	{
		return Integer.parseInt(getQuantity());
	}

	/**
	 * Set the amount of milliseconds to wait till this dependency is fulfilled.
	 * @param quantity  The amount of milliseconds to wait till this dependency is fulfilled.
	 */
	public void setQuantity(String quantity)
	{
		if ((quantity == null) || (quantity.trim().equals("")) || (Integer.parseInt(quantity) < 0))
			this.quantity = "0";
		else
			this.quantity = quantity;
	}

	/**
	 * Set the amount of milliseconds to wait till this dependency is fulfilled.
	 * @param quantity  The amount of milliseconds to wait till this dependency is fulfilled.
	 */
	public void setQuantity(int quantity)
	{
        setQuantity(""+quantity);
	}
}
