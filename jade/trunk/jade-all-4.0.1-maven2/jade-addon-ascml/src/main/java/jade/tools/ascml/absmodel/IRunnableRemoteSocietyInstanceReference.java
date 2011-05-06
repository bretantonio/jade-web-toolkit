package jade.tools.ascml.absmodel;

import jade.tools.ascml.model.jibx.Launcher;

/**
 * 
 */
public interface IRunnableRemoteSocietyInstanceReference  extends IAbstractRunnable
{
	/**
	 * Returns the name of this reference.
	 * @return the name of this reference.
	 */
	String getName();

	/**
	 * Returns the FQ- name of this reference.
	 * @return the FQ-name of this reference.
	 */
	String getFullyQualifiedName();

	/**
	 * Returns the name of the referenced SocietyType.
	 * @return  The name of the referenced SocietyType.
	 */
	String getTypeName();

	/**
	 * Returns the name of the referenced SocietyInstance.
	 * @return the name of the referenced SocietyInstance.
	 */
	String getInstanceName();

	/**
	 * Returns the name of the launcher, responsible for launching this reference.
	 * @return  the name of the launcher.
	 */
	Launcher getLauncher();

	String toString();
}
