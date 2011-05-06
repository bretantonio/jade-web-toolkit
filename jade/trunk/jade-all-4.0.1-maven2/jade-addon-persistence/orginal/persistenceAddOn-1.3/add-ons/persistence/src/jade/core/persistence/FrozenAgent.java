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
   This class represents the persisted state of a frozen JADE
   agent. It contains the saved agent as it was at the freeze time,
   and the list of all the ACL messages sent to it ever since.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public class FrozenAgent {

    public FrozenAgent(SavedAgent sa) {
	myAgent = sa;
    }

    // For the persistence service
    FrozenAgent() {
    }



    public void setAgent(SavedAgent sa) {
	myAgent = sa;
    }

    public SavedAgent getAgent() {
	return myAgent;
    }

    public void setMessageQueueFK(Long fk) {
	myMessageQueueFK = fk;
    }

    public Long getMessageQueueFK() {
	return myMessageQueueFK;
    }

    // The foreign key to the saved agent message queue (possibly
    // residing on a remote DBMS).
    private Long myMessageQueueFK;

    private SavedAgent myAgent;

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
