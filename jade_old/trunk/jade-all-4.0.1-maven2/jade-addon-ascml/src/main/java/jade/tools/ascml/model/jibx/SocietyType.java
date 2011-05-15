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


package jade.tools.ascml.model.jibx;

import java.util.*;
import javax.swing.ImageIcon;
import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.model.Document;
import jade.tools.ascml.absmodel.ISocietyInstance;
import jade.tools.ascml.absmodel.ISocietyType;
import jade.tools.ascml.absmodel.IDocument;

/**
 * Model-object containing all required information about a society including all
 * neccessary Scenario-, AgentType-, AgentInstance- and other referenced SocietyTypeModels.
 */
public class SocietyType implements ISocietyType
{

	protected String name = NAME_UNKNOWN;
	protected String description = "";
	protected String packageName = NAME_UNKNOWN;
	protected String iconName = "";

	protected ArrayList<String> importList = new ArrayList<String>();
    protected ArrayList<String> agentTypeNameList = new ArrayList<String>();
	protected ArrayList<String> societyTypeNameList = new ArrayList<String>();

	protected String defaultSocietyInstance = SocietyInstance.NAME_UNKNOWN;
    protected ArrayList<ISocietyInstance> societyInstanceList = new ArrayList<ISocietyInstance>();

	protected IDocument document = new Document();

	/**
		 * The status indicates if this model has successfully been loaded.
		 * Possible stati are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
		 */
	protected String status = "";

	/**
	 * This Exception may contain a set of detailed String-messages in case the status is != STATUS_OK
	 */
	protected ModelException statusException;

	private Vector modelChangedListener;
	private Vector longTimeActionStartListener;

	public SocietyType()
	{
		// needed by JiBX
	}

	/**
	 * Set the name of this SocietyType.
	 * @param name  The name of the SocietyType.
	 */
	public void setName(String name)
	{
		if ((name == null) || (name.trim().equals("")))
			this.name = NAME_UNKNOWN;
		if (name.equals(getName()))
			return;

		this.name = name.trim();
		throwModelChangedEvent(ModelChangedEvent.NAME_CHANGED);
	}

	/**
	 * Get the name of this SocietyType.
	 * @return The name of this SocietyType.
	 */
	public String getName()
	{
		if (name == null)
			return NAME_UNKNOWN;
		return name;
	}

	/**
	 * Get the package-name of this SocietyType
	 * @return  The package-name of this SocietyType (e.g. examples.party)
	 */
	public String getPackageName()
	{
		if (packageName == null)
			return getName();
		return packageName;
	}

	/**
	 * Set the package-name of this SocietyType
	 * @param packageName  The package-name of this SocietyType (e.g. examples.party)
	 */
	public void setPackageName(String packageName)
	{
		if (packageName == null)
			packageName = NAME_UNKNOWN;
		if (packageName.equals(getPackageName()))
			return;

		this.packageName = packageName;
		throwModelChangedEvent(ModelChangedEvent.PACKAGE_CHANGED);
	}

	/**
	 * Get the fully-qualified SocietyType's name.
	 * The name is composed of the package-name and the SocietyType's name,
	 * for example my.packageName.SocietyTypeName would be a correct 'fully-qualified' SocietyType-name.
	 *  @return  fully-qualified name of the SocietyType.
	 */
	public String getFullyQualifiedName()
	{
		if ((getPackageName() == null) || getPackageName().equals("") || getPackageName().equals(NAME_UNKNOWN))
			return getName();
		else
			return getPackageName()+"."+getName();
	}

	/**
	 * Set the icon-name for this SocietyType.
	 * Using the getIcon-method an ImageIcon is constructed out of this name.
	 * @param iconName  The name of the icon representing this SocietyType.
	 */
	public void setIconName(String iconName)
	{
		if (iconName == null)
			iconName = ImageIconLoader.SOCIETYTYPE;
		if (iconName.equals(getIconName()))
			return;

		this.iconName = iconName;
		throwModelChangedEvent(ModelChangedEvent.ICON_CHANGED);
	}

	/**
	 * Get the icon-name for this SocietyType.
	 * Using the getIcon-method an ImageIcon is constructed out of this name.
	 * @return The name of the icon representing this SocietyType.
	 */
	public String getIconName()
	{
		if (iconName != null)
			return iconName;
		else
			return ImageIconLoader.SOCIETYTYPE; // get default-iconName
	}

	/**
	 *  Get the ImageIcon representing this SocietyType.
	 *  @return The ImageIcon representing this SocietyType.
	 */
	public ImageIcon getIcon()
	{
		if ((iconName != null) && !iconName.equals(""))
			return ImageIconLoader.createImageIcon(iconName);
		else
			return ImageIconLoader.createImageIcon(ImageIconLoader.SOCIETYTYPE); // get default-icon
	}

	/* ------------------------------------------------------------------------------------ */

	/**
	 * Get the status of this SocietyType.
	 * The status indicates, whether loading was successful or not.
	 * @return  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public String getStatus()
	{
		if (status == null)
			return STATUS_ERROR;

		return this.status;
	}

	/**
	 * Set the status of this SocietyType.
	 * The status indicates, whether loading was successful or not.
	 * @param newStatus  The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public void setStatus(String newStatus)
	{
		if ((newStatus == null) || newStatus.equals(""))
			newStatus = STATUS_ERROR;
		if (newStatus.equals(getStatus()))
			return;

		this.status = newStatus;
		throwModelChangedEvent(ModelChangedEvent.STATUS_CHANGED);
	}

	/**
	 * Get the integrity-status, which describes loading-errors or -warnings in detail.
	 * The ModelIntegrityChecker may set the integrity-status upon checking the integrity.
	 * @return  A ModelException containing detailed integrity-messages.
	 */
	public ModelException getIntegrityStatus()
	{
		if ((statusException != null) && (statusException.hasExceptionDetails() || statusException.hasNestedExceptions()))
			return statusException;
		else
			return null;
	}

	/**
	 * Set the integrity-status, which describes loading-errors or -warnings in detail.
	 * The ModelIntegrityChecker may set the integrity-status upon checking the integrity.
	 * @param statusException  A ModelException containing detailed integrity-messages.
	 */
	public void setIntegrityStatus(ModelException statusException)
	{
		this.statusException = statusException;
	}

	/**
	 *  Set the document specifying the source from which this SocietyType has been loaded.
	 *  @param document  The document from which this SocietyType has been loaded..
	 */
	public void setDocument(IDocument document)
	{
		this.document = document;
	}

	/**
	 *  Get the document specifying the source from which this SocietyType has been loaded.
	 *  @return  The document from which this SocietyType has been loaded..
	 */
	public IDocument getDocument()
	{
		return document;
	}

	/**
	 * Set the SocietyType's description.
	 * @param description  The description of this SocietyType.
	 */
	public void setDescription(String description)
	{
		if (description == null)
			description = "";
		if (description.equals(getDescription()))
			return;
		this.description = description.trim();
		throwModelChangedEvent(ModelChangedEvent.DESCRIPTION_CHANGED);
	}

	/**
	 * Get the SocietyType's description.
	 * @return  The description of this SocietyType.
	 */
	public String getDescription()
	{
		if (description == null)
			return "";
		return description;
	}

	// -----------------------------------------------------------------------------------

	/**
	 * Add an import for this SocietyType.
	 * @param oneImport  String-representation of the import to add.
	 */
	public void addImport(String oneImport)
	{
		this.importList.add(oneImport);
	}

	/**
	 * Remove an import from this SocietyType.
	 * @param oneImport  String-representation of the import to be removed.
	 */
	public void removeImport(String oneImport)
	{
		this.importList.remove(oneImport);
	}

	/**
	 * Get all imports for this SocietyType.
	 * @return  String-array containing all the imports defindes for this SocietyType.
	 */
	public String[] getImports()
	{
		String[] returnArray = new String[importList.size()];
		importList.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Add an AgentType-name to the SocietyType.
	 * @param name  The AgentType's fully-qualified name (e.g. 'jade.examples.PingPongAgent') to add.
	 */
	public void addAgentTypeName(String name)
	{
		agentTypeNameList.add(name);
	}

	/**
	 * Remove an AgentType-name from this SocietyType.
	 * @param name  The AgentType's fully-qualified name (e.g. 'jade.examples.PingPongAgent') to remove
	 */
	public void removeAgentTypeName(String name)
	{
		agentTypeNameList.remove(name);
	}

	/**
	 * Get the names of all AgentTypes possibly referenced by a SocietyInstance of this SocietyType.
	 * @return  All AgentType-names as String-Array
	 */
	public String[] getAgentTypeNames()
	{
		String[] returnArray = new String[agentTypeNameList.size()];
		agentTypeNameList.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Add a SocietyType-name to the SocietyType.
	 * @param name  The SocietyType's fully-qualified name (e.g. 'jade.examples.PingPongSociety') to add
	 */
	public void addSocietyTypeName(String name)
	{
		societyTypeNameList.add(name.trim());
	}

	/**
	 * Remove a SocietyType-name from this SocietyType.
	 * @param name  The SocietyType's fully-qualified name (e.g. 'jade.examples.PingPongSociety') to remove
	 */
	public void removeSocietyTypeName(String name)
	{
		societyTypeNameList.remove(name);
	}

	/**
	 * Get the names of all SocietyTypes possibly referenced by a SocietyInstance of this SocietyType.
	 * @return  All SocietyType-namesas String-Array
	 */
	public String[] getSocietyTypeNames()
	{
		String[] returnArray = new String[societyTypeNameList.size()];
		societyTypeNameList.toArray(returnArray);
		return returnArray;
	}

	// ---------------------------------------------------------------------------------

	/**
	 * Add a SocietyInstance to this SocietyType.
	 * @param societyInstance  The SocietyInstance to add.
	 */
	public void addSocietyInstance(ISocietyInstance societyInstance)
	{
		if (societyInstance != null)
		{
			societyInstance.setParentSocietyType(this);
			societyInstanceList.add(societyInstance);
			throwModelChangedEvent(ModelChangedEvent.SOCIETYINSTANCE_ADDED);
			if (getDefaultSocietyInstanceName() == NAME_UNKNOWN)
				setDefaultSocietyInstanceName(societyInstance.getName());
		}
	}

	/**
	 * Get a SocietyInstance.
	 * @param index The index of the SocietyInstance within the internal ArrayList.
	 * @return The corresponding SocietyInstance at the specified index.
	 */
	public ISocietyInstance getSocietyInstance(int index)
	{
		return societyInstanceList.get(index);
	}

	/**
	 * Get a SocietyInstance.
	 * @param name  The name of the SocietyInstance.
	 * @return  The corresponding SocietyInstance with the given name.
	 */
	public ISocietyInstance getSocietyInstance(String name)
	{
		for (int i=0; i < societyInstanceList.size(); i++)
		{
			if (getSocietyInstance(i).getName().equals(name))
				return getSocietyInstance(i);
		}
		return null;
	}

	/**
	 * Get all SocietyInstances.
	 * @return  An array containing all SocietyInstances this SocietyType contains.
	 */
	public ISocietyInstance[] getSocietyInstances()
	{
		SocietyInstance[] returnArray = new SocietyInstance[societyInstanceList.size()];
		societyInstanceList.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Remove a SocietyInstance from this SocietyType.
	 * @param societyInstance  The SocietyInstance to remove
	 */
	public void removeSocietyInstance(ISocietyInstance societyInstance)
	{
		if (societyInstanceList.contains(societyInstance))
		{
			societyInstanceList.remove(societyInstance);
			throwModelChangedEvent(ModelChangedEvent.SOCIETYINSTANCE_REMOVED);
		}
	}

	/**
	 * Set the the name of the default-SocietyInstance.
	 * @param defaultSocietyInstance  The name of the SocietyInstance used as default-SocietyInstance.
	 */
	public void setDefaultSocietyInstanceName(String defaultSocietyInstance)
	{
		if ((defaultSocietyInstance==null) || defaultSocietyInstance.equals(""))
			defaultSocietyInstance = SocietyInstance.NAME_UNKNOWN;
		if (defaultSocietyInstance.equals(getDefaultSocietyInstanceName()))
			return;

		this.defaultSocietyInstance = defaultSocietyInstance;
		throwModelChangedEvent(ModelChangedEvent.DEFAULT_INSTANCE_CHANGED);
	}

	/**
	 * Get the name of the default-SocietyInstance.
	 * @return  The name of the SocietyInstance used as default-SocietyInstance.
	 */
	public String getDefaultSocietyInstanceName()
	{
		return this.defaultSocietyInstance;
	}

	/**
	 * Get the default-SocietyInstance.
	 * If no default SocietyInstance is explicitly definded, return the
	 * first one of the internal ArrayList.
	 * @return  The SocietyInstance used as default-SocietyInstance.
	 */
	public ISocietyInstance getDefaultSocietyInstance()
	{
		for (int i=0; i < societyInstanceList.size(); i++)
		{
			ISocietyInstance oneInstance = societyInstanceList.get(i);
			if ((oneInstance.getName().equals(defaultSocietyInstance)) || (oneInstance.getFullyQualifiedName().equals(defaultSocietyInstance)))
				return oneInstance;
		}
		return societyInstanceList.get(0);
	}

	// ---------------------------------------------------------------------------------

	/**
	 * Set the ModelChangedListener. These are informed in case of changes in this model.
	 * @param modelChangedListener  A Vector containing ModelChangedListener
	 */
	public void setModelChangedListener(Vector modelChangedListener)
	{
		this.modelChangedListener = modelChangedListener;
	}

	/**
	 * Get all the ModelChangedListener.
	 * @return  A Vector containing ModelChangedListener
	 */
	public Vector getModelChangedListener()
	{
		return modelChangedListener;
	}

	/**
	 * Get all the LongTimeActionStartListener.
	 * @return  A Vector containing LongTimeActionStartListener
	 */
	public Vector getLongTimeActionStartListener()
	{
		return longTimeActionStartListener;
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode);
		for (int i=0; i < modelChangedListener.size(); i++)
		{
			((ModelChangedListener)modelChangedListener.elementAt(i)).modelChanged(event);
		}
	}

	/**
	 * This method returns a short String with the society-name.
	 * It is used by the RepositoryTree for example, to name the nodes.
	 *
	 * @return String containing the name of this society.
	 */
	public String toString()
	{
		return getName();// + " " + super.toString();
	}

	/**
	 * This method returns a formatted String showing the societyType-model.
	 *
	 * @return formatted String showing ALL information about this society.
	 */
	public String toFormattedString()
	{
		Iterator keys;
		String str = "";

		str += "Society : name = "+name+"\n";

		String[] agentTypeNames = getAgentTypeNames();
		for (int i=0; i < agentTypeNames.length; i++)
		{
			str += "          agentTypes :  name = "+agentTypeNames[i]+"\n";
		}

		String[] societyTypeNames = getSocietyTypeNames();
		for (int i=0; i < societyTypeNames.length; i++)
		{
			str += "          societyTypes :  name = "+societyTypeNames[i]+"\n";
		}

		str += "          default-societyInstance: name = "+defaultSocietyInstance+"\n";

		ISocietyInstance[] societyInstances = getSocietyInstances();
		for (int i=0; i < societyInstances.length; i++)
		{
			str += "          societyInstances :  name = "+societyInstances[i].getName()+"\n";
		}

		return str;
	}
}