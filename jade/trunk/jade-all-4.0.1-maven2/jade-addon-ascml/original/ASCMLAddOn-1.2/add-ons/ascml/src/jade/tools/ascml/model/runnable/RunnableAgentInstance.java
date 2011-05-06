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


package jade.tools.ascml.model.runnable;

import java.util.*;
import jade.tools.ascml.onto.Born;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.*;

/**
 *  Model-object containing all required information about a running SocietyInstance. 
 */
public class RunnableAgentInstance extends AbstractRunnable implements IRunnableAgentInstance
{
	protected String className;
	protected String platformType;
	protected IAgentDescription[] agentDescriptions;
	protected IParameter[] parameters;
	protected IParameterSet[] parameterSets;
	protected IToolOption[] toolOptions;

	/**
	 *  Instantiate a new model and initialize some variables 
	 */
	public RunnableAgentInstance(String name, Object parentModel, IDependency[] dependencies, Vector modelChangedListener,
									  IAbstractRunnable parentRunnable, String className, String platformType, IParameter[] parameters, IParameterSet[] parameterSets,
									  IAgentDescription[] agentDescriptions, IToolOption[] toolOptions)
	{
		super(name, parentModel, dependencies, modelChangedListener, parentRunnable);

		this.className = className;
		this.platformType = platformType;
		this.agentDescriptions = agentDescriptions;

		setParameters(parameters);
		setParameterSets(parameterSets);
		setToolOptions(toolOptions);

		this.status = new Born();
		this.detailedStatus = "Runnable AgentInstance has been created";
	}

	public String getClassName()
	{
		return className;
	}

	public String getPlatformType()
	{
		return platformType;
	}

 	/**
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @return the agentType's AgentDescriptionModel.
	 */
	public IAgentDescription[] getAgentDescriptions()
	{
		return agentDescriptions;
	}

	/**
	 *  Get the tool-options of this runnable model.
	 *  @return  runnable agentinstance's tool-options or null, if it has no tool-options
	 */	
	public IToolOption[] getToolOptions()
	{
		return toolOptions;
	}

	public IToolOption getToolOption(String typeName)
	{
		for (int i=0; i < toolOptions.length; i++)
		{
			if (toolOptions[i].getType().equals(typeName))
				return toolOptions[i];
		}
		return null;
	}

	/**
	 *  Check if a toolOption is set.
	 *  @param typeName  toolOption's type-name.
	 *  @return  'true' if tooloption is set, 'false' otherwise.
	 */
	public boolean hasToolOption(String typeName)
	{
		for (int i=0; i < toolOptions.length; i++)
		{
			if (toolOptions[i].getType().equals(typeName))
				return true;
		}
		return false;
	}

	/**
	 * Set the tool options.
	 * This method may only be used by the RunnableFactory !!!
	 * @param toolOptions  A HashMap containing the name of the toolOption
	 * as key and the properties (String-Array) as value.
	 */
	public void setToolOptions(IToolOption[] toolOptions)
	{
		if (toolOptions != null)
			this.toolOptions = toolOptions;
		else
			this.toolOptions = new IToolOption[0];
	}

	/**
	 *  Get the agent's type-model.
	 *  @return  agent's type.
	 */
	public IAgentType getType()
	{
		return (IAgentType)parentModel;
	}

	/**
	 *  Get an agent's parameter.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	public IParameter getParameter(String name)
	{
		for (int i=0; i < parameters.length; i++)
		{
			if (name.equals(parameters[i].getName()))
				return parameters[i];
		}
		return null;
	}

	/**
	 *  Get all of the agent's parameters. The returned HashMap has parameter-names as keys and
	 *  the parameter's attributes as values. The Attributes of a parameter are represented by a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  So, this method returns a HashMap with name-String as key and attribute-HashMap as value.
	 *  @return  All of the agent's parameters.
	 */
	public IParameter[] getParameters()
	{
		return parameters;
	}

	/**
	 * Set the parameters.
	 * This method may only be used by the RunnableFactory !!!
	 * @param newParameters  An Array of new parameters, overwriting the current ones.
	 */
	public void setParameters(IParameter[] newParameters)
	{
		this.parameters = newParameters;
	}

	/**
	 *  Get an agent's parameter set.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	public IParameterSet getParameterSet(String name)
	{
		System.err.println("RunnableAgentInstance.getParameterSet: Implement parameterValues !!!");
		for (int i=0; i < parameterSets.length; i++)
		{
			if (name.equals(parameterSets[i].getName()))
				return parameterSets[i];
		}
		return null;
	}

	/**
	 *  Get all agent-parameter-sets.
	 *  @return  The agent's parameter-sets.
	 */
	public IParameterSet[] getParameterSets()
	{
		return parameterSets;
	}

	/**
	 * Set the parameterSets.
	 * This method may only be used by the RunnableFactory !!!
	 * @param newParameterSets  An Array of new parameterSets, overwriting the current ones.
	 */
	public void setParameterSets(IParameterSet[] newParameterSets)
	{
		this.parameterSets = newParameterSets;
	}

    public Vector getModelChangedListener()
	{
		return modelChangedListener;
	}
}
