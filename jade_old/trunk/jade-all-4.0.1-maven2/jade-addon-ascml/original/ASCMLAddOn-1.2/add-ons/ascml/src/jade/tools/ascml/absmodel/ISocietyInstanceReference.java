package jade.tools.ascml.absmodel;

import jade.tools.ascml.model.jibx.Launcher;
import jade.tools.ascml.model.jibx.dependency.AbstractDependency;
import jade.tools.ascml.absmodel.dependency.IDependency;

import java.util.List;
import java.util.Vector;

/**
 * 
 */
public interface ISocietyInstanceReference
{
	/**
	 * This constant is used when no name has been given to the referenced societytype.
	 * Since the societytype-name is mandatory, it is set to TYPE_UNKNOWN.
	 */
	String TYPE_UNKNOWN				= "Unknown Reference-Type";
	/**
	 * This constant is used when no name has been given to referenced societyinstance.
	 * Since the societyinstance-name is mandatory, it is set to INSTANCE_UNKNOWN.
	 */
	String INSTANCE_UNKNOWN			= "Unknown Reference-Instance";
	/**
	 * This constant is used when no name has been given to this societyinstance-reference.
	 * Since the name is mandatory, it is set to NAME_UNKNOWN.
	 */
	String NAME_UNKNOWN				= "Unnamed Reference";
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

	/**
	 *  Get the SocietyInstance to which this SocietyInstanceReference belongs.
	 *  @return  SocietyInstance to which this SocietyInstanceReference belongs.
	 */
	ISocietyInstance getParentSocietyInstance();

	/**
	 *  Set the SocietyInstance to which this SocietyInstanceReference belongs.
	 *  @param parentSocietyInstance SocietyInstance to which this SocietyInstanceReference belongs.
	 */
	void setParentSocietyInstance(ISocietyInstance parentSocietyInstance);

	/**
	 * Set the SocietyInstanceReference's name.
	 * @param name  The name of this SocietyInstanceReference.
	 */
	void setName(String name);

	/**
	 * Get the SocietyInstanceReference's name.
	 * @return The name of this SocietyInstanceReference.
	 */
	String getName();

	/**
	 *  Get the fully-qualified name of the SocietyInstanceReference.
	 *  The name is composed of the fully-qualified name of the SocietyInstance, to which this
	 *  reference points and the name of this reference.
	 *  For example my.societyTypeName.SocietyInstanceName.ReferenceName (e.g. examples.party.BirthdaySociety.SmallParty.ErnasParty)
	 *  would be a correct 'fully-qualified' name.
	 *  @return  fully-qualified name of the SocietyInstanceReference.
	 */
	String getFullyQualifiedName();

	/**
	 * Set the name of the referenced SocietyType.
	 * @param typeName  The name of the referenced SocietyType.
	 */
	void setTypeName(String typeName);

	/**
	 * Get the name of the referenced SocietyType.
	 * @return  The name of the referenced SocietyType.
	 */
	String getTypeName();

	/**
	 * Set the name of the referenced SocietyInstance.
	 * @param instanceName  The name of the referenced SocietyInstance.
	 */
	void setInstanceName(String instanceName);

	/**
	 * Get the name of the referenced SocietyInstance.
	 * @return  The name of the referenced SocietyInstance.
	 */
	String getInstanceName();

	/**
	 * Set the number of SocietyInstanceReferences to be started.
	 * @param quantity  Number of SocietyInstanceReferences to be started.
	 */
	void setQuantity(String quantity);

	/**
	 * Get the number of SocietyInstanceReferences to be started.
	 * @return  Number of SocietyInstanceReferences to be started.
	 */
	long getQuantity();

	/**
	 * Set the naming-scheme used for naming the SocietyInstanceReferences in case multiple instances of this SocietyInstanceReference should be started.
	 * The naming-scheme is used to name the runnables in order to make sure that their names are unique inside the ASCML.
	 * The String is appended at the name of the runnable. You may choose any String you like.
	 * There are a few placeholders that might be used in order order to ensure uniqueness of names.
	 * '%N' is replaced with an ID, starting by 0 and ascending to the number of runnable models of this type created so far.
	 * '%T' is replaced at the time of creation of a runnable by the current time in milliseconds.
	 * '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 * Depending on how much runnable-instances you want to create, you should combine the placeholders.
	 * One example for an naming-scheme could be: '%N_(time:%Tms)' ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 * @param namingScheme  The naming-scheme used to name runnable SocietyInstancesReferences in case multiple instances should be started.
	 */
	void setNamingScheme(String namingScheme);

	/**
	 * Get the naming-scheme used for naming the SocietyInstanceReferences in case multiple instances of this SocietyInstanceReference should be started.
	 * The naming-scheme is used to name the runnables in order to make sure that their names are unique inside the ASCML.
	 * The String is appended at the name of the runnable. You may choose any String you like.
	 * There are a few placeholders that might be used in order order to ensure uniqueness of names.
	 * '%N' is replaced with an ID, starting by 0 and ascending to the number of runnable models of this type created so far.
	 * '%T' is replaced at the time of creation of a runnable by the current time in milliseconds.
	 * '%R' is replaced with a pseudo random-number between 0 and 1,000,000,000
	 * Depending on how much runnable-instances you want to create, you should combine the placeholders.
	 * One example for an naming-scheme could be: '%N_(time:%Tms)' ... resulting for example in '%runnableName%_0_(time:109873247ms)'
	 * @return The naming-scheme used to name runnable SocietyInstancesReferences in case multiple instances should be started.
	 */
	String getNamingScheme();

	/**
	 * Set the launcher to start the referenced SocietyInstance in case this is a remote-reference.
	 * @param launcher  The Launcher to start the referenced SocietyInstance.
	 */
	void setLauncher(Launcher launcher);

	/**
	 * Get the launcher to start the referenced SocietyInstance in case this is a remote-reference.
	 * @return  The Launcher to start the referenced SocietyInstance.
	 */
	Launcher getLauncher();

	/**
	 * Returns wheter this reference points to a local or a remote reference.
	 * If the launcher is set (and it's name), this is a remote reference, otherwise it's a local reference.
	 * @return  true, if remote-reference, false if reference is local.
	 */
	boolean isRemoteReference();

	/**
	 * Set the SocietyInstance locally referenced by this SocietyInstanceReference.
	 * In case the reference points to a remote SocietyInstance nothing has to be set.
	 * @param locallyReferencedModel  The local SocietyInstanceModel, referenced by this ReferenceModel
	 */
	void setLocallyReferencedSocietyInstance(ISocietyInstance locallyReferencedModel);

	/**
	 * Get the SocietyInstance locally referenced by this SocietyInstanceReference in case this is a local-reference.
	 * In case the reference points to a remote SocietyInstance nothing has to be set.
	 * @return  The locally referenced SocietyInstance or null, if reference points to a remote SocietyInstance.
	 */
	ISocietyInstance getLocallyReferencedModel();

	/**
	 * Get the status of this SocietyInstanceReference.
	 * The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR
	 */
	String getStatus();

	/**
	 * Set the status of this SocietyInstanceReference.
	 * The status indicates, whether loading was successful or not.
	 * @param newStatus  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	void setStatus(String newStatus);

	/**
	 *  Get all of the SocietyInstanceReference's dependency-models.
	 *  @return An array containing all of the SocietyInstanceReference's dependency-models.
	 */
	IDependency[] getDependencies();

	/**
	 *  Get all of the SocietyInstanceReference's dependency-models.
	 *  @return A list containing all of the SocietyInstanceReference's dependency-models
	 */
	List<IDependency> getDependencyList();

	/**
	 * Add a dependency to this SocietyInstanceReference's dependencies.
	 * @param dependency  The Dependency to add.
	 */
	void addDependency(IDependency dependency);

	/**
	 * Remove a dependency from this SocietyInstanceReference's dependencies.
	 * @param dependency  The Dependency to remove.
	 */
	void removeDependency(IDependency dependency);

	/**
	 * Remove a dependency from this reference's dependency-list.
	 * @param dependencyIndex  The index of the Dependency-model
	 *                         to remove within the inner dependency-list.
	 */
	void removeDependency(int dependencyIndex);

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	void throwModelChangedEvent(String eventCode);

	String toString();
}
