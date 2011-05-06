package jade.tools.ascml.absmodel;

import java.util.Vector;

/**
 * 
 */
public interface IRunnableAgentInstance extends IAbstractRunnable
{
	String getClassName();

	String getPlatformType();

	/**
	*  There are two ways of describing an agent, the get-/setDescription-methods
	*  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	*  on the other hand deal with the FIPA-agentdescription, which is more technical
	*  and used for registering and searching for agents at the directory facilitator for example.
	*  @return the agentType's AgentDescriptionModel.
	*/
	IAgentDescription[] getAgentDescriptions();

	/**
	 *  Get the tool-options of this runnable model.
	 *  @return  runnable agentinstance's tool-options or null, if it has no tool-options
	 */
	IToolOption[] getToolOptions();

	IToolOption getToolOption(String typeName);

	/**
	 *  Check if a toolOption is set.
	 *  @param typeName  toolOption's type-name.
	 *  @return  'true' if tooloption is set, 'false' otherwise.
	 */
	boolean hasToolOption(String typeName);

	/**
	 * Set the tool options.
	 * This method may only be used by the RunnableFactory !!!
	 * @param toolOptions  A HashMap containing the name of the toolOption
	 * as key and the properties (String-Array) as value.
	 */
	void setToolOptions(IToolOption[] toolOptions);

	/**
	 *  Get the agent's type-model.
	 *  @return  agent's type.
	 */
	IAgentType getType();

	/**
	 *  Get an agent's parameter.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	IParameter getParameter(String name);

	/**
	 *  Get all of the agent's parameters. The returned HashMap has parameter-names as keys and
	 *  the parameter's attributes as values. The Attributes of a parameter are represented by a HashMap.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  So, this method returns a HashMap with name-String as key and attribute-HashMap as value.
	 *  @return  All of the agent's parameters.
	 */
	IParameter[] getParameters();

	/**
	 * Set the parameters.
	 * This method may only be used by the RunnableFactory !!!
	 * @param newParameters  An Array of new parameters, overwriting the current ones.
	 */
	void setParameters(IParameter[] newParameters);

	/**
	 *  Get an agent's parameter set.
	 *  Possible keys in this HashMap are <i> name, type, optional, description, value, constraints </i>.
	 *  @param name  The parameter's name.
	 *  @return  an agent's parameter.
	 */
	IParameterSet getParameterSet(String name);

	/**
	 *  Get all agent-parameter-sets.
	 *  @return  The agent's parameter-sets.
	 */
	IParameterSet[] getParameterSets();

	/**
	 * Set the parameterSets.
	 * This method may only be used by the RunnableFactory !!!
	 * @param newParameterSets  An Array of new parameterSets, overwriting the current ones.
	 */
	void setParameterSets(IParameterSet[] newParameterSets);

	Vector getModelChangedListener();
}
