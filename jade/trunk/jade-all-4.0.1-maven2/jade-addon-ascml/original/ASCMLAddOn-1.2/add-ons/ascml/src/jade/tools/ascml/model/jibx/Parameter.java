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

import jade.tools.ascml.absmodel.IConstraint;
import jade.tools.ascml.absmodel.IParameter;

import java.util.ArrayList;

/**
 *  This interface describes the properties of an agent startup parameter.
 */
public class Parameter implements IParameter, Cloneable
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The type. */
	protected String type;

	/** The description */
	protected String description = "No description available";

	/** The value. */
	protected String value;

	/** The optional flag. */
	protected String optional;

	/** The constraints. */
	protected ArrayList<IConstraint> constraintList;

	//-------- constructors --------

	/**
	 *  Create a new parameter.
	 */
	public Parameter()
	{
		this.constraintList = new ArrayList<IConstraint>();
	}

	//-------- methods --------

	/**
	 *  Set the parameter's name.
	 *  @param name  The name of the parameter.
	 */
	public void setName(String name)
	{ 
		this.name = name;
	}

	/**
	 *  Get the parameter's name.
	 *  @return  The name of the parameter.
	 */
	public String getName()
	{
		if (name == null)
			name = "";
		return name;
	}

	/**
	 *  Set the parameter's type (e.g. String).
	 *  @param type  The parameter's type.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the parameter's type (e.g. String).
	 *  @return  The parameter's type.
	 */
	public String getType()
	{
		if (type == null)
			type = "String";
		return type;
	}

	/**
	 *  Set the description for this parameter.
	 *  @param description  The description for this parameter.
	 */
	public void setDescription(String description)
	{
		if ((description == null) || description.equals(""))
			description = "No description available";
		this.description = description;
	}

	/**
	 *  Get the description for this parameter.
	 *  @return The parameter's description.
	 */
	public String getDescription()
	{
		if ((description == null) || description.equals(""))
			description = "No description available";
		return description;
	}

	/**
	 *  Set the value of this parameter.
	 *  @param value The value of this parameter.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 *  Get the value of this parameter.
	 *  @return The value of this parameter.
	 */
	public String getValue()
	{
		if (value == null)
			value = "";
		return value;
	}

	/**
	 *  Set whether this parameter is optional.
	 *  @param optional  TRUE or FALSE represented as String.
	 */
	public void setOptional(String optional)
	{
		this.optional = optional;
	}

	/**
	 *  Returns true, if the parameter is optional, false if it is mandatory.
	 *  @return 'true' if optional, 'false' otherwise.
	 */
	public boolean isOptional()
	{
		if (optional == null)
			optional = "true";
		return new Boolean(optional.trim()).booleanValue();
	}

	/**
	 *  Add a constraint to this parameter.
	 *  @param constraint  The constraint to add.
	 */
	public void addConstraint(Constraint constraint)
	{
		this.constraintList.add(constraint);
	}

	/**
	 *  Remove a constraint from this parameter.
	 *  @param constraint  The constraint to remove.
	 */
	public void removeConstraint(IConstraint constraint)
	{
		this.constraintList.remove(constraint);
	}

	/**
	 *  Get all Constraints defined for this parameter.
	 *  @return  All constraints defined for this parameter.
	 */
	public IConstraint[] getConstraints()
	{
		Constraint[] returnArray = new Constraint[constraintList.size()];
		constraintList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Get a clone of this Parameter-object.
	 *  @return  A clone of this Parameter-object.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// not important
			e.printStackTrace();
		}
		return null;
	}

	public String toString()
	{
		String str = "";
		str += "Parameter : name = "+getName()+"\n";
		str += "     type = "+getType()+"\n";
		str += "     optional = "+isOptional()+"\n";
		str += "     decription = "+getDescription()+"\n";
		str += "     value = "+getValue()+"\n";
		str += "     constraints = "+getConstraints()+"\n";
		
		return str;
	}
}
