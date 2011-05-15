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


import java.io.Serializable;

import jade.core.Agent;
import jade.core.AID;



/**
   This class represents the persisted state of a JADE agent.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public class SavedAgent {

    public SavedAgent(Agent a, java.util.List pm) {
	myAgent = a;
	myName = a.getName();
	pendingMessages = pm;
    }

    // For the persistence service
    SavedAgent() {
    }

    public void setAgentData(java.io.Serializable data) {
	myAgent = (Agent)data;
    }

    public Serializable getAgentData() {
	return myAgent;
    }

    public void setName(String name) {
	myName = name;
    }

    public String getName() {
	return myName;
    }

    public void setOwned(boolean o) {
        owned = o;
    }
    
    public boolean isOwned() {
        return owned;
    }
    
    public void setPendingMessages(java.util.List messages) {
	pendingMessages = messages;
    }

    public java.util.List getPendingMessages() {
	return pendingMessages;
    }

    public Agent getAgent() {
	return myAgent;
    }


    private Agent myAgent;
    private String myName;
    private boolean owned;

    private java.util.List pendingMessages;


    // For persistence service
    private Long persistentID;

    // For persistence service
    Long getPersistentID() {
	return persistentID;
    }

    // For persistence service
    void setPersistentID(Long l) {
	persistentID = l;
    }


}
