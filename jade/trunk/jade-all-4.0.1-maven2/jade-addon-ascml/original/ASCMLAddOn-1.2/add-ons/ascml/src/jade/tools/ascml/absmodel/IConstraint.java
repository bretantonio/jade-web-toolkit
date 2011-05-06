package jade.tools.ascml.absmodel;

/**
 * 
 */
public interface IConstraint
{
	/**
	 * Get the constraint.
	 * @return  The constraint as String.
	 */
	String getConstraint();

	/**
	 * Set the constraint.
	 * @param constraint  The constraint as String.
	 */
	void setConstraint(String constraint);

	/**
	 * Get the name of the constraint.
	 * @return  The name of the constraint.
	 */
	String getName();

	/**
	 * Set the name of the constraint.
	 * @param name  The name of the constraint.
	 */
	void setName(String name);
}
