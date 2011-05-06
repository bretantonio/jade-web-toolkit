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

package examples.persistence;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

import jade.core.persistence.*;

/**
   This agent shows the basic usage of the PersistenceHelper.
   @author Giovanni Caire - TILAB
 */
public class PersistentAgent extends Agent implements Savable {
	protected void setup() {
		try {
			PersistenceHelper helper = (PersistenceHelper) getHelper(PersistenceHelper.NAME);
			helper.registerSavable(this);
			
			addBehaviour(new TickerBehaviour(this, 1000) {
				public void onTick() {
					System.out.println(getTickCount());
				}
			} );
			
			addBehaviour(new CyclicBehaviour(this) {
				public void action() {
					ACLMessage msg = myAgent.receive();
					if (msg != null) {
						String content = msg.getContent();
						if ("freeze".equals(content)) {
							System.out.println("Agent "+myAgent.getName()+" freezing...");
							try {
								PersistenceHelper helper = (PersistenceHelper) myAgent.getHelper(PersistenceHelper.NAME);
								helper.freeze(PersistenceManager.DEFAULT_REPOSITORY, null);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else if ("save".equals(content)) {
							System.out.println("Agent "+myAgent.getName()+" saving...");
							try {
								PersistenceHelper helper = (PersistenceHelper) myAgent.getHelper(PersistenceHelper.NAME);
								helper.save(PersistenceManager.DEFAULT_REPOSITORY);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else if ("reload".equals(content)) {
							System.out.println("Agent "+myAgent.getName()+" reloading...");
							try {
								PersistenceHelper helper = (PersistenceHelper) myAgent.getHelper(PersistenceHelper.NAME);
								helper.reload(PersistenceManager.DEFAULT_REPOSITORY);
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					else {
						block();
					}
				}
			} );
		}
		catch (ServiceException se) {
			se.printStackTrace();
		}
	}
	
  public void beforeSave() {
		System.out.println(getName()+": beforeSave()");
	}
	
	public void afterLoad() {
		System.out.println(getName()+": afterLoad()");
	}
	
	public void beforeReload() {
		System.out.println(getName()+": beforeReload()");
	}
	
	public void afterReload() {
		System.out.println(getName()+": afterReload()");
	}
	
	public void beforeFreeze() {
		System.out.println(getName()+": beforeFreeze()");
	}
	
	public void afterThaw() {
		System.out.println(getName()+": afterThaw()");
	}
}
	