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


import jade.core.ServiceFinder;
import jade.core.Command;
import jade.core.HorizontalCommand;
import jade.core.VerticalCommand;
import jade.core.GenericCommand;
import jade.core.Service;
import jade.core.ServiceHelper;
import jade.core.BaseService;
import jade.core.ServiceException;
import jade.core.Sink;
import jade.core.Filter;
import jade.core.Node;

import jade.core.Profile;
import jade.core.LifeCycle;
import jade.core.Agent;
import jade.core.AID;
import jade.core.Agent.Interrupted;
import jade.core.CallbackInvokator;
import jade.core.CaseInsensitiveString;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.AgentContainer;
import jade.core.MainContainer;

import jade.core.ProfileException;
import jade.core.IMTPException;
import jade.core.NameClashException;
import jade.core.NotFoundException;
import jade.core.UnreachableException;
import jade.core.behaviours.Behaviour;
import jade.core.messaging.GenericMessage;

import jade.lang.acl.ACLMessage;
import jade.mtp.MTPDescriptor;
import jade.security.JADESecurityException;
import jade.security.CredentialsHelper;

import jade.util.leap.List;
import jade.util.leap.ArrayList;
import jade.util.leap.Map;
import jade.util.leap.HashMap;
import jade.util.leap.Iterator;
import jade.util.Logger;

import java.io.InterruptedIOException;

/**

   The JADE service to manage saving and retrieving agents and
   containers to persistent storage.

   @author Giovanni Rimassa - FRAMeTech s.r.l.

 */
public class PersistenceService extends BaseService {

	/**
     Represents the <code>saving</code> agent state. This is the state
     an agent goes into when its actual storage within a persistent
     data repository takes place.
	 */
	public static final int AP_SAVING = 10;

	/**
     Represents the <code>loading</code> agent state. This is the
     state an agent goes into when its current state is about to be
     replaced with a new one retrieved from a persistent data
     repository.
	 */
	public static final int AP_RELOADING = 11;

	/**
     Represents the <code>frozen</code> agent state. This is similar
     to the suspended state, but the agent is actually removed from
     its container and kept on a persistent store.
	 */
	public static final int AP_FROZEN = 12;


	private static class FrozenAgentsEntry {

		public FrozenAgentsEntry(AID id, String sn, String rep, Long pk) {
			agentID = id;
			sliceName = sn;
			repository = rep;
			queueID = pk;
		}

		public AID getAgentID() {
			return agentID;
		}

		public String getSliceName() {
			return sliceName;
		}

		public String getRepository() {
			return repository;
		}

		public Long getQueueID() {
			return queueID;
		}

		private AID agentID;
		private String sliceName;
		private String repository;
		private Long queueID;

	} // End of FrozenAgentsEntry class


	private static class SaveContainerOperation {

		public SaveContainerOperation(AID[] agentIDs, java.util.Set mtps) {
			myAgentIDs = agentIDs;
			myMTPs = mtps;
		}

		public AID[] getAgentIDs() {
			return myAgentIDs;
		}

		// This method is called by the agent threads that are saving
		// themselves.
		// When the last agent is added to the list, the master thread
		// (i.e. the one that started the save-container operation in
		// the first place) is restarted, so that it can perform the
		// save operation. After that, the master thread will wake up
		// all threads blocked in this method.
		public synchronized void addAgentToSave(Agent a) {
			myAgents.add(a);

			if(isComplete()) {
				synchronized(masterLock) {
					masterLock.notifyAll();
				}
			}

			try {
				while(!isFlushed()) {
					wait();
				}
			}
			catch(InterruptedException ie) {
				// Do nothing...
			}
		}

		public void waitUntilComplete() {
			synchronized(masterLock) {
				try {
					while(!isComplete()) {
						masterLock.wait();
					}
				}
				catch(InterruptedException ie) {
					// Do nothing
				}
			}
		}

		public synchronized void flushAgentsToSave() {
			flushed = true;
			notifyAll();
		}

		public boolean isComplete() {
			return myAgents.size() == myAgentIDs.length;
		}

		public boolean isFlushed() {
			return flushed;
		}


		private AID[] myAgentIDs;
		private java.util.Set myAgents = new java.util.HashSet();
		private java.util.Set myMTPs;
		private Object masterLock = new Object();
		private boolean flushed = false;

	} // End of SaveContainerOperation class


	private static final String[] OWNED_COMMANDS = new String[] {
		PersistenceHelper.GET_NODES,
		PersistenceHelper.GET_REPOSITORIES,
		PersistenceHelper.GET_SAVED_AGENTS,
		PersistenceHelper.GET_FROZEN_AGENTS,
		PersistenceHelper.GET_SAVED_CONTAINERS,
		PersistenceHelper.SAVE_AGENT,
		PersistenceHelper.LOAD_AGENT,
		PersistenceHelper.RELOAD_AGENT,
		PersistenceHelper.DELETE_AGENT,
		PersistenceHelper.FREEZE_AGENT,
		PersistenceHelper.THAW_AGENT,
		PersistenceHelper.SAVE_MYSELF,
		PersistenceHelper.RELOAD_MYSELF,
		PersistenceHelper.FREEZE_MYSELF,
		PersistenceHelper.SAVE_CONTAINER,
		PersistenceHelper.LOAD_CONTAINER,
		PersistenceHelper.DELETE_CONTAINER
	};

	public void init(AgentContainer ac, Profile p) throws ProfileException {
		super.init(ac, p);
		myContainer = ac;
		myServiceFinder = ac.getServiceFinder();
		amsHandler = new PersistenceManagementBehaviour();
	}

	public void boot(Profile p) throws ServiceException {

		try {
			String metaDB = p.getParameter(PersistenceHelper.META_DB, null);
			myPersistenceManager = new PersistenceManager(metaDB, myContainer.getID().getName());
		}
		catch(Exception e) {
			throw new ServiceException("Error in service manager startup", e);
		}

		// Reload this container from a repository if needed
		final String repository = p.getParameter(PersistenceHelper.LOAD_FROM, null);
		if(repository != null) {

			Thread loader = new Thread(new Runnable() {

				public void run() {
					try {
						defaultHelper.loadContainer(myContainer.getID(), repository);
					}
					catch(ServiceException se) {
						se.printStackTrace();
					}
					catch(IMTPException imtpe) {
						imtpe.printStackTrace();
					}
					catch(NotFoundException nfe) {
						nfe.printStackTrace();
					}
					catch(NameClashException nce) {
						nce.printStackTrace();
					}
				}
			}, "Persistent-Container-Loader");
			loader.start();
		}
	}

	public String getName() {
		return PersistenceSlice.NAME;
	}

	public Class getHorizontalInterface() {
		return PersistenceSlice.class;
	}

	public Service.Slice getLocalSlice() {
		return localSlice;
	}

	public ServiceHelper getHelper(Agent a) {
		return new PersistenceHelperImpl(this);
	}

	public Behaviour getAMSBehaviour() {
		return amsHandler;
	}

	public Filter getCommandFilter(boolean direction) {
		if(direction == Filter.OUTGOING) {
			return null;
		}
		else {
			return inFilter;
		}
	}

	public Sink getCommandSink(boolean side) {
		if(side == Sink.COMMAND_SOURCE) {
			return senderSink;
		}
		else {
			return receiverSink;
		}
	}

	public String[] getOwnedCommands() {
		return OWNED_COMMANDS;
	}

	// This inner class handles the messaging commands on the command
	// issuer side, turning them into horizontal commands and
	// forwarding them to remote slices when necessary.
	private class CommandSourceSink implements Sink {

		public void consume(VerticalCommand cmd) {
			try {
				String name = cmd.getName();
				if(name.equals(PersistenceHelper.GET_NODES)) {
					handleGetNodes(cmd);
				}
				else if(name.equals(PersistenceHelper.GET_REPOSITORIES)) {
					handleGetRepositories(cmd);
				}
				else if(name.equals(PersistenceHelper.GET_SAVED_AGENTS)) {
					handleGetSavedAgents(cmd);
				}
				else if(name.equals(PersistenceHelper.GET_FROZEN_AGENTS)) {
					handleGetFrozenAgents(cmd);
				}
				else if(name.equals(PersistenceHelper.GET_SAVED_CONTAINERS)) {
					handleGetSavedContainers(cmd);
				}
				else if(name.equals(PersistenceHelper.SAVE_AGENT)) {
					handleSaveAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.LOAD_AGENT)) {
					handleLoadAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.RELOAD_AGENT)) {
					handleReloadAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.DELETE_AGENT)) {
					handleDeleteAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.FREEZE_AGENT)) {
					handleFreezeAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.THAW_AGENT)) {
					handleThawAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.SAVE_MYSELF)) {
					handleSaveMyself(cmd);
				}
				else if(name.equals(PersistenceHelper.RELOAD_MYSELF)) {
					handleReloadMyself(cmd);
				}
				else if(name.equals(PersistenceHelper.FREEZE_MYSELF)) {
					handleFreezeMyself(cmd);
				}
				else if(name.equals(PersistenceHelper.SAVE_CONTAINER)) {
					handleSaveContainer(cmd);
				}
				else if(name.equals(PersistenceHelper.LOAD_CONTAINER)) {
					handleLoadContainer(cmd);
				}
				else if(name.equals(PersistenceHelper.DELETE_CONTAINER)) {
					handleDeleteContainer(cmd);
				}
			}
			catch(IMTPException imtpe) {
				cmd.setReturnValue(imtpe);
			}
			catch(JADESecurityException ae) {
				cmd.setReturnValue(ae);
			}
			catch(NotFoundException nfe) {
				cmd.setReturnValue(nfe);
			}
			catch(NameClashException nce) {
				cmd.setReturnValue(nce);
			}
			catch(ServiceException se) {
				cmd.setReturnValue(new IMTPException("Service error", se));
			}
		}


		// Vertical command handler methods

		private void handleGetNodes(VerticalCommand cmd) throws ServiceException, IMTPException, NotFoundException {
			Service.Slice[] slices = myServiceFinder.findAllSlices(PersistenceHelper.NAME);
			String[] result = new String[slices.length];
			for(int i = 0 ; i < result.length; i++) {
				result[i] = slices[i].getNode().getName();
			}

			cmd.setReturnValue(result);
		}

		private void handleGetRepositories(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			String nodeName = (String)params[0];

			PersistenceSlice targetSlice = (PersistenceSlice)getSlice(nodeName);
			try {
				cmd.setReturnValue(targetSlice.getRepositories());
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				targetSlice = (PersistenceSlice)getFreshSlice(nodeName);
				cmd.setReturnValue(targetSlice.getRepositories());
			}
		}

		private void handleGetSavedAgents(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			String nodeName = (String)params[0];
			String repository = (String)params[1];

			PersistenceSlice targetSlice = (PersistenceSlice)getSlice(nodeName);
			try {
				cmd.setReturnValue(targetSlice.getSavedAgents(repository));
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				targetSlice = (PersistenceSlice)getFreshSlice(nodeName);
				cmd.setReturnValue(targetSlice.getSavedAgents(repository));
			}
		}

		private void handleGetFrozenAgents(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			String nodeName = (String)params[0];
			String repository = (String)params[1];

			PersistenceSlice targetSlice = (PersistenceSlice)getSlice(nodeName);
			try {
				cmd.setReturnValue(targetSlice.getFrozenAgents(repository));
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				targetSlice = (PersistenceSlice)getFreshSlice(nodeName);
				cmd.setReturnValue(targetSlice.getFrozenAgents(repository));
			}
		}

		private void handleGetSavedContainers(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			String nodeName = (String)params[0];
			String repository = (String)params[1];

			PersistenceSlice targetSlice = (PersistenceSlice)getSlice(nodeName);
			try {
				cmd.setReturnValue(targetSlice.getSavedContainers(repository));
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				targetSlice = (PersistenceSlice)getFreshSlice(nodeName);
				cmd.setReturnValue(targetSlice.getSavedContainers(repository));
			}
		}

		private void handleSaveAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];

			if (myLogger.isLoggable(Logger.FINE)) {
				myLogger.log(Logger.FINE, "PersistenceService-SourceSink handling command SAVE_AGENT. Agent = "+agentID.getName()+". Repository = "+repository);
			}
			MainContainer impl = myContainer.getMain();
			if(impl != null) {
				// On a main container, one is able to save an agent
				// running on a different container
				ContainerID cid = impl.getContainerID(agentID);
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(cid.getName());
				try {
					targetSlice.saveAgent(agentID, repository);
				}
				catch(IMTPException imtpe) {
					// Try to get a newer slice and repeat...
					targetSlice = (PersistenceSlice)getFreshSlice(cid.getName());
					targetSlice.saveAgent(agentID, repository);
				}
			}
			else {
				// On a peripheral container, one can directly save
				// only an agent deployed on the local container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(localSlice.getNode().getName());
				targetSlice.saveAgent(agentID, repository);
			}
		}

		private void handleLoadAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException, NameClashException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];
			ContainerID where = (ContainerID)params[2];

			PersistenceSlice targetSlice = (PersistenceSlice)getSlice(where.getName());
			try {
				if(targetSlice != null) {
					targetSlice.loadAgent(agentID, repository);
				}
				else {
					throw new NotFoundException("Node <" + where.getName() + "> not found");
				}
			}
			catch(IMTPException imtpe) {
				// Try to get a newer slice and repeat...
				targetSlice = (PersistenceSlice)getFreshSlice(where.getName());
				targetSlice.loadAgent(agentID, repository);
			}
		}

		private void handleReloadAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];

			MainContainer impl = myContainer.getMain();
			if(impl != null) {
				// On a main container, one is able to reload an agent
				// running on a different container
				ContainerID cid = impl.getContainerID(agentID);
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(cid.getName());
				if (targetSlice != null) {
					try {
						targetSlice.reloadAgent(agentID, repository);
					}
					catch(IMTPException imtpe) {
						// Try to get a newer slice and repeat...
						targetSlice = (PersistenceSlice)getFreshSlice(cid.getName());
						targetSlice.reloadAgent(agentID, repository);
					}
				}
				else {
					throw new NotFoundException("Target container does not exist or does not support persistence");
				}				
			}
			else {
				// On a peripheral container, one can directly reload
				// only an agent deployed on the local container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(localSlice.getNode().getName());
				targetSlice.reloadAgent(agentID, repository);
			}
		}

		private void handleDeleteAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];
			ContainerID where = (ContainerID)params[2];

			PersistenceSlice targetSlice = (PersistenceSlice)getSlice(where.getName());
			if(targetSlice != null) {
				try {
					targetSlice.deleteAgent(agentID, repository);
				}
				catch(IMTPException imtpe) {
					// Try to get a newer slice and repeat...
					targetSlice = (PersistenceSlice)getFreshSlice(where.getName());
					targetSlice.deleteAgent(agentID, repository);
				}
			}
			else {
				throw new NotFoundException("Target container does not exist or does not support persistence");
			}				
		}

		private void handleFreezeAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];
			ContainerID bufferContainer = (ContainerID)params[2];

			MainContainer impl = myContainer.getMain();
			if(impl != null) {
				// On a main container, one is able to freeze an agent
				// running on a different container
				ContainerID cid = impl.getContainerID(agentID);
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(cid.getName());
				if (targetSlice != null) {
					try {
						targetSlice.freezeAgent(agentID, repository, bufferContainer);
					}
					catch(IMTPException imtpe) {
						// Try to get a newer slice and repeat...
						targetSlice = (PersistenceSlice)getFreshSlice(cid.getName());
						targetSlice.freezeAgent(agentID, repository, bufferContainer);
					}
				}
				else {
					throw new NotFoundException("Message-buffering container does not exist or does not support persistence");
				}				
			}
			else {
				// On a peripheral container, one can directly freeze
				// only an agent deployed on the local container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(localSlice.getNode().getName());
				targetSlice.freezeAgent(agentID, repository, bufferContainer);
			}
		}

		private void handleThawAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];
			ContainerID newContainer = (ContainerID)params[2];

			MainContainer impl = myContainer.getMain();
			if(impl != null) {
				// On a main container, one is able to thaw an agent
				// on a different container
				ContainerID cid = impl.getContainerID(agentID);
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(cid.getName());
				if (targetSlice != null) {
					try {
						targetSlice.thawAgent(agentID, repository, newContainer);
					}
					catch(IMTPException imtpe) {
						// Try to get a newer slice and repeat...
						targetSlice = (PersistenceSlice)getFreshSlice(cid.getName());
						targetSlice.thawAgent(agentID, repository, newContainer);
					}
				}
				else {
					throw new NotFoundException("Target container does not exist or does not support persistence");
				}
			}
			else {
				// On a peripheral container, one can directly thaw
				// only an agent deployed on the local container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(localSlice.getNode().getName());
				targetSlice.thawAgent(agentID, repository, newContainer);
			}
		}

		private void handleSaveMyself(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];

			if (myLogger.isLoggable(Logger.FINE)) {
				myLogger.log(Logger.FINE, "PersistenceService-SourceSink handling command SAVE_MYSELF. Agent = "+agentID.getName()+". Repository = "+repository);
			}
			// Use the Persistence Manager to save the agent
			try {
				List pendingMessages = new ArrayList();
				Agent target = myContainer.acquireLocalAgent(agentID);

				myContainer.fillListFromMessageQueue(pendingMessages, target);
				java.util.List pm = new java.util.ArrayList(pendingMessages.size());
				Iterator it = pendingMessages.iterator();
				while(it.hasNext()) {
					pm.add(it.next());
				}

				// Check whether this agent is being saved as a part of a save-container operation
				SaveContainerOperation op = retrieveSaveContainerOp(agentID);
				if(op != null) {
					myContainer.releaseLocalAgent(agentID);
					op.addAgentToSave(target);
				}
				else {

					// Direct save: use the Persistence Manager to save the agent
					if (myLogger.isLoggable(Logger.FINE)) {
						myLogger.log(Logger.FINE, "PersistenceService-SourceSink saving Agent "+agentID.getName()+" in Repository = "+repository);
					}
					myPersistenceManager.saveAgent(target, repository, pm);
					if (myLogger.isLoggable(Logger.FINE)) {
						myLogger.log(Logger.FINE, "PersistenceService-SourceSink done!");
					}
				}
			}
			finally {
				myContainer.releaseLocalAgent(agentID);
			}
		}

		private void handleReloadMyself(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException, JADESecurityException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];

			// Use the Persistence Manager to reload the agent and replace the old one in the LADT 
			Agent loaded = myPersistenceManager.loadAgent(agentID, repository);
			myContainer.addLocalAgent(agentID, loaded);

			// Start it
			myContainer.powerUpLocalAgent(agentID);
		}

		private void handleFreezeMyself(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];
			ContainerID bufferContainer = (ContainerID)params[2];

			// Use the Persistence Manager to freeze the agent
			try {
				List pendingMessages = new ArrayList();
				Agent target = myContainer.acquireLocalAgent(agentID);

				// Buffer messages on your Main Container by default
				String bufferSliceName = MAIN_SLICE;
				if(bufferContainer != null) {
					bufferSliceName = bufferContainer.getName();
				}

				// Save the newly frozen agent on the local persistent store
				myContainer.fillListFromMessageQueue(pendingMessages, target);
				java.util.List pm = new java.util.ArrayList(pendingMessages.size());
				Iterator it = pendingMessages.iterator();
				while(it.hasNext()) {
					ACLMessage msg = (ACLMessage)it.next();
					pm.add(msg);
				}

				// Use the Persistence Manager to freeze the agent and remove it from the LADT
				Long persistentID = myPersistenceManager.freezeAgent(target, repository, pm);

				// Activate message buffering for the frozen agent on the buffer container
				PersistenceSlice bufferSlice = (PersistenceSlice)getSlice(bufferSliceName);
				Long messageQueueFK;
				try {
					messageQueueFK = bufferSlice.setupFrozenAgent(agentID, persistentID, myContainer.getID(), repository);
				}
				catch(IMTPException imtpe) {
					// Try again with a newer slice
					bufferSlice = (PersistenceSlice)getFreshSlice(bufferSliceName);
					messageQueueFK = bufferSlice.setupFrozenAgent(agentID, persistentID, myContainer.getID(), repository);
				}
				myPersistenceManager.connectToMessageQueue(persistentID, messageQueueFK, repository);

				myContainer.removeLocalAgent(agentID);
			}
			finally {
				myContainer.releaseLocalAgent(agentID);
			}
		}

		private void handleSaveContainer(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			ContainerID cid = (ContainerID)params[0];
			String repository = (String)params[1];

			// Forward the request to save to the proper container
			MainContainer impl = myContainer.getMain();
			if(impl != null) {
				// On a main container, one is able to save any
				// container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(cid.getName());
				try {
					targetSlice.saveContainer(repository);
				}
				catch(IMTPException imtpe) {
					// Try to get a newer slice and repeat...
					targetSlice = (PersistenceSlice)getFreshSlice(cid.getName());
					targetSlice.saveContainer(repository);
				}
			}
			else {
				// On a peripheral container, one can only save the
				// local container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(localSlice.getNode().getName());
				targetSlice.saveContainer(repository);
			}
		}

		private void handleLoadContainer(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException, NameClashException {
			Object[] params = cmd.getParams();
			ContainerID cid = (ContainerID)params[0];
			String repository = (String)params[1];

			// Forward the request to save to the proper container
			MainContainer impl = myContainer.getMain();
			if(impl != null) {
				// On a main container, one is able to reload any
				// container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(cid.getName());
				try {
					targetSlice.loadContainer(repository);
				}
				catch(IMTPException imtpe) {
					// Try to get a newer slice and repeat...
					targetSlice = (PersistenceSlice)getFreshSlice(cid.getName());
					targetSlice.loadContainer(repository);
				}
			}
			else {
				// On a peripheral container, one can only reload the
				// local container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(localSlice.getNode().getName());
				targetSlice.loadContainer(repository);
			}
		}

		private void handleDeleteContainer(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			ContainerID cid = (ContainerID)params[0];
			ContainerID where = (ContainerID)params[1];
			String repository = (String)params[2];

			// Forward the request to delete to the proper container
			PersistenceSlice targetSlice = (PersistenceSlice)getSlice(where.getName());
			try {
				if(targetSlice != null) {
					targetSlice.deleteContainer(cid, repository);
				}
				else {
					throw new NotFoundException("Node <" + where.getName() + "> not found");
				}
			}
			catch(IMTPException imtpe) {
				// Try to get a newer slice and repeat...
				targetSlice = (PersistenceSlice)getFreshSlice(where.getName());
				targetSlice.deleteContainer(cid, repository);
			}

		}

	} // End of CommandSourceSink class


	// This inner class handles the messaging commands on the command
	// issuer side, turning them into horizontal commands and
	// forwarding them to remote slices when necessary.
	private class CommandTargetSink implements Sink {

		public void consume(VerticalCommand cmd) {

			try {
				String name = cmd.getName();
				if(name.equals(PersistenceHelper.SAVE_AGENT)) {
					handleSaveAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.LOAD_AGENT)) {
					handleLoadAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.RELOAD_AGENT)) {
					handleReloadAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.DELETE_AGENT)) {
					handleDeleteAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.FREEZE_AGENT)) {
					handleFreezeAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.THAW_AGENT)) {
					handleThawAgent(cmd);
				}
				else if(name.equals(PersistenceHelper.SAVE_CONTAINER)) {
					handleSaveContainer(cmd);
				}
				else if(name.equals(PersistenceHelper.LOAD_CONTAINER)) {
					handleLoadContainer(cmd);
				}
				else if(name.equals(PersistenceHelper.DELETE_CONTAINER)) {
					handleDeleteContainer(cmd);
				}
			}
			catch(ServiceException se) {
				cmd.setReturnValue(se);
			}
			catch(IMTPException imtpe) {
				cmd.setReturnValue(imtpe);
			}
			catch(NotFoundException nfe) {
				cmd.setReturnValue(nfe);
			}
			catch(NameClashException nce) {
				cmd.setReturnValue(nce);
			}
			catch(JADESecurityException ae) {
				cmd.setReturnValue(ae);
			}
		}

		private void handleSaveAgent(VerticalCommand cmd) throws IMTPException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];

			if (myLogger.isLoggable(Logger.FINE)) {
				myLogger.log(Logger.FINE, "PersistenceService-TargetSink handling command SAVE_AGENT. Agent = "+agentID.getName()+". Repository = "+repository);
			}
			try {
				Agent target = myContainer.acquireLocalAgent(agentID);
				if(target != null) {
					try {
						PersistenceHelper helper = (PersistenceHelper) target.getHelper(PersistenceHelper.NAME);
						helper.save(repository);
					}
					catch (ServiceException se) {
						// Should never happen
						se.printStackTrace();
					}
				}
				else {
					throw new NotFoundException("Agent " + agentID.getLocalName() + " not found during save-agent");
				}
			}
			finally {
				myContainer.releaseLocalAgent(agentID);
			}

		}

		private void handleLoadAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException, NameClashException, JADESecurityException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];

			// Check if the agent already exists
			Agent instance = myContainer.acquireLocalAgent(agentID);
			try {
				if(instance != null) {
					throw new NameClashException("An agent named <" + agentID.getName() + "> is already active on the platform");
				}
				else {
					instance = myPersistenceManager.loadAgent(agentID, repository);
					// FIXME: what Principal and credentials should we use?
					myContainer.initAgent(agentID, instance, null, null);
					myContainer.powerUpLocalAgent(agentID);
				}
			}
			finally {
				myContainer.releaseLocalAgent(agentID);
			}
		}

		private void handleReloadAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];

			try {
				Agent target = myContainer.acquireLocalAgent(agentID);
				if(target != null) {
					try {
						PersistenceHelper helper = (PersistenceHelper) target.getHelper(PersistenceHelper.NAME);
						helper.reload(repository);
					}
					catch (ServiceException se) {
						// Should never happen
						se.printStackTrace();
					}
				}
				else {
					throw new NotFoundException("Agent " + agentID.getLocalName() + " not found during reload-agent");
				}
			}
			finally {
				myContainer.releaseLocalAgent(agentID);
			}
		}

		private void handleDeleteAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];

			// Use the persistence manager to delete the agent
			myPersistenceManager.deleteAgent(agentID, repository);
		}

		private void handleFreezeAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];
			ContainerID bufferContainer = (ContainerID)params[2];

			try {
				Agent target = myContainer.acquireLocalAgent(agentID);
				if(target != null) {
					try {
						PersistenceHelper helper = (PersistenceHelper) target.getHelper(PersistenceHelper.NAME);
						helper.freeze(repository, bufferContainer);
					}
					catch (ServiceException se) {
						// Should never happen
						se.printStackTrace();
					}
				}
				else {
					throw new NotFoundException("Agent " + agentID.getLocalName() + " not found during freeze-agent");
				}
			}
			finally {
				myContainer.releaseLocalAgent(agentID);
			}
		}

		private void handleThawAgent(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException, NameClashException, JADESecurityException {
			Object[] params = cmd.getParams();
			AID agentID = (AID)params[0];
			String repository = (String)params[1];
			ContainerID newContainer = (ContainerID)params[2];

			if(newContainer == null) {
				newContainer = myContainer.getID();
			}

			// FIXME: Need for synchronization with SEND_MESSAGE filter...
			FrozenAgentsEntry e = (FrozenAgentsEntry)frozenAgents.remove(agentID);
			Long messageQueueID = e.getQueueID();

			if(messageQueueID != null) {
				// Retrieve the frozen message queue
				List bufferedMessages = new ArrayList();
				Long agentFK = myPersistenceManager.readFrozenMessageQueue(messageQueueID, repository, bufferedMessages);
				// Activate message buffering for the frozen agent on the buffer container
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(newContainer.getName());
				if (targetSlice != null) {
					try {
						try {
							targetSlice.setupThawedAgent(agentID, agentFK, newContainer, repository, bufferedMessages);
						}
						catch(IMTPException imtpe) {
							// Try again with a newer slice
							targetSlice = (PersistenceSlice)getFreshSlice(newContainer.getName());
							targetSlice.setupThawedAgent(agentID, agentFK, newContainer, repository, bufferedMessages);
						}
	
						// Actually remove the frozen message queue from the DB, without retrieving the buffered messages list
						myPersistenceManager.evictFrozenMessageQueue(messageQueueID, repository);
					}
					catch(ServiceException se) {
						// Some kind of DB-related error. Abort the operation.
						frozenAgents.put(agentID, e);
						throw se;
					}
				}
				else {
					frozenAgents.put(agentID, e);
					throw new NotFoundException("Desctination container does not exist or does not support persistence.");
				}
			}
			else {
				throw new NotFoundException("Agent " + agentID.getLocalName() + " not found during thaw-agent");
			}
		}

		private void handleSaveContainer(VerticalCommand cmd) throws ServiceException, IMTPException, NotFoundException {
			Object[] params = cmd.getParams();
			String repository = (String)params[0];

			PersistenceSlice mainSlice = (PersistenceSlice)getSlice(MAIN_SLICE);

			MTPDescriptor[] mtpsArray;
			try {
				mtpsArray = mainSlice.getInstalledMTPs(myContainer.getID());
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				mainSlice = (PersistenceSlice)getFreshSlice(MAIN_SLICE);
				mtpsArray = mainSlice.getInstalledMTPs(myContainer.getID());
			}

			java.util.Set mtps = new java.util.HashSet();
			for(int i = 0; i < mtpsArray.length; i++) {
				mtps.add(mtpsArray[i]);
			}

			AID[] agentsArray = null;
			try {
				agentsArray = mainSlice.getAgentIDs(myContainer.getID());
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				mainSlice = (PersistenceSlice)getFreshSlice(MAIN_SLICE);
				agentsArray = mainSlice.getAgentIDs(myContainer.getID());
			}


			SaveContainerOperation op = new SaveContainerOperation(agentsArray, mtps);

			// Request a save operation to all the agents
			for(int i = 0; i < agentsArray.length; i++) {
				AID id = agentsArray[i];
				Agent a = myContainer.acquireLocalAgent(id);

				// Insert a new entry into the pending operations table
				submitSaveContainerOp(id, op);
				try {
					PersistenceHelper helper = (PersistenceHelper) a.getHelper(PersistenceHelper.NAME);
					helper.save(repository);
				}
				catch (ServiceException se) {
					// Should never happen
					se.printStackTrace();
				}
				myContainer.releaseLocalAgent(id);
			}

			// Wait for all agents to be in the correct state
			op.waitUntilComplete();

			try {
				java.util.Set agents = new java.util.HashSet();
				for(int i = 0; i < agentsArray.length; i++) {

					Agent a = myContainer.acquireLocalAgent(agentsArray[i]);
					List pendingMessages = new ArrayList();
					myContainer.fillListFromMessageQueue(pendingMessages, a);
					java.util.List pm = new java.util.ArrayList(pendingMessages.size());
					Iterator it = pendingMessages.iterator();
					while(it.hasNext()) {
						pm.add(it.next());
					}

					SavedAgent sa = new SavedAgent(a, pm);
					sa.setOwned(true);
					agents.add(sa);
				}

				myPersistenceManager.saveContainer(myContainer.getID().getName(), repository, agents, mtps);

				// Restart all the blocked agent threads
				op.flushAgentsToSave();

			}
			finally {
				if(agentsArray != null) {
					for(int i = 0; i < agentsArray.length; i++) {
						myContainer.releaseLocalAgent(agentsArray[i]);
					}
				}
			}

		}

		private void handleLoadContainer(VerticalCommand cmd) throws ServiceException, IMTPException, NotFoundException, NameClashException, JADESecurityException {
			Object[] params = cmd.getParams();
			String repository = (String)params[0];

			String containerName = myContainer.getID().getName();
			SavedContainer saved = new SavedContainer();
			saved.setName(containerName);

			myPersistenceManager.retrieveSavedContainer(saved, repository);

			// Stop all active agents
			AID[] agentsArray;
			PersistenceSlice mainSlice = (PersistenceSlice)getSlice(MAIN_SLICE);
			try {
				agentsArray = mainSlice.getAgentIDs(myContainer.getID());
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				mainSlice = (PersistenceSlice)getFreshSlice(MAIN_SLICE);
				agentsArray = mainSlice.getAgentIDs(myContainer.getID());
			}

			for(int i = 0; i < agentsArray.length; i++) {
				Agent a = myContainer.acquireLocalAgent(agentsArray[i]);
				a.doDelete();
				myContainer.releaseLocalAgent(agentsArray[i]);
				a.join();
			}

			// Remove all installed MTPs
			MTPDescriptor[] mtpsArray;
			try {
				mtpsArray = mainSlice.getInstalledMTPs(myContainer.getID());
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				mainSlice = (PersistenceSlice)getFreshSlice(MAIN_SLICE);
				mtpsArray = mainSlice.getInstalledMTPs(myContainer.getID());
			}

			Service messaging = myServiceFinder.findService(jade.core.messaging.MessagingSlice.NAME);
			for(int i = 0; i < mtpsArray.length; i++) {
				MTPDescriptor mtp = mtpsArray[i];
				GenericCommand uninstallMTP = new GenericCommand(jade.core.messaging.MessagingSlice.UNINSTALL_MTP, jade.core.messaging.MessagingSlice.NAME, null);
				uninstallMTP.addParam(mtp.getAddresses()[0]);
				uninstallMTP.addParam(myContainer.getID());
				messaging.submit(uninstallMTP);
			}

			// Activate all saved MTPs
			java.util.Iterator it = saved.getInstalledMTPs().iterator();
			while(it.hasNext()) {
				MTPDescriptor mtp = (MTPDescriptor)it.next();
				GenericCommand installMTP = new GenericCommand(jade.core.messaging.MessagingSlice.INSTALL_MTP, jade.core.messaging.MessagingSlice.NAME, null);
				installMTP.addParam(mtp.getAddresses()[0]);
				installMTP.addParam(myContainer.getID());
				installMTP.addParam(mtp.getClassName());
				messaging.submit(installMTP);
			}

			// Start all saved agents
			it = saved.getAgents().iterator();
			while(it.hasNext()) {
				SavedAgent sa = (SavedAgent)it.next();

				Agent instance = sa.getAgent();
				java.util.List pendingMessages = sa.getPendingMessages();

				// Restore the agent message queue inserting
				// received messages at the start of the queue
				for(int i = pendingMessages.size(); i > 0; i--) {
					instance.putBack((ACLMessage)pendingMessages.get(i - 1));
				}

				// FIXME: what Principal and credentials should we use?
				AID agentID = instance.getAID();
				myContainer.initAgent(agentID, instance, null, null);
				myContainer.powerUpLocalAgent(agentID);
			}
		}

		private void handleDeleteContainer(VerticalCommand cmd) throws IMTPException, ServiceException, NotFoundException {
			Object[] params = cmd.getParams();
			ContainerID cid = (ContainerID)params[0];
			String repository = (String)params[1];

			// Use the persistence manager to delete the agent container
			myPersistenceManager.deleteContainer(cid, repository);
		}

	} // End of CommandTargetSink class


	private class CommandIncomingFilter extends Filter {

		public boolean accept(VerticalCommand cmd) {

			try {
				String name = cmd.getName();

				if(name.equals(jade.core.messaging.MessagingSlice.SEND_MESSAGE)) {
					return handleSendMessage(cmd);
				}
				else if(name.equals(jade.core.management.AgentManagementSlice.REQUEST_KILL)) {
					return handleRequestKill(cmd);
				}
			}
			catch(IMTPException imtpe) {
				cmd.setReturnValue(imtpe);
			}
			catch(NotFoundException nfe) {
				cmd.setReturnValue(nfe);
			}
			catch(ServiceException se) {
				cmd.setReturnValue(se);
			}

			return true;
		}

		private boolean handleSendMessage(VerticalCommand cmd) throws ServiceException, IMTPException, NotFoundException {
			Object[] params = cmd.getParams();
			AID sender = (AID)params[0];
			GenericMessage genMsg = (GenericMessage)params[1];
			AID receiver = (AID)params[2];

			// FIXME: Should directly use the GenericMessage...
			ACLMessage msg = genMsg.getACLMessage();
			FrozenAgentsEntry e = (FrozenAgentsEntry)frozenAgents.get(receiver);	    
			if(e == null) {
				return true;
			}
			else {
				// Post the message to the frozen message queue and veto the command
				Long queueID = e.getQueueID();
				String repository = e.getRepository();
				myPersistenceManager.bufferMessage(queueID, repository, (ACLMessage)msg.clone());
				return false;
			}
		}

		private boolean handleRequestKill(VerticalCommand cmd) throws ServiceException, IMTPException, NotFoundException {
			Object[] params = cmd.getParams();
			AID target = (AID)params[0];

			FrozenAgentsEntry e = (FrozenAgentsEntry)frozenAgents.remove(target);
			if(e == null) {
				return true;
			}
			else {
				// Delete the frozen message queue from the repository, discarding the buffered messages
				Long queueID = e.getQueueID();
				String repository = e.getRepository();
				String sliceName = e.getSliceName();
				Long agentFK = myPersistenceManager.evictFrozenMessageQueue(queueID, repository);

				// Delete the frozen agent from the (possibly remote) repository it was stored
				PersistenceSlice targetSlice = (PersistenceSlice)getSlice(sliceName);
				try {
					targetSlice.deleteFrozenAgent(target, repository, agentFK);
				}
				catch(IMTPException imtpe) {
					// Try again with a newer slice
					targetSlice = (PersistenceSlice)getFreshSlice(sliceName);
					targetSlice.deleteFrozenAgent(target, repository, agentFK);
				}

				// Issue an INFORM_KILLED agent management command
				ServiceFinder sf = myContainer.getServiceFinder();
				Service management = sf.findService(jade.core.management.AgentManagementSlice.NAME);

				GenericCommand gCmd = new GenericCommand(jade.core.management.AgentManagementSlice.INFORM_KILLED, jade.core.management.AgentManagementSlice.NAME, null);
				gCmd.addParam(target);
				management.submit(gCmd);

				return false;
			}
		}



		public void setBlocking(boolean newState) {
			// Do nothing. Blocking and Skipping not supported
		}

		public boolean isBlocking() {
			return false; // Blocking and Skipping not implemented
		}

		public void setSkipping(boolean newState) {
			// Do nothing. Blocking and Skipping not supported
		}

		public boolean isSkipping() {
			return false; // Blocking and Skipping not implemented
		}


	} // End of CommandIncomingFilter class



	private class ServiceComponent implements Service.Slice {


		// Implementation of the Service.Slice interface

		public Service getService() {
			return PersistenceService.this;
		}

		public Node getNode() throws ServiceException {
			try {
				return PersistenceService.this.getLocalNode();
			}
			catch(IMTPException imtpe) {
				throw new ServiceException("Problem in contacting the IMTP Manager", imtpe);
			}
		}

		public VerticalCommand serve(HorizontalCommand cmd) {
			VerticalCommand result = null;
			try {
				String cmdName = cmd.getName();
				Object[] params = cmd.getParams();

				if(cmdName.equals(PersistenceSlice.H_SAVEAGENT)) {
					AID agentID = (AID)params[0];
					String repository = (String)params[1];

					if (myLogger.isLoggable(Logger.FINE)) {
						myLogger.log(Logger.FINE, "PersistenceService-Slice handling H-command H_SAVEAGENT. Agent = "+agentID.getName()+". Repository = "+repository);
					}
					GenericCommand gCmd = new GenericCommand(PersistenceHelper.SAVE_AGENT, PersistenceHelper.NAME, null);
					gCmd.addParam(agentID);
					gCmd.addParam(repository);

					result = gCmd;
				}
				else if(cmdName.equals(PersistenceSlice.H_LOADAGENT)) {
					AID agentID = (AID)params[0];
					String repository = (String)params[1];

					GenericCommand gCmd = new GenericCommand(PersistenceHelper.LOAD_AGENT, PersistenceHelper.NAME, null);
					gCmd.addParam(agentID);
					gCmd.addParam(repository);

					result = gCmd;
				}
				else if(cmdName.equals(PersistenceSlice.H_RELOADAGENT)) {
					AID agentID = (AID)params[0];
					String repository = (String)params[1];

					GenericCommand gCmd = new GenericCommand(PersistenceHelper.RELOAD_AGENT, PersistenceHelper.NAME, null);
					gCmd.addParam(agentID);
					gCmd.addParam(repository);

					result = gCmd;
				}
				else if(cmdName.equals(PersistenceSlice.H_DELETEAGENT)) {
					AID agentID = (AID)params[0];
					String repository = (String)params[1];

					GenericCommand gCmd = new GenericCommand(PersistenceHelper.DELETE_AGENT, PersistenceHelper.NAME, null);
					gCmd.addParam(agentID);
					gCmd.addParam(repository);

					result = gCmd;
				}
				else if(cmdName.equals(PersistenceSlice.H_DELETEFROZENAGENT)) {
					AID agentID = (AID)params[0];
					String repository = (String)params[1];
					Long agentFK = (Long)params[2];

					deleteFrozenAgent(agentID, repository, agentFK);
				}
				else if(cmdName.equals(PersistenceSlice.H_FREEZEAGENT)) {
					AID agentID = (AID)params[0];
					String repository = (String)params[1];
					ContainerID bufferContainer = (ContainerID)params[2];

					GenericCommand gCmd = new GenericCommand(PersistenceHelper.FREEZE_AGENT, PersistenceHelper.NAME, null);
					gCmd.addParam(agentID);
					gCmd.addParam(repository);
					gCmd.addParam(bufferContainer);

					result = gCmd;
				}
				else if(cmdName.equals(PersistenceSlice.H_THAWAGENT)) {
					AID agentID = (AID)params[0];
					String repository = (String)params[1];
					ContainerID newContainer = (ContainerID)params[2];

					GenericCommand gCmd = new GenericCommand(PersistenceHelper.THAW_AGENT, PersistenceHelper.NAME, null);
					gCmd.addParam(agentID);
					gCmd.addParam(repository);
					gCmd.addParam(newContainer);

					result = gCmd;
				}
				else if(cmdName.equals(PersistenceSlice.H_SETUPFROZENAGENT)) {
					AID agentID = (AID)params[0];
					Long agentFK = (Long)params[1];
					ContainerID cid = (ContainerID)params[2];
					String repository = (String)params[3];

					cmd.setReturnValue(setupFrozenAgent(agentID, agentFK, cid, repository));
				}
				else if(cmdName.equals(PersistenceSlice.H_SETUPTHAWEDAGENT)) {
					AID agentID = (AID)params[0];
					Long agentFK = (Long)params[1];
					ContainerID cid = (ContainerID)params[2];
					String repository = (String)params[3];
					List bufferedMessages = (List)params[4];

					setupThawedAgent(agentID, agentFK, cid, repository, bufferedMessages);
				}
				else if(cmdName.equals(PersistenceSlice.H_FROZENAGENT)) {
					AID agentID = (AID)params[0];
					ContainerID home = (ContainerID)params[1];
					ContainerID buffer = (ContainerID)params[2];

					frozenAgent(agentID, home, buffer);
				}
				else if(cmdName.equals(PersistenceSlice.H_THAWEDAGENT)) {
					AID agentID = (AID)params[0];
					ContainerID buffer = (ContainerID)params[1];
					ContainerID home = (ContainerID)params[2];

					thawedAgent(agentID, buffer, home);
				}
				else if(cmdName.equals(PersistenceSlice.H_SAVECONTAINER)) {
					String repository = (String)params[0];

					GenericCommand gCmd = new GenericCommand(PersistenceHelper.SAVE_CONTAINER, PersistenceHelper.NAME, null);
					gCmd.addParam(repository);

					result = gCmd;
				}
				else if(cmdName.equals(PersistenceSlice.H_LOADCONTAINER)) {
					String repository = (String)params[0];

					GenericCommand gCmd = new GenericCommand(PersistenceHelper.LOAD_CONTAINER, PersistenceHelper.NAME, null);
					gCmd.addParam(repository);

					result = gCmd;
				}
				else if(cmdName.equals(PersistenceSlice.H_DELETECONTAINER)) {
					ContainerID cid = (ContainerID)params[0];
					String repository = (String)params[1];

					GenericCommand gCmd = new GenericCommand(PersistenceHelper.DELETE_CONTAINER, PersistenceHelper.NAME, null);
					gCmd.addParam(cid);
					gCmd.addParam(repository);

					result = gCmd;                    
				}
				else if(cmdName.equals(PersistenceSlice.H_GETINSTALLEDMTPS)) {
					ContainerID cid = (ContainerID)params[0];
					cmd.setReturnValue(getInstalledMTPs(cid));
				}
				else if(cmdName.equals(PersistenceSlice.H_GETAGENTIDS)) {
					ContainerID cid = (ContainerID)params[0];
					cmd.setReturnValue(getAgentIDs(cid));
				}
				else if(cmdName.equals(PersistenceSlice.H_GETREPOSITORIES)) {
					cmd.setReturnValue(getRepositories());
				}
				else if(cmdName.equals(PersistenceSlice.H_GETSAVEDAGENTS)) {
					String repository = (String)params[0];
					cmd.setReturnValue(getSavedAgents(repository));
				}
				else if(cmdName.equals(PersistenceSlice.H_GETFROZENAGENTS)) {
					String repository = (String)params[0];
					cmd.setReturnValue(getFrozenAgents(repository));
				}
				else if(cmdName.equals(PersistenceSlice.H_GETSAVEDCONTAINERS)) {
					String repository = (String)params[0];
					cmd.setReturnValue(getSavedContainers(repository));
				}
			}
			catch(Throwable t) {
				cmd.setReturnValue(t);
				if(result != null) {
					result.setReturnValue(t);
				}
			}

			return result;
		}

		private void deleteFrozenAgent(AID agentID, String repository, Long agentFK) throws ServiceException, IMTPException, NotFoundException {
			// Use the persistence manager to delete the agent
			myPersistenceManager.deleteFrozenAgent(agentFK, repository);
		}

		private Long setupFrozenAgent(AID agentID, Long agentFK, ContainerID src, String repository) throws ServiceException, IMTPException, NotFoundException {

			// Create the frozen message queue
			Long messageQueueID = myPersistenceManager.createFrozenMessageQueue((AID)agentID.clone(), agentFK, repository);
			FrozenAgentsEntry e = new FrozenAgentsEntry(agentID, src.getName(), repository, messageQueueID);

			frozenAgents.put(agentID, e);

			PersistenceSlice mainSlice = (PersistenceSlice)getSlice(MAIN_SLICE);
			ContainerID dest = myContainer.getID();
			try {
				mainSlice.frozenAgent(agentID, src, dest);
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				mainSlice = (PersistenceSlice)getFreshSlice(MAIN_SLICE);
				mainSlice.frozenAgent(agentID, src, dest);
			}

			return messageQueueID;
		}

		private void setupThawedAgent(AID agentID, Long agentFK, ContainerID buffer, String repository, List bufferedMessages) throws ServiceException, IMTPException, NotFoundException, JADESecurityException {
			Agent instance = myPersistenceManager.thawAgent(agentID, repository, agentFK);

			// Submit the buffered messages to the newly thawed agent
			for(int i = bufferedMessages.size(); i > 0; i--) {			
				instance.putBack((ACLMessage)bufferedMessages.get(i - 1));
			}

			myContainer.addLocalAgent(agentID, instance);

			PersistenceSlice mainSlice = (PersistenceSlice)getSlice(MAIN_SLICE);
			ContainerID here = myContainer.getID();
			try {
				mainSlice.thawedAgent(agentID, buffer, here);
			}
			catch(IMTPException imtpe) {
				// Try again with a newer slice
				mainSlice = (PersistenceSlice)getFreshSlice(MAIN_SLICE);
				mainSlice.thawedAgent(agentID, buffer, here);
			}
			myContainer.powerUpLocalAgent(agentID);
		}

		private void frozenAgent(AID agentID, ContainerID home, ContainerID buffer) throws IMTPException, NotFoundException {
			// Update the GADT so that the frozen agent now appears to
			// be on this container...
			MainContainer impl = myContainer.getMain();
			if(impl != null) {

				impl.acquireAgentDescriptor(agentID);

				// Commit transaction
				impl.frozenAgent(agentID, buffer);
				impl.movedAgent(agentID, home, buffer);
				impl.releaseAgentDescriptor(agentID);
			}
		}

		private void thawedAgent(AID agentID, ContainerID buffer, ContainerID home) throws IMTPException, NotFoundException {
			// Update the GADT so that the frozen agent now appears to
			// be on the new container...
			MainContainer impl = myContainer.getMain();
			if(impl != null) {

				impl.acquireAgentDescriptor(agentID);

				// Commit transaction
				impl.thawedAgent(agentID, home);
				impl.movedAgent(agentID, buffer, home);
				impl.releaseAgentDescriptor(agentID);
			}
		}

		private MTPDescriptor[] getInstalledMTPs(ContainerID cid) throws IMTPException, NotFoundException {
			MainContainer impl = myContainer.getMain();
			if(impl != null) {

				List l = impl.containerMTPs(cid);
				MTPDescriptor[] mtps = new MTPDescriptor[l.size()];
				Iterator it = l.iterator();
				int idx = 0;
				while(it.hasNext()) {
					mtps[idx++] = (MTPDescriptor)it.next();
				}

				return mtps;
			}
			else {
				throw new NotFoundException("Not a Main Container");
			}
		}

		private AID[] getAgentIDs(ContainerID cid) throws IMTPException, NotFoundException {
			MainContainer impl = myContainer.getMain();
			if(impl != null) {

				List l = impl.containerAgents(cid);
				AID[] agentIDs = new AID[l.size()];
				Iterator it = l.iterator();
				int idx = 0;
				while(it.hasNext()) {
					agentIDs[idx++] = (AID)it.next();
				}
				return agentIDs;
			}
			else {
				throw new NotFoundException("Not a Main Container");
			}
		}

		private String[] getRepositories() throws ServiceException {
			return myPersistenceManager.getRepositoryNames();
		}

		private String[] getSavedAgents(String repository) throws ServiceException, NotFoundException {
			return myPersistenceManager.getSavedAgentNames(repository);
		}

		private String[] getFrozenAgents(String repository) throws ServiceException, NotFoundException {
			return myPersistenceManager.getFrozenAgentNames(repository);
		}

		private String[] getSavedContainers(String repository) throws ServiceException, NotFoundException {
			return myPersistenceManager.getSavedContainerNames(repository);
		}


	} // End of ServiceComponent class




	/////////////////////////////////////////////////////////
	// These methods are called by the PersistenceHelperImpl
	/////////////////////////////////////////////////////////
	LifeCycle createSavingLC(String r, Savable s) {
		return new SavingLifeCycle(r, s, this);
	}

	LifeCycle createFrozenLC(String r, ContainerID cid, Savable s) {
		return new FrozenLifeCycle(r, cid, s, this);
	}

	LifeCycle createReloadingLC(String r, Savable s) {
		return new ReloadingLifeCycle(r, s, this);
	}

	/**
	   Inner class SavingLifeCycle
	 */
	private static class SavingLifeCycle extends LifeCycle {
		private String repository;
		private Savable mySavable;
		private transient PersistenceService myService; 

		private SavingLifeCycle(String r, Savable s, PersistenceService svc) {
			super(AP_SAVING);
			repository = r;
			mySavable = s;
			myService = svc;
		}

		public void init() {
			myAgent.restoreBufferedState();
			mySavable.afterLoad();
			// The PersistenceHelper is transient --> The Savable object 
			// is no longer registered --> restore it. 
			restoreRegisteredSavable(myAgent, mySavable);
		}

		public void execute() throws JADESecurityException, InterruptedException, InterruptedIOException {
			if (mySavable == null) {
				mySavable = new ReflectiveSavable(myAgent, myService.createInvokator());
			}
			mySavable.beforeSave();
			try {
				// Issue an SAVE_MYSELF vertical command
				issueSaveMyself(myAgent.getAID(), repository);
			}
			catch (Exception e) {
				if (myAgent.getState() == myState) {
					// Something went wrong during the save. Rollback
					repository = null;
					myAgent.restoreBufferedState();
					if (e instanceof JADESecurityException) {
						// Will be caught together with all other JADESecurityException-s
						throw (JADESecurityException) e;
					}
					else {
						e.printStackTrace();
						return;
					}
				}
				else {
					throw new Interrupted();
				}
			}
			// Once saved go back to the previous state
			myAgent.restoreBufferedState();
		}

		public void end() {
			myAgent.clean(false);
		}

		public boolean transitionTo(LifeCycle newLF) {
			int s = newLF.getState();
			return (s == Agent.AP_ACTIVE || s == Agent.AP_DELETED);
		}

		private void issueSaveMyself(AID agentID, String repository) throws ServiceException, JADESecurityException, NotFoundException, IMTPException {
			GenericCommand cmd = new GenericCommand(PersistenceHelper.SAVE_MYSELF, PersistenceSlice.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(repository);
			// Set the credentials of the agent to be saved
			myService.initCredentials(cmd, agentID);

			Object ret = myService.submit(cmd);
			if (ret != null) {
				if (ret instanceof JADESecurityException) {
					throw ((JADESecurityException) ret);
				}
				if (ret instanceof ServiceException) {
					throw ((ServiceException) ret);
				}
				else if (ret instanceof NotFoundException) {
					throw ((NotFoundException) ret);
				}
				else if (ret instanceof IMTPException) {
					throw ((IMTPException) ret);
				}
				else if (ret instanceof Throwable) {
					((Throwable) ret).printStackTrace();
				}
			}
		}
	} // END of inner class SavingLifeCycle


	/**
	   Inner class FrozenLifeCycle
	 */
	private static class FrozenLifeCycle extends LifeCycle {
		private String repository;
		private ContainerID bufferContainer;
		private Savable mySavable;
		private transient PersistenceService myService; 

		private boolean freezeComplete = false;

		private FrozenLifeCycle(String r, ContainerID cid, Savable s, PersistenceService svc) {
			super(AP_FROZEN);
			repository = r;
			bufferContainer = cid;
			mySavable = s;
			myService = svc;
		}

		public void init() {
			myAgent.restoreBufferedState();			
			mySavable.afterThaw();
			// The PersistenceHelper is transient --> The Savable object 
			// is no longer registered --> restore it.
			restoreRegisteredSavable(myAgent, mySavable);
		}

		public boolean alive() {
			return !freezeComplete;
		}

		public void execute() throws JADESecurityException, InterruptedException, InterruptedIOException {
			if (mySavable == null) {
				mySavable = new ReflectiveSavable(myAgent, myService.createInvokator());
			}
			mySavable.beforeFreeze();
			try {
				// Issue an FREEZE_MYSELF vertical command
				issueFreezeMyself(myAgent.getAID(), repository, bufferContainer);
				freezeComplete = true;
			}
			catch (Exception e) {
				if (myAgent.getState() == myState) {
					// Something went wrong during the freeze. Rollback
					repository = null;
					bufferContainer = null;
					myAgent.restoreBufferedState();
					if (e instanceof JADESecurityException) {
						// Will be caught together with all other JADESecurityException-s
						throw (JADESecurityException) e;
					}
					else {
						e.printStackTrace();
					}
				}
				else {
					throw new Interrupted();
				}
			}
		}

		public void end() {
			if (!freezeComplete) {
				System.err.println("***  Agent " + myAgent.getName() + " frozen in a forbidden situation ***");
				myAgent.clean(true);
			}
		}	

		public boolean transitionTo(LifeCycle newLF) {
			int s = newLF.getState();
			return (s == Agent.AP_ACTIVE || s == Agent.AP_DELETED);
		}

		private void issueFreezeMyself(AID agentID, String repository, ContainerID bufferContainer) throws ServiceException, JADESecurityException, NotFoundException, IMTPException {
			GenericCommand cmd = new GenericCommand(PersistenceHelper.FREEZE_MYSELF, PersistenceHelper.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(repository);
			cmd.addParam(bufferContainer);
			// Set the credentials of the agent to be frozen
			myService.initCredentials(cmd, agentID);

			Object ret = myService.submit(cmd);
			if (ret != null) {
				if (ret instanceof ServiceException) {
					throw ((ServiceException) ret);
				}
				if (ret instanceof JADESecurityException) {
					throw ((JADESecurityException) ret);
				}
				else if (ret instanceof NotFoundException) {
					throw ((NotFoundException) ret);
				}
				else if (ret instanceof IMTPException) {
					throw ((IMTPException) ret);
				}
				else if (ret instanceof Throwable) {
					((Throwable) ret).printStackTrace();
				}
			}
		}
	} // END of inner class FrozenLifeCycle


	/**
	   Inner class ReloadingLifeCycle
	 */
	private static class ReloadingLifeCycle extends LifeCycle {
		private String repository;
		private Savable mySavable;
		private transient PersistenceService myService;

		private boolean reloadComplete = false;

		private ReloadingLifeCycle(String r, Savable s, PersistenceService svc) {
			super(AP_FROZEN);
			repository = r;
			mySavable = s;
			myService = svc;
		}

		public void init() {
			myAgent.restoreBufferedState();
			mySavable.afterReload();
			// The PersistenceHelper is transient --> The Savable object 
			// is no longer registered --> restore it. 
			restoreRegisteredSavable(myAgent, mySavable);
		}

		public boolean alive() {
			return !reloadComplete;
		}

		public void execute() throws JADESecurityException, InterruptedException, InterruptedIOException {
			if (mySavable == null) {
				mySavable = new ReflectiveSavable(myAgent, myService.createInvokator());
			}
			mySavable.beforeReload();
			try {
				// Issue an RELOAD_MYSELF vertical command
				issueReloadMyself(myAgent.getAID(), repository);
				reloadComplete = true;
			}
			catch (Exception e) {
				if (myAgent.getState() == myState) {
					// Something went wrong during the reload. Rollback
					repository = null;
					myAgent.restoreBufferedState();
					if (e instanceof JADESecurityException) {
						// Will be caught together with all other JADESecurityException-s
						throw (JADESecurityException) e;
					}
					else {
						e.printStackTrace();
					}
				}
				else {
					throw new Interrupted();
				}
			}
		}

		public void end() {
			if (!reloadComplete) {
				System.err.println("***  Agent " + myAgent.getName() + " reloaded in a forbidden situation ***");
				myAgent.clean(true);
			}
		}	

		public boolean transitionTo(LifeCycle newLF) {
			int s = newLF.getState();
			return (s == Agent.AP_ACTIVE || s == Agent.AP_DELETED);
		}

		private void issueReloadMyself(AID agentID, String repository) throws ServiceException, JADESecurityException, NotFoundException, IMTPException {
			GenericCommand cmd = new GenericCommand(PersistenceHelper.RELOAD_MYSELF, PersistenceHelper.NAME, null);
			cmd.addParam(agentID);
			cmd.addParam(repository);
			// Set the credentials of the agent to be reloaded
			myService.initCredentials(cmd, agentID);

			Object ret = myService.submit(cmd);
			if (ret != null) {
				if (ret instanceof ServiceException) {
					throw ((ServiceException) ret);
				}
				if (ret instanceof JADESecurityException) {
					throw ((JADESecurityException) ret);
				}
				else if (ret instanceof NotFoundException) {
					throw ((NotFoundException) ret);
				}
				else if (ret instanceof IMTPException) {
					throw ((IMTPException) ret);
				}
				else if (ret instanceof Throwable) {
					((Throwable) ret).printStackTrace();
				}
			}
		}
	} // END of inner class FrozenLifeCycle


	/**
     Inner class ReflectiveSavable.
     This is the default Savable if no one is registered.
     This Savable invoke using Java reflection the proper callback
     method (if defined) on the agent itself.
	 */
	private static class ReflectiveSavable implements Savable {
		private Agent myAgent;
		private CallbackInvokator myInvokator;

		ReflectiveSavable(Agent a, CallbackInvokator ci) {
			myAgent = a;
			myInvokator = ci;
		}

		public void beforeSave(){
			myInvokator.invokeCallbackMethod(myAgent, "beforeSave");
		}
		public void afterLoad(){
			myInvokator.invokeCallbackMethod(myAgent, "afterLoad");
		}
		public void beforeReload(){
			myInvokator.invokeCallbackMethod(myAgent, "beforeReload");
		}
		public void afterReload(){
			myInvokator.invokeCallbackMethod(myAgent, "afterReload");
		}
		public void beforeFreeze(){
			myInvokator.invokeCallbackMethod(myAgent, "beforeFreeze");
		}
		public void afterThaw(){
			myInvokator.invokeCallbackMethod(myAgent, "afterThaw");
		}
	}

	private static void restoreRegisteredSavable(Agent a, Savable s) {
		try {
			PersistenceHelper helper = (PersistenceHelper) a.getHelper(PersistenceHelper.NAME);
			helper.registerSavable(s);
		}
		catch (ServiceException se) {
			// Should never happen
			se.printStackTrace();
		}
	}

	// The concrete agent container, providing access to LADT, etc.
	private AgentContainer myContainer;

	// The Service Finder component
	private ServiceFinder myServiceFinder;

	// The local slice for this service
	private final ServiceComponent localSlice = new ServiceComponent();

	// The command sink, source side
	private final CommandSourceSink senderSink = new CommandSourceSink();

	// The command sink, target side
	private final CommandTargetSink receiverSink = new CommandTargetSink();

	// The command filter, incoming direction
	private final CommandIncomingFilter inFilter = new CommandIncomingFilter();

	// The actual persistence engine
	private PersistenceManager myPersistenceManager;

	// The behaviour object to be deployed within the AMS
	private Behaviour amsHandler;

	// The default persistence helper used by the service itself and not
	// associated to any agent
	private final PersistenceHelperImpl defaultHelper = new PersistenceHelperImpl(this);

	// The table of the primary keys for the currently frozen agents
	private Map frozenAgents = new HashMap();

	private Map saveContainerOps = new HashMap();

	// Work-around for PJAVA compilation
	protected Service.Slice getFreshSlice(String name) throws ServiceException {
		return super.getFreshSlice(name);
	}

	private void submitSaveContainerOp(AID agentID, SaveContainerOperation op) {

		// FIXME: Should use a map of lists
		saveContainerOps.put(agentID, op);
	}

	private SaveContainerOperation retrieveSaveContainerOp(AID agentID) {

		// FIXME: Should use a map of lists
		return (SaveContainerOperation)saveContainerOps.get(agentID);
	}

	private void initCredentials(Command cmd, AID id) {
		Agent agent = myContainer.acquireLocalAgent(id);
		if (agent != null) {
			try {
				CredentialsHelper ch = (CredentialsHelper) agent.getHelper("jade.core.security.Security");
				cmd.setPrincipal(ch.getPrincipal());
				cmd.setCredentials(ch.getCredentials());
			}
			catch (ServiceException se) {
				// The security plug-in is not there. Just ignore it
			}
		}  		
		myContainer.releaseLocalAgent(id);
	}
}

