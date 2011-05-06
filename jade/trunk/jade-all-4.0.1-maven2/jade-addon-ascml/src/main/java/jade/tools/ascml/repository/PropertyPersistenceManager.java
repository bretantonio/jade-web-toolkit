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

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.xml.sax.SAXException;
import jade.tools.ascml.exceptions.ResourceNotFoundException;
import jade.tools.ascml.exceptions.ResourceIOException;
import jade.tools.ascml.absmodel.IAgentType;
import jade.tools.ascml.absmodel.ISocietyType;

public class PropertyPersistenceManager
{
	public final static String TAG_ASCML_PROPERTIES	= "ascml-properties";
	public final static String TAG_DEFAULTS			= "defaults";
	public final static String TAG_MODELLOADER		= "model-loader";
	public final static String TAG_LOOK_AND_FEEL	= "look-and-feel";
	public final static String TAG_STARTUP_PROJECT	= "startup-project";
	public final static String TAG_MODEL_PATHS		= "model-paths";
	public final static String TAG_PATH				= "path";
	public final static String TAG_EXCLUDE			= "exclude";
	public final static String TAG_PROJECTS			= "projects";
	public final static String TAG_PROJECT			= "project";
	public final static String TAG_TREE_VIEW		= "tree-view";
	public final static String TAG_SOCIETYTYPES		= "society-types";
	public final static String TAG_SOCIETY			= "society";
	public final static String TAG_AGENTTYPES		= "agent-types";
	public final static String TAG_AGENT			= "agent";
	public final static String TAG_WORKING_DIR		= "working-directory";
	
	public final static String ATTRIBUTE_NAME		= "name";
	
	private Object propertyLocation;
    private PropertyManager properties;

	public PropertyPersistenceManager()
	{
	}

	public void createNewProperties(PropertyManager properties) throws ResourceIOException
	{
		this.properties = properties;

		// build the new DOM-tree document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = null;
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();  // Create from whole cloth

			Element root = (Element) document.createElement(TAG_ASCML_PROPERTIES);
			document.appendChild(root);

			// create the default-part
			Element defaultsRoot = document.createElement(TAG_DEFAULTS);
			root.appendChild( defaultsRoot );
			createDefaultsNode(document, defaultsRoot);

			// create the project-part
			Element projectsRoot = document.createElement(TAG_PROJECTS);
			root.appendChild( projectsRoot );
			String[] projectNames = properties.getProjectNames();

			for (int i=0; i < projectNames.length; i++)
			{
				createProjectNode(document, projectsRoot, properties.getProject(projectNames[i])); // Empty default project
			}
		}
		catch (ParserConfigurationException exc)
		{
			// Parser with specified options can't be built
			exc.printStackTrace();
		}

		// DOM-tree has been built, now write the tree to propertyFile.

		File propertyFile = new File(properties.getSource());

		// Use a Transformer for output
		try
		{
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(propertyFile);
			transformer.transform(source, result);
			// return newPropertyFile.getCanonicalFile().toString();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void createDefaultsNode(Document document, Node rootNode)
	{
        Node modelLoaderRoot = rootNode.appendChild( document.createElement(TAG_MODELLOADER) );
		modelLoaderRoot.appendChild( document.createTextNode(properties.getModelFactory()) );

		Node lookAndFeelRoot = rootNode.appendChild( document.createElement(TAG_LOOK_AND_FEEL) );
		lookAndFeelRoot.appendChild( document.createTextNode(properties.getLookAndFeel()) );

		Node startupProjectRoot = rootNode.appendChild( document.createElement(TAG_STARTUP_PROJECT) );
		startupProjectRoot.appendChild( document.createTextNode(properties.getActiveProject().getName()) );

		Node modelPathsRoot = rootNode.appendChild( document.createElement(TAG_MODEL_PATHS) );

		String[] paths = properties.getModelLocations();
		for (int i=0; i < paths.length; i++)
		{
			Node onePath = modelPathsRoot.appendChild( document.createElement(TAG_PATH) );
			onePath.appendChild( document.createTextNode(paths[i]) );
		}

		String[] excludes = properties.getExcludeSet();
		for (int i=0; i < excludes.length; i++)
		{
			Node oneExclude = modelPathsRoot.appendChild( document.createElement(TAG_EXCLUDE) );
			oneExclude.appendChild( document.createTextNode(excludes[i]) );
		}
	}

	private void createProjectNode(Document document, Node rootNode, Project project)
	{
		Element projectElement = document.createElement(TAG_PROJECT);
		projectElement.setAttribute(ATTRIBUTE_NAME, project.getName());
		Node projectRoot = rootNode.appendChild( projectElement );

		Node treeViewNode = projectRoot.appendChild( document.createElement(TAG_TREE_VIEW) );
		treeViewNode.appendChild( document.createTextNode(project.getView()) );

		Node workingDirectoryNode = projectRoot.appendChild( document.createElement(TAG_WORKING_DIR) );
		workingDirectoryNode.appendChild( document.createTextNode(project.getWorkingDirectory()) );

		// create all the societyType-nodes

		Node socTypesRootNode = projectRoot.appendChild( document.createElement(TAG_SOCIETYTYPES) );
		ISocietyType[] societyTypes = project.getSocietyTypes();

		for (int i=0; i < societyTypes.length; i++)
		{
			Element societyElement = document.createElement(TAG_SOCIETY);
			societyElement.appendChild(document.createTextNode(societyTypes[i].getDocument().getSource()));

			socTypesRootNode.appendChild( societyElement );
		}

		// create all the agentType-nodes

		Node agentTypesRootNode = projectRoot.appendChild( document.createElement(TAG_AGENTTYPES) );
		IAgentType[] agentTypes = project.getAgentTypes();

		for (int i=0; i < agentTypes.length; i++)
		{
			Element agentElement = document.createElement(TAG_AGENT);
			agentElement.appendChild(document.createTextNode(agentTypes[i].getDocument().getSource()));

			agentTypesRootNode.appendChild( agentElement );
		}

	}

	public void writeProperties(PropertyManager properties) throws ResourceIOException
	{
		createNewProperties(properties);
	}

	/**
	 *  Read all the ASCML-properties and create the Property-object.
	 *  @param repository  The repository-object
	 */
	public void readProperties(Repository repository) throws ResourceNotFoundException, ResourceIOException
	{
		this.propertyLocation = repository.getProperties().getSource();

		InputStream is = PropertyPersistenceManager.class.getClassLoader().getResourceAsStream((String)propertyLocation);
		if (is == null)
		{
			try
			{
				// System.err.println("PropertyPersistenceManager.readProperties: ClassLoader failed, trying fileInputStream");
				is = new FileInputStream((String)propertyLocation);
				if (is == null)
				{
					// System.err.println("PropertyPersistenceManager.readProperties: FileInputStream I failed");
					// System.err.println("PropertyPersistenceManager.readProperties: trying " + System.getProperty("user.dir") + File.separatorChar + (String)propertyLocation);
					is = new FileInputStream(System.getProperty("user.dir") + File.separatorChar + (String)propertyLocation);
				}
			}
			catch(FileNotFoundException exc)
			{
				// System.err.println("PropertyPersistenceManager.readProperties: FileInputStream II failed");
				throw new ResourceNotFoundException("Sorry, couldn't find property-location '"+propertyLocation+"'", exc, ResourceNotFoundException.PROPERTIES_NOT_FOUND);
			}
		}

		Document document = readDocument(is);
		if (document == null)
			throw new ResourceNotFoundException("The required property-file could not be found at all, without it the ASCML cannot run and therefore terminates now.", (String)propertyLocation, ResourceNotFoundException.PROPERTIES_NOT_FOUND);
		
		// init the default-values and load the default-model
		initProperties(repository, document.getDocumentElement());
		initProjects(repository, document.getDocumentElement());
	}
	
	private void initProperties(Repository repository, Node rootNode)
	{
		Vector dummydefaultModelLocations = new Vector();
		Vector dummyDefaultExcludeModelPaths = new Vector();
		
		NodeList defaultORProjectNodes = rootNode.getChildNodes();
		
		for (int k=0; k < defaultORProjectNodes.getLength(); k++)
		{
			Node defaultsNode = defaultORProjectNodes.item(k); 
			
			if (defaultsNode.getNodeName().equals(TAG_DEFAULTS))
			{
				NodeList defaultNodes = defaultsNode.getChildNodes();
				
				for (int i=0; i < defaultNodes.getLength(); i++)
				{
					Node oneNode = defaultNodes.item(i);
					if(oneNode.getNodeName().equals(TAG_MODELLOADER))
					{
						repository.getProperties().setModelFactory(oneNode.getFirstChild().getNodeValue().trim());
					}
					if(oneNode.getNodeName().equals(TAG_LOOK_AND_FEEL))
					{
						repository.getProperties().setLookAndFeel(oneNode.getFirstChild().getNodeValue().trim());
					}
					else if(oneNode.getNodeName().equals(TAG_STARTUP_PROJECT))
					{
						repository.getProperties().setActiveProject(oneNode.getFirstChild().getNodeValue().trim());
					}
					else if(oneNode.getNodeName().equals(TAG_MODEL_PATHS))
					{
						NodeList defaultPathNodes = oneNode.getChildNodes();
	
						for (int j=0; j < defaultPathNodes.getLength(); j++)
						{
							Node onePathNode = defaultPathNodes.item(j);
							if(onePathNode.getNodeName().equals(TAG_PATH))
							{
								dummydefaultModelLocations.add(onePathNode.getFirstChild().getNodeValue().trim());
							}
							else if(onePathNode.getNodeName().equals(TAG_EXCLUDE))
							{
								dummyDefaultExcludeModelPaths.add(onePathNode.getFirstChild().getNodeValue().trim());
							}
						}
					}
				} // end of for
			}
		} // end of for
		
		// store the dummyVectors in the right String-arrays
		String[] defaultModelLocations = new String[dummydefaultModelLocations.size()];
		String[] defaultModelExcludes = new String[dummyDefaultExcludeModelPaths.size()];
		dummydefaultModelLocations.toArray(defaultModelLocations);
		dummyDefaultExcludeModelPaths.toArray(defaultModelExcludes);
		repository.getProperties().setModelLocations(defaultModelLocations);
		repository.getProperties().setExcludeSet(defaultModelExcludes);
	}
	
	private void initProjects(Repository repository, Node rootNode)
	{
		NodeList defaultORProjectNodes = rootNode.getChildNodes();
		
		for (int k=0; k < defaultORProjectNodes.getLength(); k++)
		{
			Node projectsNode = defaultORProjectNodes.item(k); 
			if (projectsNode.getNodeName().equals(TAG_PROJECTS))
			{	
				NodeList projectNodes = projectsNode.getChildNodes();
				
				for (int i=0; i < projectNodes.getLength(); i++)
				{
					Node oneNode = projectNodes.item(i);
					
					if (oneNode.getNodeName().equals(TAG_PROJECT) && (oneNode.getAttributes() != null))
					{
						String projectName = oneNode.getAttributes().getNamedItem(ATTRIBUTE_NAME).getNodeValue().trim();
						
						// create new project-object
						Project project = new Project(projectName, repository);
						initProject(project, oneNode);
						
						repository.getProperties().addProject(project);
					}
				} // end of for (...projectNodes.getLength()...)
			}
		} // end of for (...defaultORProjectNodes.getLength()...)
	}
	
	private void initProject(Project project, Node projectRootNode)
	{
		// System.err.println("PropertyPersistenceManager.initProject: Project '" + project+ "' gefunden, fertig zur initialisierung...");

		// now iterate through all child-nodes in order to get all other information
		NodeList projectContentNodes = projectRootNode.getChildNodes();
		for (int j=0; j < projectContentNodes.getLength(); j++)
		{
			Node oneContentNode = projectContentNodes.item(j);
			if (oneContentNode.getNodeName().equals(TAG_TREE_VIEW))
			{
				project.setView(oneContentNode.getFirstChild().getNodeValue().trim());
				// System.err.println("TreeView '" + projectModel.getTreeView());
			}
			else if (oneContentNode.getNodeName().equals(TAG_WORKING_DIR))
			{
				project.setWorkingDirectory(oneContentNode.getFirstChild().getNodeValue().trim());
				// System.err.println("Working-Dir '" + projectModel.getWorkingDirectory());
			}
			else if (oneContentNode.getNodeName().equals(TAG_SOCIETYTYPES))
			{
				NodeList societyTypeNodes = oneContentNode.getChildNodes();
				
				// process each society, one by one
				for (int m=0; m < societyTypeNodes.getLength(); m++)
				{
					Node oneSocietyTypeNode = societyTypeNodes.item(m);
					if (oneSocietyTypeNode.getNodeName().equals(TAG_SOCIETY))
					{
						String sourceName = oneSocietyTypeNode.getFirstChild().getNodeValue().trim();
						// temporarly add the agentType, cause it must be initialized (the model-object must be created) later.
						project.addTemporarySocietyType(sourceName);

						// System.err.println("PropertyPersistenceManager.initProject: Loaded societyTypeModel and added it");
					}
				}
				// these nodes are processed at the end of this method
			}
			else if (oneContentNode.getNodeName().equals(TAG_AGENTTYPES))
			{
				NodeList agentTypeNodes = oneContentNode.getChildNodes();
				// process each agent, one by one
				for (int m=0; m < agentTypeNodes.getLength(); m++)
				{
					Node oneAgentTypeNode = agentTypeNodes.item(m);
					if (oneAgentTypeNode.getNodeName().equals(TAG_AGENT))
					{
						String sourceName = oneAgentTypeNode.getFirstChild().getNodeValue().trim();
						// temporarly add the agentType, cause it must be initialized (the model-object must be created) later.
						project.addTemporaryAgentType(sourceName);
						
						// System.err.println("PropertyPersistenceManager.initProject: Loaded agentTypeModel and added it");
					}
				}
			}
		} // end of for (...projectContentNodes.getLength()...)
	}
	
	/**
	 * Read the xml file and generate the DOM-tree.
	 *
	 * @param inputStream An InputStream to the xmlFile, this may be a FileInputStream
	 *                    in case the file can be adressed directly or a ByteArrayInputStream
	 *                    in case the file is contained within a jar-archiv.
	 * @return The document.
	 */
	private Document readDocument(InputStream inputStream) throws ResourceIOException
	{
		Document document = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setIgnoringComments(true);
		// factory.setErrorHandler(this);

		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			// parse and build the DOM tree
			document = builder.parse(inputStream);
			inputStream.close();
		}
		catch(javax.xml.parsers.ParserConfigurationException p)
		{
			throw new ResourceIOException("XML-Parser for loading the model-files is not configured properly.", p, ResourceIOException.PROPERTIES_IO);
		}
		catch(SAXException s)
		{
			throw new ResourceIOException("Error while parsing the property-file '"+propertyLocation+"'", s, ResourceIOException.PROPERTIES_IO);
		}
		catch(IOException i)
		{
			throw new ResourceIOException("Error while reading the property-file '"+propertyLocation+"'", i, ResourceIOException.PROPERTIES_IO);
		}


		return document;
	}		
}
