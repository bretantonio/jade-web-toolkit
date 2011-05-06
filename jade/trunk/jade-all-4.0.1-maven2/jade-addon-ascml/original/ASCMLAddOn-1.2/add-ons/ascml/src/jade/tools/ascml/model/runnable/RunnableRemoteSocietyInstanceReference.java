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
import jade.tools.ascml.onto.Starting;
import jade.tools.ascml.model.jibx.Launcher;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.IRunnableRemoteSocietyInstanceReference;

/**
 *  This class describes the properties of a runnable societyInstance-reference.
 */
public class RunnableRemoteSocietyInstanceReference extends AbstractRunnable implements IRunnableRemoteSocietyInstanceReference
{							   
	//-------- attributes --------
	
	/** The name of the references SocietyType. */
	protected String typeName;

	/** The name of the references SocietyInstance. */
	protected String instanceName;

	/** The launcher, who shall launch this SocietyInstance. */
	protected Launcher launcher;

	//-------- constructors --------

	/**
	 *  Create a new parameter.
	 */
	public RunnableRemoteSocietyInstanceReference(String name, RunnableSocietyInstance parentModel,
												 IDependency[] dependencies, Vector runnableModelListener,
												 String typeName, String instanceName,
	                                             Launcher launcher)
	{
		super(name, parentModel, dependencies, runnableModelListener, parentModel);
		this.typeName = typeName;
		this.instanceName = instanceName;
		this.launcher = launcher;

		this.status = new Born();
		this.detailedStatus = "Runnable remote societyinstance-reference has been created";
	}

	//-------- methods --------

	/**
	 * Returns the name of this reference.
	 * @return the name of this reference.
	 */
	public String getName()
	{
		if (name == null)
			return "";
		return name;
	}

	/**
	 * Returns the FQ- name of this reference.
	 * @return the FQ-name of this reference.
	 */
	public String getFullyQualifiedName()
	{
		String fqn= getTypeName()+"."+getInstanceName()+"."+getName();
		return fqn;
	}
	
	/**
	 * Returns the name of the referenced SocietyType.
	 * @return  The name of the referenced SocietyType.
	 */
	public String getTypeName()
	{
		if (typeName == null)
			return "";
		return typeName;
	}

	/**
	 * Returns the name of the referenced SocietyInstance.
	 * @return the name of the referenced SocietyInstance.
	 */
	public String getInstanceName()
	{
		if (instanceName == null)
			return "";
		return instanceName;
	}

	/**
	 * Returns the name of the launcher, responsible for launching this reference.
	 * @return  the name of the launcher.
	 */
	public Launcher getLauncher()
	{
		return launcher;
	}

	public String toString()
	{
		String str = "";
		str += getName() + " (" + getTypeName() + "." + getInstanceName() +")";
		return str;
	}
}
