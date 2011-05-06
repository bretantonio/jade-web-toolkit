package jade.tools.ascml.absmodel;

import jade.tools.ascml.model.jibx.Constraint;

/**
 * 
 */
public interface IParameter
{
	/**
	 *  Set the parameter's name.
	 *  @param name  The name of the parameter.
	 */
	void setName(String name);

	/**
	 *  Get the parameter's name.
	 *  @return  The name of the parameter.
	 */
	String getName();

	/**
	 *  Set the parameter's type (e.g. String).
	 *  @param type  The parameter's type.
	 */
	void setType(String type);

	/**
	 *  Get the parameter's type (e.g. String).
	 *  @return  The parameter's type.
	 */
	String getType();

	/**
	 *  Set the description for this parameter.
	 *  @param description  The description for this parameter.
	 */
	void setDescription(String description);

	/**
	 *  Get the description for this parameter.
	 *  @return The parameter's description.
	 */
	String getDescription();

	/**
	 *  Set the value of this parameter.
	 *  @param value The value of this parameter.
	 */
	void setValue(String value);

	/**
	 *  Get the value of this parameter.
	 *  @return The value of this parameter.
	 */
	String getValue();

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
	 *  Add a constraint to this parameter.
	 *  @param constraint  The constraint to add.
	 */
	void addConstraint(Constraint constraint);

	/**
	 *  Remove a constraint from this parameter.
	 *  @param constraint  The constraint to remove.
	 */
	void removeConstraint(IConstraint constraint);

	/**
	 *  Get all Constraints defined for this parameter.
	 *  @return  All constraints defined for this parameter.
	 */
	IConstraint[] getConstraints();

	/**
	 *  Get a clone of this Parameter-object.
	 *  @return  A clone of this Parameter-object.
	 */
	Object clone();

	String toString();
}
