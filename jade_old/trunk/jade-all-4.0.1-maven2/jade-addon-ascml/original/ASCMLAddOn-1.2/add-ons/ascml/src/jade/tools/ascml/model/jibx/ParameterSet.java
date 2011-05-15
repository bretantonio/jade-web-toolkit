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
import jade.tools.ascml.absmodel.IParameterSet;

import java.util.ArrayList;
import java.util.List;

/**
 *  This interface describes the properties of an agent startup parameter set.
 */
public class ParameterSet implements IParameterSet, Cloneable
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The type. */
	protected String type;

	/** The description */
	protected String description = "No description available";

	/** The values. */
	public ArrayList<String> valueList;

	/** The optional flag. */
	protected String optional;

	/** The constraints. */
	protected ArrayList<IConstraint> constraintList;

	//-------- constructors --------

	/**
	 *  Create a new parameter.
	 */
	public ParameterSet()
	{
		this.valueList = new ArrayList<String>();
		this.constraintList = new ArrayList<IConstraint>();
	}

	//-------- methods --------

	/**
	 *  Set the name of this parameterSet.
	 *  @param name The name of this parameterSet.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the name of this parameterSet.
	 *  @return The name of this parameterSet.
	 */
	public String getName()
	{
		if (name == null)
			name = "";
		return name;
	}

	/**
	 *  Set the type of this parameterSet (e.g. String).
	 *  @param type The type of this parameterSet.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the type of this parameterSet.
	 *  @return The type of this parameterSet.
	 */
	public String getType()
	{
		if (type == null)
			type = "String";
		return type;
	}

	/**
	 *  Set the description for this parameterSet.
	 *  @param description  The description for this parameterSet.
	 */
	public void setDescription(String description)
	{
		if ((description == null) || description.equals(""))
			description = "No description available";
		this.description = description;
	}

	/**
	 *  Get the description for this parameterSet.
	 *  @return  The description for this parameterSet.
	 */
	public String getDescription()
	{
		if ((description == null) || description.equals(""))
			description = "No description available";
		return description;
	}

	/**
	 *  Add a value to this parameterSet.
	 *  @param value  The value to add.
	 */
	public void addValue(String value)
	{
		valueList.add(value);
	}

	/**
	 *  Remove a value from this parameterSet.
	 *  @param value  The value to remove.
	 */
	public void removeValue(String value)
	{
		valueList.remove(value);
	}

	/**
	 *  Set the values of this parameterSet (and clear all old ones)
	 *  @param newValues  The new values to set.
	 */
	public void setValues(String[] newValues)
	{
		valueList.clear();
		for (int i=0; i < newValues.length; i++)
		{
			valueList.add(newValues[i]);
		}
	}
	
	/**
	 *  Get all values of this parameterSet.
	 *  @return The values of this parameterSet.
	 */
	public String[] getValues()
	{
		String[] returnArray = new String[valueList.size()];
		valueList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Get all values of this parameterSet.
	 *  @return The values of this parameterSet.
	 */
	public List<String> getValueList()
	{
		return valueList;
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
	 *  Add a constraint to this parameterSet.
	 *  @param constraint  The constraint to add.
	 */
	public void addConstraint(Constraint constraint)
	{
		this.constraintList.add(constraint);
	}

	/**
	 *  Remove a constraint from this parameterSet.
	 *  @param constraint  The constraint to remove.
	 */
	public void removeConstraint(IConstraint constraint)
	{
		this.constraintList.remove(constraint);
	}

	/**
	 *  Get all Constraints defined for this parameterSet.
	 *  @return  All constraints defined for this parameterSet.
	 */
	public IConstraint[] getConstraints()
	{
		Constraint[] returnArray = new Constraint[constraintList.size()];
		constraintList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Get a clone of this ParameterSet-object.
	 *  @return  A clone of this ParameterSet-object.
	 */
	public Object clone()
	{
		try
		{
			ParameterSet parameterSet = (ParameterSet)super.clone();
			parameterSet.valueList = (ArrayList<String>)valueList.clone();
			return parameterSet;
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
		str += "ParameterSet : name = "+getName()+"\n";
		str += "     type = "+getType()+"\n";
		str += "     optional = "+isOptional()+"\n";
		str += "     decription = "+getDescription()+"\n";

		str += "     values = [";
		Object[] values = getValues();
		for (int i=0; i < values.length; i++)
		{
			str += values[i]+",";
		}
		str += "]\n";
		
		str += "     constraints = "+getConstraints()+"\n";
		
		return str;
	}
}
