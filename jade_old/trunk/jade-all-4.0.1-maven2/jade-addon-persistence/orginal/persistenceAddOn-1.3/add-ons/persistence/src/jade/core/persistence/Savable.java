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

//#J2ME_EXCLUDE_FILE

/**
   This interface provides callback methods that will be invoked
   during saving, loading, reloading, freezing and thawing operations
   of an agent that had previously registered this 
   <code>Savable</code> object to the agent's Persistence Helper.
   If no Savable object is registered a default one is used that implements all callback
   methods invoking (if present) methods with the same signature in the agent instance.
   @see jade.core.persistence.PersistenceHelper#registerSavable(Savable s)
   @author Giovanni Caire - TILAB.
 */
public interface Savable extends jade.util.leap.Serializable  {
  /**
     Actions to perform before saving an agent that has registered
     this Savable object to its PersistenceHelepr. 
     This method should be defined to execute some
     actions just before the current agent is saved to a persistent
     store.
  */
  void beforeSave();
  
  /**
     Actions to perform after loading an agent that has registered
     this Savable object to its PersistenceHelepr. 
     This method should be defined to execute some
     actions just after the current agent is loaded from a persistent
     store.
  */
	void afterLoad();
  
  /**
     Actions to perform before reloading an agent that has registered
     this Savable object to its PersistenceHelepr. 
     This method should be defined to execute some
     actions just before the current agent is reloaded from a persistent
     store.
  */
	void beforeReload();
  
  /**
     Actions to perform after reloading an agent that has registered
     this Savable object to its PersistenceHelepr. 
     This method should be defined to execute some
     actions just after the current agent is reloaded from a persistent
     store.
  */
	void afterReload();
  
  /**
     Actions to perform before freezing an agent that has registered
     this Savable object to its PersistenceHelepr. 
     This method should be defined to execute some
     actions just before the current agent is frozen to a persistent
     store.
  */
	void beforeFreeze();
  
  /**
     Actions to perform after thawing an agent that has registered
     this Savable object to its PersistenceHelepr. 
     This method should be defined to execute some
     actions just after the current agent is thawed from a persistent
     store.
  */
	void afterThaw();
}
