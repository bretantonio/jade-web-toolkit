package jade.tools.ascml.absmodel.dependency;

import jade.tools.ascml.absmodel.IProvider;

/**
 * 
 */
public interface ISocietyInstanceDependency extends IDependency
{
	/**
	 * Get the name of the SocietyType in which the dependend SocietyInstance is defined.
	 * @return  The name of the SocietyType in which the dependend SocietyInstance is defined.
	 */
	String getSocietyType();

	/**
	 * Set the SocietyType in which the dependend SocietyInstance is defined.
	 * @param societyType  The SocietyType in which the dependend SocietyInstance is defined.
	 */
	void setSocietyType(String societyType);

	/**
	 * Get the name of the dependend SocietyInstance.
	 * @return  The name of the dependend SocietyInstance.
	 */
	String getSocietyInstance();

	/**
	 * Set the name of the dependend SocietyInstance.
	 * @param societyInstance  The name of the dependend SocietyInstance.
	 */
	void setSocietyInstance(String societyInstance);

	/**
	 * Get the fully-qualified name of the dependend SocietyInstance.
	 * @return  The fully-qualified name of the dependend SocietyInstance.
	 */
	String getFullyQualifiedSocietyInstance();

	/**
	 * Get the status of the SocietyInstance to depend on.
	 * @return The status of the SocietyInstance to depend on.
	 */
	String getStatus();

	/**
	 * Set the status of the SocietyInstance to depend on.
	 * @param status  The status of the SocietyInstance to depend on.
	 */
	void setStatus(String status);

	/**
	 * Set the Provider responsible for the SocietyInstance.
	 * @param provider  The Provider responsible for the SocietyInstance.
	 */
	void setProvider(IProvider provider);

	/**
	 * Get the Provider responsible for the SocietyInstance.
	 * @return  The Provider responsible for the SocietyInstance.
	 */
	IProvider getProvider();
}
