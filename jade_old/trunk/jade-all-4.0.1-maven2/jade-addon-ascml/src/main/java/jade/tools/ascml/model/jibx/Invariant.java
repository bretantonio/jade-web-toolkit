package jade.tools.ascml.model.jibx;

import jade.tools.ascml.absmodel.IInvariant;

public class Invariant implements IInvariant
{
	protected String invariant;

	/**
	 * Get the invariant.
	 * @return  The invariant as String.
	 */
	public String getInvariant()
	{
		if (invariant == null)
			invariant = "";
		return this.invariant;
	}

	/**
	 * Set the invariant.
	 * @param invariant  The invariant as String.
	 */
	public void setInvariant(String invariant)
	{
		this.invariant = invariant;
	}
}
