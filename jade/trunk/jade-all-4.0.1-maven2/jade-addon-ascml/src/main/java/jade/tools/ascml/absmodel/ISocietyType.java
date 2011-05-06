package jade.tools.ascml.absmodel;

import jade.tools.ascml.exceptions.ModelException;

import javax.swing.*;
import java.util.Vector;

/**
 * 
 */
public interface ISocietyType
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
	 * Set the name of this SocietyType.
	 * @param name  The name of the SocietyType.
	 */
	void setName(String name);

	/**
	 * Get the name of this SocietyType.
	 * @return The name of this SocietyType.
	 */
	String getName();

	/**
	 * Get the package-name of this SocietyType
	 * @return  The package-name of this SocietyType (e.g. examples.party)
	 */
	String getPackageName();

	/**
	 * Set the package-name of this SocietyType
	 * @param packageName  The package-name of this SocietyType (e.g. examples.party)
	 */
	void setPackageName(String packageName);

	/**
	 * Get the fully-qualified SocietyType's name.
	 * The name is composed of the package-name and the SocietyType's name,
	 * for example my.packageName.SocietyTypeName would be a correct 'fully-qualified' SocietyType-name.
	 *  @return  fully-qualified name of the SocietyType.
	 */
	String getFullyQualifiedName();

	/**
	 * Set the icon-name for this SocietyType.
	 * Using the getIcon-method an ImageIcon is constructed out of this name.
	 * @param iconName  The name of the icon representing this SocietyType.
	 */
	void setIconName(String iconName);

	/**
	 * Get the icon-name for this SocietyType.
	 * Using the getIcon-method an ImageIcon is constructed out of this name.
	 * @return The name of the icon representing this SocietyType.
	 */
	String getIconName();

	/**
	 *  Get the ImageIcon representing this SocietyType.
	 *  @return The ImageIcon representing this SocietyType.
	 */
	ImageIcon getIcon();

	/**
	 * Get the status of this SocietyType.
	 * The status indicates, whether loading was successful or not.
	 * @return  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	String getStatus();

	/**
	 * Set the status of this SocietyType.
	 * The status indicates, whether loading was successful or not.
	 * @param newStatus  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	void setStatus(String newStatus);

	/**
	 * Get the integrity-status, which describes loading-errors or -warnings in detail.
	 * The ModelIntegrityChecker may set the integrity-status upon checking the integrity.
	 * @return  A ModelException containing detailed integrity-messages.
	 */
	ModelException getIntegrityStatus();

	/**
	 * Set the integrity-status, which describes loading-errors or -warnings in detail.
	 * The ModelIntegrityChecker may set the integrity-status upon checking the integrity.
	 * @param statusException  A ModelException containing detailed integrity-messages.
	 */
	void setIntegrityStatus(ModelException statusException);

	/**
	 *  Set the document specifying the source from which this SocietyType has been loaded.
	 *  @param document  The document from which this SocietyType has been loaded..
	 */
	void setDocument(IDocument document);

	/**
	 *  Get the document specifying the source from which this SocietyType has been loaded.
	 *  @return  The document from which this SocietyType has been loaded..
	 */
	IDocument getDocument();

	/**
	 * Set the SocietyType's description.
	 * @param description  The description of this SocietyType.
	 */
	void setDescription(String description);

	/**
	 * Get the SocietyType's description.
	 * @return  The description of this SocietyType.
	 */
	String getDescription();

	/**
	 * Add an import for this SocietyType.
	 * @param oneImport  String-representation of the import to add.
	 */
	void addImport(String oneImport);

	/**
	 * Remove an import from this SocietyType.
	 * @param oneImport  String-representation of the import to be removed.
	 */
	void removeImport(String oneImport);

	/**
	 * Get all imports for this SocietyType.
	 * @return  String-array containing all the imports defindes for this SocietyType.
	 */
	String[] getImports();

	/**
	 * Add an AgentType-name to the SocietyType.
	 * @param name  The AgentType's fully-qualified name (e.g. 'jade.examples.PingPongAgent') to add.
	 */
	void addAgentTypeName(String name);

	/**
	 * Remove an AgentType-name from this SocietyType.
	 * @param name  The AgentType's fully-qualified name (e.g. 'jade.examples.PingPongAgent') to remove
	 */
	void removeAgentTypeName(String name);

	/**
	 * Get the names of all AgentTypes possibly referenced by a SocietyInstance of this SocietyType.
	 * @return  All AgentType-names as String-Array
	 */
	String[] getAgentTypeNames();

	/**
	 * Add a SocietyType-name to the SocietyType.
	 * @param name  The SocietyType's fully-qualified name (e.g. 'jade.examples.PingPongSociety') to add
	 */
	void addSocietyTypeName(String name);

	/**
	 * Remove a SocietyType-name from this SocietyType.
	 * @param name  The SocietyType's fully-qualified name (e.g. 'jade.examples.PingPongSociety') to remove
	 */
	void removeSocietyTypeName(String name);

	/**
	 * Get the names of all SocietyTypes possibly referenced by a SocietyInstance of this SocietyType.
	 * @return  All SocietyType-namesas String-Array
	 */
	String[] getSocietyTypeNames();

	/**
	 * Add a SocietyInstance to this SocietyType.
	 * @param societyInstance  The SocietyInstance to add.
	 */
	void addSocietyInstance(ISocietyInstance societyInstance);

	/**
	 * Get a SocietyInstance.
	 * @param index The index of the SocietyInstance within the internal ArrayList.
	 * @return The corresponding SocietyInstance at the specified index.
	 */
	ISocietyInstance getSocietyInstance(int index);

	/**
	 * Get a SocietyInstance.
	 * @param name  The name of the SocietyInstance.
	 * @return  The corresponding SocietyInstance with the given name.
	 */
	ISocietyInstance getSocietyInstance(String name);

	/**
	 * Get all SocietyInstances.
	 * @return  An array containing all SocietyInstances this SocietyType contains.
	 */
	ISocietyInstance[] getSocietyInstances();

	/**
	 * Remove a SocietyInstance from this SocietyType.
	 * @param societyInstance  The SocietyInstance to remove
	 */
	void removeSocietyInstance(ISocietyInstance societyInstance);

	/**
	 * Set the the name of the default-SocietyInstance.
	 * @param defaultSocietyInstance  The name of the SocietyInstance used as default-SocietyInstance.
	 */
	void setDefaultSocietyInstanceName(String defaultSocietyInstance);

	/**
	 * Get the name of the default-SocietyInstance.
	 * @return  The name of the SocietyInstance used as default-SocietyInstance.
	 */
	String getDefaultSocietyInstanceName();

	/**
	 * Get the default-SocietyInstance.
	 * If no default SocietyInstance is explicitly definded, return the
	 * first one of the internal ArrayList.
	 * @return  The SocietyInstance used as default-SocietyInstance.
	 */
	ISocietyInstance getDefaultSocietyInstance();

	/**
	 * Set the ModelChangedListener. These are informed in case of changes in this model.
	 * @param modelChangedListener  A Vector containing ModelChangedListener
	 */
	void setModelChangedListener(Vector modelChangedListener);

	/**
	 * Get all the ModelChangedListener.
	 * @return  A Vector containing ModelChangedListener
	 */
	Vector getModelChangedListener();

	/**
	 * Get all the LongTimeActionStartListener.
	 * @return  A Vector containing LongTimeActionStartListener
	 */
	Vector getLongTimeActionStartListener();

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	void throwModelChangedEvent(String eventCode);

	/**
	 * This method returns a short String with the society-name.
	 * It is used by the RepositoryTree for example, to name the nodes.
	 *
	 * @return String containing the name of this society.
	 */
	String toString();

	/**
	 * This method returns a formatted String showing the societyType-model.
	 *
	 * @return formatted String showing ALL information about this society.
	 */
	String toFormattedString();
}
