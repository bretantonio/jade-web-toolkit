package jade.tools.ascml.absmodel;

import jade.tools.ascml.model.jibx.Property;

/**
 * 
 */
public interface IServiceDescription
{
	/**
	 *  Get the name of this ServiceDescription.
	 *  @return The service's name.
	 */
	String getName();

	/**
	 *  Set the name of this ServiceDescription.
	 *  @param name  The services's name.
	 */
	void setName(String name);

	/**
	 *  Get the type of this ServiceDescription.
	 *  @return The service's type.
	 */
	String getType();

	/**
	 *  Set the type of this ServiceDescription.
	 *  @param type  The services's type.
	 */
	void setType(String type);

	/**
	 *  Get the ownership of this ServiceDescription.
	 *  @return The service's ownership.
	 */
	String getOwnership();

	/**
	 *  Set the ownership of this ServiceDescription.
	 *  @param ownership  The services's ownership.
	 */
	void setOwnership(String ownership);

	/**
	 *  Get the properties of this service.
	 *  @return  An array containing the properties of this service.
	 */
	IProperty[] getProperties();

	/**
	 *  Set the properties of this service (old properties are removed)
	 *  @param properties  An array containing the properties for this service.
	 */
	void setProperties(String[] properties);

	/**
	 *  Add a property to this service.
	 *  @param property  The property to add.
	 */
	void addProperty(Property property);

	/**
	 *  Remove a property from this service.
	 *  @param property  The property to remove.
	 */
	void removeProperty(IProperty property);

	/**
	 *  Get the names of the protocols the service supports.
	 *  @return  An array containing the supported protocol-names as Strings.
	 */
	String[] getProtocols();

	/**
	 *  Set the protocol-names the service supports (old protocols are removed).
	 *  @param protocols  An array containing the supported protocol-names as Strings.
	 */
	void setProtocols(String[] protocols);

	/**
	 *  Add a protocol-name to this service.
	 *  @param protocol The name of the protocol.
	 */
	void addProtocol(String protocol);

	/**
	 *  Remove a protocol-name to this service.
	 *  @param protocol The name of the protocol.
	 */
	void removeProtocol(String protocol);

	/**
	 *  Get the ontology-names the service supports.
	 *  @return  An array containing the supported ontology-names as Strings.
	 */
	String[] getOntologies();

	/**
	 *  Set the ontology-names the service supports.
	 *  @param ontologies  An array containing the supported ontology-names as Strings.
	 */
	void setOntologies(String[] ontologies);

	/**
	 *  Add an ontology-name the service supports.
	 *  @param ontology  The ontology-name to add.
	 */
	void addOntology(String ontology);

	/**
	 *  Remove an ontology-name.
	 *  @param ontology  The ontology-name to remove.
	 */
	void removeOntology(String ontology);

	/**
	 *  Get the languages this service supports.
	 *  @return  An array containing the supported languages as Strings.
	 */
	String[] getLanguages();

	/**
	 *  Set the languages this service supports (removing old languages).
	 *  @param languages  An array containing the supported languages as Strings.
	 */
	void setLanguages(String[] languages);

	/**
	 *  Add a language this service supports.
	 *  @param language  The language to add.
	 */
	void addLanguage(String language);

	/**
	 *  Remove a language this service supports.
	 *  @param language  The language to remove.
	 */
	void removeLanguage(String language);

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
