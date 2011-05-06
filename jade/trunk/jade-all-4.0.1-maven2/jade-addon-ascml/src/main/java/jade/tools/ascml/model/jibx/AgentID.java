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

import jade.tools.ascml.absmodel.IAgentID;

import java.util.*;

/**
 *  Model-object containing all required information about a FIPA AgentDescription
 */
public class AgentID implements IAgentID
{

	/** The name. */
	protected String name = NAME_UNKNOWN;

	/** The reference. */
	protected String reference = "unspecified";

	/** The addresses. */
	protected ArrayList addressList = new ArrayList();

	/**
	 *  Get the name of this agent.
	 *  @return The agent's name.
	 */
	public String getName()
	{
		if (name == null)
			name = "";
		return name;
	}

	/**
	 *  Set the name of this agent.
	 *  @param name  The agent's name.
	 */
	public void setName(String name)
	{
		if(name == null)
			name = "";
		this.name = name.trim();
	}

	/**
	 *  Get the reference of this AgentID.
	 *  @return The agent's reference.
	 */
	public String getReference()
	{
		if (reference == null)
			reference = "";
		return reference;
	}

	/**
	 *  Set the reference of this AgentID.
	 *  @param reference  The agent's reference.
	 */
	public void setReference(String reference)
	{
		if(reference == null)
			reference = "";
		this.reference = reference.trim();
	}

	/**
	 *  Get the addresses, where the agent can be found.
	 *  @return  A String-array containing the agent's addresses.
	 */
	public String[] getAddresses()
	{
		String[] returnArray = new String[addressList.size()];
		addressList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Set the addresses, where the agent can be found.
	 *  @param newAddresses  A String-array containing the agent's addresses.
	 */
	public void setAddresses(String[] newAddresses)
	{
		addressList.clear();
		for (int i=0; i < newAddresses.length; i++)
		{
			addressList.add(newAddresses[i]);
		}
	}

	/**
	 * Add an address where the agent can be found.
	 * @param address  A String representing the address where the agent can be found.
	 */
	public void addAddress(String address)
	{
		this.addressList.add(address);
	}

	/**
	 * Remove an address.
	 * @param address  A String representing the address to remove.
	 */
	public void removeAddress(String address)
	{
		if ((address != null) && (addressList.contains(address)))
			addressList.remove(address);
	}

	/**
	 *  This methods returns a simple String-representation of this model.
	 */
	public String toString()
	{
		String str = "";
		str += name;
		return str;
	}

	/**
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	public String toFormattedString()
	{
		String str = "";
		str += name + " " + reference + "\n";
		str += "addresses: " + getAddresses() + "\n";
		return str;
	}
}
