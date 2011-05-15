package jade.tools.ascml.model.jibx;

import jade.tools.ascml.absmodel.IConstraint;

public class Constraint implements IConstraint
{
	protected String constraint;

	protected String name;

    /**
	 * Get the constraint.
	 * @return  The constraint as String.
	 */
	public String getConstraint()
	{
		if (constraint == null)
			constraint =  "";
		return this.constraint;
	}

	/**
	 * Set the constraint.
	 * @param constraint  The constraint as String.
	 */
	public void setConstraint(String constraint)
	{
		this.constraint = constraint;
	}

	/**
	 * Get the name of the constraint.
	 * @return  The name of the constraint.
	 */
	public String getName()
	{
		if (name == null)
			name = "";
		return this.name;
	}

	/**
	 * Set the name of the constraint.
	 * @param name  The name of the constraint.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

}
