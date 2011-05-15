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
   This class represents the persistent message queue of a frozen JADE
   agent. It contains the saved agent message queue as it was at the
   freeze time, and the list of all the ACL messages sent to it ever
   since.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public class FrozenMessageQueue {

    public FrozenMessageQueue(AID id, Long fk) {
	myAgentID = id;
	myAgentFK = fk;
	bufferedMessages = new java.util.LinkedList();
    }

    // For the persistence service
    FrozenMessageQueue() {
    }

    public void setAgentIdentifier(AID id) {
	myAgentID = id;
    }

    public AID getAgentIdentifier() {
	return myAgentID;
    }

    public void setAgentFK(Long fk) {
	myAgentFK = fk;
    }

    public Long getAgentFK() {
	return myAgentFK;
    }

    public void setBufferedMessages(java.util.List messages) {
	bufferedMessages = messages;
    }

    public java.util.List getBufferedMessages() {
	return bufferedMessages;
    }

    // The foreign key to the saved agent data (possibly residing on a
    // remote DBMS).
    private Long myAgentFK;

    private AID myAgentID;
    private java.util.List bufferedMessages;


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
