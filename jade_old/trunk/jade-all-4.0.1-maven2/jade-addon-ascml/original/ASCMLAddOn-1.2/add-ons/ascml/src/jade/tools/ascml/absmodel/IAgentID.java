package jade.tools.ascml.absmodel;

/**
 * 
 */
public interface IAgentID
{
	String NAME_UNKNOWN = "Name Unknown";

	/**
	 *  Get the name of this agent.
	 *  @return The agent's name.
	 */
	String getName();

	/**
	 *  Set the name of this agent.
	 *  @param name  The agent's name.
	 */
	void setName(String name);

	/**
	 *  Get the reference of this AgentID.
	 *  @return The agent's reference.
	 */
	String getReference();

	/**
	 *  Set the reference of this AgentID.
	 *  @param reference  The agent's reference.
	 */
	void setReference(String reference);

	/**
	 *  Get the addresses, where the agent can be found.
	 *  @return  A String-array containing the agent's addresses.
	 */
	String[] getAddresses();

	/**
	 *  Set the addresses, where the agent can be found.
	 *  @param newAddresses  A String-array containing the agent's addresses.
	 */
	void setAddresses(String[] newAddresses);

	/**
	 * Add an address where the agent can be found.
	 * @param address  A String representing the address where the agent can be found.
	 */
	void addAddress(String address);

	/**
	 * Remove an address.
	 * @param address  A String representing the address to remove.
	 */
	void removeAddress(String address);

	/**
	 *  This methods returns a simple String-representation of this model.
	 */
	String toString();

	/**
	 *  This method returns a formatted String showing the model.
	 *  @return  formatted String showing ALL information about this model.
	 */
	String toFormattedString();
}
