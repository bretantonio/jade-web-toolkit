package jade.tools.ascml.absmodel;

import jade.tools.ascml.model.jibx.Property;

/**
 * 
 */
public interface IToolOption
{
	String TOOLOPTION_SNIFF         = "sniff";
	String TOOLOPTION_DEBUG         = "debug";
	String TOOLOPTION_LOG           = "log";
	String TOOLOPTION_BENCHMARK     = "benchmark";
	String TOOLOPTION_INTROSPECTOR  = "introspector";

	/**
	 * Set the type of this ToolOption.
	 * Possible values are TOOLOPTION_SNIFF, TOOLOPTION_DEBUG, TOOLOPTION_LOG, TOOLOPTION_BENCHMARK and TOOLOPTION_INTROSPECTOR.
	 * @param type  The type of this ToolOption.
	 */
	void setType(String type);

	/**
	 * Get the type of this ToolOption.
	 * Possible values are TOOLOPTION_SNIFF, TOOLOPTION_DEBUG, TOOLOPTION_LOG, TOOLOPTION_BENCHMARK and TOOLOPTION_INTROSPECTOR.
	 * @return  The type of this ToolOption.
	 */
	String getType();

	/**
	 * Set whether this ToolOption is enabled or not.
	 * @param enabled  true, if this ToolOption is enabled, false otherwise.
	 */
	void setEnabled(boolean enabled);

	/**
	 * Return whether this ToolOption is enabled or not.
	 * @return  true, if this ToolOption is enabled, false otherwise.
	 */
	boolean isEnabled();

	/**
	 * Add a Property to this ToolOption.
	 * @param prop  The Property to add.
	 */
	void addProperty(Property prop);

	/**
	 * Remove a Property from this ToolOption.
	 * @param prop  The Property to remove.
	 */
	void removeProperty(IProperty prop);

	/**
	 * Get a Property of this ToolOption.
	 * @param index  The index of the Property within the internal ArrayList.
	 * @return The Property at the specified index.
	 */
	IProperty getProperty(int index);

	/**
	 * Get all Properties defined for this ToolOption.
	 * @return  An array containing all Properties defined for this ToolOption.
	 */
	IProperty[] getProperties();
}
