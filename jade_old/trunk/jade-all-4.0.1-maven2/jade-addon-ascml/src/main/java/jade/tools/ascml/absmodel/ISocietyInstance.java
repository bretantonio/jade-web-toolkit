package jade.tools.ascml.absmodel;

/**
 * 
 */
public interface ISocietyInstance
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
	 *  Get the SocietyType in which this SocietyInstance is declared.
	 *  @return SocietyType in which this SocietyInstance is declared.
	 */
	ISocietyType getParentSocietyType();

	/**
	 *  Set the SocietyType in which this SocietyInstance is declared.
	 *  @param parentSociety  SocietyType in which this SocietyInstance is declared.
	 */
	void setParentSocietyType(ISocietyType parentSociety);

	/**
	 *  Set the SocietyInstance's name.
	 *  @param name  name of the SocietyInstance.
	 */
	void setName(String name);

	/**
	 *  Get the SocietyInstance's name
	 *  @return name of the SocietyInstance.
	 */
	String getName();

	/**
	 *  Returns the fully-qualified name of the SocietyInstance.
	 *  The name is composed of the fully-qualified name of the SocietyType, to which this
	 *  instance belongs and the name of this SocietyInstance.
	 *  For example my.societyTypeName.SocietyInstanceName (e.g. examples.party.BirthdaySociety.SmallParty)
	 *  would be a correct 'fully qualified' name.
	 *  @return name of the SocietyInstance.
	 */
	String getFullyQualifiedName();

	/**
	 *  Set the SocietyInstance's description.
	 *  @param description  description of the SocietyInstance.
	 */
	void setDescription(String description);

	/**
	 *  Get the SocietyInstance's description
	 *  @return description-String of the SocietyInstance.
	 */
	String getDescription();

	/**
	 *  Set the number of SocietyInstances, that should be started.
	 *  When a SocietyInstance is going to start, multiple runnable-models
	 *  may be created out of this SocietyInstance.
	 *  @param quantity  Number of SocietyInstances to start.
	 */
	void setQuantity(String quantity);

	/**
	 *  Get the number of SocietyInstances, that should be started.
	 *  When a SocietyInstance is going to start, multiple runnable-models
	 *  may be created out of this SocietyInstance.
	 *  @return  Number of SocietyInstances to start.
	 */
	long getQuantity();

	/**
	 *  Set the naming-scheme used for naming the instances in case multiple instances of this
	 *  SocietyInstance should be started.
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
	 *  @param namingScheme  the naming-scheme used to name runnable SocietyInstances
	 *                       in case multiple instances should be started.
	 */
	void setNamingScheme(String namingScheme);

	/**
	 *  Get the naming-scheme used for naming the instances in case multiple instances of this
	 *  SocietyInstance should be started.
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
	 *  @return the naming-scheme used to name runnable SocietyInstances
	 *          in case multiple instances should be started.
	 */
	String getNamingScheme();

	/**
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	String getStatus();

	/**
	 * Set the status of this model. The status indicates, whether loading was successful or not.
	 * @param newStatus  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	void setStatus(String newStatus);

	/**
	 * Set the Functional-model on which the FUNCTIONAL-status relies.
	 * @param functional  The model-object containing the funtional-dependencies and invariants.
	 */
	void setFunctional(IFunctional functional);

	/**
	 * Get the Functional-model on which the FUNCTIONAL-status relies.
	 * @return  The model-object containing the funtional-dependencies and invariants.
	 */
	IFunctional getFunctional();

	/**
	 *  Add an AgentInstanceModel to this SocietyInstance.
	 *  @param agentInstance  The AgentInstance to add.
	 */
	void addAgentInstance(IAgentInstance agentInstance);

	/**
	 *  Remove an AgentInstance from this society.
	 *  @param model  The AgentInstance to remove
	 */
	void removeAgentInstance(IAgentInstance model);

	/**
	 *  Get an AgentInstance.
	 *  @param index  The model at the specified index within the inner ArrayList
	 *                containing all the AgentInstances
	 *  @return  The AgentInstance at the specified index
	 */
	IAgentInstance getAgentInstanceModel(int index);

	/**
	 *  Get all AgentInstances contained in this SocietyInstance.
	 *  @return  An array containing AgentInstances.
	 */
	IAgentInstance[] getAgentInstanceModels();

	/**
	 *  Add a  SocietyInstanceReference to this SocietyInstance.
	 *  @param societyInstanceReference  The SocietyInstanceReference to add.
	 */
	void addSocietyInstanceReference(ISocietyInstanceReference societyInstanceReference);

	/**
	 *  Remove a SocietyInstanceReference from this society.
	 *  @param model  The SocietyInstanceReference to remove.
	 */
	void removeSocietyInstanceReference(ISocietyInstanceReference model);

	/**
	 *  Get a SocietyInstanceReference.
	 *  @param  index  The index of the reference's within the reference-ArrayList.
	 *  @return  The SocietyInstanceReference at the specified index.
	 */
	ISocietyInstanceReference getSocietyInstanceReference(int index);

	/**
	 *  Get all SocietyInstanceReferences contained in this SocietyInstance.
	 *  @return  An array containing all the SocietyInstanceReference-models
	 */
	ISocietyInstanceReference[] getSocietyInstanceReferences();

	/**
	 * Get the amount of local AgentInstances, that are defined by this SocietyInstance
	 * and all locally referenced subsocieties. The 'quantity' of each instance is hereby considered.
	 * @return  agentinstance-count of local agentinstances.
	 */
	int getLocalAgentInstanceCount();

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
	 *  This method returns a short String with the scenario-name.
	 *  It is used by the RepositoryTree for example, to name the nodes.
	 *  @return  String containing the name of this scenario.
	 */
	String toString();

	/**
	 *  This method returns a formatted String showing the scenario-model.
	 *  @return  formatted String showing ALL information about this scenario.
	 */
	String toFormattedString();
}
