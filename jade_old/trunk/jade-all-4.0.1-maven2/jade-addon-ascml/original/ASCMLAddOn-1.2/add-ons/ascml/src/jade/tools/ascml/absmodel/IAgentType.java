package jade.tools.ascml.absmodel;

import jade.tools.ascml.exceptions.ModelException;

import javax.swing.*;
import java.util.Vector;

/**
 * 
 */
public interface IAgentType
{
	String PLATFORM_TYPE_JADE = "jade";
	String PLATFORM_TYPE_JADEX = "jadex";
	String NAME_UNKNOWN = "Unknown";
	String CLASS_UNKNOWN = "Unknown";

	/**
	 * This constant is used to indicate, that this model has successfully been loaded.
	 */
	String STATUS_OK		= "successfully loaded";
	/**
	 * This constant is used to indicate, that at least one error occurred while loading the model.
	 */
	String STATUS_ERROR		= "loading error";
	/**
	 * This constant is used to indicate, that at least one referenced AgentType
	 * has NOT been loaded successfully
	 */
	String STATUS_REFERENCE_ERROR	= "erroneous reference";

	/**
	 *  Set the name of this AgentType.
	 *  @param name  AgentType's name.
	 */
	void setName(String name);

	/**
	 *  Get the name of this AgentType.
	 *  @return  AgentType's name.
	 */
	String getName();

	/**
	 * Set the package-name of this AgentType
	 * @param packageName  The packageName as String (e.g. examples.party)
	 */
	void setPackageName(String packageName);

	/**
	 * Get the package-name of this AgentType
	 * @return packageName as String (e.g. examples.party)
	 */
	String getPackageName();

	/**
	 *  Returns the fully qualified AgentType-name. The name is composed of
	 *  the package-name and the AgentType-name, for example my.packageName.AgentTypeName
	 *  would be a correct 'fully qualified' AgentTypeName.
	 *  @return  fully qualified name of the AgentType.
	 */
	String getFullyQualifiedName();

	/**
	 * Set the type of agent-platform (e.g. jade) this AgentType is designed to run on.
	 * @param platformType  The platformType as String, possible values may be
	 * PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	void setPlatformType(String platformType);

	/**
	 * Get the type of agent-platform (e.g. jade) this AgentType is designed to run on.
	 * return The platformType as String, possible values may be PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	String getPlatformType();

	/**
	 *  Set the classname of this AgentType.
	 *  @param className  AgentType's classname.
	 */
	void setClassName(String className);

	/**
	 *  Get the classname of this AgentType.
	 *  @return AgentType's classname.
	 */
	String getClassName();

	/**
	 *  Set the document.
	 *  @param document  The document.
	 */
	void setDocument(IDocument document);

	/**
	 *  Get the document, which contains the AgentType's source-path.
	 *  @return document  The document.
	 */
	IDocument getDocument();

	/**
	 *  Set the path+name of the AgentType's icon-name. Using the getIcon-method
	 *  an ImageIcon is constructed out of this name.
	 *  @param iconName  The name of the icon.
	 */
	void setIconName(String iconName);

	/**
	 *  Get the name of the AgentType's icon.
	 *  @return The image-icon.
	 */
	String getIconName();

	/**
	 *  Get the ImageIcon for this AgentType.
	 *  @return The ImageIcon for this AgentType.
	 */
	ImageIcon getIcon();

	/**
	 *  Set the description of this AgentType.
	 *  @param description  AgentType's description.
	 */
	void setDescription(String description);

	/**
	 *  Get the agent's description.
	 *  @return  agent's description.
	 */
	String getDescription();

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
	 * Get the integrity-status, which describes errors or warnings in detail.
	 * The ModelIntegrityChecker may set the integrity-status upon checking the integrity.
	 * @return  A ModelException containing detailed integrity-messages as Strings.
	 */
	ModelException getIntegrityStatus();

	/**
	 * Set the integrity-status, which describes errors or warnings in detail.
	 * The ModelIntegrityChecker may set the integrity-status upon checking the integrity.
	 * @param statusException  A ModelException containing detailed integrity-messages as Strings.
	 */
	void setIntegrityStatus(ModelException statusException);

	/**
	 *  Get all AgentDescriptions.
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @return  The AgentType's AgentDescriptionModels as an array.
	 */
	IAgentDescription[] getAgentDescriptions();

	/**
	 *  Add an AgentDescription for this AgentType.
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @param description  the AgentType's AgentDescriptionModel.
	 */
	void addAgentDescription(IAgentDescription description);

	/**
	 *  Remove an AgentDescription from this AgentType.
	 *  @param description  The AgentType's AgentDescription to remove.
	 */
	void removeAgentDescription(IAgentDescription description);

	/**
	 * Add a parameter to this AgentType.
	 * @param parameter  The ParameterSet-object to add.
	 */
	void addParameter(IParameter parameter);

	/**
	 * Remove a parameter from this agent.
	 * @param name  The name of the parameter to remove.
	 */
	void removeParameter(String name);

	/**
	 * Remove a parameter from this agent.
	 * @param parameter  The Parameter-object to remove.
	 */
	void removeParameter(IParameter parameter);

	/**
	 * Get an AgentType's parameter.
	 * @param name  The name of the Parameter
	 * @return  An agent's parameter or null if no parameter with the given name exists.
	 */
	IParameter getParameter(String name);

	/**
	 * Get all of the AgentType's parameter.
	 * @return  All of the agent's parameters as Parameter-array.
	 */
	IParameter[] getParameters();

	/**
	 * Add a parameterSet to this AgentType.
	 * @param parameterSet  The ParameterSet-object to add.
	 */
	void addParameterSet(IParameterSet parameterSet);

	/**
	 * Remove a parameterSet from this agent.
	 * @param name  The name of the parameterSet to remove.
	 */
	void removeParameterSet(String name);

	/**
	 * Remove a parameterSet from this agent.
	 * @param parameter  The name of the ParameterSet to remove.
	 */
	void removeParameterSet(IParameter parameter);

	/**
	 * Get an AgentType's parameterSet.
	 * @param name  The name of the ParameterSet
	 * @return  An agent's parameterSet or null if no parameter with the given name exists.
	 */
	IParameterSet getParameterSet(String name);

	/**
	 * Get all of the AgentType's parameterSets.
	 * @return  All of the agent's parameterSets as ParameterSet-array.
	 */
	IParameterSet[] getParameterSets();

	/**
	 * Get all the ModelChangedListener.
	 * @return  A Vector containing ModelChangedListener
	 */
	Vector getModelChangedListener();

	/**
	 * Set the ModelChangedListener. These are informed in case of changes in this model.
	 * @param modelChangedListener  A Vector containing ModelChangedListener
	 */
	void setModelChangedListener(Vector modelChangedListener);

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	void throwModelChangedEvent(String eventCode);

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 * @param userObject  This object is stored in the ModelChangedEvent. For example: When runnables
	 * are added or removed, these RunnableModels are stored as userObjects to access them later on.
	 */
	void throwModelChangedEvent(String eventCode, Object userObject);

	/**
	 *  This method returns a short String with the AgentType-name.
	 *  It is used by the RepositoryTree for example, to name the nodes.
	 *  @return  String containing the name of this AgentType
	 */
	String toString();

	/**
	 *  This method returns a formatted String showing the AgentType-model.
	 *  @return  formatted String showing ALL information about this AgentType.
	 */
	String toFormattedString();
}
