/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package jade.core.persistence;

//#MIDP_EXCLUDE_FILE


import jade.core.ServiceHelper;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.IMTPException;
import jade.core.ServiceException;
import jade.core.NotFoundException;
import jade.core.NameClashException;



/**

   The vertical interface for the JADE kernel-level service managing
   saving and retrieving agents and containers to persistent storage.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
   @author Giovanni Caire - TILAB
 */
public interface PersistenceHelper extends ServiceHelper {


	/**
       The name of this service.
	 */
	public static final String NAME = "jade.core.persistence.Persistence";

	/**
       The name of a persistence-specific profile parameter, stating
       which repository to load the current container from.
	 */
	public static final String LOAD_FROM = "load-from";

	/**
       The name of a persistence-specific profile parameter, stating
       the URL from where the properties for the JADE meta-database
       can be retrieved.
	 */
	public static final String META_DB = "meta-db";


	/**
       This command name represents the <code>get-nodes</code> action.
	 */
	static final String GET_NODES = "Get-Nodes";

	/**
       This command name represents the <code>get-repositories</code>
       action.
	 */
	static final String GET_REPOSITORIES = "Get-Repositories";

	/**
       This command name represents the <code>get-saved-agents</code>
       action.
	 */
	static final String GET_SAVED_AGENTS = "Get-Saved-Agents";

	/**
       This command name represents the <code>get-frozen-agents</code>
       action.
	 */
	static final String GET_FROZEN_AGENTS = "Get-Frozen-Agents";

	/**
       This command name represents the
       <code>get-saved-containers</code> action.
	 */
	static final String GET_SAVED_CONTAINERS = "Get-Saved-Containers";

	/**
       This command name represents the <code>save-agent</code>
       action, requested by an entity external from the agent to be
       saved.
	 */
	static final String SAVE_AGENT = "Save-Agent";

	/**
       This command name represents the <code>load-agent</code>
       action, requested by an entity external from the agent to be
       loaded.
	 */
	static final String LOAD_AGENT = "Load-Agent";

	/**
       This command name represents the <code>reload-agent</code>
       action, requested by an entity external from the agent to be
       reloaded.
	 */
	static final String RELOAD_AGENT = "Reload-Agent";

	/**
       This command name represents the <code>delete-agent</code>
       action, requested by an entity external from the agent to be
       loaded.
	 */
	static final String DELETE_AGENT = "Delete-Agent";

	/**
       This command name represents the <code>freeze-agent</code>
       action, requested by an entity external from the agent to be
       frozen.
	 */
	static final String FREEZE_AGENT = "Freeze-Agent";

	/**
       This command name represents the <code>thaw-agent</code>
       action.
	 */
	static final String THAW_AGENT = "Thaw-Agent";

	/**
       This command name represents the <code>save-myself</code>
       action, requested by the agent that is to be saved.
	 */
	static final String SAVE_MYSELF = "Save-Myself";

	/**
       This command name represents the <code>reload-myself</code>
       action, requested by the agent that wants to restore its state
       to a previously saved one.
	 */
	static final String RELOAD_MYSELF = "Reload-Myself";

	/**
       This commmand name represents the <code>freeze-myself</code>
       action, requested by the agent that wants to be frozen in a
       persistent store.
	 */
	static final String FREEZE_MYSELF = "Freeze-Myself";

	/**
       This command name represents the <code>save-agent-group</code>
       action.
	 */
	static final String SAVE_AGENT_GROUP = "Save-Agent-Group";

	/**
       This command name represents the
       <code>delete-agent-group</code> action, requested by an entity
       external from the agent group to be deleted.
	 */
	static final String DELETE_AGENT_GROUP = "Delete-Agent-Group";

	/**
       This command name represents the <code>load-agent-group</code>
       action.
	 */
	static final String LOAD_AGENT_GROUP = "Load-Agent-Group";


	/**
       This command name represents the <code>save-container</code>
       action.
	 */
	static final String SAVE_CONTAINER = "Save-Container";

	/**
       This command name represents the <code>load-container</code>
       action.
	 */
	static final String LOAD_CONTAINER = "Load-Container";

	/**
       This command name represents the <code>delete-container</code>
       action.
	 */
	static final String DELETE_CONTAINER = "Delete-Container";


	String[] getNodes() throws ServiceException, IMTPException;
	String[] getRepositories(String nodeName) throws ServiceException, IMTPException, NotFoundException;
	String[] getSavedAgents(String nodeName, String repository) throws ServiceException, IMTPException, NotFoundException;
	String[] getFrozenAgents(String nodeName, String repository) throws ServiceException, IMTPException, NotFoundException;
	String[] getSavedContainers(String nodeName, String repository) throws ServiceException, IMTPException, NotFoundException;

	/**
       Register a <code>Savable</code> object whose methods will
       be called-back each time the agent associated to this 
       <code>PersistenceHelper</code> is involved in some persistence
       related operations (e.g. save, freeze ....)
	 */
	void registerSavable(Savable s);

	/**
	   Make the agent associated to this <code>PersistenceHelper</code>
	   be saved on the indicated repository. 
	   It should be noted that this method just changes the agent 
	   state to <code>AP_SAVING</code>. The actual save operation 
	   takes place asynchronously.
	 */
	void save(String repository);

	/**
	   @deprecated Use save() instead
	 */
	void saveMyself(AID agentID, String repository) throws ServiceException, NotFoundException, IMTPException;

	/**
       Make the agent associated to this <code>PersistenceHelper</code>
       be reloaded from the indicated repository.  
		   It should be noted that this method just changes the agent 
		   state to <code>AP_RELOADING</code>. The actual reload operation 
		   takes place asynchronously.
	 */
	void reload(String repository);

	/**
       @deprecated Use reload() instead
	 */
	void reloadMyself(AID agentID, String repository) throws ServiceException, IMTPException, NotFoundException;

	/**
       Make the agent associated to this <code>PersistenceHelper</code>
       be freezed on the indicated repository.  
		   It should be noted that this method just changes the agent 
		   state to <code>AP_FROZEN</code>. The actual freeze operation 
		   takes place asynchronously.
	 */
	void freeze(String repository, ContainerID bufferContainer);

	/**
       @deprecated Use freeze() instead
	 */
	void freezeMyself(AID agentID, String repository, ContainerID bufferContainer) throws ServiceException, IMTPException, NotFoundException;
}
