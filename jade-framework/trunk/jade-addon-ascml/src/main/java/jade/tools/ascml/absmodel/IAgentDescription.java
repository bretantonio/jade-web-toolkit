package jade.tools.ascml.absmodel;

/**
 * 
 */
public interface IAgentDescription
{
	/**
	 *  Get the name of this agent.
	 *  @return The agent's name.
	 */
	String getName();

	/**
	 *  Set the name of this agent.
	 *  @param name  The agent's name.
	 */
	void setName(String name);

	/**
	 *  Get the addresses, where the agent can be found.
	 *  @return  A String-array containing the agent's addresses as Strings.
	 */
	String[] getAddresses();

	/**
	 *  Set the addresses, where the agent can be found and remove all existing addresses
	 *  @param newAddresses  A String-array containing the agent's addresses as Strings.
	 */
	void setAddresses(String[] newAddresses);

	/**
	 * Add an address where the agent can be found
	 * @param address  An address as String
	 */
	void addAddress(String address);

	/**
	 *  Get the service-names the agent offers.
	 *  @return  An array containing the name of services.
	 */
	String[] getServices();

	/**
	 *  Set the service-names the agent offers and remove the old service-names.
	 *  @param serviceNames  An array containing the name of services.
	 */
	void setServices(String[] serviceNames);

	/**
	 * Add the name of a service the agent offers.
	 * @param service  The name of the service.
	 */
	void addService(String service);

	/**
	 *  Get the protocols the agent supports.
	 *  @return  A String-array containing the supported protocols.
	 */
	String[] getProtocols();

	/**
	 *  Set the protocols the agent supports and remove the old protocols
	 *  @param newProtocols  A  String-array containing the supported protocols as Strings.
	 */
	void setProtocols(String[] newProtocols);

	/**
	 * Add a protocol the agent supports.
	 * @param  protocol  A String representing the protocol.
	 */
	void addProtocol(String protocol);

	/**
	 *  Get the ontologies the agent supports.
	 *  @return  A String-array containing the supported ontologies.
	 */
	String[] getOntologies();

	/**
	 *  Set the ontologies the agent supports and remove the old ontologies.
	 *  @param newOntologies A String-array containing the supported ontologies.
	 */
	void setOntologies(String[] newOntologies);

	/**
	 * Add an ontology the agent supports.
	 * @param  ontology  A String representing the ontology.
	 */
	void addOntology(String ontology);

	/**
	 *  Get the languages this agent supports.
	 *  @return  A String-array containing the supported languages.
	 */
	String[] getLanguages();

	/**
	 *  Set the languages this agent supports and remove the old languages.
	 *  @param newLanguages  A String-array containing the supported languages.
	 */
	void setLanguages(String[] newLanguages);

	/**
	 * Add a language the agent supports.
	 * @param  language  A String representing the language.
	 */
	void addLanguage(String language);

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
