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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.hibernate.Hibernate;
import org.hibernate.MappingException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import jade.core.Agent;
import jade.core.AID;
import jade.core.ContainerID;
import jade.core.ServiceException;
import jade.core.NotFoundException;
import jade.core.NameClashException;

import jade.lang.acl.ACLMessage;

import jade.util.leap.Map;
import jade.util.leap.List;
import jade.util.leap.HashMap;
import jade.util.leap.Properties;
import jade.util.Logger;


/**

   The store/retrieve engine used to manage saving and retrieving
   agents and containers to persistent storage, using Hibernate.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
   
   modified by Vincenzo Gioviale

 */
public class PersistenceManager {

	public static final String DEFAULT_REPOSITORY = "JADE-DB";

	//Logger object
	private static Logger logger = Logger.getMyLogger(PersistenceManager.class.getName());

	public PersistenceManager(String metaDB, String nodeName) throws IOException, HibernateException {
		sessionFactories = new HashMap();
		init(metaDB);
	}

	private void init(String metaDB) throws IOException, HibernateException {
		// Create the meta-database and a default repository if needed
		Configuration metaConf = new Configuration();

		if(metaDB != null) {
			java.util.Properties props = new java.util.Properties();
			InputStream is = null;
			try {
				is = new URL(metaDB).openStream();
			}
			catch (IOException e) {
				// Try in the classpath
				is = getClass().getClassLoader().getResourceAsStream(metaDB);
			}
			if (is != null) {
				props.load(is);
				metaConf.setProperties(props);
			}
			else {
				throw new IOException("Meta-DB configuration "+metaDB+" not found! It must be either a valid URL or a resource in the classpath.");
			}
		}

		metaConf.addResource("jade/core/persistence/meta.hbm.xml", getClass().getClassLoader());

		if(logger.isLoggable(Logger.INFO))
			logger.log(Logger.INFO,">>> Connecting to the Meta-DB [" + metaDB + "] <<<");
		metaSchemaMgr = new SchemaExport(metaConf);
		metaSessions = metaConf.buildSessionFactory();
		defaultProperties = metaConf.getProperties();

		checkMetaDB(defaultProperties);
		loadRepositories();
	}

	public synchronized void destroy() {
		// Shut down all the active session factories
		Object[] keys = sessionFactories.keySet().toArray();
		for(int i = 0; i < keys.length; i++) {
			try {
				SessionFactory sf = (SessionFactory)sessionFactories.remove(keys[i]);
				sf.close();
			}
			catch(HibernateException he) {
				he.printStackTrace();
			}
		}
	}

	public synchronized void addSessionFactory(Repository rep) throws IOException, HibernateException {
		String name = rep.getName();

		Configuration newConf = new Configuration();
		java.util.Properties props = rep.getProperties().getProperties();
		if(logger.isLoggable(Logger.INFO))
			logger.log(Logger.INFO,"--- Loading " + props.size() + " properties ---");

		if(!props.isEmpty()) {
			newConf.setProperties(props);
		}

		// Add the system mappings
		newConf.addResource("jade/core/persistence/common.hbm.xml", getClass().getClassLoader());
		newConf.addClass(SavedAgent.class);
		newConf.addClass(SavedACLMessage.class);
		newConf.addClass(FrozenAgent.class);
		newConf.addClass(FrozenMessageQueue.class);
		newConf.addClass(SavedContainer.class);

		// Add the repository-specific mappings
		java.util.List mappingsList = rep.getMappings();
		String[] mappings = (String[])mappingsList.toArray(new String[mappingsList.size()]);
		for(int i = 0; i < mappings.length; i++) {
			try {
				// Try to open the URL before
				URL mapping = new URL(mappings[i]);
				mapping.openStream().close();

				newConf.addURL(mapping);
			}
			catch(IOException ioe) {
				// Ignore it and go on with the next URL
			}
		}

		// Build a new SessionFactory and put it in the table
		SessionFactory sf = newConf.buildSessionFactory();
		checkDB(name, newConf, sf);
		sessionFactories.put(name, sf);
	}

	public synchronized void removeSessionFactory(String name) throws HibernateException {
		SessionFactory sf = (SessionFactory)sessionFactories.remove(name);
		sf.close();
	}

	public synchronized void addRepository(String repName) throws NameClashException, ServiceException {
		try {
			Session s = metaSessions.openSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();

				java.util.List old = s.createQuery("from jade.core.persistence.Repository as item where item.name = ?").setParameter(0, repName, Hibernate.STRING).list();
				if(!old.isEmpty()) {
					throw new NameClashException("A repository named <" + repName + "> already exists");
				}

				Repository rep = new Repository();
				rep.setName(repName);
				Repository.StoredProperties sp = new Repository.StoredProperties();
				sp.setValues(defaultProperties);
				rep.setProperties(sp);

				s.save(rep);

				tx.commit();

				addSessionFactory(rep);
			}
			catch(HibernateException he) {
				he.printStackTrace();
				if(tx != null) {
					tx.rollback();
				}
				throw he;
			}
			finally {
				s.close();
			}
		}
		catch(IOException ioe) {
			throw new ServiceException("An I/O error occurred while adding the new repository", ioe);
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while adding a new repository", he);
		}
	}

	public synchronized void removeRepository(String repName) throws ServiceException {
		try {
			Session s = metaSessions.openSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				removeSessionFactory(repName);
				((org.hibernate.classic.Session)s).delete("from jade.core.persistence.Repository as item where item.name = ?", repName, Hibernate.STRING);
				tx.commit();
			}
			catch(HibernateException he) {
				he.printStackTrace();
				if(tx != null) {
					tx.rollback();
				}
				throw he;
			}
			finally {
				s.close();
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while removing a repository", he);
		}
	}

	public synchronized void saveRepository(Repository rep) throws ServiceException {
		try {
			Session s = metaSessions.openSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();
				s.saveOrUpdate(rep);
				tx.commit();
			}
			catch(HibernateException he) {
				he.printStackTrace();
				if(tx != null) {
					tx.rollback();
				}
				throw he;
			}
			finally {
				s.close();
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while saving a repository", he);
		}
	}

	public synchronized void loadRepositories() {
		try {
			Session s = metaSessions.openSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();

				// Retrieve all the repository descriptions from the Meta-DB
				java.util.List resultSet = s.createQuery("from jade.core.persistence.Repository").list();
				tx.commit();
				tx = null;

				java.util.Iterator it = resultSet.iterator();
				while(it.hasNext()) {
					Repository rep = (Repository)it.next();
					try {
						addSessionFactory(rep);
					}
					catch(IOException ioe) {
						if(logger.isLoggable(Logger.WARNING))
							logger.log(Logger.WARNING,"--- Could not load repository <" + rep.getName() + "> ---");
					}
					catch(HibernateException he) {
						if(logger.isLoggable(Logger.WARNING))
							logger.log(Logger.WARNING,"--- Could not load repository <" + rep.getName() + "> ---");                        
						he.printStackTrace();                        
					}
				}
			}
			catch(HibernateException he) {
				he.printStackTrace();
				if(tx != null) {
					tx.rollback();
				}
			}
			finally {
				s.close();
			}
		}
		catch(HibernateException he) {
			he.printStackTrace();
		}
	}

	public synchronized Repository getRepository(String name) throws NotFoundException, ServiceException {
		try {
			Session s = metaSessions.openSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();

				java.util.List resultSet = s.createQuery("from jade.core.persistence.Repository as item where item.name = ?").setParameter(0, name, Hibernate.STRING).list();
				tx.commit();

				if(resultSet.isEmpty()) {
					throw new NotFoundException("The repository named <" + name + "> was not found");
				}
				else {
					return (Repository)resultSet.get(0);
				}
			}
			catch(HibernateException he) {
				he.printStackTrace();
				if(tx != null) {
					tx.rollback();
				}
				throw he;
			}
			finally {
				s.close();
			}
		}
		catch(HibernateException he) {
			he.printStackTrace();
			throw new ServiceException("An error occurred while retrieving repository <" + name + ">", he);
		}
	}

	public synchronized Repository[] getRepositories() throws ServiceException {
		try {
			Session s = metaSessions.openSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();

				java.util.List resultSet = s.createQuery("from jade.core.persistence.Repository").list();
				tx.commit();

				return (Repository[])resultSet.toArray(new Repository[resultSet.size()]);
			}
			catch(HibernateException he) {
				he.printStackTrace();
				if(tx != null) {
					tx.rollback();
				}
				throw he;
			}
			finally {
				s.close();
			}
		}
		catch(HibernateException he) {
			he.printStackTrace();
			throw new ServiceException("An error occurred while retrieving the repository list", he);
		}
	}

	public synchronized String[] getRepositoryNames() throws ServiceException {
		try {
			Session s = metaSessions.openSession();
			Transaction tx = null;
			try {
				tx = s.beginTransaction();

				java.util.List resultSet = s.createQuery("select item.name from jade.core.persistence.Repository as item").list();
				tx.commit();

				return (String[])resultSet.toArray(new String[resultSet.size()]);
			}
			catch(HibernateException he) {
				he.printStackTrace();
				if(tx != null) {
					tx.rollback();
				}
				throw he;
			}
			finally {
				s.close();
			}
		}
		catch(HibernateException he) {
			he.printStackTrace();
			throw new ServiceException("An error occurred while retrieving the repository list", he);
		}
	}

	public synchronized String[] getSavedAgentNames(String repository) throws ServiceException, NotFoundException {
		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {
				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();

					java.util.List resultSet = s.createQuery("select item.name from jade.core.persistence.SavedAgent as item where item.owned = false").list();
					tx.commit();

					return (String[])resultSet.toArray(new String[resultSet.size()]);
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			he.printStackTrace();
			throw new ServiceException("An error occurred while retrieving the repository list", he);
		}
	}

	public synchronized String[] getFrozenAgentNames(String repository) throws ServiceException, NotFoundException {
		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {
				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();

					java.util.List resultSet = s.createQuery("select item.agentIdentifier.name from jade.core.persistence.FrozenMessageQueue as item").list();
					tx.commit();

					return (String[])resultSet.toArray(new String[resultSet.size()]);
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			he.printStackTrace();
			throw new ServiceException("An error occurred while retrieving the repository list", he);
		}
	}

	public synchronized String[] getSavedContainerNames(String repository) throws ServiceException, NotFoundException {
		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {
				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();

					java.util.List resultSet = s.createQuery("select item.name from jade.core.persistence.SavedContainer as item").list();
					tx.commit();

					return (String[])resultSet.toArray(new String[resultSet.size()]);
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			he.printStackTrace();
			throw new ServiceException("An error occurred while retrieving the repository list", he);
		}
	}



	// Retrieve the property values used in the Meta-DB itself
	public synchronized java.util.Map getDefaultPropertyValues() {
		return defaultProperties;
	}

	public void saveAgent(Agent target, String repository, java.util.List pendingMessages) throws ServiceException, NotFoundException {

		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {
				// Save this agent
				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					SavedAgent toSave = null;
					java.util.List resultSet = s.createQuery("from jade.core.persistence.SavedAgent as item where item.name = ?").setParameter(0, target.getName(), Hibernate.STRING).list();
					if(!resultSet.isEmpty()) {
						toSave = (SavedAgent)resultSet.get(0);
						toSave.setAgentData(target);
						java.util.List savedMessages = toSave.getPendingMessages();
						if(savedMessages != null) {
							if(savedMessages != pendingMessages) {
								savedMessages.clear();
								java.util.Iterator it = pendingMessages.iterator();
								while(it.hasNext()) {
									ACLMessage msg = (ACLMessage)it.next();
									savedMessages.add(msg.clone());
								}
							}
						}
						else {
							toSave.setPendingMessages(pendingMessages);
						}
					}
					else {
						toSave = new SavedAgent(target, pendingMessages);
					}
					s.saveOrUpdate(toSave);
					tx.commit();
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while persisting the agent", he);
		}
	}

	public Agent loadAgent(AID target, String repository) throws ServiceException, NotFoundException {
		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					java.util.List resultSet = s.createQuery("from jade.core.persistence.SavedAgent as item where item.name = ?").setParameter(0, target.getName(), Hibernate.STRING).list();
					tx.commit();

					if(!resultSet.isEmpty()) {
						SavedAgent loaded = (SavedAgent)resultSet.get(0);
						Agent result = loaded.getAgent();
						java.util.List pendingMessages = loaded.getPendingMessages();

						// Restore the agent message queue inserting
						// received messages at the start of the queue
						for(int i = pendingMessages.size(); i > 0; i--) {
							result.putBack((ACLMessage)pendingMessages.get(i - 1));
						}

						return result;
					}
					else {
						throw new NotFoundException("Agent <" + target.getLocalName() + "> was not found in repository <" + repository + ">");
					}
				}
				catch(HibernateException he) {
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while loading agent <" + target.getLocalName() + ">", he);
		}
	}

	public void deleteAgent(AID target, String repository) throws ServiceException, NotFoundException {

		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					int deleted1 = ((org.hibernate.classic.Session) s).delete("from jade.core.persistence.FrozenAgent as item where item.agent.name = ?", target.getName(), Hibernate.STRING);
					int deleted2 = ((org.hibernate.classic.Session) s).delete("from jade.core.persistence.SavedAgent as item where item.name = ?", target.getName(), Hibernate.STRING);
					tx.commit();
					if(logger.isLoggable(Logger.INFO))
						logger.log(Logger.INFO,"--- Deleted " + deleted1 + " frozen and " + deleted2 + " saved agents ---");
				}
				catch(HibernateException he) {
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while deleting agent <" + target.getLocalName() + ">", he);
		}
	}

	public void deleteFrozenAgent(Long agentPK, String repository) throws ServiceException, NotFoundException {

		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					Object toDelete = s.load(FrozenAgent.class, agentPK);
					s.delete(toDelete);
					tx.commit();
					if(logger.isLoggable(Logger.INFO))
						logger.log(Logger.INFO,"--- Deleted frozen agent <" + agentPK + "> ---");
				}
				catch(HibernateException he) {
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while deleting a frozen agent <" + agentPK + ">", he);
		}
	}

	public Long freezeAgent(Agent target, String repository, java.util.List pendingMessages) throws ServiceException, NotFoundException {

		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {
				// Save this agent
				Session s = sf.openSession();
				Transaction tx = null;
				try {
					SavedAgent sa = new SavedAgent(target, pendingMessages);
					sa.setOwned(true);
					FrozenAgent toFreeze = new FrozenAgent(sa);
					tx = s.beginTransaction();
					Long newID  = (Long)s.save(toFreeze);
					tx.commit();
					return newID;
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while persisting the agent", he);
		}
	}

	public Long createFrozenMessageQueue(AID agentID, Long agentFK, String repository) throws ServiceException, NotFoundException {
		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {
				// Save this agent
				Session s = sf.openSession();
				Transaction tx = null;
				try {
					FrozenMessageQueue toFreeze = new FrozenMessageQueue(agentID, agentFK);
					tx = s.beginTransaction();
					Long newID  = (Long)s.save(toFreeze);
					tx.commit();
					return newID;
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while persisting the agent", he);
		}
	}

	public void connectToMessageQueue(Long agentID, Long messageQueueFK, String repository) throws ServiceException, NotFoundException {
		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {
				Session s = sf.openSession();
				Transaction tx = null;
				try {
					// Update the frozen agent with the foreign key
					// for its frozen message queue (possibly stored
					// on another DBMS)
					tx = s.beginTransaction();
					FrozenAgent frozen = (FrozenAgent)s.load(FrozenAgent.class, agentID);
					frozen.setMessageQueueFK(messageQueueFK);
					tx.commit();
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while persisting the agent", he);
		}
	}

	public void bufferMessage(Long queueID, String repository, ACLMessage msg) throws ServiceException, NotFoundException {
		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {
				Session s = sf.openSession();
				Transaction tx = null;
				try {
					// Add the received message to the buffered messages
					tx = s.beginTransaction();
					FrozenMessageQueue frozen = (FrozenMessageQueue)s.load(FrozenMessageQueue.class, queueID);
					frozen.getBufferedMessages().add(msg);
					tx.commit();
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while buffering the message", he);
		}
	}

	public Agent thawAgent(AID target, String repository, Long persistentID) throws ServiceException, NotFoundException {
		try {

			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();

					FrozenAgent frozen = (FrozenAgent)s.load(FrozenAgent.class, persistentID);
					SavedAgent sa = frozen.getAgent();
					Agent result = sa.getAgent();


					// Restore the agent message queue inserting
					// received messages at the start of the queue.
					// We put the buffered messages in front of the
					// messages pending at the moment of freezing the
					// agent.

					java.util.List pendingMessages = sa.getPendingMessages();
					for(int i = pendingMessages.size(); i > 0; i--) {
						ACLMessage msg = (ACLMessage)pendingMessages.get(i - 1);
						result.putBack((ACLMessage)msg.clone());
					}

					// Remove the frozen instance from the persistent store
					s.delete(frozen);

					tx.commit();
					return result;
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while thawing agent <" + target.getLocalName() + ">", he);
		}
	}

	public Long evictFrozenMessageQueue(Long id, String repository) throws ServiceException, NotFoundException {
		try {

			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();

					FrozenMessageQueue frozen = (FrozenMessageQueue)s.load(FrozenMessageQueue.class, id);
					Long result = frozen.getAgentFK();

					// Remove the frozen instance from the persistent store
					s.delete(frozen);

					tx.commit();
					return result;
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while evicting a frozen message queue", he);
		}
	}

	public Long readFrozenMessageQueue(Long id, String repository, List bufferedMessages) throws ServiceException, NotFoundException {
		try {

			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();

					FrozenMessageQueue frozen = (FrozenMessageQueue)s.load(FrozenMessageQueue.class, id);
					Long result = frozen.getAgentFK();

					bufferedMessages.clear();
					java.util.List l = frozen.getBufferedMessages();
					for(int i = 0; i < l.size(); i++) {
						ACLMessage msg = (ACLMessage)l.get(i);
						bufferedMessages.add(msg.clone());
					}

					tx.commit();
					return result;
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while reading a frozen message queue", he);
		}
	}

	public void saveContainer(String name, String repository, java.util.Set agents, java.util.Set mtps) throws ServiceException, NotFoundException {
		try {

			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					SavedContainer toSave = new SavedContainer(name, agents, mtps);
					java.util.List resultSet = s.createQuery("from jade.core.persistence.SavedContainer as item where item.name = ?").setParameter(0, name, Hibernate.STRING).list();
					if(!resultSet.isEmpty()) {
						toSave = (SavedContainer)resultSet.get(0);
						toSave.getAgents().clear();
						toSave.getAgents().addAll(agents);
						toSave.getInstalledMTPs().clear();
						toSave.getInstalledMTPs().addAll(mtps);
					}

					s.saveOrUpdate(toSave);
					tx.commit();
				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while persisting the container", he);
		}
	}

	public void retrieveSavedContainer(SavedContainer toFill, String repository) throws ServiceException, NotFoundException {
		try {

			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					String name = toFill.getName();
					java.util.List resultSet = s.createQuery("from jade.core.persistence.SavedContainer as item where item.name = ?").setParameter(0, name, Hibernate.STRING).list();
					tx.commit();

					if(!resultSet.isEmpty()) {
						SavedContainer loaded = (SavedContainer)resultSet.get(0);

						// Transfer data to the 'toFill' object...
						toFill.getAgents().addAll(loaded.getAgents());
						toFill.getInstalledMTPs().addAll(loaded.getInstalledMTPs());

						// Fetch all saved agents and fill their message queue with the pending messages
						java.util.Iterator it = toFill.getAgents().iterator();
						while(it.hasNext()) {
							SavedAgent sa = (SavedAgent)it.next();

							Agent instance = sa.getAgent();
							java.util.List pendingMessages = sa.getPendingMessages();

							// Restore the agent message queue inserting
							// received messages at the start of the queue
							for(int i = pendingMessages.size(); i > 0; i--) {
								instance.putBack((ACLMessage)pendingMessages.get(i - 1));
							}
						}
					}
					else {
						throw new NotFoundException("Saved container <" + name + "> was not found in repository <" + repository + ">");
					}

				}
				catch(HibernateException he) {
					he.printStackTrace();
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while retrieving the saved container", he);
		}
	}

	public void deleteContainer(ContainerID cid, String repository) throws ServiceException, NotFoundException {
		try {
			SessionFactory sf = getSessionFactory(repository);
			if(sf != null) {

				Session s = sf.openSession();
				Transaction tx = null;
				try {
					tx = s.beginTransaction();
					int deleted = ((org.hibernate.classic.Session) s).delete("from jade.core.persistence.SavedContainer as item where item.name = ?", cid.getName(), Hibernate.STRING);
					tx.commit();
					if(logger.isLoggable(Logger.INFO))
						logger.log(Logger.INFO,"--- Deleted " + deleted + " saved containers ---");
				}
				catch(HibernateException he) {
					if(tx != null) {
						tx.rollback();
					}
					throw he;
				}
				finally {
					s.close();
				}
			}
			else {
				throw new NotFoundException("The repository <" + repository + "> was not found");
			}
		}
		catch(HibernateException he) {
			throw new ServiceException("An error occurred while deleting container <" + cid.getName() + ">", he);
		}
	}

	private synchronized SessionFactory getSessionFactory(String name) {
		return (SessionFactory)sessionFactories.get(name);
	}

	private void checkDB(String name, Configuration conf, SessionFactory sf) throws HibernateException {
		// Tries a simple query, and rebuilds the schema if the query fails
		Session s = null;
		try {
			s = sf.openSession();
			Object tmp = s.createQuery("select count(*) from jade.core.persistence.SavedAgent").iterate().next();
			long howMany = -1;
			if (tmp instanceof Long) {
				howMany = ((Long) tmp).longValue();
			}
			else if (tmp instanceof Integer) {
				howMany = ((Integer) tmp).longValue();
			}
			if(logger.isLoggable(Logger.INFO))
				logger.log(Logger.INFO,"--- The DB <" + name + "> holds " + howMany + " saved agents ---");
		}
		catch(HibernateException he) {

			if(logger.isLoggable(Logger.INFO)){
				logger.log(Logger.INFO,"--- The DB <" + name + "> does not appear to have a valid schema ---");
				logger.log(Logger.INFO,"--- Rebuilding DB <" + name + "> schema ---");
			}


			SchemaExport schemaMgr = new SchemaExport(conf);

			// Remove the DB schema
			schemaMgr.drop(false, true);

			// Export the DB schema
			schemaMgr.create(false, true);
		}
		finally {
			if(s != null) {
				s.close();
			}
		}
	}

	private void checkMetaDB(java.util.Properties defaultProperties) throws IOException, HibernateException {
		// Tries a simple query, and rebuild the schema if the query fails
		Session s = null;
		try {
			s = metaSessions.openSession();
			Object tmp = s.createQuery("select count(*) from jade.core.persistence.Repository").iterate().next();
			long howMany = -1;
			if (tmp instanceof Long) {
				howMany = ((Long) tmp).longValue();
			}
			else if (tmp instanceof Integer) {
				howMany = ((Integer) tmp).longValue();
			}
			if(logger.isLoggable(Logger.INFO))
				logger.log(Logger.INFO,"--- The Meta-DB holds " + howMany + " repositories ---");
		}
		catch(HibernateException he) {
			if(logger.isLoggable(Logger.WARNING)){
				logger.log(Logger.WARNING,"--- The Meta-DB does not appear to have a valid schema ---");
				logger.log(Logger.WARNING,"--- Rebuilding Meta-DB schema ---");
			}

			s.close();
			s = null;

			// Remove the DB schema
			metaSchemaMgr.drop(false, true);

			// Export the DB schema
			metaSchemaMgr.create(false, true);
			createDefaultRepository(defaultProperties);
		}
		finally {
			if(s != null) {
				s.close();
			}
		}
	}

	private void createDefaultRepository(java.util.Properties defProps) throws IOException, HibernateException {

		Session s = metaSessions.openSession();
		Repository defaultRep = new Repository();
		Transaction tx = null;
		try {
			tx = s.beginTransaction();

			defaultRep.setName(DEFAULT_REPOSITORY);
			Repository.StoredProperties sp = new Repository.StoredProperties();
			sp.setValues(defProps);
			defaultRep.setProperties(sp);

			s.save(defaultRep);
			tx.commit();
		}
		catch(HibernateException he) {
			he.printStackTrace();
			if(tx != null) {
				tx.rollback();
			}
		}
		finally {
			s.close();
		}
	}

	private Map sessionFactories;

	// Hibernate-specific variables for JADE meta-database management
	private SchemaExport metaSchemaMgr;
	private SessionFactory metaSessions;
	private java.util.Properties defaultProperties;
}
