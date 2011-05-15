package jade.tools.ascml.repository;

import jade.tools.ascml.model.jibx.*;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.absmodel.*;

/**
 * 
 */
public class ModelReferenceResolver
{
	public ModelReferenceResolver()
	{

	}

	public void resolveReferences(ISocietyType model, ModelManager modelManager) throws ModelException
	{
        ModelException resolvingException = new ModelException("Error while resolving references.", "write me !");

		String[] imports = model.getImports();
		String[] agentTypeNames = model.getAgentTypeNames();
		String[] societyTypeNames = model.getSocietyTypeNames();
		String basePackageName = model.getPackageName();

		// resolve references from AgentInstances to AgentTypes
		ISocietyInstance[] societyInstances = model.getSocietyInstances();
		for (int i=0; i < societyInstances.length; i++)
		{
			societyInstances[i].setParentSocietyType(model);

			// resolve all agentinstance->agenttype-relations
			IAgentInstance[] agentInstances = societyInstances[i].getAgentInstanceModels();
			for (int j=0; j < agentInstances.length; j++)
			{
				if (agentInstances[j].getType() == null)
				{
					agentInstances[j].setParentSocietyInstance(societyInstances[i]);
					String agentTypeName = agentInstances[j].getTypeName();
					try
					{
						IAgentType agentType = modelManager.loadAgentTypeByName(agentTypeName,  agentTypeNames, imports, basePackageName);
						agentInstances[j].setType(agentType);
					}
					catch(Exception exc)
					{
						resolvingException.addNestedException(exc);
					}
				}
			}

			// resolve all societyinstancereference->societytype-relations
			ISocietyInstanceReference[] societyInstanceReferences = societyInstances[i].getSocietyInstanceReferences();
			for (int j=0; j < societyInstanceReferences.length; j++)
			{
				societyInstanceReferences[j].setParentSocietyInstance(societyInstances[i]);
				if ((societyInstanceReferences[j].getLocallyReferencedModel() == null) &&
					(!societyInstanceReferences[j].isRemoteReference()))
				{
					String societyTypeName = societyInstanceReferences[j].getTypeName();
					String societyInstanceName = societyInstanceReferences[j].getInstanceName();
					try
					{
						// first get (or load) the SocietyType
						ISocietyType societyType = modelManager.loadSocietyTypeByName(societyTypeName,  societyTypeNames, imports, basePackageName);

						// .. then get the societyInstance
						ISocietyInstance societyInstance = societyType.getSocietyInstance(societyInstanceName);

						// ... and then set the societyInstance as locally referenced model
						societyInstanceReferences[j].setLocallyReferencedSocietyInstance(societyInstance);
					}
					catch(Exception exc)
					{
						resolvingException.addNestedException(exc);
					}
				}
			}
		}

		if (resolvingException.hasNestedExceptions())
			throw resolvingException;
	}
}
