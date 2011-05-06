package jade.tools.ascml.absmodel.dependency;

import jade.tools.ascml.absmodel.IProvider;
import jade.tools.ascml.absmodel.IServiceDescription;

/**
 * 
 */
public interface IServiceDependency extends IDependency
{
	/**
	 * Get the description of the service, on which this dependency depends.
	 * @return  The ServiceDescription on which this dependency depends.
	 */
	IServiceDescription getServiceDescription();

	/**
	 * Set the description of the service, on which this dependency depends.
	 * @param serviceDescription  The ServiceDescription on which this dependency depends.
	 */
	void setServiceDescription(IServiceDescription serviceDescription);

	/**
	 * Set the Provider, responsible for providing the specified service.
	 * @param provider  The Provider, responsible for providing the specified service.
	 */
	void setProvider(IProvider provider);

	/**
	 * Get the Provider, responsible for providing the specified service.
	 * @return  The Provider, responsible for providing the specified service.
	 */
	IProvider getProvider();
}
