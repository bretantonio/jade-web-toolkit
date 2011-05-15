package jade.tools.ascml.absmodel;

/**
 * 
 */
public interface IProperty
{
	/**
	 * Get the property-value.
	 * @return  The property-value.
	 */
	String getProperty();

	/**
	 * Set the property-value.
	 * @param property  The property-value.
	 */
	void setProperty(String property);

	/**
	 * Get the name of the property.
	 * @return  The name of the property.
	 */
	String getName();

	/**
	 * Set the name of the property.
	 * @param name  The name of the property.
	 */
	void setName(String name);

	String toString();
}
