package jade.tools.ascml.absmodel.dependency;

/**
 * 
 */
public interface IDelayDependency extends IDependency
{
	/**
	 * Get the amount of milliseconds to wait till this dependency is fulfilled.
	 * @return  The amount of milliseconds to wait till this dependency is fulfilled.
	 */
	String getQuantity();

	/**
	 * Get the amount of milliseconds to wait till this dependency is fulfilled.
	 * @return  The amount of milliseconds to wait till this dependency is fulfilled.
	 */
	int getQuantityAsInt();

	/**
	 * Set the amount of milliseconds to wait till this dependency is fulfilled.
	 * @param quantity  The amount of milliseconds to wait till this dependency is fulfilled.
	 */
	void setQuantity(String quantity);

	/**
	 * Set the amount of milliseconds to wait till this dependency is fulfilled.
	 * @param quantity  The amount of milliseconds to wait till this dependency is fulfilled.
	 */
	void setQuantity(int quantity);
}
