package jade.tools.ascml.absmodel;

import jade.tools.ascml.onto.Status;
import jade.tools.ascml.model.runnable.RunnableAgentInstance;
import jade.tools.ascml.model.runnable.RunnableRemoteSocietyInstanceReference;
import jade.tools.ascml.model.runnable.RunnableSocietyInstance;

/**
 * 
 */
public interface IRunnableSocietyInstance extends IAbstractRunnable
{
	/**
	 * Runnable AgentInstances have to inform their parent-RunnableSocietyInstance whenever
	 * their status changed.
	 * @param runnableName The name of the runnable-model, whose status changed (only used for debugging)
	 * @param childNewStatus  New status of the agent
	 * @param childOldStatus  Old status of the agent
	 */
	void informStatus(String runnableName, Status childNewStatus, Status childOldStatus);

	/**
	 * Get the status of this RunnableSocietyInstance.
	 * The status results of the status of all contained RunnableAgentInstances
	 * plus all referenced RunnableSocietyInstances (and their agents and references).
	 * @return
	 */
	Status getStatus();

	/**
	 * Get the amount of local runnable agentinstances, that are defined by this model
	 * and all local runnable subsocieties.
	 * @return  runnable agentinstance-count..
	 */
	int getLocalAgentInstanceCount();

	void addRunnableAgentInstance(RunnableAgentInstance runnableInstance);

	RunnableAgentInstance[] getRunnableAgentInstances();

	void addLocalRunnableSocietyInstanceReference(RunnableSocietyInstance runnableInstance);

	RunnableSocietyInstance[] getLocalRunnableSocietyInstanceReferences();

	void addRemoteRunnableSocietyInstanceReference(RunnableRemoteSocietyInstanceReference oneReference);

	RunnableRemoteSocietyInstanceReference[] getRemoteRunnableSocietyInstanceReferences();

	String toFormattedString();
}
