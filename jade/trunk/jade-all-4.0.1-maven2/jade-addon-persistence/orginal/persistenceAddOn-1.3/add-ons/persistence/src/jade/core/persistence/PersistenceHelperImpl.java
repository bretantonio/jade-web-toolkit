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


import jade.core.Command;
import jade.core.HorizontalCommand;
import jade.core.VerticalCommand;
import jade.core.GenericCommand;
import jade.core.Service;
import jade.core.ServiceException;

import jade.core.Agent;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.Location;

import jade.core.IMTPException;
import jade.core.NameClashException;
import jade.core.NotFoundException;

import jade.security.JADESecurityException;


/**
   The JADE service to manage saving and retrieving agents and
   containers to persistent storage.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
   @author Giovanni Caire - TILAB
 */
class PersistenceHelperImpl implements PersistenceHelper {
	private Agent myAgent;
	private Savable mySavable;
	private PersistenceService myService;
	
	PersistenceHelperImpl(PersistenceService s) {
		myService = s;
	}
	
	public void init(Agent a) {
		myAgent = a;
	}
	
	public void registerSavable(Savable s) {
		mySavable = s;
	}

	public String[] getNodes() throws ServiceException, IMTPException {

	    GenericCommand cmd = new GenericCommand(PersistenceHelper.GET_NODES, PersistenceHelper.NAME, null);
	    Object result = myService.submit(cmd);
	    if((result != null) && (result instanceof Throwable)) {

		if(result instanceof ServiceException) {
		    throw (ServiceException)result;
		}
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
	    }

	    return (String[])result;
	}

	public String[] getRepositories(String nodeName) throws ServiceException, IMTPException, NotFoundException  {

	    GenericCommand cmd = new GenericCommand(PersistenceHelper.GET_REPOSITORIES, PersistenceHelper.NAME, null);
	    cmd.addParam(nodeName);
	    Object result = myService.submit(cmd);
	    if((result != null) && (result instanceof Throwable)) {

		if(result instanceof ServiceException) {
		    throw (ServiceException)result;
		}
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}

	    }

	    return (String[])result;
	}

	public String[] getSavedAgents(String nodeName, String repository) throws ServiceException, IMTPException, NotFoundException  {
	    GenericCommand cmd = new GenericCommand(PersistenceHelper.GET_SAVED_AGENTS, PersistenceHelper.NAME, null);
	    cmd.addParam(nodeName);
	    cmd.addParam(repository);
	    Object result = myService.submit(cmd);
	    if((result != null) && (result instanceof Throwable)) {

		if(result instanceof ServiceException) {
		    throw (ServiceException)result;
		}
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}

	    }

	    return (String[])result;
	}

	public String[] getFrozenAgents(String nodeName, String repository) throws ServiceException, IMTPException, NotFoundException {
	    GenericCommand cmd = new GenericCommand(PersistenceHelper.GET_FROZEN_AGENTS, PersistenceHelper.NAME, null);
	    cmd.addParam(nodeName);
	    cmd.addParam(repository);
	    Object result = myService.submit(cmd);
	    if((result != null) && (result instanceof Throwable)) {

		if(result instanceof ServiceException) {
		    throw (ServiceException)result;
		}
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}

	    }

	    return (String[])result;
	}

	public String[] getSavedContainers(String nodeName, String repository) throws ServiceException, IMTPException, NotFoundException {
	    GenericCommand cmd = new GenericCommand(PersistenceHelper.GET_SAVED_CONTAINERS, PersistenceHelper.NAME, null);
	    cmd.addParam(nodeName);
	    cmd.addParam(repository);
	    Object result = myService.submit(cmd);
	    if((result != null) && (result instanceof Throwable)) {

		if(result instanceof ServiceException) {
		    throw (ServiceException)result;
		}
		if(result instanceof IMTPException) {
		    throw (IMTPException)result;
		}
		if(result instanceof NotFoundException) {
		    throw (NotFoundException)result;
		}

	    }

	    return (String[])result;
	}

	public void save(String repository) {
		if (repository == null) {
			repository = PersistenceManager.DEFAULT_REPOSITORY;
		}
		// bufferContainer already defaults to the MainContainer
		myAgent.changeStateTo(myService.createSavingLC(repository, mySavable));
	}

	// For backward compatibility
	/**
	   @deprecated
	 */
	public void saveMyself(AID agentID, String repository) throws ServiceException, NotFoundException, IMTPException {
		if (myAgent.getAID().equals(agentID)) {
			save(repository);
		}
	}

  public void reload(String repository) {
		if (repository == null) {
			repository = PersistenceManager.DEFAULT_REPOSITORY;
		}
		// bufferContainer already defaults to the MainContainer
		myAgent.changeStateTo(myService.createReloadingLC(repository, mySavable));
  }
  
	// For backward compatibility
	/**
	   @deprecated
	 */
	public void reloadMyself(AID agentID, String repository) throws ServiceException, IMTPException, NotFoundException {
		if (myAgent.getAID().equals(agentID)) {
			reload(repository);
		}
	}

	public void freeze(String repository, ContainerID bufferContainer) {
		if (repository == null) {
			repository = PersistenceManager.DEFAULT_REPOSITORY;
		}
		// bufferContainer already defaults to the MainContainer
		myAgent.changeStateTo(myService.createFrozenLC(repository, bufferContainer, mySavable));
	}

	// For backward compatibility
	/**
	   @deprecated
	 */
  public void freezeMyself(AID agentID, String repository, ContainerID bufferContainer) throws ServiceException, IMTPException, NotFoundException {
  	if (myAgent.getAID().equals(agentID)) {
  		freeze(repository, bufferContainer);
  	}
  }

  
  ////////////////////////////////////////////////////////
  // The following methods are only available to the AMS
  ////////////////////////////////////////////////////////
	void saveAgent(AID agentID, String repository) throws ServiceException, NotFoundException, IMTPException {

	    GenericCommand cmd = new GenericCommand(PersistenceHelper.SAVE_AGENT, PersistenceHelper.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
	    }
	}

	void loadAgent(AID agentID, String repository, ContainerID where) throws ServiceException, IMTPException, NotFoundException, NameClashException {

	    GenericCommand cmd = new GenericCommand(PersistenceHelper.LOAD_AGENT, PersistenceHelper.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);
	    cmd.addParam(where);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
		if(lastException instanceof NameClashException) {
		    throw (NameClashException)lastException;
		}
	    }
	}

	void reloadAgent(AID agentID, String repository) throws ServiceException, IMTPException, NotFoundException {
	    GenericCommand cmd = new GenericCommand(PersistenceHelper.RELOAD_AGENT, PersistenceHelper.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
	    }
	}

	void deleteAgent(AID agentID, String repository, ContainerID where) throws ServiceException, IMTPException, NotFoundException {

	    GenericCommand cmd = new GenericCommand(PersistenceHelper.DELETE_AGENT, PersistenceHelper.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);
	    cmd.addParam(where);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
	    }
	}

	void freezeAgent(AID agentID, String repository, ContainerID bufferContainer) throws ServiceException, NotFoundException, IMTPException {

	    GenericCommand cmd = new GenericCommand(PersistenceHelper.FREEZE_AGENT, PersistenceHelper.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);
	    cmd.addParam(bufferContainer);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
	    }
	}

	void thawAgent(AID agentID, String repository, ContainerID newContainer) throws ServiceException, NotFoundException, IMTPException {

	    GenericCommand cmd = new GenericCommand(PersistenceHelper.THAW_AGENT, PersistenceHelper.NAME, null);
	    cmd.addParam(agentID);
	    cmd.addParam(repository);
	    cmd.addParam(newContainer);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
	    }
	}

	void saveContainer(ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException {
	    GenericCommand cmd = new GenericCommand(PersistenceHelper.SAVE_CONTAINER, PersistenceHelper.NAME, null);
	    cmd.addParam(cid);
	    cmd.addParam(repository);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
	    }
	}

	void loadContainer(ContainerID cid, String repository) throws ServiceException, IMTPException, NotFoundException, NameClashException {
	    GenericCommand cmd = new GenericCommand(PersistenceHelper.LOAD_CONTAINER, PersistenceHelper.NAME, null);
	    cmd.addParam(cid);
	    cmd.addParam(repository);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
		if(lastException instanceof NameClashException) {
		    throw (NameClashException)lastException;
		}
	    }
	}

	void deleteContainer(ContainerID cid, ContainerID where, String repository) throws ServiceException, IMTPException, NotFoundException {
	    GenericCommand cmd = new GenericCommand(PersistenceHelper.DELETE_CONTAINER, PersistenceHelper.NAME, null);
	    cmd.addParam(cid);
            cmd.addParam(where);
	    cmd.addParam(repository);
	    Object lastException = myService.submit(cmd);

	    if(lastException != null) {

		if(lastException instanceof ServiceException) {
		    throw (ServiceException)lastException;
		}
		if(lastException instanceof IMTPException) {
		    throw (IMTPException)lastException;
		}
		if(lastException instanceof NotFoundException) {
		    throw (NotFoundException)lastException;
		}
	    }
	}
}

