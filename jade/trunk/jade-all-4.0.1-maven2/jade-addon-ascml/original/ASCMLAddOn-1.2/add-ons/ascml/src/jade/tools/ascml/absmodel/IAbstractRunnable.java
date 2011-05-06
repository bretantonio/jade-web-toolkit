package jade.tools.ascml.absmodel;

import jade.tools.ascml.absmodel.dependency.IDependency;
import jade.tools.ascml.onto.Status;

import java.util.Vector;

/**
 * 
 */
public interface IAbstractRunnable
{
	/**
	 *  Set the name of this agentType.
	 *  @param name  agentType's name.
	 */
	void setName(String name);

	/**
	 *  Get the name of this runnable.
	 *  @return  runnable's name.
	 */
	String getName();

	/**
	 *  Get the fully qualified name of this runnable
	 *  @return  runnable's fully qualified name.
	 */
	String getFullyQualifiedName();

	/**
	 *  Get the parent-model of this agentInstance.
	 *  @return  model-object of this instance's parent.
	 */
	Object getParentModel();

	/**
	 *  Get the parent-model of this agentInstance.
	 *  @return  model-object of this instance's parent.
	 */
	Object getParentRunnable();

	/**
	 * Set the parent runnable-model of this AbstractRunnable.
	 * This method should only be called right after creation.
	 * @param parentRunnable  runnable-model to which this runnable-model belongs
	 */
	void setParentRunnable(IAbstractRunnable parentRunnable);

	/**
	 *  Get the dependencies of this runnable model..
	 *  @return  runnable agentinstance's dependencies or null, if it depends on nothing.
	 */
	IDependency[] getDependencies();

	/**
		 *  Get the dependencies of this runnable model..
		 *  @return  runnable agentinstance's dependencies or null, if it depends on nothing.
		 */
	Vector<IDependency> getDependencyList();

	/**
	 *  Add dependencies to this runnable model.
	 *  @param additionalDependencies  The model's dependencies or null, if it depends on nothing.
	 */
	void addDependencies(IDependency[] additionalDependencies);

	/**
	 *  Set the status of this instance. By setting the new status to NOT_RUNNING, the runnable
	 *  instance is automatically removed from the parent's runnable-list.
	 *  @param newStatus  The instance's new status.
	 */
	void setStatus(Status newStatus);

	/**
	 * Runnables may inform their parent-Runnables (e.g. the RunnableSociety to which
	 * a RunnableSociety or -AgentInstance belongs) about a status-change of their own.
	 * @param runnableName The name of the runnable-model, whose status changed (only used for debugging)
	 * @param newStatus  New status of the agent
	 * @param oldStatus  Old status of the agent
	 */
	void informStatus(String runnableName, Status newStatus, Status oldStatus);

	/**
	 *  Get the status of this instance.
	 *  @return  instance's status.
	 */
	Status getStatus();

	/**
	 *  Get the detailed information about the status of this instance.
	 *  @return  instance's status in detail.
	 */
	String getDetailedStatus();

	/**
	 *  Set the detailed information about the status of this instance.
	 *  @param detailedStatus  instance's status in detail.
	 */
	void setDetailedStatus(String detailedStatus);

	String toString();

	String toFormattedString();
}
