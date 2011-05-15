/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.repository;

import java.util.*;
import java.util.zip.ZipEntry;
import java.util.jar.JarFile;
import java.io.*;
import jade.tools.ascml.repository.loader.ModelIndex;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.events.ProgressUpdateEvent;
import jade.tools.ascml.model.jibx.SocietyType;
import jade.tools.ascml.model.jibx.AgentType;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;


/**
 *  The ModelManager caches society- and agentType-models which have
 *  been loaded by using the <i>loadModelByFileName</i>-method 
 *  (this is the only way for storing models into the repository).
 *  It implements methods for accessing the models already loaded
 *  as well as a method for load and create models out of a given xml-file.
 *  It uses the ModelConfiguration to load new models independent from the loader-implementation.
 *  todo: Use weak hashmap?!
 */
public class ModelManager
{
	/** This constant is the index-refresh-time.
	    for example: if set to 1000, a call for autoSearch will be omitted if
		the last call for autosearch has been made less than 1000 milliseconds ago */
	private static final long INDEX_REFRESH_TIME = 5000;

	private ModelIndex modelIndex;
	private AbstractModelFactory modelFactory;
	private Repository repository;

	/**
	 * The constructor must not be called directly, instead use the
	 * getModel-methods.
	 */
	public ModelManager(Repository repository) throws ResourceNotFoundException
	{
		this.repository = repository;
		modelFactory	= createModelFactory(repository.getProperties().getModelFactory());
		modelIndex		= new ModelIndex();
	}

	/**
	 * Returns the Map, containing all the names, that were found during the autosearch-process.
	 * @return The HashMap containing all models, that were found. 
	 *         The key of this HashMap is the file-name of the model, the value either also 
	 *         the file-name or the name of the jar-file containg the model-file.
	 */
	public ModelIndex getModelIndex()
	{
		return modelIndex;
	}

	/**
	 * Load a model (either AgentType or SocietyType) from the file-system.
	 * If the model has been loaded before, the reference to this model is returned,
	 * otherwise the model-object is created out of the description-file from the given source.
	 * @param modelIdentifier  String pointing to the description file.
	 *                         In case the model is contained within a jar/zip-Archive,
	 *                         the name has to be in the following form:
	 *                        'jarFile.[jar|zip]::modelFile.[agent|society].xml'.
	 *                        Note the '::' to separate the jarFile's name from the model-name !
	 * @return model-object containing all the information specified within the source.
	 *         Either a SocietyTypeModel or an AgentTypeModel is returned.
	 * @exception  ModelException is thrown when ... (toDo)
	 */
	public synchronized Object loadModelByFileName(Object modelIdentifier) throws ModelException, ResourceNotFoundException
	{
		// toDo: Bis jetzt werden nur fileNames als ModelSource zugelassen --> etwas generischer gestalten (z.B.datenbank)
		String modelID = (String) modelIdentifier;
		if(modelID.indexOf(".jar::") != -1)
		{
			modelID = modelID.substring(modelID.indexOf(".jar::")+6, modelID.length());
		}
		else if(modelID.indexOf(".zip::") != -1)
		{
			modelID = modelID.substring(modelID.indexOf(".zip::")+6, modelID.length());
		}

		// AgentTypeModels are reused throughout the whole ASCML-agent, this
		// means every agentInstance only keeps a reference to the appropiate
		// global AgentTypeModel-object. A change in this object effects therefore
		// all other agentInstanceModels with this type.
		// Each AgentTypeModel is stored in a HashMap once it is has been loaded and
		// a reference to it is returned if requested.
		Object model = modelIndex.getModel(modelID.toString());
		if (model != null)
		{
			return model;
		}

		// Model has not been loaded before, so create a new model using the Factory
		// and store it in the ModelIndex
		try
		{
			if (modelID.contains(".society.xml"))
			{
				model = modelFactory.createSocietyTypeModel(modelID, repository);
				modelIndex.addModel(((ISocietyType)model).getFullyQualifiedName(), model);
			}
			if (modelID.contains(".agent.xml"))
			{
				model = modelFactory.createAgentTypeModel(modelID, repository);
				modelIndex.addModel(((IAgentType)model).getFullyQualifiedName(), model);
			}
		}
		catch(ModelException me)
		{
			throw me;
		}
		catch(ResourceNotFoundException re)
		{
			throw re;
		}

		// Resolve references of the SocietyType
		// (AgentTypes need no reference-resolving, because they contain no references)
		try
		{
			if (model instanceof ISocietyType)
			{
				ModelReferenceResolver resolver = new ModelReferenceResolver();
				resolver.resolveReferences((ISocietyType)model, this);
			}
		}
		catch(Exception exc)
		{
			// may occur in case some references could not be resolved.
		}
		finally
		{
			// check the integrity of the model
			ModelIntegrityChecker checker = new ModelIntegrityChecker();
			checker.checkIntegrity(model);
		}

		return model;
	}

	/**
	 * Load an AgentType-model from the ModelIndex or the file-system.
	 * If the model has been loaded before, the reference to this model is returned,
	 * otherwise the model-object is created out of the description-file from the given source.
	 * This method is used exclusivly by the ModelReferenceResolver.
	 * @param agentName  The name of the AgentType (if not fully-qualified, make sure you specify agentTypeNames or imports
	 * @param agentTypeNames  A list of fully-qualified AgentType-names; one of these should match the not-fully-qualified agentName
	 * @param imports  A list of imports; if the agentName is not fully-qualified and also not
	 *                 found in the list of fully-qualified AgentType-names, the list of imports
	 *                 is used to construct possible fully-qualified names.
	 * @param basePackageName  This package-name is used to construct a fully-qualified agentName if all the
	 *                         above stated methods of constructing a fq-name fail.
	 * @return AgentType-model containing all the information specified within the source.
	 * @exception  ModelException is thrown when ... (toDo)
	 */
	public synchronized IAgentType loadAgentTypeByName(String agentName, String[] agentTypeNames, String[] imports, String basePackageName) throws ModelException, ResourceNotFoundException
	{
        // assume name is fully qualified and check if model has already been loaded
		AgentType model = (AgentType)modelIndex.getModel(agentName);
		if (model != null)
		{
			repository.getProject().addAgentType(model);
			return model; // it has been loaded, so return it
		}

		// check if suffix of agentTypeNames match the agentName
		for (int i=0; i < agentTypeNames.length; i++)
		{
			if (agentTypeNames[i].endsWith(agentName))
				model = (AgentType)modelIndex.getModel(agentTypeNames[i]);
			if (model != null)
			{
				repository.getProject().addAgentType(model);
				return model;
			}
		}

		// check if import + agentName is contained within the ModelIndex
		for (int i=0; i < imports.length; i++)
		{
			model = (AgentType)modelIndex.getModel(imports[i] + "." + agentName);
			if (model != null)
			{
				repository.getProject().addAgentType(model);
				return model;
			}
		}

		// check if basepackage-name + agentName is contained within the ModelIndex
		model = (AgentType)modelIndex.getModel(basePackageName + "." + agentName);
		if (model != null)
		{
			repository.getProject().addAgentType(model);
			return model;
		}

		// if the model-object has not been found, try to load from the file-system
        try
		{
			model = modelFactory.createAgentTypeModel(agentName, repository);
			if (model != null)
			{
				// check the integrity of the model
				ModelIntegrityChecker checker = new ModelIntegrityChecker();
				checker.checkIntegrity(model);
				modelIndex.addModel(model.getFullyQualifiedName(), model);

				repository.getProject().addAgentType(model);
				return model;
			}
		}
		catch(ResourceNotFoundException exc)
		{
			// not bad, try harder ...
		}

		// try to load the model from file-system using the fully-qualified name
		for (int i=0; i < agentTypeNames.length; i++)
		{
			try
			{
				if (agentTypeNames[i].endsWith(agentName))
				{
					model = modelFactory.createAgentTypeModel(agentTypeNames[i], repository);
					if (model != null)
					{
						// check the integrity of the model
						ModelIntegrityChecker checker = new ModelIntegrityChecker();
						checker.checkIntegrity(model);
						modelIndex.addModel(model.getFullyQualifiedName(), model);

						repository.getProject().addAgentType(model);
						return model;
					}
				}

			}
			catch(ResourceNotFoundException exc)
			{
				// not bad, try harder ...
			}
		}

		// try to load the model from file-system using the imports
		for (int i=0; i < imports.length; i++)
		{
			try
			{
				model = modelFactory.createAgentTypeModel(imports[i] + "." + agentName, repository);
				if (model != null)
				{
					// check the integrity of the model
					ModelIntegrityChecker checker = new ModelIntegrityChecker();
					checker.checkIntegrity(model);
					modelIndex.addModel(model.getFullyQualifiedName(), model);

					repository.getProject().addAgentType(model);
					return model;
				}
			}
			catch(ResourceNotFoundException exc)
			{
				// not bad, try harder ...
			}
		}

		// try to load the model from file-system using the basepackage-name
		// if this fails, loadModelByFileName throws an Exception, don't catch this
		// but let it pass to calling method, because the model could finally not be found
		model = modelFactory.createAgentTypeModel(basePackageName + "." + agentName, repository);
		if (model != null)
		{
			// check the integrity of the model
			ModelIntegrityChecker checker = new ModelIntegrityChecker();
			checker.checkIntegrity(model);
			modelIndex.addModel(model.getFullyQualifiedName(), model);

			repository.getProject().addAgentType(model);
			return model;
		}

		// the model could not even be found in the file-system, so return null
		return null;
	}

	/**
	 * Load a SocietyType-model from the ModelIndex or the file-system.
	 * If the model has been loaded before, the reference to this model is returned,
	 * otherwise the model-object is created out of the description-file from the given source.
	 * This method is used exclusivly by the ModelReferenceResolver.
	 * @param societyName  The name of the SocietyType (if not fully-qualified, make sure you specify societyTypeNames or imports
	 * @param societyTypeNames  A list of fully-qualified SocietyType-names; one of these should match the not-fully-qualified societyName
	 * @param imports  A list of imports; if the societyName is not fully-qualified and also not
	 *                 found in the list of fully-qualified SocietyType-names, the list of imports
	 *                 is used to construct possible fully-qualified names.
	 * @param basePackageName  This package-name is used to construct a fully-qualified societyName if all the
	 *                         above stated methods of constructing a fq-name fail.
	 * @return SocietyType-model containing all the information specified within the source.
	 * @exception  ModelException is thrown when ... (toDo)
	 */
	public synchronized ISocietyType loadSocietyTypeByName(String societyName, String[] societyTypeNames, String[] imports, String basePackageName) throws ModelException, ResourceNotFoundException
	{
        // assume name is fully qualified and check if model has already been loaded
		SocietyType model = (SocietyType)modelIndex.getModel(societyName);
		if (model != null)
		{
			repository.getProject().addSocietyType(model);
			return model; // it has been loaded, so return it
		}

		// check if suffix of societyTypeNames match the societyName
		for (int i=0; i < societyTypeNames.length; i++)
		{
			if (societyTypeNames[i].endsWith(societyName))
				model = (SocietyType)modelIndex.getModel(societyTypeNames[i]);
			if (model != null)
			{
				repository.getProject().addSocietyType(model);
				return model;
			}
		}

		// check if import + agentName is contained within the ModelIndex
		for (int i=0; i < imports.length; i++)
		{
			model = (SocietyType)modelIndex.getModel(imports[i] + "." + societyName);
			if (model != null)
			{
				repository.getProject().addSocietyType(model);
				return model;
			}
		}

		// check if basepackage-name + agentName is contained within the ModelIndex
		model = (SocietyType)modelIndex.getModel(basePackageName + "." + societyName);
		if (model != null)
		{
			repository.getProject().addSocietyType(model);
			return model;
		}

		// if the model-object has not been found, try to load from the file-system
        try
		{
			model = modelFactory.createSocietyTypeModel(societyName, repository);
			if (model != null)
			{
				// resolve the references
				modelIndex.addModel(model.getFullyQualifiedName(), model);
				ModelReferenceResolver resolver = new ModelReferenceResolver();
				resolver.resolveReferences(model, this);
				// check the integrity of the model
				ModelIntegrityChecker checker = new ModelIntegrityChecker();
				checker.checkIntegrity(model);

				repository.getProject().addSocietyType(model);
				return model;
			}
		}
		catch(ResourceNotFoundException exc)
		{
			// not bad, try harder ...
		}

		// try to load the model from file-system using the fully-qualified name
		for (int i=0; i < societyTypeNames.length; i++)
		{
			try
			{
				if (societyTypeNames[i].endsWith(societyName))
				{
					model = modelFactory.createSocietyTypeModel(societyTypeNames[i], repository);
					if (model != null)
					{
						// resolve the references
						modelIndex.addModel(model.getFullyQualifiedName(), model);
						ModelReferenceResolver resolver = new ModelReferenceResolver();
						resolver.resolveReferences(model, this);
						// check the integrity of the model
						ModelIntegrityChecker checker = new ModelIntegrityChecker();
						checker.checkIntegrity(model);

						repository.getProject().addSocietyType(model);
						return model;
					}
				}

			}
			catch(ResourceNotFoundException exc)
			{
				// not bad, try harder ...
			}
		}

		// try to load the model from file-system using the imports
		for (int i=0; i < imports.length; i++)
		{
			try
			{
				model = modelFactory.createSocietyTypeModel(imports[i] + "." + societyName, repository);
				if (model != null)
				{
					// resolve the references
					modelIndex.addModel(model.getFullyQualifiedName(), model);
					ModelReferenceResolver resolver = new ModelReferenceResolver();
					resolver.resolveReferences(model, this);
					// check the integrity of the model
					ModelIntegrityChecker checker = new ModelIntegrityChecker();
					checker.checkIntegrity(model);

					repository.getProject().addSocietyType(model);
					return model;
				}
			}
			catch(ResourceNotFoundException exc)
			{
				// not bad, try harder ...
			}
		}

		// try to load the model from file-system using the basepackage-name
		// if this fails, loadModelByFileName throws an Exception, don't catch this
		// but let it pass to calling method, because the model could finally not be found
		model = modelFactory.createSocietyTypeModel(basePackageName + "." + societyName, repository);
		if (model != null)
		{
			// resolve the references
			modelIndex.addModel(model.getFullyQualifiedName(), model);
			ModelReferenceResolver resolver = new ModelReferenceResolver();
			resolver.resolveReferences(model, this);
			// check the integrity of the model
			ModelIntegrityChecker checker = new ModelIntegrityChecker();
			checker.checkIntegrity(model);

			repository.getProject().addSocietyType(model);
			return model;
		}

		// the model could not even be found in the file-system, so return null
		return null;
	}

	/**
	 * Save a model (either AgentType or SocietyType) to the file-system.
	 * @param model  The model-object to save (either AgentType or SocietyType).
	 * @return  true, if the model has been successfully saved, false otherwise
	 */
	public synchronized boolean saveModel(Object model)
	{
        try
		{
			if (model instanceof AgentType)
			{
				modelFactory.saveAgentTypeModel((AgentType)model);
			}
			else if (model instanceof SocietyType)
			{
				modelFactory.saveSocietyTypeModel((SocietyType)model);
			}
			return true; // saving successful
		}
		catch(ModelException me)
		{
			repository.throwExceptionEvent(me);
			return false; // saving not successful
		}
	}

	/**
	 * When this method is called, the index, which contains all agent- and society-
	 * description-filenames is refreshed. The method may be called due to a change
	 * of the model-path or when files are added or deleted within this path.
	 * The 'searchRoots'-method scans the complete model-path for model-description-files
	 * and stores the name of the files found in a global HashMap.
	 * @return The HashMap containing all models, that were found.
	 *         The key of this HashMap is the file-name of the model, the value either also
	 *         the file-name or the name of the jar-file containg the model-file.
	 */
	public HashMap rebuildModelIndex()
	{
		String[] modelPaths = repository.getProperties().getModelLocations();
		String[] excludePaths = repository.getProperties().getExcludeSet();

		// check if the index is up to date and if so return it immediately.
		long now = System.currentTimeMillis();
		if ((now - INDEX_REFRESH_TIME) < modelIndex.getLastIndexRefreshTime())
			return modelIndex.getModels();

		// start refreshing the index

        repository.throwProgressUpdateEvent(new ProgressUpdateEvent("Creating autosearch-environment", ProgressUpdateEvent.PROGRESS_ADVANCE));

		String[] rootSearchPaths = getExclusiveModelSearchPath(modelPaths, excludePaths);

		for(int i = 0; i<rootSearchPaths.length; i++)
		{
			File filePointer = new File(rootSearchPaths[i]);
			File[] fileArray;

			if(rootSearchPaths[i].endsWith(".jar") || rootSearchPaths[i].endsWith(".zip"))
			{
				fileArray = new File[]{filePointer};
			}
			else
				fileArray = filePointer.listFiles();

			// now search in all subfolders recursively
			searchRoots(fileArray, new Vector());
		}
		return modelIndex.getModels();
	}

	private AbstractModelFactory createModelFactory(String modelFactoryString) throws ResourceNotFoundException
	{
		try
		{
			Class fac = Class.forName(modelFactoryString);
			return (AbstractModelFactory)fac.newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new ResourceNotFoundException("ModelFactory '"+modelFactoryString+"' not found !", "The Factory-class used for loading, parsing and building the Agent- and SocietyType-models could not be found. Please check the spelling and make sure, that the class lies within your classpath.", ResourceNotFoundException.FACTORY_NOT_FOUND);
		}
	}

	/**
	 * Returns an array of paths which could be automatically searched for
	 * AgentType- and Society-modelSources. This array is constructed out of all
	 * modelpath-elements minus the directories/archives contained in the exclude-set.
	 */
	private String[] getExclusiveModelSearchPath(String[] modelPaths, String[] excludePaths)
	{
		Vector rootSearchpaths = new Vector();

		for(int i = 0; i < modelPaths.length; i++)
		{
			String onePath = modelPaths[i].trim();
			boolean addPath = true;
			for(int j = 0; j < excludePaths.length; j++)
			{
				if(onePath.indexOf(excludePaths[j]) != -1)
					addPath = false;
			}
			// only add the path to the root-searchpath-Array if the path
			// is not contained within the exclude-set.
			if(addPath)
				rootSearchpaths.add(onePath);
		}

		// create an array-object out of the Vector and set the new modelPaths
		String[] returnArray = new String[rootSearchpaths.size()];
		String newmodelPaths = "";
		for(int i = 0; i < rootSearchpaths.size(); i++)
		{
			returnArray[i] = (String)rootSearchpaths.elementAt(i);
			String pathSeparator = System.getProperty("path.separator");
			if(returnArray[i].endsWith(pathSeparator))
				returnArray[i] = returnArray[i].substring(0, returnArray[i].length()-1);
			newmodelPaths += returnArray[i]+pathSeparator;
		}
		System.setProperty("java.class.path", newmodelPaths);
		return returnArray;
	}

	/**
	 * Checks if a given path is contained in the set of excluded elements.
	 * @param path  The path, that should be checked
	 * @return  true, if the path should be excluded, false otherwise.
	 */
	private boolean isExcluded(String path)
	{
		String[] excludePaths = repository.getProperties().getExcludeSet();
		for (int i=0; i < excludePaths.length; i++)
		{
			if (excludePaths[i].equals(path))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is called recursivly to store all agent- and society-
	 * description-files found in the modelPaths and jar-files.
	 */
	private void searchRoots(File[] f, Vector dirs)
	{
		try
		{
			for(int i = 0; i<f.length; i++)
			{
				try
				{
					String name = f[i].getCanonicalPath();
					if(f[i].isDirectory())
					{
						if (!isExcluded(name))
						{
							repository.throwProgressUpdateEvent(new ProgressUpdateEvent("Scanning: " + f[i].getAbsolutePath(), ProgressUpdateEvent.PROGRESS_ADVANCE));
							dirs.add(f[i]);
							searchRoots(f[i].listFiles(), dirs);
						}
					}
					else
					{
						if(name.endsWith(".agent.xml") || name.endsWith(".society.xml"))
						{
							modelIndex.addModel(name);
						}
						else if(name.endsWith(".jar") || name.endsWith(".zip"))
						{
							// Start looking into the jar-file for description-files
							try
							{
								JarFile jarFile = new JarFile(f[i]);
								Enumeration e = jarFile.entries();
								while(e.hasMoreElements())
								{
									ZipEntry jarFileEntry = (ZipEntry)e.nextElement();
									if(jarFileEntry.getName().endsWith(".agent.xml") || jarFileEntry.getName().endsWith(".society.xml"))
									{
										// System.err.println("ModelMap.put: " + f[i] + "::" + jarFileEntry.getName());
										modelIndex.addModel(f[i]+"::"+jarFileEntry.getName());
									}
								}
								jarFile.close();
							}
							catch(Exception e)
							{
								// e.printStackTrace();
							}
						}
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch(NullPointerException e)
		{
			// it's just a path in the modelPaths that doesn't exist, no worry
			// e.printStackTrace();
		}
	}

	public void exit()
	{
		
	}
}
