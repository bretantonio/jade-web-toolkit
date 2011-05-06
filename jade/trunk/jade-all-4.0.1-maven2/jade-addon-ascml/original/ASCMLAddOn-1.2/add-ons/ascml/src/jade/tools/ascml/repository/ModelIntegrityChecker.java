package jade.tools.ascml.repository;

import jade.tools.ascml.model.jibx.*;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.absmodel.*;

/**
 * 
 */
public class ModelIntegrityChecker
{
    private ModelException statusException;

	public ModelIntegrityChecker()
	{

	}

	public boolean checkIntegrity(Object model)
	{
		if (model instanceof ISocietyType)
		{
			statusException = new ModelException("The integrity of the SocietyType '"+((ISocietyType)model).getName()+"' is violated. For detailed information see further messages.");
			checkSocietyType((ISocietyType)model);
			// System.err.println("ModelIntegrityChecker.check: " + ((ISocietyType)model).getName() + " has status '"+((ISocietyType)model).getStatus()+"'");
		}
		else if (model instanceof IAgentType)
		{
			statusException = new ModelException("The integrity of the AgentType '"+((IAgentType)model).getName()+"' is violated. For detailed information see further messages.");
			checkAgentType((IAgentType)model);
			// System.err.println("ModelIntegrityChecker.check: " + ((IAgentType)model).getName() + " has status '"+((IAgentType)model).getStatus()+"'");
		}

		if (statusException.hasExceptionDetails())
			return false;
		else
			return true;
	}

	private void checkSocietyType(ISocietyType model)
	{
		ISocietyInstance[] societyInstances = model.getSocietyInstances();

		boolean referenceError = false;
		boolean instanceError = false;

		if ((model.getName() == null) || model.getName().equals("") || model.getName().equals(ISocietyType.NAME_UNKNOWN))
		{
			statusException.addExceptionDetails("The name is missing", "write me!");
			instanceError = true;
		}
		if (!(model.getDocument().getSource().endsWith(model.getName() + ".society.xml") || model.getDocument().getSource().endsWith(model.getName())))
		{
			statusException.addExceptionDetails("The name doesn't correspond to the filename", "write me!");
			instanceError = true;
		}
		if ((model.getPackageName() == null) || model.getPackageName().equals(""))
		{
			statusException.addExceptionDetails("The package-name is missing", "write me!");
			instanceError = true;
		}
		if (model.getSocietyInstances().length == 0)
		{
			statusException.addExceptionDetails("There are no SocietyInstances specified", "write me!");
			instanceError = true;
		}

		// check the status of all contained SocietyInstances
		for (int i=0; i < societyInstances.length; i++)
		{
			checkSocietyInstance(societyInstances[i]);
			if (societyInstances[i].getStatus().equals(ISocietyInstance.STATUS_ERROR))
				instanceError = true;
			else if (societyInstances[i].getStatus().equals(ISocietyInstance.STATUS_REFERENCE_ERROR))
				referenceError = true;
		}

		if (instanceError)
			model.setStatus(ISocietyType.STATUS_ERROR);
		else if (referenceError)
			model.setStatus(ISocietyType.STATUS_REFERENCE_ERROR);
		else
			model.setStatus(ISocietyType.STATUS_OK);

		if (model.getStatus().equals(ISocietyType.STATUS_ERROR) || model.getStatus().equals(ISocietyType.STATUS_REFERENCE_ERROR))
		{
			model.setIntegrityStatus(statusException);
		}
	}

	private void checkAgentType(IAgentType model)
	{
		if ((model.getName() == null) || model.getName().equals("") || model.getName().equals(IAgentType.NAME_UNKNOWN))
			statusException.addExceptionDetails("The name is missing", "write me!");
		if (!(model.getDocument().getSource().endsWith(model.getName() + ".agent.xml") || model.getDocument().getSource().endsWith(model.getName())))
			statusException.addExceptionDetails("The name doesn't correspond to the filename", "write me!");
		if ((model.getPackageName() == null) || model.getPackageName().equals(""))
			statusException.addExceptionDetails("The package-name is missing", "write me!");
		if ((model.getClassName() == null) || model.getClassName().equals("") || model.getClassName().equals(IAgentType.CLASS_UNKNOWN))
			statusException.addExceptionDetails("The class-name is missing", "write me!");
		if ((model.getPlatformType() == null) || model.getPlatformType().equals(""))
			statusException.addExceptionDetails("The platform-type is missing", "write me!");

		if (statusException.hasExceptionDetails())
		{
			model.setIntegrityStatus(statusException);
			model.setStatus(IAgentType.STATUS_ERROR);
		}
		else
		{
			model.setStatus(IAgentType.STATUS_OK);
		}
	}

	private void checkSocietyInstance(ISocietyInstance societyInstance)
	{
		boolean instanceError = false;
		boolean referenceError = false;

		// check the integrity of the specified SocietyInstances

		if ((societyInstance.getName() == null) || societyInstance.getName().equals("") || societyInstance.getName().equals(ISocietyInstance.NAME_UNKNOWN))
		{
			statusException.addExceptionDetails("The name of a SocietyInstance is missing", "write me !");
			instanceError = true;
		}
		if ((societyInstance.getAgentInstanceModels().length == 0) && (societyInstance.getSocietyInstanceReferences().length == 0))
		{
			statusException.addExceptionDetails("The SocietyInstance "+societyInstance.getName()+" neither specifies AgentInstances not SocietyInstance-references", "write me !");
			instanceError = true;
		}

		// check the integrity of the specified AgentInstances-references
		IAgentInstance[] agentInstances = societyInstance.getAgentInstanceModels();
		for (int i=0; i < agentInstances.length; i++)
		{
            checkAgentInstance(agentInstances[i]);
			if (agentInstances[i].getStatus().equals(IAgentInstance.STATUS_ERROR))
				instanceError = true;
			else if (agentInstances[i].getStatus().equals(IAgentInstance.STATUS_REFERENCE_ERROR))
				referenceError = true;
		}

		// check the integrity of the specified SocietyInstances-references
		ISocietyInstanceReference[] societyInstanceReferences = societyInstance.getSocietyInstanceReferences();
		for (int i=0; i < societyInstanceReferences.length; i++)
		{
			checkSocietyInstanceReference(societyInstanceReferences[i]);
			if (societyInstanceReferences[i].getStatus().equals(ISocietyInstanceReference.STATUS_ERROR))
				instanceError = true;
			else if (societyInstanceReferences[i].getStatus().equals(ISocietyInstanceReference.STATUS_REFERENCE_ERROR))
				referenceError = true;
		}

		if (instanceError)
			societyInstance.setStatus(IAgentInstance.STATUS_ERROR);
		else if (referenceError)
			societyInstance.setStatus(IAgentInstance.STATUS_REFERENCE_ERROR);
		else
			societyInstance.setStatus(IAgentInstance.STATUS_OK);
	}

	private void checkAgentInstance(IAgentInstance agentInstance)
	{
		boolean instanceError = false;
		boolean referenceError = false;

		if ((agentInstance.getName() == null) || agentInstance.getName().equals("") || agentInstance.getName().equals(IAgentInstance.NAME_UNKNOWN))
		{
			statusException.addExceptionDetails("The name of an AgentInstance is missing", "write me !");
			instanceError = true;
		}
		if (agentInstance.getType() == null)
		{
			statusException.addExceptionDetails("The appropiate AgentType for AgentInstance '"+agentInstance.getName()+"' could not be resolved", "write me !");
			referenceError = true;
		}
		else if (agentInstance.getType().getStatus() == IAgentType.STATUS_ERROR)
		{
			statusException.addExceptionDetails("The AgentType '"+agentInstance.getType().getName()+"' for AgentInstance named '"+agentInstance.getName()+"' is erroneous", "write me !");
			referenceError = true;
		}
		if (agentInstance.getParentSocietyInstance() == null)
		{
			statusException.addExceptionDetails("The SocietyInstance to which the AgentInstance '"+agentInstance.getName()+"' belongs could not be resolved", "write me !");
			referenceError = true;
		}

		// toDo: check parameters (if all mandatory parameters and parameterSets defined by the AgentType have been supplied
		
		if (instanceError)
			agentInstance.setStatus(IAgentInstance.STATUS_ERROR);
		else if (referenceError)
			agentInstance.setStatus(IAgentInstance.STATUS_REFERENCE_ERROR);
		else
			agentInstance.setStatus(IAgentInstance.STATUS_OK);
	}

	private void checkSocietyInstanceReference(ISocietyInstanceReference societyInstanceReference)
	{
		boolean instanceError = false;
		boolean referenceError = false;

		if ((societyInstanceReference.getName() == null) || societyInstanceReference.getName().equals("") || societyInstanceReference.getName().equals(ISocietyInstanceReference.NAME_UNKNOWN))
		{
			statusException.addExceptionDetails("The name of a SocietyInstance-reference is missing", "write me !");
			instanceError = true;
		}
		if ((societyInstanceReference.getTypeName() == null) || societyInstanceReference.getTypeName().equals("") || societyInstanceReference.getTypeName().equals(ISocietyInstanceReference.NAME_UNKNOWN))
		{
			statusException.addExceptionDetails("The type-name of SocietyInstance-reference named '"+societyInstanceReference.getName()+"' is missing", "write me !");
			instanceError = true;
		}
		if ((societyInstanceReference.getInstanceName() == null) || societyInstanceReference.getInstanceName().equals("") || societyInstanceReference.getInstanceName().equals(ISocietyInstanceReference.NAME_UNKNOWN))
		{
			statusException.addExceptionDetails("The instance-name of SocietyInstance-reference named '"+societyInstanceReference.getName()+"' is missing", "write me !");
			instanceError = true;
		}
        if (societyInstanceReference.getParentSocietyInstance() == null)
		{
			statusException.addExceptionDetails("The SocietyInstance to which the SocietyInstance-reference '"+societyInstanceReference.getName()+"' belongs could not be resolved", "write me !");
			referenceError = true;
		}

		if (societyInstanceReference.isRemoteReference())
		{
			// if it is a remote reference, than the launcher has to be specified correctly
			Launcher launcher = societyInstanceReference.getLauncher();
			if ((launcher.getName() == null) || (launcher.getName().equals("")) || (launcher.getName().equals(Launcher.NAME_UNKNOWN)))
			{
				statusException.addExceptionDetails("The name of the launcher for the remote SocietyInstance-reference '"+societyInstanceReference.getName()+"' is missing", "write me !");
				instanceError = true;
			}
			if (launcher.getAddresses().length == 0)
			{
				statusException.addExceptionDetails("The launcher for the remote SocietyInstance-reference '"+societyInstanceReference.getName()+"' has no address", "write me !");
				instanceError = true;
			}
		}
		else
		{
            // if it is a local reference, than check if the matching SocietyInstance-model is specified correctly
			if (societyInstanceReference.getLocallyReferencedModel() == null)
			{
				statusException.addExceptionDetails("The appropiate local SocietyInstance for SocietyInstance-reference '"+societyInstanceReference.getName()+"' could not be resolved", "write me !");
				referenceError = true;
			}
			else if (societyInstanceReference.getLocallyReferencedModel().getStatus().equals(ISocietyInstance.STATUS_ERROR) || societyInstanceReference.getLocallyReferencedModel().getStatus().equals(ISocietyInstance.STATUS_REFERENCE_ERROR))
			{
				statusException.addExceptionDetails("The SocietyInstance named '"+societyInstanceReference.getInstanceName()+"' for SocietyInstance-reference named '"+societyInstanceReference.getName()+"' is erroneous", "write me !");
				referenceError = true;
			}
		}

		if (instanceError)
			societyInstanceReference.setStatus(ISocietyInstanceReference.STATUS_ERROR);
		else if (referenceError)
			societyInstanceReference.setStatus(ISocietyInstanceReference.STATUS_REFERENCE_ERROR);
		else
			societyInstanceReference.setStatus(ISocietyInstanceReference.STATUS_OK);
	}
}
