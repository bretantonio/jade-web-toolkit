package jade.tools.ascml.model.jibx;

import jade.tools.ascml.exceptions.ModelException;
import jade.tools.ascml.events.ModelChangedEvent;
import jade.tools.ascml.events.ModelChangedListener;
import jade.tools.ascml.repository.loader.ImageIconLoader;
import jade.tools.ascml.model.Document;
import jade.tools.ascml.absmodel.*;

import javax.swing.*;
import java.util.Vector;
import java.util.ArrayList;

public class AgentType implements IAgentType
{

	/** The agent's type name (not fully qualified) */
	protected String name = NAME_UNKNOWN;;

    /** The agent's package-name (not fully qualified) */
	protected String packageName = NAME_UNKNOWN;;

	/** The agent class name. */
	protected String className = CLASS_UNKNOWN;

	/** The platform-identifier (e.g. jade). */
	protected String platformType = "";

	/** The agent type's description. */
	protected String description = "";

	/** The path and name of the icon used to represent this agent in the repository-tree */
	protected String iconName = "";

	protected ArrayList<IParameter> parameterList = new ArrayList<IParameter>();

	protected ArrayList<IParameterSet> parameterSetList = new ArrayList<IParameterSet>();

	protected ArrayList<IAgentDescription> agentDescriptionList = new ArrayList<IAgentDescription>();

	protected ArrayList<IServiceDescription> serviceDescriptionList = new ArrayList<IServiceDescription>();

	/** The document. */
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

	/**
	 * The modelChangedListener are informed when some changes in the
	 * model-object are made
	 */
    protected Vector modelChangedListener;

	// -----------------------------------------------------------------------------

	/**
	 *  Set the name of this AgentType.
	 *  @param name  AgentType's name.
	 */
	public void setName(String name)
	{
		if ((name == null) || (name.trim().equals("")))
			name = NAME_UNKNOWN;
		if (name.equals(getName()))
			return;

		this.name = name.trim();
		throwModelChangedEvent(ModelChangedEvent.NAME_CHANGED);
	}

	/**
	 *  Get the name of this AgentType.
	 *  @return  AgentType's name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Set the package-name of this AgentType
	 * @param packageName  The packageName as String (e.g. examples.party)
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
	 * Get the package-name of this AgentType
	 * @return packageName as String (e.g. examples.party)
	 */
	public String getPackageName()
	{
		return packageName;
	}

	/**
	 *  Returns the fully qualified AgentType-name. The name is composed of
	 *  the package-name and the AgentType-name, for example my.packageName.AgentTypeName
	 *  would be a correct 'fully qualified' AgentTypeName.
	 *  @return  fully qualified name of the AgentType.
	 */
	public String getFullyQualifiedName()
	{
		if ((getPackageName() == null) || getPackageName().equals(""))
			return name;
		else
			return getPackageName()+"."+getName();
	}

	/**
	 * Set the type of agent-platform (e.g. jade) this AgentType is designed to run on.
	 * @param platformType  The platformType as String, possible values may be
	 * PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	public void setPlatformType(String platformType)
	{
		if(platformType == null)
			platformType = "";
		if (platformType.equals(getPlatformType()))
			return;

		this.platformType = platformType.trim();
		throwModelChangedEvent(ModelChangedEvent.PLATFORM_CHANGED);
	}

	/**
	 * Get the type of agent-platform (e.g. jade) this AgentType is designed to run on.
	 * return The platformType as String, possible values may be PLATFORM_TYPE_JADE, PLATFORM_TYPE_JADEX
	 */
	public String getPlatformType()
	{
		return platformType;
	}

	/**
	 *  Set the classname of this AgentType.
	 *  @param className  AgentType's classname.
	 */
	public void setClassName(String className)
	{
		if ((className == null) || className.equals(""))
			className = CLASS_UNKNOWN;
		if (className.equals(getClassName()))
			return;

		this.className = className.trim();
		throwModelChangedEvent(ModelChangedEvent.CLASS_CHANGED);
	}

	/**
	 *  Get the classname of this AgentType.
	 *  @return AgentType's classname.
	 */
	public String getClassName()
	{
		return this.className;
	}

	/**
	 *  Set the document.
	 *  @param document  The document.
	 */
	public void setDocument(IDocument document)
	{
		this.document = document;
	}

	/**
	 *  Get the document, which contains the AgentType's source-path.
	 *  @return document  The document.
	 */
	public IDocument getDocument()
	{
		return this.document;
	}

	/**
	 *  Set the path+name of the AgentType's icon-name. Using the getIcon-method
	 *  an ImageIcon is constructed out of this name.
	 *  @param iconName  The name of the icon.
	 */
	public void setIconName(String iconName)
	{
		if (iconName == null)
			iconName = ImageIconLoader.AGENTTYPE;
		if (iconName.equals(getIconName()))
			return;

		this.iconName = iconName;
		throwModelChangedEvent(ModelChangedEvent.ICON_CHANGED);
	}

	/**
	 *  Get the name of the AgentType's icon.
	 *  @return The image-icon.
	 */
	public String getIconName()
	{
		if (iconName != null)
			return iconName;
		else
			return ImageIconLoader.AGENTTYPE; // get default-iconName
	}

	/**
	 *  Get the ImageIcon for this AgentType.
	 *  @return The ImageIcon for this AgentType.
	 */
	public ImageIcon getIcon()
	{
		if ((iconName != null) && !iconName.equals(""))
			return ImageIconLoader.createImageIcon(iconName);
		else
			return ImageIconLoader.createImageIcon(ImageIconLoader.AGENTTYPE); // get default-icon
	}

	/**
	 *  Set the description of this AgentType.
	 *  @param description  AgentType's description.
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
	 *  Get the agent's description.
	 *  @return  agent's description.
	 */
	public String getDescription()
	{
		return description;
	}

	// -------------------------------------------------------------------------------------

	/**
	 * Set the status of this model. The status indicates, whether loading was successful or not.
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
	 * Get the status of this model. The status indicates, whether loading was successful or not.
	 * @return The status of this model, possible values are STATUS_OK, STATUS_ERROR, STATUS_REFERENCE_ERROR.
	 */
	public String getStatus()
	{
		return this.status;
	}

	/**
	 * Get the integrity-status, which describes errors or warnings in detail.
	 * The ModelIntegrityChecker may set the integrity-status upon checking the integrity.
	 * @return  A ModelException containing detailed integrity-messages as Strings.
	 */
	public ModelException getIntegrityStatus()
	{
		if (statusException.hasExceptionDetails() || statusException.hasNestedExceptions())
			return statusException;
		else
			return null;
	}

	/**
	 * Set the integrity-status, which describes errors or warnings in detail.
	 * The ModelIntegrityChecker may set the integrity-status upon checking the integrity.
	 * @param statusException  A ModelException containing detailed integrity-messages as Strings.
	 */
	public void setIntegrityStatus(ModelException statusException)
	{
		this.statusException = statusException;
	}
	
	/**
	 *  Get all AgentDescriptions.
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @return  The AgentType's AgentDescriptionModels as an array.
	 */
	public IAgentDescription[] getAgentDescriptions()
	{
		AgentDescription[] returnArray = new AgentDescription[agentDescriptionList.size()];
		agentDescriptionList.toArray(returnArray);
		return returnArray;
	}

	/**
	 *  Add an AgentDescription for this AgentType.
	 *  There are two ways of describing an agent, the get-/setDescription-methods
	 *  deal with a 'human-readable' description, the get-/setAgentDescription-methods
	 *  on the other hand deal with the FIPA-agentdescription, which is more technical
	 *  and used for registering and searching for agents at the directory facilitator for example.
	 *  @param description  the AgentType's AgentDescriptionModel.
	 */
	public void addAgentDescription(IAgentDescription description)
	{
		this.agentDescriptionList.add(description);
	}

	/**
	 *  Remove an AgentDescription from this AgentType.
	 *  @param description  The AgentType's AgentDescription to remove.
	 */
	public void removeAgentDescription(IAgentDescription description)
	{
		this.agentDescriptionList.remove(description);
	}

	//--------------------------------------------------------------------------------------

	/**
	 * Add a parameter to this AgentType.
	 * @param parameter  The ParameterSet-object to add.
	 */
	public void addParameter(IParameter parameter)
	{
		parameterList.add(parameter);
	}

	/**
	 * Remove a parameter from this agent.
	 * @param name  The name of the parameter to remove.
	 */
	public void removeParameter(String name)
	{
		for (int i=0; i < parameterList.size(); i++)
		{
			if (parameterList.get(i).getName().equals(name))
			{
				parameterList.remove(i);
				return;
			}
		}
	}

	/**
	 * Remove a parameter from this agent.
	 * @param parameter  The Parameter-object to remove.
	 */
	public void removeParameter(IParameter parameter)
	{
		parameterList.remove(parameter);
	}

	/**
	 * Get an AgentType's parameter.
	 * @param name  The name of the Parameter
	 * @return  An agent's parameter or null if no parameter with the given name exists.
	 */
	public IParameter getParameter(String name)
	{
		for (int i=0; i < parameterList.size(); i++)
		{
			if (parameterList.get(i).getName().equals(name))
			{
				return parameterList.get(i);
			}
		}
		return null;
	}

	/**
	 * Get all of the AgentType's parameter.
	 * @return  All of the agent's parameters as Parameter-array.
	 */
	public IParameter[] getParameters()
	{
		Parameter[] returnArray = new Parameter[parameterList.size()];
		parameterList.toArray(returnArray);
		return returnArray;
	}

	/**
	 * Add a parameterSet to this AgentType.
	 * @param parameterSet  The ParameterSet-object to add.
	 */
	public void addParameterSet(IParameterSet parameterSet)
	{
		parameterSetList.add(parameterSet);
	}

	/**
	 * Remove a parameterSet from this agent.
	 * @param name  The name of the parameterSet to remove.
	 */
	public void removeParameterSet(String name)
	{
		for (int i=0; i < parameterSetList.size(); i++)
		{
			if (parameterSetList.get(i).getName().equals(name))
			{
				parameterSetList.remove(i);
				return;
			}
		}
	}

	/**
	 * Remove a parameterSet from this agent.
	 * @param parameter  The name of the ParameterSet to remove.
	 */
	public void removeParameterSet(IParameter parameter)
	{
		parameterSetList.remove(parameter);
	}

	/**
	 * Get an AgentType's parameterSet.
	 * @param name  The name of the ParameterSet
	 * @return  An agent's parameterSet or null if no parameter with the given name exists.
	 */
	public IParameterSet getParameterSet(String name)
	{
		for (int i=0; i < parameterSetList.size(); i++)
		{
			if (parameterSetList.get(i).getName().equals(name))
			{
				return parameterSetList.get(i);
			}
		}
		return null;
	}

	/**
	 * Get all of the AgentType's parameterSets.
	 * @return  All of the agent's parameterSets as ParameterSet-array.
	 */
	public IParameterSet[] getParameterSets()
	{
		ParameterSet[] returnArray = new ParameterSet[parameterSetList.size()];
		parameterSetList.toArray(returnArray);
		return returnArray;
	}

	// -------------------------------------------------------------------------------------------

	/**
	 * Get all the ModelChangedListener.
	 * @return  A Vector containing ModelChangedListener
	 */
	public Vector getModelChangedListener()
	{
		return modelChangedListener;
	}

	/**
	 * Set the ModelChangedListener. These are informed in case of changes in this model.
	 * @param modelChangedListener  A Vector containing ModelChangedListener
	 */
	public void setModelChangedListener(Vector modelChangedListener)
	{
		this.modelChangedListener = modelChangedListener;
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 */
	public void throwModelChangedEvent(String eventCode)
	{
		throwModelChangedEvent(eventCode, null);
	}

	/**
	 * Throw a ModelChangedEvent notifying all the listeners that this model has been changed
	 * @param eventCode  The eventCode for the event (see ModelChangedEvent for possible codes)
	 * @param userObject  This object is stored in the ModelChangedEvent. For example: When runnables
	 * are added or removed, these RunnableModels are stored as userObjects to access them later on.
	 */
	public void throwModelChangedEvent(String eventCode, Object userObject)
	{
		ModelChangedEvent event = new ModelChangedEvent(this, eventCode, userObject);

		ModelChangedListener[] listener = new ModelChangedListener[modelChangedListener.size()];
		modelChangedListener.toArray(listener);

		for (int i=0; i < listener.length; i++)
		{
			listener[i].modelChanged(event);
		}
	}

	// ------------------------------------------------------------------------------

	/**
	 *  This method returns a short String with the AgentType-name.
	 *  It is used by the RepositoryTree for example, to name the nodes.
	 *  @return  String containing the name of this AgentType
	 */
	public String toString()
	{
		String str = getName();
		// if (!getDocument().isSaved())
		//	str += "*";

		return str; // + " " + super.toString();
	}

	/**
	 *  This method returns a formatted String showing the AgentType-model.
	 *  @return  formatted String showing ALL information about this AgentType.
	 */
	public String toFormattedString()
	{
		String str = "";

		/*str += "  Agent-Type : name = " + getName() + "\n";
		str += "     package = " + getDocument().getPackageName() + "\n";
		str += "     class = " + className + "\n";
		str += "     desc = " + description + "\n";

		Iterator keys = parameters.keySet().iterator();
		while (keys.hasNext())
		{
			String oneKey = (String)keys.next();
			HashMap optionMap = (HashMap)parameters.get(oneKey);

			str +=  "   parameter : name = " + optionMap.get("name")        + "\n";
			str +=  "     type = " + optionMap.get("type")        + "\n";
			str +=  "     optional = " + optionMap.get("optional")    + "\n";
			str +=  "     description = " + optionMap.get("description") + "\n";
			str +=  "     value = " + optionMap.get("value")       + "\n";
			str +=  "     constraints = " + optionMap.get("constraints") + "\n";

		}

		keys = parameterSets.keySet().iterator();
		while (keys.hasNext())
		{
			String oneKey   = (String)keys.next();
			HashMap optionMap = (HashMap)parameterSets.get(oneKey);

			str +=  "   param-set : name = " + optionMap.get("name")        + "\n";
			str +=  "     type = " + optionMap.get("type")        + "\n";
			str +=  "     optional = " + optionMap.get("optional")    + "\n";
			str +=  "     description = " + optionMap.get("description") + "\n";
			str +=  "     value = " ;

			Iterator oneSet = ((Collection)optionMap.get("value")).iterator();
			str += "[";
			while (oneSet.hasNext())
			{
				str += oneSet.next() + ",";
			}
			str += "]\n";

			str +=  "     constraints = ";
			oneSet = ((Collection)optionMap.get("constraints")).iterator();
			str += "[";
			while (oneSet.hasNext())
			{
				str += oneSet.next() + ",";
			}
			str += "]\n";
		} */
		return str;
	}
}
