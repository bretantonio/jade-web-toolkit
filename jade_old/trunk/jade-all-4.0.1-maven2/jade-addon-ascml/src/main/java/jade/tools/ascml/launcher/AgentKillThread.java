/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package jade.tools.ascml.launcher;

import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.domain.JADEAgentManagement.*;
import jade.lang.acl.ACLMessage;
import jade.tools.ascml.absmodel.IRunnableAgentInstance;
import jade.tools.ascml.exceptions.ModelActionException;


/**
 * Kills an IRunnableAgentInstance
 * If everything works fine getResult returns, 
 * otherwise getResult throws a ModelActionException
 * containing the reason why killing failed.
 * 
 * @author Sven Lilienthal (ascml@sven-lilienthal.de)
 *
 */
public class AgentKillThread implements Runnable {
	private IRunnableAgentInstance aModel;
	private Thread t;
	private AgentLauncher al;
	private ModelActionException mae;
	
	
	/**
	 * Kills an IRunnableAgentInstance
	 * @param aModel AgentInstanceModel to be killed
	 * @param al The AgentLauncher-agent
	 */
	public AgentKillThread(IRunnableAgentInstance aModel,AgentLauncher al) {
		this.aModel=aModel;
		this.al=al;
		t = new Thread(this,"Killthread ASCML for "+aModel.getName());
		t.start();
	}
	
	/**
	 * Waits for the agent to be killed, throws ModelActionException if killing fails
	 * @throws ModelActionException Contains the reason why killing failed
	 */
	public void getResult() throws ModelActionException {
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (mae !=null) {
			throw mae;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		int timeout = 15000;
		
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		KillAgent ka = new KillAgent();
		ka.setAgent(new AID(aModel.getName(), AID.ISLOCALNAME));
		jade.content.onto.basic.Action contentAction =
			new jade.content.onto.basic.Action();
		contentAction.setAction(ka);
		contentAction.setActor(al.getAMS());
		msg.addReceiver(al.getAMS());
		msg.setSender(al.getAID());		
		msg.setOntology(JADEManagementOntology.NAME);
		msg.setLanguage(al.codec.getName());
		StringBuffer result = new StringBuffer(0);
		try {
			al.getContentManager().fillContent(msg,contentAction);
			al.addAMSBehaviour(msg, result,aModel.getName());
			synchronized (result) {
				try {
					result.setLength(1);
					result.wait(timeout);
					if (result.length()==1) {
						result.setLength(0);
						result.append("Got timeout while killing agent "+aModel.getName());
					}
				} catch (InterruptedException ie) {					
				}
			}			
			if (result.length()!=0) {
				mae = new ModelActionException("Error while killing the agent named '"+aModel.getName()+"'.", "For some reason, the agent couldn't be killed, please take a look at the system's error-message: " + result.toString(), aModel);			
			}			
		} catch (CodecException e) {
			mae = new ModelActionException("Error while killing the agent named '"+aModel.getName()+"'.", "For some reason, the agent couldn't be killed, please take a look at the system's error-message.", e, aModel);
			e.printStackTrace();
		} catch (OntologyException e) {
			mae = new ModelActionException("Error while killing the agent named '"+aModel.getName()+"'.", "The Ontology used by the kill-message ('"+JADEManagementOntology.NAME+"') is errorneous.", e, aModel);
			e.printStackTrace();
		}

	}

}
