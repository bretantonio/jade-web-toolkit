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


import jade.core.AID;
import jade.lang.acl.ACLMessage;


//#MIDP_EXCLUDE_FILE


/**
   This class represents the persisted state of a JADE ACL message.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public class SavedACLMessage {

    public SavedACLMessage(ACLMessage msg, AID savior) {
	mySavior = savior;
	myMessage = msg;
    }

    public void setSavedBy(AID id) {
	mySavior = id;
    }

    public AID getSavedBy() {
	return mySavior;
    }

    public void setMessage(ACLMessage msg) {
	myMessage = msg;
    }

    public ACLMessage getMessage() {
	return myMessage;
    }


    // For persistence service
    private SavedACLMessage() {
    }


    // For persistence service
    private Long getPersistentID() {
	return persistentID;
    }

    // For persistence service
    private void setPersistentID(Long l) {
	persistentID = l;
    }


    private ACLMessage myMessage;
    private AID mySavior;


    // For persistence service
    private Long persistentID;

}
