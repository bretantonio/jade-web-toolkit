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

import jade.tools.ascml.absmodel.IProperty;
import jade.tools.ascml.absmodel.IServiceDescription;

import java.util.*;

/**
 *  Model-object containing all required information about a ServiceDescription
 */
public class ServiceDescription implements IServiceDescription
{

	protected String name;
	protected String type;
	protected String ownership;
	protected ArrayList propertyList = new ArrayList();
	protected ArrayList protocolList = new ArrayList();
	protected ArrayList ontologyList = new ArrayList();
	protected ArrayList languageList = new ArrayList();

	/**
	 *  Instantiate a new model and initialise some variables
	 */
	public ServiceDescription()
	{
		name				= "";
		type				= "";
		ownership			= "";
	}

	/**
	 *  Get the name of this ServiceDescription.
	 *  @return The service's name.
	 */
	public String getName()
	{
		if (name == null)
			name = "";
		return name;
	}

	/**
	 *  Set the name of this ServiceDescription.
	 *  @param name  The services's name.
	 */
	public void setName(String name)
	{
		if(name == null)
			name = "";
		this.name = name.trim();
	}

	/**
	 *  Get the type of this ServiceDescription.
	 *  @return The service's type.
	 */
	public String getType()
	{
		if (type == null)
			type = "";
		return type;
	}

	/**
	 *  Set the type of this ServiceDescription.
	 *  @param type  The services's type.
	 */
	public void setType(String type)
	{
		if (type == null)
			type = "";
		this.type = type.trim();
	}

	/**
	 *  Get the ownership of this ServiceDescription.
	 *  @return The service's ownership.
	 */
	public String getOwnership()
	{
		if (ownership == null)
			ownership = "";
		return ownership;
	}

	/**
	 *  Set the ownership of this ServiceDescription.
	 *  @param ownership  The services's ownership.
	 */
	public void setOwnership(String ownership)
	{
		if (ownership == null)
			ownership = "";
		this.ownership = ownership.trim();
	}

	/**
	 *  Get the properties of this service.
	 *  @return  An array containing the properties of this service.
	 */
	public IProperty[] getProperties()
	{
		Property[] returnArray = new Property[propertyList.size()];
		propertyList.toArray(returnArray);
		return returnArray;
	}
	
	/**
	 *  Set the properties of this service (old properties are removed)
	 *  @param properties  An array containing the properties for this service.
	 */
	public void setProperties(String[] properties)
	{
		propertyList.clear();
		for (int i=0; i < properties.length; i++)
		{
			propertyList.add(properties[i]);
		}
	}

	/**
	 *  Add a property to this service.
	 *  @param property  The property to add.
	 */
	public void addProperty(Property property)
	{
		this.propertyList.add(property);
	}

	/**
	 *  Remove a property from this service.
	 *  @param property  The property to remove.
	 */
	public void removeProperty(IProperty property)
	{
		this.propertyList.remove(property);
	}

	/**
	 *  Get the names of the protocols the service supports.
	 *  @return  An array containing the supported protocol-names as Strings.
	 */
	public String[] getProtocols()
	{
		String[] returnArray = new String[protocolList.size()];
		protocolList.toArray(returnArray);
		return returnArray;
	}
	
	/**
	 *  Set the protocol-names the service supports (old protocols are removed).
	 *  @param protocols  An array containing the supported protocol-names as Strings.
	 */
	public void setProtocols(String[] protocols)
	{
		protocolList.clear();
		for (int i=0; i < protocols.length; i++)
		{
			protocolList.add(protocols[i]);
		}
	}

	/**
	 *  Add a protocol-name to this service.
	 *  @param protocol The name of the protocol.
	 */
	public void addProtocol(String protocol)
	{
		this.protocolList.add(protocol);
	}

	/**
	 *  Remove a protocol-name to this service.
	 *  @param protocol The name of the protocol.
	 */
	public void removeProtocol(String protocol)
	{
		this.protocolList.remove(protocol);
	}

	/**
	 *  Get the ontology-names the service supports.
	 *  @return  An array containing the supported ontology-names as Strings.
	 */
	public String[] getOntologies()
	{
		String[] returnArray = new String[ontologyList.size()];
		ontologyList.toArray(returnArray);
		return returnArray;
	}
	
	/**
	 *  Set the ontology-names the service supports.
	 *  @param ontologies  An array containing the supported ontology-names as Strings.
	 */
	public void setOntologies(String[] ontologies)
	{
		ontologyList.clear();
		for (int i=0; i < ontologies.length; i++)
		{
			ontologyList.add(ontologies[i]);
		}
	}

	/**
	 *  Add an ontology-name the service supports.
	 *  @param ontology  The ontology-name to add.
	 */
	public void addOntology(String ontology)
	{
		this.ontologyList.add(ontology);
	}

	/**
	 *  Remove an ontology-name.
	 *  @param ontology  The ontology-name to remove.
	 */
	public void removeOntology(String ontology)
	{
		this.ontologyList.remove(ontology);
	}

	/**
	 *  Get the languages this service supports.
	 *  @return  An array containing the supported languages as Strings.
	 */
	public String[] getLanguages()
	{
		String[] returnArray = new String[languageList.size()];
		languageList.toArray(returnArray);
		return returnArray;
	}
	
	/**
	 *  Set the languages this service supports (removing old languages).
	 *  @param languages  An array containing the supported languages as Strings.
	 */
	public void setLanguages(String[] languages)
	{
		languageList.clear();
		for (int i=0; i < languages.length; i++)
		{
			languageList.add(languages[i]);
		}
	}

	/**
	 *  Add a language this service supports.
	 *  @param language  The language to add.
	 */
	public void addLanguage(String language)
	{
		this.languageList.add(language);
	}

	/**
	 *  Remove a language this service supports.
	 *  @param language  The language to remove.
	 */
	public void removeLanguage(String language)
	{
		this.languageList.remove(language);
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
		str += getName() + " type: " + getType() + " ownership: " + getOwnership() + "\n";
		str += "properties: " + getProperties() + "\n";
		str += "protocols: " + getProtocols() + "\n";
		str += "ontologies: " + getOntologies() + "\n";
		str += "languages:" + getLanguages() + "\n";
		return str;
	}
}
