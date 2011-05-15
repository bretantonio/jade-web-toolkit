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

import jade.tools.ascml.events.PropertyListener;
import jade.tools.ascml.events.PropertyChangedEvent;
import jade.tools.ascml.exceptions.ResourceIOException;
import jade.tools.ascml.exceptions.ASCMLException;

import javax.swing.*;
import java.util.*;

public class PropertyManager
{
	public final static String DEFAULT_PROPERTY_FILE = "ascml.repository.properties.xml";

	/** The ASCML default factory. */
	public static final String DEFAULT_FACTORY = "jade.tools.ascml.repository.JiBXModelFactory";

	private HashMap projects;
	private String source;					/** Source of the Properties (i.e. property-file) */
	private String defaultModelFactory;
	private String activeProjectName;
	private String defaultLookAndFeel;
	private String[] defaultModelLocations;
	private String[] defaultModelExcludes;

	private ModelManager modelManager;
	private Repository repository;

	public PropertyManager(Repository repository)
	{
		this.repository = repository;
		projects = new HashMap();
		source = DEFAULT_PROPERTY_FILE;
		// defaultLookAndFeel = UIManager.getSystemLookAndFeelClassName();
		defaultLookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
		
		defaultModelLocations = new String[] {System.getProperty("user.dir")};
		defaultModelExcludes = new String[] {"http.jar", "iiop.jar", "jadeTools.jar", "Base64.jar", "jade.jar", "jdsl.jar" };
	}
	
	public void setSource(String propertyLocation)		
	{
		if ((propertyLocation != null) && (!propertyLocation.equals("")))
			this.source = propertyLocation;
	}

	public String getSource()
	{
		return source;
	}

    public void createNewProject(String name)
	{
		Project newProject = new Project(name, repository);
		addProject(newProject);
		setActiveProject(name);
	}

	public void addProject(Project oneProject)
	{
		projects.put(oneProject.getName(), oneProject);
		informListener(PropertyChangedEvent.PROJECTS_CHANGED);
	}

	public void removeProject(Project oneProject)
	{
		projects.remove(oneProject);
		informListener(PropertyChangedEvent.PROJECTS_CHANGED);
	}

	public HashMap getProjects()
	{
		return projects;
	}

	public int getProjectCount()
	{
		return projects.size();
	}

	public String[] getProjectNames()
	{
		Iterator iter = projects.keySet().iterator();
		Vector projectNamesVector = new Vector();

		while (iter.hasNext())
		{
			projectNamesVector.add(iter.next());
		}
		
		String[] returnArray = new String[projectNamesVector.size()];
		projectNamesVector.toArray(returnArray);
		return returnArray;
	}

	public Project getProject(String projectName)
	{
		Project returnProject = (Project) projects.get(projectName);
        return returnProject;
	}
	
	public void setModelFactory(String loader)
	{
		defaultModelFactory = loader;
	}

	public String getModelFactory()
	{
		if ((defaultModelFactory == null) || (defaultModelFactory.equals("")))
			return DEFAULT_FACTORY;
		else
			return defaultModelFactory;
	}
	
	public void setActiveProject(String projectName)
	{
		activeProjectName = projectName;
		informListener(PropertyChangedEvent.ACTIVE_PROJECT_CHANGED);
	}

	public Project getActiveProject()
	{
		// When getting the default Project, the exception is caught here
		// for convenience. There should no exception occur, when getting
		// the default-project at runtime.
		return getProject(activeProjectName);
	}

	/**
	 *  So far only used by DetailPanelRepositoryOptions- and RepositoryOptions-classes.
	 */
	public void setExcludeSet(String[] excludeSet)
	{
		defaultModelExcludes = excludeSet;
		informListener(PropertyChangedEvent.EXCLUDELOCATIONS_CHANGED);
	}
	
	/**
	 *  So far only used by DetailPanelRepositoryOptions- and RepositoryOptions-classes.
	 */
	public String[] getExcludeSet()
	{
		return defaultModelExcludes;
	}
	
	/**
	 *  So far only used by DetailPanelRepositoryOptions- and RepositoryOptions-classes.
	 */
	public void setModelLocations(String[] locs)
	{
		defaultModelLocations = locs;
		informListener(PropertyChangedEvent.MODELLOCATIONS_CHANGED);
	}
	
	/**
	 *  So far only used by DetailPanelRepositoryOptions- and RepositoryOptions-classes.
	 */
	public String[] getModelLocations()
	{
		return defaultModelLocations;
	}

	/**
	 * Get the Look & Feel for the ASCML.
	 * @return  The classname of the Look & Feel-class
	 */
	public String getLookAndFeel()
	{
		return defaultLookAndFeel;
	}

	/**
	 * Sets the Look & Feel for the ASCML.
	 * @param lookAndFeelClassName  The className of the Look & Feel-class.
	 */
	public void setLookAndFeel(String lookAndFeelClassName)
	{
		this.defaultLookAndFeel = lookAndFeelClassName;
	}

	public void setModelManager(ModelManager modelManager)
	{
        this.modelManager = modelManager;
	}

	private void informListener(String eventCode)
	{
		PropertyChangedEvent event = new PropertyChangedEvent(eventCode, this);
        Vector propertyListener = repository.getListenerManager().getPropertyListener();
		for (int i=0; i < propertyListener.size(); i++)
		{
			((PropertyListener)propertyListener.elementAt(i)).propertiesChanged(event);
		}
	}

	/**
	 *  Read the properties from the given source
	 */
	public void readProperties()
	{
        try
		{
			new PropertyPersistenceManager().readProperties(repository);
		}
		catch(ASCMLException e)
		{
			repository.throwExceptionEvent(e);
		}
	}        
	
	/**
	 *  Write the properties to the given source
	 */
	public void writeProperties() throws ResourceIOException
	{
		new PropertyPersistenceManager().writeProperties(this);
	}

	public void createNewPropertyFile(String propertyFile)
	{
		setSource(propertyFile);
		try
		{
			createNewProject(Project.DEFAULT_PROJECT_NAME);
			new PropertyPersistenceManager().createNewProperties(this);
		}
		catch(ASCMLException exc)
		{
			repository.throwExceptionEvent(exc);
		}
	}

	public void exit() throws ResourceIOException
	{
		writeProperties();
	}
}
