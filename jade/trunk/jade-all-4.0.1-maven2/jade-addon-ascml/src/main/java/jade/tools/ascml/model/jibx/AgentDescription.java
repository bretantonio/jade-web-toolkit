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

import jade.tools.ascml.absmodel.IAgentDescription;

import java.util.*;

/**
 *  Model-object containing all required information about a FIPA AgentDescription
 */
public class AgentDescription implements IAgentDescription
{

	/** The name. */
	private String name;

	/** The addresses. */
	private ArrayList addressList = new ArrayList();

	/** The services. */
	private ArrayList serviceList = new ArrayList();

	/** The protocols. */
	private ArrayList protocolList = new ArrayList();

	/** The ontologies. */
	private ArrayList ontologyList = new ArrayList();

	/** The langauges. */
	private ArrayList languageList = new ArrayList();


	/**
	 *  Instantiate a new model and initialize some variables
	 */
	public AgentDescription()
	{
		name		= "";
		addressList	= new ArrayList();
		serviceList	= new ArrayList();
		protocolList	= new ArrayList();
		ontologyList	= new ArrayList();
		languageList	= new ArrayList();
	}
	
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
	 *  Get the addresses, where the agent can be found.
	 *  @return  A String-array containing the agent's addresses as Strings.
	 */
	public String[] getAddresses()
	{
		String[] returnArray = new String[addressList.size()];
		addressList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Set the addresses, where the agent can be found and remove all existing addresses
	 *  @param newAddresses  A String-array containing the agent's addresses as Strings.
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
	 * Add an address where the agent can be found
	 * @param address  An address as String
	 */
	public void addAddress(String address)
	{
		this.addressList.add(address);
	}

	/**
	 *  Get the service-names the agent offers.
	 *  @return  An array containing the name of services.
	 */
	public String[] getServices()
	{
		String[] returnArray = new String[serviceList.size()];
		serviceList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Set the service-names the agent offers and remove the old service-names.
	 *  @param serviceNames  An array containing the name of services.
	 */
	public void setServices(String[] serviceNames)
	{
		serviceList.clear();
		for (int i=0; i < serviceNames.length; i++)
		{
			serviceList.add(serviceNames[i]);
		}
	}

	/**
	 * Add the name of a service the agent offers.
	 * @param service  The name of the service.
	 */
	public void addService(String service)
	{
		this.serviceList.add(service);
	}

	/**
	 *  Get the protocols the agent supports.
	 *  @return  A String-array containing the supported protocols.
	 */
	public String[] getProtocols()
	{
		String[] returnArray = new String[protocolList.size()];
		protocolList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Set the protocols the agent supports and remove the old protocols
	 *  @param newProtocols  A  String-array containing the supported protocols as Strings.
	 */
	public void setProtocols(String[] newProtocols)
	{
		protocolList.clear();
		for (int i=0; i < newProtocols.length; i++)
		{
			protocolList.add(newProtocols[i]);
		}
	}

	/**
	 * Add a protocol the agent supports.
	 * @param  protocol  A String representing the protocol.
	 */
	public void addProtocol(String protocol)
	{
		this.protocolList.add(protocol);
	}

	/**
	 *  Get the ontologies the agent supports.
	 *  @return  A String-array containing the supported ontologies.
	 */
	public String[] getOntologies()
	{
		String[] returnArray = new String[ontologyList.size()];
		ontologyList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Set the ontologies the agent supports and remove the old ontologies.
	 *  @param newOntologies A String-array containing the supported ontologies.
	 */
	public void setOntologies(String[] newOntologies)
	{
		ontologyList.clear();
		for (int i=0; i < newOntologies.length; i++)
		{
			ontologyList.add(newOntologies);
		}
	}

	/**
	 * Add an ontology the agent supports.
	 * @param  ontology  A String representing the ontology.
	 */
	public void addOntology(String ontology)
	{
		this.ontologyList.add(ontology);
	}

	/**
	 *  Get the languages this agent supports.
	 *  @return  A String-array containing the supported languages.
	 */
	public String[] getLanguages()
	{
		String[] returnArray = new String[languageList.size()];
		languageList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Set the languages this agent supports and remove the old languages.
	 *  @param newLanguages  A String-array containing the supported languages.
	 */
	public void setLanguages(String[] newLanguages)
	{
		languageList.clear();
		for (int i=0; i < newLanguages.length; i++)
		{
			languageList.add(newLanguages[i]);
		}
	}

	/**
	 * Add a language the agent supports.
	 * @param  language  A String representing the language.
	 */
	public void addLanguage(String language)
	{
		this.languageList.add(language);
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
		str += name + "\n";
		str += "addresses: " + getAddresses() + "\n";
		str += "services: " + getServices() + "\n";
		str += "protocols: " + getProtocols() + "\n";
		str += "ontologies: " + getOntologies() + "\n";
		str += "languages:" + getLanguages() + "\n";
		return str;
	}
}
