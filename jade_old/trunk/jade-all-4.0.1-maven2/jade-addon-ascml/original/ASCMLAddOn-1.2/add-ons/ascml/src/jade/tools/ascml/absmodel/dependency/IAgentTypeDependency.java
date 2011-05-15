package jade.tools.ascml.absmodel.dependency;

/**
 * 
 */
public interface IAgentTypeDependency extends IDependency
{
	/**
	 * Get the name of the AgentType to depend on.
	 * @return The name of the AgentType to depend on.
	 */
	String getName();

	/**
	 * Set the name of the AgentType to depend on.
	 * @param name  The name of the AgentType to depend on.
	 */
	void setName(String name);

	/**
	 * Get the amount of Runnables of an AgentType, needed to fulfill this dependency.
	 * @return  The amount of Runnables of an AgentType, needed to fulfill this dependency.
	 */
	String getQuantity();

	/**
	 * Get the amount of Runnables of an AgentType, needed to fulfill this dependency.
	 * @return  The amount of Runnables of an AgentType, needed to fulfill this dependency.
	 */
	int getQuantityAsInt();

	/**
	 * Set the amount of Runnables of an AgentType, needed to fulfill this dependency.
	 * @param quantity  The amount of Runnables of an AgentType, needed to fulfill this dependency.
	 */
	void setQuantity(String quantity);

	/**
	 * Set the amount of Runnables of an AgentType, needed to fulfill this dependency.
	 * @param quantity  The amount of Runnables of an AgentType, needed to fulfill this dependency.
	 */
	void setQuantity(int quantity);
}
