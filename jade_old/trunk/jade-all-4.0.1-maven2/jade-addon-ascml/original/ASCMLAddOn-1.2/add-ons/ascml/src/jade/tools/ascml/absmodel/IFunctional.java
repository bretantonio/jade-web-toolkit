package jade.tools.ascml.absmodel;

import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.model.jibx.dependency.AbstractDependency;
import jade.tools.ascml.model.jibx.Invariant;

import java.util.List;

/**
 * 
 */
public interface IFunctional
{
	/**
	 *  Get all dependency-models that are needed for the state FUNCTIONAL.
	 *  @return An array containing all dependency-models.
	 */
	IDependency[] getDependencies();

	/**
	 *  Get all dependency-models that are needed for the state FUNCTIONAL.
	 *  @return A list containing all dependency-models.
	 */
	List<IDependency> getDependencyList();

	/**
	 * Add a dependency to the FUNCTIONAL-state.
	 * @param dependency  The DependencyModel to add.
	 */
	void addDependency(AbstractDependency dependency);

	/**
	 * Remove a dependency from the FUNCTIONAL-state.
	 * @param dependency  The DependencyModel to remove.
	 */
	void removeDependency(IDependency dependency);

	/**
	 *  Get all invariants for the state FUNCTIONAL.
	 *  @return  An array containing all invariants.
	 */
	IInvariant[] getInvariants();

	/**
	 * Add an invariant to the FUNCTIONAL-state.
	 * @param invariant  The invariant to add.
	 */
	void addInvariant(Invariant invariant);

	/**
	 * Remove an invariant from the FUNCTIONAL-state.
	 * @param invariant  The invariant to remove.
	 */
	void removeInvariant(IInvariant invariant);

	String toString();
}
