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

package jade.tools.ascml.dependencymanager;

import java.util.HashMap;

import jade.core.AID;
import jade.core.NotFoundException;
import jade.tools.ascml.absmodel.*;

/**
 * The DependencyManangerLibrary stores information about started models.
 * You can lookup which agent-type a specific AID belongs to,
 * or get the IRunnableSocietyInstance for a specific society-name.
 * (But only if it was started through this ASCML)
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 */
public class DependencyManangerLibrary {
	
	private HashMap<String, IRunnableAgentInstance>		nameToAgentTypeMap;
	private HashMap<String, IRunnableSocietyInstance>	nameToSocietyMap;
	
	public DependencyManangerLibrary () {
		nameToAgentTypeMap = new HashMap<String, IRunnableAgentInstance>();
		nameToSocietyMap = new HashMap<String, IRunnableSocietyInstance>();
	}
	
	/**
	 * Clear the library
	 */
	public void resetLibrary () {
		nameToAgentTypeMap.clear();
		nameToSocietyMap.clear();
	}
	
	/**
	 * Add a specific agent to the library, so that you can lookup the IRunnableAgentInstance belonging to an AID later on.
	 * @param agentAID The AID of the agent
	 * @param agentInstanceModel The IRunnableAgentInstance of the agent 
	 * @return false If the agent already is in the library, true if it was added successfully
	 */
	public boolean addAgent(AID agentAID, IRunnableAgentInstance agentInstanceModel) {
		return addAgent(agentAID.getLocalName(),agentInstanceModel);
	}

	/**
	 * Add a specific agent to the library, so that you can lookup the IRunnableAgentInstance belonging to a local name later on.
	 * @param name The local name of the agent
	 * @param agentInstanceModel The IRunnableAgentInstance of the agent 
	 * @return false If the agent already is in the library, true if it was added successfully
	 */
	public boolean addAgent(String name, IRunnableAgentInstance agentInstanceModel) {
		if (nameToAgentTypeMap.containsKey(name)) {
			return false;
		} else {
			nameToAgentTypeMap.put(name,agentInstanceModel);
			return true;
		}		
	}	
	
	/**
	 * Add a specific society to the library, so that you can lookup the IRunnableSocietyInstance belonging to a name later on.
	 * @param name The full qualified name of the society
	 * @param societyInstanceModel The IRunnableSocietyInstance of the society 
	 * @return false If the society already is in the library, true if it was added successfully
	 */
	public boolean addSociety(String name, IRunnableSocietyInstance societyInstanceModel) {
		if (nameToSocietyMap.containsKey(name)) {
			return false;
		} else {
			nameToSocietyMap.put(name,societyInstanceModel);
			return true;
		}
	}
	
	/**
	 * Remove an agent from the library
	 * @param agentAID The AID of the agent to be removed
	 * @return true if the agent was found in the library
	 */
	public boolean delAgent(AID agentAID) {	
		return delAgent(agentAID.getLocalName());
	}
	
	/**
	 * Remove an agent from the library 
	 * @param name The local name of the agent to be removed
	 * @return true if the agent was found in the library
	 */
	public boolean delAgent(String name) {
		if (nameToAgentTypeMap.containsKey(name)) {
			nameToAgentTypeMap.remove(name);
			return true;
		} else {
			return false;	
		}
	}
	
	/**
	 * Remove a society from the library 
	 * @param name The full qualified name of the society to be removed
	 * @return true If the society was successfully removed, false otherwise
	 */
	public boolean delSociety(String name){
		if (nameToSocietyMap.containsKey(name)) {
			nameToSocietyMap.remove(name);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Lookup the full qualified name of the agent-type belonging to the AID
	 * @param agentAID The AID to lookup in the library 
	 * @return The full qualified name of the agent-type the local name belongs to
	 * @throws NotFoundException @see #lookupAgentType(String)
	 */
	public String lookupAgentType (AID agentAID) throws NotFoundException {
		return lookupAgentType(agentAID.getLocalName());
	}
	
	/**
	 * Lookup the full qualified name of the agent-type the local name belongs to
	 * @param name The local name to lookup in the library 
	 * @return The full qualified name of the agent-type the local name belongs to
	 * @throws NotFoundException Throws a NotFoundException if the local name was not found in the library
	 */
	public String lookupAgentType (String name) throws NotFoundException {
		if (nameToAgentTypeMap.containsKey(name)) {
			return nameToAgentTypeMap.get(name).getType().getFullyQualifiedName();
		} else {
			throw new NotFoundException("Did not find "+name+" in the library.");
		}
	}	
	
	/**
	 * Lookup the full qualified name of the society-type the name belongs to
	 * @param name The society name to lookup in the library 
	 * @return The full qualified name of the society-type the society name belongs to
	 * @throws NotFoundException Throws a NotFoundException if the local name was not found in the library
	 */
	public IRunnableSocietyInstance lookupSocietyName (String name) throws NotFoundException {
		if (nameToSocietyMap.containsKey(name)) {
			return nameToSocietyMap.get(name);
		} else {
			throw new NotFoundException("Did not find "+name+" in the library.");
		}
	}

	/**
	 * @see #hasAgent(String)
	 * @param agentAID The AID to check
	 * @return true if the local name is found, false otherwise
	 */
	public boolean hasAgent(AID agentAID) {
		return hasAgent(agentAID.getLocalName());
	}

	/**
	 * Check if a specific local name of an agent is in the library
	 * @param name The local name to check
	 * @return true if the local name is found, false otherwise
	 */
	public boolean hasAgent(String name) {
		return nameToAgentTypeMap.containsKey(name);
	}	

	/**
	 * Check if a specific society name is found in the library
	 * @param name The society name to check
	 * @return true if the society name is found, false otherwise
	 */
	public boolean hasSociety(String name) {
		return nameToSocietyMap.containsKey(name);
	}

	/**
	 * Retrive the IRunnableAgentInstance for a specific AID
	 * @param agentAID The AID of the agent to get the model of
	 * @return The IRunnableAgentInstance of the model, the AID belongs to
	 */
	public IRunnableAgentInstance getAgent(AID agentAID) {
		return nameToAgentTypeMap.get(agentAID.getLocalName());
	}
}
