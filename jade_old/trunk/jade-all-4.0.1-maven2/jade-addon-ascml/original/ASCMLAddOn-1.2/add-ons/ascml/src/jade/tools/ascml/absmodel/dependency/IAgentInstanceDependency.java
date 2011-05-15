package jade.tools.ascml.absmodel.dependency;

import jade.tools.ascml.absmodel.IAgentID;
import jade.tools.ascml.absmodel.IProvider;

/**
 * 
 */
public interface IAgentInstanceDependency extends IDependency
{
	/**
	 * Get the name of the AgentInstance to depend on.
	 * @return The name of the AgentInstance to depend on.
	 */
	String getName();

	/**
	 * Set the name of the AgentInstance to depend on.
	 * @param name  The name of the AgentInstance to depend on.
	 */
	void setName(String name);

	/**
	 * Get the status of the AgentInstance to depend on.
	 * @return The status of the AgentInstance to depend on.
	 */
	String getStatus();

	/**
	 * Set the status of the AgentInstance to depend on.
	 * @param status  The status of the AgentInstance to depend on.
	 */
	void setStatus(String status);

	/**
	 * Set the Provider responsible for the AgentInstance.
	 * @param provider  The Provider responsible for the AgentInstance.
	 */
	void setProvider(IProvider provider);

	/**
	 * Set the Provider responsible for the AgentInstance.
	 * @return  The Provider responsible for the AgentInstance.
	 */
	IProvider getProvider();
}
