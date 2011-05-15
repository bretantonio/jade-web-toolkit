package jade.tools.ascml.absmodel;

import jade.tools.ascml.model.jibx.Constraint;

import java.util.List;

/**
 * 
 */
public interface IParameterSet
{
	/**
	 *  Set the name of this parameterSet.
	 *  @param name The name of this parameterSet.
	 */
	void setName(String name);

	/**
	 *  Get the name of this parameterSet.
	 *  @return The name of this parameterSet.
	 */
	String getName();

	/**
	 *  Set the type of this parameterSet (e.g. String).
	 *  @param type The type of this parameterSet.
	 */
	void setType(String type);

	/**
	 *  Get the type of this parameterSet.
	 *  @return The type of this parameterSet.
	 */
	String getType();

	/**
	 *  Set the description for this parameterSet.
	 *  @param description  The description for this parameterSet.
	 */
	void setDescription(String description);

	/**
	 *  Get the description for this parameterSet.
	 *  @return  The description for this parameterSet.
	 */
	String getDescription();

	/**
	 *  Add a value to this parameterSet.
	 *  @param value  The value to add.
	 */
	void addValue(String value);

	/**
	 *  Remove a value from this parameterSet.
	 *  @param value  The value to remove.
	 */
	void removeValue(String value);

	/**
	 *  Set the values of this parameterSet (and clear all old ones)
	 *  @param newValues  The new values to set.
	 */
	void setValues(String[] newValues);

	/**
	 *  Get all values of this parameterSet.
	 *  @return The values of this parameterSet.
	 */
	String[] getValues();

	/**
	 *  Get all values of this parameterSet.
	 *  @return The values of this parameterSet.
	 */
	List<String> getValueList();

	/**
	 *  Set whether this parameter is optional.
	 *  @param optional  TRUE or FALSE represented as String.
	 */
	void setOptional(String optional);

	/**
	 *  Returns true, if the parameter is optional, false if it is mandatory.
	 *  @return 'true' if optional, 'false' otherwise.
	 */
	boolean isOptional();

	/**
	 *  Add a constraint to this parameterSet.
	 *  @param constraint  The constraint to add.
	 */
	void addConstraint(Constraint constraint);

	/**
	 *  Remove a constraint from this parameterSet.
	 *  @param constraint  The constraint to remove.
	 */
	void removeConstraint(IConstraint constraint);

	/**
	 *  Get all Constraints defined for this parameterSet.
	 *  @return  All constraints defined for this parameterSet.
	 */
	IConstraint[] getConstraints();

	/**
	 *  Get a clone of this ParameterSet-object.
	 *  @return  A clone of this ParameterSet-object.
	 */
	Object clone();

	String toString();
}
