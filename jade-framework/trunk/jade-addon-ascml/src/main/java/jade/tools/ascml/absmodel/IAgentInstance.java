package jade.tools.ascml.absmodel;

import jade.tools.ascml.absmodel.dependency.IDependency;

import java.util.List;

/**
 * 
 */
public interface IAgentInstance
{
	/**
	 * This constant is used to indicate, that this model has successfully been loaded.
	 */
	String STATUS_OK		= "successfully loaded";
	/**
	 * This constant is used to indicate, that at least one error occurred while loading the model.
	 */
	String STATUS_ERROR		= "loading error";
	/**
	 * This constant is used to indicate, that at least one referenced agenttype
	 * has NOT been loaded successfully
	 */
	String STATUS_REFERENCE_ERROR	= "erroneous reference";
	String NAME_UNKNOWN = "Unknown";

	/**
	 *  Get the SocietyInstance-model to which this AgentInstance belongs.
	 *  @return SocietyInstance-model to which this AgentInstance belongs.
	 */
	ISocietyInstance getParentSocietyInstance();

	/**
	 *  Set the SocietyInstance-model to which this AgentInstance belongs.
	 *  @param parentSocietyInstance SocietyInstance-model to which this AgentInstance belongs.
	 */
	void setParentSocietyInstance(ISocietyInstance parentSocietyInstance);

	/**
	 *  Set the name of this AgentInstance.
	 *  @param name  The AgentInstance's name.
	 */
	void setName(String name);

	/**
	 *  Get the name of this AgentInstance.
	 *  @return AgentInstance's name.
	 */
	String getName();

	/**
	 * Set the AgentInstance's type-name. The type-name is used to identify the type by it's name
	 * in order to set the correct type-object later on.
	 * @param typeName  The AgentInstance's type-name.
	 */
	void setTypeName(String typeName);

	/**
	 * Get the AgentInstance's type-name. The type-name is used to identify the type by it's name
	 * in order to set the correct type-object later on.
	 * @return  The AgentInstance's type-name.
	 */
	String getTypeName();

	/**
	 * Get the AgentInstance's AgentType.
	 * @return  The AgentType-object.
	 */
	IAgentType getType();

	/**
	 * Set the AgentInstance's AgentType.
	 * @param type  The AgentType-object.
	 */
	void setType(IAgentType type);

	/**
	 *  Set the number of AgentInstances to start.
	 *  When an AgentInstance is going to start, multiple runnable-models
	 *  may be created out of this AgentInstance.
	 *  @param quantity  the number of AgentInstances to start.
	 */
	void setQuantity(String quantity);

	/**
	 *  Get the number of AgentInstances to start.
	 *  When an AgentInstance is going to start, multiple runnable-models
	 *  may be created out of this AgentInstance.
	 *  @return  The number of AgentInstances to start.
	 */
	long getQuantity();

	/**
	 *  Set the naming-scheme in case multiple runnable-instances of this
	 *  AgentInstance should be started.
	 *  The naming-scheme is used to name the runnables in order to make sure
	 *  that their names are unique inside the ASCML. The String is appended at
	 *  the Name of the runnable. You may choose any String you like.
	 *  There are a few placeholders that might be used in order order to ensure
	 *  uniqueness of names.
	 *  '%N' is replaced with an ID, starting by 0 and ascending
	 *       to the number of runnable models of this type created so far
	 *  '%T' is replaced at the time of creation of a runnable by the
	 *       current time in milliseconds
	 *  '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 *  Depending on how much runnable-instances you want to create, you should combine
	 *  the placeholders.
	 *  One example for an naming-scheme could be:
	 *  '%N_(time:%Tms)'
	 *  ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 *  @param namingScheme  the naming-scheme used to name runnable AgentInstances
	 *                       in case multiple runnable-instances should be started.
	 */
	void setNamingScheme(String namingScheme);

	/**
	 *  Get the naming-scheme in case multiple runnable-instances of this
	 *  AgentInstance should be started.
	 *  The naming-scheme is used to name the runnables in order to make sure
	 *  that their names are unique inside the ASCML. The String is appended at
	 *  the Name of the runnable. You may choose any String you like.
	 *  There are a few placeholders that might be used in order order to ensure
	 *  uniqueness of names.
	 *  '%N' is replaced with an ID, starting by 0 and ascending
	 *       to the number of runnable models of this type created so far
	 *  '%T' is replaced at the time of creation of a runnable by the
	 *       current time in milliseconds
	 *  '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 *  Depending on how much runnable-instances you want to create, you should combine
	 *  the placeholders.
	 *  One example for an naming-scheme could be:
	 *  '%N_(time:%Tms)'
	 *  ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 *  @return  The naming-scheme used to name runnable AgentInstances
	 *           in case multiple runnable-instances should be started.
	 */
	String getNamingScheme();

	/**
	 * Add a ToolOption to this agent, e.g. turn on logging, sniffing, benchmarking, etc.
	 * @param toolOptionType  The ToolOption to add.
	 */
	void addToolOption(String toolOptionType);

	/**
	 * Remove a ToolOption from this agent, e.g. turn off logging sniffing, benchmarking, etc.
	 * @param toolOptionType  The ToolOption to remove.
	 */
	void removeToolOption(String toolOptionType);

	/**
	 * Get a ToolOption.
	 * @return The ToolOption contained in the internal ArrayList of ToolOptions at the specified index.
	 */
	IToolOption getToolOption(int index);

	/**
	 * Get all ToolOptions.
	 * @return An array containing all ToolOptions specified for this AgentInstance.
	 */
	IToolOption[] getToolOptions();

	/**
	 *  Check if a toolOption is set.
	 *  @param typeName  toolOption's type-name.
	 *  @return  'true' if tooloption is set, 'false' otherwise.
	 */
	boolean hasToolOption(String typeName);

	/**
	 *  Get all of the agent's dependency-models.
	 *  @return An array containing all of the agent's dependency-models.
	 */
	IDependency[] getDependencies();

	/**
	 *  Get all of the agent's dependency-models as a list.
	 *  @return A list containing all of the agent's dependency-models.
	 */
	List<IDependency> getDependencyList();

	/**
	 * Add a dependency to this agent's dependencies.
	 * @param dependency  The DependencyModel.
	 */
	void addDependency(IDependency dependency);

	/**
	 * Remove a dependency from this agent's dependency-list.
	 * @param dependency  The Dependency-model to remove.
	 */
	void removeDependency(IDependency dependency);

	/**
	 * Remove a dependency from this agent's dependency-list.
	 * @param dependencyIndex  The index of the Dependency-model
	 *                         to remove within the inner dependency-list.
	 */
	void removeDependency(int dependencyIndex);

	/**
	 * Set the status of this model. The status indicates, whether loading was successful or not.
	 * @param newStatus  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	void setStatus(String newStatus);

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	String getStatus();

	/**
	 * Add a parameter to this agent. The parameter overwrites the value of the
	 * matching type-parameter of this agent's type. Note: The parameters set within the
	 * AgentInstance may only define a name and a value, and overwrite a possible value
	 * defined by the parameters of the AgentType.
	 * @param parameter  The Parameter-object to add.
	 */
	void addParameter(IParameter parameter);

	/**
	 * Remove a parameter from this agent.
	 * @param parameterName  The name of the parameter to remove.
	 */
	void removeParameter(String parameterName);

	/**
	 * Get an agent's parameter.
	 * When the instance does not overwrite a parametervalue of a parameter, which
	 * is already defined by the type, the type's parameter is returned.
	 * @param name  The name of the Parameter
	 * @return  An agent's parameter or null if no parameter with the given name exists.
	 */
	IParameter getParameter(String name);

	/**
	 * Get all of the agent's parameter.
	 * When the instance does not overwrite a parametervalue of a parameter, which
	 * is already defined by the type, the type's parameter is returned.
	 * @return  All of the agent's parameters as Parameter-Array or null if no parameters exist.
	 */
	IParameter[] getParameters();

	/**
	 * Add a ParameterSet to this agent. The ParameterSet overwrites the values of the
	 * matching type-parameterSet of this agent's type. Note: The parameterSets within the
	 * AgentInstance may only define a name and a list of values, and overwrite possible values
	 * defined by the ParameterSets of the AgentType.
	 * @param parameterSet  The ParameterSet-object.
	 */
	void addParameterSet(IParameterSet parameterSet);

	/**
	 * Remove a parameterSet from this agent.
	 * @param parameterSetName  The name of the parameterSet to remove.
	 */
	void removeParameterSet(String parameterSetName);

	/**
	 * Get an agent's ParameterSet.
	 * When the instance does not overwrite parametervalues of a ParameterSet, which
	 * is already defined by the type, the type's ParameterSet is returned.
	 * @param name  The name of the ParameterSet
	 * @return  The agent's ParameterSet or null if no ParameterSet with the given name exists.
	 */
	IParameterSet getParameterSet(String name);

	/**
	 * Get all of the agent's parameterSets.
	 * When the instance does not overwrite a parameterSet, which
	 * is already defined by the type, the type's parameterSet is returned.
	 * @return  All of the agent's parameterSets as ParameterSet-Array or null if no parameterSets exist.
	 */
	IParameterSet[] getParameterSets();

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed.
	 * If there are many events thrown in very short time-intervals these events will not be
	 * dispatched, if they are of the same kind. Instead a timer counts down and then dispatches
	 * one event.
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	void throwModelChangedEvent(String eventCode);

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed.
	 * If there are many events thrown in very short time-intervals these events will not be
	 * dispatched, if they are of the same kind. Instead a timer counts down and then dispatches
	 * one event.
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 * @param userObject  This object is stored in the ModelChangedEvent. For example: When runnables
	 * are added or removed, these RunnableModels are stored as userObjects to access them later on.
	 */
	void throwModelChangedEvent(String eventCode, Object userObject);

	/**
	 * This method returns a short String with the agentInstance-name.
	 * It is used by the RepositoryTree for example, to name the nodes.
	 *
	 * @return String containing the name of this agentInstance
	 */
	String toString();

	/**
	 * This method returns a formatted String showing the agentInstance-model.
	 *
	 * @return formatted String showing ALL information about this agentInstance.
	 */
	String toFormattedString();
}
