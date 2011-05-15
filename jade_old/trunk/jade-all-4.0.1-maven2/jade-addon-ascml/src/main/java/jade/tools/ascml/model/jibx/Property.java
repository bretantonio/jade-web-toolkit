package jade.tools.ascml.model.jibx;

import jade.tools.ascml.absmodel.IProperty;

public class Property implements IProperty
{
	protected String property;

	protected String name;

    /**
	 * Get the property-value.
	 * @return  The property-value.
	 */
	public String getProperty()
	{
		if (property == null)
			property = "";
		return this.property;
	}

	/**
	 * Set the property-value.
	 * @param property  The property-value.
	 */
	public void setProperty(String property)
	{
		this.property = property;
	}

	/**
	 * Get the name of the property.
	 * @return  The name of the property.
	 */
	public String getName()
	{
		if (name == null)
			name = "";
		return this.name;
	}

	/**
	 * Set the name of the property.
	 * @param name  The name of the property. 
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public String toString()
	{
		String str = "";
		str += getName() + "=" + getProperty();
		return str;
	}
}
