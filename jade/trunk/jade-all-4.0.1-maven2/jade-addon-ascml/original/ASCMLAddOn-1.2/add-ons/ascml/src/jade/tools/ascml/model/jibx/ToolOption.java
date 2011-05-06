package jade.tools.ascml.model.jibx;

import jade.tools.ascml.absmodel.IProperty;
import jade.tools.ascml.absmodel.IToolOption;

import java.util.ArrayList;

public class ToolOption implements IToolOption
{

	protected String type;

	protected String enabled;

	protected ArrayList<IProperty> propertyList = new ArrayList<IProperty>();

    // --------------------------------------------------------------------------------------

	public ToolOption()
	{

	}
	
	public ToolOption(String type)
	{
		this.type = type;
	}

	/**
	 * Set the type of this ToolOption.
	 * Possible values are TOOLOPTION_SNIFF, TOOLOPTION_DEBUG, TOOLOPTION_LOG, TOOLOPTION_BENCHMARK and TOOLOPTION_INTROSPECTOR.
	 * @param type  The type of this ToolOption.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Get the type of this ToolOption.
	 * Possible values are TOOLOPTION_SNIFF, TOOLOPTION_DEBUG, TOOLOPTION_LOG, TOOLOPTION_BENCHMARK and TOOLOPTION_INTROSPECTOR.
	 * @return  The type of this ToolOption.
	 */
	public String getType()
	{
		if (type == null)
			type = "";
		return this.type;
	}

	/**
	 * Set whether this ToolOption is enabled or not.
	 * @param enabled  true, if this ToolOption is enabled, false otherwise.
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = ""+enabled;
	}

	/**
	 * Return whether this ToolOption is enabled or not.
	 * @return  true, if this ToolOption is enabled, false otherwise.
	 */
	public boolean isEnabled()
	{
		if (enabled == null)
			enabled = "true";
		return new Boolean(enabled);
	}

	/**
	 * Add a Property to this ToolOption.
	 * @param prop  The Property to add.
	 */
	public void addProperty(Property prop)
	{
		this.propertyList.add(prop);
	}

	/**
	 * Remove a Property from this ToolOption.
	 * @param prop  The Property to remove.
	 */
	public void removeProperty(IProperty prop)
	{
		this.propertyList.remove(prop);
	}

	/**
	 * Get a Property of this ToolOption.
	 * @param index  The index of the Property within the internal ArrayList.
	 * @return The Property at the specified index.
	 */
	public IProperty getProperty(int index)
	{
		return propertyList.get(index);
	}

	/**
	 * Get all Properties defined for this ToolOption.
	 * @return  An array containing all Properties defined for this ToolOption.
	 */
	public IProperty[] getProperties()
	{
		Property[] returnArray = new Property[propertyList.size()];
		propertyList.toArray(returnArray);
		return returnArray;
	}
}
