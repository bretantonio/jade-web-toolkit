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

import java.util.*;
import jade.tools.ascml.model.jibx.dependency.AbstractDependency;
import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.absmodel.IFunctional;
import jade.tools.ascml.absmodel.IInvariant;

/**
 *  This class describes the properties of an societyInstance-reference.
 */
public class Functional implements IFunctional
{							   
	/** The dependencies, that must be fulfilled in order to launch the SocietyInstance */
	protected ArrayList<IDependency> dependencyList;

	protected ArrayList<IInvariant> invariantList;

	// -------------------------------------------------------------------------------------

	/**
	 *  Get all dependency-models that are needed for the state FUNCTIONAL.
	 *  @return An array containing all dependency-models.
	 */
	public IDependency[] getDependencies()
	{
		AbstractDependency[] returnArray = new AbstractDependency[dependencyList.size()];
		dependencyList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Get all dependency-models that are needed for the state FUNCTIONAL.
	 *  @return A list containing all dependency-models.
	 */
	public List<IDependency> getDependencyList()
	{
		return dependencyList;
	}

	/**
	 * Add a dependency to the FUNCTIONAL-state.
	 * @param dependency  The DependencyModel to add.
	 */
	public void addDependency(AbstractDependency dependency)
	{
		dependencyList.add(dependency);
	}

	/**
	 * Remove a dependency from the FUNCTIONAL-state.
	 * @param dependency  The DependencyModel to remove.
	 */
	public void removeDependency(IDependency dependency)
	{
		dependencyList.remove(dependency);
	}

	/**
	 *  Get all invariants for the state FUNCTIONAL.
	 *  @return  An array containing all invariants.
	 */
	public IInvariant[] getInvariants()
	{
		Invariant[] returnArray = new Invariant[invariantList.size()];
		invariantList.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Add an invariant to the FUNCTIONAL-state.
	 * @param invariant  The invariant to add.
	 */
	public void addInvariant(Invariant invariant)
	{
		invariantList.add(invariant);
	}

	/**
	 * Remove an invariant from the FUNCTIONAL-state.
	 * @param invariant  The invariant to remove.
	 */
	public void removeInvariant(IInvariant invariant)
	{
		invariantList.remove(invariant);
	}

	public String toString()
	{
		String str = "";
		str += "Dependencies: " + dependencyList + "\n";
		str += "Invariants  : " + invariantList + "\n";
		return str;
	}
}
