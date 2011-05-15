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

/**
   This class represents the persisted state of a JADE agent
   container.

   @author Giovanni Rimassa - FRAMeTech s.r.l.
*/
public class SavedContainer {

    public SavedContainer() {
    }

    public SavedContainer(String n, java.util.Set a, java.util.Set i) {
	name = n;
	agents = a;
	installedMTPs = i;
    }


    public String getName() {
	return name;
    }

    public void setName(String n) {
	name = n;
    }

    public java.util.Set getAgents() {
	return agents;
    }

    public void setAgents(java.util.Set a) {
	agents = a;
    }

    public java.util.Set getInstalledMTPs() {
	return installedMTPs;
    }

    public void setInstalledMTPs(java.util.Set imtps) {
	installedMTPs = imtps;
    }

    private String name;
    private java.util.Set agents = new java.util.HashSet();
    private java.util.Set installedMTPs = new java.util.HashSet();



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
